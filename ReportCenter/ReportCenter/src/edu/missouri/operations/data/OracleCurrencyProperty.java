package edu.missouri.operations.data;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;

@SuppressWarnings("serial")
public class OracleCurrencyProperty extends AbstractProperty<OracleCurrency> {

	private OracleCurrency value;

	public OracleCurrencyProperty() {
	}

	public OracleCurrencyProperty(OracleCurrency value) {
		this.value = value;
	}

	public OracleCurrencyProperty(OracleCurrency value, boolean readOnly) {
		this.value = value;
		setReadOnly(readOnly);
	}

	@Override
	public OracleCurrency getValue() {
		return value;
	}

	@Override
	public void setValue(OracleCurrency newValue) throws Property.ReadOnlyException {
		if (isReadOnly()) {
			throw new Property.ReadOnlyException();
		}
		
		this.value = newValue;
		fireValueChange();
		
	}

	@Override
	public Class<? extends OracleCurrency> getType() {
		return OracleCurrency.class;
	}

}
