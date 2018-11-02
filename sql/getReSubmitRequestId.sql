set linesize 999
set pagesize 100
set verify off
set feedback 1

--C:\dev\db\output\reSubmitRequestId.txt
accept outputDir char prompt "Enter Output Dir : "

spool '&outputDir'

set termout off

prompt ******* Request ID List
SELECT DISTINCT oct.request_id, oct.batch_no, ofd.status
      FROM os_crystal_trigger   oct,
           os_form_version      ofv,
           os_form_distribution ofd
     WHERE oct.os_crystal_trigger_pk = ofv.os_crystal_trigger_fk
       AND ofv.os_form_version_pk = ofd.source_record_fk
       and (ofd.status = 'SUBMITTED' OR ofd.status = 'ENDOFDAY');

spool off

set verify on		
set termout on