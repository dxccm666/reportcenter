/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.operations.reportcenter.ui.views.ErrorView;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public abstract class HelpTopBarView extends VerticalLayout implements View, ProjexView {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected Button homeButton;
	
	public HelpTopBarView() {
		super();
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener
	 * .ViewChangeEvent)
	 */
	@Override
	public abstract void enter(ViewChangeEvent event);
	
	private void init() {
		
		setSizeFull();
		addStyleName("bordered mainscreen");
		setMargin(false);
		
		// start of button bar
		HorizontalLayout topbar = new HorizontalLayout();
		topbar.addStyleName("sidebar");
		topbar.setWidth("100%");
		//topbar.setHeight("40px");
		addComponent(topbar);
		
		CssLayout menu = new CssLayout();
		menu.addStyleName("menu");
		menu.setWidth("100%");
		topbar.addComponent(menu);
		topbar.setExpandRatio(menu, 1.0f);
		
		homeButton = new NativeButton();
		homeButton.addStyleName("logo");
		homeButton.setIcon(new ThemeResource("images/projextblogo_topbar.png"));
		menu.addComponent(homeButton);
		
		CssLayout menu2 = new CssLayout();
		menu2.addStyleName("menu");
		//menu2.setWidth("100%");
		topbar.addComponent(menu2);
		topbar.setComponentAlignment(menu2, Alignment.TOP_RIGHT);
		
	}
	
	@Override
	public void resetScreen() {

	}

	@Override
	public void saveScreen() {

	}

	@Override
	public void setScreenData(String o) {

	}
	
	public void showInvalidParametersView() {
		Projex4UI.get().getProjexViewNavigator().getDisplay().showView(new ErrorView());
	}
	
}


