set serveroutput ON
set verify off
set feedback off
set linesize 1000
set pagesize 50
spool uninstall_policy_info.log

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

  PROCEDURE uninstall_policy_info is
    v_sql_stmt varchar2(500);
    v_count number(15);
  BEGIN
    BEGIN
       v_sql_stmt := 'SELECT   count(1) FROM  tab t WHERE   t.tname=''POLICY_INFO''';
       --pl('stmt : ' || v_sql_stmt);
       EXECUTE IMMEDIATE v_sql_stmt INTO v_count;  
       --pl('count : ' || v_count);
       if (v_count > 0) then
        v_sql_stmt := 'drop table POLICY_INFO';
        EXECUTE IMMEDIATE v_sql_stmt;
        pl('dropped table POLICY_INFO');
       else
         pl('POLICY_INFO table not found in schema');
       end if;
	END;
  END uninstall_policy_info;

BEGIN
  pl('Starting Uninstall docupro forms test - ' || SYSTIMESTAMP);
  uninstall_policy_info;
  pl('Uninstall of Docupro_forms_test completed');
END;
/
commit;
/
spool off
set verify on
set serveroutput off;
exit;
