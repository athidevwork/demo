package dti.oasis.navigationmgr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.navigationmgr.impl.NavigationManagerImpl;
import dti.oasis.util.PageBean;
import dti.oasis.util.PageDefLoadProcessor;
import dti.oasis.util.MenuBean;
import dti.oasis.recordset.Record;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * This NavigationManager class provides abstract methods to access the navigation configuration information of menus
 * (both global and tab menus) and action items via PageBean Object.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 30, 2007
 *
 * @author mmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/27/2008      mlm          90311 - Added Jump navigation related methods.
 * 09/12/2017      kshen        Grid replacement: added method isPageUseJqxGrid.
 * ---------------------------------------------------
 */
public abstract class NavigationManager {

    /**
     * The bean name of a NavigationManager extension if it is configured in the ApplicationContext.
     */
    public static final String BEAN_NAME = "NavigationManager";

    /**
     * The name of the property that points to the top navigation configuration file.
     */
    public static final String PROPERTY_TOPNAV_FILENAME = "topnavmenu";

    /**
     * Returns a synchronized static instance of Navigation Manager that has the implementation information.
     */
    public synchronized static NavigationManager getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(BEAN_NAME)) {
                c_instance = (NavigationManager) ApplicationContext.getInstance().getBean(BEAN_NAME);
            }
            else{
                c_instance = new NavigationManagerImpl();
                ((NavigationManagerImpl) c_instance).verifyConfig();
                ((NavigationManagerImpl) c_instance).initialize();
            }
        }
        return c_instance;
    }

    /**
     * This method returns a PageBean associated with the provided className. It contains information about Page title,
     * help url and all navigation elements like global menus, tab menus and action items associated with the page.
     *
     * @param conn        JDBC Connection
     * @param request     HttpServletRequest
     * @param className   Action Class Name
     * @param userId      Current User Id
     * @param pageDefLoadProcessor An instance of Page Load Processor
     * @return PageBean
     */
    public abstract PageBean getPageBean(Connection conn, HttpServletRequest request,
                            String className, String userId, PageDefLoadProcessor pageDefLoadProcessor) ;

    /**
     * Check if a page uses jqxGrid by struts action class name.
     * @param request
     * @param className
     * @return
     */
    public abstract boolean isPageUseJqxGrid(HttpServletRequest request, String className);

  /**
   * This method returns a actionItem MenuBean for the provided actionItemId from the PageBean. If no matching action item
   * is found, a null value is returned.
   *
   * @param userId
   * @param pageBean
   * @return MenuBean actionItem
   * @param actionItemGroupId
   * @param actionItemId
   */
    public abstract MenuBean getActionItem(HttpServletRequest request, String userId, PageBean pageBean, String actionItemGroupId, String actionItemId);

  /**
   * This method returns a cloned copy of actionItem MenuBean for the provided actionItemId from the PageBean.
   * If no matching action item is found, a null value is returned.
   *
   * @param userId
   * @param pageBean
   * @return MenuBean actionItem
   * @param actionItemGroupId
   * @param actionItemId
   * @param isClonedCopy
   */
    public abstract MenuBean getActionItem(HttpServletRequest request, String userId, PageBean pageBean, String actionItemGroupId, String actionItemId, boolean isClonedCopy);

  /**
   * This method returns an ArrayList that contains all action items which are available from leftNavActions and
   * actionItemGroups for the provided PageBean.
   *
   * @param pageBean
   * @return ArrayList of actionItems
   */
    public abstract ArrayList getAllActionItems(PageBean pageBean) ;

  /**
   * This method removes all action items from the provided PageBean
   *
   * @param pageBean
   */
    public abstract void removeAllActionItems(PageBean pageBean);

  /**
   * This method removes all action items from a particular action item group for the provided PageBean.
   *
   * @param pageBean
   * @param actionItemGroupId
   */
    public abstract void removeAllActionItemsForGroup(PageBean pageBean, String actionItemGroupId);

  /**
   * This method removes a specific action item from a particular action item group for the provided PageBean.
   *
   * @param userId
   * @param pageBean
   * @param actionItemGroupId
   * @param actionItemId
   */
    public abstract void removeActionItem(HttpServletRequest request, String userId, PageBean pageBean, String actionItemGroupId, String actionItemId);

  /**
   * This method returns a Record that contains all information about a particular jump navigation .
   *
   * @param conn
   * @param pfWebJumpNavigationId
   */
    public abstract Record getJumpNavigationInfo(Connection conn, String pfWebJumpNavigationId);

  /**
   * This method returns a Record that contains a summary of context conversion.
   *
   * @param inputRecord
   */
    public abstract Record getJumpToContextConversion(Record inputRecord);

    /**
     * Clear all Navigation Manager cached data.
     * @param dbPoolId
     */
    public abstract void clearNavigationCache(String dbPoolId);

    private static NavigationManager c_instance;
}
