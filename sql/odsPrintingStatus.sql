set linesize 999
set pagesize 100
set verify off
set feedback 1

--C:\dev\db\output\formsPerf.txt
accept outputDir char prompt "Enter Output Dir : "
accept numberOfDaysToGoBack char prompt "Enter number of days to go back to : "
--accept formId char prompt "Enter form id (if any) : "

spool '&outputDir'

prompt ******* Recent Request Id in the last '&numberOfDaysToGoBack' days
SELECT *
FROM os_form_request t
WHERE t.sys_update_time > SYSDATE - '&numberOfDaysToGoBack';

accept requestId char prompt "Enter Request ID : "

set termout off

prompt ******* ODS Printing Status for request ID '&requestId'
SELECT ofv.filename,
       oct.form_id,
       oct.crystal_status,
       ofv.copy_type_code,
       ofv.status,
       ofd.form_device_code,
       ofd.attrb_grp_code,
       ofd.status,
       ofda.attrb_name,
       ofda.attrb_value
  FROM os_crystal_trigger         oct,
       os_form_version            ofv,
       os_form_distribution       ofd,
       os_form_distribution_attrb ofda
 WHERE oct.os_crystal_trigger_pk = ofv.os_crystal_trigger_fk
   AND ofv.os_form_version_pk = ofd.source_record_fk
   AND ofd.os_form_distribution_pk = ofda.os_form_distribution_fk(+)
   AND oct.request_id = '&requestId';


spool off

set verify on		
set termout on