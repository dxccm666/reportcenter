package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface ManualsText {
	
	@En("Closeout Requirements")
	String screenName();
	
	@En("Deficiencies")
	String tabName2();
	
	@En("Contractor Instructions")
	String instructionsHeading();
	
	@En("add instructions here....")
	String instructions();
	
	@En("Originating Closeout Template")
	String template();
	
	@En("")
	String template_help();
	
	@En("Add New Closeout Requirement")
	String addManualEntryButtonDescription();
	
	@En("Report Deficiency")
	String addManualDeficiencyButtonDescription();
	
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
	
	@En("")
	String manualDeficiencyContextHelp();
	
	ManualEntryTableText manualEntry();
	
	ManualDeficiencyTableText manualDeficiency();

	CommonText common();
	
	StandardButtonText buttons();

}
