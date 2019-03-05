package dti.oasis.util;

import dti.oasis.app.ConfigurationException;
import dti.oasis.cachemgr.UserCacheManager;
import dti.oasis.security.Authenticator;
import dti.oasis.session.UserSessionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Oasis User bean
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p> 
 * Date:   Dec 22, 2003
 * @author jbe
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  1/28/2004       jbe     Add internalUser
 *  2/6/2004        jbe     Added toString()
 *  12/10/2004      jbe     Added entityPk
 *  06/29/2006      sxm     Added userName
 *  10/01/2013      fcb     Issue#145725 Removed user profiles, they are cached now in UserCacheManager
 *  05/11/2016      wdang   Issue#176749 Added sourceContext.
 *  02/22/2017      tzeng   Issue#168385 Added hasRequestedTransactionTime, getRequestedTransactionTime,
 *                                       setRequestedTransactionTime.
 * ---------------------------------------------------
 */

public class OasisUser implements java.io.Serializable {
    private String userId;
    private Date lastLoggedIn;
    private Date passwordUpdated;
    private boolean anonymous;
    private boolean ignorePasswordExp;
    private boolean internalUser;
    private long entityPk;
    private String userName;
    private SourceContextEnum sourceContext;
    private String requestedTransactionTime;

    public static enum SourceContextEnum {
        WEB, OWS, BATCH
    }

    public OasisUser() {
        anonymous = true;
        userId = null;
    }

    public OasisUser(String userId, boolean anonymous) {
        this.userId = userId;
        this.anonymous = anonymous;
    }

    public OasisUser(String userId, Date lastLoggedIn, Date passwordUpdated, boolean internalUser) {
        this.userId = userId;
        this.lastLoggedIn = lastLoggedIn;
        this.passwordUpdated = passwordUpdated;
        this.internalUser = internalUser;
    }

    public OasisUser(String userId, Date lastLoggedIn, Date passwordUpdated) {
        this.userId = userId;
        this.lastLoggedIn = lastLoggedIn;
        this.passwordUpdated = passwordUpdated;
        this.anonymous = false;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Date getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(Date lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    public Date getPasswordUpdated() {
        return passwordUpdated;
    }

    public void setPasswordUpdated(Date passwordUpdated) {
        this.passwordUpdated = passwordUpdated;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public boolean isIgnorePasswordExp() {
        return ignorePasswordExp;
    }

    public void setIgnorePasswordExp(boolean ignorePasswordExp) {
        this.ignorePasswordExp = ignorePasswordExp;
    }

    public boolean isInternalUser() {
        return internalUser;
    }

    public void setInternalUser(boolean internalUser) {
        this.internalUser = internalUser;
    }

    public long getEntityPk() {
        return entityPk;
    }

    public void setEntityPk(long entityPk) {
        this.entityPk = entityPk;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public SourceContextEnum getSourceContext() {
        return sourceContext;
    }

    public void setSourceContext(SourceContextEnum sourceContext) {
        this.sourceContext = sourceContext;
    }

    public boolean hasRequestedTransactionTime() {
        return (requestedTransactionTime != null && !requestedTransactionTime.isEmpty());
    }

    public String getRequestedTransactionTime() {
        return requestedTransactionTime;
    }

    public void setRequestedTransactionTime(String requestedTransactionTime) {
        this.requestedTransactionTime = requestedTransactionTime;
    }

    public void setProfiles() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setProfiles");
        }
        
        String dbPoolId = (String) UserSessionManager.getInstance().getUserSession().get(dti.oasis.session.UserSessionIds.DB_POOL_ID);
        try {
            ArrayList profiles = Authenticator.getUserProfs(dbPoolId, getUserId());
            UserCacheManager userCache = UserCacheManager.getInstance();
            userCache.set(UserCacheManager.USER_PROFILES_CACHE_KEY, profiles);
        }
        catch (Exception e) {
            StringBuffer errorStringBuffer = new StringBuffer("Could not load user profiles.\n");
            ConfigurationException ce = new ConfigurationException(errorStringBuffer.toString());
            l.throwing(getClass().getName(), "setProfiles", ce);
            throw ce;
        }
        l.exiting(getClass().getName(), "setProfiles");
    }
    
    public boolean hasProfile(String profile) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasProfile", new Object[]{profile});
        }

        boolean profileExists = false;
        if (!UserCacheManager.getInstance().has(UserCacheManager.USER_PROFILES_CACHE_KEY)) {
            setProfiles();
        }

        ArrayList <String> configuredUserProfiles = (ArrayList)UserCacheManager.getInstance().get(UserCacheManager.USER_PROFILES_CACHE_KEY);
        for (String configuredUserProfile : configuredUserProfiles) {
            if (configuredUserProfile.equalsIgnoreCase(profile)) {
                profileExists = true;
                break;
            }
        }
        l.exiting(getClass().getName(), "hasProfile", Boolean.valueOf(profileExists));

        return profileExists;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.util.OasisUser");
        buf.append("{userId=").append(userId);
        buf.append(",lastLoggedIn=").append(lastLoggedIn);
        buf.append(",passwordUpdated=").append(passwordUpdated);
        buf.append(",anonymous=").append(anonymous);
        buf.append(",ignorePasswordExp=").append(ignorePasswordExp);
        buf.append(",internalUser=").append(internalUser);
        buf.append(",entityPk=").append(entityPk);
        buf.append(",userName=").append(userName);
        buf.append(",sourceContext=").append(sourceContext);
        buf.append('}');
        return buf.toString();
    }
    private final Logger l = LogUtils.getLogger(getClass());
}
