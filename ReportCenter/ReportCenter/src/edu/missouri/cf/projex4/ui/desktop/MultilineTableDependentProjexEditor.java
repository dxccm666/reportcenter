/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.tepi.filtertable.FilterTable;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.data.BufferedValidatable;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.common.system.ExportButton;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public abstract class MultilineTableDependentProjexEditor extends DependentProjexEditor
		implements EditorComponent, BufferedValidatable {

	private static Logger logger = Loggers.getLogger(MultilineTableDependentProjexEditor.class);

	protected Button addButton;
	protected ExportButton exportButton;
	protected Button deleteButton;
	protected StandardTable table;
	protected OracleContainer sqlContainer;

	protected TableControlLayout buttons;

	protected Label instructions;

	boolean addingPermitted = true;
	boolean deletingPermitted = true;

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
	public MultilineTableDependentProjexEditor() {
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
			} catch (UnsupportedOperationException | SQLException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Could not commit", e);
					// TODO Remove this notification before final release
					Notification.show("Could not commit table changes - " + e.getMessage());
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
	 * @param mode
	 * @see edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator#setEditingState(edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator.EditingState)
	 */
	public void setEditingState(EditingState mode) {

		editingStateManipulator.setEditingState(mode);

		switch (mode) {

		case ADDING:
		case EDITING:

			if (logger.isDebugEnabled()) {
				logger.debug("Application Name = {}, SubApplicationName = {}", objectManipulator.getApplicationName(),
						getSubApplicationName());
			}

			String objectId = null;

			if (objectManipulator.getObjectData() != null) {
				objectId = objectManipulator.getObjectData().getObjectId();
			}

			if (User.canDo(User.getUser().getUserId(), objectManipulator.getApplicationName(), objectId,
					getSubApplicationName() + ".EDIT")) {

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

		addButton = new PopupButton() {
			{

				setCaption("add ...");
				setDescription("add n rows to table");
				setIcon(new ThemeResource("icons/chalkwork/basic/add_16x16.png"));
				addStyleName("borderless");

				final Button addOneButton = new Button() {
					{
						setCaption("1 row");
						addStyleName("borderless");

						addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								addRows(1);
								setPopupVisible(false);
							}

						});
					}
				};

				final Button addTenButton = new Button() {
					{
						setCaption("10 rows");
						addStyleName("borderless");

						addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								addRows(10);
								setPopupVisible(false);
							}

						});
					}
				};

				final Button addFiftyButton = new Button() {
					{
						setCaption("50 rows");
						addStyleName("borderless");

						addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								addRows(50);
								setPopupVisible(false);
							}
						});

					}
				};

				setContent(new VerticalLayout() {
					{
						addComponent(addOneButton);
						addComponent(addTenButton);
						addComponent(addFiftyButton);
					}
				});

			}
		};

		deleteButton = new Button("delete") {
			{
				setIcon(new ThemeResource("icons/general/small/Delete.png"));
				addStyleName("borderless");
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
				addLeftComponent(addButton);
				addLeftComponent(deleteButton);
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

	public abstract void addRows(int num);

}
