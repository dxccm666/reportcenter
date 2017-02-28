/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop.reports;

import java.sql.SQLException;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c10n.C10N;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button;
import edu.missouri.operations.data.OracleBoolean;
import edu.missouri.operatons.data.OracleString;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.data.reports.ReportCronTasks;
import edu.missouri.operations.data.User;
import edu.missouri.cf.projex4.ui.c10n.configuration.ReportCronTaskText;
import edu.missouri.cf.projex4.ui.common.OracleBooleanCheckBox;
import edu.missouri.cf.projex4.ui.common.OracleTimestampField;
import edu.missouri.cf.projex4.ui.desktop.StandardEditorView;
import edu.missouri.cf.projex4.ui.desktop.lookups.persons.UserField;
import edu.missouri.cf.projex4.ui.desktop.scheduler.SchedulerComponent;

/**
 * @author graumannc
 */

@SuppressWarnings("serial")
public class ReportCronTaskEditorView extends StandardEditorView {

	private static final transient Logger logger = LoggerFactory.getLogger(ReportCronTaskEditorView.class);

	private ReportCronTaskText st;

	@PropertyId("REPORTID")
	private TextField reportId;

	@PropertyId("ID")
	private TextField taskId;

	@PropertyId("REPORTNAME")
	private TextField reportName;

	@PropertyId("SCHEDULED")
	private OracleTimestampField scheduled;

	@PropertyId("SCHEDULEDBY")
	private UserField scheduledBy;

	@PropertyId("CRONEXPRESSION")
	private TextField cronexpression;

	@PropertyId("FILEFORMAT")
	private TextField fileFormat;

	@PropertyId("ISONETIME")
	private OracleBooleanCheckBox isOneTime;

	@PropertyId("ISACTIVE")
	private OracleBooleanCheckBox isActive;

	private ReportCronTaskParametersComponent cronTaskParameters;

	private ReportCronTaskEmailsComponent cronTaskEmails;

	private SchedulerComponent schedulerComponent;

	private Button scheduleButton;

	public ReportCronTaskEditorView() {
		super();
	}

	@Override
	public void attach() {
		super.attach();
		init();
		layout();
	}

