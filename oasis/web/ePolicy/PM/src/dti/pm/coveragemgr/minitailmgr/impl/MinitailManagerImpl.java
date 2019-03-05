package dti.pm.coveragemgr.minitailmgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.error.ValidationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.minitailmgr.MinitailManager;
import dti.pm.coveragemgr.minitailmgr.dao.MinitailDAO;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.busobjs.RecordMode;
import dti.pm.riskmgr.RiskFields;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * this class is implementation of MinitailManager
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 23, 2007
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/23/2007       zlzhu       Created
 * 09/23/2010       syang       Issue 110819 - Added FilterOfficialRowForEndquoteRecordLoadProcessor to
 *                                             loadAllMinitailRiskCoverage() and loadAllMinitail().
 * 11/25/2010       bhong       114074 - added additional parameter "inputRecord" in loadAllMinitailRiskCoverage
 *                              and loadAllMinitail methods, also cleaned up a few nasty codes.
 * 04/25/2012       xnie        132237 - 1) Modified validateAllMinitail for make validation message clear.
 *                                       2) Modified saveAllMinitail to add parentInputRecords for validation.
 * 11/05/2013      jyang2      158679 - 1. Modified saveAllMinitail method, added parameter policyHeader to get policy's
 *                                         latest transaction fk.
 *                                      2. Modified saveAllMinitail to only update temp mode records and the official
 *                                         mode records which has been updated.
 * 12/15/2014       fcb         159796 - Fixed implementation to address the discrepancies between the business rules
 *                                       and revised logic stated in the UC & implemented in ePolicy.
 * 08/21/2015       ssheng      165340 - Use Common Function PolicyHeader.getRecordMode to replace calcuate recordModeCode
 *                                       via screenMode.
 * ---------------------------------------------------
 */

