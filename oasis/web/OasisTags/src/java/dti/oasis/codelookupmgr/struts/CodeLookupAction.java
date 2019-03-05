package dti.oasis.codelookupmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.BaseAction;
import dti.oasis.struts.ActionHelper;
import dti.oasis.http.RequestIds;
import dti.oasis.codelookupmgr.CodeLookupManager;
import dti.oasis.codelookupmgr.impl.CodeLookupManagerImpl;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DatabaseUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.util.LabelValueBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.Connection;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 6, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/21/2007       sxm         Moved writeListOfValuesAsXML() into BaseAction
 * 08/14/2018       kshen       194134. Added method loadListOfValuesForJqxGrid to
 * ---------------------------------------------------
 */
public class CodeLookupAction extends BaseAction {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS, "lookup");
        return loadListOfValues(mapping, form, request, response);
    }

    /**
     * Method to load the specified List of Values in XML
     * <p/>
     */
    public ActionForward loadListOfValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadListOfValues", new Object[]{mapping, form, request, response});
        }

        // Parse the request parameters as Fields in a Record.
        Record inputRecord = getInputRecord(request);
        String fieldId = inputRecord.getStringValue("fieldId");
        String currentValue = inputRecord.getStringValue("currentValue");
        currentValue = currentValue == null ? "" : currentValue;
        boolean isReadOnly = Boolean.valueOf(inputRecord.getStringValue("isReadOnly"));

        // Get the List of Values for the request fieldId
        ArrayList listOfValues;
        Connection conn = null;
        try {
            conn = ActionHelper.getConnection(request);
            listOfValues = getCodeLookupManager().getListOfValues(conn, request, inputRecord);
        } finally {
            DatabaseUtils.close(conn);
        }

        // Write the List of Values as XML
        writeListOfValuesAsXML(response, listOfValues, fieldId, currentValue, isReadOnly);

        l.exiting(getClass().getName(), "loadListOfValues");
        return null;
    }

    /**
     * Ajax call to load list of values recordset.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadListOfValuesForJqxGrid(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadListOfValuesForJqxGrid", new Object[]{mapping, form, request, response});
        }

        Connection conn = null;
        try {
            Record inputRecord = getInputRecord(request);
            conn = ActionHelper.getConnection(request);
            ArrayList listOfValues = getCodeLookupManager().getListOfValues(conn, request, inputRecord);

            RecordSet recordSet = new RecordSet();
            for (int i = 0; i < listOfValues.size(); i++) {
                LabelValueBean labelValueBean = (LabelValueBean) listOfValues.get(i);

                Record record = new Record();
                record.setFieldValue("code", labelValueBean.getValue());
                record.setFieldValue("label", labelValueBean.getLabel());

                recordSet.addRecord(record);
            }

            writeAjaxXmlResponse(response, recordSet, true);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,"Unable to load list of values.", e, response);

        } finally {
            DatabaseUtils.close(conn);
        }

        return null;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public CodeLookupAction() {
        super();
    }

    public void verifyConfig() {
        if (getCodeLookupManager() == null)
            throw new ConfigurationException("The required property 'codeLookupManager' is missing.");
    }

    public CodeLookupManager getCodeLookupManager() {
        if (m_codeLookupManager == null) {
            // Allow this class to not be configured through Spring
            m_codeLookupManager = new CodeLookupManagerImpl();
        }
        return m_codeLookupManager;
    }

    public void setCodeLookupManager(CodeLookupManager codeLookupManager) {
        m_codeLookupManager = codeLookupManager;
    }

    private CodeLookupManager m_codeLookupManager;
}
