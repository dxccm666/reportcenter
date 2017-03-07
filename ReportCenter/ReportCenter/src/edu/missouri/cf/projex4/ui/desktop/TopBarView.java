/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c10n.C10N;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.system.SystemLocks;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Bulletins;
import edu.missouri.cf.projex4.data.system.core.securitygroups.SecurityGroups;
import edu.missouri.cf.projex4.ui.c10n.TopBarText;
import edu.missouri.operations.ui.views.ErrorView;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public abstract class TopBarView extends VerticalLayout implements View, ProjexView {

	protected TopBarView view;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected Button homeButton;
	protected Button projectsButton;
	protected Button configurationButton;
	protected Button documentsearchButton;
	protected Button userButton;
	private Button logoffButton;

	protected TopBarText topBarText;

	protected TopBarLink reportsLink;
	protected TopBarLink helpLink;

	protected VerticalLayout notificationLayout;

	public void trace(String message) {

		if (logger.isDebugEnabled()) {
			logger.debug("{} - {}", message, new java.util.Date().getTime());
		}

	}

	public TopBarView() {
		super();
		init();
		layout();
	}

	public TopBarView(Component... children) {
		super(children);
	}

	@Override
	public void attach() {
		
		super.attach();
		view = this;
		
		if (!User.getUser().getUserLocale().equals(Locale.ENGLISH)) {

			topBarText = C10N.get(TopBarText.class, User.getUser().getUserLocale());

			homeButton.setDescription(topBarText.projectsHome());

			projectsButton.setCaption(topBarText.projects());
			projectsButton.setDescription(topBarText.projects_help());

			configurationButton.setCaption(topBarText.configuration());
			configurationButton.setDescription(topBarText.configuration_help());

			reportsLink.setCaption(topBarText.reports());
			reportsLink.setDescription(topBarText.reports_help());

			documentsearchButton.setCaption(topBarText.documentSearch());
			documentsearchButton.setDescription(topBarText.documentSearch_help());
			
			userButton.setCaption(topBarText.userSettings());
			userButton.setDescription(topBarText.userSettings_help());
			
			logoffButton.setCaption(topBarText.signOff());
			logoffButton.setDescription(topBarText.signOff_help());
			
		}	
		
		if (!SecurityGroups.canAccess(ProjexViewProvider.Views.CONFIGURATION)) {
			configurationButton.setVisible(false);
			configurationButton.setEnabled(false);
		}
		
		if (!SecurityGroups.canAccess(ProjexViewProvider.Views.DOCUMENTSEARCH)) {
			documentsearchButton.setVisible(false);
			documentsearchButton.setEnabled(false);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener
	 * .ViewChangeEvent)
	 */
	@Override
	public abstract void enter(ViewChangeEvent event);

	private void init() {

		topBarText = C10N.get(TopBarText.class, Locale.ENGLISH);

		homeButton = new TopbarButton(ProjexViewProvider.Views.HOME) {
			{
				addStyleName("logo");
				setIcon(new ThemeResource("images/projextblogo_topbar.png"));
				setDescription(topBarText.projectsHome());
			}
		};

		projectsButton = new TopbarButton(ProjexViewProvider.Views.PROJECTS, topBarText.projects()) {
			{
				addStyleName("icon-projects");
				setIcon(new ThemeResource("icons/special/project_icon_16x16.png"));
				setDescription(topBarText.projects_help());
			}
		};

		configurationButton = new TopbarButton(ProjexViewProvider.Views.CONFIGURATION, topBarText.configuration()) {
			{
				addStyleName("icon-configuration");
				setIcon(new ThemeResource("icons/chalkwork/basic/settings_16x16.png"));
				setDescription(topBarText.configuration_help());
			}
		};

		reportsLink = new TopBarLink(ProjexViewProvider.Views.REPORTS, topBarText.reports()) {
			{
				addStyleName("icon-reports");
				setIcon(new ThemeResource("icons/chalkwork/basic/report_16x16.png"));
				setDescription(topBarText.reports_help());
			}
		};

		documentsearchButton = new TopbarButton(ProjexViewProvider.Views.DOCUMENTSEARCH, topBarText.documentSearch()) {
			{
				addStyleName("icon-documentsearch");
				setIcon(new ThemeResource("icons/chalkwork/editing_controls/search_document_16x16.png"));
				setDescription(topBarText.documentSearch_help());
			}
		};


		userButton = new TopbarButton(ProjexViewProvider.Views.USERSETTINGS, topBarText.userSettings(), true) {
			{
				addStyleName("icon-users");
				setIcon(new ThemeResource("icons/general/small/User.png"));
				setDescription(topBarText.userSettings_help());
				addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						if (logger.isDebugEnabled()) {
							logger.debug("Switch to users clicked");
						}
						addStyleName("selected");
					}
				});
			}
		};

		logoffButton = new NativeButton(topBarText.signOff()) {
			{
				addStyleName("icon-logout");
				setIcon(new ThemeResource("icons/general/small/Sign_Out.png"));
				setDescription(topBarText.signOff_help());

				addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						
						if(logger.isDebugEnabled()) {
							logger.debug("Sign off clicked");
						}
						
						addStyleName("selected");
						SystemLocks.removeAllLocks(User.getUser().getUserId());
						User.setUser(null);
						Projex4UI.get().getPage().setLocation("");
						Projex4UI.get().getSession().close();
					}
				});
			}
		};

		/*
		 * helpButton = new NativeButton("help");
		 * helpButton.addStyleName("icon-help"); helpButton.setIcon(new
		 * ThemeResource("icons/general/small/Help.png"));
		 * helpButton.setDescription (
		 * "Everything you ever wanted to know about Projex.");
		 * helpButton.addClickListener(new Button.ClickListener() {
		 * 
		 * @Override public void buttonClick(ClickEvent event) {
		 * addStyleName("selected"); String fragment =
		 * Page.getCurrent().getUriFragment().substring(1);
		 * Projex4UI.get().getProjexViewNavigator
		 * ().navigateTo(ProjexViewProvider.Views.HELP, fragment); } });
		 * menu2.addComponent(helpButton);
		 */

		helpLink = new TopBarLink(ProjexViewProvider.Views.HELP, topBarText.help()) {
			{
				addStyleName("icon-help");
				setIcon(new ThemeResource("icons/general/small/Help.png"));
				setDescription("Everything you ever wanted to know about Projex.");
			}
		};

		notificationLayout = new VerticalLayout() {
			{
				setWidth("100%");
			}
		};

	}

	private void layout() {

		setSizeFull();
		addStyleName("bordered mainscreen");
		setMargin(false);

		// start of button bar
		addComponent(new HorizontalLayout() {
			{
				addStyleName("sidebar");
				setWidth("100%");

				CssLayout menu = new CssLayout() {
					{
						addStyleName("menu");
						setWidth("100%");
						addComponent(homeButton);
						addComponent(projectsButton);
						addComponent(configurationButton);
						addComponent(new HorizontalLayout() {
							{
								addStyleName("topbarlifter");
								addComponent(reportsLink);
							}
						});
						addComponent(documentsearchButton);

					}
				};
				addComponent(menu);
				setExpandRatio(menu, 1.0f);

				CssLayout menu2 = new CssLayout() {
					{
						addStyleName("menu");
						addComponent(userButton);
						addComponent(logoffButton);
						addComponent(new HorizontalLayout() {
							{
								addStyleName("topbarlifter");
								addComponent(helpLink);
							}
						});
					}
				};

				addComponent(menu2);
				setComponentAlignment(menu2, Alignment.TOP_RIGHT);
			}
		});
		addComponent(notificationLayout);
		
		checkForBulletins();

	}
	
	public void checkForBulletins() {
		
		String bulletinText = Bulletins.getCurrentBulletin();
		if(bulletinText!=null) {
			addBulletinNotification(bulletinText);
		}
		
	}
	
	public Component addBulletinNotification(String message) {

		final HorizontalLayout notification = new HorizontalLayout() {
			{
				setSpacing(true);
				setMargin(true);
				setStyleName("onscreenbulletin");
				setWidth("100%");

			}
		};
		
		Button closeButton = new Button() {
			{
				setIcon(new ThemeResource("icons/special/notification_close.png"));
				setDescription("Close");
				setStyleName("borderless");

				addClickListener(new ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						notification.setVisible(false);
					}

				});
			}
		};

		notification.addComponent(closeButton);

		Label messageLabel = new Label(message) {
			{
				setWidth("100%");
			}
		};
		notification.addComponent(messageLabel);
		notification.setExpandRatio(messageLabel, 1.0f);

		notificationLayout.addComponent(notification);

		return notification;

	}
	
	public Component addNotification(String message) {

		final HorizontalLayout notification = new HorizontalLayout() {
			{
				setSpacing(true);
				setMargin(true);
				setStyleName("onscreennotification");
				setWidth("100%");

			}
		};
		Button closeButton = new Button() {
			{
				setIcon(new ThemeResource("icons/special/notification_close.png"));
				setDescription("Close");
				setStyleName("borderless");

				addClickListener(new ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						notification.setVisible(false);
					}

				});
			}
		};

		notification.addComponent(closeButton);

		Label messageLabel = new Label(message) {
			{
				setWidth("100%");
			}
		};
		notification.addComponent(messageLabel);
		notification.setExpandRatio(messageLabel, 1.0f);

		notificationLayout.addComponent(notification);

		return notification;

	}

	public void removeNotification(Component notification) {
		notificationLayout.removeComponent(notification);
	}
	
	public void removeAllNotifications() {
		notificationLayout.removeAllComponents();
	}

	@Override
	public void resetScreen() {
		
		String fragment = getUI().getPage().getUriFragment();
		String parameter = fragment.substring(fragment.indexOf("/")+1);
		setScreenData(parameter);
		
		// String[] fragments = getUI().getPage().getUriFragment().split("/");
		// setScreenData(fragments[1]);

		// setScreenData(getUI().getPage().getUriFragment());

	}

	public void setReportsEnabled(boolean enabled) {
		reportsLink.setEnabled(enabled);
		reportsLink.setVisible(enabled);
	}

	public void setConfigurationEnabled(boolean enabled) {
		configurationButton.setEnabled(enabled);
		configurationButton.setVisible(enabled);
	}

	@Override
	public void saveScreen() {

	}

	@Override
	public void setScreenData(String o) {

	}

	public void showInvalidParametersView() {
		Projex4UI.get().getProjexViewNavigator().getDisplay().showView(new ErrorView());
	}

}
