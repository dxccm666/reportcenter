package edu.missouri.operations.reportcenter.ui.views.configuration.users;

import java.sql.SQLException;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.operations.reportcenter.data.CampusUsers;
import edu.missouri.operations.reportcenter.data.SecurityGroupUsers;
import edu.missouri.operations.reportcenter.data.SecurityGroups;
import edu.missouri.operations.reportcenter.data.UserLoginHistory;
import edu.missouri.operations.reportcenter.data.Users;
import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.OracleBooleanCheckBox;
import edu.missouri.operations.ui.OracleStringTextArea;
import edu.missouri.operations.ui.OracleTimestampField;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.TableColumn;
import edu.missouri.operations.ui.desktop.buttons.AddButton;
import edu.missouri.operations.ui.desktop.buttons.CancelButton;
import edu.missouri.operations.ui.desktop.buttons.DeleteButton;
import edu.missouri.operations.ui.desktop.buttons.SaveButton;

public class UserEditorView extends TopBarView {

	@PropertyId("ID")
	private TextField id;

	@PropertyId("USERLOGIN")
	private TextField userLogin;

	@PropertyId("FULLNAME")
	private TextField fullName;

	@PropertyId("SORTNAME")
	private TextField sortName;

	@PropertyId("EMPLID")
	private TextField emplID;

	@PropertyId("ISACTIVE")
	private OracleBooleanCheckBox isActive;

	@PropertyId("CREATED")
	private OracleTimestampField created;

	@PropertyId("CREATEDBY")
	private TextField createdBy;

	private FieldGroup binder;
	private OracleContainer container;
	private Item item;

	private StandardTable loginHistoryTable;

	private StandardTable securityGroupTable;

	private SaveButton saveButton;

	private CancelButton cancelButton;

	private AddButton addGroupButton;

	private DeleteButton deleteGroupButton;

	private OracleContainer sguContainer;

	private OracleContainer ulhContainer;

	private StandardTable campusTable;

	private AddButton addCampusButton;

	private DeleteButton deleteCampusButton;

	private OracleContainer cuContainer;

