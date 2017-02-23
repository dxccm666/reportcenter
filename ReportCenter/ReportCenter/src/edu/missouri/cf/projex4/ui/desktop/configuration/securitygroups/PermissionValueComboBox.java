package edu.missouri.cf.projex4.ui.desktop.configuration.securitygroups;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.ui.ComboBox;

import edu.missouri.cf.data.OracleString;

@SuppressWarnings("serial")
public class PermissionValueComboBox extends ComboBox {

	public PermissionValueComboBox() {
		super();
		init();
	}
	public PermissionValueComboBox(String caption, Collection<?> options) {
        super(caption, options);
		init();
    }

    public PermissionValueComboBox(String caption, Container dataSource) {
        super(caption, dataSource);
		init();
    }

    public PermissionValueComboBox(String caption) {
        super(caption);
		init();
    }
	private void init() {
		System.err.println("constructor of PermissionValueComboBox");
		addItem(new OracleString("DENY"));
		addItem(new OracleString("ALLOW"));
		addItem(new OracleString("ALWAYSDENY"));
		addItem(new OracleString("ALWAYSALLOW"));
		setNullSelectionAllowed(false);
		setImmediate(true);
		
	}
}
