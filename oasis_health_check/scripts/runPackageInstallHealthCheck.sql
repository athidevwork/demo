set serveroutput on
set term on
set echo off
set feedback off
set define on
set verify off

define OHC_MAIN_PACKAGE = 'pkg/oasis_health_check_main.pkg';
define OHC_OUTPUT_PACKAGE = 'pkg/oasis_health_check_output.pkg';
define OHC_POLICY_PACKAGE = 'pkg/oasis_health_check_policy.pkg';
define OHC_CLAIMS_PACKAGE = 'pkg/oasis_health_check_claims.pkg';
define OHC_CIS_PACKAGE = 'pkg/oasis_health_check_cis.pkg';
define OHC_FM_PACKAGE = 'pkg/oasis_health_check_fm.pkg';

prompt
prompt Package file to be compiled : &OHC_MAIN_PACKAGE
prompt

@@'&OHC_MAIN_PACKAGE';
/

prompt
prompt Package file to be compiled : &OHC_OUTPUT_PACKAGE
prompt

@@'&OHC_OUTPUT_PACKAGE';
/

prompt
prompt Package file to be compiled : &OHC_POLICY_PACKAGE
prompt

@@'&OHC_POLICY_PACKAGE';
/

prompt
prompt Package file to be compiled : &OHC_CLAIMS_PACKAGE
prompt

@@'&OHC_CLAIMS_PACKAGE';
/

prompt
prompt Package file to be compiled : &OHC_CIS_PACKAGE
prompt

@@'&OHC_CIS_PACKAGE';
/

prompt
prompt Package file to be compiled : &OHC_FM_PACKAGE
prompt

@@'&OHC_FM_PACKAGE';
/

exit;
