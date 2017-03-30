/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop.reports;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import org.slf4j.Logger;

import c10n.C10N;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

import edu.missouri.cf.data.OracleBoolean;
import edu.missouri.cf.data.OracleString;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.common.PersonDetails;
import edu.missouri.cf.projex4.data.reports.ReportListings;
import edu.missouri.cf.projex4.data.reports.Reports;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.properties.SystemProperties;
import edu.missouri.cf.projex4.ui.common.OracleBooleanCheckBox;
import edu.missouri.cf.projex4.ui.common.OraclePopupDateField;
import edu.missouri.cf.projex4.ui.common.OracleStringTextArea;
import edu.missouri.cf.projex4.ui.common.system.DefaultFormEditControls;
import edu.missouri.cf.projex4.ui.common.system.StandardComboBox;
import edu.missouri.cf.projex4.ui.desktop.StandardEditorView;
import edu.missouri.cf.projex4.ui.desktop.configuration.campuses.CampusIdComboBox;
import edu.missouri.cf.projex4.ui.desktop.documents.ProjexFileDetailBean;
import edu.missouri.cf.projex4.ui.desktop.documents.ProjexUploadFinishedHandler;
import edu.missouri.cf.projex4.ui.desktop.lookups.ApplicationComboBox;
import edu.missouri.cf.projex4.ui.desktop.lookups.persons.UserPersonField;
import edu.missouri.operations.reportcenter.ui.c10n.configuration.ReportEditorText;

/**
 * @author graumannc
 */

@SuppressWarnings("serial")
public class ReportEditorView extends StandardEditorView {

	private static Logger logger = Loggers.getLogger(ReportEditorView.class);

	private ReportEditorText st;

	@PropertyId("ID")
	private TextField reportId;

	@PropertyId("REPORTNAME")
	private TextField reportName;

	@PropertyId("DESCRIPTION")
	private OracleStringTextArea description;

	@PropertyId("CAMPUSID")
	private CampusIdComboBox campusId;

	@PropertyId("ISALLCAMPUS")
	private OracleBooleanCheckBox isAllCampus;

	@PropertyId("ISACTIVE")
	private OracleBooleanCheckBox isActive;

	@PropertyId("ISSCREENREPORT")
	private OracleBooleanCheckBox isScreenReport;

	@PropertyId("SCREENNAME")
	private ApplicationComboBox screenName;

	@PropertyId("HELPTEXT")
	private TextField helpTopicId;

	@PropertyId("REQUESTED")
	private OraclePopupDateField requested;

	@PropertyId("REQUESTEDBY")
	private UserPersonField requestedBy;

	@PropertyId("REASON")
	private OracleStringTextArea requestReason;

	@PropertyId("REGISTEREDBY")
	private UserPersonField registeredBy;

	@PropertyId("REGISTERED")
	private TextField registered;

	@PropertyId("CATEGORY")
	private StandardComboBox category;

	private ReportParametersComponent reportParameters;

	private ReportRunHistoryComponent reportRunHistory;

	private ReportRunHistoryParametersComponent reportRunHistoryParameters;

	private ReportModificationHistoryComponent reportModificationHistory;

	private Reports query;

	private OracleContainer sqlContainer;

	@PropertyId("FILENAME")
	private TextField fileName;

	private MultiFileUpload fileUpload;

	private SecurityGroupReportsComponent securityGroupReports;
	
	@PropertyId("ISSLOWRUNNING")
	private OracleBooleanCheckBox slowRunning;

	/**
	 * 
	 */
	public ReportEditorView() {
		super();
	}

	@Override
	public void attach() {
		super.attach();
		init();
		layout();
	}

