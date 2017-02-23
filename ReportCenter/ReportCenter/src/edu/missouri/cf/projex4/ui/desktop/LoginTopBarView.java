package edu.missouri.cf.projex4.ui.desktop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;

@SuppressWarnings("serial")
public abstract class LoginTopBarView extends VerticalLayout implements View, ProjexView {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
		
	protected Button helpButton;
	protected Button homeButton;
	
	public LoginTopBarView() {
		super();
		init();
	}

	public LoginTopBarView(Component... children) {
		super(children);
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
	
	/* private String createReportLink() {
		String location = Page.getCurrent().getLocation().toString();
		String fragment = Page.getCurrent().getUriFragment().substring(1);
		String newLocation = location.substring(0,location.length()-fragment.length()) + "default" + "/" + fragment;
		return newLocation;
	} */

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
		
		
		
		CssLayout menu2 = new CssLayout();
		menu2.addStyleName("menu");
		//menu2.setWidth("100%");
		topbar.addComponent(menu2);
		topbar.setComponentAlignment(menu2, Alignment.TOP_RIGHT);
		
		homeButton = new TopbarButton(ProjexViewProvider.Views.HOME) {
			{
				addStyleName("logo");
				setIcon(new ThemeResource("images/projextblogo_topbar.png"));
				addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						addStyleName("selected");
						String fragment = Page.getCurrent().getUriFragment().substring(1);
						Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.LOGIN, fragment);
					}
				});
			}
		};
		
		menu.addComponent(homeButton);
		
			
		
		helpButton = new NativeButton("help");
		helpButton.addStyleName("icon-help");
		helpButton.setIcon(new ThemeResource("icons/general/small/Help.png"));
		helpButton.setDescription("Everything you ever wanted to know about Projex.");
		helpButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				addStyleName("selected");
				String fragment = Page.getCurrent().getUriFragment().substring(1);
				Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.HELP, fragment);
			}
		});
		menu2.addComponent(helpButton);

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
