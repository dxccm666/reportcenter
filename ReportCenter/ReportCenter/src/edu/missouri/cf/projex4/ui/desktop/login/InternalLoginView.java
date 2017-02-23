package edu.missouri.cf.projex4.ui.desktop.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.Pools;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.users.Users;
import edu.missouri.cf.projex4.system.Authenticator;
import edu.missouri.cf.projex4.ui.desktop.LoginTopBarView;
import edu.missouri.cf.security.EnterpriseAuthenticator;

@SuppressWarnings("serial")
public class InternalLoginView extends LoginTopBarView {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private TextField pawprint;
	private PasswordField password;
	private TextField invitationCode;

	private String invitationValue = null;

	private Button step1Button;

	private Label label_1;

	private Label title;

	public InternalLoginView() {
		setSizeFull();
		init();
		layout();
	}

	public void init() {
		pawprint = new TextField("Single Sign on ID") {
			{
				setImmediate(true);
				addStyleName("login-user");
			}
		};

		password = new PasswordField("Password") {
			{
				setImmediate(true);
				addStyleName("login-password");
			}
		};

		invitationCode = new TextField("Invitation Code");
		
		title = new Label("<h1>Invitation Code Verification</h1>", ContentMode.HTML);

		label_1 = new Label(
				"<p>You should have received an email containing an <b>invitation code</b>.  "
				+ "This invitation code can only be used once to register your identity.  "
				+ "Please enter the code and sign in with <b>SSO</b> to the right. "
				+ "If you did not receive an email or you have difficulties, please contact an Owner's Representative.</p>"
						+ "<p>By logging into this site with the code that you received, "
						+ "you agree that you have read and understood "
						+ "our <a href=\"/projex/#!privacypolicy\">privacy policy</a> "
						+ "and that you will abide by our <a href=\"/projex/#!termsandconditions\">terms "
						+ "and conditions</a>.</p>",

		ContentMode.HTML);


		step1Button = new Button("Login") {
			{
				setIcon(new ThemeResource("icons/chalkwork/basic/arrow_right_16x16.png"));
				addStyleName("borderless");
				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

						runLogin();
					}
				});
			}
		};

	}

	public void layout() {
		VerticalLayout root = new VerticalLayout() {
			{
				setSpacing(true);
				setMargin(true);
				
				addComponent(new VerticalLayout() {
					{
						setSpacing(true);
						setMargin(true);
						addStyleName("border");
						addStyleName("locallogin");
						setWidth("978px");
						addComponent(title);

						addComponent(new HorizontalLayout() {
							{
								setWidth("978px");
								addComponent(label_1);
								setExpandRatio(label_1, 0.6f);
								VerticalLayout form = new VerticalLayout() {
									{
										setSpacing(true);
										setMargin(true);
										addComponent(pawprint);
										addComponent(password);
										addComponent(invitationCode);
										addComponent(step1Button);
									}
								};

								addComponent(form);
								setExpandRatio(form, 0.4f);
							}
						});
					}
				});

			}
		};

		addComponent(root);
		setExpandRatio(root, 1.0f);
	}

	public void runLogin() {
		Authenticator authenticator = new EnterpriseAuthenticator();
		User.setUser(new User());

		if (authenticator.authenticate(pawprint.getValue(), password.getValue(), User.getUser())) {

			if (verifyInvitationCode()) {
				Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.VERIFYINTERNALPERSONALINFOVIEW,
						Users.getUserID(invitationCode.getValue()));
			} else {
				User.setUser(null);
				if (logger.isDebugEnabled()) {
					logger.debug("verifyInvitationCode failed for {}", pawprint.getValue());
				}
			}

		} else {
			User.setUser(null);
			if (logger.isDebugEnabled()) {
				logger.debug("Authentication failed for {}", pawprint.getValue());
			}
		}
	}

	public boolean verifyInvitationCode() {

		Connection conn = null;
		invitationValue = invitationCode.getValue();

		if (invitationValue != null && !(invitationValue.equals(" "))) {
			try {
				conn = Pools.getConnection(Pools.Names.PROJEX);
				try (PreparedStatement pstmt = conn.prepareStatement("select * from users where invitationcode = ? ")) {

					pstmt.setString(1, invitationValue);
					try (ResultSet rs = pstmt.executeQuery()) {

						if (rs.next()) {
							return true;
						}
					}
				}

			} catch (SQLException e) {
				logger.error(e.getMessage());
				return false;
			} finally {
				Pools.releaseConnection(Pools.Names.PROJEX, conn);
			}

		}
		return false;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
