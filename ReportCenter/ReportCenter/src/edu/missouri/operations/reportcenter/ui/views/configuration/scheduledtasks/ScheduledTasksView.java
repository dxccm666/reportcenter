package edu.missouri.operations.reportcenter.ui.views.configuration.scheduledtasks;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.desktop.buttons.EditButton;
import edu.missouri.operations.ui.desktop.buttons.RescheduleButton;

public class ScheduledTasksView extends TopBarView {

	private StandardTable table;
	private RescheduleButton rescheduleButton;

	public ScheduledTasksView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	private void init() {

		table = new StandardTable() {
			{
				
			}
		};
		
		rescheduleButton = new RescheduleButton() {{}};

		addInnerComponent(new VerticalLayout() {
			{
				setSizeFull();
				setMargin(true);
				addComponent(new Label("Scheduled Tasks", ContentMode.HTML) {
					{
						addStyleName("maintitle");
					}
				});
				addComponent(new HorizontalLayout() {
					{
						addComponent(rescheduleButton);
					}
				});
				addComponent(table);
				setExpandRatio(table, 1.0f);

			}
		});

	}

	@Override
	public void enter(ViewChangeEvent event) {
		
		/*
		
		try {
			
			EmailLogs query = new EmailLogs();
			OracleContainer container = new OracleContainer(query);
			table.setContainerDataSource(container);
			table.configure();
			
		} catch (SQLException e) {
			
			if(logger.isDebugEnabled()) {
				logger.debug("Error occurred", e);
			}
			
		}
		
		*/

	}

}
