package edu.missouri.cf.projex4.ui.desktop;

import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.missouri.cf.projex4.EnumNavigator;
import edu.missouri.cf.projex4.Projex4UI;

@SuppressWarnings("serial")
public class NavigatorButton extends TableButton {
	
	// table button contains the table now.
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private Enum<?> view;
	private String urifragment;
	
	private EnumNavigator navigator;
	
	private boolean openInNewWindow = false;
	
	public void setOpenInNewWindow(boolean openInNewWindow) {
		this.openInNewWindow = openInNewWindow;
	}
	
	public NavigatorButton() {
		init();
	}

	public NavigatorButton(Enum<?> view) {
		super();
		setView(view);
		init();
	}

	public NavigatorButton(Enum<?> view, String caption) {
		super(caption);
		setView(view);
		init();
	}

	public NavigatorButton(Enum<?> view, String caption, ClickListener listener) {
		super(caption, listener);
		setView(view);
		init();
	}
	
	private void init() {
		addStyleName("borderless");
		setImmediate(true);
		//addClickListener(new TableClickListener(this));
		navigator = Projex4UI.get().getProjexViewNavigator();
	}

	public Enum<?> getView() {
		return view;
	}

	public void setView(Enum<?> view) {
		this.view = view;
	}

	public String getUriFragment() {
		return urifragment;
	}
	
	public void setUriFragment(String...urifragments) {
		urifragment = "";
		for (int i = 0; i < urifragments.length; i++) {
			urifragment += urifragments[i] + "/";
		}
		urifragment = urifragment.substring(0, urifragment.length()-1);
	}
	
	public void setUriFragment(String urifragment) {
		this.urifragment = urifragment;
	}
	
	/**
	 * @return the navigator
	 */
	public EnumNavigator getNavigator() {
		return navigator;
	}

	/**
	 * @param navigator the navigator to set
	 */
	public void setNavigator(EnumNavigator navigator) {
		this.navigator = navigator;
	}
	
	public void setClickListener(ClickListener listener) {
		@SuppressWarnings("unchecked")
		Collection<ClickListener> list = (Collection<ClickListener>) getListeners(ClickListener.class);
		for (Iterator<ClickListener> it = list.iterator(); it.hasNext(); ) {
			removeClickListener(it.next());
		}
		addClickListener(listener);
	}
	
	public void navigateTo(String fragment) {
		getNavigator().navigateTo(getView(), fragment);
	}
	
	public String getUrl(String fragment) {
		return getNavigator().getUrl(getView(), fragment);
	}

}
