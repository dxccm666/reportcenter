package edu.missouri.operations.ui.desktop.filtertable.modulargenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.In;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;

import edu.missouri.operations.data.OracleString;

public class OracleStringFilterFieldHandler extends AbstractFilterFieldHandler {
	
	private static Logger logger = LoggerFactory.getLogger(OracleStringFilterFieldHandler.class);

	@SuppressWarnings("serial")
	public class OracleStringTextField extends TextField {
		public OracleStringTextField() {
			super();
		}
	}

	@Override
	public boolean handlesField(Property<?> field) {
		return field instanceof OracleStringTextField;
	}

	@Override
	public boolean handlesType(Class<?> type) {
		return (type == OracleString.class || type == String.class);
	}

	@SuppressWarnings("serial")
	@Override
	public AbstractField<?> createField(Class<?> type, Object propertyId) {
		if(logger.isTraceEnabled()) {
			logger.trace("creating text field");
		}

		final OracleStringTextField textField = new OracleStringTextField();
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
		
		String s = String.valueOf(value);
		if(s.startsWith("!=")) {
			if(s.indexOf(";")!=-1) {
				
				List<String> specificvalues = Arrays.asList(s.substring(2).split(";"));
				boolean nullfound = false;
				ArrayList<String> notnulls = new ArrayList<String>();
				for(String s1 : specificvalues) {
					if("null".equals(s1.toLowerCase())) {
						nullfound = true;
					} else {
						if(s1.trim().startsWith("!=")) {
							notnulls.add(s1.substring(2).trim());
						} else {
							notnulls.add(s1.trim());
						}
					}
				}
				
				if(nullfound) {
					return new Not(new Or(new In((String) propertyId, notnulls), new IsNull((String) propertyId)));
				} else {
					return new Or(new Not(new In((String) propertyId, notnulls)), new IsNull((String) propertyId));
				}
				
			} else {
				String s1 = s.substring(2).trim();
				if("null".equals(s1.toLowerCase())) {
					return new Not(new IsNull((String) propertyId));
				}
				return new Not(new Compare.Equal(propertyId, s.substring(2)));
			}
			
		} else if(s.startsWith("=")) {
			
			if(s.indexOf(";")!=-1) {
				
				List<String> specificvalues = Arrays.asList(s.substring(1).split(";"));
				
				boolean nullfound = false;
				ArrayList<String> notnulls = new ArrayList<String>();
				for(String s1 : specificvalues) {
					if("null".equals(s1.toLowerCase())) {
						nullfound = true;
					} else {
						if(s1.trim().startsWith("=")) {
							notnulls.add(s1.substring(2).trim());
						} else {
							notnulls.add(s1.trim());
						}
					}
				}
				
				if(nullfound) {
					return new Or(new In((String) propertyId, notnulls), new IsNull((String) propertyId));
				} else {
					return new In((String) propertyId, specificvalues);
				}
				
			} else {
				
				String s1 = s.substring(1);
				if("null".equals(s1.toLowerCase().trim())) {
					return new IsNull((String) propertyId);
				} else {
					return new Compare.Equal(propertyId, s1.trim());
				}
				
			}
			
		} else if ("".equals(s) || s==null || s.length() ==0 ) {
			
			return null;
			
		} else {
			return new SimpleStringFilter(propertyId, s, true, false);
		}
	}

}
