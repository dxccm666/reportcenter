package edu.missouri.cf.projex4.ui.desktop.configuration.users;

import java.sql.SQLException;
import java.util.Random;

import org.slf4j.Logger;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;

import edu.missouri.cf.data.OracleBoolean;
import edu.missouri.cf.data.OracleString;
import edu.missouri.cf.data.OracleTimestamp;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.DataNotFoundException;
import edu.missouri.cf.projex4.data.ItemInitializer;
import edu.missouri.cf.projex4.data.common.EnterprisePersons;
import edu.missouri.cf.projex4.data.common.Persons;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.users.Users;
import edu.missouri.cf.projex4.ui.common.OracleBooleanCheckBox;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.desktop.StandardTable;
import edu.missouri.cf.projex4.ui.desktop.documents.PopupWindow;

@SuppressWarnings("serial")
public class AddNewPSUserView extends PopupWindow {

	private StandardTable personTable;
	protected final static transient Logger logger = Loggers.getLogger(AddNewPSUserView.class);

	private Button addButton;
	private OracleBooleanCheckBox emergencyContact;
	private String user_id;
	private OracleBooleanCheckBox facilitiesUser;

	public AddNewPSUserView() {

	}

	@Override
	public void attach() {
		super.attach();
		init();
		layout();
	}

