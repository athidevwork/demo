package dti.pm.validationmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;

/**
 * An interface that provides DAO operation for Validation.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 21, 2007
 *
 * @author sma
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public interface ValidationDAO {
    /**
     * Validate Retroactive Date
     *
     * @param inputRecord
     * @param riskId
     * @param productCoverageFieldName
     * @param coverageEffectiveFromdateFieldName
     * @param retroDateFieldName
     * @return Record
     */
    Record validateRetroactiveDate(Record inputRecord, String riskId, String productCoverageFieldName,
                                          String coverageEffectiveFromdateFieldName, String retroDateFieldName);

    /**
     * Validate Accounting Month
     *
     * @param inputRecord Record contains input values
     * @return YesNoFlag indicates if accounting month is valid
     */
    YesNoFlag checkAccountingMonth(Record inputRecord);

    /**
     * Validate policy type
     *
     * @param inputRecord Record contains input values
     * @return YesNoFlag indicates if policy type is valid
     */
    YesNoFlag checkPolicyType(Record inputRecord);

    public static final String BEAN_NAME = "ValidationDAO";
}
