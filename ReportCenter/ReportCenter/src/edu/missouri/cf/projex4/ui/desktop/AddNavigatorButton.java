package edu.missouri.cf.projex4.ui.desktop;

import java.util.Locale;

import c10n.C10N;

import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.ui.c10n.StandardButtonText;

@SuppressWarnings("serial")
public class AddNavigatorButton extends NavigatorButton {

	private StandardButtonText st;

	public AddNavigatorButton() {
		super();

		if (User.getUser() != null) {
			st = C10N.get(StandardButtonText.class, User.getUser().getUserLocale());
		} else {
			st = C10N.get(StandardButtonText.class, Locale.ENGLISH);
		}

		setCaption(st.addButton());
		setDescription(st.addButton_help());
		setIcon(Projex4UI.iconSet.get("add"));
		addStyleName("borderless");
	}
}
