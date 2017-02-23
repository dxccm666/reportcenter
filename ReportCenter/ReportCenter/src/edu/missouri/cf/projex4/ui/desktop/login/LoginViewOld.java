package edu.missouri.cf.projex4.ui.desktop.login;

import java.util.Map;

import org.scribe.builder.api.GoogleApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.oauthpopup.OAuthPopupButton;
import org.vaadin.openid.OpenIdHandler.OpenIdLoginListener;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.LoginProvider;
import edu.missouri.cf.projex4.data.system.core.LoginProviders;
import edu.missouri.cf.projex4.system.Authenticator;
import edu.missouri.cf.projex4.system.DummyAuthenticator;
import edu.missouri.cf.projex4.system.InternalAuthenticator;
import edu.missouri.cf.projex4.system.MicrosoftAuthenticator;
import edu.missouri.cf.projex4.ui.desktop.NavigatorButton;
import edu.missouri.cf.security.EnterpriseAuthenticator;

@SuppressWarnings("serial")
public class LoginViewOld extends VerticalLayout implements View {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	final boolean bypasslogin = false;

	private ComboBox loginproviderComboBox;
	private TextField userName;
	private PasswordField password;
	private Button login;
	private Link loginLink;
	private Button createAccount;
	private Link mobileLogin;

	public enum ScreenState {
		RESET, PROVIDERCHOSEN
	}

	private ScreenState currentState;

	private String attemptedScreenAccess;

	private Image logo;

	private Label title;

	private Button helpButton;

	private Label copyright;

	private Label errorMessage;

	private LoginProvider selectedProvider = null;

	private void setScreenState(ScreenState state) {

		currentState = state;

		if (logger.isDebugEnabled()) {
			logger.debug("LoginView: setting screen state to " + state);
		}

		userName.setValue("");
		password.setValue("");
		userName.setVisible(false);
		password.setVisible(false);

		switch (state) {

		case PROVIDERCHOSEN:

			login.setEnabled(false);
			loginLink.setEnabled(false);

			login.setVisible(false);
			loginLink.setVisible(false);

			/*
			 * if (bypasslogin) { login.setVisible(true); }
			 */

			switch (selectedProvider.getType()) {

			case ENTERPRISE:
				userName.setVisible(true);
				userName.setCaption("User name");
				password.setVisible(true);
				password.setCaption("Password");
				login.setIcon(selectedProvider.getIcon());
				login.setVisible(true);
				break;

			case INTERNAL:
				userName.setVisible(true);
				userName.setCaption("Email address");
				password.setVisible(true);
				password.setCaption("Password");
				login.setIcon(selectedProvider.getIcon());
				login.setVisible(true);
				break;

			case OPENID:

				userName.setVisible(selectedProvider.isNameRequired());
				password.setVisible(false);

				if (selectedProvider.isNameRequired()) {
					if ("OpenId".equals(selectedProvider.getName())) {
						userName.setCaption("OpenId URL");
					} else {
						userName.setCaption("User Id");
					}
					loginLink.setEnabled(false);
					loginLink.setVisible(true);

				} else {
					loginLink.setVisible(true);
					loginLink.setResource(new ExternalResource(selectedProvider.getUrl(userName.getValue())));
					loginLink.setEnabled(true);
				}
				loginLink.setIcon(selectedProvider.getIcon());
				break;

			case MICROSOFT:
				userName.setVisible(true);
				userName.setCaption("Microsoft.com Account");
				password.setVisible(true);
				password.setCaption("Password");
				login.setIcon(selectedProvider.getIcon());
				login.setVisible(true);
				break;

			case OPENAUTH:
				userName.setVisible(false);
				password.setVisible(false);
				loginLink.setVisible(true);
				loginLink.setResource(new ExternalResource(selectedProvider.getUrl(userName.getValue())));
				loginLink.setEnabled(true);
				loginLink.setIcon(selectedProvider.getIcon());
				break;

			default:
				break;

			}
			break;

		case RESET:

			loginLink.setVisible(false);
			loginproviderComboBox.select(null);
			loginproviderComboBox.focus();
			selectedProvider = null;

			if (bypasslogin) {
				login.setVisible(true);
				login.setEnabled(true);
			} else {
				login.setVisible(false);
			}

			break;

		}

	}

	public LoginViewOld() {
		setSizeFull();
		init();
		layout();
		setScreenState(ScreenState.RESET);

	}

