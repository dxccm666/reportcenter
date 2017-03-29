package edu.missouri.operations.ui.desktop.filtertable.modulargenerator;

import com.vaadin.data.Property;

import org.tepi.filtertable.FilterFieldGenerator.IFilterTable;

import com.vaadin.data.Container.Filter;
import com.vaadin.ui.AbstractField;

public interface FilterFieldHandler {

	public void setOwner(IFilterTable owner);

	public boolean handlesField(Property<?> field);

	public boolean handlesType(Class<?> type);

	public AbstractField<?> createField(Class<?> type, Object propertyId);

	public Filter generateFilter(Property<?> field, Object propertyId, Object value);
}