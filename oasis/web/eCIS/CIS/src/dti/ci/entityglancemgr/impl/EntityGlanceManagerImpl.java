package dti.ci.entityglancemgr.impl;

import dti.ci.entityglancemgr.EntityGlanceFields;
import dti.ci.entityglancemgr.EntityGlanceManager;
import dti.ci.entityglancemgr.dao.EntityGlanceDAO;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.*;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of EntityGlanceManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * User: Michael
 * Date: September 08, 2011
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EntityGlanceManagerImpl implements EntityGlanceManager {

    /**
     * Get Entity Demographic 
     *
     * @param record
     * @return
     */
    public Record loadEntityDemographic(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityDemographic", new Object[]{record});
        }

        Record rtnRecord = getEntityGlanceDAO().loadEntityDemographic(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityDemographic", rtnRecord);
        }
        return rtnRecord;
    }

    /**
     * Get Entity Relationships 
     *
     * @param record
     * @return
     */
    public RecordSet loadRelationships(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadRelationships", new Object[]{record});
        }

        RecordSet rs = getEntityGlanceDAO().loadRelationships(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadRelationships", rs);
        }
        return rs;
    }

    /**
     * Get Entity Policy/Quote 
     *
     * @param record
     * @return
     */
    public RecordSet loadPolicyQuote(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyQuote", new Object[]{record});
        }

        RecordSet rs = getEntityGlanceDAO().loadPolicyQuote(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyQuote", rs);
        }
        return rs;
    }

    /**
     * Get Policy Transactions 
     *
     * @param record
     * @return
     */
    public RecordSet loadTransactions(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadTransactions", new Object[]{record});
        }

        RecordSet rs = getEntityGlanceDAO().loadTransactions(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTransactions", rs);
        }
        return rs;
    }

        /**
     * Get Policy Transaction Forms
     *
     * @param record
     * @return
     */
    public RecordSet loadTransactionForms(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadTransactions", new Object[]{record});
        }

        RecordSet rs = getEntityGlanceDAO().loadTransactionForms(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTransactions", rs);
        }
        return rs;
    }

    /**
     * Get Entity Finances 
     *
     * @param record
     * @return
     */
    public RecordSet loadFinances(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadFinances", new Object[]{record});
        }

        RecordSet rs = getEntityGlanceDAO().loadFinances(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadFinances", rs);
        }
        return rs;
    }

    /**
     * Get Entity Finance Invoices 
     *
     * @param record
     * @return
     */
    public RecordSet loadInvoices(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadInvoices", new Object[]{record});
        }

        RecordSet rs = getEntityGlanceDAO().loadInvoices(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadInvoices", rs);
        }
        return rs;
    }

    /**
     * Get Entity Finance Forms 
     *
     * @param record
     * @return
     */
    public RecordSet loadFinanceForms(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadFinanceForms", new Object[]{record});
        }

        RecordSet rs = getEntityGlanceDAO().loadFinanceForms(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadFinanceForms", rs);
        }
        return rs;
    }

    /**
     * Get Entity Claims 
     *
     * @param record
     * @return
     */
    public RecordSet loadClaims(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadClaims", new Object[]{record});
        }

        RecordSet rs = getEntityGlanceDAO().loadClaims(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadClaims", rs);
        }
        return rs;
    }

    /**
     * Get Entity Participants 
     *
     * @param record
     * @return
     */
    public RecordSet loadParticipants(Record record){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadParticipants", new Object[]{record});
        }

        RecordSet rs = getEntityGlanceDAO().loadParticipants(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadParticipants", rs);
        }
        return rs;
    } 
    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getEntityGlanceDAO() == null)
            throw new ConfigurationException("The required property 'EntityGlanceDAO' is missing.");
    }

    public EntityGlanceDAO getEntityGlanceDAO() {
        return entityGlanceDAO;
    }

    public void setEntityGlanceDAO(EntityGlanceDAO entityGlanceDAO) {
        this.entityGlanceDAO = entityGlanceDAO;
    }

    private EntityGlanceDAO entityGlanceDAO;

    private static final String ROW_STATUS = "rowStatus";
    private static final String NEW = "NEW";
    private static final String MODIFIED = "MODIFIED";
    private static final String DELETED = "DELETED";
}
