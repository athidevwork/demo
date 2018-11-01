set linesize 999
set pagesize 50000
set verify off
set feedback off
set heading off
Set Newpage none

COLUMN comma_separated_list FORMAT A255

prompt
prompt Database params - output file = &1, date = '&&2', run no = &&3
prompt

SPOOL &1

set termout off

prompt "SUB SYSTEM","TEST ID","PARAMETER NAME","EXPECTED VALUE/CONDITION","STATUS","STATUS MESSAGE","PARAMETER DESCRIPTION"
SELECT o.sub_system||','||o.ohc_code||','||o.parm_name||','||o.parm_value||','||o.status||','||o.msg||','||o.parm_desc
FROM oasis_health_check o
WHERE TO_DATE(substr(o.run_date, 0, instr(o.run_date, ' ')), 'MM/DD/YYYY HH:MI:SS') = TO_DATE('&&2', 'MM/DD/YYYY')
      AND o.run_no = &&3
order by o.parm_name;

SPOOL OFF

set heading on
set verify on	
set feedback on
set termout on
exit;
