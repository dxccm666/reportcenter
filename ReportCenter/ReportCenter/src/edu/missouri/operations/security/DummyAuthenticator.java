package edu.missouri.operations.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.missouri.operations.reportcenter.Pools;
import edu.missouri.operations.data.User;

public class DummyAuthenticator implements Authenticator {

	public DummyAuthenticator() {
	}

	@Override
	public boolean authenticate(String username, String password, User u) {

		if (u == null) {
			u = new User();
			User.setUser(u);
		}

		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			c = Pools.getConnection(Pools.Names.PROJEX);
			stmt = c.prepareStatement("select id, userlogin from users where upper(userlogin) = upper(?)");
			stmt.setString(1, "PROJEX4");

			rs = stmt.executeQuery();
			if (rs.next()) {
				u.setValuesFromDummy("PROJEX4", "Dummy Authenticator", rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Pools.close(rs, stmt);
			Pools.releaseConnection(Pools.Names.PROJEX, c);
		}
		
		System.err.println("Welcome PROJEX4");

		return true;
	}

}
