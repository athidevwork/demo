package dti.pm.riskmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.StringUtils;
import dti.oasis.util.CollectionUtils;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskCopyFields;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Load Processor for Risk Copy All UC, to add default attributes for source risk
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 27, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/07/2010       dzhang      #108261 - Modified constructor method RiskCopyAllRecordLoadProcessor() to add parameter riskFormFields.
 * 07/17/2017       wrong       168374  - Modify postProcessRecordSet to add logic for pcf county and pcf specialty
 *                                        entitlements.
 * 07/06/2018       xnie        187070 - Modified postProcessRecordSet() to set isGr1CompAvailable.
 * ---------------------------------------------------
 */
public class RiskCopyAllRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        String sourceRiskId = getPolicyHeader().getRiskHeader().getRiskId();
        //filter out the source risk
        if (sourceRiskId.equals(RiskFields.getRiskId(record))) {
            return false;
        }

        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Record record = recordSet.getSummaryRecord();

        //iterate through the m_nameList and set indicator's default value to N
        for (int i = 0; i < getNameList().size(); i++) {
            record.setFieldValue((String) getNameList().get(i), YesNoFlag.N);
        }

        record.setFieldValue("coiSelectedB", YesNoFlag.N);
        record.setFieldValue("affiliationSelectedB", YesNoFlag.N);
        record.setFieldValue("scheduleSelectedB", YesNoFlag.N);

        //set page entitlement fields
        record.setFieldValue("isSpecialtyAvailable", YesNoFlag.N);
        record.setFieldValue("isPcfCountyAvailable", YesNoFlag.N);
        record.setFieldValue("isPcfSpecialtyAvailable", YesNoFlag.N);
        record.setFieldValue("isGr1CompAvailable", YesNoFlag.N);
        if (Integer.parseInt(RiskCopyFields.getFromRiskClassCount(getInputRecord())) > 0) {
            record.setFieldValue("isSpecialtyAvailable", YesNoFlag.Y);
        }
        record.setFieldValue("isNetworkAvailable", YesNoFlag.N);
        if (getInputRecord().hasStringValue(RiskFields.RISK_SOCIETY_ID)) {
            record.setFieldValue("isNetworkAvailable", YesNoFlag.Y);
        }
        if (getInputRecord().getStringValue(RiskCopyFields.IS_FUND_STATE).equals("Y")) {
            record.setFieldValue("isPcfCountyAvailable", YesNoFlag.Y);
            record.setFieldValue("isPcfSpecialtyAvailable", YesNoFlag.Y);
        }
        if (getInputRecord().getStringValue(RiskFields.IS_GR1_COMP_VISIBLE).equals("Y")) {
            record.setFieldValue("isGr1CompAvailable", YesNoFlag.Y);
        }
        //set default risk filter type
        record.setFieldValue("riskTypeFilter", getPolicyHeader().getRiskHeader().getRiskTypeCode());

        //set srcRiskFormFields
        String[] srcRiskFormFields = (String[]) getNameList().toArray(new String[getNameList().size()]);
        record.setFieldValue("srcRiskFormFields", StringUtils.arrayToDelimited(srcRiskFormFields, ",", false, false));
    }


    public RiskCopyAllRecordLoadProcessor(PolicyHeader policyHeader, Record inputRecord, String riskFormFields) {
        setPolicyHeader(policyHeader);
        setInputRecord(inputRecord);
        setNameList(riskFormFields);
    }


    private PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    private void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }


    private Record getInputRecord() {
        return m_inputRecord;
    }

    private void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }


    public List getNameList() {
        return m_nameList;
    }

    private void setNameList(String srcRiskFormFields) {
        if (!StringUtils.isBlank(srcRiskFormFields)) {
            m_nameList = new ArrayList(Arrays.asList(srcRiskFormFields.split(",")));
        } else {
            m_nameList = new ArrayList();
        }
    }

    private List m_nameList;
    private Record m_inputRecord;
    private PolicyHeader m_policyHeader;
}
