package edu.missouri.cf.projex4.scheduler.jobs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.sql.DataSource;

import oracle.jdbc.pool.OracleDataSource;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.x5.template.Chunk;
import com.x5.template.Theme;

import edu.missouri.operations.data.OracleHelper;
import edu.missouri.operations.data.OracleTimestamp;
import edu.missouri.operations.reportcenter.Pools;

public abstract class ProjexEmailerJob implements Job, ProjexJob {

	protected transient Logger logger = LoggerFactory.getLogger(ProjexEmailerJob.class);

	protected DataSource dataSource;
	protected OracleTimestamp start;

	Connection conn = null;

	protected void setConnection(Connection conn) {
		this.conn = conn;
	}

	protected void start() {
		if (logger.isDebugEnabled()) {
			logger.debug("{} executing.", this.getClass().getCanonicalName());
		}
		start = OracleTimestamp.now();
	}

	protected String server;
	protected String port;
	protected String protocol;
	protected String userName;
	protected String password;
	protected String from;
	private String subject;
	protected String body;
	protected String startTLS;
	protected String authenticationRequired;
	protected Map<String, String> imageMap;
	protected ArrayList<FilePair> fileList = new ArrayList<FilePair>();
	protected Stack<String> tos = new Stack<String>();

	/*
	 * final static String emailhead = "<!DOCTYPE html>" + "<html>" + "<head>"
	 * + "<link href=\"//fonts.googleapis.com/css?family=Quattrocento Sans&subset=latin\" rel=\"stylesheet\" type=\"text/css\">"
	 * + "</head>"
	 * + "<body style=\"font-family: 'Quattrocento Sans', Candara, Tahoma, sans-serif; border:1px solid #cccccc; padding: 0px; margin:6px;\">"
	 * +
	 * "<div id=\"header\" bgcolor=\"#000000\" style=\"background-color: black; height: 46px; margin-bottom: 48px; color: white; font-size: 2em; vertical-align: middle; padding-left: 6px\"><img src=\"cid:projexlogo\" align=\"middle\" alt=\"MU Logo\" style=\"padding-left: 4px\">Projex</div>"
	 * + "<div id=\"content\" style=\"margin-left: 6px; margin-right: 6px;\">";
	 * 
	 * final static String emailfoot = "</div>"
	 * +
	 * "<div id=\"footer\" style=\"margin-top: 48px; padding-left: 6px; padding-top: 14px; padding-bottom: 14px; font-size: 1em;\">Copyright &copy; 2015 &mdash; Curators of the University of Missouri. All rights reserved.  An equal opportunity/access/affirmative action/pro-disabled and veteran employer.</div>"
	 * + "</body>" + "</html>";
	 * 
	 */

	// TODO Change to system properties.
	protected String projexbg = "small-mosaic-black1.png";
	protected String projexlogo = "mulogotb.png";

	// protected ST template;
	protected Chunk template;
	protected Chunk head;
	protected Chunk footer;

	final static String templateDirectory = "/home/projex4/templates";

	protected static Theme theme = new Theme(templateDirectory, "");

	/**
	 * @param to
	 *            the to to set
	 */
	public void setTo(String to) {
		tos.add(to);
	}

