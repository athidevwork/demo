package dti.ci.entityclassmgr.struts;

import dti.ci.core.CIFields;
import dti.ci.entityclassmgr.EntityClassFields;
import dti.ci.entityclassmgr.EntityClassManager;
import dti.ci.entitymgr.EntityManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.*;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;

import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.recordset.Record;
import dti.oasis.messagemgr.MessageManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Superclass Action Class for Entity Class Add and Modify.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 * Date:   Apr 1, 2004
 * <p/>
 * Revision Date    Revised By  Description
 * ---------------------------------------------------------------------
 * 04/04/2005        HXY        Extends CIBaseAction.
 * 08/10/2007        FWCH       Created dependent relation between class and sub class
 * 10/24/2008        Fred       Add multi-class once
 * 08/13/2009        Fred       Added overlapped-class validation
 * 10/16/2009        hxk        Added call to determine whether entity has any valid expert
 *                              witness, and if so set session var expWit for later use
 *                              in .jsp (issue 97591).
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 09/23/2013        kshen      Issue 148100.
 * 03/20/2014       bzhu        Issue 149270. Set value for entityClassCodeDesc and entitySubClassCode to form.
 * 03/02/2017       ddai        Issue 183591. Change the setProperty method for entityClassCode.
 * 04/04/2018        kshen      Refactor entity class pages.
 *                              1. Moved the logic about setting fields of Entity Class Modify page to be readonly to OBR if user_can_update_B is "N".
 *                              2. Changed Save button of Entity Class Modify page to be disabled in OBR if user_can_update_B is "N".
 *                              3. It looks like ALLOW_SUBCLASS_SELECT_PROPERTY and ALLOW_SUBTYPE_SELECT_PROPERTY is nott used.
 * ---------------------------------------------------------------------
 */

public abstract class BaseMaintainEntityClassAction extends CIBaseAction {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Set common property to request.
     * @param request
     */
    protected void processSetCommonPropertiesToRequest(HttpServletRequest request, ActionMapping mapping) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processSetCommonPropertiesToRequest", new Object[]{request, mapping});
        }

        Record inputRecord = getInputRecord(request);

        // Set entity ID.
        request.setAttribute(CIFields.ENTITY_ID, inputRecord.getStringValue(CIFields.ENTITY_ID));

        // Set checkbox span.
        request.setAttribute(CIFields.CHECKBOX_SPAN_PROPERTY, "2");

        // Set checkbox span.
        if (request.getAttribute(EntityClassFields.ADD_WITH_ERROR) == null) {
            request.setAttribute(EntityClassFields.ADD_WITH_ERROR, "N");
        }

        // Show/hide fields
        processShowHideFields(request);

        // Set tax ID exists flag.
        setTaxIdExistsFlagToRequest(request);

        // Set form action to request since Entity Class Add and Entity Class Modify share JSP page.
        setFormActionURIToRequest(request, mapping);

        addJsMessages();

        saveToken(request);

        l.exiting(getClass().getName(), "processSetCommonPropertiesToRequest");
    }

    /**
     * Processing fields:
     * 1. Hide the field Network Discount for Organization.
     *
     * @param request
     */
    protected void processShowHideFields(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processShowHideFields", new Object[]{request});
        }

        Record inputRecord = getInputRecord(request);

        // Hide the field Network Discount for person.
        String entityType = getEntityManager().getEntityType(inputRecord);

        if (StringUtils.isBlank(entityType) || entityType.charAt(0) != CIFields.ENTITY_TYPE_ORG_CHAR) {
            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);

            fields.getField(EntityClassFields.ENTITY_CLASS_PREFIX + EntityClassFields.NETWORK_DISCOUNT).setIsVisible(false);
        }

        l.exiting(getClass().getName(), "processShowHideFields");
    }

    /**
     * Get tax ID info exists flag of the current entity, and set the Y/N flag to request.
     *
     * @param request
     */
    protected void setTaxIdExistsFlagToRequest(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processTaxIdInfo", new Object[]{request});
        }

        Record inputRecord = getInputRecord(request);
        boolean taxIdExists = getEntityManager().hasTaxIdInfo(inputRecord);

        request.setAttribute(CIFields.ENTITY_HAS_TAX_ID_EXISTS, YesNoFlag.getInstance(taxIdExists).getName());

        l.exiting(getClass().getName(), "setTaxIdExistsFlagToRequest");
    }

    /**
     * Since the page Entity Class Add and Entity Class Modify shares the same JSP page, we need to set the correct
     * form action to request according what's the current page.
     *
     * @param request
     */
    protected void setFormActionURIToRequest(HttpServletRequest request, ActionMapping mapping) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setFormActionURIToRequest", new Object[]{request, mapping});
        }

        request.setAttribute(FORM_ACTION_PROPERTY, getFormActionURI(request, mapping));

        l.exiting(getClass().getName(), "setFormActionURIToRequest");
    }

    /**
     * Get form action URI of the current page. It can be overwritten in sub classes optional.
     *
     * @param request
     * @param mapping
     * @return
     */
    protected String getFormActionURI(HttpServletRequest request, ActionMapping mapping) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFormActionURI", new Object[]{request, mapping});
        }

        String formActionURI = mapping.getPath().substring(1) + ".do";

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFormActionURI", formActionURI);
        }
        return formActionURI;
    }

    /**
     * Get entity type and set it to input record and request.
     * @param request
     */
    protected void processGetEntityType(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processGetEntityType", new Object[]{request});
        }

        Record inputRecord = getInputRecord(request);

        String entityType = getEntityManager().getEntityType(inputRecord);

        request.setAttribute(CIFields.ENTITY_TYPE, entityType);
        inputRecord.setFieldValue(CIFields.ENTITY_TYPE, entityType);

        l.exiting(getClass().getName(), "processGetEntityType");
    }

    //add js message
    protected void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.another.select");
        MessageManager.getInstance().addJsMessage("ci.common.error.classCode.required");
        MessageManager.getInstance().addJsMessage("ci.entity.class.invalidNetworkDiscount");
        MessageManager.getInstance().addJsMessage("ci.entity.class.newEffectiveFromDateBeforeCurrent.error");
        MessageManager.getInstance().addJsMessage("ci.entity.class.newEffectiveFromDateCannotBeEmpty.error");
        MessageManager.getInstance().addJsMessage("ci.entity.class.invalidVendor.error");
    }

    public void verifyConfig() {
        super.verifyConfig();

        if (getEntityClassManager() == null)
            throw new ConfigurationException("The required property 'entityClassManager' is missing.");

        if (getEntityManager() == null)
            throw new ConfigurationException("The required property 'entityManager' is missing.");
    }

    public EntityClassManager getEntityClassManager() {
        return m_entityClassManager;
    }

    public void setEntityClassManager(EntityClassManager entityClassManager) {
        m_entityClassManager = entityClassManager;
    }

    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        m_entityManager = entityManager;
    }

    private EntityClassManager m_entityClassManager;
    private EntityManager m_entityManager;
}
