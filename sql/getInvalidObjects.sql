set linesize 999
set pagesize 100
set verify off
set feedback 1

--C:\dev\db\output\getInvalidObjects.txt
accept outputDir char prompt "Enter Output Dir : "

spool '&outputDir'

set termout off

prompt ******* Invalid Objects List

select * FROM all_objects
WHERE object_type NOT IN ('TABLE', 'INDEX', 'CLUSTER','INDEX PARTITION','TABLE PARTITION')
AND status != 'VALID'
ORDER BY last_ddl_time DESC;

spool off

set verify on		
set termout on
