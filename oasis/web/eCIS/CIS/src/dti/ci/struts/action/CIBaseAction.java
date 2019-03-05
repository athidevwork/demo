package dti.ci.struts.action;

import dti.ci.core.CIFields;
import dti.ci.core.EntityInfo;
import dti.ci.core.error.EntityPkNotExistsException;
import dti.ci.core.error.InvalidEntityPkException;
import dti.ci.core.struts.MaintainCIBaseAction;
import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.entitymgr.EntityManager;
import dti.ci.entitysearch.EntitySearchFields;
import dti.ci.helpers.CIEntityModifyHelper;
import dti.ci.helpers.ICIConstants;
import dti.cs.notemgr.NoteManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.navigationmgr.NavigationManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.security.Authenticator;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.BaseAction;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisElements;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.tags.OasisWebElement;
import dti.oasis.util.*;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.taglib.html.Constants;
import org.apache.struts.util.LabelValueBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CI base action class.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 29, 2005
 *
 * @author Hong Yuan
 * Revision Date    Revised By  Description
 * ----------------------------------------------------------------------------
 * 05/10/2005       HXY         Added method getSystemParameter.
 * 01/18/2008       Kenney      incorporate framework changes that are utilized by
 *                              ePolicy
 *                              1. Make the CIBaseAction inherit from BaseAction
 *                              2. Add loadFields and hasValidSaveToken method *
 * 08/13/2009       Guang       94091: refactored getLovLabelsForInitialValues from MaintainAgentction
 * 12/04/2009       James       Move common methods to BaseAction
 * 12/13/2010       Kenney      Added securePage and removeActions
 * 01/20/2011       Michael Li  Issue:116335
 * 12/22/2011       Michael Li  to add new method recordToString
 * 08/22/2013       Parker      Issue#142990 default externalId to clientId in File Notes Page
 * 11/13/2014       Elvin       Issue 158261: enale resultBackList in setCisHeaderFields if system has searched entities
 * 01/18/2018       ylu         Issue 189050: pass CIS header field value for display
 * 02/15/2018       jld         Issue 189050: Handle CIS header field update when coming from global search.
 * 03/02/2018       cesar       #189605 - call BaseAction.saveToken() to be used for CSRFInterceptor
 * 06/12/2018       dpang       Issue 109161: check if entity pk exists in request before retrieving.
 * ---------------------------------------------------------------------------
 */

public abstract class CIBaseAction extends BaseAction implements ICIConstants {
    private final Logger l = LogUtils.getLogger(getClass());

    private static HashMap pageMenuItemHM = null;

