package edu.missouri.operations.reportcenter.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Notification;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;

import edu.missouri.operations.data.User;
import edu.missouri.operations.reportcenter.ReportcenterUI;
import edu.missouri.operations.reportcenter.ui.views.ErrorView;
import edu.missouri.operations.ui.EnumNavigator;

/**
 * ProjexViewNavigator is a single point of instantiation for all screens.
 * Currently each screen is declared static, but Vaadin recommends instantiating
 * a new instance every switch. I don't know which is best and which one works
 * in a multi-user environment.
 * 
 * This doesn't seem to handle fragments of screens.
 * 
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class ReportCenterViewNavigator extends EnumNavigator {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public ReportCenterViewNavigator(UI ui, ComponentContainer container) {
		super(ui, container);
		init();
	}

	public ReportCenterViewNavigator(UI ui, SingleComponentContainer container) {
		super(ui, container);
		init();
	}

	public ReportCenterViewNavigator(UI ui, ViewDisplay display) {
		super(ui, display);
		init();
	}

	public ReportCenterViewNavigator(UI ui, NavigationStateManager stateManager, ViewDisplay display) {
		super(ui, stateManager, display);
		init();
	}

	private void init() {
		
		setErrorView(ErrorView.class);

		addViewChangeListener(new ViewChangeListener() {

			/* This should allow us to force a save before switching view */

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				
				logger.debug("screenswitcher.ViewChangeListener.beforeViewChange called");
				logger.debug("screenswitcher.ViewChangeListener.beforeViewChange parameters = " + event.getParameters());
				logger.debug("viewName = {}", event.getViewName());
				
				/*
				
				if (User.getUser() == null && ((ReportCenterViewProvider)getProvider()).requireLogin(event.getViewName())) {
					logger.debug("User is not logged in");
					String fragmentAndParameters = event.getViewName();
					if (event.getParameters() != null && !event.getParameters().isEmpty()) {
						fragmentAndParameters += "/" + event.getParameters();
					}
					ReportcenterUI.get().getViewNavigator().getDisplay().showView(new LoginView(fragmentAndParameters));
					return false;
				}				
				
				*/
				
				// This should stop the screen switch if the current screen is
				// in Editing Mode.
				// TODO We should make this a pretty notification.
			
				/*
				if (event.getOldView() instanceof EditingStateManipulator && ((EditingStateManipulator) event.getOldView()).getEditingState() == EditingState.EDITING) {
					logger.debug("Screen is in editing mode.  Refusing to switch to new screen.");
					
					// TODO Change to C10N
					Notification.show("Need to save or cancel edits before switching screen");
					
					return false;
				} else {
					logger.debug("Screen can be switched.");
					return true;
				}
				*/
				return true;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {
				logger.debug("screenswitcher.ViewChangeListener.afterViewChange called");
			}

		});

	}

}
