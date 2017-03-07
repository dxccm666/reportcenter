package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface FilterComponentText {
	
	@En("")
	String filterSelect();
	
	@En("Select filter from list")
	String filterSelect_help();

	@En("")
	String showFilterControls();
	
	@En("show filter controls")
	String showFilterControls_help();

	@En("")
	String hideFilterControls();
	
	@En("hide filter controls")
	String hideFilterControls_help();
	
	@En("")
	String filterNameField();
	
	@En("filter name")
	String filterNameField_help();
	
	@En("")
	String saveFilter();
	
	@En("save filter")
	String saveFilter_help();
	
	@En("")
	String deleteFilter();
	
	@En("delete filter")
	String deleteFilter_help();
	
	@En("")
	String defaultFilter();
	
	@En("set current filter as default")
	String defaultFilter_help();
	
	@En("")
	String clearFilters();
	
	@En("clear all filters")
	String clearFilters_help();
	
	@En("")
	String addBookmark();
	
	@En("bookmark selected items") 
	String addBookmark_help();
	
	@En("")
	String removeBookmark();
	
	@En("unbookmark selected items")
	String removeBookmark_help();
	

}
