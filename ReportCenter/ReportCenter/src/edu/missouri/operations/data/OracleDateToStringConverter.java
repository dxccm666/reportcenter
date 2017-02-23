package edu.missouri.operations.data;

import java.text.ParseException;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("serial")
public class OracleDateToStringConverter implements Converter<String, OracleDate> {

	@Override
	public OracleDate convertToModel(String value,
			Class<? extends OracleDate> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		try {
			return new OracleDate(Formatter.DATE.parse(value).getTime());
		} catch (ParseException e) {
			throw new com.vaadin.data.util.converter.Converter.ConversionException(e.getMessage());
		}
	}

	@Override
	public String convertToPresentation(OracleDate value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return Formatter.DATE.format(new java.util.Date(value.getTime()));
	}

	@Override
	public Class<OracleDate> getModelType() {
		return OracleDate.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}



}
