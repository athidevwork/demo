--@H:\bin\showpol.sql

set linesize 155
rem set pagesize 25
set verify off
set feedback 1

col effec_from format a10
col effec_to format a10
col acct_from format a10
col acct_to format a10
col mt_rate_dt format a10
col mt_acct_dt format a10
col name format a15
col quote_id format a15
col rkey format a10
col ckey format a10
col prikey format a12

accept quoteno char prompt "Enter Quote No: "
spool H:\dev\policy\'&quoteno'.txt

set termout off

prompt ******* Quote Request Data
select q.ws_qq_request_pk prikey, q.quick_quote_id quote_id, q.quick_quote_generation_date, q.policy_type_code,
       q.start_date, q.end_date 
from WS_QQ_Request q
WHERE q.quick_quote_id = upper('&quoteno')
/

prompt ******* Quote Request Insured Data
select q.ws_qq_insured_pk prikey, q.quick_quote_id quote_id, q.risk_key rkey, q.risk_type_code, q.risk_class_code, q.state_code,
       q.postal_code, q.postal_plus_four
from WS_QQ_Insured q
WHERE q.quick_quote_id = upper('&quoteno')
order by 1 desc
/

prompt ******* Quote Request Coverage Data
select q.ws_qq_coverage_pk prikey, q.quick_quote_id quote_id, q.risk_key rkey, q.coverage_key ckey, q.policy_form_code, 
       q.product_coverage_code, q.retroactive_date, q.coverage_limit_code, q.current_cm_step, q.premium_amount, 
       q.net_premium_amount
from WS_QQ_Coverage q
WHERE q.quick_quote_id = upper('&quoteno')
order by 1 desc
/

prompt ******* Quote Request Component Data
select q.ws_qq_component_pk prikey, q.quick_quote_id quote_id, q.coverage_key ckey, q.coverage_component_code, q.component_value,
       q.current_cm_step, q.premium_amount
from WS_QQ_Component q
WHERE q.quick_quote_id = upper('&quoteno')
order by 1 desc
/

spool off

set verify on		
set termout on
--ed H:\dev\policy\'&policyno'.txt
