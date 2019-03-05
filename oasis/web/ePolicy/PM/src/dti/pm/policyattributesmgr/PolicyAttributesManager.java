package dti.pm.policyattributesmgr;

import dti.oasis.recordset.Record;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.PolicyStatus;
import dti.pm.busobjs.QuoteCycleCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionTypeCode;
import dti.pm.policymgr.PolicyHeader;

import java.util.List;

/**
 * Interface to handle Implementation of PmAttribute operation.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  July 1, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/26/16 wdang   167534 - Initial version.
 * 04/02/18 tzeng   192229 - Added isAddtlExposureAvailable.
 * ---------------------------------------------------
 */
public interface PolicyAttributesManager {

    public static final String BEAN_NAME = "PolicyAttributesManager";

    /**
     * load PM Attribute by specified type code.
     * @param typeCode
     * @return list of PmAttribute
     */
    public List<PmAttributeBean> loadPmAttribute(String typeCode);

    /**
     * Check if current phase is valid to be added to batch renewal.
     * @param policyHeader
     * @return
     */
    public boolean isPhaseValidToBatch(PolicyHeader policyHeader);

    /**
     * Check if prompt is enable after save official for renewal batch.
     * @param policyHeader
     * @return
     */
    public boolean isSaveOfficialPromptEnableForRenewalBatch(PolicyHeader policyHeader);

    /**
     * Check if prompt is enable after auto renew for renewal batch.
     * @param policyHeader
     * @return
     */
    public boolean isAutoRenewPromptEnableForRenewalBatch(PolicyHeader policyHeader);

    /**
     * Check if Copy Deny Accept Quote Menu is disable.
     * @param effectiveDate
     * @param policyTypeCode
     * @param quoteCycleCode
     * @return
     */

    public boolean isCopyDenyAcceptQuoteMenuDisable (String effectiveDate, String policyTypeCode, QuoteCycleCode quoteCycleCode);

    /**
     * Check if Copy Deny Accept Quote Button is disable.
     * @param effectiveDate
     * @param policyTypeCode
     * @param quoteCycleCode
     * @return
     */
    public boolean isCopyDenyAcceptQuoteButtonDisable (String effectiveDate,
                                                       String policyTypeCode,
                                                       QuoteCycleCode quoteCycleCode);

    /**
     * Check if system displays policy/quote versions in view mode drop down list.
     * @param effectiveDate
     * @param policyCycleCode
     * @param quoteCycleCode
     * @param transactionTypeCode
     * @param recordModeCode
     * @param policyStatus
     * @return
     */
    public boolean isDisplayQuoteInViewModeEnable (String effectiveDate,
                                                   PolicyCycleCode policyCycleCode,
                                                   QuoteCycleCode quoteCycleCode,
                                                   TransactionTypeCode transactionTypeCode,
                                                   RecordMode recordModeCode,
                                                   PolicyStatus policyStatus);

    /**
     * Return formatted text for each policy/quote versions in view mode drop down list.
     * @param effectiveDate
     * @param policyCycleCode
     * @param quoteCycleCode
     * @param inputRecord
     * @return
     */
    public String getDisplayQuoteInViewModeText (String effectiveDate,
                                                 PolicyCycleCode policyCycleCode,
                                                 QuoteCycleCode quoteCycleCode,
                                                 Record inputRecord);

    /**
     * Return if system will auto delete/backup/restore/merge the renewal WIP
     * when processing endorse/OOSE/Cancel risk/coverage.
     * @param effectiveDate
     * @param currentTransCode
     * @param renewalTransCode
     * @return
     */
    public boolean isAutoPendingRenewalEnable (String effectiveDate,
                                               TransactionCode currentTransCode,
                                               TransactionCode renewalTransCode);

    /**
     * Check if the risk exposure navigation item is available.
     * @param riskType
     * @param effectiveDate
     * @return
     */
    public boolean isAddtlExposureAvailable (String policyType,
                                             String riskType,
                                             String effectiveDate);
}
