REM record our current settings so that we can set everything back to the
REM originals later on
store set origenv.sql repl

REM first setup helper function used below
CREATE OR REPLACE FUNCTION temp_get_pk_cols_sql (
  p_table          IN All_Ind_Columns.table_name%TYPE)
RETURN VARCHAR2 IS
  v_col_list    varchar2(1000);
  v_col_sql     varchar2(1000);
  v_pk_cols     oasis_config.t_pk_cols;
  v_tbl_owner   varchar2(30);

  CURSOR get_pk_cols IS
     SELECT column_name,
            decode(data_type,
                   'DATE', 'to_char(' || p_table || '.' || column_name || ',''mm/dd/yyyy hh24:mi:ss'')',
                   'NUMBER', 'to_char(' || p_table || '.' || column_name || ')',
                   p_table ||'.' || column_name) col_select
       FROM All_Tab_Columns
      WHERE owner = v_tbl_owner
        AND table_name = upper(rtrim(ltrim(p_table)))
        AND v_col_list like '%,' || column_name || ',%'
     ORDER BY column_id;
BEGIN
    BEGIN
      v_tbl_owner := PM_Table_Owner('SYSTEM_PARAMETER_UTIL');
    EXCEPTION
      WHEN OTHERS THEN RAISE;
        v_tbl_owner := get_oasis_user;
    END;
    
    -- get the array of PK columns
    v_pk_cols := OASIS_Config.Get_Pk_Cols(p_table);
    FOR v_pk_idx IN 1..v_pk_cols.COUNT
    LOOP
       v_col_list := v_col_list || ',' || v_pk_cols(v_pk_idx);
    END LOOP;

    v_col_list := v_col_list || ',';

    -- Use the cursor to return the list of PK columns as SQL so that we can use it in a select.
    FOR col IN get_pk_cols LOOP
      IF v_col_sql is null THEN
         v_col_sql := col.col_select;
      ELSE
         v_col_sql := v_col_sql || ' || ' || col.col_select;
      END IF;
    END LOOP;
    Return (v_col_sql);
END;
/

define logpath = '' 
define q3 = ''
define q4 = ''
accept logpath prompt "Enter the path to store the extract file (no slash at the end): "
accept q3 prompt "Enter U to extract UPDATE statements only (recommended) or I to extract INSERT/UPDATE statements: "
accept q4 prompt "Do you wish to extract statements to purge Output transactional data (Y/N)? "
accept old_user_id prompt "Enter the old userid that you wish to replace: "
accept new_user_id prompt "Enter the desired new userid: "

set echo off
set verify off
set feedback off
set termout off
set linesize 999
set heading off
set serveroutput on size 999999

spool &logpath\extract_apply.sql

prompt REM record our current settings so that we can set everything back to the
prompt REM originals later on
prompt store set origenv.sql repl

prompt accept q2 prompt "Processing updates for environment specific configuration.  Please press 'Enter' to continue:"

prompt set define off
prompt set echo on

DECLARE
  v_text      VARCHAR2(20000);
  v_upd1      VARCHAR2(5000);
  v_upd2      VARCHAR2(5000);
  v_upd3      VARCHAR2(5000);
  v_upd4      VARCHAR2(5000);
  v_ins1      VARCHAR2(5000);
  v_ins2      VARCHAR2(5000);
  v_ins3      VARCHAR2(5000); 
  r_curs      Oasis_Utility.t_ref_cursor;   
  v_msg       VARCHAR2(200);  
  x_error     EXCEPTION;
  v_idx       INTEGER := 0;
  
    
