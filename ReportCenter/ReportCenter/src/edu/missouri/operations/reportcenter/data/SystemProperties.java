package edu.missouri.operations.reportcenter.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.missouri.operations.reportcenter.Pools;

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
		super(Pools.getConnectionPool(Pools.Names.REPORTCENTER));
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
			
			c = Pools.getConnection(Pools.Names.REPORTCENTER);
			retval = get(c, property);
			c.commit();

		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			Pools.releaseConnection(Pools.Names.REPORTCENTER, c);
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

			conn = Pools.getConnection(Pools.Names.REPORTCENTER);
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
			Pools.releaseConnection(Pools.Names.REPORTCENTER, conn);
		}
	}

}
