package dti.pm.core.request;

import dti.pm.core.http.RequestIds;

/**
 * This interface extends the base request storage ids and provides with a list of request storage id constants that
 * are expected to exists in storage manager for a given request thread.
 * 
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface RequestStorageIds extends dti.oasis.request.RequestStorageIds {
    public final static String SELECTED_POLICY_VIEW_MODE = RequestIds.SELECTED_POLICY_VIEW_MODE;
    public final static String POLICY_HEADER = RequestIds.POLICY_HEADER;
    //public final static String POLICY_LOCK_ID = RequestIds.POLICY_LOCK_ID;
    public final static String CIS_LOOKUP_FIELDS = "cisLookupFields";    
    public final static String POLICY_NO = RequestIds.POLICY_NO;
    public final static String POLICY_TERM_HISTORY_ID = RequestIds.POLICY_TERM_HISTORY_ID;
    public final static String QQ_DEFAULTS_CACHE = RequestIds.QQ_DEFAULTS_CACHE;
}
