package edu.missouri.operations.reportcenter.ui.views;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;

import edu.missouri.operations.reportcenter.ui.TopBarView;

@SuppressWarnings("serial")
public class DefaultView extends TopBarView {

	public DefaultView() {
		
	}
	
	@Override
	public void attach() {
		super.attach();
		init();
	}

	private void init() {
		
		Label nothingLabel = new Label("Screen has not been implemented yet.");
		addComponent(nothingLabel);
		
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