	public UserEditorView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void attach() {
		super.attach();

		id = new TextField() {
			{
				setCaption("#");
				setReadOnly(true);
			}
		};

		userLogin = new TextField() {
			{
				setCaption("User Login");
				setRequired(true);
			}
		};

		fullName = new TextField() {
			{
				setCaption("Full Name");
				setRequired(true);
			}
		};

		sortName = new TextField() {
			{
				setCaption("Sort Name");
				setRequired(true);
			}
		};

		emplID = new TextField() {
			{
				setCaption("PeopleSoft ID");
				setRequired(true);
			}
		};

		isActive = new OracleBooleanCheckBox() {
			{
				setCaption("Active?");
			}
		};

		created = new OracleTimestampField() {
			{
				setCaption("Date Created");
				setReadOnly(true);
			}
		};

		createdBy = new TextField() {
			{
				setCaption("Created By");
				setRequired(true);
			}
		};

		loginHistoryTable = new StandardTable() {
			{
				add(new TableColumn("LOGGEDIN", "Logged In"));
				add(new TableColumn("IPADDRESS", "IP Address"));
			}
		};

		securityGroupTable = new StandardTable() {
			{
				setMultiSelect(false);
				add(new TableColumn("SECURITYGROUPID", "Security Group Id"));
				add(new TableColumn("SECURITYGROUPNAME", "Security Group"));
				add(new TableColumn("CREATED", "Created"));

			}
		};

		campusTable = new StandardTable() {
			{
				setMultiSelect(false);
				add(new TableColumn("CAMPUSID", "ID"));
				add(new TableColumn("CAMPUS", "Campus"));
				add(new TableColumn("DESCRIPTION", "Description"));
			}
		};

		saveButton = new SaveButton() {
			{
			}
		};

		cancelButton = new CancelButton() {
			{
			}
		};

		addGroupButton = new AddButton();
		deleteGroupButton = new DeleteButton();

		addCampusButton = new AddButton();
		deleteCampusButton = new DeleteButton();

		VerticalLayout layout = new VerticalLayout() {
			{
				setSizeFull();
				addComponent(new HorizontalLayout() {
					{
						addComponent(saveButton);
						addComponent(cancelButton);
					}
				});

				TabSheet tabs = new TabSheet() {
					{
						setSizeFull();
						addTab(new VerticalLayout() {
							{
								setCaption("User Info");
								setMargin(true);
								setSpacing(true);

								addComponent(new HorizontalLayout() {
									{
										setSpacing(true);
										setWidth("100%");
										addComponent(id);
										addComponent(userLogin);
										userLogin.setWidth("100%");
										setExpandRatio(userLogin, 0.20f);
										addComponent(fullName);
										fullName.setWidth("100%");
										setExpandRatio(fullName, 0.30f);
										addComponent(sortName);
										sortName.setWidth("100%");
										setExpandRatio(sortName, 0.30f);
									}
								});
								addComponent(new HorizontalLayout() {
									{
										setSpacing(true);
										addComponent(emplID);
										addComponent(created);
										addComponent(createdBy);
									}
								});
								addComponent(isActive);
							}
						});

						addTab(new VerticalLayout() {
							{
								setCaption("Campuses");
								setMargin(true);
								setSpacing(true);
								setSizeFull();
								addComponent(new HorizontalLayout() {
									{
										addComponent(addCampusButton);
										addComponent(deleteCampusButton);
									}
								});
								
								addComponent(campusTable);
								setExpandRatio(campusTable, 1.0f);
								
							}
						});

						addTab(new VerticalLayout() {
							{
								setCaption("Security Groups");
								setMargin(true);
								setSpacing(true);
								setSizeFull();

								addComponent(new HorizontalLayout() {
									{
										addComponent(addGroupButton);
										addComponent(deleteGroupButton);
									}
								});

								addComponent(securityGroupTable);
								setExpandRatio(securityGroupTable, 1.0f);
							}
						});

						addComponent(new VerticalLayout() {
							{
								setSizeFull();
								setCaption("Login History");
								addComponent(loginHistoryTable);
								setExpandRatio(loginHistoryTable, 1.0f);
							}
						});
					}
				};
				addComponent(tabs);
				setExpandRatio(tabs, 1.0f);

			}
		};

		addInnerComponent(layout);

	}

	@Override
	public void enter(ViewChangeEvent event) {

		try {
			// parameters should be securitygroup id value.
			String parameters = event.getParameters();
			logger.debug("Parameters inside enter = {}", parameters);

			Users query = new Users();
			query.setMandatoryFilters(new Compare.Equal("ID", parameters));

			container = new OracleContainer(query);

			if (container.size() == 1) {

				item = container.getItem(container.getIdByIndex(0));
				logger.debug("item = {}", item);

				binder = new FieldGroup(item);
				binder.bindMemberFields(this);

				id.setReadOnly(true);
				emplID.setReadOnly(true);
				created.setReadOnly(true);
				createdBy.setReadOnly(true);
				
				CampusUsers cuQuery = new CampusUsers();
				cuQuery.setUserId(parameters);
				cuContainer = new OracleContainer(cuQuery);
				campusTable.setContainerDataSource(cuContainer);
				campusTable.configure();

				SecurityGroupUsers sguQuery = new SecurityGroupUsers();
				sguQuery.setUserId(parameters);
				sguContainer = new OracleContainer(sguQuery);
				securityGroupTable.setContainerDataSource(sguContainer);
				securityGroupTable.configure();

				UserLoginHistory ulhQuery = new UserLoginHistory();
				ulhQuery.setUserId(parameters);
				ulhContainer = new OracleContainer(ulhQuery);
				loginHistoryTable.setContainerDataSource(ulhContainer);
				loginHistoryTable.configure();

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {

		}

	}

}
