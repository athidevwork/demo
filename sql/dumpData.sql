--set linesize 999
--set pagesize 60
set verify off
set heading off
set feedback off
set long 100000

col xml format a100000

spool C:\dev\db\forms\dump.xml

set termout off

SELECT dbms_xmlgen.getxml('SELECT * FROM OS_FORM_INTERFACE_CONFIG o ORDER BY o.category, o.sub_category, o.code') xml from dual;

SELECT dbms_xmlgen.getxml('SELECT spu.sysparm_code as code, spu.sysparm_value as value, spu.sysparm_description AS DESCRIPTION
FROM System_Parameter_Util spu
WHERE spu.sysparm_code IN (''OS_XML_DIRECTORY'', ''ELOQ_DATE_SPLIT_LIST'', ''OS_CPY_OFFCL_FRMSET'',''PREVIEW_COPY_TYPE'', ''DOC_GEN_PRD_NAME'', ''OS_DOC_DIRECTORY'')
ORDER BY 1 DESC') xml FROM dual;

SELECT dbms_xmlgen.getxml('SELECT * FROM OS_FORM_MAP ORDER BY 1 DESC') xml from dual;

spool off
set verify on		
set termout on