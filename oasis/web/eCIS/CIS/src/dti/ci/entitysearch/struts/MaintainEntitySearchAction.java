package dti.ci.entitysearch.struts;

import dti.ci.entitymgr.EntityFields;
import dti.ci.entitysearch.EntitySearchFields;
import dti.ci.helpers.CILinkGenerator;
import dti.ci.helpers.ICIConstants;
import dti.oasis.accesstrailmgr.AccessTrailRequestIds;
import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entity List Action Class.
 * </p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 * Date:   Oct 20, 2003
 * <p/>
 * Revision Date    Revised By  Description
 * ------------------------------------------------------------------------
 * 03/30/2005      HXY         Made changes due to CIEntityListHelper is not a singleton class.
 * 04/07/2005      HXY         Used OasisFields to set up grid header.
 * 01/17/2007      Fred        Added call to CILinkGenerator.saveResultsToSession()
 * 08/12/2008      Jacky       Added support for parameter 'AgentAddlSql' from eFM
 * 08/14/2008      Jacky       Added support for return back to result list
 * 05/08/2009      kshen       Changed to support column order config.
 * 10/16/2009      Jacky       Add 'Jurisdiction' logic for issue #97673
 * 01/12/2010      Fred        Set criteria to session in any cases(iss101345)
 * 06/15/2010      Kenney      Issue#108868, clear the criteria cache in session when searching from global
 * 07/13/2010      shchen      Provide search on CIS relationships and entitytype for issue 106849.
 * 08/31/2010      Michael     for issue 111461.
 * 12/03/2015      ylu         Issue 165742: convert phone number field from "PH" to "Text" to support wild search driven by CI_PHONE_PART_SRCH
 * ------------------------------------------------------------------------
 */