    /**
     * Method that returns the action forward mapping for the request after loading entity information for the
     * provided entity attributes (via request parameter).
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "execute", new Object[]{mapping, form, request, response});
        }

        ActionForward af = null;

        boolean isAjaxRequest = false;
        boolean retrieveEntity = false;

        EntityInfo entityInfo = null;
        String entityId = null;

        try {
            isAjaxRequest = isAjaxRequest(request);

            if (!isAjaxRequest) {
                // Initial token and save in progress property.
                initializeLoadRequestForSave(request, getTokenConstant());
            }

            // Check if we need to retrieve entity info.
            retrieveEntity = needRetrieveEntityInfo(request);

            if (retrieveEntity) {
                // Get entity Info.
                entityId = getEntityIdForMaintainEntityAction(request);
                // Check if entity PK is povoided.
                checkEntityPk(entityId);

                // Load entity info.
                entityInfo = getEntityInfoBean(request, entityId, false);

                setEntityInfoToRequest(request, entityInfo);

                // Set header fields to request.
                setCisHeaderFields(request);
            }

            // Process action.
            af = super.execute(mapping, form, request, response);

            if (!isAjaxRequest && retrieveEntity && needLoadEntityFolderInfo(request)) {
                OasisFields fields = ActionHelper.getFields(request);
                if (fields == null) {
                    // If the fields is null in request, call securePage to load fields.
                    securePage(request, form);
                }

                // If the current request is not from AJAX, generate Previous/Next Link, and show/hide tabs for person/org.

                if (!StringUtils.isBlank(entityInfo.getEntityType()) && entityInfo.getEntityType().charAt(0) == CIFields.ENTITY_TYPE_ORG_CHAR) {
                    // Handle CIS folder menus for organization.
                    checkCisFolderMenu(request);
                }

                // Generate Previous, Next... navigation links of CIS entity folder.
                generateLink(request, entityId, this.getClass().getName());
            }
        } catch (EntityPkNotExistsException|InvalidEntityPkException e) {
            // Forward to entity search page for invalid entity PK.
            af = mapping.findForward(CIFields.INVALID_ENTITY_PK_FORWARD);

        } catch (Exception e) {
            if (isAjaxRequest) {
                af = null;
                handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to process request", e, response);
            } else {
                AppException ae = ExceptionHelper.getInstance().handleException("Failed to process request.", e);
                l.throwing(getClass().getName(), "execute", ae);

                String forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to process request.", e, request, mapping);
                af = mapping.findForward(forwardString);
            }
        }

        if (isSaveInProgress()) {
            if (isAjaxRequest && MessageManager.getInstance().hasConfirmationPrompts()) {
                // The Ajax request was a save request, and did not complete because it requires confirmations.
                // Remove the save in-progress session attribute to indicate that the save process has been exited.
                removeSaveInProgressIndicator();
            } else if (request.getAttribute(IOasisAction.KEY_ERROR) == null
                    && request.getAttribute(IOasisAction.KEY_VALIDATION_ERROR) == null
                    && !MessageManager.getInstance().hasErrorMessages()) {
                saveProcessSucceeded(request, getTokenConstant());
            } else {
                saveProcessFailed(request);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "execute", af);
        }
        return af;
    }

    /**
     * Check if we need to load entity info for the request.
     *
     *
     * @param request
     * @return
     */
    protected boolean needRetrieveEntityInfo(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "needRetrieveEntityInfo", new Object[]{request});
        }

        boolean retrieve = false;

        if (isMaintainEntityAction(request) && !StringUtils.isBlank(getEntityIdForMaintainEntityAction(request))) {
            if (!skipRetrieveEntityInfo(request)) {
                retrieve = true;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "needRetrieveEntityInfo", retrieve);
        }

        return retrieve;
    }

    /**
     * Check if the current request should skip load entity info.
     *
     * This function could be override by sub class. The typically codes are:
     * <code>
     *     <br/>
     *     // ...<br/>
     *     Record input = getInputRequest(request);<br/>
     *     String process = input.getStringValue(RequestIds.PROCESS, "");<br/>
     *     <br/>
     *     if ("someProcessNeedToSkipLoadEntity".equals(process) {<br/>
     *     &nbsp;&nbsp;&nbsp;&nbsp;return true;<br/>
     *     }<br/>
     *     <br/>
     *     return false;<br/>
     *     // ...<br/>
     * </code>
     *
     * @param request
     * @return
     */
    protected boolean skipRetrieveEntityInfo(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "skipRetrieveEntityInfo", new Object[]{request});
        }

        boolean skip = false;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "skipRetrieveEntityInfo", skip);
        }
        return skip;
    }

    protected boolean needLoadEntityFolderInfo(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "needLoadEntityFolderInfo", new Object[]{request});
        }

        boolean load = false;
        if (isMaintainEntityFolderAction(request)) {
            if(!skipLoadEntityFolderInfo(request)) {
                load = true;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "needLoadEntityFolderInfo", load);
        }
        return load;
    }

    protected boolean skipLoadEntityFolderInfo(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "skipLoadEntityFolderInfo", new Object[]{request});
        }

        boolean skip = false;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "skipLoadEntityFolderInfo", skip);
        }
        return false;
    }

    /**
     * Check if the current action maintains entity info.
     *
     * This method will determine if the current action maintains entity info by checking if the current object extends
     * the abstract class MaintainCIBaseAction by default.
     *
     * We can optionally overwrite this method in sub class.
     *
     * @param request
     * @return
     */
    protected boolean isMaintainEntityAction(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isMaintainEntityAction", new Object[]{request});
        }

        boolean result = isMaintainEntityFolderAction(request) || (this instanceof MaintainCIBaseAction);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isMaintainEntityAction", result);
        }
        return result;
    }

    /**
     * Check if the current action is for entity folder tabs.
     *
     * This method will determine if the current action maintains entity info by checking if the current object extends
     * the abstract class MaintainCIBaseAction by default.
     *
     * We can optionally overwrite this method in sub class.
     *
     * @param request
     * @return
     */
    protected boolean isMaintainEntityFolderAction(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isMaintainEntityFolderAction", new Object[]{request});
        }

        boolean result = (this instanceof MaintainEntityFolderBaseAction);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isMaintainEntityFolderAction", result);
        }
        return (this instanceof MaintainEntityFolderBaseAction);
    }

    protected String getEntityIdForMaintainEntityAction(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityId", new Object[]{request});
        }

        Record inputRecord = getInputRecord(request);

        String entityId = inputRecord.getStringValueDefaultEmpty(CIFields.PK);
        if (StringUtils.isBlank(entityId)) {
            entityId = inputRecord.getStringValueDefaultEmpty(CIFields.ENTITY_ID);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityIdForMaintainEntityAction", entityId);
        }
        return entityId;
    }

    /**
     * Load entity basic information of given entity pk
     *
     * @param request
     * @param entityId
     * @param forceRefresh
     * @return
     * @throws Exception
     */
    protected EntityInfo getEntityInfoBean(HttpServletRequest request, String entityId, boolean forceRefresh) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityInfoBean", new Object[]{request, entityId, forceRefresh});
        }

        checkEntityPk(entityId);

        EntityInfo result = null;
        boolean needRefresh = false;
        if (forceRefresh) {
            needRefresh = true;
        } else {
            EntityInfo previousEntityInfo = (EntityInfo) request.getSession().getAttribute("EntityInfo");
            if (previousEntityInfo != null && !StringUtils.isBlank(previousEntityInfo.getEntityId())) {
                if (!entityId.equals(previousEntityInfo.getEntityId())) {
                    needRefresh = true;
                } else {
                    result = previousEntityInfo;
                }
            } else {
                needRefresh = true;
            }
        }

        if (needRefresh) {
            Record queryRecord = new Record();
            queryRecord.setFieldValue(CIFields.ENTITY_ID, entityId);
            result = getEntityManager().getEntityInfo(queryRecord);

            // load noteExistB
            queryRecord.setFieldValue("sourceTableName", "ENTITY");
            queryRecord.setFieldValue("sourceRecordId", entityId);
            queryRecord.setFieldValue("noteGroupCode", "ENTITY");
            queryRecord.setFieldValue("noteCategoryCode", "ALL");
            RecordSet noteRs = getNoteManager().loadNoteList(queryRecord);
            String noteExistsB = noteRs.getSize() > 0 ? "Yes" : "No";
            result.setNoteExistB(noteExistsB);

            request.getSession().setAttribute("EntityInfo", result);
        }

        if (result == null || StringUtils.isBlank(result.getEntityId())) {
            // Add error message.
            MessageManager.getInstance().addErrorMessage("ci.cicore.error.entityPkInvalid", new String[]{ entityId });

            // Throw InvalidEntityPkException
            InvalidEntityPkException e = new InvalidEntityPkException(entityId);
            l.throwing(getClass().getName(), "getEntityInfoBean", e);
            throw e;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityInfoBean", result);
        }
        return result;
    }

    protected void setEntityInfoToRequest(HttpServletRequest request, EntityInfo entityInfo) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setEntityInfoToRequest", new Object[]{request, entityInfo});
        }

        request.setAttribute(CIFields.PK, entityInfo.getEntityId());
        request.setAttribute(CIFields.ENTITY_NAME, entityInfo.getEntityName());
        request.setAttribute(CIFields.ENTITY_TYPE, entityInfo.getEntityType());
        request.setAttribute(CIFields.CIS_HEADER_CLIENT_ID, entityInfo.getClientId());
        request.setAttribute(CIFields.CIS_HEADER_LEGACY_DATA_ID, entityInfo.getLegacyDataID());
        request.setAttribute(CIFields.CIS_HEADER_REFERENCE_NUMBER, entityInfo.getReferenceNumber());
        request.setAttribute(CIFields.CIS_HEADER_NOTE_IND, entityInfo.getNoteExistB());

        l.exiting(getClass().getName(), "setEntityInfoToRequest");
    }

    /**
     * Generate hyperlink then put them into request
     *
     * @param request
     * @param entityPk
     * @param actionClass
     */
    protected void generateLink(HttpServletRequest request,
                                String entityPk,
                                String actionClass) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateLink", new Object[]{request, entityPk, actionClass});
        }

        //To judge if the page have Previous/Next navigation
        OasisFields fields = ActionHelper.getFields(request);
        OasisFormField linkField = (OasisFormField) fields.get(CIFields.NAVIGATION_PAGE_CODE);
        if (linkField == null || !linkField.getIsVisible()) {
            request.setAttribute(CIFields.INCLUDE_MULTI_ENTITY, VALUE_FOR_NO);
            return;
        }
        //Set the display type of link field to null
        linkField.setDisplayType(null);
        //Create the hyperlink
        String result = (String) request.getSession(false).getAttribute(CIFields.ENTITY_SELECT_RESULTS);
        String reqUrl = request.getRequestURI();
        String head = "-1";
        String end = "0";
        String orgUrl = "ciEntityOrgModify.do";
        String perUrl = "ciEntityPersonModify.do";

        if (result != null) {
            String[] splitResult = result.split(CIFields.ENTITY_SPLIT_SIGN);
            int total = splitResult.length / 3;
            //Only one record, needn't navigation
            if (total <= 1) {
                request.setAttribute(CIFields.INCLUDE_MULTI_ENTITY, CIFields.VALUE_FOR_NO);
                return;
            }
            int count = 0;
            try {
                // Identify the current position
                for (; count < splitResult.length - 3; count += 3) {
                    if (entityPk.equals(splitResult[count])) {
                        break;
                    }
                }
                StringBuffer sb = new StringBuffer();
                //At first position
                if (count == 0) {
                    sb.append("1").append(CIFields.ENTITY_SPLIT_SIGN).
                            append(String.valueOf(total)).append(CIFields.ENTITY_SPLIT_SIGN).
                            append(head).append(CIFields.ENTITY_SPLIT_SIGN).
                            append(splitResult[count]).append(CIFields.ENTITY_SPLIT_SIGN);
                    boolean isNextOrg = "O".equalsIgnoreCase(splitResult[4]);
                    if (isNextOrg) {
                        reqUrl = reqUrl.replaceFirst(perUrl, orgUrl);
                        sb.append(reqUrl).append("?").
                                append(CIFields.PK_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[3]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_TYPE_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[4]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_NAME_PROPERTY).append(CIFields.EQUAL_SIGN).append(encodeEntityName(splitResult[5]));
                    } else {
                        reqUrl = reqUrl.replaceFirst(orgUrl, perUrl);
                        sb.append(reqUrl).append("?").
                                append(CIFields.PK_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[3]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_TYPE_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[4]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_NAME_PROPERTY).append(CIFields.EQUAL_SIGN).append(encodeEntityName(splitResult[5]));
                    }
                    request.setAttribute(CIFields.ENTITY_SELECT_RESULTS, sb.toString());
                    request.setAttribute(CIFields.INCLUDE_MULTI_ENTITY, CIFields.VALUE_FOR_YES);
                }
                //At last position
                else if (count == ((total - 1) * 3)) {
                    sb.append(String.valueOf(total)).append(CIFields.ENTITY_SPLIT_SIGN).
                            append(String.valueOf(total)).append(CIFields.ENTITY_SPLIT_SIGN);
                    boolean isNextOrg = "O".equalsIgnoreCase(splitResult[count - 2]);
                    if (isNextOrg) {
                        reqUrl = reqUrl.replaceFirst(perUrl, orgUrl);
                        sb.append(reqUrl).append("?").
                                append(CIFields.PK_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[count - 3]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_TYPE_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[count - 2]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_NAME_PROPERTY).append(CIFields.EQUAL_SIGN).append(encodeEntityName(splitResult[count - 1])).
                                append(CIFields.ENTITY_SPLIT_SIGN);
                    } else {
                        reqUrl = reqUrl.replaceFirst(orgUrl, perUrl);
                        sb.append(reqUrl).append("?").
                                append(CIFields.PK_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[count - 3]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_TYPE_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[count - 2]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_NAME_PROPERTY).append(CIFields.EQUAL_SIGN).append(encodeEntityName(splitResult[count - 1])).
                                append(CIFields.ENTITY_SPLIT_SIGN);
                    }
                    sb.append(splitResult[count]).append(CIFields.ENTITY_SPLIT_SIGN).
                            append(end);
                    request.setAttribute(CIFields.ENTITY_SELECT_RESULTS, sb.toString());
                    request.setAttribute(CIFields.INCLUDE_MULTI_ENTITY, CIFields.VALUE_FOR_YES);
                }
                //At middle position
                else {
                    sb.append(String.valueOf(count / 3 + 1)).append(CIFields.ENTITY_SPLIT_SIGN).
                            append(String.valueOf(total)).append(CIFields.ENTITY_SPLIT_SIGN);
                    //Previous record
                    boolean isNextOrg = "O".equalsIgnoreCase(splitResult[count - 2]);
                    if (isNextOrg) {
                        reqUrl = reqUrl.replaceFirst(perUrl, orgUrl);
                        sb.append(reqUrl).append("?").
                                append(CIFields.PK_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[count - 3]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_TYPE_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[count - 2]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_NAME_PROPERTY).append(CIFields.EQUAL_SIGN).append(encodeEntityName(splitResult[count - 1])).
                                append(CIFields.ENTITY_SPLIT_SIGN);
                    } else {
                        reqUrl = reqUrl.replaceFirst(orgUrl, perUrl);
                        sb.append(reqUrl).append("?").
                                append(CIFields.PK_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[count - 3]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_TYPE_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[count - 2]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_NAME_PROPERTY).append(CIFields.EQUAL_SIGN).append(encodeEntityName(splitResult[count - 1])).
                                append(CIFields.ENTITY_SPLIT_SIGN);
                    }
                    //Current record
                    sb.append(splitResult[count]).append(CIFields.ENTITY_SPLIT_SIGN);
                    //Next record
                    isNextOrg = "O".equalsIgnoreCase(splitResult[count + 4]);
                    if (isNextOrg) {
                        reqUrl = reqUrl.replaceFirst(perUrl, orgUrl);
                        sb.append(reqUrl).append("?").
                                append(CIFields.PK_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[count + 3]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_TYPE_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[count + 4]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_NAME_PROPERTY).append(CIFields.EQUAL_SIGN).append(encodeEntityName(splitResult[count + 5]));
                    } else {
                        reqUrl = reqUrl.replaceFirst(orgUrl, perUrl);
                        sb.append(reqUrl).append("?").
                                append(CIFields.PK_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[count + 3]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_TYPE_PROPERTY).append(CIFields.EQUAL_SIGN).append(splitResult[count + 4]).append(CIFields.URL_ELEMENT_SIGN).
                                append(CIFields.ENTITY_NAME_PROPERTY).append(CIFields.EQUAL_SIGN).append(encodeEntityName(splitResult[count + 5]));
                    }
                    request.setAttribute(CIFields.ENTITY_SELECT_RESULTS, sb.toString());
                    request.setAttribute(CIFields.INCLUDE_MULTI_ENTITY, CIFields.VALUE_FOR_YES);
                }
            } catch (Exception e) {
                request.setAttribute(CIFields.INCLUDE_MULTI_ENTITY, VALUE_FOR_NO);
                l.finest("Split the results with error, method generateLink exit.");
            }
        } else {
            request.setAttribute(CIFields.INCLUDE_MULTI_ENTITY, CIFields.VALUE_FOR_NO);
            l.finest("The selected entity less than 2, needn't navigate, method generateLink exit.");
        }
    }

    /**
     * Encode & in entityName field
     * @param entityName
     * @return
     */
    private String encodeEntityName(String entityName) {
        if (!StringUtils.isBlank(entityName)) {
            //%26 will be transformed to & in js method navigateRecord, change to %2526
            entityName = entityName.replaceAll("&", "%2526");
        }
        return entityName;
    }

    /**
     * Throws EntityPkNotExistsException if entity ID is empty.
     * @param entityId
     */
    protected void checkEntityPk(String entityId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkEntityPk", new Object[]{entityId});
        }

        if (StringUtils.isBlank(entityId)) {
            // Add error message.
            MessageManager.getInstance().addErrorMessage("ci.cicore.error.entityPkInvalid", new String[]{ entityId });

            // Throw EntityPkNotExistsException.
            EntityPkNotExistsException e = new EntityPkNotExistsException();
            l.throwing(getClass().getName(), "checkEntityPk", e);
            throw e;
        }

        l.exiting(getClass().getName(), "checkEntityPk");
    }

    /**
     * Closes a JDBC connection object.
     *
     * @param conn Connection to be closed.
     */
    protected final void closeConnection(Connection conn) {
        String methodName = "closeConnection";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName, conn);
        if (conn != null) {
            DatabaseUtils.close(conn);
        }

    }

    public String getSystemParameter(String dbPoolID, String sysParam, String sysParamDefaultVal) {
        String methodName = "getSystemParameter";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName, new Object[]{dbPoolID, sysParam, sysParamDefaultVal});
        SysParmProvider prov = SysParmProvider.getInstance();
        String sysParamValue = sysParamDefaultVal;
        try {
            sysParamValue = prov.get(dbPoolID, sysParam);
            if (StringUtils.isBlank(sysParamValue)) {
                lggr.fine("setting value for " + sysParam + " to default " + sysParamDefaultVal);
                sysParamValue = sysParamDefaultVal;
            }
            lggr.fine("system parameter " + sysParam + " = " + sysParamValue);
        } catch (Exception ignore) {
            lggr.fine("exception getting sys parm " + sysParam +
                    " from parm provider:  " + ignore.toString());
            sysParamValue = sysParamDefaultVal;
        }
        lggr.exiting(this.getClass().getName(), methodName, sysParamValue);
        return sysParamValue;
    }

    /**
     * Disable an OasisWebElement in the OasisElements collection.
     *
     * @param elements OasisElements collection
     * @param name     Name of OasisWebElement.
     */
    protected void disableElement(OasisElements elements, String name) {
        Logger l = LogUtils.enterLog(getClass(), "disableElement",
                new Object[]{elements, name});
        if (elements == null)
            throw new IllegalArgumentException("OasisElements is null.");
        OasisWebElement el = (OasisWebElement) elements.get(name);
        if (el == null)
            throw new IllegalArgumentException("Oasis Element named " + name + " not found in OasisElements.");
        el.setAvailable(false);

        l.exiting(getClass().getName(), "disableElement");
    }

    /**
     * Returns the Token Constant to use.  Return null to utilize default Struts
     * token processing.  Otherwise, override this method to return a different token. This
     * will cause the overridden code within saveToken and isTokenValid to be executed
     * rather than the default Struts code.
     *
     * @return null
     */
    protected String getTokenConstant() {
        return null;
    }

    /**
     * Return <code>true</code> if there is a transaction token stored in
     * the user's current session, and the value submitted as a request
     * parameter with this action matches it.  Returns <code>false</code>
     * <ul>
     * <li>No session associated with this request</li>
     * <li>No transaction token saved in the session</li>
     * <li>No transaction token included as a request parameter</li>
     * <li>The included transaction token value does not match the
     * transaction token in the user's session</li>
     * </ul>
     * If yopu override getTokenConstant, this will utilizes that constant rather than
     * the default Struts token.
     *
     * @param request The servlet request we are processing
     * @param reset   Should we reset the token after checking it?
     */
    protected boolean isTokenValid(HttpServletRequest request, boolean reset) {
        Logger l = LogUtils.enterLog(getClass(), "isTokenValid",
                new Object[]{request, new Boolean(reset)});
        boolean valid = false;
        if (getTokenConstant() == null) {
            valid = super.isTokenValid(request, reset);
        } else {
            String token = null;
            String saved = null;
            // Retrieve the current session for this request
            HttpSession session = request.getSession(false);
            if (session != null) {
                // Retrieve the transaction token from this session, and
                // reset it if requested
                saved = (String) session.getAttribute(getTokenConstant());
                if (saved != null) {
                    if (reset) {
                        this.resetToken(request);
                    }

                    // Retrieve the transaction token included in this request
                    token = request.getParameter(Constants.TOKEN_KEY);
                    if (token != null)
                        valid = (saved.equals(token));
                }
            }
        }
        l.exiting(getClass().getName(), "isTokenValid", new Boolean(valid));
        return valid;

    }

    /**
     * Save a new transaction token in the user's current session, creating
     * a new session if necessary.  If you override getTokenConstant, this will save
     * the token using the constant rather than the default STRUTS token.
     *
     * @param request The servlet request we are processing
     */
    protected void saveToken(HttpServletRequest request) {
        Logger l = LogUtils.enterLog(getClass(), "saveToken", request);

        saveToken(request, getTokenConstant());

        l.exiting(getClass().getName(), "saveToken");

    }

    /**
     * Reset the saved transaction token in the user's session.  This
     * indicates that transactional token checking will not be needed
     * on the next request that is submitted.
     * If you override getTokenConstant, this will utilize that constant
     * rather than the default Struts token.
     *
     * @param request The servlet request we are processing
     */
    protected void resetToken(HttpServletRequest request) {
        Logger l = LogUtils.enterLog(getClass(), "resetToken", request);
        String tok = getTokenConstant();
        // call super method if we have no token constant.
        if (tok == null)
            super.resetToken(request);
        else {
            HttpSession session = request.getSession(false);
            if (session != null)
                session.removeAttribute(tok);
        }
        l.exiting(getClass().getName(), "resetToken");
    }

    /**
     * Highlight current menuitem in the menu.
     *
     * @param pageBean the PageBean object for the current page
     * @throws Exception
     */
    protected void highLightCurrentMenuItem(PageBean pageBean) throws Exception {
        Logger l = LogUtils.enterLog(this.getClass(), "highLightCurrentMenuItem", pageBean);
        if (pageMenuItemHM == null)
            pageMenuItemHM = getPageMenuItemMap();
        Object currentMenuItem = pageMenuItemHM.get(this.getClass().getName());
        if (currentMenuItem != null && !StringUtils.isBlank(currentMenuItem.toString())) {
            ArrayList leftNav = pageBean.getLeftNavMenu();
            int sz = leftNav.size();
            // loop through menu items
            for (int i = 0; i < sz; i++) {
                MenuBean menu = (MenuBean) leftNav.get(i);
                if (currentMenuItem.toString().equals(menu.getId())) {
                    menu.setIsLink(false);
                    break;
                }
            }
        }
        l.exiting(CIBaseAction.class.getName(), "highLightCurrentMenuItem");
    }

    /**
     * Set up HashMap: key - a page's struts action class name
     * value - menuitem in the page's menu corresponding to the page
     * page - struts action is one-to-one correspondence
     *
     * @return a HashMap
     */
    private static HashMap getPageMenuItemMap() {
        HashMap hm = new HashMap();
        // modify person page
        hm.put("dti.ci.struts.action.CIEntityPersonModify", "CI_ENTMODIFY_MI");
        // modify organization page
        hm.put("dti.ci.struts.action.CIEntityOrgModify", "CI_ENTMODIFY_MI");
        // address list page
        hm.put("dti.ci.struts.action.CIAddressList", "CI_ENTADDRES_MI");
        // phone number list page
        hm.put("dti.ci.phonemgr.struts.MaintainPhoneListAction", "CI_ENTPHONE_MI");
        // entity class list page
        hm.put("dti.ci.struts.action.CIEntityClassList", "CI_ENTCLASS_MI");
        // entity role list page
        hm.put("dti.ci.struts.action.CIEntityRole", "CI_ENTROLE_MI");
        // vendor page
        hm.put("dti.ci.vendormgr.struts.MaintainVendorAction", "CI_ENTVENDOR_MI");
        // vendor address page
        hm.put("dti.ci.struts.action.CIVendorAddress", "CI_ENTVNDADR_MI");
        return hm;
    }

    /**
     * Get some long pk from string to long
     *
     * @param form actionform
     * @param key  field id (action form key)
     * @return long value of pk
     */
    protected long getLongPk(ActionForm form, String key) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getLongPk",
                new Object[]{form, key});
        /* get the key value from the form */
        String pk = ActionHelper.getFormString(form, key);
        /* if not long, error */
        if (!FormatUtils.isLong(pk)) {
            String msg = "Invalid " + key + '=' + pk;
            l.severe(msg);
            throw new IllegalArgumentException(msg);
        }
        // return as long
        long lPk = Long.parseLong(pk);
        l.exiting(getClass().getName(), "getLongPk", pk);
        return lPk;
    }


    /**
     * Check whether grid xml data is changed.
     *
     * @param gridData grid xml data
     * @return boolean true for data has been changed in grid
     */
    protected boolean isGridDataChanged(String gridData) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "isGridDataChanged",
                new Object[]{gridData});
        boolean result = true;
        if (StringUtils.isBlank(gridData))
            result = false;
        else if (ICIConstants.GRID_BASE_XML_DATA.equals(gridData))
            result = false;
        l.exiting(getClass().getName(), "isGridDataChanged", new Boolean(result));
        return result;
    }

    /**
     * Checks the CIS folder menu .  Search & Select an entity type of  'organization'
     * Navigate to the entity pages
     * both Education page and Training page are available for selection
     * the user is able to enter data in both of the pages and save sucessfully,
     * however the data cannot be viewed in the client server, since these tabs are disabled.
     */
    public boolean checkCisFolderMenu(HttpServletRequest request) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "checkCisFolderMenu",
                new Object[]{request});
        boolean currPageOk = true;

        String UIStyleEdition = Authenticator.getEnvString("UIStyleEdition", "0");
        if (UIStyleEdition.equalsIgnoreCase("2")) {
            request.setAttribute(IOasisAction.KEY_TAB_MENU_IDS_TO_EXCLUDE,
                    Authenticator.getEnvString(ICIConstants.KEY_TAB_MENUIDS_EXCLUDELIST_FOR_ORGANIZATION_TYPE, ""));
        } else {
            PageBean pageBean = (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN);//"pageBean"
            // get menu
            ArrayList leftNav = pageBean.getLeftNavMenu();
            int sz = leftNav.size();
            // loop through menu items
            if (currPageOk) for (int i = 0; i < sz; i++) {
                // get this menuitem
                MenuBean menu = (MenuBean) leftNav.get(i);
                String menuID = (String) menu.getId();
                if (menuID.equals("CI_EDUC_MI") || menuID.equals("CI_TRAINING_MI") || menuID.equals("CI_EXPWIT_MI")|| menuID.equals("CI_ENT_ADDITIONAL")) {                
                    //             remove it and decrement the counter
                    leftNav.remove(i--);
                    //             decrement the size of the leftnav
                    sz--;
                }
            }
        }
        l.exiting(getClass().getName(), "checkCisFolderMenu", new Boolean(currPageOk));
        return currPageOk;
    }

    /**
     * Secure that the user has access to the web page.
     * Optionally load the Request with DynaBeans that represent the form fields defined in the OasisFields definition.
     * The values are taken from the given ActionForm.
     *
     * @param request
     * @param form
     * @param loadFields boolean indicating if the fields should be loaded.
     */
    protected void securePage(HttpServletRequest request, ActionForm form, boolean loadFields) throws Exception {
        securePage(request, form, getClass().getName(), loadFields, DefaultPageDefLoadProcessor.getInstance());
    }


    protected void setEntityTypeAndName(HttpServletRequest request,
                                        String type,
                                        String name) {
        request.setAttribute(ICIConstants.ENTITY_TYPE_PROPERTY, type);
        request.setAttribute(ICIConstants.ENTITY_NAME_PROPERTY, name);
    }

    protected void setEntityCommonInfoToRequest(HttpServletRequest request,
                                                ActionForm form) {
        request.setAttribute(ICIConstants.PK_PROPERTY,
                ActionHelper.getFormString(form, ICIConstants.PK_PROPERTY));
        request.setAttribute(ICIConstants.ENTITY_TYPE_PROPERTY,
                ActionHelper.getFormString(form, ICIConstants.ENTITY_TYPE_PROPERTY));
        request.setAttribute(ICIConstants.ENTITY_NAME_PROPERTY,
                ActionHelper.getFormString(form, ICIConstants.ENTITY_NAME_PROPERTY));
    }

    protected void setEntityCommonInfoToRequest(HttpServletRequest request,
                                                Record inputRecord) {
        request.setAttribute(ICIConstants.PK_PROPERTY,
                inputRecord.getStringValue(ICIConstants.PK_PROPERTY));
        request.setAttribute(ICIConstants.ENTITY_TYPE_PROPERTY,
                inputRecord.getStringValue(ICIConstants.ENTITY_TYPE_PROPERTY));
        request.setAttribute(ICIConstants.ENTITY_NAME_PROPERTY,
                inputRecord.getStringValue(ICIConstants.ENTITY_NAME_PROPERTY));
    }

   /**
     * for a givien record's each field, find the fieldName+"LOV" attribute from request.
     * add the fieldName+LOVLABEL back to the record
     *
     * @param request
     * @param inputRecord Record with field values
     */
    protected void getLovLabelsForInitialValues(HttpServletRequest request, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLovLabelsForInitialValues", new Object[]{request, inputRecord});
        }

        // loop through all fields with initial values
        Record outputRecord = new Record();
        Iterator fieldNames = inputRecord.getFieldNames();
        while (fieldNames.hasNext()) {
            // get field name and value
            String fieldName = (String) fieldNames.next();
            String fieldValue = inputRecord.getStringValue(fieldName);

            // set the description for LOV field
            ArrayList lov = (ArrayList) request.getAttribute(fieldName + "LOV");
            if (lov != null) {
                int size = lov.size();
                int i;
                for (i = 0; i < size; i++) {
                    LabelValueBean lvb = (LabelValueBean) lov.get(i);
                    if (lvb.getValue().equals(fieldValue)) {
                        outputRecord.setFieldValue(fieldName + "LOVLABEL", lvb.getLabel());
                    }
                }
            }
        }

        // add the descriptions to the Record
        inputRecord.setFields(outputRecord);
        l.exiting(getClass().getName(), "getLovLabelsForInitialValues", inputRecord);
    }

    /**
     * Puts the cis header fields map into the request.
     *
     * @param request HTTP servlet request.
     * @param conn    JDBC connection object.
     * @throws Exception
     */
    protected void setCisHeaderFields(HttpServletRequest request,
                                      Connection conn)
            throws Exception {
        String methodName = "setCisHeaderFields";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{request, conn});

        Record inputRecord = getInputRecord(request);
        String entityPk = "";
        // If we are coming from global search CIFields.PK will be empty. Try getting entity_pk attribute
        if (!StringUtils.isBlank(request.getParameter(CIFields.PK))) {
            entityPk =  request.getParameter(CIFields.PK);
        }  else if (!StringUtils.isBlank((String) request.getAttribute("entity_pk"))) {
            entityPk = (String) request.getAttribute("entity_pk");
        }

        if (!StringUtils.isBlank(entityPk) && request.getAttribute("cisHeaderClientId") == null) {
            CIEntityModifyHelper helper = getCIEntityModifyHelper();
            Map entityDataMap = helper.retrieveEntityDataMap(conn, entityPk);
            request.setAttribute("cisHeaderClientId", entityDataMap.get("entity_clientID"));
            request.setAttribute("cisHeaderLegacyDataId", entityDataMap.get("entity_legacyDataID"));
            request.setAttribute("cisHeaderReferencenumber", entityDataMap.get("entity_referenceNumber"));
        }
        // Get the claim header fields map.
        OasisFields hdrFldsMap = OasisFields.createInstance(
                "dti.ci.core.struts.CisHeader",
                ActionHelper.getCurrentUserId(request), conn);

        hdrFldsMap.getListOfValues(conn, null, request, true);
        // Put the OasisFieldsHeader object in the request.
        request.setAttribute("cisHeaderFieldsMap", hdrFldsMap);
        request.setAttribute(IOasisAction.KEY_HEADER_PAGE_FIELDS, hdrFldsMap);

        ArrayList fields = hdrFldsMap.getFieldIds();
        for (int i = 0; i < fields.size(); i++) {         //on LayerId
            String fieldName = fields.get(i).toString();
            if (fieldName != null && fieldName.indexOf("on LayerId") > -1) {
                fieldName = fieldName.substring(0, fieldName.indexOf("on LayerId"));
            }

            String value = ((OasisFormField) hdrFldsMap.get(fieldName)).getDefaultValue();
            if (!StringUtils.isBlank(inputRecord.getStringValue(fieldName, ""))) {
                if (inputRecord.getStringValue(ICIConstants.CIS_REFRESH_HEADER_FIELDS, "N").equals("N")) {
                    value = inputRecord.getStringValue(fieldName);
                }
            }

            request.setAttribute(fieldName, BeanDtiUtils.createValueBean(fieldName, value));
        }
        String filterPage = getSystemParameter(ActionHelper.getDbPoolId(request), ICIConstants.CIS_PAGE_INACT_FLR, "");
        if (filterPage != null) {
            request.setAttribute(ICIConstants.CIS_PAGE_INACT_FLR, filterPage);
        }

        // if genCriteriaIDForSession(request, EntitySearchFields.SEARCH_CRITERIA_FOR_SESSION_PREFIX)  is not empty which means we have searched entities
        // enable result back link
        // genCriteriaIDForSession(request, EntitySearchFields.SEARCH_CRITERIA_FOR_SESSION_PREFIX) is only set in MaintainEntitySearchAction
        // And get removed in MaintainEntitySearchAction
        if(request.getSession().getAttribute(genCriteriaIDForSession(request, EntitySearchFields.SEARCH_CRITERIA_FOR_SESSION_PREFIX)) != null) {
            request.setAttribute("resultBackLink", MessageManager.getInstance().formatMessage("ci.entity.search.label.backToList"));
        }

        lggr.exiting(this.getClass().getName(), methodName);
        lggr = null;
    }


    /**
     * Puts the cis header fields map into the request.
     *
     * @param request HTTP servlet request.
     * @throws Exception
     */
    protected void setCisHeaderFields(HttpServletRequest request)
            throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setCisHeaderFields", new Object[]{request});
        }

        Connection conn = null;

        try {
            conn = ActionHelper.getConnection(request);
            setCisHeaderFields(request, conn);
            l.exiting(getClass().getName(), "setCisHeaderFields");
        } finally {
            if (conn != null) DatabaseUtils.close(conn);
        }
    }
    /*
     * Remove one or more left nav actions from menu.
     *
     * @param request HttpServletRequest
     * @param names   String Array of menu ids
     */
    protected void removeActions(HttpServletRequest request, String[] names) {
        Logger l = LogUtils.enterLog(getClass(), "removeActions",
            new Object[]{request, names});
        String removedActionItems = "";
        ArrayList actions = null;
        if (!ApplicationContext.getInstance().getProperty("UIStyleEdition", "0").equalsIgnoreCase("2")) {
            actions = ((PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN)).getLeftNavActions();
        }

        int sz1 = names.length;
        for (int i = 0; i < sz1; i++) {
            removedActionItems = removedActionItems + names[i] + ",";
            if (ApplicationContext.getInstance().getProperty("UIStyleEdition", "0").equalsIgnoreCase("2")) {
                PageBean pageBean = ((PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN));
                String userId = "";
                try {
                    userId = ActionHelper.getCurrentUserId(request);
                    NavigationManager.getInstance().removeActionItem(request, userId, pageBean, "", names[i]);
                } catch (Exception e) {
                    AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Unexpected Exception while getting application temporary action items", e);
                    l.throwing(getClass().getName(), "removeActions", ae);
                    throw ae;
                }
            } else {
                int sz2 = actions.size();
                for (int j = 0; j < sz2; j++) {
                    if (((MenuBean) actions.get(j)).getId().equals(names[i])) {
                        actions.remove(j);
                        break;
                    }
                }
            }
        }

        request.setAttribute(RequestIds.ACTION_ITEM_IDS_TO_REMOVE, removedActionItems);
        l.exiting(getClass().getName(), "removeActions");
   }

    /**
     * Generate session id for search criteria.
     *
     * @param request
     * @return
     * @throws Exception
     */
    protected String genCriteriaIDForSession(HttpServletRequest request, String prefix) {
        String dbPoolID = ActionHelper.getDbPoolId(request);
        String userID = ActionHelper.getCurrentUser(request).getUserId();
        return prefix + "_search_criteria_" + dbPoolID + "_" + userID;
    }

    /**
     * @param request
     * @param record
     * @param fields
     * @throws javax.servlet.jsp.JspException
     */
    public void recordToString(HttpServletRequest request, Record record,
                               OasisFields fields) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "recordToString", new Object[]{
                request, record, fields});
        Iterator it = record.getFieldNames();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (fields.get(key) == null) {
                request.setAttribute(key, record.getStringValue(key, ""));
            }
        }
        l.exiting(getClass().getName(), "recordToString");
    }

    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.m_entityManager = entityManager;
    }

    public NoteManager getNoteManager() {
        return m_noteManager;
    }

    public void setNoteManager(NoteManager noteManager) {
        this.m_noteManager = noteManager;
    }

    private EntityManager m_entityManager;
    private NoteManager m_noteManager;

    private static CIEntityModifyHelper c_CIEntityModifyHelper;

    public CIEntityModifyHelper getCIEntityModifyHelper() {
        if (c_CIEntityModifyHelper == null) {
            c_CIEntityModifyHelper = new CIEntityModifyHelper();
        }
        return c_CIEntityModifyHelper;
    }
}
