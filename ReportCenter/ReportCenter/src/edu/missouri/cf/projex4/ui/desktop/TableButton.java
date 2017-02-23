package edu.missouri.cf.projex4.ui.desktop;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;

@SuppressWarnings("serial")
public class TableButton extends Button {
	
	private AbstractSelect table;
	
	public TableButton() {
		init();
	}
	
	public TableButton(String caption) {
		super(caption);
		init();
	}
	
	public TableButton(String caption, ClickListener listener) {
		super(caption, listener);
		init();
	}
	
	private void init() {
		addStyleName("borderless");
		setImmediate(true);
	}
	
	public AbstractSelect getTable() {
		return table;
	}
	
	public void setTable(AbstractSelect table) {
		this.table = table;
	}
	
}
