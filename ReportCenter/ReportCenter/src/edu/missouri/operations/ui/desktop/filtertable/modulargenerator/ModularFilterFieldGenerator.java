package edu.missouri.operations.ui.desktop.filtertable.modulargenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tepi.filtertable.FilterFieldGenerator;
import org.tepi.filtertable.FilterTable;
import org.tepi.filtertable.paged.PagedFilterTable;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

public class ModularFilterFieldGenerator extends FilterFieldGenerator {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public Map<AbstractField<?>, Object> fields = new HashMap<AbstractField<?>, Object>();

	/* ValueChangeListener for filter components */
	private ValueChangeListener listener = initializeListener();

	private ArrayList<FilterFieldHandler> handlers = new ArrayList<FilterFieldHandler>();

	private boolean debug = false;

	protected void addHandler(FilterFieldHandler handler) {
		handlers.add(handler);
		handler.setOwner(owner);
	}

	public ModularFilterFieldGenerator(IFilterTable owner) {
		this.owner = owner;
		addHandler(new BooleanFilterFieldHandler());
		addHandler(new EnumFilterFieldHandler());
		addHandler(new NumberFilterFieldHandler());
		addHandler(new DateFilterFieldHandler());
	}

	public void clearFilterData() {
		/* Remove all filters from container */
		for (Object propertyId : filters.keySet()) {
			if (owner.getFilterable() != null) {
				owner.getFilterable().removeContainerFilter(filters.get(propertyId));
			}
			if (owner.getFilterGenerator() != null) {
				owner.getFilterGenerator().filterRemoved(propertyId);
			}
		}
		/* Remove listeners */
		for (AbstractField<?> af : customFields.keySet()) {
			af.removeValueChangeListener(listener);
		}

		for (AbstractField<?> field : fields.keySet()) {
			field.removeValueChangeListener(listener);
		}

		/* Clear the data related to filters */
		customFields.clear();
		fields.clear();

		owner.setRefreshingEnabled(true);

		/* also clear on-demand data */
		if (owner.getFilterable() != null) {
			owner.getFilterable().removeContainerFilter(lastOnDemandFilter);
		}

	}

	public void initializeFilterFields() {
		/* Create new filters only if Filterable */
		if (owner.getFilterable() != null) {
			for (Object property : owner.getVisibleColumns()) {
				if (owner.getContainerPropertyIds().contains(property)) {
					Component filter = createField(property, owner.getContainerDataSource().getType(property));
					addFilterColumn(property, filter);
				} else {
					addFilterColumn(property, createField(null, null));
				}
			}
		}
		if (!runFiltersOnDemand) {

			/* Add listeners */
			for (AbstractField<?> af : customFields.keySet()) {
				af.addValueChangeListener(listener);
			}

			for (AbstractField<?> field : fields.keySet()) {
				field.addValueChangeListener(listener);
			}
			;
		}
	}

	private Filter generateFilter(Property<?> field, Object propertyId, Object value) {
		if (debug && logger.isDebugEnabled()) {
			logger.debug("generateFilter: propertyId:" + propertyId.toString() + " value: " + value);
			logger.debug("field class = {}, type = {}", field.getClass(), field.getType());
		}

		for (FilterFieldHandler handler : handlers) {
			if (handler.handlesField(field)) {
				return handler.generateFilter(field, propertyId, value);
			}
		}

		if (value != null && !value.equals("")) {
			return generateGenericFilter(field, propertyId, value);
		}
		return null;
	}

	private Filter generateGenericFilter(Property<?> field, Object propertyId, Object value) {
		if (debug && logger.isDebugEnabled()) {
			logger.debug("generateGenericFilter({}, {}, {})", field, propertyId, value);
		}
		/* Handle filtering for other data */
		if (owner.getFilterGenerator() != null) {
			Filter newFilter = owner.getFilterGenerator().generateFilter(propertyId, value);
			if (newFilter != null) {
				return newFilter;
			}
		}
		return new SimpleStringFilter(propertyId, String.valueOf(value), true, false);
	}

	private void addFilterColumn(Object propertyId, Component filter) {
		owner.getColumnIdToFilterMap().put(propertyId, filter);
		filter.setParent(owner.getAsComponent());
		// owner.requestRepaint();
	}

	private void removeFilter(Object propertyId) {
		if (filters.get(propertyId) != null) {
			owner.getFilterable().removeContainerFilter(filters.get(propertyId));
			filters.remove(propertyId);
		}
	}

	private void setFilter(Filter filter, Object propertyId) {
		owner.getFilterable().addContainerFilter(filter);
		filters.put(propertyId, filter);
	}

