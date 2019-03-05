package dti.pm.core.cachemgr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.RefreshParmsEventListener;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.struts.IOasisAction;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class extends implements a policy cache.
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 24, 2013
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/06/13 fcb     148037 - changes for performance tuning.
 * 12/16/13 fcb     150767 - refactored to use RefreshParmsEventListener.
 * 11/18/14 fcb     157975 - added nddValidateCacheKey.
 * 01/12/15 awu     160142 - Modified to cache the Charges Fee configuration.
 * 03/09/18 lzhang  191644 - Add policyCacheMap: store separate
 *                           policyCache Map per dbPoolId.
 * ---------------------------------------------------
 */

public class PolicyCacheManager implements RefreshParmsEventListener {

    public static final String BEAN_NAME = "PolicyCacheManager";
    static final String transactionSnapshotCacheKey = "transactionSnapshotCacheKey";
    static final String riskTypeAddCodeCacheKey = "riskTypeAddCodeCacheKey";
    static final String profEntityCacheKey = "profEntityCacheKey";
    static final String replicationCacheKey = "replicationCacheKey";
    static final String notifyCacheKey = "notifyCacheKey";
    static final String notifyConfiguredCacheKey = "notifyConfiguredCacheKey";
    static final String chargesNFeeCacheKey = "chargesNFeeCacheKey";
    static final String taxCacheKey = "taxCacheKey";
    static final String nddValidateCacheKey = "nddValidateCacheKey";
    
