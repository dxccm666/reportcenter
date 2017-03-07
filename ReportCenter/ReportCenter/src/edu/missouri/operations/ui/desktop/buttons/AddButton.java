package edu.missouri.operations.ui.desktop.buttons;

import c10n.C10N;

import java.util.Locale;

import com.vaadin.ui.Button;

import edu.missouri.cf.projex4.ui.c10n.StandardButtonText;

@SuppressWarnings("serial")
public class AddButton extends Button {

	private StandardButtonText st;

	public AddButton() {
		init();
	}

	private void init() {
		st = C10N.get(StandardButtonText.class, Locale.ENGLISH);
		setCaption(st.addButton());
		setDescription(st.addButton_help());
		//setIcon(Projex4UI.iconSet.get("add"));
		addStyleName("borderless");
	}

}
