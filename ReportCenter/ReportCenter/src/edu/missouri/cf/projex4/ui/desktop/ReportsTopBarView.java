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
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.operations.ui.views.ErrorView;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public abstract class ReportsTopBarView extends VerticalLayout implements View, ProjexView {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected Button homeButton;
	protected Button configurationButton;
	protected Button helpButton;

	private TopBarLink helpLink;

	public ReportsTopBarView() {
		super();
	}

	public ReportsTopBarView(Component... children) {
		super(children);
	}
	
	@Override 
	public void attach() {
		super.attach();
		init();
		layout();
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

	/*
	 * private String createReportLink() { String location =
	 * Page.getCurrent().getLocation().toString(); String fragment =
	 * Page.getCurrent().getUriFragment().substring(1); String newLocation =
	 * location.substring(0,location.length()-fragment.length()) + "default" +
	 * "/" + fragment; return newLocation; }
	 */

	private void init() {

		homeButton = new NativeButton() {
			{
				addStyleName("logo");
				setIcon(new ThemeResource("images/projextblogo_topbar.png"));
			}
		};
		
		configurationButton = new TopbarButton(ProjexViewProvider.Views.CONFIGURATION, "configuration") {
			{
				addStyleName("icon-configuration");
				setIcon(new ThemeResource("icons/chalkwork/basic/settings_16x16.png"));
			}
		};
		
		helpLink = new TopBarLink(ProjexViewProvider.Views.HELP, "Help") {
			{
				addStyleName("icon-help");
				setIcon(new ThemeResource("icons/general/small/Help.png"));
				setDescription("Everything you ever wanted to know about Projex.");
			}
		};

	}

	private void layout() {

		setSizeFull();
		addStyleName("bordered mainscreen");
		setMargin(false);

		addComponent(new HorizontalLayout() {
			{
				addStyleName("sidebar");
				setWidth("100%");

				CssLayout menu = new CssLayout() {
					{
						addStyleName("menu");
						setWidth("100%");
						addComponent(homeButton);
						addComponent(configurationButton);
					}
				};
				addComponent(menu);
				setExpandRatio(menu, 1.0f);
				CssLayout menu2 = new CssLayout() {
					{
						addStyleName("menu");
						addComponent(new HorizontalLayout() {
							{
								addStyleName("topbarlifter");
								addComponent(helpLink);
							}
						});
					}
				};
				addComponent(menu2);
				setComponentAlignment(menu2, Alignment.TOP_RIGHT);

			}
		});

	
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
