package dti.pm.pmdefaultmgr.impl;

import dti.pm.pmdefaultmgr.PMDefaultManager;
import dti.pm.pmdefaultmgr.dao.PMDefaultDAO;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionFields;
import dti.oasis.recordset.Record;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.StringUtils;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.StringTokenizer;

/**
 * The PMDefaultManager provides access to the PM Default configuration data.
 * <p/>
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
 *                                      Added logic to process mapped defaults.
 * 04/19/2010       fcb         105849: getDefaultLevel(): bug fixed.
 * 08/17/2010       fcb         97217:  getDefaultLevel(): added support for defaulting
 *                                      multiselect lists with comma separated list.
 * ---------------------------------------------------
 */
public class PMDefaultManagerImpl implements PMDefaultManager {

    /**
     * Load the PM Level Defaults, returning each default as a separate field in the resulting Record.
     *
     * @param level The default level code.
     * @param transactionLogId the transaction log id
     * @param termEffectiveFromdate the term effective from date
     * @param transEffectiveFromDate the transaction effective from date
     * @param code1 the first code name
     * @param value1 the value for the first code
     * @param code2 the second code name
     * @param value2 the value for the second code
     * @param code3 the third code name
     * @param value3 the value for the third code
     * @return a Record containing all defaults for the given input as separate fields.
     */
    public Record getDefaultLevel(String level,
                                  String transactionLogId,
                                  String termEffectiveFromdate,
                                  String transEffectiveFromDate,
                                  String code1, String value1,
                                  String code2, String value2,
                                  String code3, String value3) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultLevel", new Object[]{level, termEffectiveFromdate, transEffectiveFromDate, code1, value1, code2, value2, code3, value3});
        }

        Record input = new Record();
        DefaultLevelFields.setLevel(input, level);
        PolicyHeaderFields.setTermEffectiveFromDate(input, termEffectiveFromdate);
        TransactionFields.setTransactionEffectiveFromDate(input, transEffectiveFromDate);
        TransactionFields.setTransactionLogId(input, transactionLogId);
        DefaultLevelFields.setCode1(input, code1);
        DefaultLevelFields.setValue1(input, value1);
        DefaultLevelFields.setCode2(input, code2);
        DefaultLevelFields.setValue2(input, value2);
        DefaultLevelFields.setCode3(input, code3);
        DefaultLevelFields.setValue3(input, value3);
        DefaultLevelFields.setWebFieldIdB(input, YesNoFlag.Y);

        Record fieldValues = getPmDefaultDAO().getDefaultLevel(input);
        Record result = new Record();
        if (fieldValues.hasStringValue(DefaultLevelFields.COLUMN_LIST) &&
            fieldValues.hasStringValue(DefaultLevelFields.VALUE_LIST)) {
            String fields = fieldValues.getStringValue(DefaultLevelFields.COLUMN_LIST);
            String values = fieldValues.getStringValue(DefaultLevelFields.VALUE_LIST);
            StringTokenizer fieldToken = new StringTokenizer(fields, ",");
            StringTokenizer valueToken = new StringTokenizer(values, ",");
            while (fieldToken.hasMoreTokens()) {
                String token = valueToken.nextToken();
                // For multiselect list support, we replace "|;|" separator back to ","
                if (token!=null && token.indexOf("|;|")>0) {
                    token = token.replaceAll("\\|;\\|",",");
                }
                result.setFieldValue(fieldToken.nextToken(), token);
            }
        }

        if (fieldValues.hasStringValue(DefaultLevelFields.MAP_COLUMN_LIST) &&
            fieldValues.hasStringValue(DefaultLevelFields.MAP_VALUE_LIST)) {
            String mapColumnList = fieldValues.getStringValue(DefaultLevelFields.MAP_COLUMN_LIST);
            String mapValueList = fieldValues.getStringValue(DefaultLevelFields.MAP_VALUE_LIST);
            if (mapColumnList.length()>0 && mapValueList.length()>0) {
                Record mappedFieldValues = getPmDefaultDAO().getMappedDefaultLevel(input);
                Record mappedResult = new Record();
                if (mappedFieldValues.hasStringValue(DefaultLevelFields.COLUMN_LIST) &&
                    mappedFieldValues.hasStringValue(DefaultLevelFields.VALUE_LIST)) {
                    String fields = mappedFieldValues.getStringValue(DefaultLevelFields.COLUMN_LIST);
                    String values = mappedFieldValues.getStringValue(DefaultLevelFields.VALUE_LIST);
                    if (fields.length()>0 && values.length()>0 ) {
                        String[] fieldList = fields.split(",");
                        String[] valueList = values.split(",");
                        for(int i=0 ;i<fieldList.length && i<valueList.length; i++) {
                            String fieldName = fieldList[i];
                            String value = valueList[i];
                             if (!StringUtils.isBlank(fieldName) && !StringUtils.isBlank(value)) {
                                mappedResult.setFieldValue(fieldName, value);
                             }
                        }
                        result.setFields(mappedResult, true);
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultLevel", result);
        }
        return result;
    }

    /**
     * Get default state code based on entity ID
     *
     * @param defaultLevel default level code
     * @param entityId     entity ID
     * @param asOfDate     as of date in mm/dd/yyyy format
     * @return String containing the default state code
     */
    public String getDefaultState(String defaultLevel, String entityId, String asOfDate) {
        return(getPmDefaultDAO().getDefaultState(defaultLevel, entityId, asOfDate));
    }

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
    public String getInitialDddwForRisk(String termEffectiveFromdate, String transEffectiveFromDate,
                                 String policyTypeCode, String riskTypeCode, String practiceStateCode) {
        return(getPmDefaultDAO().getInitialDddwForRisk(termEffectiveFromdate, transEffectiveFromDate,
            policyTypeCode, riskTypeCode, practiceStateCode));
    }


    /**
     * Get Map from codes for default coverage, class, and components.
     *
     * @param mappedLevel  Level at which the defaults are being determined
     * @param policyHeader Instance of the policy header with summary policy, risk, coverage details
     * @param inputRecord  Input record for which the mapped value needs to be set
     */
    public void processMappedDefaults(String mappedLevel, PolicyHeader policyHeader, Record inputRecord) {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processMappedDefaults", new Object[]{mappedLevel, policyHeader, inputRecord});
        }

        String mappedDefaultCodes = null;

        /* Check system parameter for mapped default rule */
        YesNoFlag mappedParameter = YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("PM_ADD_MAPPED_DFLTS", "N"));

        /* If mappedParameter and the current risk is not the primary risk, then determine the mapped code */
        if (mappedParameter.booleanValue() && !policyHeader.getRiskHeader().getPrimaryRiskB().booleanValue()) {

            Record rec = new Record();
            rec.setFieldValue("mappedLevel", mappedLevel);
            rec.setFields(policyHeader.toRecord(), false);

            mappedDefaultCodes = getPmDefaultDAO().getMappedDefaultValues(rec);

            // Add mapping information to the input record
            inputRecord.setFieldValue("chkMapDefFrom", mappedDefaultCodes);
        }

        l.exiting(getClass().getName(), "processMappedDefaults");
    }

    /**
     * Get default value for relationship type or to rate.
     *
     * @param inputRecord Input record for which the mapped value needs to be set
     * @return String of the default value
     */
    public String getDefaultValue(Record inputRecord) {
        return getPmDefaultDAO().getDefaultValue(inputRecord);
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getPmDefaultDAO() == null)
            throw new ConfigurationException("The required property 'pmDefaultDAO' is missing.");
    }

    public PMDefaultManagerImpl() {
    }


    public PMDefaultDAO getPmDefaultDAO() {
        return m_pmDefaultDAO;
    }

    public void setPmDefaultDAO(PMDefaultDAO pmDefaultDAO) {
        m_pmDefaultDAO = pmDefaultDAO;
    }

    private PMDefaultDAO m_pmDefaultDAO;
}
