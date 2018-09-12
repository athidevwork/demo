set serveroutput ON
set verify off
set feedback off
set linesize 1000
set pagesize 50

spool install_oasis_health_check.log
DECLARE
  v_verbose       BOOLEAN := FALSE;

  table_does_not_exist exception;
  table_already_exists exception;
  pragma exception_init( table_does_not_exist, -942 );
  pragma exception_init( table_already_exists, -955 );
  
  drop_table_sql varchar(200);
  trunc_table_sql varchar2(200);
  create_table_sql varchar(4000);
  env_table_sql varchar(4000);

  tablename varchar2(128) := 'OASIS_HEALTH_CHECK';
  envtablename varchar2(128) := 'OASIS_HEALTH_CHECK_ENV';
  tableseq varchar2(128) := 'OASIS_HEALTH_CHECK_SEQ';
  dailyseq varchar2(128) := 'DAILY_OHC_SEQ';
  
  PROCEDURE pl(s IN VARCHAR2, v IN BOOLEAN DEFAULT TRUE) IS
  BEGIN 
    IF (v) THEN 
       dbms_output.enable(1000000);
       dbms_output.put_line(s); 
    END IF;
  END pl;

  procedure init_sql is
  BEGIN
    drop_table_sql := 'drop table '||tablename;
    trunc_table_sql := 'truncate table '||tablename;
    create_table_sql := 'create table '||tablename||chr(10)||
                         '(OHC_PK number(15),'||chr(10)||
                         ' RUN_DATE varchar2(75),'||chr(10)||
						 ' RUN_NO number(15),'||chr(10)||
                         ' OHC_LEVEL varchar2(50),'||chr(10)||
						 ' SUB_SYSTEM varchar2(50),'||chr(10)||
                         ' OHC_CATEGORY varchar2(50),'||chr(10)||
                         ' STATUS varchar2(50),'||chr(10)||
                         ' MSG varchar2(300),'||chr(10)||                           
                         ' PARM_NAME varchar2(50),'||chr(10)||
                         ' PARM_VALUE varchar(200),'||chr(10)||
                         ' PARM_DESC varchar2(2000),'||chr(10)||
						 ' OHC_SQL CLOB'||chr(10)||
                         ' )';
	env_table_sql := 'CREATE TABLE '||envtablename||chr(10)||
					 '(ENV_NAME varchar2(25),'||chr(10)||
					 ' ENV_CONN_STR varchar2(75)'||chr(10)||
					 ')';
  END init_sql;
  
  procedure install_oasis_health_check IS
    v_sql_stmt varchar2(500);
    v_count number(15);
  BEGIN
     init_sql;
     
     v_sql_stmt := 'SELECT count(1) FROM  tab t WHERE t.tname=''OASIS_HEALTH_CHECK''';
     --pl('stmt : ' || v_sql_stmt);
     EXECUTE IMMEDIATE v_sql_stmt INTO v_count;
     --pl('count : ' || v_count);
     IF (v_count = 0) THEN
       PL('Creating table: '||tablename);
       PL('sql = ' || create_table_sql);
       EXECUTE IMMEDIATE create_table_sql;
       pl('created table OASIS_HEALTH_CHECK successfully');
       else
         pl('OASIS_HEALTH_CHECK table already found in schema');       
     END IF;

	 v_sql_stmt := 'SELECT count(1) FROM  tab t WHERE t.tname=''OASIS_HEALTH_CHECK_ENV''';
	 EXECUTE IMMEDIATE v_sql_stmt INTO v_count;
     IF (v_count = 0) THEN
       PL('Creating table: '||envtablename);
       PL('sql = ' || env_table_sql);
       EXECUTE IMMEDIATE env_table_sql;
       pl('created table OASIS_HEALTH_CHECK_ENV successfully');
       else
         pl('OASIS_HEALTH_CHECK_ENV table already found in schema');       
     END IF;
		  
     v_sql_stmt := 'SELECT count(1) FROM user_sequences u WHERE u.sequence_name = ''OASIS_HEALTH_CHECK_SEQ''';
     --pl('stmt : ' || v_sql_stmt);
     EXECUTE IMMEDIATE v_sql_stmt INTO v_count;
     --pl('count : ' || v_count);
     IF (v_count = 0) THEN      
        pl('Creating table sequence ' || tableseq);
        tableseq := 'CREATE SEQUENCE ' || tableseq || ' start with 1 increment by 1 NOCYCLE';
        EXECUTE IMMEDIATE tableseq;
        pl(tableseq||' sequence was created successfully');
       else
         pl('OASIS_HEALTH_CHECK_SEQ sequence already found in schema');         
     END IF;

     v_sql_stmt := 'SELECT count(1) FROM user_sequences u WHERE u.sequence_name = ''DAILY_OHC_SEQ''';
     --pl('stmt : ' || v_sql_stmt);
     EXECUTE IMMEDIATE v_sql_stmt INTO v_count;
     --pl('count : ' || v_count);
     IF (v_count = 0) THEN      
        pl('Creating sequence ' || dailyseq);
        dailyseq := 'CREATE SEQUENCE ' || dailyseq || ' start with 1 increment by 1 NOCYCLE';
        EXECUTE IMMEDIATE dailyseq;
        pl(dailyseq||' sequence was created successfully');
       else
         pl('DAILY_OHC_SEQ sequence already found in schema');         
     END IF;
	 
  END install_oasis_health_check;

BEGIN
  pl('Starting Install Oasis_Health_Check - ' || SYSTIMESTAMP);
  install_oasis_health_check;
--EXCEPTION
  --WHEN table_does_not_exist THEN
    --PL('Exception during uninstall_oasis_health_check main');
  --WHEN table_already_exists THEN
    --PL('Exception during uninstall_oasis_health_check main table exists');
    pl('Install of Oasis_Health_Check completed');
END;
/
commit;

spool off
set verify on
set serveroutput off;
exit;
