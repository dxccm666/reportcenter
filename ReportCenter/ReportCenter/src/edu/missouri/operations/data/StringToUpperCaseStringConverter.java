package edu.missouri.operations.data;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("serial")
public class StringToUpperCaseStringConverter implements Converter<String, String> {
	
	static StringToUpperCaseStringConverter me = new StringToUpperCaseStringConverter();
	
	public static StringToUpperCaseStringConverter get() {
		return me;
	}

	@Override
	public String convertToModel(String value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		
		if(value!=null) {
			return value.toUpperCase();
		}
		return null;
	}

	@Override
	public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if(value!=null) {
			return value.toUpperCase();
		}
		return null;
	}

	@Override
	public Class<String> getModelType() {
		return String.class; 
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
