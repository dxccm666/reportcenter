package edu.missouri.cf.projex4.ui.desktop;

import com.vaadin.ui.HorizontalLayout;

public class LayoutControls extends HorizontalLayout {

	protected LayoutButton layoutButton;
	protected LayoutResetButton layoutResetButton;
	StandardTable table;

	public LayoutControls() {
		
		layoutButton = new LayoutButton();
		layoutResetButton = new LayoutResetButton();
		
		setMargin(false);
		setSpacing(false);
		
		addComponent(layoutButton);
		addComponent(layoutResetButton);
		
	}
	
	public void setTable(StandardTable table) {
		this.table = table;
		layoutButton.setTable(table);
		layoutResetButton.setTable(table);
	}
	
	public void setTableName(String tableName) {
		if(table!=null) {
			table.setTableName(tableName);
			layoutButton.setTableName(tableName);
			layoutResetButton.setTableName(tableName);
		}
	}

}
