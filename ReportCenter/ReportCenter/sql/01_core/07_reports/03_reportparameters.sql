CREATE SEQUENCE REPORTPARAMETERSEQ;

CREATE TABLE REPORTPARAMETERS (
	ID VARCHAR2(10) NOT NULL,
	ROWSTAMP VARCHAR2(10) NOT NULL,
	REPORTID VARCHAR2(10) NOT NULL,
	PARAMETERNUMBER NUMBER(5) DEFAULT 1 NOT NULL,
	PARAMETER VARCHAR2(200),
	PARAMETERTYPE VARCHAR2(20),
	LISTNAME VARCHAR2(200),
	PRIMARY KEY (ID),
	CONSTRAINT REPORTPARAMETERS_U UNIQUE (REPORTID, PARAMETER),
	CONSTRAINT REPORTPARAMETERS_FK1 FOREIGN KEY (REPORTID) REFERENCES REPORTS (ID),
	CONSTRAINT REPORTPARAMETERS_FK2 FOREIGN KEY (LISTNAME) REFERENCES LISTS (LISTNAME)
);

CREATE OR REPLACE TRIGGER REPORTPARAMETERS_T 
BEFORE INSERT OR UPDATE ON REPORTPARAMETERS
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

CREATE INDEX REPORTPARAMETERS_IDX10 ON REPORTPARAMETERS (REPORTID);
CREATE INDEX REPORTPARAMETERS_IDX11 ON REPORTPARAMETERS (REPORTID, PARAMETERNUMBER);
