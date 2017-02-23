package edu.missouri.cf.projex4.ui.desktop.login;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.ui.desktop.LoginTopBarView;

@SuppressWarnings("serial")
public class ForgetPasswordCompleteView extends LoginTopBarView {

	private Button comfirmButton;

	private Label instructions;

	public ForgetPasswordCompleteView() {
		init();
		layout();

	}

	public void init() {
		
		instructions = new Label("<h1>Password Reset</h1><p>Your user account has been reset.  You should receive a new invitation email in a few minutes.</p>",ContentMode.HTML);
		
		comfirmButton = new Button("go to login screen");
		comfirmButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
					Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.LOGIN);
			}
		});

	}

	public void layout() {

		VerticalLayout root = new VerticalLayout() {
			{
				setSpacing(true);
				setMargin(true);
				
				addComponent(instructions);
				addComponent(comfirmButton);

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