public class MinitailManagerImpl implements MinitailManager {
    /**
     * save all mini tail data
     * <p/>
     *
     * @param miniTails the mini tail needed to save
     *        parentInputRecords mini tail parent data for validation
     */
    public void saveAllMinitail(RecordSet miniTails, RecordSet parentInputRecords, PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllMinitail", new Object[]{miniTails});
        miniTails = miniTails.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        RecordSet wipMinitails = miniTails.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));
        RecordSet offMinitails = miniTails.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));
        RecordSet updMinitails = offMinitails.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        updMinitails.addRecords(wipMinitails);

        validateAllMinitail(updMinitails, parentInputRecords);

        miniTails.setFieldValueOnAll("transactionLogId", policyHeader.getLastTransactionId());
        getMinitailDAO().saveAllMinitail(updMinitails);
        l.exiting(getClass().getName(), "saveAllMinitail");
    }

    /**
     * load the mini tail related risk coverage data
     * <p/>
     *
     * @param inputRecord
     * @param policyHeader policy header
     * @return the result
     */
    public RecordSet loadAllMinitailRiskCoverage(Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllMinitailRiskCoverage", new Object[]{inputRecord,policyHeader});
        }

        RecordLoadProcessor loadProcessor = new RiskCoverageEntitlementRecordLoadProcessor(policyHeader);
        // Issue 110819, filter official record for end quote.
        FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor =
            new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, "coverageBaseRecordId");
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, endquoteLoadProcessor);
        // issue#114074 get riskBaseRecordId from inputRecord instead of policy header to avoid wrong value passed
        // during work flow
        Record rec = policyHeader.toRecord();
        if (inputRecord.hasStringValue("riskBaseRecordId")) {
            rec.setFieldValue("riskBaseRecordId", inputRecord.getStringValue("riskBaseRecordId"));
        }
        else {
            rec.setFieldValue("riskBaseRecordId", null);
        }
        // Perform business logic to determine record mode code
        setRecordMode(policyHeader, rec);
        RecordSet riskCoverage = getMinitailDAO().loadAllMinitailRiskCoverage(rec, loadProcessor);
        l.exiting(getClass().getName(), "loadAllMinitailRiskCoverage",new Object[]{riskCoverage});
        return riskCoverage;
    }

    /**
     * load the mini tail data
     *
     * @param inputRecord
     * @param policyHeader policy header
     * @return the mini tail data
     */
    public RecordSet loadAllMinitail(Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllMinitail", new Object[]{policyHeader});
        RecordLoadProcessor loadProcessor = new MinitailEntitlementRecordLoadProcessor();
        // Issue 110819, filter official record for end quote.
        FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor = 
            new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, "miniTailId");
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, endquoteLoadProcessor);
        // issue#114074 get riskBaseRecordId from inputRecord instead of policy header to avoid wrong value passed
        // during work flow
        Record rec = policyHeader.toRecord();
        if (inputRecord.hasStringValue("riskBaseRecordId")) {
            rec.setFieldValue("riskBaseRecordId", inputRecord.getStringValue("riskBaseRecordId"));
        }
        else {
            rec.setFieldValue("riskBaseRecordId", null);
        }
        // Perform business logic to determine record mode code
        setRecordMode(policyHeader,rec);
        RecordSet minitail = getMinitailDAO().loadAllMinitail(rec, loadProcessor);
        l.exiting(getClass().getName(), "loadAllMinitail",new Object[]{minitail});
        return minitail;
    }

    /**
     * set record mode code to the record
     * @param policyHeader policy header
     * @param inputRecord which record to set
     */
    private void setRecordMode(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setRecordMode", new Object[]{policyHeader, inputRecord});
        }
        String recordModeCode = policyHeader.getRecordMode().getName();
        inputRecord.setFieldValue("recordModeCode", recordModeCode);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setRecordMode");
        }
    }

    /**
     * validate the input recordset before save,mainly checks if the rating basis
     * >=0 and <=100
     * @param recordSet input recordset
     */
    protected void validateAllMinitail(RecordSet recordSet, RecordSet parentInputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "validateAllMinitail", new Object[]{recordSet});
        Iterator parentIt = parentInputRecords.getRecords();
        while (parentIt.hasNext()) {
            Record parentRec = (Record) parentIt.next();
            RecordSet minitail = new RecordSet();
            minitail = recordSet.getSubSet(new RecordFilter(CoverageFields.COVERAGE_BASE_RECORD_ID,
                                           parentRec.getRowId()));
            Iterator minitailIt = minitail.getRecords();
            Integer i = 0;
            while (minitailIt.hasNext()) {
                Record minitailRec = (Record) minitailIt.next();
                i ++;
                Integer ratingBasis = minitailRec.getIntegerValue("minitailRatingBasis");
                if (ratingBasis == null ||
                    StringUtils.isBlank(ratingBasis.toString()) ||
                    !StringUtils.isNumeric(ratingBasis.toString(), true) ||
                    ratingBasis.intValue() < MIN_RATING || ratingBasis.intValue() > MAX_RATING ) {
                    MessageManager.getInstance().addErrorMessage("pm.processMinitail.ratingBasis.error",
                        new Object[] {String.valueOf(i),
                                      RiskFields.getRiskName(parentRec),
                                      CoverageFields.getCoverageDescription(parentRec)});
                }   
            }
        }
        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid mini tail data.");
        }
        l.exiting(getClass().getName(), "validateAllMinitail");
    }

    /**
     * first it checks if the mini tail is mandatory,if it is,it's never editable,if not,following rules
     * will be checked:
     * There are two rules here(refer to [GDR55.4]),if only one of them is met,return true
     * <br>Rule 2:
     * if the mini tail effective date matches the current transaction effective date.
     * If this matches, the fields are editable
     * <br>Rule 3:
     * if the mini tail effective date is less than the current term effective date, this could be a mini tail generated
     * as a result of prior acts data.  The business requires the ability to edit these within the first term.  To
     * determine if this is due to a prior acts piece of data the following functions are called:
     * Function PM_Dates.NB_Risk_StartDt,which returns priorActsEffectiveDate
     * Function  PM_GET_MIN_RISK_EFF_DATE ,which returns riskInceptionDate
     * If the mini tail is a prior acts mini tail, and the risk was created during the current term, then the
     * mini tail is editable (provided that it is not a mandatory mini tail).
     * <p/>
     *
     * @param policyHeader the policy header
     * @param miniTail     the mini tail records
     * @return return true if only one of them are met
     */
    public Record getMinitailEditable(PolicyHeader policyHeader, Record miniTail) {
        Logger l = LogUtils.enterLog(getClass(), "getMinitailEditable", new Object[]{policyHeader, miniTail});

        boolean mandatory = miniTail.getBooleanValue("mandatoryMinitailB").booleanValue();
        boolean flag = false;

        if (!mandatory) {
            Date transEffectiveDate, termEffectiveDate, termExpirationDate, miniTailEffectiveDate;
            DateFormat format = new SimpleDateFormat("mm/dd/yyyy", Locale.ENGLISH);

            try {
                transEffectiveDate = format.parse(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                termEffectiveDate = format.parse(policyHeader.getTermEffectiveFromDate());
                termExpirationDate = format.parse(policyHeader.getTermEffectiveToDate());
                miniTailEffectiveDate = format.parse(miniTail.getStringValue("effectiveFromDate"));
            }
            catch (ParseException e) {
                throw new AppException("Error Parsing Dates.");
            }

            // The transaction effective date is on the mini tail effective date
            if (transEffectiveDate.equals(miniTailEffectiveDate)) {
                flag = true;
            }
            else {
                Date priorActsEffectiveDate = getMinitailDAO().getRiskStartDate(miniTail);
                Date riskInceptionDate = getMinitailDAO().getMiniRiskEffectiveDate(miniTail);
                String termEffectiveDateStr = DateUtils.formatDate(termEffectiveDate);
                String riskInceptionDateStr = DateUtils.formatDate(riskInceptionDate);

                // It is a prior acts mini tail and the risk was created in the current term
                if ( miniTailEffectiveDate.after(priorActsEffectiveDate) && miniTailEffectiveDate.before(riskInceptionDate) &&
                     (termEffectiveDate.before(riskInceptionDate) || termEffectiveDateStr.equals(riskInceptionDateStr)) &&
                     termExpirationDate.after(riskInceptionDate) ) {
                    flag = true;
                }
            }
        }

        Record miniTailFlags = setMiniTailEditFlags(flag);

        l.exiting(getClass().getName(), "getMinitailEditable",new Object[]{miniTailFlags});

        return miniTailFlags;
    }

    private Record setMiniTailEditFlags(boolean flag) {
        Record rec = new Record();
        if (flag) {
            rec.setFieldValue("isApplyEditable", YesNoFlag.Y);
            rec.setFieldValue("isBasisEditable", YesNoFlag.Y);
        }
        else {
            rec.setFieldValue("isApplyEditable", YesNoFlag.N);
            rec.setFieldValue("isBasisEditable", YesNoFlag.N);
        }
        return rec;
    }

    /**
     * Load All free mini tail
     *
     * @param policyHeader
     * @return RecordSet
     */
    public RecordSet loadAllFreeMiniTail(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllFreeMiniTail", new Object[]{policyHeader});
        }

        RecordSet rs;
        rs = getMinitailDAO().loadAllFreeMiniTail(policyHeader.toRecord());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllFreeMiniTail", rs);
        }
        return rs;
    }

    /**
     * Check if free mini tail exist
     *
     * @param policyHeader
     * @return boolean
     */
    public boolean isFreeMiniTailExist(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isFreeMiniTailExist", new Object[]{policyHeader,});
        }
        boolean isExist = false;
        int rc = getMinitailDAO().checkFreeMiniTail(policyHeader.toRecord());
        if (rc > 0) {
            isExist = true;
        }
        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isFreeMiniTailExist", Boolean.valueOf(isExist));
        }
        return isExist;
    }

    public MinitailManagerImpl() {

    }

    /**
     * get current DAO
     * <p/>
     *
     * @return current DAO
     */
    public MinitailDAO getMinitailDAO() {
        return m_minitailDAO;
    }

    /**
     * set current DAO
     * <p/>
     *
     * @param minitailDAO mini tail DAO
     */
    public void setMinitailDAO(MinitailDAO minitailDAO) {
        m_minitailDAO = minitailDAO;
    }

    /**
     * verify config
     */
    public void verifyConfig() {
        if (getMinitailDAO() == null)
            throw new ConfigurationException("The required property 'minitailDAO' is missing.");
    }

    private MinitailDAO m_minitailDAO;
    public static final int MIN_RATING = 0;
    public static final int MAX_RATING = 100;
}
