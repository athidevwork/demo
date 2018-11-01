set serveroutput ON
set verify off
set feedback off
set linesize 1000
set pagesize 50

BEGIN
	INSERT ALL 
	INTO OASIS_HEALTH_CHECK_ENV(ENV_NAME, ENV_CONN_STR) VALUES ('env1','user1/passwd1@host1_sid1')
	INTO OASIS_HEALTH_CHECK_ENV(ENV_NAME, ENV_CONN_STR) VALUES ('env2','user2/passwd2@host2_sid2')
	INTO OASIS_HEALTH_CHECK_ENV(ENV_NAME, ENV_CONN_STR) VALUES ('env3','b''gAAAAABb10RYjVrZx5XwKrdi5xQlxuqoHlkIb38iIIwkWtDjaXxY0mdXyY4r_lVDsXROa9nvqYzhGhLedvBiyZN1cmo80V6EePvmqEXUIb1o5ZrKcd7GpmxsAme6uO83FyUcbApXftCV''')
	SELECT * FROM dual;
END;
/
commit;
/
set serveroutput off
set verify on
set feedback on
exit
