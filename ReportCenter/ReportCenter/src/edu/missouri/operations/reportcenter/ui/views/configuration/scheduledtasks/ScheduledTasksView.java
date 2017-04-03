package edu.missouri.operations.reportcenter.ui.views.configuration.scheduledtasks;

import java.sql.SQLException;

import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.operations.reportcenter.data.CronTaskRuns;
import edu.missouri.operations.reportcenter.data.CronTasks;
import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.TableColumn;
import edu.missouri.operations.ui.desktop.buttons.EditButton;
import edu.missouri.operations.ui.desktop.buttons.RescheduleButton;

public class ScheduledTasksView extends TopBarView {

	private StandardTable tasktable;
	private RescheduleButton rescheduleButton;
	private StandardTable taskruntable;
	private CronTaskRuns runs;
	private Button clearButton;

	public ScheduledTasksView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	private void init() {

		tasktable = new StandardTable() {
			{
				add(new TableColumn("JAVACLASS","Java Class").setExpandRatio(0.5f));
				add(new TableColumn("DESCRIPTION","Description").setExpandRatio(0.5f));
				add(new TableColumn("CRONEXPRESSION","Cron Expression").setWidth(200));
				add(new TableColumn("ISACTIVE","Active?").setWidth(100));

			}
		};

		taskruntable = new StandardTable() {
			{
				add(new TableColumn("RUNSTART","Start").setWidth(200));
				add(new TableColumn("RUNEND","End").setWidth(200));
				add(new TableColumn("STATUS","Status").setWidth(200));
				add(new TableColumn("ERROR","Error").setExpandRatio(1.0f));
				
			}
		};

		rescheduleButton = new RescheduleButton() {
			{
			}
		};

		clearButton = new Button() {
			{
				setCaption("clear logs");
			}
		};

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
				addComponent(tasktable);
				setExpandRatio(tasktable, 0.75f);
				addComponent(new HorizontalLayout() {
					{
						addComponent(clearButton);
					}
				});
				addComponent(taskruntable);
				setExpandRatio(taskruntable, 0.25f);

			}
		});

	}

	@Override
	public void enter(ViewChangeEvent event) {

		try {

			CronTasks query = new CronTasks();
			OracleContainer container = new OracleContainer(query);
			tasktable.setContainerDataSource(container);
			tasktable.configure();

			runs = new CronTaskRuns();
			runs.setMandatoryFilters(new Compare.Equal("ID", "-1"));
			OracleContainer runcontainer = new OracleContainer(runs);
			taskruntable.setContainerDataSource(runcontainer);
			taskruntable.configure();

		} catch (SQLException e) {

			if (logger.isDebugEnabled()) {
				logger.debug("Error occurred", e);
			}

		}

	}

}