	class SendMailAuthenticator extends Authenticator {

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(userName, password);
		}

	}

	class FilePair {

		String name;
		String location;

		public FilePair(String name, String location) {
			this.name = name;
			this.location = location;
		}

	}

	protected String getSystemProperty(String property) throws SQLException {

		String retval = null;

		try (PreparedStatement stmt = conn.prepareStatement("select * from properties where property = ? and refidtype = 'SYSTEM'")) {
			stmt.setString(1, property);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					retval = rs.getString("VALUE");
				} else {
					logger.info("Could not retrieve value for system property " + property);
				}
			}
		}

		return retval;
	}

	String logMessage;

	private Transport transport;

	private Session session;

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	public ProjexEmailerJob() {

		try {

			OracleDataSource odataSource = new OracleDataSource();
			odataSource.setURL(Pools.dbConnectionString);
			odataSource.setUser("projex4");
			odataSource.setPassword("prj4_user");
			dataSource = odataSource;

			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			setServer(getSystemProperty("email.server"));
			setProtocol(getSystemProperty("email.protocol"));
			setUserName(getSystemProperty("email.authentication.username"));
			setPassword(getSystemProperty("email.authentication.password"));
			setFrom(getSystemProperty("email.from"));
			setPort(getSystemProperty("email.server.port"));
			setStartTLS(getSystemProperty("email.starttls.enable"));
			setAuthenticationRequired(getSystemProperty("email.authentication.required"));

			if (logger.isDebugEnabled()) {
				logger.debug("Email Server = {}", getServer());
				logger.debug("Email Protocol = {}", getProtocol());
				logger.debug("Email User Name = {}", getUserName());
				logger.debug("Email User Password = {}", getPassword());
				logger.debug("From address = {}", getFrom());
			}

		} catch (SQLException e) {

			if (logger.isErrorEnabled()) {
				logger.error("Could not initialize ProjexEmailerJob", e);
			}

		}
	}

	public void setTemplate(String templateName) {

		template = theme.makeChunk();

		try {
			template.append(getTemplate(templateName));
		} catch (SQLException sqle) {

			if (logger.isErrorEnabled()) {
				logger.error("Could not load template", sqle);
			}

		}

		String imageBasePath = "/home/projex4/images/";
		// String background = imageBasePath + projexbg;
		String logo = imageBasePath + projexlogo;

		HashMap<String, String> imageMap = new HashMap<String, String>();
		imageMap.put("projexlogo", logo);
		setImageMap(imageMap);

		if (logger.isDebugEnabled()) {
			logger.debug("imageBasePath = {}", imageBasePath);
			logger.debug("logo = {}", logo);
		}

	}

	public void setBody() {

		head = theme.makeChunk("projexemail#header");
		footer = theme.makeChunk("projexemail#footer");

		String body = template.toString();

		System.err.println("Email Body = " + head.toString() + body + footer.toString());
		setBody(head.toString() + body + footer.toString());

	}

	public void set(String tag, String value) {
		template.set(tag, value);
	}

	public void set(Item item) {

		for (Object propertyId : item.getItemPropertyIds()) {
			if (item.getItemProperty(propertyId).getValue() != null) {
				set(propertyId.toString(), item.getItemProperty(propertyId).getValue().toString());
			}
		}

	}

	public enum Status {
		SUCCESS, FAILURE
	}

	public void success(String message) {
		if (logger.isDebugEnabled()) {
			logger.debug("{} execution successful - {}", this.getClass().getCanonicalName(), message);
		}
		addRecord(Status.SUCCESS, message);
	}

	public void success() {
		if (logger.isDebugEnabled()) {
			logger.debug("{} execution successful", this.getClass().getCanonicalName());
		}
		addRecord(Status.SUCCESS, "crontask executed successfully.");
	}

	public void failure(String message) {
		if (logger.isDebugEnabled()) {
			logger.debug("{} execution unsuccessful - {} ", this.getClass().getCanonicalName(), message);
		}
		addRecord(Status.FAILURE, message);
	}

	private void addRecord(Status status, String message) {

		if (conn != null) {

			try (CallableStatement call = conn.prepareCall("{ ? = call core.crontaskrun(?,?,?,?,?,?,?) }")) {

				int i = 1;
				call.registerOutParameter(i++, Types.VARCHAR);

				OracleHelper.setString(call, i++, null);
				OracleHelper.setString(call, i++, this.getClass().getCanonicalName());

				try {
					OracleHelper.setString(call, i++, InetAddress.getLocalHost().getHostAddress());
				} catch (UnknownHostException e) {
					OracleHelper.setString(call, i++, "Unknown Host");
				}

				OracleHelper.setTimestamp(call, i++, start);
				OracleHelper.setTimestamp(call, i++, OracleTimestamp.now());
				OracleHelper.setString(call, i++, status.name());
				OracleHelper.setString(call, i++, message);
				call.executeUpdate();

			} catch (SQLException sqle) {

				if (logger.isErrorEnabled()) {
					logger.error("Unable to add CronTaskRun", sqle);
				}

			}

		}

	}

	protected void emailLog(String emailAddress, String statusMessage) throws SQLException {

		try (java.sql.CallableStatement call = conn.prepareCall("{ ? = call EMAIL.EMAILLOG (?, ?, ?, ?) }")) {

			int i = 1;
			call.registerOutParameter(i++, java.sql.Types.VARCHAR);
			OracleHelper.setString(call, i++, null);
			OracleHelper.setString(call, i++, emailAddress);
			OracleHelper.setTimestamp(call, i++, OracleTimestamp.now());
			OracleHelper.setString(call, i++, statusMessage);

		}

	}

	public void connect() throws MessagingException {

		@SuppressWarnings("serial")
		Properties pros = new Properties() {
			{
				setProperty("mail.transport.protocol", getProtocol());
				setProperty("mail.smtp.host", getServer());
				setProperty("mail.smtp.port", getPort());
				setProperty("mail.smtp.auth", getAuthenticationRequired());
				setProperty("mail.smtp.starttls.enable", getStartTLS());
				setProperty("mail.smtp.ssl.trust", getServer());
			}
		};

		session = null;
		if ("true".equals(getAuthenticationRequired())) {

			if (logger.isDebugEnabled()) {
				logger.debug("Authentication is required.");
			}

			SendMailAuthenticator ma = new SendMailAuthenticator();
			session = Session.getInstance(pros, ma);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Authentication is not required.");
			}
			session = Session.getInstance(pros);
		}

		session.setDebug(true);

		transport = session.getTransport();
		transport.connect();

		if (logger.isDebugEnabled()) {
			logger.debug("Connected to smtp server");
		}

	}

	public void disconnect() throws MessagingException {

		transport.close();
		if (logger.isDebugEnabled()) {
			logger.debug("disconnected from smtp server");
		}

	}

	protected void send() throws MessagingException, SQLException {

		MimeMessage msg = createMessage(session);

		while (!tos.empty()) {

			String to = tos.pop();

			if (logger.isDebugEnabled()) {
				logger.debug("Beginning to send mail to {}", to);
			}

			try {

				msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
				transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));

				if (logger.isDebugEnabled()) {
					logger.debug("Should have sent email to {}", to);
				}

				emailLog(to, "SUCCESSFULLY TRANSFERRED TO RELAY");

				if (logger.isDebugEnabled()) {
					logger.debug("Should have stored log message {}", to);
				}

			} catch (MessagingException e) {
				emailLog(to, "ERROR IN CREATING MESSAGE " + e.getMessage());
				throw e;
			}

		}

	}

	protected MimeMessage createMessage(Session mailSession) throws MessagingException {

		return new MimeMessage(mailSession) {
			{
				setFrom(new InternetAddress(from));
				setSubject(subject);

				setContent(new MimeMultipart() {
					{
						addBodyPart(createContent());

						if (fileList != null) {
							for (final FilePair file : fileList) {
								addBodyPart(new MimeBodyPart() {
									{
										FileDataSource fds = new FileDataSource(file.location);
										setDataHandler(new DataHandler(fds));
										setFileName(file.name);
									}
								});
							}
						}
					}
				});

				saveChanges();
			}
		};

	}

	protected MimeBodyPart createContent() throws MessagingException {

		return new MimeBodyPart() {
			{
				setContent(new MimeMultipart("related") {
					{
						addBodyPart(new MimeBodyPart() {
							{
								setContent(getBody(), "text/html;charset=UTF-8");
							}
						});

						if (imageMap != null && imageMap.size() > 0) {

							Set<Entry<String, String>> set = imageMap.entrySet();
							for (final Entry<String, String> entry : set) {

								System.err.println("file = " + entry.getValue());

								final FileDataSource fds = new FileDataSource(entry.getValue());
								if (!fds.getFile().exists()) {
									System.err.println("file does not exist");
								}

								if (logger.isDebugEnabled()) {
									logger.debug("Content Type = {}", fds.getContentType());
								}

								addBodyPart(new MimeBodyPart() {
									{
										setDataHandler(new DataHandler(fds));
										setContentID(entry.getKey());
									}
								});

							}

						}
					}
				});
			}
		};
	}

	public String getTemplate(String emailName) throws SQLException {

		String template = null;

		try (PreparedStatement stmt = conn.prepareStatement("select content, emailsubject from emailtemplates where emailname = ?")) {
			OracleHelper.setString(stmt, 1, emailName);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					template = rs.getString(1);
					setSubject(rs.getString(2));
				}
			}
		}

		return template;

	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		start();

		try {

			System.err.println("Starting execution of ProjexEmailerJob");

			if (conn != null) {

				execute(context, conn);

				if (logMessage != null) {
					success(logMessage);
				} else {
					success();
				}

				conn.commit();
				conn.close();
				
			} else {
				failure("Could not make database connection.");
			}

		} catch (Exception e) {

			e.printStackTrace();

			if (logger.isErrorEnabled()) {
				logger.error("Error executing crontask", e);
			}

			try {

				if (conn != null) {
					conn.rollback();
				}

			} catch (SQLException sqle) {

				if (logger.isErrorEnabled()) {
					logger.error("Could not rollback transactions", sqle);
				}

			}

			failure(e.getMessage());

			try {

				if (conn != null) {
					conn.close();
				}

			} catch (SQLException sqle) {
				if (logger.isErrorEnabled()) {
					logger.error("Unable to close connection", sqle);
				}
			}

			throw new JobExecutionException(e.getMessage());

		}

	}

	@Override
	public void executeNow() {

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				try {
					execute(null);
				} catch (JobExecutionException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Unable to run cron task manually", e);
					}

				}
			}
		};

		new Thread(runnable).start();

	}

	public abstract void execute(JobExecutionContext context, Connection conn) throws JobExecutionException, SQLException, MessagingException;

	/**
	 * @return the imageMap
	 */
	public Map<String, String> getImageMap() {
		return imageMap;
	}

	/**
	 * @param imageMap
	 *            the imageMap to set
	 */
	public void setImageMap(Map<String, String> imageMap) {
		this.imageMap = imageMap;
	}

	/**
	 * @return the server
	 */
	protected String getServer() {
		return server;
	}

	/**
	 * @param server
	 *            the server to set
	 */
	protected void setServer(String server) {
		this.server = server;
	}

	/**
	 * @return the port
	 */
	protected String getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	protected void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return the protocol
	 */
	protected String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol
	 *            the protocol to set
	 */
	protected void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @return the userName
	 */
	protected String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	protected void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	protected String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	protected void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the startTLS
	 */
	protected String getStartTLS() {
		return startTLS;
	}

	/**
	 * @param startTLS
	 *            the startTLS to set
	 */
	protected void setStartTLS(String startTLS) {
		this.startTLS = startTLS;
	}

	/**
	 * @return the authenticationRequired
	 */
	protected String getAuthenticationRequired() {
		return authenticationRequired;
	}

	/**
	 * @param authenticationRequired
	 *            the authenticationRequired to set
	 */
	protected void setAuthenticationRequired(String authenticationRequired) {
		this.authenticationRequired = authenticationRequired;
	}

	/**
	 * @return the from
	 */
	protected String getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	protected void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @return the body
	 */
	protected String getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	protected void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the fileList
	 */
	public ArrayList<FilePair> getFileList() {
		return fileList;
	}

	protected void setFileList(ArrayList<FilePair> fileList) {
		this.fileList = fileList;
	}

	public void setFileList(List<String> fileList) {
		this.fileList.clear();
		for (String file : fileList) {
			addFile(file);
		}
	}

	public void addFile(String fileName, String fileLocation) {
		fileList.add(new FilePair(fileName, fileLocation));
	}

	public void addFile(String fileLocation) {
		fileList.add(new FilePair(fileLocation, fileLocation));
	}

}
