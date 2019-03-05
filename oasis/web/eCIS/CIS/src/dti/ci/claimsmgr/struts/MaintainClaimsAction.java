package dti.ci.claimsmgr.struts;

import dti.ci.claimsmgr.ClaimsManager;
import dti.ci.helpers.CILinkGenerator;
import dti.ci.helpers.ICIConstants;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.PageBean;
import dti.oasis.util.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

import static dti.ci.helpers.ICIClaimsConstants.*;
import static dti.oasis.request.RequestStorageIds.CURRENT_GRID_ID;

/**
 * Action Class CIS Claims page.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 7, 2005
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2007       Fred        Added calling CILinkGenerator.generateLink()
 * 06/09/2009       Leo         Issue 94697
 * 10/16/2009       Jacky       Add 'Jurisdiction' logic for issue #97673
 * 03/06/2012       Parker      130270. set CIS notes visiable for this business.
 * 02/13/2015       bzhu        Issue 160886. Move the filter logic via access control out of the system parameter.
 * 09/10/2015       dpang       Issue 165980. Remove the second getConnection to avoid failure of db connection closure.
 * 06/26/2017       ddai        Issue 185457. Add new claim no filter.
 * 12/08/2017       ylu         Issue 190017
 * 01/25/2018       ylu         Issue 190665: in companion grid, use accurate fieldId name to for Claim Access Control
 * 04/19/2018       jld         Issue 192609: Refactor for eCIS.
 * 05/24/2018       ylu         Issue 192609: accordingly update for refactor testing.
 * 10/04/2018       hxk         Issue 191329
 *                              1)  Add parm to loadCompanion call.
 * 11/12/2018       hxk         Issue 196950
 *                              1)  Set restrictCaseB attribute in request appropriately
 * ---------------------------------------------------
*/

