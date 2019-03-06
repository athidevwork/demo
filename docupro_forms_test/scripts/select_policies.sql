set serveroutput ON
set verify off
set feedback off
set linesize 1000
set pagesize 50

prompt forms pk = &1, number of policies = &2

BEGIN
	INSERT INTO policy_info
	(select policy_no,request_id
	 from (select xp.policy_no,xp.request_id
           from xt_policy xp,xt_risk xr,os_request_util u
           where xp.request_id = u.request_id
               and u.os_form_fk = &1 /*to select Transaction that have at least one form that has a GD form equivalent*/
               /*and xp.request_id = 1931184367*/
               and xr.xt_policy_fk = xp.xt_policy_pk
               and u.base_b = 'Y'
               and u.cust_b = 'Y'
               group by xp.policy_no,xp.request_id
               having count(xr.xt_risk_pk) > 100) x /*to select Large Policies*/
	 where rownum <= &2
	 );
END;
/
commit;
/
set serveroutput off
set verify on
set feedback on
exit