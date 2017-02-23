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
public class OracleBooleanToBooleanConverter implements Converter<Boolean, OracleBoolean> {

	@Override
	public OracleBoolean convertToModel(Boolean value, Class<? extends OracleBoolean> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {

		if (null == value || !value.booleanValue()) {
			return OracleBoolean.FALSE;
		} else {
			return OracleBoolean.TRUE;
		}

	}

	@Override
	public Boolean convertToPresentation(OracleBoolean value, Class<? extends Boolean> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
		
		if (OracleBoolean.TRUE.equals(value)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
		
	}

	@Override
	public Class<OracleBoolean> getModelType() {
		return OracleBoolean.class;
	}

	@Override
	public Class<Boolean> getPresentationType() {
		return Boolean.class;
	}

}
