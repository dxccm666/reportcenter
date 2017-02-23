package edu.missouri.operations.data;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;

@SuppressWarnings("serial")
public class OracleTimestampProperty extends AbstractProperty<OracleTimestamp> {

	private OracleTimestamp value;

	public OracleTimestampProperty() {
	}

	public OracleTimestampProperty(OracleTimestamp value) {
		this.value = value;
	}

	public OracleTimestampProperty(OracleTimestamp value, boolean readOnly) {
		this.value = value;
		setReadOnly(readOnly);
	}

	@Override
	public OracleTimestamp getValue() {
		return value;
	}

	@Override
	public void setValue(OracleTimestamp newValue) throws Property.ReadOnlyException {
		if (isReadOnly()) {
			throw new Property.ReadOnlyException();
		}
		
		this.value = newValue;
		fireValueChange();
		
	}

	@Override
	public Class<? extends OracleTimestamp> getType() {
		return OracleTimestamp.class;
	}

}