	public LoginViewOld(String attemptedScreenAccess) {
		this.attemptedScreenAccess = attemptedScreenAccess;
		setSizeFull();
		init();
		layout();
		setScreenState(ScreenState.RESET);
	}

	private void checkValidity() {

		switch (currentState) {

		case PROVIDERCHOSEN:

			switch (selectedProvider.getType()) {

			case ENTERPRISE:
			case INTERNAL:
			case MICROSOFT:
				if (userName.getValue() != null && password.getValue() != null && userName.getValue().length() > 0
						&& password.getValue().length() > 0) {
					login.setEnabled(true);
				} else {
					login.setEnabled(false);
				}
				break;

			case OAUTH:
			case OPENAUTH:
				
				if("Google".equals(selectedProvider.name)) {
					
					
					
				}
				
				break;

			case OPENID:
				if (selectedProvider.isNameRequired()) {
					if (userName.getValue() != null && userName.getValue().length() > 0) {
						loginLink.setEnabled(true);
						LoginProvider provider = (LoginProvider) loginproviderComboBox.getValue();
						loginLink.setResource(new ExternalResource(provider.getUrl(userName.getValue())));
					} else {
						loginLink.setEnabled(false);
					}
				} else {
					loginLink.setEnabled(false);
				}
				break;

			}

			break;

		case RESET:
			break;
		}

	}
	
	class GoogleButton extends OAuthPopupButton {
		
		public GoogleButton(String key, String secret) {
			super(GoogleApi.class, key, secret);
			setIcon(new ThemeResource("/icons/openid/small/google.png"));
			setCaption("Google");
		}
		
	}

