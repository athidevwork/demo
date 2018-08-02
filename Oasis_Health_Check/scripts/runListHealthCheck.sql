set linesize 255;
set serveroutput on;

execute oasis_health_check_main.display_health_checks('&1');

commit;

set serveroutput off;
exit;
