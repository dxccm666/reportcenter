package edu.missouri.cf.projex4.ui.desktop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.server.Page;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.EnumNavigator;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.system.User;

/**
 * This is only a {@link PopupButton}. The ClickListener on this only opens up
 * the Popup. It does not actually do any of the navigating. This class assumes
 * that this Button is only attached to a single Table.
 * 
 * @author reynoldsjj
 * 
 */
@SuppressWarnings("serial")
public class NavigatorOpenerButton extends PopupButton {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	LinkedHashMap<Enum<?>, NavigatorButton> buttons;

	protected ArrayList<Enum<?>> enums = new ArrayList<Enum<?>>();

	Layout content;
	String urifragment;
	AbstractSelect table;
	private String screenName;
	private EnumNavigator navigator;

	public AbstractSelect getTable() {
		return table;
	}

	/**
	 * Sets the table of the button component so that the uuid is retrieved only
	 * when a NavigatorButton is clicked.
	 * 
	 * @param table
	 *            - AbstractSelect to be set.
	 * @author reynoldsjj
	 */
	public void setTable(AbstractSelect table) {
		this.table = table;

		if (content != null) {
			for (Component c : content) {
				((NavigatorButton) c).setTable(table);
			}
		}
	}

	/**
	 * @return the urifragment
	 */
	public String getUriFragment() {
		return urifragment;
	}

	/**
	 * @param urifragment
	 *            the urifragment to set
	 */
	public void setUriFragment(String urifragment) {

		this.urifragment = urifragment;

		if (content != null) {
			for (Component c : content) {
				((NavigatorButton) c).setUriFragment(this.urifragment);
			}
		}

	}

	Button.ClickListener clicklistener = new Button.ClickListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void buttonClick(ClickEvent event) {
			if (screenName != null && table != null) {
				Container container = table.getContainerDataSource();
				if (container instanceof Filterable) {
					Collection<Filter> filters = ((Filterable) container).getContainerFilters();
					if (!filters.isEmpty())
						User.getUser().setLastFilters(filters);
				}
			}

			if (table.isMultiSelect()) {

					if (table.getValue() != null && ((Collection<Object>) table.getValue()).size() > 0) {
						User.getUser().setLastSelectedItem((Collection<Object>) table.getValue());
					}

			} else {

				if (table.getValue() != null) {
					User.getUser().setLastSelectedItem(table.getValue());
				}

			}

			setPopupVisible(false);
		}
	};

	public NavigatorOpenerButton() {
		setCaption("open in ...");
		setDescription("Open selected item in a screen");
		init();
	}

	public NavigatorOpenerButton(String caption) {
		super(caption);
		init();
	}

	/**
	 * Uses the default TableClickListener. If a custom TableClickListener needs
	 * to be set, call {@link #addComponent(Enum, String, TableClickListener)}
	 * instead.
	 * 
	 * @param viewName
	 * @param description
	 */
	public void addComponent(Enum<?> viewName, String description) {
		addComponent(viewName, description, null);
	}

	/**
	 * Add a NavigatorButton to the NavigatorOpenerButton.
	 * 
	 * @param viewName
	 * @param description
	 * @param tableClickListener
	 *            - Specify which TableClickListener to use. Look at
	 *            {@link TableClickListener}
	 */
	public void addComponent(Enum<?> viewName, String description, TableClickListener tableClickListener) {
		NavigatorButton button = new NavigatorButton(viewName, description);
		enums.add(viewName);
		button.setNavigator(navigator);
		button.addClickListener(clicklistener);
		content.addComponent(button);
		buttons.put(viewName, button);

		if (tableClickListener != null) {
			logger.debug("set new tableclicklistener for {}", viewName);
			button.addClickListener(tableClickListener);
			tableClickListener.setNavigatorButton(button);
		} else {
			button.addClickListener(new DefaultTableClickListener(button));
		}

	}

	public NavigatorButton getNavigatorButton(ProjexViewProvider.Views view) {
		return buttons.get(view);
	}

	public void removeComponent(Enum<?> viewName) {
		NavigatorButton button = buttons.get(viewName);
		button.removeClickListener(clicklistener);
		button.removeClickListener(new DefaultTableClickListener(button));
		content.removeComponent(button);
		buttons.remove(viewName);
	}

	public void removeAllComponents() {

	}

	public void setVisible(ProjexViewProvider.Views viewName, boolean enabled) {

		if (viewName != null && buttons.get(viewName) != null) {
			buttons.get(viewName).setVisible(enabled);
			buttons.get(viewName).setEnabled(enabled);
		}

	}

	public void setVisible(ArrayList<ProjexViewProvider.Views> viewList, boolean enabled) {

		for (ProjexViewProvider.Views view : viewList) {
			setVisible(view, enabled);
		}

	}

	public void setEnabled(ProjexViewProvider.Views viewName, boolean enabled) {
		buttons.get(viewName).setEnabled(enabled);
	}

	public void setEnabled(ArrayList<ProjexViewProvider.Views> viewList, boolean enabled) {

		for (ProjexViewProvider.Views view : viewList) {
			setEnabled(view, enabled);
		}

	}

	public void setAllEnabled(boolean enabled) {
		for (NavigatorButton b : buttons.values()) {
			b.setEnabled(enabled);
		}
	}

	public void setAllVisible(boolean visible) {
		for (NavigatorButton b : buttons.values()) {
			b.setVisible(visible);
		}
	}

	private void init() {

		buttons = new LinkedHashMap<Enum<?>, NavigatorButton>();
		setIcon(Projex4UI.iconSet.get("folder"));
		addStyleName("borderless");
		setImmediate(true);

		if (Page.getCurrent().getBrowserWindowHeight() < 600) {
			content = new GridLayout(2, 10);
		} else {
			content = new VerticalLayout();
		}

		content.addStyleName("buttonopener");
		setContent(content);
		setNavigator(Projex4UI.get().getProjexViewNavigator());

	}

	/*
	 * @Deprecated public void addComponent(NavigatorButton button) {
	 * content.addComponent(button); button.addClickListener(clicklistener); }
	 * 
	 * @Deprecated public void removeComponent(NavigatorButton button) {
	 * button.removeClickListener(clicklistener);
	 * content.removeComponent(button); }
	 */

	/**
	 * @return the navigator
	 */
	public EnumNavigator getNavigator() {
		return navigator;
	}

	/**
	 * @param navigator
	 *            the navigator to set
	 */
	public void setNavigator(EnumNavigator navigator) {

		this.navigator = navigator;

		if (content != null) {
			for (Component c : content) {
				((NavigatorButton) c).setNavigator(navigator);
			}
		}
	}

	/**
	 * @return the screenName
	 */
	public String getScreenName() {
		return screenName;
	}

	/**
	 * @param screenName
	 *            the screenName to set
	 */
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

}
