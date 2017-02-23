package edu.missouri.cf.projex4.ui.desktop.configuration.users;

import java.sql.SQLException;

import org.slf4j.Logger;

import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.OracleContainer;

import edu.missouri.cf.projex4.data.projects.ProjectAssignDetails;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.common.system.StandardComboBox;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;

@SuppressWarnings("serial")
public class UserProjectAssignComponent extends TableDependentProjexEditor {
	
	private static Logger logger = Loggers.getLogger(UserProjectAssignComponent.class);
	
	public static class SettingEditor extends StandardComboBox {
		public SettingEditor() {
			setListName("usernotificationsettings.settinglevel");
			refreshDataCollection();
		}
	}

	private ProjectAssignDetails projectDetails;
	private OracleContainer projectDetailsSqlContainer;
	private String userID;

	public UserProjectAssignComponent() {
	
		setCaption("");		
		setAddingPermitted(false);		
		table.add(new TableColumn("CAMPUSNAME", "Campus Name"));						
		table.add(new TableColumn("PROJECTNUMBER", "Project Number"));
		table.add(new TableColumn("COMBINEDTITLE", "Combine Title"));
		table.add(new TableColumn("STATUS", "Status"));
		table.add(new TableColumn("PRIMARYPMNAME", "Primary PM Name"));
		
	}
	

	public void setData(String userID) {
		
		this.userID = userID;
				
		projectDetails = new ProjectAssignDetails();
		projectDetails.setMandatoryFilters(new Compare.Equal("USERID", userID));
		try {
			projectDetailsSqlContainer = new OracleContainer(projectDetails);
			table.setContainerDataSource(projectDetailsSqlContainer);
			table.configure();
		} catch (SQLException e) {
			logger.error(e.getSQLState());
		}
		
		
	}

	@Override
	public void setData(ObjectData refObjectData) {
		
	}

}
