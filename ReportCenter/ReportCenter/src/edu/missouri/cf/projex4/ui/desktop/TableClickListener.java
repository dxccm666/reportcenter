package edu.missouri.cf.projex4.ui.desktop;

import com.vaadin.data.Item;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.missouri.operations.ui.desktop.buttons.NavigatorButton;


/**
 * This is implemented on the navigator opener button. This is the default click listener on the opener button if no
 * TableClickListener is set. If you would like to specify which TableClickListener an individual NavigatorButton uses, 
 * you can do so on the {@link NavigatorOpenerButton#addComponent(Enum, String, TableClickListener)} method.
 * The {@link #setUriFragments(Item)} function should be the only function that needs to be overriden.
 * @author reynoldsjj
 *
 */
public interface TableClickListener extends ClickListener {
	
	public void setNavigatorButton(NavigatorButton navigatorButton);
	public void setUriFragments(Item selectedItem);
	
	@Override
	public void buttonClick(ClickEvent event);
	
};
