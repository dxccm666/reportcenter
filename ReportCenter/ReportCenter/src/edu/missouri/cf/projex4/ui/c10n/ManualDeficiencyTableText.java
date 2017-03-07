package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface ManualDeficiencyTableText {

	@En("#")
	String rowNumber();
	
	@En("Type")
	String deficiencyType();
	
	@En("")
	String deficiencyType_help();
	
	@En("Classification")
	String classification();
	
	@En("")
	String classification_help();
	
	@En("Reported By")
	String reportedBy();
	
	@En("")
	String reportedBy_help();
	
	@En("Reported")
	String reported();
	
	@En("")
	String reported_help();
	
	@En("Resolved By")
	String resolvedBy();
	
	@En("")
	String resolvedBy_help();
	
	@En("Resolved")
	String resolved();
	
	@En("")
	String resolved_help();
	
	CommonText common();
	
	StandardButtonText buttons();
	
}
