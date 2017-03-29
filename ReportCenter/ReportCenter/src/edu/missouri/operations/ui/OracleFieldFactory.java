package edu.missouri.operations.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;

import edu.missouri.operations.data.OracleBoolean;
import edu.missouri.operations.data.OracleCurrency;
import edu.missouri.operations.data.OracleDate;
import edu.missouri.operations.data.OracleString;
import edu.missouri.operations.data.OracleTimestamp;

@SuppressWarnings({ "serial" })
public class OracleFieldFactory extends DefaultFieldFactory implements TableFieldFactory {

	private static final OracleFieldFactory instance = new OracleFieldFactory();
	private static final transient Logger logger = LoggerFactory.getLogger(OracleFieldFactory.class);

	class FieldStyles {

		Class<?> fieldClass;
		String fieldStyle;

		FieldStyles(Class<?> fieldClass, String fieldStyle) {
			this.fieldClass = fieldClass;
			this.fieldStyle = fieldStyle;
		}

	}

	HashMap<String, ArrayList<ValueChangeListener>> listeners = new HashMap<String, ArrayList<ValueChangeListener>>();

	public void addListener(String propertyId, ValueChangeListener l) {

		if (listeners.containsKey(propertyId)) {
			ArrayList<ValueChangeListener> a = listeners.get(propertyId);
			a.add(l);
		} else {
			ArrayList<ValueChangeListener> a = new ArrayList<ValueChangeListener>();
			a.add(l);
			listeners.put(propertyId, a);
		}
	}

	// TODO probably need to implement removeListener also

	public OracleFieldFactory() {

		// Common Oracle Types
		assign(OracleBoolean.class, OracleBooleanCheckBox.class);
		assign(OracleTimestamp.class, OracleTimestampField.class);
		assign(OracleDate.class, OracleDateField.class);
		assign(OracleCurrency.class, OracleCurrencyField.class);
		assign(OracleString.class, TextField.class);
		// assign(OracleInteger.class, OracleIntegerField.class);
		// assign(OracleDecimal.class, OracleDecimalField.class);

	}

	public static OracleFieldFactory get() {
		return instance;
	}

	LinkedHashMap<String, FieldStyles> presetFields = new LinkedHashMap<String, FieldStyles>();
	LinkedHashMap<Class<?>, FieldStyles> presetTypes = new LinkedHashMap<Class<?>, FieldStyles>();
	LinkedHashMap<String, String> constructorFieldParameters = new LinkedHashMap<String, String>();
	LinkedHashMap<Class<?>, String> constructorTypeParameters = new LinkedHashMap<Class<?>, String>();

	public void assign(Class<?> clazz, Class<?> fieldclazz, String styles, String constructorParameter) {
		presetTypes.put(clazz, new FieldStyles(fieldclazz, (styles != null) ? styles : ""));
		if (constructorParameter != null) {
			constructorTypeParameters.put(clazz, constructorParameter);
		}
	}

	public void assign(Class<?> clazz, Class<?> fieldclazz, String styles) {
		assign(clazz, fieldclazz, styles, null);
	}

	public void assign(Class<?> clazz, Class<?> fieldclazz) {
		assign(clazz, fieldclazz, null, null);
	}

	public void assign(String propertyId, Class<?> fieldclazz, String styles, String constructorParameter) {
		logger.debug("Assign3 called");
		presetFields.put(propertyId, new FieldStyles(fieldclazz, (styles != null) ? styles : ""));
		if (constructorParameter != null) {
			constructorFieldParameters.put(propertyId, constructorParameter);
		}
	}

	public void assign(String propertyId, Class<?> fieldclazz, String styles) {
		assign(propertyId, fieldclazz, styles, null);
	}

	public void assign(String propertyId, Class<?> fieldclazz) {
		assign(propertyId, fieldclazz, null, null);
	}

	LinkedHashMap<String, Class<? extends Converter<String, ?>>> presetConverters = new LinkedHashMap<String, Class<? extends Converter<String, ?>>>();

	public void assignConverter(String propertyId, Class<? extends Converter<String, ?>> converter) {
		presetConverters.put(propertyId, converter);
	}

	private Field<?> getAssignedFieldForPropertyId(String propertyId) throws Exception {

		Field<?> field = null;
		FieldStyles f = presetFields.get(propertyId);
		if (f != null && f.fieldClass != null) {

			try {
				String constructorParameter = constructorFieldParameters.get(propertyId);
				if (constructorParameter == null) {
					field = (Field<?>) f.fieldClass.getDeclaredConstructor().newInstance();
				} else {
					field = (Field<?>) f.fieldClass.getDeclaredConstructor(String.class).newInstance(constructorParameter);
				}

			} catch (NoSuchMethodException e) {
				if(logger.isErrorEnabled()) {
					//logger.error("Error constructing field {} for {}", f.fieldClass.getSimpleName(), propertyId, e);
				}
			}

		}
		return field;

	}

