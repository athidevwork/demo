set linesize 999
set pagesize 100
set verify off
set feedback 1

--C:\dev\db\output\formsPerf.txt
accept outputDir char prompt "Enter Output Dir : "
spool '&outputDir'

set termout off

prompt ******* Forms Performance Processing Times
--Forms Performance.
SELECT form_id,
       COUNT(*) form_count,
       AVG(ce_processing_time)
  FROM os_crystal_trigger
 WHERE TRUNC(sys_update_time) >= TRUNC(SYSDATE)
 GROUP BY form_id;

prompt
prompt
prompt ******* Forms Performance By Year/Month
--Forms Performance broken down by month.
SELECT form_id,
       to_char(sys_update_time, 'yyyymm') yyyymm,
       COUNT(*),
       trunc(AVG(ce_processing_time), 1)
  FROM os_crystal_trigger
 WHERE TRUNC(sys_update_time) >= TRUNC(SYSDATE - 180)
--AND form_id IN ('SOI','EXCESSSOI')
 GROUP BY form_id,
          to_char(sys_update_time, 'yyyymm')
 ORDER BY yyyymm DESC;
 
spool off

set verify on		
set termout on