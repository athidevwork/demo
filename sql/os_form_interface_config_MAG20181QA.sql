prompt Importing table os_form_interface_config...
set feedback off
set define off
insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441249991, 'ELOQUENCE', 'GENERAL', 'CE_OUTPUT', 'ENDPOINT_URL', 'The end point URI to OutputWSPort for CEOutput.', 'http://eloquence_server_name:eloquence_port_number_for_ceoutput:8080/CEOutput/OutputWSPort', to_date('17-05-2018 12:14:32', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('22-05-2018 09:39:32', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441249992, 'ELOQUENCE', 'GENERAL', 'CE_OUTPUT', 'PASSWORD', 'The password to connect to OutputWSPort.', null, to_date('17-05-2018 12:14:32', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('17-05-2018 12:14:32', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441249993, 'ELOQUENCE', 'GENERAL', 'CE_OUTPUT', 'USERNAME', 'The username to connect to OutputWSPort.', null, to_date('17-05-2018 12:14:32', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('17-05-2018 12:14:32', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441249994, 'ELOQUENCE', 'GENERAL', 'CS_REQUEST', 'FORWARD_URL', 'The URL to forward from OASIS for rendering Eloquence Web within eOASIS.', 'http://eloquence_server_name:eloquence_port_number_for_ceoutput:8280/delphidev_CEWeb/api.do', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('22-05-2018 09:39:32', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441249995, 'ELOQUENCE', 'GENERAL', 'CS_REQUEST', 'INIT_ENTITY_NAME', 'The initial entity name that needs to be set in the CSOutput request XML for Eloquence Web Invocation.', null, to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441249996, 'ELOQUENCE', 'GENERAL', 'CS_REQUEST', 'INIT_LABEL', 'The initial Eloquence web folder name for listing interactive forms in Eloquence Web. This value will be set in the CSOutput request XML for Eloquence Web Invocation.', 'test', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441249997, 'ELOQUENCE', 'GENERAL', 'CS_REQUEST', 'INPUT_RESOURCE_LOCATOR', 'The input resource locator name defined in Eloquence that maps to the XML folder where OASIS XML files are stored. This value will be set in the CSOutput request XML for Eloquence Web Invocation.', 'InputFiles_RL', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441249998, 'ELOQUENCE', 'GENERAL', 'CS_REQUEST', 'LOCAL_DEFAULT_DEVICE_NAME', 'The default device where the final delivery from eloquence web needs to be made that invokes OWS calls. This value will be set in the CSOutput request XML for Eloquence Web Invocation.', 'SaveToOASIS', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441249999, 'ELOQUENCE', 'GENERAL', 'CS_REQUEST', 'MAP_FILENAME', 'The variable set name that needs to be set in the CSOutput request XML for Eloquence Web Invocation.', 'MAG_XML_VS.xml', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('22-05-2018 09:39:32', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250000, 'ELOQUENCE', 'GENERAL', 'GENERAL', 'CONFIG_PATH', 'The location on web logic server, where eloquence config xml files are located. eg. dti\eloquence', 'dti\\Eloquence', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('22-05-2018 10:19:01', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250001, 'ELOQUENCE', 'GENERAL', 'OUTPUT_PREVIEW_REQUEST', 'ARCHIVE_PATH', 'The local archive path with respect to the eloquence server that needs to be set in the CEOutput request XML for CEOutput Preview Invocation.', 'E:/data/webdav_delphidev/archive', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250002, 'ELOQUENCE', 'GENERAL', 'OUTPUT_PREVIEW_REQUEST', 'COLLECTION_NAME', 'The collection name value that needs to be set in the CEOutput request XML for CEOutput Preview Invocation.', 'MAG_Collection', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('22-05-2018 09:39:32', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250003, 'ELOQUENCE', 'GENERAL', 'OUTPUT_PREVIEW_REQUEST', 'ENTITY_NAME', 'The entity name that needs to be set in the CEOutput request XML for CEOutput Preview Invocation.', 'MAG_Collection', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('22-05-2018 09:39:32', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250004, 'ELOQUENCE', 'GENERAL', 'OUTPUT_PREVIEW_REQUEST', 'LOCAL_DEVICE_NAME', 'The local device name where PDF files are stored locally on Eloquence server that will be set in the CEOutput request XML for CEOutput Preview Invocation.', 'pdftest', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250005, 'ELOQUENCE', 'GENERAL', 'OUTPUT_PREVIEW_REQUEST', 'LOG_LEVEL', 'The log level value that needs to be set in the CEOutput request XML for CEOutput Preview Invocation.', 'debug', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250006, 'ELOQUENCE', 'GENERAL', 'OUTPUT_PREVIEW_REQUEST', 'NETWORK_DEVICE_NAME', 'The network device name where PDF files are stored, which will be set in the CEOutput request XML for CEOutput Preview Invocation.', 'MAGOasisFolder', to_date('17-05-2018 12:14:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('22-05-2018 09:39:32', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250007, 'ELOQUENCE', 'GENERAL', 'OUTPUT_PREVIEW_REQUEST', 'VARIABLESET_NAME', 'The variable set value that needs to be set in the CEOutput request XML for CEOutput Preview Invocation.', 'MAG_XML_VS', to_date('17-05-2018 12:14:34', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('22-05-2018 09:39:32', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250008, 'ELOQUENCE', 'GENERAL', 'OUTPUT_REQUEST', 'ARCHIVE_PATH', 'The local archive path with respect to the eloquence server that needs to be set in the CEOutput request XML for CEOutput Invocation.', 'E:/data/webdav_delphidev/archive', to_date('17-05-2018 12:14:34', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('17-05-2018 12:14:34', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250009, 'ELOQUENCE', 'GENERAL', 'OUTPUT_REQUEST', 'COLLECTION_NAME', 'The collection name value that needs to be set in the CEOutput request XML for CEOutput Invocation.', 'MAG_Collection', to_date('17-05-2018 12:14:34', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('22-05-2018 09:39:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250010, 'ELOQUENCE', 'GENERAL', 'OUTPUT_REQUEST', 'ENTITY_NAME', 'The entity name that needs to be set in the CEOutput request XML for CEOutput Invocation.', 'MAG_Collection', to_date('17-05-2018 12:14:34', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('22-05-2018 09:39:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250011, 'ELOQUENCE', 'GENERAL', 'OUTPUT_REQUEST', 'LOCAL_DEVICE_NAME', 'The local device name where PDF files are stored locally on Eloquence server that will be set in the CEOutput request XML for CEOutput Invocation.', 'pdftest', to_date('17-05-2018 12:14:34', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('17-05-2018 12:14:34', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250012, 'ELOQUENCE', 'GENERAL', 'OUTPUT_REQUEST', 'LOG_LEVEL', 'The log level value that needs to be set in the CEOutput request XML for CEOutput Invocation.', 'debug', to_date('17-05-2018 12:14:34', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('17-05-2018 12:14:34', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250013, 'ELOQUENCE', 'GENERAL', 'OUTPUT_REQUEST', 'NETWORK_DEVICE_NAME', 'The network device name where PDF files are stored, which will be set in the CEOutput request XML for CEOutput Invocation.', 'MAGOasisFolder', to_date('17-05-2018 12:14:34', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('22-05-2018 09:39:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441250014, 'ELOQUENCE', 'GENERAL', 'OUTPUT_REQUEST', 'VARIABLESET_NAME', 'The variable set value that needs to be set in the CEOutput request XML for CEOutput Invocation.', 'MAG_XML_VS', to_date('17-05-2018 12:14:34', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('22-05-2018 09:39:33', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441259583, 'ELOQUENCE', 'GENERAL', 'PREVIEW', 'CLEANUP_AFTER_PREVIEW', 'Boolean value that instructs whether to cleanup Extract and output tables after preview PDF gets generated. Default value is true, if property not defined.', 'true', to_date('22-05-2018 11:02:36', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('22-05-2018 11:02:36', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE, SYS_CREATE_TIME, SYS_CREATED_BY, SYS_UPDATE_TIME, SYS_UPDATED_BY)
values (441259584, 'ELOQUENCE', 'GENERAL', 'PREVIEW', 'IS_EXCLUDE_NULL_VALUE_FIELDS_IN_XML', 'Boolean value that instructs whether to exclude all null value elements. Default value is true, if property not defined.', 'false', to_date('22-05-2018 11:02:36', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX', to_date('22-05-2018 11:02:36', 'dd-mm-yyyy hh24:mi:ss'), 'MAG20181QAX');

prompt Done.
