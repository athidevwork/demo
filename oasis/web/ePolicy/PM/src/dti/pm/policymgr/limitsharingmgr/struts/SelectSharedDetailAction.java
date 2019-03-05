package dti.pm.policymgr.limitsharingmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.limitsharingmgr.LimitSharingManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for select shared detail info
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 20, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/23/2016       lzhang      Modified loadAllAvailableSharedDetail:
 *                              not load risk header information
 *                              when get policy header information
 * ---------------------------------------------------
 */
public class SelectSharedDetailAction extends PMBaseAction {

    /**
     * This method is triggered automatically when there is no process parameter
     * sent in along the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllAvailableSharedDetail(mapping, form, request, response);
    }

    /**
     * Load all available components
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllAvailableSharedDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAvailableSharedDetail",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadResult";

        try {
            PolicyHeader policyHeader = getPolicyHeader(request);
            securePage(request, form);
            Record record = getInputRecord(request);
            loadListOfValues(request, form);
            RecordSet rs = getLimitSharingManager().loadAllAvailableSharedDetail(policyHeader, record);
            if (rs.getSize() == 0) {
                rs.getSummaryRecord().setFieldValue("hasRecords", YesNoFlag.N);
            }
            else {
                rs.getSummaryRecord().setFieldValue("hasRecords", YesNoFlag.Y);
            }
            addJsMessages();
            publishOutputRecord(request, rs.getSummaryRecord());
            loadGridHeader(request);
            setDataBean(request, rs);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load select shared detail page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAvailableSharedDetail", af);
        return af;
    }

    /**
     * Add Js messages
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.addSharedDetail.noData.error");
        MessageManager.getInstance().addJsMessage("pm.addSharedDetail.noSelection.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getLimitSharingManager() == null)
            throw new ConfigurationException("The required property 'limitSharingManager' is missing.");
    }

    public SelectSharedDetailAction() {
    }

    public LimitSharingManager getLimitSharingManager() {
        return limitSharingManager;
    }

    public void setLimitSharingManager(LimitSharingManager limitSharingManager) {
        this.limitSharingManager = limitSharingManager;
    }

    private LimitSharingManager limitSharingManager;
}
