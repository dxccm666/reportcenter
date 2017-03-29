package edu.missouri.operations.ui.desktop.filtertable.modulargenerator;

import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;

class BooleanFilterFieldHandler extends AbstractFilterFieldHandler {

	@SuppressWarnings("serial")
	class BooleanComboBox extends ComboBox {
		BooleanComboBox() {
			super();
		}
	}

	@Override
	public boolean handlesField(Property<?> field) {
		return field instanceof BooleanComboBox;
	}

	@Override
	public boolean handlesType(Class<?> type) {
		return (type == boolean.class || type == Boolean.class);
	}

	@Override
	public AbstractField<?> createField(Class<?> type, Object propertyId) {
		BooleanComboBox booleanSelect = new BooleanComboBox();
		booleanSelect.addItem(true);
		booleanSelect.addItem(false);
		if (owner.getFilterDecorator() != null) {
			/* Add possible 'view all' item */
			if (owner.getFilterDecorator().getAllItemsVisibleString() != null) {
				Object nullItem = booleanSelect.addItem();
				booleanSelect.setNullSelectionItemId(nullItem);
				booleanSelect.setItemCaption(nullItem, owner.getFilterDecorator().getAllItemsVisibleString());
			}
			String caption = owner.getFilterDecorator().getBooleanFilterDisplayName(propertyId, true);
			booleanSelect.setItemCaption(true, caption == null ? "true" : caption);
			Resource icon = owner.getFilterDecorator().getBooleanFilterIcon(propertyId, true);
			if (icon != null) {
				booleanSelect.setItemIcon(true, icon);
			}
			caption = owner.getFilterDecorator().getBooleanFilterDisplayName(propertyId, false);
			booleanSelect.setItemCaption(false, caption == null ? "false" : caption);
			icon = owner.getFilterDecorator().getBooleanFilterIcon(propertyId, false);
			if (icon != null) {
				booleanSelect.setItemIcon(false, icon);
			}
		} else {
			booleanSelect.setItemCaption(true, "true");
			booleanSelect.setItemCaption(false, "false");
		}
		return booleanSelect;
	}

	@Override
	public Filter generateFilter(Property<?> field, Object propertyId, Object value) {
		if (null == value) {
			return null;
		}
		
		if (owner.getFilterGenerator() != null) {
			Filter newFilter = owner.getFilterGenerator().generateFilter(propertyId, value);
			if (newFilter != null) {
				return newFilter;
			}
		}

		return new Compare.Equal(propertyId, value.toString());
	}
}