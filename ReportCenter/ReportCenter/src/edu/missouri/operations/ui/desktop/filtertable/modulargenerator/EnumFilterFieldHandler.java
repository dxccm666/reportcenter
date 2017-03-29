package edu.missouri.operations.ui.desktop.filtertable.modulargenerator;

import java.util.EnumSet;

import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;

class EnumFilterFieldHandler extends AbstractFilterFieldHandler {

	@SuppressWarnings("serial")
	class EnumComboBox extends ComboBox {
		EnumComboBox() {
			super();
		}
	}

	@Override
	public boolean handlesField(Property<?> field) {
		return field instanceof EnumComboBox;
	}

	@Override
	public boolean handlesType(Class<?> clazz) {
		return clazz == Enum.class;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AbstractField<?> createField(Class<?> type, Object propertyId) {
		EnumComboBox enumSelect = new EnumComboBox();
		/* Add possible 'view all' item */
		if (owner.getFilterDecorator() != null && owner.getFilterDecorator().getAllItemsVisibleString() != null) {
			Object nullItem = enumSelect.addItem();
			enumSelect.setNullSelectionItemId(nullItem);
			enumSelect.setItemCaption(nullItem, owner.getFilterDecorator().getAllItemsVisibleString());
		}
		/* Add items from enumeration */
		for (Object o : EnumSet.allOf((Class<Enum>) type)) {
			enumSelect.addItem(o);
			if (owner.getFilterDecorator() != null) {
				String caption = owner.getFilterDecorator().getEnumFilterDisplayName(propertyId, o);
				enumSelect.setItemCaption(o, caption == null ? o.toString() : caption);
				Resource icon = owner.getFilterDecorator().getEnumFilterIcon(propertyId, o);
				if (icon != null) {
					enumSelect.setItemIcon(o, icon);
				}
			} else {
				enumSelect.setItemCaption(o, o.toString());
			}
		}
		return enumSelect;
	}

	@Override
	public Filter generateFilter(Property<?> field, Object propertyId, Object value) {
		
		if(value == null) {
			return null;
		}
		
		if (owner.getFilterGenerator() != null) {
			Filter newFilter = owner.getFilterGenerator().generateFilter(
					propertyId, value);
			if (newFilter != null) {
				return newFilter;
			}
		}
		return new Compare.Equal(propertyId, String.valueOf(value));
	}

}