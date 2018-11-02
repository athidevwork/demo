set linesize 999
set pagesize 60
set verify off
set feedback 1

col description format a75
col sys_create_time format a25
col sys_update_time format a25
--col primary_key format a20

accept formId char prompt "Enter Form ID : "
spool C:\dev\db\forms\'&formId'.txt

set termout off

prompt ******* System Parameters Data
SELECT spu.sysparm_code as code, spu.sysparm_value as value, spu.sysparm_description AS DESCRIPTION  
FROM System_Parameter_Util spu
WHERE spu.sysparm_code IN ('OS_XML_DIRECTORY', 'ELOQ_DATE_SPLIT_LIST', 'OS_CPY_OFFCL_FRMSET', 
							'PREVIEW_COPY_TYPE', 'DOC_GEN_PRD_NAME', 'OS_DOC_DIRECTORY') 
ORDER BY 1 DESC;

prompt
prompt
prompt ******* Os Form Interface Config Data
SELECT * 
FROM OS_FORM_INTERFACE_CONFIG o 
ORDER BY o.category, o.sub_category, o.code;

--non crystal form config
prompt
prompt
prompt ******* Os Form Interface Data
SELECT o.* FROM os_form_interface o WHERE o.form_id like '%&formId%';

--Form setup, check valid_form_date, valid_to_date
prompt
prompt
prompt ******* Os Form Data
SELECT * FROM os_form o WHERE o.form_id LIKE '%&formId%' ORDER BY 1 DESC;

--mapping for base_rule and cust_rule, valid_from and valid_to
prompt
prompt
prompt ******* Os Form Map Data
SELECT * FROM os_form_map o 
WHERE o.form_id LIKE '%&formId%' 
ORDER BY 1 DESC
;

--device mapping
prompt
prompt
prompt ******* Os Form Device Type Map Data
select * from os_form_device_map m where m.form_id like '&formId';

--device type 
prompt
prompt
prompt ******* Os Form Device Type Data
select * from os_form_device ct ORDER BY 1 DESC;

--copy type mapping
prompt
prompt
prompt ******* Os Form Copy Type Map Data
select * from os_form_copy_type_map m where m.form_id = '&formId'; 

--copy type 
prompt
prompt
prompt ******* Os Form Copy Type Data
select * from os_form_copy_type ct ORDER BY 1 DESC;

--form schedule
prompt
prompt
prompt ******* Os Form Schedule Data
select * from os_form_schedule o where o.form_id like '%END09-031%';

--base rule / transaction
prompt
prompt
prompt ******* Base Rule per transaction Data
SELECT ofm.transaction_code, ofr.*
FROM os_form_rule ofr, os_form_map ofm
WHERE ofr.rule_code = ofm.base_rule_code
      AND ofm.form_id = '&formId';
	  
spool off

set verify on		
set termout on