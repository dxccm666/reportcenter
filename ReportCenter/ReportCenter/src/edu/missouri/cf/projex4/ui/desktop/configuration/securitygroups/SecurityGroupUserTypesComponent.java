package edu.missouri.cf.projex4.ui.desktop.configuration.securitygroups;

import java.sql.SQLException;

import org.slf4j.Logger;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.data.system.core.securitygroups.SecurityGroupUserTypes;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;

@SuppressWarnings("serial")
public class SecurityGroupUserTypesComponent extends TableDependentProjexEditor {

	private static Logger logger = Loggers.getLogger(SecurityGroupUserTypesComponent.class);

	public SecurityGroupUserTypesComponent() {

		setCaption("User Types");
		addButton.setDescription("Add New User Type");
		
		table.add(new TableColumn("USERTYPE", "User Type"));
		
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

		SecurityGroupUserTypes query = new SecurityGroupUserTypes();
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
		// TODO Auto-generated method stub
	}
	
}