public class MaintainClaimsAction extends CIBaseAction {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Unspecified
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
        return loadClaim(mapping, form, request, response);
    }

    /**
     * Get Claim data.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadClaim(ActionMapping mapping, ActionForm form,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadClaim", new Object[]{mapping, form, request, response});
        }

        String forwardString = "success";
        String message = "";

        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);

            /* validate */
            String entityId = request.getParameter(PK_PROPERTY);
            String entityType = request.getParameter(ENTITY_TYPE_PROPERTY);
            String entityName = request.getParameter(ENTITY_NAME_PROPERTY);

            /* set menu beans Search & Select an entity type of  'organization'.*/
            if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
                checkCisFolderMenu(request);
            }

            inputRecord.setFieldValue(ENTITY_ID,entityId);
            String claimPKStr = "";
            if (inputRecord.hasFieldValue(CLAIMPK)) {
                claimPKStr = inputRecord.getStringValue(CLAIMPK);
            }

            Record claimRec = new Record();
            if (!StringUtils.isBlank(claimPKStr)) {
                if (!FormatUtils.isLong(claimPKStr)) {
                    String msg = "Claim PK [" + claimPKStr + "] should be a number.";
                    l.severe(msg);
                    throw new IllegalArgumentException(msg);
                }
                claimRec.setFieldValue("claimId",claimPKStr);
            } else {
                claimRec = getClaimsManager().loadFirstClaim(inputRecord);
            }

            inputRecord.setFieldValue("claimId",claimRec.getFieldValue("claimId"));

            PageBean pageBean = ((PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN));
            String pageCode = "";
            if (pageBean != null) {
                pageCode = pageBean.getId();
            } else {
                pageCode = "NO PAGE CODE";
            }
            inputRecord.setFieldValue("pageCode",pageCode);

            // load Claim Data
            RecordSet cmInfoRecordSet = getClaimsManager().loadClaimInfo(inputRecord);
            Record outputRecord = new Record();
            request.setAttribute("restrictB", "N");
            request.setAttribute("restrictCaseB", "N");
            if (cmInfoRecordSet.getSize() > 0) {
                outputRecord = cmInfoRecordSet.getFirstRecord();
                if (outputRecord.hasFieldValue("restrictB") && "Y".equalsIgnoreCase(outputRecord.getStringValue("restrictB"))) {
                    request.setAttribute("restrictB", "Y");
                }
                if (outputRecord.hasFieldValue("restrictCaseB") && "Y".equalsIgnoreCase(outputRecord.getStringValue("restrictCaseB"))) {
                    request.setAttribute("restrictCaseB", "Y");
                }
            }

            RecordSet participantRs = new RecordSet();
            participantRs = getClaimsManager().loadClaimParticipants(inputRecord, request);

            RecordSet companionRs = new RecordSet();
            companionRs = getClaimsManager().loadCompanion(inputRecord, request);
            setCisHeaderFields(request);
            publishOutputRecord(request,outputRecord);
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PARTICIPANT_GRID_ID);
            setDataBean(request, participantRs, PARTICIPANT_GRID_ID);
            loadGridHeader(request,null,PARTICIPANT_GRID_ID,PARTICIPANTS_LAYER);
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COMPANION_GRID_ID);
            setDataBean(request, companionRs, COMPANION_GRID_ID);
            loadGridHeader(request,null,COMPANION_GRID_ID,COMPANION_CLAIMS_LAYER);
            setEntityCommonInfoToRequest(request, inputRecord);
            loadListOfValues(request,form);
            request.setAttribute("claimId", outputRecord.getStringValue("claimId",""));
            request.setAttribute(CASE_PK, outputRecord.getStringValue("caseId",""));

            /* Gets links for Paging */
            new CILinkGenerator().generateLink(request, entityId, this.getClass().getName());
            saveToken(request);
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleError(
                    AppException.UNEXPECTED_ERROR, "Failed to load Claims page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadClaim", af);
        }
        return af;
    }

    //add js message
    private void addJsMessages() {
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        l.entering(getClass().getName(), "getAnchorColumnName");
        String anchorName;
        if (RequestStorageManager.getInstance().has(CURRENT_GRID_ID)) {
            String currentGridId = (String) RequestStorageManager.getInstance().get(CURRENT_GRID_ID);
            if (currentGridId.equals(PARTICIPANT_GRID_ID)) {
                anchorName = getParticipantsGridAnchorColumnName();
            } else if (currentGridId.equals(COMPANION_GRID_ID)) {
                anchorName = getCompanionGridAnchorColumnName();
            } else {
                anchorName = super.getAnchorColumnName();
            }
        } else {
            anchorName = super.getAnchorColumnName();
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAnchorColumnName", anchorName);
        }
        return anchorName;
    }

    /**
     * verify configuration method
     */
    public void verifyConfig() {
        if (getClaimsManager() == null)
            throw new ConfigurationException("The required property 'entityGlanceManager' is missing.");

        if (getParticipantsGridAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'participantsGridAnchorColumnName' is missing.");

        if (getCompanionGridAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'companionGridAnchorColumnName' is missing.");

    }

    public ClaimsManager getClaimsManager() {
        return m_claimsManager;
    }

    public void setClaimsManager(ClaimsManager m_claimsManager) {
        this.m_claimsManager = m_claimsManager;
    }

    public String getParticipantsGridAnchorColumnName() {
        return participantsGridAnchorColumnName;
    }

    public void setParticipantsGridAnchorColumnName(String participantsGridAnchorColumnName) {
        this.participantsGridAnchorColumnName = participantsGridAnchorColumnName;
    }

    public String getCompanionGridAnchorColumnName() {
        return companionGridAnchorColumnName;
    }

    public void setCompanionGridAnchorColumnName(String companionGridAnchorColumnName) {
        this.companionGridAnchorColumnName = companionGridAnchorColumnName;
    }
    private String participantsGridAnchorColumnName;
    private String companionGridAnchorColumnName;
    private ClaimsManager m_claimsManager;
}
