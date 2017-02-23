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
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.Pools;
import edu.missouri.cf.projex4.data.system.core.users.Users;
import edu.missouri.cf.projex4.ui.desktop.LoginTopBarView;

@SuppressWarnings("serial")
public class ExternalVerifyInvitation extends LoginTopBarView {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private TextField emailVerifyField;
	private TextField invitationCodeField;
	private Button step1Button;
	private Label label_1;

	private Label label_3;

	private Label title;

	public ExternalVerifyInvitation() {
		setSizeFull();
		init();
		layout();

	}

	private void init() {

		emailVerifyField = new TextField("Invitation Email") {
			{
				setCaption("Invitation Email");
				setWidth("300px");
			}
		};
		invitationCodeField = new TextField("Invitation Code");

		title = new Label("<h1>Invitation Code Verification</h1>", ContentMode.HTML);

		label_1 = new Label(
				"<p>You should have received an email containing an <b>invitation code</b>.  This invitation code can only be used once to register your identity.  Please enter the invitation email address and code to the right. If you did not receive an email or you have difficulties, please contact an Owner's Representative.</p>"
						+ "<p>By logging into this site with the code that you received, you agree that you have read and understood our <a href=\"/projex/#!privacypolicy\">privacy policy</a> and that you will abide by our <a href=\"/projex/#!termsandconditions\">terms and conditions</a>.</p>",

		ContentMode.HTML);

		label_3 = new Label(
				"<h3>Invitation to Supplier Diversity Participation. </h3>"
						+ "<p>The University of Missouri promotes and encourages the participation of businesses in our Supplier Diversity Program. "
						+ "We invite minority, women, disabled and veteran owned businesses "
						+ "to share their status when introducing themselves to consultants that have "
						+ "received Requests for Qualifications (RFQs) and at the start of Prebid Meetings.</p>"
						+ "<h3>Sustainable Design Policy </h3>"
						+ "<p>It is the policy of the University of Missouri to incorporate sustainability principles"
						+ " and concepts in the design of all facilities and infrastructure projects to the fullest "
						+ "extent possible, while being consistent with budget constraints , appropriate life cycle cost "
						+ "analysis, and customer priorities. This policy applies to renovation and new construction regardless "
						+ "of funding source or amount; to projects accomplished both in-house and through A/E contracts; "
						+ "and to design associated with all construction methods.</p>" + "<h3>Tobacco Free Campus Policy</h3>"
						+ "<p>MU campus (Columbia) has been tobacco free since July 1, 2013. Smoking and other tobacco use, "
						+ "including E-cigarettes or vaping, is prohibited in all areas. Please advise your subcontractors and "
						+ "all on-site workers as this policy will be strictly enforced. This applies to everyone, no exceptions.",
				ContentMode.HTML);

		step1Button = new Button("agree and next") {
			{

				setIcon(new ThemeResource("icons/chalkwork/basic/arrow_right_16x16.png"));
				addStyleName("borderless");

				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						step1Button.setEnabled(true);

						if (runLogin_External()) {

							if (verifyInitialized()) {

								step1Button.setEnabled(false);
								Notification.show("You have already been initialized, please use regular log in.");

							} else {

								Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.EXTERNALLOGINVIEW,
										Users.getUserID(invitationCodeField.getValue()));
							}

						} else {
							Notification.show("Your invitation code or invitation email address is not correct, please re-enter.");
						}
					}

				});
			}
		};
	}

	private void layout() {

		VerticalLayout root = new VerticalLayout() {
			{
				setSpacing(true);
				setMargin(true);
				setWidth("978px");

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
										addComponent(emailVerifyField);
										addComponent(invitationCodeField);
										addComponent(step1Button);
									}
								};

								addComponent(form);
								setExpandRatio(form, 0.4f);
							}
						});
					}
				});

				addComponent(label_3);

			}
		};

		addComponent(root);
		setExpandRatio(root, 1.0f);
	}

	public boolean runLogin_External() {

		String invitationValue = invitationCodeField.getValue();
		Connection c = null;

		if (invitationValue != null && !(invitationValue.equals(" "))) {

			try {

				c = Pools.getConnection(Pools.Names.PROJEX);

				try (PreparedStatement stmt = c
						.prepareStatement("select * from users where upper(invitationemail) = upper(?) and invitationcode = ?")) {
					stmt.setString(1, emailVerifyField.getValue().toUpperCase());
					stmt.setString(2, invitationValue);
					try (ResultSet rs = stmt.executeQuery()) {

						if (rs.next()) {
							return true;
						}
					}
				}

			} catch (SQLException e) {
				logger.error(e.getMessage());
			} finally {
				Pools.releaseConnection(Pools.Names.PROJEX, c);
			}
		}

		return false;

	}

	private boolean verifyInitialized() {

		String invitationValue = invitationCodeField.getValue();
		Connection c = null;

		if (invitationValue != null && !(invitationValue.equals(" "))) {

			try {

				c = Pools.getConnection(Pools.Names.PROJEX);

				try (PreparedStatement stmt = c
						.prepareStatement("select INITIALIZED from users where upper(invitationemail) = upper(?) and invitationcode = ?")) {
					stmt.setString(1, emailVerifyField.getValue());
					stmt.setString(2, invitationValue);
					try (ResultSet rs = stmt.executeQuery()) {

						if (rs.next()) {
							Integer i = rs.getInt("INITIALIZED");
							System.out.println("get INITIALIZED = " + i);
							if (i == 1) {
								return true;
							} else if (i == 0) {
								return false;
							}
						}
					}
				}

			} catch (SQLException e) {
				logger.error(e.getMessage());
			} finally {
				Pools.releaseConnection(Pools.Names.PROJEX, c);
			}
		}

		return true;

	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

}
