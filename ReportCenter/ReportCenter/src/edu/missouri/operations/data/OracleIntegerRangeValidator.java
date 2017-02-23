package edu.missouri.operations.data;

import com.vaadin.data.validator.RangeValidator;

@SuppressWarnings("serial")
public class OracleIntegerRangeValidator extends RangeValidator<OracleInteger> {

	public OracleIntegerRangeValidator(String errorMessage, Class<OracleInteger> type, OracleInteger minValue,
			OracleInteger maxValue) {
		super(errorMessage, type, minValue, maxValue);
	}

}
