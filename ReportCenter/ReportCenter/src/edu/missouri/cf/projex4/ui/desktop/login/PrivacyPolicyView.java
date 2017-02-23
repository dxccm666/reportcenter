package edu.missouri.cf.projex4.ui.desktop.login;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.ui.desktop.LoginTopBarView;

@SuppressWarnings("serial")
public class PrivacyPolicyView extends LoginTopBarView {

	class PrivacyPolicyLayout extends CustomLayout {

		public PrivacyPolicyLayout() {
			super("privacy");
		}

	}

	public PrivacyPolicyView() {
		setSizeFull();
		layout();
	}

	private void layout() {

		VerticalLayout root = new VerticalLayout() {
			{
				setMargin(true);
				PrivacyPolicyLayout l = new PrivacyPolicyLayout();
				l.setSizeFull();
				addComponent(l);
				setComponentAlignment(l, Alignment.TOP_LEFT);
				setExpandRatio(l, 1.0f);
			}
		};
		
		addComponent(root);
		setExpandRatio(root, 1.0f);

	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

}
