package dti.oasis.navigationmgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ApplicationLifecycleListener;
import dti.oasis.app.RefreshParmsEventListener;
import dti.oasis.cachemgr.Cache;
import dti.oasis.cachemgr.CacheManager;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.http.Module;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.navigationmgr.NavigationManager;
import dti.oasis.recordset.Record;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.MenuBean;
import dti.oasis.util.PageBean;
import dti.oasis.util.PageDefLoadProcessor;
import dti.oasis.util.StringUtils;
import oracle.xdb.XMLType;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.ComparatorUtils;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details for NavigationManager, which is used to render navigation UI.
 * <p/>
 * <p>This class loads all navigation information from webwb configuration. This class has a method that loads the
 * PageBean object for the requested Page.
 * <p/>
 * All the menus are cached at the page level (for backward compatibility) by caching the PageBean or at global level
 * (loaded only if the menus are not found in the cache) and used up for each PageBean request. In addition to the menus,
 * the PageBean is always cached for better performance.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 30, 2007
 *
 * @author mmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/28/2007       sxm         Added tooltip attribute
 * 01/11/2007       James       Add a new menu Type called "ROOTMENU" to define
 *                              the root menu for an application
 * 03/18/2008       wer         Fix NavigationManager caching of action item groups
 * 03/18/2008       wer         Set the order of action items based on the sequence
 * 04/25/2008       kenney      Fix the function getClonedCopyAndAddToActionItemGroupMapper()
 * 06/23/2008       fcb         83145: Changed the value passed to setHelpUrl.
 * 06/25/2008       yhchen      83415: For UI0/1 also set action groups for page bean
 * 09/25/2008       Larry       Issue 86826 DB connection leakage change
 * 02/27/2009       mlm         90311 - Added Jump navigation related methods.
 * 05/19/2009       mlm         Refactor to replace page fk reference with page code
 * 10/07/2009       fcb         Issue 96764: temporaryActionItemQuery, globalMenuQuery, allActionGroupAndItemsQuery,
 *                              and menuQuery modified: enforced security items that are defined as
 *                              secured, but do not have any profile assigned.
 * 10/20/2009       mlm         Refactor to add ODS Reports link to global menu
 * 01/12/2010       James       Added id, access_trail_b when loading page bean.
 * 06/08/2010       James       Issue#105820 add table PF_WEB_PAGE_NAV_XREF
 * 09/01/2010       mlm         111518 - Performance tuning for views.
 * 10/28/2011       mlm         126732 - Refactored to treat NULL web application security value as "Y".
 * 07/18/2012       mlm         135057 - Performance tuning for ROOTMENU, TOP NAV, TABGROUP and ACTIONGRP queries to use
 *                              direct SQL statement instead of using PF_WEB_NAVIGATION_UTIL view.
 * 02/04/2013       mlm         141810 - Enhanced to load navigation groups via DOM/XPATH in order to avoid round-trips
 *                              to DB, during recursive load of all menus.
 * 04/11/2013       mlm         142772 - Refactored to render secured global menus correctly.
 * 06/14/2013       mlm         145900 - Refactored to CAST connect_path_by_pk column as VARCHAR2(4000)
 * 09/17/13         Parker      148113 - The 'Set to NULL' logic isn't work in page/jump navigation.
 * 12/16/13         fcb         150767 - refactored to use RefreshParmsEventListener.
 * 10/17/2014       mlm         158449 - Added support for WL 12.1.3
 * 09/11/2017       kshen       Grid replacement: added method isPageUseJqxGrid. Changed page bean related methods to
 *                              support use_jqx_grid_b.
 * ---------------------------------------------------
 */
public class NavigationManagerImpl extends NavigationManager implements ApplicationLifecycleListener, RefreshParmsEventListener {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * This methods returns a PageBean associated with the provided className. It contains information about Page title,
     * help url and all navigation elements like global menus, tab menus and action items associated with the page.
     * <p/>
     * The PageBean is always load from the cache. If the PageBean is not found in the cache, it is loaded and saved
     * into the cache.
     * <p/>
     * For old UIStyle, the menus and action items are not cached at the global level since they are always referenced by
     * the requested page.
     * <p/>
     * For new UIStyle, the menus and action items are cached one time for the very first request for the application and
     * used for initializing the Page Bean during subsequent requests.
     *
     * @param conn                 JDBC Connection
     * @param request              HttpServletRequest
     * @param className            Action Class Name
     * @param userId               Current User Id
     * @param pageDefLoadProcessor An instance of Page Load Processor
     * @return PageBean
     */
    public PageBean getPageBean(Connection conn, HttpServletRequest request,
                                String className, String userId, PageDefLoadProcessor pageDefLoadProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPageBean", new Object[]{conn, request, className, userId, pageDefLoadProcessor});
        }

        PageBean pageBean = null;

