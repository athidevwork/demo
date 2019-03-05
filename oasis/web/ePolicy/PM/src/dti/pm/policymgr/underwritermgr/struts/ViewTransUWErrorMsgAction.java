package dti.pm.policymgr.underwritermgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.struts.PMBaseAction;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for view error msg for transfer underwriter.
 * <p/>
 * <p>(C) 2006 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 17, 2008
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class ViewTransUWErrorMsgAction extends PMBaseAction {
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
        return viewErrorMsg(mapping, form, request, response);
    }


    /**
     * Method to view error message.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward viewErrorMsg(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "viewErrorMsg", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);           // Gets Policy Header
            UserSession userSession = UserSessionManager.getInstance().getUserSession();
            RecordSet rs = null;
            if (userSession.has("errMsgForTransUW")) {
                Record inputRecord = (Record) userSession.get("errMsgForTransUW");
                userSession.remove("errMsgForTransUW");
                rs = getErrorRecordSet(inputRecord);
            }
            else {
                rs = new RecordSet();
                ArrayList fields = new ArrayList();
                fields.add("rowId");
                fields.add("policyNo");
                fields.add("message");
                rs.addFieldNameCollection(fields);
            }
            // Sets data bean
            setDataBean(request, rs);
            // Loads list of values
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);

        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to view error msg.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "viewErrorMsg", af);
        return af;
    }


    //verify spring config
    public void verifyConfig() {
//        if (getAnchorColumnName() == null)
//            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    //parse comma string to recordset
    private RecordSet getErrorRecordSet(Record inputRecord) {
        String[] errMsg = inputRecord.getStringValue("errMsg").split(",");
        String[] policyNos = inputRecord.getStringValue("policyNos").split(",");
        RecordSet rs = new RecordSet();
        ArrayList fields = new ArrayList();
        fields.add("rowId");
        fields.add("policyNo");
        fields.add("message");
        rs.addFieldNameCollection(fields);

        for (int i = 0; i < policyNos.length; i++) {
            if (!StringUtils.isBlank(errMsg[i])) {
                Record record = new Record();
                record.setFieldValue("rowId", i + "");
                record.setFieldValue("policyNo", policyNos[i]);
                record.setFieldValue("message", errMsg[i]);
                rs.addRecord(record);
            }

        }
        return rs;
    }

}