public class MaintainEntitySearchAction extends MaintainEntitySearchBaseAction {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Handle unspecified action
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
        return initPage(mapping, form, request, response);
    }

    /**
     * Initialize Entity Search page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward initPage(ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        String methodName = "initPage";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String actionForward = LOAD_EMPTY_PAGE_FORWARD_NAME;
        try {
            // Secure the page and get the fields.
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            inputRecord.clear();

            request.setAttribute(ICIConstants.IS_NEW_VAL_PROPERTY, "Y");
            checkIfEnablePhoneNumberPartSearch(request);

            //Set an empty recordset
            setDataBean(request, new RecordSet());
            loadListOfValues(request, form);
            addJsMessages();

            //Remove search criteria from session
            request.getSession(false).removeAttribute(genCriteriaIDForSession(request, EntitySearchFields.SEARCH_CRITERIA_FOR_SESSION_PREFIX));
        } catch (Exception e) {
            actionForward = handleError(AppException.UNEXPECTED_ERROR, "Failed to load Entity Search page.", e, request, mapping);
            l.throwing(this.getClass().getName(), methodName, e);
        }
        ActionForward af = mapping.findForward(actionForward);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }

    /**
     * Process search
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward search(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request, HttpServletResponse response) {
        String methodName = "search";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String actionForward = LOAD_SEARCH_RESULT_FORWARD_NAME;
        try {
            /* Secure the page and get the fields. */
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            saveSearchCriteriaToSession(request, inputRecord);

            //Retrieve the record set based on criteria
            RecordSet rs = getEntitySearchManager().searchEntities(getSearchCriteriaRecord(request, inputRecord));
            new CILinkGenerator().saveResultsToSession(request, rs);

            commonLoadSearchResult(request, form, rs, inputRecord);
        } catch (AppException ae) {
            actionForward = LOAD_INITIAL_PAGE_FORWARD_NAME;
            l.throwing(this.getClass().getName(), methodName, ae);
        } catch (Exception e) {
            actionForward = handleError(AppException.UNEXPECTED_ERROR, "Failed to search entity.", e, request, mapping);
            l.throwing(this.getClass().getName(), methodName, e);
        }

        ActionForward af = mapping.findForward(actionForward);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }

    /**
     * Process global search
     * If found only one entity, forward to entity modify page, otherwise forward to entity search page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward globalSearch(ActionMapping mapping,
                                      ActionForm form,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        String methodName = "globalSearch";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String actionForward = "";
        try {
            securePage(request, form);

            Record searchCriteriaRecord = getSearchCriteriaRecord(request, getInputRecord(request));

            saveSearchCriteriaToSession(request, searchCriteriaRecord);

            RecordSet rs = getEntitySearchManager().searchEntities(searchCriteriaRecord);
            new CILinkGenerator().saveResultsToSession(request, rs);
            if (rs.getSize() == 1) {
                Record record = rs.getFirstRecord();

                if (ICIConstants.ENTITY_TYPE_PERSON_STRING.equalsIgnoreCase(EntityFields.getEntityType(record))) {
                    actionForward = ONE_RECORD_PERSON_FORWARD_NAME;
                } else {
                    actionForward = ONE_RECORD_ORGANIZATION_FORWARD_NAME;
                }

                request.setAttribute(EntityFields.ENTITY_TYPE, EntityFields.getEntityType(record));
                request.setAttribute(EntityFields.ENTITY_ID, EntityFields.getEntityId(record));
                request.setAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_TABLE_NAME, "ENTITY");
                request.setAttribute(AccessTrailRequestIds.ACCESS_TRAIL_SOURCE_RECORD_FK, EntityFields.getEntityId(record));
            } else {
                commonLoadSearchResult(request, form, rs, searchCriteriaRecord);
                actionForward = LOAD_SEARCH_RESULT_FORWARD_NAME;
            }
        } catch (AppException ae) {
            actionForward = LOAD_INITIAL_PAGE_FORWARD_NAME;
            l.throwing(this.getClass().getName(), methodName, ae);
        } catch (Exception e) {
            actionForward = handleError(AppException.UNEXPECTED_ERROR, "Failed to global search entity.", e, request, mapping);
            l.throwing(this.getClass().getName(), methodName, e);
        }

        ActionForward af = mapping.findForward(actionForward);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }

    /**
     * Process returnToList
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward returnToList(ActionMapping mapping,
                                      ActionForm form,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        String methodName = "returnToList";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String actionForward = ENTITY_LIST_FORWARD_NAME;
        try {
            Record inputRecord = (Record) request.getSession(false).getAttribute(genCriteriaIDForSession(request, EntitySearchFields.SEARCH_CRITERIA_FOR_SESSION_PREFIX));
            RequestStorageManager.getInstance().set("inputRecord", inputRecord);
        } catch (Exception e) {
            actionForward = handleError(AppException.UNEXPECTED_ERROR, "Failed to return to entity list.", e, request, mapping);
            l.throwing(this.getClass().getName(), methodName, e);
        }

        ActionForward af = mapping.findForward(actionForward);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }

    /**
     * Method used by search and globalSearch processes
     *
     * @param request
     * @param form
     * @param rs
     * @param inputRecord
     * @throws Exception
     */
    private void commonLoadSearchResult(HttpServletRequest request, ActionForm form, RecordSet rs, Record inputRecord) throws Exception {
        String methodName = "commonLoadSearchResult";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{request, form, rs, inputRecord});
        }

        checkIfEnablePhoneNumberPartSearch(request);

        request.setAttribute(ICIConstants.LIST_DISPLAYED_PROPERTY, YesNoFlag.getInstance(rs.getSize() > 0).getName());

        setDataBean(request, rs);

        publishOutputRecord(request, inputRecord);

        loadListOfValues(request, form);

        loadGridHeader(request);

        addJsMessages();

        l.exiting(getClass().getName(), methodName);
    }

    /**
     * Save the search criteria to session. It will be re-used when user clicks on back to entity list
     *
     * @param request
     * @param inputRecord
     */
    private void saveSearchCriteriaToSession(HttpServletRequest request, Record inputRecord) {
        request.getSession(false).setAttribute(genCriteriaIDForSession(request, EntitySearchFields.SEARCH_CRITERIA_FOR_SESSION_PREFIX), inputRecord);
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.entity.message.noRoles.associated");
        MessageManager.getInstance().addJsMessage("ci.common.error.value.mustEntered");
        MessageManager.getInstance().addJsMessage("ci.entity.message.searchCriteria.enter");
        MessageManager.getInstance().addJsMessage("ci.entity.message.selected.without");
        MessageManager.getInstance().addJsMessage("ci.common.error.record.select");
        MessageManager.getInstance().addJsMessage("ci.common.error.existRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.entitySearch.lastName");
        MessageManager.getInstance().addJsMessage("ci.common.error.entitySearch.longName");
        MessageManager.getInstance().addJsMessage("ci.entity.message.phoneNumber.invalid");
    }

    private static final String LOAD_INITIAL_PAGE_FORWARD_NAME = "loadInitialPage";
    private static final String LOAD_EMPTY_PAGE_FORWARD_NAME = "loadEmptyPage";
    private static final String LOAD_SEARCH_RESULT_FORWARD_NAME = "loadSearchResult";
    private static final String ONE_RECORD_PERSON_FORWARD_NAME = "oneRecordPerson";
    private static final String ONE_RECORD_ORGANIZATION_FORWARD_NAME = "oneRecordOrganization";
    private static final String ENTITY_LIST_FORWARD_NAME = "entityList";
}
