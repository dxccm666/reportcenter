package edu.missouri.operations.ui.desktop.buttons;

import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.ui.Button;
import edu.missouri.operations.ui.EnumNavigator;

@SuppressWarnings("serial")
public class NavigatorButton extends TableButton implements Button.ClickListener {
	
	// table button contains the table now.
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private Enum<?> view;
	private String urifragment;
	
	protected EnumNavigator navigator;
	
	protected boolean openInNewWindow = false;
	
	public void setOpenInNewWindow(boolean openInNewWindow) {
		this.openInNewWindow = openInNewWindow;
	}
	
	public NavigatorButton(EnumNavigator navigator, Enum<?> view) {
		super();
		addStyleName("borderless");
		setImmediate(true);
		setView(view);
		setNavigator(navigator);
		setClickListener(this);
	}

	public NavigatorButton(EnumNavigator navigator, Enum<?> view, String caption) {
		super(caption);
		addStyleName("borderless");
		setImmediate(true);
		setView(view);
		setNavigator(navigator);
		setClickListener(this);
	}

	public NavigatorButton(EnumNavigator navigator, Enum<?> view, String caption, ClickListener listener) {
		super(caption, listener);
		addStyleName("borderless");
		setImmediate(true);
		setView(view);
		setNavigator(navigator);
		setClickListener(listener);
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
	
	public void navigateTo() {
		getNavigator().navigateTo(getView(), getUriFragment());
	}
	
	public void navigateTo(String fragment) {
		getNavigator().navigateTo(getView(), fragment);
	}
	
	public String getUrl(String fragment) {
		return getNavigator().getUrl(getView(), fragment);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		navigateTo();
	}

}
