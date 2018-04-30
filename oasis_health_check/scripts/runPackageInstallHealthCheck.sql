set serveroutput on
set term on
set echo off
set feedback off
set define on
set verify off

define OHC_MAIN_PACKAGE = 'pkg/oasis_health_check_main.pkg';
define OHC_OUTPUT_PACKAGE = 'pkg/oasis_health_check_output.pkg';
define OHC_POLICY_PACKAGE = 'pkg/oasis_health_check_policy.pkg';

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
exit;
