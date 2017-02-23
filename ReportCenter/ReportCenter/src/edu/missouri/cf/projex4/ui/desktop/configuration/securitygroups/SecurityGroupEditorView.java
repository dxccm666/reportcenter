/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop.configuration.securitygroups;

import java.sql.SQLException;
import java.util.Locale;

import org.slf4j.Logger;

import c10n.C10N;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.securitygroups.SecurityGroups;
import edu.missouri.cf.projex4.ui.common.OracleBooleanCheckBox;
import edu.missouri.cf.projex4.ui.common.OracleStringTextArea;
import edu.missouri.cf.projex4.ui.common.system.AdvancedTableEditComponent;
import edu.missouri.cf.projex4.ui.common.system.ExportButton;
import edu.missouri.cf.projex4.ui.desktop.StandardEditorView;
import edu.missouri.cf.projex4.ui.desktop.StandardTable;
import edu.missouri.cf.projex4.ui.desktop.TableControlLayout;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class SecurityGroupEditorView extends StandardEditorView {

	private final Logger logger = Loggers.getLogger(SecurityGroupEditorView.class);

	boolean allowswitch = true;

	private Label screendescription;

	private ExportButton exportButton;

	private StandardTable table;

	private AdvancedTableEditComponent editControls;

	private SecurityGroupsText st;

	@PropertyId("SECURITYGROUPNAME")
	private TextField securityGroupName;

	@PropertyId("DESCRIPTION")
	private OracleStringTextArea description;

	@PropertyId("ISACTIVE")
	private OracleBooleanCheckBox isActive;

	@PropertyId("ISSYSTEMSECURITYGROUP")
	private OracleBooleanCheckBox isSystemSecurityGroup;

	//private SecurityGroupDefaultsComponent securityGroupDefaults;

	private SecurityGroupUserTypesComponent securityGroupUserTypes;

	private SecurityGroupApplicationsComponent securityGroupApplications;

	private SecurityGroupUsersComponent securityGroupUsers;

	private String securityGroupId;

	private DefaultNotificationSettingsComponent securityGroupNotificationDefaults;

	/**
	 * 
	 */
	public SecurityGroupEditorView() {
		super();
	}

	@Override
	public void attach() {
		super.attach();
		init();
		layout();
	}

	protected void init() {

		// decide display languages
		if (User.getUser() != null) {
			st = C10N.get(SecurityGroupsText.class, User.getUser().getUserLocale());
		} else {
			st = C10N.get(SecurityGroupsText.class, Locale.ENGLISH);
		}

		screendescription = new Label("<h1>" + st.screenName() + "</h1>", ContentMode.HTML);
		screendescription.addStyleName("projectlisting_label");

		securityGroupName = new TextField() {
			{
				setCaption(st.securityGroupName());
				setDescription(st.securityGroupName_help());
				setWidth("100%");
				setImmediate(true);
			}
		};

		description = new OracleStringTextArea() {
			{
				setCaption(st.description());
				setDescription(st.description_help());
				setWidth("100%");
				setImmediate(true);
			}
		};

		isActive = new OracleBooleanCheckBox() {
			{
				setCaption(st.isActive());
				setDescription(st.isActive_help());
				setImmediate(true);
			}
		};

		isSystemSecurityGroup = new OracleBooleanCheckBox() {
			{
				setCaption(st.isSystemSecurityGroup());
				setDescription(st.isSystemSecurityGroup_help());
				setImmediate(true);
			}
		};

		// Can we combine defaults and usertypes here?
		
		securityGroupUserTypes = new SecurityGroupUserTypesComponent();
		securityGroupUsers = new SecurityGroupUsersComponent();

		securityGroupApplications = new SecurityGroupApplicationsComponent();
		securityGroupNotificationDefaults = new DefaultNotificationSettingsComponent();

		// TODO Need to add actual security rights table here.
		
		controls.setEditor(this);
		controls.setVisible(true);


	}

	private void setEditableComponents() {
		
		clearComponents();

		addEditableComponent(securityGroupName);
		addEditableComponent(description);
		addEditableComponent(isActive);
		addEditableComponent(isSystemSecurityGroup);

		
		addDependentProjexEditor(securityGroupUserTypes);
		addDependentProjexEditor(securityGroupUsers);
		addDependentProjexEditor(securityGroupApplications);
		addDependentProjexEditor(securityGroupNotificationDefaults);

	}

	private void layout() {

		TabSheet tabs = new TabSheet() {
			{
				addStyleName("tabs");

				addTab(new VerticalLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setCaption("Security Group");
						
						addComponent(new TableControlLayout() {
							{
								addLeftComponent(controls);
							}
						});
						
						// Need to add in edit controls.
						addComponent(securityGroupName);
						addComponent(new HorizontalLayout() {
							{
								setSpacing(true);
								addComponent(isActive);
								addComponent(isSystemSecurityGroup);
							}
						});
						addComponent(description);
					}
				});

				addTab(new VerticalLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setCaption("Security Rights");
						addComponent(securityGroupApplications);
					}
				});

				addTab(new VerticalLayout() {
					{

						setMargin(true);
						setSpacing(true);
						setCaption("Group Membership");
						
						addComponent(securityGroupUserTypes);
						addComponent(securityGroupUsers);

					}
				});

				addTab(new VerticalLayout() {
					{
						setMargin(true);
						setSpacing(true);
						setHeight("600px");
						setCaption("Default Notification Settings");
						securityGroupNotificationDefaults.setSizeFull();
						securityGroupNotificationDefaults.getTable().setSizeFull();
						// securityGroupNotificationDefaults.setExpandRatio(securityGroupNotificationDefaults.getTable(), 1.0f);
						addComponent(securityGroupNotificationDefaults);
						// setExpandRatio(securityGroupNotificationDefaults,1.0f);
					}
				});

			}
		};

		addComponent(tabs);
		setExpandRatio(tabs, 1.0f);

		setEditableComponents();

	}

	@Override
	public void setScreenData(String parameters) {

		// Not getting parameters passed from opener. Why?

		if (parameters == null) {
			System.err.println("parameters were null");
			return;
		}

		securityGroupId = parameters;
		System.err.println("++++++++++++parameters = " + parameters);

		try {

			SecurityGroups query = new SecurityGroups();
			query.setSecurityGroupId(parameters);
			OracleContainer sqlContainer = new OracleContainer(query);
			
			clearOracleContainers();
			addOracleContainer(sqlContainer);

			Item item = sqlContainer.getItemByProperty("ID", parameters);
			bind(item);

			securityGroupApplications.setData(securityGroupId);			
			securityGroupUserTypes.setData(securityGroupId);
			securityGroupUsers.setData(securityGroupId);
			securityGroupNotificationDefaults.setData(securityGroupId);

		} catch (SQLException e) {
			logger.error("Error setting query", e);
		}
		
		controls.setEditingState(EditingState.READONLY);

	}

}
