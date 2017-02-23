package edu.missouri.cf.projex4.ui.desktop.reports;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefn;
import org.eclipse.birt.report.engine.api.IParameterSelectionChoice;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.missouri.cf.data.OracleBoolean;
import edu.missouri.cf.data.OracleTimestamp;
import edu.missouri.cf.data.collectioncontainer.CollectionContainer;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.data.reports.ReportCronTaskEmails;
import edu.missouri.cf.projex4.data.reports.ReportCronTaskParameters;
import edu.missouri.cf.projex4.data.reports.ReportCronTasks;
import edu.missouri.cf.projex4.data.reports.ReportRunHistory;
import edu.missouri.cf.projex4.data.reports.ReportRunParameters;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.data.system.core.objects.Objects;
import edu.missouri.cf.projex4.data.system.properties.SystemProperties;
import edu.missouri.cf.projex4.email.EmailAddressValidator;
import edu.missouri.cf.projex4.scheduler.jobs.ReportJob;
import edu.missouri.cf.projex4.ui.desktop.scheduler.SchedulerComponent;
import edu.missouri.cf.projex4.ui.desktop.scheduler.SchedulerComponent.ScheduleType;

@SuppressWarnings({ "serial", "deprecation" })
public class ReportWindow extends Window {

	protected final static transient Logger logger = Loggers.getLogger(ReportWindow.class);

	Window reportWindow;
	private IReportEngine reportEngine = null;
	private IRunAndRenderTask task = null;
	private IReportRunnable design = null;
	IScalarParameterDefn scalar = null;
	IGetParameterDefinitionTask p_task;

	final List<String> outType = Arrays.asList(new String[] { "PDF", "Excel", "Word" });
	final OptionGroup box = new OptionGroup("Output File Format", outType);

	private Button closeButton;
	private Button runButton;
	private VerticalLayout subLayout;
	private Label reportNameLabel;
	private Label parametersLabel;
	private SchedulerComponent scheduler;
	private TextField emailAddress;
	String reportName;
	String reportId;

	private String uuid;
	private String objectId;

	ArrayList<ParameterData> holders;

	private Label popupInstructionsLabel;

	private Label instructionLabel;

