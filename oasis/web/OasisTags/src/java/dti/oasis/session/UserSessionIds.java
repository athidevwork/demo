package dti.oasis.session;

import dti.oasis.struts.IOasisAction;

/**
 * This interface provides with a list of session id constants that are expected to exists for any given user session.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 29, 2007
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/01/2017       cv      182615 - Added() to get the SERVER_RUNTIME_HTTP_URL to store ServerRuntime url.
 * 03/09/2018       cesar   189605 - added constants to be user for CSRF implementation.
 * ---------------------------------------------------
 */
public interface UserSessionIds {
    public static final String DB_POOL_ID = IOasisAction.KEY_DBPOOLID;
    public static final String SERVER_RUNTIME_HTTP_URL = "SERVER_RUNTIME_HTTP_URL";

    public static final String TOKEN_SUFFIX = ".token";
    public static final String NEW_TOKEN_GENERATED = "new.token.generated";
    public static final String PAGE_TOKEN = "page.token";
    public static final String SUPER_CLASS_ACTION_NAME = "super.class.action.name";
}
