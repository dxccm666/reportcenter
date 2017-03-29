package edu.missouri.operations.reportcenter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;

import edu.missouri.operations.data.HikariConnectionPool;

public class Pools {

	public enum Names {
		REPORTCENTER, PSHR 
	}

	public enum Mode {
		PRODUCTION, DEVELOPMENT
	}

	private final static transient Logger logger = LoggerFactory.getLogger(Pools.class);

	private JDBCConnectionPool connectionPool = null;
	private JDBCConnectionPool pshrConnectionPool = null;

	public final static Mode mode = Mode.PRODUCTION;
	public static String dbConnectionString;
	
	private static final String userName = "reportcenter";
	private static final String password = "rptUser392";

	static Pools pools;

	public Pools getCurrent() {
		return pools;
	}

	public Pools() {

		int maxConnections = 0;

		switch (mode) {

		case DEVELOPMENT:
			if (logger.isTraceEnabled()) {
				logger.trace("Using development server connection");
			}
			dbConnectionString = "jdbc:oracle:thin:@//128.206.190.72:1521/projex4.projex4db.cf.missouri.edu";
			break;

		case PRODUCTION:
			if (logger.isTraceEnabled()) {
				logger.trace("Using production server connection");
			}
			dbConnectionString = "jdbc:oracle:thin:@//128.206.190.72:1521/projex4.projex4db.cf.missouri.edu";
			break;

		}

		maxConnections = 40;
		connectionPool = new HikariConnectionPool("oracle.jdbc.OracleDriver", dbConnectionString, userName, password, 50, maxConnections);
		pshrConnectionPool = new HikariConnectionPool("oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@um-hrreport-01.umsystem.edu:1521/HRRPT.UMSYSTEM.EDU", "mu_hr", "Marbles1", 2, 5);

		pools = this;

	}

	/**
	 * 
	 * Universal method for getting the various connection pools. If you call
	 * use this method, you should use JDBCCloser.release to return the
	 * connection to the pool.
	 * 
	 * @param pool
	 * @return
	 */
	public static JDBCConnectionPool getConnectionPool(Pools.Names pool) {

		if (pools != null) {

			switch (pool) {
			case REPORTCENTER:
				return pools.connectionPool;
			case PSHR:
				return pools.pshrConnectionPool;
			}

		}

		return null;

	}

	public static Connection getConnection(Pools.Names pool) throws SQLException {

		try {
			return getConnectionPool(pool).reserveConnection();
		} catch (SQLException e) {
			throw e;
		}
	}

	public static void releaseConnection(Pools.Names pool, Connection conn) {
		if (conn != null) {
			getConnectionPool(pool).releaseConnection(conn);
		}
	}

	private static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.error("Error closing ResultSet", e);
			}
		}
	}

	private static void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				logger.error("Error closing Statement", e);
			}
		}
	}

	/**
	 * Close all sql objects. Order probably matters, but it has not been
	 * tested. To be sure, close all derived objects before closing their
	 * deriver.
	 * 
	 * @param objects
	 */
	public static void close(Object... objects) {
		for (Object o : objects) {
			if (o instanceof java.sql.Statement) {
				close((Statement) o);
			} else if (o instanceof java.sql.ResultSet) {
				close((ResultSet) o);
			} else if (o != null) {
				Exception e = new Exception();
				logger.error("Invalid call to close : o is of type {}", o.getClass().getName(), e);

			}
		}
	}

	@Deprecated
	public static PreparedStatement getPreparedStatement(Connection conn, String query, String... values) throws SQLException {
		if (values == null) {
			throw new IllegalArgumentException("values array cannot be null");
		}
		DBObject[] list = new DBObject[values.length];
		for (int i = 0; i < values.length; i++) {
			list[i] = new DBObject(values[i], Types.VARCHAR);
		}
		return getPreparedStatement(conn, query, list);
	}

	/**
	 * Provides the ability to create a prepared statement for null values and
	 * other data types.
	 * <p>
	 * More data types will need to be added in later.
	 * 
	 * @param conn
	 * @param query
	 * @param values
	 *            - ie. array of DBObject(Object value, Types.VARCHAR);
	 * @return
	 * @throws SQLException
	 */
	public static PreparedStatement getPreparedStatement(Connection conn, String query, DBObject... values) throws SQLException {

		if (conn == null) {
			throw new IllegalArgumentException("connection cannot be null");
		}
		if (query == null) {
			throw new IllegalArgumentException("query cannot be null");
		}
		if (values == null) {
			throw new IllegalArgumentException("values array cannot be null");
		}

		PreparedStatement stmt = conn.prepareStatement(query);
		for (int i = 0; i < values.length; i++) {
			int type = values[i].getType();
			Object value = values[i].getValue();
			if (value == null) {
				stmt.setNull(i + 1, type);
			} else {
				switch (type) {
				case Types.VARCHAR:
					stmt.setString(i + 1, value.toString());
					break;
				case Types.INTEGER:
					stmt.setInt(i + 1, (int) value);
					break;
				case Types.NUMERIC:
					if (value instanceof Integer) {
						stmt.setInt(i + 1, (int) value);
					} else if (value instanceof BigDecimal) {
						stmt.setBigDecimal(i + 1, (BigDecimal) value);
					} else {
						throw new IllegalArgumentException("Class " + value.getClass() + " is not handled yet");
					}
					break;
				default:
					throw new IllegalArgumentException("Class " + value.getClass() + " is not handled yet");

				}
			}
		}

		return stmt;
	}

	public static class DBObject {

		private Object value;
		private int type;

		public DBObject(Object value, int type) {
			this.value = value;
			this.type = type;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
	}

	public static String generateInClause(String[] strings) {

		String query = "";
		if (strings == null || strings.length == 0) {
			throw new IllegalArgumentException("in list cannot be null or empty");
		}

		if (strings.length == 1) {
			query = " = ?";
		} else {
			query = "in (";
			for (int i = 0; i < strings.length; i++) {
				query += "?,";
			}
			query = query.substring(0, query.length() - 1);
			query += ")";
		}

		if (logger.isTraceEnabled()) {
			logger.trace("in query = {}", query);
		}
		return query;
	}

	public void shutdown() {
		connectionPool.destroy();
		pshrConnectionPool.destroy();
	}

}
