package dti.pm.coverageclassmgr;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.busobjs.PMStatusCode;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 4, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/26/2007           Bhong       Did refactor.
 * 07/20/2011           syang       121208 - Added productCoverageClassId.
 * 07/19/2012           awu         134738 - Added origCoverageClassEffectiveToDate.
 * 04/26/2013           awu         141758 - Added selectedCoverageClass.
 * 05/29/2013           xnie        142949 - Added subCovgCopyB.
 * ---------------------------------------------------
 */

public class CoverageClassFields {
    public static final String COVERAGE_CLASS_ID = "coverageClassId";
    public static final String PRODUCT_COVERAGE_CLASS_CODE = "productCoverageClassCode";
    public static final String PRODUCT_COVERAGE = "productCoverage";
    public static final String PARENT_COVERAGE_BASE_RECORD_ID = "parentCoverageBaseRecordId";
    public static final String COVERAGE_CLASS_EFFECTIVE_FROM_DATE = "coverageClassEffectiveFromDate";
    public static final String COVERAGE_CLASS_EFFECTIVE_TO_DATE = "coverageClassEffectiveToDate";
    public static final String ORIG_COVERAGE_CLASS_EFFECTIVE_TO_DATE = "origCoverageClassEffectiveToDate";
    public static final String RETRO_DATE = "retroDate";
    public static final String POLICY_FORM_CODE = "policyFormCode";
    public static final String ISO_CUST_CODE = "isoCustCode";
    public static final String ADDL_INFO1 = "addlInfo1";
    public static final String ADDL_INFO2 = "addlInfo2";
    public static final String ADDL_INFO3 = "addlInfo3";
    public static final String RATING_MODULE_CODE = "ratingModuleCode";
    public static final String LEVEL = "level";
    public static final String COVERAGE_CLASS_SHORT_DESCRIPTION = "coverageClassShortDescription";
    public static final String COVERAGE_CLASS_LONG_DESCRIPTION = "coverageClassLongDescription";
    public static final String COVERAGE_CLASS_BASE_RECORD_ID = "coverageClassBaseRecordId";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String AFTER_IMAGE_RECORD_B = "afterImageRecordB";
    public static final String EXPOSURE_BASIS_CODE = "exposureBasisCode";
    public static final String NUM1 = "num1";
    public static final String NUM2 = "num2";
    public static final String NUM3 = "num3";
    public static final String COVERAGE_CLASS_STATUS = "coverageClassStatus";
    public static final String EXPOSURE_UNIT = "exposureUnit";
    public static final String PRODUCT_COVERAGE_CLASS_ID = "productCoverageClassId";
    public static final String IS_MANUALLY_RATED = "isManuallyRated";
    public static final String SELECTED_COVERAGE_CLASS = "selectedCoverageClass";
    public static final String SUB_COVERAGE_COPY_B = "subCovgCopyB";

    public static String getCoverageClassId(Record record) {
        return record.getStringValue(COVERAGE_CLASS_ID);
    }

    public static String getCoverageClassBaseRecordId(Record record) {
      return record.getStringValue(COVERAGE_CLASS_BASE_RECORD_ID);
    }

    public static String getSelectedCoverageClass(Record record) {
        return record.getStringValue(SELECTED_COVERAGE_CLASS);    
    }
    
    public static String getOrigCoverageClassEffectiveToDate(Record record){
        return record.getStringValue(ORIG_COVERAGE_CLASS_EFFECTIVE_TO_DATE);
    }

    public static String getProductCoverageClassCode(Record record) {
        return record.getStringValue(PRODUCT_COVERAGE_CLASS_CODE);
    }

    public static String getIsoCustCode(Record record) {
        return record.getStringValue(ISO_CUST_CODE);
    }

    public static String getRatingModuleCode(Record record) {
        return record.getStringValue(RATING_MODULE_CODE);
    }

    public static String getCoverageClassShortDescription(Record record) {
        return record.getStringValue(COVERAGE_CLASS_SHORT_DESCRIPTION);
    }

    public static String getCoverageClassLongDescription(Record record) {
        return record.getStringValue(COVERAGE_CLASS_LONG_DESCRIPTION);
    }

    public static String getPolicyFormCode(Record record) {
        return record.getStringValue(POLICY_FORM_CODE);
    }

    public static String getExposureBasisCode(Record record) {
        return record.getStringValue(EXPOSURE_BASIS_CODE);
    }

    public static String getOfficialRecordId(Record record) {
        return record.getStringValue(OFFICIAL_RECORD_ID);
    }

    public static String getNum1(Record record) {
        return record.getStringValue(NUM1);
    }

    public static String getNum2(Record record) {
        return record.getStringValue(NUM2);
    }

    public static String getNum3(Record record) {
        return record.getStringValue(NUM3);
    }

    public static String getCoverageClassEffectiveToDate(Record record) {
        return record.getStringValue(COVERAGE_CLASS_EFFECTIVE_TO_DATE);
    }

    public static boolean hasCoverageClassStatus(Record record) {
        return record.hasStringValue(COVERAGE_CLASS_STATUS);
    }

    public static PMStatusCode getCoverageClassStatus(Record record) {
        Object value = record.getFieldValue(COVERAGE_CLASS_STATUS);
        PMStatusCode result = null;
        if (value == null || value instanceof PMStatusCode) {
            result = (PMStatusCode) value;
        }
        else {
            result = PMStatusCode.getInstance(value.toString());
        }
        return result;
    }

