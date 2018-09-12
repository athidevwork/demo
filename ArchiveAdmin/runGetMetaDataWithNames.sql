set linesize 999
set pagesize 50000
set verify off
set feedback off
set heading off
Set Newpage none

COLUMN comma_separated_list FORMAT A255

prompt
prompt months to go back = &1, output file = &2
prompt

SPOOL &2

set termout off

prompt "SYSTEM CREATED TIME","FILE_NAME","FILE WITH PATH","POLICY NUMBER","FORM ID","FORM DESCRIPTION","TRANSACTION CODE","TERM EFFECTIVE FROM DATE","TERM EFFECTIVE TO DATE","TRANSACTION NUMBER","TRANSACTION TYPE CODE","TRANSACTION DESCRIPTION","FORM STATUS","FORM VERSION STATUS","SUB SYSTEM","REQUEST ID","BATCH NUMBER","RISK NAME","POLICY HOLDER NAME","COI HOLDER NAME"
SELECT oct.sys_create_time || ', ' || ofv.filename || ', ' || ofv.drive_destination||decode(substr(ofv.drive_destination, LENGTH(ofv.drive_destination) -1), '\', '', '\')||ofv.filename || ', ' ||
                ofr.external_id || ', ' || oct.form_id || ', ' || replace(o.form_desc, ',', '') || ', ' || ofr.request_type || ', ' || 
                xp.effective_from_date|| ', ' || xp.effective_to_date || ', ' || ofr.transaction_log_fk || ', ' || 
                ofr.transaction_code || ', ' || ofr.transaction_desc || ', ' || oct.crystal_status || ', ' || ofv.status || ', ' || 
				ofr.subsystem_code || ', ' || oct.request_id || ', ' || oct.batch_no || ', ' || replace(xr.risk_name, ',', '') || ', ' || replace(decode(xn.entity_type, 'O', xn.organization_name, xn.first_name||' '||xn.last_name), ',', '')|| ', ' || replace(xch.coi_name, ',', '')
FROM os_crystal_trigger oct, os_form_request ofr, os_request_detail ord, xt_policy xp, xt_name xn, xt_risk xr, os_form o, os_form_version ofv, xt_coi_holder xch
WHERE oct.request_id = ofr.request_id
AND oct.request_id = ord.request_id
AND oct.request_id = xp.request_id
AND oct.request_id = xn.request_id
and xn.source_table_name = 'POLHOLDER'
AND oct.request_id = xr.request_id
AND ord.index_1 = xr.xt_risk_pk (+)
AND ord.index_2 = xch.xt_coi_holder_pk(+)
AND oct.form_id = o.form_id
AND oct.os_crystal_trigger_pk = ofv.os_crystal_trigger_fk
AND oct.sys_create_time < (select TO_DATE(TO_CHAR(add_months(SYSDATE, - &&1),'mm/dd/yyyy'),'mm/dd/yyyy') from dual)
ORDER BY oct.os_crystal_trigger_pk DESC;

SPOOL OFF

set heading on
set verify on	
set termout on
exit;
