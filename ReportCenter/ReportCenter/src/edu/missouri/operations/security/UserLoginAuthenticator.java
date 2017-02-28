package edu.missouri.operations.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.missouri.operations.reportcenter.Pools;
import edu.missouri.operations.data.User;
import edu.missouri.operations.data.User.UserAttribute;

public class UserLoginAuthenticator implements Authenticator {

	private final static Logger logger = LoggerFactory.getLogger(UserLoginAuthenticator.class);

	private String authproviderId;

	public UserLoginAuthenticator(String authproviderId) {
		this.authproviderId = authproviderId;
	}

	@Override
	public boolean authenticate(String username, String password, User u) {
		
		Connection c = null;
		try {

			c = Pools.getConnection(Pools.Names.PROJEX);

			try (PreparedStatement stmt = c
					.prepareStatement("select * from userdetails where upper(userlogin) = upper(?) and isactive = 1 and status = 'ACTIVE'")) {

				stmt.setString(1, username);
				try (ResultSet rs = stmt.executeQuery()) {

					if (rs.next()) {

						if (logger.isDebugEnabled()) {
							logger.debug("ID from the users table = {}", rs.getString("USERID"));
						}
						
						u.setValuesFromDatabase(username, "OAUTH", rs);
						u.put(UserAttribute.AUTHPROVIDERLOGIN, username);
						u.put(UserAttribute.AUTHPROVIDERID, authproviderId);
						
						return true;

					} else {

						if (logger.isDebugEnabled()) {
							logger.debug("Could not select from userdetails from sso = {}", username);
						}

					}

				}
			}

		} catch (SQLException sqle) {

			if (logger.isErrorEnabled()) {
				logger.error("Could not get ID info from users table", sqle);
			}

		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, c);
		}
	
		return true;
	}
}
