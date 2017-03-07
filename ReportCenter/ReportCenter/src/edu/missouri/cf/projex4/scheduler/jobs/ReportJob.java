package edu.missouri.cf.projex4.scheduler.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.mail.MessagingException;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefn;
import org.eclipse.birt.report.engine.api.IParameterSelectionChoice;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractField;

import edu.missouri.operations.data.OracleHelper;

/*
 * TODO Should delete onetime scheduled crontasks from table after running them.
 */

public class ReportJob extends ProjexEmailerJob {

	private IReportEngine reportEngine = null;
	private IRunAndRenderTask task = null;
	private IReportRunnable design = null;
	IScalarParameterDefn scalar = null;
	IGetParameterDefinitionTask p_task;

	private String reportCronTaskId = null;
	private String reportId;
	private String scheduledBy;
	private String fileFormat;

	private boolean oneTime;

	public void setOneTime(boolean oneTime) {
		this.oneTime = oneTime;
	}

	public void setReportCronTaskId(String reportCrontTaskId) {
		this.reportCronTaskId = reportCrontTaskId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public void setScheduledBy(String scheduledBy) {
		this.scheduledBy = scheduledBy;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	private String reportName;
	private String fileName;

	ArrayList<ParameterData> holders;

	public ReportJob() {

	}

	class ReportCronTaskParameter {
		String parameterType;
		String parameterName;
		String parameterValue;

		public ReportCronTaskParameter(String parameterType, String parameterName, String parameterValue) {
			this.parameterType = parameterType;
			this.parameterName = parameterName;
			this.parameterValue = parameterValue;
		}

	}

	HashMap<String, ReportCronTaskParameter> reportCronTaskParameters = new HashMap<String, ReportCronTaskParameter>();
	ArrayList<String> emailAddresses = new ArrayList<String>();

	public String storeRunParameters(Item row) throws SQLException {

		logger.debug("storeRow");

		try (CallableStatement call = conn.prepareCall("{ ? = call report.reportrunparameter(?,?,?,?,?) }")) {

			call.registerOutParameter(1, Types.VARCHAR);
			OracleHelper.setString(call, 2, OracleHelper.getString(row, "ID"));
			OracleHelper.setString(call, 3, OracleHelper.getString(row, "REPORTRUNHISTORYID"));
			OracleHelper.setBigDecimal(call, 4, OracleHelper.getOracleDecimal(row, "PARAMETERNUMBER"));
			OracleHelper.setString(call, 5, OracleHelper.getString(row, "PARAMETERNAME"));
			OracleHelper.setString(call, 6, OracleHelper.getString(row, "PARAMETERVALUE"));

			call.executeUpdate();
			return call.getString(1);
		}
	}

	public void saveRunHistory(Connection conn, File tf) {

		System.err.println("Saving run history");

		try {

			String runHistoryId = null;

			try (CallableStatement call = conn.prepareCall("{ ? = call report.reportrunhistory(?,?,?,?) }")) {

				int i = 1;
				call.registerOutParameter(i++, Types.VARCHAR);
				OracleHelper.setString(call, i++, scheduledBy);
				OracleHelper.setString(call, i++, reportId);
				OracleHelper.setString(call, i++, fileFormat);
				OracleHelper.setString(call, i++, tf.getAbsolutePath());
				call.executeUpdate();
				runHistoryId = call.getString(1);

				if (holders != null) {

					try (PreparedStatement stmt = conn.prepareStatement(
							"insert into reportrunparameters (ID, REPORTRUNHISTORYID, PARAMETERNUMBER, PARAMETERNAME, PARAMETERVALUE) values (reportrunparametersseq.nextval, ?,?,?,?) ")) {
						
						int x = 1;
						for (ParameterData h : holders) {

							stmt.setString(1, runHistoryId);
							stmt.setInt(2, x);
							x++;
							stmt.setString(3, h.getName());
							stmt.setString(4, h.getValue()!=null ? h.getValue().toString() : "null");
							stmt.addBatch();
						}
						
						stmt.executeBatch();
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Unable to save History", e);
		}

	}

	@Override
	public void executeNow() {
		throw new UnsupportedOperationException("ReportJobs cannot be manually run.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext context, Connection conn)
			throws JobExecutionException, SQLException, MessagingException {

		System.err.println("ReportJob starting execution");
		System.err.println("ReportId = " + reportId);

		try {

			JobKey key = context.getJobDetail().getKey();
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();

			try (PreparedStatement stmt = conn.prepareStatement("select * from reports where id = ?")) {

				stmt.setString(1, reportId);
				try (ResultSet rs = stmt.executeQuery()) {

					if (rs.next()) {

						reportName = rs.getString("REPORTNAME");

						System.err.println("reportName = " + reportName);

						if (logger.isDebugEnabled()) {
							logger.debug("reportName = {}", reportName);
						}

						fileName = rs.getString("FILENAME");

						System.err.println("fileName = " + fileName);
						if (logger.isDebugEnabled()) {
							logger.debug("fileName = {}", fileName);
						}

					} else {

						throw new JobExecutionException("ReportId does not retrieve report information");

					}
				}
			}

			try (PreparedStatement stmt = conn.prepareStatement(
					"select * from reportcrontaskparameters where reportcrontaskid = ? order by parameternumber")) {

				stmt.setString(1, reportCronTaskId);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {

						System.err.println("Report Paramenter " + rs.getString("PARAMETERNAME") + " is of "
								+ rs.getString("PARAMETERTYPE") + " = " + rs.getString("PARAMETERVALUE"));

						reportCronTaskParameters.put(rs.getString("PARAMETERNAME"), new ReportCronTaskParameter(
								rs.getString("PARAMETERTYPE"), rs.getString("PARAMETERNAME"), rs.getString("PARAMETERVALUE")));
					}
				}
			}

			if (reportCronTaskParameters.size() == 0) {
				throw new JobExecutionException("Cannot run report - No usuable parameters");
			}

			try (PreparedStatement stmt = conn.prepareStatement("select * from reportcrontaskemails where reportcrontaskid = ?")) {
				stmt.setString(1, reportCronTaskId);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {

						System.err.println("emailAddress = " + rs.getString("EMAILADDRESS"));
						emailAddresses.add(rs.getString("EMAILADDRESS"));

					}
				}
			}

			if (emailAddresses.size() == 0) {
				throw new JobExecutionException("Cannot run report - No usuable email addresses");
			}

			if ("HTML".equals(fileFormat)) {
				throw new JobExecutionException("Cannot email report - HTML Format");
			}

			EngineConfig config = new EngineConfig();
			config.setLogConfig("/home/projex4/reports.log", java.util.logging.Level.SEVERE);
			IReportEngine reportEngine = null;

			Platform.startup(config);
			IReportEngineFactory factory = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);

			reportEngine = factory.createReportEngine(config);
			// reportEngine.setLogger(logger);

			String reportFileName = getSystemProperty("report.root") + "/" + fileName;
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

			p_task.setParameterValue("USERID", scheduledBy);

			Collection<IScalarParameterDefn> params = p_task.getParameterDefns(true);

			if (params != null && !params.isEmpty()) {

				ParameterData parmDetails;

				holders = new ArrayList<ParameterData>();

				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

				System.err.println("Loading Parameters into ReportEngine");

				for (IScalarParameterDefn scalar : params) {

					parmDetails = loadParameterDetails(p_task, scalar);

					if ("USERID".equals(parmDetails.getName())) {
						task.setParameterValue("USERID", scheduledBy);
						parmDetails.setValue(scheduledBy);
					} else if (!reportCronTaskParameters.containsKey(parmDetails.getName())) {
						throw new JobExecutionException("Parameter " + parmDetails.getName() + " not present in saved parameters.");

					} else {

						switch (parmDetails.getParameterType()) {

						case LIST_BOX:

							if (logger.isDebugEnabled()) {
								logger.debug("LIST_BOX");
							}
							task.setParameterValue(parmDetails.getName(),
									reportCronTaskParameters.get(parmDetails.getName()).parameterValue);

							parmDetails.setValue(reportCronTaskParameters.get(parmDetails.getName()).parameterValue);
							break;

						case TEXT_BOX:
							if (logger.isDebugEnabled()) {
								logger.debug("TEXT_BOX {}", parmDetails.getDataType());
								if (parmDetails.getDataType() == null) {
									logger.debug("getDataType returns null");
								}
							}

							switch (parmDetails.getDataType()) {

							case DATE:

								if (logger.isDebugEnabled()) {
									logger.debug("Date Field");
								}
								java.util.Date d = dateFormat
										.parse(reportCronTaskParameters.get(parmDetails.getName()).parameterValue);
								task.setParameterValue(parmDetails.getName(), d);
								parmDetails.setValue(d);
								break;

							case DATETIME:

								if (logger.isDebugEnabled()) {
									logger.error("Set Date Time Field");
								}

								java.util.Date dt = dateTimeFormat
										.parse(reportCronTaskParameters.get(parmDetails.getName()).parameterValue);
								task.setParameterValue(parmDetails.getName(), dt);
								parmDetails.setValue(dt);
								break;

							case DECIMAL:
							case FLOAT:

								// TODO - Future Enhancement - Add better/
								// support for Currency.

								task.setParameterValue(parmDetails.getName(),
										new Double(reportCronTaskParameters.get(parmDetails.getName()).parameterValue));
								parmDetails.setValue(reportCronTaskParameters.get(parmDetails.getName()).parameterValue);
								break;

							case STRING:
								task.setParameterValue(parmDetails.getName(),
										reportCronTaskParameters.get(parmDetails.getName()).parameterValue);
								parmDetails.setValue(reportCronTaskParameters.get(parmDetails.getName()).parameterValue);
								break;

							case BOOLEAN:

								// TODO - Not currently used in P4 - but should
								// probably add handling for more universal use.

								task.setParameterValue(parmDetails.getName(), new Boolean(
										"1".equals(reportCronTaskParameters.get(parmDetails.getName()).parameterValue) || "true"
												.equals(reportCronTaskParameters.get(parmDetails.getName()).parameterValue)));
								parmDetails.setValue(reportCronTaskParameters.get(parmDetails.getName()).parameterValue);
								break;

							case INTEGER:
								task.setParameterValue(parmDetails.getName(),
										new Integer(reportCronTaskParameters.get(parmDetails.getName()).parameterValue));
								parmDetails.setValue(reportCronTaskParameters.get(parmDetails.getName()).parameterValue);
								break;

							default:
								break;

							}
							break;
						}
					}

					holders.add(parmDetails);

				}

			} else {
				task.setParameterValue(null, null);
			}

			p_task.close();

			String cacheDir = getSystemProperty("report.cache");
			Path cachePath = Paths.get(cacheDir);
			if (!cachePath.toFile().exists()) {
				Files.createDirectories(cachePath);
			}

			File tf = null;
			RenderOption options = null;

			switch (fileFormat) {

			case "PDF":

				System.err.println("PDF Format");
				options = new PDFRenderOption();
				options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SWF;SVG");
				options.setOutputFormat("pdf");
				tf = Files.createTempFile(cachePath, "rpt", ".pdf").toFile();
				break;

			case "Excel":

				System.err.println("Excel Format");
				options = new EXCELRenderOption();
				((EXCELRenderOption) options).setEnableMultipleSheet(false);
				options.setOutputFormat("xlsx");
				((EXCELRenderOption) options).setWrappingText(true);
				tf = Files.createTempFile(cachePath, "rpt", ".xlsx").toFile();
				break;

			case "Word":

				System.err.println("Word Format");
				options = new RenderOption();
				options.setOutputFormat("docx");
				options.setSupportedImageFormats("PNG;GIF;JPG;BMP;SWF;SVG");
				tf = Files.createTempFile(cachePath, "rpt", ".docx").toFile();
				break;

			}
			
			options.setOutputFileName(tf.getCanonicalPath());

			task.setRenderOption(options);
			task.run();
			task.close();
			System.err.println("Successfully generated report");

			saveRunHistory(conn, tf);

			System.err.println("Trying to email report.");

			setTemplate("REPORT");
			set("REPORTTITLE", reportName);
			setBody();
			setSubject("Report Generated");
			addFile(tf.getAbsolutePath());

			connect();

			for (String address : emailAddresses) {
				System.err.println("Sending Report to " + address);
				setTo(address);
			}

			send();
			disconnect();

			conn.commit();

			System.err.println("Should have sent report.");

		} catch (IOException e) {
			throw new JobExecutionException("Could not create report output file " + e.getMessage());
		} catch (ParseException e) {
			throw new JobExecutionException("Unable to parse date or datetime parameter " + e.getMessage());
		} catch (BirtException e) {
			throw new JobExecutionException("Could not execute report " + e.getMessage());
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
		private Object value;

		@SuppressWarnings("rawtypes")
		private AbstractField field;

		@SuppressWarnings("rawtypes")
		private Collection selectionList;

		public ParameterData() {
		}

		public String getName() {
			return name;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
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
				if (logger.isDebugEnabled()) {
					logger.debug("Using String");
				}
				setDataType(DataTypes.STRING);
				break;
			case IParameterDefn.TYPE_FLOAT:
				setDataType(DataTypes.FLOAT);
				break;
			case IParameterDefn.TYPE_DECIMAL:
				setDataType(DataTypes.DECIMAL);
				break;
			case IParameterDefn.TYPE_DATE_TIME:
				if (logger.isDebugEnabled()) {
					logger.debug("Using DateTime");
				}
				setDataType(DataTypes.DATETIME);
				break;
			case IParameterDefn.TYPE_BOOLEAN:
				setDataType(DataTypes.BOOLEAN);
				break;
			case IParameterDefn.TYPE_INTEGER:
				setDataType(DataTypes.INTEGER);
				break;
			case IParameterDefn.TYPE_DATE:
				if (logger.isDebugEnabled()) {
					logger.debug("Using Date");
				}
				setDataType(DataTypes.DATE);
				break;
			default:
				if (logger.isDebugEnabled()) {
					logger.debug("Using Default");
				}
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
