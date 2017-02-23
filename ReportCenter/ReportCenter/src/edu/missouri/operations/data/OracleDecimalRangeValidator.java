package edu.missouri.operations.data;

import com.vaadin.data.validator.RangeValidator;

@SuppressWarnings("serial")
public class OracleDecimalRangeValidator extends RangeValidator<OracleDecimal> {

	public OracleDecimalRangeValidator(String errorMessage, Class<OracleDecimal> type, OracleDecimal minValue,
			OracleDecimal maxValue) {
		super(errorMessage, type, minValue, maxValue);
	}

}
