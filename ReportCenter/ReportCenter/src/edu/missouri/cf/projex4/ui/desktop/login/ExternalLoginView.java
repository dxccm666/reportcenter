package edu.missouri.cf.projex4.ui.desktop.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.scribe.builder.api.GoogleApi;
import org.scribe.builder.api.LiveApi;
import org.scribe.builder.api.YahooApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.oauthpopup.OAuthPopupButton;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.Pools;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.LoginProvider;
import edu.missouri.cf.projex4.data.system.core.LoginProviders;
import edu.missouri.cf.projex4.data.system.properties.SystemProperties;
import edu.missouri.cf.projex4.ui.desktop.LoginTopBarView;
import edu.missouri.cf.security.EnterpriseAuthenticator;

@SuppressWarnings("serial")
public class ExternalLoginView extends LoginTopBarView {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	final boolean bypasslogin = false;

	private TextField enterpriseUserName;
	private PasswordField enterprisePassword;
	private Button enterpriseLogin;
	private Label enterpriseErrorMessage;

	private PasswordField localPassword;
	private PasswordField repeatLocalPassword;
	private Button createButton;
	private TextField emailLabel;

	boolean enableEnterpriseProvidersForOutside = true;
	boolean enableExternalProviders = false;

	Component googleButton;
	Component yahooButton;
	Component microsoftButton;

	private Label instructions1;
	private Label instructions2;
	private String id;
	private Label overAllInstructions;
	private Label title1;
	private Label title2;

	private Label instructions3;
	private Label title3;

	private Label localPasswordResults;

	private String invitationEmail;

	public ExternalLoginView() {

		final boolean testing = false;

		if (testing) {
			enableEnterpriseProvidersForOutside = true;
			enableExternalProviders = true;
		} else {
			enableEnterpriseProvidersForOutside = "true"
					.equals(SystemProperties.get("authproviders.outsideusers.enterprise.enabled"));
			enableExternalProviders = "true".equals(SystemProperties.get("authproviders.outsideusers.external.enabled"));
		}

		setSizeFull();
		init();
		layout();

	}

	private void updateUser(final String id, final String verifier, final String userName, final String password) {

		/*
		 * Runnable runnable = new Runnable() {
		 * 
		 * @Override public void run() {
		 */

		Connection conn = null;
		try {

			conn = Pools.getConnection(Pools.Names.PROJEX);

			// String sql = "update users set initialized = 1, verifier = ?,
			// userlogin = ?, password = ? where personid = ?";

			String sql = "insert into temppassword (verifier, userlogin, password, personid) values (?,?,?,?)";

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {

				String encryptedPassword = encryptPassword(password);

				if (logger.isDebugEnabled()) {
					logger.debug("Screen encrypted password = {}", encryptedPassword);
				}

				stmt.setString(1, verifier);
				stmt.setString(2, userName);
				stmt.setString(3, encryptedPassword);
				stmt.setString(4, id);
				stmt.executeUpdate();
				conn.commit();
			}

		} catch (SQLException sqle) {
			if (logger.isErrorEnabled()) {
				logger.error("Unable to update user", sqle);
			}
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}
	}

	/*
	 * }; new Thread(runnable).start();
	 * 
	 * }
	 */

