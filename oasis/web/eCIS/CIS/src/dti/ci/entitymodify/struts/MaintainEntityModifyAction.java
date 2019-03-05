package dti.ci.entitymodify.struts;

import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.entitymgr.EntityConstants;
import dti.ci.entitymgr.EntityFields;
import dti.ci.entitymgr.EntityManager;
import dti.ci.entitymodify.EntityModifyManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.*;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entity Modify Action Class
 * </p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Dec 10, 2003
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ---------------------------------------------------------------
 *         03/31/2005      HXY         Extends CIBaseAction.
 *         04/08/2005      HXY         Used OasisFields to set up grid header.
 *         04/13/2005      HXY         Added fields to dataMap for grid
 *         size control.
 *         05/10/2005      HXY         Added system parameter
 *         "CM_CHK_VENDOR_VERIFY".
 *         08/02/2006     ligj        Add loss history DAO.
 *         09/06/2006     ligj        Issue #63168 Select an entity type of  'organization' both Education page and Training page are available for selection
 *         01/17/2007     Fred        Added call to CILinkGenerator.generateLink()
 *         02/16/2027     kshen       Added codes for DBA Name History Grid (iss68160)
 *         02/07/2007     FWCH        Added codes to update client type
 *         08/14/2008     Jacky       Added support for return back to result list
 *         07/13/2009     Guang       Added unspecfied method to call commonModify.
 *         09/08/2009     Kenney      Modified for issue 97135
 *         10/16/2009     hxk         Added call to determine whether entity is an expert
 *                                    witness, and if so set session var expWit for later use
 *                                    in .jsp (issue 97591).
 *         04/03/2013     kshen       Issue 141547
 *         08/30/2013     kshen       Issue 143051. Correct the field value of suffix.
 *         04/02/2014     jld         Issue 153427.
 *         08/29/2014     ylu         Issue 156588
 *         05/05/2015     dpang       Issue 162923. Set data to form for field entity_profDesignation
 *         12/30/2016     dpang       Issue 181349. Add orgSortInfo to session.
 *         04/13/2018     ylu         Issue 109088: refactor from CIEntityModify.java
 *         06/02/2018     ylu         Issue 109088: refactor update to use new note's function
 *         06/25/2018     ylu                       re-add checkCisFolderMenu code
 *         06/26/2018     ylu         Issue 194117: update for CSRF security.
 *         ---------------------------------------------------------------
 */

public abstract class MaintainEntityModifyAction extends MaintainEntityFolderBaseAction implements EntityConstants {

