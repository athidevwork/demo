set serveroutput ON
set verify off
set feedback off
set linesize 1000
set pagesize 50

DECLARE

  PROCEDURE pl(s IN VARCHAR2, v IN BOOLEAN DEFAULT TRUE) IS
  BEGIN 
    IF (v) THEN 
       dbms_output.enable(1000000);
       dbms_output.put_line(s); 
    END IF;
  END pl;

  PROCEDURE uninstall_policy_info is
    v_sql_stmt varchar2(500);
  BEGIN
    BEGIN
		v_sql_stmt := 'create table policy_info as select policy_no,request_id  from xt_policy where 1=2';
		EXECUTE IMMEDIATE v_sql_stmt;
	END;
  END uninstall_policy_info;
BEGIN
	pl('Starting Install docupro forms test - ' || SYSTIMESTAMP);	
	uninstall_policy_info;
END;
/
commit;
/
set serveroutput off
set verify on
set feedback on
exit
