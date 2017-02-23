package edu.missouri.operations.data;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;

@SuppressWarnings("serial")
public class OracleBooleanProperty extends AbstractProperty<OracleBoolean> {

	private OracleBoolean value;

	public OracleBooleanProperty() {
	}

	public OracleBooleanProperty(OracleBoolean value) {
		this.value = value;
	}

	public OracleBooleanProperty(OracleBoolean value, boolean readOnly) {
		this.value = value;
		setReadOnly(readOnly);
	}

	@Override
	public OracleBoolean getValue() {
		return value;
	}

	@Override
	public void setValue(OracleBoolean newValue) throws Property.ReadOnlyException {
		if (isReadOnly()) {
			throw new Property.ReadOnlyException();
		}
		
		this.value = newValue;
		fireValueChange();
		
	}

	@Override
	public Class<? extends OracleBoolean> getType() {
		return OracleBoolean.class;
	}

}
