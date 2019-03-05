package dti.ci.summarymgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Jun 20, 2008
 */
/*
 * Record and Page Fields operation for CIS Summary
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class SummaryFields {
    //fields name definition
    public static final String INDPAID = "indPaid";
    public static final String EXPPAID = "expPaid";
    public static final String OUTEXP = "outExp";
    public static final String OUTIND = "outInd";
    public static final String TOTAL_INDPAID = "totalIndPaid";
    public static final String TOTAL_EXPPAID = "totalExpPaid";
    public static final String TOTAL_OUTEXP = "totalOutExp";
    public static final String TOTAL_OUTIND = "totalOutInd";
    public static final String TOTAL_CLAIMS_COUNTS = "totalClaimsCount";
    public static final String STORED_PROC_PARA_ENTITY_PK_NAME = "entityId";

    /**
     * Get Ind Paid double value from record
     * @param record
     * @return
     */
    public static Double getIndPaid(Record record) {
        return record.getDoubleValue(INDPAID);
    }

    /**
     * Get Exp Paid double value from record
     * @param record
     * @return
     */
    public static Double getExpPaid(Record record) {
        return record.getDoubleValue(EXPPAID);
    }

    /**
     * Get Out Exp double value from record
     * @param record
     * @return
     */
    public static Double getOutExp(Record record) {
        return record.getDoubleValue(OUTEXP);
    }

    /**
     * Get Out Ind double value from record
     * @param record
     * @return
     */
    public static Double getOutInd(Record record) {
        return record.getDoubleValue(OUTIND);
    }

    /**
     * Set double Total Ind Paid value into record
     * @param record
     * @param totalIndPaid
     */
    public static void setTotalIndPaid(Record record, Double totalIndPaid) {
        record.setFieldValue(TOTAL_INDPAID, totalIndPaid);
    }

    /**
     * Set double Total Exp Paid value into record
     * @param record
     * @param totalExpPaid
     */
    public static void setTotalExpPaid(Record record, Double totalExpPaid) {
        record.setFieldValue(TOTAL_EXPPAID, totalExpPaid);
    }

    /**
     * Set double Total Out Exp value into record
     * @param record
     * @param totalOutExp
     */
    public static void setTotalOutExp(Record record, Double totalOutExp) {
        record.setFieldValue(TOTAL_OUTEXP, totalOutExp);
    }

    /**
     * Set double Total Out Ind value into record
     * @param record
     * @param totalOutInd
     */
    public static void setTotalOutInd(Record record, Double totalOutInd) {
        record.setFieldValue(TOTAL_OUTIND, totalOutInd);
    }

    /**
     * Set int Total Claims Counts value into record
     * @param record
     * @param
     */
    public static void setTotalClaimsCount(Record record, Integer totalClaimsCount) {
        record.setFieldValue(TOTAL_CLAIMS_COUNTS, totalClaimsCount);
    }
}
