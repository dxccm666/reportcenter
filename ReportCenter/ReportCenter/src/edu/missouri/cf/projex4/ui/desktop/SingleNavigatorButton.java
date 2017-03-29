package edu.missouri.cf.projex4.ui.desktop;

import com.vaadin.ui.Button;

import edu.missouri.operations.ui.desktop.buttons.NavigatorButton;

@SuppressWarnings("serial")
public class SingleNavigatorButton extends NavigatorButton implements Button.ClickListener {

	public SingleNavigatorButton() {
		addClickListener(this);
	}

	public SingleNavigatorButton(Enum<?> view) {
		super(view);
		addClickListener(this);
	}

	public SingleNavigatorButton(Enum<?> view, String caption) {
		super(view, caption);
		addClickListener(this);
	}

	public SingleNavigatorButton(Enum<?> view, String caption, ClickListener listener) {
		super(view, caption, listener);
		addClickListener(this);
	}
	
	public void navigateTo() {
		getNavigator().navigateTo(getView(), getUriFragment());
	}
	
	boolean useDefaultClickListener = true;
	
	public void setUseDefaultClickListener(boolean b) {
		useDefaultClickListener = b;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if(useDefaultClickListener) navigateTo();
	}
		
}
