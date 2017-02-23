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
public class OracleReferenceToStringConverter implements Converter<String, OracleReference> {

	@Override
	public OracleReference convertToModel(String value,
			Class<? extends OracleReference> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if(null == value || value.length() == 0 || value.length() > 10) {
			throw new com.vaadin.data.util.converter.Converter.ConversionException("Reference format incorrect");
		}
		
		return new OracleReference(value);
	}

	@Override
	public String convertToPresentation(OracleReference value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value.toString();
	}

	@Override
	public Class<OracleReference> getModelType() {
		return OracleReference.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