BEGIN
  DELETE FROM PM_REPORT_TEMP;

  IF UPPER('&q3') NOT IN ('U','I') THEN
    v_msg := 'Illegal value entered for UPDATE and/or INSERT stmt generation.  Process aborted.';
    RAISE x_error;
  END IF;

  IF UPPER('&q4') NOT IN ('Y','N') THEN
    v_msg := 'Illegal value entered for output data purge stmt generation.  Process aborted.';
    RAISE x_error;
  END IF;

  v_idx := v_idx + 1;
  INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'exec oasis_config.set_integration(''Y'');');

  -- Create insert stmts "where not exists"
  -- Exclude PF_WEB_NAVIGATION_UTIL, PF_WEB_FORM, PF_WEB_REPORT, PF_WEB_PAGE_FIELD as they are BASE web tables
  IF UPPER('&q3') = 'I' THEN
    FOR r_extract in (SELECT UPPER(s.table_name)   table_name,
                             UPPER(s.column_name)  column_name,
                             UPPER(s.column_value) column_value,
                             DECODE(UPPER(s.table_name),
                                    'SYSTEM_PARAMETER_UTIL', 'SYSPARM_VALUE',
                                    'SYSTEM_PARAMETER_VT', 'SYSPARM_VALUE',                                  
                                    UPPER(s.column_name)) value_column
                        FROM Config_Event_Warning_Setup s
                       WHERE UPPER(s.table_name) NOT IN ('PF_WEB_NAVIGATION_UTIL','WEB_FORM','WEB_REPORT','PF_WEB_PAGE_FIELD')
                       ORDER BY 1,2
                       )
    LOOP
      v_text := '';
      v_msg := 'Processing: '||r_extract.table_name||', '||r_extract.column_name;
      
      FOR r_sql IN (
              SELECT 0 order_by1, column_id order_by2,
                     DECODE(column_id, 
                            1, 'select '' insert into ' || table_name || ' (',
                            ''',') || column_name || '''||' text
                FROM User_Tab_Columns
               WHERE table_name = UPPER(r_extract.table_name)
                 AND column_name not like 'SYS_UPD%'
                 AND column_name not like 'SYS_CR%'
                 AND data_type NOT IN ('LONG','BLOB')
               UNION
              SELECT 1, 1, ''' ) select '' ins1, ' text 
                FROM User_Tables
               WHERE table_name = UPPER(r_extract.table_name)
               UNION 
              SELECT 2 order_by1, column_id order_by2,
                     decode(column_id, 
                            1, ' ',
                            ' || '','' || ') ||
                     decode(data_type,
                            'NUMBER', '''to_number('''''' || to_char(',
                            'DATE',   '''to_date('''''' || to_char(',
                            ''''''''' || replace(') ||
                     column_name ||
                     decode(data_type, 
                            'NUMBER', ') || ' || ''''''')''',
                            'DATE', ',''yyyymmdd'') || ''''''' || ',''''yyyymmdd'''')''' ,
                            ', '''''''', '''''''''''') || ''''''''')||' ' text 
                FROM User_Tab_Columns
               WHERE table_name = UPPER(r_extract.table_name)
                 AND column_name not like 'SYS_UPD%'
                 AND column_name not like 'SYS_CR%'
                 AND data_type NOT IN ('LONG','BLOB')
               UNION
              SELECT 3, 1, '|| '' from dual where not exists (select 0 from '||Upper(r_extract.table_name)||''
                FROM Dual
               UNION
              SELECT 4, 1,
                     DECODE(Upper(r_extract.table_name),
                            'PF_WEB_NAVIGATION_UTIL', ''' WHERE url LIKE ''''%openWebApplication%'''' AND short_description ',
                            ' WHERE ' || REPLACE(REPLACE(REPLACE(temp_get_pk_cols_sql(UPPER(r_extract.table_name)),
                                                   'to_char(',''),
                                                   ')', ''),
                                                   UPPER(r_extract.table_name)||'.', '')
                            )                                          
                            || ' = '' ins2,' text
                FROM Dual 
               UNION 
              SELECT 5 order_by1, 1,
                     DECODE(data_type,
                            'NUMBER', '''to_number('''''' || to_char(',
                            'DATE', '''to_date('''''' || to_char(',
                            ''''''''' || replace(') ||
                     column_name ||
                     DECODE(data_type, 
                            'NUMBER', ') || ' || ''''''')''',
                            'DATE', ',''yyyymmdd'') || ''''''' || ',''''yyyymmdd'''')''' ,
                            ', '''''''', '''''''''''') || ''''''''') || '|| ''||'' ||'  text 
                FROM User_Tab_Columns
               WHERE table_name = UPPER(r_extract.table_name)
                 AND INSTR(REPLACE(REPLACE(REPLACE(temp_get_pk_cols_sql(UPPER(r_extract.table_name)),
                           'to_char(',''),
                           ')', ''),
                           UPPER(r_extract.table_name)||'.', ''), column_name) > 0
               UNION
              SELECT 6 order_by1, 1,
                     ''''''''''')'' ins3 FROM '||UPPER(r_extract.table_name)
                FROM Dual               
               UNION
              SELECT 7, 1,
                     ' WHERE ' || r_extract.column_name || ' = ''' || r_extract.column_value ||'''' 
                FROM Dual
               WHERE r_extract.column_value IS NOT NULL 
                 AND r_extract.table_name <> 'PF_WEB_NAVIGATION_UTIL'
               ORDER BY 1,2)
       LOOP
         v_text := v_text || r_sql.text;                               
       END LOOP;
       
       OPEN r_curs FOR v_text;
       LOOP
         FETCH r_curs INTO v_ins1, v_ins2, v_ins3;
         EXIT WHEN r_curs%NOTFOUND;

         v_idx := v_idx + 1;
         INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) 
         VALUES (v_idx, v_ins1||v_ins2||v_ins3||';');

       END LOOP;             
    END LOOP;
  END IF;

  FOR r_extract in (SELECT UPPER(s.table_name)   table_name,
                           UPPER(s.column_name)  column_name,
                           UPPER(s.column_value) column_value,
                           DECODE(UPPER(s.table_name),
                                  'SYSTEM_PARAMETER_UTIL', 'SYSPARM_VALUE',
                                  'SYSTEM_PARAMETER_VT', 'SYSPARM_VALUE',                                  
                                  UPPER(s.column_name)) value_column
                      FROM Config_Event_Warning_Setup s
                     WHERE UPPER(s.table_name) NOT IN ('PF_WEB_NAVIGATION_UTIL','WEB_FORM','WEB_REPORT','PF_WEB_PAGE_FIELD')
                     ORDER BY 1,2
                     )
  LOOP
    v_text := '';
    v_msg := 'Processing: '||r_extract.table_name||', '||r_extract.column_name;
    
    FOR r_sql IN (
            SELECT 0 order_by1, 1 order_by2,
                   'SELECT '' UPDATE ' || UPPER(r_extract.table_name) ||
                   ' SET ' || r_extract.value_column || '= '' upd1,' text
              FROM Dual
             UNION
            SELECT 2 order_by1,column_id order_by2,
                   DECODE(data_type,
                          'NUMBER', '''to_number('''''' || to_char(',
                          'DATE',   '''to_date('''''' || to_char(',
                          ''''''''' || replace(') ||
                   column_name ||
                   DECODE(data_type, 
                          'NUMBER', ') || ' || ''''''')''',
                          'DATE', ',''yyyymmdd'') || ''''''' || ',''''yyyymmdd'''')''' ,
                          ', '''''''', '''''''''''') || ''''''''')||' upd2,' text 
              FROM User_Tab_Columns
             WHERE table_name = UPPER(r_extract.table_name)
               AND column_name = r_extract.value_column     
             UNION
            SELECT 3, 1,
                   DECODE(r_extract.table_name,
                          'PF_WEB_NAVIGATION_UTIL', ''' WHERE url LIKE ''''%openWebApplication%'''' AND short_description ',
                          ''' WHERE ' || REPLACE(REPLACE(REPLACE(temp_get_pk_cols_sql(UPPER(r_extract.table_name)),
                                                 'to_char(',''),
                                                 ')', ''),
                                                 UPPER(r_extract.table_name)||'.', '')
                          )                                          
                          || ' = '' upd3,' text 
              FROM Dual 
             UNION
            SELECT 4 order_by1, column_id order_by2,
                   DECODE(data_type,
                          'NUMBER', '''to_number('''''' || to_char(',
                          'DATE', '''to_date('''''' || to_char(',
                          ''''''''' || replace(') ||
                   column_name ||
                   DECODE(data_type, 
                          'NUMBER', ') || ' || ''''''')''',
                          'DATE', ',''yyyymmdd'') || ''''''' || ',''''yyyymmdd'''')''' ,
                          ', '''''''', '''''''''''') || ''''''''') || '|| ''||'' ||'  text 
              FROM User_Tab_Columns
             WHERE table_name = UPPER(r_extract.table_name)
               AND INSTR(REPLACE(REPLACE(REPLACE(temp_get_pk_cols_sql(UPPER(r_extract.table_name)),
                         'to_char(',''),
                         ')', ''),
                         UPPER(r_extract.table_name)||'.', ''), column_name) > 0 
               AND r_extract.table_name <> 'PF_WEB_NAVIGATION_UTIL'     
             UNION
            SELECT 4 order_by1, column_id order_by2,
                   DECODE(data_type,
                          'NUMBER', '''to_number('''''' || to_char(',
                          'DATE', '''to_date('''''' || to_char(',
                          ''''''''' || replace(') ||
                   column_name ||
                   DECODE(data_type, 
                          'NUMBER', ') || ' || ''''''')''',
                          'DATE', ',''yyyymmdd'') || ''''''' || ',''''yyyymmdd'''')''' ,
                          ', '''''''', '''''''''''') || ''''''''') || '|| ''||'' ||'  text 
              FROM User_Tab_Columns
             WHERE table_name = UPPER(r_extract.table_name)
               AND column_name = 'SHORT_DESCRIPTION'
               AND r_extract.table_name = 'PF_WEB_NAVIGATION_UTIL'                                                                        
             UNION
            SELECT 5 order_by1, 1,
                   ''''''''''''' FROM '||UPPER(r_extract.table_name)
              FROM Dual       
             UNION
            SELECT 6, 1,
                   ' WHERE ' || r_extract.column_name || ' = ''' || r_extract.column_value ||'''' 
              FROM Dual
             WHERE r_extract.column_value IS NOT NULL 
               AND r_extract.table_name <> 'PF_WEB_NAVIGATION_UTIL'     
             UNION
            SELECT 6, 1,
                   ' WHERE ' || r_extract.column_name || ' LIKE ''%openWebApplication%'''
              FROM Dual
             WHERE r_extract.table_name = 'PF_WEB_NAVIGATION_UTIL'                                                                        
             ORDER BY 1,2)
     LOOP
       v_text := v_text || r_sql.text;                                
     END LOOP;
     
     OPEN r_curs FOR v_text;
     LOOP
       FETCH r_curs INTO v_upd1, v_upd2, v_upd3, v_upd4;
       EXIT WHEN r_curs%NOTFOUND;
       v_idx := v_idx + 1;
       INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) 
       VALUES (v_idx, v_upd1||v_upd2||v_upd3||v_upd4||';');
     END LOOP;
                  
  END LOOP;

  IF UPPER('&q4') = 'Y' THEN
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'set echo off');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'accept q3 prompt "Processing deletion of output related tables (OS).  Please press ''Enter'' to continue:"');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'set echo on');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_AUDIT_DETAIL;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_AUDIT_HEADER;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_AUDIT_UTIL;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_BUNDLE_QUEUE;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_COI_EXTRACT;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_CRYSTAL_TRIGGER;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_FORM_DISTRIBUTION;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_FORM_DISTRIBUTION_ATTRB;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_FORM_REQUEST;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_FORM_VERSION;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_FREE_FORM_DATA;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_MANUSCRIPT_EXTRACT;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_QUEST_EXTRACT;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_QUEST_FORMS;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_REQUEST_DETAIL;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_REQUEST_QUEUE;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_REQUEST_UTIL;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_COMMENTS_UTIL;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_COMPUSET_STYLE_UTIL;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_CONTEXT_INFO_UTIL;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_DLS_PRINT_QUEUE_UTIL;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_DLS_TRIGGER;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_DOCUCORP_TRIGGER;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_MNSCR_DATA;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_PRINT_HISTORY;');
    v_idx := v_idx + 1;
    INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'truncate table OS_PRINT_QUEUE_UTIL;');
  END IF;

  IF (UPPER('&old_user_id') IS NOT NULL AND UPPER('&new_user_id') IS NOT NULL) THEN
    IF UPPER('&old_user_id') <> UPPER('&new_user_id') THEN
      v_idx := v_idx + 1;
      INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'set echo off');
      v_idx := v_idx + 1;
      INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'accept q5 prompt "Processing userid update.  Please press ''Enter'' to continue:"');
      v_idx := v_idx + 1;
      INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'set echo on');
      v_idx := v_idx + 1;
      INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'UPDATE CLAIMS_STAFF SET USERID = UPPER(''&new_user_id'') WHERE USERID = UPPER(''&old_user_id'');'); 
      v_idx := v_idx + 1;
      INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'UPDATE OASIS_DATASEC_USERRULE_XREF SET USERID = UPPER(''&new_user_id'') WHERE USERID = UPPER(''&old_user_id'');');
      v_idx := v_idx + 1;
      INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'UPDATE OASIS_DATA_SECURITY SET USERID = UPPER(''&new_user_id'') WHERE USERID = UPPER(''&old_user_id'');');
      v_idx := v_idx + 1;
      INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'UPDATE PFUSER SET USERID = UPPER(''&new_user_id'') WHERE USERID = UPPER(''&old_user_id'');');
      v_idx := v_idx + 1;
      INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'UPDATE PFUSER_COL SET USERID = UPPER(''&new_user_id'') WHERE USERID = UPPER(''&old_user_id'');');
      v_idx := v_idx + 1;
      INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'UPDATE PFUSER_CTRL SET USERID = UPPER(''&new_user_id'') WHERE USERID = UPPER(''&old_user_id'');');
      v_idx := v_idx + 1;
      INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'UPDATE PFUSER_PROF SET USERID = UPPER(''&new_user_id'') WHERE USERID = UPPER(''&old_user_id'');');
      v_idx := v_idx + 1;
      INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'UPDATE PFUSER_WEB_EVENT SET USERID = UPPER(''&new_user_id'') WHERE USERID = UPPER(''&old_user_id'');');
      v_idx := v_idx + 1;
      INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'UPDATE PFUSER_WIN SET USERID = UPPER(''&new_user_id'') WHERE USERID = UPPER(''&old_user_id'');');
    END IF;
  END IF;

  v_idx := v_idx + 1;
  INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'exec oasis_config.set_integration(''N'');');
  v_idx := v_idx + 1;
  INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'prompt Commit or Rollback as necessary.');
  v_idx := v_idx + 1;
  INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'set echo off');
  v_idx := v_idx + 1;
  INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, 'REM Set our environment back to the original settings.');
  v_idx := v_idx + 1;
  INSERT INTO PM_REPORT_TEMP(pm_report_temp_pk, char20) values (v_idx, '@origenv');
  
  COMMIT;

EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK;
    RAISE_APPLICATION_ERROR(-20001, 'ERROR: '||v_msg);
END;
/

select char20 from pm_report_temp
order by pm_report_temp_pk;

spool off

REM drop the temp function
drop function temp_get_pk_cols_sql;

REM Set our environment back to the original settings.
@origenv

prompt Extracted file to &logpath\extract_apply.sql
