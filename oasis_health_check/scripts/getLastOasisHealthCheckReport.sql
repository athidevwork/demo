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

prompt "SUB SYSTEM","TEST ID","PARAMETER NAME","EXPECTED VALUE/CONDITION","STATUS","STATUS MESSAGE","PARAMETER DESCRIPTION"
SELECT o.sub_system||','||o.ohc_code||','||o.parm_name||','||o.parm_value||','||o.status||','||o.msg||','||o.parm_desc
FROM oasis_health_check o
WHERE o.run_date IN (
  select max(o1.run_date)
  FROM oasis_health_check o1
)
order by o.sub_system, o.ohc_code;

SPOOL OFF

set heading on
set verify on	
set feedback on
set termout on
exit;
