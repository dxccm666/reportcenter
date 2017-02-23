/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop;

import java.sql.SQLException;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;

import org.slf4j.Logger;
import org.tepi.filtertable.FilterTable;

import com.vaadin.data.Property;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.data.system.core.securitygroups.SecurityGroups;
import edu.missouri.cf.projex4.ui.common.system.ExportButton;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public abstract class TableDependentProjexEditor extends DependentProjexEditor implements EditorComponent, Validatable {

	private static Logger logger = Loggers.getLogger(TableDependentProjexEditor.class);

	protected Button addButton;
	protected ExportButton exportButton;
	protected Button editButton;
	protected Button deleteButton;
	protected LayoutControls layoutControls;

	protected StandardTable table;
	protected OracleContainer sqlContainer;

	protected TableControlLayout buttons;

	protected Label instructions;

	protected boolean addingPermitted = true;
	protected boolean deletingPermitted = true;
	protected boolean editingPermitted = false;
	protected boolean formattingPermitted = false;

	public void setFormattingPermitted(boolean formattingPermitted) {
		this.formattingPermitted = formattingPermitted;
		layoutControls.setEnabled(formattingPermitted);
		layoutControls.setVisible(formattingPermitted);
	}

	public void setTableName(String tableName) {
		table.setTableName(tableName);
		layoutControls.setTableName(tableName);
	}

	public void setAddingPermitted(boolean addingPermitted) {
		this.addingPermitted = addingPermitted;
	}

	public boolean isAddingPermitted() {
		return addingPermitted;
	}

	public void setDeletingPermitted(boolean deletingPermitted) {
		this.deletingPermitted = deletingPermitted;
	}

	public boolean isDeletingPermitted() {
		return deletingPermitted;
	}

	public void setEditingPermitted(boolean editingPermitted) {
		this.editingPermitted = editingPermitted;
	}

	public boolean isEditingPermitted() {
		return editingPermitted;
	}

	/**
	 * @return the addButton
	 */
	public Button getAddButton() {
		return addButton;
	}

	/**
	 * @param addButton
	 *            the addButton to set
	 */
	public void setAddButton(Button addButton) {
		this.addButton = addButton;
	}

	/**
	 * @param deleteButton
	 */
	public void setDeleteButton(Button deleteButton) {
		this.deleteButton = deleteButton;
	}

	public Button getDeleteButton() {
		return deleteButton;
	}

	public void setEditButton(Button editButton) {
		this.editButton = editButton;
	}

	public Button getEditButton() {
		return editButton;
	}

	/**
	 * @return the exportButton
	 */
	public ExportButton getExportButton() {
		return exportButton;
	}

	/**
	 * @param exportButton
	 *            the exportButton to set
	 */
	public void setExportButton(ExportButton exportButton) {
		this.exportButton = exportButton;
	}

	public void setExportTitle(String exportTitle) {
		exportButton.setExportTitle(exportTitle);
	}

	public void setExportSheetName(String exportSheetName) {
		exportButton.setExportSheetName(exportSheetName);
	}

	public void setExportFileName(String exportFileName) {
		exportButton.setExportFileName(exportFileName);
	}

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
	public TableDependentProjexEditor() {
		addStyleName("dependenteditor");
		init();
		layout();
	}

	String subApplicationName;

	public void setSubApplicationName(String subApplicationName) {
		this.subApplicationName = subApplicationName;
	}

	public String getSubApplicationName() {
		return subApplicationName;
	}

	@Override
	public void commit() throws SQLException, CommitException, InvalidValueException {
		if (logger.isDebugEnabled()) {
			logger.debug("TableDependentProjexEditor.commit called");
		}

		try {
			table.validate();
			if (sqlContainer != null) {
				sqlContainer.commit();
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("sqlContainer is null");
				}

				if (sqlContainer != null) {
					sqlContainer.commit();
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("sqlContainer is null");
					}
				}
			}

			table.commit();
			afterCommit();
			table.setValue(null);

		} catch (UnsupportedOperationException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Could not commit", e);
				// TODO Remove this notification before final release
				Notification.show("Could not commit table changes - " + e.getMessage());
			}
			throw new CommitException("Could not commit");
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Could not commit", e);
				// TODO Remove this notification before final release
				Notification.show("Could not commit table changes - " + e.getMessage());
			}
		}

	}

	public void afterCommit() {

	}

	public void setInstructionText(String text) {
		instructions.setVisible(true);
		instructions.setCaption(text);
	}

	@Override
	public void addValidator(Validator validator) {
		table.addValidator(validator);
	}

	@Override
	public java.util.Collection<Validator> getValidators() {
		return table.getValidators();
	}

	private boolean validationEnabled = true;

	public boolean isValidationEnabled() {
		return validationEnabled;
	}

	public void setValidationEnabled(boolean validationEnabled) {
		this.validationEnabled = validationEnabled;
	}

	@Override
	public boolean isInvalidAllowed() {
		return table.isInvalidAllowed();
	}

	@Override
	public boolean isValid() {
		if (!validationEnabled) {
			return true;
		} else {
			return table.isValid();
		}
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
	public void validate() throws Validator.InvalidValueException {
		if (validationEnabled) {
			table.validate();
		}
	}

	public boolean isInvalidCommitted() {
		return table.isInvalidCommitted();
	}

	public void setInvalidCommitted(boolean isCommitted) {
		table.setInvalidCommitted(isCommitted);
	}

	public void discard() {
		rollback();
	}

	public boolean isBuffered() {
		return table.isBuffered();
	}

	public boolean isModified() {
		return table.isModified();
	}

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

	private boolean useSecurityGroupCheck = false;

	public void setSecurityGroupCheck(boolean securityGroupCheck) {
		useSecurityGroupCheck = securityGroupCheck;
	}

	private boolean overrideSecurityChecks = false;

	public void setOverrideSecurityChecks(boolean overrideSecurityChecks) {
		this.overrideSecurityChecks = overrideSecurityChecks;
	}

	@SuppressWarnings("deprecation")
	protected boolean checkPermitted() {

		String objectId = null;
		if (objectManipulator.getObjectData() != null) {
			objectId = objectManipulator.getObjectData().getObjectId();
		}
		
		boolean permitted = false;

		if (overrideSecurityChecks) {
			permitted = true;
		} else if (useSecurityGroupCheck) {
			permitted = SecurityGroups.canDo(User.getUser().getUserId(), objectManipulator.getApplicationName(), getSubApplicationName() + ".EDIT");
		} else if (User.UserType.FACILITIES == User.getUser().getUserType()) {
			permitted = SecurityGroups.canDo(User.getUser().getUserId(), objectManipulator.getApplicationName(), getSubApplicationName() + ".EDIT");
		} else {
			permitted = User.canDo(User.getUser().getUserId(), objectManipulator.getApplicationName(), objectId, getSubApplicationName() + ".EDIT");
		}

		return permitted;

	}

	/**
	 * @param mode
	 * @see edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator#setEditingState(edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator.EditingState)
	 */
	@SuppressWarnings("deprecation")
	public void setEditingState(EditingState mode) {

		editingStateManipulator.setEditingState(mode);

		switch (mode) {

		case ADDING:
		case EDITING:

			if (logger.isDebugEnabled()) {
				logger.debug("Application Name = {}, SubApplicationName = {}", objectManipulator.getApplicationName(), getSubApplicationName());
			}

			boolean permitted = checkPermitted();

			if (permitted) {

				if (editingPermitted) {
					editButton.setVisible(true);
					editButton.setEnabled(true);
				} else {
					editButton.setVisible(false);
					editButton.setEnabled(false);
				}

				if (addingPermitted) {
					addButton.setVisible(true);
					addButton.setEnabled(true);
				} else {
					addButton.setVisible(false);
					addButton.setEnabled(false);
				}

				if (deletingPermitted) {
					deleteButton.setVisible(true);
					deleteButton.setEnabled(true);
				} else {
					deleteButton.setVisible(false);
					deleteButton.setEnabled(false);
				}

				table.setEditable(true);
				if (filterBarVisible) {
					table.setFilterBarVisible(false);
				}
				table.setColumnReorderingAllowed(false);
				table.setSortEnabled(false);
			}

			break;

		case READONLY:

			addButton.setVisible(false);
			addButton.setEnabled(false);

			editButton.setVisible(false);
			editButton.setEnabled(false);

			deleteButton.setVisible(false);
			deleteButton.setEnabled(false);

			table.setEditable(false);
			if (filterBarVisible) {
				table.setFilterBarVisible(true);
			}
			table.setColumnReorderingAllowed(true);
			table.setSortEnabled(true);

			break;

		default:
			break;

		}
	}

	/**
	 * @param objectData
	 *            the objectData to set
	 */
	@Deprecated
	public void setRefObjectData(ObjectData objectData) {
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

		instructions = new Label("") {
			{
				setVisible(false);
			}
		};

		addButton = new Button("new") {
			{
				setIcon(new ThemeResource("icons/general/small/Add.png"));
				addStyleName("borderless");
			}
		};

		editButton = new Button("edit") {
			{
				setIcon(new ThemeResource("icons/general/small/Edit.png"));
				addStyleName("borderless");
			}
		};

		deleteButton = new Button("delete") {
			{
				setIcon(new ThemeResource("icons/general/small/Delete.png"));
				addStyleName("borderless");
			}
		};

		exportButton = new ExportButton();
		layoutControls = new LayoutControls() {
			{
				setEnabled(false);
				setVisible(false);
			}
		};

		table = new StandardTable();
		table.setAutoSelectFirstItem(false);
		exportButton.setAttachedTable(table);
		layoutControls.setTable(table);

		setEditingState(EditingState.READONLY);

	}

	private void layout() {

		buttons = new TableControlLayout() {
			{
				addStyleName("control_buttons");
				setWidth("100%");
				addLeftComponent(addButton);
				addLeftComponent(editButton);
				addLeftComponent(deleteButton);
				addRightComponent(layoutControls);
				addRightComponent(exportButton);
			}
		};

		addComponent(instructions);
		addComponent(buttons);
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

	boolean filterBarVisible = false;

	public void setFilterBarVisible(boolean visible) {
		filterBarVisible = visible;
	}

	/*
	 * @Override public void setHeight(String height) { super.setHeight(height);
	 * table.setHeight(height); }
	 */

	public void removeAllItems() {
		table.removeAllItems();
	}

	public abstract void setData(ObjectData refObjectData);

}
