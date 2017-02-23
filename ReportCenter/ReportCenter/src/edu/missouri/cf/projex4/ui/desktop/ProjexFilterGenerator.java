package edu.missouri.cf.projex4.ui.desktop;

import java.io.Serializable;

import org.slf4j.Logger;
import org.tepi.filtertable.FilterGenerator;

import com.vaadin.data.Container.Filter;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;

import edu.missouri.cf.projex4.data.system.core.Loggers;

@SuppressWarnings("serial")
public class ProjexFilterGenerator implements FilterGenerator, Serializable {

	protected Logger logger = Loggers.getLogger(this.getClass());
	
	@Override
	public Filter generateFilter(Object propertyId, Object value) {
		// TODO VERY LOW - Unimplemented Function
		return null;
	}

	@Override
	public AbstractField<?> getCustomFilterComponent(Object propertyId) {
		// TODO VERY LOW - Unimplemented Function
		return null;
	}

	@Override
	public void filterRemoved(Object propertyId) {
		// TODO VERY LOW - Unimplemented Function
	}

	@Override
	public void filterAdded(Object propertyId,
			Class<? extends Filter> filterType, Object value) {
		// TODO VERY LOW - Unimplemented Function
	}

	@Override
	public Filter generateFilter(Object propertyId, Field<?> originatingField) {
		// TODO VERY LOW - Unimplemented Function
		return null;
	}

	@Override
	public Filter filterGeneratorFailed(Exception reason, Object propertyId, Object value) {
		// TODO VERY LOW - Unimplemented Function
		return null;
	}

}
