package dti.ci.riskmgr;

import dti.oasis.recordset.Record;

/**
 * The constants for Risk Manager Records.
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 23, 2008
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/22/2015       bzhu        Issue 156487 - Added ACC_DIST_POINT.
 * ---------------------------------------------------
 */

public class RiskFields {
    public static final String ENTITY_ID = "entityId";
    public static final String CURRENT_RM_DISCOUNT_DESCR = "currentRmDiscountDescr";
    public static final String RM_DISCOUNT_DESCR = "rmDiscountDescr";
    public static final String CURRENT_MANDATE_WINDOW_PERIOD_DESCR = "currentMandateWindowPeriodDescr";
    public static final String WINDOW_PERIOD_DESCR = "windowPeriodDescr";
    public static final String MANDATE_WINDOW_PERIOD_FULFILLED = "mandateWindowPeriodFulfilled";
    public static final String WINDOW_PERIOD_STATUS = "windowPeriodStatus";
    public static final String NEGATIVE_POINTS = "negativePoints";
    public static final String NEGATIVE_POINTS_COUNT = "negativePointsCount";
    public static final String NETPOINT_CAP = "netpointCap";
    public static final String PRESUMPTIVE_NETPTS = "presumptiveNetpts";
    public static final String NET_POINTS = "netPoints";
    public static final String SATISFIED_B = "satisfiedB";
    public static final String ACC_DIST_POINT = "accDistPoint";

    public static final String SHOW_CURRENT_RM_DISCOUNT = "showCurrentRmDiscount";
    public static final String SHOW_MANDATE_WINDOW_PERIOD = "showMandateWindowPeriod";
    public static final String SHOW_PROGRAM_HISTORY = "showProgramHistory";
    public static final String SHOW_WINDOW_PERIOD_HISTORY = "showWindowPeriodHistory";
    public static final String SHOW_ADDITIONAL_RM_DISCOUNT = "showAdditionalRmDiscount";
    public static final String SHOW_ERS_POINTS_HISTORY = "showErsPointsHistory";

    public static Long getEntityId(Record record) {
        return record.getLongValue(ENTITY_ID);
    }

    public static String getCurrentRmDiscountDescr(Record record) {
        return record.getStringValue(CURRENT_RM_DISCOUNT_DESCR);
    }

    public static String getRmDiscountDescr(Record record) {
        return record.getStringValue(RM_DISCOUNT_DESCR);
    }

    public static String getCurrentMandateWindowPeriodDescr(Record record) {
        return record.getStringValue(CURRENT_MANDATE_WINDOW_PERIOD_DESCR);
    }

    public static String getWindowPeriodDescr(Record record) {
        return record.getStringValue(WINDOW_PERIOD_DESCR);
    }

    public static String getMandateWindowPeriodFulfilled(Record record) {
        return record.getStringValue(MANDATE_WINDOW_PERIOD_FULFILLED);
    }

    public static String getWindowPeriodStatus(Record record) {
        return record.getStringValue(WINDOW_PERIOD_STATUS);
    }

    public static Integer getNegativePoints(Record record) {
        return record.getIntegerValue(NEGATIVE_POINTS);
    }

    public static Integer getNegativePointsCount(Record record) {
        return record.getIntegerValue(NEGATIVE_POINTS_COUNT);
    }

    public static Integer getNetpointCap(Record record) {
        return record.getIntegerValue(NETPOINT_CAP);
    }

    public static Integer getPresumptiveNetpts(Record record) {
        return record.getIntegerValue(PRESUMPTIVE_NETPTS);
    }

    public static Integer getNetPoints(Record record) {
        return record.getIntegerValue(NET_POINTS);
    }

    public static String getSatisfiedB(Record record) {
        return record.getStringValue(SATISFIED_B);
    }

    public static String getAccDistPoint(Record record) {
        return record.getStringValue(ACC_DIST_POINT);
    }

    public static void setEntityId(Record record, Long entityId) {
        record.setFieldValue(ENTITY_ID, entityId);
    }

    public static void setCurrentRmDiscountDescr(Record record, String currentRmDiscountDescr) {
        record.setFieldValue(CURRENT_RM_DISCOUNT_DESCR, currentRmDiscountDescr);
    }

    public static void setRmDiscountDescr(Record record, String rmDiscountDescr) {
        record.setFieldValue(RM_DISCOUNT_DESCR, rmDiscountDescr);
    }

    public static void setCurrentMandateWindowPeriodDescr(Record record, String currentMandateWindowPeriodDescr) {
        record.setFieldValue(CURRENT_MANDATE_WINDOW_PERIOD_DESCR, currentMandateWindowPeriodDescr);
    }

    public static void setWindowPeriodDescr(Record record, String windowPeriodDescr) {
        record.setFieldValue(WINDOW_PERIOD_DESCR, windowPeriodDescr);
    }

    public static void setMandateWindowPeriodFulfilled(Record record, String mandateWindowPeriodFulfilled) {
        record.setFieldValue(MANDATE_WINDOW_PERIOD_FULFILLED, mandateWindowPeriodFulfilled);
    }

    public static void setWindowPeriodStatus(Record record, String windowPeriodStatus) {
        record.setFieldValue(WINDOW_PERIOD_STATUS, windowPeriodStatus);
    }

    public static void setNegativePoints(Record record, Integer negativePoints) {
        record.setFieldValue(NEGATIVE_POINTS, negativePoints);
    }

    public static void setNegativePointsCount(Record record, Integer negativePointsCount) {
        record.setFieldValue(NEGATIVE_POINTS_COUNT, negativePointsCount);
    }

    public static void setAccDistPoint(Record record, String accDistPoint) {
        record.setFieldValue(ACC_DIST_POINT, accDistPoint);
    }

    public static void setNetpointCap(Record record, Integer netpointCap) {
        record.setFieldValue(NETPOINT_CAP, netpointCap);
    }

    public static void setPresumptiveNetpts(Record record, Integer presumptiveNetpts) {
        record.setFieldValue(PRESUMPTIVE_NETPTS, presumptiveNetpts);
    }

    public static void setNetPoints(Record record, Integer netPoints) {
        record.setFieldValue(NET_POINTS, netPoints);
    }

    public static void setSatisfiedB(Record record, String satisfiedB) {
        record.setFieldValue(SATISFIED_B, satisfiedB);
    }
}
