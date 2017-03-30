package edu.missouri.cf.projex4.ui.desktop.configuration.securitygroups;

import c10n.C10NMessages;
import c10n.annotations.En;
import edu.missouri.operations.reportcenter.ui.c10n.CommonText;

@C10NMessages
public interface SecurityGroupsText {
	
	@En("Security Groups")
	String screenName();
	
	@En("Name") 
	String securityGroupName();
	
	@En("")
	String securityGroupName_help();
	
	@En("Description")
	String description();
	
	@En("")
	String description_help();
	
	@En("is Active?")
	String isActive();
	
	@En("")
	String isActive_help();
	
	@En("is System Security Group?")
	String isSystemSecurityGroup();
	
	@En("")
	String isSystemSecurityGroup_help();
	
	@En("")
	String contextHelp();
	
	CommonText common();

}
