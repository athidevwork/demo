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

accept policyno char prompt "Enter policy No: "
spool H:\dev\policy\'&policyno'.txt

set termout off

prompt ******* Transaction Log Data
select *
from transaction_log
 where policy_fk = 
       ( select policy_pk
           from policy
          where policy_no = upper('&policyno'))
order by transaction_log_pk desc
/

prompt
prompt
prompt ******* Policy Data
select 
POLICY_PK                      
,POLICY_NO                      
,POLICY_CYCLE_CODE              
,POLICY_TYPE_CODE               
,POL_CURR_STATUS_CODE           
,POL_CURR_REASON_CODE           
,PROCESS_STATUS_CODE            
,LEGACY_POLICY_NO               
,INCEPTION_DATE                 
,SID                            
,SERIAL#                        
,LOGON_TIME                     
,WIP_B                          
,LOCK_TIME                      
,LEGACY_ISSUE_COMPANY_FK        
,FORMATTED_POL_NO               
from policy
 where policy_no = upper('&policyno')
/

prompt
prompt
prompt ******* Policy Period data
select policy_period_pk per_pk, term_base_record_fk term_base_fk,
       substr(record_mode_code,1,3) rec_mode,
       official_record_fk official, transaction_log_fk trans, closing_trans_log_fk close,
       to_char(effective_from_date,'mm/dd/yyyy') effec_from, 
       to_char(effective_to_date,'mm/dd/yyyy') effec_to,
       to_char(accounting_from_date,'mm/dd/yyyy') acct_from, 
       to_char(accounting_to_date,'mm/dd/yyyy') acct_to,
       rate_indicator_code rate
  from policy_period_rate
 where policy_fk = 
       ( select policy_pk
           from policy
          where policy_no = upper('&policyno'))
/

prompt
prompt
prompt ******* Term data
select policy_term_history_pk term_pk, term_base_record_fk term_base_fk,
       base_record_b base,
       pol_rel_stat_type_code, policy_term_code term,
       substr(record_mode_code,1,9), 
       official_record_fk official, transaction_log_fk trans, closing_trans_log_fk close,
       to_char(effective_from_date,'mm/dd/yyyy') effec_from, 
       to_char(effective_to_date,'mm/dd/yyyy') effec_to,
       to_char(accounting_from_date,'mm/dd/yyyy') acct_from, 
       to_char(accounting_to_date,'mm/dd/yyyy') acct_to,
       last_transaction_fk last_trans,
	issue_state_code i_st,
	issue_company_entity_fk i_co,	
       process_location_code,
	short_term_b,
	renewal_cycle_code RC,
	renewal_INDICATOR_code RI,
        peer_groups_code mp
  from policy_term_history
 where policy_fk = 
       ( select policy_pk
           from policy
          where policy_no = upper('&policyno'))
 order by 2,3 desc,1 asc 
/

prompt
prompt
prompt ******* Policy Component data
select POLICY_COV_COMPONENT_PK pcc_pk,
       pol_cov_comp_base_rec_fk pcc_base,
       coverage_base_record_fk cov_base,
       base_record_b base, substr(record_mode_code,1,3),
       official_record_fk official, transaction_log_fk trans, closing_trans_log_fk close,
       after_image_record_b after, 
       to_char(effective_from_date,'mm/dd/yyyy') effec_from, 
       to_char(effective_to_date,'mm/dd/yyyy') effec_to,
       to_char(accounting_from_date,'mm/dd/yyyy') acct_from, 
       to_char(accounting_to_date,'mm/dd/yyyy') acct_to,
       component_value value,
       product_cov_component_fk
  from policy_coverage_component
 where coverage_base_record_fk =
                         ( select policy_pk 
                             from policy 
                            where policy_no = upper('&policyno'))
 order by 2, 4 desc, 1
/

