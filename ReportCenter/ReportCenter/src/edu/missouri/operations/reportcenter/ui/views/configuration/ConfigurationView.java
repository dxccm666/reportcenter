package edu.missouri.operations.reportcenter.ui.views.configuration;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.operations.reportcenter.ReportcenterUI;
import edu.missouri.operations.reportcenter.ui.ReportCenterViewProvider;
import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.desktop.buttons.NavigatorButton;

public class ConfigurationView extends TopBarView {

	private NavigatorButton systemPropertiesButton;
	private NavigatorButton listsButton;
	private NavigatorButton usersButton;
	private NavigatorButton securityGroupsButton;
	private NavigatorButton reportsButton;
	private NavigatorButton emailTemplatesButton;
	private NavigatorButton scheduledReportsButton;
	private NavigatorButton emailLogsButton;
	private NavigatorButton scheduledTasksButton;

	public ConfigurationView() {
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	public void init() {
	
		systemPropertiesButton = new NavigatorButton(ReportcenterUI.get().getViewNavigator(), ReportCenterViewProvider.Views.SYSTEMPROPERTIES, "System Properties");
		listsButton = new NavigatorButton(ReportcenterUI.get().getViewNavigator(), ReportCenterViewProvider.Views.LISTS, "Lists");
		emailTemplatesButton = new NavigatorButton(ReportcenterUI.get().getViewNavigator(), ReportCenterViewProvider.Views.EMAILTEMPLATES, "Email Templates");
		emailLogsButton = new NavigatorButton(ReportcenterUI.get().getViewNavigator(), ReportCenterViewProvider.Views.EMAILLOGS, "Email Logs");
		usersButton = new NavigatorButton(ReportcenterUI.get().getViewNavigator(), ReportCenterViewProvider.Views.USERS, "Users");
		securityGroupsButton = new NavigatorButton(ReportcenterUI.get().getViewNavigator(), ReportCenterViewProvider.Views.SECURITYGROUPS, "Security Groups");
		reportsButton = new NavigatorButton(ReportcenterUI.get().getViewNavigator(), ReportCenterViewProvider.Views.REPORTS, "Report Registration");
		scheduledReportsButton = new NavigatorButton(ReportcenterUI.get().getViewNavigator(), ReportCenterViewProvider.Views.SCHEDULEDREPORTS, "Scheduled Reports");
		scheduledTasksButton = new NavigatorButton(ReportcenterUI.get().getViewNavigator(), ReportCenterViewProvider.Views.SCHEDULEDTASKS, "Scheduled Tasks");
		
		addInnerComponent(new VerticalLayout() {
			{
				setSizeFull();
				setMargin(true);
				addComponent(new Label("ReportCenter Configuration", ContentMode.HTML) {
					{
						addStyleName("maintitle");
					}
				});
				addComponent(systemPropertiesButton);
				addComponent(listsButton);
				addComponent(emailTemplatesButton);
				addComponent(emailLogsButton);
				addComponent(scheduledTasksButton);
				addComponent(usersButton);
				addComponent(securityGroupsButton);
				addComponent(reportsButton);
				addComponent(scheduledReportsButton);
				Label spacer = new Label("");
				addComponent(spacer);
				setExpandRatio(spacer, 1.0f);
			}
		});
		
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
