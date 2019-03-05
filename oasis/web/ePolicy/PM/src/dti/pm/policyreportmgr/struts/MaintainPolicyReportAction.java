package dti.pm.policyreportmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.struts.ActionHelper;
import dti.oasis.tags.OasisGrid;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policyreportmgr.PolicyReportManager;
import dti.oasis.app.ApplicationContext;
import dti.oasis.filter.CharacterEncodingFilter;

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
 * Action class for Maintain Policy Report.
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
 * 03/02/2011       fcb         112664 & 107021: generatePolicyCsvReport and  generatePolicyXmlReport added.
 * 11/19/2015       eyin        167171 - Modified generatePolicyCsvReport(), Add logic to remove spaces when dispositionType is not null.
 * ---------------------------------------------------
 */
public class MaintainPolicyReportAction extends PMBaseAction {

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
        return generatePolicyReport(mapping, form, request, response);
    }

    /**
     * Method to generate policy report.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward generatePolicyReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "generatePolicyReport", new Object[]{mapping, form, request, response});
        String forwardString;
        Connection conn = null;
        try {
            // Secure access to the page.
            securePage(request, form, false);
            // Get connection.
            conn = ActionHelper.getConnection(request);
            // Get inputRecord.
            Record inputRecord = getInputRecord(request);
            // Get the pdf output stream.
            ByteArrayOutputStream bos = getPolicyReportManager().generatePDFStream(inputRecord, conn);
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
     * Method to generate Excel policy reports.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward generatePolicyCsvReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "generatePolicyCsvReport", new Object[]{mapping, form, request, response});
        String forwardString;
        Connection conn = null;
        try {
            // Secure access to the page.
            securePage(request, form, false);
            // Get connection.
            conn = ActionHelper.getConnection(request);
            // Get inputRecord.
            Record inputRecord = getInputRecord(request);
            byte [] bytes = (getPolicyReportManager().generateCSVStream(inputRecord, conn)).getBytes();
            ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
            bos.write(bytes, 0, bytes.length);

            String encoding = ApplicationContext.getInstance().getProperty(CharacterEncodingFilter.CHARACTER_ENCODING_DEFAULT);
            response.setContentType("text/html; charset=" + encoding);
            String dispositionType = request.getParameter("dispositionType");
            if (StringUtils.isBlank(dispositionType)) {
                dispositionType = OasisGrid.ATTACH_DISP_TYPE;
            }
            else {
                dispositionType = org.apache.commons.lang3.StringUtils.deleteWhitespace(dispositionType);
            }
            String reportName = inputRecord.getFieldValue("reportCode").toString().toLowerCase()+".csv";
            response.setHeader("Content-Disposition", dispositionType + "; filename="+reportName);

            response.setContentLength(bos.size());
            ServletOutputStream sos = response.getOutputStream();
            bos.writeTo(sos);
            bos.close();
            sos.flush();
            return null;
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to call generatePolicyCsvReport.", e, request, mapping);
        }
        finally {
            if (conn != null) {
                DatabaseUtils.close(conn);
            }
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "generatePolicyCsvReport", af);
        return af;
    }

    /**
     * Method to generate Excel policy XLS or XLSX reports.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward generatePolicyXLSReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "generatePolicyXLSReport", new Object[]{mapping, form, request, response});
        String forwardString;
        Connection conn = null;
        try {
            // Secure access to the page.
            securePage(request, form, false);
            // Get connection.
            conn = ActionHelper.getConnection(request);
            // Get inputRecord.
            Record inputRecord = getInputRecord(request);
            String textForFile = getPolicyReportManager().generateCSVStream(inputRecord, conn);
            String exportType = request.getParameter("exportType");
            String fileExt = ".xlsx";
            if (exportType.equalsIgnoreCase("XLS"))
                fileExt = ".xls";

            String reportName = inputRecord.getFieldValue("reportCode").toString().toLowerCase();

            processExcelExport(request, response, textForFile, reportName+fileExt);
            return null;
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to call generatePolicyXLSReport.", e, request, mapping);
        }
        finally {
            if (conn != null) {
                DatabaseUtils.close(conn);
            }
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "generatePolicyXLSReport", af);
        return af;
    }

    /**
     * Method to generate Excel policy reports.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward generatePolicyXmlReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "generatePolicyXmlReport", new Object[]{mapping, form, request, response});
        String forwardString;
        Connection conn = null;
        try {
            // Secure access to the page.
            securePage(request, form, false);
            // Get connection.
            conn = ActionHelper.getConnection(request);
            // Get inputRecord.
            Record inputRecord = getInputRecord(request);
            byte [] bytes = (getPolicyReportManager().generateXMLStream(inputRecord, conn)).getBytes();
            ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
            bos.write(bytes, 0, bytes.length);

            response.setContentType("application/vnd.ms-excel");
            response.setContentLength(bos.size());
            ServletOutputStream sos = response.getOutputStream();
            bos.writeTo(sos);
            bos.close();
            sos.flush();
            return null;
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to call generatePolicyXmlReport.", e, request, mapping);
        }
        finally {
            if (conn != null) {
                DatabaseUtils.close(conn);
            }
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "generatePolicyXmlReport", af);
        return af;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getPolicyReportManager() == null)
            throw new ConfigurationException("The required property 'policyReportManager' is missing.");
    }

    public PolicyReportManager getPolicyReportManager() {
        return m_PolicyReportManager;
    }

    public void setPolicyReportManager(PolicyReportManager policyReportManager) {
        m_PolicyReportManager = policyReportManager;
    }

    private PolicyReportManager m_PolicyReportManager;

}
