package dti.pm.core.session;

import dti.pm.core.http.RequestIds;

/**
 * This interface extends oasis user session ids and provides with a list of session id constants that
 * are expected to exists for any given user session.
 * 
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 9, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/23/2007       sxm         Added POLICY_LIST
 * 01/27/2016       wdang       Added POLICY_SAVE_MESSAGE
 * 06/17/2016       tzeng       Added POLICY_BATCH_RENEWAL_ID
 * ---------------------------------------------------
 */
public interface UserSessionIds extends dti.oasis.session.UserSessionIds {
    public final static String POLICY_HEADER = RequestIds.POLICY_HEADER;
    public final static String POLICY_LIST = "policyList";
    public final static String POLICY_NO_SEARCH_CRITERIA = "POLICY_NO_SEARCH_CRITERIA";
    public final static String POLICY_SAVE_MESSAGE = "policySaveMessage";
    public final static String POLICY_BATCH_RENEWAL_ID = "policyBatchRenewalId";
}