	private void doEnterpriseLogin() {

		enterpriseErrorMessage.setCaption("");

		EnterpriseAuthenticator authenticator;
		authenticator = new EnterpriseAuthenticator();
		User.setUser(new User());

		if (authenticator.newUserAuthenticate(enterpriseUserName.getValue(), enterprisePassword.getValue(), User.getUser())) {

			System.out.println("getpersonid = " + id);

			updateUser(id, "ENTERPRISE", enterpriseUserName.getValue(), enterprisePassword.getValue());
			User.getUser().registerLogin();

			Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.EXTERNALDETAILSVIEW, id);

		} else {

			User.setUser(null);
			enterpriseErrorMessage.setCaption("Invalid User Credentials.");

			if (logger.isDebugEnabled()) {
				logger.debug("Authentication failed for {}", enterpriseUserName.getValue());
			}
		}
	}

	private void doLocalLogin() {

		if (isLocalPasswordValid()) {
			
			updateUser(id, "INTERNAL", invitationEmail, localPassword.getValue());
			Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.EXTERNALDETAILSVIEW, id);

		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Invalid password for local user");
			}
		}

	}

	private String encryptPassword(String password) {

		StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();
		String encryptedPassword = encryptor.encryptPassword(password);

		if (logger.isDebugEnabled()) {
			logger.debug("encrypted password is {} {}", encryptedPassword.length(), encryptedPassword);
		}

		return encryptedPassword;

	}

	private void init() {

		overAllInstructions = new Label(
				"<h1>Password Verification Choice</h1>"
						+ "<p>You need to choose a method for verifying your password.  Your identity and password should not be shared with anyone else.  "
						+ "If you have coworkers who need access to the system, please inform your Owner's Representative to get them added.",
				ContentMode.HTML);

		title1 = new Label("<h2>Local Password Verification</h2>", ContentMode.HTML);

		instructions1 = new Label(
				"<p>You may create a unique password to access our system by entering a password to the right. "
						+ "If you choose to use this option, you will have to use your invitaton email address and your chosen password to log into the system.</p>"
						+ "<p><b>Your password must contain one upper case unaccented letter (A-Z), one lower case unaccented letter (a-z), and one number (0-9). It must be between 8 and 15 characters in length.</b></p>",
				ContentMode.HTML);

		title2 = new Label("<h2>University of Missouri Single Sign On</h2>", ContentMode.HTML);

		instructions2 = new Label(
				"<p>If you have been issued a SSO (Single Sign On) ID from the University of Missouri, you may choose to use that ID and Password by entering them to the right.</p>",
				ContentMode.HTML);

		title3 = new Label("<h2>Third-Party Password Verifier Selection</h2>", ContentMode.HTML);

		instructions3 = new Label(
				"<p>You may choose to use a third party provider to verify your username and password. "
						+ "Providers that are currently authorized are shown below. "
						+ "To authenticate using one of them, click on the providers name or icon. A new "
						+ "page will open and you will be asked to log in using your account with that provider. "
						+ "You will need to grant access to your profile information for MU Projex. "
						+ "Once your account has been verified, you will need to use the same provider each time to access this system.</p> ",
				ContentMode.HTML);

		enterpriseErrorMessage = new Label("", ContentMode.HTML);

		emailLabel = new TextField() {
			{
				setCaption("Invitation Email");
				setWidth("200px");
			}
		};

		enterpriseUserName = new TextField() {
			{
				setImmediate(true);
				addStyleName("login-user");
				setCaption("University of Missouri SSO ID");

				addBlurListener(new FieldEvents.BlurListener() {

					@Override
					public void blur(BlurEvent event) {

						// doLogin();
						enterprisePassword.focus();

					}
				});
			}
		};

		enterprisePassword = new PasswordField() {
			{
				setImmediate(true);
				setCaption("Password");
				addStyleName("login-password");

				addValueChangeListener(new Property.ValueChangeListener() {

					@Override
					public void valueChange(Property.ValueChangeEvent event) {
						doEnterpriseLogin();
					}
				});
			}
		};
		
		localPasswordResults = new Label();

		localPassword = new PasswordField() {
			{
				setImmediate(true);
				setCaption("Password");
				addStyleName("login-password");
				addValidator(new Validator() {

					@Override
					public void validate(Object value) throws InvalidValueException {
						
						if(isLocalPasswordRuleCompliant()) {
							localPasswordResults.setVisible(false);
						} else {
							localPasswordResults.setVisible(true);
							localPasswordResults.setValue("Password does not match rules");
							throw new InvalidValueException("Password does not match rules.");
						}
						
					}});

			}
		};

		repeatLocalPassword = new PasswordField() {
			{
				setImmediate(true);
				setCaption("Repeat Password");
				addStyleName("login-password");
				
				addValidator(new Validator() {

					@Override
					public void validate(Object value) throws InvalidValueException {
						
						if(isLocalPasswordMatched()) {
							localPasswordResults.setVisible(false);
						} else {
							localPasswordResults.setVisible(true);
							localPasswordResults.setValue("Passwords are not the same.");
							throw new InvalidValueException("Passwords are not the same.");
						}
						
					}});


			}
		};

		enterpriseLogin = new Button() {
			{
				setCaption("sign in");
				setImmediate(true);
				setIcon(new ThemeResource("icons/chalkwork/basic/lock_open_16x16.png"));
				addStyleName("borderless");

				addClickListener(new ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						doEnterpriseLogin();
					}

				});
			}
		};

		createButton = new Button() {
			{
				setCaption("Create Local Account");
				setImmediate(true);
				setIcon(new ThemeResource("icons/chalkwork/basic/user_add_16x16.png"));
				addStyleName("borderless");

				addClickListener(new ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						doLocalLogin();
					}

				});
			}
		};

		LoginProvider googleProvider = LoginProviders.getLoginProviderByName("Google");
		if (googleProvider != null && googleProvider.getAuthKey() != null) {

			googleButton = new OAuthPopupButton(GoogleApi.class, googleProvider.getAuthKey(), googleProvider.getAuthSecret()) {
				{
					setIcon(new ThemeResource("icons/openid/medium/google.png"));
					setCaption("Google");
					getButton().addStyleName("borderless");
				}
			};

		} else {

			googleButton = new Button("Google") {
				{
					setIcon(new ThemeResource("icons/openid/medium/google.png"));
					addStyleName("borderless");
				}
			};

		}

		LoginProvider yahooProvider = LoginProviders.getLoginProviderByName("Yahoo");
		if (yahooProvider != null && yahooProvider.getAuthKey() != null) {

			yahooButton = new OAuthPopupButton(YahooApi.class, yahooProvider.getAuthKey(), yahooProvider.getAuthSecret()) {
				{
					setIcon(new ThemeResource("icons/openid/medium/yahoo.png"));
					setCaption("Yahoo!");
					getButton().addStyleName("borderless");
				}
			};

		} else {

			yahooButton = new Button("Yahoo!") {
				{
					setIcon(new ThemeResource("icons/openid/medium/yahoo.png"));
					addStyleName("borderless");
				}
			};

		}

		LoginProvider microsoftProvider = LoginProviders.getLoginProviderByName("Microsoft");
		if (microsoftProvider != null && microsoftProvider.getAuthKey() != null) {

			microsoftButton = new OAuthPopupButton(LiveApi.class, microsoftProvider.getAuthKey(),
					microsoftProvider.getAuthSecret()) {
				{
					setIcon(new ThemeResource("icons/openid/medium/microsoft.png"));
					setCaption("Microsoft");
					getButton().addStyleName("borderless");
				}
			};

		} else {

			microsoftButton = new Button("Microsoft") {
				{
					setIcon(new ThemeResource("icons/openid/small/microsoft.png"));
					addStyleName("borderless");
				}
			};

		}

	}

	protected void DoCreateAccount() {

		Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.HOME);

	}
	
	private boolean isLocalPasswordValid() {
		
		return isLocalPasswordMatched() && isLocalPasswordRuleCompliant();
		
	}

	private boolean isLocalPasswordMatched() {

		if (!repeatLocalPassword.getValue().equals(localPassword.getValue())) {
			if (logger.isDebugEnabled()) {
				logger.debug("Password fields don't match");
			}
			return false;
		}
		
		return true;

	}

	private boolean isLocalPasswordRuleCompliant() {

		// Switched from using regexp to using Passay library.
		PasswordValidator validator = new PasswordValidator(Arrays.asList(new LengthRule(8, 15),
				new CharacterRule(EnglishCharacterData.UpperCase, 1), new CharacterRule(EnglishCharacterData.LowerCase, 1),
				new CharacterRule(EnglishCharacterData.Digit, 1), new WhitespaceRule()));

		RuleResult result = validator.validate(new PasswordData(localPassword.getValue()));
		
		return result.isValid();

	}

	private void layout() {

		VerticalLayout wrapper = new VerticalLayout();
		VerticalLayout loginBox = new VerticalLayout() {
			{
				setSpacing(true);
				setMargin(true);
				overAllInstructions.setWidth("978px");
				addComponent(overAllInstructions);

				addComponent(new VerticalLayout() {
					{
						setSpacing(true);
						setMargin(true);
						addComponent(title1);
						addStyleName("border");
						addStyleName("locallogin");
						setWidth("978px");

						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(instructions1);
								setExpandRatio(instructions1, 0.7f);
								setWidth("978px");

								VerticalLayout newPasswordlogin = new VerticalLayout() {
									{
										setSpacing(true);
										setMargin(true);
										addComponent(emailLabel);
										addComponent(localPassword);
										addComponent(repeatLocalPassword);
										addComponent(localPasswordResults);
										addComponent(createButton);
									}
								};
								addComponent(newPasswordlogin);
								setExpandRatio(newPasswordlogin, 0.3f);
							}
						});
					}
				});

				if (enableEnterpriseProvidersForOutside) {

					addComponent(new VerticalLayout() {
						{
							setMargin(true);
							setSpacing(true);
							addComponent(title2);
							addStyleName("border");
							addStyleName("enterpriselogin");
							setWidth("978px");

							addComponent(new HorizontalLayout() {
								{
									setSpacing(true);
									addComponent(instructions2);
									setExpandRatio(instructions2, 0.7f);
									setWidth("978px");

									VerticalLayout umlogin = new VerticalLayout() {
										{
											setSpacing(true);
											setMargin(true);
											addComponent(enterpriseUserName);
											addComponent(enterprisePassword);
											addComponent(enterpriseErrorMessage);
											addComponent(enterpriseLogin);
										}
									};
									addComponent(umlogin);
									setExpandRatio(umlogin, 0.3f);
								}
							});
						}
					});

				}

				if (enableExternalProviders) {

					addComponent(new VerticalLayout() {
						{
							setMargin(true);
							setSpacing(true);
							addComponent(title3);
							addStyleName("border");
							addStyleName("externallogin");
							setWidth("978px");
							addComponent(instructions3);

							addComponent(new HorizontalLayout() {
								{
									setSpacing(true);
									setMargin(true);
									setCaption("Authorized External Verifiers");
									addComponent(googleButton);
									addComponent(yahooButton);
									addComponent(microsoftButton);

								}
							});
						}
					});
				}
			}

		};

		wrapper.addComponent(loginBox);
		wrapper.setExpandRatio(loginBox, 1.0f);

		addComponent(wrapper);
		setExpandRatio(wrapper, 1.0f);
		setSizeFull();
		setDefaultComponentAlignment(Alignment.TOP_CENTER);

	}

	@Override
	public void enter(ViewChangeEvent event) {

		id = event.getParameters();
		
		invitationEmail = getInvitationEmail(id);
		emailLabel.setValue(invitationEmail);
		emailLabel.setReadOnly(true);

	}
	
	public String getInvitationEmail(String id) {
		
		
		String invitationEmail = null;

		Connection conn = null;
		try {

			conn = Pools.getConnection(Pools.Names.PROJEX);
			try (PreparedStatement stmt = conn.prepareStatement("select * from personsmview where id = ?")) {
				stmt.setString(1, id);

				try (ResultSet rs = stmt.executeQuery()) {

					if (rs.next()) {
						invitationEmail = rs.getString("INVITATIONEMAIL");
					}

				}
			}

		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Could not retrieve firm data from firm id {}", e);
			}
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}

		return invitationEmail;

	}
	
	
	
}
