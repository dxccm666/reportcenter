package edu.missouri.cf.projex4.ui.desktop.configuration.users;

import java.sql.SQLException;

import org.slf4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.data.system.core.securitygroups.Permissions;
import edu.missouri.cf.projex4.data.system.core.securitygroups.ApplicationPermissions;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.common.system.AdvancedTableEditComponent;
import edu.missouri.cf.projex4.ui.desktop.NavigatorOpenerButton;
import edu.missouri.cf.projex4.ui.desktop.StandardTable;
import edu.missouri.cf.projex4.ui.desktop.TableControlLayout;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;
import edu.missouri.cf.projex4.ui.desktop.configuration.securitygroups.PermissionValueComboBox;

@SuppressWarnings("serial")
public class UserApplicationsComponent extends TableDependentProjexEditor {

	private static Logger logger = Loggers.getLogger(UserApplicationsComponent.class);
	private StandardTable permissionTable;
	private AdvancedTableEditComponent permissionTableEditControls;
	private ApplicationRightIdOpener opener;

	public UserApplicationsComponent() {

		setCaption("Applications");
		addButton.setDescription("Add New Application");

		permissionTable = new StandardTable();
		permissionTable.add(new TableColumn("ID", "ID").setReadOnly(true));
		permissionTable.add(new TableColumn("APPLICATIONRIGHTID", "APPLICATIONRIGHTID").setReadOnly(true));
		permissionTable.add(new TableColumn("RIGHT", "RIGHT").setReadOnly(true));
		permissionTable.add(new TableColumn("PERMISSIONVALUE", "Permission Value").setEditorClass(PermissionValueComboBox.class));
		permissionTable.setCaption("Permissions");

		permissionTableEditControls = new AdvancedTableEditComponent();
		opener = new ApplicationRightIdOpener();

		addComponent(new TableControlLayout() {
			{
				addLeftComponent(permissionTableEditControls);
				addLeftComponent(opener);
			}
		});
		addComponent(permissionTable);
	}

	String userId;
	private OracleContainer permissionSqlContainer;
	private Permissions permissionQuery;

	public void setData(String userId) {

		this.userId = userId;

		table.add(new TableColumn("APPLICATIONID", "Application"));
		// table.add(new TableColumn("REFID", "userID"));

		ApplicationPermissions query = new ApplicationPermissions();
		System.err.println("userId = " + userId);
		query.setUserId(userId);

		try {
			sqlContainer = new OracleContainer(query);
			table.setContainerDataSource(sqlContainer);
			table.configure();

		} catch (SQLException e) {
			logger.error("Could not set sqlcontainer", e);
		}

		addButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				sqlContainer.addItem();

			}

		});

		permissionQuery = new Permissions();
		try {
			permissionSqlContainer = new OracleContainer(permissionQuery);
			permissionTable.setContainerDataSource(permissionSqlContainer);
			permissionTable.configure();
			permissionTableEditControls.setAttachedTable(permissionTable);
		} catch (SQLException e) {
			logger.error("Could not set sqlcontainer", e);
		}

		table.setMultiSelect(false);
		table.addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {

				Object rowId = table.getValue();
				if (rowId != null) {
					String id = rowId.toString();
					permissionSqlContainer.setAutoRefresh(false);
					permissionSqlContainer.removeAllContainerFilters();
					// permissionQuery.removeMandatoryFilters();
					permissionQuery.setMandatoryFilters(new Compare.Equal("APPLICATIONPERMISSIONID", id));
					permissionSqlContainer.setAutoRefresh(true);
				} else {
					permissionSqlContainer.setAutoRefresh(false);
					permissionSqlContainer.removeAllContainerFilters();
					permissionQuery.setMandatoryFilters(new Compare.Equal("ID", "0"));
					permissionSqlContainer.setAutoRefresh(true);
				}
			}

		});

		opener.setTable(permissionTable);
	}

	@Override
	public void setData(ObjectData refObjectData) {
		// TODO Auto-generated method stub
	}

	class ApplicationRightIdOpener extends NavigatorOpenerButton {

		public ApplicationRightIdOpener() {
			init();
		}

		private void init() {
			addComponent(ProjexViewProvider.Views.APPLICATIONRIGHTID, "ApplicationRight");
		}

	}

}
