package edu.missouri.operations.data;

import java.util.Date;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;
import com.vaadin.data.util.sqlcontainer.RowId;

import oracle.sql.ROWID;

@SuppressWarnings("serial")
public class OracleConverterFactory extends DefaultConverterFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> createConverter(Class<PRESENTATION> presentationType,
			Class<MODEL> modelType) {

		if (String.class == presentationType) {

			// Still need to do Strings from text areas

			if (OracleBoolean.class == modelType) {
				return (Converter<PRESENTATION, MODEL>) new OracleBooleanToStringConverter();
			}

			if (OracleCurrency.class == modelType) {
				return (Converter<PRESENTATION, MODEL>) OracleCurrencyToStringConverter.get();
			}

			if (OracleDate.class == modelType) {
				return (Converter<PRESENTATION, MODEL>) new OracleDateToStringConverter();
			}

			if (OracleDecimal.class == modelType) {
				return (Converter<PRESENTATION, MODEL>) new OracleDecimalToStringConverter();
			}

			if (OracleInteger.class == modelType) {
				return (Converter<PRESENTATION, MODEL>) new OracleIntegerToStringConverter();
			}

			if (OracleTimestamp.class == modelType) {
				return (Converter<PRESENTATION, MODEL>) new OracleTimestampToStringConverter();
			}

			if (OracleString.class == modelType) {
				return (Converter<PRESENTATION, MODEL>) new OracleStringToStringConverter();
			}

		} else if (Date.class == presentationType) {

			if (OracleDate.class == modelType) {
				return (Converter<PRESENTATION, MODEL>) new OracleDateToDateConverter();
			}

			if (OracleTimestamp.class == modelType) {
				return (Converter<PRESENTATION, MODEL>) new OracleTimestampToDateConverter();
			}

		} else if (Boolean.class == presentationType) {

			if (OracleBoolean.class == modelType) {
				return (Converter<PRESENTATION, MODEL>) new OracleBooleanToBooleanConverter();
			}

		}

		return super.createConverter(presentationType, modelType);
	}

}
