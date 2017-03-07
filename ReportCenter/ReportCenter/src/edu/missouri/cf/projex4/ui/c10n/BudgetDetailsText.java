package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface BudgetDetailsText {
	
	@En("Budget Details")
	String componentName();
	
	@En("Add new Budget Detail record")
	String addButton_help();
	
	@En("Column")
	String column();
	
	@En("")
	String column_help();
	
	@En("Row")
	String row();
	
	@En("")
	String row_help();
	
	@En("Category")
	String pcsRowCategory();
	
	@En("")
	String pcsRowCategory_help();
	
	@En("Description")
	String pcsRowDescription();
	
	@En("")
	String pcsRowDescription_help();
	
	@En("Amount")
	String amount();
	
	@En("")
	String amount_help();
	
	@En("Project Id")
	String projectId();
	
	@En("Project")
	String projectNumber();
	
	CommonText common();
	
}
