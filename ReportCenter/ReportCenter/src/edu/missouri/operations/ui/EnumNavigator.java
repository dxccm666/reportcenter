package edu.missouri.operations.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.Page;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;

/**
 * EnumNavigator adds methods to navigator to allow use of Enums instead of
 * strings.
 * 
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class EnumNavigator extends Navigator {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public EnumNavigator(UI ui, ComponentContainer container) {
		super(ui, container);
	}

	public EnumNavigator(UI ui, SingleComponentContainer container) {
		super(ui, container);
	}

	public EnumNavigator(UI ui, ViewDisplay display) {
		super(ui, display);
	}

	public EnumNavigator(UI ui, NavigationStateManager stateManager, ViewDisplay display) {
		super(ui, stateManager, display);
	}

	public void navigateTo(Enum<?> view) {
		super.navigateTo(view.name().toLowerCase());
	}

	/**
	 * appends urifragment to the view.
	 * 
	 * @param view
	 * @param urifragment
	 * @author reynoldsjj
	 */
	public void navigateTo(Enum<?> view, String urifragment) {
		String uri = view.name().toLowerCase();
		if (urifragment != null) {
			uri = uri.concat("/" + urifragment);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("navigateTo(" + uri + ")");
		}
		super.navigateTo(uri);
	}
	
	public String getUrl(Enum<?> view, String urifragment) {
		
		StringBuilder url = new StringBuilder(Page.getCurrent().getLocation().toString());
		
		if(logger.isDebugEnabled()) {
			logger.debug("URL = {}", url);
		}
		
		if(Page.getCurrent().getUriFragment()!=null) {
			url.delete(url.length()-Page.getCurrent().getUriFragment().length()+1, url.length());
		}
		url.append(view.name().toLowerCase()).append("/").append(urifragment);
		
		return url.toString();
		
	}

	ViewProvider provider;

	@Override
	public void addProvider(ViewProvider provider) {
		super.addProvider(provider);
		this.provider = provider;
	}

	public ViewProvider getProvider() {
		return provider;
	}

}
