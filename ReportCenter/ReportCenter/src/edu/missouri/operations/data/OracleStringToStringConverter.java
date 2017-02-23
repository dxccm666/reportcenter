/**
 * 
 */
package edu.missouri.operations.data;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class OracleStringToStringConverter implements Converter<String, OracleString> {

	@Override
	public OracleString convertToModel(String value,
			Class<? extends OracleString> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return new OracleString(value);
	}

	@Override
	public String convertToPresentation(OracleString value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value != null ? value.toString() : null;
	}

	@Override
	public Class<OracleString> getModelType() {
		return OracleString.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
