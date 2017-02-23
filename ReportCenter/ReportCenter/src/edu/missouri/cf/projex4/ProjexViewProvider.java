package edu.missouri.cf.projex4;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class ProjexViewProvider implements ViewProvider {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public enum Views {
		DEFAULT, HOME, LOGIN 
	}

	class ProjexViewCache extends HashMap<ProjexViewProvider.Views, View> {

	}

	public ProjexViewProvider() {

		ProjexViewCache viewCache = new ProjexViewCache();
		Projex4UI.getCurrent().getSession().setAttribute(ProjexViewCache.class, viewCache);

	}

	@Override
	public String getViewName(String viewAndParameters) {
		if (logger.isTraceEnabled()) {
			logger.trace("getViewName({})", viewAndParameters);
		}

		if (null == viewAndParameters) {
			return null;
		}

		for (Views view : Views.values()) {
			if (viewAndParameters.equals(view.toString().toLowerCase()) || viewAndParameters.startsWith(view.toString().toLowerCase() + "/")) {
				return view.toString().toLowerCase();
			}
		}
		/*
		 * Handles the case where a user types in the url for the website, but
		 * doesn't specify which screen to go to. Automatically goes to the Home
		 * screen after login.
		 */
		if (viewAndParameters.equals("")) {
			return Views.HOME.toString().toLowerCase();
		}

		return null;
	}

	// static LoginView loginView = new LoginView();

	public View getView(Views view) {

		ProjexViewCache viewCache = UI.getCurrent().getSession().getAttribute(ProjexViewCache.class);

		View screen;

		switch (view) {

		default:
			screen = getInternalView(view);
			break;
		}

		return screen;

	}

	public View getInternalView(Views view) {

		switch (view) {
		case HOME:
			// return new HomeView();
		case LOGIN:
			// return new LoginView();
			// return loginView;
			
		default:
			return new DefaultView();
		}
	}

	public boolean requireLogin(Views view) {
		switch (view) {
		
		default:
			return true;
		}
	}

	public boolean requireLogin(String viewName) {
		return requireLogin(Views.valueOf(viewName.toUpperCase()));
	}

	public UI getViewAsUI(Views view) {
		return (UI) getView(view);
	}

	@Override
	public View getView(String viewName) {
		return getView(Views.valueOf(viewName.toUpperCase()));
	}

}
