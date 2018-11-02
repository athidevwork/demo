set serveroutput on
declare

 n_policy_fk number := &Policy_Fk;
 v_wip_b     VARCHAR2(1) := '&WIP_B';  -- wip_b indicator
 n_rc 	number      := 0;
 v_rmsg varchar2(80); 
 
begin

pm_lock.Unlock_WIP ( n_policy_fk --p_policy_pk IN  Policy.policy_pk%TYPE,
           , v_wip_b   --  IN  VARCHAR2,  -- wip_b indicator
           , n_rc      --  OUT NUMBER,    -- return code
           , v_rmsg);  --  OUT VARCHAR2 ) -- return message
dbms_output.put_line('n_rc - '||n_rc);
dbms_output.put_line('n_rmsg - '||v_rmsg);

end;
/

