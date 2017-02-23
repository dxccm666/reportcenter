package edu.missouri.cf.projex4.ui.desktop;

import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.ui.Component;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.common.system.DefaultFormEditControls;
import edu.missouri.cf.projex4.ui.common.system.StandardFormEditControls;
import edu.missouri.cf.projex4.ui.desktop.common.ObjectNotifier;

/**
 * 
 * 
 */
@SuppressWarnings("serial")
public abstract class ListEditorView extends EditorView implements EditorComponent {

	protected Logger logger = Loggers.getLogger(this.getClass());

	/**
	 * Editing controls for the form. Already initialized.
	 */
	protected DefaultFormEditControls controls;

	protected StandardTable table;

	public void setAttachedTable(StandardTable table) {
		this.table = table;
	}

	public ListEditorView() {
		super();
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	protected String objectName;
	protected ObjectNotifier notifier;

	private void init() {

		editingStateManipulator = new ExtendedEditingStateManipulator();
		controls = new StandardFormEditControls();
		notifier = new ObjectNotifier();
	}

	@Override
	public void setEditingState(EditingState state) {
		editingStateManipulator.setEditingState(state);
	}

	private ArrayList<OracleContainer> sqlContainers = new ArrayList<OracleContainer>();

	protected void addOracleContainer(OracleContainer container) {
		sqlContainers.add(container);
	}

	protected void clearOracleContainers() {
		sqlContainers.clear();
	}

	@Override
	public void validate() throws InvalidValueException {
		
		for (Validator validator : validators) {
			validator.validate(null);
		}

		if (table != null) {
			table.validate();
		}

		for (EditorComponent e : ((ExtendedEditingStateManipulator) editingStateManipulator).getDependentEditorComponents()) {
			logger.debug("committing dependent EditorComponents");
			e.validate();
		}

	}
	
	protected ArrayList<Validator> validators = new ArrayList<Validator>();

	protected void addValidator(Validator validator) {
		validators.add(validator);
	}

	/**
	 * Commits the binder and sqlcontainer. On success it sets the item and
	 * rebinds the item with its current data. Makes sure the non-editable
	 * components are read only. Disables editing. Extra steps should be handled
	 * in {@link #afterCommit()}, such as updating calculated fields not found
	 * in the OracleContainer.
	 */
	@Override
	public void commit() throws CommitException, SQLException {

		try {
			validate();
		} catch (InvalidValueException e) {
			throw new CommitException(e.getMessage());
		}

		if (table != null) {
			table.commit();
		}

		for (EditorComponent e : ((ExtendedEditingStateManipulator) editingStateManipulator).getDependentEditorComponents()) {
			logger.debug("committing dependent EditorComponents");
			e.commit();
		}

		for (OracleContainer c : sqlContainers) {
			c.commit();
		}

		afterCommit();

		if (getObjectData() != null && getObjectName() != null) {
			notifier.updatedNotification(getObjectData().getObjectId(), getObjectName());
			setScreenData(getObjectData().getUUID());
		}

		setEditingState(EditingState.READONLY);
	}

	public void afterCommit() {

	}

	@Override
	public void rollback() {

		for (EditorComponent e : ((ExtendedEditingStateManipulator) editingStateManipulator).getDependentEditorComponents()) {
			logger.debug("rolling back dependent EditorComponents");
			e.rollback();
		}

		logger.debug("rolling back");

		setEditingState(EditingState.READONLY);

	}

	public void addEditableComponent(Component c) {
		if (c == null) {
			return;
		}
		((ExtendedEditingStateManipulator) editingStateManipulator).addEditableComponent(c);
	}

	public void addNonEditableComponent(Component c) {

		if (c == null) {
			return;
		}
		((ExtendedEditingStateManipulator) editingStateManipulator).addNonEditableComponent(c);

	}

	@Override
	public void setApplicationName(ProjexViewProvider.Views view) {
		super.setApplicationName(view.name());
		notifier.setApplicationName(view.name());
		controls.setApplicationName(view.name());
	}

	@Override
	public void setApplicationName(String applicationName) {
		super.setApplicationName(applicationName);
		notifier.setApplicationName(applicationName);
		controls.setApplicationName(applicationName);
	}

	@Override
	public void setObjectData(ObjectData objectData) {
		super.setObjectData(objectData);
		controls.setObjectData(objectData);
	}

	/**
	 * @return the objectName
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @param objectName
	 *            the objectName to set
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
		controls.setObjectName(objectName);
	}

}
