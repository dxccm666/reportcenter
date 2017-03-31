package edu.missouri.operations.reportcenter.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.UI;

import edu.missouri.operations.reportcenter.ui.views.DefaultView;
import edu.missouri.operations.reportcenter.ui.views.configuration.ConfigurationView;
import edu.missouri.operations.reportcenter.ui.views.configuration.email.EmailLogsView;
import edu.missouri.operations.reportcenter.ui.views.configuration.email.EmailTemplatesView;
import edu.missouri.operations.reportcenter.ui.views.configuration.lists.ListsView;
import edu.missouri.operations.reportcenter.ui.views.configuration.properties.SystemPropertiesView;
import edu.missouri.operations.reportcenter.ui.views.configuration.reports.ReportsView;
import edu.missouri.operations.reportcenter.ui.views.configuration.reports.ScheduledReportsView;
import edu.missouri.operations.reportcenter.ui.views.configuration.scheduledtasks.ScheduledTasksView;
import edu.missouri.operations.reportcenter.ui.views.configuration.securitygroups.SecurityGroupsView;
import edu.missouri.operations.reportcenter.ui.views.configuration.users.UsersView;
import edu.missouri.operations.reportcenter.ui.views.main.MainView;

@SuppressWarnings("serial")
public class ReportCenterViewProvider implements ViewProvider {

	protected static final transient Logger logger = LoggerFactory.getLogger(ReportCenterViewProvider.class);

	public enum Views {
		DEFAULT, HOME, LOGIN, CONFIGURATION, SYSTEMPROPERTIES, 
		USERS, USEREDITOR, SECURITYGROUPS, SECURITYGROUPEDITOR, 
		REPORTS, REPORTEDITOR, LISTS, EMAILTEMPLATES, EMAILTEMPLATEEDITOR, 
		EMAILLOGS, SCHEDULEDREPORTS, SCHEDULEDTASKS
	}

	public ReportCenterViewProvider() { }

	@Override
	public String getViewName(String viewAndParameters) {
		if (logger.isTraceEnabled()) {
			logger.trace("getViewName({})", viewAndParameters);
		}

		if (null == viewAndParameters) {
			return null;
		}

		for (Views view : Views.values()) {
			if (viewAndParameters.equals(view.toString().toLowerCase()) || viewAndParameters.startsWith(view.toString().toLowerCase() + "/")) {
				return view.toString().toLowerCase();
			}
		}
		/*
		 * Handles the case where a user types in the url for the website, but
		 * doesn't specify which screen to go to. Automatically goes to the Home
		 * screen after login.
		 */
		if (viewAndParameters.equals("")) {
			return Views.HOME.toString().toLowerCase();
		}

		return null;
	}

	// static LoginView loginView = new LoginView();

	public View getView(Views view) {

		View screen;

		switch (view) {

		default:
			screen = getInternalView(view);
			break;
		}

		return screen;

	}

	public View getInternalView(Views view) {

		switch (view) {
		case HOME:
			 return new MainView();
		case CONFIGURATION :
			return new ConfigurationView();
		case SYSTEMPROPERTIES :
			return new SystemPropertiesView();
		case LISTS :
			return new ListsView();
		case EMAILTEMPLATES :
			return new EmailTemplatesView();
		case USERS:
			return new UsersView();
		case SECURITYGROUPS:
			return new SecurityGroupsView();
		case REPORTS:
			return new ReportsView();
		case SCHEDULEDREPORTS:
			return new ScheduledReportsView();
		case EMAILLOGS :
			return new EmailLogsView();
		case SCHEDULEDTASKS :
			return new ScheduledTasksView();
			 
		case LOGIN:
			// return new LoginView();
		default:
			// return new LoginView();
			return new MainView();
		}
	}

	public boolean requireLogin(Views view) {
		switch (view) {
		
		default:
			return true;
		}
	}

	public boolean requireLogin(String viewName) {
		return requireLogin(Views.valueOf(viewName.toUpperCase()));
	}

	public UI getViewAsUI(Views view) {
		return (UI) getView(view);
	}

	@Override
	public View getView(String viewName) {
		return getView(Views.valueOf(viewName.toUpperCase()));
	}

}
