package edu.missouri.cf.projex4.ui.desktop.configuration.users;

import java.sql.SQLException;

import org.slf4j.Logger;

import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;

import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.data.system.core.users.UserLoginHistory;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.common.system.StandardComboBox;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;

@SuppressWarnings("serial")
public class UserLoginHistoryComponent extends TableDependentProjexEditor {
	
	private static Logger logger = Loggers.getLogger(UserNotificationSettingsComponent.class);
	
	public static class SettingEditor extends StandardComboBox {
		public SettingEditor() {
			setListName("usernotificationsettings.settinglevel");
			refreshDataCollection();
		}
	}

	
	private UserLoginHistory userloginHistoryQuery;
	private OracleContainer loginHistorySqlContainer;
	private String userId;

	public UserLoginHistoryComponent() {
	
		setCaption("Notification Settings");
		
		setAddingPermitted(false);
		
		table.add(new TableColumn("USERID", "USERID"));				
		table.add(new TableColumn("LOGGEDIN", "LOGGEDIN"));
		table.add(new TableColumn("IPADDRESS", "IPADDRESS"));
		
	}
	

	public void setData(String userId) {
		
		this.userId = userId;
				
		userloginHistoryQuery = new UserLoginHistory();
		userloginHistoryQuery.setMandatoryFilters(new Compare.Equal("USERID", userId));
		try {
			loginHistorySqlContainer = new OracleContainer(userloginHistoryQuery);
			loginHistorySqlContainer.addOrderBy(new OrderBy("LOGGEDIN", true));
			table.setContainerDataSource(loginHistorySqlContainer);
			table.configure();
		} catch (SQLException e) {
			logger.error(e.getSQLState());
		}
		
		
	}

	@Override
	public void setData(ObjectData refObjectData) {
		
	}

}
