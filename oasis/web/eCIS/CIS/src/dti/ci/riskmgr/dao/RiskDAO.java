package dti.ci.riskmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * The Data Object for Risk Management.
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

public interface RiskDAO {
    /**
     * Get the current risk management discount for an entity.
     * @param inputRecord the information of an entity.
     * @return The current risk management discount information of the entity.
     */
    public Record getCurrentRiskManagementDiscount(Record inputRecord);

    /**
     * Get the current mandate window period information for an entity.
     * @param inputRecord The information of an entity.
     * @return The current mandate window period informantion of the entity.
     */
    public Record getCurrentMandateWindowPeriod(Record inputRecord);

    /**
     * Get the program history information for an entity.
     * @param inputRecord The entity information.
     * @return The program history information of the entity.
     */
    public RecordSet getProgramHistory(Record inputRecord);

    /**
     * Get the window period history information for an entity.
     * @param inputRecord The entity information.
     * @return The window period history information of the entity.
     */
    public RecordSet getWindowPeriodHistory(Record inputRecord);

    /**
     * Get the Additional Risk Management Discount for an entity.
     * @param inputRecord The entity.
     * @return The Additional Risk Manangement Discount information of the entity.
     */
    public RecordSet getAdditionalRiskManagementDiscount(Record inputRecord);

    /**
     * Get ERS Point History for an entity.
     * @param inputRecord The entity information.
     * @return The ERS Point History information of the entity.
     */
    public RecordSet getErsPointHistory(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Get Accumulated Discount Point for an entity.
     * @param inputRecord The entity.
     * @return The accumulated discount point information of the entity.
     */
    public Record getAccumulatedDiscountPoint(Record inputRecord);
}
