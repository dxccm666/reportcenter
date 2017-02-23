package edu.missouri.operations.security;

import com.jp.windows.live.LogonManager;
import com.jp.windows.live.LogonManagerException;
import com.jp.windows.live.SecurityToken;

import edu.missouri.operations.data.User;

public class MicrosoftAuthenticator implements Authenticator {

	@Override
	public boolean authenticate(String username, String password, User u) {
		// Needs more work.
		try {
			SecurityToken securityToken = new LogonManager().logon("live.com", username, password);

			System.out.println("Logon succeeded!");
			System.out.println("Passport Token: " + securityToken.getBinarySecurityToken());
			System.out.println("Issue Date: " + securityToken.getIssueDate());
			System.out.println("Expire Date: " + securityToken.getExpireDate());
			
			// Need to look up User info.
			return true;
		} catch (LogonManagerException lme) {
			System.out.println("Logon failed: " + lme.getDetailedMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

}
