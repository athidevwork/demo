package dti.ci.entityminipopupmgr;

import dti.ci.helpers.ICIConstants;
import dti.oasis.recordset.Record;

/**
 * Interface to handle logics of Entity Mini Popup Manager
 * <p/>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 28, 2010
 *
 * @author bchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/17/2018       dzhang      Issue 192649: entity mini popup refactor
 * ---------------------------------------------------
 */
public class EntityMiniPopupFields implements ICIConstants {

    public static final String ADDRESS_GRID = "entityAddressList";
    public static final String ADDRESS_GRID_HEADER_LAYER = "Entity_MiniPopup_Address_Grid_Header_Layer";

    public static final String CONTACT_GRID = "entityContactList";
    public static final String CONTACT_GRID_HEADER_LAYER = "Entity_MiniPopup_Contact_Grid_Header_Layer";

    public static final String ADDRESS_PHONE_GRID = "addressPhoneList";
    public static final String ADDRESS_PHONE_GRID_HEADER_LAYER = "Entity_MiniPopup_Address_Phone_Grid_Header_Layer";

    public static final String ENTITY_PHONE_GRID = "entityPhoneList";
    public static final String ENTITY_PHONE_GRID_HEADER_LAYER = "Entity_MiniPopup_General_Phone_Grid_Header_Layer";

    public static final String ENTITY_TYPE_ID = "entity_entityType";
    public static final String SSN_ID = "entity_socialSecurityNumber";
    public static final String FED_TAX_ID_ID = "entity_federalTaxID";

    public static final String ENTITY_PREFIX = "entity_";
    public static final String CONTACT_PREFIX = "contact_";
    public static final String GRID_FIELD_SUFFIX = "_GH";
    public static final String IS_POPUP_PAGE = "isPopupPage";
    public static final String ENTITY_TYPE_B = "entity_type_b";

    public static String getEntityType(Record inputRecord) {

        return inputRecord.getStringValue(ENTITY_TYPE_ID, "");
    }

    public static String getIsPopupPage(Record inputRecord) {
        return inputRecord.getStringValue(IS_POPUP_PAGE, "");
    }
}
