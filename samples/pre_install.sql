set serveroutput ON
set verify off
set feedb off
set linesize 1000
set pagesize 50
spool pre_install.log
DECLARE
  v_verbose       BOOLEAN := FALSE;
  v_debug         BOOLEAN := FALSE;
  v_oracle_ver    VARCHAR2(20);
  v_oasis_ver     VARCHAR2(100);
  v_user          VARCHAR2(40);
  v_owner         VARCHAR2(40);
  v_q_setting     VARCHAR2(40);
  v_q_status      VARCHAR2(40);
  v_q_sysparm     VARCHAR2(100);
  v_plsql_ver     VARCHAR2(200);
  v_rate_eng      VARCHAR2(100);
  v_line          VARCHAR2(400);
  v_kill_cmd      VARCHAR2(4000);
  v_using_queue   VARCHAR2(2);
  v_q_up          VARCHAR2(2);
  i_cnt           INTEGER;
  I_ODS_CONN_CNT  INTEGER;
  i_jdbc_conn_cnt INTEGER;
  i_other_conn_cnt INTEGER;
  i_total_conn_cnt INTEGER; 
  NOT_SCHEMA_OWNER EXCEPTION;
  NO_BUILD_APPLIED EXCEPTION;
  v_conn_type	varchar2(30);
  col_size NUMBER;
  min_col_size NUMBER := 100;
  cmd VARCHAR2(4000);
  
  table_does_not_exist exception;
  table_already_exists exception;
  pragma exception_init( table_does_not_exist, -942 );
  pragma exception_init( table_already_exists, -955 );
 
  drop_table_sql varchar(200);
  trunc_table_sql varchar2(200);
  create_table_sql varchar(4000);
  ins_invalids_sql varchar(4000);
  comp_invalids_sql varchar(4000);
  tablename varchar2(128) := 'RE_PRE_INVALIDS';
  
PROCEDURE pl(s IN VARCHAR2, v IN BOOLEAN DEFAULT TRUE) IS
BEGIN 
  IF (v) THEN 
     dbms_output.enable(1000000);
     dbms_output.put_line(s); 
  END IF;
END pl;

-- Procedures for comparing invalids.
-- INIT_SQL Proc
  procedure init_sql is
  begin
    drop_table_sql := 'drop table '||tablename;
    trunc_table_sql := 'truncate table '||tablename;
    create_table_sql := 'create table '||tablename||'(
              INVALID_OBJECT varchar2(128),
              OBJECT_TYPE varchar2(25))';
                
    ins_invalids_sql := 'insert into RE_PRE_INVALIDS(INVALID_OBJECT, OBJECT_TYPE)
      select object_name, object_type
        from obj 
       where status = ''INVALID''
         and  object_type in ( ''PACKAGE BODY'', ''PACKAGE'', ''FUNCTION'', ''MATERIALIZED VIEW'',
                        ''PROCEDURE'', ''TRIGGER'', ''VIEW'', ''TYPE'', ''SYNONYM'' )
        order by object_type, object_name';
      
  end init_sql;
  
-- DROP_CREATE_TABLE Proc  
  procedure drop_create_table is
    sql_str varchar(4000);
  begin
      begin 
        -- Drop Table
        pl('Dropping table: '||tablename, v_verbose);
        execute immediate drop_table_sql;
  
      exception
        when table_does_not_exist then
        pl('   table does not exist.', v_verbose);
      end;
  
      -- Create Table
      pl('Creating table: '||tablename);
      execute immediate create_table_sql;
      pl('Table was created successfully', v_verbose);
        
  end drop_create_table;

-- GET_INVALIDS Proc
  procedure Get_Invalids is
  begin
    -- create Table
    pl('Inserting invalids', v_verbose);
    execute immediate ins_invalids_sql;
    execute immediate 'commit';
    --pl('Table was created successfully');
  end Get_Invalids;

