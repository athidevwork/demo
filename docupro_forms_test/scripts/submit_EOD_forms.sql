BEGIN
	update os_crystal_trigger 
	   set crystal_status = 'SUBMITTED',STATUS_MSG = ''
	where crystal_status = 'ENDOFDAY' and request_id in(select request_id from policy_info);
	 
	update os_form_distribution ofd 
		   set ofd.status = 'SUBMITTED'
	where ofd.status = 'ENDOFDAY' and ofd.source_record_fk in(select ofv.os_form_version_pk
	from os_crystal_trigger            oct,
		 os_form_version               ofv,
		 policy_info                   pi
	where ofv.os_crystal_trigger_fk = oct.os_crystal_trigger_pk
	and oct.request_id = pi.request_id);
END;
/
exit;
/