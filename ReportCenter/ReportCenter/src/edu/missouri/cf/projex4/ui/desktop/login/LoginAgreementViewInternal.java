package edu.missouri.cf.projex4.ui.desktop.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.ui.desktop.LoginTopBarView;

@SuppressWarnings("serial")
public class LoginAgreementViewInternal extends LoginTopBarView {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private Label laebl_MU;

	private Button agreeButton;

	public enum ScreenState {
		MUEMPLOYEE, NOTMU
	}

	public LoginAgreementViewInternal() {
		setSizeFull();
		init();
		layout();

	}

	private void init() {		

		laebl_MU = new Label("");

		agreeButton = new Button("Agree") {
			{
				setIcon(new ThemeResource("icons/chalkwork/basic/confirm_16x16.png"));
				addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {						
						Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.INTERNALLOGINVIEW);
					}
				});
			}
		};

	}

	private void layout() {
		VerticalLayout root = new VerticalLayout() {
			{
				setSpacing(true);
				setMargin(true);

				addComponent(laebl_MU);

				addComponent(new HorizontalLayout() {
					{
						addStyleName("login-buttons");
						setSpacing(true);
						addComponent(agreeButton);

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
