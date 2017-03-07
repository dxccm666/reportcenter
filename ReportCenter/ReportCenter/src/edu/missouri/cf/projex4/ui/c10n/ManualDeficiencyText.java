package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface ManualDeficiencyText {

	@En("Deficiency Type")
	public String deficiencyType();
	
	@En("")
	public String deficiencyType_help();
	
	@En("Deficiency")
	public String deficiency();
	
	@En("A detailed description of what is missing or incomplete in the project manual.  Document Center or Archives personnel will use this information to complete the Project Manual after the close of the project.")
	public String deficiency_help();
	
	@En("Responded")
	public String responded();
	
	@En("The date that the deficiency report was responded to")
	public String responded_help();
	
	@En("Responded By")
	public String respondedBy();
	
	@En("The person who responded to this deficiency report.")
	public String respondedBy_help();
	
	@En("Response")
	public String response();
	
	@En("Notes to indicate the result of this deficiency report.  Still Missing or incomplete files should be noted here.  Fixed files should be attached to the appropriate manual entry.")
	public String response_help();
	
	
	
	
}
