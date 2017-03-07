package edu.missouri.cf.projex4.ui.desktop;

import java.sql.SQLException;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;

public interface EditorComponent extends EditingStateManipulator {
	
	public void commit() throws CommitException, SQLException, InvalidValueException;
	public void rollback();
	public void validate() throws InvalidValueException;
	
}
