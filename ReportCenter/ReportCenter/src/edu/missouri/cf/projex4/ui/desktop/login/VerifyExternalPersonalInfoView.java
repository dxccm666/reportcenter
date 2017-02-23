package edu.missouri.cf.projex4.ui.desktop.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.ui.desktop.LoginTopBarView;

@SuppressWarnings("serial")
public class VerifyExternalPersonalInfoView extends LoginTopBarView{
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private TextField nameField;
	private TextField usernameField;
	
	private Button nextButton;
	
	
	public VerifyExternalPersonalInfoView() {
		setSizeFull();
		init();
		layout();

	}
	
	public void init() {
		nameField = new TextField("Full Name");
		usernameField = new TextField("Username");
		nextButton = new Button("Next") {
			{
				addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {		
						
						Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.HOME);
					}
				});
			}
		};
	}

	public void layout() {
		
		VerticalLayout root = new VerticalLayout() {
			{
				setSpacing(true);
				setMargin(true);

				addComponent(nameField);
				addComponent(usernameField);

				addComponent(new HorizontalLayout() {
					{
						addStyleName("login-buttons");
						setSpacing(true);
						addComponent(nextButton);

					}
				});

			}
		};

		addComponent(root);
		
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
		
	}

}
