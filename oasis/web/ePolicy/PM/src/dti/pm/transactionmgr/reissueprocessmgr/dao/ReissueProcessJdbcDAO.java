package dti.pm.transactionmgr.reissueprocessmgr.dao;

import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.pm.core.dao.BaseDAO;
import dti.pm.policymgr.CreatePolicyFields;

import java.util.logging.Logger;
import java.sql.SQLException;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 22, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/01/2013       xnie        148591 Modified reissuePolicy() to map shortRateB to shortTermB.
 * ---------------------------------------------------
 */
public class ReissueProcessJdbcDAO extends BaseDAO implements ReissueProcessDAO {

    private static final String STORED_PROC_GET_RENEWAL_TRANSACTION_CODE ="Pm_Reissue.Pm_Check_Reissue_Renew";
    private static final String STORED_PROC_SHOULD_GENERATE_COI_MESSAGE_FOR_POLICY ="Pm_Reissue.PM_COI_Forward";
    private static final String STORED_PROC_REISSUE_POLICY ="Pm_Reissue.Reissue_Policy";
    private static final String STORED_PROC_BOOKED_TERM_COUNT = "Pm_web_transaction.Get_Booked_Term_Count";
    private static final String STORED_PROC_CHECK_OPEN_TERM = "Pm_Check_Open_Term";

    /**    method to return the transaction code for the reissue transction to be created
     *
     * @param inputRecord  a record that is used to determine the transactionCode
     * @return transaction code
     */
    public String getTransactionCodeForReissueRenewalTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "getTransactionCodeForReissueRenewalTransaction", new Object[]{inputRecord});
        String transactionCode = "";

        // get the transaction code by calling Pm_Reissue.Pm_Check_Reissue_Renew
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("issCompanyId", "issueCompanyEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffDate", "termEffectiveFromDateFromHeader"));

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_GET_RENEWAL_TRANSACTION_CODE, mapping);
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            transactionCode = outputRecordSet.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException se) {
            AppException ae = new AppException(se.toString());
            l.throwing(getClass().getName(), "getTransactionCodeForReissueRenewalTransaction", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getTransactionCodeForReissueRenewalTransaction");
        return transactionCode;
    }

   /**
     *
     * @param inputRecord  a record that is used to determine if a Coi message should be generated
     * @return   YesNoFlag
     */

    public YesNoFlag isCoiCarriedForward(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "getTransactionCodeForCreateRenewalTransaction", new Object[]{inputRecord});

        YesNoFlag carryForward;

        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termId", "policyTermHistoryId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("reissueEffDt", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("cancelDt", "termEffectiveToDateFromHeader"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_SHOULD_GENERATE_COI_MESSAGE_FOR_POLICY, mapping);
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            carryForward = YesNoFlag.getInstance(outputRecordSet.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD));
        }
        catch (SQLException se) {
            AppException ae = new AppException(se.toString());
            l.throwing(getClass().getName(), "isCoiCarriedForward", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isCoiCarriedForward", carryForward);
        return carryForward;
    }

   /**
     *
     * @param inputRecord a record that is used to reissue policy
     */
    public void reissuePolicy(Record inputRecord) {
       Logger l = LogUtils.enterLog(getClass(),"reissuePolicy");

       try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termId", "policyTermHistoryId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("effDate","termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("expDate","termEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "termEffectiveToDateFromHeader"));
            mapping.addFieldMapping(new DataRecordFieldMapping("shortTermB", CreatePolicyFields.SHORT_RATE_B));
            mapping.addFieldMapping(new DataRecordFieldMapping("isuComp", "issueCompanyEntityId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("isuStCd", "issueStateCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("processLoc", "regionalOffice"));
            mapping.addFieldMapping(new DataRecordFieldMapping("carryCoiFwdB", "coiCarryForwardB"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_REISSUE_POLICY, mapping);
            spDao.executeUpdate(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = new AppException(se.toString());
            l.throwing(getClass().getName(), "reissuePolicy", ae);
            throw ae;
        }

       l.exiting(getClass().getName(),"reissuePolicy");
    }

    /**
     *
     * @param inputRecord a record that is used to determine if a policy
     *         has at least one of its term(s) booked
     * @return  true/false indicating if policy has its term(s) booked
     */

    public boolean isPolicyTermBooked(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isPolicyTermBooked", new Object[]{inputRecord});
        boolean isBooked = false;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_BOOKED_TERM_COUNT);
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            int count = outputRecordSet.getSummaryRecord().getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
            if (count > 1) {
                isBooked = true;
            }
        }
        catch (SQLException se) {
            AppException ae = new AppException(se.toString());
            l.throwing(getClass().getName(), "isPolicyTermBooked", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "isPolicyTermBooked", Boolean.valueOf(isBooked));
        return isBooked;
    }

    /**
     *
     * @param inputRecord a record that is used to determine if the dates are
     *          overlapping with a policy's exsting terms
     * @return   true/false indicating if the dates are overlapping
     */

    public boolean areTermDatesOverlapping(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "areTermDatesOverlapping", new Object[]{inputRecord});
        boolean overlapping = false;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff","termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp","termEffectiveToDate"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_CHECK_OPEN_TERM,mapping);
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            overlapping = !outputRecordSet.getSummaryRecord().getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();
        }
        catch (SQLException se) {
            AppException ae = new AppException(se.toString());
            l.throwing(getClass().getName(), "areTermDatesOverlapping", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "areTermDatesOverlapping", Boolean.valueOf(overlapping));
        return overlapping;
    }
}
