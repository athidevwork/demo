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

prompt "CURRENT STATUS TIME","FILE_NAME","FILE OUTPUT PATH","POLICY NUMBER","FORM ID","FORM DESCRIPTION","FORM LEVEL","TRANSACTION CODE","TERM ACCOUNTING DATE","TERM EFFECTIVE DATE","TRANSACTION NUMBER","FORM STATUS","SUB SYSTEM","REQUEST ID","RISK NAME",
SELECT ov.current_status_date || ', ' || ov.filename || ', ' || ov.output_path || ', ' || ov.external_id || ', ' || ov.form_id || ', ' || ov.form_desc || ', ' || f.form_level || ', ' || ov.transaction_code || ', ' || ov.req_accounting_date || ', ' || ov.req_effective_date || ', ' || ov.transaction_log_fk || ', ' || ov.trigger_status || ', ' || ov.subsystem_code || ', ' || ov.request_id || ', ' || replace(ov.risk_name, ',', '')
FROM os_form_info_ct_dw_view ov, os_form f
WHERE ov.form_id = f.form_id
AND ov.trigger_status <> 'ERROR'
AND ov.current_status_date < (select TO_DATE(TO_CHAR(add_months(SYSDATE, - &&1),'mm/dd/yyyy'),'mm/dd/yyyy') from dual)
ORDER BY ov.current_status_date DESC;

SPOOL OFF

set heading on
set verify on	
set termout on
exit;
