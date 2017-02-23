/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop;

import java.sql.SQLException;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.common.system.DefaultObjectManipulator;
import edu.missouri.cf.projex4.ui.common.system.ObjectManipulator;
import edu.missouri.cf.projex4.ui.desktop.EditorComponent;
import edu.missouri.cf.projex4.data.system.SystemLockException;
/**
 * @author graumannc
 *
 */
public class DependentProjexEditor extends VerticalLayout implements EditorComponent {
	
	protected ObjectManipulator objectManipulator = new DefaultObjectManipulator();
	protected EditingStateManipulator editingStateManipulator = new DefaultEditingStateManipulator();
	
	/**
	 * 
	 */
	public DependentProjexEditor() {
		addStyleName("dependenteditor");
	}

	/* (non-Javadoc)
	 * @see edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator#getEditingState()
	 */
	@Override
	public EditingState getEditingState() {
		return editingStateManipulator.getEditingState();
	}

	/* (non-Javadoc)
	 * @see edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator#setEditingState(edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator.EditingState)
	 */
	@Override
	public void setEditingState(EditingState mode) {
		
	}

	/* (non-Javadoc)
	 * @see edu.missouri.cf.projex4.ui.common.system.ObjectManipulator#setApplicationName(java.lang.String)
	 */
	@Override
	public void setApplicationName(String applicationName) {
		objectManipulator.setApplicationName(applicationName);
	}
	
	public void setApplicationName(ProjexViewProvider.Views view) {
		setApplicationName(view.name());
	}
	
	/* (non-Javadoc)
	 * @see edu.missouri.cf.projex4.ui.common.system.ObjectManipulator#getApplicationName()
	 */
	@Override
	public String getApplicationName() {
		return objectManipulator.getApplicationName();
	}

	/* (non-Javadoc)
	 * @see edu.missouri.cf.projex4.ui.common.system.ObjectManipulator#setObjectData(edu.missouri.cf.projex4.data.system.core.objects.ObjectData)
	 */
	@Override
	public void setObjectData(ObjectData objectData) {
		objectManipulator.setObjectData(objectData);
	}

	/* (non-Javadoc)
	 * @see edu.missouri.cf.projex4.ui.common.system.ObjectManipulator#getObjectData()
	 */
	@Override
	public ObjectData getObjectData() {
		return objectManipulator.getObjectData();
	}
	
	@Override 
	public void setObjectId(String objectId) {
		objectManipulator.setObjectId(objectId);
	}
	
	@Override
	public String getObjectId() {
		return objectManipulator.getObjectId();
	}

	/* (non-Javadoc)
	 * @see edu.missouri.cf.projex4.ui.common.system.ObjectManipulator#setLockName(java.lang.String)
	 */
	@Override
	public void setLockName(String lockName) {
		objectManipulator.setLockName(lockName);
	}

	/* (non-Javadoc)
	 * @see edu.missouri.cf.projex4.ui.common.system.ObjectManipulator#getLockName()
	 */
	@Override
	public String getLockName() {
		return objectManipulator.getLockName();
	}

	/* (non-Javadoc)
	 * @see edu.missouri.cf.projex4.ui.common.system.ObjectManipulator#setEditingLock(boolean)
	 */
	@Override
	public void setEditingLock(boolean locked) throws SystemLockException {
		objectManipulator.setEditingLock(locked);
	}

	/* (non-Javadoc)
	 * @see edu.missouri.cf.projex4.ui.desktop.EditorComponent#commit()
	 */
	@Override
	public void commit() throws CommitException, SQLException {

	}

	/* (non-Javadoc)
	 * @see edu.missouri.cf.projex4.ui.desktop.EditorComponent#rollback()
	 */
	@Override
	public void rollback() {

	}

	@Override
	public void validate() throws InvalidValueException {
		
	}

}
