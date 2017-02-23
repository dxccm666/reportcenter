SET SERVEROUTPUT ON SIZE 1000000 
SET LINESIZE 200
SET ECHO ON
SET FEED ON

SELECT SYSTIMESTAMP FROM DUAL;


BEGIN 
  FOR C IN
  (SELECT C.OWNER, C.TABLE_NAME, C.CONSTRAINT_NAME
   FROM USER_CONSTRAINTS C, USER_TABLES T
   WHERE C.TABLE_NAME = T.TABLE_NAME
   AND C.CONSTRAINT_TYPE = 'R'  
   ORDER BY C.TABLE_NAME, C.CONSTRAINT_TYPE)
  LOOP
      BEGIN
    DBMS_UTILITY.EXEC_DDL_STATEMENT('ALTER TABLE ' || C.OWNER || '.' || C.TABLE_NAME || ' DROP CONSTRAINT ' || C.CONSTRAINT_NAME);

	EXCEPTION WHEN OTHERS THEN 
		DBMS_OUTPUT.PUT_LINE('ERROR DROPPING FOREIGN KEY CONSTRAINT '||C.CONSTRAINT_NAME||' ON TABLE '|| C.OWNER||'.'||C.TABLE_NAME);

    END;
  END LOOP;
END;
/

BEGIN
  FOR C IN
  (SELECT SEQUENCE_NAME FROM USER_SEQUENCES)
  LOOP
  	BEGIN
	DBMS_UTILITY.EXEC_DDL_STATEMENT('DROP SEQUENCE '||C.SEQUENCE_NAME);

	EXCEPTION WHEN OTHERS THEN
	DBMS_OUTPUT.PUT_LINE('ERROR DROPPING SEQUENCE '||C.SEQUENCE_NAME);

	END;
  END LOOP;
END;
/

BEGIN
  FOR C IN
  (SELECT SYNONYM_NAME FROM USER_SYNONYMS)
  LOOP
  	BEGIN
	DBMS_UTILITY.EXEC_DDL_STATEMENT('DROP SYNONYM '||C.SYNONYM_NAME);

	EXCEPTION WHEN OTHERS THEN
	DBMS_OUTPUT.PUT_LINE('ERROR DROPPING SYNONYM '||C.SYNONYM_NAME);

	END;
  END LOOP;
END;
/

BEGIN
  FOR C IN
  (SELECT MASTER FROM USER_MVIEW_LOGS)
  LOOP
  	BEGIN
  	DBMS_UTILITY.EXEC_DDL_STATEMENT('DROP MATERIALIZED VIEW LOG ON '||C.MASTER);

	EXCEPTION WHEN OTHERS THEN
		DBMS_OUTPUT.PUT_LINE('ERROR DROPPING MATERIALIZED VIEW LOG ON '||C.MASTER);

	END;
  END LOOP;
END;
/

BEGIN
  FOR C IN
  (SELECT MVIEW_NAME FROM USER_MVIEWS)
  LOOP
  	BEGIN
  	DBMS_UTILITY.EXEC_DDL_STATEMENT('DROP MATERIALIZED VIEW '||C.MVIEW_NAME);
	EXCEPTION WHEN OTHERS THEN
		DBMS_OUTPUT.PUT_LINE('ERROR DROPPING MATERIALIZED VIEW '||C.MVIEW_NAME);
	END;
  END LOOP;
END;
/

BEGIN
  FOR C IN
  (SELECT VIEW_NAME FROM USER_VIEWS)
  LOOP
  	BEGIN
  	DBMS_UTILITY.EXEC_DDL_STATEMENT('DROP VIEW '||C.VIEW_NAME);
	EXCEPTION WHEN OTHERS THEN
		DBMS_OUTPUT.PUT_LINE('ERROR DROPPING VIEW '||C.VIEW_NAME);
	END;
  END LOOP;
END;
/

BEGIN
  FOR C IN 
  (SELECT INDEX_NAME FROM USER_INDEXES WHERE GENERATED = 'N' AND NOT UNIQUENESS = 'UNIQUE' )
  LOOP
  	BEGIN
  	DBMS_UTILITY.EXEC_DDL_STATEMENT('DROP INDEX '||C.INDEX_NAME);
	EXCEPTION WHEN OTHERS THEN
		DBMS_OUTPUT.PUT_LINE('ERROR DROPPING INDEX '||C.INDEX_NAME);
	END;
  END LOOP;
END;
/

DROP TABLE MIMETYPES;

BEGIN
  FOR C IN
  (SELECT TABLE_NAME FROM USER_TABLES)
  LOOP
  	BEGIN
  	DBMS_UTILITY.EXEC_DDL_STATEMENT('DELETE FROM '||C.TABLE_NAME);
	DBMS_UTILITY.EXEC_DDL_STATEMENT('DROP TABLE '||C.TABLE_NAME||' CASCADE CONSTRAINTS PURGE');
	EXCEPTION WHEN OTHERS THEN
		DBMS_OUTPUT.PUT_LINE('ERROR DROPPING TABLE '||C.TABLE_NAME);
	END;
  END LOOP;
END;
/

BEGIN
	FOR C IN
	(SELECT OBJECT_NAME, OBJECT_TYPE FROM USER_OBJECTS WHERE OBJECT_TYPE IN ('PACKAGE','PACKAGE_BODY','PROCEDURE','FUNCTION'))
	LOOP
	BEGIN
		DBMS_UTILITY.EXEC_DDL_STATEMENT('DROP '||C.OBJECT_TYPE||' '||C.OBJECT_NAME);
	EXCEPTION WHEN OTHERS THEN
		DBMS_OUTPUT.PUT_LINE('ERROR DROPPING OBJECT '||C.OBJECT_NAME);
	END;
	END LOOP;
END;
/

