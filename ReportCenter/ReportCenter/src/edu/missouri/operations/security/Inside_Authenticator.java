package edu.missouri.operations.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import edu.missouri.operations.data.User;
import edu.missouri.operations.data.User.UserAttribute;



public class Inside_Authenticator {
	/* Parameters for initial global catalog query */

	private String gcBindDN = "CN=MU CF SDAV,CN=USERS,DC=COL,DC=MISSOURI,DC=EDU";
	private String gcPassword = "Dbsb&484";
	private String gcServer = "col.missouri.edu";
	
	private final static Logger logger = LoggerFactory.getLogger(Inside_Authenticator.class);

	private String getServer(String dn) {
		return dn.substring(dn.indexOf("DC=")).replaceAll("DC=", "").replaceAll(",", ".");
	}
	
	
	@SuppressWarnings("unused")
	public boolean LoginVerify(String username, String password){
		
		String sso;
				
	    if (username.indexOf("@") != -1)
	    {
	        sso = username.substring(0,username.indexOf("@") - 1);
	    }
	    else if (username.indexOf("\\") != -1)
	    {
	        sso = username.substring(username.indexOf("\\") + 1).toUpperCase();
	    }
	    else
	    {
	        sso = username;
	    }
   
          LDAPConnection ldapconnection = null;
		  SearchResult searchResult;
		  
		try {
			
			ldapconnection = new LDAPConnection(gcServer, 3268, gcBindDN, gcPassword);

			searchResult = ldapconnection.search("DC=EDU", SearchScope.SUB, "(sAMAccountName=" + sso + ")");
			

			if (searchResult.getEntryCount() == 1)
	        {
	            SearchResultEntry e = searchResult.getSearchEntries().get(0);
	           
	            User u = User.getUser();
	            String dn = e.getDN();	            	            
	            
	            String newserver = getServer(dn); 
	            
	            ldapconnection.close();
	            ldapconnection = new LDAPConnection(newserver, 389, dn, password);
	            u.put(UserAttribute.USERID, username);
	        

	            if (ldapconnection != null)
	             {		
	            	logger.info("Verified PeopleSoft User name is " + u.get(User.UserAttribute.USERID));         
					return  true;
	              }
	            else{	
	            	System.out.println("log in has error");
	                return false;
	               }
	        }
	         else
	            {
	        	 System.out.println("log in has error A");
	             return false;
	            }    	
	        	        
		}catch (LDAPException e1) {    	
	    	  
			logger.error(e1.getMessage());
	    	
	    }
		return false;
	}
}