prompt
prompt
prompt ******* Coverage Part data
select coverage_part_pk, product_coverage_part_code cov_code, coverage_part_base_record_fk cbase_fk, base_record_b base, 
       substr(curr_pol_rel_stat_type_cd,1,7),substr(curr_pol_rel_reason_code,1,5),substr(record_mode_code,1,3),
       official_record_fk official, transaction_log_fk trans, closing_trans_log_fk close,
       after_image_record_b, 
       to_char(effective_from_date,'mm/dd/yyyy') effec_from, 
       to_char(effective_to_date,'mm/dd/yyyy') effec_to,
       to_char(accounting_from_date,'mm/dd/yyyy') acct_from, 
       to_char(accounting_to_date,'mm/dd/yyyy') acct_to, 
       cancellation_method_code,
       retroactive_date
  from coverage_part
 where policy_fk =
                ( select policy_pk 
                    from policy 
                   where policy_no = upper('&policyno'))
 order by 3, 5 desc, 1
/

prompt
prompt
prompt ******* Coverage Part Component data
select POLICY_COV_COMPONENT_PK pcc_pk, pol_cov_comp_base_rec_fk pcc_base, coverage_base_record_fk cov_part_base,
       base_record_b base, substr(record_mode_code,1,9),
       official_record_fk official, transaction_log_fk trans, closing_trans_log_fk close,
       after_image_record_b after, 
       to_char(effective_from_date,'mm/dd/yyyy') effec_from, 
       to_char(effective_to_date,'mm/dd/yyyy') effec_to,
       to_char(accounting_from_date,'mm/dd/yyyy') acct_from, 
       to_char(accounting_to_date,'mm/dd/yyyy') acct_to,
       component_value value,
       product_cov_component_fk
  from policy_coverage_component
 where coverage_base_record_fk in
       ( select coverage_part_base_record_fk
           from coverage_part
          where policy_fk =
                ( select policy_pk 
                    from policy 
                   where policy_no = upper('&policyno')))
 order by 2, 4 desc, 1
/

prompt
prompt
prompt ******* Risk data
select risk_pk, POLICY_FK, risk_base_record_fk rbase_fk, base_record_b baserecord,primary_risk_b primaryrisk,
	   substr(curr_pol_rel_stat_type_cd,1,9), substr(curr_pol_rel_reason_code,1,5), substr(record_mode_code,1,9),
	   official_record_fk official, transaction_log_fk trans, closing_trans_log_fk close,
	   after_image_record_b ai_b, 
	   to_char(effective_from_date,'mm/dd/yyyy') effec_from, 
	   to_char(effective_to_date,'mm/dd/yyyy') effec_to,
	   to_char(accounting_from_date,'mm/dd/yyyy') acct_from, 
	   to_char(accounting_to_date,'mm/dd/yyyy') acct_to,
	   entity_fk,risk_type_code,
	   SLOT_ID,
	   substr(pm_sel_entity_name(risk_base_record_fk, 'RISK'),1,20),
	   risk_cls_used_to_rate risk_class,
	   NVL(risk_process_code,'NULL') procs_cd,
		property_risk_fk,
		practice_state_code PS,
		coverage_part_base_record_fk cp_base,
		nvl(char3,'nvl') char3,
		county_code_used_to_rate cnty,
		city_code city,
		rating_basis,
		exposure_basis_code
		, rolling_ibnr_b ibnr
		, rolling_ibnr_status ibnr_stat
		, frame_type_code COMM
		--, claims_trigger
 from risk
 where policy_fk =
       ( select policy_pk
           from policy
          where policy_no = upper('&policyno'))
 order by 2, 3 desc, 1
/

prompt
prompt
prompt ******* Coverage data
select coverage_pk, product_coverage_code cov_code, coverage_base_record_fk cbase_fk, risk_base_record_fk rbase_fk, base_record_b base, 
       substr(curr_pol_rel_stat_type_cd,1,9),substr(curr_pol_rel_reason_code,1,5),substr(record_mode_code,1,9),
       official_record_fk official, transaction_log_fk trans, closing_trans_log_fk close,
       after_image_record_b, 
       to_char(effective_from_date,'mm/dd/yyyy') effec_from, 
       to_char(effective_to_date,'mm/dd/yyyy') effec_to,
       to_char(accounting_from_date,'mm/dd/yyyy') acct_from, 
       to_char(accounting_to_date,'mm/dd/yyyy') acct_to ,
       term_base_record_fk term_fk,
       to_char(retroactive_date,'mm/dd/yyyy') retro_dt,
	   to_char(PRIORACTS_RETRO_DATE,'mm/dd/yyyy') pacts_retro_dt,
       coverage_limit_code,
	   limit_erosion_code,
       shared_limit_b,
		annual_base_rate,
		pcf_participation_date pcf_date,
		primary_coverage_b,
		RATING_BASIS,
		num1,
		date1,
		date2,
		state_code state,
		exposure_unit,
		parent_coverage_base_record_fk prnt_covg_fk,
        char3 AttPt,
        building_rate cvgmin
  from coverage
 where risk_base_record_fk in
       ( select distinct risk_base_record_fk
           from risk
          where policy_fk =
                ( select policy_pk 
                    from policy 
                   where policy_no = upper('&policyno')))
   --and parent_coverage_base_record_fk is null
 order by 3, 5 desc, 1
