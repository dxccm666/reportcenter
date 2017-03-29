package edu.missouri.cf.projex4.ui.desktop.configuration.users;

import java.sql.SQLException;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.ui.common.OracleBooleanCheckBox;
import edu.missouri.cf.projex4.ui.common.OraclePopupDateField;
import edu.missouri.cf.projex4.ui.common.OracleTimestampField;
import edu.missouri.cf.projex4.ui.common.system.StandardFormEditControls;
import edu.missouri.cf.projex4.ui.desktop.StandardEditorView;
import edu.missouri.operations.reportcenter.data.Users;

@SuppressWarnings("serial")
public class UserEditorView extends StandardEditorView {

	@PropertyId("ID")
	private TextField userId;

	@PropertyId("EMPLID")
	private TextField emplid;

	@PropertyId("FULLNAME")
	private TextField fullName;

	@PropertyId("DISPLAYNAME")
	private TextField displayName;

	@PropertyId("SORTNAME")
	private TextField sortName;

	@PropertyId("JOBTITLE")
	private TextField jobTitle;

	@PropertyId("PERSONTYPE")
	private TextField personType;

	@PropertyId("GENDER")
	private TextField gender;

	@PropertyId("LANGUAGECODE")
	private TextField languageCode;

	@PropertyId("BUSINESSUNITNAME")
	private TextField busUnitName;

	@PropertyId("SUPERDIVISION")
	private TextField superDivision;

	@PropertyId("SUPERDIVISIONNAME")
	private TextField superDivisionName;

	@PropertyId("DIVISIONNAME")
	private TextField divisionName;

	@PropertyId("DEPTIDNAME")
	private TextField departmentName;

	@PropertyId("SUBDEPTIDNAME")
	private TextField subDepartmentName;

	@PropertyId("NOTIFYBYEMAIL")
	private OracleBooleanCheckBox notifyByEmail;

	@PropertyId("NOTIFYINEMERGENCY")
	private OracleBooleanCheckBox notifyEmergency;

	@PropertyId("ISVIP")
	private OracleBooleanCheckBox isVip;

	@PropertyId("PROGRAMVERSION")
	private TextField programVersion;
	
	@PropertyId("INVITATIONEMAIL")
	private TextField invitationEmail;
	
	@PropertyId("INVITATIONEMAILED")
	private OracleTimestampField invitationEmailed;
	
	@PropertyId("INVITED")
	private OraclePopupDateField invited;
	
	@PropertyId("INITIALIZED")
	private OracleBooleanCheckBox initialized;
	
	@PropertyId("PASSWORDEXPIRATION")
	private OraclePopupDateField passwordExpiration;
	
	@PropertyId("FORCEEXPIRATION")
	private OracleBooleanCheckBox forceExpiration;
	
	@PropertyId("REGISTRATIONMETHOD")
	private TextField registrationMethod;
	
	@PropertyId("USERTYPE")
	private TextField userType;
	
	@PropertyId("ISACTIVE")
	private OracleBooleanCheckBox isActive;
	
	@PropertyId("EMAILSCHEDULE")
	private TextField emailSchedule;
	
	@PropertyId("VERIFIER")
	private TextField verifier;
	
	@PropertyId("USERLOGIN")
	private TextField userLogin;
		
	private Users userQuery;
	private OracleContainer userSqlContainer;

	private UserApplicationsComponent userApplicationsComponent;
	private UserLoginHistoryComponent userLoginComponent;
	private UserProjectAssignComponent userProjectComponent;

	private UserSecurityGroupsComponent securityGroupsComponent;

	private UserCampusesComponent campusUsers;
	private DefaultReviewComponent defaultReviewerComponent;

	private Label screendescription;
	private Label layoutdescription;

	private Label layoutdescription_user;

	private UserPropertiesComponent userPropertiesComponent;

	public UserEditorView() {
		super();
	}

	@Override
	public void attach() {
		super.attach();
		init();
		layout();
	}