    /**
     * Returns a synchronized static instance of Policy Cache Manager that contains policy configurations.
     * @return PolicyCacheManager, an instance of Policy Cache Manager with policy configurations.
     */
    public synchronized static final PolicyCacheManager getInstance() {
        if (PolicyCacheManager.c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(PolicyCacheManager.BEAN_NAME)) {
                PolicyCacheManager.c_instance = (PolicyCacheManager) ApplicationContext.getInstance().getBean(PolicyCacheManager.BEAN_NAME);
            }
            else{
                PolicyCacheManager.c_instance = new PolicyCacheManager();
            }
        }
        return PolicyCacheManager.c_instance;
    }

    /**
     * Returns true/false depending on whether the snapshot functionality is configured or not.
     * @return boolean
     */
    public boolean getSnapshotConfigured() {
        YesNoFlag value = (YesNoFlag) getPolicyCache().get(transactionSnapshotCacheKey);
        return value.booleanValue();
    }

    /**
     * Sets the snapshot configuration information.
     * @param isConfigured
     */
    public void setSnapshotConfigured(boolean isConfigured) {
        YesNoFlag value = YesNoFlag.getInstance(isConfigured);
        setPolicyCache(transactionSnapshotCacheKey, value);
    }

    /**
     * Returns true/false depending on whether the snapshot indicator has been loaded or not.
     * @return boolean
     */
    public boolean hasSnapshotConfigured() {
        return hasPolicyCache() ? getPolicyCache().containsKey(transactionSnapshotCacheKey) : false;
    }

    /**
     * Returns the record set with the risk type add code configuration.
     * @return RecordSet
     */
    public RecordSet getRiskTypeAddCode() {
        return (RecordSet) getPolicyCache().get(riskTypeAddCodeCacheKey);
    }

    /**
     * Sets the risk type add code configuration information
     * @param recordSet
     */
    public void setRiskTypeAddCode(RecordSet recordSet) {
        setPolicyCache(riskTypeAddCodeCacheKey, recordSet);
    }

    /**
     * Checks if the cache contains information about the risk type add code configuration.
     * @return
     */
    public boolean hasRiskTypeAddCode() {
        return hasPolicyCache() ? getPolicyCache().containsKey(riskTypeAddCodeCacheKey) : false;
    }

    /**
     * Returns true/false depending on whether the professional entity is configured or not.
     * @return boolean
     */
    public boolean getProfEntityConfigured() {
        YesNoFlag value = (YesNoFlag) getPolicyCache().get(profEntityCacheKey);
        return value.booleanValue();
    }

    /**
     * Sets the professional entity configuration information.
     * @param isConfigured
     */
    public void setProfEntityConfigured(boolean isConfigured) {
        YesNoFlag value = YesNoFlag.getInstance(isConfigured);
        setPolicyCache(profEntityCacheKey, value);
    }

    /**
     * Returns true/false depending on whether the professional entity indicator has been loaded or not.
     * @return boolean
     */
    public boolean hasProfEntityConfigured() {
        return hasPolicyCache() ? getPolicyCache().containsKey(profEntityCacheKey) : false;
    }

    /**
     * Returns true/false depending on whether the replication  is configured or not at the system level.
     * @return boolean
     */
    public boolean getReplicationConfigured() {
        YesNoFlag value = (YesNoFlag) getPolicyCache().get(replicationCacheKey);
        return value.booleanValue();
    }

    /**
     * Sets the system level replication indicator.
     * @param isConfigured
     */
    public void setReplicationConfigured(boolean isConfigured) {
        YesNoFlag value = YesNoFlag.getInstance(isConfigured);
        setPolicyCache(replicationCacheKey, value);
    }

    /**
     * Returns true/false depending on whether the replication is configured at the system level.
     * @return boolean
     */
    public boolean hasReplicationConfigured() {
        return hasPolicyCache() ? getPolicyCache().containsKey(replicationCacheKey) : false;
    }

    /**
     * Returns true/false depending on whether product notify is configured or not.
     * @return boolean
     */
    public boolean getNotifyConfigured() {
        YesNoFlag value = (YesNoFlag) getPolicyCache().get(notifyConfiguredCacheKey);
        return value.booleanValue();

    }

    /**
     * Sets the true/false indicator based on whether notification is configured or not.
     * @param isConfigured
     */
    public void setNotifyConfigured(boolean isConfigured) {
        YesNoFlag value = YesNoFlag.getInstance(isConfigured);
        setPolicyCache(notifyConfiguredCacheKey, value);
    }

    /**
     * Checks if the cache contains the general flag that states whether product notification is configured.
     * @return boolean
     */
    public boolean hasNotifyConfigured() {
        return hasPolicyCache() ? getPolicyCache().containsKey(notifyConfiguredCacheKey) : false;
    }

    /**
     * Returns the record set with the transaction code notification configuration.
     * @return RecordSet
     */
    public RecordSet getNotifyTransactionCode() {
        return (RecordSet) getPolicyCache().get(notifyCacheKey);
    }

    /**
     * Sets the list of transaction code notification configuration.
     * @param recordSet
     */
    public void setNotifyTransactionCode(RecordSet recordSet) {
        setPolicyCache(notifyCacheKey, recordSet);
    }

    /**
     * Checks if the cache contains information about the transaction code notification configuration.
     * @return
     */
    public boolean hasNotifyTransactionCode() {
        return hasPolicyCache() ? getPolicyCache().containsKey(notifyCacheKey) : false;
    }

    /**
     * Returns true/false depending on whether charges fees are configured or not.
     * @return boolean
     */
    public boolean getChargesNFeesConfigured() {
        YesNoFlag value = (YesNoFlag) getPolicyCache().get(chargesNFeeCacheKey);
        return value.booleanValue();

    }

    /**
     * Sets the true/false indicator based on whether charges fees are configured or not.
     * @param isConfigured
     */
    public void setChargesNFeeConfigured(boolean isConfigured) {
        YesNoFlag value = YesNoFlag.getInstance(isConfigured);
        setPolicyCache(chargesNFeeCacheKey, value);
    }

    /**
     * Checks if the cache contains the general flag that states whether charges fees are configured.
     * @return boolean
     */
    public boolean hasChargesNFeesConfigured() {
        return hasPolicyCache() ? getPolicyCache().containsKey(chargesNFeeCacheKey) : false;
    }

    /**
     * Returns true/false depending on whether the tax calculation is configured or not at the system level.
     * @return boolean
     */
    public boolean getTaxConfigured() {
        YesNoFlag value = (YesNoFlag) getPolicyCache().get(taxCacheKey);
        return value.booleanValue();
    }

    /**
     * Sets the system level tax calculation indicator.
     * @param isConfigured
     */
    public void setTaxConfigured(boolean isConfigured) {
        YesNoFlag value = YesNoFlag.getInstance(isConfigured);
        setPolicyCache(taxCacheKey, value);
    }

    /**
     * Returns true/false depending on whether the tax is configured at the system level.
     * @return boolean
     */
    public boolean hasTaxConfigured() {
        return hasPolicyCache() ? getPolicyCache().containsKey(taxCacheKey) : false;
    }

    /**
     * Returns true/false depending on whether the ndd validation for exp date is configured or not at the system level.
     * @return boolean
     */
    public boolean getNddValConfigured() {
        YesNoFlag value = (YesNoFlag) getPolicyCache().get(nddValidateCacheKey);
        return value.booleanValue();
    }

    /**
     * Sets the system level ndd validation for exp date indicator.
     * @param isConfigured
     */
    public void setNddValConfigured(boolean isConfigured) {
        YesNoFlag value = YesNoFlag.getInstance(isConfigured);
        setPolicyCache(nddValidateCacheKey, value);
    }

    /**
     * Returns true/false depending on whether the ndd validation for exp date is configured at the system level.
     * @return boolean
     */
    public boolean hasNddValConfigured() {
        return hasPolicyCache() ? getPolicyCache().containsKey(nddValidateCacheKey) : false;
    }

    /**
     * Sets PolicyCache value.
     */
    protected void setPolicyCache(String cacheKey, Object objVal) {
        if (hasPolicyCache()){
            getPolicyCache().put(cacheKey, objVal);
        }
        else{
            ConcurrentHashMap<String,Object> policyCache = new ConcurrentHashMap<String,Object>();
            policyCache.put(cacheKey, objVal);
            getPolicyCacheMap().put(getCurrentDBPoolID(), policyCache);
        }
    }
    /**
     * Returns true/false depending on whether policyCacheMap has currentDBPoolID.
     * @return boolean
     */
    protected boolean hasPolicyCache() {
        return getPolicyCacheMap().containsKey(getCurrentDBPoolID());
    }

    /**
     * Return the user cache map.
     */
    protected ConcurrentHashMap<String,Object> getPolicyCache() {
        return getPolicyCacheMap().get(getCurrentDBPoolID());
    }

    /**
     * Implements the refresh parameters listener.
     * @param request
     */
    public void refreshParms(HttpServletRequest request) {
        getPolicyCacheMap().clear();
        MessageManager.getInstance().addInfoMessage("core.refresh.policy.cache.parameters");
    }

    /**
     * Return the user cache map.
     */
    protected ConcurrentHashMap<String,ConcurrentHashMap<String,Object>> getPolicyCacheMap() {
        return m_policyCacheMap;
    }

    /**
     * Return the current DBPoolID.
     */
    protected String getCurrentDBPoolID() {
        UserSession userSession = UserSessionManager.getInstance().getUserSession();
        return userSession.get(IOasisAction.KEY_DBPOOLID).toString();
    }
    //-------------------------------------------------
    // Configuration constructor
    //-------------------------------------------------
    public void verifyConfig() {
    }

    private ConcurrentHashMap<String,ConcurrentHashMap<String,Object>> m_policyCacheMap = new ConcurrentHashMap<String,ConcurrentHashMap<String,Object>>();
    private static PolicyCacheManager c_instance;
}

