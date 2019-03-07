-- Created on 2/7/2017 by CMEARS 
-- Updated on 5/23/17 by Pam T.
set serveroutput ON
set verify off
set feedb off
set linesize 1000
set pagesize 50
set trimspool on
spool post_install.log
prompt
accept grantee_list Prompt "Enter comma delimited list of users/roles to issue grants in addition to existing grantees: "
prompt
accept create_syns_type Prompt "What type of synonyms should be created.  Choices are PUBLIC, PRIVATE or NONE (default is NONE): "
prompt
prompt

declare 
  v_verbose          boolean  := FALSE;
  v_debug            boolean  := FALSE;
  v_sql              varchar2(1000);
  i_cnt              integer;
  v_grantee_list     varchar2(1000) := ',' || upper('&grantee_list') || ',';
  v_create_syns_type varchar2(100) := upper('&create_syns_type');

  table_does_not_exist exception;
  pragma exception_init( table_does_not_exist, -942 );
  
  comp_invalids_sql varchar(4000);
  tablename varchar2(128) := 'RE_PRE_INVALIDS';

  cursor get_grantees is
    SELECT grantee,
           (SELECT 'Y'
              FROM dual
             WHERE 100 <
                   (SELECT count(*)
                      FROM user_tab_privs utp
                     WHERE utp.grantee = x.grantee
                       AND utp.privilege = 'DELETE')
                OR instr(v_grantee_list, ',' || x.grantee || ',') > 0
           ) write_privs,
           (SELECT 'Y'
              FROM dual
             WHERE 100 <  
                   (SELECT count(*)
                      FROM user_tab_privs utp
                     WHERE utp.grantee = x.grantee
                       AND utp.privilege = 'EXECUTE')
                OR instr(v_grantee_list, ',' || x.grantee || ',') > 0
           ) exec_privs
  -- this finds users/roles that have really received grants.  We ignore one-off grantees         
  FROM (SELECT grantee
          FROM user_tab_privs
         GROUP BY grantee
         HAVING COUNT(*) > 1000
        UNION
        SELECT au.USERNAME
          FROM all_users au
         WHERE instr(v_grantee_list, ',' || au.username || ',') > 0
        ) x;
         
  cursor find_missing_grants (a_grantee  varchar2, a_write_privs varchar2, a_exec_privs varchar2) IS
    WITH grant_type as 
         (SELECT 'JAVA CLASS' object_type, 'EXECUTE' priv from dual UNION
          SELECT 'JAVA CLASS' object_type, 'DEBUG' priv from dual UNION
          SELECT 'JAVA RESOURCE', 'EXECUTE' from dual UNION
          SELECT 'JAVA RESOURCE', 'DEBUG' from dual UNION
          SELECT 'JAVA SOURCE', 'EXECUTE' from dual UNION
          SELECT 'JAVA SOURCE', 'DEBUG' from dual UNION
          SELECT 'FUNCTION', 'EXECUTE' from dual UNION
          SELECT 'TYPE', 'EXECUTE' from dual UNION
          SELECT 'TYPE BODY', 'EXECUTE' from dual UNION
          SELECT 'PACKAGE', 'EXECUTE' from dual UNION
          SELECT 'PROCEDURE', 'EXECUTE' from dual UNION
          SELECT 'SEQUENCE', 'SELECT' from dual UNION
          SELECT 'SYNONYM', 'SELECT' from dual UNION
          SELECT 'SYNONYM', 'INSERT' from dual UNION
          SELECT 'SYNONYM', 'UPDATE' from dual UNION
          SELECT 'SYNONYM', 'DELETE' from dual UNION
          SELECT 'TABLE', 'SELECT' from dual UNION
          SELECT 'TABLE', 'INSERT' from dual UNION
          SELECT 'TABLE', 'UPDATE' from dual UNION
          SELECT 'TABLE', 'DELETE' from dual UNION
		  SELECT 'MATERIALIZED VIEW', 'SELECT' from dual UNION 
		  SELECT 'MATERIALIZED VIEW', 'INSERT' from dual UNION
		  SELECT 'MATERIALIZED VIEW', 'UPDATE' from dual UNION 
		  SELECT 'MATERIALIZED VIEW', 'DELETE'from dual UNION
          SELECT 'VIEW', 'SELECT' from dual UNION
          SELECT 'VIEW', 'INSERT' from dual UNION
          SELECT 'VIEW', 'UPDATE' from dual UNION
          SELECT 'VIEW', 'DELETE' from dual)
    SELECT 'GRANT ' || gt.priv || ' on ' || 
           -- some java objects require explicitly naming the type of objects getting granted
           decode(obj.object_type,
                  'JAVA RESOURCE', 'JAVA RESOURCE ',
                  'JAVA SOURCE', 'JAVA SOURCE ') ||
           ' "' || obj.OBJECT_NAME || '" to ' || a_grantee || '' sql_statement
      FROM obj, grant_type gt
     WHERE obj.object_type = gt.object_type
       AND obj.object_type NOT IN ('INDEX', 'TRIGGER', 'PACKAGE BODY', 'DATABASE LINK', 'QUEUE', 'LOB', 'JOB', 
                                   'TYPE BODY', 'SYNONYM', 'USER', 'xxMATERIALIZED VIEW')
       --AND obj.object_name <> 'DB_INSTANCE'
       AND obj.object_name not in ( 'DB_INSTANCE', 'PLAN_TABLE')
       AND obj.object_name not like 'AQ$%'
       AND (a_write_privs = 'Y' or gt.priv not in ('INSERT','UPDATE','DELETE'))
       AND (a_exec_privs = 'Y' or gt.priv not in ('EXECUTE'))
       -- don't do the queues
       AND obj.object_name not in
           (SELECT distinct queue_table from user_queues)
       -- don't do external tables
       AND obj.object_name not in
           (SELECT table_name from user_external_tables)
       -- look for grant that does not exist
       AND not exists
           (SELECT 0
              FROM user_tab_privs_made tpm
             WHERE tpm.table_name = obj.object_name
               AND tpm.privilege = gt.priv
               AND tpm.GRANTEE = a_grantee)
    order by object_name;

