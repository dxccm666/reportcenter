package edu.missouri.cf.projex4.ui.desktop;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Link;
import edu.missouri.cf.projex4.ProjexViewProvider;

@SuppressWarnings("serial")
public class TopBarLink extends Link {
	
	ProjexViewProvider.Views view;
	
	private static String createContextLink(ProjexViewProvider.Views viewName) {
		String location = Page.getCurrent().getLocation().toString();
		String fragment = Page.getCurrent().getUriFragment();
		String newLocation;
		if(fragment!=null && !"".equals(fragment)) {
			fragment = fragment.substring(1);
            newLocation = location.substring(0,location.length()-fragment.length()) + viewName.toString().toLowerCase() + "/" + fragment;
		} else {
            newLocation = location + "#!" + viewName.toString().toLowerCase();
		}
		// System.err.println("contextLink location = " + location + " fragment = " + fragment);
		// System.err.println("newLocation = " + newLocation);
		return newLocation;
	}

	public TopBarLink(ProjexViewProvider.Views view, String caption) {
		super(caption.toUpperCase(), new ExternalResource(createContextLink(view)));
		this.view = view;
		// System.err.println("new TopBarLink");
		setTargetName("_blank");
		addStyleName("topbarlink");
	}
	
	public void resetResource() {
		setResource(new ExternalResource(createContextLink(view)));
	}
	
	
	
	

}
