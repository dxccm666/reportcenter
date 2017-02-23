package edu.missouri.cf.projex4.ui.desktop;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;

import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.common.system.DefaultObjectManipulator;
import edu.missouri.cf.projex4.ui.common.system.ObjectManipulator;
import edu.missouri.cf.projex4.data.system.SystemLockException;
import edu.missouri.cf.projex4.data.system.User;

@SuppressWarnings("serial")
public abstract class EditorView extends TopBarView implements EditorComponent {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected EditingStateManipulator editingStateManipulator = new DefaultEditingStateManipulator();
	protected ObjectManipulator objectManipulator = new DefaultObjectManipulator();
	
	/**
	 * @param locked
	 * @see edu.missouri.cf.projex4.ui.common.system.ObjectManipulator#setEditingLock(boolean)
	 */
	public void setEditingLock(boolean locked) throws SystemLockException {
		objectManipulator.setEditingLock(locked);
	}

	public EditorView() {
		super();
	}
	
	public EditorView(Component...components) {
		super(components);
	}

	@Override
	public EditingState getEditingState() {
		return editingStateManipulator.getEditingState();
	}

	@Override
	public void setEditingState(EditingState state) {
		editingStateManipulator.setEditingState(state);
	}

	@Override
	public String getLockName() {
		return objectManipulator.getLockName();
	}

	@Override
	public void setLockName(String lockName) {
		objectManipulator.setLockName(lockName);
	}
	
	@Override
	public abstract void setScreenData(String parameters);
	
	@Override
	public void setObjectData(ObjectData objectData) {
		objectManipulator.setObjectData(objectData);
		
		if(getApplicationName()!=null) {
			User.preComputeRights(User.getUser().getUserId(), getApplicationName(), objectData.getObjectId());
		} else {
			try {
				throw new Exception("Call of setObjectData before setApplicationName - fix this");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public ObjectData getObjectData() {
		return objectManipulator.getObjectData();
	}
	
	@Override
	public void setObjectId(String objectId) {
		System.err.println("Setting objectId - editor View");
		objectManipulator.setObjectId(objectId);
	}
	
	@Override 
	public String getObjectId() {
		return objectManipulator.getObjectId();
	}
	
	public void setApplicationName(ProjexViewProvider.Views view) {
		objectManipulator.setApplicationName(view.name());
	}
	
	public void setApplicationName(String applicationName) {
		objectManipulator.setApplicationName(applicationName);
	}
	
	public String getApplicationName() {
		return objectManipulator.getApplicationName();
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		reportsLink.resetResource();
		helpLink.resetResource();
		setScreenData(event.getParameters());
	}
	
	/**
	 * Override this in each screen if you need to commit the FieldGroup or the OracleContainer.
	 * FieldGroup throws CommitException and OracleContainer throws SQLException.
	 * This is already done in SQLEditorView.
	 * @author reynoldsjj
	 */
	@Override
	public void commit() throws CommitException, SQLException {
		
	}

	/**
	 * Override this in each screen if rollback is needed.
	 * FieldGroup and Container would need to be rolled back.
	 * This is already done in SQLEditorView.
	 * @author reynoldsjj
	 */
	@Override
	public void rollback() {
		
	}
		
}
