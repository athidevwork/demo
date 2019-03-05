package dti.ci.riskmgr.impl;

import dti.ci.riskmgr.RiskManager;
import dti.ci.riskmgr.dao.RiskDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.ConfigurationException;

/**
 * The inplementation of Risk Manager.
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 18, 2008
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/22/2015       bzhu        Issue 156487 - retrieve accumulated discount point.
 * ---------------------------------------------------
 */

public class RiskManagerImpl implements RiskManager {
    /**
     * Get the current risk management discount for an entity.
     *
     * @param inputRecord the information of an entity.
     * @return The current risk management discount information of the entity.
     */
    public Record getCurrentRiskManagementDiscount(Record inputRecord) {
        return getRiskDao().getCurrentRiskManagementDiscount(inputRecord);
    }

    /**
     * Get the current mandate window period information for an entity.
     *
     * @param inputRecord The information of an entity.
     * @return The current mandate window period informantion of the entity.
     */
    public Record getCurrentMandateWindowPeriod(Record inputRecord) {
        return getRiskDao().getCurrentMandateWindowPeriod(inputRecord);
    }

    /**
     * Get the program history information for an entity.
     *
     * @param inputRecord The entity information.
     * @return The program history information of the entity.
     */
    public RecordSet getProgramHistory(Record inputRecord) {
        return getRiskDao().getProgramHistory(inputRecord);
    }

    /**
     * Get Accumulated Discount Point for an entity.
     *
     * @param inputRecord The entity.
     * @return The accumulated discount point information of the entity.
     */
    public Record getAccumulatedDiscountPoint(Record inputRecord) {
        return getRiskDao().getAccumulatedDiscountPoint(inputRecord);
    }

    /**
     * Get the window period history information for an entity.
     *
     * @param inputRecord The entity information.
     * @return The window period history information of the entity.
     */
    public RecordSet getWindowPeriodHistory(Record inputRecord) {
        return getRiskDao().getWindowPeriodHistory(inputRecord);
    }

    /**
     * Get the Additional Risk Management Discount for an entity.
     *
     * @param inputRecord The entity.
     * @return The Additional Risk Manangement Discount information of the entity.
     */
    public RecordSet getAdditionalRiskManagementDiscount(Record inputRecord) {
        return getRiskDao().getAdditionalRiskManagementDiscount(inputRecord);
    }

    /**
     * Get ERS Point History for an entity.
     *
     * @param inputRecord The entity information.
     * @return The ERS Point History information of the entity.
     */
    public RecordSet getErsPointHistory(Record inputRecord) {
        return getRiskDao().getErsPointHistory(inputRecord, new RmErsPointHistoryRecordLoadProcessor());
    }

    public void verifyConfig() {
        if (getRiskDao() == null) {
            throw new ConfigurationException("The required property 'riskDao' is missing.");
        }
    }

    public RiskManagerImpl() {
    }

    public RiskDAO getRiskDao() {
        return riskDao;
    }

    public void setRiskDao(RiskDAO riskDao) {
        this.riskDao = riskDao;
    }

    private RiskDAO riskDao;
}
