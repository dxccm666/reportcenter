package edu.missouri.operations.reportcenter.ui;

import com.vaadin.server.Page;
import com.vaadin.ui.NativeButton;
import edu.missouri.operations.reportcenter.ReportcenterUI;

@SuppressWarnings("serial")
public class TopbarButton extends NativeButton {

	private ReportCenterViewProvider.Views view;

	private boolean contextNeeded = false;

	public TopbarButton(ReportCenterViewProvider.Views view) {
		super();
		this.view = view;
		init();
	}

	public TopbarButton(ReportCenterViewProvider.Views view, String caption) {
		super(caption);
		this.view = view;
		init();
	}

	public TopbarButton(ReportCenterViewProvider.Views view, String caption, boolean contextNeeded) {
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
				ReportcenterUI.get().getViewNavigator().navigateTo(view, fragment);
			}
		});

	}

}