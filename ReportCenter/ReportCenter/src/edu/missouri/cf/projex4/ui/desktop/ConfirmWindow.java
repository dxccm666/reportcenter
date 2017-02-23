package edu.missouri.cf.projex4.ui.desktop;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.cf.projex4.ui.desktop.documents.PopupWindow;

@SuppressWarnings("serial")
public abstract class ConfirmWindow extends PopupWindow {

	private GridLayout layout;
	private Label message;
	
	public ConfirmWindow() {
		super();
		init();
	}
	
	private void init() {
		setWidth("300px");
		setHeight("200px");
		
		layout = new GridLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.setSizeFull();
		layout.setColumns(1);
		layout.setRows(2);
		layout.setRowExpandRatio(0, 1.0f);
		layout.addComponent(getButtons(), 0, 1);
		layout.setComponentAlignment(getButtons(), Alignment.MIDDLE_RIGHT);
		
		setContent(layout);
		
		message = new Label();
		layout.addComponent(message, 0, 0);
		
		Button confirm = new Button("yes", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				confirmAction();
			}
		});
		
		Button cancel = new Button("cancel", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		getButtons().addComponent(confirm);
		getButtons().addComponent(cancel);
		//getButtons().setComponentAlignment(cancel, Alignment.MIDDLE_RIGHT);
		
		addWindow();		
	}
	
	public void setMessage(String message) {
		this.message.setValue(message);
	}
	
	public abstract void confirmAction();
	
}
