package dti.pm.pmdefaultmgr.dao;

import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 19, 2007
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/10/2007       sxm         1. Moved getDefaultState() from PolicyDAO
 *                              2. Moved getInitialDddwForRisk() from RiskDAO
 * 03/01/2010       fcb         1. 104191 - logic to get the mapped default values.
 * ---------------------------------------------------
 */
public interface PMDefaultDAO {

    /**
     * Load the PM Level Defaults, returning each default as a field in the resulting Record.
     */
    Record getDefaultLevel(Record input);

    /**
     * Load the PM Mapped Level Defaults, returning each default as a field in the resulting Record.
     */
    Record getMappedDefaultLevel(Record input);

    /**
     * Get default state code based on entity ID
     *
     * @param defaultLevel default level code
     * @param entityId     entity ID
     * @param asOfDate     as of date in mm/dd/yyyy format
     * @return String containing the default state code
     */
    String getDefaultState(String defaultLevel, String entityId, String asOfDate);

    /**
     * Get risk default DDDWs.
     *
     * @param termEffectiveFromdate  Term effective from date.
     * @param transEffectiveFromDate Transaction effective from date.
     * @param policyTypeCode         Policy type code.
     * @param riskTypeCode           Risk type code.
     * @param practiceStateCode      Risk paractice state code.
     * @return String that contains DDDW field ID list.
     */
    String getInitialDddwForRisk(String termEffectiveFromdate, String transEffectiveFromDate,
                                 String policyTypeCode, String riskTypeCode, String practiceStateCode);

    /**
     * Get mapping code values from primary risk to others.
     *
     * @param inputRecord Record containing the mapped level as well as policy, risk, coverage details
     * @return String comma-delimited string containing all parent mapping code values
     */
    String getMappedDefaultValues(Record inputRecord);

    /**
     * Get default value for relationship type or to rate.
     *
     * @param inputRecord Record containing the expected values
     * @return String of the default value
     */
    String getDefaultValue(Record inputRecord);
}
