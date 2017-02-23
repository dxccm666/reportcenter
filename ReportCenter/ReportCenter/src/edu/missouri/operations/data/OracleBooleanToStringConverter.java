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
public class OracleBooleanToStringConverter implements Converter<String, OracleBoolean> {

	/**
	 * 
	 */
	public OracleBooleanToStringConverter() { }

	/* (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.util.Locale)
	 */
	@Override
	public OracleBoolean convertToModel(String value,
			Class<? extends OracleBoolean> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		
		if("true".equals(value)) {
			return OracleBoolean.TRUE;
		} else {
			return OracleBoolean.FALSE;
		}
	}

	/* (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.util.Locale)
	 */
	@Override
	public String convertToPresentation(OracleBoolean value, Class<? extends String> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
		return (null!=value ? value.toString() : null);
	}

	/* (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#getModelType()
	 */
	@Override
	public Class<OracleBoolean> getModelType() {
		return OracleBoolean.class;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.data.util.converter.Converter#getPresentationType()
	 */
	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
