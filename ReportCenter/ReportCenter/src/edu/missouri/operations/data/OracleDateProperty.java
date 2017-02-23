package edu.missouri.operations.data;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;

@SuppressWarnings("serial")
public class OracleDateProperty extends AbstractProperty<OracleDate> {

	private OracleDate value;

	public OracleDateProperty() {
	}

	public OracleDateProperty(OracleDate value) {
		this.value = value;
	}

	public OracleDateProperty(OracleDate value, boolean readOnly) {
		this.value = value;
		setReadOnly(readOnly);
	}

	@Override
	public OracleDate getValue() {
		return value;
	}

	@Override
	public void setValue(OracleDate newValue) throws Property.ReadOnlyException {
		if (isReadOnly()) {
			throw new Property.ReadOnlyException();
		}
		
		this.value = newValue;
		fireValueChange();
		
	}

	@Override
	public Class<? extends OracleDate> getType() {
		return OracleDate.class;
	}

}
