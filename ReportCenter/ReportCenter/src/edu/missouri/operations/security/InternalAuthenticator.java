package edu.missouri.operations.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jasypt.digest.StandardStringDigester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.missouri.operations.reportcenter.Pools;
import edu.missouri.operations.data.User;

public class InternalAuthenticator implements Authenticator {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private PreparedStatement getPreparedStatement(Connection conn, String password, String userid) throws SQLException {

		PreparedStatement stmt = conn.prepareStatement("update users set password = ? where userid = ?");
		String digestedpassword = digester.digest(password);
		stmt.setString(1, digestedpassword);
		stmt.setString(2, userid);

		return stmt;
	}

	public void setPassword(String username, String password) {

		Connection c = null;

		try {

			c = Pools.getConnection(Pools.Names.PROJEX);
			try (PreparedStatement stmt = getPreparedStatement(c, password, username)) {
				stmt.executeUpdate();
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, c);
		}

	}

	private StandardStringDigester digester;

	public InternalAuthenticator() {
		digester = new StandardStringDigester();
		digester.setAlgorithm("SHA-1");
		digester.setIterations(50000);
	}

	private PreparedStatement getPreparedStatement(Connection conn, String userid) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("select password from users where userid = ?");
		stmt.setString(1, userid);
		return stmt;
	}

	public boolean authenticate(String username, String password, User user) {

		Connection c = null;

		try {
			c = Pools.getConnection(Pools.Names.PROJEX);
			try (PreparedStatement stmt = getPreparedStatement(c, username); ResultSet rs = stmt.executeQuery()) {

				if (rs.next()) {
					String digestedpassword = digester.digest(password);
					String savedpassword = rs.getString("PASSWORD");

					if (digester.matches(savedpassword, digestedpassword)) {
						logger.error("User " + username + " was found - passwords match");
						// TODO Need to set user info.
						return true;
					} else {
						logger.error("User " + username + " was found but passwords do not match");
					}

				} else {
					logger.error("User " + username + " was not found");
				}

			}

		} catch (SQLException sqle) {
			logger.error("Could not verify internal user id and password ", sqle);
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, c);
		}

		return false;
	}
}
