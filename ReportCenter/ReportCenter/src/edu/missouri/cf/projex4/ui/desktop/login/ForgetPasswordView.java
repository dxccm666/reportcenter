package edu.missouri.cf.projex4.ui.desktop.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.Pools;
import edu.missouri.cf.projex4.ui.desktop.LoginTopBarView;

@SuppressWarnings("serial")
public class ForgetPasswordView extends LoginTopBarView {

	private Button comfirmButton;

	private TextField emailField;
	private Label instructions;

	private static Logger logger = LoggerFactory.getLogger(ForgetPasswordView.class);

	public ForgetPasswordView() {
		init();
		layout();

	}

	public void init() {
		
		instructions = new Label("<h1>Password Reset</h1><p>A new invitation code can be sent to the email address that was originally used to invite you into the Projex system.<br/>  If that email address is no longer valid, you will need to contact the Owner's Representative to arrange for a new invitation code to be sent to you.</p><p>Once you have received your new invitation code, you will need to re-verify your account information and set up a new password.</p>",ContentMode.HTML);
		
		emailField = new TextField("Invitation Email Address");
		emailField.setWidth("300px");

		comfirmButton = new Button("send new invitation code");
		comfirmButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (doVerify()) {
					
					sendResetPassword();
					Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.FORGETPASSWORDCOMPLETE);
					
				} else {
					Notification.show("Projex4 does not currently have a person record.");
				}

			}
		});

	}

	public void layout() {

		VerticalLayout root = new VerticalLayout() {
			{
				setSpacing(true);
				setMargin(true);
				
				addComponent(instructions);
				addComponent(emailField);
				addComponent(comfirmButton);

			}
		};

		addComponent(root);
		setExpandRatio(root, 1.0f);

	}

	private boolean doVerify() {

		// Projex4 does not currently have a person record.

		Connection conn = null;

		try {

			conn = Pools.getConnection(Pools.Names.PROJEX);

			try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM USERS WHERE UPPER(INVITATIONEMAIL) = UPPER(?)")) {

				stmt.setString(1, emailField.getValue().toString());

				try (ResultSet rs = stmt.executeQuery()) {

					if (rs.next()) {
						return true;
					}

				}

			}

		} catch (SQLException sqle) {
			if (logger.isErrorEnabled()) {
				logger.error("Could not retrieve userid from userLogin {}", emailField.getValue(), sqle);
			}
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}

		return false;

	}

	private void updateUser(final String invitationEmail, final String invitationCode) {
    	
    	Connection conn = null;
    	try {
    		conn = Pools.getConnection(Pools.Names.PROJEX);
    		try (PreparedStatement stmt = conn.prepareStatement("update users set initialized = 0, invitationemailed = null, invitationcode = ? where upper(invitationemail) = upper(?)" )) {
    			stmt.setString(1, invitationCode);
    			stmt.setString(2, invitationEmail);
    			stmt.executeUpdate();
    			conn.commit();
    		}
    	} catch (SQLException sqle) {
			if (logger.isErrorEnabled()) {
				logger.error("Could not update user record for {}", invitationEmail, sqle);
			}
    	} finally {
    		Pools.releaseConnection(Pools.Names.PROJEX, conn);
    	}
    	
    	
    }

	private void sendResetPassword() {

		String newInvitationCode = invitationCodeGenerator();
		String invitationEmail = emailField.getValue().toString();
		updateUser(invitationEmail, newInvitationCode);
		
		ForgetPasswordEmailer forgetPasswordEmailer = new ForgetPasswordEmailer(invitationEmail, newInvitationCode);
		forgetPasswordEmailer.send();
		
	}

	private String invitationCodeGenerator() {

		String invitationCode = null;
		StringBuffer buffer = new StringBuffer("0123456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ");
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		int range = buffer.length();
		for (int i = 0; i < 13; i++) {
			sb.append(buffer.charAt(r.nextInt(range)));
		}

		invitationCode = sb.toString();

		if (logger.isDebugEnabled()) {
			logger.debug("new invitation code = {}", invitationCode);
		}

		return invitationCode;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
