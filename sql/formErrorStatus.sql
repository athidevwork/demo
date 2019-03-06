set linesize 999
set pagesize 100
set verify off
set feedback 1

--C:\dev\db\output\formsPerf.txt
accept outputDir char prompt "Enter Output Dir : "
accept numberOfDaysToGoBack char prompt "Enter number of days to go back to : "
--accept formId char prompt "Enter form id (if any) : "

spool '&outputDir'

set termout off

prompt ******* Forms Error Status
-- script to find any error generated by specific form

/*IF formId != '' THEN
	SELECT ofr.external_id           ext_id,
		   ord.form_id,
		   oct.form_id               form_id,
		   ofd.distribution_batch_no batch_no,
		   ofd.unique_sort           unique_sort,
		   ofv.copy_type_code        copy_type,
		   oct.crystal_status        oct_status,
		   oct.status_msg            oct_msg,
		   oct.status_date           oct_status_date,
		   ofv.status                ofv_status,
		   ofv.status_msg            ofv_msg,
		   ofv.status_date           ofv_status_date,
		   ofd.status                ofd_status,
		   ofd.status_msg            ofd_msg,
		   ofd.status_date           ofd_status_date,
		   obq.bundle_status         obq_status,
		   obq.bundle_status_date    obq_status_date,
		   obq.bundle_status_msg     obq_msg
	  FROM os_crystal_trigger   oct,
		   os_form_version      ofv,
		   os_form_distribution ofd,
		   os_bundle_queue      obq,
		   os_form_request      ofr,
		   os_request_detail    ord
	 WHERE oct.os_crystal_trigger_pk = ofv.os_crystal_trigger_fk(+)
	   AND ofv.os_form_version_pk = ofd.source_record_fk(+)
	   AND obq.request_id(+) = oct.request_id
	   AND ofr.request_id = oct.request_id
	   AND oct.sys_update_time >= trunc(SYSDATE-'&numberOfDaysToGoBack')
	   and ord.form_id = oct.form_id
	   AND ord.form_id LIKE '%&formId%'
	   AND ord.request_id = oct.request_id
	   AND (oct.crystal_status = 'ERROR' OR ofv.status = 'ERROR' OR ofd.status = 'ERROR' OR obq.bundle_status = 'ERROR')
	   ORDER BY oct.status_date DESC;
ELSE*/
	SELECT ofr.external_id           ext_id,
		   ord.form_id,
		   oct.form_id               form_id,
		   ofd.distribution_batch_no batch_no,
		   ofd.unique_sort           unique_sort,
		   ofv.copy_type_code        copy_type,
		   oct.crystal_status        oct_status,
		   oct.status_msg            oct_msg,
		   oct.status_date           oct_status_date,
		   ofv.status                ofv_status,
		   ofv.status_msg            ofv_msg,
		   ofv.status_date           ofv_status_date,
		   ofd.status                ofd_status,
		   ofd.status_msg            ofd_msg,
		   ofd.status_date           ofd_status_date,
		   obq.bundle_status         obq_status,
		   obq.bundle_status_date    obq_status_date,
		   obq.bundle_status_msg     obq_msg
	  FROM os_crystal_trigger   oct,
		   os_form_version      ofv,
		   os_form_distribution ofd,
		   os_bundle_queue      obq,
		   os_form_request      ofr,
		   os_request_detail    ord
	 WHERE oct.os_crystal_trigger_pk = ofv.os_crystal_trigger_fk(+)
	   AND ofv.os_form_version_pk = ofd.source_record_fk(+)
	   AND obq.request_id(+) = oct.request_id
	   AND ofr.request_id = oct.request_id
	   AND oct.sys_update_time >= trunc(SYSDATE-'&numberOfDaysToGoBack')
	   and ord.form_id = oct.form_id
	--   AND ord.form_id LIKE '%LPLE%22%'
	   AND ord.request_id = oct.request_id
	   AND (oct.crystal_status = 'ERROR' OR ofv.status = 'ERROR' OR ofd.status = 'ERROR' OR obq.bundle_status = 'ERROR')
	   ORDER BY oct.status_date DESC;
--END IF;  

spool off

set verify on		
set termout on