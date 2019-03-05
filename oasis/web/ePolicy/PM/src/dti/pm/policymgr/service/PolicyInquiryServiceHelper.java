package dti.pm.policymgr.service;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

import java.util.List;

/**
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   02/24/2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/24/2016       wdang       169197 - Initial version.
 * 01/09/2017       tzeng       166929 - Added getLatestTerm.
 * 02/06/2017       lzhang      190834 - 1) Modified loadPolicyInformation/buildPolicyHeader:
 *                                       add transactionStatusCode parameter
 *                                       2) Added validatePolicyNosExist and validateTermBaseRecordIdsExist.
 * 04/12/2018       lzhang      191379 - Added loadPolicyHeaderForWS.
 * 11/28/2018       eyin        197179 - Added loadPolicyDetailList().
 * ---------------------------------------------------
 */
public interface PolicyInquiryServiceHelper {

    /**
     * Retrieve Policy Term list via input Record.
     * @param inputRecord
     * @return String[0]: termBaseRecordId, String[1]: policyNO
     */
    public List<String[]> getTermPolicyList(Record inputRecord);

    /**
     * Retrieve Policy Information via policyNo and termBaseRecordId
     * @param policyNo
     * @param termBaseRecordId
     * @return Record of policy information
     */
    public Record loadPolicyInformation(String policyNo, String termBaseRecordId, String transactionStatusCode);

    /**
     * Retrieve Policy Detail List via policyNo and termBaseRecordId
     * @param policyNo
     * @param termBaseRecordId
     * @return RecordSet of policy detail list
     */
    public RecordSet loadPolicyDetailList(String policyNo, String termBaseRecordId, String transactionStatusCode);

    /**
     * Retrieve Policy Term Information List via policyId
     * @param policyId
     * @return RecordSet of policy term information
     */
    public RecordSet loadPolicyTermList(String policyId);
    /**
     * Build policy header via input record
     * @param policyRecord
     * @return policy header
     */
    public PolicyHeader buildPolicyHeader(PolicyHeader policyHeader, Record policyRecord);

    /**
     * Retrieve latest term by policy no.
     * @param inputRecord
     * @return String[0]: termBaseRecordId, String[1]: policyNO, String[2]: policyPk
     */
    public String[] getLatestTerm(Record inputRecord);

    /**
     * Identify whether policyNos exist in system
     * <p/>
     *
     * @param policyNos
     * @return invalid policyNo
     */
    public String validatePolicyNosExist(String policyNos);

    /**
     * Identify whether termBaseRecordIds exist in system
     * <p/>
     *
     * @param termBaseRecordIds
     * @return invalid termBaseRecordIds
     */
    public String validateTermBaseRecordIdsExist(String termBaseRecordIds);
    /**
     * load policy header
     * <p/>
     *
     * @param policyNo
     * @param termBaseRecordId
     * @param transactionStatusCode
     * @return policyHeader
     */
    public PolicyHeader loadPolicyHeader(String policyNo, String termBaseRecordId, String transactionStatusCode);
}


