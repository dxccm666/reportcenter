package edu.missouri.operations.ui.desktop.filtertable.modulargenerator;

import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;

import edu.missouri.operations.data.OracleBoolean;

class OracleBooleanFilterFieldHandler extends AbstractFilterFieldHandler {

	@SuppressWarnings("serial")
	class OracleBooleanComboBox extends ComboBox {
		OracleBooleanComboBox() {
			super();
		}
	}

	@Override
	public boolean handlesField(Property<?> field) {
		return field instanceof OracleBooleanComboBox;
	}

	@Override
	public boolean handlesType(Class<?> clazz) {
		return clazz == OracleBoolean.class;
	}

	@Override
	public AbstractField<?> createField(Class<?> clazz, Object propertyId) {
		OracleBooleanComboBox booleanSelect = new OracleBooleanComboBox();
		booleanSelect.addItem(OracleBoolean.TRUE);
		booleanSelect.addItem(OracleBoolean.FALSE);
		/* Do we need all this filter decorator stuff */
		if (owner.getFilterDecorator() != null) {
			/* Add possible 'view all' item */
			if (owner.getFilterDecorator().getAllItemsVisibleString() != null) {
				Object nullItem = booleanSelect.addItem();
				booleanSelect.setNullSelectionItemId(nullItem);
				booleanSelect.setItemCaption(nullItem, owner.getFilterDecorator().getAllItemsVisibleString());
			}

			String caption = owner.getFilterDecorator().getBooleanFilterDisplayName(propertyId, true);
			booleanSelect.setItemCaption(OracleBoolean.TRUE, caption == null ? "true" : caption);
			Resource icon = owner.getFilterDecorator().getBooleanFilterIcon(propertyId, true);
			if (icon != null) {
				booleanSelect.setItemIcon(OracleBoolean.TRUE, icon);
			}
			caption = owner.getFilterDecorator().getBooleanFilterDisplayName(propertyId, false);
			booleanSelect.setItemCaption(OracleBoolean.FALSE, caption == null ? "false" : caption);
			icon = owner.getFilterDecorator().getBooleanFilterIcon(propertyId, false);
			if (icon != null) {
				booleanSelect.setItemIcon(OracleBoolean.FALSE, icon);
			}
		} else {
			booleanSelect.setItemCaption(OracleBoolean.TRUE, "true");
			booleanSelect.setItemCaption(OracleBoolean.FALSE, "false");
		}
		return booleanSelect;
	}

	@Override
	public Filter generateFilter(Property<?> Field, Object propertyId, Object value) {
		if (null == value) {
			return null;
		}
		if (owner.getFilterGenerator() != null) {
			Filter newFilter = owner.getFilterGenerator().generateFilter(propertyId, value);
			if (newFilter != null) {
				return newFilter;
			}
		}

		/*
		 * TODO This is a hack to get it working. The Compare.Equals seems
		 * to only work with strings
		 */
		if (OracleBoolean.TRUE.equals(value)) {
			return new Compare.Equal(propertyId, "1");
		} else {
			return new Compare.Equal(propertyId, "0");
		}
	}
}