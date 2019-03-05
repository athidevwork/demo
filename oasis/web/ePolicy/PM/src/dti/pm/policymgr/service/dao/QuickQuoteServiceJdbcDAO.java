package dti.pm.policymgr.service.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details of all DAO operations that are performed against the quick quote service manager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 15, 2018
 *
 * @author athi
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/15/2018       athi        194448 - Added methods to support Quick Quote Service.
 * ---------------------------------------------------
 */

public class QuickQuoteServiceJdbcDAO extends BaseDAO implements QuickQuoteServiceDAO {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public void logXml(String logType, String xml, String msgCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "logXml", new Object[]{logType, xml});
        }

        Record input = new Record();
        input.setFieldValue("xml", xml);
        if (logType.equalsIgnoreCase("request")) {
            input.setFieldValue("type", "REQUEST");
            input.setFieldValue("status", "");
        }
        else {
            input.setFieldValue("type", "RESPONSE");
            input.setFieldValue("status", msgCode);
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.WS_Log");
            spDao.execute(input);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to insert web services xml data into table", e);
            l.throwing(getClass().getName(), "logXml", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "logXml");
        }
    }

    @Override
    public RecordSet getCacheDefaults() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCacheDefaults", new Object[]{});
        }

        Record inputRecord = new Record();

        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.Get_Cache_Defaults");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get policy defaults", e);
            l.throwing(getClass().getName(), "getCacheDefaults", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCacheDefaults");
        }
        return rs;
    }

    @Override
    public RecordSet insertRequestToDb(String xml) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "insertRequestToDb", new Object[]{});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("xml", xml);
        RecordSet rs = null;
        //String quoteId = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.Insert_Request_To_Db");
            rs = spDao.execute(inputRecord);
            /*Record summary = rs.getSummaryRecord();
            quoteId = summary.getStringValue("quoteId");*/
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to insert request to db", e);
            l.throwing(getClass().getName(), "insertRequestToDb", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "insertRequestToDb");
        }
        return rs;
    }

    @Override
    public String validateRiskType(String policyTypeCode, String riskTypeCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateRiskType", new Object[]{policyTypeCode, riskTypeCode});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("policyTypeCode", policyTypeCode);
        inputRecord.setFieldValue("riskTypeCode", riskTypeCode);
        String result = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.validate_Risk_Type");
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate risk type", e);
            l.throwing(getClass().getName(), "validateRiskType", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateRiskType",  new Object[]{result});
        }
        return result;
    }

    @Override
    public String validateCoverageType(String policyTypeCode, String riskTypeCode, String coverageCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCoverageType", new Object[]{policyTypeCode, riskTypeCode, coverageCode});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("policyTypeCode", policyTypeCode);
        inputRecord.setFieldValue("riskTypeCode", riskTypeCode);
        inputRecord.setFieldValue("coverageCode", coverageCode);
        String result = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.validate_covg_type");
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate coverage type", e);
            l.throwing(getClass().getName(), "validateCoverageType", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCoverageType",  new Object[]{result});
        }
        return result;
    }

    @Override
    public String validateIndividualCoverage(String coverageCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateIndividualCoverage", new Object[]{coverageCode});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("coverageCode", coverageCode);
        String result = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.validate_auto_covg");
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate auto individual coverage", e);
            l.throwing(getClass().getName(), "validateIndividualCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateIndividualCoverage",  new Object[]{result});
        }
        return result;
    }

    @Override
    public String validateInsuredClass(String insuredTypeCode, String insuredClassCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateInsuredClass", new Object[]{insuredTypeCode, insuredClassCode});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("insrTypeCode", insuredTypeCode);
        inputRecord.setFieldValue("insrClassCode", insuredClassCode);
        String result = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.validate_insured_class");
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate insured class", e);
            l.throwing(getClass().getName(), "validateInsuredClass", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateInsuredClass",  new Object[]{result});
        }
        return result;
    }

    @Override
    public String validateComponentForCoverage(String componentCode, String coverageCode, String effDate) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateComponentForCoverage", new Object[]{componentCode, coverageCode, effDate});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("compCode", componentCode);
        inputRecord.setFieldValue("covgCode", coverageCode);
        inputRecord.setFieldValue("effDate", effDate);
        String result = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.validate_component_covg");
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate component for a coverage", e);
            l.throwing(getClass().getName(), "validateComponentForCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateComponentForCoverage",  new Object[]{result});
        }
        return result;
    }

    @Override
    public String validateLimitForCoverage(String coverageCode, String limitTypeCode, String effDate) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateLimitForCoverage", new Object[]{coverageCode, limitTypeCode, effDate});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("limitCode", limitTypeCode);
        inputRecord.setFieldValue("coverageCode", coverageCode);
        inputRecord.setFieldValue("effDate", effDate);
        String result = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.validate_limit_covg");
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate limit for a coverage", e);
            l.throwing(getClass().getName(), "validateLimitForCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateLimitForCoverage",  new Object[]{result});
        }
        return result;
    }

    @Override
    public String validateCoverageForPolicyRiskType(String policyTypeCode, String insuredTypeCode, String coverageCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCoverageForPolicyRiskType", new Object[]{policyTypeCode, insuredTypeCode, coverageCode});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("polTypeCode", policyTypeCode);
        inputRecord.setFieldValue("insrTypeCode", insuredTypeCode);
        inputRecord.setFieldValue("covgCode", coverageCode);
        String result = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.validate_covg_pol_risk");
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate coverage for policy and risk types", e);
            l.throwing(getClass().getName(), "validateCoverageForPolicyRiskType", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCoverageForPolicyRiskType",  new Object[]{result});
        }
        return result;
    }

    @Override
    public String validatePostalCode(String postalCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePostalCode", new Object[]{postalCode});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("postalCode", postalCode);
        String result = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.validate_postal_code");
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate postal code", e);
            l.throwing(getClass().getName(), "validatePostalCode", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePostalCode",  new Object[]{result});
        }
        return result;
    }

    @Override
    public String validatePostalState(String postalCode, String stateCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePostalState", new Object[]{postalCode, stateCode});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("postalCode", postalCode);
        inputRecord.setFieldValue("stateCode", stateCode);
        String result = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.validate_postal_state");
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate postal state code combination", e);
            l.throwing(getClass().getName(), "validatePostalState", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePostalState",  new Object[]{result});
        }
        return result;
    }

    @Override
    public String validateStateCounty(String stateCode, String countyCode, String postalCode, String effDate) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateStateCounty", new Object[]{stateCode, countyCode, postalCode, effDate});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("stateCode", stateCode);
        inputRecord.setFieldValue("countyCode", countyCode);
        inputRecord.setFieldValue("postalCode", postalCode);
        inputRecord.setFieldValue("effDate", effDate);
        String result = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.validate_state_county");
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate state/county code combination", e);
            l.throwing(getClass().getName(), "validateStateCounty", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateStateCounty",  new Object[]{result});
        }
        return result;
    }

    @Override
    public String performQuickQuote(String quoteId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performQuickQuote", new Object[]{});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("quickQuoteId", quoteId);

        RecordSet rs = null;
        String output = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("RT_Qq.Main");
            rs = spDao.execute(inputRecord);
            output = rs.getSummaryRecord().getStringValue("rc");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to initiate a quick quote request", e);
            l.throwing(getClass().getName(), "performQuickQuote", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performQuickQuote");
        }
        return output;
    }

    @Override
    public RecordSet getPremium(String quoteId, String recordType) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPremium", new Object[]{quoteId, recordType});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("quickQuoteId", quoteId);
        inputRecord.setFieldValue("recordType", recordType);

        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.Get_Premium");
            rs = spDao.execute(inputRecord);
            //output = rs.getSummaryRecord().getStringValue("rc");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get premium for quick quote", e);
            l.throwing(getClass().getName(), "getPremium", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPremium");
        }
        return rs;
    }

    @Override
    public RecordSet getDataForPostalCode(String postalCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDataForPostalCode", new Object[]{postalCode});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("postalCode", postalCode);

        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.Get_Data_For_Postal_Code");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get state for postal code", e);
            l.throwing(getClass().getName(), "getDataForPostalCode", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDataForPostalCode");
        }
        return rs;
    }

    @Override
    public String getDataForPostalAndState(String postalCode, String practiceStateOrProvinceCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDataForPostalAndState", new Object[]{postalCode, practiceStateOrProvinceCode});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("postalCode", postalCode);
        inputRecord.setFieldValue("stateCode", practiceStateOrProvinceCode);

        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.Get_Data_For_Postal_State_Codes");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get state " + practiceStateOrProvinceCode + " for postal code " + postalCode, e);
            l.throwing(getClass().getName(), "getDataForPostalAndState", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDataForPostalAndState");
        }
        return rs.getSummaryRecord().getStringValue("countyCode");
    }

    @Override
    public String getDataForPostalAndCounty(String postalCode, String practiceCountyCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDataForPostalAndCounty", new Object[]{postalCode, practiceCountyCode});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("postalCode", postalCode);
        inputRecord.setFieldValue("countyCode", practiceCountyCode);

        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.Get_Data_For_Postal_County_Codes");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get state for postal and county codes", e);
            l.throwing(getClass().getName(), "getDataForPostalAndCounty", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDataForPostalAndCounty");
        }
        return rs.getSummaryRecord().getStringValue("stateCode");
    }

    @Override
    public void removeQuickQuoteRequestData(String quickQuoteId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "removeQuickQuoteRequestData", new Object[]{quickQuoteId});
        }

        Record input = new Record();
        input.setFieldValue("quickQuoteId", quickQuoteId);

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ows_Quick_Quote.Remove_QQ_Request_Data");
            spDao.execute(input);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to remove quick quote request data", e);
            l.throwing(getClass().getName(), "removeQuickQuoteRequestData", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "removeQuickQuoteRequestData");
        }
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public QuickQuoteServiceJdbcDAO() {
    }
}
