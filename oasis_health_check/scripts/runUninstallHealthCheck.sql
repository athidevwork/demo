set serveroutput ON
set verify off
set feedback off
set linesize 1000
set pagesize 50
spool uninstall_oasis_health_check.log
DECLARE
  v_verbose       BOOLEAN := FALSE;

  table_does_not_exist exception;
  table_already_exists exception;
  pragma exception_init( table_does_not_exist, -942 );
  pragma exception_init( table_already_exists, -955 );
  
  PROCEDURE pl(s IN VARCHAR2, v IN BOOLEAN DEFAULT TRUE) IS
  BEGIN 
    IF (v) THEN 
       dbms_output.enable(1000000);
       dbms_output.put_line(s); 
    END IF;
  END pl;

  PROCEDURE uninstall_oasis_health_check is
    v_sql_stmt varchar2(500);
    v_count number(15);
  BEGIN
    BEGIN
       v_sql_stmt := 'SELECT   count(1) FROM  tab t WHERE   t.tname=''OASIS_HEALTH_CHECK''';
       --pl('stmt : ' || v_sql_stmt);
       EXECUTE IMMEDIATE v_sql_stmt INTO v_count;  
       --pl('count : ' || v_count);
       if (v_count > 0) then
        v_sql_stmt := 'drop table OASIS_HEALTH_CHECK';
        EXECUTE IMMEDIATE v_sql_stmt;
        pl('dropped table OASIS_HEALTH_CHECK');
       else
         pl('OASIS_HEALTH_CHECK table not found in schema');
       end if;

       v_sql_stmt := 'SELECT   count(1) FROM  all_sequences a WHERE   a.SEQUENCE_NAME=''OASIS_HEALTH_CHECK_SEQ''';
       --pl('stmt : ' || v_sql_stmt);
       EXECUTE IMMEDIATE v_sql_stmt INTO v_count;
       --pl('count : ' || v_count);
       if (v_count > 0) then
        v_sql_stmt := 'drop sequence oasis_health_check_seq';
        EXECUTE IMMEDIATE v_sql_stmt;
        pl ('dropped sequence OASIS_HEALTH_CHECK_SEQ');
       else
         pl('OASIS_HEALTH_CHECK_SEQ sequence not found in schema');        
       end if;

       v_sql_stmt := 'select count(1)  FROM USER_OBJECTS WHERE OBJECT_TYPE = ''PACKAGE'' AND OBJECT_NAME = ''OASIS_HEALTH_CHECK_MAIN''';
       --pl('stmt : ' || v_sql_stmt);
       EXECUTE IMMEDIATE v_sql_stmt INTO v_count;
       --pl('count : ' || v_count);
       IF (v_count > 0) then
        v_sql_stmt := 'DROP PACKAGE BODY OASIS_HEALTH_CHECK_MAIN';
        EXECUTE IMMEDIATE v_sql_stmt;
        v_sql_stmt := 'DROP PACKAGE OASIS_HEALTH_CHECK_MAIN';
        EXECUTE IMMEDIATE v_sql_stmt;        
        pl('dropped package OASIS_HEALTH_CHECK_MAIN');
       else
         pl('OASIS_HEALTH_CHECK_MAIN package not found in schema');        
       END IF;

       v_sql_stmt := 'select count(1)  FROM USER_OBJECTS WHERE OBJECT_TYPE = ''PACKAGE'' AND OBJECT_NAME = ''OASIS_HEALTH_CHECK_OUTPUT''';
       --pl('stmt : ' || v_sql_stmt);
       EXECUTE IMMEDIATE v_sql_stmt INTO v_count;
       --pl('count : ' || v_count);
       IF (v_count > 0) then
        v_sql_stmt := 'DROP PACKAGE BODY OASIS_HEALTH_CHECK_OUTPUT';
        EXECUTE IMMEDIATE v_sql_stmt;
        v_sql_stmt := 'DROP PACKAGE OASIS_HEALTH_CHECK_OUTPUT';
        EXECUTE IMMEDIATE v_sql_stmt;        
        pl('dropped package OASIS_HEALTH_CHECK_OUTPUT'); 
       else
         pl('OASIS_HEALTH_CHECK_OUTPUT package not found in schema');               
       END IF;
	   
       v_sql_stmt := 'select count(1)  FROM USER_OBJECTS WHERE OBJECT_TYPE = ''PACKAGE'' AND OBJECT_NAME = ''OASIS_HEALTH_CHECK_POLICY''';
       --pl('stmt : ' || v_sql_stmt);
       EXECUTE IMMEDIATE v_sql_stmt INTO v_count;
       --pl('count : ' || v_count);
       IF (v_count > 0) then
        v_sql_stmt := 'DROP PACKAGE BODY OASIS_HEALTH_CHECK_POLICY';
        EXECUTE IMMEDIATE v_sql_stmt;
        v_sql_stmt := 'DROP PACKAGE OASIS_HEALTH_CHECK_POLICY';
        EXECUTE IMMEDIATE v_sql_stmt;        
        pl('dropped package OASIS_HEALTH_CHECK_POLICY'); 
       else
         pl('OASIS_HEALTH_CHECK_POLICY package not found in schema');               
       END IF;	   
    END; 
  END uninstall_oasis_health_check;

BEGIN
  pl('Starting Uninstall Oasis_Health_Check - ' || SYSTIMESTAMP);
  uninstall_oasis_health_check;
--EXCEPTION
  --WHEN table_does_not_exist THEN
    --PL('Exception during uninstall_oasis_health_check main');
  --WHEN table_already_exists THEN
    --PL('Exception during uninstall_oasis_health_check main table exists');
    pl('Uninstall of Oasis_Health_Check completed');
END;
/
commit;

spool off
set verify on
set serveroutput off;
exit;
