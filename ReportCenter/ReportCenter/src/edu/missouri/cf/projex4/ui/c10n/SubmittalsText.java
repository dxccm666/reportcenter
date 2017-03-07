package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface SubmittalsText {
	
	@En("Submittals")
	String screenName();
	
	@En("Contractor Instructions")
	String instructionsHeading();
	
	@En("add instructions here....")
	String instructions();
	
	@En("Add New Submittal")
	String addManualEntryButtonDescription();
	
	@En("Attach new File")
	String attachFile();
	
	@En("Upload Requirements File")
	String uploadButtonText();
	
	@En("")
	String uploadButtonText_help();
	
	@En("")
	String messageEnableButton();
	
	@En("Enable notifications for selected items")
	String messageEnableButton_help();
	
	@En("")
	String messageDisableButton();
	
	@En("Disable notifications for selected items")
	String messageDisableButton_help();
	
	@En("")
	String manualEntryContextHelp();
	
	SubmittalsTableText submittalEntry();
	
	CommonText common();
	
	StandardButtonText buttons();

}
