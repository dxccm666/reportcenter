CREATE OR REPLACE PACKAGE BODY CORE AS

FUNCTION CAMPUS(VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VCAMPUS IN VARCHAR2, VDESCRIPTION IN VARCHAR2, VISACTIVE IN NUMBER)
RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN
	IF VID IS NULL THEN
		SELECT CAMPUSSEQ.NEXTVAL INTO NID FROM DUAL;
	ELSE 
		NID := VID;
	END IF;

	MERGE INTO CAMPUSES A
	USING (SELECT ID FROM (SELECT ID FROM CAMPUSES WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
	ON (A.ID = B.ID)
	WHEN MATCHED THEN 
	UPDATE SET A.ROWSTAMP = VROWSTAMP, A.CAMPUS = VCAMPUS, A.DESCRIPTION = VDESCRIPTION, A.ISACTIVE = VISACTIVE
	WHEN NOT MATCHED THEN
	INSERT (A.ID, A.ROWSTAMP, A.CAMPUS, A.DESCRIPTION, A.ISACTIVE) VALUES 
		(NID, VROWSTAMP, VCAMPUS, VDESCRIPTION, VISACTIVE);

	IF SQL%ROWCOUNT = 0 THEN
		NID := NULL;
	END IF;

	RETURN NID;
	
END CAMPUS;

PROCEDURE CAMPUS (VCAMPUS IN VARCHAR2, VDESCRIPTION IN VARCHAR2, VISACTIVE IN NUMBER) IS
NID VARCHAR2(10);
BEGIN
	NID := CAMPUS(null, 'AAAA', VCAMPUS, VDESCRIPTION, VISACTIVE);
END CAMPUS; 

FUNCTION LIST (VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VLISTNAME IN VARCHAR2, VDESCRIPTION IN VARCHAR2, VHELPTEXT IN VARCHAR2, VISACTIVE IN NUMBER, VISSYSTEM IN NUMBER) RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN
	IF VID IS NULL THEN 
		SELECT LISTSEQ.NEXTVAL INTO NID FROM DUAL;
	ELSE
		NID := VID;
	END IF;

	MERGE INTO LISTS A
	USING (SELECT ID FROM (SELECT ID FROM LISTS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
	ON (A.ID = B.ID) 
	WHEN MATCHED THEN
	UPDATE SET A.ROWSTAMP=VROWSTAMP, A.LISTNAME=VLISTNAME, A.DESCRIPTION=VDESCRIPTION, A.HELPTEXT=VHELPTEXT, A.ISACTIVE=VISACTIVE, A.ISSYSTEM=VISSYSTEM
	WHEN NOT MATCHED THEN
	INSERT (A.ID, A.ROWSTAMP, A.LISTNAME, A.DESCRIPTION, A.HELPTEXT, A.ISACTIVE, A.ISSYSTEM) VALUES 
		(NID, VROWSTAMP, VLISTNAME, VDESCRIPTION, VHELPTEXT, VISACTIVE, VISSYSTEM);

	IF SQL%ROWCOUNT = 0 THEN
		NID := NULL;
	END IF;

	RETURN NID;

END LIST;

PROCEDURE LIST (VLISTNAME IN VARCHAR2, VDESCRIPTION IN VARCHAR2, VHELPTEXT IN VARCHAR2, VISACTIVE IN NUMBER, VISSYSTEM IN NUMBER) IS
NID VARCHAR2(10);
BEGIN
	NID := LIST(null, 'AAAA', VLISTNAME, VDESCRIPTION, VHELPTEXT, VISACTIVE, VISSYSTEM);
END LIST;

FUNCTION LISTITEM(VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VLISTID IN VARCHAR2, VISALLCAMPUS IN NUMBER, VCAMPUSID IN VARCHAR2, VDISPLAYORDER IN NUMBER, VSYSTEMVALUE IN VARCHAR2, VVALUE IN VARCHAR2, VISDEFAULT IN NUMBER, VDESCRIPTION IN VARCHAR2) RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN
	IF VID IS NULL THEN 
		SELECT LISTITEMSEQ.NEXTVAL INTO NID FROM DUAL;
	ELSE
		NID := VID;
	END IF;

	MERGE INTO LISTITEMS A
	USING (SELECT ID FROM (SELECT ID FROM LISTITEMS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
	ON (A.ID = B.ID) 
	WHEN MATCHED THEN
	UPDATE SET A.ROWSTAMP=VROWSTAMP, A.LISTID = VLISTID, A.ISALLCAMPUS=VISALLCAMPUS, A.CAMPUSID = VCAMPUSID, A.DISPLAYORDER=VDISPLAYORDER, A.SYSTEMVALUE = VSYSTEMVALUE, A.VALUE = VVALUE, A.ISDEFAULT=VISDEFAULT, A.DESCRIPTION=VDESCRIPTION
	WHEN NOT MATCHED THEN
	INSERT (A.ID, A.ROWSTAMP, A.LISTID,  A.ISALLCAMPUS, A.CAMPUSID, A.DISPLAYORDER, A.SYSTEMVALUE, A.VALUE, A.ISDEFAULT, A.DESCRIPTION) VALUES
		(NID, VROWSTAMP, VLISTID, VISALLCAMPUS, VCAMPUSID, VDISPLAYORDER, VSYSTEMVALUE, VVALUE, VISDEFAULT, VDESCRIPTION);

	IF SQL%ROWCOUNT = 0 THEN
		NID := NULL;
	END IF;

	RETURN NID;

END LISTITEM;

PROCEDURE LISTITEM (VLISTNAME IN VARCHAR2, VCAMPUS IN VARCHAR2, VISALLCAMPUS IN NUMBER, VDISPLAYORDER IN NUMBER, VSYSTEMVALUE IN VARCHAR2, VVALUE IN VARCHAR2, VISDEFAULT IN NUMBER, VDESCRIPTION IN VARCHAR2) IS
NID VARCHAR2(10);
NID2 VARCHAR2(10);
LLISTID VARCHAR2(10);
LCAMPUSID VARCHAR2(10);
BEGIN
	
	SELECT ID INTO LLISTID FROM LISTS WHERE LISTNAME = VLISTNAME;
	
	IF VISALLCAMPUS = 1 THEN 
		NID := LISTITEM (null, 'AAAA', LLISTID, VISALLCAMPUS, null, VDISPLAYORDER, VSYSTEMVALUE, VVALUE, VISDEFAULT, VDESCRIPTION);
	ELSE
	
		SELECT ID INTO LCAMPUSID FROM CAMPUSES WHERE CAMPUS = VCAMPUS;
		NID := LISTITEM (null, 'AAAA', LLISTID, 0, LCAMPUSID, VDISPLAYORDER, VSYSTEMVALUE, VVALUE, 0, VDESCRIPTION);
		
	END IF;
	
END LISTITEM;


FUNCTION USER(VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VUSERLOGIN IN VARCHAR2, 
	VFULLNAME IN VARCHAR2, VSORTNAME IN VARCHAR2, VEMPLID IN VARCHAR2,
	VPASSWORD IN VARCHAR2, VISACTIVE IN NUMBER, VSECRETKEY IN VARCHAR2, 
	VCREATED IN DATE, VCREATEDBY IN VARCHAR2) RETURN VARCHAR2
IS
NID VARCHAR2(10);
BEGIN
		
        IF VID IS NULL THEN 
                SELECT USERSEQ.NEXTVAL INTO NID FROM DUAL;
        ELSE
                NID := VID;
        END IF;

        MERGE INTO USERS A
        USING (SELECT ID FROM (SELECT ID FROM USERS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
        ON (A.ID = B.ID)
        WHEN MATCHED THEN 
        UPDATE SET 
		A.ROWSTAMP=VROWSTAMP, A.USERLOGIN = VUSERLOGIN, 
		A.FULLNAME = VFULLNAME, A.SORTNAME = VSORTNAME,
		A.EMPLID = VEMPLID, A.PASSWORD = VPASSWORD, 
		A.ISACTIVE = VISACTIVE, A.SECRETKEY = VSECRETKEY,
		A.CREATED = VCREATED, A.CREATEDBY = VCREATEDBY
        WHEN NOT MATCHED THEN
        INSERT (
			A.ID, A.ROWSTAMP, A.USERLOGIN, 
			A.FULLNAME, A.SORTNAME, A.EMPLID,
			A.PASSWORD, A.ISACTIVE, A.SECRETKEY,
			A.CREATED, A.CREATEDBY
		) VALUES (
			NID, VROWSTAMP, VUSERLOGIN,
		 	VFULLNAME, VSORTNAME, VEMPLID,
			VPASSWORD, VISACTIVE, VSECRETKEY,
			SYSDATE, VCREATEDBY);

        IF SQL%ROWCOUNT = 0 THEN
                NID := NULL;
        END IF;

        RETURN NID;

	EXCEPTION WHEN OTHERS THEN 
		RETURN NID;


END USER;

FUNCTION USERLOGINHISTORY(VID IN VARCHAR2, VUSERID IN VARCHAR2, VLOGGEDIN IN TIMESTAMP, VIPADDRESS IN VARCHAR2) RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN

	SELECT USERLOGINHISTORYSEQ.NEXTVAL INTO NID FROM DUAL;

	INSERT INTO USERLOGINHISTORY VALUES (NID, VUSERID, VLOGGEDIN, VIPADDRESS);

	RETURN NID;

END USERLOGINHISTORY;

FUNCTION CAMPUSUSER( VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VCAMPUSID IN VARCHAR2, VUSERID IN VARCHAR2) RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN
	
		
        IF VID IS NULL THEN 
                SELECT CAMPUSUSERSEQ.NEXTVAL INTO NID FROM DUAL;
        ELSE
                NID := VID;
        END IF;
        
		IF VCAMPUSID IS NULL OR VUSERID IS NULL THEN
			RETURN NID;
		END IF;
        
       	MERGE INTO CAMPUSUSERS A
        USING (SELECT ID FROM (SELECT ID FROM CAMPUSUSERS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
        ON (A.ID = B.ID)
        WHEN MATCHED THEN        
		UPDATE SET A.ROWSTAMP = VROWSTAMP, A.CAMPUSID = VCAMPUSID, A.USERID = VUSERID
		WHEN NOT MATCHED THEN
		INSERT (A.ID, A.ROWSTAMP, A.CAMPUSID, A.USERID) VALUES 
			(NID, VROWSTAMP, VCAMPUSID, VUSERID);
			
        IF SQL%ROWCOUNT = 0 THEN
                NID := NULL;
        END IF;
        
        RETURN NID;
        
        EXCEPTION 
        	WHEN OTHERS THEN 
        		RETURN NID;
        
END CAMPUSUSER;

PROCEDURE CAMPUSUSER(VCAMPUS IN VARCHAR2, VUSERLOGIN IN VARCHAR2) IS
LCAMPUSID VARCHAR2(10);
LUSERID VARCHAR2(10);
NID VARCHAR2(10);
BEGIN
	SELECT ID INTO LUSERID FROM USERS WHERE USERLOGIN = VUSERLOGIN;
	SELECT ID INTO LCAMPUSID FROM CAMPUSES WHERE CAMPUS = VCAMPUS;
	NID := CAMPUSUSER(null,'AAAA',LCAMPUSID, LUSERID);	
END CAMPUSUSER;

PROCEDURE USER(VUSERLOGIN IN VARCHAR2, VFULLNAME IN VARCHAR2, VSORTNAME IN VARCHAR2, VEMPLID IN VARCHAR2,
	VPASSWORD IN VARCHAR2, VISACTIVE IN NUMBER, VSECRETKEY IN VARCHAR2) IS
NID VARCHAR2(10);
BEGIN
	NID := USER(null, 'AAAA', VUSERLOGIN, VFULLNAME, VSORTNAME, VEMPLID, 
	VPASSWORD, VISACTIVE, null, SYSDATE, '1'); 
END USER;

FUNCTION CRONTASK (VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VJAVACLASS IN VARCHAR2, VDESCRIPTION IN VARCHAR2, VCRONEXPRESSION IN VARCHAR2, VISACTIVE IN NUMBER) RETURN VARCHAR2
IS
NID VARCHAR2(10);
BEGIN
        IF VID IS NULL THEN 
                SELECT CRONTASKSEQ.NEXTVAL INTO NID FROM DUAL;
        ELSE
                NID := VID;
        END IF;
        
       	MERGE INTO CRONTASKS A
        USING (SELECT ID FROM (SELECT ID FROM CRONTASKS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
        ON (A.ID = B.ID)
        WHEN MATCHED THEN        
		UPDATE SET A.ROWSTAMP = VROWSTAMP, A.JAVACLASS = VJAVACLASS, A.DESCRIPTION = VDESCRIPTION, A.CRONEXPRESSION = VCRONEXPRESSION, A.ISACTIVE = VISACTIVE
		WHEN NOT MATCHED THEN
		INSERT (A.ID, A.ROWSTAMP, A.JAVACLASS, A.DESCRIPTION, A.CRONEXPRESSION, A.ISACTIVE) VALUES 
			(NID, VROWSTAMP, VJAVACLASS, VDESCRIPTION, VCRONEXPRESSION, VISACTIVE);
			
        IF SQL%ROWCOUNT = 0 THEN
                NID := NULL;
        END IF;

        RETURN NID;
END CRONTASK;

PROCEDURE CRONTASK(VJAVACLASS IN VARCHAR2, VDESCRIPTION IN VARCHAR2, VCRONEXPRESSION IN VARCHAR2, VISACTIVE IN NUMBER) IS
NID VARCHAR2(10);
BEGIN
		NID := CRONTASK(null,'AAAA', VJAVACLASS, VDESCRIPTION, VCRONEXPRESSION, VISACTIVE);
END CRONTASK;	

FUNCTION CRONTASKRUN(VID IN VARCHAR2, VJAVACLASS IN VARCHAR2, VSERVER IN VARCHAR2, VRUNSTART IN TIMESTAMP, VRUNEND IN TIMESTAMP, VSTATUS IN VARCHAR2, VERROR IN VARCHAR2) RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN

        IF VID IS NULL THEN 
                SELECT CRONTASKRUNSEQ.NEXTVAL INTO NID FROM DUAL;
        ELSE
                NID := VID;
        END IF;
        
       	MERGE INTO CRONTASKRUNS A
        USING (SELECT ID FROM (SELECT ID FROM CRONTASKRUNS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
        ON (A.ID = B.ID)
        WHEN MATCHED THEN        
		UPDATE SET A.JAVACLASS = VJAVACLASS, A.SERVER = VSERVER, A.RUNSTART = VRUNSTART, A.RUNEND = VRUNEND, A.STATUS = VSTATUS, A.ERROR = VERROR
		WHEN NOT MATCHED THEN
		INSERT (A.ID, A.JAVACLASS, A.SERVER, A.RUNSTART, A.RUNEND, A.STATUS, A.ERROR) VALUES 
			(NID, VJAVACLASS, VSERVER, VRUNSTART, VRUNEND, VSTATUS, VERROR);
			
        IF SQL%ROWCOUNT = 0 THEN
                NID := NULL;
        END IF;

        RETURN NID;	
	
	
END CRONTASKRUN;


FUNCTION SECURITYGROUP (VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VSECURITYGROUPNAME IN VARCHAR2, VDESCRIPTION IN VARCHAR2, VISACTIVE IN NUMBER) RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN
        IF VID IS NULL THEN 
                SELECT SECURITYGROUPSEQ.NEXTVAL INTO NID FROM DUAL;
        ELSE
                NID := VID;
        END IF;
        
		MERGE INTO SECURITYGROUPS A
        USING (SELECT ID FROM (SELECT ID FROM SECURITYGROUPS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
        ON (A.ID = B.ID)
        WHEN MATCHED THEN       
		UPDATE SET A.ROWSTAMP = VROWSTAMP, A.SECURITYGROUPNAME = VSECURITYGROUPNAME, A.DESCRIPTION = VDESCRIPTION, A.ISACTIVE = VISACTIVE, A.MODIFIED = SYSDATE
        WHEN NOT MATCHED THEN
		INSERT (A.ID,  A.ROWSTAMP, A.SECURITYGROUPNAME, A.DESCRIPTION, A.ISACTIVE, A.MODIFIED ) VALUES 
			(NID,  VROWSTAMP, VSECURITYGROUPNAME, VDESCRIPTION, VISACTIVE, SYSDATE);
			
        IF SQL%ROWCOUNT = 0 THEN
                NID := NULL;
        END IF;

        RETURN NID;
        
END SECURITYGROUP;


PROCEDURE SECURITYGROUP(VSECURITYGROUPNAME IN VARCHAR2, VDESCRIPTION IN VARCHAR2, VISACTIVE IN NUMBER) IS
NID VARCHAR2(10);
BEGIN
	NID := SECURITYGROUP(null, 'AAAA', VSECURITYGROUPNAME, VDESCRIPTION, VISACTIVE);
END SECURITYGROUP;


FUNCTION SECURITYGROUPUSER(VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VSECURITYGROUPID IN VARCHAR2, VUSERID IN VARCHAR2) RETURN VARCHAR2 
IS 
NID VARCHAR2(10);
BEGIN
        IF VID IS NULL THEN 
                SELECT SECURITYGROUPUSERSEQ.NEXTVAL INTO NID FROM DUAL;
        ELSE
                NID := VID;
        END IF;
        
        IF VSECURITYGROUPID IS NULL OR VUSERID IS NULL THEN
        	RETURN NID;
        END IF;
        
       	MERGE INTO SECURITYGROUPUSERS A
        USING (SELECT ID FROM (SELECT ID FROM SECURITYGROUPUSERS WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
        ON (A.ID = B.ID)
        WHEN MATCHED THEN        
		UPDATE SET A.ROWSTAMP = VROWSTAMP, A.SECURITYGROUPID = VSECURITYGROUPID, A.USERID = VUSERID
		WHEN NOT MATCHED THEN
		INSERT (A.ID, A.ROWSTAMP, A.SECURITYGROUPID, A.USERID, A.CREATED) VALUES 
			(NID, VROWSTAMP, VSECURITYGROUPID, VUSERID, SYSDATE);
			
        IF SQL%ROWCOUNT = 0 THEN
                NID := NULL;
        END IF;

        RETURN NID;

	EXCEPTION 
		WHEN OTHERS THEN
			RETURN NID;

END SECURITYGROUPUSER;

PROCEDURE SECURITYGROUPUSER(VSECURITYGROUPNAME IN VARCHAR2, VUSERLOGIN IN VARCHAR2) IS 
NSECURITYGROUPID VARCHAR2(10);
NUSERID VARCHAR2(10);
NID VARCHAR2(10);
BEGIN
	SELECT ID INTO NSECURITYGROUPID FROM SECURITYGROUPS WHERE SECURITYGROUPNAME = VSECURITYGROUPNAME;
	SELECT ID INTO NUSERID FROM USERS WHERE USERLOGIN = VUSERLOGIN;
	NID := SECURITYGROUPUSER(null, 'AAAA', NSECURITYGROUPID, NUSERID);

	EXCEPTION WHEN NO_DATA_FOUND THEN 
		DBMS_OUTPUT.PUT_LINE('ERROR EXECUTING SECURITYGROUPUSER FOR SECURITYGROUP '||VSECURITYGROUPNAME ||' USER '||VUSERLOGIN);
END SECURITYGROUPUSER;



FUNCTION PROPERTY(VID IN VARCHAR2, VROWSTAMP IN VARCHAR2, VREFIDTYPE IN VARCHAR2, VREFID IN VARCHAR2, VPROPERTY IN VARCHAR2, VVALUE IN VARCHAR2)
RETURN VARCHAR2 IS
NID VARCHAR2(10);
BEGIN
	IF VID IS NULL THEN
		SELECT PROPERTYSEQ.NEXTVAL INTO NID FROM DUAL;
	ELSE 
		NID := VID;
	END IF;

	MERGE INTO PROPERTIES A
	USING (SELECT ID FROM (SELECT ID FROM PROPERTIES WHERE ID = NID UNION ALL SELECT NULL ID FROM DUAL) WHERE ROWNUM = 1) B
	ON (A.ID = B.ID)
	WHEN MATCHED THEN 
	UPDATE SET A.ROWSTAMP = VROWSTAMP, A.REFIDTYPE = VREFIDTYPE, A.REFID = VREFID, A.PROPERTY = VPROPERTY, A.VALUE = VVALUE
	WHEN NOT MATCHED THEN
	INSERT (A.ID, A.ROWSTAMP, A.REFIDTYPE, A.REFID, A.PROPERTY, A.VALUE) VALUES 
		(NID, VROWSTAMP, VREFIDTYPE, VREFID, VPROPERTY, VVALUE);

	IF SQL%ROWCOUNT = 0 THEN
		NID := NULL;
	END IF;

	RETURN NID;
	
END PROPERTY;

PROCEDURE SYSTEMPROPERTY (VPROPERTY IN VARCHAR2, VVALUE IN VARCHAR2) IS
NID VARCHAR2(10);
BEGIN
	NID := PROPERTY(null,'AAAA','SYSTEM',null,VPROPERTY, VVALUE);
END SYSTEMPROPERTY;

PROCEDURE CAMPUSPROPERTY (VCAMPUS IN VARCHAR2, VPROPERTY IN VARCHAR2, VVALUE IN VARCHAR2) IS
NID VARCHAR2(10);
NREFID VARCHAR2(10);
BEGIN
	SELECT ID INTO NREFID FROM CAMPUSES WHERE CAMPUS = VCAMPUS;
	NID := PROPERTY(null,'AAAA', 'CAMPUS', NREFID, VPROPERTY, VVALUE);
END CAMPUSPROPERTY;

PROCEDURE USERPROPERTY (VUSERID IN VARCHAR2, VPROPERTY IN VARCHAR2, VVALUE IN VARCHAR2) IS 
NID VARCHAR2(10);
BEGIN
	NID := PROPERTY(null,'AAAA', 'USER', VUSERID, VPROPERTY, VVALUE);
END USERPROPERTY;

PROCEDURE SECURITYGROUPPROPERTY (VSECURITYGROUPNAME IN VARCHAR2, VPROPERTY IN VARCHAR2, VVALUE IN VARCHAR2) IS 
NID VARCHAR2(10);
NREFID VARCHAR2(10);
BEGIN
	SELECT ID INTO NREFID FROM SECURITYGROUPS WHERE SECURITYGROUPNAME = VSECURITYGROUPNAME;
	NID := PROPERTY(null,'AAAA', 'SECURITYGROUP', NREFID, VPROPERTY, VVALUE);
END SECURITYGROUPPROPERTY;

END CORE;
/

show errors;

