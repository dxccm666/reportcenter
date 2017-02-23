package edu.missouri.cf.projex4.ui.desktop;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Deprecated
public abstract class TabbedEditorView extends EditorView {
	
	protected TabSheet tabs;
	protected VerticalLayout sheet1;
	protected VerticalLayout documentsSheet;

	public TabbedEditorView() {
		super();
		init();
	}

	@Override
	public abstract void enter(ViewChangeEvent event);
	
	private void init() {
		
		tabs = new TabSheet();
		tabs.addStyleName("tabs align-top");
		
		sheet1 = new VerticalLayout();
		sheet1.addStyleName("align-top");
		sheet1.setSizeFull();
		tabs.addTab(sheet1);
		
		/* Replace with documents Tab Component */
		documentsSheet = new VerticalLayout();
		documentsSheet.setSizeFull();
		tabs.addTab(documentsSheet);
		tabs.getTab(documentsSheet).setCaption("Documents");
		
		addComponent(tabs);
		
		VerticalLayout spacer = new VerticalLayout();
		addComponent(spacer);
		setExpandRatio(spacer,1.0f);
		
	}

}