/

prompt
prompt
prompt ******* Coverage Relation data
select coverage_relation_pk cr_pk, cvg_relation_type_code type, coverage_parent_fk parent,
       coverage_child_fk child, substr(record_mode_code,1,3),
       official_record_fk official, transaction_log_fk trans, closing_trans_log_fk close,
       to_char(effective_from_date,'mm/dd/yyyy') effec_from, 
       to_char(effective_to_date,'mm/dd/yyyy') effec_to,
       to_char(accounting_from_date,'mm/dd/yyyy') acct_from, 
       to_char(accounting_to_date,'mm/dd/yyyy') acct_to
  from coverage_relation
 where coverage_parent_fk in
       ( select distinct coverage_base_record_fk 
           from coverage
          where risk_base_record_fk in
                ( select distinct risk_base_record_fk
                    from risk
                   where policy_fk =
                         ( select policy_pk 
                             from policy 
                            where policy_no = upper('&policyno'))))
 order by 1 asc
/

prompt
prompt
prompt ******* Component data
select POLICY_COV_COMPONENT_PK pcc_pk, 
       pol_cov_comp_base_rec_fk pcc_base,
       coverage_base_record_fk cov_base,
       base_record_b base, substr(record_mode_code,1,3),
       official_record_fk official, transaction_log_fk trans, closing_trans_log_fk close,
       after_image_record_b after, 
       to_char(effective_from_date,'mm/dd/yyyy') effec_from, 
       to_char(effective_to_date,'mm/dd/yyyy') effec_to,
       to_char(accounting_from_date,'mm/dd/yyyy') acct_from, 
       to_char(accounting_to_date,'mm/dd/yyyy') acct_to,
       component_value value,
       product_cov_component_fk,
       to_char(component_cycle_date,'mm/dd/yyyy') cycle,
	renew_b
  from policy_coverage_component
 where coverage_base_record_fk in
       ( select distinct coverage_base_record_fk 
           from coverage
          where risk_base_record_fk in
                ( select distinct risk_base_record_fk
                    from risk
                   where policy_fk =
                         ( select policy_pk 
                             from policy 
                            where policy_no = upper('&policyno'))))
 order by 2, 4 desc, 1
/

prompt
prompt
prompt ******* Sub Coverage data
select coverage_PK sub_pk, coverage_base_record_fk sub_base_fk, parent_coverage_base_record_fk parent_cov_base,
       base_record_b base,product_coverage_code pcode, substr(record_mode_code,1,3),
       official_record_fk official, transaction_log_fk trans, closing_trans_log_fk close,
       after_image_record_b after, 
       to_char(effective_from_date,'mm/dd/yyyy') effec_from, 
       to_char(effective_to_date,'mm/dd/yyyy') effec_to,
       to_char(accounting_from_date,'mm/dd/yyyy') acct_from, 
       to_char(accounting_to_date,'mm/dd/yyyy') acct_to,
       to_char(retroactive_date,'mm/dd/yyyy') retro_dt,
       annual_base_rate,
	exposure_unit,
	num1 n1,
	RATING_BASIS
  from coverage
 where parent_coverage_base_record_fk in
       ( select distinct coverage_base_record_fk 
           from coverage
          where risk_base_record_fk in
                ( select distinct risk_base_record_fk
                    from risk
                   where policy_fk =
                         ( select policy_pk 
                             from policy 
                            where policy_no = upper('&policyno'))))
 order by 2, 4 desc, 1
/

