set linesize 999
set pagesize 50000
set verify off
set feedback off
set heading off
Set Newpage none

COLUMN comma_separated_list FORMAT A255

prompt
prompt Database params - output file = &1, date = '&&2'
prompt

SPOOL &1

set termout off

prompt "PARAMETER NAME","PARAMETER VALUE","STATUS","STATUS MESSAGE","PARAMETER DESCRIPTION"
SELECT o.parm_name||','||o.parm_value||','||o.status||','||o.msg||','||o.parm_desc
FROM oasis_health_check o
WHERE o.run_date = '&&2'
order by o.parm_name;

SPOOL OFF

set heading on
set verify on	
set feedback on
set termout on
exit;
