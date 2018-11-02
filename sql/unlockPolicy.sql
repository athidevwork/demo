set feedback 1
set verify off
--set termout off

accept policyno char prompt "Enter policy No: "

update policy
set lock_time = null, logon_time = null, sid = null, serial# = null, locked_by = null
where policy_no = '&policyno'
/

update oasis_web_lock_util o set o.source_record_fk = ''
where o.source_record_fk =
	(select p.policy_pk
	 FROM oasis_web_lock_util o, policy p
	 WHERE o.source_record_fk = p.policy_pk
	 AND p.policy_no = '&policyno');
/

prompt Please commit this session and try logging in now to policy &policyno
set verify on
--set termout on