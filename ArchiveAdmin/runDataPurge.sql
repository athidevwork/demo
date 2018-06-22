SET SERVEROUTPUT ON
set autoprint on
set verify off

prompt
prompt Email = &1, Months = &2, preview = &3
prompt

BEGIN
	cs_oasis_archive.G_RUN_WEEKDAY_CHK := 'N';
	cs_oasis_archive.G_RUN_DELTRG_OPT := 'D';
	cs_email.MAX_LOB_WIDTH := 32765;
	DBMS_OUTPUT.ENABLE(BUFFER_SIZE=>NULL);

	cs_oasis_archive.purge_id(
	  p_arch_id=>'OSARCH',
	  p_params=>'^param1^&&2^',
	  p_arch_sql_type=> 'BYMONTHS',
	  p_preview=>'&&3',
	  p_to_email_addr=>'&&1');

	cs_oasis_archive.purge_id(
	  p_arch_id=>'NONPMOSARCH',
	  p_params=>'^param1^&&2^',
	  p_arch_sql_type=> 'BYMONTHS',
	  p_preview=>'&&3',
	  p_to_email_addr=>'&&1');
	  
	cs_oasis_archive.purge_id(
	  p_arch_id=>'XTARCH',
	  p_params=>'^param1^&&2^',
	  p_arch_sql_type=> 'BYMONTHS',
	  p_preview=>'&&3',
	  p_to_email_addr=>'&&1');

	cs_oasis_archive.purge_id(
	  p_arch_id=>'XTFARCH',
	  p_params=>'^param1^&&2^',
	  p_arch_sql_type=> 'BYMONTHS',
	  p_preview=>'&&3',
	  p_to_email_addr=>'&&1');

	cs_oasis_archive.purge_id(
	  p_arch_id=>'FMXTARCH',
	  p_params=>'^param1^&&2^',
	  p_arch_sql_type=> 'BYMONTHS',
	  p_preview=>'&&3',
	  p_to_email_addr=>'&&1');	  
END;
/

exit