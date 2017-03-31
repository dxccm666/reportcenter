package edu.missouri.operations.reportcenter.ui.views.configuration.reports;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.desktop.buttons.AddButton;
import edu.missouri.operations.ui.desktop.buttons.EditButton;

public class ReportsView extends TopBarView {

	private StandardTable properties;
	private AddButton addButton;
	private EditButton editButton;

	public ReportsView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	private void init() {

		addButton = new AddButton() {
			{
			}
		};

		editButton = new EditButton() {
			{
			}
		};

		properties = new StandardTable() {
			{

			}
		};

		addInnerComponent(new VerticalLayout() {
			{
				setSizeFull();
				setMargin(true);
				addComponent(new Label("Report Registration", ContentMode.HTML) {
					{
						addStyleName("maintitle");
					}
				});
				addComponent(new HorizontalLayout() {
					{
						addComponent(addButton);
						addComponent(editButton);
					}
				});
				addComponent(properties);
				setExpandRatio(properties, 1.0f);

			}
		});

	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
