package edu.missouri.operations.data;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;

@SuppressWarnings("serial")
public class OracleIntegerProperty extends AbstractProperty<OracleInteger> {

	private OracleInteger value;

	public OracleIntegerProperty() {
	}

	public OracleIntegerProperty(OracleInteger value) {
		this.value = value;
	}

	public OracleIntegerProperty(OracleInteger value, boolean readOnly) {
		this.value = value;
		setReadOnly(readOnly);
	}

	@Override
	public OracleInteger getValue() {
		return value;
	}

	@Override
	public void setValue(OracleInteger newValue) throws Property.ReadOnlyException {
		if (isReadOnly()) {
			throw new Property.ReadOnlyException();
		}
		
		this.value = newValue;
		fireValueChange();
		
	}

	@Override
	public Class<? extends OracleInteger> getType() {
		return OracleInteger.class;
	}

}