	private void init() {

		st = C10N.get(ReportCronTaskText.class, User.getUser().getUserLocale());

		// controls = new DefaultFormEditControls();
		controls.setEditor(this);

		taskId = new TextField() {
			{
				setCaption("Schedule Id");
				setDescription("id of this scheduled report.");
			}
		};

		reportId = new TextField() {
			{
				setCaption(st.reportId());
			}
		};

		reportName = new TextField() {
			{
				setCaption(st.reportName());
				setDescription(st.reportName_help());
				setWidth("400px");
			}
		};

		scheduled = new OracleTimestampField() {
			{
				setCaption(st.scheduled());
				setDescription(st.scheduled_help());
			}
		};

		scheduledBy = new UserField() {
			{
				setCaption(st.scheduledByUserName());
				setDescription(st.scheduledByUserName_help());
				getTextField().setWidth("300px");
			}
		};

		cronexpression = new TextField() {
			{
				// setCaption(st.cronExpression());
				setDescription("Cron expression");
			}
		};

		fileFormat = new TextField() {
			{
				setCaption(st.fileFormat());
				setDescription(st.fileFormat_help());
			}
		};

		isOneTime = new OracleBooleanCheckBox() {
			{
				setCaption(st.isOneTime());
				setDescription("Will only run once at the time specified.");
			}
		};

		isActive = new OracleBooleanCheckBox() {
			{
				setCaption(st.isActive());
				setDescription(st.isActive_help());
			}
		};

		scheduleButton = new Button() {
			{
				setDescription("Converts the values entered above into a cron expression for use with the scheduler.");
				setIcon(new ThemeResource("icons/chalkwork/basic/clock_16x16.png"));
				addStyleName("borderless");

				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

						String expression = schedulerComponent.getValue();
						if (expression != null) {

							isOneTime.setReadOnly(false);
							cronexpression.setReadOnly(false);

							if ("ONETIME".equals(schedulerComponent.getChosenValue())) {
								isOneTime.setConvertedValue(OracleBoolean.TRUE);
							} else {
								isOneTime.setConvertedValue(OracleBoolean.TRUE);
							}

							cronexpression.setConvertedValue(new OracleString(expression));

							isOneTime.setReadOnly(true);
							cronexpression.setReadOnly(true);
						}
					}
				});

			}
		};

		schedulerComponent = new SchedulerComponent(false);

		cronTaskParameters = new ReportCronTaskParametersComponent();
		cronTaskEmails = new ReportCronTaskEmailsComponent();

		addNonEditableComponent(taskId);
		addNonEditableComponent(reportId);
		addNonEditableComponent(reportName);
		addNonEditableComponent(scheduled);
		addNonEditableComponent(scheduledBy);
		addNonEditableComponent(isOneTime);
		addEditableComponent(isActive);
		addEditableComponent(fileFormat);
		addNonEditableComponent(cronexpression);
		addEditableComponent(schedulerComponent);

		addDependentProjexEditor(cronTaskParameters);
		addDependentProjexEditor(cronTaskEmails);

	}

	private void layout() {

		VerticalLayout wrapper = new VerticalLayout() {
			{
				setSpacing(true);
				setMargin(true);
				addComponent(new Label("<h3>Scheduled Report</h3>", ContentMode.HTML));
				addComponent(controls);
				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);
						addComponent(taskId);
						addComponent(reportId);
						addComponent(reportName);
						setExpandRatio(reportName, 1.0f);
						addComponent(isActive);
					}
				});

				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);
						addComponent(scheduled);
						addComponent(scheduledBy);
						setExpandRatio(scheduledBy, 1.0f);
						addComponent(fileFormat);
						// addComponent(isOneTime);
					}
				});

				addComponent(schedulerComponent);
				addComponent(new HorizontalLayout() {
					{
						setCaption("Cron Expression");
						addComponent(cronexpression);
						addComponent(scheduleButton);
					}
				});

				addComponent(cronTaskParameters);
				setExpandRatio(cronTaskParameters, 1.0f);
				addComponent(cronTaskEmails);

			}
		};

		addComponent(wrapper);
		setExpandRatio(wrapper, 1.0f);

	}

	String id;

	private ReportCronTasks query;

	private OracleContainer sqlContainer;

	private Item item;

	@Override
	public void setScreenData(String parameters) {

		id = parameters;

		query = new ReportCronTasks();
		query.setId(id);

		try {

			sqlContainer = new OracleContainer(query);
			sqlContainer.overrideType("SCHEDULEDBYUSERNAME", OracleString.class);
			sqlContainer.overrideType("ISACTIVE", OracleBoolean.class);

			clearOracleContainers();
			addOracleContainer(sqlContainer);

			item = sqlContainer.getItemByProperty("ID", parameters);
			bind(item);

			cronTaskParameters.setData(id);
			cronTaskEmails.setData(id);

		} catch (SQLException e) {
			logger.error("Could not set data", e);
		}

		controls.setEditingState(EditingState.READONLY);

	}

	@Override
	public void afterCommit() {

		try {

			Scheduler sched = Projex4UI.getScheduler();

			TriggerKey triggerKey = new TriggerKey("trigger" + item.getItemProperty("ID").getValue().toString(), "reportgroup");

			CronTrigger newTrigger = TriggerBuilder.newTrigger().withIdentity("trigger" + item.getItemProperty("ID").getValue().toString(), "reportgroup").withSchedule(CronScheduleBuilder
					.cronSchedule(item.getItemProperty("CRONEXPRESSION").getValue().toString())).build();

			sched.rescheduleJob(triggerKey, newTrigger);

		} catch (SchedulerException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Scheduler - error in rescheduling task", e);
			}
		}

		setScreenData(id);

	}

}
