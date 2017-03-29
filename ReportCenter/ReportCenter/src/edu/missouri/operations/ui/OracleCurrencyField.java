package edu.missouri.operations.ui;

import com.vaadin.data.Property;
import com.vaadin.ui.TextField;

import edu.missouri.operations.data.OracleCurrency;

@SuppressWarnings("serial")
public class OracleCurrencyField extends TextField {

	public OracleCurrencyField() {
		init();
	}

	public OracleCurrencyField(String caption) {
		super(caption);
		init();
	}

	@SuppressWarnings("rawtypes")
	public OracleCurrencyField(Property dataSource) {
		super(dataSource);
		init();
	}

	@SuppressWarnings("rawtypes")
	public OracleCurrencyField(String caption, Property dataSource) {
		super(caption, dataSource);
		init();
	}

	public OracleCurrencyField(String caption, String value) {
		super(caption, value);
		init();
	}
	
	private void init() {
		setConverter(OracleCurrency.class);
		setInvalidAllowed(false);
		setInvalidCommitted(false);
		setValidationVisible(true);
		addStyleName("rightjustified monospaced");
		//TODO C10N here.
		setNullRepresentation("$0.00");
		
	}

}
