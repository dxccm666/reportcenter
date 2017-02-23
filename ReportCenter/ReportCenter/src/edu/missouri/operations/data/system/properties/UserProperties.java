/**
 * 
 */
package edu.missouri.operations.data.system.properties;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;

import edu.missouri.cf.data.OracleString;
import edu.missouri.cf.projex4.data.Pools;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class UserProperties extends Properties {

	private static Logger logger = Loggers.getLogger(UserProperties.class);

	String userId;
	
	public UserProperties() {
		super(Pools.getConnectionPool(Pools.Names.PROJEX));
	}

	/**
	 * @param connectionPool
	 */
	public UserProperties(JDBCConnectionPool connectionPool) {
		super(connectionPool);
	}

	/**
	 * @param connectionPool
	 * @param userId
	 */
	public UserProperties(JDBCConnectionPool connectionPool, String userId) {
		super(connectionPool);
		setUserId(userId);
	}

	/**
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
		setQueryString("select * from properties where refidtype = 'USER' and refid = '" + userId + "'");
	}

	/**
	 * @param userId
	 * @param property
	 * @return
	 */
	public static String get(String property) {

		String userId = User.getUser().getUserId();
		String retval = null;
		Connection c = null;
		try {
			c = Pools.getConnection(Pools.Names.PROJEX);
			try (PreparedStatement stmt = c
					.prepareStatement("select * from properties where refidtype = 'USER' and refid = ? and property = ?")) {
				setString(stmt, 1, userId);
				setString(stmt, 2, property);
				try (ResultSet rs = stmt.executeQuery()) {

					if (rs.next()) {
						retval = rs.getString("VALUE");
					} else {
						logger.info("Could not retrieve value for system property " + property);
					}
				}
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, c);
		}

		return retval;
	}

	/**
	 * @param userId
	 * @param property
	 * @param value
	 * @throws SQLException
	 */
	public static void put(String property, String value) throws SQLException {

		String userId = User.getUser().getUserId();

		Connection conn = null;

		try {
			
			conn = Pools.getConnection(Pools.Names.PROJEX);
			try (PreparedStatement stmt = conn
					.prepareStatement("select * from properties where refidtype = 'USER' and refid = ? and property = ?")) {
				setString(stmt, 1, userId);
				setString(stmt, 2, property);
				try (ResultSet rs = stmt.executeQuery()) {

					String id = null;
					String rowstamp = null;
					String refidtype = "USER";
					if (rs.next()) {
						id = rs.getString("ID");
						rowstamp = rs.getString("ROWSTAMP");
					} else {
						rowstamp = "AAAA";
					}

					try (CallableStatement call = getCallableStatement(conn, id, rowstamp, new OracleString(refidtype),
							new OracleString(userId), new OracleString(property), new OracleString(value))) {
						call.executeUpdate();
						conn.commit();
					}
				}
			}

		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}
	}

}
