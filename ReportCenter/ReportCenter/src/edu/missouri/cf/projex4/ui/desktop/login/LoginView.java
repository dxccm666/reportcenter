package edu.missouri.cf.projex4.ui.desktop.login;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi;
import org.scribe.builder.api.LiveApi;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.oauthpopup.OAuthListener;
import org.vaadin.addon.oauthpopup.OAuthPopupButton;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.system.SystemLocks;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.LoginProvider;
import edu.missouri.cf.projex4.data.system.core.LoginProviders;
import edu.missouri.cf.projex4.data.system.properties.SystemProperties;
import edu.missouri.cf.projex4.system.Authenticator;
import edu.missouri.cf.projex4.ui.desktop.NavigatorButton;
import edu.missouri.cf.security.EnterpriseAuthenticator;
import edu.missouri.cf.security.UserLoginAuthenticator;

@SuppressWarnings("serial")
public class LoginView extends VerticalLayout implements View {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	final boolean bypasslogin = false;

	private TextField userName;
	private PasswordField password;
	private Button login;
	private Button createAccount;
	private Link mobileLogin;

	private Button resetPasswordButton;

	private String attemptedScreenAccess;

	private Image logo;

	private Label title;

	private NavigatorButton helpButton;

	private Label copyright;

	private Label errorMessage;

	// private Label service;

	Component googleButton;
	Component yahooButton;
	Component microsoftButton;

	boolean enableExternalProviders = false;

	private TextArea responseArea;

	public LoginView() {

		enableExternalProviders = "true".equals(SystemProperties.get("authproviders.outsideusers.external.enabled"));
		setSizeFull();
		init();
		layout();

	}

	public LoginView(String attemptedScreenAccess) {
		this.attemptedScreenAccess = attemptedScreenAccess;
		setSizeFull();
		init();
		layout();
	}

	class YahooButton extends OAuthPopupButton {
		public YahooButton(String key, String secret) {
			super(YahooApi.class, key, secret);
			setCaption("Yahoo!");
		}
	}

	class MicrosoftButton extends OAuthPopupButton {

		public MicrosoftButton(String key, String secret) {
			super(LiveApi.class, key, secret);
			setIcon(new ThemeResource("/icons/openid/medium/google.png"));
			setCaption("Microsoft");
		}
	}

