package edu.missouri.operations.ui.desktop.filtertable.modulargenerator;

import java.sql.Timestamp;
import org.tepi.filtertable.datefilter.DateFilterPopup;
import org.tepi.filtertable.datefilter.DateInterval;

import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractField;

class DateFilterFieldHandler extends AbstractFilterFieldHandler {

	@Override
	public boolean handlesField(Property<?> field) {
		return field instanceof DateFilterPopup;
	}

	@Override
	public boolean handlesType(Class<?> clazz) {
		return (clazz == java.util.Date.class || clazz == Timestamp.class );
	}

	@Override
	public AbstractField<?> createField(Class<?> type, Object propertyId) {
		DateFilterPopup dateFilterPopup = new DateFilterPopup(owner.getFilterDecorator(), propertyId);
		dateFilterPopup.setWidth(100, Unit.PERCENTAGE);
		return dateFilterPopup;
	}
	
	@Override
	public Filter generateFilter(Property<?> field, Object propertyId, Object value) {
		
		if(value==null) {
			return null;
		}
		
		/* Handle date filtering */
		DateInterval interval = ((DateFilterPopup) field).getValue();
		if (interval == null || interval.isNull()) {
			/* Date interval is empty -> no filter */
			return null;
		}
		/* Try to get a custom filter from a provided filter generator */
		if (owner.getFilterGenerator() != null) {
			Filter newFilter = owner.getFilterGenerator().generateFilter(propertyId, interval);
			if (newFilter != null) {
				return newFilter;
			}
		}
		/* On failure we generate the default filter */
		Comparable<?> actualFrom = interval.getFrom(), actualTo = interval.getTo();
		Class<?> type = owner.getType(propertyId);
		if (Timestamp.class.equals(type)) {
			actualFrom = (actualFrom == null ? null : new Timestamp(interval.getFrom().getTime()));
			actualTo = (actualTo == null ? null : new Timestamp(interval.getTo().getTime()));
		}
		if (actualFrom != null && actualTo != null) {
			return new Between(propertyId, actualFrom, actualTo);
		} else if (actualFrom != null) {
			return new Compare.GreaterOrEqual(propertyId, actualFrom);
		} else {
			return new Compare.LessOrEqual(propertyId, actualTo);
		}
	}
}