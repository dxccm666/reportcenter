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
public class OracleUppercaseStringToStringConverter implements Converter<String, OracleUppercaseString> {

	@Override
	public OracleUppercaseString convertToModel(String value,
			Class<? extends OracleUppercaseString> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return new OracleUppercaseString(value);
	}

	@Override
	public String convertToPresentation(OracleUppercaseString value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value.toString();
	}

	@Override
	public Class<OracleUppercaseString> getModelType() {
		return OracleUppercaseString.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
