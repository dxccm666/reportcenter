package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface MetaComponentText {
	
	@En("Object Details")
	public String componentcaption();
	
	@En("Object Details")
	public String componentdescription();
	
	@En("UUID")
	public String uuid();
	
	@En("")
	public String uuiddescription();

	@En("Status")
	public String status();
	
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

}
