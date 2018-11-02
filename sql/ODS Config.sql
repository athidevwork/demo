-- ODS Configuration
SELECT * 
FROM System_Parameter_Util spu 
WHERE spu.sysparm_code IN ('ODS_VERSION', 'OS_BUNDLE_FORMS', 'OS_BUNDLE_TRANS', 'OS_DOC_DIRECTORY', 'OS_XML_DIRECTORY', 
                           'OS_APP_DATE_TO_DIR', 'OS_DIRECTORY_SIZE', 'OS_ASSEMBLER', 'OS_DDN_PARAM_NAME', 'OS_DDN_PARAM_VALUE', 
                           'ELOQ_DATE_SPLIT_LIST', 'OS_CPY_OFFCL_FRMSET') 
ORDER BY 1
--FOR update;

--OS_XML_DIRECTORY missing
-- what is the protocol for model office - is it a config event? Malcolm or Jiffy?

select * from os_form_interface_config o
ORDER BY o.doc_gen_prd_name, o.category, o.sub_category, o.code
--FOR UPDATE
;

select o.doc_gen_prd_name, o.category, o.sub_category, o.code, o.value, o.description from os_form_interface_config o
ORDER BY o.doc_gen_prd_name, o.category, o.sub_category, o.code
FOR UPDATE
;

select o.doc_gen_prd_name, o.category, o.sub_category, o.code, o.value, o.description from os_form_interface_config o
WHERE o.value IS NULL
ORDER BY o.doc_gen_prd_name, o.category, o.sub_category, o.code
;
--2018 MO updates
insert into System_Parameter_Util (SYSPARM_CODE, SYSPARM_DESCRIPTION, SYSPARM_VALUE)
values ('OS_XML_DIRECTORY', 'The network location to save XML file used by 3rd party ODS form generation product', '\\mo-webapp20181.ad.dti\ODSOutput\MO20181\XMLData');

INSERT INTO OS_FORM_INTERFACE_CONFIG (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE)
SELECT oasis_sequence.nextval,'GHOSTDRAFT','GENERAL','DOCUMENT_SERVICE','TEMPLATE_CATALOGS_RESOURCE_NAME','A value that indicates the resource name for invoking Get Template Catalogs call using REST Service.','GetTemplateCatalogs'
  FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM OS_FORM_INTERFACE_CONFIG
                    WHERE DOC_GEN_PRD_NAME = 'GHOSTDRAFT' 
                      AND CATEGORY = 'GENERAL' 
                      AND SUB_CATEGORY = 'DOCUMENT_SERVICE' 
                      AND CODE = 'TEMPLATE_CATALOGS_RESOURCE_NAME');

