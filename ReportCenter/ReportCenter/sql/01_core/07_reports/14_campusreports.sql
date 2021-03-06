CREATE SEQUENCE CAMPUSREPORTSEQ;

CREATE TABLE CAMPUSREPORTS (
	ID VARCHAR2(10) NOT NULL,
	ROWSTAMP VARCHAR2(10) NOT NULL,
	CAMPUSID VARCHAR2(10) NOT NULL,
	REPORTID VARCHAR2(10) NOT NULL,
	PRIMARY KEY (ID),
	CONSTRAINT CAMPUSREPORTS_U UNIQUE (CAMPUSID, REPORTID),
	CONSTRAINT CAMPUSREPORTS_FK1 FOREIGN KEY (CAMPUSID) REFERENCES CAMPUSES (ID),
	CONSTRAINT CAMPUSREPORTS_FK2 FOREIGN KEY (REPORTID) REFERENCES REPORTS (ID)
);

CREATE OR REPLACE TRIGGER CAMPUSREPORTS_T 
BEFORE INSERT OR UPDATE ON CAMPUSREPORTS
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


