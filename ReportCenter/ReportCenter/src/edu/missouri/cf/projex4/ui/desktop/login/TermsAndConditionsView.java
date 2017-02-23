package edu.missouri.cf.projex4.ui.desktop.login;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.ui.desktop.LoginTopBarView;

@SuppressWarnings("serial")
public class TermsAndConditionsView extends LoginTopBarView {

	class TermsAndConditionsLayout extends CustomLayout {
		
		public TermsAndConditionsLayout() {
			super("terms");
		}
		
	}

	public TermsAndConditionsView() {
		setSizeFull();
		layout();
	}

	private void layout() {

		VerticalLayout root = new VerticalLayout() {
			{
				setSpacing(true);
				setMargin(true);
				TermsAndConditionsLayout t = new TermsAndConditionsLayout();
				t.setSizeFull();
				addComponent(t);
				setComponentAlignment(t, Alignment.TOP_LEFT);
				setExpandRatio(t,1.0f);
			}
		};
		
		addComponent(root);
		setExpandRatio(root,1.0f);

	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

}
