package edu.missouri.operations.data;

import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("serial")
public class OracleTimestampToDateConverter implements Converter<Date, OracleTimestamp> {

	@Override
	public OracleTimestamp convertToModel(Date value,
			Class<? extends OracleTimestamp> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value != null ? new OracleTimestamp(value.getTime()) : null;
	}

	@Override
	public Date convertToPresentation(OracleTimestamp value,
			Class<? extends Date> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value != null ? new java.util.Date(value.getTime()) : null;
	}

	@Override
	public Class<OracleTimestamp> getModelType() {
		return OracleTimestamp.class;
	}

	@Override
	public Class<Date> getPresentationType() {
		return java.util.Date.class;
	}

}
