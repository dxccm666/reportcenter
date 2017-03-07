package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface ManualEntryTableText {
	
	@En("Type")
	String manualEntryType();
	
	@En("")
	String manualEntryType_help();
	
	@En("Classification")
	String classification();
	
	@En("")
	String classification_help();
	
	@En("Classification Description")
	String classificationDescription();
	
	@En("")
	String classificationDescription_help();
	
	@En("Item")
	String item();
	
	@En("")
	String item_help();
	
	@En("Documentation Needed")
	String documentationNeeded();
	
	@En("")
	String documentationNeeded_help();
	
	@En("Document Type Required")
	String documentTypeRequired();
	
	@En("")
	String documentTypeRequired_help();
	
	@En("Deadline")
	String deadline();
	
	@En("")
	String deadline_help();
	
	@En("Approval Required")
	String requireApproval();
	
	@En("")
	String requireApproval_help();
	
	@En("# Attached Files")
	String fileCount();
	
	@En("Expected From")
	String expectedFrom();
	
	@En("")
	String expectedFrom_help();
	
	@En("Responsible Firm")
	String responsibleFirm();
	
	CommonText common();
	
	StandardButtonText buttons();
	
	
	

}
