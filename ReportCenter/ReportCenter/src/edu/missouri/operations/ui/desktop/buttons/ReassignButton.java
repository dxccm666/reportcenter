package edu.missouri.operations.ui.desktop.buttons;

import c10n.C10N;
import edu.missouri.operations.reportcenter.ReportcenterUI;
import edu.missouri.operations.reportcenter.ui.c10n.StandardButtonText;

import java.util.Locale;

import com.vaadin.ui.Button;

@SuppressWarnings("serial")
public class ReassignButton extends Button {

	private StandardButtonText st;

	public ReassignButton() {
		init();
	}

	private void init() {
		st = C10N.get(StandardButtonText.class, Locale.ENGLISH);
		setCaption(st.reassignButton());
		setDescription(st.reassignButton_help());
		setIcon(ReportcenterUI.iconSet.get("reassign"));
		addStyleName("borderless");
	}

}
