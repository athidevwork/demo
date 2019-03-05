package dti.pm.pmdefaultmgr;

import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

/**
 * The PMDefaultManager provides access to the PM Default configuration data.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 19, 2007
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/10/2007       sxm         Added getDefaultState() and getInitialDddwForRisk().
 * 03/01/2010       fcb         104191: transactionLogId added to getDefaultLevel.
 * ---------------------------------------------------
 */
public interface PMDefaultManager {
    /**
     * Load the PM Level Defaults, returning each default as a field in the resulting Record.
     *
     * @param level The default level code.
     * @param transactionLogId The latest transaction on the policy.
     * @param termEffectiveFromdate the term effective from date
     * @param transEffectiveFromDate the transaction effective from date
     * @param code1 the first code name
     * @param value1 the value for the first code
     * @param code2 the second code name
     * @param value2 the value for the second code
     * @param code3 the third code name
     * @param value3 the value for the third code
     * @return a Record containing all defaults for the given input.
     */
    Record getDefaultLevel(String level,
                           String transactionLogId,
                           String termEffectiveFromdate, 
                           String transEffectiveFromDate,
                           String code1, String value1,
                           String code2, String value2,
                           String code3, String value3);

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
     * Get Map from codes for default coverage, class, and components.
     *
     * @param mappedLevel  Level at which the defaults are being determined
     * @param policyHeader Instance of the policy header with summary policy, risk, coverage details
     * @param inputRecord  Input record for which the mapped value needs to be set
     */
    void processMappedDefaults(String mappedLevel, PolicyHeader policyHeader, Record inputRecord);

    /**
     * Get default value for relationship type or to rate.
     *
     * @param inputRecord Input record for which the mapped value needs to be set
     * @return String of the default value
     */
    String getDefaultValue(Record inputRecord);
}
