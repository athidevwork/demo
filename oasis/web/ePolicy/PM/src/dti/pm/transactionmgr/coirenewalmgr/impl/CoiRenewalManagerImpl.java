package dti.pm.transactionmgr.coirenewalmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.transactionmgr.coirenewalmgr.CoiRenewalManager;
import dti.pm.transactionmgr.coirenewalmgr.dao.CoiRenewalDAO;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this class is an implements class for CoiRenewalProcessManger interface
 * for coi renewal manager
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 23, 2010
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/05/2010       dzhang      Rename this file & methods name.
 * 07/06/2010       dzhang      Change method validateForCreateCoiRenewal().
 * 03/23/2011       sxm         Issue 117872 - fixed start/end date validation error
 * ---------------------------------------------------
 */

public class CoiRenewalManagerImpl implements CoiRenewalManager {

    /**
     * save all the COI renewal data
     * <p/>
     *
     * @param inputRecord COI renewal data that needed to save
     */
    public void createCoiRenewal(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "createCoiRenewal", new Object[]{inputRecord});
        }

        //Set the procedure parameter i_type to 'SUBMIT' 
        inputRecord.setFieldValue("type", SUBMIT);
        //validate search criteria
        validateForCreateCoiRenewal(inputRecord);
        getCoiRenewalDAO().createCoiRenewal(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "createCoiRenewal");
        }
    }

    /**
     * do validation before create Coi renewal
     *
     * @param record input record for Coi renewal
     */
    protected void validateForCreateCoiRenewal(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForCreateCoiRenewal", new Object[]{record});
        }

        Date effDate = record.getDateValue("effDate");
        Date expDate = record.getDateValue("expDate");
        if (effDate.after(expDate)) {
            MessageManager.getInstance().addErrorMessage("pm.createCoiRenewal.endDate.prior.startDate.error");
        }

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid COI renewal data.");
        }
        l.exiting(getClass().getName(), "validateForCreateCoiRenewal");
    }

    /**
     * Get the initial values for COI event search criteria
     * <p/>
     *
     * @return the result met the condition
     */
    public Record getInitialValuesForSearchCriteria() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForSearchCriteria");
        }

        Record result = new Record();
        result.setFieldValue("termTypeFilter", COMMON);
        result.setFieldValue("submittedByFilter", "ALL");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForSearchCriteria");
        }
        return result;
    }

    /**
     * To load all COI renewal event data.
     * <p/>
     *
     * @param inputRecord input record with search criteria.
     * @return a record set met the condition.
     */
    public RecordSet loadAllCoiRenewalEvent(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoiRenewalEvent", new Object[]{inputRecord});
        }
        RecordSet rs;

        // Validate the search data.
        validateForSearchCoiRenewalEvent(inputRecord);

        rs = getCoiRenewalDAO().loadAllCoiRenewalEvent(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoiRenewalEvent", rs);
        }
        return rs;
    }

    /**
     * validate search criteria when loading COI renewal event data
     *
     * @param record record with search criteria
     */
    protected void validateForSearchCoiRenewalEvent(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForSearchCoiRenewalEvent", new Object[]{record});
        }

        // Validation #1: Invalid Search Criteria - NULL Start Search or End Search
        String startSearchDateStr = record.getStringValue("startSearchDateFilter");
        String endSearchDateStr = record.getStringValue("endSearchDateFilter");

        if (StringUtils.isBlank(startSearchDateStr) || StringUtils.isBlank(endSearchDateStr)) {
            MessageManager.getInstance().addErrorMessage("pm.coiRenewal.dates.null.error");
        }
        else {
            // Validation #2: Invalid Search Criteria - End Date Prior to Start Date
            Date startSearchDate = DateUtils.parseDate(startSearchDateStr);
            Date endSearchDate = DateUtils.parseDate(endSearchDateStr);
            if (startSearchDate.after(endSearchDate)) {
                MessageManager.getInstance().addErrorMessage("pm.coiRenewal.endDate.prior.startDate.error");
            }
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid COI Renewal Search Criteria.");
        }

        l.exiting(getClass().getName(), "validateForSearchCoiRenewalEvent");
    }

    /**
     * To load all COI renewal event detail data.
     * <p/>
     *
     * @param inputRecord input record with search criteria.
     * @return a record set met the condition.
     */
    public RecordSet loadAllCoiRenewalEventDetail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoiRenewalEventDetail", new Object[]{inputRecord});
        }

        // Load COI renewal event detail data
        RecordSet rs = getCoiRenewalDAO().loadAllCoiRenewalEventDetail(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoiRenewalEventDetail", rs);
        }
        return rs;
    }

    /**
     * get current DAO
     * <p/>
     *
     * @return current DAO
     */
    public CoiRenewalDAO getCoiRenewalDAO() {
        return m_coiRenewalDAO;
    }

    /**
     * set current DAO
     * <p/>
     *
     * @param coiRenewalDAO Coi Renewal DAO
     */
    public void setCoiRenewalDAO(CoiRenewalDAO coiRenewalDAO) {
        m_coiRenewalDAO = coiRenewalDAO;
    }

    /**
     * verify config
     */
    public void verifyConfig() {
        if (getCoiRenewalDAO() == null)
            throw new ConfigurationException("The required property 'getCoiRenewalDAO' is missing.");
    }

    public CoiRenewalManagerImpl() {
    }

    private CoiRenewalDAO m_coiRenewalDAO;
    protected static final String COMMON = "COMMON";
    protected static final String SUBMIT = "SUBMIT";
}
