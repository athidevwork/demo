package dti.ci.relationshipmgr.struts;

import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.helpers.ICIConstants;
import dti.ci.relationshipmgr.RelationshipFields;
import dti.ci.relationshipmgr.RelationshipManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class CIS relationship page.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 10, 2005
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 * 09/23/2010       kshen       Added codes for bulk expire relationship.
 * 01/20/2011       Michael Li  Issue:116335
 * 04/19/2011       kshen       Refactor the method setReverseRelationIndicatorImg
 *                              and invoke the method setReverseRelationIndicatorImg when load the page.
 * 07/01/2013       hxk         Issue 141840
 *                              Add pk to request which CIS security needs.
 * 11/21/2013       hxk         Issue 150116
 *                              If we have a null arraylist, log a warning and return.  This will prevent
 *                              an error when we try to get the size of the arraylist if it doesn't exist.
 * 06/08/2017       jdingle     Issue 190314. Save performance.
 * 11/09/2018       Elvin       Issue 195835: grid replacement
 * ---------------------------------------------------
*/

public class RelationshipListAction extends MaintainEntityFolderBaseAction {

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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllRelationShip(mapping, form, request, response);
    }

    /**
     * Load all relationships of an entity.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllRelationShip(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRelationShip", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAllRelationShipResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            String entityId = inputRecord.getStringValueDefaultEmpty(ICIConstants.PK_PROPERTY);
            if (StringUtils.isBlank(entityId)) {
                entityId = inputRecord.getStringValueDefaultEmpty(ICIConstants.ENTITY_ID);
            } else {
                inputRecord.setFieldValue(ICIConstants.ENTITY_ID, entityId);
            }

            RecordSet rs = getRelationshipManager().loadAllRelationship(inputRecord);
            // Save relationship list to avoid loading from database again when saving a relationship record in the Modify Relationship page
            UserSessionManager.getInstance().getUserSession(request).set("ENTITY_RELATION_LIST", rs);

            loadListOfValues(request, form);

            // below method is not needed for jqx grid
            // but it is required to the old framework, displaying reverse relation indicator image in reverseRelationIndicatorImg column
            // so we will have a different column visibility setting here
            // if use jqx grid, we need to show reverseRelationIndicator column, hide reverseRelationIndicatorImg
            // for the old framework, we need to show reverseRelationIndicatorImg column but hide reverseRelationIndicator
            setReverseRelationIndicatorImg(rs, request);

            setDataBean(request, rs);
            loadGridHeader(request);

            // Always set expDate to ""
            inputRecord.setFieldValue(RelationshipFields.EXP_DATE, "");

            publishOutputRecord(request, inputRecord);

            request.setAttribute(RelationshipFields.ENTITY_CHILD_FK, inputRecord.getStringValue(RelationshipFields.ENTITY_CHILD_FK, ""));
            request.setAttribute(RelationshipFields.NAME_COMPUTED, inputRecord.getStringValue(RelationshipFields.NAME_COMPUTED, ""));

            addJsMessage();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load relationship page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRelationShip", af);
        }
        return af;
    }

    /**
     * Expire relation ships.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward expireRelationships(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "expireRelationShips", new Object[]{mapping, form, request, response});
        }

        String forwardString = "expireRelationshipsResult";

        try {
            if (isTokenValid(request, true)) {
                securePage(request, form);
                getRelationshipManager().expireRelationShips(getInputRecord(request));
            }
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to expire relationships.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "expireRelationships", af);
        }
        return af;
    }

    /**
     * Set reverse relation indicator img column
     *
     * @param rs relationship record set.
     * @param request
     * @throws Exception
     */
    protected void setReverseRelationIndicatorImg(RecordSet rs, HttpServletRequest request) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setReverseRelationIndicatorImg", new Object[]{rs, request});
        }
        int size = 0;
        String reverseRelationIndicator = "";

        ArrayList reverseRelationIndicatorLOV = (ArrayList) request.getAttribute("reverseRelationIndicator_GHLOV");
        
        // If we have a null arraylist above, we should get out, this could be due to configuration
        if (reverseRelationIndicatorLOV != null) {
            size = reverseRelationIndicatorLOV.size();
        } else {
            l.warning("Warning:  Unable to obtain information from request for reverseRelationIndicator_GHLOV in "  +
                      getClass().getName() + " setReverseRelationIndicatorImg.\n");
            return;
        }


        LabelValueBean lbl = null;
        String code = null;

        Iterator records =  rs.getRecords();
        while(records.hasNext()) {
            Record record = (Record) records.next();
            reverseRelationIndicator = record.getStringValue(RelationshipFields.REVERSE_RELATION_INDICATOR);
            if (!StringUtils.isBlank(reverseRelationIndicator)) {
                for (int i = 0; i < size; i++) {
                    lbl = (LabelValueBean) reverseRelationIndicatorLOV.get(i);
                    code = lbl.getValue();
                    if (reverseRelationIndicator.equals(code)) {
                        record.setFieldValue(RelationshipFields.REVERSE_RELATION_INDICATOR_IMG, lbl.getLabel());
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "setReverseRelationIndicatorImg");
    }

    private void addJsMessage() {
        MessageManager.getInstance().addJsMessage("ci.CIRelationship.selectRow.msg");
        MessageManager.getInstance().addJsMessage("ci.CIRelationship.expireDate.required.msg");
        MessageManager.getInstance().addJsMessage("ci.entity.message.client.relate");
        MessageManager.getInstance().addJsMessage("ci.entity.message.notesError.notAvailable");
    }

    public void verifyConfig() {
        if (getRelationshipManager() == null) {
            throw new ConfigurationException("The required property 'relationshipManager' is missing.");
        }
    }

    public RelationshipManager getRelationshipManager() {
        return m_relationshipManager;
    }

    public void setRelationshipManager(RelationshipManager relationshipManager) {
        m_relationshipManager = relationshipManager;
    }

    private RelationshipManager m_relationshipManager;
}
