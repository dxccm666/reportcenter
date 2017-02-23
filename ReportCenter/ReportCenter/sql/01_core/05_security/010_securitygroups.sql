/*
	SECURITYGROUPS
	
	SECURITYGROUPS ARE COLLECTIONS OF USERS NOT PERSONS - 
	SECURITY IS APPLIED FOR THE USER IN A MORE GLOBAL FASHION.


	INDIVIDUALS MAY HAVE SPECIAL RIGHTS JUST FOR THEM.

*/

CREATE SEQUENCE SECURITYGROUPSEQ;

CREATE TABLE SECURITYGROUPS (
	ID VARCHAR2(10) NOT NULL, 
	ROWSTAMP VARCHAR2(10) NOT NULL,
	SECURITYGROUPNAME VARCHAR2(50) NOT NULL,
	DESCRIPTION VARCHAR2(200) NOT NULL,
	ISACTIVE NUMBER(1) DEFAULT 1 NOT NULL,
	ISSYSTEMSECURITYGROUP NUMBER(1) DEFAULT 0 NOT NULL,
	MODIFIED TIMESTAMP NOT NULL,
	PRIMARY KEY (ID),
	CONSTRAINT SECURITYGROUPS_U UNIQUE (SECURITYGROUPNAME)
);

CREATE OR REPLACE TRIGGER SECURITYGROUPS_T 
BEFORE INSERT OR UPDATE ON SECURITYGROUPS
FOR EACH ROW
DECLARE 
	OBJECTMODIFICATION EXCEPTION;
BEGIN
	IF UPDATING AND NOT :NEW.ROWSTAMP = :OLD.ROWSTAMP THEN
		RAISE OBJECTMODIFICATION;
	END IF;

	:NEW.MODIFIED := SYSTIMESTAMP;

	SELECT DBMS_RANDOM.STRING('U',10) INTO :NEW.ROWSTAMP FROM DUAL;
	EXCEPTION 
	   WHEN OBJECTMODIFICATION THEN
		RAISE_APPLICATION_ERROR(-20100,'Record has been modified by another user');
END;
/

CREATE MATERIALIZED VIEW LOG ON SECURITYGROUPS WITH ROWID, PRIMARY KEY;
ALTER MATERIALIZED VIEW LOG FORCE ON SECURITYGROUPS ADD ROWID;

