package dti.pm.transactionmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.transactionmgr.TransactionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.struts.IOasisAction;
import dti.oasis.messagemgr.MessageManager;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   March 20, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class LoadSaveOptionsAction extends PMBaseAction {

       /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadSaveOptions(mapping, form, request, response);
    }

    /**
     * Method to load list save types available for the current transaction
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadSaveOptions(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadSaveOptions",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadResult";

        try {
            String lovSQL = "[NO_ADD_SELECT_OPTION]LIST:WIP,WIP";
            Map saveOptions = null;

            /* Get the policy header and create the input record */
            Record inputRecord = getInputRecord(request);
            inputRecord.setFields(getPolicyHeader(request).toRecord(), false);

            /* Secures access to the page, load the Oasis Fields without loading the LOVs,
               and map the input parameters to the Fields */
            securePage(request, form);

            /* Get the applicable save options */
            saveOptions = getTransactionManager().loadSaveOptions(inputRecord);

            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            OasisFormField field = (OasisFormField) fields.get("saveAsCode");

            if (saveOptions.size() > 0) {
                Iterator itr = saveOptions.keySet().iterator();
                while (itr.hasNext()) {
                    String saveOptionCode = (String) itr.next();
                    lovSQL += "," + saveOptionCode + "," + saveOptions.get(saveOptionCode);
                }
            }
            field.setLovSql(lovSQL);
            field.setRows(String.valueOf(saveOptions.size() + 1));

            /* Load LOV */
            loadListOfValues(request, form);
            // add messages for javascript
            addJsMessages();
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the load the save options.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadSaveOptions", af);
        return af;
    }

    /**
     * Check if source policy in WIP status
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward isSourcePolicyInWip(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSourcePolicyInWip", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form, false);
            Record result = getTransactionManager().isSourcePolicyInWip(getInputRecord(request));
            writeAjaxXmlResponse(response, result);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to check source policy status.", e, response);
        }
        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSourcePolicyInWip", af);
        }
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.amalgamation.saveAsOfficialConfirm.info");
    }

    public TransactionManager getTransactionManager() {
       return  m_transactionManager;
   }

   public void setTransactionManager(TransactionManager transactionManager) {
       m_transactionManager = transactionManager;
   }

   public void verifyConfig() {
       if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
   }

   private TransactionManager m_transactionManager;
}
