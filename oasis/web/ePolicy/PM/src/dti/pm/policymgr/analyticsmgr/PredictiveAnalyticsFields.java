package dti.pm.policymgr.analyticsmgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 19, 2011
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
public class PredictiveAnalyticsFields {

    public static final String MODEL_TYPE = "modelType";
    public static final String SCORE_REQUEST_TYPE = "scoreRequestType";

    public static String getModelType(Record record) {
        return record.getStringValue(MODEL_TYPE);
    }

    public static void setModelType(Record record, String modelType) {
        record.setFieldValue(MODEL_TYPE, modelType);
    }

    public static String getScoreRequestType(Record record) {
        return record.getStringValue(SCORE_REQUEST_TYPE);
    }

    public static void setScoreRequestType(Record record, String scoreRequestType) {
        record.setFieldValue(SCORE_REQUEST_TYPE, scoreRequestType);
    }
}
