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

prompt "DATE","RUN_NO"
select o.run_date||','||o.run_no
from oasis_health_check o
group by o.run_date, o.run_no
;

SPOOL OFF

set heading on
set verify on	
set feedback on
set termout on
exit;
