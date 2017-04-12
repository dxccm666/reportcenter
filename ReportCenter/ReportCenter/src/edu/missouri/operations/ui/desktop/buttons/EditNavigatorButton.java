package edu.missouri.operations.ui.desktop.buttons;

import java.util.Locale;

import com.vaadin.ui.Button;

import c10n.C10N;
import edu.missouri.operations.reportcenter.ReportcenterUI;
import edu.missouri.operations.reportcenter.ui.c10n.StandardButtonText;
import edu.missouri.operations.ui.EnumNavigator;

@SuppressWarnings("serial")
public class EditNavigatorButton extends NavigatorButton {

	private StandardButtonText st;

	public EditNavigatorButton(EnumNavigator navigator, Enum<?> view) {
		super(navigator, view);
		init();
	}
	
	public EditNavigatorButton(EnumNavigator navigator, Enum<?> view, Button.ClickListener listener) {
		super(navigator, view);
		init();
		setClickListener(listener);
	}
	
	private void init() {
		st = C10N.get(StandardButtonText.class, Locale.ENGLISH);
		setCaption(st.editButton());
		setDescription(st.editButton_help());
		setIcon(ReportcenterUI.iconSet.get("edit"));
		addStyleName("borderless");
		
	}
}