prompt
prompt
prompt ******* Risk Relation data
select RISK_RELATION_PK rrel_pk, risk_child_fk child, risk_parent_fk parent,
       RISK_RELATION_TYPE_CODE type,substr(record_mode_code,1,3),
       official_record_fk official, transaction_log_fk trans, closing_trans_log_fk close,
       after_image_record_b, 
       curr_status_code status,
       to_char(effective_from_date,'mm/dd/yyyy'), 
       to_char(effective_to_date,'mm/dd/yyyy'),
       to_char(accounting_from_date,'mm/dd/yyyy'), 
       to_char(accounting_to_date,'mm/dd/yyyy') --,
       override_risk_base_fk,
       curr_status_code,
	   to_rate_b
  from risk_relation
 where risk_parent_fk in
       ( select distinct risk_base_record_fk
           from risk
          where policy_fk =
                ( select policy_pk 
                    from policy 
                   where policy_no = upper('&policyno')))
    or risk_child_fk in
       ( select distinct risk_base_record_fk
           from risk
          where policy_fk =
                ( select policy_pk 
                    from policy 
                   where policy_no = upper('&policyno')))
 order by 1
/

prompt
prompt
prompt ******* Special Handling data
select POL_SPECIAL_HANDLING_PK pol_spec_pk, policy_fk, transaction_log_fk trans, 
       closing_trans_log_fk close,
       to_char(effective_from_date,'mm/dd/yyyy'), 
       to_char(effective_to_date,'mm/dd/yyyy'),
       to_char(accounting_from_date,'mm/dd/yyyy'), 
       to_char(accounting_to_date,'mm/dd/yyyy')
  from policy_special_handling
 where policy_fk =
       ( select policy_pk 
           from policy 
          where policy_no = upper('&policyno'))
/

prompt
prompt
prompt ******* Tax data
select PREMIUM_TAX_HEADER_PK tax_pk, policy_fk, risk_fk, transaction_log_fk trans ,
       closing_trans_log_fk close, official_record_fk official,
       after_image_record_b,
       record_mode_code, 
       to_char(effective_from_date,'mm/dd/yyyy') effec_from, 
       to_char(effective_to_date,'mm/dd/yyyy') effec_to,
       to_char(accounting_from_date,'mm/dd/yyyy') acct_from, 
       to_char(accounting_to_date,'mm/dd/yyyy') acct_to,
	tax_level,
	tax_code,
	state_code state,
	county_code cnty,
	city_code city 
  from premium_tax_header, premium_tax_code
 where premium_tax_code_pk = premium_tax_code_fk 
and policy_fk =
       ( select policy_pk 
           from policy 
          where policy_no = upper('&policyno'))
/

prompt
prompt
prompt ******** Manuscript data
select MANUSCRIPT_ENDORSEMENT_PK manus_pk, coverage_fk, transaction_log_fk trans ,
       closing_trans_log_fk close, official_record_fk official,
       after_image_record_b,
       record_mode_code, 
       to_char(effective_from_date,'mm/dd/yyyy') effec_from, 
       to_char(effective_to_date,'mm/dd/yyyy') effec_to,
       to_char(accounting_from_date,'mm/dd/yyyy') acct_from, 
       to_char(accounting_to_date,'mm/dd/yyyy') acct_to,
       form_code  
  from manuscript_endorsement
 where coverage_fk in
       ( select distinct coverage_base_record_fk 
           from coverage
          where risk_base_record_fk in
                ( select distinct risk_base_record_fk
                    from risk
                   where policy_fk =
                         ( select policy_pk 
                             from policy 
                            where policy_no = upper('&policyno'))))
/

prompt
prompt
prompt ******** Mini Tail data
select MINI_TAIL_PK mini_pk, COVERAGE_BASE_RECORD_FK cov_base, transaction_log_fk trans ,
       closing_trans_log_fk close, official_record_fk official,
       record_mode_code, 
       to_char(effective_from_date,'mm/dd/yyyy') effec_from, 
       to_char(effective_to_date,'mm/dd/yyyy') effec_to,
       to_char(accounting_from_date,'mm/dd/yyyy') acct_from, 
       to_char(accounting_to_date,'mm/dd/yyyy') acct_to,
       mini_tail_factor  
  from mini_tail
 where coverage_base_record_fk in
       ( select distinct coverage_base_record_fk 
           from coverage
          where risk_base_record_fk in
                ( select distinct risk_base_record_fk
                    from risk
                   where policy_fk =
                         ( select policy_pk 
                             from policy 
                            where policy_no = upper('&policyno'))))
