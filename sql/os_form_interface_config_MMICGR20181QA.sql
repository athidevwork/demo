prompt Importing table os_form_interface_config...
set feedback off
set define off
insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'CE_OUTPUT', 'ENDPOINT_URL', 'The end point URI to OutputWSPort for CEOutput.', 'http://ny2-eloqpoc01.ad.dti:8080/CEOutput/OutputWSPort', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'CE_OUTPUT', 'PASSWORD', 'The password to connect to OutputWSPort.', null, null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'CE_OUTPUT', 'USERNAME', 'The username to connect to OutputWSPort.', null, null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'CS_REQUEST', 'FORWARD_URL', 'The URL to forward from OASIS for rendering Eloquence Web within eOASIS.', 'http://ny2-eloqpoc01.ad.dti:8280/delphidev_CEWeb/api.do', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'CS_REQUEST', 'INIT_ENTITY_NAME', 'The initial entity name that needs to be set in the CSOutput request XML for Eloquence Web Invocation.', null, null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'CS_REQUEST', 'INIT_LABEL', 'The initial Eloquence web folder name for listing interactive forms in Eloquence Web. This value will be set in the CSOutput request XML for Eloquence Web Invocation.', 'test', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'CS_REQUEST', 'INPUT_RESOURCE_LOCATOR', 'The input resource locator name defined in Eloquence that maps to the XML folder where OASIS XML files are stored. This value will be set in the CSOutput request XML for Eloquence Web Invocation.', 'InputFiles_RL', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'CS_REQUEST', 'LOCAL_DEFAULT_DEVICE_NAME', 'The default device where the final delivery from eloquence web needs to be made that invokes OWS calls. This value will be set in the CSOutput request XML for Eloquence Web Invocation.', 'SaveToOASIS', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'CS_REQUEST', 'MAP_FILENAME', 'The variable set name that needs to be set in the CSOutput request XML for Eloquence Web Invocation.', 'MMIC_XML_VS.xml', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'GENERAL', 'CONFIG_PATH', 'The location on web logic server, where eloquence config xml files are located. eg. dti\eloquence', 'dti\Eloquence', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'OUTPUT_PREVIEW_REQUEST', 'ARCHIVE_PATH', 'The local archive path with respect to the eloquence server that needs to be set in the CEOutput request XML for CEOutput Preview Invocation.', 'E:/data/webdav_delphidev/archive', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'OUTPUT_PREVIEW_REQUEST', 'COLLECTION_NAME', 'The collection name value that needs to be set in the CEOutput request XML for CEOutput Preview Invocation.', 'MMIC_Collection', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'OUTPUT_PREVIEW_REQUEST', 'ENTITY_NAME', 'The entity name that needs to be set in the CEOutput request XML for CEOutput Preview Invocation.', 'MMIC_Collection', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'OUTPUT_PREVIEW_REQUEST', 'LOCAL_DEVICE_NAME', 'The local device name where PDF files are stored locally on Eloquence server that will be set in the CEOutput request XML for CEOutput Preview Invocation.', 'pdftest', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'OUTPUT_PREVIEW_REQUEST', 'LOG_LEVEL', 'The log level value that needs to be set in the CEOutput request XML for CEOutput Preview Invocation.', 'debug', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'OUTPUT_PREVIEW_REQUEST', 'NETWORK_DEVICE_NAME', 'The network device name where PDF files are stored, which will be set in the CEOutput request XML for CEOutput Preview Invocation.', 'MMICOasisFolder', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'OUTPUT_PREVIEW_REQUEST', 'VARIABLESET_NAME', 'The variable set value that needs to be set in the CEOutput request XML for CEOutput Preview Invocation.', 'MMIC_XML_VS', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'OUTPUT_REQUEST', 'ARCHIVE_PATH', 'The local archive path with respect to the eloquence server that needs to be set in the CEOutput request XML for CEOutput Invocation.', 'E:/data/webdav_delphidev/archive', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'OUTPUT_REQUEST', 'COLLECTION_NAME', 'The collection name value that needs to be set in the CEOutput request XML for CEOutput Invocation.', 'MMIC_Collection', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'OUTPUT_REQUEST', 'ENTITY_NAME', 'The entity name that needs to be set in the CEOutput request XML for CEOutput Invocation.', 'MMIC_Collection', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'OUTPUT_REQUEST', 'LOCAL_DEVICE_NAME', 'The local device name where PDF files are stored locally on Eloquence server that will be set in the CEOutput request XML for CEOutput Invocation.', 'pdftest', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'OUTPUT_REQUEST', 'LOG_LEVEL', 'The log level value that needs to be set in the CEOutput request XML for CEOutput Invocation.', 'debug', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'OUTPUT_REQUEST', 'NETWORK_DEVICE_NAME', 'The network device name where PDF files are stored, which will be set in the CEOutput request XML for CEOutput Invocation.', 'MMICOasisFolder', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'OUTPUT_REQUEST', 'VARIABLESET_NAME', 'The variable set value that needs to be set in the CEOutput request XML for CEOutput Invocation.', 'MMIC_XML_VS', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'PREVIEW', 'CLEANUP_AFTER_PREVIEW', 'Boolean value that instructs whether to cleanup Extract and output tables after preview PDF gets generated. Default value is true, if property not defined.', 'true', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'PMS', 'OUTPUT_REQUEST', 'COLLECTION_NAME', 'The collection name value that needs to be set in the CEOutput request XML for CEOutput Invocation.', 'MMIC_PM_Collection', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'PMS', 'OUTPUT_REQUEST', 'ENTITY_NAME', 'The entity name that needs to be set in the CEOutput request XML for CEOutput Invocation.', 'MMIC_PM_Collection', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'CIS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_NAME', 'A value that indicates Document Package Name.', 'OASIS CIS Form Letter Model Library', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'CIS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_URI', 'A URL value that indicates the  document service used to launch interactive form.', 'https://secure.ELOQUENCE.com/instances/delphidev/ODEV20181/OASIS CIS Form Letter Model Library/1.0/', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'CIS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_VERSION', 'A value that indicates Document Package Version.', '1.0', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'CIS_FORM_LETTER', 'MAPPING_SERVICE', 'MAPPING_NAME', 'A value that indicates Mapping Name.', 'OASIS CIS Form Letter Mapping v1.0', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'CMS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_NAME', 'A value that indicates Document Package Name.', 'OASIS Claim Form Letter Model Library', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'CMS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_URI', 'A URL value that indicates the  document service used to launch interactive form.', 'https://secure.ELOQUENCE.com/instances/delphidev/ODEV20181/OASIS Claim Form Letter Model Library/1.0/', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'CMS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_VERSION', 'A value that indicates Document Package Version.', '1.0', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'CMS_FORM_LETTER', 'MAPPING_SERVICE', 'MAPPING_NAME', 'A value that indicates Mapping Name.', 'OASIS Claim Form Letter Mapping v1.0', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'FMS', 'DATA_ENTRY', 'DEFAULT_DESIGN', 'Indicates the default U/I design for data entry page.', 'Memo Data Capture', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'FMS', 'DATA_ENTRY', 'OUTPUT_FORMAT', 'Indicates the default output format for data entry page.', 'PDF', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'FMS', 'DOCUMENT_SERVICE', 'PACKAGE_NAME', 'A value that indicates Document Package Name.', 'OASIS Finance Model Library', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'FMS', 'DOCUMENT_SERVICE', 'PACKAGE_URI', 'A URL value that indicates the  document service used to launch interactive form.', 'https://secure.ELOQUENCE.com/instances/delphidev/ODEV20171/OASIS Finance Model Library/1.0/', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'FMS', 'DOCUMENT_SERVICE', 'PACKAGE_VERSION', 'A value that indicates Document Package Version.', '1.0', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'FMS', 'MAPPING_SERVICE', 'MAPPING_NAME', 'A value that indicates Mapping Name.', 'OASIS Finance Mapping v1.0', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'FMS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_NAME', 'A value that indicates Document Package Name.', 'OASIS Finance Form Letter Model Library', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'FMS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_URI', 'A URL value that indicates the  document service used to launch interactive form.', 'https://secure.ELOQUENCE.com/instances/delphidev/ODEV20171/OASIS Finance Form Letter Model Library/1.0/', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'FMS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_VERSION', 'A value that indicates Document Package Version.', '1.0', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'FMS_FORM_LETTER', 'MAPPING_SERVICE', 'MAPPING_NAME', 'A value that indicates Mapping Name.', 'OASIS Finance Form Letter Mapping.gdx v1.0', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'DATA_ENTRY', 'DEFAULT_DESIGN', 'Indicates the default U/I design for data entry page.', 'Memo Data Capture', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'DATA_ENTRY', 'LOGO', 'Indicates the logo to be rendered in the data entry page.', 'project/Delphi Logo', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'DATA_ENTRY', 'OUTPUT_FORMAT', 'Indicates the default output format for data entry page.', 'PDF', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'DATA_ENTRY', 'URI', 'Indicates the entry point URI for prologue data entry page.', '/ui/captureData', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'DOCUMENT_SERVICE', 'REST_URI', 'A value that indicates the REST URI.', 'https://secure.ELOQUENCE.com/instances/delphidev/ELOQUENCEServer/RestApi/', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'DOCUMENT_SERVICE', 'SERVICE_NAME', 'A value that indicates ELOQUENCE Document Service Name.', 'ODEV20181', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'DOCUMENT_SERVICE', 'TEMPLATE_CATALOGS_RESOURCE_NAME', 'A value that indicates the resource name for invoking Get Template Catalogs call using REST Service.', 'GetTemplateCatalogs', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'GENERAL', 'COMPRESSION_TYPE', 'A value that indicates the compress type to be used for XML compression. Default is Plain.', 'gzip', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'GENERAL', 'INSTANCE_URI', 'A value that indicates ELOQUENCE instance URI.', 'https://secure.ELOQUENCE.com/instances/delphidev/', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'GENERAL', 'NAMESPACE_URI', 'NamespaceUri associated with ELOQUENCE DelphiTech Web Service.', 'http://ELOQUENCE.korbitec.com/delphitechservice', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'GENERAL', 'NAMESPACE_URI_LOCAL_PART', 'NamespaceUri LocalPart associated with ELOQUENCE DelphiTech Web Service.', 'DelphiTechService', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'GENERAL', 'OS_TEMP_DIRECTORY', 'A value that indicates a temporary location for processing individual pdf files from compressed GD output.', null, null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'GENERAL', 'PASSWORD', 'A valid password to access ELOQUENCE configuration manager portal.', 'P@ssw0rd', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'GENERAL', 'USER_NAME', 'A valid username to access ELOQUENCE configuration manager portal.', 'oasis', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'GENERAL', 'WATERMARK_TEMPLATE_NAME', 'A value that indicates the GD document name for watermark content.', null, null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'GENERAL', 'WEB_SERVICE_NAME', 'A value that indicates the ELOQUENCE DelphiTech Web Service Name.', 'delphitech', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'GENERAL', 'WEB_SERVICE_WSDL_URI', 'WSDL link to ELOQUENCE DelphiTech Web Service.', 'https://secure.ELOQUENCE.com/instances/delphidev/DelphiTech?wsdl', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'MAPPING_SERVICE', 'MAPPING_INFO_RESOURCE_NAME', 'A value that indicates the resource name for invoking MappingInfo call for REST Service.', 'GetMappingInfo', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'MAPPING_SERVICE', 'REST_URI', 'A value that indicates the REST URI.', 'https://secure.ELOQUENCE.com/instances/delphidev/ELOQUENCEDataMappingServer/RestApi/', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'MAPPING_SERVICE', 'SERVER_XML_RESOURCE_NAME', 'A value that indicates the resource name for invoking ServerXml call for REST Service.', 'GetServerXml', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'MAPPING_SERVICE', 'SERVICE_NAME', 'A value that indicates ELOQUENCE Mapping Service Name.', 'ODEV20181', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'PREVIEW', 'CLEANUP_AFTER_PREVIEW', 'Boolean value that instructs whether to cleanup Extract and output tables after preview PDF gets generated. Default value is true, if property not defined.', 'true', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'PREVIEW', 'PDF_TEMPLATE_LOCATION', 'Specify the PREVIEW watermark template designed in ELOQUENCE. This is mutually exclusive with TEMPLATE_NAME Configuration.', null, null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'GENERAL', 'PREVIEW', 'TEMPLATE_NAME', 'Specify the location of the PREVIEW watermark pdf (eg. dti/templates/GDPreview.pdf).This is mutually exclusive with PDF_TEMPLATE_LOCATION Configuration.', null, null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'MANUSCRIPT', 'MAPPING_SERVICE', 'MAPPING_NAME', 'A value that indicates Mapping Name for Manuscript Forms.', 'OASIS Manuscript Mapping v1.0', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'PMS', 'DATA_ENTRY', 'DEFAULT_DESIGN', 'Indicates the default U/I design for data entry page.', 'Memo Data Capture', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'PMS', 'DATA_ENTRY', 'OUTPUT_FORMAT', 'Indicates the default output format for data entry page.', 'PDF', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'PMS', 'DOCUMENT_SERVICE', 'PACKAGE_NAME', 'A value that indicates Document Package Name.', 'OASIS Policy Model Library', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'PMS', 'DOCUMENT_SERVICE', 'PACKAGE_URI', 'A URL value that indicates the  document service used to launch interactive form.', 'https://secure.ELOQUENCE.com/instances/delphidev/ODEV20181/OASIS Policy Model Library/1.0/', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'PMS', 'DOCUMENT_SERVICE', 'PACKAGE_VERSION', 'A value that indicates Document Package Version.', '1.0', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'PMS', 'MAPPING_SERVICE', 'MAPPING_NAME', 'A value that indicates Mapping Name.', 'OASIS Policy Mapping v1.0', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'PMS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_NAME', 'A value that indicates Document Package Name.', 'OASIS Policy Form Letter Model Library', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'PMS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_URI', 'A URL value that indicates the  document service used to launch interactive form.', 'https://secure.ELOQUENCE.com/instances/delphidev/ODEV20181/OASIS Policy Form Letter Model Library/1.0/', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'PMS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_VERSION', 'A value that indicates Document Package Version.', '1.0', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'PMS_FORM_LETTER', 'MAPPING_SERVICE', 'MAPPING_NAME', 'A value that indicates Mapping Name.', 'OASIS Policy Form Letter Mapping v1.0', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'RMS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_NAME', 'A value that indicates Document Package Name.', 'OASIS RM Form Letter Model Library', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'RMS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_URI', 'A URL value that indicates the  document service used to launch interactive form.', 'https://secure.ELOQUENCE.com/instances/delphidev/ODEV20181/OASIS RM Form Letter Model Library/1.0/', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'RMS_FORM_LETTER', 'DOCUMENT_SERVICE', 'PACKAGE_VERSION', 'A value that indicates Document Package Version.', '1.0', null, null, null, null);

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (oasis_sequence.nextval, 'ELOQUENCE', 'RMS_FORM_LETTER', 'MAPPING_SERVICE', 'MAPPING_NAME', 'A value that indicates Mapping Name.', 'OASIS RM Form Letter Mapping v1.0', null, null, null, null);

prompt Done.
