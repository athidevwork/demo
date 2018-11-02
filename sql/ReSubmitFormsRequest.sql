-- SQL to Resubmit ODS V2.1 from the backend.

--C:\dev\db\output\ReSubmitFormsRequest.txt
--accept outputDir char prompt "Enter Output Dir : "
accept requestId char prompt "Enter Request ID : "

--spool '&outputDir'

set termout on
set serveroutput on

--prompt ******* Resubmit forms Request '&requestId'
	
DECLARE	
-- Cursor c_print_req retrieves the unique request IDs to be resubmitted.
  CURSOR c_print_req IS
    SELECT DISTINCT oct.request_id
      FROM os_crystal_trigger   oct,
           os_form_version      ofv,
           os_form_distribution ofd
     WHERE oct.os_crystal_trigger_pk = ofv.os_crystal_trigger_fk
       AND ofv.os_form_version_pk = ofd.source_record_fk
       --and ofd.status = 'SUBMITTED'; 
       --and ofd.status = 'ENDOFDAY'; -- this is for EOD jobs that didn’t get executed the night before

       and oct.request_id = '&requestId';
       --AND oct.batch_no = '26037';
BEGIN	
	  FOR req IN c_print_req LOOP
	  
	  -- Update the OS_Crystal_Trigger Table.
		UPDATE os_crystal_trigger oct
		   SET oct.crystal_status         = decode(oct.form_id, 'BUNDLE', 'BUNDLEHOLD', 'SUBMITTED'),
			   oct.status_msg             = NULL,
			   oct.bundle_form_status     = decode(oct.form_id, 'BUNDLE', 'BUNDLEHOLD', NULL),
			   oct.bundle_form_start_time = NULL
		 WHERE oct.request_id = req.request_id;
	  
	  -- Update the OS_Form_Version Table.
		UPDATE os_form_version ofv
		   SET ofv.status = 'SUBMITTED'
		 WHERE ofv.os_crystal_trigger_fk IN
			   (SELECT os_crystal_trigger_pk
				  FROM os_crystal_trigger oct
				 WHERE oct.request_id = req.request_id);
	  
	  -- Update the OS_Form_Distribution Table.
		UPDATE os_form_distribution ofd
		   SET ofd.status = 'SUBMITTED'
		 WHERE ofd.status <> 'NONEDISTRB'-- ofd.status = 'ENDOFDAY' -- this is for EOD jobs that didn’t get executed the night before
		   AND ofd.source_record_fk IN
			   (SELECT ofv.os_form_version_pk
				  FROM os_form_version ofv
				 WHERE ofv.os_crystal_trigger_fk IN
					   (SELECT os_crystal_trigger_pk
						  FROM os_crystal_trigger oct
						 WHERE oct.request_id = req.request_id));

	  -- Update OS_Bundle_Queue Table.
		UPDATE os_bundle_queue obq
		   SET obq.bundle_status = 'SUBMITTED'
		 WHERE obq.request_id = req.request_id;

	--======================================================================================
	--===   This step is extra and it is not required for resubmitting the jobs unless   ===
	--===   there is a need to change the attribute values before submitting the jobs.   ===
	--===                                                                                ===
	--===   PLEASE COMMENT OUT THIS UPDATE STATEMENT IF NOT NEEDED                       ===
	--======================================================================================

	  -- Update OS_Form_Distribution_Attrb Table,
	  /* UPDATE os_form_distribution_attrb a
		   SET a.Attrb_Value = decode(a.attrb_value, '4', '256',
													 '3', '11', a.attrb_value)
		 WHERE a.attrb_name = 'PRT_TRAY'
		   AND a.os_form_distribution_fk IN
			   (SELECT ofd.os_form_distribution_pk
				  FROM os_form_distribution ofd
				 WHERE ofd.status <> 'NONEDISTRB'
				   AND ofd.Form_Device_Code IN ('\\SRV-BOE\UND-OASIS', '\\SRV-BOE\ITHP9050')
				   AND ofd.source_record_fk IN
					   (SELECT ofv.os_form_version_pk
						  FROM os_form_version ofv
						 WHERE ofv.os_crystal_trigger_fk IN
							   (SELECT os_crystal_trigger_pk
								  FROM os_crystal_trigger oct
								 WHERE oct.request_id = req.request_id
								 --WHERE oct.batch_no = '26037'
								)));
	  */
	END LOOP;
END;
/