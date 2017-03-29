package edu.missouri.operations.ui;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.PopupDateField;

import edu.missouri.operations.data.OracleTimestampToDateConverter;

@SuppressWarnings("serial")
public class OraclePopupDateField extends PopupDateField {

	public OraclePopupDateField() {
		super();
		init();
	}

	public OraclePopupDateField(@SuppressWarnings("rawtypes") Property dataSource) throws IllegalArgumentException {
		super();
		init();
		setPropertyDataSource(dataSource);		
	}

	public OraclePopupDateField(String caption, java.sql.Date value) {
		super(caption);
		init();
		setConvertedValue(value);		
	}

	public OraclePopupDateField(String caption, @SuppressWarnings("rawtypes") Property dataSource) {
		super(caption);
		init();
		setPropertyDataSource(dataSource);		
	}

	public OraclePopupDateField(String caption) {
		super(caption);
		init();		
	}
	
	private void init() {
		setResolution(Resolution.DAY);
		setConverter(new OracleTimestampToDateConverter());
		addStyleName("popupcalendar");
	}
	
	/*
	@Override
	public void setValue(Object value) {
		
	}
	*/

}
