package edu.missouri.operations.ui;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;

@SuppressWarnings("serial")
public class OracleDateField extends DateField {

	public OracleDateField() {
		super();
		setResolution(Resolution.DAY);
	}

	public OracleDateField(@SuppressWarnings("rawtypes") Property dataSource) throws IllegalArgumentException {
		super();
		setResolution(Resolution.DAY);
		setPropertyDataSource(dataSource);
	}

	public OracleDateField(String caption, java.sql.Date value) {
		super(caption);
		setResolution(Resolution.DAY);
		setConvertedValue(value);
	}

	public OracleDateField(String caption, @SuppressWarnings("rawtypes") Property dataSource) {
		super(caption);
		setResolution(Resolution.DAY);
		setPropertyDataSource(dataSource);
	}

	public OracleDateField(String caption) {
		super(caption);
		setResolution(Resolution.DAY);
	}

}
