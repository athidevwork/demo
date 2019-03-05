package dti.pm.transactionmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.transactionmgr.TransactionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for view professional entity transaction and details.
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   July 02, 2010
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/06/2010       syang       103797 - Modified loadAllProfessionalEntityTransaction() to publish summaryRecord.
 * ---------------------------------------------------
 */
public class ViewProfessionalEntityDetailAction extends PMBaseAction {
    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return loadAllProfessionalEntityTransaction(mapping, form, request, response);
    }

    /**
     * Method to load view professional entity detail page.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward ViewProfessionalEntityDetail(ActionMapping mapping,
                                                      ActionForm form,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "ViewProfessionalEntityDetail", new Object[]{mapping, form, request, response});
        String forwardString = "loadTransaction";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            // Get input record.
            Record inputRecord = getInputRecord(request);
            RecordSet rs = new RecordSet();
            // Get initial value for output
            Record output = getTransactionManager().getDefaultValuesForProfessionalEntitySearchCriteria();
            output.setFields(inputRecord);
            // Set datebean.
            setDataBean(request, rs);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            // Load the list of values after loading the data
            loadListOfValues(request, form);
            // Load grid header
            loadGridHeader(request, getAnchorColumnNames()[0], getGridHeaderLayerIds()[0]);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the view professional entity detail page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "ViewProfessionalEntityDetail", af);
        return af;
    }

    /**
     * Load all professional entity transactions.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllProfessionalEntityTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProfessionalEntityTransaction", new Object[]{mapping, form, request, response});
        }
        String forwardString = "loadTransaction";
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            // Get input record.
            Record inputRecord = getInputRecord(request);
            if (!inputRecord.hasStringValue("searchB")) {
                // Get initial value for output
                Record output = getTransactionManager().getDefaultValuesForProfessionalEntitySearchCriteria();
                inputRecord.setFields(output);
            }

            // Gets RecordSet
            RecordSet rs = getTransactionManager().loadAllProfessionalEntityTransaction(inputRecord);
            if (rs.getSize() <= 0) {
                MessageManager.getInstance().addErrorMessage("pm.viewProfEntityDetails.no.transaction");
            }
            // Sets data bean
            setDataBean(request, rs);
            Record output = rs.getSummaryRecord();
            output.setFields(inputRecord, false);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            // Load the list of values after loading the data
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request, getAnchorColumnNames()[0], getGridHeaderLayerIds()[0]);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the ViewProfessionalEntityDetail page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllProfessionalEntityTransaction", af);
        return af;
    }

    /**
     * Load all professional entity transaction details.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllProfessionalEntityTransactionDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                                    HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProfessionalEntityTransactionDetail", new Object[]{mapping, form, request, response});
        }
        String forwardString = "loadDetails";
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            // Get input record.
            Record inputRecord = getInputRecord(request);
            // Gets RecordSet
            RecordSet rs = getTransactionManager().loadAllProfessionalEntityTransactionDetail(inputRecord);
            // Sets data bean
            setDataBean(request, rs);
            // Load grid header bean
            loadGridHeader(request, getAnchorColumnNames()[1], getGridHeaderLayerIds()[1]);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the ViewProfessionalEntityDetail page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllProfessionalEntityTransactionDetail", af);
        return af;
    }

    //verify spring config
    public void verifyConfig() {
        if (getAnchorColumnNames() == null)
            throw new ConfigurationException("The required property 'anchorColumnNames' is missing.");
        if (getGridHeaderLayerIds() == null)
            throw new ConfigurationException("The required property 'gridHeaderLayerIds' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
    }

    /**
     * Get TransactionManager
     *
     * @return
     */
    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    /**
     * Set TransactionManager
     *
     * @param transactionManager
     */
    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    /**
     * Get anchor column names
     *
     * @return String[]
     */
    public String[] getAnchorColumnNames() {
        return m_anchorColumnNames;
    }

    /**
     * Set anchor column name
     *
     * @param anchorColumnNames
     */
    public void setAnchorColumnNames(String[] anchorColumnNames) {
        m_anchorColumnNames = anchorColumnNames;
    }

    /**
     * Get grid header layer ids
     *
     * @return String[]
     */
    public String[] getGridHeaderLayerIds() {
        return m_gridHeaderLayerIds;
    }

    /**
     * Set grid header layer ids
     *
     * @param gridHeaderLayerIds
     */
    public void setGridHeaderLayerIds(String[] gridHeaderLayerIds) {
        m_gridHeaderLayerIds = gridHeaderLayerIds;
    }

    private TransactionManager m_transactionManager;
    private String[] m_anchorColumnNames;
    private String[] m_gridHeaderLayerIds;
}
