package edu.missouri.operations.ui.desktop.filtertable.modulargenerator;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;

import edu.missouri.operations.data.OracleString;

public class OracleLongStringFilterFieldHandler extends AbstractFilterFieldHandler {

	private static Logger logger = LoggerFactory.getLogger(OracleLongStringFilterFieldHandler.class);

	@SuppressWarnings("serial")
	public class OracleLongStringTextField extends TextField {
		public OracleLongStringTextField() {
			super();
		}
	}

	@Override
	public boolean handlesField(Property<?> field) {
		return field instanceof OracleLongStringTextField;
	}

	@Override
	public boolean handlesType(Class<?> type) {
		return (type == OracleString.class || type == String.class);
	}

	@SuppressWarnings("serial")
	@Override
	public AbstractField<?> createField(Class<?> type, Object propertyId) {
		if (logger.isTraceEnabled()) {
			logger.trace("creating text field");
		}

		final OracleLongStringTextField textField = new OracleLongStringTextField();
		if (owner.getFilterDecorator() != null) {
			if (owner.getFilterDecorator().isTextFilterImmediate(propertyId)) {
				textField.addTextChangeListener(new TextChangeListener() {

					public void textChange(TextChangeEvent event) {
						textField.setValue(event.getText());
					}
				});
				textField.setTextChangeTimeout(owner.getFilterDecorator().getTextChangeTimeout(propertyId));
			}
			if (owner.getFilterDecorator().getAllItemsVisibleString() != null) {
				textField.setInputPrompt(owner.getFilterDecorator().getAllItemsVisibleString());
			}
		}
		return textField;
	}

	@Override
	public Filter generateFilter(Property<?> field, Object propertyId, Object value) {

		if (owner.getFilterGenerator() != null) {
			Filter newFilter = owner.getFilterGenerator().generateFilter(propertyId, value);
			if (newFilter != null) {
				return newFilter;
			}
		}
		
		if(value == null) {
			return null;
		}

		String s = String.valueOf(value);
		if (s.startsWith("~")) {
			if (s.indexOf(",") != -1) {

				List<String> specificvalues = Arrays.asList(s.substring(1).split(","));
				Filter[] filters = new Filter[specificvalues.size()];
				int x = 0;
				for (String s1 : specificvalues) {
					filters[x++] = new SimpleStringFilter(propertyId, s1, true, false);
				}

				return new Or(filters);

			} else {

				return new SimpleStringFilter(propertyId, s.substring(1), true, false);
			}
			
		} else if ("".equals(s) || s==null || s.length() ==0 ) {
			
			return null;

		} else {
			return new SimpleStringFilter(propertyId, s, true, false);
		}
	}

}
