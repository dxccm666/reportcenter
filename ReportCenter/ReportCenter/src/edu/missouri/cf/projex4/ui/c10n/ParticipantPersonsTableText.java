package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface ParticipantPersonsTableText {
	
	@En("")
	String contextHelp();
	
	@En("Person")
	String person();
	
	@En("Person Full Name")
	String fullName();
	
	@En("Display Name")
	String displayName();
	
	@En("Sort Name")
	String sortName();
	
	@En("Person Type")
	String personType();
	
	@En("Business Unit")
	String businessUnit();
	
	@En("Business Unit Name")
	String businessUnitName();
	
	@En("Superdivision")
	String superDivision();
	
	@En("Superdivision Name")
	String superDivisionName();
	
	@En("Division")
	String division();
	
	@En("Division Name")
	String divisionName();
	
	@En("Department")
	String department();
	
	@En("Department Name")
	String departmentName();
	
	@En("Subdepartment")
	String subDepartment();
	
	@En("Subdepartment Name")
	String subDepartmentName();
	
	@En("Dept ID")
	String deptId();
	
	@En("Dept ID Name")
	String deptIdName();
	
	@En("Firm Id")
	String firmId();
	
	@En("Firm Type")
	String firmType();
	
	@En("Firm")
	String firmDisplayName();
	
	@En("Firm Legal Name")
	String firmLegalName();
	
	@En("Firm Sort Name")
	String firmSortName();
	
	@En("Role")
	String role();
	
	@En("Primary?")
	String primary();
	
	@En("Emergency?")
	String notifyInEmergency();
	
	@En("Work Phone #")
	String workPhone();
	
	@En("Mobile Phone #")
	String mobilePhone();
	
	@En("Fax")
	String faxNumber();
	
	@En("Email Address")
	String emailAddress();
	
	@En("Address")
	String address();
	
	@En("City")
	String city();
	
	@En("County")
	String county();
	
	@En("State")
	String state();
	
	@En("Country Code")
	String countryCode();
	
	@En("Postal Code")
	String postalCode();
	
	@En("Time Zone")
	String timeZone();
	
	@En("Approver?")
	String approver();
	
	@En("User Type")
	String userType();
	
}
