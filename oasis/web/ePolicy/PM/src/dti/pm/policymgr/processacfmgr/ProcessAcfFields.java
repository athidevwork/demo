package dti.pm.policymgr.processacfmgr;

import dti.oasis.recordset.Record;

/**
 * Constants for Process Acf.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 30, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ProcessAcfFields {
    public static final String DELTA_AMT = "deltaAmt";
    public static final String TRANSACTIONAL_TIV = "transactionalTiv";
    public static final String RT_ALLOC_AMT = "rtAllocAmt";
    public static final String RT_COMM_AMT = "rtCommAmt";
    public static final String OVERRIDE_TRANS_ID = "overrideTransId";
    public static final String OVERRIDE_TERM_ID = "overrideTermId";
    public static final String TRANS_WP_TOTAL = "transWPTotal";
    public static final String TRANS_TIV_TOTAL = "transTivTotal";
    public static final String COMM_AMT_TOTAL = "commAmtTotal";
    public static final String ALLOC_AMT_TOTAL = "allocAmtTotal";
    public static final String FEE_AMT = "feeAmt";
    public static final String FEE_ENTITY_ID = "feeEntityId";
    public static final String TRANS_ID = "transId";
    public static final String FEE_TRANS_ID = "feeTransId";
    public static final String TERM_ID = "termId";
    public static final String FEE_TERM_ID = "feeTermId";
    public static final String INTERFACE_STATUS_CODE = "interfaceStatusCode";
    public static final String TRANS_EFF = "transEff";
    public static final String POLICY_BROKERAGE_FEE_DETAIL_ID = "policyBrokerageFeeDetailId";
    public static final String POLICY_BROKERAGE_OVERRIDE_ID = "policyBrokerageOverrideId";
    public static final String PRODUCT_BROKERAGE_ID = "productBrokerageId";
    public static final String OVERRIDE_ALLOC_TYPE = "overrideAllocType";
    public static final String OVERRIDE_ALLOC_AMT = "overrideAllocAmt";
    public static final String OVERRIDE_COMM_TYPE = "overrideCommType";
    public static final String OVERRIDE_COMM_AMT = "overrideCommAmt";
    public static final String OVERRIDE_ENTITY_ID = "overrideEntityId";
    public static final String OVERRIDE_EXTERNAL_ID = "overrideExternalId";
    public static final String OVERRIDE_LAYER_NO = "overrideLayerNo";
    public static final String TRANSACTION_LOG_ID = "transactionLogId";

    public static String getRtCommAmt(Record record) {
        return record.getStringValue(RT_COMM_AMT);
    }

    public static void setRtCommAmt(Record record, String rtCommAmt) {
        record.setFieldValue(RT_COMM_AMT, rtCommAmt);
    }

    public static String getRtAllocAmt(Record record) {
        return record.getStringValue(RT_ALLOC_AMT);
    }

    public static void setRtAllocAmt(Record record, String rtAllocAmt) {
        record.setFieldValue(RT_ALLOC_AMT, rtAllocAmt);
    }

    public static String getTransactionalTiv(Record record) {
        return record.getStringValue(TRANSACTIONAL_TIV);
    }

    public static void setTransactionalTiv(Record record, String transactionalTiv) {
        record.setFieldValue(TRANSACTIONAL_TIV, transactionalTiv);
    }

    public static String getDeltaAmt(Record record) {
        return record.getStringValue(DELTA_AMT);
    }

    public static void setDeltaAmt(Record record, String deltaAmt) {
        record.setFieldValue(DELTA_AMT, deltaAmt);
    }

    public static String getOverrideTransId(Record record) {
        return record.getStringValue(OVERRIDE_TRANS_ID);
    }

    public static void setOverrideTransId(Record record, String overrideTransId) {
        record.setFieldValue(OVERRIDE_TRANS_ID, overrideTransId);
    }

    public static String getOverrideTermId(Record record) {
        return record.getStringValue(OVERRIDE_TERM_ID);
    }

    public static void setOverrideTermId(Record record, String overrideTermId) {
        record.setFieldValue(OVERRIDE_TERM_ID, overrideTermId);
    }

    public static String getTransWPTotal(Record record) {
        return record.getStringValue(TRANS_WP_TOTAL);
    }

    public static void setTransWPTotal(Record record, String transWPTotal) {
        record.setFieldValue(TRANS_WP_TOTAL, transWPTotal);
    }

    public static String getTransTivTotal(Record record) {
        return record.getStringValue(TRANS_TIV_TOTAL);
    }

    public static void setTransTivTotal(Record record, String transTivTotal) {
        record.setFieldValue(TRANS_TIV_TOTAL, transTivTotal);
    }

    public static String getCommAmtTotal(Record record) {
        return record.getStringValue(COMM_AMT_TOTAL);
    }

    public static void setCommAmtTotal(Record record, String commAmtTotal) {
        record.setFieldValue(COMM_AMT_TOTAL, commAmtTotal);
    }

    public static String getAllocAmtTotal(Record record) {
        return record.getStringValue(ALLOC_AMT_TOTAL);
    }

    public static void setAllocAmtTotal(Record record, String allocAmtTotal) {
        record.setFieldValue(ALLOC_AMT_TOTAL, allocAmtTotal);
    }

    public static String getFeeEntityId(Record record) {
        return record.getStringValue(FEE_ENTITY_ID);
    }

    public static void setFeeEntityId(Record record, String feeEntityId) {
        record.setFieldValue(FEE_ENTITY_ID, feeEntityId);
    }

    public static String getFeeAmt(Record record) {
        return record.getStringValue(FEE_AMT);
    }

    public static void setFeeAmt(Record record, String feeAmt) {
        record.setFieldValue(FEE_AMT, feeAmt);
    }

    public static String getPolicyBrokerageFeeDetailId(Record record) {
        return record.getStringValue(POLICY_BROKERAGE_FEE_DETAIL_ID);
    }

    public static void setPolicyBrokerageFeeDetailId(Record record, String policyBrokerageFeeDetailId) {
        record.setFieldValue(POLICY_BROKERAGE_FEE_DETAIL_ID, policyBrokerageFeeDetailId);
    }

    public static String getPolicyBrokerageOverrideId(Record record) {
        return record.getStringValue(POLICY_BROKERAGE_OVERRIDE_ID);
    }

    public static void setPolicyBrokerageOverrideId(Record record, String policyBrokerageOverrideId) {
        record.setFieldValue(POLICY_BROKERAGE_OVERRIDE_ID, policyBrokerageOverrideId);
    }

    public static String getTransId(Record record) {
        return record.getStringValue(TRANS_ID);
    }

    public static void setTransId(Record record, String transId) {
        record.setFieldValue(TRANS_ID, transId);
    }

    public static String getFeeTransId(Record record) {
        return record.getStringValue(FEE_TRANS_ID);
    }

    public static void setFeeTransId(Record record, String feeTransId) {
        record.setFieldValue(FEE_TRANS_ID, feeTransId);
    }

    public static String getFeeTermId(Record record) {
        return record.getStringValue(FEE_TERM_ID);
    }

    public static void setFeeTermId(Record record, String feeTermId) {
        record.setFieldValue(FEE_TERM_ID, feeTermId);
    }

    public static String getTermId(Record record) {
        return record.getStringValue(TERM_ID);
    }

    public static void setTermId(Record record, String termId) {
        record.setFieldValue(TERM_ID, termId);
    }

    public static String getTransEff(Record record) {
        return record.getStringValue(TRANS_EFF);
    }

    public static void setTransEff(Record record, String transEff) {
        record.setFieldValue(TRANS_EFF, transEff);
    }

    public static String getInterfaceStatusCode(Record record) {
        return record.getStringValue(INTERFACE_STATUS_CODE);
    }

    public static void setInterfaceStatusCode(Record record, String interfaceStatusCode) {
        record.setFieldValue(INTERFACE_STATUS_CODE, interfaceStatusCode);
    }

    public static String getProductBrokerageId(Record record) {
        return record.getStringValue(PRODUCT_BROKERAGE_ID);
    }

    public static void setProductBrokerageId(Record record, String productBrokerageId) {
        record.setFieldValue(PRODUCT_BROKERAGE_ID, productBrokerageId);
    }

    public static String getOverrideAllocType(Record record) {
        return record.getStringValue(OVERRIDE_ALLOC_TYPE);
    }

    public static void setOverrideAllocType(Record record, String overrideAllocType) {
        record.setFieldValue(OVERRIDE_ALLOC_TYPE, overrideAllocType);
    }

    public static String getOverrideAllocAmt(Record record) {
        return record.getStringValue(OVERRIDE_ALLOC_AMT);
    }

    public static void setOverrideAllocAmt(Record record, String overrideAllocAmt) {
        record.setFieldValue(OVERRIDE_ALLOC_AMT, overrideAllocAmt);
    }

    public static String getOverrideCommType(Record record) {
        return record.getStringValue(OVERRIDE_COMM_TYPE);
    }

    public static void setOverrideCommType(Record record, String overrideCommType) {
        record.setFieldValue(OVERRIDE_COMM_TYPE, overrideCommType);
    }

    public static String getOverrideCommAmt(Record record) {
        return record.getStringValue(OVERRIDE_COMM_AMT);
    }

    public static void setOverrideCommAmt(Record record, String overrideCommAmt) {
        record.setFieldValue(OVERRIDE_COMM_AMT, overrideCommAmt);
    }

    public static String getOverrideEntityId(Record record) {
        return record.getStringValue(OVERRIDE_ENTITY_ID);
    }

    public static void setOverrideEntityId(Record record, String overrideEntityId) {
        record.setFieldValue(OVERRIDE_ENTITY_ID, overrideEntityId);
    }

    public static String getOverrideExternalId(Record record) {
        return record.getStringValue(OVERRIDE_EXTERNAL_ID);
    }

    public static void setOverrideExternalId(Record record, String overrideExternalId) {
        record.setFieldValue(OVERRIDE_EXTERNAL_ID, overrideExternalId);
    }

    public static String getTransactionLogId(Record record) {
        return record.getStringValue(TRANSACTION_LOG_ID);
    }

    public static void setTransactionLogId(Record record, String transactionLogId) {
        record.setFieldValue(TRANSACTION_LOG_ID, transactionLogId);
    }

    public class OverrideAllocTypeCodeValues {
        public static final String PREM_DIFF = "PREM_DIFF";
        public static final String PERCENT = "PERCENT";
        public static final String FLAT = "FLAT$";
    }

    public class PolicyTypeCodeValues {
        public static final String STANDALONE_POL_TYPE = "OTHER";
    }
}
