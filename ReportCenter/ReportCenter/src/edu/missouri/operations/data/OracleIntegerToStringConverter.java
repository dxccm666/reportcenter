/**
 * 
 */
package edu.missouri.operations.data;

import java.math.BigDecimal;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class OracleIntegerToStringConverter implements Converter<String, OracleInteger> {

	@Override
	public OracleInteger convertToModel(String value,
			Class<? extends OracleInteger> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		try {
			if (null != value) {
				return new OracleInteger(value);
			} else {
				return new OracleInteger(BigDecimal.ZERO);
			}
		} catch (NumberFormatException e) {
			throw new com.vaadin.data.util.converter.Converter.ConversionException("Value needs to be an integer", e);
		}
	}

	@Override
	public String convertToPresentation(OracleInteger value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		
		if(value==null) return "";
		
		return Formatter.getIntegerFormat().format(value);
	}

	@Override
	public Class<OracleInteger> getModelType() {
		return OracleInteger.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
