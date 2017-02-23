package edu.missouri.cf.projex4.ui.desktop.configuration.securitygroups;

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

@SuppressWarnings("serial")
public class SecurityGroupApplicationsComponent extends TableDependentProjexEditor {

	private static Logger logger = Loggers.getLogger(SecurityGroupApplicationsComponent.class);
	private StandardTable permissionTable;
	private AdvancedTableEditComponent permissionTableEditControls;
	private ApplicationRightIdOpener opener;

	public SecurityGroupApplicationsComponent() {

		setCaption("Applications");
		addButton.setDescription("Add New Application");
		
		addButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				sqlContainer.addItem();

			}

		});
		
		table.add(new TableColumn("APPLICATIONID", "Application"));

		permissionTable = new StandardTable() {
			{
				add(new TableColumn("ID", "ID").setReadOnly(true));
				add(new TableColumn("APPLICATIONRIGHTID", "APPLICATIONRIGHTID").setReadOnly(true));
				add(new TableColumn("RIGHT", "RIGHT").setReadOnly(true));
				add(new TableColumn("PERMISSIONVALUE", "Permission Value").setEditorClass(PermissionValueComboBox.class));
				setCaption("Permissions");
			}
		};

		permissionTableEditControls = new AdvancedTableEditComponent();
		opener = new ApplicationRightIdOpener();
		
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
		addComponent(new TableControlLayout() {
			{
				addLeftComponent(permissionTableEditControls);
				addLeftComponent(opener);
			}
		});
		
		addComponent(permissionTable);
	}

	String securityGroupId;
	private OracleContainer permissionSqlContainer;
	private Permissions permissionQuery;

	public void setData(String securityGroupId) {

		this.securityGroupId = securityGroupId;

		ApplicationPermissions query = new ApplicationPermissions();
		query.setSecurityGroupId(securityGroupId);

		try {
			sqlContainer = new OracleContainer(query);
			table.setContainerDataSource(sqlContainer);
			table.configure();

		} catch (SQLException e) {
			logger.error("Could not set sqlcontainer", e);
		}

		permissionQuery = new Permissions();
		try {
			permissionSqlContainer = new OracleContainer(permissionQuery);
			permissionTable.setContainerDataSource(permissionSqlContainer);
			permissionTable.configure();
			permissionTableEditControls.setAttachedTable(permissionTable);
		} catch (SQLException e) {
			logger.error("Could not set sqlcontainer", e);
		}
		
	}

	@Override
	public void setData(ObjectData refObjectData) {
		// Not implemented due to data not being an object.
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
