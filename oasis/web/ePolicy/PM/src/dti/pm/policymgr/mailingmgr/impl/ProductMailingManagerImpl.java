package dti.pm.policymgr.mailingmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.policymgr.mailingmgr.ProductMailingFields;
import dti.pm.policymgr.mailingmgr.ProductMailingManager;
import dti.pm.policymgr.mailingmgr.dao.ProductMailingDAO;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to handle Implementation of Policy Mailing.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2013
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */

public class ProductMailingManagerImpl implements ProductMailingManager {

    public static final String MAINTAIN_PRODUCT_MAILING_ACTION_CLASS_NAME =
        "dti.pm.policymgr.mailingmgr.struts.MaintainProductMailingAction";

    /**
     * Return a record set with a list of product mailing data.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadProductMailing(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadProductMailing", new Object[]{inputRecord});
        }
        ProductMailingEntitlementRecordLoadProcessor productMailingEp = new ProductMailingEntitlementRecordLoadProcessor();
        RecordSet rs = getProductMailingDAO().loadAllProductMailing(inputRecord, productMailingEp);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadProductMailing", rs);
        }
        return rs;
    }

    /**
     * This method used to save the product mailing data.
     *
     * @param inputRecordSet
     */
    public void saveProductMailing(RecordSet inputRecordSet) {
        Logger l = LogUtils.enterLog(getClass(), "saveProductMailing", new Object[]{inputRecordSet});

        int updateCount = 0;
        validateProductMailing(inputRecordSet);
        RecordSet changedRecords = PMRecordSetHelper.setRowStatusOnModifiedRecords(inputRecordSet);
        if (changedRecords.getSize() > 0) {
            updateCount = getProductMailingDAO().saveProductMailingInfo(changedRecords);
        }

        l.exiting(getClass().getName(), "saveProductMailing", new Long(updateCount));
    }

    /**
     * This method used to set initial values for adding product mailing.
     *
     * @return
     */
    public Record getInitialValuesForProductMailing() {
        Logger l = LogUtils.getLogger(getClass());

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForProductMailing");
        }
        Record output = new Record();
        ProductMailingFields.setEffectiveDate(output, DateUtils.formatDate(new Date()));
        ProductMailingFields.setExpirationDate(output, "01/01/3000");

        output.setFields(ProductMailingEntitlementRecordLoadProcessor.getInitialEntitlementValuesForAddtionalInsured());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForProductMailing", output);
        }
        return output;
    }

    protected void validateProductMailing(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateProductMailing", new Object[]{inputRecords});
        }

        int recordSize = inputRecords.getSize();
        for (int i = 0; i < recordSize; i++) {
            Record currentRec = inputRecords.getRecord(i);
            String rowNum = String.valueOf(currentRec.getRecordNumber() + 1);
            String rowId = ProductMailingFields.getProductMailingId(currentRec);

            if (!currentRec.hasStringValue(ProductMailingFields.EFFECTIVE_DATE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainProductMailing.required.error",
                    new String[]{rowNum, "Effective From Date"}, ProductMailingFields.EFFECTIVE_DATE, rowId);
                throw new ValidationException("Invalid Effective From Date.");
            }
            if (!currentRec.hasStringValue(ProductMailingFields.EXPIRATION_DATE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainProductMailing.required.error",
                    new String[]{rowNum, "Effective To Date"}, ProductMailingFields.EXPIRATION_DATE, rowId);
                throw new ValidationException("Invalid Effective To Date.");
            }
            if(!currentRec.hasStringValue(ProductMailingFields.SHORT_DESCRIPTION)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainProductMailing.required.error",
                    new String[]{rowNum, "Short Description"}, ProductMailingFields.SHORT_DESCRIPTION, rowId);
                throw new ValidationException("Invalid Short Description.");
            }
            if(!currentRec.hasStringValue(ProductMailingFields.LONG_DESCRIPTION)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainProductMailing.required.error",
                    new String[]{rowNum, "Long Description"}, ProductMailingFields.LONG_DESCRIPTION, rowId);
                throw new ValidationException("Invalid Long Description.");
            }
            if(!currentRec.hasStringValue(ProductMailingFields.ENDORSEMENT_CODE)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainProductMailing.required.error",
                    new String[]{rowNum, "Endorsement Code"}, ProductMailingFields.ENDORSEMENT_CODE, rowId);
                throw new ValidationException("Invalid Endorsement Code.");
            }
            if (currentRec.getDateValue(ProductMailingFields.EXPIRATION_DATE).before
                (currentRec.getDateValue(ProductMailingFields.EFFECTIVE_DATE))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainProductMailing.date.error",
                    new String[]{rowNum}, ProductMailingFields.EXPIRATION_DATE, rowId);
                throw new ValidationException(("Invalid Expiration Date."));
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateProductMailing");
        }
    }

    /**
     * Verify ProductMailingDAO in spring config.
     */
    public void verifyConfig() {
        if (getProductMailingDAO() == null) {
            throw new ConfigurationException("The required property 'productMailingDAO' is missing.");
        }
    }

    public ProductMailingDAO getProductMailingDAO() {
        return this.productMailingDAO;
    }

    public void setProductMailingDAO(ProductMailingDAO productMailingDAO) {
        this.productMailingDAO = productMailingDAO;
    }

    private ProductMailingDAO productMailingDAO;

}
