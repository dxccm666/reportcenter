package edu.missouri.operations.data;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;

@SuppressWarnings("serial")
public class OracleStringProperty extends AbstractProperty<OracleString> {

	private OracleString value;

	public OracleStringProperty() {
	}

	public OracleStringProperty(OracleString value) {
		this.value = value;
	}

	public OracleStringProperty(OracleString value, boolean readOnly) {
		this.value = value;
		setReadOnly(readOnly);
	}

	@Override
	public OracleString getValue() {
		return value;
	}

	@Override
	public void setValue(OracleString newValue) throws Property.ReadOnlyException {
		if (isReadOnly()) {
			throw new Property.ReadOnlyException();
		}
		
		this.value = newValue;
		fireValueChange();
		
	}

	@Override
	public Class<? extends OracleString> getType() {
		return OracleString.class;
	}

}