----------------
-- Procedures --
----------------	
  PROCEDURE pl(s IN VARCHAR2, v IN BOOLEAN DEFAULT TRUE) IS
  BEGIN 
    IF (v) THEN 
       dbms_output.enable(1000000);
       dbms_output.put_line(s); 
    END IF;
  END pl;
  
  procedure init_sql is
  begin
    comp_invalids_sql := 'select object_name, object_type
        from obj 
       where status = ''INVALID''
         and object_type in ( ''PACKAGE BODY'', ''PACKAGE'', ''FUNCTION'', ''MATERIALIZED VIEW'',
                        ''PROCEDURE'', ''TRIGGER'', ''VIEW'', ''TYPE'', ''SYNONYM'' )
      minus
      select invalid_object, object_type
        from '||tablename;
      
  end init_sql;
  
  procedure Compare_Invalids is
    obj_name varchar2(128);
    obj_type varchar2(128);
    counter number := 0;
    type cur_type is ref cursor;
    inv_cur cur_type;
    
  begin
    pl('List of invalid object introduced by this delivery:');
	pl(chr(13));
    open inv_cur for comp_invalids_sql;
    loop
      fetch inv_cur into obj_name, obj_type;
      exit when inv_cur%NOTFOUND;
      pl('>   '||obj_name||' - ['||obj_type||'] - is invalid.');
      counter:= counter +1;
      
    end loop;
    pl(chr(13));
	pl(to_char(counter)||' new invalid object(s) were introduced by this delivery.');

  exception
    when table_does_not_exist then
      pl('   table '||tablename||' does not exist.');

  end Compare_Invalids;
---------------------------------------------------------------------
        
