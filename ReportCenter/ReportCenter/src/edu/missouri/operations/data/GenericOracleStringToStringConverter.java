package edu.missouri.operations.data;

import java.util.HashMap;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("serial")
public abstract class GenericOracleStringToStringConverter<T extends OracleString, V extends ConverterDetail> implements Converter<String, T> {

	private Class<T> clazz;
	
	private static HashMap<OracleString, ConverterDetail> idCache = new HashMap<>();
	
	private T currentId;
	
	private boolean secondaryDisplayName = false;
	
	public GenericOracleStringToStringConverter(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	public GenericOracleStringToStringConverter(Class<T> clazz, boolean secondaryDisplayName) {
		this.clazz = clazz;
		this.secondaryDisplayName = secondaryDisplayName;
	}
	
	@Override
	public T convertToModel(String value, Class<? extends T> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return currentId;
	}

	@Override
	public String convertToPresentation(T value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		currentId = value;
		if (value != null) {
			ConverterDetail detail = getDetail(value);
			if (detail != null) {
				if (secondaryDisplayName) {
					return detail.getSecondaryDisplayName();
				}
				return detail.getDisplayName();
			}
		}
		return "";
	}

	@Override
	public Class<T> getModelType() {
		return clazz;
	}
	
	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

	private ConverterDetail getDetail(T value) {
		if (value != null) {
			ConverterDetail detail = null;
			if ((detail = idCache.get(value)) == null) {
				detail = getTypeDetail(value);
				setDetail(detail);
			}
			return detail;
		} else {
			return null;
		}
	}

	private void setDetail(ConverterDetail detail) {
		if (detail != null) {
			T id = newT(detail.getObjectId());
			idCache.put(id, detail);
		}
	}
	
	public T newT(String value) {
		try {
			T id = clazz.newInstance();
			id.setValue(value);
			return id;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public abstract ConverterDetail getTypeDetail(T value);

	public boolean isSecondaryDisplayName() {
		return secondaryDisplayName;
	}

	public void setSecondaryDisplayName(boolean secondaryDisplayName) {
		this.secondaryDisplayName = secondaryDisplayName;
	}
	
}