	private void init() {
		
		// TODO Convert to C10N
		setApplicationName(ProjexViewProvider.Views.USEREDITOR);
		
		screendescription = new Label("<h1>User Details</h1>", ContentMode.HTML);
		screendescription.addStyleName("projectlisting_label");
		
		
		emplid = new TextField() {
			{
				setCaption("EMPLID");
				setDescription("");
				setNullRepresentation("");
			}
		};
		
		userId = new TextField() {
			{
				setCaption("USERID");
				setDescription("");
				setNullRepresentation("");
			}
		};
		
		fullName = new TextField() {
			{
				setCaption("Full Name");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
			}
		};
		displayName = new TextField() {
			{
				setCaption("Display Name");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
			}
		};
		sortName = new TextField() {
			{
				setCaption("Sort Name");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
			}
		};
		jobTitle = new TextField() {
			{
				setCaption("Job Title");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
			}
		};
		personType = new TextField() {
			{
				setCaption("User Type");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
			}
		};
		gender = new TextField() {
			{
				setCaption("Gender");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
			}
		};
		languageCode = new TextField() {
			{
				setCaption("Language Code");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
			}
		};
		busUnitName = new TextField() {
			{
				setCaption("Business Unit");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
			}
		};
		superDivisionName = new TextField() {
			{
				setCaption("Super Division");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
			}
		};
		divisionName = new TextField() {
			{
				setCaption("Division");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
			}
		};
		departmentName = new TextField() {
			{
				setCaption("Department");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
			}
		};
		subDepartmentName = new TextField() {
			{
				setCaption("Sub-Department");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
			}
		};
		notifyByEmail = new OracleBooleanCheckBox() {
			{
				setCaption("Notify by email");
				setDescription("");
				setImmediate(true);
			}
		};
		notifyEmergency = new OracleBooleanCheckBox() {
			{
				setCaption("Notify in Emergency");
				setDescription("");
				setImmediate(true);
			}
		};
		isVip = new OracleBooleanCheckBox() {
			{
				setCaption("Is VIP?");
				setDescription("");
				setImmediate(true);
			}
		};
		programVersion = new TextField() {
			{
				setCaption("Program Version");
				setDescription("");
				setImmediate(true);
				setNullRepresentation("");
			}
		};
		
		invitationEmail = new TextField("Invitation Email"){
			{
				setNullRepresentation("");
			}
		};
		
		invitationEmailed = new OracleTimestampField("Invitation Emailed");
		invited = new OraclePopupDateField("Invited Date");		
		initialized = new OracleBooleanCheckBox("is intialized?");		
		passwordExpiration = new OraclePopupDateField("Password Expiration Date");		
		forceExpiration = new OracleBooleanCheckBox("Force to Expiration?");		
		userType = new TextField("User Type"){
			{
				setNullRepresentation("");
			}
		};		
		isActive = new OracleBooleanCheckBox("is Active?");
		emailSchedule = new TextField("Email Schedule"){
			{
				setNullRepresentation("");
			}
		};
	    verifier = new TextField("Verifier"){
	    	{
	    		setNullRepresentation("");
	    	}
	    };
	    registrationMethod = new TextField("Registration Method"){
	    	{
	    		setNullRepresentation("");
	    	}
	    };
	    userLogin= new TextField("User Login"){
	    	{
	    		setNullRepresentation("");
	    	}
	    };

		layoutdescription = new Label("<h2>Person Information</h2>", ContentMode.HTML);
		
		layoutdescription_user = new Label("<h2>User Information</h2>", ContentMode.HTML);

		controls.setEditor(this);
		controls.setVisible(true);
		
		userLoginComponent = new UserLoginHistoryComponent();
		userProjectComponent = new UserProjectAssignComponent();

		userApplicationsComponent = new UserApplicationsComponent();
		securityGroupsComponent = new UserSecurityGroupsComponent();
		campusUsers = new UserCampusesComponent();
		
		defaultReviewerComponent = new DefaultReviewComponent();

		userPropertiesComponent = new UserPropertiesComponent();
		
		((StandardFormEditControls) controls).setStatusChangeButtonEnabled(false);
		((StandardFormEditControls) controls).setStatusChangeButtonVisible(false);
		
		setEditableComponents();
		
	}

