package edu.missouri.cf.projex4.ui.desktop;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;

import edu.missouri.cf.projex4.Projex4UI;

@SuppressWarnings("serial")
public class DatabaseNavigatorButton extends NavigatorButton {

	public DatabaseNavigatorButton() {
		
		super();
		setIcon(Projex4UI.iconSet.get("database"));
		addStyleName("borderless");
		
		addClickListener(new Button.ClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void buttonClick(ClickEvent event) {
				
				Page.getCurrent().open(new ExternalResource(Projex4UI.get().getProjexViewNavigator().getUrl(getView(), null)), "_blank", false);
				
			}
		});
		
	}
}
