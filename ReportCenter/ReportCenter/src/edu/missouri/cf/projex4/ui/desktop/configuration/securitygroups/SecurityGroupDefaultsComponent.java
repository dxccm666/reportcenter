package edu.missouri.cf.projex4.ui.desktop.configuration.securitygroups;

import java.sql.SQLException;

import org.slf4j.Logger;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.data.system.core.securitygroups.SecurityGroupDefaults;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;

@SuppressWarnings("serial")
public class SecurityGroupDefaultsComponent extends TableDependentProjexEditor {

	private static Logger logger = Loggers.getLogger(SecurityGroupDefaultsComponent.class);

	public SecurityGroupDefaultsComponent() {

		setCaption("Group Defaults");
		addButton.setDescription("Add New Default For");
		
		table.add(new TableColumn("DEFAULTFOR", "Default For"));
		
		addButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				sqlContainer.addItem();
				
			}

		});
		
	}
	
	String securityGroupId;

	public void setData(String securityGroupId) {
		
		this.securityGroupId = securityGroupId;

		SecurityGroupDefaults query = new SecurityGroupDefaults();
		query.setSecurityGroupId(securityGroupId);

		try {
			sqlContainer = new OracleContainer(query);
			table.setContainerDataSource(sqlContainer);
			table.configure();

		} catch (SQLException e) {
			logger.error("Could not set sqlcontainer", e);
		}
		
	}

	@Override
	public void setData(ObjectData refObjectData) {
		
	}
	
}
