package edu.missouri.cf.projex4.ui.desktop.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.ui.desktop.LoginTopBarView;

@SuppressWarnings("serial")
public class SelectEmployeeTypeView extends LoginTopBarView {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public Label label_1;
	public Button yesButton;
	public Button noButton;
	
	public SelectEmployeeTypeView() {
		setSizeFull();
		init();
		layout();
	}
	
	public void init() {
		
		label_1 = new Label("<h2>Are you an employee of the University of Missouri?</h2>", ContentMode.HTML);
		
		yesButton = new Button("Yes") {
			{
				setIcon(new ThemeResource("icons/chalkwork/basic/confirm_16x16.png"));
				addStyleName("borderless");
				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {		
						
						Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.INTERNALLOGINVIEW);
					}
				});
			}
		};
		
		noButton = new Button("No") {			
			{
				setIcon(new ThemeResource("icons/chalkwork/basic/close_16x16.png"));
				addStyleName("borderless");
				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {	
						
						Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.EXTERNALVERIFYINVITATION);
						
					}
				});
			}
		};
		
	}
	
	public void layout() {
		
		final HorizontalLayout h1 = new HorizontalLayout() {
			{
				setSpacing(true);
				addComponent(yesButton);
				addComponent(noButton);
			}
		};
		
		VerticalLayout root = new VerticalLayout() {
			{
				setSpacing(true);
				setMargin(true);
				setSizeFull();

				addComponent(label_1);
				setComponentAlignment(label_1, Alignment.MIDDLE_CENTER);

				addComponent(h1);
				setExpandRatio(h1,1.0f);

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
