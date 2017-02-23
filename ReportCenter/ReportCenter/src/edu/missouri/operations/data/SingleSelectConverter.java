package edu.missouri.operations.data;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.AbstractSelect;

/**
 * Right now this only works for BeanContainer<String, ListBean>.
 * 
 * @author reynoldsjj
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public class SingleSelectConverter<T> implements Converter<Object, OracleString> {

	private final AbstractSelect select;
	transient final protected static Logger logger = LoggerFactory.getLogger(SingleSelectConverter.class);

	public SingleSelectConverter(AbstractSelect select) {
		this.select = select;
	}

	@SuppressWarnings("unchecked")
	private BeanContainer<String, T> getContainer() {
		return (BeanContainer<String, T>) select.getContainerDataSource();
	}

	@Override
	public OracleString convertToModel(Object value, Class<? extends OracleString> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {

		if (value!=null && value != select.getNullSelectionItemId() && select.size() != 0) {
			try {
				
				if (logger.isTraceEnabled()) {
					logger.trace("convertToModel({}) returns {}", new Object[] { value, getContainer().getItem(value).getBean() });
				}
				
				if (getContainer() != null && getContainer().getItem(value) != null
						&& getContainer().getItem(value).getBean() != null) {
					return new OracleString(getContainer().getItem(value).getBean().toString());
				} else {
					return null;
				}
				
			} catch (NullPointerException npe) {
				logger.error("Unable to get Bean value for {}", value, npe);
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public Object convertToPresentation(OracleString value, Class<? extends Object> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (logger.isTraceEnabled()) {
			logger.trace("convertToPresentation({}) returns {}", new Object[] { value, String.valueOf(value) });
			logger.trace("getNullSelectionItemId = {}", select.getNullSelectionItemId());
		}
		if (value != null) {
			return value.toString();
		}
		return select.getNullSelectionItemId();
	}

	public Class<OracleString> getModelType() {
		if (logger.isTraceEnabled()) {
			logger.trace("getModelType()");
		}
		return OracleString.class;
	}

	public Class<Object> getPresentationType() {
		if (logger.isTraceEnabled()) {
			logger.trace("getPresentationType");
		}
		return Object.class;
	}

}
