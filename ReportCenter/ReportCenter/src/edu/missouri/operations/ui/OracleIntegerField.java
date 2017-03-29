package edu.missouri.operations.ui;

import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.TextField;

import edu.missouri.operations.data.OracleInteger;

@SuppressWarnings("serial")
public class OracleIntegerField extends TextField {

	public OracleIntegerField() {
		init();
	}

	public OracleIntegerField(String caption) {
		super(caption);
		init();
	}

	@SuppressWarnings("rawtypes")
	public OracleIntegerField(Property dataSource) {
		super(dataSource);
		init();
	}

	@SuppressWarnings("rawtypes")
	public OracleIntegerField(String caption, Property dataSource) {
		super(caption, dataSource);
		init();
	}

	public OracleIntegerField(String caption, String value) {
		super(caption, value);
		init();
	}
	
	private void init() {
		setConverter(OracleInteger.class);
		setInvalidAllowed(false);
		setInvalidCommitted(false);
		setValidationVisible(true);
		addStyleName("leftjustified monospaced");
		setNullRepresentation("0");
		
		addBlurListener(new BlurListener() {

			@Override
			public void blur(BlurEvent event) {
				
				System.err.println(getValue());
				
			}});
		
	}

}