    public static String getParentCoverageBaseRecordId(Record record) {
        return record.getStringValue(PARENT_COVERAGE_BASE_RECORD_ID);
    }

    // Set methods
    public static void setCoverageClassBaseRecordId(Record record, String coverageClassBaseRecordId) {
      record.setFieldValue(COVERAGE_CLASS_BASE_RECORD_ID, coverageClassBaseRecordId);
    }

    public static void setCoverageClassId(Record record, String covgClassId) {
        record.setFieldValue(COVERAGE_CLASS_ID, covgClassId);
    }

    public static void setProductCoverageClassCode(Record record, String productCoverageClassCode) {
        record.setFieldValue(PRODUCT_COVERAGE_CLASS_CODE, productCoverageClassCode);
    }

    public static String getCoverageClassEffectiveFromDate(Record record) {
        return record.getStringValue(COVERAGE_CLASS_EFFECTIVE_FROM_DATE);
    }

    public static void setCoverageClassEffectiveFromDate(Record record, String coverageClassEffectiveFromDate) {
        record.setFieldValue(COVERAGE_CLASS_EFFECTIVE_FROM_DATE, coverageClassEffectiveFromDate);
    }

    public static void setCoverageClassEffectiveToDate(Record record, String coverageClassEffectiveToDate) {
        record.setFieldValue(COVERAGE_CLASS_EFFECTIVE_TO_DATE, coverageClassEffectiveToDate);
    }

    public static void setOrigCoverageClassEffectiveToDate(Record record, String origCoverageClassEffectiveToDate) {
        record.setFieldValue(ORIG_COVERAGE_CLASS_EFFECTIVE_TO_DATE, origCoverageClassEffectiveToDate);
    }

    public static void setRetroDate(Record record, String retroDate) {
        record.setFieldValue(RETRO_DATE, retroDate);
    }

    public static void setAddlInfo1(Record record, String addlInfo1) {
        record.setFieldValue(ADDL_INFO1, addlInfo1);
    }

    public static void setAddlInfo2(Record record, String addlInfo2) {
        record.setFieldValue(ADDL_INFO2, addlInfo2);
    }

    public static void setAddlInfo3(Record record, String addlInfo3) {
        record.setFieldValue(ADDL_INFO3, addlInfo3);
    }

    public static void setRatingModuleCode(Record record, String ratingModuleCode) {
        record.setFieldValue(RATING_MODULE_CODE, ratingModuleCode);
    }

    public static void setCoverageClassLongDescription(Record record, String longDescription) {
        record.setFieldValue(COVERAGE_CLASS_LONG_DESCRIPTION, longDescription);
    }

    public static void setProductCoverage(Record record, String productCoverage) {
        record.setFieldValue(PRODUCT_COVERAGE, productCoverage);
    }

    public static void setParentCoverageBaseRecordId(Record record, String parentCoverageBaseRecordId) {
        record.setFieldValue(PARENT_COVERAGE_BASE_RECORD_ID, parentCoverageBaseRecordId);
    }

    public static void setLevel(Record record, String level) {
        record.setFieldValue(LEVEL, level);
    }

    public static void setOfficialRecordId(Record record, String officialRecordId) {
        record.setFieldValue(OFFICIAL_RECORD_ID, officialRecordId);
    }

    public static void setAfterImageRecordB(Record record, YesNoFlag afterImageRecordB) {
        record.setFieldValue(AFTER_IMAGE_RECORD_B, afterImageRecordB);
    }

    public static void setExposureBasisCode(Record record, String exposureBasisCode) {
        record.setFieldValue(EXPOSURE_BASIS_CODE, exposureBasisCode);
    }

    public static void setNum1(Record record, String num1) {
        record.setFieldValue(NUM1, num1);
    }

    public static void setNum2(Record record, String num2) {
        record.setFieldValue(NUM2, num2);
    }

    public static void setNum3(Record record, String num3) {
        record.setFieldValue(NUM3, num3);
    }

    public static void setPolicyFormCode(Record record, String policyFormCode) {
        record.setFieldValue(POLICY_FORM_CODE, policyFormCode);
    }

    public static void setCoverageClassStatus(Record record, PMStatusCode covgClassStatus) {
        record.setFieldValue(COVERAGE_CLASS_STATUS, covgClassStatus);
    }

    public static String getExposureUnit(Record record) {
        return record.getStringValue(EXPOSURE_UNIT);
    }

    public static void setExposureUnit(Record record, String exposureUnit) {
        record.setFieldValue(EXPOSURE_UNIT, exposureUnit);
    }

    public static String getProductCoverageClassId(Record record) {
        return record.getStringValue(PRODUCT_COVERAGE_CLASS_ID);
    }

    public static void setProductCoverageClassId(Record record, String productCoverageClassId) {
        record.setFieldValue(PRODUCT_COVERAGE_CLASS_ID, productCoverageClassId);
    }

    public static void setIsManuallyRated(Record record, YesNoFlag isManuallyRated) {
        record.setFieldValue(IS_MANUALLY_RATED, isManuallyRated);
    }

    public static void setSubCovgCopyB(Record record, YesNoFlag subCovgCopyB) {
        record.setFieldValue(SUB_COVERAGE_COPY_B, subCovgCopyB);
    }
}