/

prompt
prompt
prompt ******* Scheduled Component data
select POLICY_SCH_COMPONENT_PK, POLICY_COV_COMPONENT_FK,        
       SCHEDULED_COMPONENT_FK         
  from policy_scheduled_component
 where policy_cov_component_fk in
       ( select policy_cov_component_pk
           from policy_coverage_component
          where coverage_base_record_fk in
                ( select distinct coverage_base_record_fk 
                    from coverage
                   where risk_base_record_fk in
                         ( select distinct risk_base_record_fk
                             from risk
                            where policy_fk =
                                  ( select policy_pk 
                                      from policy 
                                     where policy_no = upper('&policyno')))))
/

prompt
prompt
prompt ******** Risk Suspend data
select RISK_SUSPEND_PERIOD_PK, risk_fk, 
       substr(record_mode_code,1,3),
       official_record_fk official, transaction_log_fk trans, closing_trans_log_fk close,
       to_char(suspend_from_date,'mm/dd/yyyy'), 
       to_char(SUSPEND_TO_DATE,'mm/dd/yyyy'),
       to_char(accounting_from_date,'mm/dd/yyyy'), 
       to_char(accounting_to_date,'mm/dd/yyyy')
  from risk_suspend_period
 where risk_fk in
       ( select distinct risk_base_record_fk
           from risk
          where policy_fk =
                ( select policy_pk 
                    from policy 
                   where policy_no = upper('&policyno')))
/

prompt
prompt
prompt ******** VL data
select COV_RELATED_ENTITY_PK crel_pk, COVERAGE_FK cov_base, risk_fk risk_base,
       curr_status_code status, transaction_log_fk trans ,
       closing_trans_log_fk close, official_record_fk official,
       substr(record_mode_code,1,3), 
       to_char(effective_from_date,'mm/dd/yyyy'), 
       to_char(effective_to_date,'mm/dd/yyyy'),
       to_char(accounting_from_date,'mm/dd/yyyy'), 
       to_char(accounting_to_date,'mm/dd/yyyy')
  from coverage_related_entity
 where coverage_fk in
       ( select distinct coverage_base_record_fk 
           from coverage
          where risk_base_record_fk in
                ( select distinct risk_base_record_fk
                    from risk
                   where policy_fk =
                         ( select policy_pk 
                             from policy 
                            where policy_no = upper('&policyno'))))
/

prompt
prompt
prompt ******** Entity role
select er.entity_role_pk er_pk, er.entity_fk, er.role_type_code, er.record_mode_code,
er.official_record_fk off, er.transaction_log_fk trans, er.closing_trans_log_fk close,
       to_char(er.effective_from_date,'mm/dd/yyyy') effec_from, 
       to_char(er.effective_to_date,'mm/dd/yyyy') effec_to,
       to_char(er.accounting_from_date,'mm/dd/yyyy') acct_from, 
       to_char(er.accounting_to_date,'mm/dd/yyyy') acct_to,
       e.first_name_upper||' '||e.last_name_upper name,
       er.source_record_fk,
       er.external_id, er.source_table_name
from entity_role er, entity e
where er.external_id = upper('&policyno')
and e.entity_pk = er.entity_fk
/

prompt
prompt
prompt ******** Shared Group Master
SELECT  
M.POLICY_SHARED_GROUP_MASTER_PK
,M.POLICY_FK
,M.PROD_SHARED_GROUP_MASTER_FK
,M.SHARE_GROUP_NO
,M.SHARE_LIMIT_B
,M.SHARE_DEDUCT_B
,M.RECORD_MODE_CODE
,M.OFFICIAL_RECORD_FK
,M.TRANSACTION_LOG_FK
,M.CLOSING_TRANS_LOG_FK
,TO_CHAR(M.EFFECTIVE_FROM_DATE,'MM/DD/YYYY') EFF_FROM
,TO_CHAR(M.EFFECTIVE_TO_DATE,'MM/DD/YYYY') EFF_TO
,TO_CHAR(M.ACCOUNTING_FROM_DATE,'MM/DD/YYYY') ACCT_FROM
,TO_CHAR(M.ACCOUNTING_TO_DATE,'MM/DD/YYYY') ACCT_TO
,M.SHARE_GROUP_DESC
,M.SHARE_SIR_B
  FROM policy_shared_group_master m
 WHERE m.policy_fk =
 ( SELECT policy_pk 
     FROM policy 
    WHERE policy_no = upper('&policyno'))
