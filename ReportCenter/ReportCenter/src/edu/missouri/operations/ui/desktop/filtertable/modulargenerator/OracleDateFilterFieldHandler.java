package edu.missouri.operations.ui.desktop.filtertable.modulargenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tepi.filtertable.datefilter.DateFilterPopup;
import org.tepi.filtertable.datefilter.DateInterval;

import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractField;

import edu.missouri.operations.data.OracleDate;
import edu.missouri.operations.data.OracleTimestamp;

class OracleDateFilterFieldHandler extends AbstractFilterFieldHandler {

	private final static Logger logger = LoggerFactory.getLogger(OracleDateFilterFieldHandler.class);

	@Override
	public boolean handlesField(Property<?> field) {
		return field instanceof DateFilterPopup;
	}

	@Override
	public boolean handlesType(Class<?> clazz) {
		return (clazz == java.sql.Timestamp.class || clazz == java.sql.Date.class || clazz == OracleDate.class || clazz == OracleTimestamp.class);
	}

	@Override
	public AbstractField<?> createField(Class<?> type, Object propertyId) {
		DateFilterPopup dateFilterPopup = new DateFilterPopup(owner.getFilterDecorator(), propertyId);
		dateFilterPopup.setWidth(100, Unit.PERCENTAGE);
		return dateFilterPopup;
	}

	@Override
	public Filter generateFilter(Property<?> field, Object propertyId, Object value) {
		
		if(value == null) {
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

		if (java.sql.Date.class == type) {
			actualFrom = (actualFrom == null ? null : new java.sql.Date(interval.getFrom().getTime()));
			actualTo = (actualTo == null ? null : new java.sql.Date(interval.getTo().getTime()));
		} else if (java.sql.Timestamp.class == type) {
			actualFrom = (actualFrom == null ? null : new java.sql.Timestamp(interval.getFrom().getTime()));
			actualTo = (actualTo == null ? null : new java.sql.Timestamp(interval.getTo().getTime()));
		} else if (OracleDate.class == type) {
			actualFrom = (actualFrom == null ? null : new OracleDate(interval.getFrom().getTime()));
			actualTo = (actualTo == null ? null : new OracleDate(interval.getTo().getTime()));
		} else if (OracleTimestamp.class == type) {
			actualFrom = (actualFrom == null ? null : new OracleTimestamp(interval.getFrom().getTime()));
			actualTo = (actualTo == null ? null : new OracleTimestamp(interval.getTo().getTime()));
		}

		Filter filter = null;
		if (actualFrom != null && actualTo != null) {
			filter = new Between(propertyId, actualFrom, actualTo);
		} else if (actualFrom != null) {
			filter = new Compare.GreaterOrEqual(propertyId, actualFrom);
		} else {
			filter = new Compare.LessOrEqual(propertyId, actualTo);
		}

		if (logger.isTraceEnabled()) {
			logger.trace("filter = {}", filter);
		}

		return filter;
	}
}