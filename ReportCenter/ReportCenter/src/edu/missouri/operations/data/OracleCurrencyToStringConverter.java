/**
 * 
 */
package edu.missouri.operations.data;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.Notification;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class OracleCurrencyToStringConverter implements
		Converter<String, OracleCurrency> {
	
	protected final static transient Logger logger = LoggerFactory.getLogger(OracleCurrencyToStringConverter.class);
	
	private static OracleCurrencyToStringConverter converter = new OracleCurrencyToStringConverter();
	
	public static OracleCurrencyToStringConverter get() {
		return converter;
	}

	@Override
	public OracleCurrency convertToModel(String value,
			Class<? extends OracleCurrency> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		
		try {
			
			if(value==null || "".equals(value.trim())) {
				return new OracleCurrency("0");
			} else { 
				return new OracleCurrency(value.trim());
	//			return new OracleCurrency(Formatter.getCurrencyFormat().parse(value.trim()).toString());
			} 
			
		} catch (NumberFormatException nfe) {
			
			Notification.show("Currency value is incorrect");
			if(logger.isDebugEnabled()) {
				logger.debug("NFE THROWN, value = {}, message = {}", new Object[]{value.toString(), nfe.getMessage()});
			}
			throw new com.vaadin.data.util.converter.Converter.ConversionException(
					"Currency value is incorrect - " + (nfe.getMessage()!=null ? nfe.getMessage() : ""));
			
		}
	}

	@Override
	public String convertToPresentation(OracleCurrency value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		
		if (null != value) {
			return Formatter.getCurrencyFormat().format(value.doubleValue());
		} else {
			return "";
		}
	}

	@Override
	public Class<OracleCurrency> getModelType() {
		return OracleCurrency.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
