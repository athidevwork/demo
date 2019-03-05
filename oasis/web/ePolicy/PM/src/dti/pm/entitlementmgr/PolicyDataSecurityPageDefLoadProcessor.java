package dti.pm.entitlementmgr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.tags.OasisElements;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.tags.OasisWebElement;
import dti.oasis.util.LogUtils;
import dti.oasis.util.MenuBean;
import dti.oasis.util.PageDefLoadProcessor;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.core.securitymgr.SecurityManager;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class enforces data level security for policy folder. The methods/functions of this class is accessed
 * via OasisTags like OasisFields, OasisElements and menu helper.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 21, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/18/2008       wer         Set the order of action items based on the sequence
 * 05/26/2008       Bhong       Clean up codes and remove implementation for transaction mode entitlement
 * ---------------------------------------------------
 */
public class PolicyDataSecurityPageDefLoadProcessor implements PageDefLoadProcessor {
    public static final String BEAN_NAME = "PolicyDataSecurityPageDefLoadProcessor";

    /**
     * Return an instance of ready-to-use PolicyDataSecurityPageDefLoadProcessor
     * with all parameters loaded into class memebers.
     *
     * @param policyId
     * @return instance of PolicyDataSecurityPageDefLoadProcessor
     */
    public static PolicyDataSecurityPageDefLoadProcessor getInstance(String policyId) {
        Logger l = LogUtils.getLogger(PolicyDataSecurityPageDefLoadProcessor.class);
        if (l.isLoggable(Level.FINER)) {
            l.entering(PolicyDataSecurityPageDefLoadProcessor.class.getName(), "getInstance", new Object[]{policyId});
        }
        // Initalize the instance
        PolicyDataSecurityPageDefLoadProcessor instance;
        instance = (PolicyDataSecurityPageDefLoadProcessor) ApplicationContext.getInstance().getBean(BEAN_NAME);
        instance.setDataSecuredForTheUser(instance.getSecurityManager().isDataSecured(
            SUB_SYSTEM, SECURITY_TYPE, SOURCE_TABLE, policyId));

        // todo: move it out ot this class
        RequestStorageManager.getInstance().set(
            RequestStorageIds.DATA_SECURITY_PAGE_DEF_LOAD_PROCESSOR, instance);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(PolicyDataSecurityPageDefLoadProcessor.class.getName(), "getInstance", instance);
        }
        return instance;
    }

    /**
     * Method that enforces security on the provided oasis form field.
     *
     * @param fld OasisFormField, for which postProcess checks for security
     */
    public void postProcessField(OasisFormField fld) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessField", new Object[]{fld});
        }
        // If user level data security enabled for current user, set the field to readonly
        if (isDataSecuredForTheUser()) {
            fld.setIsReadOnly(true);
        }
        l.exiting(PolicyDataSecurityPageDefLoadProcessor.class.getName(), "postProcessField");
    }

    /**
     * Method that enforces security on a collection of provided oasis form fields.
     *
     * @param flds OasisFields collection, for which postProcess checks for security
     */
    public void postProcessFields(OasisFields flds) {
    }

    /**
     * Method that enforces security on the provided oasis web element.
     *
     * @param element OasisWebElement, for which postProcess checks for security
     */
    public void postProcessWebElement(OasisWebElement element) {
        // Leave this method empty since the web element is not used by ePolicy project.
    }

    /**
     * Method that enforces security on a collection of provided oasis web elements.
     *
     * @param elements OasisElements collection, for which postProcess checks for security
     */
    public void postProcessWebElements(OasisElements elements) {
    }

    /**
     * Method that returns a boolean value that indicates whether the menu item is secured.
     *
     * @param menuitem
     * @return true if the menu item is not protected
     */
    public boolean postProcessMenuItem(MenuBean menuitem) {
        // Menu items are defined as only navigating to other pages but not performing actions.
        // Therefore we never secure them even if the user has READONLY priviledge to allow

        // Return true to indicate the menu item is not protected.
        // It is used in several places in NavigationManagerImpl calss.
        return true;
    }

    /**
     * Method that enforces security against a collection of menu items.
     *
     * @param menuitems
     */
    public void postProcessMenuItems(List menuitems) {
    }

    /**
     * Method that returns a boolean value that indicates whether the action item is secured.
     *
     * @param actionitem
     * @return true, if the action item is not protected, otherwise, false
     */
    public boolean postProcessActionItem(MenuBean actionitem) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessActionItem", new Object[]{actionitem});
        }
        boolean isNotProtected = true;
        // If user level security is enabled and the action item Id is not in
        // the visible action item id list, set "isNotProtected" to false.
        if (isDataSecuredForTheUser() && getVisibleActionItemIds().indexOf(actionitem.getId()) == -1) {
            isNotProtected = false;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessActionItem", Boolean.valueOf(isNotProtected));
        }
        return isNotProtected;
    }

    /**
     * Method that enforces security against a collection of action items.
     *
     * @param actionitems
     */
    public void postProcessActionItems(List actionitems) {
    }

    /**
     * Verify config
     */
    public void verifyConfig() {
        if (getSecurityManager() == null) {
            throw new ConfigurationException("The required property 'securityManager' is missing.");
        }
        if (getVisibleActionItemIds() == null) {
            throw new ConfigurationException("The required property 'visibleActionItemIds' is missing.");
        }
    }

    public SecurityManager getSecurityManager() {
        return m_securityManager;
    }

    public void setSecurityManager(dti.pm.core.securitymgr.SecurityManager securityManager) {
        m_securityManager = securityManager;
    }

    public String getVisibleActionItemIds() {
        return m_visibleActionItemIds;
    }

    public void setVisibleActionItemIds(String visibleActionItemIds) {
        m_visibleActionItemIds = visibleActionItemIds;
    }

    public boolean isDataSecuredForTheUser() {
        return m_isDataSecuredForTheUser;
    }

    public void setDataSecuredForTheUser(boolean dataSecuredForTheUser) {
        m_isDataSecuredForTheUser = dataSecuredForTheUser;
    }

    private SecurityManager m_securityManager;
    private String m_visibleActionItemIds;
    private boolean m_isDataSecuredForTheUser;

    private final static String SUB_SYSTEM = "PMS";
    private final static String SECURITY_TYPE = "POLICY_WINDOW_DISPLAY";
    private final static String SOURCE_TABLE = "POLICY";
}