begin
        
  IF v_verbose THEN
    dbms_output.put_line('Verbose mode in place.');
    dbms_output.enable(10000000);
  ELSE
    dbms_output.enable(100000);    
  END IF;

  IF v_debug THEN
    dbms_output.put_line('. ');
    dbms_output.put_line('************************************************');
    dbms_output.put_line('Running in debug mode.  No changes will be made.');
    dbms_output.put_line('************************************************');
    dbms_output.put_line('. ');
  END IF;
  
  pl(chr(10));
  dbms_output.put_line('VVVVVVVVVVVVVVVVVVVVVVV About to issue grants to users/roles missing grants. VVVVVVVVVVVVVVVVVVVVVVV');
  pl(chr(10));
  FOR grantee IN get_grantees LOOP
    i_cnt := 0;
    IF v_verbose or v_debug THEN
      IF grantee.write_privs = 'Y' and grantee.exec_privs = 'Y' THEN
        dbms_output.put_line('Generating Read/write/execute grants for user/role ' || grantee.grantee);
      ELSIF grantee.write_privs = 'Y' THEN
        dbms_output.put_line('Generating Write/execute grants for user/role ' || grantee.grantee);
      ELSIF  grantee.exec_privs = 'Y' THEN
        dbms_output.put_line('Generating Read/execute grants for user/role ' || grantee.grantee);
      ELSE
        dbms_output.put_line('Generating Read grants for user/role ' || grantee.grantee);
      END IF;
    END IF;
    
    FOR grnt in find_missing_grants(grantee.grantee, grantee.write_privs, grantee.exec_privs) LOOP
      IF v_verbose THEN
        dbms_output.put_line(grnt.sql_statement);
      END IF;
      BEGIN
        IF not v_debug THEN
          execute immediate grnt.sql_statement;
        END IF;
        i_cnt := i_cnt + 1;
      EXCEPTION
        WHEN others then 
          dbms_output.put_line('Grant statement "' || grnt.sql_statement || '" has error: ' || sqlerrm);
      END;
    END LOOP;
   
    -- grant access to queue 
    IF not v_debug THEN
      BEGIN
        dbms_aqadm.grant_queue_privilege (privilege => 'ALL',queue_name => 'FM_PM_INTQ',grantee => grantee.grantee, grant_option => FALSE );
      EXCEPTION 
        WHEN others then
          dbms_output.put_line('Warning:  Grant to queue FM_PM_INTQ failed.');
      END;
      v_sql := 'grant insert, update, delete, select on FM_PM_INTQ to ' || grantee.grantee || ';';    
      IF v_verbose THEN
        dbms_output.put_line(v_sql);
      END IF;
    END IF;
    
    dbms_output.put_line('Granted ' || i_cnt || ' privileges to ' || grantee.grantee || '.');
  END LOOP;
  
  pl(chr(10));
  dbms_output.put_line('^^^^^^^^^^^^^^^^^^^    Completed grants process.   ^^^^^^^^^^^^^^^^^^^');
  pl(chr(10));
  pl(chr(10));
  
  -- work on synonyms
  DECLARE
    CURSOR get_syn_types IS
      SELECT owner syn_owner
        FROM dba_synonyms 
       WHERE table_owner = user
         AND owner <> table_owner
       GROUP by owner
       HAVING count(*) > 500
      UNION
      SELECT 'PUBLIC'
        FROM dual
       WHERE v_create_syns_type = 'PUBLIC'
      UNION
      SELECT au.USERNAME
        FROM all_users au
       WHERE instr(v_grantee_list, ',' || au.username || ',') > 0
         AND v_create_syns_type = 'PRIVATE';
       
    CURSOR pub_syns IS
      SELECT 'create public synonym "' || a.object_name || '" ' ||
             'for ' || user || '."' || a.object_name || '"' sql_statement
        FROM dba_objects a, dba_synonyms b
       WHERE a.owner = user
         AND a.owner = b.table_owner(+) 
         AND a.object_type not in
              ( 'INDEX', 'TRIGGER', 'PACKAGE BODY', 'DATABASE LINK', 'QUEUE','LOB','TYPE BODY')
         AND a.object_name = b.synonym_name(+)
         AND b.owner(+)  = 'PUBLIC'
         AND b.synonym_name is null
         --AND a.object_name <> 'DUAL'
         AND a.object_name not in ('DUAL', 'PLAN_TABLE')
       ORDER BY a.object_type, a.object_name;
       
    CURSOR priv_syns (v_grantee   varchar2) IS
     SELECT 'create synonym ' || v_grantee || '."' || a.object_name || '" ' ||
             'for ' || user || '."' || a.object_name || '"' sql_statement
        FROM dba_objects a, dba_synonyms b
       WHERE a.owner = user
         AND a.owner = b.table_owner(+) 
         AND a.object_type not in
              ( 'INDEX', 'TRIGGER', 'PACKAGE BODY', 'DATABASE LINK', 'QUEUE','LOB','TYPE BODY')
         AND a.object_name = b.synonym_name(+)
         AND b.owner(+) = v_grantee
         AND b.synonym_name is null
         --AND a.object_name <> 'DUAL'
         AND a.object_name not in ('DUAL', 'PLAN_TABLE')
       ORDER BY a.object_type, a.object_name;
  BEGIN
    dbms_output.put_line('');
    dbms_output.put_line('');
    dbms_output.put_line('');
    dbms_output.put_line('VVVVVVVVVVVVVVVVVVVVVVV    Starting synonym process.  VVVVVVVVVVVVVVVVVVVVVVV');
    FOR syn IN get_syn_types LOOP
      i_cnt := 0;
      dbms_output.put_line('Generating synonyms for ' || syn.syn_owner);
      IF syn.syn_owner = 'PUBLIC' THEN
        FOR syn_sql IN pub_syns LOOP
          IF v_verbose THEN
            dbms_output.put_line(syn_sql.sql_statement);
          END IF;
          BEGIN
            IF not v_debug THEN
              execute immediate syn_sql.sql_statement;
            END IF;
            i_cnt := i_cnt + 1;
          EXCEPTION
            WHEN others then 
              dbms_output.put_line('Synonym statement "' || syn_sql.sql_statement || '" has error: ' || sqlerrm);
          END;
        END LOOP;
        dbms_output.put_line('Created ' || i_cnt || ' PUBLIC synonyms.');
      ELSE
        FOR syn_sql IN priv_syns(syn.syn_owner) LOOP
          IF v_verbose THEN
            dbms_output.put_line(syn_sql.sql_statement);
          END IF;
          BEGIN
            IF not v_debug THEN
              execute immediate syn_sql.sql_statement;
            END IF;
            i_cnt := i_cnt + 1;
          EXCEPTION
            WHEN others then 
              dbms_output.put_line('Synonym statement "' || syn_sql.sql_statement || '" has error: ' || sqlerrm);
          END;
        END LOOP;
        dbms_output.put_line('Created ' || i_cnt || ' synonyms for user ' || syn.syn_owner || '.');
      END IF;
    END LOOP;
	
	pl(chr(10));
    dbms_output.put_line('^^^^^^^^^^^^^^^^^^^    Completed synonym process.   ^^^^^^^^^^^^^^^^^^^');
    pl(chr(10));
    pl(chr(10));
  END;


  -- process FM queue
  DECLARE
    v_fm_queue_sysparm    varchar2(100);
    v_fm_queue_status     varchar2(100);
    v_msg                 varchar2(100);
  BEGIN
    dbms_output.put_line('');
    dbms_output.put_line('');
    dbms_output.put_line('');
    dbms_output.put_line('VVVVVVVVVVVVVVVVVVVVVVV   Checking on FM queue.   VVVVVVVVVVVVVVVVVVVVVVV');
    BEGIN
      SELECT sysparm_value
        INTO v_fm_queue_sysparm
        FROM system_parameter_util
       WHERE sysparm_code like '%PM_WAIT_FOR_FM%'
         AND rownum = 1;
    EXCEPTION
      WHEN no_data_found THEN
        v_fm_queue_sysparm := 'N';
    END;

    BEGIN
      SELECT decode(count(*),
                    0, 'Queue is down',
                    1, 'Queue is up',
                    'Found more than 1 queue, possible error' ) "Queue Status"
        INTO v_fm_queue_status
        FROM dba_jobs
       WHERE substr(lower(what),1,28) = 'fmn_pol_interface_queue_proc'
         AND schema_user = ( select fmn_get_schema_owner from dual );
    END;
    
    IF v_fm_queue_sysparm = 'N' and v_fm_queue_status = 'Queue is down' THEN
      dbms_output.put_line('System is set to use FM Queue. Starting queue....');
      IF not v_debug THEN
        fmn_control_pol_int_queue('START',v_msg);
        dbms_output.put_line(v_msg);
      END IF;
    ELSIF v_fm_queue_sysparm = 'N' and v_fm_queue_status = 'Queue is up' THEN
      dbms_output.put_line('System is set to use FM Queue. Queue is already up.  All set.');
    ELSIF v_fm_queue_sysparm = 'Y' and v_fm_queue_status = 'Queue is down' THEN
      dbms_output.put_line('System is not set to use FM Queue.  Queue is down. All set.');
    ELSIF v_fm_queue_sysparm = 'Y' and v_fm_queue_status = 'Queue is up' THEN
      dbms_output.put_line('System is not set to use FM Queue. Stopping queue....');
      IF not v_debug THEN
        fmn_control_pol_int_queue('STOP',v_msg);
        dbms_output.put_line(v_msg);
      END IF;
    END IF;      
    pl(chr(10));
    dbms_output.put_line('^^^^^^^^^^^^^^^^^^^    Completed FM queue process.   ^^^^^^^^^^^^^^^^^^^');
  END;
  
  -- refresh materialized views
  BEGIN
    pl(chr(10));
    dbms_output.Put_line('Refreshing PF_WEB materialized views...');
    execute immediate ('BEGIN cs_web_mat_view.refresh_all_pfweb_mvs;  END;');
  EXCEPTION
    WHEN OTHERS THEN
      -- means they are running a version of OASIS prior to PF_WEB MV's so ignore
      null;
  END;
  
  BEGIN
    pl(chr(10));
    dbms_output.Put_line('Refreshing PF_PROF materialized views...');
    execute immediate ('BEGIN cs_web_mat_view.refresh_all_pfprof_mvs;  END;');
  EXCEPTION
    WHEN OTHERS THEN
      -- means they are running a version of OASIS prior to PF_PROF MV's so ignore
      null;
  END;
  
  pl(chr(10));
  pl('*****************************************');
  pl('***  Check invalid objects - Started  ***');
  init_sql;
  Compare_Invalids;
  
  pl('*** Check invalid objects - Completed ***');
  pl('*****************************************');

  pl(chr(10));
end;
/
spool off
set verify on

undefine grantee_list
undefine create_prv_syns
exit
