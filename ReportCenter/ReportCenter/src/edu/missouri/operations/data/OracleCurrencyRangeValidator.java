package edu.missouri.operations.data;

import com.vaadin.data.validator.RangeValidator;

@SuppressWarnings("serial")
public class OracleCurrencyRangeValidator extends RangeValidator<OracleCurrency> {

	public OracleCurrencyRangeValidator(String errorMessage, OracleCurrency minValue,
			OracleCurrency maxValue) {
		super(errorMessage, OracleCurrency.class, minValue, maxValue);
	}

}
