package dti.oasis.dwr;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Apr 2, 2009
 * Time: 8:27:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DwrConstants {
    //PROCESS RELATED CONSTANTS
    public static final String BUS_VIEW = "BUS_VIEW"; // corresponds to action name
    public static final String BUS_EVENT="process"; //corresponds to the value in "process" attribute/parameter for dispatch action
    public static final String BUS_EVENT_NAME="DWR_EVENT";
    public static final String FORM_FIELD_ORIG = "DWR_ORIG";//used to store collected original values and attach to the form
    public static final String FORM_FIELD_CURR = "DWR_CURR";//used to store collected current form values and attach to the form
    public static final String PROCESS_TRANSACTION_CONNECTION="CONNECTION";

    //the message will indicate that exception was thrown due to errors found by DWR process
    public static final String DWR_VALIDATION_EXCEPTION_MSG ="DWR_VALIDATION_EXCEPTION_MSG";

    //CONDITION RELATED COONSTANTS
    public static final String LOGIC_AND ="AND";
    public static final String LOGIC_OR ="OR";
    public static final String LOGIC_NOT ="NOT";
    public static final String LOGIC_XOR="XOR";
    public static final String BEAN_SHELL_EXP_TYPE="BS";
    public static final String JAVA_EXP_TYPE="JV";

    //ARGUMENT RELATED CONSTANTS
    public static final String ORIGNAL_INDICATOR = "ORIG_";
    public static final String FIELD_XPATH="form/fields";
    public static final String FIELD_NODE_NAME="field";
    public static final String FIELD_NODE_NAME_ATTR="name";
    public static final String FIELD_NODE_VAL_ATTR = "value";

    public static final String GRID_XPATH="form/ROWS/ROW";
    //ACTION RELATED CONSTANTS
    public static final String ACTION_DIARY="CLMDIARY";
   
    public static final String ACTION_ERROR = "ERROR";
    public static final String ACTION_WARNING="WARNING";
    public static final String ACTION_MESSAGE="MESSAGE";
    public static final String ACTION_FORM="FORM";
    public static final String ACTION_PROC_CALL="PROCCALL";
    public static final String ACTION_COLN_UPD ="COLUPD";

    
    public static final String ACTION_ARG_FIELD_TYPE = "FIELD";
    public static final String ACTION_ARG_CONST_TYPE = "VALUE";

    public static final String ACTION_MESSAGE_PREFIX="DWR"; //will be used for keys to DWR related messages

    //DIARY PARAMETERS
    public static final String DIARY_SRC_REC_PK="SOURCE_RECORD_PK"; //obtained from form
    public static final String DIARY_ENTITY_PK="ENTITY_PK"; //not required can be obtained from session
    public static final String DIARY_ROLE_CODE="ROLE_CODE";
    public static final String DIARY_EVENT = "DIARY_EVENT"; //should be "Y"
    public static final String DIARY_SCHD_DATE="SCHEDULED_DATE";  //should be sysdate
    public static final String DIARY_COMP_DATE="COMPLTION_DATE";//should be null or ""
    public static final String DIARY_NOTE = "DIARY_NOTE";
    public static final String DIARY_SRC_TBL_NAME="SOURCE_TABLE_NAME";// ?????

    //MESSAGE RELATED CONSTANTS
    // will be used to supress message when DWR errors exist
    public static final String SUPRESS_VALIDATION_MESSAGE="SUPPRESME";


}
