package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface DefaultFoldersText {
	
	@En("Default Folders") 
	String screenName();
	
	@En("Add New Default Folder")
	String addButton_help();
	
	@En("Screen Name")
	String screen();
	
	@En("Parent Screen Name")
	String parentscreen();
	
	@En("Folder Name")
	String folderName();
	
	@En("Description")
	String description();
	
	@En("is top level?")
	String isTop();
	
	@En("")
	String contextHelp();
	
	CommonText common();
	
	StandardErrorText errors();

}
