package edu.missouri.cf.projex4.ui.desktop.configuration.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.In;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import c10n.C10N;
import edu.missouri.cf.data.OracleBoolean;
import edu.missouri.cf.data.OracleString;
import edu.missouri.cf.data.OracleTimestamp;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.DataNotFoundException;
import edu.missouri.cf.projex4.data.ItemInitializer;
import edu.missouri.cf.projex4.data.Pools;
import edu.missouri.cf.projex4.data.common.Addresses;
import edu.missouri.cf.projex4.data.common.EmailAddresses;
import edu.missouri.cf.projex4.data.common.EnterprisePersons;
import edu.missouri.cf.projex4.data.common.ParticipantPersons;
import edu.missouri.cf.projex4.data.common.PersonDetails;
import edu.missouri.cf.projex4.data.common.Persons;
import edu.missouri.cf.projex4.data.common.Phones;
import edu.missouri.cf.projex4.data.projects.Projects;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.ObjectClasses;
import edu.missouri.cf.projex4.data.system.core.Roles;
import edu.missouri.cf.projex4.data.system.core.Statuses;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.email.SendMail;
import edu.missouri.cf.projex4.ui.c10n.ParticipantPersonsText;
import edu.missouri.cf.projex4.ui.common.OracleBooleanCheckBox;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.desktop.StandardTable;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;
import edu.missouri.cf.projex4.ui.desktop.lookups.Lookup;
import edu.missouri.cf.projex4.ui.desktop.lookups.firms.FirmIdToStringConverter;
import edu.missouri.cf.projex4.ui.desktop.lookups.persons.ExternalPersonEntryComponent;
import edu.missouri.cf.projex4.ui.desktop.lookups.persons.InsiderUserEmailer;
import edu.missouri.cf.projex4.ui.desktop.lookups.persons.OutsideUserEmailer;
import edu.missouri.cf.projex4.ui.desktop.lookups.persons.ParticipantPersonsComponent;
import edu.missouri.cf.projex4.ui.desktop.lookups.persons.PersonIdToStringConverter;
import edu.missouri.cf.projex4.ui.desktop.lookups.roles.RoleIdToStringConverter;
import edu.missouri.cf.projex4.ui.desktop.lookups.status.StatusIdToStringConverter;
import edu.missouri.operations.reportcenter.data.CampusUsers;
import edu.missouri.operations.reportcenter.data.Campuses;
import edu.missouri.operations.reportcenter.data.SecurityGroupUsers;
import edu.missouri.operations.reportcenter.data.SecurityGroups;
import edu.missouri.operations.reportcenter.data.Users;
import edu.missouri.operations.ui.desktop.filtertable.modulargenerator.StandardCellStyleGenerator;

@SuppressWarnings("serial")
public class NewUserComponent extends TableDependentProjexEditor {

	protected final static transient Logger logger = Loggers.getLogger(ParticipantPersonsComponent.class);

	public enum ComponentType {
		PROJECT, CONTRACT, AGREEMENT
	}

	ComponentType componentType;

	private boolean userAdded = false;

	public boolean isUserAdded() {
		return userAdded;
	}

	public void setUserAdded(boolean userAdded) {
		this.userAdded = userAdded;
	}

	class StyleGenerator extends StandardCellStyleGenerator {

		@Override
		public String getAdditionalStyles(Table source, Object itemId, Object propertyId) {

			Item i = source.getItem(itemId);

			if (i.getItemProperty("STATUS") != null && i.getItemProperty("STATUS").getValue() != null) {
				switch (i.getItemProperty("STATUS").getValue().toString()) {

				case "DELETED":
					return "deleted";

				}
			}

			return null;
		}

	}

	ArrayList<Callable<Boolean>> backgroundPersonCommits = new ArrayList<Callable<Boolean>>();

	protected void setComponentType(ComponentType componentType) {
		this.componentType = componentType;
		lookUp.setComponentType(componentType);
	}

	private ParticipantPersons participantPersons;

	protected ParticipantPersonsText st;
	protected ParticipantPersonsLookupComponent lookUp = new ParticipantPersonsLookupComponent();

	public NewUserComponent(ComponentType componentType, String applicationName) {

		setComponentType(componentType);
		setApplicationName(applicationName);
		init();

	}

	public NewUserComponent(ComponentType componentType, ProjexViewProvider.Views view) {
		setComponentType(componentType);
		setApplicationName(view);
		init();
	}

