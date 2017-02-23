package edu.missouri.cf.projex4.ui.desktop.login;

import java.sql.SQLException;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.common.PersonDetails;
import edu.missouri.cf.projex4.ui.desktop.LoginTopBarView;

@SuppressWarnings("serial")
public class VerifyInternalPersonalInfoView extends LoginTopBarView {
	
	@PropertyId("FULLNAME")
	private TextField fullnameField;
	@PropertyId("DISPLAYNAME")
	private TextField displaynameField;
	@PropertyId("SORTNAME")
	private TextField sortnameField;
	@PropertyId("GENDER")
	private TextField genderField;
	@PropertyId("EMPLID")
	private TextField emplidField;
	@PropertyId("BUSINESSUNIT")
	private TextField businiessUnitField;
	@PropertyId("BUSINESSUNITNAME")
	private TextField businiessUnitNameField;
	@PropertyId("SUPERDIVISION")
	private TextField superDivisionField;
	@PropertyId("SUPERDIVISIONNAME")
	private TextField superDivisionNameField;
	@PropertyId("DIVISION")
	private TextField divisionField;
	@PropertyId("DIVISIONNAME")
	private TextField divisionnameField;
	@PropertyId("DEPARTMENT")
	private TextField departmentField;
	@PropertyId("DEPARTMENTNAME")
	private TextField departmentNameField;
	@PropertyId("SUBDEPARTMENT")
	private TextField subDepartmentField;
	@PropertyId("SUBDEPARTMENTNAME")
	private TextField subDepartmentNameField;
	@PropertyId("DEPTID")
	private TextField deptidField;
	@PropertyId("DEPTIDNAME")
	private TextField deptidNameField;
	@PropertyId("LANGUAGECODE")
	private TextField languageCodeBox;
	@PropertyId("JOBTITLE")
	private TextField jobTileField;

	private Label label_1;

	private Button nextButton;
	private String id;
	private OracleContainer sqlContainer;
	private Item item;

	public VerifyInternalPersonalInfoView() {
		setSizeFull();
		init();
		layout();

	}

	public void init() {
		
		fullnameField = new TextField("Full Name");
		displaynameField = new TextField("Display Name");
		sortnameField = new TextField("Sort Name");
		genderField = new TextField("Gender");
		emplidField = new TextField("EMPLID");
		businiessUnitField = new TextField("Business Unit");
		businiessUnitNameField = new TextField("Business Unit Name");
		superDivisionField = new TextField("Super Division");
		superDivisionNameField = new TextField("Super Division Name");
		divisionField = new TextField("Division");
		divisionnameField = new TextField("Division Name");
		departmentField = new TextField("Department");
		departmentNameField = new TextField("Department Name");
		subDepartmentField = new TextField("SubDepartment");
		subDepartmentNameField = new TextField("SubDepartment Name");
		deptidField = new TextField("DEPTID");
		deptidNameField = new TextField("DEPTID Name");
		jobTileField = new TextField("Job Title");

		languageCodeBox = new TextField("Language Code");

		nextButton = new Button("Next") {
			{
				setIcon(new ThemeResource("icons/chalkwork/basic/arrow_right_16x16.png"));
				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

						Projex4UI.get().getProjexViewNavigator().navigateTo(ProjexViewProvider.Views.HOME);
						updateInit();
					}

				});
			}
		};

		label_1 = new Label(
				"<h2>Please verify your personal information</h2> <br>" + "You can change your Display Name and Language Code.<br>"
						+ "If you find other personal information is not correct, please contact with Administrator.<br><br>",
				ContentMode.HTML);

	}

	public void layout() {

		VerticalLayout root = new VerticalLayout() {
			{
				setSpacing(true);
				setMargin(true);
				addComponent(label_1);

				addComponent(new HorizontalLayout() {
					{

						setSpacing(true);
						addComponent(fullnameField);
						addComponent(displaynameField);
						addComponent(sortnameField);

					}
				});

				addComponent(new HorizontalLayout() {
					{

						setSpacing(true);
						addComponent(genderField);
						addComponent(emplidField);
						addComponent(jobTileField);
						addComponent(languageCodeBox);
					}
				});

				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);
						addComponent(businiessUnitField);
						addComponent(businiessUnitNameField);
						addComponent(superDivisionField);
						addComponent(superDivisionNameField);
					}
				});

				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);
						addComponent(divisionField);
						addComponent(divisionnameField);
						addComponent(departmentField);
						addComponent(departmentNameField);
					}
				});

				addComponent(new HorizontalLayout() {
					{

						setSpacing(true);
						addComponent(subDepartmentField);
						addComponent(subDepartmentNameField);
						addComponent(deptidField);
						addComponent(deptidNameField);
					}
				});

				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);
						addComponent(nextButton);

					}
				});

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

	@Override
	public void enter(ViewChangeEvent event) {
		id = event.getParameters();
		try {
			PersonDetails query = new PersonDetails();
			query.setMandatoryFilters(new Compare.Equal("ID", id));
			sqlContainer = new OracleContainer(query);
			item = sqlContainer.getItemByProperty("ID", id);

			setItemDataSource(item);

		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Error setting persondetails query", e);
			}
		}

	}

	private void updateInit() {

	}

}
