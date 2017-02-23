package edu.missouri.cf.projex4.ui.desktop;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;


public class LayoutResetButton extends Button {
	
	StandardTable table;
	
	public void setTable(StandardTable table) {
		this.table = table;
	}
	
	public void setTableName(String tableName) {
		if(table!=null) {
			table.setTableName(tableName);
		}
	}

	public LayoutResetButton() {
		
		setDescription("reset table layout to defaults");
		setIcon(new ThemeResource("icons/special/layoutreset.png"));
		addStyleName("borderless");
		setEnabled(true);
		setVisible(true);

		addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				table.deleteColumnSettings();
			}
		});
	
	}

}
