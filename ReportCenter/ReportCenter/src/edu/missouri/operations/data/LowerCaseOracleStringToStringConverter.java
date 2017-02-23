package edu.missouri.operations.data;

import java.util.Locale;


import com.vaadin.data.util.converter.Converter;

/**
 * 
 * The caching in this converted may be unnecessary.
 * 
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class LowerCaseOracleStringToStringConverter implements Converter<String, OracleString> {

	@Override
	public Class<OracleString> getModelType() {
		return OracleString.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

	@Override
	public OracleString convertToModel(String value, Class<? extends OracleString> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if(value == null) { return null; }
		return new OracleString(value.toLowerCase());
	}

	@Override
	public String convertToPresentation(OracleString value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if(value == null || value.toString()==null) { return null; }
		return value.toString().toLowerCase();
	}

	private static LowerCaseOracleStringToStringConverter converter = new LowerCaseOracleStringToStringConverter();

	public static LowerCaseOracleStringToStringConverter get() {
		return converter;
	}

}
