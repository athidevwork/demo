set linesize 999
set pagesize 100
set verify off
set feedback 1

--C:\dev\db\output\formsPerf.txt
accept outputDir char prompt "Enter Output Dir : "
accept numberOfDaysToGoBack char prompt "Enter number of days to go back to : "

spool '&outputDir'



set termout off

prompt ******* Forms Processing Status
-- script to find any document error in the past 7 days
  SELECT oct.form_id, oct.crystal_status oct_status,
         COUNT(DISTINCT oct.os_crystal_trigger_pk) oct_count,
         ofv.status ofv_status,
         COUNT(DISTINCT ofv.os_form_version_pk) ofv_count,
         ofd.status ofd_status,
         COUNT(DISTINCT ofd.os_form_distribution_pk) ofd_count,
         obq.bundle_status obq_Status,
         COUNT(DISTINCT obq.os_bundle_queue_pk) obq_count
    FROM os_crystal_trigger   oct,
         os_form_version      ofv,
         os_form_distribution ofd,
         os_bundle_queue      obq
   WHERE oct.os_crystal_trigger_pk = ofv.os_crystal_trigger_fk(+)
     AND ofv.os_form_version_pk = ofd.source_record_fk(+)
     AND obq.request_id(+) = oct.request_id
     AND oct.sys_update_time >= TRUNC(SYSDATE - '&numberOfDaysToGoBack')
   GROUP BY oct.form_id, 
			oct.crystal_status,
            ofv.status,
            ofd.status,
            obq.bundle_status;
			
spool off

set verify on		
set termout on