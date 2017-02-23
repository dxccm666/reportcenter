package edu.missouri.cf.projex4.ui.desktop.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.data.common.Persons;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.data.system.core.objects.Objects;
import edu.missouri.cf.projex4.ui.common.system.StandardComboBox;
import edu.missouri.cf.projex4.ui.desktop.StandardEditorView;



@SuppressWarnings("serial")
public class InternalVerifyDetailsView extends StandardEditorView{
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@PropertyId("FULLNAME")
	private TextField fullName;

	@PropertyId("DISPLAYNAME")
	private TextField displayName;

	@PropertyId("SORTNAME")
	private TextField sortName;

	@PropertyId("JOBTITLE")
	private TextField jobTitle;

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
	
	@PropertyId("DEPARTMENT")
	private TextField department;

	@PropertyId("DEPTIDNAME")
	private TextField departmentName;

	@PropertyId("SUBDEPTIDNAME")
	private TextField subDepartmentName;

	@PropertyId("NOTIFYBYEMAIL")
	private TextField notifyByEmail;

	@PropertyId("NOTIFYINEMERGENCY")
	private TextField notifyInEmergency;

	@PropertyId("ISVIP")
	private CheckBox isVip;

	@PropertyId("PROGRAMVERSION")
	private TextField programVersion;
	
	private StandardComboBox personType;
	
	public InternalVerifyDetailsView() {
		setSizeFull();
		init();
		layout();	

	}
	
	public void init() {

		
		displayName = new TextField("Display Name");
		notifyByEmail = new TextField("Notify By Email");
		notifyInEmergency = new TextField("Notify In Emergency");
		isVip = new CheckBox("VIP?");

		fullName = new TextField("Full Name");
		sortName = new TextField("Sort Name");
		gender = new TextField("Gender");
		jobTitle = new TextField("Job Title");

		busUnitName = new TextField("BusinessUnit");
		superDivisionName = new TextField("SuperDivision");
		divisionName = new TextField("Division");
		departmentName = new TextField("Department");
		subDepartmentName = new TextField("SubDepartment");				
	
	}
	
	public void layout() {

		VerticalLayout wrapper = new VerticalLayout() {
			{
				setMargin(true);
				setSpacing(true);
				
				addComponent(new HorizontalLayout() {
					{
						setMargin(true);
						setSpacing(true);
						addComponent(displayName);
						// addComponent(notifyByEmail);
						addComponent(notifyInEmergency);
						addComponent(personType);
						addComponent(isVip);
					}
				});
				addComponent(new HorizontalLayout() {
					{
						setMargin(true);
						setSpacing(true);
						addComponent(fullName);
						addComponent(sortName);
						addComponent(gender);
						addComponent(jobTitle);
						addComponent(departmentName);
					}
				});
				addComponent(new HorizontalLayout() {
					{
						setMargin(true);
						setSpacing(true);
						addComponent(busUnitName);
						addComponent(superDivisionName);
						addComponent(subDepartmentName);
					}
				});
			}
		};
		addComponent(wrapper);
		setExpandRatio(wrapper, 1.0f);
	
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setScreenData(String parameters) {
		
		ObjectData object = Objects.getObjectDataFromUUID(parameters);
		
		Persons query = new Persons();
		query.setMandatoryFilters(new Compare.Equal("ID", object.getObjectId()));		

	}


}