BEGIN
  -- display Oracle and OASIS versions.
  select version
    INTO v_oracle_ver
    from v$instance;

  BEGIN 
    SELECT get_oasis_version
      INTO v_oasis_ver
      from dual;
  EXCEPTION
    WHEN no_data_found THEN
       v_oasis_ver := 'could not retrieve OASIS version.';
  END;
  pl(rpad('Oracle Version:',20, ' ')||v_oracle_ver);
  pl(rpad('OASIS Version:',20, ' ')||v_oasis_ver);
  
  -- Checking schema owner
  pl(chr(10));
  pl(chr(10));
  pl('vvvvvvvvvvvvvvvvvvvv Check schema owner vvvvvvvvvvvvvvvvvvvv');

  BEGIN 
    v_user := USER;
    SELECT owner
      INTO v_owner
      FROM ( select table_owner owner
               FROM all_synonyms
              where synonym_name = 'ENTITY'
                and ( owner = v_user
                    or
                    (owner = 'PUBLIC'
                     and not exists ( select 0 
                                        from all_synonyms 
                                       where synonym_name = 'ENTITY'
                                         and owner = v_user)
                    )
                    )
                and not exists ( select 0 
                                   from user_tables
                                  where table_name = 'ENTITY'
                               )
              Union
              select v_user
                from user_tables
               where table_name ='ENTITY');
  EXCEPTION
    WHEN NO_DATA_FOUND THEN 
      RAISE NOT_SCHEMA_OWNER;
  END;
  
  IF v_user = v_owner THEN
    pl('OK - You are logged on as the schema owner ('||v_owner||').');
  ELSE
    RAISE NOT_SCHEMA_OWNER;
  END IF;
  pl('^^^^^^^^^^^^^^^^^^^^ Completed checking schema owner ^^^^^^^^^^^^^^^^^^^^');
  pl(chr(10));
  pl(chr(10));

  -- Checking the build_applied table and the build_id column's size
  pl(chr(10), v_verbose);
  pl(chr(10), v_verbose);
  pl('vvvvvvvvvvvvvvvvvvvv Check build_id column size vvvvvvvvvvvvvvvvvvvv', v_verbose);

  BEGIN 
    SELECT data_length 
      INTO col_size
      FROM user_tab_columns
     WHERE table_name = 'BUILD_APPLIED'
       AND column_name = 'BUILD_ID';
     
    IF col_size < min_col_size THEN
      cmd := 'ALTER TABLE build_applied MODIFY (build_id varchar2('||to_char(min_col_size)||'))';
      PL('Altering the build_id column size from '||to_char(col_size)||' to '||to_char(min_col_size), v_verbose);
      PL(CHR(10), v_verbose);
      PL('Executing: '||cmd, v_verbose);
	  
      IF NOT v_debug THEN
        EXECUTE IMMEDIATE cmd;
      END IF;
    ELSE
      PL('The build_id colmn size ('||to_char(col_size)||') meets the min requirements of '||to_char(min_col_size)||' char.', v_verbose);
    END IF;

  EXCEPTION
    WHEN NO_DATA_FOUND THEN 
      RAISE NO_BUILD_APPLIED;
  END;

  pl('^^^^^^^^^^^^^^^^^^^^ Completed checking build_is column size ^^^^^^^^^^^^^^^^^^^^', v_verbose);
  pl(chr(10), v_verbose);
  pl(chr(10), v_verbose);  
  
  -- checking the FM queues
  pl(chr(10));
  pl('vvvvvvvvvvvvvvvvvvvv Check the FM queue status vvvvvvvvvvvvvvvvvvvv');
  select decode(count(*),
       0, 'Queue setting:      Not using Queue',
          'Queue setting:      Using Queue'
       ),
       decode(count(*),
       0, 'N',
          'Y'
       )
    INTO v_q_setting, v_using_queue
    from dual
   where exists (select 0 
                   from system_parameter_util 
                  where SYSPARM_CODE = 'PM_WAIT_FOR_FM' 
                    and SYSPARM_VALUE ='N');

  -- pl(v_q_setting, v_verbose);
    
  IF v_using_queue = 'N' THEN
    pl('System is not set to use FM queues.');
  ELSE
    FOR rec IN (SELECT sysparm_code, sysparm_value
                  FROM system_parameter_util 
                 WHERE sysparm_code LIKE '%PM_WAIT_FOR_FM%')
    LOOP
      v_line := rpad(rec.sysparm_code||' :', 20, ' ')||rec.sysparm_value ;
      pl(v_line, v_verbose);
    END LOOP;

    pl(chr(10), v_verbose);
    pl('Values to check', v_verbose);
    pl(' (a) If the sysparm_value  = N then the customer uses the Queue and it should be UP when you are done with applying a build.', v_verbose);
    pl(' (b) If the sysparm_value = Y then the customer does NOT use the Queue so you can leave the Queue DOWN when done applying a build. ', v_verbose);
    pl(' (c) If the system parameter is not there at all, the default setting is N so they would use the Queue.  Leave the Queue UP when you are done applying a build. ', v_verbose);
    pl(chr(10), v_verbose); 
    
    SELECT DECODE(COUNT(*),
                  0, '   >>> Queue is down',
                  1, '   >>> Queue is up',
                     '   >>> Found more than 1 queue, possible error' ),
           DECODE(COUNT(*),
                  0, 'N',
                     'Y' )
      INTO v_q_status, v_q_up
      FROM dba_jobs
     WHERE SUBSTR(LOWER(what),1,28) = 'fmn_pol_interface_queue_proc'
       AND schema_user = v_owner
       AND priv_user = v_owner;
       
  END IF;
  
  pl('   >>> Stopping the FM queue <<<');
  BEGIN
    fmn_control_pol_int_queue('STOP',v_line);
    pl(v_line);
      
  EXCEPTION
    WHEN OTHERS THEN
      pl('   >>>Exception while stoping FM queue...');
  END;

  pl('^^^^^^^^^^^^^^^^^^^^ Completed checking the FM queue status ^^^^^^^^^^^^^^^^^^^^');
  pl(chr(10));
  pl(chr(10));
  
  pl(chr(10));
  pl('vvvvvvvvvvvvvvvvvvvv Current connections vvvvvvvvvvvvvvvvvvvv');
  pl(chr(10));
  pl('LOGON_TIME           MODULE               SERVER     SID_SERIAL OSUSER               SCHEMANAME           STATUS   MACHINE              PROGRAM                                  USERNAME');
  pl('-------------------- -------------------- ---------- ---------- -------------------- -------------------- -------- -------------------- ---------------------------------------- ------------------------------');
  i_ods_conn_cnt := 0;
  i_jdbc_conn_cnt := 0;
  i_other_conn_cnt := 0;
  i_total_conn_cnt := 0;
  FOR rec IN ( Select distinct to_char(s.logon_time,'mm/dd/yyyy hh24:mi:ss') logon_time, 
                      substr(s.module,1,20) module,
                      substr(s.server,1,20) server,
                      s.sid||','||s.serial# sid_serial,
                      substr(s.osuser,1,20) osuser,
                      substr(s.SCHEMANAME,1,20) schemaname,
                      s.status,
                      substr(s.MACHINE,1,20) machine,
                      substr(s.PROGRAM,1,40)  PROGRAM,
                      USERNAME
                 from v$session S 
                where s.schemaname = SYS_CONTEXT('USERENV','CURRENT_SCHEMA')
                  AND s.AUDSID <> USERENV('sessionid')
                order by schemaname, module, status, osuser)
  LOOP
    CASE lower(SUBSTR(rec.module, 1, 3))
      WHEN 'ods' THEN i_ods_conn_cnt := i_ods_conn_cnt +1; v_conn_type:= 'ODS';
      WHEN 'jdb' THEN i_jdbc_conn_cnt := i_jdbc_conn_cnt + 1; v_conn_type:= 'WebLogic/Trinisys TDES';
	  --WHEN 'oracle.exe (j
      ELSE i_other_conn_cnt := i_other_conn_cnt + 1; v_conn_type:= 'OTHERS';
    END CASE;
    i_total_conn_cnt := i_total_conn_cnt + 1;
	
    v_line := rpad(rec.logon_time, 21, ' ')||
              rpad(rec.module, 21, ' ')||
              rpad(rec.server, 11, ' ')||
              rpad(rec.sid_serial, 11, ' ')||
              rpad(rec.osuser, 21, ' ')||
              rpad(rec.schemaname, 21, ' ')||
              rpad(rec.status, 9, ' ')||
              rpad(rec.machine, 21, ' ')||
              rpad(rec.program, 41, ' ')||
              rec.username;
              
    v_kill_cmd := v_kill_cmd || 'exec sys.kill_session('||rec.sid_serial||');  -- ['||v_conn_type ||']'  ||chr(10);
             
    pl(v_line);
  END LOOP;

  IF i_jdbc_conn_cnt > 0 THEN
    pl(chr(10));
    pl('WebLogic and/or Trinisys TDES connections found. Please make sure to shut down the WebLogic Data Source and/or Trinisys TDES connection.');
	pl(chr(10));
  END IF;

  IF i_ods_conn_cnt > 0 THEN 
    pl(chr(10));
	pl('ODS connections found.');
	pl(chr(10));
  END IF;
  
  IF i_other_conn_cnt > 0 THEN 
    pl(chr(10));
    pl('Other user connections found.');
	pl(chr(10));
  END IF;
  
  IF i_total_conn_cnt > 0 THEN
	pl(chr(10));
	pl('To ensure the delivery will install successfully, we recommend the sessions found above be killed or terminated.');
	pl(chr(10));
	--pl(v_kill_cmd);
  END IF;

  pl('^^^^^^^^^^^^^^^^^^^^ Completed checking for current connections ^^^^^^^^^^^^^^^^^^^^');
  pl(chr(10));
  pl(chr(10));
  
  pl(chr(10));
  pl('vvvvvvvvvvvvvvvvvvvv Running Jobs vvvvvvvvvvvvvvvvvvvv');
  pl(chr(10));
  pl('SOURCE                      WHAT/NAME                      JOB        SID_SERIAL SPID       LOCKWAIT                   LOGIN_TIME');
  pl('--------------------------- ------------------------------ ---------- ---------- ---------- -------------------------- --------------------');
  i_total_conn_cnt:=0;
  FOR rec IN ( SELECT *
                 FROM (SELECT 'USER_JOBS/DBA_JOBS_RUNNING' SOURCE,
                              substr(J.WHAT, 1, 30)        "WHAT",
                              to_char(JR.JOB)              "JOB",
                              S.SID || ',' || S.SERIAL#    SID_SERIAL,
                              to_char(P.SPID)              SPID,
                              S.LOCKWAIT,
                              to_char(S.LOGON_TIME, 'mm/dd/yyyy hh24:mi:ss') login_time
                         FROM USER_JOBS        J,
                              DBA_JOBS_RUNNING JR,
                              V$SESSION        S,
                              V$PROCESS        P
                        WHERE J.JOB = JR.JOB
                          AND JR.SID = S.SID
                          AND S.PADDR = P.ADDR
                        UNION
                        SELECT 'USER_SCHEDULER_RUNNING_JOBS' SOURCE,
                               substr(RJ.JOB_NAME, 1, 30)    "WHAT",
                               '0',
                               S.SID || ',' || S.SERIAL#     SID_SERIAL,
                               to_char(P.SPID),
                               S.LOCKWAIT,
                               to_char(S.LOGON_TIME, 'mm/dd/yyyy hh24:mi:ss')
                          FROM USER_SCHEDULER_RUNNING_JOBS RJ,
                               V$SESSION                   S,
                               V$PROCESS                   P
                         WHERE RJ.SESSION_ID = S.SID
                           AND S.PADDR = P.ADDR)
                ORDER BY 1, 2, 3)
  LOOP
    i_total_conn_cnt := i_total_conn_cnt + 1;
    v_line := rpad(rec.SOURCE, 28, ' ')||
              rpad(rec.WHAT, 31, ' ')||
              lpad(rec.JOB, 11, ' ')||
              rpad(rec.sid_serial, 11, ' ')||
              rpad(rec.SPID, 11, ' ')||
              rpad(rec.LOCKWAIT, 27, ' ')||
              rec.login_time;
              
    v_kill_cmd := v_kill_cmd || 'exec sys.kill_session('||rec.sid_serial||');  -- ['||v_conn_type ||']'  ||chr(10);
             
    pl(v_line);
  END LOOP;

  IF i_total_conn_cnt > 0 THEN
	pl(chr(10));
	pl('To ensure that the installation can complete successfully, make sure that all jobs have been completed or removed.');
	pl('If needed, please kill all jobs sessions.');
	pl(chr(10));
	--pl(v_kill_cmd);
  ELSE
    pl(chr(10));
	pl('Currently no job is running.');
	pl(chr(10));
  END IF;

  pl('^^^^^^^^^^^^^^^^^^^^ Completed checking for Running Jobs ^^^^^^^^^^^^^^^^^^^^');
  pl(chr(10));
  pl(chr(10));

  pl(chr(10));
  pl('vvvvvvvvvvvvvvvvvvvv Scheduled Jobs vvvvvvvvvvvvvvvvvvvv');
  pl(chr(10));
  pl('SOURCE              JOB        USER/CREATOR                   NEXT_RUN_DATE       JOB_NAME                       Action');
  pl('------------------- ---------- ------------------------------ ------------------- ------------------------------ --------------------------------------------------');
  i_total_conn_cnt:=0;
  FOR rec IN ( SELECT *
                 FROM (SELECT 'USER_JOBS' SOURCE,
                              J.JOB,
                              J.SCHEMA_USER,
                              TO_CHAR(J.NEXT_DATE, 'mm/dd/yyyy') || ' ' || J.NEXT_SEC NEXT_RUN_DATE,
                              ' ' Job_Name,
                              J.WHAT action
                         FROM USER_JOBS J
                        WHERE J.BROKEN = 'N'
                       UNION
                       SELECT 'USER_SCHEDULER_JOBS',
                              0,
                              SJ.JOB_CREATOR,
                              TO_CHAR(SJ.NEXT_RUN_DATE,'mm/dd/yyyy hh24:mi:ss'),
                              SJ.JOB_NAME,
                              SJ.JOB_ACTION
                         FROM USER_SCHEDULER_JOBS SJ
                        WHERE SJ.STATE = 'SCHEDULED')
                ORDER BY NEXT_RUN_DATE)
  LOOP
    i_total_conn_cnt := i_total_conn_cnt + 1;
    v_line := rpad(rec.SOURCE, 20, ' ')||
              rpad(rec.job, 11, ' ')||
              rpad(rec.SCHEMA_USER, 31, ' ')||
              rpad(rec.NEXT_RUN_DATE, 20, ' ')||
              rpad(rec.Job_Name, 31, ' ')||
              rpad(substr(rec.action, 1, 50), 50, ' ');
              
    pl(v_line);
  END LOOP;

  IF i_total_conn_cnt > 0 THEN
     pl(chr(10));
     pl('Please make sure none of the above scheduled jobs will run while installing the build.');
     pl(chr(10));
  ELSE
    pl(chr(10));
	pl('Currently there is no scheduled job.');
	pl(chr(10));
  END IF;

  pl('^^^^^^^^^^^^^^^^^^^^ Completed checking for Scheduled Jobs ^^^^^^^^^^^^^^^^^^^^');
  pl(chr(10));
  pl(chr(10));
  
 --ToDo: Add a new section for running jobs in dba_jobs_running/user_scheduler_running_jobs or All_scheduer_running_jobs.
  -- Check config event mode.
  pl(chr(10), v_verbose);
  pl('vvvvvvvvvvvvvvvvvvvv Checking CONFIG EVENT MODE vvvvvvvvvvvvvvvvvvvv');
  BEGIN 
    select rpad('CONFIG EVENT MODE:', 20, ' ')||decode(event_mode, 1, 'ON', 'OFF')
      INTO v_line
      from config_event_mode ;
    pl(v_line);
  EXCEPTION
    WHEN no_data_found THEN
      pl('No Data Found - Selecting from config_event_mode');
  END;
  pl('^^^^^^^^^^^^^^^^^^^^ Completed checking CONFIG EVENT MODE ^^^^^^^^^^^^^^^^^^^^');
  pl(chr(10), v_verbose);
  pl(chr(10), v_verbose);
  
  -- PL/SQL Rating Engine.
  pl(chr(10), v_verbose);

  pl('vvvvvvvvvvvvvvvvvvvv Check PLSQL Rating Engine Settings vvvvvvvvvvvvvvvvvvvv', v_verbose);

  select rt_ver.rev_info PLSQL_version 
    INTO v_plsql_ver
    FROM dual;
    
  pl(v_plsql_ver, v_verbose);

  FOR rec IN (SELECT RPAD(SYSPARM_CODE || ' :',20, ' ') || SYSPARM_VALUE sysparm
                FROM SYSTEM_PARAMETER_UTIL
               WHERE SYSPARM_CODE = 'RATE_ENGINES'
                  OR SYSPARM_CODE = 'RATE_LOG_DIR'
               ORDER BY SYSPARM_CODE)
  LOOP
    pl(rec.sysparm, v_verbose);
  END LOOP;
  
  BEGIN
    select 'Not Setting for PLSQL engine.'
      INTO v_line
      from dual
     where not exists(select 0 
                        from system_parameter_util
                       where SYSPARM_CODE = 'RATE_ENGINES' 
                          or SYSPARM_CODE = 'RATE_LOG_DIR');
    pl(v_line, v_verbose);
  EXCEPTION
    WHEN no_data_found THEN
      NULL;
  END;
  
  BEGIN 
    SELECT RPAD(name || ' :',20, ' ') || value 
      INTO v_line
      from  v$parameter 
     where name like 'utl_file_dir'
       and exists(select 0 
                    from system_parameter_util
                   where SYSPARM_CODE = 'RATE_ENGINES' 
                      or SYSPARM_CODE = 'RATE_LOG_DIR');
    pl(v_line, v_verbose);
  EXCEPTION
    WHEN no_data_found THEN
      pl('No Data Found - Selecting utl_file_dir from v$parameter', v_verbose);
  END;
  pl('^^^^^^^^^^^^^^^^^^^^ Completed checking PLSQL Rating Engine Settings ^^^^^^^^^^^^^^^^^^^^', v_verbose);
  pl(chr(10), v_verbose);
  pl(chr(10), v_verbose);
  
  pl('vvvvvvvvvvvvvvvvvvvv Getting the invalid objects list vvvvvvvvvvvvvvvvvvvv', v_verbose);
  -- Getting INVALID objects.
  init_sql;
  drop_create_table;
  Get_Invalids;
  pl('vvvvvvvvvvvvvvvvvvvv Completed getting the invalid objects list vvvvvvvvvvvvvvvvvvvv', v_verbose);
  pl(chr(10), v_verbose);
  pl(chr(10), v_verbose);
  
EXCEPTION
  WHEN NOT_SCHEMA_OWNER THEN
    pl('Sorry, you are not logged on as the schema owner. Please log on as the schema owner to continue.');
    pl(rpad('Your Login:',20, ' ')||USER, v_verbose);
    pl(rpad('OASIS Owner:',20, ' ')||v_owner, v_verbose);
    
  WHEN NO_BUILD_APPLIED THEN
    pl('Failed to locate the Build_Applied table. Please contact Release Engineering before proceeding with the build.');
    
END;
/
spool off
set verify on