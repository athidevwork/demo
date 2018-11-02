set linesize 255;
set serveroutput on;

prompt subsystem: &1, email: &2, stats: &3

execute oasis_health_check_main.main('&1', '&2', &3);
--execute oasis_health_check_main.main('&1');
--execute oasis_health_checks_ods.oasis_health_check_main('&1');

commit;

set serveroutput off;
exit;