ORDER BY 4,1
/



prompt
prompt
prompt ******** Shared Group Detail
SELECT 
D.POLICY_SHARED_GROUP_DETAIL_PK
,D.POLICY_SHARED_GROUP_MASTER_FK
,D.SOURCE_RECORD_FK
,D.SOURCE_TABLE_NAME
,D.OWNER_B
,D.RECORD_MODE_CODE
,D.OFFICIAL_RECORD_FK
,D.TRANSACTION_LOG_FK
,D.CLOSING_TRANS_LOG_FK
,TO_CHAR(D.EFFECTIVE_FROM_DATE,'MM/DD/YYYY') EFF_FROM
,TO_CHAR(D.EFFECTIVE_TO_DATE,'MM/DD/YYYY') EFF_TO
,TO_CHAR(D.ACCOUNTING_FROM_DATE,'MM/DD/YYYY') ACCT_FROM
,TO_CHAR(D.ACCOUNTING_TO_DATE,'MM/DD/YYYY') ACCT_TO
  FROM policy_shared_group_detail d
 WHERE d.policy_shared_group_master_fk in
       ( SELECT distinct m.policy_shared_group_master_pk
           FROM policy_shared_group_master m
          WHERE m.policy_fk =
                ( SELECT policy_pk 
                    FROM policy 
                   WHERE policy_no = upper('&policyno')))
ORDER BY 2,3
/


prompt
prompt
prompt ******** Additional Insured
--SELECT ai.*
SELECT ai.ADDITIONAL_INSURED_PK, ai.POLICY_FK, ai.RISK_FK, ai.ADDITIONAL_INSURED_CODE, ai.RENEW_B,
		ai.CGL_B, ai.PL_B, ai.SHOW_EXCESS_B, ai.SHOW_SIR_B, ai.LIMIT_APPLY_RULE
FROM policy p, ADDITIONAL_INSURED ai
WHERE 1=1
AND p.policy_pk = ai.policy_fk
AND p.policy_pk = ( SELECT policy_pk 
                    FROM policy 
                   WHERE policy_no = upper('&policyno'))
ORDER BY 1
/

prompt
prompt
prompt ******** Risk Relation

SELECT DISTINCT ep.first_name||', '||ep.last_name, ec.organization_name, rr.*
FROM policy p
   , risk rc
   , risk rp
   , risk_relation rr
   , entity ec
   , entity ep
WHERE 1=1
-- Risk (Child) RRG Corp
AND rc.base_record_b ='N'
AND rc.accounting_to_date =to_date('01/01/3000','mm/dd/yyyy')
-- Corp overlaps relation dates
AND rc.effective_from_date < rr.effective_to_date
AND rc.effective_to_date   > rr.effective_from_date
AND rc.effective_from_date < rc.effective_to_date
AND rc.entity_fk = ec.entity_pk

-- Risk (Parent) Related Risks
AND rp.base_record_b ='N'
AND rp.accounting_to_date =to_date('01/01/3000','mm/dd/yyyy')
-- Related risk overlap relation dates
AND rp.effective_from_date < rr.effective_to_date
AND rp.effective_to_date   > rr.effective_from_date
AND rp.effective_from_date < rp.effective_to_date
AND rp.entity_fk = ep.entity_pk
-- Risk Relation has open dates
AND rr.effective_from_date < rr.effective_to_date
AND rr.accounting_to_date =to_date('01/01/3000','mm/dd/yyyy')
-- Risk (Child) RRG Corp is on Policy
AND rr.risk_child_fk = rc.risk_base_record_fk
-- Related Risk may not be on Policy
AND rr.risk_parent_fk = rp.risk_base_record_fk

AND p.policy_pk = rc.policy_fk
AND p.policy_pk = ( SELECT policy_pk 
                    FROM policy 
                   WHERE policy_no = upper('&policyno'))
ORDER BY rr.risk_relation_pk
/

spool off

set verify on		
set termout on
--ed H:\dev\policy\'&policyno'.txt
