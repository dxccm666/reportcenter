package edu.missouri.operations.ui.desktop.buttons;

import java.util.Locale;

import c10n.C10N;
import edu.missouri.operations.reportcenter.ReportcenterUI;
import edu.missouri.operations.reportcenter.ui.c10n.StandardButtonText;
import edu.missouri.operations.ui.EnumNavigator;

@SuppressWarnings("serial")
public class AddNavigatorButton extends NavigatorButton {

	private StandardButtonText st;

	public AddNavigatorButton(EnumNavigator navigator, Enum<?> view) {
		
		super(navigator, view);
		st = C10N.get(StandardButtonText.class, Locale.ENGLISH);
		setCaption("add");
		setDescription(st.addButton_help());
		setIcon(ReportcenterUI.iconSet.get("add"));
		addStyleName("borderless");
		
	}
}