	private Field<?> getAssignedFieldForType(Class<?> type) throws Exception {

		/*
		 * Check to see if type exactly equals one of the keys - if so use that.
		 * If not, then check to see if there is a key class that is assignable
		 * from the type.
		 */

		Field<?> field = null;

		for (Class<?> assignedClass : presetTypes.keySet()) {
			if (assignedClass.equals(type)) {
				FieldStyles f = presetTypes.get(assignedClass);
				field = (Field<?>) f.fieldClass.getConstructor().newInstance();
				field.addStyleName(f.fieldStyle);
				if(type.isAssignableFrom(OracleCurrency.class)) {
					field.addStyleName("currency");
				} 
				if (type.isAssignableFrom(BigDecimal.class)) {
					field.addStyleName("numerical");
				}
				return field;
			}
		}

		for (Class<?> assignedClass : presetTypes.keySet()) {
			if (assignedClass.isAssignableFrom(type)) {
				FieldStyles f = presetTypes.get(assignedClass);
				field = (Field<?>) f.fieldClass.getConstructor().newInstance();
				if(type.isAssignableFrom(OracleCurrency.class)) {
					field.addStyleName("currency");
				} 
				if (type.isAssignableFrom(BigDecimal.class)) {
					field.addStyleName("numerical");
				}
				return field;
			}
		}

		return null;

	}

	private boolean existingLinesEditable = true;
	private HashSet<Object> readOnlyFields = new HashSet<Object>();
	private HashSet<Object> alwaysEditableFields = new HashSet<Object>();

	public void setReadOnly(Object propertyId) {
		readOnlyFields.add(propertyId);
	}

	public void setAlwaysEditable(Object propertyId) {
		alwaysEditableFields.add(propertyId);
	}

	/**
	 * @return the existingLinesEditable
	 */
	public boolean isExistingLinesEditable() {
		return existingLinesEditable;
	}

	/**
	 * @param existingLinesEditable
	 *            the existingLinesEditable to set
	 */
	public void setExistingLinesEditable(boolean existingLinesEditable) {
		this.existingLinesEditable = existingLinesEditable;
	}

	class FieldFocusListener implements FocusListener {

		Object itemId;
		Component uiContext;

		public FieldFocusListener(Object itemId, Component uiContext) {

			this.itemId = itemId;
			this.uiContext = uiContext;

		}

		@Override
		public void focus(FocusEvent event) {

			if (uiContext instanceof Table) {
				((Table) uiContext).setValue(itemId);
			}

		}

	}

	@Override
	public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {

		Property<?> containerProperty = container.getContainerProperty(itemId, propertyId);
		Class<?> type = containerProperty.getType();

		Field<?> field = null;

		if (!readOnlyFields.contains(propertyId)) {

			try {

				field = getAssignedFieldForPropertyId(propertyId.toString());

				if (field == null) {
					field = getAssignedFieldForType(type);
				}

				if (field != null) {

					/* This type is handled by this FieldFactory */

					/* CheckBoxes shouldn't have captions inside tables */
					if (!OracleBooleanCheckBox.class.isAssignableFrom(field.getClass()))
						field.setCaption(DefaultFieldFactory.createCaptionByPropertyId(propertyId));

				} else {

					/* Generate default field for this type */

					field = DefaultFieldFactory.get().createField(container, itemId, propertyId, uiContext);
					field.addStyleName("leftjustified");
				}

			} catch (Exception e) {
				e.printStackTrace();

				field = DefaultFieldFactory.get().createField(container, itemId, propertyId, uiContext);
				field.addStyleName("leftjustified");
			}

			if (readOnlyFields.contains(propertyId)) {
				
				field.setReadOnly(true);
				field.addStyleName("readonlyfield");
				
			} else if (alwaysEditableFields.contains(propertyId)) {
				field.setReadOnly(false);
			} else if (!existingLinesEditable) {
				Item item = container.getItem(itemId);
				if (item instanceof RowItem) {
					field.setReadOnly(!(((RowItem) item).getId() instanceof TemporaryRowId));
				}
			}

			if (field instanceof TextField) {
				((TextField) field).setNullRepresentation("");
				((TextField) field).addFocusListener(new FieldFocusListener(itemId, uiContext));
			}

			if (listeners.containsKey(propertyId)) {
				ArrayList<ValueChangeListener> a = listeners.get(propertyId);
				for (ValueChangeListener l : a) {
					field.addValueChangeListener(l);
				}
			}

		} else {

			// LabelField label = new LabelField();
			TextField label = new TextField();
			label.setImmediate(true);
			label.addFocusListener(new FieldFocusListener(itemId, uiContext));
			label.setNullRepresentation("");

			if (presetConverters.containsKey(propertyId)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Applying converter");
				}
				try {
					
					Converter<String, ?> converter = presetConverters.get(propertyId).newInstance();
					label.setConverter(converter);

				} catch (InstantiationException | IllegalAccessException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Could not instantiate new converter", e);
					}
				}

			}

			field = label;
			field.setReadOnly(true);

		}

		field.setWidth("100%");

		return field;
	}

}
