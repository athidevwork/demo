package dti.pm.policymgr.service;

import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestParametersType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryResultType;
import com.delphi_tech.ows.quickquoteservice.QuickQuoteRequestType;
import com.delphi_tech.ows.quickquoteservice.QuickQuoteResultType;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.service.cache.QuickQuoteServiceDefaultsCache;
import dti.pm.policymgr.service.impl.PolicyInquiryItemResultMT;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/24/12
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/01/2018       athi       issue #194448 Added Quick Quote Web Service.
 * ---------------------------------------------------
 */

public interface QuickQuoteServiceManager {
    public QuickQuoteResultType getQuickQuote(QuickQuoteRequestType quickQuoteRequest);

    /**
     * Logs web service request to log_ws table in the database.
     *
     * @param logType
     * @param xml
     * @param msgCode
     */
    void logXml(String logType, String xml, String msgCode);

    /**
     * Retrieves the default values from pm_default_values.
     *
     * @return recordSet
     */
    QuickQuoteServiceDefaultsCache getDefaultsCache(String startDate, String endDate);

    /**
     * Inserts request to tables to rate for quick quote service
     *
     * @param xml
     * @return quoteId
     */
    RecordSet insertRequestToDb(String xml);

    /**
     * validate risk type if consistent for policy type.
     *
     * @param policyTypeCode
     * @param riskTypeCode
     * @return Y|N
     */
    String validateRiskType(String policyTypeCode, String riskTypeCode);

    /**
     * validates if a coverage code is configured for a given policy and risk type.
     *
     * @param policyTypeCode
     * @param riskTypeCode
     * @param coverageCode
     * @return Y|N
     */
    String validateCoverageType(String policyTypeCode, String riskTypeCode, String coverageCode);

    /**
     * validates if a coverage is automatic individual coverage
     * @param coverageCode
     * @return
     */
    String validateIndividualCoverage(String coverageCode);

    /**
     * Validates for a correct post code.
     *
     * @param postalCode
     * @return Y|N
     */
    String validatePostalCode(String postalCode);

    /**
     * inititate a quick quote request.
     *
     * @param quoteId
     * @return success or failure
     */
    String performQuickQuote(String quoteId);

    /**
     * get premium for quick quote request.
     * @param quoteId
     * @param recordType
     * @return
     */
    RecordSet getPremium(String quoteId, String recordType);

    /**
     *  Removes all quick quote request data.
     *
     * @param quickQuoteId
     */
    void removeQuickQuoteRequestData(String quickQuoteId);
}