	private void init() {

		st = C10N.get(ParticipantPersonsText.class, User.getUser().getUserLocale());
		setSubApplicationName("PARTICIPANTPERSON");

		setCaption(st.screenName());

		addButton.setDescription(st.addButtonDescription());
		addButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				lookUp.setLookupMode(LookupMode.PERSON);
				lookUp.addWindow();

			}
		});

		deleteButton.setDescription(st.deleteButtonDescription());
		deleteButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				try {

					String statusId = Statuses.getStatusId("PARTICIPANTPERSON", "DELETED");

					if (getTable().isMultiSelect()) {
						if (getTable().getValue() != null && ((Collection<?>) getTable().getValue()).size() > 0) {
							Collection<?> selectedItems = (Collection<?>) getTable().getValue();
							for (Object itemId : selectedItems) {
								
								Item i = sqlContainer.getItem(itemId);
								if(i instanceof TemporaryRowId) {
									
									Statuses.changeStatusId(i.getItemProperty("ID").getValue().toString(), statusId, User.getUser().getUserId());
									sqlContainer.removeItem(i);
								} else {
									sqlContainer.removeItem(i);
								}
								

							}
						}

					} else {
						if (getTable().getValue() != null) {
							Item i = getTable().getItem(getTable().getValue());
							Statuses.changeStatusId(i.getItemProperty("ID").getValue().toString(), statusId, User.getUser().getUserId());
						}

					}

				} catch (SQLException | DataNotFoundException e) {

					if (logger.isErrorEnabled()) {
						logger.error("Unable to mark participant as deleted");
					}

				}

			}
		});

		// TODO Fix 7.5 table.setCellStyleGenerator(new StyleGenerator());

		table.setContextHelp(st.table().contextHelp());
		table.add(new TableColumn("PERSONID", st.table().person()).setReadOnly(true).setWidth(200).setConverter(new PersonIdToStringConverter()).setOverrideClass(OracleString.class));
		table.add(new TableColumn("FULLNAME", st.table().fullName()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("DISPLAYNAME", st.table().displayName()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("SORTNAME", st.table().sortName()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("ROLEID", st.table().role()).setRequired(true).setWidth(200).setConverter(new RoleIdToStringConverter()).setReadOnly(true));
		table.add(new TableColumn("ISPRIMARY", st.table().primary()).setWidth(60));
		table.add(new TableColumn("NOTIFYINEMERGENCY", st.table().notifyInEmergency()).setWidth(75));
		// table.add(new TableColumn("ISAPPROVER",
		// st.table().approver()).setWidth(60));
		table.add(new TableColumn("PERSONTYPE", st.table().personType()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("FIRMID", st.table().firmId()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("FIRMTYPE", st.table().firmType()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("FIRMDISPLAYNAME", st.table().firmDisplayName()).setReadOnly(true));
		table.add(new TableColumn("FIRMLEGALNAME", st.table().firmLegalName()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("FIRMSORTNAME", st.table().firmSortName()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("BUSINESSUNIT", st.table().businessUnit()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("BUSINESSUNITNAME", st.table().businessUnitName()).setReadOnly(true));
		table.add(new TableColumn("SUPERDIVISION", st.table().superDivision()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("SUPERDIVISIONNAME", st.table().superDivisionName()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("DIVISION", st.table().division()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("DIVISIONNAME", st.table().divisionName()).setReadOnly(true));
		table.add(new TableColumn("DEPARTMENT", st.table().department()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("DEPARTMENTNAME", st.table().departmentName()).setReadOnly(true));
		table.add(new TableColumn("SUBDEPARTMENT", st.table().subDepartment()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("SUBDEPARTMENTNAME", st.table().subDepartmentName()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("DEPTID", st.table().deptId()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("DEPTIDNAME", st.table().deptIdName()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("WORKPHONE", st.table().workPhone()).setReadOnly(true));
		table.add(new TableColumn("MOBILEPHONE", st.table().mobilePhone()).setReadOnly(true));
		table.add(new TableColumn("FAXNUMBER", st.table().faxNumber()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("EMAILADDRESS", st.table().emailAddress()).setReadOnly(true));
		table.add(new TableColumn("ADDRESS", st.table().address()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("CITY", st.table().city()).setReadOnly(true));
		table.add(new TableColumn("COUNTY", st.table().county()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("STATE", st.table().state()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("COUNTRYCODE", st.table().countryCode()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("POSTALCODE", st.table().postalCode()).setReadOnly(true).setCollapsed(true));
		table.add(new TableColumn("TIMEZONE", st.table().timeZone()).setReadOnly(true));
		table.addCommonObjectColumns("PARTICIPANTPERSON");

	}

	class DataLoader implements Runnable {

		ObjectData refObjectData;

		public DataLoader(ObjectData refObjectData) {
			this.refObjectData = refObjectData;
		}

		@Override
		public void run() {

			java.util.Date start = new java.util.Date();

			participantPersons = new ParticipantPersons();
			participantPersons.setRefObjectId(refObjectData.getObjectId());

			try {

				sqlContainer = new OracleContainer(participantPersons);
				sqlContainer.addOrderBy(new OrderBy("ISPRIMARY", false), new OrderBy("SORTNAME", true));
				sqlContainer.overrideType("PERSONID", OracleString.class);
				sqlContainer.overrideType("CREATEDBY", OracleString.class);
				sqlContainer.overrideType("MODIFIEDBY", OracleString.class);
				sqlContainer.overrideType("STATUSEDBY", OracleString.class);
				sqlContainer.setAutoCommit(false);

			} catch (SQLException e) {
				logger.error("Error retrieving ParticipantPersons ", e);
			}

			table.setContainerDataSource(sqlContainer);
			table.configure();

			if (logger.isDebugEnabled()) {
				logger.debug("ParticipantPersons load took {}", new java.util.Date().getTime() - start.getTime());
			}

		}

	}

	public void setData(ObjectData refObjectData) {

		setObjectData(refObjectData);
		DataLoader loader = new DataLoader(refObjectData);
		loader.run();

	}

	public enum LookupType {
		USER, PERSON
	}

	public enum LookupMode {
		PERSON, PEOPLESOFT, EXTERNAL
	}

	public class ParticipantPersonsLookupComponent extends Lookup {

		private ComponentType componentType;

		public void setComponentType(ComponentType componentType) {
			this.componentType = componentType;
		}

		private LookupType lookupType = LookupType.PERSON;

		public LookupType getLookupType() {
			return lookupType;
		}

		public void setLookupType(LookupType lookupType) {
			this.lookupType = lookupType;
		}

		private LookupMode lookupMode = LookupMode.PERSON;

		public LookupMode getLookupMode() {

			return lookupMode;
		}

		public void setLookupMode(LookupMode lookupMode) {

			this.lookupMode = lookupMode;
			switch (lookupMode) {

			case EXTERNAL:

				personTable.setVisible(false);
				externalEntry.setVisible(true);
				externalEntry.newItemDataSource();
				setRoleData();

				break;

			case PEOPLESOFT:
				personTable.setVisible(true);
				externalEntry.setVisible(false);
				setPersonData();
				setRoleData();

				break;
			case PERSON:
				personTable.setVisible(true);
				externalEntry.setVisible(false);
				setPersonData();
				setRoleData();

				break;
			default:

				personTable.setVisible(true);
				externalEntry.setVisible(false);
				setPersonData();
				setRoleData();

				break;
			}

		}

		private boolean additionAllowed = false;

		public boolean isAdditionAllowed() {
			return additionAllowed;
		}

		public void setAdditionAllowed(boolean additionAllowed) {
			this.additionAllowed = additionAllowed;
		}

		private StandardTable personTable;
		private StandardTable roleTable;
		private ExternalPersonEntryComponent externalEntry;
		private OracleBooleanCheckBox showInactive;
		private OracleBooleanCheckBox inviteUser;
		private OracleBooleanCheckBox isPrimary;
		private OracleBooleanCheckBox emergencyContact;
		private Button enterpriseNotFoundButton;
		private Button externalNotFoundButton;

		final boolean sendemail = false;

		@SuppressWarnings("deprecation")
		public ParticipantPersonsLookupComponent() {

			setCaption("Add Participant");

			setWidth("85%");
			setHeight("85%");

			personTable = new StandardTable() {
				{
					setMultiSelect(false);
					setSizeFull();
					setRequired(true);
				}
			};

			externalEntry = new ExternalPersonEntryComponent();

			roleTable = new StandardTable() {
				{
					setMultiSelect(false);
					setSizeFull();
					setCaption("Role");
					setRequired(true);

					add(new TableColumn("USERTYPE", "User Type"));
					add(new TableColumn("DESCRIPTION", "Description"));
					add(new TableColumn("CLASSIFICATION", "Classification"));
					add(new TableColumn("ISREVIEWPARTICIPANT", "Review Participant?"));
				}
			};

			showInactive = new OracleBooleanCheckBox("Show Inactive") {

				{
					setImmediate(true);

					if (User.canDo(User.getUser().getUserId(), getApplicationName(), null, "PARTICIPANTPERSON.ADDINACTIVEPERSON")) {
						setEnabled(true);
						setVisible(true);
					} else {
						setEnabled(false);
						setVisible(false);
					}

					addValueChangeListener(new Property.ValueChangeListener() {

						@Override
						public void valueChange(Property.ValueChangeEvent event) {
							setPersonData();
						}

					});
				}
			};

			enterpriseNotFoundButton = new Button() {
				{
					setCaption("look up university employee");

					if (User.canDo(User.getUser().getUserId(), getApplicationName(), null, "PARTICIPANTPERSON.ADDENTERPRISEPERSON")) {
						setEnabled(true);
						setVisible(true);

					} else {
						setEnabled(false);
						setVisible(false);

					}

					addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							setLookupMode(LookupMode.PEOPLESOFT);

						}

					});
				}
			};

			externalNotFoundButton = new Button() {
				{
					setCaption("add external person");

					if (User.canDo(User.getUser().getUserId(), getApplicationName(), null, "PARTICIPANTPERSON.ADDEXTERNALPERSON")) {
						setEnabled(true);
						setVisible(true);
					} else {
						setEnabled(false);
						setVisible(false);
					}

					addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {

							setLookupMode(LookupMode.EXTERNAL);
							setRoleData();

						}

					});
				}
			};

			inviteUser = new OracleBooleanCheckBox() {
				{
					setCaption("Invite to be User?");
					setDescription("");

					if (User.canDo(User.getUser().getUserId(), getApplicationName(), null, "PARTICIPANTPERSON.INVITEUSER")) {
						setEnabled(true);
						setVisible(true);
					} else {
						setEnabled(false);
						setVisible(false);
					}

				}
			};

			isPrimary = new OracleBooleanCheckBox("Is Primary in selected Role?");
			emergencyContact = new OracleBooleanCheckBox("Contact in Emergencies?");
			getConfirmButton().setCaption("add");
			getConfirmButton().setEnabled(true);
			getConfirmButton().addClickListener(new Button.ClickListener() {

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
					Item roleItem;

					switch (lookupMode) {

					case EXTERNAL:

						if (logger.isDebugEnabled()) {
							logger.debug("New person is External");
						}
						personItem = externalEntry.getItemDataSource();
						firmItem = externalEntry.getFirmItemDataSource();
						roleItem = roleTable.getItem(roleTable.getValue());
						System.err.println("-------------------externalExist == " + externalExist(personItem));
						if (externalExist(personItem)) {
							new Notification("This email address is already been used!", Notification.Type.WARNING_MESSAGE).show(Page.getCurrent());
						} else {

							PersonUser p1 = addRecordForOutsideUser(personItem, roleItem, firmItem, (OracleBoolean) isPrimary.getConvertedValue(), (OracleBoolean) emergencyContact
									.getConvertedValue());
							if (sendemail) {
								OutsideUserEmailer outsideEmail = new OutsideUserEmailer(p1.emailAddress, p1.invitationCode);
								outsideEmail.send();
							}
						}

						break;

					case PEOPLESOFT:

						if (logger.isDebugEnabled()) {
							logger.debug("New person is from PeopleSoft");
						}

						personItem = personTable.getItem(personTable.getValue());
						roleItem = roleTable.getItem(roleTable.getValue());

						PersonUser p2 = addRecordForPeopleSoft(personItem, roleItem, (OracleBoolean) isPrimary.getConvertedValue(), (OracleBoolean) emergencyContact.getConvertedValue());

						if (sendemail) {
							InsiderUserEmailer insideEmail = new InsiderUserEmailer(p2.emailAddress, p2.invitationCode);
							insideEmail.send();
						}

						break;

					case PERSON:
					default:

						if (logger.isDebugEnabled()) {
							logger.debug("Selected person is already in Persons Table");
						}

						personItem = personTable.getItem(personTable.getValue());
						roleItem = roleTable.getItem(roleTable.getValue());

						PersonUser p3 = addRecordForFoundPerson(personItem, roleItem, (OracleBoolean) isPrimary.getConvertedValue(), (OracleBoolean) emergencyContact.getConvertedValue());

						break;
					}

					table.refreshRowCache();
					setUserAdded(true);
					close();

				}

			});

			layout();

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

		@SuppressWarnings("unchecked")
		public PersonUser addRecordForFoundPerson(final Item personItem, final Item roleItem, OracleBoolean isPrimary, OracleBoolean notifyInEmergency) {

			if (logger.isDebugEnabled()) {
				logger.debug("Attempt to add record to Participant Person Table");
			}
			
			if(personItem == null) {
				
				if(logger.isDebugEnabled()) {
					logger.debug("PersonItem is null -- This shouldn't happen");
				}
				return null;
				
			}
			
			if(personItem.getItemProperty("USERID") == null || personItem.getItemProperty("USERID").getValue() == null) {

				if (logger.isDebugEnabled()) {
					logger.debug("Need to create new User Record for person?");
				}
				Notification.show("Creation of new users from existing person record is not yet enabled.");

			}

			try {

				final PersonUser p = new PersonUser();

				p.userId = personItem.getItemProperty("USERID").getValue().toString();
				p.personId = personItem.getItemProperty("ID").getValue().toString();

				Item item = ItemInitializer.initialize("PARTICIPANTPERSON", "ACTIVE", (RowItem) sqlContainer.getItem(sqlContainer.addItem()));

				item.getItemProperty("REFOBJECTID").setValue(new OracleString(getObjectData().getObjectId()));
				item.getItemProperty("REFOBJECTCLASSID").setValue(new OracleString(getObjectData().getObjectClassId()));

				// Data from personItem...
				item.getItemProperty("PERSONID").setValue(personItem.getItemProperty("ID").getValue());
				item.getItemProperty("PERSONTYPE").setValue(personItem.getItemProperty("PERSONTYPE").getValue());
				item.getItemProperty("FULLNAME").setValue(personItem.getItemProperty("FULLNAME").getValue());
				item.getItemProperty("DISPLAYNAME").setValue(personItem.getItemProperty("DISPLAYNAME").getValue());
				item.getItemProperty("SORTNAME").setValue(personItem.getItemProperty("SORTNAME").getValue());
				item.getItemProperty("EMPLID").setValue(personItem.getItemProperty("EMPLID").getValue());
				item.getItemProperty("BUSINESSUNIT").setValue(personItem.getItemProperty("BUSINESSUNIT").getValue());
				item.getItemProperty("BUSINESSUNITNAME").setValue(personItem.getItemProperty("BUSINESSUNITNAME").getValue());
				item.getItemProperty("SUPERDIVISION").setValue(personItem.getItemProperty("SUPERDIVISION").getValue());
				item.getItemProperty("SUPERDIVISIONNAME").setValue(personItem.getItemProperty("SUPERDIVISIONNAME").getValue());
				item.getItemProperty("DIVISION").setValue(personItem.getItemProperty("DIVISION").getValue());
				item.getItemProperty("DIVISIONNAME").setValue(personItem.getItemProperty("DIVISIONNAME").getValue());
				item.getItemProperty("DEPARTMENT").setValue(personItem.getItemProperty("DEPARTMENT").getValue());
				item.getItemProperty("DEPARTMENTNAME").setValue(personItem.getItemProperty("DEPARTMENTNAME").getValue());
				item.getItemProperty("SUBDEPARTMENT").setValue(personItem.getItemProperty("SUBDEPARTMENT").getValue());
				item.getItemProperty("SUBDEPARTMENTNAME").setValue(personItem.getItemProperty("SUBDEPARTMENTNAME").getValue());
				item.getItemProperty("DEPTID").setValue(personItem.getItemProperty("DEPTID").getValue());
				item.getItemProperty("DEPTIDNAME").setValue(personItem.getItemProperty("DEPTIDNAME").getValue());
				item.getItemProperty("FIRMID").setValue(personItem.getItemProperty("FIRMID").getValue());
				item.getItemProperty("FIRMTYPE").setValue(personItem.getItemProperty("FIRMTYPE").getValue());
				item.getItemProperty("FIRMDISPLAYNAME").setValue(personItem.getItemProperty("FIRMDISPLAYNAME").getValue());
				item.getItemProperty("FIRMLEGALNAME").setValue(personItem.getItemProperty("FIRMLEGALNAME").getValue());
				item.getItemProperty("FIRMSORTNAME").setValue(personItem.getItemProperty("FIRMSORTNAME").getValue());
				item.getItemProperty("PARTICIPANTTYPE").setValue(new OracleString("PROJECT"));

				// Data from role item
				item.getItemProperty("ROLEID").setValue(roleItem.getItemProperty("ID").getValue());

				// Data from form
				item.getItemProperty("ISPRIMARY").setValue(isPrimary);
				item.getItemProperty("NOTIFYINEMERGENCY").setValue(notifyInEmergency);
				item.getItemProperty("ISAPPROVER").setValue(OracleBoolean.FALSE);

				if (logger.isDebugEnabled()) {
					logger.debug("New Participant Person record = {}", item);
				}

				Runnable runnable = new Runnable() {

					@Override
					public void run() {

						Connection conn = null;
						try {

							if (logger.isDebugEnabled()) {
								logger.debug("Attempting to save usergroup and campus user in background thread.");
							}

							conn = Pools.getConnection(Pools.Names.PROJEX);

							newUserGroup(conn, p, personItem, roleItem);
							newCampusUser(conn, p, personItem, roleItem);

							conn.commit();

						} catch (SQLException sqle) {

							Notification.show("Could not create user at this time, please try again later.");

							try {
								conn.rollback();
							} catch (SQLException sqle1) {
								if (logger.isErrorEnabled()) {
									logger.error("Could not rollback");
								}

							}

						} finally {
							Pools.releaseConnection(Pools.Names.PROJEX, conn);
						}

					}

				};

				new Thread(runnable).start();

				return p;

			} catch (UnsupportedOperationException | DataNotFoundException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Could not initialize new item", e);
				}
			}

			return null;

		}

		@SuppressWarnings("unchecked")
		protected PersonUser addRecordForPeopleSoft(final Item selected, final Item roleItem, final OracleBoolean isPrimary, final OracleBoolean notifyInEmergency) {

			if (logger.isDebugEnabled()) {
				logger.debug("Attempt to add record to Participant Person Table");
			}

			try {

				final PersonUser p = new PersonUser();
				p.emailAddress = selected.getItemProperty("EMAILADDRESS").getValue().toString();
				p.invitationCode = invitationCodeGenerator();
				p.personId = newPerson(selected, roleItem);

				Runnable runnable = new Runnable() {

					@Override
					public void run() {

						Connection conn = null;
						try {

							conn = Pools.getConnection(Pools.Names.PROJEX);
							p.userId = newUser(conn, p, roleItem);
							newUserGroup(conn, p, selected, roleItem);
							newCampusUser(conn, p, selected, roleItem);
							updateEmailPhoneAddress(conn, p, selected);
							conn.commit();

						} catch (SQLException e) {

							if (logger.isErrorEnabled()) {
								logger.error("Unable to create new peoplesoft user on first attempt.");
							}

							Pools.releaseConnection(Pools.Names.PROJEX, conn);

							try {

								Thread.sleep(6000);
								conn = Pools.getConnection(Pools.Names.PROJEX);
								p.userId = newUser(conn, p, roleItem);
								newUserGroup(conn, p, selected, roleItem);
								newCampusUser(conn, p, selected, roleItem);
								updateEmailPhoneAddress(conn, p, selected);
								conn.commit();

								Pools.releaseConnection(Pools.Names.PROJEX, conn);

							} catch (InterruptedException e2) {

								if (logger.isErrorEnabled()) {
									logger.error("Thread sleep interrupted", e2);
								}

							} catch (SQLException e1) {

								if (logger.isErrorEnabled()) {
									logger.error("Unable to run save routine a second time", e1);
								}

								Notification.show("Unable to add new user on second attempt - database is too busy.");

							}

						} finally {
							Pools.releaseConnection(Pools.Names.PROJEX, conn);
						}
					}

				};

				new Thread(runnable).start();

				Item item = ItemInitializer.initialize("PARTICIPANTPERSON", "ACTIVE", (RowItem) sqlContainer.getItem(sqlContainer.addItem()));

				item.getItemProperty("REFOBJECTID").setValue(new OracleString(getObjectData().getObjectId()));
				item.getItemProperty("REFOBJECTCLASSID").setValue(new OracleString(getObjectData().getObjectClassId()));

				// Data from personItem...
				item.getItemProperty("PERSONID").setValue(new OracleString(p.personId));

				switch (roleItem.getItemProperty("USERTYPE").getValue().toString()) {
				case "FACILITIES":
					item.getItemProperty("PERSONTYPE").setValue(new OracleString("FACILITIES"));
					break;

				case "CLIENT":
				case "STAKEHOLDER":
					item.getItemProperty("PERSONTYPE").setValue(new OracleString("ENTERPRISE"));
					break;

				case "EXTERNAL":
				case "FIRM":
					item.getItemProperty("PERSONTYPE").setValue(new OracleString("EXTERNAL"));
					break;

				}

				item.getItemProperty("FULLNAME").setValue(selected.getItemProperty("NAME").getValue());
				item.getItemProperty("DISPLAYNAME").setValue(selected.getItemProperty("NAME").getValue());
				item.getItemProperty("SORTNAME").setValue(selected.getItemProperty("NAME").getValue());
				item.getItemProperty("EMPLID").setValue(selected.getItemProperty("EMPLID").getValue());
				item.getItemProperty("BUSINESSUNIT").setValue(selected.getItemProperty("BUSINESSUNIT").getValue());
				item.getItemProperty("BUSINESSUNITNAME").setValue(selected.getItemProperty("BUSINESSUNITNAME").getValue());
				item.getItemProperty("SUPERDIVISION").setValue(selected.getItemProperty("SUPERDIVISION").getValue());
				item.getItemProperty("SUPERDIVISIONNAME").setValue(selected.getItemProperty("SUPERDIVISIONNAME").getValue());
				item.getItemProperty("DIVISION").setValue(selected.getItemProperty("DIVISION").getValue());
				item.getItemProperty("DIVISIONNAME").setValue(selected.getItemProperty("DIVISIONNAME").getValue());
				item.getItemProperty("DEPARTMENT").setValue(selected.getItemProperty("DEPARTMENT").getValue());
				item.getItemProperty("DEPARTMENTNAME").setValue(selected.getItemProperty("DEPARTMENTNAME").getValue());
				item.getItemProperty("SUBDEPARTMENT").setValue(selected.getItemProperty("SUBDEPARTMENT").getValue());
				item.getItemProperty("SUBDEPARTMENTNAME").setValue(selected.getItemProperty("SUBDEPARTMENTNAME").getValue());
				item.getItemProperty("DEPTID").setValue(selected.getItemProperty("DEPTID").getValue());
				item.getItemProperty("DEPTIDNAME").setValue(selected.getItemProperty("DEPTIDNAME").getValue());
				item.getItemProperty("PARTICIPANTTYPE").setValue(new OracleString("PROJECT"));

				// Data from role item
				item.getItemProperty("ROLEID").setValue(roleItem.getItemProperty("ID").getValue());

				// Data from form
				item.getItemProperty("ISPRIMARY").setValue(isPrimary);
				item.getItemProperty("NOTIFYINEMERGENCY").setValue(notifyInEmergency);

				// TODO Set Data from form
				item.getItemProperty("ISAPPROVER").setValue(OracleBoolean.FALSE);

				return p;

			} catch (UnsupportedOperationException | DataNotFoundException e) {

				if (logger.isErrorEnabled()) {
					logger.error("Could not initialize new item", e);
				}

			} catch (SQLException e) {

				if (logger.isErrorEnabled()) {
					logger.error("Error creating person", e);
				}

			}

			return null;

		}

		@SuppressWarnings("unchecked")
		protected PersonUser addRecordForOutsideUser(final Item selected, final Item roleItem, final Item firmItem, final OracleBoolean isPrimary, final OracleBoolean notifyInEmergency) {

			if (logger.isDebugEnabled()) {
				logger.debug("Attempt to add record to Participant Person Table");
			}

			try {

				final PersonUser p = new PersonUser();
				p.emailAddress = selected.getItemProperty("EMAILADDRESS").getValue().toString();
				p.invitationCode = invitationCodeGenerator();
				p.personId = newPersonForOutside(selected, roleItem, firmItem);

				Runnable runnable = new Runnable() {

					@Override
					public void run() {

						Connection conn = null;
						try {

							conn = Pools.getConnection(Pools.Names.PROJEX);
							p.userId = newUser(conn, p, roleItem);
							newUserGroup(conn, p, selected, roleItem);
							newCampusUser(conn, p, selected, roleItem);
							updateEmailPhoneAddress(conn, p, selected);
							conn.commit();

						} catch (SQLException sqle) {

							if (logger.isErrorEnabled()) {
								logger.error("Unable to create new outside user on first attempt.", sqle);
							}

							sqle.printStackTrace();


						} finally {
							Pools.releaseConnection(Pools.Names.PROJEX, conn);
						}

					}
				};

				new Thread(runnable).start();

				try {

					Item item = ItemInitializer.initialize("PARTICIPANTPERSON", "ACTIVE", (RowItem) sqlContainer.getItem(sqlContainer.addItem()));

					item.getItemProperty("REFOBJECTID").setValue(new OracleString(getObjectData().getObjectId()));
					item.getItemProperty("REFOBJECTCLASSID").setValue(new OracleString(getObjectData().getObjectClassId()));

					// Data from personItem...
					item.getItemProperty("PERSONID").setValue(new OracleString(p.personId));

					switch (roleItem.getItemProperty("USERTYPE").getValue().toString()) {

					case "FACILITIES":
						item.getItemProperty("PERSONTYPE").setValue(new OracleString("FACILITIES"));
						break;

					case "CLIENT":
					case "STAKEHOLDER":
						item.getItemProperty("PERSONTYPE").setValue(new OracleString("ENTERPRISE"));
						break;

					case "EXTERNAL":
					case "FIRM":
						item.getItemProperty("PERSONTYPE").setValue(new OracleString("EXTERNAL"));
						break;

					}

					item.getItemProperty("FULLNAME").setValue(selected.getItemProperty("FULLNAME").getValue());
					item.getItemProperty("DISPLAYNAME").setValue(selected.getItemProperty("DISPLAYNAME").getValue());
					item.getItemProperty("SORTNAME").setValue(selected.getItemProperty("SORTNAME").getValue());
					item.getItemProperty("FIRMID").setValue(firmItem.getItemProperty("ID").getValue());
					item.getItemProperty("FIRMTYPE").setValue(firmItem.getItemProperty("FIRMTYPE").getValue());
					item.getItemProperty("FIRMDISPLAYNAME").setValue(firmItem.getItemProperty("DISPLAYNAME").getValue());
					item.getItemProperty("FIRMLEGALNAME").setValue(firmItem.getItemProperty("LEGALNAME").getValue());
					item.getItemProperty("FIRMSORTNAME").setValue(firmItem.getItemProperty("SORTNAME").getValue());
					item.getItemProperty("PARTICIPANTTYPE").setValue(new OracleString("PROJECT"));

					// Data from role item
					item.getItemProperty("ROLEID").setValue(roleItem.getItemProperty("ID").getValue());

					// Data from form
					item.getItemProperty("ISPRIMARY").setValue(isPrimary);
					item.getItemProperty("NOTIFYINEMERGENCY").setValue(notifyInEmergency);

				} catch (UnsupportedOperationException | DataNotFoundException e) {

					e.printStackTrace();

					if (logger.isErrorEnabled()) {
						logger.error("Could not initialize new item", e);
					}
				}

				return p;

			} catch (SQLException sqle) {

				if (logger.isErrorEnabled()) {
					logger.error("Unable to add person/user records", sqle);
				}

			}

			return null;

		}

		public class PersonUser {
			String personId;
			String userId;
			String invitationCode;
			String emailAddress;

			public PersonUser() {

			}

			public PersonUser(String personId, String userId, String invitationCode, String emailAddress) {
				this.personId = personId;
				this.userId = userId;
				this.invitationCode = invitationCode;
				this.emailAddress = emailAddress;
			}

			public String toString() {
				String s = "personId:" + personId + ",userId:" + userId + ", invitationCode:" + invitationCode + ", emailAddress:" + emailAddress;
				return s;
			}
		}

		protected String newPersonForOutside(Item selected, Item roleItem, Item firmItem) throws SQLException {

			if (logger.isDebugEnabled()) {
				logger.debug("newPersonForOutside({},{},{}", selected, roleItem, firmItem);
			}

			java.util.Date start = new java.util.Date();

			String personLastId = null;

			try {

				Item item = ItemInitializer.initialize("PERSON", "ACTIVE", new PropertysetItem());

				// Data from personItem...
				item.addItemProperty("PERSONTYPE", roleItem.getItemProperty("USERTYPE"));
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

			} catch (DataNotFoundException e) {

				if (logger.isErrorEnabled()) {
					logger.error("Could not initialize person", e);
				}

			}

			return personLastId;

		}

		protected String newPerson(Item selected, Item roleItem) throws SQLException {

			if (logger.isDebugEnabled()) {
				logger.debug("newPerson({},{}", selected, roleItem);
			}

			java.util.Date start = new java.util.Date();

			String personLastId = null;

			try {

				Item item = ItemInitializer.initialize("PERSON", "ACTIVE", new PropertysetItem());

				switch (roleItem.getItemProperty("USERTYPE").getValue().toString()) {
				case "FACILITIES":
					item.addItemProperty("PERSONTYPE", new ObjectProperty<OracleString>(new OracleString("FACILITIES")));
					break;

				case "CLIENT":
				case "STAKEHOLDER":
					item.addItemProperty("PERSONTYPE", new ObjectProperty<OracleString>(new OracleString("ENTERPRISE")));
					break;

				case "EXTERNAL":
				case "FIRM":
					item.addItemProperty("PERSONTYPE", new ObjectProperty<OracleString>(new OracleString("EXTERNAL")));
					break;

				}

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
				item.addItemProperty("ROLEID", roleItem.getItemProperty("ID"));
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

			} catch (DataNotFoundException e) {

				if (logger.isErrorEnabled()) {
					logger.error("Exception creating new person", e);
				}

			}

			return personLastId;

		}

		protected String newUser(Connection conn, PersonUser personUser, Item roleItem) throws SQLException {

			if (logger.isDebugEnabled()) {
				logger.debug("newUser({},{},{})", conn, personUser, roleItem);
			}

			java.util.Date start = new java.util.Date();

			Item item = new PropertysetItem();

			// Data from personItem...
			item.addItemProperty("ROWSTAMP", new ObjectProperty<OracleString>(new OracleString("AAAA")));
			item.addItemProperty("PERSONID", new ObjectProperty<OracleString>(new OracleString(personUser.personId)));
			item.addItemProperty("INVITATIONEMAIL", new ObjectProperty<OracleString>(new OracleString(personUser.emailAddress)));

			switch (roleItem.getItemProperty("USERTYPE").getValue().toString()) {
			case "FACILITIES":
				item.addItemProperty("USERTYPE", new ObjectProperty<OracleString>(new OracleString(User.UserType.FACILITIES.name())));
				break;

			case "CLIENT":
			case "STAKEHOLDER":
				item.addItemProperty("USERTYPE", new ObjectProperty<OracleString>(new OracleString(User.UserType.ENTERPRISE.name())));
				break;

			case "EXTERNAL":
			case "FIRM":
				item.addItemProperty("USERTYPE", new ObjectProperty<OracleString>(new OracleString(User.UserType.EXTERNAL.name())));
				break;

			}

			item.addItemProperty("INVITATIONCODE", new ObjectProperty<OracleString>(new OracleString(personUser.invitationCode)));
			item.addItemProperty("INVITED", new ObjectProperty<OracleTimestamp>(OracleTimestamp.now()));
			item.addItemProperty("REGISTRATIONMETHOD", new ObjectProperty<OracleString>(new OracleString("INVITED")));
			item.addItemProperty("ISACTIVE", new ObjectProperty<OracleBoolean>(OracleBoolean.TRUE));

			Users query = new Users();
			query.storeRow(conn, item);
			Object lastId = query.getLastId();

			if (logger.isDebugEnabled()) {
				logger.debug("newUser() = {}, {} ms", lastId, (new java.util.Date().getTime() - start.getTime()));
			}

			return lastId.toString();

		}

		protected void newUserGroup(Connection conn, PersonUser personUser, Item selected, Item roleItem) {

			if (logger.isDebugEnabled()) {
				logger.debug("newUserGroup({},{},{},{})", conn, personUser, selected, roleItem);
			}

			java.util.Date start = new java.util.Date();

			try {

				String groupId = null;

				switch (roleItem.getItemProperty("USERTYPE").getValue().toString()) {

				case "FACILITIES":
					groupId = SecurityGroups.getSecurityGroupID("FACILITIES");
					break;

				case "CLIENT":
				case "STAKEHOLDER":
					groupId = SecurityGroups.getSecurityGroupID("ENTERPRISE");
					break;

				case "EXTERNAL":
				case "FIRM":
					groupId = SecurityGroups.getSecurityGroupID("EXTERNALINVITED");
					break;

				default:
					System.err.println("roleItem = " + roleItem.getItemProperty("USERTYPE").toString());
					break;

				}

				if (logger.isTraceEnabled()) {
					logger.trace("groupID = " + groupId);
				}

				Item item = new PropertysetItem();
				item.addItemProperty("ROWSTAMP", new ObjectProperty<OracleString>(new OracleString("AAAA")));
				item.addItemProperty("SECURITYGROUPID", new ObjectProperty<OracleString>(new OracleString(groupId)));
				item.addItemProperty("USERID", new ObjectProperty<OracleString>(new OracleString(personUser.userId)));

				SecurityGroupUsers query = new SecurityGroupUsers();
				query.storeRow(conn, item);

				if (logger.isDebugEnabled()) {
					logger.debug("newUserGroup(), {}ms", (new java.util.Date().getTime() - start.getTime()));
				}

			} catch (ReadOnlyException | UnsupportedOperationException | SQLException e) {

				e.printStackTrace();

				if (logger.isErrorEnabled()) {
					logger.error("Exception occurred in newUserGroup", e);
				}

			}

		}

		protected void newCampusUser(Connection conn, PersonUser personUser, Item selected, Item roleItem) {

			if (logger.isDebugEnabled()) {
				logger.debug("newCampusUser({},{},{},{})", conn, personUser, selected, roleItem);
			}

			java.util.Date start = new java.util.Date();

			if (getObjectData() != null) {

				try {

					CampusUsers query = new CampusUsers();
					String campusId = null;

					switch (ObjectClasses.getObjectClass(getObjectData().getObjectClassId())) {

					case "AGREEMENT":
					case "CONTRACT":
						campusId = Campuses.getCampusId(Projects.getProjectCampus(getObjectData().getRefObjectId()));
						break;

					case "PROJECT":
						campusId = Campuses.getCampusId(Projects.getProjectCampus(getObjectData().getObjectId()));
						break;

					default:
						break;

					}

					Item item = new PropertysetItem();
					item.addItemProperty("ROWSTAMP", new ObjectProperty<OracleString>(new OracleString("AAAA")));
					item.addItemProperty("CAMPUSID", new ObjectProperty<OracleString>(new OracleString(campusId)));
					item.addItemProperty("USERID", new ObjectProperty<OracleString>(new OracleString(personUser.userId)));
					query.storeRow(conn, item);

					if (logger.isDebugEnabled()) {
						logger.debug("newCampusUser() = {} ms", (new java.util.Date().getTime() - start.getTime()));
					}

				} catch (SQLException e) {

					e.printStackTrace();

					if (!"ORA-00001".equals(e.getErrorCode())) {

						if (logger.isErrorEnabled()) {
							logger.error("Error saving CampusUser", e);
						}
					}

				} catch (DataNotFoundException e) {

					if (logger.isErrorEnabled()) {
						logger.error("Could not retrieve campusId", e);
					}

				}

			} else {

				System.err.println("newCampusUser - objectData is null");
			}

		}

		public void updateEmailPhoneAddress(Connection conn, final PersonUser personUser, final Item personItem) throws SQLException {

			if (logger.isDebugEnabled()) {
				logger.debug("updateEmailPhoneAddress({},{},{})", conn, personUser, personItem);
			}

			java.util.Date start = new java.util.Date();

			try {

				Item emailItem = ItemInitializer.initialize("EMAILADDRESS", "ACTIVE", new PropertysetItem());
				emailItem.addItemProperty("REFOBJECTID", new ObjectProperty<OracleString>(new OracleString(personUser.personId)));
				emailItem.addItemProperty("EMAILTYPE", new ObjectProperty<OracleString>(new OracleString("WORK")));
				emailItem.addItemProperty("EMAILADDRESS", personItem.getItemProperty("EMAILADDRESS"));
				emailItem.addItemProperty("ISPRIMARY", new ObjectProperty<OracleBoolean>(OracleBoolean.TRUE));
				EmailAddresses query = new EmailAddresses();
				query.storeRow(conn, emailItem);

				if (logger.isDebugEnabled()) {
					logger.debug("Updated email address", emailItem);
				}

				Item addressItem = ItemInitializer.initialize("ADDRESS", "ACTIVE", new PropertysetItem());
				addressItem.addItemProperty("REFOBJECTID", new ObjectProperty<OracleString>(new OracleString(personUser.personId)));
				addressItem.addItemProperty("ADDRESSTYPE", new ObjectProperty<OracleString>(new OracleString("WORK")));
				addressItem.addItemProperty("ISPRIMARY", new ObjectProperty<OracleBoolean>(OracleBoolean.TRUE));
				Addresses query_address = new Addresses();
				query_address.storeRow(conn, addressItem);

				if (logger.isDebugEnabled()) {
					logger.debug("Updating Address", addressItem);
				}

				Item phoneItem = ItemInitializer.initialize("PHONE", "ACTIVE", new PropertysetItem());
				phoneItem.addItemProperty("REFOBJECTID", new ObjectProperty<OracleString>(new OracleString(personUser.personId)));
				phoneItem.addItemProperty("PHONETYPE", new ObjectProperty<OracleString>(new OracleString("WORK")));
				phoneItem.addItemProperty("ISPRIMARY", new ObjectProperty<OracleBoolean>(OracleBoolean.TRUE));

				switch (lookupMode) {

				case EXTERNAL:
					phoneItem.addItemProperty("PHONENUM", personItem.getItemProperty("PHONENUM"));
					break;

				case PEOPLESOFT:
				case PERSON:
				default:
					phoneItem.addItemProperty("PHONENUM", personItem.getItemProperty("WORKPHONE"));
					break;
				}

				Phones query_phones = new Phones();
				query_phones.storeRow(conn, phoneItem);

				if (logger.isDebugEnabled()) {
					logger.debug("Updating Phone Number", phoneItem);
				}

			} catch (DataNotFoundException e) {

				e.printStackTrace();

				if (logger.isErrorEnabled()) {
					logger.error("Could not update phone, email, or address", e);
				}

			}

			if (logger.isDebugEnabled()) {
				logger.debug("updateEmailPhoneAddress() = {}ms", (new java.util.Date().getTime() - start.getTime()));
			}

		}

		public void setTableData() {
			setPersonData();
			setRoleData();
		}

		public void layout() {

			setContent(new VerticalLayout() {

				{
					setMargin(true);
					setSpacing(true);
					setSizeFull();

					addComponent(personTable);
					setExpandRatio(personTable, 0.6f);

					addComponent(externalEntry);

					addComponent(roleTable);
					setExpandRatio(roleTable, 0.40f);

					getButtons().addComponent(externalNotFoundButton, 0);
					getButtons().setComponentAlignment(externalNotFoundButton, Alignment.BOTTOM_CENTER);

					getButtons().addComponent(enterpriseNotFoundButton, 0);
					getButtons().setComponentAlignment(enterpriseNotFoundButton, Alignment.BOTTOM_CENTER);
					getButtons().addComponent(showInactive, 0);
					getButtons().addComponent(emergencyContact, 0);
					getButtons().addComponent(isPrimary, 0);
					getButtons().addComponent(inviteUser, 0);

					addComponent(getButtons());
					setComponentAlignment(getButtons(), Alignment.MIDDLE_RIGHT);
				}
			});

		};

		class ProjexPersonsDataLoader implements Runnable {

			@Override
			public void run() {

				try {

					PersonDetails query = new PersonDetails();

					if (OracleBoolean.TRUE.equals(showInactive.getConvertedValue()) && LookupType.USER.equals(lookupType)) {
						query.setMandatoryFilters(new Not(new IsNull("USERID")));
					} else if (LookupType.PERSON.equals(lookupType)) {
						query.setMandatoryFilters(new Compare.Equal("STATUS", "ACTIVE"));
					} else {
						query.setMandatoryFilters(new And(new Not(new IsNull("USERID")), new Compare.Equal("STATUS", "ACTIVE")));
					}

					final OracleContainer sqlContainer = new OracleContainer(query);
					sqlContainer.addOrderBy(new OrderBy("SORTNAME", true));

					personTable.setContainerDataSource(sqlContainer);
					personTable.configure();

				} catch (SQLException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Error executing External Person Details", e);
					}
				}

			}

		}

		class EnterprisePersonsDataLoader implements Runnable {

			@Override
			public void run() {

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

		}

		public void setPersonData() {

			java.util.Date start = new java.util.Date();

			switch (lookupMode) {

			case EXTERNAL:
				break;

			case PERSON:
			default:

				enterpriseNotFoundButton.setVisible(true);
				enterpriseNotFoundButton.setEnabled(true);
				externalNotFoundButton.setVisible(true);
				externalNotFoundButton.setEnabled(true);
				showInactive.setVisible(true);
				showInactive.setEnabled(true);

				if (logger.isDebugEnabled()) {
					logger.debug("Person Lookup Mode");
				}

				personTable.resetColumns();
				personTable.add(new TableColumn("DISPLAYNAME", "Display Name").setWidth(200).setReadOnly(true));
				personTable.add(new TableColumn("FIRMID", "Firm").setConverter(new FirmIdToStringConverter()).setReadOnly(true).setExpandRatio(0.4f));
				personTable.add(new TableColumn("DEPARTMENTNAME", "Department").setExpandRatio(0.6f).setReadOnly(true));
				personTable.add(new TableColumn("JOBTITLE", "Title").setWidth(200).setReadOnly(true));
				personTable.add(new TableColumn("EMAILADDRESS", "Email").setWidth(200).setReadOnly(true));
				personTable.add(new TableColumn("MOBILEPHONE", "Mobile Phone").setWidth(110).setReadOnly(true));
				personTable.add(new TableColumn("WORKPHONE", "Work Phone").setWidth(110).setReadOnly(true));
				personTable.add(new TableColumn("ADDRESS", "Address").setWidth(200).setCollapsed(true).setReadOnly(true));
				personTable.add(new TableColumn("CITY", "City").setWidth(100).setReadOnly(true));
				personTable.add(new TableColumn("STATE", "State").setWidth(50).setCollapsed(true).setReadOnly(true));
				personTable.add(new TableColumn("COUNTRYCODE", "Country").setWidth(50).setCollapsed(true).setReadOnly(true));
				personTable.add(new TableColumn("POSTALCODE", "Postal Code").setWidth(200).setCollapsed(true).setReadOnly(true));
				personTable.add(new TableColumn("STATUSID", "Status").setWidth(50).setCollapsed(true).setConverter(new StatusIdToStringConverter("PERSON")).setReadOnly(true));

				String caption = null;
				if (OracleBoolean.TRUE.equals(showInactive.getConvertedValue()) && LookupType.PERSON.equals(lookupType)) {
					caption = "All Persons";
				} else if (OracleBoolean.TRUE.equals(showInactive.getConvertedValue()) && LookupType.USER.equals(lookupType)) {
					caption = "All Users";
				} else if (LookupType.PERSON.equals(lookupType)) {
					caption = "Active Persons";
				} else {
					caption = "Active Users";
				}
				personTable.setCaption(caption);

				new ProjexPersonsDataLoader().run();

				if (logger.isDebugEnabled()) {
					logger.debug("Elapsed time 1 is {}", new java.util.Date().getTime() - start.getTime());
				}

				break;

			case PEOPLESOFT:

				if (logger.isDebugEnabled()) {
					logger.debug("PeopleSoft Lookup Mode");
				}

				personTable.resetColumns();
				personTable.add(new TableColumn("SSOID", "SSO").setCollapsed(true));
				personTable.add(new TableColumn("NAME", "Display Name"));
				personTable.add(new TableColumn("DEPTID", "DEPTID").setCollapsed(true));
				personTable.add(new TableColumn("DEPTIDNAME", "DEPTIDNAME"));
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

				enterpriseNotFoundButton.setVisible(false);
				enterpriseNotFoundButton.setEnabled(false);
				externalNotFoundButton.setVisible(false);
				externalNotFoundButton.setEnabled(false);
				showInactive.setVisible(false);
				showInactive.setEnabled(false);

				new EnterprisePersonsDataLoader().run();

				if (logger.isDebugEnabled()) {
					logger.debug("Elapsed time 2 is {}", new java.util.Date().getTime() - start.getTime());
				}

				break;

			}

		}

		public void setRoleData() {

			java.util.Date start = new java.util.Date();

			Roles query = new Roles();

			switch (componentType) {

			default:
			case PROJECT:

				switch (lookupMode) {

				case EXTERNAL:
					query.setMandatoryFilters(new And(new Compare.Equal("OBJECTCLASS", componentType.name()), new In("USERTYPE", "FIRM", "EXTERNAL")));
					break;

				case PEOPLESOFT:
					query.setMandatoryFilters(new And(new Compare.Equal("OBJECTCLASS", componentType.name()), new In("USERTYPE", "STAKEHOLDER", "CLIENT", "FACILITIES")));
					break;

				case PERSON:
				default:
					query.setMandatoryFilters(new Compare.Equal("OBJECTCLASS", componentType.name()));
					break;

				}
				break;

			case CONTRACT:

				switch (lookupMode) {

				case EXTERNAL:
					query.setMandatoryFilters(new And(new Compare.Equal("OBJECTCLASS", componentType.name()), new In("USERTYPE", "FIRM", "EXTERNAL")));
					break;

				case PEOPLESOFT:
					query.setMandatoryFilters(new And(new Compare.Equal("OBJECTCLASS", componentType.name()), new In("USERTYPE", "STAKEHOLDER", "CLIENT", "FACILITIES")));
					break;

				case PERSON:
				default:
					query.setMandatoryFilters(new Compare.Equal("OBJECTCLASS", componentType.name()));
					break;

				}
				break;

			case AGREEMENT:

				switch (lookupMode) {

				case EXTERNAL:
					query.setMandatoryFilters(new And(new Compare.Equal("OBJECTCLASS", componentType.name()), new In("USERTYPE", "FIRM", "EXTERNAL")));
					break;

				case PEOPLESOFT:
					query.setMandatoryFilters(new And(new Compare.Equal("OBJECTCLASS", componentType.name()), new In("USERTYPE", "STAKEHOLDER", "CLIENT", "FACILITIES")));
					break;

				case PERSON:
				default:
					query.setMandatoryFilters(new Compare.Equal("OBJECTCLASS", componentType.name()));
					break;

				}
				break;
			}

			try {

				OracleContainer container = new OracleContainer(query);
				container.addOrderBy(new OrderBy("USERTYPE", true));
				container.addOrderBy(new OrderBy("DESCRIPTION", true));
				roleTable.setContainerDataSource(container);
				roleTable.configure();

				if (logger.isDebugEnabled()) {
					logger.debug("Role lookup took {} ms", new java.util.Date().getTime() - start.getTime());
				}

			} catch (SQLException sqle) {
				logger.error("Could not set table data", sqle);
			}

		}

		public void sendEmail() {

			String emailContent = "how to login to Projex4 system";

			Map<String, String> map = new HashMap<String, String>();

			map.put("subject", "introduction of projex4");
			map.put("body", emailContent);

			Map<String, String> image = new HashMap<String, String>();
			List<String> list = new ArrayList<String>();

			SendMail sm = new SendMail(map, list, image);
			sm.send();

		}

		@Override
		public boolean isValid() {

			switch (lookupMode) {
			case EXTERNAL:
				return externalEntry.isValid();

			case PEOPLESOFT:
			case PERSON:
			default:

				if (personTable.getValue() == null) {
					Notification.show("Need to select a Row from the Persons Table");
					if (logger.isDebugEnabled()) {
						logger.debug("Did not select a record from the persons Table");
					}
					return false;
				}

				if (roleTable.getValue() == null) {
					Notification.show("Need to select a Row from the Roles Table");
					if (logger.isDebugEnabled()) {
						logger.debug("Did not select a record from the Roles Table");
					}
					return false;
				}

				return true;

			}

		}

	}

	@Override
	public void afterCommit() {

		if (userAdded) {

			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					if (logger.isDebugEnabled()) {
						logger.debug("Refreshing PersonDetails views");
					}
					PersonDetails.refreshView();
				}

			};

			new Thread(runnable).start();

		}

	}

}
