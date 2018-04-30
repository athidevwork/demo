set linesize 999
set pagesize 50000
set verify off
set feedback off
set heading off
Set Newpage none

COLUMN comma_separated_list FORMAT A255

prompt
prompt output file = &1
prompt

SPOOL &1

set termout off

prompt "PK","DATE","LEVEL","CATEGORY","STATUS","STATUS MESSAGE","PARAMETER NAME","PARAMETER VALUE","STATUS","STATUS MESSAGE","PARAMETER DESCRIPTION"
SELECT o.ohc_pk||','||o.ohc_date||','||o.ohc_level||','||o.ohc_category||','||o.ohc_status||','||o.ohc_status_msg||','||o.parm_name||','||o.parm_value||','||o.parm_desc
FROM oasis_health_check o
order by o.ohc_pk;

SPOOL OFF

set heading on
set verify on	
set feedback on
set termout on
exit;
