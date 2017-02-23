package edu.missouri.operations.data;

import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("serial")
public class OracleDateToDateConverter implements Converter<Date, OracleDate> {

	@Override
	public OracleDate convertToModel(Date value, Class<? extends OracleDate> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value != null) {
			return new OracleDate(value.getTime());
		} else {
			return null;
		}
	}

	@Override
	public Date convertToPresentation(OracleDate value, Class<? extends Date> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value != null) {
			return new java.util.Date((value).getTime());
		} else {
			return null;
		}
	}

	@Override
	public Class<OracleDate> getModelType() {
		return OracleDate.class;
	}

	@Override
	public Class<Date> getPresentationType() {
		return java.util.Date.class;
	}

}
