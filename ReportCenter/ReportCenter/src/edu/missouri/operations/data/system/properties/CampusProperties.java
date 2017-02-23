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
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;

import edu.missouri.cf.data.OracleString;
import edu.missouri.cf.projex4.data.Pools;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class CampusProperties extends Properties {

	private static Logger logger = LoggerFactory.getLogger(CampusProperties.class);

	String campusId;

	/**
	 * @param connectionPool
	 */
	public CampusProperties(JDBCConnectionPool connectionPool) {
		super(connectionPool);
	}

	/**
	 * @param connectionPool
	 * @param campusId
	 */
	public CampusProperties(JDBCConnectionPool connectionPool, String campusId) {
		super(connectionPool);
		setCampusId(campusId);
	}

	public CampusProperties() {
		super(Pools.getConnectionPool(Pools.Names.PROJEX));
		setQueryString("select * from properties");
		setMandatoryFilters(new Compare.Equal("REFID", "0"));
	}

	/**
	 * @param campusId
	 */
	public void setCampusId(String campusId) {
		this.campusId = campusId;
		removeMandatoryFilters();
		setMandatoryFilters(new And(new Compare.Equal("REFID",campusId), new Compare.Equal("REFIDTYPE","CAMPUS")));
		// setQueryString("select * from properties where refidtype = 'CAMPUS' and refid = '" + campusId + "'");
	}

	/**
	 * @param c
	 * @param campusId
	 * @param property
	 * @return
	 * @throws SQLException
	 */
	private static PreparedStatement getPreparedStatement(Connection c, String campusId, String property) throws SQLException {
		PreparedStatement stmt = c
				.prepareStatement("select * from properties where refidtype = 'CAMPUS' and refid = ? and property = ?");
		stmt.setString(1, campusId);
		stmt.setString(2, property);
		return stmt;
	}

	/**
	 * @param campusId
	 * @param property
	 * @return
	 */
	public static String get(String campusId, String property) {

		String retval = null;

		Connection c = null;
		try {
			c = Pools.getConnection(Pools.Names.PROJEX);
			try (PreparedStatement stmt = getPreparedStatement(c, campusId, property); ResultSet rs = stmt.executeQuery()) {

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
	 * @param campusId
	 * @param property
	 * @param value
	 * @throws SQLException
	 */
	public static void put(String campusId, String property, String value) throws SQLException {

		Connection conn = null;

		try {
			conn = Pools.getConnection(Pools.Names.PROJEX);
			try (PreparedStatement stmt = getPreparedStatement(conn, campusId, property); ResultSet rs = stmt.executeQuery()) {

				String id = null;
				String rowstamp = "AAAA";
				String refidtype = "CAMPUS";

				if (rs.next()) {
					id = rs.getString("ID");
					rowstamp = rs.getString("ROWSTAMP");
				}

				try (CallableStatement call = getCallableStatement(conn, id, rowstamp, new OracleString(refidtype),
						new OracleString(campusId), new OracleString(property), new OracleString(value))) {
					call.executeUpdate();
				}
			}

		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}
	}

}
