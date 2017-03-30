package edu.missouri.operations.ui.desktop.buttons;

import c10n.C10N;
import edu.missouri.operations.reportcenter.ui.c10n.StandardButtonText;

import java.util.Locale;

import com.vaadin.ui.Button;

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
