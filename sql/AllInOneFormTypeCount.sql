DECLARE
  v_tab VARCHAR2(50);
  v_forms_type VARCHAR(25);
  v_forms_count INTEGER;
  forms_cursor SYS_REFCURSOR;
BEGIN 
  --one script overview to work in all  env.
  
  --check if the user is schema owner
  --select get_oasis_user from dual;
  --select PM_TABLE_OWNER from dual;
  
  --check if table exists
  SELECT t.tname
  INTO v_tab
  from tab t
  where upper(t.tname) = 'OS_FORM_INTERFACE' and t.tabtype = 'TABLE';
  
  dbms_output.put_line('Table : ' || v_tab);
  IF v_tab = 'OS_FORM_INTERFACE' THEN
    --ufe / crystal / Ghostdraft / eloquence
    OPEN forms_cursor FOR
      SELECT DECODE(ofi.doc_gen_prd_name, 
                    'ELOQUENCE',  'ELOQUENCE', 
                    'GHOSTDRAFT', 'GHOSTDRAFT',
                     decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL')
                    ) AS formstype, 
             count(DISTINCT f.form_id) AS formcount
      FROM os_form f, os_form_interface ofi
      WHERE f.form_id = ofi.form_id
      group by DECODE(ofi.doc_gen_prd_name, 
                    'ELOQUENCE',  'ELOQUENCE', 
                    'GHOSTDRAFT', 'GHOSTDRAFT',
                     decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL') 
                    )
      ORDER BY 1;
  ELSE
    --ufe / crystal
    OPEN forms_cursor FOR
      SELECT decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL') AS formstype, count(f.form_id) AS formcount
      FROM os_form f
      group by decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL')
      ORDER BY formstype;
  END IF;

  LOOP
    FETCH forms_cursor INTO v_forms_type, v_forms_count;
    EXIT WHEN forms_cursor%NOTFOUND;
    dbms_output.put_line(v_forms_type || '-' || v_forms_count);
  END LOOP;

  CLOSE forms_cursor;    
EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_tab := NULL;  
END;
/
