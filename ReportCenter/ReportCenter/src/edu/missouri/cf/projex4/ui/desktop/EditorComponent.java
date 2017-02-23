package edu.missouri.cf.projex4.ui.desktop;

import java.sql.SQLException;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;

import edu.missouri.cf.projex4.ui.common.system.ObjectManipulator;

public interface EditorComponent extends EditingStateManipulator, ObjectManipulator {
	
	public void commit() throws CommitException, SQLException, InvalidValueException;
	public void rollback();
	public void validate() throws InvalidValueException;
	
}
