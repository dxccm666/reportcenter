package edu.missouri.operations.ui;

import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;

import edu.missouri.operations.data.OracleBoolean;
import edu.missouri.operations.data.OracleBooleanToBooleanConverter;

@SuppressWarnings("serial")
public class OracleBooleanCheckBox extends CheckBox {
	
	public OracleBooleanCheckBox() {
		super();
		setConverter(new OracleBooleanToBooleanConverter());
	}
	
	public OracleBooleanCheckBox(String caption, OracleBoolean initialState) {
		super(caption);
		setConverter(new OracleBooleanToBooleanConverter());
		setConvertedValue(initialState);
		
	}

	public OracleBooleanCheckBox(String caption, boolean initialState) {
		super(caption, initialState);
		setConverter(new OracleBooleanToBooleanConverter());
	}

	public OracleBooleanCheckBox(String caption, Property<?> dataSource) {
		super(caption);
		setPropertyDataSource(dataSource);
		setConverter(new OracleBooleanToBooleanConverter());
	}

	public OracleBooleanCheckBox(String caption) {
		super(caption);
		setConverter(new OracleBooleanToBooleanConverter());
	}
	
}