	private void doLogin() {

		errorMessage.setCaption("");

		Authenticator authenticator;
		if (bypasslogin) {

			authenticator = new DummyAuthenticator();

		} else {

			checkValidity();

			if (!login.isEnabled()) {
				return;
			}

			switch (selectedProvider.getType()) {
			case ENTERPRISE:
				authenticator = new EnterpriseAuthenticator();
				break;
			case INTERNAL:
				authenticator = new InternalAuthenticator();
				break;
			case MICROSOFT:
				authenticator = new MicrosoftAuthenticator();
				break;
			default:
				return;
			}
		}

		User.setUser(new User());
		User.getUser().put(User.UserAttribute.AUTHPROVIDERID, selectedProvider.id);

		if (authenticator.authenticate(userName.getValue(), password.getValue(), User.getUser())) {
			if (attemptedScreenAccess != null) {
				Projex4UI.get().getProjexViewNavigator().navigateTo(attemptedScreenAccess);
			} else {
				Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.HOME);
			}
			User.getUser().registerLogin();

		} else {
			User.setUser(null);
			errorMessage.setCaption("   Invalid User Credentials.");
			logger.debug("Authentication failed for {}", userName.getValue());
		}
	}

	private void init() {

		logo = new Image();
		logo.setIcon(new ThemeResource("images/projextblogo.png"));
		logo.addStyleName("login");

		errorMessage = new Label("", ContentMode.HTML) {
			{
				addStyleName("login-error");
			}
		};

		java.util.Date d = new java.util.Date();
		long t = d.getTime();
		
		switch((int) t%6) {
		
		case 0 :
			
		case 1 :
			title = new Label("Teamwork is the ability to work as a group toward a common vision, even if that vision becomes extremely blurry", ContentMode.HTML);
			break;
			
		case 2 :
			title = new Label("The achievements of an organization are the results of the combined efforts of each individual.", ContentMode.HTML);
			break;
			
		case 3 :
			title = new Label("A job worth doing is worth doing together.", ContentMode.HTML);
			break;
			
		case 4 :
			title = new Label("Everything is awesome, Everything is cool when you're part of a team.", ContentMode.HTML);
			break;
			
		case 5 :
			title = new Label("We are all just cogs in the Great Machine.", ContentMode.HTML);
			break;
			
		default :
			title = new Label("One location for construction project development and completion", ContentMode.HTML);
			break;
			
		}
		
		title.addStyleName("login-note");

		loginproviderComboBox = new ComboBox() {
			{
				setCaption("Sign In Using");
				setContainerDataSource(LoginProviders.getBeanItemContainer(LoginProviders.Type.ALL));
				setImmediate(true);
				addStyleName("login-provider");

			}
		};

		loginproviderComboBox.addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {

				selectedProvider = (LoginProvider) loginproviderComboBox.getValue();
				if (selectedProvider != null) {
					setScreenState(ScreenState.PROVIDERCHOSEN);
				}

			}
		});

		userName = new TextField() {
			{
				setImmediate(true);
				addStyleName("login-user");
			}
		};

		userName.addBlurListener(new FieldEvents.BlurListener() {

			@Override
			public void blur(BlurEvent event) {

				switch (selectedProvider.getType()) {
				case ENTERPRISE:
				case INTERNAL:
				case MICROSOFT:
					doLogin();
					password.focus();
					break;
				case OPENID:
					if (selectedProvider.isNameRequired()) {
						checkValidity();
					}
					break;
				default:
					break;
				}

			}
		});

		password = new PasswordField() {
			{
				setImmediate(true);
				addStyleName("login-password");
			}
		};

		password.addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				doLogin();
			}
		});

		loginLink = new Link() {
			{
				setCaption("sign in");
				addStyleName("borderless login-openid");
			}
		};

		login = new Button() {
			{
				setCaption("sign in");
				setImmediate(true);
				addStyleName("borderless");
			}
		};
		
		login.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				doLogin();
			}

		});

		createAccount = new Button() {
			{
				setCaption("create account");
				setImmediate(true);
				addStyleName("borderless login-newuser");
				setIcon(new ThemeResource("icons/chalkwork/basic/user_add_16x16.png"));

				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.SELECTEMPLOYEETYPE);
					}
				});

			}
		};

		mobileLogin = new Link() {
			{
				setCaption("mobile site");
				addStyleName("borderless login-mobile");
				setIcon(new ThemeResource("icons/special/tablet_16x16.png"));
				setEnabled(false);
			}
		};

		helpButton = new NavigatorButton() {
			{
				setCaption("help");
				setImmediate(true);
				addStyleName("borderless login-help");
				setIcon(new ThemeResource("icons/chalkwork/basic/help_16x16.png"));
				setView(ProjexViewProvider.Views.LOGINHELP);
			}
		};

		copyright = new Label(
				"&copy; 2015 &mdash; Curators of the <a href=\"http://www.umsystem.edu\">University of Missouri</a>. All rights reserved.<br/><a href=\"http://missouri.edu/eeo-aa/\">An equal opportunity/access/affirmative action/pro-disabled and veteran employer</a>.",
				ContentMode.HTML);
		copyright.addStyleName("eeo-aa");

	}

	protected void DoCreateAccount() {

		Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.HOME);

	}

	private void layout() {

		VerticalLayout wrapper = new VerticalLayout();

		VerticalLayout loginBox = new VerticalLayout() {
			{
				setSpacing(true);
				setMargin(true);
				addStyleName("login-box");
				addComponent(logo);
				addComponent(title);
				addComponent(loginproviderComboBox);
				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);
						addComponent(new VerticalLayout() {
							{
								addComponent(userName);
								addComponent(password);
							}
						});
						addComponent(new VerticalLayout() {
							{
								setSizeFull();
								addComponent(errorMessage);
							}
						});
					}
				});

				addComponent(new HorizontalLayout() {
					{
						addStyleName("login-buttons");
						setSpacing(true);
						HorizontalLayout hzlLogins = new HorizontalLayout() {
							{
								addComponent(loginLink);
								addComponent(login);
							}
						};
						addComponent(hzlLogins);
						setExpandRatio(hzlLogins, 1.0f);
						addComponent(createAccount);
						addComponent(mobileLogin);
						addComponent(helpButton);
					}
				});
			}
		};

		wrapper.addComponent(loginBox);
		wrapper.addComponent(copyright);

		addComponent(wrapper);
		setSizeFull();
		setDefaultComponentAlignment(Alignment.TOP_CENTER);

		/*
		 * VerticalLayout spacer = new VerticalLayout(); addComponent(spacer);
		 * setExpandRatio(spacer, 1.0f);
		 */

		// Service provided by MU Campus Facilities. By logging into this site,
		// you agree that you have read and understood our privacy policy and
		// that you will abide by our terms and conditions.",

	}

	/* Need to add open Auth Listener */

	class ExternalLoginListener implements OpenIdLoginListener {

		public void onLogin(String id, Map<org.vaadin.openid.OpenIdHandler.UserAttribute, String> openidInfo) {
			LoginProvider p = (LoginProvider) loginproviderComboBox.getValue();

			User user = User.getUser();
			user.setValuesFromOpenId(id, p.getName(), openidInfo);
			// openIdHandler.close();

			// implement navigation
		}

		public void onCancel() {
			// openIdHandler.close();
			// need to display error screen.
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		setScreenState(ScreenState.RESET);
	}

}
