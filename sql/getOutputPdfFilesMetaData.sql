set linesize 999
set pagesize 1000
set verify off
set feedback 1
set heading off

COLUMN comma_separated_list FORMAT A255

/*accept outputDir char prompt "Enter Output Dir : "
spool '&outputDir'*/

accept cutOffDate char prompt "Enter Cut Off date (mm/dd/yyyy): "

--SET SQLFORMAT CSV
--set markup csv on

SPOOL C:\dev\db\output\pdfMetadata.csv
set termout off

/*SELECT oct.request_id, CONCAT(oct.drive_destination, oct.filename), ofr.external_id, oct.form_id, ofr.request_type, xp.effective_from_date, xp.effective_to_date, ofr.transaction_log_fk, ofr.transaction_code, ofr.transaction_desc, ofr.subsystem_code, oct.*--count(*)
FROM os_crystal_trigger oct, os_form_request ofr, xt_policy xp 
WHERE oct.request_id = ofr.request_id
AND oct.request_id = xp.request_id
ORDER BY oct.os_crystal_trigger_pk DESC;*/

prompt "SYSTEM UPDATED TIME","FILE WITH PATH","POLICY NUMBER","FORM ID","FORM DESCRIPTION","TRANSACTION CODE","TERM EFFECTIVE FROM DATE","TERM EFFECTIVE TO DATE","TRANSACTION NUMBER","TRANSACTION TYPE CODE","TRANSACTION DESCRIPTION","SUB SYSTEM","REQUEST ID","BATCH NUMBER"
SELECT oct.sys_update_time || ', ' || CONCAT(ofv.drive_destination, ofv.filename) || ', ' ||
                ofr.external_id || ', ' || oct.form_id || ', ' || o.form_desc || ', ' || ofr.request_type || ', ' || 
                xp.effective_from_date|| ', ' || xp.effective_to_date || ', ' || ofr.transaction_log_fk || ', ' || 
                ofr.transaction_code || ', ' || ofr.transaction_desc || ', ' || ofr.subsystem_code || ', ' || 
				oct.request_id || ', ' || oct.batch_no
FROM os_crystal_trigger oct, os_form_request ofr, xt_policy xp, os_form o, os_form_version ofv
WHERE oct.request_id = ofr.request_id
AND oct.request_id = xp.request_id
AND oct.form_id = o.form_id
AND oct.os_crystal_trigger_pk = ofv.os_crystal_trigger_fk
AND oct.sys_update_time < TO_DATE('&cutOffDate','mm/dd/yyyy')
ORDER BY oct.os_crystal_trigger_pk DESC;

SPOOL OFF
--/
set heading on
set verify on	
set termout on
