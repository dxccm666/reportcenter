package edu.missouri.operations.reportcenter.ui.c10n;

import java.sql.Date;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface StandardErrorText {
	
	@En("{0} must be between {1} and {2} characters in length.")
	String lengthError(String fieldName, int minLength, int maxLength);
	
	@En("{0} date must be between {1} and {2}.")
	String dateError(String fieldName, Date minDate, Date maxDate);
	
	@En("{0} must be between {1} and {2}.")
	String numberError(String fieldName, double min, double max);
	
	@En("{0} must be a valid PeopleSoftAccount")
	String accountError(String fieldName);

}
