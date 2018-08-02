package dti.ic.installer;

/**
 * Class that defines the various constants and variable names for the eApp installation.
 * <p/>
 * <p>(C) 2012 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 30, 2012
 *
 * @author Toby Wang
 */
/*
*
* Revision Date    Revised By  Description
* ---------------------------------------------------
* Jun 05, 2013     Toby Wang   143059 - Remove Date format
* Apr 02, 2014     Toby Wang   153188 - Create index through SQL in alt script
* Mar 12, 2015     Toby Wang   Enhanced to update web urls
* Sep 19, 2015     Toby Wang   Add variable EAPP_INSTALLER_VERSION
* ---------------------------------------------------
*/
public final class InstallConstants {

    public final static String TRUE = "true";

    public final static String FALSE = "false";

    public final static String YES = "Yes";

    public final static String NO = "No";

    public final static String GRAPHIC = "SWING";

    public final static String CONSOLE = "CONSOLE";

    public final static String SILENT = "SILENT";

    public final static String FD_LOGS = "Logs";

    public final static String VAR_EAPP_INSTALLER_VERSION = "$EAPP_INSTALLER_VERSION$";

    public final static String VAR_USER_INSTALL_DIR = "$USER_INSTALL_DIR$";

    public final static String VAR_START_TIME = "$START_TIME$";

    public final static String VAR_USER_MAGIC_FOLDER_1 = "$USER_MAGIC_FOLDER_1$";

    public final static String VAR_INSTALLER_TITLE = "$INSTALLER_TITLE$";

    public final static String VAR_IA_ROLLBACK = "$IA_ROLLBACK$";

    public final static String VAR_DEVELOPER_DISK_SPACE_ADDITIONAL = "$DEVELOPER_DISK_SPACE_ADDITIONAL$";

    public final static String VAR_INSTALL_LOG_DESTINATION = "$INSTALL_LOG_DESTINATION$";

    public final static String VAR_INSTALLER_UI = "$INSTALLER_UI$";

    public final static String VAR_PATH_INVALID = "$PATH_INVALID$";

    public final static String VAR_HOST_NAME = "$HOST_NAME$";

    public final static String VAR_INSTALL_SUCCESS = "$INSTALL_SUCCESS$";

    public final static String VAR_ADDITION_DISK_SPACE = "$DEVELOPER_DISK_SPACE_ADDITIONAL$";

    public final static String VAR_INSTALLER_LAUNCH_DIR = "$INSTALLER_LAUNCH_DIR$";

    public final static String VAR_INSTALL_LOG_NAME = "$INSTALL_LOG_NAME$";

    public final static String VAR_COMPANY_NAME = "$COMPANY_NAME$";

    public final static String VAR_PRODUCT_NAME = "$PRODUCT_NAME$";

    public final static String VAR_USER_SHORTCUTS = "$USER_SHORTCUTS$";

    public final static String VAR_JDBC_DATA_SOURCE_NAME = "$JDBC_DATA_SOURCE_NAME$";

    public final static String VAR_DB_SERVER_NAME = "$DB_SERVER_NAME$";

    public final static String VAR_DB_SERVER_IP = "$DB_SERVER_IP$";

    public final static String VAR_DB_SID_NAME = "$DB_SID_NAME$";

    public final static String VAR_DB_SERVER_PORT = "$DB_SERVER_PORT$";

    public final static String VAR_DB_USER_NAME = "$DB_USER_NAME$";

    public final static String VAR_DB_USER_PASSWORD = "$DB_USER_PASSWORD$";

    public final static String VAR_IS_DB_VALID = "$IS_DB_VALID$";

    public final static String VAR_ERROR_DB_CONNECT = "$ERROR_DB_CONNECT$";

    public final static String VAR_ERROR_CHECK_TABLE_EXIST = "$ERROR_CHECK_TABLE_EXIST$";

    public final static String VAR_DB_SCHEMA_SERVER_NAME = "$DB_SCHEMA_SERVER_NAME$";

    public final static String VAR_DB_SCHEMA_SERVER_IP = "$DB_SCHEMA_SERVER_IP$";

    public final static String VAR_DB_SCHEMA_SID_NAME = "$DB_SCHEMA_SID_NAME$";

    public final static String VAR_DB_SCHEMA_SERVER_PORT = "$DB_SCHEMA_SERVER_PORT$";

    public final static String VAR_DB_SCHEMA_USER_NAME = "$DB_SCHEMA_USER_NAME$";

    public final static String VAR_DB_SCHEMA_USER_PASSWORD = "$DB_SCHEMA_USER_PASSWORD$";

    public final static String VAR_IS_DB_SCHEMA_VALID = "$IS_DB_SCHEMA_VALID$";

    public final static String VAR_ERROR_DB_SCHEMA_CONNECT = "$ERROR_DB_SCHEMA_CONNECT$";
    
    public final static String VAR_IS_DATABASE_SCHEMA_READY = "$IS_DATABASE_SCHEMA_READY$";

    public final static String VAR_ERROR_SCHEMA_CHECK_TABLE_EXIST = "$ERROR_SCHEMA_CHECK_TABLE_EXIST$";

    public final static String VAR_DATA_SOURCE_URL = "$DATA_SOURCE_URL$";

