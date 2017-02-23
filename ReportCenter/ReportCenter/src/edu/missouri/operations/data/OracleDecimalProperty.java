package edu.missouri.operations.data;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;

@SuppressWarnings("serial")
public class OracleDecimalProperty extends AbstractProperty<OracleDecimal> {

	private OracleDecimal value;

	public OracleDecimalProperty() {
	}

	public OracleDecimalProperty(OracleDecimal value) {
		this.value = value;
	}

	public OracleDecimalProperty(OracleDecimal value, boolean readOnly) {
		this.value = value;
		setReadOnly(readOnly);
	}

	@Override
	public OracleDecimal getValue() {
		return value;
	}

	@Override
	public void setValue(OracleDecimal newValue) throws Property.ReadOnlyException {
		if (isReadOnly()) {
			throw new Property.ReadOnlyException();
		}
		
		this.value = newValue;
		fireValueChange();
		
	}

	@Override
	public Class<? extends OracleDecimal> getType() {
		return OracleDecimal.class;
	}

}
