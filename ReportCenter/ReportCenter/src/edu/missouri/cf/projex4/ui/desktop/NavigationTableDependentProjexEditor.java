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
import com.vaadin.ui.Label;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.common.system.ExportButton;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public abstract class NavigationTableDependentProjexEditor extends DependentProjexEditor implements EditorComponent, BufferedValidatable {

	private static Logger logger = Loggers.getLogger(NavigationTableDependentProjexEditor.class);

	protected ExportButton exportButton;
	protected StandardTable table;
	protected OracleContainer sqlContainer;

	protected TableControlLayout buttons;
	
	@SuppressWarnings("unused")
	private boolean useSecurityGroupCheck = false;
	
	public void setSecurityGroupCheck(boolean securityGroupCheck) {
		useSecurityGroupCheck = securityGroupCheck;
	}

	protected Label instructions;

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
	public NavigationTableDependentProjexEditor() {
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
			} catch (UnsupportedOperationException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Could not commit", e);
				}
			} catch (SQLException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Could not commit", e);
				}
			}
			table.commit();
			afterCommit();

		}

		table.setValue(null);

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
	 * @param mode
	 * @see edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator#setEditingState(edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator.EditingState)
	 */
	public void setEditingState(EditingState mode) {

		editingStateManipulator.setEditingState(mode);
		
		table.setFilterBarVisible(true);
		table.setColumnReorderingAllowed(true);
		table.setSortEnabled(true);

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

		exportButton = new ExportButton();
		table = new StandardTable();
		table.setAutoSelectFirstItem(false);
		exportButton.setAttachedTable(table);

		setEditingState(EditingState.READONLY);

	}

	private void layout() {

		buttons = new TableControlLayout() {
			{
				addStyleName("control_buttons");
				setWidth("100%");
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

	@Override
	public void setHeight(String height) {
		table.setHeight(height);
	}

	public void removeAllItems() {
		table.removeAllItems();
	}

	public abstract void setData(ObjectData refObjectData);

}