    private final Logger l = LogUtils.getLogger(getClass());

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return loadEntityData(mapping, form, request, response);
    }

    /**
     * change entity type
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward changeEntityType(ActionMapping mapping, ActionForm form,
                                          HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changeEntityType", new Object[]{mapping, form, request, response});
        }

        String forwardString ="changeTypeResult";

        try {
            if (isTokenValid(request)) {
                // Secure the page and get the fields.
                securePage(request, form);
                Record inputRecord = getInputRecord(request);
                String pk = inputRecord.getStringValue(PK_PROPERTY);
                // update entity type
                inputRecord.setFieldValue(EntityFields.ENTITY_ID, pk);
                String entityNewName = getEntityModifyManager().changeEntityType(inputRecord);
                //Update client name and client type in navigation string in session
                String entityNewType = inputRecord.getStringValue(EntityFields.ENTITY_TYPE);
                updateSessionNavigationString(request, entityNewName, pk, entityNewType);
            }
        } catch (AppException ae ) {
            l.throwing(getClass().getName(), "changeEntityType", ae);
            if (!MessageManager.getInstance().hasErrorMessages()) {
                MessageManager.getInstance().addErrorMessage("ci.generic.error",ae.getMessageParameters());
            }
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                    "Failed to change Entity type.",
                    e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "changeEntityType", forwardString);
        }

        return af;
    }

    /**
     * Load enity data
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward loadEntityData(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityData", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadEntityData";

        try {
            securePage(request, form);

            /* Generate input records */
            Record inputRecord = getInputRecord(request);
            String pk = inputRecord.getStringValue(PK_PROPERTY,"");
            String entityType = inputRecord.getStringValue(ENTITY_TYPE_PROPERTY,"");
            if (pk == null || StringUtils.isBlank(pk)) {
                //may be come from workcenter
                pk = (String) request.getAttribute(EntityFields.ENTITY_ID);
            }

            //may be come from add Entity
            if (pk == null || StringUtils.isBlank(pk)) {
                pk = (String) request.getAttribute(PK_PROPERTY);
            }

            if (entityType == null || StringUtils.isBlank(entityType)) {
                //may be come from workcenter
                entityType = (String) request.getAttribute(EntityFields.ENTITY_TYPE);
            }

            /* deal with original Search List Grid Sort Order*/
            processOrigSearchListGrid(request, inputRecord);

            /* load entity record */
            inputRecord.setFieldValue(EntityFields.ENTITY_ID, pk); // use for load grid data
            inputRecord.setFieldValue(EntityFields.ENTITY_TYPE, entityType);
            Record entityRecord = (Record) request.getAttribute("entityRecord");
            if (entityRecord == null) {
                //invoke EntityManager instead of EntityModifyManager,
                //since loadEntityData() is moved into EntityManagerImpl for it re-used by mini-popup
                //entityRecord = getEntityModifyManager().loadEntityData(inputRecord);
                entityRecord = getEntityManager().loadEntityData(inputRecord);
            }

            /*load for CI_EntitySelect.jsp, */
            request.setAttribute(PK_PROPERTY, pk);
            /*set current type & name to request */
            setEntityTypeAndName(request, entityType, entityRecord.hasField(ENTITY_NAME_PROPERTY) ? entityRecord.getStringValue(ENTITY_NAME_PROPERTY) : entityRecord.getStringValue(ENTITY_NAME_COMPUTED_ID));

            publishOutputRecord(request, entityRecord);

            /* hide some menu beans for an entity type of  'organization'.*/
            processCheckCisFolderMenu(request, entityRecord);

            loadGridsData(request, inputRecord);

            loadListOfValues(request, form);

            // setup discarded message info
            buildClientDiscardedMessage(request,inputRecord);

            //show or hide ExpWitness menu (default is show)
            setExpertWitnessTabMenuVisibility(request, inputRecord);

            //deal with CM_CHK_VENDOR_VERIFY sysparm
            String vendorVerifySysParamVal = SysParmProvider.getInstance().getSysParm(
                    VENDOR_VERIFY_SYS_PARAM, "N");
            request.setAttribute(VENDOR_VERIFY_SYS_PARAM, vendorVerifySysParamVal);

            addJsMessages();
            saveToken(request);
        } catch (AppException ae) {
            l.throwing(getClass().getName(), "loadEntityData", ae);
            if (!MessageManager.getInstance().hasErrorMessages()) {
                MessageManager.getInstance().addErrorMessage("ci.generic.error",
                        new Object[]{StringUtils.formatDBErrorForHtml(ae.getCause().getMessage())});
            }
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                    "Failed to load Entity data.",
                    e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityData", af);
        }
        return af;
    }

    /**
     * save Entity Data
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveEntityData(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response)
        throws Exception{

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntityData", new Object[]{mapping,form,request,response});
        }

        String forwardString = "saveEntityData";
        Record inputRecord = null;
        String okToSkipTaxIDDups = "N";

        try {
            if (isTokenValid(request)) {
                securePage(request, form);
                inputRecord = getInputRecord(request);
                inputRecord.setFieldValue(EntityFields.ENTITY_ID, inputRecord.getStringValue(PK_PROPERTY));
                getEntityModifyManager().saveEntityData(inputRecord);
                if (MessageManager.getInstance().hasWarningMessages()) {
                    okToSkipTaxIDDups = "Y";
                    //store user's input, ready for second save click
                    request.setAttribute("entityRecord", inputRecord);
                }
                // turn flag to Y in reuqest, ready for second save click
                request.setAttribute(OK_TO_SKIP_TAX_ID_DUPS_PROPERTY, okToSkipTaxIDDups);
            }
        } catch (ValidationException ve) {
            //store user's input, ready for second save click
            request.setAttribute("entityRecord", inputRecord);
            l.throwing(getClass().getName(), "saveEntityData", ve);
            handleValidationException(ve, request);
        } catch (AppException ae) {
            l.throwing(getClass().getName(), "saveEntityData", ae);
            if (!MessageManager.getInstance().hasErrorMessages()) {
                MessageManager.getInstance().addErrorMessage(ae.getMessageKey(),ae.getMessageParameters());
            }
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                    "Failed to save Entity data.",
                    e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveEntityData", af);
        }
        return af;
    }

    /**
     * global search
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward globalSearch(ActionMapping mapping, ActionForm form,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "globalSearch", new Object[]{mapping,form,request,response});
        }

        return loadEntityData(mapping, form, request, response);
    }

    /**
     * Validate Policy number for reference number field
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public  void validateReferenceNumber(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) throws Exception{
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateRefNum", new Object[]{mapping,form,request,response});
        }

        Record inputRecord = getInputRecord(request);
        try {
            //get the value result
            String isValidB = getEntityModifyManager().validateReferenceNumberAsStr(inputRecord);
            writeAjaxResponse(response, isValidB);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate Reference Number.", e, response);
        }
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.entity.message.change.clientType");
        MessageManager.getInstance().addJsMessage("ci.entity.message.change.riskType");
        MessageManager.getInstance().addJsMessage("ci.entity.message.change.notRollback");
        MessageManager.getInstance().addJsMessage("ci.entity.message.change.operationCancel");
        MessageManager.getInstance().addJsMessage("ci.entity.message.dateValue.after");
        MessageManager.getInstance().addJsMessage("ci.common.error.classCode.required");
        MessageManager.getInstance().addJsMessage("ci.entity.message.entityType.unknown");
        MessageManager.getInstance().addJsMessage("ci.entity.message.dateValue.beforeToday");
        MessageManager.getInstance().addJsMessage("ci.entity.message.dateValue.afterToday");

    }

    /**
     *
     * @param request
     * @param newClientName
     * @param entityPk
     * @param entityType
     * @throws Exception
     */
    private void updateSessionNavigationString(HttpServletRequest request,
                                               String newClientName,
                                               String entityPk,
                                               String entityType) throws Exception {
        String entityLst = (String) request
                .getSession(false).getAttribute(ENTITY_SELECT_RESULTS);
        if (!StringUtils.isBlank(entityLst)) {
            String[] properties = entityLst.split(ENTITY_SPLIT_SIGN);
            StringBuffer sb = new StringBuffer();
            for (int index = 0; index < properties.length; index += 3) {
                sb.append(properties[index]).append(ENTITY_SPLIT_SIGN);
                if (entityPk.equals(properties[index])) {
                    //new type
                    sb.append(entityType).append(ENTITY_SPLIT_SIGN);
                    //new client name
                    sb.append(newClientName).append(ENTITY_SPLIT_SIGN);
                } else {
                    sb.append(properties[index + 1]).append(ENTITY_SPLIT_SIGN);
                    sb.append(properties[index + 2]).append(ENTITY_SPLIT_SIGN);
                }
            }
            request.getSession(false).setAttribute(ENTITY_SELECT_RESULTS,
                    sb.toString().substring(0, sb.lastIndexOf(ENTITY_SPLIT_SIGN)));
        }
    }

    /**
     * process check if can see CIS menu items
     * @param request
     * @param entityRecord
     */
    private void processCheckCisFolderMenu(HttpServletRequest request, Record entityRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processCheckCisFolderMenu", new Object[]{entityRecord});
        }

        try {
            String entityType = entityRecord.getStringValue(ENTITY_TYPE_ID, "");
            if (StringUtils.isBlank(entityType)) {
                entityType = (String) request.getAttribute(EntityFields.ENTITY_TYPE);
                if (StringUtils.isBlank(entityType)) {
                    entityType = ENTITY_TYPE_PERSON_STRING;
                }
            }

            if (entityType.charAt(0) != ENTITY_TYPE_ORG_CHAR && entityType.charAt(0) != ENTITY_TYPE_PERSON_CHAR) {
                entityType = ENTITY_TYPE_PERSON_STRING;
            }

            if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
                checkCisFolderMenu(request);
            }

            l.exiting(getClass().getName(), "processCheckCisFolderMenu");
        } catch (Exception ignore) {
            l.throwing(getClass().getName(), "processCheckCisFolderMenu", ignore);
        }
    }

    /**
     * load data into Grids column
     * @param request
     * @param inputRecord
     */
    private void loadGridsData(HttpServletRequest request, Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadGridsData", new Object[]{inputRecord});
        }

        /* load grid data */
        RecordSet nameRs = getEntityModifyManager().loadNameHistory(inputRecord);
        RecordSet taxRs = getEntityModifyManager().loadTaxHistory(inputRecord);
        RecordSet lossRs = getEntityModifyManager().loadLossHistory(inputRecord);
        RecordSet dbaRs = getEntityModifyManager().loadDbaHistory(inputRecord);
        RecordSet etdRs = getEntityModifyManager().loadEtdHistory(inputRecord);

        /* set data bean for Grids*/
        processGridSetDataBean(request,nameRs,taxRs,lossRs,dbaRs,etdRs);

        String entityType = inputRecord.getStringValue(ENTITY_TYPE_PROPERTY,"");

        if (entityType.charAt(0) == ENTITY_TYPE_PERSON_CHAR) {
            /* Load name history header bean */
            processGridLoadHeaderBean(request,"nameGrid",PER_NAME_HISTORY_GRID_LAYER_ID);
            /* load tax history header bean*/
            processGridLoadHeaderBean(request,"taxGrid",PER_TAX_GRID_LAYER_ID);
            /* load loss history header bean*/
            processGridLoadHeaderBean(request,"lossGrid",PER_LOSS_GRID_LAYER_ID);
            /* load dba history header bean*/
            processGridLoadHeaderBean(request,"dbaGrid",PER_DBA_GRID_LAYER_ID);
            /* load etd history header bean*/
            processGridLoadHeaderBean(request,"etdGrid",PER_ETD_GRID_LAYER_ID);
        } else if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
            /* Load name history header bean */
            processGridLoadHeaderBean(request,"nameGrid",ORG_NAME_HISTORY_GRID_LAYER_ID);
            /* load tax history header bean*/
            processGridLoadHeaderBean(request,"taxGrid",ORG_TAX_GRID_LAYER_ID);
            /* load loss history header bean*/
            processGridLoadHeaderBean(request,"lossGrid",ORG_LOSS_GRID_LAYER_ID);
            /* load dba history header bean*/
            processGridLoadHeaderBean(request,"dbaGrid",ORG_DBA_GRID_LAYER_ID);
            /* load etd history header bean*/
            processGridLoadHeaderBean(request,"etdGrid",ORG_ETD_GRID_LAYER_ID);
        }
        l.exiting(getClass().getName(), "loadGridsData");
    }

    /**
     * stroe origianl entity Search list grid's sort order
     * @param request
     * @param inputRecord
     */
    private void processOrigSearchListGrid(HttpServletRequest request, Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processOrigSearchListGrid", new Object[]{inputRecord});
        }

        if (inputRecord.hasField("orgSortColumn")) {
            Record origRecord = new Record();
            origRecord.setFieldValue("orgSortColumn", inputRecord.getFieldValue("orgSortColumn", null));
            origRecord.setFieldValue("orgSortType", inputRecord.getFieldValue("orgSortType", null));
            origRecord.setFieldValue("orgSortOrder", inputRecord.getFieldValue("orgSortOrder", null));
            origRecord.setFieldValue("orgRowId", inputRecord.getFieldValue("pk", null));
            UserSessionManager.getInstance().getUserSession(request).set("backToEntityListOrgInfo", origRecord);
        }

        l.exiting(getClass().getName(), "processOrigSearchListGrid");
    }

    /**
     * build discarded message
     * @param request
     * @param inputRecord
     */
    public void buildClientDiscardedMessage(HttpServletRequest request, Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "buildClientDiscardedMessage", new Object[]{inputRecord});
        }

        String discardedMsgKey = getEntityModifyManager().getClientDiscardPolCheck(inputRecord);
        request.setAttribute(CLIENT_DISCARDED_MSG, discardedMsgKey);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "buildClientDiscardedMessage", discardedMsgKey);
        }
    }

    /**
     * set Expert Witness menu visible or not
     * @param request
     * @param inputRecord
     */
    public void setExpertWitnessTabMenuVisibility(HttpServletRequest request, Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setExpertWitnessTabMenuVisibilityOrNot", new Object[]{inputRecord});
        }

        HttpSession session = request.getSession(false);
        boolean isNeedShowExperWitnessB = getEntityModifyManager().getExpWitTabVisibilityflag(inputRecord);
        if (isNeedShowExperWitnessB) {
            session.setAttribute("expWit","Y");
        } else {
            session.setAttribute("expWit","N"); //close Expert Witness tab menu
        }
        l.exiting(getClass().getName(), "setExpertWitnessTabMenuVisibilityOrNot");
    }

    /**
     * set data bean for grids
     */
    public void processGridSetDataBean(HttpServletRequest request,
                                       RecordSet nameRs,
                                       RecordSet taxRs,
                                       RecordSet lossRs,
                                       RecordSet dbaRs,
                                       RecordSet etdRs) {
        l.entering(getClass().getName(), "processGridSetDataBean");

        /* set name history data Bean*/
        setDataBean(request, nameRs, "nameGrid");
        /* set tax history data bean*/
        setDataBean(request, taxRs, "taxGrid");
        /* set loss history data bean*/
        setDataBean(request, lossRs, "lossGrid");
        /* set dba history data bean*/
        setDataBean(request, dbaRs, "dbaGrid");
        /* set eletrinic data data bean*/
        setDataBean(request, etdRs, "etdGrid");

        l.exiting(getClass().getName(), "processGridSetDataBean");
    }

    public void processGridLoadHeaderBean(HttpServletRequest request, String gridId, String layerId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processGridLoadHeaderBean", new Object[]{gridId,layerId});
        }

        RequestStorageManager.getInstance().set("currentGridId", gridId);
        loadGridHeader(request, null, gridId, layerId);

        l.exiting(getClass().getName(), "processGridLoadHeaderBean");

    }

    public void verifyConfig() {
        if (getEntityModifyManager() == null) {
            throw new ConfigurationException("The required property 'entityModifyManager' is missing.");
        }

        if (getEntityManager() == null) {
            throw new ConfigurationException("The required property 'entityManager' is missing.");
        }

        if (getNameAnchorColumnName()== null) {
            throw new ConfigurationException("The required property 'nameAnchorColumnName' is missing.");
        }

        if (getTaxAnchorColumnName()== null) {
            throw new ConfigurationException("The required property 'taxAnchorColumnName' is missing.");
        }

        if (getLossAnchorColumnName()== null) {
            throw new ConfigurationException("The required property 'lossAnchorColumnName' is missing.");
        }

        if (getDbaAnchorColumnName()== null) {
            throw new ConfigurationException("The required property 'dbaAnchorColumnName' is missing.");
        }

        if (getEtdAnchorColumnName()== null) {
            throw new ConfigurationException("The required property 'etdAnchorColumnName' is missing.");
        }
    }

    /**
     * override getAnchorColumnName method of BaseAction
     * @return
     */
    public String getAnchorColumnName() {
        Logger l = LogUtils.enterLog(getClass(), "getAnchorColumnName");
        String anchorName;
        if (RequestStorageManager.getInstance().has("currentGridId")) {
            String currentGridId = (String) RequestStorageManager.getInstance().get("currentGridId");
            if (currentGridId.equals("nameGrid")) {
                anchorName = getNameAnchorColumnName();
            } else if (currentGridId.equals("taxGrid")) {
                anchorName = getTaxAnchorColumnName();
            } else if (currentGridId.equals("lossGrid")) {
                anchorName = getLossAnchorColumnName();
            }else if (currentGridId.equals("dbaGrid")) {
                anchorName = getDbaAnchorColumnName();
            }else if (currentGridId.equals("etdGrid")) {
                anchorName = getEtdAnchorColumnName();
            }else {
                anchorName = super.getAnchorColumnName();
            }
        } else {
            anchorName = super.getAnchorColumnName();
        }
        l.exiting(getClass().getName(), "getAnchorColumnName", anchorName);
        return anchorName;
    }

    public String getNameAnchorColumnName() {
        return nameAnchorColumnName;
    }

    public void setNameAnchorColumnName(String nameAnchorColumnName) {
        this.nameAnchorColumnName = nameAnchorColumnName;
    }

    public String getTaxAnchorColumnName() {
        return taxAnchorColumnName;
    }

    public void setTaxAnchorColumnName(String taxAnchorColumnName) {
        this.taxAnchorColumnName = taxAnchorColumnName;
    }

    public String getLossAnchorColumnName() {
        return lossAnchorColumnName;
    }

    public void setLossAnchorColumnName(String lossAnchorColumnName) {
        this.lossAnchorColumnName = lossAnchorColumnName;
    }

    public String getDbaAnchorColumnName() {
        return dbaAnchorColumnName;
    }

    public void setDbaAnchorColumnName(String dbaAnchorColumnName) {
        this.dbaAnchorColumnName = dbaAnchorColumnName;
    }

    public String getEtdAnchorColumnName() {
        return etdAnchorColumnName;
    }

    public void setEtdAnchorColumnName(String etdAnchorColumnName) {
        this.etdAnchorColumnName = etdAnchorColumnName;
    }

    public EntityModifyManager getEntityModifyManager() {
        return m_entityModifyManager;
    }

    public void setEntityModifyManager(EntityModifyManager m_entityModifyManager) {
        this.m_entityModifyManager = m_entityModifyManager;
    }

    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    public void setEntityManager(EntityManager m_entityManager) {
        this.m_entityManager = m_entityManager;
    }

    private EntityModifyManager m_entityModifyManager;
    private EntityManager m_entityManager;

    private String nameAnchorColumnName;
    private String taxAnchorColumnName;
    private String lossAnchorColumnName;
    private String dbaAnchorColumnName;
    private String etdAnchorColumnName;

    protected static final String PER_NAME_HISTORY_GRID_LAYER_ID = "Entity_Person_Name_History_Grid_Header_Layer";
    protected static final String PER_TAX_GRID_LAYER_ID = "Entity_Person_Tax_History_Grid_Header_Layer";
    protected static final String PER_LOSS_GRID_LAYER_ID = "Entity_Person_Loss_History_Grid_Header_Layer";
    protected static final String PER_DBA_GRID_LAYER_ID = "Entity_Person_Dba_History_Grid_Header_Layer";
    protected static final String PER_ETD_GRID_LAYER_ID = "Entity_Electr_Distrib_History_Grid_Header_Layer";

    protected static final String ORG_NAME_HISTORY_GRID_LAYER_ID = "Entity_Organization_Name_History_Grid_Header_Layer";
    protected static final String ORG_TAX_GRID_LAYER_ID = "Entity_Organization_Tax_History_Grid_Header_Layer";
    protected static final String ORG_LOSS_GRID_LAYER_ID = "Entity_Organization_Loss_History_Grid_Header_Layer";
    protected static final String ORG_DBA_GRID_LAYER_ID = "Entity_Organization_Dba_History_Grid_Header_Layer";
    protected static final String ORG_ETD_GRID_LAYER_ID = "Entity_Electr_Distrib_History_Grid_Header_Layer";

}
