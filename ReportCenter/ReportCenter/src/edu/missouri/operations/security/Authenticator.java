package edu.missouri.operations.security;

import edu.missouri.operations.data.User;

public interface Authenticator {
	public boolean authenticate(String username, String password, User u);
}
