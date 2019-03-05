package dti.pm.policymgr.premiummgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.struts.ActionHelper;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.premiummgr.PremiumManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.logging.Logger;

/**
 * Action class for Maintain Premium Worksheet.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 26, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class MaintainPremiumWorkSheetAction extends PMBaseAction {


    /**
     * This method is triggered automatically when where is no process parameter sent in along the requested url.
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
        return validateTransactionForPremiumWorksheet(mapping, form, request, response);
    }

    /**
     * Method to validate the transaction for premium worksheet.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validateTransactionForPremiumWorksheet(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateTransactionForPremiumWorksheet", new Object[]{mapping, form, request, response});
        Connection conn = null;
        try {
            // Secures access to the page.
            securePage(request, form);
            // Get inputRecord.
            Record inputRecord = getInputRecord(request);
            // Get the connection.
            conn = ActionHelper.getConnection(request);
            // Validate the transaction.
            getPremiumManager().validateTransactionForPremiumWorksheet(inputRecord,conn);
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            // Handle the validation exception.
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate the transaction for premium worksheet.", e, response);
        }
        finally {
            if (conn != null) {
                DatabaseUtils.close(conn);
            }
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "validateTransactionForPremiumWorksheet", af);
        return af;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getPremiumManager() == null)
            throw new ConfigurationException("The required property 'premiumManager' is missing.");
    }

    public PremiumManager getPremiumManager() {
        return m_PremiumManager;
    }

    public void setPremiumManager(PremiumManager premiumManager) {
        m_PremiumManager = premiumManager;
    }

    private PremiumManager m_PremiumManager;
}
