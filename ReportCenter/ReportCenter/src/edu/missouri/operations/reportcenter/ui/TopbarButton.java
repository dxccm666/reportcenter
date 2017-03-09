package edu.missouri.operations.reportcenter.ui;

import com.vaadin.server.Page;
import com.vaadin.ui.NativeButton;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;

@SuppressWarnings("serial")
public class TopbarButton extends NativeButton {

	private ProjexViewProvider.Views view;

	private boolean contextNeeded = false;

	public TopbarButton(ProjexViewProvider.Views view) {
		super();
		this.view = view;
		init();
	}

	public TopbarButton(ProjexViewProvider.Views view, String caption) {
		super(caption);
		this.view = view;
		init();
	}

	public TopbarButton(ProjexViewProvider.Views view, String caption, boolean contextNeeded) {
		super(caption);
		this.view = view;
		this.contextNeeded = contextNeeded;
		init();
	}

	private void init() {
		
		addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				String fragment = null;

				if (contextNeeded) {
					if (Page.getCurrent() != null && Page.getCurrent().getUriFragment() != null) {
						fragment = Page.getCurrent().getUriFragment().substring(1);
					}
				}

				addStyleName("selected");
				Projex4UI.get().getProjexViewNavigator().navigateTo(view, fragment);
			}
		});

	}

}