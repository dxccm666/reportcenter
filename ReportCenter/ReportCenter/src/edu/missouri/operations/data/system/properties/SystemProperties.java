package edu.missouri.operations.data.system.properties;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.missouri.cf.projex4.data.Pools;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class SystemProperties extends Properties {

	private final static Logger logger = LoggerFactory.getLogger(SystemProperties.class);

	public enum Server {
		ELASTICSEARCH, PROJEX
	}

	/**
	 * @param connectionPool
	 */
	public SystemProperties() {
		super(Pools.getConnectionPool(Pools.Names.PROJEX));
		setQueryString("select * from properties where refidtype = 'SYSTEM'");
	}

	/**
	 * @param property
	 * @return
	 */
	public static String get(String property) {

		String retval = null;

		Connection c = null;
		try {
			c = Pools.getConnection(Pools.Names.PROJEX);
			try (PreparedStatement stmt = c
					.prepareStatement("select * from properties where property = ? and refidtype = 'SYSTEM'")) {

				stmt.setString(1, property);
				try (ResultSet rs = stmt.executeQuery()) {

					if (rs.next()) {
						retval = rs.getString("VALUE");
					} else {
						logger.info("Could not retrieve value for system property " + property);
					}
				}
			}

			c.commit();

		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, c);
		}

		return retval;
	}

	public static String get(Connection c, String property) {

		String retval = null;

		try (PreparedStatement stmt = c.prepareStatement("select * from properties where property = ? and refidtype = 'SYSTEM'")) {

			stmt.setString(1, property);
			try (ResultSet rs = stmt.executeQuery()) {

				if (rs.next()) {
					retval = rs.getString("VALUE");
				} else {
					logger.info("Could not retrieve value for system property " + property);
				}
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		}

		return retval;
	}

	/**
	 * @param property
	 * @param value
	 * @throws SQLException
	 */
	public static void put(String property, String value) throws SQLException {
		
		if(logger.isDebugEnabled()) {
			logger.debug("Saving system property {} = {}", property, value);
		}

		Connection conn = null;

		try {

			conn = Pools.getConnection(Pools.Names.PROJEX);
			try (PreparedStatement stmt = conn
					.prepareStatement("select * from properties where property = ? and refidtype = 'SYSTEM'")) {

				stmt.setString(1, property);
				try (ResultSet rs = stmt.executeQuery()) {

					String id = null;
					String rowstamp = null;
					String refidtype = "SYSTEM";
					String refid = null;
					if (rs.next()) {
						id = rs.getString("ID");
						rowstamp = rs.getString("ROWSTAMP");
					} else {
						rowstamp = "AAAA";
					}

					try (CallableStatement call = conn.prepareCall("{ ? = call coreproperty.property(?,?,?,?,?,?) }")) {
						call.registerOutParameter(1, Types.VARCHAR);
						setString(call, 2, id);
						setString(call, 3, rowstamp);
						setString(call, 4, refidtype);
						setString(call, 5, refid);
						setString(call, 6, property);
						setString(call, 7, value);
						call.executeUpdate();
						conn.commit();
					}

				}
			}

		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}
	}

	/**
	 * Check to see if the projex application/elasticsearch is deployed on a
	 * local machine while testing or if it is deployed onto the server.
	 * 
	 * @param server
	 * @return
	 */
	public static boolean isDeployedOnServer(Server server) {
		InetAddress tempaddress = null;
		InetAddress localMachine = null;
		try {
			localMachine = java.net.InetAddress.getLocalHost();
			if (Server.ELASTICSEARCH.equals(server)) {
				tempaddress = InetAddress.getByName(get("elasticsearch.server"));
			} else {
				tempaddress = InetAddress.getByName(get("projex.server"));
			}
			logger.debug("Hostname of local machine: {}", localMachine.getHostName());

			return localMachine.equals(tempaddress);
		} catch (UnknownHostException e) {
			logger.debug("Hostname can not be resolved");
		}
		return false;
	}

}
