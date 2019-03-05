package dti.pm.policymgr.lockmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class, matain unlock policies action
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 7, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/12/2010       fcb         107461: unlockPolicy added.
 * 12/17/2014       fcb         149906: unlockPolicy removed due to replacing its use with MaintainLockAction.unlockPolicy
 * ---------------------------------------------------
 */
public class MaintainUnlockPolicyAction extends PMBaseAction {

    public ActionForward loadAllLockedPolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllLockedPolicy", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            //get query parameters from request
            Record inputRecord = getInputRecord(request);

            // Attempt to get the gridRecordSet out of the request.  This will be populated
            // on a validation error to provide data to reload the page.
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);

            // load all locked policy data
            if (rs == null) {
                //prepare record load processor
                RecordLoadProcessor lp = AddSelectIndLoadProcessor.getInstance();
                rs = getLockManager().loadAllLockedPolicy(inputRecord, lp);
            }
            //set isUnlockAvailable indicator
            if (rs.getSize() == 0) {
                request.setAttribute(IS_UNLOCK_AVAILABLE, "N");
                if (!(inputRecord.hasField("noLoadData") && inputRecord.getBooleanValue("noLoadData").booleanValue())) {
                    MessageManager.getInstance().addErrorMessage("pm.maitainUnlockPolicy.noData.error");
                }
            }
            else {
                request.setAttribute(IS_UNLOCK_AVAILABLE, "Y");

                //add warning message, policy count exceeds the maxium value
                if (rs.getSize() > 0 &&
                    rs.getFirstRecord().hasField("maxRows")) {
                    int intTotalRowsReturned = rs.getSize();
                    int intMaxRowsConfigured = Integer.parseInt(rs.getFirstRecord().getStringValue("maxRows"));
                    if (intTotalRowsReturned >= intMaxRowsConfigured) {
                        MessageManager.getInstance().addWarningMessage("pm.maitainUnlockPolicy.abortSearch.listHeader",
                            new String[]{String.valueOf(intMaxRowsConfigured)});
                    }
                }
            }

            setDataBean(request, rs);

            // Load the list of values after loading the data
            loadListOfValues(request, form);
            // Load tail grid header
            loadGridHeader(request);
            //add messages for JavaScript usage            
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the locked policies page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllLockedPolicy", af);
        }
        return af;
    }

    public ActionForward unlockAllPolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "unlockAllPolicy", new Object[]{mapping, form, request, response});
        }

        String forwardString = "viewUnlockResults";
        try {

            RecordSet inputRecords = getInputRecordSet(request);
            //unlock all locked policy
            getLockManager().unlockAllPolicy(inputRecords);
            //save records back to request, and these records will be re-displayed with unlick infos
            request.setAttribute(dti.pm.core.http.RequestIds.GRID_RECORD_SET, inputRecords);
        }
        catch (Exception e) {
            forwardString = handleError("pm.maitainUnlockPolicy.fail.error",
                "Failed to unlock policies.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "unlockAllPolicy", af);
        }
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maitainUnlockPolicy.noPolicySelected.error");
    }

    private static final String IS_UNLOCK_AVAILABLE = "isUnlockAvailable";
}
