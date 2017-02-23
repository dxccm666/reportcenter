CREATE SEQUENCE OBJECTCLASSRIGHTSEQ;
CREATE TABLE OBJECTCLASSRIGHTS (
	ID VARCHAR2(10) NOT NULL,
	ROWSTAMP VARCHAR2(10) NOT NULL,
	OBJECTCLASS VARCHAR2(50) NOT NULL,
	RIGHT VARCHAR2(100) NOT NULL,
	PRIMARY KEY (ID),
	CONSTRAINT RIGHTDEFAULTS_U UNIQUE (OBJECTCLASS, RIGHT),
	CONSTRAINT RIGHTDEFAULTS_FK1 FOREIGN KEY (OBJECTCLASS) REFERENCES OBJECTCLASSES (OBJECTCLASS)
);
CREATE OR REPLACE TRIGGER OBJECTCLASSRIGHTS_T
BEFORE INSERT OR UPDATE ON OBJECTCLASSRIGHTS
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
