package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface ManualEntryText {
	
	@En("Manual Entry Details")
	String screenName();
	
	@En("Entry Type")
	String submittalType();
	
	@En("Project Type")
	String projectType();

	
	@En("Campus")
	String campus();
	
	@En("")
	String submittalType_help();
	
	@En("Item")
	String item();
	
	@En("")
	String item_help();
	
	@En("Documentation Expected From")
	String documentationExpectedFrom();
	
	@En("")
	String documentationExpectedFrom_help();
	
	@En("Documentation Needed")
	String documentationNeeded();
	
	@En("")
	String documentationNeeded_help();
	
	@En("Documentation Type Required")
	String documentationTypeRequired();
	
	@En("")
	String documentationTypeRequired_help();
	
	@En("Submittal Deadline")
	String deadline();
	
	@En("")
	String deadline_help();
	
	@En("Requires Approval")
	String requireApproval();
	
	@En("")
	String requireApproval_help();
	
	@En("Approval Deadline")
	String approvalDeadline();
	
	@En("")
	String approvalDeadline_help();
	
	@En("Requires Sealed Copy")
	String requireSealedCopy();
	
	@En("")
	String requireSignedCopy_help();
	
	@En("Requires Hard Copy")
	String requireHardCopy();
	
	@En("")
	String requireHardCopy_help();
	
	@En("Responsible Firm")
	String responsibleFirm();
	
	@En("")
	String responsibleFirm_help();
	
	@En("Comments")
	String comments();
	
	@En("")
	String comments_help();
	
	@En("Manufacturer")
	String manufacturerFirmId();
	
	@En("")
	String manufacturerFirmId_help();
	
	@En("Manufacturers Model Number")
	String itemModelNumber();
	
	@En("")
	String itemModelNumber_help();
	
	@En("Serial Number")
	String itemSerialNumber();
	
	@En("")
	String itemSerialNumber_help();
	
	@En("Purchased")
	String itemPurchased();
	
	@En("")
	String itemPurchased_help();
	
	@En("Installed")
	String itemInstalled();
	
	@En("")
	String itemInstalled_help();
	
	@En("Location")
	String itemLocationId();
	
	@En("")
	String itemLocationId_help();
	
	@En("Specific Location")
	String itemSpecificLocation();
	
	@En("")
	String itemSpecificLocation_help();
	
	@En("Warrantor")
	String warrantyFirmId();
	
	@En("")
	String warrantyFirmId_help();
	
	@En("Warranty Terms (Years)")
	String warrantyTerms();
	
	@En("")
	String warrantyTerms_help();
	
	@En("Warranty Start")
	String warrantyStart();
	
	@En("")
	String warrantyStart_help();
	
	@En("Warranty End")
	String warrantyExpire();
	
	@En("")
	String warrantyExpire_help();
	
	@En("Inspection Needed")
	String inspectionNeeded();
	
	@En("")
	String inspectionNeeded_help();
	
	@En("Inspection Description")
	String inspectionTypeNeeded();
	
	@En("")
	String inspectionTypeNeeded_help();
	
	@En("Inspected")
	String inspected();
	
	@En("")
	String inspected_help();
	
	@En("Inspecting Firm")
	String inspectionFirmId();
	
	@En("")
	String inspectionFirmId_help();
	
	@En("Inspected By")
	String inspectorId();
	
	@En("")
	String inspectorId_help();
	
	@En("Inspector Notes")
	String inspectorNotes();
	
	@En("")
	String inspectorNotes_help();
	
	@En("Owner Witness Needed")
	String isWitnessNeeded();
	
	@En("")
	String isWitnessNeeded_help();
	
	@En("Witnessed")
	String witnessed();
	
	@En("")
	String witnessed_help();
	
	@En("Witnessed By")
	String witnessId();
	
	@En("")
	String witnessId_help();
	
	@En("Witness Notes")
	String witnessNotes();
	
	@En("")
	String witnessNotes_help();
	
	@En("Classification")
	String classification();
	
	@En("")
	String classification_help();
	
	@En("Required for Contract/Phase")
	String requiredForContract();
	
	@En("")
	String requiredForContract_help();
	
	@En("Search Keywords")
	String tags();
	
	@En("")
	String tags_help();
	
	StandardErrorText errors();
	
	CommonText common();
}
