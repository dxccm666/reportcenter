package edu.missouri.operations.ui;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;

@SuppressWarnings("serial")
public class OracleTimestampField extends DateField {

	public OracleTimestampField() {
		super();
		setResolution(Resolution.SECOND);
	}

	public OracleTimestampField(@SuppressWarnings("rawtypes") Property dataSource) throws IllegalArgumentException {
		super();
		setResolution(Resolution.SECOND);
		setPropertyDataSource(dataSource);
	}

	public OracleTimestampField(String caption, java.sql.Timestamp value) {
		super(caption);
		setResolution(Resolution.SECOND);
		setConvertedValue(value);
	}

	public OracleTimestampField(String caption, @SuppressWarnings("rawtypes") Property dataSource) {
		super(caption);
		setResolution(Resolution.SECOND);
		setPropertyDataSource(dataSource);
	}

	public OracleTimestampField(String caption) {
		super(caption);
		setResolution(Resolution.SECOND);
	}

}
