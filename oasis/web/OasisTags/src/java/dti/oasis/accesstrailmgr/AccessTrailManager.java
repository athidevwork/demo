package dti.oasis.accesstrailmgr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import javax.servlet.http.HttpServletRequest;

/**
 * This is an interface for recording user activities in eOASIS.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 12, 2010
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public abstract class AccessTrailManager {

    public static final String BASE_EXCLUDED_METHOD_LIST = "oasis.access.trail.base.excluded.methods";
    public static final String BASE_EXCLUDED_URL_LIST = "oasis.access.trail.base.excluded.urls";

    public static final String CUSTOM_EXCLUDED_METHOD_LIST = "oasis.access.trail.custom.excluded.methods";
    public static final String CUSTOM_EXCLUDED_URL_LIST = "oasis.access.trail.custom.excluded.urls";

    public static final String WEB_DEFAULT_PAGE_AUDIT = "WEB_DFLT_PAGE_AUDIT";

    public static final int SESSION_INFO = 1;
    public static final int COMPLIANCE_INFO = 2;    

    /**
     * The bean name of a AccessTrailManager extension if it is configured in the ApplicationContext.
     */
    public static final String BEAN_NAME = "AccessTrailManager";

    /**
     * Return an instance of the AccessTrailManager.
     */
    public synchronized static final AccessTrailManager getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(BEAN_NAME)) {
                c_instance = (AccessTrailManager) ApplicationContext.getInstance().getBean(BEAN_NAME);
            } else {
                throw new ConfigurationException("The required bean '" + BEAN_NAME + "' is missing.");
            }
        }
        return c_instance;
    }

    /**
     * Initialize any information for this request
     *
     * @param request
     */
    public abstract void initializeForRequest(HttpServletRequest request);


    /**
     * This method accepts the user activity information and saves it into database.
     *
     * @param oasisUserId
     * @param userName
     * @param ipAddress
     * @param subsystemCode
     * @param pageCode
     * @param method
     * @param sourceTableName
     * @param sourceRecordNo
     * @param sourceRecordFk
     */
    public abstract void recordAccessTrail(String oasisUserId, String userName, String ipAddress,
                                           String subsystemCode, String pageCode, String method,
                                           String sourceTableName, String sourceRecordNo, String sourceRecordFk);

    /**
     * record access trail
     *
     * @param request
     */
    public abstract void processAccessInfo(HttpServletRequest request);

    /**
     * record access trail
     *
     * @param request
     */
    public abstract void recordAccessTrail(HttpServletRequest request);

    /**
     * This method accepts the user activity information and saves it into database.
     *
     * @param oasisUserId
     * @param userName
     * @param ipAddress
     * @param subsystemCode
     * @param pageCode
     * @param method
     * @param sourceTableName
     * @param sourceRecordNo
     * @param sourceRecordFk
     * @param webSessionId
     */
    public abstract void recordSessionInfo(String oasisUserId, String userName, String ipAddress,
                                           String subsystemCode, String pageCode, String method,
                                           String sourceTableName, String sourceRecordNo, String sourceRecordFk,
                                           String webSessionId);

    /**
     * record access trail
     *
     * @param request
     */
    public abstract void recordSessionInfo(HttpServletRequest request);

    
    /**
     * This method get last login date/time.
     *
     * @param oasisUserId
     * @param webSessionId
     */
    public abstract String getPriorLoginTimestamp(String oasisUserId, String webSessionId);
    /**
     * load latest active users
     *
     * @param record
     * @return
     */
    public abstract RecordSet loadAllActiveUsers(Record record);    

    private static AccessTrailManager c_instance;
}
