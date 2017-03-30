package edu.missouri.operations.reportcenter.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.*;

@C10NMessages
public interface CommonText {
	
	@En("Title")
	public String title();
	
	@En("Status")
	public String status();
	
	@En("Status Id")
	public String statusId();
	
	@En("System Status")
	public String systemStatus();
	
	@En("")
	public String statusdescription();
	
	@En("Statused")
	public String statused();
	
	@En("")
	public String statuseddescription();
	
	@En("Statused By")
	public String statusedby();
	
	@En("")
	public String statusedbydescription();
	
	@En("Created")
	public String created();
	
	@En("")
	public String createddescription();
	
	@En("Created By")
	public String createdby();
	
	@En("")
	public String createdbydescription();
	
	@En("Modified")
	public String modified();
	
	@En("")
	public String modifieddescription();
	
	@En("Modified By")
	public String modifiedby();
	
	@En("")
	public String modifiedbydescription();
	
	@En("#")
	public String rowNum();
	
	@En("Instructions")
	public String instructions();
	
	@En("In Work Flow")
	public String inWorkFlow();
	
	@En("Discussions Active")
	public String discussionsActive();

}
