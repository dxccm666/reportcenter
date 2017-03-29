package edu.missouri.cf.projex4.ui.desktop.configuration.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import org.slf4j.Logger;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.data.OracleBoolean;
import edu.missouri.cf.data.OracleString;
import edu.missouri.cf.data.OracleTimestamp;
import edu.missouri.cf.projex4.data.DataNotFoundException;
import edu.missouri.cf.projex4.data.ItemInitializer;
import edu.missouri.cf.projex4.data.Pools;
import edu.missouri.cf.projex4.data.common.Persons;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.ui.common.OracleBooleanCheckBox;
import edu.missouri.cf.projex4.ui.desktop.documents.PopupWindow;
import edu.missouri.cf.projex4.ui.desktop.lookups.persons.ExternalPersonEntryComponent;
import edu.missouri.operations.reportcenter.data.Users;

@SuppressWarnings("serial")
public class AddNewExternalUserView  extends PopupWindow {

	protected final static transient Logger logger = Loggers.getLogger(AddNewExternalUserView.class);
	private Label screendescription;
	
	ExternalPersonEntryComponent externalEntry = new ExternalPersonEntryComponent();
	
	private Button addButton;
	
	private OracleBooleanCheckBox emergencyContact;
	
	//NewUserComponent newUserComponent = new NewUserComponent();
	public AddNewExternalUserView() { 
		externalEntry.newItemDataSource();
	}

	@Override
	public void attach() {
		super.attach();
		init();
		layout();
	}

	private void init() {

		screendescription = new Label("<h1>External User</h1>", ContentMode.HTML);
		screendescription.addStyleName("projectlisting_label");
		
		emergencyContact = new OracleBooleanCheckBox("Contact in Emergencies?");
		addButton = new Button();
		addButton.setCaption("add");
		addButton.setEnabled(true);
		addButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				logger.debug("Add Button called");

				if (!isValid()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Form is not valid");
					}
					Notification.show("Incomplete Form Data.");
					return;
				}

				Item firmItem;
				Item personItem;
				
				if (logger.isDebugEnabled()) {
					logger.debug("New person is External");
				}
				personItem = externalEntry.getItemDataSource();
				firmItem = externalEntry.getFirmItemDataSource();
				
