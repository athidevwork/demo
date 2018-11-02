--create or replace view MAG_ENVIRONMENT_VARIABLES AS
select 'SYSTEM_PARAMETER_UTIL' TABLE_NAME,
       s.sysparm_code          COL1,
       s.sysparm_description   COL2,
       s.sysparm_value         COL3,
       ''                      COL4,
       ''                      COL5,
       ''                      COL6,
       ''                      COL7,
       ''                      COL8
 from system_parameter_util s
where s.sysparm_code like 'OWS%'

union

select 'SYSTEM_PARAMETER_UTIL',
       s.sysparm_code,
       s.sysparm_description,
       s.sysparm_value,
       '',
       '',
       '',
       '',
       ''
 from system_parameter_util s
where s.sysparm_code like 'ODS%'

union

select 'SYSTEM_PARAMETER_UTIL',
       s.sysparm_code,
       s.sysparm_description,
       s.sysparm_value,
       '',
       '',
       '',
       '',
       ''
 from system_parameter_util s
where s.sysparm_code like 'OS%'

union

select 'SYSTEM_PARAMETER_UTIL',
       s.sysparm_code,
       s.sysparm_description,
       s.sysparm_value,
       '',
       '',
       '',
       '',
       ''
 from system_parameter_util s
where s.sysparm_code like 'PM_CALL%'

union

select 'SYSTEM_PARAMETER_UTIL',
       s.sysparm_code,
       s.sysparm_description,
       s.sysparm_value,
       '',
       '',
       '',
       '',
       ''
 from system_parameter_util s
where s.sysparm_code like '%DIR%'

union

select 'OWS_PARAMETER',
       to_char(o.ows_parameter_pk),
       o.request_name,
       o.url,
       o.userid,
       o.password,
       to_char(o.effective_from_date,'MM/DD/YYYY'),
       to_char(o.effective_to_date,'MM/DD/YYYY'),
       ''
  from ows_parameter o

union

select 'OWS_ACCESS_TRAIL_CONFIG',
       oa.request_name,
       oa.enable_logging_b,
       '',
       '',
       '',
       '',
       '',
       ''
  from ows_access_trail_config oa
  
union
  
select 'PF_WEB_NAVIGATION_UTIL_CUST',
       p.cust_unique_id,
       p.security_b,
       to_char(p.sequence),
       p.url,
       p.tooltip,
       p.hidden_b,
       p.parent_cust_unique_id,
       ''
  from pf_web_navigation_util_cust p
 where url like '%centrify%'
 
union 
  
select 'LOOKUP_CODE',
       l.code,
       l.lookup_type_code,
       l.short_description,
       l.long_description,
       l.long_desc_variation1,
       l.long_desc_variation2,
       to_char(l.effective_from_date,'MM/DD/YYYY'),
       to_char(l.effective_to_date,'MM/DD/YYYY')
  from lookup_code l
 where lookup_type_code like '%ELO%'

union

select 'ALL_DIRECTORIES',
       d.OWNER,
       d.DIRECTORY_NAME,
       d.DIRECTORY_PATH,
       to_char(d.ORIGIN_CON_ID),
       '',
       '',
       '',
       ''      
  from all_directories d
 
order by 1,2;
