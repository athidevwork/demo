-- Script to create a new user from an old one.
VARIABLE PFUSERX VARCHAR2(200)
--EXEC '&PFUSERX':=upper(pm_get_schema_owner);
ACCEPT PFUSERX CHAR PROMPT  'USERID? '
--VARIABLE WLSERVER VARCHAR2(200)
--EXEC :WLSERVER:=lower('nj-wlsqa1.ad.dti')
-- nj-wlsqa1.ad.dti
-- nj-wlsdev1.ad.dti

--ACCEPT PFUSERX CHAR PROMPT  'USERID? '
--prompt "If this is a production system, just tap enter on the next question."
--prompt "We only need to enter the WebLogic Server name for internal Delphi systems."
--ACCEPT WLSERVER CHAR PROMPT 'WebLogic Server (i.e. nj-wlsqa1.ad.dti) ? '

-- Turn config_prop off.
DECLARE
  V_COUNT NUMBER :=0;
BEGIN
  SELECT COUNT(*) 
    INTO V_COUNT
  FROM pfuser 
  WHERE UPPER(USERID) = UPPER('&PFUSERX');
  IF V_COUNT > 0 THEN
   RAISE_APPLICATION_ERROR(-20400, 'Entity already exists');
   GOTO SKIPALL;
  END IF;
  
     -- Create an entity record.
  INSERT INTO entity 
         (entity_pk, 
        client_id, 
        gender, 
        entity_type, 
        first_name, 
        first_name_upper, 
        last_name, 
        last_name_upper, 
        last_name_soundex
         ,very_important_person_b
         ,ssn_verified_b
         ,federal_tax_id_verified_b 
         ,tax_info_effective_date 
         ,deceased_b 
         ,web_user_b)
    VALUES    (oasis_sequence.NEXTVAL
         ,clientid_sequence.NEXTVAL
         ,'U'
         ,'P'
         ,upper('&PFUSERX')
         ,upper('&PFUSERX')
         ,upper('&PFUSERX')
         ,upper('&PFUSERX')
         ,substr(upper('&PFUSERX'),1,5)
         ,'N'
         ,'N'
         ,'N'
         ,SYSDATE
         ,'N'
         ,'N');

  -- Create an address record.
   INSERT INTO address
    (address_pk
    ,address_type_code
    ,source_record_fk
    ,source_table_name
    ,address_line1
    ,city
    ,county_code
    ,state_code
    ,zipcode
    ,country_code
    ,primary_address_b
    ,usa_address_b
    ,post_office_address_b
    ,effective_from_date
    ,effective_to_date)
    SELECT oasis_sequence.NEXTVAL
      ,'OFFICE'
      ,entity_pk
      ,'ENTITY'
      ,'303 George St'
      ,'New Brunswick'
      ,'3712'
      ,'NJ'
      ,'08901'
      ,'USA'
      ,'Y'
      ,'Y'
      ,'N'
      ,SYSDATE - 1
      ,to_date('01/01/3000', 'mm/dd/yyyy') FROM entity e WHERE e.last_name_upper = upper('&PFUSERX');

  -- Create a phone # record.
  INSERT INTO phone_number
    (phone_number_pk
    ,source_record_fk
    ,source_table_name
    ,phone_number_type_code
    ,area_code
    ,phone_number
    ,primary_number_b
    ,usa_number_b
    ,listed_number_b
    ,permission_to_release_b)
    SELECT oasis_sequence.NEXTVAL
      ,entity_pk
      ,'ADDRESS'
      ,'OFFICE'
      ,'732'
      ,'4180008'
      ,'Y'
      ,'Y'
      ,'Y'
      ,'N'
    FROM entity e
     WHERE e.last_name_upper = upper('&PFUSERX')
     AND e.sys_create_time > SYSDATE -1/24/60;
     
  -- Create the File Manager Classification
  INSERT INTO entity_class 
         (entity_class_pk
         ,entity_fk
         ,entity_class_code)
  SELECT oasis_sequence.NEXTVAL
      ,e.entity_pk
      ,'EXAMINER'
    FROM entity  e
     WHERE e.last_name_upper = upper('&PFUSERX')
     AND e.sys_create_time > SYSDATE -1/24/60;

  -- Create the Claims Classification
  INSERT INTO claims_staff 
    (claims_staff_pk 
    ,entity_fk 
    ,userid 
    ,effective_from_date 
    )
    SELECT oasis_sequence.NEXTVAL
      ,e.entity_pk
      ,upper('&PFUSERX')
      ,SYSDATE
     FROM entity e
     WHERE e.last_name_upper = upper('&PFUSERX')
     AND e.sys_create_time > SYSDATE -1/24/60;

  -- Create a PFUSER record
  INSERT INTO PFUSER
    (USERID
    ,PASSWORD
    ,FIRST_NAME
    ,MIDDLE_NAME
    ,LAST_NAME
    ,STATUS
    ,NO_UNSUCC_LOGONS
    ,DEPARTMENT
    ,COST_CENTER
    ,PHONE_NUMBER
    ,LOCATION
    ,AUDIT_CREATE_DATE
    ,AUDIT_CREATE_USER
    ,AUDIT_UPDATE_DATE
    ,AUDIT_UPDATE_USER
    ,ENTITY_FK
    ,INTERNAL_USER_B
    ,CLIENT_SERVER_USER_B
    ,WEB_USER_B
    ,NO_UNSUCC_LOGONS_WEB
    ,PASSWORD_REMINDER
    ,USER_CREATION_ROLE_TYPE_CODE
    ,USER_CREATION_EXTERNAL_ID
    ,DEFAULT_INSURED_ENTITY_FK
    ,PASSWORD_UPDATE_DATE
    ,LAST_LOGGED_IN_DATE)
    SELECT UPPER('&PFUSERX')
      ,NULL
      ,UPPER('&PFUSERX')
      ,NULL
      ,UPPER('&PFUSERX')
      ,'A'
      ,0
      ,NULL
      ,NULL
      ,NULL
      ,NULL
      ,SYSDATE
      ,UPPER('&PFUSERX')
      ,SYSDATE
      ,UPPER('&PFUSERX')
      ,e.entity_pk
      ,NULL
      ,NULL
      ,'Y'
      ,NULL
      ,NULL
      ,NULL
      ,NULL
      ,NULL
      ,NULL
      ,NULL
    FROM entity e
     WHERE 1=1 
     AND e.last_name_upper = upper('&PFUSERX')
     AND e.sys_create_time > SYSDATE -1/24/60
     AND NOT EXISTS
     (SELECT 'x' FROM pfuser WHERE userid = UPPER('&PFUSERX'));

  -- Copy the Profile information from the OASIS user.
  INSERT INTO pfuser_prof
  SELECT upper('&PFUSERX')
      ,application
      ,PROFILE
      ,effective_date
      ,status
      ,audit_create_date
      ,audit_create_user
      ,audit_update_date
      ,audit_update_user
    FROM pfuser_prof p
  -- WHERE p.Userid = 'OASIS'
   WHERE p.Userid = '&copyfromprofile'
     AND (UPPER('&PFUSERX'), p.application, p.profile) NOT IN
     (SELECT p3.userid, p3.application, p3.profile
       FROM pfuser_prof p3);
  <<SKIPALL>>
  NULL;
END;  
/
