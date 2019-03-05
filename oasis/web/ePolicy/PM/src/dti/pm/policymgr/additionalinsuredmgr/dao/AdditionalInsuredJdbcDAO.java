package dti.pm.policymgr.additionalinsuredmgr.dao;

import dti.pm.core.dao.BaseDAO;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.additionalinsuredmgr.AdditionalInsuredFields;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;

/**
 * This Class provides the implementation details of AdditionalInsuredDAO Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 15, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * -----------------------------------------------------------------------
 *  10/23/2008      GXC         Issue 86879 - pass renewalb to the save procedure
 *  07/01/2010      syang       Issue - 108651. Modified saveAllAdditionalInsured,termExpDate should match policyExpirationDate. 
 *  09/16/2010      syang       Issue 111445 - Modified saveAllAdditionalInsured() to add fields mapping and
 *                                             added getAddInsCoverageData() to retrieve coverage data.
 * 10/21/2010       syang       Issue 113283 - Changed "covg1OverrideRetroDate" to "covg1OverrideRetroText".
 * 02/27/2013       xnie        Issue 138026 - Added generateAllAddIns() to generate all of Additional Insureds.
 * -----------------------------------------------------------------------
 */
public class AdditionalInsuredJdbcDAO extends BaseDAO implements AdditionalInsuredDAO {


    /**
     * load all additioanl insured
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return recordset of additional insured
     */
    public RecordSet loadAllAdditionalInsured(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAdditionalInsured", new Object[]{inputRecord});
        }
        RecordSet rs = null;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effectiveFromDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effectiveToDate", "termEffectiveToDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Additional_Insured.Sel_Additional_Insured", mapping);
        try {
            rs = spDao.execute(inputRecord,recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load additional insured information ", e);
            l.throwing(getClass().getName(), "loadAllAdditionalInsured", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAdditionalInsured", rs);
        }
        return rs;
    }


    /**
     * save all additional insured data
     *
     * @param inputRecords     *
     * @return processed records count
     */
    public int saveAllAdditionalInsured(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllAdditionalInsured", new Object[]{inputRecords});
        }

        int processCount = 0;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effectiveFromDate", "startDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "endDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "policyExpirationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sqFootage", "squareFootage"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyNo", "externalId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("entRoleId", "entityRoleId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("renewB", "renewalB"));
        // For issue 111445.
        mapping.addFieldMapping(new DataRecordFieldMapping("cvg1OvdOccLmt", "covg1OverrideOccLmt"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cvg1OvdAggLmt", "covg1OverrideAggLmt"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cvg1OvdRetroText", "covg1OverrideRetroText"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cvg2OvdOccLmt", "covg2OverrideOccLmt"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cvg2OvdAggLmt", "covg2OverrideAggLmt"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cvg3OvdOccLmt", "covg3OverrideOccLmt"));
        mapping.addFieldMapping(new DataRecordFieldMapping("lmtRule", "limitApplyRule"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Save_Screens.Save_Addins",mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save additional insured information ", e);
            l.throwing(getClass().getName(), "saveAllAdditionalInsured", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllAdditionalInsured", new Integer(processCount));
        }

        return processCount;

    }


    /**
     * get Additionsl Insured Policy Type Count
     *
     * @param inputRecord
     * @return the count of Additionsl Insured Policy Type
     */
    public int getAdditionslInsuredPolicyTypeCount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAdditionslInsuredPolicyTypeCount", new Object[]{inputRecord});
        }
        int count=-1;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Get_Addl_Ins_Pol_Type_Count");
        try {
            count = spDao.execute(inputRecord).getSummaryRecord().
                getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD ).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get additional insured policy type count ", e);
            l.throwing(getClass().getName(), "getAdditionslInsuredPolicyTypeCount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAdditionslInsuredPolicyTypeCount", String.valueOf(count));
        }

        return count;

    }

    /**
     * To get coverage data
     *
     * @param inputRecord
     * @return Record
     */
    public Record getAddInsCoverageData(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAddInsCoverageData", new Object[]{inputRecord});
        }
        Record output = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_WEB_ADDITIONAL_INSURED.Get_Covg_Info_Of_AddlIns");
        try {
            RecordSet rs = spDao.execute(inputRecord);
            if (rs.getSize() > 0) {
                output = rs.getFirstRecord();
            }
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get coverage data ", e);
            l.throwing(getClass().getName(), "getAddInsCoverageData", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAddInsCoverageData", output);
        }
        return output;
    }

    /**
     * Generate all Additional Insured.
     *
     * @param inputRecord input Additional Insured data.
     */
    public void generateAllAddIns(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "generateAllAddIns", new Object[]{inputRecord});

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", PolicyFields.TERM_BASE_RECORD_ID));
        mapping.addFieldMapping(new DataRecordFieldMapping("addInsPks", AdditionalInsuredFields.SELECT_TO_GENERATE_ADDINS_IDS));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Additional_Insured.Generate_Additional_Insured", mapping);
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to generate Additional Insured.", e);
            l.throwing(getClass().getName(), "generateAllAddIns", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "generateAllAddIns");
    }
}
