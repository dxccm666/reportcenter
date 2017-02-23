package edu.missouri.cf.projex4.ui.desktop.login;

import edu.missouri.cf.projex4.email.ProjexEmailer;

public class ForgetPasswordEmailer extends ProjexEmailer {

	public ForgetPasswordEmailer(String invitationEmail,String invitationCode) {
		
		super();
		setTemplate("OUTSIDEUSERINVITATION");
		setTo(invitationEmail);
		setSubject("Reset Your Account Password");
		set("INVITATIONCODE", invitationCode);
		setBody();
		send();
		
		
	}

}
