declare
   v_sql         VARCHAR2(10000);
   v_req_id      NUMBER;
   
   ba_fk number;
   run_date date;
   run_billing_b varchar2(1);
   msg varchar2(100);
   
  v_parmArray    cs_runsp.parameter_type;
  args_tab      cs_runsp.parameter_type;
  forms_tab      cs_runsp.parameter_type;

  v_ret_msg     VARCHAR2(200);
  v_msg         VARCHAR2(200);
  v_subsystem   VARCHAR2(10) := 'PM';
  v_ret_code    NUMBER;
  v_context_fk  NUMBER;
  v_term        NUMBER;
  v_numParm     NUMBER;
  v_trans_code  os_form_request.transaction_code%type;

  TYPE                t_ref_curs IS REF CURSOR;
  cur_tl              t_ref_curs;

  CURSOR Policy_list IS
    select * from policy_info;

BEGIN
    FOR r_pol_list IN Policy_list LOOP
      BEGIN
        /* Delete OS records if exist */
        DELETE FROM os_form_distribution d
         WHERE EXISTS (
                   SELECT 1
                     FROM os_form_version v, os_crystal_trigger c
                    WHERE v.os_crystal_trigger_fk = c.os_crystal_trigger_pk
                      AND v.os_form_version_pk = d.source_record_fk
                      AND c.request_id = r_pol_list.request_id);
  
        DELETE FROM os_form_version v
         WHERE EXISTS (
                   SELECT 1
                     FROM os_crystal_trigger c
                    WHERE c.os_crystal_trigger_pk = v.os_crystal_trigger_fk
                      AND c.request_id = r_pol_list.request_id);
  
        DELETE FROM os_crystal_trigger c
         WHERE c.request_id = r_pol_list.request_id;
  
        DELETE FROM os_request_queue q
         WHERE q.request_id = r_pol_list.request_id;
  
        DELETE FROM os_request_detail rd
         WHERE rd.request_id = r_pol_list.request_id;
  
        SELECT transaction_code
          INTO v_trans_code
          FROM os_form_request
         WHERE request_id = r_pol_list.request_id;
  
        DELETE FROM os_form_request fr
         WHERE fr.request_id = r_pol_list.request_id;
  
        DELETE FROM os_request_util ru
         WHERE ru.request_id = r_pol_list.request_id;
    
        /* Delete XTF data */
        os_forms_extract_purge.xtf_purge(r_pol_list.request_id, 'Y', NULL);
        BEGIN
          os_forms_extract.xt_to_xtf(r_pol_list.request_id, null);
        EXCEPTION
          WHEN OTHERS THEN
          dbms_output.put_line('XT_TO_XTF extract error happened in the current Transaction is '||to_char(r_pol_list.request_id, '9999999999'));
        END;
    
        v_numParm := 6;
        SELECT p.policy_no,
               p.trans_type_code,
               to_char(p.effective_from_date,'mm/dd/yyyy') eff_date,
               to_char(p.trans_accounting_date,'mm/dd/yyyy') acct_date,
               to_char(p.transaction_fk) transaction_fk,
               to_char(p.term_base_fk) term_base_fk
          INTO v_parmArray(1),
               v_parmArray(2),
               v_parmArray(3),
               v_parmArray(4),
               v_parmArray(5),
               v_parmArray(6)
          FROM xt_policy p
         WHERE p.request_id = r_pol_list.request_id;
           
        v_msg := 'Calling os_gen_request.main.';
        BEGIN
          os_gen_request.main (v_subsystem,
                               v_numParm,
                               v_parmArray,
                               r_pol_list.request_id,
                               v_ret_code,
                               v_ret_msg);
  
          dbms_output.put_line('Policy '||r_pol_list.policy_no||' request_id '||r_pol_list.request_id||' has re-extracted with forms.');
        exception
          when others then
            dbms_output.put_line('Error happens on form generation current request_id is '||to_char(r_pol_list.request_id, '9999999999'));
        end;
      Exception
        WHEN OTHERS THEN
         dbms_output.put_line('Error happens in the current request_id is '||to_char(r_pol_list.request_id, '9999999999'));
         dbms_output.put_line(SQLCODE);
         dbms_output.put_line(SQLERRM);
         dbms_output.put_line(' ');
      END;
    END LOOP; 
end;
/
exit;
/
