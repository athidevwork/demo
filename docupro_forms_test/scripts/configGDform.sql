set serveroutput ON
set verify off
set feedback off
set linesize 1000
set pagesize 50

prompt effective_date=&1, form_pk=&2

BEGIN
	oasis_config.Set_Integration('Y');
	update os_form_interface ofi set ofi.effective_to_date = TO_DATE('&&1','MM/DD/YYYY') where ofi.os_form_interface_pk = &2;
	oasis_config.Set_Integration('N');
END;
/
commit;
/
set serveroutput off
set verify on
set feedback on
exit
