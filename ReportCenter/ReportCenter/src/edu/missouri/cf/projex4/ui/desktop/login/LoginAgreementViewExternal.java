package edu.missouri.cf.projex4.ui.desktop.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.ui.desktop.LoginTopBarView;

@SuppressWarnings("serial")
public class LoginAgreementViewExternal extends LoginTopBarView{
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private Label label;

	private Button agreeButton;

	public enum ScreenState {
		MUEMPLOYEE, NOTMU
	}

	public LoginAgreementViewExternal() {
		setSizeFull();
		init();
		layout();

	}

	private void init() {		

		label = new Label("<b>Invitation to Supplier Diversity and Women Businesses </b> <br><br>" +
						"The University of Missouri promotes and encourages the participation of Supplier Diversity " +
						"and Women owned Businesses. We invite minority and women owned business " +
						"to share their status when introducing themselves to consultants that have " +
						"received Requests for Qualifications (RFQs) and at the start of Prebid Meetings. <br><br>" +
						"<b>Sustainable Design Policy </b> <br><br>" +
						"It is the policy of the University of Missouri to incorporate sustainability principles" +
						" and concepts in the design of all facilities and infrastructure projects to the fullest " +
						"extent possible, while being consistent with budget constraints , appropriate life cycle cost " +
						"analysis, and customer priorities. This policy applies to renovation and new construction regardless " +
						"of funding source or amount; to projects accomplished both in-house and through A/E contracts; " +
						"and to design associated with all construction methods.", ContentMode.HTML);
		

		agreeButton = new Button("Agree") {
			{
				setIcon(new ThemeResource("icons/chalkwork/basic/confirm_16x16.png"));
				
				addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {						
						Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.EXTERNALVERIFYINVITATION);
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
				setSizeFull();
				
				addComponent(label);

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
