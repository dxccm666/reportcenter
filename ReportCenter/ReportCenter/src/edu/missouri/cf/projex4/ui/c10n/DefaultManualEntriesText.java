package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface DefaultManualEntriesText {
	
	@En("Default Manual Requirements") 
	String screenName();
	
	@En("Add New Requirement Set")
	String addButton_help();
	
	@En("Add New Default Manual Requirement")
	String addEntryButton_help();
	
	@En("")
	String contextHelp();
	
	@En("Name")
	String name();
	
	@En("Project Type")
	String type();
	
	@En("Campus")
	String campus();
	
	@En("All Campuses?")
	String isAllCampus();
	
	ManualEntryText manual();
	
	CommonText common();
	
	StandardErrorText errors();

}
