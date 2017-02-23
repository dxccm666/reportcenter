package edu.missouri.cf.projex4.ui.desktop;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ErrorView extends VerticalLayout implements View {
	
	public ErrorView() {
		Label label = new Label("Sorry! We could not find the Page you were looking for.");
		addComponent(label);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
