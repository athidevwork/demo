set linesize 255;
set serveroutput on;

execute form_interface_cfg.ghostdraft_os_form_intf_config('&1','&2','&3');

commit;

set serveroutput off;
exit;
