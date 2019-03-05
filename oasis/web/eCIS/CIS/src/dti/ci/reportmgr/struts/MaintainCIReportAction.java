package dti.ci.reportmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.struts.ActionHelper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DatabaseUtils;
import dti.ci.struts.action.CIBaseAction;
import dti.ci.reportmgr.CIReportManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.logging.Logger;

/**
 * Action class for Maintain Report.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   June 16, 2009
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
public class MaintainCIReportAction extends CIBaseAction {

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
        return generateReport(mapping, form, request, response);
    }

    /**
     * Method to generate report.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward generateReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "generatePolicyReport", new Object[]{mapping, form, request, response});
        String forwardString;
        Connection conn = null;
        try {
            // Secure access to the page.
            securePage(request, form);
            // Get connection.
            conn = ActionHelper.getConnection(request);
            // Get inputRecord.
            Record inputRecord = getInputRecord(request);
            String baseDir = request.getRealPath("");
            inputRecord.setFieldValue("baseDir", baseDir);
            // Get the pdf output stream.
            ByteArrayOutputStream bos = getCIReportManager().generatePDFStream(inputRecord, conn);
            response.setContentType("application/pdf");
            response.setContentLength(bos.size());
            ServletOutputStream sos = response.getOutputStream();
            bos.writeTo(sos);
            bos.close();
            sos.flush();
            return null;
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to generate policy report.", e, request, mapping);
        }
        finally {
            if (conn != null) {
                DatabaseUtils.close(conn);
            }
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "generatePolicyReport", af);
        return af;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getCIReportManager() == null)
            throw new ConfigurationException("The required property 'CIReportManager' is missing.");
    }

    public CIReportManager getCIReportManager() {
        return m_CIReportManager;
    }

    public void setCIReportManager(CIReportManager CIReportManager) {
        m_CIReportManager = CIReportManager;
    }

    private CIReportManager m_CIReportManager;

}
