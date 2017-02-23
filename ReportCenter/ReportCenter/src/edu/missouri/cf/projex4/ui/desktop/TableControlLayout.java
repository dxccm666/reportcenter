package edu.missouri.cf.projex4.ui.desktop;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class TableControlLayout extends HorizontalLayout {

	HorizontalLayout leftSide = new HorizontalLayout();
	HorizontalLayout rightSide = new HorizontalLayout();

	public TableControlLayout() {
		super();
		setWidth("100%");
		addStyleName("tablecontrols");

		leftSide = new HorizontalLayout() {
			{
				addStyleName("tablecontrols");
				setSpacing(true);
			}
		};

		addComponent(leftSide);
		
		Label buttonSpacer = new Label();
		addComponent(buttonSpacer);
		setExpandRatio(buttonSpacer, 1.0f);
		
		rightSide = new HorizontalLayout() {
			{
				addStyleName("tablecontrols");
				setSpacing(true);
			}
		};

		addComponent(rightSide);

	}

	public void addRightComponent(Component c) {
		rightSide.addComponent(c);
	}
	
	public void removeRightComponent(Component c) {
		rightSide.removeComponent(c);
	}

	public void addLeftComponent(Component c) {
		leftSide.addComponent(c);
	}
	
	public void removeLeftComponent(Component c) {
		leftSide.removeComponent(c);
	}
	
	public void removeAllLeftComponents() {
		leftSide.removeAllComponents();
	}
	
	public void removeAllRightComponents() {
		rightSide.removeAllComponents();
	}

}
