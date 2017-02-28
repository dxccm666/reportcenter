package edu.missouri.cf.projex4.ui.desktop.reports;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;

import edu.missouri.operations.data.OracleString;

@SuppressWarnings("serial")
public class ReportFileField extends CustomField<OracleString> {

	@Override
	protected Component initContent() {
		return null;
	}

	@Override
	public Class<? extends OracleString> getType() {
		return OracleString.class;
	}

}
