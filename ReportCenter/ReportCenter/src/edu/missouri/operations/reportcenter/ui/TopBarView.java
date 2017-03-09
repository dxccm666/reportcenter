/**
 * 
 */
package edu.missouri.operations.reportcenter.ui;

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
import edu.missouri.cf.projex4.data.system.core.securitygroups.SecurityGroups;
import edu.missouri.cf.projex4.ui.c10n.TopBarText;
import edu.missouri.operations.data.User;
import edu.missouri.operations.reportcenter.ui.views.ErrorView;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public abstract class TopBarView extends VerticalLayout implements View {

	protected TopBarView view;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected Button homeButton;
	protected Button configurationButton;
	private Button logoffButton;

	protected TopBarText topBarText;

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

		/*

		if (!SecurityGroups.canAccess(ProjexViewProvider.Views.CONFIGURATION)) {
			configurationButton.setVisible(false);
			configurationButton.setEnabled(false);
		}
		
		*/

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
		
		/*
		configurationButton = new TopbarButton(ProjexViewProvider.Views.CONFIGURATION, topBarText.configuration()) {
			{
				addStyleName("icon-configuration");
				setIcon(new ThemeResource("icons/chalkwork/basic/settings_16x16.png"));
				setDescription(topBarText.configuration_help());
			}
		};
		*/

		logoffButton = new NativeButton(topBarText.signOff()) {
			{
				addStyleName("icon-logout");
				setIcon(new ThemeResource("icons/general/small/Sign_Out.png"));
				setDescription(topBarText.signOff_help());

				addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {

						if (logger.isDebugEnabled()) {
							logger.debug("Sign off clicked");
						}

						addStyleName("selected");
						User.setUser(null);
						Projex4UI.get().getPage().setLocation("");
						Projex4UI.get().getSession().close();
					}
				});
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
			//			addComponent(configurationButton);

					}
				};
				addComponent(menu);
				setExpandRatio(menu, 1.0f);

				CssLayout menu2 = new CssLayout() {
					{
						addStyleName("menu");
						addComponent(logoffButton);
					}
				};

				addComponent(menu2);
				setComponentAlignment(menu2, Alignment.TOP_RIGHT);
			}
		});

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

	public void resetScreen() {

		String fragment = getUI().getPage().getUriFragment();
		String parameter = fragment.substring(fragment.indexOf("/") + 1);
		setScreenData(parameter);

	}

	public void setConfigurationEnabled(boolean enabled) {
		configurationButton.setEnabled(enabled);
		configurationButton.setVisible(enabled);
	}

	public void saveScreen() {

	}

	public void setScreenData(String o) {

	}

}
