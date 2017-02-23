package edu.missouri.operations.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.zaxxer.hikari.HikariDataSource;

public class HikariConnectionPool extends HikariDataSource implements JDBCConnectionPool {

	HikariDataSource connectionPool = new HikariDataSource();
	private final static transient Logger logger = LoggerFactory.getLogger(HikariConnectionPool.class);

	public HikariConnectionPool(String driverClassName, String jdbcUrl, String userName, String password, int minConnections, int maxPoolSize) {

		setJdbcUrl(jdbcUrl);
		setDriverClassName(driverClassName);
		setUsername(userName);
		setPassword(password);
		setMaximumPoolSize(maxPoolSize);
		setAutoCommit(false);
		addDataSourceProperty("setImplicitCachingEnabled", "true");
		setLeakDetectionThreshold(TimeUnit.MINUTES.toMillis(5));
		setMaxLifetime(TimeUnit.HOURS.toMillis(24));

	}

	@Override
	public Connection reserveConnection() throws SQLException {

		Connection conn = getConnection();
		
		/*

		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Reserving Connection {}", conn.toString());
			}
			throw new Exception("reserving connection");
		} catch (Exception e) {
			if(logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			}
		} */

		return conn;
	}

	@Override
	public void releaseConnection(Connection conn) {

		/* try {
			if (logger.isDebugEnabled()) {
				logger.debug("Releasing Connection {}", conn.toString());
			}
			throw new Exception("releasing connection");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			}
		} */

		try {
			
			// This eliminates the dirty commit error - but it's adds overhead.  
			// Would still be better to find missing commit.
			// I think it is in the login process.
			
			conn.commit();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void destroy() {
		close();
	}

}
