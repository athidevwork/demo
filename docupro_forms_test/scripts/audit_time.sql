set serveroutput ON
set verify off
set feedback off
set linesize 1000
set pagesize 50

prompt request id = &1

DECLARE
  start_time varchar2(75);
  end_time varchar2(75);
  diff_hours varchar2(75);
  diff_mins varchar2(75);
  diff_seconds varchar2(75);
  
  PROCEDURE pl(s IN VARCHAR2, v IN BOOLEAN DEFAULT TRUE) IS
  BEGIN 
    IF (v) THEN 
       dbms_output.enable(1000000);
       dbms_output.put_line(s); 
    END IF;
  END pl;

BEGIN
	pl('Starting get time for docupro forms test - ' || SYSTIMESTAMP);	
	SELECT to_char(a.ttime,'dd/mm/yyyy hh:mi:ss am') as max_time, to_char(b.ttime,'dd/mm/yyyy hh:mi:ss am') as min_time,  
			trunc(24*mod(a.ttime - b.ttime,1)) as hrs,
			trunc(mod(mod(a.ttime - b.ttime,1)*24,1)*60) as mins ,
			round(mod(mod(mod(a.ttime - b.ttime,1)*24,1)*60,1)*60) as secs
	INTO end_time, start_time, diff_hours, diff_mins, diff_seconds
	FROM (select MAX(oct.sys_update_time) as ttime 
		  from os_crystal_trigger oct 
		  WHERE 1=1 AND oct.request_id = &1 
		  order by oct.sys_create_time DESC) a,
		  (select MIN(oct.sys_create_time) as ttime
		  from os_crystal_trigger oct 
		  WHERE 1=1 AND oct.request_id = &1
		  order by oct.sys_create_time DESC) b
	;
	pl('Start Time : ' ||start_time);
	pl('End Time : ' ||end_time);
	pl('Hours : ' ||diff_hours||', Min : '||diff_mins||', Seconds : '||diff_seconds);
END;
/
commit;
/
set serveroutput off
set verify on
set feedback on
exit