	public void addWindow() {
		if (!UI.getCurrent().getWindows().contains(this)) {
			UI.getCurrent().addWindow(this);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ReportWindow(String reportId, String reportName, String fileName, Map<String, String> map, String uuid, boolean slowRunning) {

		super();

		this.reportId = reportId;
		this.reportName = reportName;

		if (logger.isDebugEnabled()) {
			logger.debug("Report Window created");
			logger.debug("Report Name = {} map = {} uuid = {}", reportName, map, uuid);
		}

		if (reportName == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("ReportName is null");
			}
		}

		reportWindow = this;

		this.uuid = uuid;
		ObjectData d = Objects.getObjectDataFromUUID(uuid);
		if (d != null) {
			objectId = d.getObjectId();
		}

		setModal(true);
		setWidth("70%");
		setHeight("70%");
		setCaption("Report Options");

		reportNameLabel = new Label("<h3>" + reportName + "</h3>", ContentMode.HTML);

		instructionLabel = new Label("<p></p>", ContentMode.HTML);

		subLayout = new VerticalLayout() {
			{
				setSizeFull();
				setMargin(true);
				setSpacing(false);
			}
		};

		box.select("PDF");
		box.setImmediate(true);
		box.addStyleName("horizontal");

		runButton = new Button() {
			{
				setCaption("submit");
				addClickListener(new RunReportListener());
			}
		};

		closeButton = new Button() {
			{
				setCaption("close");
				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						// TODO VERY LOW - can we call close? instead.
						getUI().removeWindow(reportWindow);
					}

				});
			}
		};

		popupInstructionsLabel = new Label("Be sure to allow pop-ups from this server.");

		scheduler = new SchedulerComponent(slowRunning);

		if (slowRunning) {
			instructionLabel.setCaption("This report must be scheduled to run outside working hours.");
		} else {
			instructionLabel.setVisible(false);
		}

		scheduler.getSelector().addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				popupInstructionsLabel.setVisible("now".equals(scheduler.getValue()));
			}
		});

		emailAddress = new TextField() {
			{
				setCaption("E-mail To");
				setDescription("");
				setWidth("100%");
				setImmediate(true);
				setValidationVisible(true);
				addValidator(new EmailAddressValidator("E-mail is not valid format"));
			}
		};

		VerticalLayout verticalLayout_1 = new VerticalLayout() {
			{
				setMargin(true);
				setSpacing(true);
				addComponent(reportNameLabel);
				addComponent(instructionLabel);
				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);
						addComponent(subLayout);
						addComponent(scheduler);
					}
				});
				addComponent(box);
				addComponent(emailAddress);
				addComponent(popupInstructionsLabel);
				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);
						addComponent(runButton);
						addComponent(closeButton);
					}
				});
			}
		};

		setContent(verticalLayout_1);

		reportEngine = Projex4UI.getReportEngine();

		if (reportEngine == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("ReportEngine is null");
			}
		}

		addCloseListener(new CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {

				if (p_task != null) {
					p_task.close();
				}
				if (task != null) {
					task.close();
				}
			}

		});

		try {

			String reportFileName = SystemProperties.get("report.root") + "/" + fileName;
			if (logger.isDebugEnabled()) {
				logger.debug("Report File name = {}", reportFileName);
			}

			design = reportEngine.openReportDesign(reportFileName);
			task = reportEngine.createRunAndRenderTask(design);

			/*
			 * We are setting the hidden values first before getting the list
			 * values. Does this cause the list to requery the database for it's
			 * values if the query uses one of those parameters.
			 */

			p_task = reportEngine.createGetParameterDefinitionTask(design);

			if (uuid != null) {
				p_task.setParameterValue("UUID", uuid);
			}

			p_task.setParameterValue("USERID", User.getUser().getUserId());

			if (objectId != null) {
				p_task.setParameterValue("ID", objectId);
			}

			Collection<IScalarParameterDefn> params = p_task.getParameterDefns(true);

			if (params != null && !params.isEmpty()) {

				ParameterData parmDetails;
				AbstractField f = null;

				holders = new ArrayList<ParameterData>();

				boolean header = false;

				for (IScalarParameterDefn scalar : params) {

					parmDetails = loadParameterDetails(p_task, scalar);

					switch (parmDetails.getParameterType()) {

					case LIST_BOX:
						if (logger.isDebugEnabled()) {
							logger.debug("LIST_BOX");
						}

						f = new ComboBox("Please select an item");
						if (parmDetails.getSelectionList() == null || parmDetails.getSelectionList().isEmpty()) {
							if (logger.isDebugEnabled()) {
								logger.debug("selection list is not set");
							}
						} else {
							if (logger.isDebugEnabled()) {
								logger.debug("Attempting to set list collection");
								logger.debug("ParmDetails = {}", parmDetails.getSelectionList());
							}

							((ComboBox) f).setContainerDataSource(CollectionContainer.fromBeans(parmDetails.getSelectionList()));
						}
						f.setImmediate(true);
						f.setCaption("description");

						f.setCaption(parmDetails.getPromptText());
						f.setRequired(parmDetails.isRequired());
						subLayout.addComponent(f);
						break;

					case TEXT_BOX:

						if (logger.isDebugEnabled()) {
							logger.debug("TEXT_BOX {}", parmDetails.getDataType());
						}

						if (parmDetails.getDataType() == null) {
							if (logger.isDebugEnabled()) {
								logger.debug("getDataType returns null");
							}
						}

						switch (parmDetails.getDataType()) {

						case DATE:

							if (logger.isDebugEnabled()) {
								logger.debug("Date Field");
							}

							f = new PopupDateField();
							((PopupDateField) f).setResolution(Resolution.DAY);
							((PopupDateField) f).setDateFormat("MM/dd/yyyy");
							break;

						case DATETIME:

							if (logger.isDebugEnabled()) {
								logger.debug("Set Date Time Field");
							}

							f = new PopupDateField();
							((PopupDateField) f).setResolution(Resolution.DAY);
							((PopupDateField) f).setDateFormat("MM/dd/yyyy");
							break;

						case DECIMAL:
						case FLOAT:
							// TODO - Future Enhancement - Add better support
							// for Currency.
							f = new TextField("", new ObjectProperty<Double>(new Double(0.0d), Double.class));
							break;

						case STRING:
							f = new TextField();
							break;

						case BOOLEAN:
							// TODO - Not currently used in P4 - but should
							// probably add handling for more universal use.
							break;

						case INTEGER:
							f = new TextField("", new ObjectProperty<Integer>(new Integer(0), Integer.class));
							break;

						default:
							break;

						}
						break;
					}

					parmDetails.setField(f);
					holders.add(parmDetails);

					f.setCaption(parmDetails.getPromptText());
					f.setRequired(true);

					if (!("UUID".equals(parmDetails.getPromptText()) || "ID".equals(parmDetails.getPromptText()) || "USERID".equals(parmDetails.getPromptText()))) {
						if (!header) {
							parametersLabel = new Label("Report Parameters");
							subLayout.addComponent(parametersLabel);
							header = true;
						}
						subLayout.addComponent(f);
					} else {

						if (logger.isDebugEnabled()) {
							logger.debug("prompt text for field = {}", parmDetails.getPromptText());
						}

					}

				}

				if (header) {

					Label spacer = new Label();
					subLayout.addComponent(spacer);
					subLayout.setExpandRatio(spacer, 1.0f);

				}

			}

			p_task.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class RunReportListener implements ClickListener {

		public void saveScheduledReport() {

			System.err.println("Saving scheduled report");

			try {

				ReportCronTasks cronTask = new ReportCronTasks();
				PropertysetItem cronTaskItem = new PropertysetItem();
				cronTaskItem.addItemProperty("REPORTID", new ObjectProperty<String>(reportId));
				cronTaskItem.addItemProperty("SCHEDULEDBY", new ObjectProperty<String>(User.getUser().getUserId()));
				cronTaskItem.addItemProperty("SCHEDULED", new ObjectProperty<OracleTimestamp>(OracleTimestamp.now()));
				cronTaskItem.addItemProperty("CRONEXPRESSION", new ObjectProperty<String>(scheduler.getValue()));
				cronTaskItem.addItemProperty("ISACTIVE", new ObjectProperty<OracleBoolean>(OracleBoolean.TRUE));
				cronTaskItem.addItemProperty("FILEFORMAT", new ObjectProperty<String>((String) box.getValue()));
				cronTaskItem.addItemProperty("ISONETIME", new ObjectProperty<OracleBoolean>(ScheduleType.ONETIME.equals(scheduler.getChosenValue()) ? OracleBoolean.TRUE : OracleBoolean.FALSE));

				cronTask.storeExternalRow(cronTaskItem);
				String cronTaskId = (String) cronTask.getLastId();

				if (holders.size() > 0) {

					if (logger.isDebugEnabled()) {
						logger.debug("Saving parameters for scheduled report");
					}

					ReportCronTaskParameters cronTaskParameters = new ReportCronTaskParameters();

					int x = 1;
					for (ParameterData h : holders) {

						PropertysetItem parameterItem = new PropertysetItem();

						parameterItem.addItemProperty("REPORTCRONTASKID", new ObjectProperty<String>(cronTaskId));
						parameterItem.addItemProperty("PARAMETERNUMBER", new ObjectProperty<Integer>(new Integer(x++)));
						parameterItem.addItemProperty("PARAMETERTYPE", new ObjectProperty<String>(h.getDataType().name()));
						parameterItem.addItemProperty("PARAMETERNAME", new ObjectProperty<String>(h.getName()));
						parameterItem.addItemProperty("PARAMETERNAME", new ObjectProperty<String>(h.getName()));

						if (h.getField().getPropertyDataSource() != null) {
							parameterItem.addItemProperty("PARAMETERVALUE", new ObjectProperty<String>(h.getField().getPropertyDataSource().getValue().toString()));
						} else if (h.getName().equals("UUID") && uuid != null) {
							parameterItem.addItemProperty("PARAMETERVALUE", new ObjectProperty<String>(uuid));
						} else if (h.getName().equals("ID") && objectId != null) {
							parameterItem.addItemProperty("PARAMETERVALUE", new ObjectProperty<String>(objectId));
						} else if (h.getName().equals("USERID") && User.getUser().getUserId() != null) {
							parameterItem.addItemProperty("PARAMETERVALUE", new ObjectProperty<String>(User.getUser().getUserId()));
						} else {

							if (h.getField().getValue() == null) {
								parameterItem.addItemProperty("PARAMETERVALUE", new ObjectProperty<String>("null"));
							} else {
								parameterItem.addItemProperty("PARAMETERVALUE", new ObjectProperty<String>(h.getField().getValue().toString()));
							}

						}

						if (logger.isDebugEnabled()) {
							logger.debug("Saving parameter {}", h.getName());
						}

						cronTaskParameters.storeExternalRow(parameterItem);

					}

					ReportCronTaskEmails emails = new ReportCronTaskEmails();

					String[] addresses = emailAddress.getValue().split(";");
					for (String address : addresses) {

						PropertysetItem emailItem = new PropertysetItem();
						emailItem.addItemProperty("REPORTCRONTASKID", new ObjectProperty<String>(cronTaskId));
						emailItem.addItemProperty("EMAILADDRESS", new ObjectProperty<String>(address.trim()));
						emails.storeExternalRow(emailItem);

						if (logger.isDebugEnabled()) {
							logger.debug("Saving crontask emailaddress {}", address);
						}

					}

					JobDetail jobDetail = JobBuilder.newJob(ReportJob.class).withIdentity("report" + cronTaskId, "reportgroup").usingJobData("ReportCronTaskId", cronTaskId).usingJobData("ReportId",
							reportId).usingJobData("ScheduledBy", User.getUser().getUserId()).usingJobData("FileFormat", (String) box.getValue()).usingJobData("OneTime", ScheduleType.ONETIME.equals(
									scheduler.getChosenValue())).build();

					CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger" + cronTaskId, "reportgroup").
							withSchedule(CronScheduleBuilder.cronSchedule(scheduler.getValue())).build();

					Scheduler sched = Projex4UI.getScheduler();

					sched.scheduleJob(jobDetail, trigger);

				}

			} catch (SQLException e) {

				if (logger.isErrorEnabled()) {
					logger.error("Could not save cronTask", e);
				}

			} catch (SchedulerException e) {

				if (logger.isErrorEnabled()) {
					logger.error("Could not schedule cronTask - {}", scheduler.getValue(), e);
				}
			}

		}

		public void saveRunHistory(File tf) {

			try {

				ReportRunHistory runHistory = new ReportRunHistory();

				PropertysetItem item = new PropertysetItem();
				item.addItemProperty("USERID", new ObjectProperty<String>(User.getUser().getUserId()));
				item.addItemProperty("REPORTID", new ObjectProperty<String>(reportId));
				item.addItemProperty("FILEFORMAT", new ObjectProperty<String>((String) box.getValue()));
				item.addItemProperty("FILELOCATION", new ObjectProperty<String>(tf.getAbsolutePath()));

				runHistory.storeExternalRow(item);

				String runHistoryId = (String) runHistory.getLastId();

				if (holders != null) {

					ReportRunParameters runParameters = new ReportRunParameters();

					int x = 1;

					for (ParameterData h : holders) {

						PropertysetItem parameterItem = new PropertysetItem();

						parameterItem.addItemProperty("REPORTRUNHISTORYID", new ObjectProperty<String>(runHistoryId));
						parameterItem.addItemProperty("PARAMETERNUMBER", new ObjectProperty<Integer>(new Integer(x++)));
						parameterItem.addItemProperty("PARAMETERNAME", new ObjectProperty<String>(h.getName()));

						if (h.getField().getPropertyDataSource() != null) {
							parameterItem.addItemProperty("PARAMETERVALUE", new ObjectProperty<String>(h.getField().getPropertyDataSource().getValue().toString()));
						} else if (h.getName().equals("UUID") && uuid != null) {
							parameterItem.addItemProperty("PARAMETERVALUE", new ObjectProperty<String>(uuid));
						} else if (h.getName().equals("ID") && objectId != null) {
							parameterItem.addItemProperty("PARAMETERVALUE", new ObjectProperty<String>(objectId));
						} else if (h.getName().equals("USERID") && User.getUser().getUserId() != null) {
							parameterItem.addItemProperty("PARAMETERVALUE", new ObjectProperty<String>(User.getUser().getUserId()));
						} else if (h.getField() != null) {
							if (h.getField().getValue() == null) {
								parameterItem.addItemProperty("PARAMETERVALUE", new ObjectProperty<String>("null"));
							} else {
								parameterItem.addItemProperty("PARAMETERVALUE", new ObjectProperty<String>(h.getField().getValue().toString()));
							}
						}

						runParameters.storeExternalRow(parameterItem);

					}

				}

			} catch (SQLException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Unable to save History", e);
				}
			}

		}

		public void buttonClick(ClickEvent event) {

			if (task == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Cannot run report - task is null");
				}
				return;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Schedule = {}, Format = {}, Email Address = {}", scheduler.getValue(), box.getValue(), emailAddress.getValue());
			}

			if (ScheduleType.NOW.equals(scheduler.getChosenValue())) {

				if (holders != null) {

					for (ParameterData h : holders) {
						if (h.getField().getPropertyDataSource() != null) {
							if (logger.isDebugEnabled()) {
								logger.debug("Setting parameter {} to {}", h.getName(), h.getField().getPropertyDataSource().getValue());
							}
							task.setParameterValue(h.getName(), h.getField().getPropertyDataSource().getValue());
						} else if (h.getName().equals("UUID")) {
							if (logger.isDebugEnabled()) {
								logger.debug("Setting parameter {} to {}", h.getName(), uuid);
							}
							task.setParameterValue("UUID", uuid);
						} else if (h.getName().equals("ID")) {
							if (logger.isDebugEnabled()) {
								logger.debug("Setting parameter {} to {}", h.getName(), objectId);
							}
							task.setParameterValue("ID", objectId);
						} else if (h.getName().equals("USERID")) {
							if (logger.isDebugEnabled()) {
								logger.debug("Setting parameter {} to {}", h.getName(), User.getUser().getUserId());
							}
							task.setParameterValue("USERID", User.getUser().getUserId());
						} else {
							if (logger.isDebugEnabled()) {
								logger.debug("Setting parameter {} to {}", h.getName(), h.getField().getValue());
							}
							if (h.getField().isValid()) {
								task.setParameterValue(h.getName(), h.getField().getValue());
							} else {
								Notification.show("Parameter " + h.getName() + " is invalid.");
								return;
							}
						}
					}

				} else {

					task.setParameterValue(null, null);

				}

				try {

					String cacheDir = SystemProperties.get("report.cache");
					Path cachePath = Paths.get(cacheDir);
					if (!cachePath.toFile().exists()) {
						Files.createDirectories(cachePath);
					}

					File tf = null;
					RenderOption options = null;

					switch ((String) box.getValue()) {

					case "PDF":

						options = new PDFRenderOption();
						options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SWF;SVG");
						options.setOutputFormat("pdf");
						tf = Files.createTempFile(cachePath, "rpt", ".pdf").toFile();
						options.setOutputFileName(tf.getCanonicalPath());
						break;

					case "Excel":

						options = new EXCELRenderOption();
						((EXCELRenderOption) options).setEnableMultipleSheet(false);
						options.setOutputFormat("xlsx");
						((EXCELRenderOption) options).setWrappingText(true);
						tf = Files.createTempFile(cachePath, "rpt", ".xlsx").toFile();
						options.setOutputFileName(tf.getCanonicalPath());
						break;

					case "Word":

						options = new RenderOption();
						options.setOutputFormat("docx");
						options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SWF;SVG");
						tf = Files.createTempFile(cachePath, "rpt", ".docx").toFile();
						options.setOutputFileName(tf.getCanonicalPath());
						break;

					case "HTML":
						break;

					}

					saveRunHistory(tf);

					java.util.Date start = new java.util.Date();

					task.setRenderOption(options);
					task.run();
					task.close();

					if (logger.isDebugEnabled()) {
						logger.debug("Report took {} ms", new java.util.Date().getTime() - start.getTime());
					}

					if (emailAddress.getValue() != null && !"".equals(emailAddress.getValue())) {

						if ("HTML".equals((String) box.getValue())) {
							Notification.show("HTML Formatted output cannot be emailed");
						} else {

							String[] addresses = emailAddress.getValue().split(";");
							ReportEmailer emailer = new ReportEmailer(reportName, tf.getAbsolutePath());
							for (String address : addresses) {
								System.err.println("Emailing to address :" + address);
								emailer.setTo(address.trim());
							}
							emailer.send();

							close();

						}

					} else {

						if (logger.isDebugEnabled()) {
							logger.debug("Attempting to download {}", tf.getAbsolutePath());
						}

						Page.getCurrent().open(new FileResource(tf), "_blank", false);

						close();

					}

				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Could not create report output file", e);
					}
				} catch (EngineException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Could not execute report", e);
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("Unexpected Exception ", e);
					}
				}

			} else {

				if (emailAddress.getValue() == null) {
					Notification.show("Scheduled Reports must be emailed.");
				} else {
					saveScheduledReport();
					close();
				}
			}
		}
	}

	public enum ParameterTypes {
		TEXT_BOX, LIST_BOX
	}

	public enum DataTypes {
		STRING, FLOAT, DECIMAL, DATETIME, BOOLEAN, INTEGER, DATE
	}

	public class ParameterData {

		private String name;
		private String promptText;
		private boolean isRequired;
		private ParameterTypes parameterType;
		private DataTypes dataType;
		private String defaultValue;
		private boolean isHidden;

		@SuppressWarnings("rawtypes")
		private AbstractField field;

		@SuppressWarnings("rawtypes")
		private Collection selectionList;

		public ParameterData() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPromptText() {
			return promptText;
		}

		public void setPromptText(String promptText) {
			this.promptText = promptText;
		}

		public boolean isRequired() {
			return isRequired;
		}

		public void setRequired(boolean isRequired) {
			this.isRequired = isRequired;
		}

		public ParameterTypes getParameterType() {
			return parameterType;
		}

		public void setParameterType(ParameterTypes parameterType) {
			this.parameterType = parameterType;
		}

		public void setParameterType(int iscalarparameterdefn) {

			switch (iscalarparameterdefn) {
			case IScalarParameterDefn.TEXT_BOX:
				setParameterType(ParameterTypes.TEXT_BOX);
				break;

			case IScalarParameterDefn.LIST_BOX:
				setParameterType(ParameterTypes.LIST_BOX);
				break;

			case IScalarParameterDefn.RADIO_BUTTON:
				setParameterType(ParameterTypes.LIST_BOX);
				break;
			case IScalarParameterDefn.CHECK_BOX:
				setParameterType(ParameterTypes.LIST_BOX);
				break;

			default:
				setParameterType(ParameterTypes.TEXT_BOX);
				break;
			}
		}

		public DataTypes getDataType() {
			return dataType;
		}

		public void setDataType(DataTypes dataType) {
			this.dataType = dataType;
		}

		public void setDataType(int iscalardatatype) {

			// FOR OUR TEST REPORTS, BIRT IS ALWAYS SAYING IT's A STRING.

			switch (iscalardatatype) {
			case IParameterDefn.TYPE_STRING:
				System.err.println("Using String");
				setDataType(DataTypes.STRING);
				break;
			case IParameterDefn.TYPE_FLOAT:
				setDataType(DataTypes.FLOAT);
				break;
			case IParameterDefn.TYPE_DECIMAL:
				setDataType(DataTypes.DECIMAL);
				break;
			case IParameterDefn.TYPE_DATE_TIME:
				System.err.println("Using DateTime");
				setDataType(DataTypes.DATETIME);
				break;
			case IParameterDefn.TYPE_BOOLEAN:
				setDataType(DataTypes.BOOLEAN);
				break;
			case IParameterDefn.TYPE_INTEGER:
				setDataType(DataTypes.INTEGER);
				break;
			case IParameterDefn.TYPE_DATE:
				System.err.println("Using Date");
				setDataType(DataTypes.DATE);
				break;
			default:
				System.err.println("Using Default");
				setDataType(DataTypes.STRING);
				break;
			}

		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		@SuppressWarnings("rawtypes")
		public Collection getSelectionList() {
			return selectionList;
		}

		public void setSelectionList(Collection<IParameterSelectionChoice> selectionList) {

			ArrayList<String> results = new ArrayList<String>();
			for (IParameterSelectionChoice c : selectionList) {
				if (c.getValue() != null) {
					results.add((String) c.getValue());
				}
			}
			this.selectionList = results;

		}

		@SuppressWarnings("rawtypes")
		public AbstractField getField() {
			return field;
		}

		@SuppressWarnings("rawtypes")
		public void setField(AbstractField field) {
			this.field = field;
		}

		/**
		 * @return the isHidden
		 */
		public boolean isHidden() {
			return isHidden;
		}

		/**
		 * @param isHidden
		 *            the isHidden to set
		 */
		public void setHidden(boolean isHidden) {
			this.isHidden = isHidden;
		}

	}

	@SuppressWarnings("unchecked")
	private ParameterData loadParameterDetails(final IGetParameterDefinitionTask task, final IScalarParameterDefn scalar) {

		return new ParameterData() {
			{
				setName(scalar.getName());
				setPromptText(scalar.getPromptText());
				setRequired(scalar.isRequired());
				setParameterType(scalar.getControlType());
				setDataType(scalar.getDataType());
				setDefaultValue(scalar.getDefaultValue());
				if (scalar.getControlType() == IScalarParameterDefn.LIST_BOX) {
					setSelectionList(task.getSelectionList(scalar.getName()));
				}
				setHidden(scalar.isHidden());

				if (logger.isDebugEnabled()) {
					logger.debug("DataType = {} {}", scalar.getDataType(), getDataType());

				}
			}
		};
	}
}
