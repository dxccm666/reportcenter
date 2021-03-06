CREATE SEQUENCE REPORTSEQ;

DROP TABLE REPORTS;

CREATE TABLE REPORTS (
	ID VARCHAR2(10) NOT NULL,
	ROWSTAMP VARCHAR2(10) NOT NULL,
	REPORTNAME VARCHAR2(200),
	DESCRIPTION VARCHAR2(2000),
	HELPTEXT VARCHAR2(200),
	ISACTIVE NUMBER(1) DEFAULT 1 NOT NULL,
	FILENAME VARCHAR2(300),
	REQUESTED TIMESTAMP,
	REQUESTEDBY VARCHAR2(10), /* USERID */
	REQUESTERBUSINESSUNIT VARCHAR2(10),
	REQUESTERBUSINESSUNITNAME VARCHAR2(30),
	REQUESTERSUPERDIVISION VARCHAR2(10),
	REQUESTERSUPERDIVISIONNAME VARCHAR2(30),
	REQUESTERDIVISION VARCHAR2(10),
	REQUESTERDIVISIONNAME VARCHAR2(100),
	REQUESTERDEPARTMENT VARCHAR2(10),
	REQUESTERDEPARTMENTNAME VARCHAR2(100),
	REQUESTERSUBDEPARTMENT VARCHAR2(10),
	REQUESTERSUBDEPARTMENTNAME VARCHAR2(100),
	REQUESTERDEPTID VARCHAR2(10),
	REQUESTERDEPTIDNAME VARCHAR2(30),
	REASON	VARCHAR2(4000),
	REGISTERED TIMESTAMP, 
	REGISTEREDBY VARCHAR2(10), /* USERID */
	CATEGORY VARCHAR2(500),
	TOOL VARCHAR2(50),
	TOOLVERSION VARCHAR2(50),
	RUNTIMEENVIRONMENT VARCHAR2(200),
	PROGRAMVERSION VARCHAR2(30),
	STATUS VARCHAR2(50),
	STATUSED TIMESTAMP,
	STATUSEDBY VARCHAR2(10),
	ISSLOWRUNNING NUMBER(1) DEFAULT 0 NOT NULL,
	EMAILTEMPLATEID VARCHAR2(10),
	PRIMARY KEY (ID),
	CONSTRAINT REPORTS_U UNIQUE (REPORTNAME)
);

CREATE OR REPLACE TRIGGER REPORTS_T 
BEFORE INSERT OR UPDATE ON REPORTS
FOR EACH ROW
DECLARE 
	OBJECTMODIFICATION EXCEPTION;
BEGIN
	IF UPDATING AND NOT :NEW.ROWSTAMP = :OLD.ROWSTAMP THEN
		RAISE OBJECTMODIFICATION;
	END IF;

	SELECT DBMS_RANDOM.STRING('U',10) INTO :NEW.ROWSTAMP FROM DUAL;
	EXCEPTION 
	   WHEN OBJECTMODIFICATION THEN
	   RAISE_APPLICATION_ERROR(-20100,'Record has been modified by another user');
END;
/

create index reports_idx10 on reports (isactive);
create index reports_idx11 on reports (requestedby);
create index reports_idx12 on reports (registeredby);
create index reports_idx15 on reports (category);
create index reports_idx16 on reports (status);
	
	
