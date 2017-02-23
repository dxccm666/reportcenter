package edu.missouri.operations.data;

import com.vaadin.data.validator.AbstractValidator;

@SuppressWarnings("serial")
public abstract class AbstractOracleStringValidator extends AbstractValidator<OracleString> {

	public AbstractOracleStringValidator(String errorMessage) {
		super(errorMessage);
	}
	
	@Override
	public Class<OracleString> getType() {
		return OracleString.class;
	}
	
}
