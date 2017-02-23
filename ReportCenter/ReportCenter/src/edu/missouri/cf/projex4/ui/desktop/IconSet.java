package edu.missouri.cf.projex4.ui.desktop;

import java.util.HashMap;

import com.vaadin.server.ThemeResource;

@SuppressWarnings("serial")
public class IconSet extends HashMap<String, ThemeResource> {
	
	{
		put("add", new ThemeResource("icons/chalkwork/basic/add_16x16.png"));
		put("edit", new ThemeResource("icons/general/small/Edit.png"));
	    put("save",new ThemeResource("icons/general/small/Save.png"));
	    put("delete",new ThemeResource("icons/general/small/Delete.png"));
	    put("database", new ThemeResource("icons/chalkwork/basic/folder_open_16x16.png"));
	    put("folder", new ThemeResource("icons/chalkwork/basic/folder_open_16x16.png"));
	}

}
