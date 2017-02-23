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
public class OracleRowNumberToStringConverter implements Converter<String, OracleRowNumber> {

	@Override
	public OracleRowNumber convertToModel(String value,
			Class<? extends OracleRowNumber> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		try {
			if (null != value) {
				return new OracleRowNumber(value);
			} else {
				return new OracleRowNumber("0");
			}
		} catch (NumberFormatException e) {
			throw new com.vaadin.data.util.converter.Converter.ConversionException(e.getMessage());
		}
	}

	@Override
	public String convertToPresentation(OracleRowNumber value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return Formatter.getIntegerFormat().format(value);
	}

	@Override
	public Class<OracleRowNumber> getModelType() {
		return OracleRowNumber.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