        try {
            String dbPoolId = (String) ActionHelper.getDbPoolId(request);

            ArrayList userList;
            userList = getUserListFromCache(dbPoolId);
            if (!userList.contains(userId)) {
                if (!userList.contains(userId)) {
                    userList.add(userId);
                }
                getDbPoolIdListCache().put(dbPoolId, userList);
            }

            Cache pageBeans = getUserPageBeanCache(userId, dbPoolId);

            // Always return a clone of PageBean from the cache, so that if any changes are made to the PageBean members
            // directly at Action Class level, it will not effect the cached PageBean.
            if (pageBeans.contains(className)) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "getPageBean", "PageBean retreived from Cache for className:" + className);
                }
                pageBean = (PageBean) ((PageBean) pageBeans.get(className)).clone();
            } else {
                pageBeans.put(className, initalizePageBean(conn, request, className, userId, pageDefLoadProcessor));
                pageBean = (PageBean) ((PageBean) pageBeans.get(className)).clone();
            }
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Unexpected Exception while getting Page Bean", e);
            l.throwing(getClass().getName(), "getPageBean", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPageBean", pageBean);
        }
        return pageBean;
    }

    /**
     * Check if a page uses jqxGrid by struts action class name.
     * @param request
     * @param className
     * @return
     */
    public boolean isPageUseJqxGrid(HttpServletRequest request, String className) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPageUseJqxGrid", new Object[]{request, className});
        }

        PageBean pageBean = getCachedPageBean(request, className);

        String useJqxGridB = "";

        if (pageBean != null) {
            useJqxGridB = pageBean.getUseJqxGridB();
        } else {
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = ActionHelper.getConnection(request);

                stmt = conn.prepareStatement(pageUseJqxGridQuery);
                stmt.setString(1, className);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    useJqxGridB = rs.getString(1);
                }
            } catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Unable to get page useJqxGrid property.", e);
                l.throwing(getClass().getName(), "isPageUseJqxGrid", ae);
                throw ae;
            } finally {
                DatabaseUtils.close(stmt, rs, conn);
            }
        }

        boolean useJqxGrid;

        if (StringUtils.isBlank(useJqxGridB)) {
            useJqxGrid = ActionHelper.isJqxGridEnabled(request);
        } else {
            useJqxGrid = "Y".equals(useJqxGridB);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPageUseJqxGrid", useJqxGrid);
        }
        return useJqxGrid;
    }

    /**
     * Get cached page bean by struts action name.
     * @param request
     * @param className
     * @return
     */
    private PageBean getCachedPageBean(HttpServletRequest request, String className) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCachedPageBean", new Object[]{className});
        }

        PageBean pageBean = null;

        String userId = ActionHelper.getCurrentUserId(request);
        String dbPoolId = ActionHelper.getDbPoolId(request);

        Cache pageBeans = getUserPageBeanCache(userId, dbPoolId);

        if (pageBeans.contains(className)) {
            pageBean = (PageBean) pageBeans.get(className);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCachedPageBean", pageBean);
        }
        return pageBean;
    }

    /**
     * This method returns a actionItem MenuBean for the provided actionItemId within the actionItemGroupId from the PageBean.
     * If the provided actionItemGroupId is null or empty string, first occurrence of the action item is returned, if it is found
     * in multiple action item groups; otherwise action item group is automatically determined before the action item is returned.
     * <p/>
     * If no matching action item is found, a null value is returned.
     *
     * @param userId
     * @param pageBean
     * @param actionItemGroupId
     * @param actionItemId
     * @return MenuBean actionItem
     */
    public MenuBean getActionItem(HttpServletRequest request, String userId, PageBean pageBean, String actionItemGroupId, String actionItemId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getActionItem", new Object[]{request, userId, pageBean, actionItemGroupId, actionItemId});
        }

        MenuBean actionItem = getActionItem(request, userId, pageBean, actionItemGroupId, actionItemId, false);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getActionItem", actionItem);
        }
        return actionItem;
    }

    /**
     * This method returns a cloned copy of actionItem MenuBean for the provided actionItemId within the actionItemGroupId
     * from the pageBean. If the provided actionItemGroupId is null or empty string, first occurrence of the action item
     * is returned, if it is found in multiple action item groups; otherwise action item group is automatically determined
     * before the action item is returned.
     * <p/>
     * If no matching action item is found, a null value is returned.
     *
     * @param userId
     * @param pageBean
     * @param actionItemGroupId
     * @param actionItemId
     * @param isClonedCopy
     * @return MenuBean actionItem
     */
    public MenuBean getActionItem(HttpServletRequest request, String userId, PageBean pageBean, String actionItemGroupId, String actionItemId, boolean isClonedCopy) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getActionItem", new Object[]{pageBean, actionItemGroupId, actionItemId, isClonedCopy});
        }

        MenuBean actionItem = null;
        String dbPoolId = (String) ActionHelper.getDbPoolId(request);

        if (StringUtils.isBlank(actionItemGroupId)) {

            Cache cacheForallActionItemGroups = getUserActionItemGroupCache(userId, dbPoolId);

            Map actionItemToActionItemGroupMapper = getActionItemToActionItemGroupMapper(cacheForallActionItemGroups, userId, dbPoolId);
            if (actionItemToActionItemGroupMapper.containsKey(actionItemId)) {
                String actionItemGroups = (String) actionItemToActionItemGroupMapper.get(actionItemId);
                String[] groups = actionItemGroups.split(",");
                for (int i = 0; i < groups.length; i++) {
                    actionItem = getActionItem(request, userId, pageBean, groups[i], actionItemId, isClonedCopy);
                    break;
                }
            }
        } else {
            if (pageBean.getActionItemGroups().get(actionItemGroupId) != null) {
                Iterator actionItemIt = ((ArrayList) pageBean.getActionItemGroups().get(actionItemGroupId)).iterator();
                while (actionItemIt.hasNext()) {
                    MenuBean menuBean = (MenuBean) actionItemIt.next();
                    if (menuBean.getId().equalsIgnoreCase(actionItemId)) {
                        actionItem = menuBean;
                        break;
                    }
                }
            }
        }

        // If an action item is not yet located, then it is not yet converted into action item group. So, look in
        // temporarily action item list (in leftNavActions())
        if (actionItem == null) {
            Iterator tempActionItemIt = ((ArrayList) pageBean.getLeftNavActions()).iterator();
            while (tempActionItemIt.hasNext()) {
                MenuBean tempActionItem = (MenuBean) tempActionItemIt.next();
                if (tempActionItem.getId().equalsIgnoreCase(actionItemId)) {
                    actionItem = tempActionItem;
                }
            }
        }

        if (isClonedCopy && actionItem != null) {
            try {
                MenuBean clonedActionItem = (MenuBean) actionItem.clone();
                return clonedActionItem;
            } catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Unexpected Exception while getting action item", e);
                l.throwing(getClass().getName(), "getActionItem", ae);
                throw ae;
            }
        } else {
            return actionItem;
        }
    }

    /**
     * This method returns an ArrayList that contains all action items which are available from leftNavActions and
     * actionItemGroups for the provided PageBean.
     *
     * @param pageBean
     * @return ArrayList of actionItems
     */
    public ArrayList getAllActionItems(PageBean pageBean) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllActionItems", new Object[]{pageBean});
        }

        ArrayList allActionItems = new ArrayList();
        if (pageBean != null) {
            allActionItems.addAll(pageBean.getLeftNavActions());
            Iterator it = pageBean.getActionItemGroups().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                allActionItems.addAll((ArrayList) pageBean.getActionItemGroups().get(entry.getKey()));
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllActionItems", allActionItems);
        }
        return allActionItems;
    }

    public void removeAllActionItems(PageBean pageBean) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "removeAllActionItems", new Object[]{pageBean});
        }

        if (pageBean != null) {
            pageBean.getLeftNavActions().clear();
            Iterator it = pageBean.getActionItemGroups().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                ((ArrayList) pageBean.getActionItemGroups().get(entry.getKey())).clear();
            }
        }

        l.exiting(getClass().getName(), "removeAllActionItems");
        return;
    }

    public void removeAllActionItemsForGroup(PageBean pageBean, String actionItemGroupId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "removeAllActionItemsForGroup", new Object[]{pageBean, actionItemGroupId});
        }

        if (pageBean != null) {
            if (pageBean.getActionItemGroups().get(actionItemGroupId) != null) {
                ((ArrayList) pageBean.getActionItemGroups().get(actionItemGroupId)).clear();
            }
        }

        l.exiting(getClass().getName(), "removeAllActionItemsForGroup");
        return;
    }

    public void removeActionItem(HttpServletRequest request, String userId, PageBean pageBean, String actionItemGroupId, String actionItemId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "removeActionItem", new Object[]{request, userId, pageBean, actionItemGroupId, actionItemId});
        }

        String dbPoolId = (String) ActionHelper.getDbPoolId(request);

        if (pageBean != null) {
            if (StringUtils.isBlank(actionItemGroupId)) {
                Cache cacheForallActionItemGroups = getUserActionItemGroupCache(userId, dbPoolId);

                Map actionItemToActionItemGroupMapper = getActionItemToActionItemGroupMapper(cacheForallActionItemGroups, userId, dbPoolId);
                if (actionItemToActionItemGroupMapper.containsKey(actionItemId)) {
                    String actionItemGroups = (String) actionItemToActionItemGroupMapper.get(actionItemId);
                    String[] groups = actionItemGroups.split(",");
                    for (int i = 0; i < groups.length; i++) {
                        removeActionItem(request, userId, pageBean, groups[i], actionItemId);
                    }
                }
            } else {
                if (pageBean.getActionItemGroups().get(actionItemGroupId) != null) {
                    ArrayList actionItems = ((ArrayList) pageBean.getActionItemGroups().get(actionItemGroupId));
                    ArrayList newActionItems = new ArrayList();
                    Iterator it = actionItems.iterator();
                    while (it.hasNext()) {
                        MenuBean menuBean = (MenuBean) it.next();
                        if (!menuBean.getId().equalsIgnoreCase(actionItemId)) {
                            if (!newActionItems.contains(menuBean)) {
                                newActionItems.add(menuBean);
                            }
                        }
                    }
                    pageBean.getActionItemGroups().put(actionItemGroupId, newActionItems);
                }
            }
        }

        l.exiting(getClass().getName(), "removeActionItem");
        return;
    }

    /**
     * This method returns a Record that contains all information about a particular jump navigation .
     *
     * @param conn
     * @param pfWebJumpNavigationId
     */
    public Record getJumpNavigationInfo(Connection conn, String pfWebJumpNavigationId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getJumpNavigationInfo", new Object[]{conn, pfWebJumpNavigationId});
        }

        Record outputRecord = new Record();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(jumpNavigationQuery);
            stmt.setLong(1, Long.parseLong(pfWebJumpNavigationId));
            stmt.setLong(2, Long.parseLong(pfWebJumpNavigationId));
            stmt.setString(3, "");
            l.fine(new StringBuffer("Executing: ").append(jumpNavigationQuery).append(" with ").
                        append(pfWebJumpNavigationId).toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                outputRecord.setFieldValue("sourceContextCode", rs.getString("source_context_code"));
                outputRecord.setFieldValue("sourceContextFieldId", rs.getString("source_field_id").substring(0,1).toLowerCase() +
                                                                    rs.getString("source_field_id").substring(1));
                outputRecord.setFieldValue("destinationApplicationId", rs.getString("destination_App_Id"));
                outputRecord.setFieldValue("destinationContextCode", rs.getString("destination_context_code"));
                outputRecord.setFieldValue("destinationContextFieldId", rs.getString("destination_field_id").substring(0,1).toLowerCase() +
                                                                        rs.getString("destination_field_id").substring(1));
                outputRecord.setFieldValue("navUrl", rs.getString("nav_url"));
                outputRecord.setFieldValue("urlParameter", rs.getString("url_parameter"));
            }
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to retreive Jump Navigation Information for provided Id:" + pfWebJumpNavigationId, e);
            l.throwing(getClass().getName(), "getJumpNavigationInfo", ae);
            throw ae;
        } finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null) DatabaseUtils.close(conn);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getJumpNavigationInfo", outputRecord);
        }
        return outputRecord;
    }

   /**
   * This method returns a Record that contains a summary of context conversion.
   *
   * @param inputRecord
   */
    public Record getJumpToContextConversion(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getJumpToContextConversion", new Object[]{inputRecord});
        }

        Record outputRecord;
       StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Context_Conversion");
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get context conversion information. ", e);
            l.throwing(getClass().getName(), "getJumpToContextConversion", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getJumpToContextConversion", outputRecord);
        }
       return outputRecord;
   }

    private PageBean initalizePageBean(Connection conn, HttpServletRequest request,
                                       String className, String userId,
                                       PageDefLoadProcessor pageDefLoadProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initalizePageBean", new Object[]{conn, request, className, userId, pageDefLoadProcessor});
        }

        ResultSet rs = null;
        PreparedStatement stmt = null;
        PageBean bean = new PageBean();
        String dbPoolId = (String) ActionHelper.getDbPoolId(request);
        try {
            stmt = conn.prepareStatement(pageBeanQuery);
            stmt.setString(1, className);
            l.fine(new StringBuffer("Executing: ").append(pageBeanQuery).append(" with ").
                    append(className).toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                bean.setWidth(rs.getInt(1));
                bean.setHeight(rs.getInt(2));
                bean.setTop(rs.getInt(3));
                bean.setLeft(rs.getInt(4));
                bean.setTitle(rs.getString(5));
                bean.setHelpUrl((rs.getString(6) == null ? "" : rs.getString(6)));
                bean.setId(rs.getString(7));
                bean.setAccessTrailB(rs.getString(8));

                String useJqxGridB = rs.getString(9);
                if (StringUtils.isBlank(useJqxGridB)) {
                    useJqxGridB = ActionHelper.isJqxGridEnabled(request) ? "Y": "N";
                }
                bean.setUseJqxGridB(useJqxGridB);
            }
            String UIStyleEdition = ApplicationContext.getInstance().getProperty("UIStyleEdition", "0");
            if (UIStyleEdition.equalsIgnoreCase("0") || UIStyleEdition.equalsIgnoreCase("1")) {
                Map allActionItemGroups = getAllActionItemGroups(conn, className, bean, pageDefLoadProcessor, userId, dbPoolId);
                bean.setActionItemGroups(allActionItemGroups);
                bean.setJumpNavigations(new ArrayList());
//                bean.setTopNavMenu(getTopNavMenu(request));
                Map allMenuGroups = getAllMenuGroups(request, conn, className, bean, pageDefLoadProcessor, userId);
                bean.setTopNavMenu((ArrayList) allMenuGroups.get(getCacheIdForApplicationTopNav(userId, dbPoolId)));
                stmt = conn.prepareStatement(menuQuery);
                stmt.setString(1, className);
                stmt.setString(2, userId);
                stmt.setString(3, className);
                stmt.setString(4, userId);
                l.fine(new StringBuffer("Executing: ").append(menuQuery).append(" with ").
                        append(className).append(",").append(className).toString());
                processMenu(bean, stmt.executeQuery(), className, pageDefLoadProcessor);

            } else {
                Map allMenuGroups = getAllMenuGroups(request, conn, className, bean, pageDefLoadProcessor, userId);
                Map allActionItemGroups = getAllActionItemGroups(conn, className, bean, pageDefLoadProcessor, userId, dbPoolId);

                bean.setMenuGroups(allMenuGroups);
                bean.setActionItemGroups(allActionItemGroups);

                List allowedNavigations = getAllowedNavigations(conn, className);
                bean.setAllowedNavigations(allowedNavigations);

                // For UI2, the leftNavMenu will always contain the global navigation menus.
                bean.setLeftNavMenu((ArrayList) allMenuGroups.get(getCacheIdForUserGlobalNavMenuGroup(userId,dbPoolId)));
                bean.setGlobalMenuId(getGlobalMenuId(conn));

                // For UI2, the leftNavActions will always contain only temporary action items.
                bean.setLeftNavActions(new ArrayList());

                bean.setTopNavMenu((ArrayList) allMenuGroups.get(getCacheIdForApplicationTopNav(userId,dbPoolId)));

                bean.setJumpNavigations((ArrayList) allMenuGroups.get(getCacheIdForMenuGroupId(
                                                                        getCacheIdForPageJumpNavigation(bean.getTitle()),
                                                                        userId,dbPoolId)));
            }

        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to retreive Page Bean for the provided className:" + className, e);
            l.throwing(getClass().getName(), "initalizePageBean", ae);
            throw ae;
        } finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);

            removeNavigationDOMFromCache(userId, dbPoolId);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "initalizePageBean", bean);
        }
        return bean;
    }

    private Map getAllMenuGroups(HttpServletRequest request, Connection conn, String className, PageBean bean, PageDefLoadProcessor pageDefLoadProcessor, String userId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllMenuGroups", new Object[]{request, conn, className, bean, pageDefLoadProcessor, userId});
        }

        /**
         *  Always get a clone version of the menu group from cache to avoid changes made in Action Class to effect the
         *  cached entry.
         **/
        Map allMenuGroups = new HashMap();
        PreparedStatement stmt = null;
        ResultSet rsForRootMenu = null;
        String dbPoolId = (String) ActionHelper.getDbPoolId(request);
        Boolean cacheNavigationMenus = Boolean.valueOf(ApplicationContext.getInstance().getProperty(KEY_FOR_LOAD_NAVIGATION_LIST_AS_DOM, "true"));

        try {
            Cache cacheForallMenuGroups = getUserMenuGroupCache(userId, dbPoolId);

            // 1. Get all application top nav menu group
            if (cacheForallMenuGroups.contains(getCacheIdForApplicationTopNav(userId,dbPoolId))) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "getAllMenuGroups", "Top Nav menu retreived from cache for className:" + className);
                }
                allMenuGroups.put(getCacheIdForApplicationTopNav(userId,dbPoolId), getClonedCopy((ArrayList) cacheForallMenuGroups.get(getCacheIdForApplicationTopNav(userId, dbPoolId))));
            } else {
                //Top nav menu group is not yet cached, so load the top nav menu and cache it for subsequent requests.
                try {
                    ArrayList allTopNavMenus;
                    if (hasPageEntitlementFileName()) {
                        allTopNavMenus = getTopNavMenu(request);
                    } else {
                        allTopNavMenus = new ArrayList();
                        String topnavMenuId = ApplicationContext.getInstance().getProperty("topnavMenuId", "");
                        if (l.isLoggable(Level.FINE)) {
                            l.logp(Level.FINE, getClass().getName(), "getAllMenuGroups", "Getting topnav menu Information for menuId:" + topnavMenuId);
                        }
                        if (!StringUtils.isBlank(topnavMenuId)) {
                            long topnavFk = -1;
                            stmt = conn.prepareStatement(topnavMenuQuery);
                            stmt.setString(1, topnavMenuId);
                            ResultSet rsForTopnavMenu = stmt.executeQuery();
                            if (rsForTopnavMenu.next()) {
                                topnavFk = rsForTopnavMenu.getLong(1);
                            }
                            if (topnavFk > 0) {
                                boolean result = getAllNavGroupMenus(bean, topnavFk, className, pageDefLoadProcessor, conn, userId, allTopNavMenus, false, dbPoolId, true);
                                String idListForAddlGlobalNavMenu = ApplicationContext.getInstance().getProperty(KEY_FOR_ADDL_GLOBAL_NAV_MENU_ID_LIST);
                                stmt = conn.prepareStatement(addlGlobalNavMenuQuery);
                                stmt.setLong(1, topnavFk);
                                stmt.setString(2, idListForAddlGlobalNavMenu);
                                stmt.setString(3, userId);
                                l.fine(new StringBuffer("Executing: ").append(addlGlobalNavMenuQuery)
                                        .append("\r\n WITH ").append("className:" + className).append(",")
                                        .append("parentNavFk:" + topnavFk).append(",")
                                        .append("idListForAddlGlobalNavMenu:" + idListForAddlGlobalNavMenu).append(",")
                                        .append("userId:").append(userId).toString());
                                rsForTopnavMenu = stmt.executeQuery();
                                while (rsForTopnavMenu.next()) {
                                    MenuBean menuBean = getMenuBean(rsForTopnavMenu);
                                    if (pageDefLoadProcessor.postProcessMenuItem(menuBean)) {  //if the menu item is not protected
                                        if (!allTopNavMenus.contains(menuBean)) {
                                            allTopNavMenus.add(menuBean);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    cacheForallMenuGroups.put(getCacheIdForApplicationTopNav(userId,dbPoolId), allTopNavMenus);
                    allMenuGroups.put(getCacheIdForApplicationTopNav(userId, dbPoolId), getClonedCopy(allTopNavMenus));
                } catch (Exception e) {
                    allMenuGroups.put(getCacheIdForApplicationTopNav(userId,dbPoolId), new ArrayList());
                    AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Unexpected Exception while getting top nav menus", e);
                    l.throwing(getClass().getName(), "getAllMenuGroups", ae);
                    throw ae;
                }
            }

            // 2. Get all application level menu group - global navigation menus
            if (cacheForallMenuGroups.contains(getCacheIdForUserGlobalNavMenuGroup(userId,dbPoolId))) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "getAllMenuGroups", "Global Nav menu retreived from cache for className:" + className);
                }
                allMenuGroups.put(getCacheIdForUserGlobalNavMenuGroup(userId,dbPoolId), getClonedCopy((ArrayList) cacheForallMenuGroups.get(getCacheIdForUserGlobalNavMenuGroup(userId, dbPoolId))));
            } else {
                //Global navigation group is not yet cached, so load and cache it for subsequent requests.
                try {
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "getAllMenuGroups", "Getting Global Menus for Application ID:" + ApplicationContext.getInstance().getProperty(KEY_FOR_APPLICATION_ID));
                    }
                    long rootWebNavFk = -1;
                    stmt = conn.prepareStatement(rootMenuQueryForGlobalNav);
                    stmt.setString(1, ApplicationContext.getInstance().getProperty(KEY_FOR_APPLICATION_ID));
                    rsForRootMenu = stmt.executeQuery();
                    if (rsForRootMenu.next()) {
                        rootWebNavFk = rsForRootMenu.getLong(1);
                    }
                    ArrayList allGlobalNavMenus = new ArrayList();
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "getAllMenuGroups", "Getting Global Navigation Menus For Root Nav Fk:" + rootWebNavFk);
                    }
                    if (rootWebNavFk > 0) {
                        processedMenuGroups.clear();
                        boolean result = getAllMenus(bean, rootWebNavFk, className, pageDefLoadProcessor, conn, userId, allGlobalNavMenus, dbPoolId);
                        pageDefLoadProcessor.postProcessMenuItems(allGlobalNavMenus);
                        if (!cacheNavigationMenus) {
                            Iterator it = allGlobalNavMenus.iterator();
                            while (it.hasNext()) {
                                MenuBean menuBean = (MenuBean) it.next();
                                if (processedMenuGroups.containsKey(menuBean.getId())) {
                                    if (l.isLoggable(Level.FINE)) {
                                        l.logp(Level.FINE, getClass().getName(), "getAllMenuGroups", "Setting isSubMenuExists for Id:" + menuBean.getId());
                                    }
                                    menuBean.setSubMenuExists((Boolean) processedMenuGroups.get(menuBean.getId()));
                                }
                            }
                        }
                        cacheForallMenuGroups.put(getCacheIdForUserGlobalNavMenuGroup(userId,dbPoolId), allGlobalNavMenus);
                    }
                    allMenuGroups.put(getCacheIdForUserGlobalNavMenuGroup(userId,dbPoolId), getClonedCopy(allGlobalNavMenus));
                } catch (Exception e) {
                    allMenuGroups.put(getCacheIdForUserGlobalNavMenuGroup(userId,dbPoolId), new ArrayList());
                    AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Unexpected Exception while getting global application menus", e);
                    l.throwing(getClass().getName(), "getAllMenuGroups", ae);
                    throw ae;
                } finally {
                    if (rsForRootMenu != null) DatabaseUtils.close(rsForRootMenu);
                    if (stmt != null) DatabaseUtils.close(stmt);
                }
            }

            ResultSet rsForTabGroupMenu = null;
            try {
                // 3. Get all tab menus
                stmt = conn.prepareStatement(tabGroupQuery);
                rsForTabGroupMenu = stmt.executeQuery();
                //Get all tabMenuGroups and cache them for subsequent requests.
                while (rsForTabGroupMenu.next()) {
                    ArrayList allItemsForTheGroup;
                    if (cacheForallMenuGroups.contains(getCacheIdForMenuGroupId(rsForTabGroupMenu.getString(2), userId, dbPoolId))) {
                        if (l.isLoggable(Level.FINE)) {
                            l.logp(Level.FINE, getClass().getName(), "getAllMenuGroups", "Tab Menu:" + getCacheIdForMenuGroupId(rsForTabGroupMenu.getString(2), userId, dbPoolId) + " retreived from cache for className:" + className);
                        }
                        allMenuGroups.put(getCacheIdForMenuGroupId(rsForTabGroupMenu.getString(2), userId, dbPoolId), getClonedCopy((ArrayList) cacheForallMenuGroups.get(getCacheIdForMenuGroupId(rsForTabGroupMenu.getString(2), userId, dbPoolId))));
                    } else {
                        //This tab menu group is not yet cached, so load and cache it for subsequent requests.

                        allItemsForTheGroup = new ArrayList();
                        processedMenuGroups.clear();
                        //Load tab menu.
                        boolean result = getAllNavGroupMenus(bean, rsForTabGroupMenu.getLong(1), className, pageDefLoadProcessor, conn, userId, allItemsForTheGroup, false, dbPoolId, false);
                        if (!cacheNavigationMenus) {
                            Iterator it = allItemsForTheGroup.iterator();
                            while (it.hasNext()) {
                                MenuBean menuBean = (MenuBean) it.next();
                                if (processedMenuGroups.containsKey(menuBean.getId())) {
                                    if (l.isLoggable(Level.FINE)) {
                                        l.logp(Level.FINE, getClass().getName(), "getAllMenuGroups", "Setting isSubMenuExists for Id:" + menuBean.getId());
                                    }
                                    menuBean.setSubMenuExists((Boolean) processedMenuGroups.get(menuBean.getId()));
                                }
                            }
                        }
                        //Add tab menu to list of available menu groups and cache it.
                        cacheForallMenuGroups.put(getCacheIdForMenuGroupId(rsForTabGroupMenu.getString(2), userId, dbPoolId), allItemsForTheGroup);
                        allMenuGroups.put(getCacheIdForMenuGroupId(rsForTabGroupMenu.getString(2), userId, dbPoolId), getClonedCopy(allItemsForTheGroup));
                    }
                }
            } catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Unexpected Exception while getting application tab menus", e);
                l.throwing(getClass().getName(), "getAllMenuGroups", ae);
                throw ae;
            } finally {
                if (rsForTabGroupMenu != null) DatabaseUtils.close(rsForTabGroupMenu);
                if (stmt != null) DatabaseUtils.close(stmt);
            }

            try
            {
                String jumpNavigationGroupId =  getCacheIdForPageJumpNavigation(bean.getTitle());
                if (cacheForallMenuGroups.contains(getCacheIdForMenuGroupId(jumpNavigationGroupId, userId, dbPoolId))) {
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "getAllMenuGroups", "Jump Navigation List:" + getCacheIdForMenuGroupId(jumpNavigationGroupId, userId, dbPoolId) + " retreived from cache for className:" + className);
                    }
                    allMenuGroups.put(getCacheIdForMenuGroupId(jumpNavigationGroupId, userId, dbPoolId), getClonedCopy((ArrayList) cacheForallMenuGroups.get(getCacheIdForMenuGroupId(jumpNavigationGroupId, userId, dbPoolId))));
                } else {
                    //The jump navigation is not yet cached, so load and cache it for subsequent requests.

                    //Load jump navigation.
                    List allItemsForTheGroup = getJumpNavigationList(conn, className, bean, pageDefLoadProcessor, userId);

                    //Add tab menu to list of available menu groups and cache it.
                    cacheForallMenuGroups.put(getCacheIdForMenuGroupId(jumpNavigationGroupId, userId, dbPoolId), allItemsForTheGroup);
                    allMenuGroups.put(getCacheIdForMenuGroupId(jumpNavigationGroupId, userId, dbPoolId), getClonedCopy(allItemsForTheGroup));
                }
            } catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Unexpected Exception while getting jump navigation list for pageBean", e);
                l.throwing(getClass().getName(), "getAllMenuGroups", ae);
                throw ae;
            } finally {
                if (rsForTabGroupMenu != null) DatabaseUtils.close(rsForTabGroupMenu);
                if (stmt != null) DatabaseUtils.close(stmt);
            }

        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Unexpected Exception while getting all menu group list for pageBean", e);
            l.throwing(getClass().getName(), "getAllMenuGroups", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllMenuGroups", allMenuGroups);
        }
        return allMenuGroups;
    }

    /**
     * get allowed navigations for page
     * @param conn
     * @param className
     * @return
     */
    private List getAllowedNavigations(Connection conn, String className) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllowedNavigations", new Object[]{conn, className});
        }

        ArrayList allowedNavigations = new ArrayList();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(allowedNavigationsForPage);
            stmt.setString(1, className);
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (!allowedNavigations.contains(rs.getString(1))) {
                    allowedNavigations.add(rs.getString(1));
                }
            }
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR,
                    "Unexpected Exception while getting allowed navigations for pageBean", e);
            l.throwing(getClass().getName(), "getAllowedNavigations", ae);
            throw ae;
        } finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllowedNavigations", allowedNavigations);
        }
        return allowedNavigations;
    }

   /**
     * get global menu id
     * @param conn
     * @return
     */
   private String getGlobalMenuId(Connection conn) {
       if (l.isLoggable(Level.FINER)) {
           l.entering(getClass().getName(), "getGlobalMenuId", new Object[]{conn});
       }

       String globalMenuId = null;
       PreparedStatement stmt = null;
       ResultSet rs = null;
       try {
           stmt = conn.prepareStatement(rootMenuQueryForGlobalNav);
           stmt.setString(1, ApplicationContext.getInstance().getProperty(KEY_FOR_APPLICATION_ID));
           rs = stmt.executeQuery();
           if (rs.next()) {
               globalMenuId = rs.getString(2);
           }
       } catch (Exception e) {
           AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR,
                   "Unexpected Exception while getting global menu id", e);
           l.throwing(getClass().getName(), "getGlobalMenuId", ae);
           throw ae;
       } finally {
           if (rs != null) DatabaseUtils.close(rs);
           if (stmt != null) DatabaseUtils.close(stmt);
       }

       if (l.isLoggable(Level.FINER)) {
           l.exiting(getClass().getName(), "getGlobalMenuId", globalMenuId);
       }
       return globalMenuId;
   }

    private Map getAllActionItemGroups(Connection conn, String className, PageBean bean, PageDefLoadProcessor pageDefLoadProcessor, String userId, String dbPoolId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllActionItemGroups", new Object[]{conn, className, bean, pageDefLoadProcessor, userId, dbPoolId});
        }

        Map allActionItemGroups = new HashMap();
        /**
         *  Always get a clone version of the action item group from cache to avoid changes made in Action Class to effect the
         *  cached entry.
         **/
        try {
            Cache cacheForallActionItemGroups = getUserActionItemGroupCache(userId, dbPoolId);
            boolean actionItemGroupsAreCached = cacheForallActionItemGroups.getSize() > 0;

            // Next, load the Action Item Groups if they are not cached.
            if (!actionItemGroupsAreCached) {
                loadAllActionItems(conn, className, bean, pageDefLoadProcessor, userId, dbPoolId);
            }

            // After loading the master list and creating the required cache for the user, copy all of the items from the cache
            Iterator keyIter = cacheForallActionItemGroups.keyIterator();
            while (keyIter.hasNext()) {
                String key = (String) keyIter.next();
                Object fromCache = cacheForallActionItemGroups.get(key);
                if (fromCache instanceof List) {
                    List clonedGroup = getClonedCopy((List) fromCache);
                    allActionItemGroups.put(key, clonedGroup);
                }
            }

        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Error while getting action item group menus", e);
            l.throwing(getClass().getName(), "getAllActionItemGroups", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllActionItemGroups", allActionItemGroups);
        }
        return allActionItemGroups;
    }

    private void loadAllActionItems(Connection conn, String className, PageBean bean, PageDefLoadProcessor pageDefLoadProcessor, String userId, String dbPoolId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllActionItems", new Object[]{conn, className, bean, pageDefLoadProcessor, userId, dbPoolId});
        }

        synchronized (this) {
            Cache cacheForallActionItemGroups = getUserActionItemGroupCache(userId, dbPoolId);
            boolean actionItemGroupsAreCached = cacheForallActionItemGroups.getSize() > 0;
            if (actionItemGroupsAreCached) {
                l.fine("Action items are already loaded and cached for the user:" + userId + " with db pool id:" + dbPoolId);
                return;
            }

            Map actionItemToActionItemGroupMapper = getActionItemToActionItemGroupMapper(cacheForallActionItemGroups, userId, dbPoolId);

            Map allActionItemGroups = new HashMap();
            PreparedStatement stmt = null;
            ResultSet rsForAllActionGroupAndItems = null;
            try {
                StringBuffer applicationIdsBuff = getApplicationIdList();

                stmt = conn.prepareStatement(allActionGroupsAndItemsQuery);
                stmt.setString(1, applicationIdsBuff.toString());
                stmt.setString(2, userId);
                rsForAllActionGroupAndItems = stmt.executeQuery();
                List allItemsForTheGroup = null;
                String groupShortDescription = null;
                while (rsForAllActionGroupAndItems.next()) {
                    String type = rsForAllActionGroupAndItems.getString(3);
                    // If this is a new Group, and has action items
                    if ("ACTIONGRP".equals(type) ||
                            ("MENU".equals(type) && Boolean.valueOf(rsForAllActionGroupAndItems.getString(10)).booleanValue())) {
                        // If there is a prior allItemsForTheGroup, add it to the cache and map of all groups
                        if (allItemsForTheGroup != null) {
                            addActionItemsGroup(allItemsForTheGroup, cacheForallActionItemGroups, groupShortDescription, allActionItemGroups, actionItemToActionItemGroupMapper);
                        }

                        groupShortDescription = rsForAllActionGroupAndItems.getString(8);
                        //  Create the new List or get it from the cache, if already exists
                        if (cacheForallActionItemGroups.contains(groupShortDescription))
                        {
                            // allItemsForTheGroup already created due to navigation out of order. So, pull it from the cache.
                            allItemsForTheGroup = (ArrayList) cacheForallActionItemGroups.get(groupShortDescription);
                        } else {
                            allItemsForTheGroup = new ArrayList();
                        }

                        if (!Boolean.valueOf(rsForAllActionGroupAndItems.getString(10)).booleanValue()) {
                            if (l.isLoggable(Level.WARNING)) {
                                l.logp(Level.WARNING, getClass().getName(), "loadAllActionItems", rsForAllActionGroupAndItems.getString(10) + "->The action item group '" + groupShortDescription + "' has no child ACTIONITEM entries.");
                            }
                        }

                        // Verify that the action item group does not have a parent
                        if ("ACTIONGRP".equals(type) && !StringUtils.isBlank(rsForAllActionGroupAndItems.getString(9))) {
                            if (l.isLoggable(Level.SEVERE)) {
                                l.logp(Level.SEVERE, getClass().getName(), "loadAllActionItems", "The action item group '" + groupShortDescription + "' is a child of '" + rsForAllActionGroupAndItems.getString(9) + "'. Multiple levels of action item groups are not supported! This action item group is being added as a top-level action item group.");
                            }
                        }
                    }
                    //If this is an action item
                    else if ("ACTIONITEM".equals(type)) {
                        // does this action item have a parent? or has the right parent?
                        String parentId = rsForAllActionGroupAndItems.getString(9);
                        if (StringUtils.isBlank(parentId) ) {
                            if (l.isLoggable(Level.WARNING)) {
                                l.logp(Level.WARNING, getClass().getName(), "loadAllActionItems", "The pf_web_navigation_util record with short_description '" + rsForAllActionGroupAndItems.getString(8) + " is a action item, but has no parent Id");
                            }
                        }
                        else {
                         // This is an ActionItem that belongs to the group
                        //  Create the menu bean for the action item record and add it to the arraylist
                            MenuBean menuBean = getMenuBean(rsForAllActionGroupAndItems);
                            String sequence = rsForAllActionGroupAndItems.getString(7);
                            try {
                                menuBean.setSequence(new Integer(sequence));
                            }
                            catch (NumberFormatException e) {
                                // skip the sequence.
                            }
                            if (!groupShortDescription.equalsIgnoreCase(parentId)) {
                                if (l.isLoggable(Level.FINE)) {
                                    l.logp(Level.FINE, getClass().getName(), "loadAllActionItems", "The pf_web_navigation_util record with short_description '" + rsForAllActionGroupAndItems.getString(8) + " is a action item, but can not get a valid parent Id in sequence due to out of order retrieval from DB.");
                                }
                                // action Item belongs to different group. Save the items added to the current group and
                                // get the appropriate group for adding this action item.
                                addActionItemsGroup(allItemsForTheGroup, cacheForallActionItemGroups, groupShortDescription, allActionItemGroups, actionItemToActionItemGroupMapper);
                                groupShortDescription = parentId;
                                if (cacheForallActionItemGroups.contains(groupShortDescription))
                                {
                                    allItemsForTheGroup = (ArrayList) cacheForallActionItemGroups.get(groupShortDescription);
                                } else {
                                     allItemsForTheGroup = new ArrayList();
                                }
                            }
                            if (pageDefLoadProcessor.postProcessMenuItem(menuBean)) {  //if the menu item is not protected
                                if (!allItemsForTheGroup.contains(menuBean)) {
                                    allItemsForTheGroup.add(menuBean);
                                }
                            }
                        }
                    } else {
                        if (l.isLoggable(Level.WARNING)) {
                            l.logp(Level.WARNING, getClass().getName(), "loadAllActionItems", "The pf_web_navigation_util record with short_description '" + rsForAllActionGroupAndItems.getString(8) + "' has an unexpected type value '" + type + "'.");
                        }
                    }
                }
                // If there is a prior allItemsForTheGroup, add it to the cache and map of all groups
                if (allItemsForTheGroup != null) {
                    addActionItemsGroup(allItemsForTheGroup, cacheForallActionItemGroups, groupShortDescription, allActionItemGroups, actionItemToActionItemGroupMapper);
                }
            }
            catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Failed to load all action groups and items.", e);
                l.throwing(getClass().getName(), "loadAllActionItems", ae);
                throw ae;
            } finally {
                if (rsForAllActionGroupAndItems != null) DatabaseUtils.close(rsForAllActionGroupAndItems);
                if (stmt != null) DatabaseUtils.close(stmt);
            }

            //Finally add the action item to action item group mapper into cache
            cacheForallActionItemGroups.put(getCacheIdForActionItemToActionItemGroupMapper(userId, dbPoolId), actionItemToActionItemGroupMapper);
        }
        l.exiting(getClass().getName(), "loadAllActionItems");
    }

    private StringBuffer getApplicationIdList() {
        l.entering(getClass().getName(), "getApplicationIdList");

        // applicationIdsBuff contains the current application ID and the depand application IDs.
        // The format of applications IDs should be ",System1,System2,System3,...,"
        StringBuffer applicationIdsBuff = new StringBuffer(",");
        String currentApplicationId = ApplicationContext.getInstance().getProperty(KEY_FOR_APPLICATION_ID);
        applicationIdsBuff.append(currentApplicationId);
        String dependantApplicationIds = ApplicationContext.getInstance().getProperty(KEY_DEPENDANT_APPLICATION_IDS, "");
        if (StringUtils.isBlank(dependantApplicationIds)) {
            applicationIdsBuff.append(",");
        } else {
            if (!dependantApplicationIds.startsWith(",")) {
                applicationIdsBuff.append(",");
            }
            applicationIdsBuff.append(dependantApplicationIds);
            if (!dependantApplicationIds.endsWith(",")) {
                applicationIdsBuff.append(",");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getApplicationIdList", applicationIdsBuff);
        }
        return applicationIdsBuff;
    }

    private void addActionItemsGroup(List allItemsForTheGroup,
                                     Cache cacheForallActionItemGroups,
                                     String groupShortDescription,
                                     Map allActionItemGroups,
                                     Map actionItemToActionItemGroupMapper) throws CloneNotSupportedException {
        Comparator actionItemsComparator =
                ComparatorUtils.transformedComparator(
                        ComparatorUtils.nullLowComparator(ComparatorUtils.naturalComparator()),
                        new BeanToPropertyValueTransformer("sequence"));
        Collections.sort(allItemsForTheGroup, actionItemsComparator);
        //  Add the prior group to the cacheForallActionItemGroups,
        cacheForallActionItemGroups.put(groupShortDescription, allItemsForTheGroup);
        //  Add a clone of the prior group to the allActionItemGroups,
        allActionItemGroups.put(groupShortDescription,
                getClonedCopyAndAddToActionItemGroupMapper(allItemsForTheGroup,
                        groupShortDescription,
                        actionItemToActionItemGroupMapper));
    }

    private ArrayList getTopNavMenu(HttpServletRequest request)
            throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTopNavMenu", new Object[]{request});
        }

        ArrayList menulist = new ArrayList();
        InputStream fis = null;
        try {
            fis = ActionHelper.getResourceAsInputStream(request.getSession().getServletContext(), this.getTopNavFileName());
            DocumentBuilder docbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Parse the XML
            Document menu = docbuilder.parse(fis);
            l.fine("topnav menu: " + menu);
            // get menu node
            NodeList nl = menu.getElementsByTagName("menu");
            // get selected menuitem
            String startMenu = nl.item(0).getAttributes().getNamedItem("selectedmi").getNodeValue();
            // loop through the menu item elements
            nl = menu.getElementsByTagName("mi");
            int sz = nl.getLength();
            for (int i = 0; i < sz; i++) {
                NamedNodeMap map = nl.item(i).getAttributes();
                String id = map.getNamedItem("id").getNodeValue();
                String url = map.getNamedItem("url").getNodeValue();
                url = Module.getRelativePath(request, url);
                String text = map.getNamedItem("text").getNodeValue();
                String status = map.getNamedItem("status").getNodeValue();
                String openInNewBrowser = map.getNamedItem("openinnewbrowser").getNodeValue();
                if (!url.startsWith("javascript")) {
                    if (openInNewBrowser.equals("Y")) {
                        url = new StringBuffer("javascript:openMain('").append(url).
                                append("','").append(id).append("')").toString();
                    }
                }
                if (status.equals("A")) {
                    MenuBean mb = new MenuBean(!(startMenu.equals(id)), id, url, text, (openInNewBrowser.equals("Y")));
                    if (!menulist.contains(mb)) {
                        menulist.add(mb);
                    }
                }
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getTopNavMenu", menulist);
            }
            return menulist;
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Unexpected Exception while getting top navigation menus.", e);
            e.printStackTrace();
            l.throwing(getClass().getName(), "getAllMenuGroups", ae);
            throw ae;
        } finally {
            if (fis != null) {
                fis.close();
                fis = null;
            }
        }
    }

    private List getJumpNavigationList(Connection conn, String className, PageBean bean, PageDefLoadProcessor pageDefLoadProcessor, String userId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getJumpNavigationList", new Object[]{conn, className, bean, pageDefLoadProcessor, userId});
        }

        List jumpNavigationList = new ArrayList();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        int i = 0;
        try {
            stmt = conn.prepareStatement(jumpNavigationQuery);
            stmt.setLong(1, -1);
            stmt.setLong(2, -1);
            stmt.setString(3, className);
            l.fine(new StringBuffer("Executing: ").append(jumpNavigationQuery).toString());
            rs = stmt.executeQuery();
            while (rs.next()) {
                i++;
                MenuBean menuBean = new MenuBean(true, rs.getString(1), rs.getString(7),  rs.getString(2), true);
                if (pageDefLoadProcessor.postProcessMenuItem(menuBean)) {  //if the jump navigation is not protected
                    if (!jumpNavigationList.contains(menuBean)) {
                        jumpNavigationList.add(menuBean);
                    }
                }
            }

            l.exiting(getClass().getName(), "getAllJumpNavigation");
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Error while getting all navigation links", e);
            l.throwing(getClass().getName(), "getAllJumpNavigation", ae);
            throw ae;
        } finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
        }
        return jumpNavigationList;
    }

    /**
     * This method returns a boolean indicating whether there are menus loaded into the array list allGlobalNavMenus
     * for the input parameter parentNavFk (Parent Navigation Fk).
     *
     * If the property navigationManager.loadNavigationUsingDOM is set to false, then there is a recursive call to this
     * method to load any associated child menus for each processed menu.
     *
     * The default value for property navigationManager.loadNavigationUsingDOM is true, in which case all the menus along
     * with child menus are loaded at once into DOM and processed.
     *
     * @param bean
     * @param parentNavFk
     * @param className
     * @param pageDefLoadProcessor
     * @param userId
     * @param allGlobalNavMenus
     * @param dbPoolId
     */
    private boolean getAllMenus(PageBean bean, long parentNavFk, String className, PageDefLoadProcessor pageDefLoadProcessor, Connection conn, String userId, ArrayList allGlobalNavMenus, String dbPoolId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllMenus", new Object[]{bean, parentNavFk, className, pageDefLoadProcessor, conn, userId, allGlobalNavMenus, dbPoolId});
        }

        boolean hasMenus = false;
        Boolean cacheNavigationMenus = Boolean.valueOf(ApplicationContext.getInstance().getProperty(KEY_FOR_LOAD_NAVIGATION_LIST_AS_DOM, "true"));
        if (cacheNavigationMenus) {
            try {
                Document doc = getNavigationAsDOM(conn, userId, dbPoolId);
                XPathFactory xPathfactory = XPathFactory.newInstance();
                XPath xpath = xPathfactory.newXPath();
                //String xpathExpr = "//ROWSET/ROW[PARENT_PF_WEB_NAVIGATION_FK=" + parentNavFk + " and (TYPE='MENU' or TYPE='TABGROUP' or TYPE='MENUITEM') and (NAV_STRUTS_ACTION = 'NOT_EXISTS' or NAV_STRUTS_ACTION ='" + className + "')]";
                String xpathExpr = "//NAVIGATIONLIST/NAVIGATION[contains(@connect_path_by_pk, " + parentNavFk + ") and PF_WEB_NAVIGATION_PK!=" + parentNavFk + " and ( ((TYPE='MENU' or TYPE='TABGROUP') and ISSUBMENUEXISTS='TRUE') or TYPE='MENUITEM') and (NAV_STRUTS_ACTION = 'NOT_EXISTS' or NAV_STRUTS_ACTION ='" + className + "')]";
                XPathExpression expr = xpath.compile(xpathExpr);
                if (l.isLoggable(Level.FINEST)) {
                    l.logp(Level.FINEST, getClass().getName(), "getAllMenus", "Applying XPath Expr:" + xpathExpr);
                }
                NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                if (l.isLoggable(Level.FINEST)) {
                    l.logp(Level.FINEST, getClass().getName(), "getAllMenus", "Found:" + String.valueOf(nl.getLength()) + " navigation items.");
                }
                MenuBean menuBean = null;
                Map<String, MenuBean> globalNavigationMenus = new LinkedHashMap<String, MenuBean>();
                for (int i=0; i<nl.getLength(); i++) {
                    if (l.isLoggable(Level.FINEST)) {
                        l.logp(Level.FINEST, getClass().getName(), "getAllMenus", "Node Content:" + nl.item(i).getTextContent());
                    }
                    NavigationInfo navigationInfo = new NavigationInfo(nl.item(i));

                    menuBean = getMenuBean(navigationInfo.longDescription, navigationInfo.url, navigationInfo.urlStruts,
                                           navigationInfo.shortDescription, navigationInfo.parentId, navigationInfo.isSubMenuExists,
                                           navigationInfo.toolTip, navigationInfo.isHidden);

                    if (pageDefLoadProcessor.postProcessMenuItem(menuBean)) {  //if the menu item is not protected
                        if (!globalNavigationMenus.containsKey(menuBean.getId())) {
                            globalNavigationMenus.put(menuBean.getId(), menuBean);
                        }
                    }
                }
                allGlobalNavMenus.addAll(globalNavigationMenus.values()) ;
                if (l.isLoggable(Level.FINEST)) {
                    l.logp(Level.FINEST, getClass().getName(), "getAllMenus", "allGlobalNavMenus.size()=" + allGlobalNavMenus.size());
                }

            } catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Error while getting navigation list nodes", e);
                l.throwing(getClass().getName(), "getAllMenus", ae);
                throw ae;
            }
        } else {
            ResultSet rs = null;
            PreparedStatement stmt = null;
            try {

                String menuTypes = "'MENU', 'TABGROUP', 'MENUITEM'";
                stmt = conn.prepareStatement(StringUtils.replace(globalMenuQuery, "&menuTypes&", menuTypes));
                stmt.setLong(1, parentNavFk);
                stmt.setString(2, className);
                stmt.setLong(3, parentNavFk);
                stmt.setString(4, userId);
                l.fine(new StringBuffer("Executing: ").append(globalMenuQuery)
                        .append("\r\n WITH ").append("className:" + className).append(",")
                        .append("parentNavFk:" + parentNavFk).append(",")
                        .append("userId:").append(userId).toString());
                rs = stmt.executeQuery();
                while (rs.next()) {
                    hasMenus = false;
                    String type = rs.getString(3);
                    MenuBean menuBean = getMenuBean(rs);
                    if (pageDefLoadProcessor.postProcessMenuItem(menuBean)) {  //if the menu item is not protected
                        if (l.isLoggable(Level.FINEST)) {
                            l.logp(Level.FINEST, getClass().getName(), "getAllMenus", "Processing menuBean:" + menuBean);
                        }
                        if (!allGlobalNavMenus.contains(menuBean)) {
                            allGlobalNavMenus.add(menuBean);
                            if (!(type.equalsIgnoreCase("MENUITEM") || type.equalsIgnoreCase("ACTIONITEM"))) {
                                //This is a navigation group
                                if (!processedMenuGroups.containsKey(menuBean.getId())) {
                                    processedMenuGroups.put(menuBean.getId(), hasMenus);
                                }
                            }
                        }

                        if (!getAllMenus(bean, rs.getLong(1), className, pageDefLoadProcessor, conn, userId, allGlobalNavMenus, dbPoolId)) {
                            // *** child menu do not exists
                            // Check whether the current item is part of a MENU group.
                            // If the current item is an another group, remove the group and get the hasMenu indicator of the parent.
                            // If the current item is a menu item and has a parent, then set the hasMenu indicator for the parent
                            // - meaning the parent has at least one child.

                            String parentId = "";
                            try {
                                parentId = menuBean.getParentId();
                            } catch (IllegalArgumentException e) {
                                // menu items directly under root menu will not have parentId populated.
                                // it is safe to ignore them, since menuBean will not be added for ROOTMENU item.
                            }

                            if (!(type.equalsIgnoreCase("MENUITEM") || type.equalsIgnoreCase("ACTIONITEM"))) {
                                if (l.isLoggable(Level.FINER)) {
                                    l.logp(Level.FINER, getClass().getName(), "getAllMenus", "There is no child menu for menu id:" + menuBean.toString() + ". Removing meanBean:" + menuBean.toString());
                                }
                                allGlobalNavMenus.remove(menuBean);
                                if (!StringUtils.isBlank(parentId)) {
                                    if (processedMenuGroups.containsKey(parentId)) {
                                        hasMenus = (boolean) processedMenuGroups.get(parentId);
                                    }
                                }
                            } else {
                                hasMenus = true;
                                if (!StringUtils.isBlank(parentId)) {
                                    processedMenuGroups.put(parentId, hasMenus);
                                }
                            }
                        } else {
                            // *** child menu exists.
                            hasMenus = true;
                            if (!(type.equalsIgnoreCase("MENUITEM") || type.equalsIgnoreCase("ACTIONITEM"))) {
                                processedMenuGroups.put(menuBean.getId(), hasMenus);
                            }
                        }
                    }
                }

                l.exiting(getClass().getName(), "getAllMenus");
            } catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Error while getting all menus", e);
                l.throwing(getClass().getName(), "getAllMenus", ae);
                throw ae;
            } finally {
                if (rs != null) DatabaseUtils.close(rs);
                if (stmt != null) DatabaseUtils.close(stmt);
            }
        }
        return hasMenus;
    }

    private boolean getAllNavGroupMenus(PageBean bean, long parentNavFk, String className, PageDefLoadProcessor pageDefLoadProcessor, Connection conn, String userId, ArrayList allItemsForTheGroup, boolean isForActionItemGroup, String dbPoolId, boolean isForTopNav) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllNavGroupMenus", new Object[]{bean, parentNavFk, className, pageDefLoadProcessor, conn, userId, allItemsForTheGroup, isForActionItemGroup, dbPoolId, isForTopNav});
        }

        boolean  hasMenus = false;
        Boolean cacheNavigationMenus = Boolean.valueOf(ApplicationContext.getInstance().getProperty(KEY_FOR_LOAD_NAVIGATION_LIST_AS_DOM, "true"));
        if (cacheNavigationMenus) {
            //l.info("Processing for className:" + className + "/navigation fk:" + parentNavFk);
            try {
                Map<String, MenuBean> globalNavigationMenus = new LinkedHashMap<String, MenuBean>();
                Document doc = getNavigationAsDOM(conn, userId, dbPoolId);
                XPathFactory xPathfactory = XPathFactory.newInstance();
                XPath xpath = xPathfactory.newXPath();
                String xpathExpr = "";
                //xpathExpr = "//ROWSET/ROW[PARENT_PF_WEB_NAVIGATION_FK=" + parentNavFk ;
                xpathExpr = "//NAVIGATIONLIST/NAVIGATION[";
                if (isForTopNav) {
                    xpathExpr += "@connect_path_by_pk=" + parentNavFk ;
                } else {
                    xpathExpr += "contains(@connect_path_by_pk, " + parentNavFk + ")";
                }
                xpathExpr += " and PF_WEB_NAVIGATION_PK!=" + parentNavFk ;
                if (isForActionItemGroup) {
                    xpathExpr += " and ( (TYPE='ACTIONGRP' and ISSUBMENUEXISTS='TRUE') or TYPE='ACTIONITEM')";
                } else {
                    xpathExpr += " and ( (TYPE='TABGROUP' and ISSUBMENUEXISTS='TRUE') or TYPE='MENUITEM')";
                }
                xpathExpr += " and (NAV_STRUTS_ACTION = 'NOT_EXISTS' or NAV_STRUTS_ACTION ='" + className + "')]";
                XPathExpression expr = xpath.compile(xpathExpr);
                if (l.isLoggable(Level.FINEST)) {
                    l.logp(Level.FINEST, getClass().getName(), "getAllNavGroupMenus", "Applying XPath Expr:" + xpathExpr);
                }
                NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                if (l.isLoggable(Level.FINEST)) {
                    l.logp(Level.FINEST, getClass().getName(), "getAllNavGroupMenus", "Found:" + String.valueOf(nl.getLength()) + " navigation items.");
                }
                for (int i=0; i<nl.getLength(); i++) {
                    if (l.isLoggable(Level.FINEST)) {
                        l.logp(Level.FINEST, getClass().getName(), "getAllNavGroupMenus", "Node Content:" + nl.item(i).getTextContent());
                    }
                    NavigationInfo navigationInfo = new NavigationInfo(nl.item(i));

                    MenuBean menuBean = getMenuBean(navigationInfo.longDescription, navigationInfo.url, navigationInfo.urlStruts,
                            navigationInfo.shortDescription, navigationInfo.parentId, navigationInfo.isSubMenuExists,
                            navigationInfo.toolTip, navigationInfo.isHidden);

                    if (pageDefLoadProcessor.postProcessMenuItem(menuBean)) {  //if the menu item is not protected
                        if (!globalNavigationMenus.containsKey(menuBean.getId())) {
                            globalNavigationMenus.put(menuBean.getId(), menuBean);
                        }
                    }
                }
                allItemsForTheGroup.addAll(globalNavigationMenus.values());

                l.exiting(getClass().getName(), "getAllNavGroupMenus");

             } catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Error while getting navigation group menus", e);
                l.throwing(getClass().getName(), "getAllNavGroupMenus", ae);
                throw ae;
            }
        } else {
            ResultSet rs = null;
            PreparedStatement stmt = null;
            try {
                String menuTypes = "";
                if (isForActionItemGroup) {
                    menuTypes = "'ACTIONGRP', 'ACTIONITEM'";
                } else {
                    menuTypes = "'TABGROUP', 'MENUITEM'";
                }

                stmt = conn.prepareStatement(StringUtils.replace(globalMenuQuery, "&menuTypes&", menuTypes));
                stmt.setLong(1, parentNavFk);
                stmt.setString(2, className);
                stmt.setLong(3, parentNavFk);
                stmt.setString(4, userId);
                l.fine(new StringBuffer("Executing: ").append(StringUtils.replace(globalMenuQuery, "&menuTypes&", menuTypes))
                        .append("\r\n WITH ").append("className:" + className).append(",")
                        .append("parentNavFk:" + parentNavFk).append(",")
                        .append("userId:").append(userId).toString());
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String type = rs.getString(3);
                    MenuBean menuBean = getMenuBean(rs);
                    if (l.isLoggable(Level.FINEST)) {
                        l.logp(Level.FINEST, getClass().getName(), "getAllMenus", "Processing menuBean:" + menuBean);
                    }
                    if (pageDefLoadProcessor.postProcessMenuItem(menuBean)) {  //if the menu item is not protected
                        if (!allItemsForTheGroup.contains(menuBean)) {
                            allItemsForTheGroup.add(menuBean);
                            if (!(type.equalsIgnoreCase("MENUITEM") || type.equalsIgnoreCase("ACTIONITEM"))) {
                                //This is a navigation group
                                if (!processedMenuGroups.containsKey(menuBean.getId())) {
                                    processedMenuGroups.put(menuBean.getId(), hasMenus);
                                }
                            }
                        }

                        if (!getAllNavGroupMenus(bean, rs.getLong(1), className, pageDefLoadProcessor, conn, userId, allItemsForTheGroup, isForActionItemGroup, dbPoolId, isForTopNav)){
                            String parentId = "";
                            try {
                                parentId = menuBean.getParentId();
                            } catch (IllegalArgumentException e) {
                                // menu items directly under root menu will not have parentId populated.
                                // it is safe to ignore them, since menuBean will not be added for ROOTMENU item.
                            }

                            if (!(type.equalsIgnoreCase("MENUITEM") || type.equalsIgnoreCase("ACTIONITEM"))) {
                                if (l.isLoggable(Level.FINER)) {
                                    l.logp(Level.FINER, getClass().getName(), "getAllNavGroupMenus", "There is no child menu for menu id:" + menuBean.getId() + ". Removing meanBean:" + menuBean.toString());
                                }
                                allItemsForTheGroup.remove(menuBean);
                                if (!StringUtils.isBlank(parentId)) {
                                    if (processedMenuGroups.containsKey(parentId)) {
                                        hasMenus = (boolean) processedMenuGroups.get(parentId);
                                    }
                                }
                            } else {
                                hasMenus = true;
                                if (!StringUtils.isBlank(parentId)) {
                                    processedMenuGroups.put(parentId, hasMenus);
                                }
                            }
                        } else {
                            // *** child menu exists.
                            hasMenus = true;
                            if (!(type.equalsIgnoreCase("MENUITEM") || type.equalsIgnoreCase("ACTIONITEM"))) {
                                processedMenuGroups.put(menuBean.getId(), hasMenus);
                            }
                        }
                    }
                }

                l.exiting(getClass().getName(), "getAllNavGroupMenus");
            } catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Error while getting navigation group menus", e);
                l.throwing(getClass().getName(), "getAllNavGroupMenus", ae);
                throw ae;
            } finally {
                    if (rs != null) DatabaseUtils.close(rs);
                    if (stmt != null) DatabaseUtils.close(stmt);
            }
        }
        return hasMenus;
    }

    public String loadNavigationListAsXML (Connection conn, String userId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadNavigationListAsXML", new Object[]{conn, userId});
        }

        StringBuffer  navigationListAsXML = new StringBuffer();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            String applicationIdList = getApplicationIdList().toString();
            stmt = conn.prepareStatement(globalMenuQueryAsXML);
            stmt.setString(1, applicationIdList);
            stmt.setString(2, userId);

            l.fine(new StringBuffer("Executing: ").append(globalMenuQueryAsXML)
                    .append("\r\n WITH ").append("applicationId:").append(applicationIdList).append(", userId:").append(userId).toString());
            rs = stmt.executeQuery();
            while (rs.next()) {
                String NavXML = rs.getString("NavXML");

                navigationListAsXML.append("<?xml version=\"1.0\"?>");

                navigationListAsXML.append(NavXML);

                break;
            }
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Error while getting all navigation as XML", e);
            l.throwing(getClass().getName(), "loadNavigationListAsXML", ae);
            throw ae;
        } finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
        }
        l.exiting(getClass().getName(), "loadNavigationListAsXML", navigationListAsXML.toString());
        return navigationListAsXML.toString();
    }

    private Document getNavigationAsDOM(Connection conn, String userId, String dbPoolId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getNavigationAsDOM", new Object[]{conn, userId, dbPoolId});
        }

        Document navigationListAsDOM = null;
        String navigationListAsXML = "";
        try {
            String cacheName = CACHE_ID_FOR_NAVIGATION_LIST_AS_DOM + "For" + dbPoolId + "_" + userId;
            Cache cacheForNavigationListAsDOM = CacheManager.getInstance().getCache(cacheName);
            if (cacheForNavigationListAsDOM.contains(CACHE_ID_FOR_NAVIGATION_LIST_AS_DOM)) {
                l.finer("Retrieving navigation DOM from cache....");
                navigationListAsDOM =  (Document) cacheForNavigationListAsDOM.get(CACHE_ID_FOR_NAVIGATION_LIST_AS_DOM);
            } else {
                l.fine(KEY_FOR_LOAD_NAVIGATION_LIST_AS_DOM + " property is set to true. Constructing DOM for navigation list to avoid round-trips to DB...");
                l.finer("Loading from DB to get navigation list as XML....");
                navigationListAsXML = loadNavigationListAsXML(conn, userId);

                if (l.isLoggable(Level.FINEST)) {
                    l.finest(navigationListAsXML);
                }
                
                l.finer("Creating DOM object based on navigation XML:" + navigationListAsXML);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                navigationListAsDOM = builder.parse(new InputSource(new StringReader(navigationListAsXML)));

                cacheForNavigationListAsDOM.put(CACHE_ID_FOR_NAVIGATION_LIST_AS_DOM, navigationListAsDOM);
            }
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Error while getting all navigation as XML", e);
            l.throwing(getClass().getName(), "loadNavigationListAsXML", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "getNavigationListAsXML", navigationListAsDOM);
        return navigationListAsDOM;
    }

    private void removeNavigationDOMFromCache(String userId, String dbPoolId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "removeNavigationDOMFromCache", new Object[]{userId, dbPoolId});
        }

        String cacheName = CACHE_ID_FOR_NAVIGATION_LIST_AS_DOM + "For" + dbPoolId + "_" + userId;
        Cache cacheForNavigationListAsDOM = CacheManager.getInstance().getCache(cacheName);
        if (cacheForNavigationListAsDOM.contains(CACHE_ID_FOR_NAVIGATION_LIST_AS_DOM)) {
            l.finer("Removing navigation DOM from cache....");
            cacheForNavigationListAsDOM.remove(CACHE_ID_FOR_NAVIGATION_LIST_AS_DOM);
        }
        l.exiting(getClass().getName(), "removeNavigationDOMFromCache");
        return;
    }

    private void processMenu(PageBean bean, ResultSet rs, String className, PageDefLoadProcessor pageDefLoadProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processMenu", new Object[]{bean, rs, className, pageDefLoadProcessor});
        }

        ArrayList menu = new ArrayList();
        ArrayList actions = new ArrayList();
        //boolean selected = false;
        boolean isUrl;
        try {
            while (rs.next()) {
                String urlOnly = rs.getString(4);
                String urlStruts = rs.getString(5);
                /* Use the URL value first. If none is found, use web page url */
                String url = (urlOnly == null) ? ((urlStruts == null) ? "" : urlStruts) : urlOnly;
                if (!rs.getString(3).equals(IOasisAction.TYPE_MENU)) {
                    MenuBean menuBean = new MenuBean(true, rs.getString(8), url, rs.getString(2), false);
                    if (pageDefLoadProcessor.postProcessActionItem(menuBean)) {  //if the action item is not protected
                        if (!actions.contains(menuBean)) {
                            actions.add(menuBean);
                        }
                    }
                } else {
                    isUrl = (!className.equals(rs.getString(6)));
                    //if(!selected)
                    //    selected = !isUrl;
                    url = (isUrl) ? url : "#";
                    MenuBean menuBean = new MenuBean(isUrl, rs.getString(8), url, rs.getString(2), false);
                    if (pageDefLoadProcessor.postProcessMenuItem(menuBean))  { //if the menu item is not protected
                        if (!menu.contains(menuBean)) {
                            menu.add(menuBean);
                        }
                    }
                }
            }

            pageDefLoadProcessor.postProcessMenuItems(menu);
            pageDefLoadProcessor.postProcessActionItems(actions);

            /*if(!selected && menu.size()>0) {
                MenuBean defaultMenu = (MenuBean) menu.get(0);
                defaultMenu.setIsLink(false);
                defaultMenu.setUrl("#");
            } */

            bean.setLeftNavMenu(menu);
            bean.setLeftNavActions(actions);
            l.exiting(getClass().getName(), "processMenu");
        } catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "SQL Exception Raised", se);
            l.throwing(getClass().getName(), "processMenu", ae);
            throw ae;
        } finally {
                if (rs != null) DatabaseUtils.close(rs);             
        }
    }

    private MenuBean getMenuBean(ResultSet rs) throws SQLException {
        String urlOnly = rs.getString(4);
        String urlStruts = rs.getString(5);
        /* Use the URL value first. If none is found, use web page url */
        String url = (urlOnly == null) ? ((urlStruts == null) ? "" : urlStruts) : urlOnly;
        boolean isUrl = !StringUtils.isBlank(url);
/*
            boolean isUrl = (!className.equals(rs.getString(6)));
            //if(!selected)
            //    selected = !isUrl;
            url = (isUrl) ? url : "#";
*/
        MenuBean menuBean = new MenuBean(isUrl, rs.getString(8), url, rs.getString(2), false, rs.getString(9),
                Boolean.valueOf(rs.getString(10)).booleanValue(), rs.getString(11), Boolean.valueOf(rs.getString(12)).booleanValue());
        return menuBean;
    }

    private MenuBean getMenuBean(String longDescription, String urlOnly, String urlStruts, String shortDescription, String parentId, String isSubMenuExists, String toolTip, String isHidden) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getMenuBean", new Object[]{longDescription, urlOnly, urlStruts, shortDescription, parentId, isSubMenuExists, toolTip, isHidden});
        }

        /* Use the URL value first. If none is found, use web page url */
        String url = (StringUtils.isBlank(urlOnly) ? ( StringUtils.isBlank(urlStruts) ? "" : urlStruts) : urlOnly);
        boolean isUrl = !StringUtils.isBlank(url);
        MenuBean menuBean = new MenuBean(isUrl, shortDescription, url, longDescription, false, parentId,
                Boolean.valueOf(isSubMenuExists).booleanValue(), toolTip, Boolean.valueOf(isHidden).booleanValue());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getMenuBean", menuBean);
        }
        return menuBean;
    }

    private List getClonedCopy(List navigationGroup) throws CloneNotSupportedException {
        ArrayList clonedGroup = new ArrayList();
        Iterator it = navigationGroup.iterator();
        while (it.hasNext()) {
            MenuBean menuBean = ((MenuBean) ((MenuBean) it.next()).clone());
            if (!clonedGroup.contains(menuBean)) {
                clonedGroup.add(menuBean);
            }
        }
        return clonedGroup;
    }

    private ArrayList getClonedCopyAndAddToActionItemGroupMapper(List navigationGroup, String actionItemGroupId, Map actionItemToActionItemGroupMapper) throws CloneNotSupportedException {
        ArrayList clonedGroup = new ArrayList();
        Iterator it = navigationGroup.iterator();
        while (it.hasNext()) {
            MenuBean menuBean = ((MenuBean) ((MenuBean) it.next()).clone());
            if (!clonedGroup.contains(menuBean)) {
                clonedGroup.add(menuBean);
            }
            String actionItemGroupIds = "";
            if (actionItemToActionItemGroupMapper.containsKey(menuBean.getId())) {
                actionItemGroupIds = (String) actionItemToActionItemGroupMapper.get(menuBean.getId());
            }
            // If the group is not yet added, then add the group into comma delimited action item group list
            if (actionItemGroupIds.indexOf(actionItemGroupId + ",") == -1) {
                actionItemGroupIds += (StringUtils.isBlank(actionItemGroupIds) ? "" : ",") + actionItemGroupId;
            }
            actionItemToActionItemGroupMapper.put(menuBean.getId(), actionItemGroupIds);
        }
        return clonedGroup;
    }

    private Map getActionItemToActionItemGroupMapper(Cache cacheForallActionItemGroups, String userId, String dbPoolId) {
        Map actionItemToGroupMapper = null;
        // Get the Mapper for actionItem to actionItemGroup
        if (cacheForallActionItemGroups.contains(getCacheIdForActionItemToActionItemGroupMapper(userId, dbPoolId))) {
            actionItemToGroupMapper = (Map) cacheForallActionItemGroups.get(getCacheIdForActionItemToActionItemGroupMapper(userId, dbPoolId));
        } else {
            actionItemToGroupMapper = new HashMap();
        }
        return actionItemToGroupMapper;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    /**
     * This method is triggered either by Spring or factory class whenever a new instance of Navigation Manager is requested.
     * It loads all navigation related items like global menus, tab menus and action items from the Web WB configuration
     * into appropriate collection.
     * <p/>
     * This method is automatically triggered by either by ApplicationLifecycleAdvisor (if it is Spring configured) or
     * by getInstance() of NavigationManager.
     */
    public void initialize() {
        // Initialization is done by getPageBean()
    }

    /**
     * Cleans up the instance of PageEntitlementManager.
     */
    public void terminate() {

    }

    public void verifyConfig() {
        // Leave this even if this is empty, so that the page entitlement manager can work with
        // PageEntitlementManager.getInstance()
    }

    public NavigationManagerImpl() {
    }

    public boolean hasPageEntitlementFileName() {
        return (!StringUtils.isBlank(m_topNavFileName));
    }

    public String getTopNavFileName() {
        if (m_topNavFileName == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_TOPNAV_FILENAME)) {
                m_topNavFileName = ApplicationContext.getInstance().getProperty(PROPERTY_TOPNAV_FILENAME);
            }
        }
        return m_topNavFileName;
    }

    public void setTopNavFileName(String topNavFileName) {
        m_topNavFileName = topNavFileName;
    }


    /**
     * Clear all Navigation Manager cached data.
     * @param dbPoolId
     */
    public void clearNavigationCache(String dbPoolId) {
        CacheManager cacheManager = CacheManager.getInstance();
        ArrayList userList = getUserListFromCache(dbPoolId);
        Iterator it = userList.iterator();
        while (it.hasNext()) {
            String userId = (String) it.next();
            getUserPageBeanCache(userId, dbPoolId).clear();
            getUserMenuGroupCache(userId, dbPoolId).clear();
            getUserActionItemGroupCache(userId, dbPoolId).clear();
        }
    }

    private Cache getUserPageBeanCache(String userId, String dbPoolId) {
        String cacheName = CACHE_ID_FOR_PAGE_BEANS + "For" + dbPoolId + "_" + userId;
        Cache pageBeans = CacheManager.getInstance().getCache(cacheName);
        return pageBeans;
    }

    private Cache getUserMenuGroupCache(String userId, String dbPoolId) {
        String cacheName = CACHE_ID_FOR_MENU_GROUPS + "For" + dbPoolId + "_" + userId;
        Cache cacheForallMenuGroups = CacheManager.getInstance().getCache(cacheName);
        return cacheForallMenuGroups;
    }

    private Cache getUserActionItemGroupCache(String userId, String dbPoolId) {
        String cacheName = CACHE_ID_FOR_ACTION_ITEM_GROUPS + "For" + dbPoolId + "_" + userId;
        Cache cacheForallActionItemGroups = CacheManager.getInstance().getCache(cacheName);
        return cacheForallActionItemGroups;
    }

    private Cache getDbPoolIdListCache() {
        Cache cacheForDbPoolIdList = CacheManager.getInstance().getCache(CACHE_ID_FOR_DBPOOLID_LIST);
        return cacheForDbPoolIdList;
    }

    private boolean hasDbPoolIdInCache(String dbPoolId) {
        Cache cacheForDbPoolIdList = getDbPoolIdListCache();
        return cacheForDbPoolIdList.contains(dbPoolId);
    }

    private ArrayList getUserListFromCache(String dbPoolId) {
        if (!hasDbPoolIdInCache(dbPoolId)) {
            getDbPoolIdListCache().put(dbPoolId, new ArrayList());
        }
        Cache cacheForDbPoolIdList = getDbPoolIdListCache();
        return (ArrayList) cacheForDbPoolIdList.get(dbPoolId);
    }

    private String getCacheIdForApplicationTopNav(String userId, String dbPoolId) {
        return APPLICATION_TOP_NAV_GROUP + "For" + dbPoolId + "_" + userId;
    }

    private String getCacheIdForUserGlobalNavMenuGroup(String userId, String dbPoolId) {
        return APPLICATION_GLOBAL_MENU_GROUP + "For" + dbPoolId + "_" + userId;
    }

    private String getCacheIdForActionItemToActionItemGroupMapper(String userId, String dbPoolId) {
        return CACHE_ID_FOR_ACTION_ITEM_GROUP_MAPPER + "For" + dbPoolId + "_" + userId;
    }

    private String getCacheIdForMenuGroupId(String menuGroupId, String userId, String dbPoolId) {
        return menuGroupId + "For" + dbPoolId + "_" + userId;
    }

    private String getCacheIdForPageJumpNavigation(String pageTitle) {
        return APPLICATION_JUMP_NAV_GROUP + "_FOR_PAGE_" + pageTitle;
    }

    /**
     * Implements the refresh parameters listener.
     * @param request
     */
    public void refreshParms(HttpServletRequest request) {
        Logger l = LogUtils.enterLog(getClass(), "refreshParms");
        MessageManager messageManager = MessageManager.getInstance();
        try {
            clearNavigationCache(ActionHelper.getDbPoolId(request));
            messageManager.addInfoMessage("core.refresh.navigation.success");
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "refreshParms", "Navigation has been refreshed!");
            }
        } catch (Exception e) {
            if (l.isLoggable(Level.SEVERE)) {
                l.logp(Level.SEVERE, getClass().getName(), "refreshParms", "Failed to refresh the Navigation", e);
            }
            messageManager.addErrorMessage("core.refresh.navigation.fail");
        }
        l.exiting(getClass().getName(), "refreshParms");
    }

    private static final String menuQuery = "(SELECT u.pf_web_navigation_pk, " +
            "       u.long_description, " +
            "       u.TYPE, " +
            "       u.url, " +
            "       dp.url, " +
            "       dp.struts_action, " +
            "       u.sequence, " +
            "       u.short_description " +
            "  FROM pf_web_navigation_util u, " +
            "       pf_web_page            sp, " +
            "       pf_web_page            dp, " +
            "       pf_web_application     wa " +
            " WHERE sp.struts_action = ? " +
            "   AND wa.pf_web_application_pk = u.pf_web_application_fk " +
            "   AND u.TYPE != 'MENU' " +
            "   AND u.status = 'A' " +
            "   AND sp.pf_web_navigation_fk = u.parent_pf_web_navigation_fk " +
            "   AND u.pf_web_page_fk = dp.pf_web_page_pk(+) " +
            "   AND ((nvl(wa.security_b, 'Y') = 'N' OR nvl(u.security_b, 'N') = 'N') OR " +
            "        (nvl(wa.security_b, 'Y') = 'Y' AND nvl(u.security_b, 'N') = 'Y' AND " +
            "        (EXISTS (SELECT 1 " +
            "                          FROM pfuser_prof up, pfprof_web_navigation_xref x " +
            "                         WHERE x.application = up.application " +
            "                           AND x.profile = up.profile " +
            "                           AND upper(up.userid) = upper(?) " +
            "                           AND x.pf_web_navigation_fk = u.pf_web_navigation_pk " +
            "                           AND x.status = 'A' " +
            "                           AND up.status = 'A')))) " +
            "UNION " +
            "SELECT u1.pf_web_navigation_pk, " +
            "       u1.long_description, " +
            "       u1.TYPE, " +
            "       u1.url, " +
            "       dp.url, " +
            "       dp.struts_action, " +
            "       u1.sequence, " +
            "       u1.short_description " +
            "  FROM pf_web_navigation_util u1, " +
            "       pf_web_navigation_util u2, " +
            "       pf_web_page            sp, " +
            "       pf_web_page            dp, " +
            "       pf_web_application     wa " +
            " WHERE sp.struts_action = ? " +
            "   AND u1.status = 'A' " +
            "   AND wa.pf_web_application_pk = u1.pf_web_application_fk " +
            "   AND sp.pf_web_navigation_fk = u2.pf_web_navigation_pk " +
            "   AND u2.parent_pf_web_navigation_fk = u1.parent_pf_web_navigation_fk " +
            "   AND u1.TYPE != 'MENU' " +
            "   AND u2.status = 'A' " +
            "   AND u1.pf_web_page_fk = dp.pf_web_page_pk(+) " +
            "   AND ((nvl(wa.security_b, 'Y') = 'N' OR nvl(u1.security_b, 'N') = 'N') OR " +
            "        (nvl(wa.security_b, 'Y') = 'Y' AND nvl(u1.security_b, 'N') = 'Y' AND " +
            "        (EXISTS (SELECT 1 " +
            "                          FROM pfuser_prof up, pfprof_web_navigation_xref x " +
            "                         WHERE x.application = up.application " +
            "                           AND x.profile = up.profile " +
            "                           AND upper(up.userid) = upper(?) " +
            "                           AND x.pf_web_navigation_fk = u1.pf_web_navigation_pk " +
            "                           AND x.status = 'A' " +
            "                           AND up.status = 'A'))))) " +
            " ORDER BY TYPE, sequence";

    private static final String pageBeanQuery = "" +
            "select nvl(wp.width, -9999), nvl(wp.height, -9999), nvl(wp.top, -9999), nvl(wp.left, -9999), " +
            "       wp.long_description, " +
            "       CASE WHEN length(nvl(wp.help_id,''))!=0 THEN" +
            "            nvl(wa.base_help_uri, '') || CASE WHEN length(nvl(wa.base_help_uri, '')) !=0 " +
            "                                    THEN " +
            "                                         CASE WHEN substr(wa.base_help_uri,-1)!='/' " +
            "                                         THEN " +
            "                                              '/' " +
            "                                         ELSE '' " +
            "                                         END " +
            "                                    ELSE '' " +
            "                                    END || nvl(wp.help_id,'') " +
            "       ELSE '' END help_url, " +
            "       code, " +
            "       access_trail_b, " +
            "       use_jqx_grid_b " +
            "  from pf_web_application wa, " +
            "       pf_web_page wp" +
            " where wa.pf_web_application_pk (+) = wp.pf_web_application_fk " +
            "   and wp.struts_action = ?";

    private static final String pageUseJqxGridQuery = "" +
            "select use_jqx_grid_b " +
            "  from pf_web_page wp " +
            " where wp.struts_action = ?";

  private static final String allowedNavigationsForPage ="SELECT u.short_description " +
          "  FROM pf_web_page p, pf_web_page_nav_xref x, pf_web_navigation_util u " +
          " WHERE p.pf_web_page_pk = x.pf_web_page_base_fk " +
          "   AND x.pf_web_navigation_util_base_fk = u.pf_web_navigation_pk " +
          "   AND p.struts_action = ?";

  private static final String commonNavigationQuery = "" +
          "WITH v_pf_web_id AS (\n" +
          "          SELECT base_unique_id, cust_unique_id\n" +
          "          FROM   pf_web_id wid\n" +
          "          WHERE  wid.effective_to_date = to_date('1/1/3000','mm/dd/yyyy')\n" +
          "          AND    wid.source_table_name = 'PF_WEB_NAVIGATION_UTIL_BASE' ),\n" +
          "     v_pf_web_nav AS (\n" +
          "          SELECT b.pf_web_navigation_util_base_pk       AS pf_web_navigation_pk,\n" +
          "                 b.short_description                    AS short_description,\n" +
          "                 b.pf_web_page_base_fk                  AS pf_web_page_fk,\n" +
          "                 decode(c.cust_unique_id,\n" +
          "                        NULL, nvl(c.url, b.url),\n" +
          "                        decode(cs_web_maint.is_null_config_value('PF_WEB_NAVIGATION_UTIL_CUST',\n" +
          "                                                                 c.pf_web_navigation_util_cust_pk,\n" +
          "                                                                 'URL'),\n" +
          "                               'Y', NULL,\n" +
          "                               NVL(c.url, b.url)))      AS URL,\n" +
          "                 NVL(b1.pf_web_navigation_util_base_pk,\n" +
          "                     b.parent_pf_web_navigation_fk)     AS parent_pf_web_navigation_fk,\n" +
          "                 NVL(c.type, b.type)                    AS TYPE,\n" +
          "                 b.pf_web_application_base_fk           AS  pf_web_application_fk\n" +
          "          FROM   pf_web_navigation_util_base b1,\n" +
          "                 v_pf_web_id wid1,\n" +
          "                 pf_web_navigation_util_cust c,\n" +
          "                 v_pf_web_id wid,\n" +
          "                 pf_web_navigation_util_base b\n" +
          "          WHERE  b1.short_description(+) = wid1.base_unique_id\n" +
          "          AND    wid1.cust_unique_id(+) = c.parent_cust_unique_id\n" +
          "          AND    c.cust_unique_id (+) = wid.cust_unique_id\n" +
          "          AND    wid.base_unique_id = b.short_description )";

  private static final String rootMenuQueryForGlobalNav = commonNavigationQuery + "\n" +
          "SELECT pf_web_navigation_pk, u1.short_description  " +
          "  FROM v_pf_web_nav u1, " +
          "       pf_web_application a " +
          " WHERE u1.pf_web_page_fk+0 IS NULL " +
          "   AND u1.url IS NULL " +
          "   AND u1.parent_pf_web_navigation_fk IS NULL " +
          "   AND u1.type = 'ROOTMENU' " +
          "   AND u1.pf_web_application_fk = a.pf_web_application_pk " +
          "   AND upper(a.short_description) = upper(?) ";

  private static final String topnavMenuQuery = commonNavigationQuery + "\n" +
          "SELECT pf_web_navigation_pk " +
          "  FROM v_pf_web_nav u1" +
          " WHERE u1.pf_web_page_fk+0 IS NULL " +
          "   AND u1.url IS NULL " +
          "   AND u1.parent_pf_web_navigation_fk IS NULL " +
          "   AND u1.type IN ('MENU','ROOTMENU') " +
          "   AND upper(u1.short_description) = upper(?) ";

  private static final String tabGroupQuery = commonNavigationQuery + "\n" +
          "SELECT pf_web_navigation_pk, short_description " +
          "  FROM v_pf_web_nav u1 " +
          " WHERE u1.pf_web_page_fk+0 IS NULL " +
          "   AND u1.url IS NULL " +
          "   AND u1.parent_pf_web_navigation_fk IS NULL " +
          "   AND u1.type = 'TABGROUP' ";

  private static final String actionGroupQuery = commonNavigationQuery + "\n" +
          "SELECT pf_web_navigation_pk, short_description " +
          "  FROM v_pf_web_nav u1 " +
          " WHERE u1.pf_web_page_fk+0 IS NULL " +
          "   AND u1.url IS NULL " +
          "   AND u1.parent_pf_web_navigation_fk IS NULL " +
          "   AND u1.type = 'ACTIONGRP' ";

    private static final String commonPfWebNavigationUtilQuery = "" +
            "WITH NavigationList AS ( " +
            "SELECT u.pf_web_navigation_pk, " +
            "       u.long_description, " +
            "       u.type, " +
            "       u.url action_url, " +
            "       dp.url nav_url, " +
            "       dp.struts_action, " +
            "       u.sequence, " +
            "       u.short_description," +
            "       u.parent_pf_web_navigation_fk,  " +
            "       (SELECT short_description " +
            "          FROM pf_web_navigation_util_base b" +
            "         WHERE (b.type IN ('MENU', 'ACTIONGRP') OR b.parent_pf_web_navigation_fk IS NOT NULL)" +
            "           AND b.pf_web_navigation_util_base_pk = u.parent_pf_web_navigation_fk) parentId,  " +
            "       u.tooltip, " +
            "       decode(u.hidden_b, 'Y', 'TRUE', 'FALSE') isHidden," +
            "       (SELECT NVL(MAX(struts_action), 'NOT_EXISTS')" +
            "          FROM pf_web_page_base sp " +
            "         WHERE sp.pf_web_navigation_util_base_fk = u.pf_web_navigation_pk) nav_struts_action, " +
            "       u.url, " +
            "       u.pf_web_page_fk, " +
            "       u.pf_web_application_fk " +
            "  FROM pf_web_page dp, " +
            "       pf_web_navigation_util u, " +
            "       pf_web_application wa " +
            " WHERE u.status='A' " +
            "   AND UPPER(?) LIKE '%,' || UPPER(wa.short_description) || ',%' " +
            "   AND wa.pf_web_application_pk = u.pf_web_application_fk " +
            "   AND u.pf_web_page_fk = dp.pf_web_page_pk (+) " +
            "   AND (nvl(wa.security_b,'Y') = 'N' OR nvl(u.security_b,'N') = 'N' " +
            "    OR (nvl(wa.security_b,'Y') = 'Y' AND nvl(u.security_b,'N') = 'Y' " +
            "   AND (" +
            "         exists (select 1 from pfuser_prof up, pfprof_web_navigation_xref x " +
            "                where x.application = up.application " +
            "                  and x.profile = up.profile and upper(up.userid) = upper(?) " +
            "                  and x.pf_web_navigation_fk = u.pf_web_navigation_pk " +
            "                  and x.status = 'A' and up.status = 'A') " +
            "        ) " +
            "        )) " +
            " ORDER BY u.sequence)," +
            " PfWebNavigationView (lvl, pf_web_navigation_pk, long_description, type, action_url, nav_url, struts_action, sequence, " +
            "                      short_description, parent_pf_web_navigation_fk, parentId, isSubMenuExists, tooltip, isHidden, " +
            "                      nav_struts_action, url, pf_web_page_fk, pf_web_application_fk, connect_path_by_pk)" +
            "  AS " +
            "   (SELECT 1 as lvl, " +
            "           n.pf_web_navigation_pk, n.long_description, n.type, n.action_url, n.nav_url, n.struts_action, " +
            "           n.sequence, n.short_description, n.parent_pf_web_navigation_fk, n.parentId, " +
            "           (SELECT DECODE(COUNT(*), 0, 'FALSE', 'TRUE') FROM NavigationList WHERE parent_pf_web_navigation_fk = n.pf_web_navigation_pk) isSubMenuExists, " +
            "           n.tooltip, n.isHidden, n.nav_struts_action, n.url, n.pf_web_page_fk, n.pf_web_application_fk, '' as connect_path_by_pk" +
            "      FROM NavigationList n" +
            "     WHERE parent_pf_web_navigation_fk is null" +
            "    UNION ALL " +
            "    SELECT pn.lvl+1, " +
            "           n.pf_web_navigation_pk, n.long_description, n.type, n.action_url, n.nav_url, n.struts_action, " +
            "           n.sequence, n.short_description, n.parent_pf_web_navigation_fk, n.parentId, " +
            "           (SELECT DECODE(COUNT(*), 0, 'FALSE', 'TRUE') FROM NavigationList WHERE parent_pf_web_navigation_fk = n.pf_web_navigation_pk) isSubMenuExists, " +
            "           n.tooltip, n.isHidden, n.nav_struts_action, n.url, n.pf_web_page_fk, n.pf_web_application_fk, " +
            "           CAST(connect_path_by_pk AS VARCHAR2(4000)) || ',' || to_char(n.parent_pf_web_navigation_fk) as connect_path_by_pk" +
            "      FROM NavigationList n, PfWebNavigationView pn" +
            "     WHERE pn.pf_web_navigation_pk = n.parent_pf_web_navigation_fk)" +
            "  SEARCH DEPTH FIRST BY sequence, pf_web_navigation_pk " +
            "  SET order_id " ;

    private static final String allActionGroupsAndItemsQuery = commonPfWebNavigationUtilQuery +
        " SELECT U.PF_WEB_NAVIGATION_PK, " +
        "        U.LONG_DESCRIPTION, " +
        "        U.TYPE, " +
        "        U.URL ACTION_URL, " +
        "        DP.URL NAV_URL, " +
        "        DP.STRUTS_ACTION, " +
        "        U.SEQUENCE, " +
        "        U.SHORT_DESCRIPTION, " +
        "        U.PARENTID, " +
        "        CASE " +
        "          WHEN TYPE = 'MENU' OR TYPE = 'ROOTMENU' OR TYPE = 'ACTIONGRP' OR TYPE = 'TABGROUP' THEN" +
        "               decode((SELECT count(*) " +
        "                         FROM PfWebNavigationView " +
        "                        WHERE instr((connect_path_by_pk || ','), (',' || U.pf_web_navigation_pk || ',') ) > 0 " +
        "                          AND type IN ('ACTIONITEM','MENUITEM')), 0, 'FALSE', 'TRUE')" +
        "          ELSE" +
        "               U.isSubMenuExists" +
        "        END  isSubMenuExists, " +
        "        U.TOOLTIP, " +
        "        U.ISHIDDEN, " +
        "        U.PARENT_PF_WEB_NAVIGATION_FK " +
        "   FROM PF_WEB_PAGE_BASE DP, " +
        "        PfWebNavigationView U, " +
        "        PF_WEB_APPLICATION WA " +
        "  WHERE ((U.TYPE IN ('MENU', 'ACTIONGRP') " +
        "    AND   U.ISSUBMENUEXISTS = 'TRUE') " +
        "     OR   U.TYPE IN ('ACTIONITEM')) " +
        "    AND WA.PF_WEB_APPLICATION_PK = U.PF_WEB_APPLICATION_FK " +
        "    AND DP.PF_WEB_PAGE_BASE_PK(+) = U.PF_WEB_PAGE_FK  " +
        "  ORDER BY ORDER_ID " ;

    private static final String globalMenuQuery = "" +
            "SELECT u.pf_web_navigation_pk, " +
            "       u.long_description, u.type, u.url action_url, " +
            "       dp.url nav_url, dp.struts_action, u.sequence, u.short_description,  " +
            "       (SELECT short_description " +
            "          FROM pf_web_navigation_util_base " +
            "         WHERE parent_pf_web_navigation_fk is not null " +
            "           AND pf_web_navigation_util_base_pk = ?) parentId,  " +
            "       NULL AS isSubMenuExists, " +
            "       u.tooltip, " +
            "       decode(u.hidden_b, 'Y', 'TRUE', 'FALSE') isHidden " +
            "  FROM pf_web_page dp, " +
            "       pf_web_navigation_util u, " +
            "       pf_web_application wa " +
            " WHERE (NOT EXISTS (SELECT 1 FROM pf_web_page_base sp " +
            "                     WHERE sp.pf_web_navigation_util_base_fk = u.pf_web_navigation_pk) " +
            "    OR EXISTS (SELECT 1 FROM pf_web_page_base sp " +
            "                WHERE sp.pf_web_navigation_util_base_fk = u.pf_web_navigation_pk " +
            "                  AND sp.struts_action = ?)) " +
            "   AND u.type IN (&menuTypes&) " +
            "   AND u.parent_pf_web_navigation_fk = ? " +
            "   AND u.status='A' " +
            "   AND wa.pf_web_application_pk = u.pf_web_application_fk " +
            "   AND u.pf_web_page_fk = dp.pf_web_page_pk (+) " +
            "   AND (nvl(wa.security_b,'Y') = 'N' OR nvl(u.security_b,'N') = 'N' " +
            "    OR (nvl(wa.security_b,'Y') = 'Y' AND nvl(u.security_b,'N') = 'Y' " +
            "   AND (" +
            "         exists (select 1 from pfuser_prof up, pfprof_web_navigation_xref x " +
            "                where x.application = up.application " +
            "                  and x.profile = up.profile and upper(up.userid) = upper(?) " +
            "                  and x.pf_web_navigation_fk = u.pf_web_navigation_pk " +
            "                  and x.status = 'A' and up.status = 'A') " +
            "        ) " +
            "        )) " +
            " ORDER BY u.sequence";

    private static final String globalMenuQueryAsXML = commonPfWebNavigationUtilQuery +
            "  SELECT xmlelement(\"NAVIGATIONLIST\", xmlagg(xmlelement(\"NAVIGATION\", xmlattributes (substr(n.connect_path_by_pk,2) as \"connect_path_by_pk\"), " +
            "                                        xmlforest(pf_web_navigation_pk, long_description, type, action_url," +
            "                                                  nav_url, struts_action, sequence, short_description, " +
            "                                                  parent_pf_web_navigation_fk, parentId, " +
            "                                                  CASE " +
            "                                                    WHEN TYPE = 'MENU' OR TYPE = 'ROOTMENU' OR TYPE = 'ACTIONGRP' OR TYPE = 'TABGROUP' THEN" +
            "                                                      decode((SELECT count(*) " +
            "                                                                FROM PfWebNavigationView " +
            "                                                               WHERE instr((connect_path_by_pk || ','), (',' || n.pf_web_navigation_pk || ',') ) > 0 " +
            "                                                                 AND type IN ('ACTIONITEM','MENUITEM')), 0, 'FALSE', 'TRUE')" +
            "                                                    ELSE" +
            "                                                      n.isSubMenuExists" +
            "                                                  END  isSubMenuExists, " +
            "                                                  tooltip, isHidden, nav_struts_action)))) as NavXML " +
            "    FROM PfWebNavigationView n" ;

    private static final String jumpNavigationQuery = "" +
           "SELECT u.pf_web_jump_navigation_util_pk,   " +
           "       u.description, " +
           "       u.source_context_code, " +
           "       u.source_context_field_id," +
           "       CASE WHEN u.destination_context_code = 'CLAIM' AND u.source_context_code = 'FM' THEN " +
           "            'FMCLAIM' ELSE" +
           "            u.destination_context_code" +
           "       END as destination_context_code," +
           "       u.destination_context_field_id," +
           "       dp.url nav_url," +
           "       u.url_parameter," +
           "       sl.long_description AS source_field_id," +
           "       dl.long_description AS destination_field_id," +
           "       da.short_description AS destination_app_Id" +
           "  FROM pf_web_application da, " +
           "       pf_web_page dp, " +
           "       pf_web_page sp, " +
           "       pf_web_jump_navigation_util u, " +
           "       (SELECT * FROM lookup_Code WHERE lookup_type_code LIKE 'WEBJMP_%' AND lookup_type_code LIKE '%_FLD') sl," +
           "       (SELECT * FROM lookup_Code WHERE lookup_type_code LIKE 'WEBJMP_%' AND lookup_type_code LIKE '%_FLD') dl" +
           " WHERE ((sl.lookup_type_code = 'WEBJMP_FM_CLAIM_FLD' AND u.source_context_code = 'FM' AND u.destination_context_code = 'CLAIM') " +
           "    OR sl.lookup_type_code = 'WEBJMP_' || u.source_context_code || '_FLD')" +
           "   AND sl.code = u.source_context_field_id" +
           "   AND ((dl.lookup_type_code = 'WEBJMP_FM_CLAIM_FLD' AND u.source_context_code = 'FM' AND u.destination_context_code = 'CLAIM') " +
           "    OR dl.lookup_type_code = 'WEBJMP_' || u.destination_context_code || '_FLD')" +
           "   AND dl.code = u.destination_context_field_id " +
           "   AND da.pf_web_Application_pk = dp.pf_web_application_fk" +
           "   AND dp.code = u.destination_page_code" +
           "   AND sp.code = u.source_page_code" +
           "   AND u.status = 'A'" +
           "   AND u.pf_web_jump_navigation_util_pk = DECODE(NVL(?, -1), -1, pf_web_jump_navigation_util_pk, ?) "+
           "   AND sp.struts_action = NVL(?, sp.struts_action) " +
           " ORDER BY u.description ";

    private static final String addlGlobalNavMenuQuery = "" +
          "SELECT u.pf_web_navigation_pk, " +
          "       u.long_description, u.type, u.url action_url, " +
          "       dp.url nav_url, dp.struts_action, u.sequence, u.short_description,  " +
          "       (SELECT short_description FROM pf_web_navigation_util WHERE parent_pf_web_navigation_fk is not null and pf_web_navigation_pk = ?) parentId,  " +
          "       (SELECT decode(count(*), 0, 'FALSE', 'TRUE') " +
          "         FROM pf_web_navigation_util " +
          "        WHERE parent_pf_web_navigation_fk = u.pf_web_navigation_pk " +
          "          AND status = 'A') isSubMenuExists, " +
          "       u.tooltip, " +
          "       decode(u.hidden_b, 'Y', 'TRUE', 'FALSE') isHidden " +
          "  FROM pf_web_page dp, " +
          "       pf_web_navigation_util u ,pf_web_application wa " +
          " WHERE u.type = 'MENUITEM' " +
          "   AND instr( (',' || ? || ','), (',' || u.short_description || ',')) > 0 " +
          "   AND u.status='A' " +
          "   AND wa.pf_web_application_pk = u.pf_web_application_fk " +
          "   AND u.pf_web_page_fk = dp.pf_web_page_pk (+) " +
          "   AND (nvl(wa.security_b,'Y') = 'N' OR nvl(u.security_b,'N') = 'N' " +
          "    OR (nvl(wa.security_b,'Y') = 'Y' AND nvl(u.security_b,'N') = 'Y' " +
          "   AND (not exists (select 1 from pfprof_web_navigation_xref x1 " +
          "                     WHERE x1.pf_web_navigation_fk = u.pf_web_navigation_pk) " +
          "    OR exists (select 1 from pfuser_prof up, pfprof_web_navigation_xref x " +
          "                where x.application = up.application " +
          "                  and x.profile = up.profile and upper(up.userid) = upper(?) " +
          "                  and x.pf_web_navigation_fk = u.pf_web_navigation_pk " +
          "                  and x.status = 'A' and up.status = 'A') " +
          "        ) " +
          "        )) " +
          " ORDER BY u.sequence";

    private String m_topNavFileName;

    private static final String KEY_FOR_APPLICATION_ID = "applicationId";
    private static final String KEY_DEPENDANT_APPLICATION_IDS = "dependantApplicationIds";
    private static final String APPLICATION_GLOBAL_MENU_GROUP = "applicationMenus";
    private static final String APPLICATION_TOP_NAV_GROUP = "applicationTopNav";
    private static final String APPLICATION_JUMP_NAV_GROUP = "applicationJumpNav";

    private static final String CACHE_ID_FOR_PAGE_BEANS = "pageBeans";
    private static final String CACHE_ID_FOR_MENU_GROUPS = "menuGroups";
    private static final String CACHE_ID_FOR_ACTION_ITEM_GROUPS = "ActionItemGroups";
    private static final String CACHE_ID_FOR_DBPOOLID_LIST = "DBPoolIdList";
    private static final String CACHE_ID_FOR_ACTION_ITEM_GROUP_MAPPER = "ActionItemToAtionItemGroupMapper";
    private static final String CACHE_ID_FOR_NAVIGATION_LIST_AS_DOM  = "NavigationListAsDOM";
    private static final String KEY_FOR_ADDL_GLOBAL_NAV_MENU_ID_LIST = "addlGlobalNavMenuIdList";
    private static final String KEY_FOR_LOAD_NAVIGATION_LIST_AS_DOM  = "navigationManager.loadNavigationUsingDOM";

    private Map<String, Boolean> processedMenuGroups = new HashMap<String, Boolean>();
    
    private class NavigationInfo {
        public String navigationPk = null;
        public String longDescription = null;
        public String type = null;
        public String url = null;
        public String urlStruts = null;
        public String shortDescription = null;
        public String parentId = null;
        public String parentNavigationFk = null;
        public String isHidden = null;
        public String isSubMenuExists = null;
        public String toolTip = null;
        public String strutsAction = null;
        public String sequence = null;
        public String navStrutsAction = null;

        public NavigationInfo (Node n) {
            for (int j=0; j<n.getChildNodes().getLength(); j++) {
                if (n.getChildNodes().item(j).getNodeName().equalsIgnoreCase("PF_WEB_NAVIGATION_PK")) {
                    navigationPk = n.getChildNodes().item(j).getTextContent();
                } else if (n.getChildNodes().item(j).getNodeName().equalsIgnoreCase("LONG_DESCRIPTION")) {
                    longDescription = n.getChildNodes().item(j).getTextContent();
                } else if (n.getChildNodes().item(j).getNodeName().equalsIgnoreCase("TYPE")) {
                    type = n.getChildNodes().item(j).getTextContent();
                } else if (n.getChildNodes().item(j).getNodeName().equalsIgnoreCase("ACTION_URL")) {
                    url = n.getChildNodes().item(j).getTextContent();
                } else if (n.getChildNodes().item(j).getNodeName().equalsIgnoreCase("NAV_URL")) {
                    urlStruts = n.getChildNodes().item(j).getTextContent();
                } else if (n.getChildNodes().item(j).getNodeName().equalsIgnoreCase("STRUTS_ACTION")) {
                    strutsAction = n.getChildNodes().item(j).getTextContent();
                } else if (n.getChildNodes().item(j).getNodeName().equalsIgnoreCase("SEQUENCE")) {
                    sequence = n.getChildNodes().item(j).getTextContent();
                } else if (n.getChildNodes().item(j).getNodeName().equalsIgnoreCase("SHORT_DESCRIPTION")) {
                    shortDescription = n.getChildNodes().item(j).getTextContent();
                } else if (n.getChildNodes().item(j).getNodeName().equalsIgnoreCase("PARENTID")) {
                    parentId = n.getChildNodes().item(j).getTextContent();
                } else if (n.getChildNodes().item(j).getNodeName().equalsIgnoreCase("PARENT_PF_WEB_NAVIGATION_FK")) {
                    parentNavigationFk = n.getChildNodes().item(j).getTextContent();
                    parentNavigationFk = (StringUtils.isBlank(parentNavigationFk)? "-1" : parentNavigationFk);
                } else if (n.getChildNodes().item(j).getNodeName().equalsIgnoreCase("ISSUBMENUEXISTS")) {
                    isSubMenuExists = n.getChildNodes().item(j).getTextContent();
                } else if (n.getChildNodes().item(j).getNodeName().equalsIgnoreCase("ISHIDDEN")) {
                    isHidden = n.getChildNodes().item(j).getTextContent();
                    isHidden = (StringUtils.isBlank(isHidden) ? "false" : isHidden);
                } else if (n.getChildNodes().item(j).getNodeName().equalsIgnoreCase("NAV_STRUTS_ACTION")) {
                    navStrutsAction = n.getChildNodes().item(j).getTextContent();
                }
            }
        }

    }
}
