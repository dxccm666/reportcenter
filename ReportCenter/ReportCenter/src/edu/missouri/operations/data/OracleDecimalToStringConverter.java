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
public class OracleDecimalToStringConverter implements Converter<String, OracleDecimal> {

	@Override
	public OracleDecimal convertToModel(String value,
			Class<? extends OracleDecimal> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		try {
			if (null != value) {
				return new OracleDecimal(value);
			} else {
				return new OracleDecimal("0");
			}
		} catch (NumberFormatException e) {
			throw new com.vaadin.data.util.converter.Converter.ConversionException(e.getMessage());
		}
	}

	@Override
	public String convertToPresentation(OracleDecimal value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (null != value) {
			return Formatter.getDecimalFormat().format(value);
		} else {
			return Formatter.getDecimalFormat().format(0.0d);
		}
	}

	@Override
	public Class<OracleDecimal> getModelType() {
		return OracleDecimal.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
