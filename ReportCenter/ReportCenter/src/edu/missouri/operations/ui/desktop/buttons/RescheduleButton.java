package edu.missouri.operations.ui.desktop.buttons;

import c10n.C10N;
import edu.missouri.operations.reportcenter.ReportcenterUI;
import edu.missouri.operations.reportcenter.ui.c10n.StandardButtonText;

import java.util.Locale;

import com.vaadin.ui.Button;

@SuppressWarnings("serial")
public class RescheduleButton extends Button {

	private StandardButtonText st;

	public RescheduleButton() {
		init();
	}

	private void init() {
		st = C10N.get(StandardButtonText.class, Locale.ENGLISH);
		setCaption(st.rescheduleButton());
		setDescription(st.rescheduleButton_help());
		setIcon(ReportcenterUI.iconSet.get("reschedule"));
		addStyleName("borderless");
	}

}
