package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface TransactionsText {
	
	@En("Transaction References")
	String componentName();
	
	@En("Add new Transaction Reference")
	String addButton_help();
	
	@En("")
	String contextHelp();
	
	@En("Type")
	String type();
	
	@En("External Source")
	String extSource();
	
	@En("External Reference Number")
	String extRefNumber();
	
	@En("Comments")
	String comments();
	
	@En("Primary?")
	String isPrimary();
	
	@En("Submitted")
	String submitted();
	
	CommonText common();

}