	private void init() {

		st = C10N.get(ReportEditorText.class, User.getUser().getUserLocale());

		controls = new DefaultFormEditControls();
		controls.setEditor(this);

		controls.setApplicationName(ProjexViewProvider.Views.REPORTEDITOR.name());

		reportId = new TextField() {
			{
				setCaption(st.reportId());
				setDescription(st.reportId_help());
				setImmediate(true);
			}
		};

		reportName = new TextField() {
			{
				setCaption(st.reportName());
				setDescription(st.reportName_help());
				setRequired(true);
				setNullRepresentation("");
				setWidth("100%");
				setImmediate(true);
			}
		};

		campusId = new CampusIdComboBox() {
			{
				setCaption(st.campus());
				setDescription(st.campus_help());
				setImmediate(true);

				addValueChangeListener(new Property.ValueChangeListener() {

					@Override
					public void valueChange(Property.ValueChangeEvent event) {

						if (!isReadOnly()) {
							if (getValue() != null) {
								logger.debug("Value = {}, ConvertedValue = {}", getValue(), getConvertedValue());
								isAllCampus.setConvertedValue(OracleBoolean.FALSE);
							} else {
								isAllCampus.setConvertedValue(OracleBoolean.TRUE);
							}
						}

					}
				});
			}
		};

		isAllCampus = new OracleBooleanCheckBox() {
			{
				setCaption(st.isAllCampus());
				setDescription(st.isAllCampus_help());
				setImmediate(true);

				// No listener here because of possibility of circular firing.

			}
		};

		isActive = new OracleBooleanCheckBox() {
			{
				setCaption(st.isActive());
				setDescription(st.isActive_help());
				setImmediate(true);
			}
		};

		isScreenReport = new OracleBooleanCheckBox() {
			{
				setCaption(st.isScreenReport());
				setDescription(st.isScreenReport_help());
				setImmediate(true);
			}
		};

		screenName = new ApplicationComboBox() {
			{
				setRequired(true);
			}
		};

		// TODO fileName should be a custom field with the button, so we can
		// mark it required.

		fileName = new TextField() {
			{
				setDescription(st.fileName_help());
				setNullRepresentation("");
				setRequired(true);
				setImmediate(true);
			}
		};

		// TODO Change to help topic id lookup
		helpTopicId = new TextField() {
			{
				setCaption(st.helpText());
				setDescription(st.helpText_help());
				setNullRepresentation("");
				setImmediate(true);
			}
		};

		description = new OracleStringTextArea() {
			{
				setWidth("100%");
				setCaption(st.description());
				setDescription(st.description_help());
				setRequired(true);
				setNullRepresentation("");
				setImmediate(true);
			}
		};

		requested = new OraclePopupDateField() {
			{
				setCaption(st.modification().requested());
				setDescription(st.modification().requested_help());
				setRequired(true);
				setImmediate(true);
			}
		};

		requestedBy = new UserPersonField(PersonDetails.Type.PERSON) {
			{
				setCaption(st.modification().requestedBy());
				setDescription(st.modification().requestedBy_help());
				setRequired(true);
				setImmediate(true);
			}
		};

		requestReason = new OracleStringTextArea() {
			{
				setWidth("100%");
				setCaption(st.modification().reason());
				setDescription(st.modification().reason_help());
				setRequired(true);
				setImmediate(true);
			}
		};

		registeredBy = new UserPersonField(PersonDetails.Type.USER) {
			{
				setCaption(st.modification().registeredBy());
				setDescription(st.modification().registeredBy_help());
				setImmediate(true);
			}
		};

		registered = new TextField() {
			{
				setCaption(st.modification().registered());
				setDescription(st.modification().registered_help());
				setNullRepresentation("");
				setImmediate(true);
			}
		};

		category = new StandardComboBox() {
			{
				setCaption(st.category());
				setDescription(st.category_help());
				setListName("reports.category");
				refreshDataCollection();
			}
		};

		UploadStateWindow window = new UploadStateWindow() {
			{
				setWindowPosition(WindowPosition.TOP_RIGHT);
			}
		};

		ReportFileUploadFinishedHandler handler = new ReportFileUploadFinishedHandler();

		fileUpload = new MultiFileUpload(handler, window, false) {
			{
				getSmartUpload().setUploadButtonCaptions("", st.uploadButtonText_help());
				// setDescription(st.uploadButtonText_help());
				getSmartUpload().addStyleName("borderless");
				getSmartUpload().setUploadButtonIcon(new ThemeResource("icons/chalkwork/basic/arrow_up_16x16.png"));
				addStyleName("myupload");
				setWidth("130px");
				setImmediate(true);
			}
		};

		reportParameters = new ReportParametersComponent() {
			{
				setApplicationName(ProjexViewProvider.Views.REPORTEDITOR);
				setSubApplicationName("REPORTPARAMETERS");
			}
		};

		reportRunHistory = new ReportRunHistoryComponent();
		reportRunHistoryParameters = new ReportRunHistoryParametersComponent();
		reportModificationHistory = new ReportModificationHistoryComponent();

		securityGroupReports = new SecurityGroupReportsComponent() {
			{
				setApplicationName(ProjexViewProvider.Views.REPORTEDITOR);
				setSubApplicationName("REPORTSECURITY");
			}
		};
		
		slowRunning = new OracleBooleanCheckBox() {
			{
				setCaption(st.slowRunning());
				setDescription(st.slowRunning_help());
			}
		};

		clearComponents();

		addNonEditableComponent(reportId);
		addEditableComponent(reportName);
		addEditableComponent(campusId);
		addEditableComponent(isAllCampus);
		addEditableComponent(requested);
		addEditableComponent(requestedBy);
		addEditableComponent(requestReason);
		addEditableComponent(isActive);
		addEditableComponent(isScreenReport);
		addEditableComponent(screenName);
		addEditableComponent(helpTopicId);
		addEditableComponent(description);
		addEditableComponent(category);
		addEditableComponent(slowRunning);
		addNonEditableComponent(fileName);
		addNonEditableComponent(registered);
		addNonEditableComponent(registeredBy);

		addDependentProjexEditor(reportParameters);
		addDependentProjexEditor(securityGroupReports);

	}

	public class ReportFileUploadFinishedHandler implements ProjexUploadFinishedHandler {

		private String failedReason;
		private ProjexFileDetailBean.Status status;
		private String objectId;

		private String reportRoot;

		public void setReportRoot(String reportRoot) {
			this.reportRoot = reportRoot;
		}

