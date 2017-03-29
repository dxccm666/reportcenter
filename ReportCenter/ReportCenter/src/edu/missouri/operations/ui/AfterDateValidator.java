package edu.missouri.operations.ui;

import com.vaadin.data.Validator;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;

import edu.missouri.operations.data.OracleTimestamp;

public class AfterDateValidator implements Validator {

	PopupDateField[] dependentField;
	String mainFieldCaption;
	
	public AfterDateValidator(String mainFieldCaption, PopupDateField... dependentField) {
		this.dependentField = dependentField;
		this.mainFieldCaption = mainFieldCaption;
	}
	
	@Override
	public void validate(Object value) throws InvalidValueException {
		
		OracleTimestamp f = (OracleTimestamp) value;
		for (PopupDateField sf : dependentField) {
			OracleTimestamp s = (OracleTimestamp) sf.getConvertedValue();
			if(f != null && s != null) {
				if (f.before(s)) {
					Notification.show(this.mainFieldCaption + " cannot be before " + sf.getCaption());
					throw new InvalidValueException(this.mainFieldCaption + " cannot be before " + sf.getCaption());
				}
			}
		}

	}

}