				System.err.println("-------------------externalExist == " + externalExist(personItem));
				if (externalExist(personItem)) {
					new Notification("This email address is already been used!", Notification.Type.WARNING_MESSAGE).show(Page.getCurrent());
				} else {

					addRecordForOutsideUser(personItem, firmItem, (OracleBoolean) emergencyContact.getConvertedValue());
					close();
				}
			}
		});
	}

	private void layout() {

		VerticalLayout root = new VerticalLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setSizeFull();
				addComponent(screendescription);
				addComponent(externalEntry);
				setExpandRatio(externalEntry, 1.0f);
				addComponent(emergencyContact);
				setExpandRatio(emergencyContact, 1.0f);
				addComponent(new HorizontalLayout() {
					{
						addComponent(addButton);
						setSpacing(true);
						
					}
				});
				
			}
		};
		setContent(root);

	}
	
	public boolean isValid() {
		
			return externalEntry.isValid();
		
	}
	
	
	private boolean externalExist(Item personUser) {

		Connection conn = null;

		try {
			conn = Pools.getConnection(Pools.Names.PROJEX);

			try (PreparedStatement stmt = conn.prepareStatement("select ID from users where INVITATIONEMAIL = ?")) {

				System.err.println("-------------------EMAILADDRESS == " + personUser.getItemProperty("EMAILADDRESS").getValue().toString());

				stmt.setString(1, personUser.getItemProperty("EMAILADDRESS").getValue().toString());

				try (ResultSet rs = stmt.executeQuery()) {

					if (rs.next()) {
						return true;
					}

				}

			}
		} catch (SQLException sqle) {
			logger.error("external invitation email is exist. {}", sqle);
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}

		return false;

	}
	
	@SuppressWarnings("unchecked")
	protected void addRecordForOutsideUser(final Item selected, final Item firmItem, final OracleBoolean notifyInEmergency) {

		if (logger.isDebugEnabled()) {
			logger.debug("Attempt to add record to Participant Person Table");
		}

		
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					
					java.util.Date start = new java.util.Date();

					String personLastId = null;

					try {

						Item item = ItemInitializer.initialize("PERSON", "ACTIVE", new PropertysetItem());

						// Data from personItem...
						//item.addItemProperty("PERSONTYPE", roleItem.getItemProperty("USERTYPE"));
						item.addItemProperty("FULLNAME", selected.getItemProperty("FULLNAME"));
						item.addItemProperty("DISPLAYNAME", selected.getItemProperty("FULLNAME"));
						item.addItemProperty("SORTNAME", selected.getItemProperty("FULLNAME"));

						item.addItemProperty("FIRMID", firmItem.getItemProperty("ID"));
						item.addItemProperty("FIRMTYPE", firmItem.getItemProperty("FIRMTYPE"));
						item.addItemProperty("FIRMDISPLAYNAME", firmItem.getItemProperty("DISPLAYNAME"));
						item.addItemProperty("FIRMLEGALNAME", firmItem.getItemProperty("LEGALNAME"));
						item.addItemProperty("FIRMSORTNAME", firmItem.getItemProperty("SORTNAME"));

						// Data from role item
						item.addItemProperty("CREATEDBY", new ObjectProperty<OracleString>(new OracleString(User.getUser().getUserId())));
						item.addItemProperty("CREATED", new ObjectProperty<OracleTimestamp>(OracleTimestamp.now()));
						item.addItemProperty("PROGRAMVERSION", new ObjectProperty<OracleString>(new OracleString("Projex 4")));
						item.addItemProperty("LANGUAGECODE", new ObjectProperty<OracleString>(new OracleString("en")));

						Persons query = new Persons();
						query.storeExternalRow(item);
						Object lastId = query.getLastId();
						personLastId = lastId.toString();

						if (logger.isDebugEnabled()) {
							logger.debug("newPersonForOutside() = {}, {}ms", lastId, (new java.util.Date().getTime() - start.getTime()));
						}
						
						Item item_user = new PropertysetItem();

						// Data from personItem...
						item_user.addItemProperty("ROWSTAMP", new ObjectProperty<OracleString>(new OracleString("AAAA")));
						item_user.addItemProperty("PERSONID", new ObjectProperty<OracleString>(new OracleString(personLastId)));
						item_user.addItemProperty("INVITATIONEMAIL", new ObjectProperty<OracleString>(new OracleString(selected.getItemProperty("EMAILADDRESS"))));


						item_user.addItemProperty("INVITATIONCODE", new ObjectProperty<OracleString>(new OracleString(invitationCodeGenerator())));
						item_user.addItemProperty("INVITED", new ObjectProperty<OracleTimestamp>(OracleTimestamp.now()));
						item_user.addItemProperty("REGISTRATIONMETHOD", new ObjectProperty<OracleString>(new OracleString("INVITED")));
						item_user.addItemProperty("ISACTIVE", new ObjectProperty<OracleBoolean>(OracleBoolean.TRUE));

						Users query_user = new Users();
						query_user.storeExternalRow(item_user);
						Object lastId_user = query_user.getLastId();

						if (logger.isDebugEnabled()) {
							logger.debug("newUser() = {}, {} ms", lastId_user, (new java.util.Date().getTime() - start.getTime()));
						}

						
						

					} catch (DataNotFoundException | SQLException e) {

						if (logger.isErrorEnabled()) {
							logger.error("Could not initialize person", e);
						}

					}

					
				}
			};

			new Thread(runnable).start();

		

	}
	
	private String invitationCodeGenerator() {

		String invitationCode = null;
		StringBuffer buffer = new StringBuffer("0123456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ");
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		int range = buffer.length();
		for (int i = 0; i < 13; i++) {
			sb.append(buffer.charAt(r.nextInt(range)));
		}

		invitationCode = sb.toString();

		if (logger.isDebugEnabled()) {
			logger.debug("new invitation code = {}", invitationCode);
		}

		return invitationCode;
	}


}