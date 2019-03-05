package dti.pm.transactionmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * This is an action class for manage undo term.
 * <p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 14, 2010
 *
 * @author dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class PerformUndoTermAction extends PMBaseAction {

    /**
     * Method for AJAX call to process undo term
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward processUndoTerm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processUndoTerm", new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);
            getTransactionManager().processUndoTerm(policyHeader, inputRecord);

            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to process undo term.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "processUndoTerm", af);
        return af;
    }
}
