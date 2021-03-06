/* 
	LISTITEMS 

	PROVIDES VALUES FOR DROP DOWN LISTS 
*/
CREATE SEQUENCE LISTITEMSEQ;

DROP TABLE LISTITEMS;

CREATE TABLE LISTITEMS (
	ID VARCHAR2(10) NOT NULL,
	ROWSTAMP VARCHAR2(10) NOT NULL,
	LISTID VARCHAR2(10) NOT NULL,
	ISALLCAMPUS NUMBER(1) NOT NULL,
	CAMPUSID VARCHAR2(10),
	DISPLAYORDER NUMBER(5) NOT NULL,
	SYSTEMVALUE VARCHAR2(100) NOT NULL,
	VALUE VARCHAR2(100) NOT NULL,
	ISDEFAULT NUMBER(1) DEFAULT 0 NOT NULL,
	DESCRIPTION VARCHAR2(500),
	PRIMARY KEY (ID),
	CONSTRAINT LISTITEMS_U UNIQUE (LISTID, SYSTEMVALUE, VALUE),
	CONSTRAINT LISTITEMS_FK1 FOREIGN KEY (LISTID) REFERENCES LISTS (ID)
);

CREATE OR REPLACE TRIGGER LISTITEMS_T 
BEFORE INSERT OR UPDATE ON LISTITEMS
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

CREATE INDEX LISTITEMS_IDX1 ON LISTITEMS (LISTID);
CREATE INDEX LISTITEMS_IDX2 ON LISTITEMS (ISALLCAMPUS);
CREATE INDEX LISTITEMS_IDX3 ON LISTITEMS (ISDEFAULT);