    public final static String VAR_DATA_SOURCE_USER = "$DATA_SOURCE_USER$";
    
    public final static String VAR_DATA_SCHEMA_USER = "$DATA_SCHEMA_USER$";

    public final static String newLine = System.getProperty("line.separator");

    public static final String VAR_GHOSTDRAFT_INSTANCE = "$GHOST_DRAFT_INSTANCE$";

    public static final String VAR_GHOSTDRAFT_DOC_SERVICE = "$GHOST_DRAFT_DOCUMENT_MAPPING$";

    public static final String VAR_GHOSTDRAFT_TEMPLATE_SERVICE = "$GHOST_DRAFT_TEMPLATE_MAPPING$";

    public static final String VAR_ELOQUENCE_OUTPUT_WS_HOST = "$ELOQUENCE_OUTPUT_WS_HOST$";

    public static final String VAR_ELOQUENCE_OUTPUT_WS_PORT = "$ELOQUENCE_OUTPUT_WS_PORT$";

    public static final String VAR_ELOQUENCE_WEB_HOST = "$ELOQUENCE_WEB_HOST$";

    public static final String VAR_ELOQUENCE_WEB_PORT = "$ELOQUENCE_WEB_PORT$";

    public static final String VAR_ELOQUENCE_INTERACTIVE_FORM = "$ELOQUENCE_INTERACTIVE_FORMS$";

    public static final String VAR_ELOQUENCE_INPUT_RESOURCE_LOC = "$ELOQUENCE_INPUT_RESOURCE_LOCATOR$";

    public static final String VAR_ELOQUENCE_DEFAULT_DEVICE_NAME = "$ELOQUENCE_DEFAULT_DEVICE_NAME$";

    public static final String VAR_ELOQUENCE_MAP_FILE_NAME = "$ELOQUENCE_MAP_FILE_NAME$";

    public static final String VAR_FORMS_ENGINE = "$FORMS_ENGINE$";

    public static final String VAR_XML_DIR = "$XML_DIR$";

    public static final String VAR_ELOQUENCE_CONFIG_PATH = "$CONFIG_PATH$";

    public static final String VAR_ELOQUENCE_PREVIEW_ARCHIVE_PATH = "$PREVIEW_ARCHIVE_PATH$";

    public static final String VAR_ELOQUENCE_PREVIEW_GENERAL_COLLECTION = "$PREVIEW_GENERAL_COLLECTION$";

    public static final String VAR_ELOQUENCE_PREVIEW_GENERAL_ENTITY = "$PREVIEW_GENERAL_ENTITY$";

    public static final String VAR_ELOQUENCE_PREVIEW_LOCAL_DEVICE = "$PREVIEW_LOCAL_DEVICE$";

    public static final String VAR_ELOQUENCE_PREVIEW_LOG_LEVEL = "$PREVIEW_LOG_LEVEL$";

    public static final String VAR_ELOQUENCE_PREVIEW_NETWORK_DEVICE = "$PREVIEW_NETWORK_DEVICE$";

    public static final String VAR_ELOQUENCE_PREVIEW_VARIABLE_SET = "$PREVIEW_VARIABLE_SET$";

    public static final String VAR_ELOQUENCE_ARCHIVE_PATH = "$ARCHIVE_PATH$";

    public static final String VAR_ELOQUENCE_GENERAL_COLLECTION = "$GENERAL_COLLECTION$";

    public static final String VAR_ELOQUENCE_GENERAL_ENTITY = "$GENERAL_ENTITY$";

    public static final String VAR_ELOQUENCE_LOCAL_DEVICE = "$LOCAL_DEVICE$";

    public static final String VAR_ELOQUENCE_LOG_LEVEL = "$LOG_LEVEL$";

    public static final String VAR_ELOQUENCE_NETWORK_DEVICE = "$NETWORK_DEVICE$";

    public static final String VAR_ELOQUENCE_VARIABLE_SET = "$VARIABLE_SET$";

    public static final String VAR_ELOQUENCE_CLEANUP_AFTER_PREVIEW = "$CLEANUP_AFTER_PREVIEW$";

    public static final String VAR_ELOQUENCE_PMS_COLLECTION = "$PMS_COLLECTION$";

    public static final String VAR_ELOQUENCE_PMS_ENTITY = "$PMS_ENTITY$";

    public static final String VAR_ELOQUENCE_FMS_COLLECTION = "$FMS_COLLECTION$";

    public static final String VAR_ELOQUENCE_FMS_ENTITY = "$FMS_ENTITY$";

    public static final String VAR_ELOQUENCE_CMS_COLLECTION = "$CMS_COLLECTION$";

    public static final String VAR_ELOQUENCE_CMS_ENTITY = "$CMS_ENTITY$";

    public static final String VAR_ELOQUENCE_RMS_COLLECTION = "$RMS_COLLECTION$";

    public static final String VAR_ELOQUENCE_RMS_ENTITY = "$RMS_ENTITY$";

    public static final String VAR_IS_SQLPLUS_EXIST = "$IS_SQLPLUS_AVAILABLE$";

    public static final String VAR_SQLPLUS_HOME = "$SQLPLUS_HOME$";
}
