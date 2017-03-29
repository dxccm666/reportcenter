package edu.missouri.operations.ui;

import com.vaadin.data.Property;
import com.vaadin.ui.TextField;

import edu.missouri.operations.data.OracleDecimal;

@SuppressWarnings("serial")
public class OracleDecimalField extends TextField {

	public OracleDecimalField() {
		init();
	}

	public OracleDecimalField(String caption) {
		super(caption);
		init();
	}

	@SuppressWarnings("rawtypes")
	public OracleDecimalField(Property dataSource) {
		super(dataSource);
		init();
	}

	@SuppressWarnings("rawtypes")
	public OracleDecimalField(String caption, Property dataSource) {
		super(caption, dataSource);
		init();
	}

	public OracleDecimalField(String caption, String value) {
		super(caption, value);
		init();
	}
	
	private void init() {
		setConverter(OracleDecimal.class);
		setInvalidAllowed(false);
		setInvalidCommitted(false);
		setValidationVisible(true);
		addStyleName("rightjustified monospaced");
		setNullRepresentation("0");
		
	}

}
