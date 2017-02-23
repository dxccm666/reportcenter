package edu.missouri.operations.data;

import java.text.ParseException;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("serial")
public class OracleTimestampToStringConverter implements Converter<String, OracleTimestamp> {

	@Override
	public OracleTimestamp convertToModel(String value,
			Class<? extends OracleTimestamp> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		try {
			return value != null ? new OracleTimestamp(Formatter.TIMESTAMP.parse(value).getTime()) : null;
		} catch (ParseException e) {
			throw new com.vaadin.data.util.converter.Converter.ConversionException(e.getMessage());
		}
	}

	@Override
	public String convertToPresentation(OracleTimestamp value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
			return value != null ? Formatter.TIMESTAMP.format(new java.util.Date(value.getTime())) : null;
	}

	@Override
	public Class<OracleTimestamp> getModelType() {
		return OracleTimestamp.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
