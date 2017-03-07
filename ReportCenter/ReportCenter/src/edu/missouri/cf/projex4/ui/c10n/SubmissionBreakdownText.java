package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface SubmissionBreakdownText {
	
	@En("Submission Breakdown")
	String screenName();
	
	@En("Add New Submission Breakdown")
	String addButtonDescription();
	
	SubmissionBreakdownTableText table();
	
	CommonText common();
	
	StandardButtonText buttons();

	@En("Column")
	String column();
	
	@En("Row")
	String row();

	@En("Title")
	String title();
	
	@En("Request ID")
	String breakdownid();
	
	@En("Project Number")
	String projectNumber();
	
	@En("Row Title")
	String rowtitle();
	
	@En("Amount")
	String amount();

}
