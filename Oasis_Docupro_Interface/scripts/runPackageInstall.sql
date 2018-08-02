set serveroutput on
set term on
set echo off
set feedback off
set define on
set verify off

define FIC_PACKAGE = '../pkg/form_interface_cfg.pkg';

prompt
prompt Package file to be compiled : &FIC_PACKAGE
prompt

@@'&FIC_PACKAGE';
/

exit;
