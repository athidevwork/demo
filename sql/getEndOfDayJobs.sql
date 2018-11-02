set linesize 999
set pagesize 100
set verify off
set feedback 1

--C:\dev\db\output\getEndOfDayJobs.txt
accept outputDir char prompt "Enter Output Dir : "

spool '&outputDir'

set termout off

prompt ******* End Of Day Jobs List

select * FROM dba_jobs 
WHERE WHAT LIKE 'OS_FORM_ORDER.MAIN%';

spool off

set verify on		
set termout on
