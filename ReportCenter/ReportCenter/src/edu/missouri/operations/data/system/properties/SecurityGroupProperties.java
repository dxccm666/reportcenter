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
import edu.missouri.cf.projex4.data.system.core.Loggers;

/**
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class SecurityGroupProperties extends Properties {
	
	private static Logger logger = Loggers.getLogger(SecurityGroupProperties.class);
	
	String securityGroupId;

	/**
	 * @param connectionPool
	 */
	public SecurityGroupProperties(JDBCConnectionPool connectionPool) {
		super(connectionPool);
	}
	
	/**
	 * @param connectionPool
	 * @param securityGroupId
	 */
	public SecurityGroupProperties(JDBCConnectionPool connectionPool, String securityGroupId) {
		super(connectionPool);
		setSecurityGroupId(securityGroupId);
	}
	
	/**
	 * @param securityGroupId
	 */
	public void setSecurityGroupId(String securityGroupId) {
		this.securityGroupId = securityGroupId;
		setQueryString("select * from properties where refidtype = 'SECURITYGROUP' and refid = '" + securityGroupId +"'");
	}
	
	/**
	 * @param c
	 * @param securityGroupId
	 * @param property
	 * @return
	 * @throws SQLException
	 */
	private static PreparedStatement getPreparedStatement(Connection c, String securityGroupId, String property) throws SQLException {
		PreparedStatement stmt = c.prepareStatement("select * from properties where refidtype = 'SECURITYGROUP' and refid = ? and property = ?");
		stmt.setString(1,securityGroupId);
		stmt.setString(2,property);
		return stmt;
	}
	
	/**
	 * @param securityGroupId
	 * @param property
	 * @return
	 */
	public static String get(String securityGroupId, String property) {
		
		String retval = null;

		Connection c = null;
		try {
			c = Pools.getConnection(Pools.Names.PROJEX);
			try (PreparedStatement stmt = getPreparedStatement(c, securityGroupId, property); ResultSet rs = stmt.executeQuery()) {

				if (rs.next()) {
					retval = rs.getString("VALUE");
				} else {
					logger.info("Could not retrieve value for system property " + property);
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
	 * @param securityGroupId
	 * @param property
	 * @param value
	 * @throws SQLException
	 */
	public static void put(String securityGroupId, String property, String value) throws SQLException {

		Connection conn = null;
		
		try {
			conn = Pools.getConnection(Pools.Names.PROJEX);
			try (PreparedStatement stmt = getPreparedStatement(conn, securityGroupId, property); ResultSet rs = stmt.executeQuery()) {
				
				String id = null;
				String rowstamp = null;
				String refidtype = "SECURITYGROUP";
				if(rs.next()) {
					id = rs.getString("ID");
					rowstamp = rs.getString("ROWSTAMP");
				} else {
					rowstamp = "AAAA";
				}
				
				try (CallableStatement call = getCallableStatement(conn, id, rowstamp, new OracleString(refidtype), new OracleString(securityGroupId), new OracleString(property), new OracleString(value))) {
					call.executeUpdate();
				}
			}
			
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}
	}

}
