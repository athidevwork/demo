--only crystal forms
--overview
select decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL') AS formstype, count(f.form_id) AS formcount 
FROM os_form f
group by decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL')
ORDER BY formstype
;
--detail
select decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL') AS formstype, f.form_id, f.form_desc, count(f.form_id) AS formcount 
FROM os_form f
group by decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL'), f.form_id, f.form_desc
ORDER BY formstype
;
--crystal, ghostdraft and eloquence forms
--overview
SELECT decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL') AS formstype,
       count(f.form_id) AS formcount 
FROM os_form f, os_form_interface ofi
WHERE f.form_id = ofi.form_id
group by decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL')
UNION
SELECT decode(nvl(ofi.doc_gen_prd_name, ''), 'ELOQUENCE', 'ELOQUENCE', 'GHOSTDRAFT') AS tformstype,
       count(f.form_id) AS formcount 
FROM os_form f, os_form_interface ofi
WHERE f.form_id = ofi.form_id
group BY decode(nvl(ofi.doc_gen_prd_name, ''), 'ELOQUENCE', 'ELOQUENCE', 'GHOSTDRAFT')
ORDER BY formstype
;
--detail
SELECT decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL') AS formstype, f.form_id, f.form_desc,
       count(f.form_id) AS formcount 
FROM os_form f, os_form_interface ofi
WHERE f.form_id = ofi.form_id
group by decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL'), f.form_id, f.form_desc
UNION
SELECT decode(nvl(ofi.doc_gen_prd_name, ''), 'ELOQUENCE', 'ELOQUENCE', 'GHOSTDRAFT') AS tformstype, f.form_id, f.form_desc,
       count(f.form_id) AS formcount 
FROM os_form f, os_form_interface ofi
WHERE f.form_id = ofi.form_id
group BY decode(nvl(ofi.doc_gen_prd_name, ''), 'ELOQUENCE', 'ELOQUENCE', 'GHOSTDRAFT'), f.form_id, f.form_desc
ORDER BY formstype
;
--one script overview
SELECT DECODE(ofi.doc_gen_prd_name, 
              'ELOQUENCE',  'ELOQUENCE', 
              'GHOSTDRAFT', 'GHOSTDRAFT',
               decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL') 
              ) formstype, 
       count(DISTINCT f.form_id) AS formcount 
FROM os_form f, os_form_interface ofi
WHERE f.form_id = ofi.form_id
group by DECODE(ofi.doc_gen_prd_name, 
              'ELOQUENCE',  'ELOQUENCE', 
              'GHOSTDRAFT', 'GHOSTDRAFT',
               decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL') 
              )
ORDER BY 1
;
--one script detail
SELECT DECODE(ofi.doc_gen_prd_name, 
              'ELOQUENCE',  'ELOQUENCE', 
              'GHOSTDRAFT', 'GHOSTDRAFT',
               decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL') 
              ) formstype,
       f.form_id, f.form_desc,
       count(DISTINCT f.form_id) AS formcount 
FROM os_form f, os_form_interface ofi
WHERE f.form_id = ofi.form_id
group by DECODE(ofi.doc_gen_prd_name, 
              'ELOQUENCE',  'ELOQUENCE', 
              'GHOSTDRAFT', 'GHOSTDRAFT',
               decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL') 
              ), f.form_id, f.form_desc
ORDER BY 1
;
--one script overview to work in all  env.
BEGIN 
  v_tab varchar2;
  --check if the user is schema owner
  select get_oasis_user from dual;
  select PM_TABLE_OWNER from dual;
  
  SELECT t.tname
  INTO v_tab
  from tab t
  where upper(t.tname) = 'OS_FORM_INTERFACE' and t.tabtype = 'TABLE';
  
  IF (v_tab != '') THEN
    SELECT DECODE(ofi.doc_gen_prd_name, 
                  'ELOQUENCE',  'ELOQUENCE', 
                  'GHOSTDRAFT', 'GHOSTDRAFT',
                   decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL') 
                  ) formstype, 
           count(DISTINCT f.form_id) AS formcount 
    FROM os_form f, os_form_interface ofi
    WHERE f.form_id = ofi.form_id
    group by DECODE(ofi.doc_gen_prd_name, 
                  'ELOQUENCE',  'ELOQUENCE', 
                  'GHOSTDRAFT', 'GHOSTDRAFT',
                   decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL') 
                  )
    ORDER BY 1
    ;
  ELSE
    select decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL') AS formstype, count(f.form_id) AS formcount 
    FROM os_form f
    group by decode(nvl(f.ufe_b, 'N'), 'Y', 'UFE', 'CRYSTAL')
    ORDER BY formstype
    ;
  END IF;
END
/
