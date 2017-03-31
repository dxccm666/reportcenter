package edu.missouri.operations.reportcenter.ui.views.configuration.reports;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.desktop.buttons.DeleteButton;
import edu.missouri.operations.ui.desktop.buttons.ReassignButton;
import edu.missouri.operations.ui.desktop.buttons.RescheduleButton;

public class ScheduledReportsView extends TopBarView {

	private StandardTable properties;
	private RescheduleButton rescheduleButton;
	private ReassignButton reassignButton;
	private DeleteButton deleteButton;

	public ScheduledReportsView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	private void init() {

		rescheduleButton = new RescheduleButton() {
			{
			}
		};

		reassignButton = new ReassignButton() {
			{
			}
		};

		deleteButton = new DeleteButton() {
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
				addComponent(new Label("Scheduled Reports", ContentMode.HTML) {
					{
						addStyleName("maintitle");
					}
				});
				addComponent(new HorizontalLayout() {
					{
						addComponent(rescheduleButton);
						addComponent(reassignButton);
						addComponent(deleteButton);
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
