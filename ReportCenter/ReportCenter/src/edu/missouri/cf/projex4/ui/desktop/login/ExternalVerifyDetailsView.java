package edu.missouri.cf.projex4.ui.desktop.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.data.OracleBoolean;
import edu.missouri.cf.data.OracleString;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.Pools;
import edu.missouri.cf.projex4.data.common.PersonDetails;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.ui.common.system.StandardComboBox;
import edu.missouri.cf.projex4.ui.desktop.LoginTopBarView;

@SuppressWarnings("serial")
public class ExternalVerifyDetailsView extends LoginTopBarView {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@PropertyId("FULLNAME")
	private TextField fullName;

	@PropertyId("DISPLAYNAME")
	private TextField displayName;

	@PropertyId("SORTNAME")
	private TextField sortName;

	@PropertyId("JOBTITLE")
	private TextField jobTitle;

	@PropertyId("FIRMDISPLAYNAME")
	private TextField firmId;

	@PropertyId("ADDRESS")
	private TextField address;

	@PropertyId("CITY")
	private TextField city;

	@PropertyId("COUNTY")
	private TextField county;

	@PropertyId("STATE")
	private TextField state;

	@PropertyId("COUNTRYCODE")
	private StandardComboBox countryCode;

	@PropertyId("POSTALCODE")
	private TextField postalCode;

	@PropertyId("TIMEZONE")
	private StandardComboBox timezone;

	@PropertyId("MOBILEPHONE")
	private TextField mobilePhoneNum;

	@PropertyId("FAXNUMBER")
	private TextField faxNum;

	@PropertyId("INVITATIONEMAIL")
	private TextField emailAddress;

	@PropertyId("WORKPHONE")
	private TextField workNum;

	private Label label_1;

	private OracleContainer sqlContainer;

	private Item item;
	private Button confirmButton;
	private String id;

	/*
	 * private OracleContainer sqlContainer_person; private OracleContainer
	 * sqlContainer_email; private OracleContainer sqlContainer_address; private
	 * OracleContainer sqlContainer_phone;
	 */

	public ExternalVerifyDetailsView() {
		super();
	}

	private boolean initialized = false;

	@Override
	public void attach() {
		super.attach();

		if (!initialized) {
			setSizeFull();
			init();
			layout();
			initialized = true;
		}
	}