	private void doLogin() {

		errorMessage.setCaption("");

		Authenticator authenticator;
		authenticator = new EnterpriseAuthenticator();

		// authenticator = new InternalAuthenticator();

		User.setUser(new User());

		if (authenticator.authenticate(userName.getValue(), password.getValue(), User.getUser())) {

			SystemLocks.removeAllLocks(User.getUser().getUserId());

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

	@SuppressWarnings("unused")
	private void doLoginExternal(String userLogin, String authproviderId) {

		errorMessage.setCaption("");

		Authenticator authenticator = new UserLoginAuthenticator(authproviderId);
		User.setUser(new User());

		if (authenticator.authenticate(userLogin, "", User.getUser())) {

			SystemLocks.removeAllLocks(User.getUser().getUserId());

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

	private Label getRotatedLabel() {

		title = new Label(getRotatedLabelTitle(), ContentMode.HTML);
		title.addStyleName("login-note");
		return title;

	}

	private String getRotatedLabelTitle() {

		java.util.Date d = new java.util.Date();
		long t = d.getTime();

		String title;

		switch ((int) t % 9) {

		case 0:
		case 1:
			title = "Teamwork is the ability to work as a group toward a common vision, even if that vision becomes extremely blurry";
			break;

		case 2:
			title = "The achievements of an organization are the results of the combined efforts of each individual.";
			break;

		case 3:
			title = "A job worth doing is worth doing together.";
			break;

		case 4:
			title = "Everything is awesome, Everything is cool when you're part of a team.";
			break;

		case 5:
			title = "We are all just cogs in the Great Machine.";
			break;

		case 6:
			title = "Individually we are one drop but together, we are an ocean.";
			break;

		case 7:
			title = "A boat doesn't go forward if each one is rowing their own way.";
			break;

		case 8:
			title = "Someone is sitting in the shade today because someone planted a tree a long time ago.";
			break;

		default:
			title = "One location for construction project development and completion";
			break;

		}

		return title;

	}

	private void init() {

		logo = new Image();
		logo.setIcon(new ThemeResource("images/projextblogo.png"));
		// logo.setIcon(new ThemeResource("images/projex4shared.png"));
		logo.addStyleName("login");

		errorMessage = new Label("", ContentMode.HTML) {
			{
				addStyleName("login-error");
			}
		};

		title = getRotatedLabel();

		userName = new TextField() {
			{
				setImmediate(true);
				addStyleName("login-user");
				setCaption("User Login");

				addBlurListener(new FieldEvents.BlurListener() {

					@Override
					public void blur(BlurEvent event) {

						// doLogin();
						password.focus();

					}
				});
			}
		};

		password = new PasswordField() {
			{
				setImmediate(true);
				setCaption("Password");
				addStyleName("login-password");
			}
		};

		login = new Button() {
			{
				setCaption("sign in");
				setImmediate(true);
				addStyleName("whitetext");
				setClickShortcut(KeyCode.ENTER);

				addClickListener(new ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						doLogin();
					}

				});
			}
		};

		responseArea = new TextArea("Response");

		if (enableExternalProviders) {

			final LoginProvider googleProvider = LoginProviders.getLoginProviderByName("Google");
			if (googleProvider != null && googleProvider.getAuthKey() != null) {

				googleButton = new OAuthPopupButton(GoogleApi.class, googleProvider.getAuthKey(), googleProvider.getAuthSecret()) {
					{
						setIcon(new ThemeResource("icons/openid/small/google.png"));
						setCaption("Google");
						getButton().addStyleName("borderless whitetext");
						setScope("profile");
						addOAuthListener(new OAuthListener() {

							@Override
							public void authSuccessful(String accessToken, String accessTokenSecret, String oauthRawResponse) {

								if (logger.isDebugEnabled()) {
									logger.debug("Authentication successful");
								}

								errorMessage.setCaption("Google Authentication successful");

								// TODO NEED TO FIGURE OUT HOW TO GET THE USER
								// NAME
								// USING THE OAUTH ACCESSTOKEN.

								// doLoginExternal("", googleProvider.id);

							}

							@Override
							public void authDenied(String reason) {

								if (logger.isDebugEnabled()) {
									logger.debug("Google Authentication not successful.");
								}
								User.setUser(null);
								errorMessage.setCaption("   Google Authentication Failed");

							}

						});
					}
				};

			} else {

				googleButton = new Button("Google") {
					{
						setIcon(new ThemeResource("icons/openid/small/google.png"));
						addStyleName("borderless whitetext");
					}
				};

			}

			final LoginProvider yahooProvider = LoginProviders.getLoginProviderByName("Yahoo");
			if (yahooProvider != null && yahooProvider.getAuthKey() != null) {

				yahooButton = new OAuthPopupButton(YahooApi.class, yahooProvider.getAuthKey(), yahooProvider.getAuthSecret()) {
					{
						setIcon(new ThemeResource("icons/openid/small/yahoo.png"));
						setCaption("Yahoo!");
						getButton().addStyleName("borderless whitetext");
						addOAuthListener(new OAuthListener() {

							@Override
							public void authSuccessful(String accessToken, String accessTokenSecret, String oauthRawResponse) {
								if (logger.isDebugEnabled()) {
									logger.debug("Authentication successful");
								}

								String resource = "http://social.yahooapis.com/v1/me/guid?format=json";
								OAuthRequest request = new OAuthRequest(Verb.GET, resource);

								ServiceBuilder sb = new ServiceBuilder();
								sb.provider(YahooApi.class);
								sb.apiKey(yahooProvider.getAuthKey());
								sb.apiSecret(yahooProvider.getAuthSecret());
								sb.callback("https://devapp1.cf.missouri.edu/Projex4_7on7");

								OAuthService service = sb.build();
								service.signRequest(new Token(accessToken, accessTokenSecret), request);
								Response resp = request.send();

								String bodyText = resp.getBody();
								responseArea.setVisible(true);
								responseArea.setValue(bodyText);

								// TODO NEED TO FIGURE OUT HOW TO GET THE USER
								// NAME
								// USING THE OAUTH ACCESSTOKEN.

								// For now set it to me to see if we can shift
								// screens properly.
								// doLoginExternal("GRAUMANNC",
								// yahooProvider.id);
							}

							@Override
							public void authDenied(String reason) {
								// TODO Auto-generated method stub
								if (logger.isDebugEnabled()) {
									logger.debug("Authentication unsuccessful");
								}
								errorMessage.setCaption("Yahoo authentication failed.");
								User.setUser(null);
							}

						});
					}
				};

			} else {

				yahooButton = new Button("Yahoo!") {
					{
						setIcon(new ThemeResource("icons/openid/small/yahoo.png"));
						addStyleName("borderless whitetext");
					}
				};

			}

			final LoginProvider microsoftProvider = LoginProviders.getLoginProviderByName("Microsoft");
			if (microsoftProvider != null && microsoftProvider.getAuthKey() != null) {

				microsoftButton = new OAuthPopupButton(LiveApi.class, microsoftProvider.getAuthKey(), microsoftProvider.getAuthSecret()) {
					{
						setIcon(new ThemeResource("icons/openid/small/microsoft.png"));
						setCaption("Microsoft");
						getButton().addStyleName("borderless whitetext");
						addOAuthListener(new OAuthListener() {

							@Override
							public void authSuccessful(String accessToken, String accessTokenSecret, String oauthRawResponse) {
								if (logger.isDebugEnabled()) {
									logger.debug("Authentication successful");
								}
								errorMessage.setCaption("Microsoft Authentication successful");
								// TODO NEED TO FIGURE OUT HOW TO GET THE USER
								// NAME
								// USING THE OAUTH ACCESSTOKEN.
								// doLoginExternal("", microsoftProvider.id);
							}

							@Override
							public void authDenied(String reason) {
								if (logger.isDebugEnabled()) {
									logger.debug("Authentication successful");
								}
								errorMessage.setCaption("Microsoft Authentication failed.");

							}
						});
					}
				};

			} else {

				microsoftButton = new Button("Microsoft") {
					{
						setIcon(new ThemeResource("icons/openid/small/microsoft.png"));
						addStyleName("borderless whitetext");
					}
				};

			}

		}

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

		resetPasswordButton = new Button("reset password") {
			{
				addStyleName("borderless login-newuser");
				setIcon(new ThemeResource("icons/chalkwork/basic/lock_open_16x16.png"));
				addClickListener(new Button.ClickListener() {

					@SuppressWarnings("unused")
					@Override
					public void buttonClick(ClickEvent event) {
						Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.FORGETPASSWORD);
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

				addClickListener(new Button.ClickListener() {

					@SuppressWarnings("deprecation")
					@Override
					public void buttonClick(ClickEvent event) {

						logger.debug("Help Button Clicked");

						// Shouldn't have had to hard code the url. Somehting is
						// wrong with the navigator's url.
						// Page.getCurrent().open(new
						// ExternalResource(Projex4UI.get().getProjexViewNavigator().getUrl(getView(),
						// null)), "_blank", false);

						Page.getCurrent().open(new ExternalResource("https://projex4.cf.missouri.edu/projex/#!loginhelp"), "_blank", false);

					}
				});
			}
		};

		WebBrowser wb = UI.getCurrent().getPage().getWebBrowser();

		String popupMessage;

		if (wb.isIE() || wb.isFirefox()) {
			popupMessage = "<span style=\"color: red; font-weight: bold; font-size: 1.2em\">You will need to allow popups for this site to use Projex.<br/>"
					+ "For best results, your browser should be set to open popups as new tabs.</span><br/>";
		} else {
			popupMessage = "<span style=\"color: red; font-weight: bold; font-size: 1.2em\">You will need to allow popups for this site to use Projex.</span><br/>";
		}

		copyright = new Label(popupMessage
				+ "<br/>&copy; 2016 &mdash; Curators of the <a href=\"http://www.umsystem.edu\">University of Missouri</a>. All rights reserved.<br/><a href=\"http://missouri.edu/eeo-aa/\">An equal opportunity/access/affirmative action/pro-disabled and veteran employer</a>.<br/>"
				+ "Service provided by MU Campus Facilities.<br/>By logging into this site, you agree that you have read and<br/>understood our <a href=\"/projex/#!privacypolicy\">privacy policy</a> and that you will abide by our <a href=\"/projex/#!termsandconditions\">terms and conditions</a>.",
				ContentMode.HTML) {
			{
				addStyleName("eeo-aa");

			}
		};

		/*
		 * service = new Label( ContentMode.HTML); { { addStyleName("eeo-aa"); }
		 * } ;
		 */

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
				addComponent(errorMessage);
				addComponent(new HorizontalLayout() {
					{
						setWidth("100%");
						setSpacing(true);
						VerticalLayout umlogin = new VerticalLayout() {
							{
								setSpacing(true);
								setMargin(true);
								// setCaption("University of Missouri Users");
								setStyleName("whitetext");
								addComponent(userName);
								addComponent(password);
								addComponent(login);
							}
						};
						addComponent(umlogin);
						setExpandRatio(umlogin, 0.6f);

						if (enableExternalProviders) {
							VerticalLayout externallogin = new VerticalLayout() {
								{
									setSpacing(true);
									setMargin(true);
									addStyleName("whitetext");
									setCaption("External Users");
									addComponent(googleButton);
									addComponent(yahooButton);
									addComponent(microsoftButton);
								}
							};
							addComponent(externallogin);
							setExpandRatio(externallogin, 0.4f);
						}
					}
				});

				addComponent(new HorizontalLayout() {
					{
						addStyleName("login-buttons");
						setSpacing(true);
						addComponent(createAccount);
						setExpandRatio(createAccount, 1.0f);
						addComponent(resetPasswordButton);
						addComponent(mobileLogin);
						addComponent(helpButton);
					}
				});
			}
		};
		wrapper.addComponent(loginBox);
		wrapper.addComponent(copyright);
		// wrapper.addComponent(service);

		responseArea.setWidth("100%");
		responseArea.setHeight("300px");

		HorizontalLayout hzl = new HorizontalLayout() {
			{
				setWidth("100%");
			}
		};

		hzl.addComponent(wrapper);
		hzl.addComponent(responseArea);

		addComponent(hzl);

		responseArea.setVisible(false);
		setSizeFull();
		setDefaultComponentAlignment(Alignment.TOP_CENTER);

		/*
		 * VerticalLayout spacer = new VerticalLayout(); addComponent(spacer);
		 * setExpandRatio(spacer, 1.0f);
		 */

	}

	/* Need to add open Auth Listener */

	/*
	 * class ExternalLoginListener implements OpenIdLoginListener {
	 * 
	 * public void onLogin(String id,
	 * Map<org.vaadin.openid.OpenIdHandler.UserAttribute, String> openidInfo) {
	 * 
	 * LoginProvider p = (LoginProvider) loginproviderComboBox.getValue(); User
	 * user = User.getUser(); user.setValuesFromOpenId(id, p.getName(),
	 * openidInfo); // implement navigation }
	 * 
	 * public void onCancel() { // openIdHandler.close(); // need to display
	 * error screen. } }
	 */

	@Override
	public void enter(ViewChangeEvent event) {

		title.setCaption(getRotatedLabelTitle());

	}

}
