CREATE OR REPLACE PACKAGE BODY REPORT AS

FUNCTION REPORT(VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VREPORTNAME IN VARCHAR2, VDESCRIPTION IN VARCHAR2,VHELPTEXT IN VARCHAR2, VISSCREENREPORT IN NUMBER, VSCREENNAME IN VARCHAR2, VCAMPUSID IN VARCHAR2, VISALLCAMPUS IN NUMBER, VISACTIVE IN NUMBER,
VFILENAME IN VARCHAR2, VREQUESTED IN TIMESTAMP, VREQUESTEDBY IN VARCHAR2, VREASON IN VARCHAR2, VREGISTERED IN TIMESTAMP, VREGISTEREDBY IN VARCHAR2, VCATEGORY IN VARCHAR2)
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
	UPDATE SET A.ROWSTAMP = VROWSTAMP, A.REPORTNAME = VREPORTNAME,A.DESCRIPTION = VDESCRIPTION, A.HELPTEXT = VHELPTEXT , A.ISSCREENREPORT = VISSCREENREPORT, A.SCREENNAME = VSCREENNAME, A.CAMPUSID = VCAMPUSID, A.ISALLCAMPUS = VISALLCAMPUS, A.ISACTIVE = VISACTIVE,
		A.FILENAME = VFILENAME, A.REQUESTED = VREQUESTED, A.REQUESTEDBY = VREQUESTEDBY, A.REASON = VREASON, A.REGISTERED = VREGISTERED, A.REGISTEREDBY = VREGISTEREDBY, A.CATEGORY = VCATEGORY
	WHEN NOT MATCHED THEN
	INSERT (A.ID, A.ROWSTAMP, A.REPORTNAME, A.DESCRIPTION, A.HELPTEXT, A.ISSCREENREPORT, A.SCREENNAME, A.CAMPUSID, A.ISALLCAMPUS, A.ISACTIVE, A.FILENAME, A.REQUESTED, A.REQUESTEDBY, A.REASON, A.REGISTERED, A.REGISTEREDBY, A.CATEGORY) VALUES 
		(NID, VROWSTAMP, VREPORTNAME, VDESCRIPTION, VHELPTEXT, VISSCREENREPORT, VSCREENNAME, VCAMPUSID, VISALLCAMPUS, VISACTIVE, VFILENAME, VREQUESTED, VREQUESTEDBY, VREASON, VREGISTERED, VREGISTEREDBY, VCATEGORY);

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

PROCEDURE REPORT (VREPORTNAME IN VARCHAR2, VDESCRIPTION IN VARCHAR2, VHELPTEXT IN VARCHAR2, VISSCREENREPORT IN NUMBER, VSCREENNAME IN VARCHAR2, VCAMPUSID IN VARCHAR2, VISALLCAMPUS IN NUMBER, VISACTIVE IN NUMBER, VFILENAME IN VARCHAR2, VREQUESTED in TIMESTAMP, VREQUESTEDBY IN VARCHAR2, VREASON IN VARCHAR2, VREGISTERED IN TIMESTAMP, VREGISTEREDBY IN VARCHAR2, VCATEGORY IN VARCHAR2) IS
NID VARCHAR2(10);
BEGIN
	NID := REPORT(null, 'AAAA', VREPORTNAME, VDESCRIPTION, VHELPTEXT, VISSCREENREPORT, VSCREENNAME, VCAMPUSID, VISALLCAMPUS, VISACTIVE, VFILENAME, VREQUESTED, VREQUESTEDBY, VREASON, VREGISTERED, VREGISTEREDBY, VCATEGORY);
END REPORT; 

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

PROCEDURE REPORTPARAMETER (VREPORTNAME IN VARCHAR2, VPARAMETERNUMBER IN NUMBER, VPARAMETER IN VARCHAR2, VPARAMETERTYPE IN VARCHAR2, VLISTNAME IN VARCHAR2) 
IS 
NID VARCHAR2(10);
LREPORTID VARCHAR2(10);
BEGIN
	
	SELECT ID INTO LREPORTID FROM REPORTS WHERE REPORTNAME = VREPORTNAME;
	NID := REPORTPARAMETER(null, 'AAAA', LREPORTID, VPARAMETERNUMBER, VPARAMETER, VPARAMETERTYPE, VLISTNAME); 
 	
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

PROCEDURE SECURITYGROUPREPORT(VSECURITYGROUPNAME IN VARCHAR2, VREPORTNAME IN VARCHAR2)
IS 
NID VARCHAR2(10);
LREPORTID VARCHAR2(10);
LSECURITYGROUPID VARCHAR2(10);
BEGIN
	
	SELECT ID INTO LSECURITYGROUPID FROM SECURITYGROUPS WHERE SECURITYGROUPNAME = VSECURITYGROUPNAME;
	SELECT ID INTO LREPORTID FROM REPORTS WHERE REPORTNAME = VREPORTNAME;
	NID := SECURITYGROUPREPORT(null, 'AAAA', LSECURITYGROUPID, LREPORTID); 
 		
END SECURITYGROUPREPORT;

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