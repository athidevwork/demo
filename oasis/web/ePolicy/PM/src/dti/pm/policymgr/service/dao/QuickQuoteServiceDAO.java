package dti.pm.policymgr.service.dao;

import dti.oasis.recordset.RecordSet;

/**
 * An interface to provide DAO operation for Quikc Quote Service.
 * </p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 15, 2018
 *
 * @author athi
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/15/2018       athi        194448 - Added methods for Quick Quote Service.
 * ---------------------------------------------------
 */
public interface QuickQuoteServiceDAO {
    /**
     * Logs web service request to log_ws table in the database.
     *      *
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
    RecordSet getCacheDefaults();

    /**
     * Inserts request to tables to rate for quick quote service
     *
     * @param xml
     * @return recordset
     */
    RecordSet insertRequestToDb(String xml);

    /**
     * validates risk type for a given policy type.
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
     * validates if a coverage code is auto and individual coverage
     *
     * @param coverageCode
     * @return Y|N
     */
    String validateIndividualCoverage(String coverageCode);

    /**
     * Validates the insured type, insured class combination.
     *
     * @param insuredTypeCode
     * @param insuredClassCode
     * @return Y|N
     */
    String validateInsuredClass(String insuredTypeCode, String insuredClassCode);

    /** Validates component for a coverage.
     *
     * @param componentCode
     * @param coverageCode
     * @return Y|N
     */
    String validateComponentForCoverage(String componentCode, String coverageCode, String effDate);

    /**
     * Validates limit for a coverage.
     *
     * @param coverageCode
     * @param limitTypeCode
     * @return Y|N
     */
    String validateLimitForCoverage(String coverageCode, String limitTypeCode, String effDate);

    /**
     * Validates policy, risk and coverage types combination.
     *
     * @param policyTypeCode
     * @param insuredTypeCode
     * @param coverageCode
     * @return Y|N
     */
    String validateCoverageForPolicyRiskType(String policyTypeCode, String insuredTypeCode, String coverageCode);

    /**
     * validate for postal code being valid.
     *
     * @param postalCode
     * @return Y|N
     */
    String validatePostalCode(String postalCode);

    /**
     * validates the postal code and state code combination.
     *
     * @param postalCode
     * @param stateCode
     * @return Y|N
     */
    String validatePostalState(String postalCode, String stateCode);

    /**
     * validate state and county combination.
     *
     * @param stateCode
     * @param countyCode
     * @param effDate
     * @return Y|N
     */
    String validateStateCounty(String stateCode, String countyCode, String postalCode, String effDate);

    /**
     * initiate a quick quote request.
     *
     * @param quoteId
     * @return success or failure
     */
    String performQuickQuote(String quoteId);

    /**
     * gets premium for quick quote.
     *
     * @param quoteId
     * @param recordType
     * @return
     */
    RecordSet getPremium(String quoteId, String recordType);

    /**
     * Get State for zip code
     *
     * @param postalCode
     * @return
     */
    RecordSet getDataForPostalCode(String postalCode);

    /**
     * Get County code given zip and state codes.
     *
     * @param postalCode
     * @param practiceStateOrProvinceCode
     * @return
     */
    String getDataForPostalAndState(String postalCode, String practiceStateOrProvinceCode);

    /**
     * Get state code given zip and county codes.
     *
     * @param postalCode
     * @param practiceCountyCode
     * @return
     */
    String getDataForPostalAndCounty(String postalCode, String practiceCountyCode);

    /**
     * Removes all quick quote data for a request.
     *
     * @param quickQuoteId
     */
    void removeQuickQuoteRequestData(String quickQuoteId);

    public static final String BEAN_NAME = "QuickQuoteServiceDAO";
}