		public ReportFileUploadFinishedHandler() {
			setReportRoot(SystemProperties.get("report.root"));
		}

		@Override
		public void handleFile(InputStream stream, String uploadFileName, String mimeType, long length) {

			if (logger.isDebugEnabled()) {
				logger.debug("Uploaded File = {}.  MimeType = {}. Length = {}. ", uploadFileName, mimeType, length);
			}

			if ("application/octet-stream".equals(mimeType) || "application/vnd.birt-rptdesign".equals(mimeType)) {

				if (logger.isDebugEnabled()) {
					logger.debug("BIRT Report");
				}

				try {

					String reportDir = SystemProperties.get("report.root");
					Path reportPath = Paths.get(reportDir);
					if (!reportPath.toFile().exists()) {
						Files.createDirectory(reportPath);
					}

					String fileTitle = uploadFileName.substring(0, uploadFileName.indexOf(".rptdesign"));
					Path path = Files.createTempFile(reportPath, fileTitle + ".", ".rptdesign");
					Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);

					if (logger.isDebugEnabled()) {
						logger.debug("Uploaded file saved to {}", path.toAbsolutePath());
					}

					fileName.setReadOnly(false);
					fileName.setValue(path.getFileName().toString());
					fileName.setReadOnly(true);

				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Could not save uploaded file", e);
					}
				}

			} else {

				if (logger.isDebugEnabled()) {
					logger.debug("Not a BIRT Report");
				}

			}

		}

		@Override
		public void handleUploadStatus(ProjexFileDetailBean fileDetailBean) {
			fileDetailBean.setObjectId(objectId);
			fileDetailBean.setFailedReason(failedReason);
			fileDetailBean.setStatus(status);
		}

	}

	private void layout() {

		TabSheet tabs = new TabSheet() {
			{
				addStyleName("tabs");

				addTab(new VerticalLayout() {
					{
						setCaption(st.tab1());
						setMargin(true);
						setSpacing(true);
						setSizeFull();

						addComponent(controls);

						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								setWidth("100%");
								addComponent(reportId);
								addComponent(reportName);
								setExpandRatio(reportName, 1.0f);
							}
						});

						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(requested);
								addComponent(requestedBy);
								addComponent(registered);
								addComponent(registeredBy);
							}
						});

						addComponent(new HorizontalLayout() {
							{
								setCaption(st.fileName());
								fileName.setRequired(true);
								setWidth("100%");
								fileName.setWidth("100%");
								addComponent(fileName);
								setExpandRatio(fileName, 1.0f);
								addComponent(new CssLayout() {
									{
										addComponent(new HorizontalLayout() {
											{
												addStyleName("uploadbuttonlifter");
												addComponent(fileUpload);
											}
										});
									}
								});
							}
						});

						addComponent(requestReason);

						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(campusId);
								addComponent(isAllCampus);
								addComponent(isActive);
								addComponent(isScreenReport);
								addComponent(screenName);
								addComponent(category);
								addComponent(helpTopicId);
								addComponent(slowRunning);
							}
						});

						addComponent(description);
						addComponent(reportParameters);
						addComponent(securityGroupReports);

					}
				});

				addTab(new VerticalLayout() {
					{
						setCaption(st.tab2());
						setMargin(true);
						setSpacing(true);
						setSizeFull();
						addComponent(reportRunHistory);
						setExpandRatio(reportRunHistory, 1.0f);
						addComponent(reportRunHistoryParameters);
					}
				});

				addTab(new VerticalLayout() {
					{
						setCaption(st.tab3());
						setMargin(true);
						setSpacing(true);
						setSizeFull();
						addComponent(reportModificationHistory);
						reportModificationHistory.getTable().setHeight("100%");
						setExpandRatio(reportModificationHistory, 1.0f);

					}
				});

			}
		};

		addComponent(tabs);
		setExpandRatio(tabs, 1.0f);

	}

	String id;

	@Override
	public void setScreenData(String parameters) {

		id = parameters;
		
		setObjectId(id);

		query = new Reports();
		query.setId(parameters);

		try {
			sqlContainer = new OracleContainer(query);
			sqlContainer.overrideType("CAMPUSID", OracleString.class);
			sqlContainer.overrideType("REQUESTEDBY", OracleString.class);
			sqlContainer.overrideType("REGISTEREDBY", OracleString.class);

			Item item = sqlContainer.getItemByProperty("ID", parameters);
			bind(item);
			clearOracleContainers();
			addOracleContainer(sqlContainer);

		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Could not set data", e);
			}
		}

		controls.setEditingState(EditingState.READONLY);

		reportParameters.setData(parameters);
		reportModificationHistory.setData(parameters);
		reportRunHistory.setData(parameters);
		securityGroupReports.setData(parameters);

		/* Don't have object to check if canDo */
		if (User.canDo(ProjexViewProvider.Views.REPORTEDITOR, "EDIT")) {
			controls.setEnabled(true);
		} else {
			controls.setEnabled(false);
		}

	}

	@Override
	public void afterCommit() {
		ReportListings.refreshView();
		setScreenData(id);
	}

}
