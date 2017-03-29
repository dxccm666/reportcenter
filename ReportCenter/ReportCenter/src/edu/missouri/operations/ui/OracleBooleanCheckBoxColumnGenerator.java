package edu.missouri.operations.ui;

import com.vaadin.data.Property;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

import edu.missouri.operations.data.OracleBoolean;

@SuppressWarnings("serial")
public class OracleBooleanCheckBoxColumnGenerator implements ColumnGenerator {

	public OracleBooleanCheckBoxColumnGenerator() {
	}

	private boolean editable = false;

	protected boolean isEditable() {
		return editable;
	}

	protected void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		
		OracleBooleanCheckBox cb = new OracleBooleanCheckBox();
		@SuppressWarnings("rawtypes")
		Property prop = source.getItem(itemId).getItemProperty(columnId);
		cb.setConvertedValue((OracleBoolean) prop.getValue());
		cb.setReadOnly(editable);
		return cb;
	}

}
