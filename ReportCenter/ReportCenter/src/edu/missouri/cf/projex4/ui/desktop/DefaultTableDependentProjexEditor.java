/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.tepi.filtertable.FilterTable;

import com.vaadin.data.BufferedValidatable;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.common.system.DefaultObjectManipulator;
import edu.missouri.cf.projex4.ui.common.system.ObjectManipulator;
import edu.missouri.cf.projex4.data.system.SystemLockException;
/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public abstract class DefaultTableDependentProjexEditor extends VerticalLayout implements EditorComponent, BufferedValidatable {

	private static Logger logger = Loggers.getLogger(DefaultTableDependentProjexEditor.class);

	protected StandardTable table;
	protected OracleContainer sqlContainer;

	protected ObjectManipulator objectManipulator = new DefaultObjectManipulator();
	protected EditingStateManipulator editingStateManipulator = new DefaultEditingStateManipulator();
	protected TableControlLayout buttons;

	/**
	 * @return the table
	 */
	public FilterTable getTable() {
		return table;
	}

	/**
	 * @return the sqlContainer
	 */
	public OracleContainer getSqlContainer() {
		return sqlContainer;
	}

	/**
	 * @param sqlContainer
	 *            the sqlContainer to set
	 */
	public void setSqlContainer(OracleContainer sqlContainer) {
		this.sqlContainer = sqlContainer;
	}

	public TableControlLayout getButtons() {
		return buttons;
	}

	/**
	 * 
	 */
	public DefaultTableDependentProjexEditor() {
		init();
		layout();
	}

	@Override
	public void setObjectData(ObjectData objectData) {
		objectManipulator.setObjectData(objectData);
	}

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
	
	/**
	 * @param lockName
	 * @see edu.missouri.cf.projex4.ui.common.system.ObjectManipulator#setLockName(java.lang.String)
	 */
	@Override
	public void setLockName(String lockName) {
		objectManipulator.setLockName(lockName);
	}

	/**
	 * @return
	 * @see edu.missouri.cf.projex4.ui.common.system.ObjectManipulator#getLockName()
	 */
	@Override
	public String getLockName() {
		return objectManipulator.getLockName();
	}

	@Override
	public void setApplicationName(String applicationName) {
		objectManipulator.setApplicationName(applicationName);
	}

	@Override
	public String getApplicationName() {
		return objectManipulator.getApplicationName();
	}

	/**
	 * @param locked
	 * @see edu.missouri.cf.projex4.ui.common.system.ObjectManipulator#setEditingLock(boolean)
	 */
	@Override
	public void setEditingLock(boolean locked) throws SystemLockException {
		objectManipulator.setEditingLock(locked);
	}

	@Override
	public void commit() {
		if (logger.isDebugEnabled()) {
			logger.debug("TableDependentProjexEditor.commit called");
		}

		if (table.isValid()) {

			try {
				if (sqlContainer != null) {
					sqlContainer.commit();
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("sqlContainer is null");
					}
				}
			} catch (UnsupportedOperationException | SQLException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Could not commit", e);
				}
			}
			table.commit();
		}

	}

	@Override
	public void addValidator(Validator validator) {
		table.addValidator(validator);
	}

	@Override
	public java.util.Collection<Validator> getValidators() {
		return table.getValidators();
	}

	@Override
	public boolean isInvalidAllowed() {
		return table.isInvalidAllowed();
	}

	@Override
	public boolean isValid() {
		return table.isValid();
	}

	@Override
	public void removeAllValidators() {
		table.removeAllValidators();
	}

	@Override
	public void removeValidator(Validator validator) {
		table.removeValidator(validator);
	}

	@Override
	public void setInvalidAllowed(boolean invalidAllowed) {
		table.setInvalidAllowed(invalidAllowed);
	}

	@Override
	public void validate() {
		table.validate();
	}

	@Override
	public boolean isInvalidCommitted() {
		return table.isInvalidCommitted();
	}

	@Override
	public void setInvalidCommitted(boolean isCommitted) {
		table.setInvalidCommitted(isCommitted);
	}

	@Override
	public void discard() {
		rollback();
	}

	@Override
	public boolean isBuffered() {
		return table.isBuffered();
	}

	@Override
	public boolean isModified() {
		return table.isModified();
	}

	@Override
	public void setBuffered(boolean buffered) {
		table.setBuffered(buffered);
	}

	@Override
	public void rollback() {

		logger.debug("TableDependentProjexEditor.rollback called");
		table.discard();
		try {
			if (sqlContainer != null)
				sqlContainer.rollback();
		} catch (UnsupportedOperationException | SQLException | ClassCastException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 * @see edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator#getEditingState()
	 */
	public EditingState getEditingState() {
		return editingStateManipulator.getEditingState();
	}

	/**
	 * @param mode
	 * @see edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator#setEditingState(edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator.EditingState)
	 */
	public void setEditingState(EditingState mode) {

		editingStateManipulator.setEditingState(mode);

		switch (mode) {

		case ADDING:
		case EDITING:

			table.setEditable(true);
			table.setFilterBarVisible(false);
			table.setColumnReorderingAllowed(false);
			table.setSortEnabled(false);

			break;

		case READONLY:

			table.setEditable(false);
			table.setFilterBarVisible(true);
			table.setColumnReorderingAllowed(true);
			table.setSortEnabled(true);

			break;

		default:
			break;

		}
	}

	@Override
	@Deprecated
	public void setReadOnly(boolean readOnly) {

		if (readOnly) {
			setEditingState(EditingState.READONLY);
		} else {
			setEditingState(EditingState.EDITING);
		}

	}

	private void init() {

		addStyleName("component_view");

		table = new StandardTable();
		table.setAutoSelectFirstItem(false);

		setEditingState(EditingState.READONLY);

	}

	private void layout() {

		addComponent(table);
		setExpandRatio(table, 1.0f);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setItemProperty(Property property, Object value) {
		boolean readOnly = property.isReadOnly();
		property.setReadOnly(false);
		property.setValue(value);
		property.setReadOnly(readOnly);

	}

	@Override
	public void setHeight(String height) {
		table.setHeight(height);
	}

	public void removeAllItems() {
		table.removeAllItems();
	}

	public abstract void setData(ObjectData refObjectData);

}
