package edu.missouri.operations.ui;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.PopupDateField;

@SuppressWarnings("serial")
public class OraclePopupTimestampField extends PopupDateField {

	public OraclePopupTimestampField() {
		super();
		setResolution(Resolution.MINUTE);
	}

	public OraclePopupTimestampField(@SuppressWarnings("rawtypes") Property dataSource) throws IllegalArgumentException {
		super();
		setResolution(Resolution.MINUTE);
		setPropertyDataSource(dataSource);
	}

	public OraclePopupTimestampField(String caption, java.sql.Timestamp value) {
		super(caption);
		setResolution(Resolution.MINUTE);
		setConvertedValue(value);
	}

	public OraclePopupTimestampField(String caption, @SuppressWarnings("rawtypes") Property dataSource) {
		super(caption);
		setResolution(Resolution.MINUTE);
		setPropertyDataSource(dataSource);
	}

	public OraclePopupTimestampField(String caption) {
		super(caption);
		setResolution(Resolution.MINUTE);
	}

}