	private void layout() {

		TabSheet tabs = new TabSheet() {
			{
				addStyleName("tabs");
				
				addTab(new VerticalLayout() {
					{
						
						setMargin(true);
						setSpacing(true);
						setSizeFull();
						setCaption("Details");
												
						addComponent(screendescription);						
						addComponent(controls);
						addComponent(layoutdescription);
						
						
						addComponent(userId);
						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(personType);
								addComponent(emplid);
								addComponent(programVersion);
								//addComponent(annotationColor);
							}
						});
						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(fullName);
								addComponent(displayName);
								addComponent(sortName);
							}
						});
						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(gender);
								addComponent(languageCode);
								addComponent(jobTitle);
							}
						});
						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(busUnitName);
								addComponent(superDivisionName);
								addComponent(divisionName);
							}
						});
						
						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(departmentName);
								addComponent(subDepartmentName);
							}
						});
						
						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(notifyEmergency);
								addComponent(isVip);
							}
						});
						addComponent(layoutdescription_user);

						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(userLogin);
								addComponent(registrationMethod);
							}
						});
						
						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(invitationEmail);
								addComponent(invited);
								addComponent(invitationEmailed);
							}
						});
						
						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(userType);
								addComponent(passwordExpiration);
								addComponent(forceExpiration);
								
							}
						});
						
						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(emailSchedule);
								addComponent(verifier);
							}
						});
						
						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(initialized);
								addComponent(isActive);
								addComponent(notifyByEmail);
							}
						});
						
					}
				});
				addTab(new VerticalLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setCaption("Login History");
						addComponent(userLoginComponent);
					}
				});
				
				addTab(new VerticalLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setCaption("Application Rights");
						addComponent(userApplicationsComponent);
					}
				});
				addTab(new VerticalLayout() {

					{
						setMargin(true);
						setSpacing(true);
						setCaption("SecurityGroups");
						addComponent(securityGroupsComponent);
					}
				});
				
				addTab(new VerticalLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setCaption("Assigned Project(s)");
						addComponent(userProjectComponent);
					}
				});
				
				addTab(new VerticalLayout() {

					{
						setMargin(true);
						setSpacing(true);
						setCaption("Campuses");
						addComponent(campusUsers);
					}
				});
				addTab(new VerticalLayout() {

					{
						setMargin(true);
						setSpacing(true);
						setCaption("Properties");
						addComponent(userPropertiesComponent);
					}
				});
				
				addTab(new VerticalLayout() {

					{
						setMargin(true);
						setSpacing(true);
						setCaption("Default Reviewers");
						addComponent(defaultReviewerComponent);
					}
				});
			}
		};
		addComponent(tabs);
		setExpandRatio(tabs, 1.0f);
	}

	protected void setEditableComponents() {
		
		clearComponents();
		
		addNonEditableComponent(userId);
		addNonEditableComponent(emplid);
		addNonEditableComponent(fullName);
		addNonEditableComponent(displayName);
		addNonEditableComponent(sortName);
		addNonEditableComponent(jobTitle);
		addNonEditableComponent(personType);
		addNonEditableComponent(gender);
		addNonEditableComponent(languageCode);
		addNonEditableComponent(busUnitName);
		addNonEditableComponent(superDivisionName);
		addNonEditableComponent(divisionName);
		addNonEditableComponent(departmentName);
		addNonEditableComponent(subDepartmentName);
		addNonEditableComponent(notifyByEmail);
		addNonEditableComponent(notifyEmergency);
		addNonEditableComponent(programVersion);		
		addNonEditableComponent(isVip);
		
		addEditableComponent(invitationEmail);
		addEditableComponent(invited);
		addNonEditableComponent(initialized);
		addEditableComponent(passwordExpiration);
		addEditableComponent(forceExpiration);
		addEditableComponent(userType);
		addEditableComponent(isActive);
		addEditableComponent(emailSchedule);
	    addNonEditableComponent(verifier);
	    addNonEditableComponent(registrationMethod);
	    addNonEditableComponent(userLogin);
		addDependentProjexEditor(campusUsers);
		addDependentProjexEditor(userPropertiesComponent);
		addDependentProjexEditor(userProjectComponent);
		addDependentProjexEditor(userLoginComponent);
		addDependentProjexEditor(securityGroupsComponent);
		addDependentProjexEditor(defaultReviewerComponent);
	}

	@Override
	public void setScreenData(String parameters) {
		
		setObjectId(parameters);
	
		userLoginComponent.setData(parameters);
		
		userQuery = new Users();
		userQuery.setMandatoryFilters(new Compare.Equal("ID", parameters));
		
		
			
		try {
			
			userSqlContainer = new OracleContainer(userQuery);
			addOracleContainer(userSqlContainer);
			
			
			Item item = userSqlContainer.getItemByProperty("ID", parameters);
			String userID = item.getItemProperty("ID").getValue().toString();	
			bind(item);
			
			
		
			
			securityGroupsComponent.setData(userID);			
            userProjectComponent.setData(userID);           
			userLoginComponent.setData(userID);			
			campusUsers.setData(userID);			
			userApplicationsComponent.setData(userID);		
			userPropertiesComponent.setData(userID);			
			defaultReviewerComponent.setData(userID);
			
			
		} catch (SQLException e) {
			logger.error(e.getSQLState());
		}

		controls.setEditingState(EditingState.READONLY);
		
	}
	
	@Override
	public void afterCommit() {
		
	}

}
