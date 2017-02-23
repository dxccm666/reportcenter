package edu.missouri.operations.ui.common;

import com.vaadin.data.Property;
import com.vaadin.ui.TextField;

import c10n.C10N;
import edu.missouri.operations.data.OracleCurrency;
import edu.missouri.operations.data.OracleCurrencyRangeValidator;

public class OracleCurrencyEditor extends TextField {
	
	public OracleCurrencyEditor() {
	}

	public OracleCurrencyEditor(String caption) {
		super(caption);
	}

	@SuppressWarnings("rawtypes")
	public OracleCurrencyEditor(Property dataSource) {
		super(dataSource);
	}

	@SuppressWarnings("rawtypes")
	public OracleCurrencyEditor(String caption, Property dataSource) {
		super(caption, dataSource);
	}

	public OracleCurrencyEditor(String caption, String value) {
		super(caption, value);
	}
	
	protected void setValidator(final double minValue, final double maxValue) {
		

		addValidator(new OracleCurrencyRangeValidator("Out of acceptable range",
				new OracleCurrency(minValue), new OracleCurrency(maxValue)));
		
		setValidationVisible(true);
		
	}

}
