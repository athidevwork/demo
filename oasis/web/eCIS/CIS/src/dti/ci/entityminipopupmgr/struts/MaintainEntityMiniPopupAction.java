package dti.ci.entityminipopupmgr.struts;

import dti.ci.core.struts.MaintainCIBaseAction;
import dti.ci.entityminipopupmgr.EntityMiniPopupFields;
import dti.ci.entityminipopupmgr.EntityMiniPopupManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class for Entity Mini Popup.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 * Date:   Apr 22, 2004
 * <p/>
 * Revision Date    Revised By  Description
 * ----------------------------------------------------------------
 * 03/31/2005       HXY         Extends CIBaseAction.
 * 04/08/2005       HXY         Used OasisFields to set up grid header.
 * 04/13/2005       HXY         Added fields to dataMap for grid size
 * control.
 * 04/28/2010       shchen      Add contact list layer for this page.
 * 04/17/2018       dzhang      Issue 192649: entity mini popup refactor
 * -----------------------------------------------------------------
 */

public class MaintainEntityMiniPopupAction extends MaintainCIBaseAction {


    private final Logger l = LogUtils.getLogger(getClass());


    @Override
    protected ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadEntityMiniPopupData(mapping, form, request, response);
    }

    /**
     * load entity Mini Popup data.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward loadEntityMiniPopupData(ActionMapping mapping, ActionForm form,
                                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityMiniPopupData", new Object[]{mapping, form, request, response});
        }

        String forwardString = SUCCESS;

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            String pk = inputRecord.getStringValue(PK_PROPERTY);

            //1.1 - Load current entity
            inputRecord.setFieldValue(ENTITY_ID, pk);
            Record outputRecord = getEntityMiniPopupManager().loadEntity(inputRecord);

            //1.2 - Load entity address list
            RecordSet rsAddress = getEntityMiniPopupManager().loadEntityAddressList(inputRecord);

            //1.3 - Load Address phone list
            RecordSet addrPhoneList = getEntityMiniPopupManager().loadAddressPhoneList(inputRecord);

            //1.4 - load entity contact list
            RecordSet contactList = getEntityMiniPopupManager().getContactList(inputRecord);

            //1.5 - load entity phone list
            RecordSet entityPhoneList = getEntityMiniPopupManager().loadEntityGeneralPhoneList(inputRecord);

            //2.1 - Publish entity data
            outputRecord.setFields(inputRecord, false);
            publishOutputRecord(request, outputRecord);

            //2.2 - Set address data bean and grid bean
            setDataBean(request, rsAddress, EntityMiniPopupFields.ADDRESS_GRID);
            RequestStorageManager.getInstance().set(RequestStorageIds.CURRENT_GRID_ID, EntityMiniPopupFields.ADDRESS_GRID);
            loadGridHeader(request, EntityMiniPopupFields.GRID_FIELD_SUFFIX, EntityMiniPopupFields.ADDRESS_GRID, EntityMiniPopupFields.ADDRESS_GRID_HEADER_LAYER);

            //2.3 - Set address phone list data bean and grid bean
            setDataBean(request, addrPhoneList, EntityMiniPopupFields.ADDRESS_PHONE_GRID);
            RequestStorageManager.getInstance().set(RequestStorageIds.CURRENT_GRID_ID, EntityMiniPopupFields.ADDRESS_PHONE_GRID);
            loadGridHeader(request, EntityMiniPopupFields.GRID_FIELD_SUFFIX, EntityMiniPopupFields.ADDRESS_PHONE_GRID, EntityMiniPopupFields.ADDRESS_PHONE_GRID_HEADER_LAYER);

            //2.4 - Set entity contact list data bean and grid bean
            setDataBean(request, contactList, EntityMiniPopupFields.CONTACT_GRID);
            RequestStorageManager.getInstance().set(RequestStorageIds.CURRENT_GRID_ID, EntityMiniPopupFields.CONTACT_GRID);
            loadGridHeader(request, EntityMiniPopupFields.GRID_FIELD_SUFFIX, EntityMiniPopupFields.CONTACT_GRID, EntityMiniPopupFields.CONTACT_GRID_HEADER_LAYER);

            //2.5 - Set entity phone list data bean and grid bean
            setDataBean(request, entityPhoneList, EntityMiniPopupFields.ENTITY_PHONE_GRID);
            RequestStorageManager.getInstance().set(RequestStorageIds.CURRENT_GRID_ID, EntityMiniPopupFields.ENTITY_PHONE_GRID);
            loadGridHeader(request, EntityMiniPopupFields.GRID_FIELD_SUFFIX, EntityMiniPopupFields.ENTITY_PHONE_GRID, EntityMiniPopupFields.ENTITY_PHONE_GRID_HEADER_LAYER);

            /* Process LOVs. */
            loadListOfValues(request, form);

        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load entity mini popup data", e, request, mapping);
            l.throwing(getClass().getName(), "loadEntityMiniPopupData", e);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityMiniPopupData", af);
        }
        return af;
    }

    public void verifyConfig() {
        if (getEntityMiniPopupManager() == null) {
            throw new ConfigurationException("The required property 'entityMiniPopupManager' is missing");
        }
    }

    public EntityMiniPopupManager getEntityMiniPopupManager() {

        return m_entityMiniPopupManager;
    }

    public String getAnchorColumnName() {
        return getAnchorColumnName((String) RequestStorageManager.getInstance().get(RequestStorageIds.CURRENT_GRID_ID));
    }

    private String getAnchorColumnName(String currentGridId) {
        if (EntityMiniPopupFields.ADDRESS_GRID.equals(currentGridId)) {
            return getEntityAddressListAnchorColumnName();
        } else if (EntityMiniPopupFields.CONTACT_GRID.equals(currentGridId)) {
            return getEntityContactListAnchorColumnName();
        } else if (EntityMiniPopupFields.ADDRESS_PHONE_GRID.equals(currentGridId)) {
            return getAddressPhoneListAnchorColumnName();
        } else if (EntityMiniPopupFields.ENTITY_PHONE_GRID.equals(currentGridId)) {
            return getEntityPhoneListAnchorColumnName();
        }else {
            return super.getAnchorColumnName();
        }
    }

    public void setEntityMiniPopupManager(EntityMiniPopupManager entityMiniPopupManager) {
        this.m_entityMiniPopupManager = entityMiniPopupManager;
    }

    public String getEntityAddressListAnchorColumnName() {

        return m_entityAddressListAnchorColumnName;
    }

    public void setEntityAddressListAnchorColumnName(String addressListAnchorColumnName) {
        this.m_entityAddressListAnchorColumnName = addressListAnchorColumnName;
    }

    public String getEntityContactListAnchorColumnName() {

        return m_entityContactListAnchorColumnName;
    }

    public void setEntityContactListAnchorColumnName(String entityContactListAnchorColumnName) {
        this.m_entityContactListAnchorColumnName = entityContactListAnchorColumnName;
    }

    public String getAddressPhoneListAnchorColumnName() {

        return m_addressPhoneListAnchorColumnName;
    }

    public void setAddressPhoneListAnchorColumnName(String addressPhoneListAnchorColumnName) {
        this.m_addressPhoneListAnchorColumnName = addressPhoneListAnchorColumnName;
    }

    public String getEntityPhoneListAnchorColumnName() {

        return m_entityPhoneListAnchorColumnName;
    }

    public void setEntityPhoneListAnchorColumnName(String entityPhoneListAnchorColumnName) {
        this.m_entityPhoneListAnchorColumnName = entityPhoneListAnchorColumnName;
    }

    private EntityMiniPopupManager m_entityMiniPopupManager;
    private String m_entityAddressListAnchorColumnName;
    private String m_entityContactListAnchorColumnName;
    private String m_addressPhoneListAnchorColumnName;
    private String m_entityPhoneListAnchorColumnName;
}