	private void init() {
		emergencyContact = new OracleBooleanCheckBox("Contact in Emergencies?");
		facilitiesUser = new OracleBooleanCheckBox("Facilities User?");

		personTable = new StandardTable() {
			{
				setMultiSelect(false);
				setSizeFull();
				setRequired(true);
			}
		};

		personTable.add(new TableColumn("SSOID", "SSO").setCollapsed(true));
		personTable.add(new TableColumn("NAME", "Display Name"));
		personTable.add(new TableColumn("DEPTID", "DEPTID"));
		personTable.add(new TableColumn("DEPTIDNAME", "DEPTIDNAME").setCollapsed(true));
		personTable.add(new TableColumn("BUSINESSUNIT", "BusUnit").setCollapsed(true));
		personTable.add(new TableColumn("BUSINESSUNITNAME", "Business Unit").setCollapsed(true));
		personTable.add(new TableColumn("SUPERDIVISION", "VCVP").setCollapsed(true));
		personTable.add(new TableColumn("SUPERDIVISIONNAME", "Super Division").setCollapsed(true));
		personTable.add(new TableColumn("DIVISION", "Div").setCollapsed(true));
		personTable.add(new TableColumn("DIVISIONNAME", "Division").setCollapsed(true));
		personTable.add(new TableColumn("DEPARTMENT", "Depart").setCollapsed(true));
		personTable.add(new TableColumn("DEPARTMENTNAME", "Department"));
		personTable.add(new TableColumn("SUBDEPARTMENT", "SUBDEPARTMENT").setCollapsed(true));
		personTable.add(new TableColumn("SUBDEPARTMENTNAME", "Sub Department").setCollapsed(true));
		personTable.add(new TableColumn("JOBTITLE", "Title"));
		personTable.add(new TableColumn("EMAILADDRESS", "Email"));
		personTable.add(new TableColumn("WORKADDRESS", "Address").setCollapsed(true));
		personTable.add(new TableColumn("WORKCITY", "City").setCollapsed(true));
		personTable.add(new TableColumn("WORKCOUNTY", "County").setCollapsed(true));
		personTable.add(new TableColumn("WORKPHONE", "Phone"));
		personTable.add(new TableColumn("WORKSTATE", "State").setCollapsed(true));
		personTable.add(new TableColumn("WORKPOSTAL", "Postal Code").setCollapsed(true));
		personTable.add(new TableColumn("JOBTITLE", "JOBTITLE").setCollapsed(true));
		personTable.add(new TableColumn("GENDER", "GENDER").setCollapsed(true));
		personTable.add(new TableColumn("SSOID", "SSOID").setCollapsed(true));
		personTable.add(new TableColumn("EMAILADDRESS", "EMAILADDRESS"));
		personTable.setCaption("Active Enterprise Employees");

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

				Item personItem;

				if (logger.isDebugEnabled()) {
					logger.debug("New person is External");
				}

				personItem = personTable.getItem(personTable.getValue());
				addRecordForPeopleSoft(personItem, (OracleBoolean) emergencyContact.getConvertedValue(), (OracleBoolean) facilitiesUser.getConvertedValue());
				Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.USEREDITOR, user_id);
				close();
			}

		});

		setData();
	}

	private void setData() {
		try {

			EnterprisePersons query = new EnterprisePersons();
			final OracleContainer sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("NAME", true));

			personTable.setContainerDataSource(sqlContainer);
			personTable.configure();

		} catch (SQLException e) {

			if (logger.isErrorEnabled()) {
				logger.error("Error in PeopleSoft query", e);
			}

		}

	}

	private void layout() {
		setContent(new VerticalLayout() {

			{
				setMargin(true);
				setSpacing(true);
				setSizeFull();

				addComponent(personTable);
				setExpandRatio(personTable, 0.6f);
				addComponent(new HorizontalLayout() {
					{
						addComponent(emergencyContact);
						addComponent(facilitiesUser);
					}
				});
				addComponent(addButton);

			}
		});
	}

	private boolean isValid() {
		if (personTable.getValue() == null) {
			Notification.show("Need to select a Row from the Persons Table");
			if (logger.isDebugEnabled()) {
				logger.debug("Did not select a record from the persons Table");
			}
			return false;
		}

		return true;
	}

	protected void addRecordForPeopleSoft(final Item selected, final OracleBoolean notifyInEmergency, final OracleBoolean facilitiesuser) {

		if (logger.isDebugEnabled()) {
			logger.debug("Attempt to add record to Participant Person Table");
		}

		java.util.Date start = new java.util.Date();

		String personLastId = null;

		try {

			Item item = ItemInitializer.initialize("PERSON", "ACTIVE", new PropertysetItem());
			item.addItemProperty("FULLNAME", selected.getItemProperty("NAME"));
			item.addItemProperty("DISPLAYNAME", selected.getItemProperty("NAME"));
			item.addItemProperty("SORTNAME", selected.getItemProperty("NAME"));

			item.addItemProperty("EMPLID", selected.getItemProperty("EMPLID"));

			item.addItemProperty("BUSINESSUNIT", selected.getItemProperty("BUSINESSUNIT"));
			item.addItemProperty("BUSINESSUNITNAME", selected.getItemProperty("BUSINESSUNITNAME"));
			item.addItemProperty("SUPERDIVISION", selected.getItemProperty("SUPERDIVISION"));
			item.addItemProperty("SUPERDIVISIONNAME", selected.getItemProperty("SUPERDIVISIONNAME"));
			item.addItemProperty("DIVISION", selected.getItemProperty("DIVISION"));
			item.addItemProperty("DIVISIONNAME", selected.getItemProperty("DIVISIONNAME"));
			item.addItemProperty("DEPARTMENT", selected.getItemProperty("DEPARTMENT"));
			item.addItemProperty("DEPARTMENTNAME", selected.getItemProperty("DEPARTMENTNAME"));
			item.addItemProperty("SUBDEPARTMENT", selected.getItemProperty("SUBDEPARTMENT"));
			item.addItemProperty("SUBDEPARTMENTNAME", selected.getItemProperty("SUBDEPARTMENT"));
			item.addItemProperty("DEPTID", selected.getItemProperty("DEPTID"));
			item.addItemProperty("DEPTIDNAME", selected.getItemProperty("DEPTIDNAME"));
			item.addItemProperty("JOBTITLE", selected.getItemProperty("JOBTITLE"));
			item.addItemProperty("GENDER", selected.getItemProperty("GENDER"));

			// Data from role item
			// item.addItemProperty("ROLEID", roleItem.getItemProperty("ID"));
			item.addItemProperty("CREATEDBY", new ObjectProperty<OracleString>(new OracleString(edu.missouri.cf.projex4.data.system.User.getUser().getUserId())));
			item.addItemProperty("CREATED", new ObjectProperty<OracleTimestamp>(OracleTimestamp.now()));
			item.addItemProperty("PROGRAMVERSION", new ObjectProperty<OracleString>(new OracleString("Projex 4")));
			item.addItemProperty("LANGUAGECODE", new ObjectProperty<OracleString>(new OracleString("en")));

			Persons query = new Persons();
			query.storeExternalRow(item);
			Object lastId = query.getLastId();
			personLastId = lastId.toString();

			if (logger.isDebugEnabled()) {
				logger.debug("newPerson() = {}, {} ms", personLastId, (new java.util.Date().getTime() - start.getTime()));
			}

			Item item_user = new PropertysetItem();

			// Data from personItem...
			item_user.addItemProperty("ROWSTAMP", new ObjectProperty<OracleString>(new OracleString("AAAA")));
			item_user.addItemProperty("PERSONID", new ObjectProperty<OracleString>(new OracleString(personLastId)));
			item_user.addItemProperty("INVITATIONEMAIL", selected.getItemProperty("EMAILADDRESS"));
			item_user.addItemProperty("INVITATIONCODE", new ObjectProperty<OracleString>(new OracleString(invitationCodeGenerator())));
			item_user.addItemProperty("INVITED", new ObjectProperty<OracleTimestamp>(OracleTimestamp.now()));
			item_user.addItemProperty("REGISTRATIONMETHOD", new ObjectProperty<OracleString>(new OracleString("INVITED")));
			item_user.addItemProperty("ISACTIVE", new ObjectProperty<OracleBoolean>(OracleBoolean.TRUE));

			if (facilitiesuser.toBoolean()) {
				item_user.addItemProperty("USERTYPE", new ObjectProperty<OracleString>(new OracleString("FACILITIES")));
			} else {
				item_user.addItemProperty("USERTYPE", new ObjectProperty<OracleString>(new OracleString("ENTERPRISE")));
			}

			item_user.addItemProperty("USERLOGIN", selected.getItemProperty("SSOID"));
			item_user.addItemProperty("INITIALIZED", new ObjectProperty<OracleBoolean>(OracleBoolean.FALSE));
			item_user.addItemProperty("NOTIFYBYEMAIL", new ObjectProperty<OracleBoolean>(OracleBoolean.FALSE));

			Users query_user = new Users();
			query_user.storeExternalRow(item_user);
			Object lastId_user = query_user.getLastId();
			user_id = lastId_user.toString();

			if (logger.isDebugEnabled()) {
				logger.debug("newUser() = {}, {} ms", lastId_user, (new java.util.Date().getTime() - start.getTime()));
			}

		} catch (DataNotFoundException | SQLException e) {

			if (logger.isErrorEnabled()) {
				logger.error("Exception creating new person", e);
			}
		}

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
