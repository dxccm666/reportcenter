/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.tepi.filtertable.FilterTable;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.common.CollapsibleLayout;
import edu.missouri.cf.projex4.ui.common.system.DefaultObjectManipulator;
import edu.missouri.cf.projex4.ui.common.system.ExportButton;
import edu.missouri.cf.projex4.ui.common.system.ObjectManipulator;
import edu.missouri.cf.projex4.data.system.SystemLockException;

/**
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public abstract class CollapsibleTableDependentProjexEditor extends CollapsibleLayout implements EditorComponent {

	private static Logger logger = Loggers.getLogger(CollapsibleTableDependentProjexEditor.class);

	protected Button addButton;
	protected ExportButton exportButton;
	protected FilterTable table;
	protected ObjectData refObjectData;
	protected OracleContainer sqlContainer;
	/**
	 * @return the addButton
	 */
	public Button getAddButton() {
		return addButton;
	}

	/**
	 * @param addButton the addButton to set
	 */
	public void setAddButton(Button addButton) {
		this.addButton = addButton;
	}

	/**
	 * @return the exportButton
	 */
	public ExportButton getExportButton() {
		return exportButton;
	}

	/**
	 * @param exportButton the exportButton to set
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
	 * @param table the table to set
	 */
	public void setTable(FilterTable table) {
		this.table = table;
	}

	/**
	 * @return the sqlContainer
	 */
	public OracleContainer getSqlContainer() {
		return sqlContainer;
	}

	/**
	 * @param sqlContainer the sqlContainer to set
	 */
	public void setSqlContainer(OracleContainer sqlContainer) {
		this.sqlContainer = sqlContainer;
	}

	protected ObjectManipulator objectManipulator = new DefaultObjectManipulator();
	protected EditingStateManipulator editingStateManipulator = new DefaultEditingStateManipulator();
	
	/**
	 * 
	 */
	public CollapsibleTableDependentProjexEditor() {
		init();
	}

	@Override
	public void setObjectData(ObjectData objectData) {
		objectManipulator.setObjectData(objectData);
	}
	
	@Override
	public ObjectData getObjectData() {
		return objectManipulator.getObjectData();
	}

	/**
	 * @param lockName
	 * @see edu.missouri.cf.projex4.ui.common.system.ObjectManipulator#setLockName(java.lang.String)
	 */
	public void setLockName(String lockName) {
		objectManipulator.setLockName(lockName);
	}

	/**
	 * @return
	 * @see edu.missouri.cf.projex4.ui.common.system.ObjectManipulator#getLockName()
	 */
	public String getLockName() {
		return objectManipulator.getLockName();
	}

	/**
	 * @param locked
	 * @see edu.missouri.cf.projex4.ui.common.system.ObjectManipulator#setEditingLock(boolean)
	 */
	public void setEditingLock(boolean locked) throws SystemLockException {
		objectManipulator.setEditingLock(locked);
	}
	
	@Override
	public void commit() {
		
		logger.debug("ParticipantPersonComponent.commit called");
		try {
			if(sqlContainer!=null) sqlContainer.commit();
		} catch (UnsupportedOperationException | SQLException e) {
			e.printStackTrace();
		}
		table.commit();
		
	}

	@Override
	public void rollback() {
		
		logger.debug("ParticipantPersonComponent.rollback called");
		table.discard();
		try {
			if(sqlContainer!=null) sqlContainer.rollback();
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
	}
	
	/**
	 * @return the objectData
	 */
	public ObjectData getRefObjectData() {
		return refObjectData;
	}

	/**
	 * @param objectData the objectData to set
	 */
	public void setRefObjectData(ObjectData objectData) {
		refObjectData = objectData;
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
		
		addButton.setVisible(!readOnly);
		try {
		table.setEditable(!readOnly);
		table.setFilterBarVisible(readOnly);
		table.setColumnReorderingAllowed(readOnly);
		table.setSortEnabled(readOnly);
		}catch (Exception e) {
			logger.error("Error thrown",e);
		}
		
	}
	
	private void init() {
		
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setWidth("100%");
		
		addButton = new Button("new");
		addButton.setIcon(new ThemeResource("icons/general/small/Add.png"));
		addButton.addStyleName("borderless");
		buttons.addComponent(addButton);
	
		Label buttonSpacer = new Label();
		buttons.addComponent(buttonSpacer);
		buttons.setExpandRatio(buttonSpacer, 1.0f);
		
		exportButton = new ExportButton();
		buttons.addComponent(exportButton);
		
		addComponent(buttons);
		table = new FilterTable();
		table.addStyleName("projectlisting_table");
		table.setImmediate(true);
		table.setWidth("100%");
		table.setSizeFull();
		
		table.setFilterBarVisible(true);
		table.setSelectable(false);
		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(true);
		table.setEditable(false);
		
		exportButton.setAttachedTable(table);
		
		setReadOnly(true);
		
		addComponent(table);
		
	}
	
	public abstract void setData(ObjectData refObjectData);
	
}
