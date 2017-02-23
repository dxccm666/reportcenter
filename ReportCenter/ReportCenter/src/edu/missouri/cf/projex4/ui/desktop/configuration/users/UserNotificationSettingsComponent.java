package edu.missouri.cf.projex4.ui.desktop.configuration.users;

import java.sql.SQLException;

import org.slf4j.Logger;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.server.Page;

import edu.missouri.cf.data.OracleBoolean;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.notifications.UserNotificationSettings;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.common.system.CachedStandardComboBox;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;

@SuppressWarnings("serial")
public class UserNotificationSettingsComponent extends TableDependentProjexEditor {

	private static Logger logger = Loggers.getLogger(UserNotificationSettingsComponent.class);

	public static class SettingEditor extends CachedStandardComboBox {

		public SettingEditor() {

			String uriFragment = Page.getCurrent().getUriFragment();

			switch (User.getUser().getUserType()) {

			case ENTERPRISE:
				
				if (uriFragment.indexOf("/manuals/") != 0 || uriFragment.indexOf("/manualentryeditor/") != 0) {
					setListName("usernotificationsettings.settinglevel.enterprise.manualitems");
				} else {
					setListName("usernotificationsettings.settinglevel.enterprise");
				}
				break;

			case EXTERNAL:
				setListName("usernotificationsettings.settinglevel.external");
				break;

			case FACILITIES:
				
				if (uriFragment.indexOf("/manuals/") != 0 || uriFragment.indexOf("/manualentryeditor/") != 0) {
					setListName("usernotificationsettings.settinglevel.facilities.manualitems");
				} else {
					setListName("usernotificationsettings.settinglevel.facilities");
				}
				break;

			case INTERNAL:
			case SYSTEM:
			default:

				setListName("usernotificationsettings.settinglevel");
				break;

			}
			refreshDataCollection();
		}
	}

	private UserNotificationSettings query;

	private String queryApplicationName;

	public void setQueryApplicationName(String queryApplicationName) {
		this.queryApplicationName = queryApplicationName;
	}

	public UserNotificationSettingsComponent() {

		setCaption("Notification Settings");
		setApplicationName("USERSETTINGS");

		setAddingPermitted(false);

		table.add(new TableColumn("APPLICATIONNAME", "Application").setReadOnly(true));
		table.add(new TableColumn("OBJECTCLASS", "Object Class").setReadOnly(true));
		table.add(new TableColumn("NOTIFICATIONTYPE", "Notification Type").setReadOnly(true));
		table.add(new TableColumn("UNSISACTIVE", "Active?"));
		table.add(new TableColumn("UNSISEMAILED", "Emailed?").setCollapsed(true));
		// table.add(new TableColumns("UNSEMAILSCHEDULE","Email
		// Schedule").setCollapsed(true));
		table.add(new TableColumn("UNSSETTINGVALUE", "Value").setEditorClass(SettingEditor.class));

	}

	class DataLoader implements Runnable {

		DataLoader() {
		}

		@Override
		public void run() {

			query = new UserNotificationSettings();

			if (queryApplicationName == null) {
				System.err.println("QUERYAPPLICATIONNAME IS NULL");
				query.setRefObjectId(User.getUser().getUserId());
			} else {
				System.err.println("QUERYAPPLICATIONNAME IS '" + queryApplicationName + "'");
				query.setRefObjectId(User.getUser().getUserId(), queryApplicationName);
			}

			try {

				sqlContainer = new OracleContainer(query);
				sqlContainer.addOrderBy(new OrderBy("APPLICATIONNAME", true), new OrderBy("OBJECTCLASS", true), new OrderBy("NOTIFICATIONTYPE", true));
				sqlContainer.overrideType("UNSISACTIVE", OracleBoolean.class);
				sqlContainer.overrideType("UNSISEMAILED", OracleBoolean.class);

				table.setContainerDataSource(sqlContainer);
				table.configure();

			} catch (SQLException e) {

				if (logger.isErrorEnabled()) {
					logger.error("Could not retrieve notification list");
				}

			}
		}

	}

	public void setData() {

		new DataLoader().run();

	}

	@Override
	public void setData(ObjectData refObjectData) {

	}

}
