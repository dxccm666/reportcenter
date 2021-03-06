CREATE OR REPLACE PACKAGE BODY REPORT AS

FUNCTION REPORT(VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, 
	VREPORTNAME IN VARCHAR2, VDESCRIPTION IN VARCHAR2,
	VHELPTEXT IN VARCHAR2, VISACTIVE IN NUMBER,
	VFILENAME IN VARCHAR2, VREQUESTED IN TIMESTAMP, 
	VREQUESTEDBY IN VARCHAR2, 
	VREQUESTERBUSINESSUNIT IN VARCHAR2,
	VREQUESTERBUSINESSUNITNAME IN VARCHAR2,
	VREQUESTERSUPERDIVISION IN VARCHAR2,
	VREQUESTERSUPERDIVISIONNAME IN VARCHAR2,
	VREQUESTERDIVISION IN VARCHAR2,
	VREQUESTERDIVISIONNAME IN VARCHAR2,
	VREQUESTERDEPARTMENT IN VARCHAR2,
	VREQUESTERDEPARTMENTNAME IN VARCHAR2,
	VREQUESTERSUBDEPARTMENT IN VARCHAR2,
	VREQUESTERSUBDEPARTMENTNAME IN VARCHAR2,
	VREQUESTERDEPTID IN VARCHAR2,
	VREQUESTERDEPTIDNAME IN VARCHAR2,
	VREASON IN VARCHAR2, 
	VREGISTERED IN TIMESTAMP, VREGISTEREDBY IN VARCHAR2, 
	VCATEGORY IN VARCHAR2, VTOOL IN VARCHAR2,
	VTOOLVERSION IN VARCHAR2, VRUNTIMEENVIRONMENT IN VARCHAR2,
	VPROGRAMVERSION IN VARCHAR2, VSTATUS IN VARCHAR2,
	VSTATUSED IN TIMESTAMP, VSTATUSEDBY IN VARCHAR2,
	VISSLOWRUNNING IN NUMBER,
	VEMAILTEMPLATEID IN VARCHAR2
	)
RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN
	IF VID IS NULL THEN
		SELECT REPORTSEQ.NEXTVAL INTO NID FROM DUAL;
	ELSE 
		NID := VID;
	END IF;

	MERGE INTO REPORTS A
	USING (SELECT ID FROM (SELECT ID FROM REPORTS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
	ON (A.ID = B.ID)
	WHEN MATCHED THEN 
	UPDATE SET 
		A.ROWSTAMP = VROWSTAMP, A.REPORTNAME = VREPORTNAME,
		A.DESCRIPTION = VDESCRIPTION, A.HELPTEXT = VHELPTEXT,
		A.ISACTIVE = VISACTIVE, A.FILENAME = VFILENAME, 
		A.REQUESTED = VREQUESTED, A.REQUESTEDBY = VREQUESTEDBY, 
		A.REQUESTERBUSINESSUNIT = VREQUESTERBUSINESSUNIT,
		A.REQUESTERBUSINESSUNITNAME = VREQUESTERBUSINESSUNITNAME,
		A.REQUESTERSUPERDIVISION = VREQUESTERSUPERDIVISION,
		A.REQUESTERSUPERDIVISIONNAME = VREQUESTERSUPERDIVISIONNAME,
		A.REQUESTERDIVISION = VREQUESTERDIVISION,
		A.REQUESTERDIVISIONNAME = VREQUESTERDIVISIONNAME,
		A.REQUESTERDEPARTMENT = VREQUESTERDEPARTMENT,
		A.REQUESTERDEPARTMENTNAME = VREQUESTERDEPARTMENTNAME,
		A.REQUESTERSUBDEPARTMENT = VREQUESTERSUBDEPARTMENT,
		A.REQUESTERSUBDEPARTMENTNAME = VREQUESTERSUBDEPARTMENTNAME,
		A.REQUESTERDEPTID = VREQUESTERDEPTID,
		A.REQUESTERDEPTIDNAME = VREQUESTERDEPTIDNAME,
		A.REASON = VREASON, A.REGISTERED = VREGISTERED, 
		A.REGISTEREDBY = VREGISTEREDBY, A.CATEGORY = VCATEGORY,
		A.TOOL = VTOOL, A.TOOLVERSION = VTOOLVERSION,
		A.RUNTIMEENVIRONMENT = VRUNTIMEENVIRONMENT,
		A.PROGRAMVERSION = VPROGRAMVERSION,
		A.STATUS = VSTATUS,
		A.STATUSED = VSTATUSED, 
		A.STATUSEDBY = VSTATUSEDBY,
		A.ISSLOWRUNNING = VISSLOWRUNNING,
		A.EMAILTEMPLATEID = VEMAILTEMPLATEID
	WHEN NOT MATCHED THEN
	INSERT (
		A.ID, A.ROWSTAMP, A.REPORTNAME, A.DESCRIPTION, A.HELPTEXT,
		A.ISACTIVE, A.FILENAME,
		A.REQUESTED, A.REQUESTEDBY, 
		A.REQUESTERBUSINESSUNIT, A.REQUESTERBUSINESSUNITNAME, 
		A.REQUESTERSUPERDIVISION, A.REQUESTERSUPERDIVISIONNAME,
		A.REQUESTERDIVISION, A.REQUESTERDIVISIONNAME,
		A.REQUESTERDEPARTMENT, A.REQUESTERDEPARTMENTNAME, 
		A.REQUESTERSUBDEPARTMENT, A.REQUESTERSUBDEPARTMENTNAME,
		A.REQUESTERDEPTID, A.REQUESTERDEPTIDNAME,
		A.REASON, A.REGISTERED, A.REGISTEREDBY, A.CATEGORY,
		A.TOOL, A.TOOLVERSION, A.RUNTIMEENVIRONMENT,
		A.PROGRAMVERSION, A.STATUS, A.STATUSED,
		A.STATUSEDBY, A.ISSLOWRUNNING, A.EMAILTEMPLATEID
	) VALUES (
		NID, VROWSTAMP, VREPORTNAME, VDESCRIPTION, VHELPTEXT, 
		VISACTIVE, VFILENAME,
		VREQUESTED, VREQUESTEDBY,
		VREQUESTERBUSINESSUNIT, VREQUESTERBUSINESSUNITNAME,
		VREQUESTERSUPERDIVISION, VREQUESTERSUPERDIVISIONNAME,
		VREQUESTERDIVISION, VREQUESTERDIVISIONNAME,
		VREQUESTERDEPARTMENT, VREQUESTERDEPARTMENTNAME,
		VREQUESTERSUBDEPARTMENT, VREQUESTERSUBDEPARTMENTNAME,
		VREQUESTERDEPTID, VREQUESTERDEPTIDNAME,
		VREASON, VREGISTERED, VREGISTEREDBY, VCATEGORY,
		VTOOL, VTOOLVERSION, VRUNTIMEENVIRONMENT, VPROGRAMVERSION,
		VSTATUS, VSTATUSED, VSTATUSEDBY, VISSLOWRUNNING, VEMAILTEMPLATEID
	);

	IF SQL%ROWCOUNT = 0 THEN
		NID := NULL;
	END IF;

	RETURN NID;
	
END REPORT;

FUNCTION REPORTRUNHISTORY (VUSERID IN VARCHAR2, VREPORTID IN VARCHAR2, VFILEFORMAT IN VARCHAR2, VFILELOCATION IN VARCHAR2 ) 
RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN
	SELECT REPORTRUNHISTORYSEQ.NEXTVAL INTO NID FROM DUAL;

	INSERT INTO REPORTRUNHISTORY (ID, USERID, REPORTID, FILEFORMAT, RANON, FILELOCATION) VALUES (NID, VUSERID, VREPORTID, VFILEFORMAT, SYSTIMESTAMP, VFILELOCATION);
	
	RETURN NID;
END REPORTRUNHISTORY;

FUNCTION REPORTRUNPARAMETER (VID IN VARCHAR2, VREPORTRUNHISTORYID IN VARCHAR2, VPARAMETERNUMBER IN NUMBER, VPARAMETERNAME IN VARCHAR2, VPARAMETERVALUE IN VARCHAR2 ) 
RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN
	SELECT REPORTRUNPARAMETERSSEQ.NEXTVAL INTO NID FROM DUAL;

	INSERT INTO REPORTRUNPARAMETERS (ID, REPORTRUNHISTORYID, PARAMETERNUMBER, PARAMETERNAME, PARAMETERVALUE) 
		VALUES (NID, VREPORTRUNHISTORYID, VPARAMETERNUMBER, VPARAMETERNAME, VPARAMETERVALUE);
	
	RETURN NID;
END REPORTRUNPARAMETER;

FUNCTION REPORTPARAMETER (VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VREPORTID IN VARCHAR2, VPARAMETERNUMBER IN NUMBER, VPARAMETER IN VARCHAR2, VPARAMETERTYPE IN VARCHAR2, VLISTNAME IN VARCHAR2) RETURN VARCHAR2
IS
NID VARCHAR2(10);
BEGIN
	
	IF VID IS NULL THEN
		SELECT REPORTPARAMETERSEQ.NEXTVAL INTO NID FROM DUAL;
	ELSE 
		NID := VID;
	END IF;

	MERGE INTO REPORTPARAMETERS A
	USING (SELECT ID FROM (SELECT ID FROM REPORTPARAMETERS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
	ON (A.ID = B.ID)
	WHEN MATCHED THEN 
	UPDATE SET A.ROWSTAMP = VROWSTAMP, A.REPORTID = VREPORTID, A.PARAMETERNUMBER = VPARAMETERNUMBER, A.PARAMETER = VPARAMETER, A.PARAMETERTYPE = VPARAMETERTYPE , A.LISTNAME = VLISTNAME
	WHEN NOT MATCHED THEN
	INSERT (A.ID, A.ROWSTAMP, A.REPORTID, A.PARAMETERNUMBER, A.PARAMETER, A.PARAMETERTYPE, A.LISTNAME) VALUES 
		(NID, VROWSTAMP, VREPORTID, VPARAMETERNUMBER, VPARAMETER, VPARAMETERTYPE, VLISTNAME);

	IF SQL%ROWCOUNT = 0 THEN
		NID := NULL;
	END IF;

	RETURN NID;
	
END REPORTPARAMETER;

FUNCTION SECURITYGROUPREPORT(VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VSECURITYGROUPID IN VARCHAR2, VREPORTID IN VARCHAR2) RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN
	
	IF VID IS NULL THEN
		SELECT SECURITYGROUPREPORTSEQ.NEXTVAL INTO NID FROM DUAL;
	ELSE 
		NID := VID;
	END IF;

	MERGE INTO SECURITYGROUPREPORTS A
	USING (SELECT ID FROM (SELECT ID FROM SECURITYGROUPREPORTS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
	ON (A.ID = B.ID)
	WHEN MATCHED THEN 
	UPDATE SET A.ROWSTAMP = VROWSTAMP, A.SECURITYGROUPID = VSECURITYGROUPID,  A.REPORTID = VREPORTID
	WHEN NOT MATCHED THEN
	INSERT (A.ID, A.ROWSTAMP, A.SECURITYGROUPID, A.REPORTID) VALUES 
		(NID, VROWSTAMP, VSECURITYGROUPID, VREPORTID);

	IF SQL%ROWCOUNT = 0 THEN
		NID := NULL;
	END IF;

	RETURN NID;	
	
END SECURITYGROUPREPORT;

FUNCTION CAMPUSREPORT(VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VCAMPUSID IN VARCHAR2, VREPORTID IN VARCHAR2) RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN
	
	IF VID IS NULL THEN
		SELECT CAMPUSREPORTSEQ.NEXTVAL INTO NID FROM DUAL;
	ELSE 
		NID := VID;
	END IF;

	MERGE INTO CAMPUSREPORTS A
	USING (SELECT ID FROM (SELECT ID FROM CAMPUSREPORTS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
	ON (A.ID = B.ID)
	WHEN MATCHED THEN 
	UPDATE SET A.ROWSTAMP = VROWSTAMP, A.CAMPUSID = VCAMPUSID,  A.REPORTID = VREPORTID
	WHEN NOT MATCHED THEN
	INSERT (A.ID, A.ROWSTAMP, A.CAMPUSID, A.REPORTID) VALUES 
		(NID, VROWSTAMP, VCAMPUSID, VREPORTID);

	IF SQL%ROWCOUNT = 0 THEN
		NID := NULL;
	END IF;

	RETURN NID;	
	
END CAMPUSREPORT;

FUNCTION REPORTCRONTASK(VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VREPORTID IN VARCHAR2, VSCHEDULEDBY IN VARCHAR2, VSCHEDULED IN TIMESTAMP, 
	VCRONEXPRESSION IN VARCHAR2, VISACTIVE IN NUMBER, VFILEFORMAT IN VARCHAR2, VISONETIME IN NUMBER) RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN
	
	IF VID IS NULL THEN
		SELECT REPORTCRONTASKSEQ.NEXTVAL INTO NID FROM DUAL;
	ELSE 
		NID := VID;
	END IF;

	MERGE INTO REPORTCRONTASKS A
	USING (SELECT ID FROM (SELECT ID FROM REPORTCRONTASKS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
	ON (A.ID = B.ID)
	WHEN MATCHED THEN 
	UPDATE SET A.ROWSTAMP = VROWSTAMP, A.REPORTID = VREPORTID, A.SCHEDULEDBY = VSCHEDULEDBY, A.SCHEDULED = VSCHEDULED,
		A.CRONEXPRESSION = VCRONEXPRESSION, A.ISACTIVE = VISACTIVE, A.FILEFORMAT = VFILEFORMAT, A.ISONETIME = VISONETIME
	WHEN NOT MATCHED THEN
	INSERT (A.ID, A.ROWSTAMP, A.REPORTID, A.SCHEDULEDBY, A.SCHEDULED, A.CRONEXPRESSION, A.ISACTIVE, A.FILEFORMAT, A.ISONETIME) VALUES 
		(NID, VROWSTAMP, VREPORTID, VSCHEDULEDBY, VSCHEDULED, VCRONEXPRESSION, VISACTIVE, VFILEFORMAT, VISONETIME);

	IF SQL%ROWCOUNT = 0 THEN
		NID := NULL;
	END IF;

	RETURN NID;	
	
END REPORTCRONTASK;

FUNCTION REPORTCRONTASKPARAMETER(VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VREPORTCRONTASKID IN VARCHAR2, VPARAMETERNUMBER IN NUMBER, VPARAMETERTYPE IN VARCHAR2, VPARAMETERNAME IN VARCHAR2, VPARAMETERVALUE IN VARCHAR2) RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN
	
	IF VID IS NULL THEN
		SELECT REPORTCRONTASKPARAMETERSEQ.NEXTVAL INTO NID FROM DUAL;
	ELSE 
		NID := VID;
	END IF;

	MERGE INTO REPORTCRONTASKPARAMETERS A
	USING (SELECT ID FROM (SELECT ID FROM REPORTCRONTASKPARAMETERS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
	ON (A.ID = B.ID)
	WHEN MATCHED THEN 
	UPDATE SET A.ROWSTAMP = VROWSTAMP, A.REPORTCRONTASKID = VREPORTCRONTASKID, A.PARAMETERNUMBER = VPARAMETERNUMBER, A.PARAMETERTYPE = VPARAMETERTYPE, A.PARAMETERNAME = VPARAMETERNAME, A.PARAMETERVALUE = VPARAMETERVALUE 
	WHEN NOT MATCHED THEN
	INSERT (A.ID, A.ROWSTAMP, A.REPORTCRONTASKID, A.PARAMETERNUMBER, A.PARAMETERTYPE, A.PARAMETERNAME, A.PARAMETERVALUE) VALUES 
		(NID, VROWSTAMP, VREPORTCRONTASKID, VPARAMETERNUMBER, VPARAMETERTYPE, VPARAMETERNAME, VPARAMETERVALUE);

	IF SQL%ROWCOUNT = 0 THEN
		NID := NULL;
	END IF;

	RETURN NID;	
	
END REPORTCRONTASKPARAMETER;

FUNCTION REPORTCRONTASKEMAIL(VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VREPORTCRONTASKID IN VARCHAR2, VEMAILADDRESS IN VARCHAR2) RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN
	
	IF VID IS NULL THEN
		SELECT REPORTCRONTASKEMAILSEQ.NEXTVAL INTO NID FROM DUAL;
	ELSE 
		NID := VID;
	END IF;

	MERGE INTO REPORTCRONTASKEMAILS A
	USING (SELECT ID FROM (SELECT ID FROM REPORTCRONTASKEMAILS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
	ON (A.ID = B.ID)
	WHEN MATCHED THEN 
	UPDATE SET A.ROWSTAMP = VROWSTAMP, A.REPORTCRONTASKID = VREPORTCRONTASKID, A.EMAILADDRESS = VEMAILADDRESS 
	WHEN NOT MATCHED THEN
	INSERT (A.ID, A.ROWSTAMP, A.REPORTCRONTASKID, A.EMAILADDRESS) VALUES 
		(NID, VROWSTAMP, VREPORTCRONTASKID, VEMAILADDRESS);

	IF SQL%ROWCOUNT = 0 THEN
		NID := NULL;
	END IF;

	RETURN NID;	
	
END REPORTCRONTASKEMAIL;

END REPORT;
/

sho err
