package edu.missouri.operations.ui.desktop.buttons;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.operations.ui.EnumNavigator;

@SuppressWarnings("serial")
public class DatabaseNavigatorButton extends NavigatorButton {
	
	public class DatabaseNavigatorButtonListener implements Button.ClickListener {
		
		@SuppressWarnings("deprecation")
		@Override
		public void buttonClick(ClickEvent event) {
				Page.getCurrent().open(new ExternalResource(navigator.getUrl(getView(), null)), "_blank", false);
		}
		
	}

	public DatabaseNavigatorButton(EnumNavigator navigator, Enum<?> view) {
		
		super(navigator, view);
		setIcon(new ThemeResource("icons/chalkwork/basic/disc_16x16.png"));
		addClickListener(new DatabaseNavigatorButtonListener());
		
	}
}
