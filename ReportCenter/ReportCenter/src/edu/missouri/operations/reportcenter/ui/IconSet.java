package edu.missouri.operations.reportcenter.ui;

import java.util.HashMap;

import com.vaadin.server.ThemeResource;

@SuppressWarnings("serial")
public class IconSet extends HashMap<String, ThemeResource> {
	
	{
		put("add", new ThemeResource("icons/chalkwork/basic/add_16x16.png"));
		put("edit", new ThemeResource("icons/general/small/Edit.png"));
	    put("save",new ThemeResource("icons/general/small/Save.png"));
	    put("delete",new ThemeResource("icons/general/small/Delete.png"));
	    put("cancel", new ThemeResource("icons/general/small/Cancel.png"));
	    put("database", new ThemeResource("icons/chalkwork/basic/folder_open_16x16.png"));
	    put("folder", new ThemeResource("icons/chalkwork/basic/folder_open_16x16.png"));
	    put("reschedule", new ThemeResource("icons/chalkwork/basic/clock_share_16x16.png"));
	    put("schedule", new ThemeResource("icons/chalkwork/basic/clock_16x16.png"));
	    put("reassign", new ThemeResource("icons/chalkwork/basic/user_share_16x16.png"));
	    put("report", new ThemeResource("icons/chalkwork/basic/report_16x16.png"));
	    
	}

}
