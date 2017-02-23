package edu.missouri.cf.projex4.ui.desktop.configuration.users;

import java.sql.SQLException;

import org.slf4j.Logger;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;

import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.data.system.properties.UserProperties;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;

@SuppressWarnings("serial")
public class UserPropertiesComponent extends TableDependentProjexEditor {
	
	private static Logger logger = Loggers.getLogger(UserPropertiesComponent.class);
	
	private UserProperties query;

	public UserPropertiesComponent() {
	
		setAddingPermitted(false);
		
		table.add(new TableColumn("REFIDTYPE", "TYPE"));
		table.add(new TableColumn("REFID", "REFID"));
		table.add(new TableColumn("PROPERTY", "Property"));
		table.add(new TableColumn("VALUE", "Value"));
		
		table.setContextHelp("");
		table.setMultiSelect(false);
		table.setSizeFull();
	}
	

	public void setData(String userId) {
		
		query = new UserProperties();
		query.setUserId(userId);
		
		try {
			
			sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("ID", true));
			table.setContainerDataSource(sqlContainer);
			table.configure();
			
		} catch (SQLException e) {
			
			if(logger.isErrorEnabled()) {
				logger.error(e.getSQLState());
			}
			
		}
		
		
	}

	@Override
	public void setData(ObjectData refObjectData) {
		throw new UnsupportedOperationException("ObjectData is not accepted");
	}

}
