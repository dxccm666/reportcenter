package edu.missouri.operations.data;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("serial")
public class OracleRawToStringConverter implements Converter<String, OracleRaw> {

	@Override
	public OracleRaw convertToModel(String value,
			Class<? extends OracleRaw> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return new OracleRaw(value);
	}

	@Override
	public String convertToPresentation(OracleRaw value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value != null) {
			return(value.toString());
		}
		return null;
	}

	@Override
	public Class<OracleRaw> getModelType() {
		return OracleRaw.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
