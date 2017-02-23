package edu.missouri.cf.projex4.ui.desktop;

import c10n.C10N;

import com.vaadin.ui.Button;

import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.ui.c10n.StandardButtonText;

@SuppressWarnings("serial")
public class DeleteButton extends Button {

	private StandardButtonText st;

	public DeleteButton() {
		init();
	}

	private void init() {
		if (User.getUser() != null) {
			st = C10N.get(StandardButtonText.class, User.getUser().getUserLocale());
			setCaption(st.deleteButton());
			setDescription(st.deleteButton_help());
		}
		setIcon(Projex4UI.iconSet.get("delete"));
		addStyleName("borderless");
	}

}
