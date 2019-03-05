package dti.pm.policymgr.premiummgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.premiummgr.PremiumFields;
import dti.pm.policymgr.premiummgr.PremiumManager;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for view fund
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   August 16, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/01/2011       ryzhao      118806 - Modified loadAllFund() to get transaction information by invoking
 *                                       TransactionManager.loadTransactionById() when there is no data found.
 * ---------------------------------------------------
 */

public class ViewFundAction extends PMBaseAction {


    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllFund(mapping, form, request, response);
    }

    /**
     * Method to load all fund info for requested policy.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllFund(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllFund", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadFundResult";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);
            publishOutputRecord(request, policyHeader.toRecord());
            Record inputRecord = getInputRecord(request);
            RecordSet rs = getPremiumManager().loadAllFund(policyHeader, inputRecord);
            //Set page UI attributes
            Record record = new Record();
            if ((rs == null) || (rs.getSize()) <= 0) {
                Record transRecord = getTransactionManager().loadTransactionById(inputRecord);
                if (transRecord != null) {
                    record.setFields(transRecord);
                    record.setFieldValue("transaction", TransactionFields.getTransactionTypeCode(transRecord));
                }
                record.setFieldValue(PremiumFields.HAS_PREM_DATA_FOR_TRANSACTION, YesNoFlag.N);
                MessageManager.getInstance().addErrorMessage("pm.viewFundInfo.fundList.noDataFound");
            }
            else {
                record = rs.getSummaryRecord();
                record.setFieldValue(PremiumFields.HAS_PREM_DATA_FOR_TRANSACTION, YesNoFlag.Y);
            }
            record.setFieldValue("changeRecord", "-1");
            record.setFieldValue("riskBaseRecordId", "-1");
            // publish page field
            publishOutputRecord(request, record);
            // Sets data bean
            setDataBean(request, rs);
            // Loads list of values
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);
            //decide the coverage part is visible or not
            boolean visibleFlag = policyHeader.isCoveragePartConfigured();
            OasisFields fieldsMap = (OasisFields) request.getAttribute("fieldsMap");
            HashMap layerFieldsMap = fieldsMap.getLayerFieldsMap("PM_VIEW_FUND_GH");
            OasisFormField field = (OasisFormField) (layerFieldsMap.get("COVGPARTCODE_GH"));
            field.setIsVisible(visibleFlag);

        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadAllFund page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllFund", af);
        return af;
    }

    //verify spring config
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getPremiumManager() == null)
            throw new ConfigurationException("The required property 'premiumManager' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
    }

    public PremiumManager getPremiumManager() {
        return m_PremiumManager;
    }

    public void setPremiumManager(PremiumManager premiumManager) {
        m_PremiumManager = premiumManager;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }
    
    private PremiumManager m_PremiumManager;
    private TransactionManager m_transactionManager;
}
