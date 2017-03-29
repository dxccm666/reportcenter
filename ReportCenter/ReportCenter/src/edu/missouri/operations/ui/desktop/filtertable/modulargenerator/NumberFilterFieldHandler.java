package edu.missouri.operations.ui.desktop.filtertable.modulargenerator;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tepi.filtertable.numberfilter.NumberFilterPopup;
import org.tepi.filtertable.numberfilter.NumberInterval;

import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractField;

public class NumberFilterFieldHandler extends AbstractFilterFieldHandler {

	transient final static Logger logger = LoggerFactory.getLogger(NumberFilterFieldHandler.class);
	
	@Override
	public boolean handlesField(Property<?> field) {
		return field instanceof NumberFilterPopup;
	}

	@Override
	public boolean handlesType(Class<?> clazz) {
		return (clazz == Integer.class || clazz == Long.class || clazz == Float.class || clazz == Double.class || clazz == int.class
				|| clazz == long.class || clazz == float.class || clazz == double.class || clazz == BigDecimal.class
				|| clazz == BigInteger.class);
	}

	@Override
	public AbstractField<?> createField(Class<?> clazz, Object propertyId) {
		NumberFilterPopup numberFilterPopup = new NumberFilterPopup(owner.getFilterDecorator());
		numberFilterPopup.setWidth(100, Unit.PERCENTAGE);
		numberFilterPopup.setImmediate(true);
		return numberFilterPopup;
	}
	
	private Class<?> getProperNumericClass(Object propertyId) {
		Class<?> clazz = owner.getContainerDataSource().getType(propertyId);
		if (clazz.equals(int.class)) {
			return Integer.class;
		}
		if (clazz.equals(long.class)) {
			return Long.class;
		}
		if (clazz.equals(float.class)) {
			return Float.class;
		}
		if (clazz.equals(double.class)) {
			return Double.class;
		}
		return clazz;
	}

	@Override
	public Filter generateFilter(Property<?> field, Object propertyId, Object value) {
		/* Handle number filtering */
		
		if(value == null) {
			return null;
		}
		
		NumberInterval interval = ((NumberFilterPopup) field).getValue();
		if (interval == null) {
			/* Number interval is empty -> no filter */
			return null;
		}
		if (owner.getFilterGenerator() != null) {
			Filter newFilter = owner.getFilterGenerator().generateFilter(propertyId, interval);
			if (newFilter != null) {
				return newFilter;
			}
		}
		String ltValue = interval.getLessThanValue();
		String gtValue = interval.getGreaterThanValue();
		String eqValue = interval.getEqualsValue();
		Class<?> clazz = getProperNumericClass(propertyId);

		Method valueOf;

		// We use reflection to get the vaueOf method of the container datatype
		try {
			// added in this bigdecimal class check, because BigDecimal does not have a valueOf(string) function.
			if (clazz.equals(BigDecimal.class)) {
				if (eqValue != null) {
					return new Compare.Equal(propertyId, new BigDecimal(eqValue));
				} else if (ltValue != null && gtValue != null) {
					return new And(new Compare.Less(propertyId, new BigDecimal(ltValue)), new Compare.Greater(propertyId,
							new BigDecimal(gtValue)));
				} else if (ltValue != null) {
					return new Compare.Less(propertyId, new BigDecimal(ltValue));
				} else if (gtValue != null) {
					return new Compare.Greater(propertyId, new BigDecimal(gtValue));
				} else {
					return null;
				}
			} else {
				valueOf = clazz.getMethod("valueOf", String.class);
			}
			if (eqValue != null) {
				return new Compare.Equal(propertyId, valueOf.invoke(clazz, eqValue));
			} else if (ltValue != null && gtValue != null) {
				return new And(new Compare.Less(propertyId, valueOf.invoke(clazz, ltValue)), new Compare.Greater(propertyId,
						valueOf.invoke(clazz, gtValue)));
			} else if (ltValue != null) {
				return new Compare.Less(propertyId, valueOf.invoke(clazz, ltValue));
			} else if (gtValue != null) {
				return new Compare.Greater(propertyId, valueOf.invoke(clazz, gtValue));
			} else {
				return null;
			}
			
		} catch (Exception e) {
			logger.error("Creating number filter has failed",e);
		}
		return null; 
	}
}