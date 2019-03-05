package dti.pm.billingmgr.dao;

import dti.oasis.busobjs.YesNoFlag;
import dti.pm.core.dao.BaseDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;

import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 1, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/29/2009       yhyang      91531: Add the method getPolicyRelationValue().
 * 07/30/2010       bhong       110357 - Changed field mapping in "updateBillingAccount" method to use
 *                              "newTermEffectiveDate" as "policyEffectiveDate".
 * 08/25/2010       bhong       110269 - Added isCoverageIdExists
 * 04/06/2012       jshen       132152 - Add issueCompanyEntityId mapping for saveBilling() method.
 * 02/25/2013       kmv         142132 - Change field mapping in SaveBilling to use baseBillMmDd
 * 01/31/2014       kmv         151384 - Change issueCompanyEntityId mapping for getInitialValuesForBilling()
 * 10/20/2014       awu         145137 - Modified getInitialValuesForBilling to call the procedure
 *                                      'Get_Initial_Values_For_Billing'.
 * ---------------------------------------------------
 */
public class BillingJdbcDAO extends BaseDAO implements BillingDAO {

    /**
     * Method that gets the default values with recordLoadProcessor
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param inputRecord Record that contains input parameters
     * @return Record that contains default (init) values
     */
    public RecordSet getInitialValuesForBilling(Record inputRecord) {
        return getInitialValuesForBilling(inputRecord, null);
    }

    /**
     * Method that gets the default values with recordLoadProcessor
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param inputRecord         Record that contains input parameters
     * @param recordLoadProcessor RecordLoad Processor
     * @return Record that contains default (init) values
     */

    public RecordSet getInitialValuesForBilling(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {

        Logger l = LogUtils.enterLog(this.getClass(), "getInitialValuesForBilling", new Object[]{inputRecord});

        RecordSet rs = null;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("isMoreOptionAvailable", "isAdditionalBillingSettingsEditable"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueCompanyEntityId", "issCompEntityId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Billing.Get_Initial_Values_For_Billing",mapping);

        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get initial values for BillingSetup", e);
            l.throwing(getClass().getName(), "getInitialValuesForBilling", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getInitialValuesForBilling", rs);
        return rs;
    }

    /**
     * Method that validates the billing data
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param inputRecord Record that contains input parameters
     * @return Record that contains validated data.
     */

    public Record validateValuesForBilling(Record inputRecord) {
        return validateValuesForBilling(inputRecord, null);
    }

    /**
     * Method that validates the billing data with recordLoadProcessor
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param inputRecord Record that contains input parameters
     * @return Record that contains validated data.
     */

    public Record validateValuesForBilling(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {

        Logger l = LogUtils.enterLog(this.getClass(), "validateValuesForBilling", new Object[]{inputRecord});

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Billing.Validate_Values");

        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get initial values for BillingSetup", e);
            l.throwing(getClass().getName(), "validateValuesForBilling", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "validateValuesForBilling", rs);
        return rs.getFirstRecord();
    }

    /**
     * Method that saves the billing data with recordLoadProcessor
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param inputRecord Record that have been validated
     * @return Record that returned by Database process in additon to validated input data (for redisplay).
     */

    public Record saveBilling(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveBilling", new Object[]{inputRecord});
        Record outputRecord = null;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("checkAccountExistsB", "acctHolderIsPolHolderB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("checkBrelExistsB", "brlCheck"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueCompanyEntityId", "issCompEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("baseBillMmDd", "baseBillMonthDay"));
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_web_billing.Save_Billing", mapping);
            outputRecord = spDao.executeUpdate(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save Billing information for Policy: " + inputRecord.getStringValue("policyNo"), e);
            l.throwing(getClass().getName(), "saveBilling", ae);
            throw ae;
        }

       return outputRecord;
    }

    /**
     * Method that updates the Billing Account information.
     *
     * @param inputRecord Record that contains input parameters
     */
    public void updateBillingAccount(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "updateBillingAccount", new Object[]{inputRecord});

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("policyEffectiveDate", "newTermEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("policyEntityId", "policyHolderNameEntityId"));

            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Fm_Billing_Relation_Pol_Upd", mapping);
            spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update billing account for the new term dates.", e);
            l.throwing(getClass().getName(), "updateBillingAccount", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "updateBillingAccount");
    }

    /**
     * Get the policy relation value.
     *
     * @param inputRecord
     * @return String
     */
    public String getPolicyRelationValue(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getPolicyRelationValue", new Object[]{inputRecord});
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("fm_relation_exist");
            rs = spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get the policy relation value", e);
            l.throwing(getClass().getName(), "getPolicyRelationValue", ae);
            throw ae;
        }
        String returnValue = rs.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        l.exiting(getClass().getName(), "getPolicyRelationValue", returnValue);
        return returnValue;
    }

    /**
     * Check if coverage id exists
     *
     * @param inputRecord
     * @return boolean
     */
    public boolean isCoverageIdExists(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isCoverageIdExists", new Object[]{inputRecord,});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Billing.Is_Covg_Id_Exists");
            rs = spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get the policy relation value", e);
            l.throwing(getClass().getName(), "getPolicyRelationValue", ae);
            throw ae;
        }
        boolean returnValue = YesNoFlag.getInstance(rs.getSummaryRecord().getStringValue(
            StoredProcedureDAO.RETURN_VALUE_FIELD)).booleanValue();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCoverageIdExists", Boolean.valueOf(returnValue));
        }
        return returnValue;
    }

    /**
     * To validate if an account exists for an entity
     * @param inputRecord a Record with entityId.
     * @return  String
     */
    public Record validateAccountExistsForEntity(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateAccountExistsForEntity");

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Fm_Web_Billing_Setup.Check_If_Account_Exists");
        Record record;
        try {
            record = spDao.execute(inputRecord).getSummaryRecord();
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check if an account exists for an entity.", e);
            l.throwing(getClass().getName(), "validateAccountExistsForEntity", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "validateAccountExistsForEntity", record);
        return record;
    }

}
