package edu.missouri.operations.reportcenter.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface NotificationText {
	
	@En("{0} was created.")
	String created(String name);
	
	@En("{0} was updated")
	String updated(String name);
	
	@En("{0} was deleted")
	String deleted(String name);
	
	@En("{0} status was changed to {1}")
	String statusChanged(String name, String status);
	
	@En("File was uploaded")
	String uploaded();
	
	@En("{1} replied to {0}")
	String responded(String name, String userName);
	
	@En("File was downloaded")
	String downloaded();
	
	@En("{0} logged in")
	String loggedIn(String userName);
	
	@En("{0} logged out")
	String loggedOut(String userName);
	
	@En("{0} has been authorized to proceed")
	String noticeToProceed(String name);

}
