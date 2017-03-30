package edu.missouri.operations.reportcenter.ui.c10n;
import c10n.C10NMessages;
import c10n.annotations.*;

@C10NMessages
public interface StandardButtonText {
	
	@En("open")
	String openButton();
	
	@En("")
	String openButton_help();
	
	@En("add")
	String addButton();
	
	@En("")
	String addButton_help();
	
	@En("edit")
	String editButton();
	
	@En("")
	String editButton_help();
	
	@En("save")
	String saveButton();
	
	@En("")
	String saveButton_help();
	
	@En("cancel")
	String cancelButton();
	
	@En("")
	String cancelButtonDescription();
	
	@En("export")
	String exportButton();
	
	@En("")
	String exportButton_help();
	
	@En("download")
	String downloadButton();
	
	@En("first") 
	String firstButton();
	
	@En("")
	String firstButton_help();
	
	@En("earlier")
	String earlierButton();
	
	@En("")
	String earlierButton_help();
	
	@En("later")
	String laterButton();
	
	@En("")
	String laterButton_help();
	
	@En("last")
	String lastButton();
	
	@En("")
	String lastButton_help();
	
	@En("delete")
	String deleteButton();
	
	@En("")
	String deleteButton_help();
	
	@En("You must save the current record before navigating")
	String navigationMessage();
	
}