	private Component createField(Object property, Class<?> type) {
		AbstractField<?> component = null;
		if (owner.getFilterGenerator() != null) {

			component = owner.getFilterGenerator().getCustomFilterComponent(property);
			if (component != null) {
				component.addValueChangeListener(listener);
				component.setImmediate(true);
				customFields.put(component, property);
				return component;
			}

		} else if (type != null && !"UUID".equals(property)) {

			// CG - Added in above UUID check because OracleRaw types were
			// trigger statusid handler

			for (FilterFieldHandler handler : handlers) {
				if (handler.handlesType(type)) {
					if (debug && logger.isDebugEnabled()) {
						logger.debug("Creating field for {} {}", type, property);
					}
					component = handler.createField(type, property);
					component.setImmediate(true);
					component.addValueChangeListener(listener);
					fields.put(component, property);
					return component;
				}
			}

		}

		component = createTextField(property);
		fields.put(component, property);
		component.setWidth(100, Unit.PERCENTAGE);
		component.setImmediate(true);
		component.addValueChangeListener(listener);
		return component;
	}

	private AbstractField<?> createTextField(Object propertyId) {

		final TextField textField = new TextField();
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

	private ValueChangeListener initializeListener() {
		return new Property.ValueChangeListener() {
			public void valueChange(Property.ValueChangeEvent event) {
				if (owner.getFilterable() == null) {
					return;
				}

				Property<?> field = event.getProperty();
				Object value;
				if (field instanceof AbstractField) {
					value = ((AbstractField<?>) field).getConvertedValue();
				} else {
					value = field.getValue();
				}
				Object propertyId = null;
				if (customFields.containsKey(field)) {
					propertyId = customFields.get(field);
				} else if (fields.containsKey(field)) {
					propertyId = fields.get(field);
				}

				if (debug && logger.isDebugEnabled()) {
					if (value != null) {
						logger.debug("value = " + value.getClass().getSimpleName() + " " + value);
					} else {
						logger.debug("value = null");
					}
				}

				removeFilter(propertyId);
				/* Generate and set a new filter */
				Filter newFilter = generateFilter(field, propertyId, value);
				if (newFilter != null) {
					setFilter(newFilter, propertyId);
					if (owner.getFilterGenerator() != null) {
						owner.getFilterGenerator().filterAdded(propertyId, newFilter.getClass(), value);
					}
				} else {
					if (owner.getFilterGenerator() != null) {
						owner.getFilterGenerator().filterRemoved(propertyId);
					}
				}

				/* If the owner is a PagedFilteringTable, move to the first page */
				if (owner instanceof PagedFilterTable<?>) {
					((PagedFilterTable<?>) owner).setCurrentPage(1);
				}
				// TODO -- set value null when filters change?
				if (owner instanceof FilterTable) {
					((FilterTable) owner).setValue(null);
				}

				// owner.requestRepaint();
			}
		};
	}

	// New Methods added in 0.9.9.7 Need to look at implementation..
	private boolean runFiltersOnDemand;

	@Override
	public void setFilterOnDemandMode(boolean filterOnDemand) {
		if (runFiltersOnDemand == filterOnDemand) {
			return;
		} else {
			runFiltersOnDemand = filterOnDemand;
			clearFilterData();
			initializeFilterFields();
		}
	}

	private And lastOnDemandFilter;

	private Filter generateFilterForField(Property<?> field) {
		Object value = field.getValue();
		Object propertyId = null;
		if (customFields.containsKey(field)) {
			propertyId = customFields.get(field);
		} else if (fields.containsKey(field)) {
			propertyId = fields.get(field);
		}

		return generateFilter(field, propertyId, value);
	}

	private void addNonNullFilter(List<Filter> filters, AbstractField<?> f) {
		Filter filter = generateFilterForField(f);
		if (null != filter) {
			filters.add(filter);
		}
	}

	@Override
	public void runFiltersNow() {
		owner.setRefreshingEnabled(false);
		if (owner.getFilterable() != null) {
			owner.getFilterable().removeContainerFilter(lastOnDemandFilter);
		}
		List<Filter> filters = new ArrayList<Filter>();

		for (AbstractField<?> f : customFields.keySet()) {
			addNonNullFilter(filters, f);
		}

		for (AbstractField<?> f : fields.keySet()) {
			addNonNullFilter(filters, f);
		}

		Filter[] filtersArray = filters.toArray(new Filter[0]);
		lastOnDemandFilter = new And(filtersArray);
		if (owner.getFilterable() != null) {
			owner.getFilterable().addContainerFilter(lastOnDemandFilter);
		}
		owner.setRefreshingEnabled(true);

	}
}