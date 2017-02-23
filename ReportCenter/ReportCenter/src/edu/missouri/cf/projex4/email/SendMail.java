package edu.missouri.cf.projex4.email;

import java.sql.SQLException;
import java.util.ArrayList;
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
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

import edu.missouri.cf.projex4.data.system.core.email.EmailLogs;
import edu.missouri.operations.data.system.properties.SystemProperties;

public class SendMail {

	protected final static transient Logger logger = LoggerFactory.getLogger(SendMail.class);

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

	private java.sql.Connection conn;

	public void setDatabaseConnection(java.sql.Connection conn) {
		this.conn = conn;
	}

	private String server;
	private String port;
	private String protocol;
	private String userName;
	private String password;
	private String from;
	private String subject;
	private String body;
	private String startTLS;
	private String authenticationRequired;

	private Map<String, String> imageMap;
	private ArrayList<FilePair> fileList = new ArrayList<FilePair>();

	public SendMail() {

		setServer(SystemProperties.get("email.server"));
		setProtocol(SystemProperties.get("email.protocol"));
		setUserName(SystemProperties.get("email.authentication.username"));
		setPassword(SystemProperties.get("email.authentication.password"));
		setFrom(SystemProperties.get("email.from"));
		setPort(SystemProperties.get("email.server.port"));
		setStartTLS(SystemProperties.get("email.starttls.enable"));
		setAuthenticationRequired(SystemProperties.get("email.authentication.required"));

		if (logger.isDebugEnabled()) {
			logger.debug("Email Server = {}", getServer());
			logger.debug("Email Protocol = {}", getProtocol());
			logger.debug("Email User Name = {}", getUserName());
			logger.debug("Email User Password = {}", getPassword());
			logger.debug("From address = {}", getFrom());
		}

	}

	public SendMail(java.sql.Connection conn) {

		setDatabaseConnection(conn);
		setServer(SystemProperties.get(conn, "email.server"));
		setProtocol(SystemProperties.get(conn, "email.protocol"));
		setUserName(SystemProperties.get(conn, "email.authentication.username"));
		setPassword(SystemProperties.get(conn, "email.authentication.password"));
		setFrom(SystemProperties.get(conn, "email.from"));
		setPort(SystemProperties.get(conn, "email.server.port"));
		setStartTLS(SystemProperties.get(conn, "email.starttls.enable"));
		setAuthenticationRequired(SystemProperties.get(conn, "email.authentication.required"));

		if (logger.isDebugEnabled()) {
			logger.debug("Email Server = {}", getServer());
			logger.debug("Email Protocol = {}", getProtocol());
			logger.debug("Email User Name = {}", getUserName());
			logger.debug("Email User Password = {}", getPassword());
			logger.debug("From address = {}", getFrom());
		}

	}

	public SendMail(Map<String, String> map, List<String> fileList, Map<String, String> imageMap) {

		this();

		setTo(map.get("to"));
		setSubject(map.get("subject"));
		setBody(map.get("body"));

		for (String s : fileList) {
			addFile(s);
		}

		setImageMap(imageMap);

	}

	public class EmailRunnable implements Runnable {

		@Override
		public void run() {

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

			Session session = null;
			if ("true".equals(getAuthenticationRequired())) {
				System.err.println("Authentication is required.");
				SendMailAuthenticator ma = new SendMailAuthenticator();
				session = Session.getInstance(pros, ma);
			} else {
				System.err.println("Authentication is not required.");
				session = Session.getInstance(pros);
			}

			session.setDebug(true);

			try {

				MimeMessage msg = createMessage(session);
				Transport ts = session.getTransport();

				ts.connect();
				System.err.println("Connected to smtp server");

				while (!tos.empty()) {

					String to = tos.pop();
					System.err.println("Should have sent email to " + to);
					try {
						
						msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
						ts.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));

						Item item = new PropertysetItem();
						item.addItemProperty("EMAILADDRESS", new ObjectProperty<String>(to));
						item.addItemProperty("STATUSMESSAGE", new ObjectProperty<String>("SUCCESSFULLY TRANSFERRED TO RELAY"));
						EmailLogs logs = new EmailLogs();
						logs.storeExternalRow(item);

						System.err.println("Should have stored log message " + to);
						
					} catch (AddressException ae) {
						
						System.err.println("Could not send email to " + to + ae.getMessage());

					}

				}

				ts.close();
				System.err.println("disconnected to smtp server");

			} catch (MessagingException e) {

				e.printStackTrace();

				if (logger.isErrorEnabled()) {
					logger.error("Could not send email", e);
				}

				for (String to : tos) {
					Item item = new PropertysetItem();
					item.addItemProperty("EMAILADDRESS", new ObjectProperty<String>(to));
					item.addItemProperty("STATUSMESSAGE", new ObjectProperty<String>("ERROR IN SENDING EMAIL " + e.getMessage()));
					try {
						EmailLogs logs = new EmailLogs();
						if (conn == null) {
							logs.storeExternalRow(item);
						} else {
							logs.storeRow(conn, item);
							conn.commit();
						}

					} catch (SQLException e1) {

						if (logger.isErrorEnabled()) {
							logger.error("Could not save log", e1);
						}

					}
				}

			} catch (SQLException e) {

				e.printStackTrace();

				System.err.println("Could not save log");
				e.printStackTrace();

				if (logger.isErrorEnabled()) {
					logger.error("Could not save log", e);
				}

			}

		}

	}

	public void send() {

		Runnable task = new EmailRunnable();

		Thread worker = new Thread(task);
		worker.start();

	}

	protected MimeMessage createMessage(Session mailSession) throws MessagingException {

		return new MimeMessage(mailSession) {
			{
				setFrom(new InternetAddress(from));
				// setRecipients(Message.RecipientType.TO,
				// InternetAddress.parse(getTo()));
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

	/**
	 * @return the server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * @param server
	 *            the server to set
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol
	 *            the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @param to
	 *            the to to set
	 */
	public void setTo(String to) {
		tos.add(to);
	}

	Stack<String> tos = new Stack<String>();

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
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

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

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return the startTLS
	 */
	public String getStartTLS() {
		return startTLS;
	}

	/**
	 * @param startTLS
	 *            the startTLS to set
	 */
	public void setStartTLS(String startTLS) {
		this.startTLS = startTLS;
	}

	/**
	 * @return the authenticationRequired
	 */
	public String getAuthenticationRequired() {
		return authenticationRequired;
	}

	/**
	 * @param authenticationRequired
	 *            the authenticationRequired to set
	 */
	public void setAuthenticationRequired(String authenticationRequired) {
		this.authenticationRequired = authenticationRequired;
	}

}
