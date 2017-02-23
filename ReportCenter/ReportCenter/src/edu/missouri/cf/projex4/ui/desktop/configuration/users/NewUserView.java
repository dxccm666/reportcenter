package edu.missouri.cf.projex4.ui.desktop.configuration.users;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.ui.desktop.TopBarView;

@SuppressWarnings("serial")
public class NewUserView extends TopBarView {

	
	private Label screendescription;
	
	//NewUserComponent newUserComponent = new NewUserComponent();
	public NewUserView() { 
		
	}

	@Override
	public void attach() {
		super.attach();
		init();
		layout();
	}

	private void init() {

		screendescription = new Label("<h1>Users</h1>", ContentMode.HTML);
		screendescription.addStyleName("projectlisting_label");
		
	}

	private void layout() {

		VerticalLayout root = new VerticalLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setSizeFull();
				addComponent(screendescription);
				//addCOmponent()
				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);
						
					}
				});
				
			}
		};
		addComponent(root);
		setExpandRatio(root, 1.0f);

	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