	public void init() {

		label_1 = new Label("<h2>Please verify your personal information in here:</h2> <br>", ContentMode.HTML);

		fullName = new TextField() {
			{
				setCaption("Full Name");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
				setInputPrompt("Firstname Lastname");
			}
		};

		displayName = new TextField() {
			{
				setCaption("Display Name");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
				setInputPrompt("Firstname Lastname");
			}
		};

		sortName = new TextField() {
			{
				setCaption("Sort Name");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
				setInputPrompt("Lastname, Firstname");
			}
		};

		jobTitle = new TextField() {
			{
				setCaption("Job Title");
				setDescription("");
				setWidth("250px");
				setImmediate(true);
				setNullRepresentation("");
			}
		};

		firmId = new TextField() {
			{
				setCaption("Firm");
				setDescription("");
				setWidth("350px");
				setImmediate(true);
				setReadOnly(true);
			}
		};

		address = new TextField() {
			{
				setCaption("Address");
				setDescription("");
				setWidth("350px");
				setImmediate(true);
				setNullRepresentation("");

			}
		};

		city = new TextField() {
			{
				setCaption("City");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");

			}
		};

		county = new TextField() {
			{
				setCaption("County");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");

			}
		};

		state = new TextField() {
			{
				setCaption("State");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");

			}
		};

		countryCode = new StandardComboBox("addresses.countrycode", "Country") {
			{
				setDescription("");
				setImmediate(true);
				refreshDataCollection();

			}
		};

		postalCode = new TextField() {
			{
				setCaption("Postal Code");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");

			}
		};

		timezone = new StandardComboBox("addresses.timezones", "Timezone") {
			{
				setDescription("");
				setImmediate(true);
				refreshDataCollection();
			}
		};

		mobilePhoneNum = new TextField() {
			{
				setCaption("Mobile Phone");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");

			}
		};

		faxNum = new TextField() {
			{
				setCaption("Fax");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");

			}
		};

		workNum = new TextField() {
			{
				setCaption("Work Phone");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");

			}
		};

		emailAddress = new TextField() {
			{
				setCaption("Email");
				setWidth("300px");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");

			}
		};

		confirmButton = new Button("Confirm") {
			{
				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						if(User.getUser()==null) {
							User.setUser(getUser(id));
						}
						saveData();
						Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.HOME);
					}
				});
			}
		};
	}
	
	public User getUser(String id) {

		User user = null;

		Connection conn = null;
		try {

			conn = Pools.getConnection(Pools.Names.PROJEX);
			try (PreparedStatement stmt = conn.prepareStatement("select * from personsmview where id = ?")) {
				stmt.setString(1, id);

				try (ResultSet rs = stmt.executeQuery()) {

					if (rs.next()) {
						user = new User();
						user.setValuesFromDatabaseForPersonId(id, rs);
					}

				}
			}

		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Could not retrieve firm data from firm id {}", e);
			}
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}

		return user;

	}
	
	private void layout() {
		VerticalLayout root = new VerticalLayout() {
			{
				setSpacing(true);
				setMargin(true);
				setWidth("100%");
				addComponent(label_1);

				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);

						addComponent(fullName);
						setExpandRatio(fullName, 0.3f);
						addComponent(displayName);
						setExpandRatio(displayName, 0.3f);
						addComponent(sortName);
						setExpandRatio(sortName, 0.3f);

					}
				});
				addComponent(firmId);
				addComponent(jobTitle);
				addComponent(address);
				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);
						addComponent(city);
						addComponent(county);
						addComponent(state);
						addComponent(countryCode);
						addComponent(postalCode);
						addComponent(timezone);
					}
				});
				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);
						addComponent(countryCode);
						addComponent(postalCode);
						addComponent(timezone);
					}
				});
				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);
						addComponent(workNum);
						addComponent(mobilePhoneNum);
						addComponent(faxNum);

					}
				});
				addComponent(emailAddress);
				addComponent(confirmButton);
			}
		};

		addComponent(root);
		setExpandRatio(root, 1.0f);
	}

	FieldGroup binder;

	public void setItemDataSource(Item item) {
		this.item = item;
		binder = new FieldGroup(item);
		binder.bindMemberFields(this);
	}

	public void commit() {

		try {

			binder.commit();
			sqlContainer.commit();

		} catch (UnsupportedOperationException | SQLException e) {

			if (logger.isErrorEnabled()) {
				logger.error("Could not save persondetails records", e);
			}

			e.printStackTrace();

		} catch (CommitException e) {

			if (logger.isErrorEnabled()) {
				logger.error("Could not commit persondetails screen", e);
			}

			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void enter(ViewChangeEvent event) {

		id = event.getParameters();

		try {

			PersonDetails query = new PersonDetails();
			query.setMandatoryFilters(new Compare.Equal("ID", id));
			sqlContainer = new OracleContainer(query);
			item = sqlContainer.getItemByProperty("ID", id);
			
			if(logger.isDebugEnabled()) {
				logger.debug("Encrypted password = {}", item.getItemProperty("PASSWORD").getValue());
			}
			
			// This is just a hack to get around the user commit delay

			try(Connection conn = Pools.getConnection(Pools.Names.PROJEX)) {
				
				try(PreparedStatement stmt = conn.prepareStatement("select * from temppassword where personid = ?")) {
					stmt.setString(1, item.getItemProperty("ID").getValue().toString());
					
					try(ResultSet rs = stmt.executeQuery()) {
						
						if(rs.next()) {
							
							item.getItemProperty("PASSWORD").setValue(new OracleString(rs.getString("PASSWORD")));
							item.getItemProperty("VERIFIER").setValue(new OracleString(rs.getString("VERIFIER")));
							item.getItemProperty("USERLOGIN").setValue(new OracleString(rs.getString("USERLOGIN")));
							item.getItemProperty("INITIALIZED").setValue(OracleBoolean.TRUE);
							
						}
					}
					
				}
				
				try(PreparedStatement stmt = conn.prepareStatement("delete from temppassword where personid = ?")) {
					stmt.setString(1, item.getItemProperty("ID").getValue().toString());
					stmt.executeUpdate();
				}
				
				conn.commit();
				
			} catch (SQLException sqle) {
				if(logger.isErrorEnabled()) {
					logger.error("Unable to retreive temp password");
				}
			}
			
			setItemDataSource(item);

		} catch (SQLException e) {

			if (logger.isErrorEnabled()) {
				logger.error("Error setting temps query", e);
			}

		}
	}

	public Item getItemDataSource() {

		try {
			binder.commit();
		} catch (CommitException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Could not commit fields ", e);
			}
		}
		return item;

	}

	private void saveData() {

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				
				commit();
			}

		};

		new Thread(runnable).start();

	}

}
