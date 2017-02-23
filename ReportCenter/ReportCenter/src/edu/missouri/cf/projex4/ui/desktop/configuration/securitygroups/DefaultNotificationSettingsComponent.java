package edu.missouri.cf.projex4.ui.desktop.configuration.securitygroups;

import java.sql.SQLException;

import org.slf4j.Logger;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.notifications.DefaultNotificationSettings;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;

@SuppressWarnings("serial")
public class DefaultNotificationSettingsComponent extends TableDependentProjexEditor {

	private static Logger logger = Loggers.getLogger(DefaultNotificationSettingsComponent.class);

	public DefaultNotificationSettingsComponent() {

		setCaption("Default Notification Settings");
		//table.add(new TableColumn("DEFAULTFOR", "Default For"));
		
		setAddingPermitted(false);
		
	}
	
	String securityGroupId;

	public void setData(String securityGroupId) {
		
		this.securityGroupId = securityGroupId;

		DefaultNotificationSettings query = new DefaultNotificationSettings();
		query.setSecurityGroupId(securityGroupId);

		try {
			sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("APPLICATIONNAME",true));
			sqlContainer.addOrderBy(new OrderBy("OBJECTCLASS",true));
			sqlContainer.addOrderBy(new OrderBy("NOTIFICATIONTYPEDESCRIPTION",true));
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
