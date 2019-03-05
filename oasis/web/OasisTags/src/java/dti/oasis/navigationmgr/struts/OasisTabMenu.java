package dti.oasis.navigationmgr.struts;

import dti.oasis.http.RequestIds;
import dti.oasis.struts.ActionHelper;
import dti.oasis.tags.OasisTagHelper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.MenuBean;
import dti.oasis.util.PageBean;
import dti.oasis.util.StringUtils;
import dti.oasis.app.ConfigurationException;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extends the struts BodyTagSupport to form a new tag that renders the tab menu panel.
 * <p/>
 * This tag goes hand-in-hand with menu.css and menu.js files.
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
 * 06/08/2010       James       Issue#105820 add table PF_WEB_PAGE_NAV_XREF
 * 11/17/2011       mlm         Added id attribute to <li> sub menu element.
 * 01/29/2014       Jyang       151396 - Modified generateTabMenu(), set tabMenu to hidden only when the tab is configured
 *                                       hidden and it is not the current selected tabMenu.
 * ---------------------------------------------------
 */
public class OasisTabMenu extends javax.servlet.jsp.tagext.BodyTagSupport {

    @Override
    public int doStartTag() throws JspException {
        l.entering(getClass().getName(), "doStartTag");
        int rc = EVAL_BODY_BUFFERED;
        TagUtils util = TagUtils.getInstance();

        boolean useJqxGrid = OasisTagHelper.isUseJqxGrid(pageContext);
        PageBean pageBean = null;
        if (pageContext.getRequest().getAttribute("pageBean") == null) {
            l.logp(Level.WARNING, getClass().getName(), "doStartTag", "pageBean for the current request is NULL");
        }
        else {

            pageBean = (PageBean) pageContext.getRequest().getAttribute("pageBean");
            if (pageBean.getAllowedNavigations() != null) {
                if (!pageBean.getAllowedNavigations().contains(getMenuGroupId())) {
                    String message = new StringBuffer().append("Tab menu [id=").append(getMenuGroupId())
                            .append("] is not configed on page").append(" [id=").append(pageBean.getId()).append("].")
                            .toString();
                    l.logp(Level.SEVERE, getClass().getName(), "doStartTag", message);
                    throw new ConfigurationException(message);
                }
            }
            if (pageBean.getMenuGroups().containsKey(getCacheIdForMenuGroupId(getMenuGroupId()))) {
                String requestURI = (String) pageContext.getRequest().getAttribute(RequestIds.REQUEST_URI);
                ArrayList tabMenu = (ArrayList) pageBean.getMenuGroups().get(getCacheIdForMenuGroupId(getMenuGroupId()));

                selectedMenuIds = getSelectedMenuIds(selectedMenuIds, requestURI, tabMenu);
                if (StringUtils.isBlank(selectedMenuIds)) {
                    selectedMenuIds = "";
                }

                Iterator it = tabMenu.iterator();

                int tabMenuItemNo = 0;           //Used to position the sub menu under appropriate tab menu
                int subMenuIdx = 0;              //Used to define the height of iframe for each sub menu
                String lastProcessedParentId = "";
                String startIdxForParentSubmenu = "";
                String menuBreadCrumbs = "";
                boolean isSubMenuTagEnded = true;

                StringBuffer menu = new StringBuffer("");
                menu.append("<!-- BEGIN: Tab Menu Group '").append(getMenuGroupId()).append("' -->").append("\r\n");
                menu.append("<div class='jTabMenuDiv'>").append("\r\n");
                menu.append("\t<ul id='").append(getMenuGroupId()).append("' class='jTabMenu'>").append("\r\n");
                /*
                   Tab Menu1 - Tab Menu2 - Tab Menu3
                                    |
                               Tab Sub Menu1
                               Tab Sub Menu2
                               Tab Sub Menu3

                   The iterator will hold the menus in following order:

                   1. Tab Menu1
                   2. Tab Menu2
                   3.    Tab Sub Menu1
                   6.    Tab Sub Menu2
                   7.    Tab Sub Menu3
                   8. Tab Menu3

                   The menuBreadCrumbs will hold Parent menu path for sub menus like:
                       1. Tab Menu2 (If you are at Tab Sub Menu1)
                */
                while (it.hasNext()) {

                    MenuBean menuBean = (MenuBean) it.next();

                    if (StringUtils.isBlank(getMenuIdsToExclude()) ||
                        (!StringUtils.isBlank(getMenuIdsToExclude()) &&
                            (getMenuIdsToExclude().indexOf("," + menuBean.getId()) == -1 &&
                                getMenuIdsToExclude().indexOf(menuBean.getId() + ",") == -1))) {
                        String parentId = "";
                        if (menuBean.hasParentId()) {
                            parentId = menuBean.getParentId();
                        }

                        // Generate closing tag for each sub menu - BEGIN.
                        if (!parentId.equalsIgnoreCase(lastProcessedParentId)) {
                            // StringUtils.isBlank(lastProcessedParentId) - this condition will make sure that we write out the closing tags only for sub menus
                            if (menuBreadCrumbs.endsWith(lastProcessedParentId) && StringUtils.isBlank(lastProcessedParentId) == false) {

                                //Close all tags related to the processed sub menu
                                menu.append("\t\t\t\t</ul>").append("\r\n");
                                menu.append("\t\t\t</li>").append("\r\n");
                                isSubMenuTagEnded = true;

                                if (menuBreadCrumbs.indexOf(",") > 0) {
                                    menuBreadCrumbs = menuBreadCrumbs.substring(0, menuBreadCrumbs.lastIndexOf("," + lastProcessedParentId));
                                }
                                else {
                                    menuBreadCrumbs = "";
                                }

                                // At this point the menuBreadCrumbs must end with the parent Id, if not close the tags for each sub menu,
                                // until we the menuBreadCrumbs ends with parent Id.
                                if (!menuBreadCrumbs.endsWith(parentId) || (!StringUtils.isBlank(menuBreadCrumbs) && StringUtils.isBlank(parentId))) {
                                    while (!menuBreadCrumbs.endsWith(parentId) || (!StringUtils.isBlank(menuBreadCrumbs) && StringUtils.isBlank(parentId))) {
                                        lastProcessedParentId = menuBreadCrumbs.substring(menuBreadCrumbs.lastIndexOf(",") + 1);
                                        if (startIdxForParentSubmenu.lastIndexOf(",") > 0) {
                                            subMenuIdx = Integer.parseInt(startIdxForParentSubmenu.substring(startIdxForParentSubmenu.lastIndexOf(",") + 1));
                                            startIdxForParentSubmenu = startIdxForParentSubmenu.substring(0, startIdxForParentSubmenu.lastIndexOf(","));
                                        }
                                        else {
                                            if (startIdxForParentSubmenu.length() > 0) {
                                                subMenuIdx = Integer.parseInt(startIdxForParentSubmenu);
                                                startIdxForParentSubmenu = "";
                                            }
                                        }
                                        menu.append("\t\t\t\t</ul>").append("\r\n");
                                        menu.append("\t\t</li>").append("\r\n");

                                        if (menuBreadCrumbs.indexOf(",") > 0) {
                                            menuBreadCrumbs = menuBreadCrumbs.substring(0, menuBreadCrumbs.lastIndexOf(","));
                                        }
                                        else {
                                            menuBreadCrumbs = "";
                                        }

                                    }
                                }

                                if (startIdxForParentSubmenu.lastIndexOf(",") > 0) {
                                    subMenuIdx = Integer.parseInt(startIdxForParentSubmenu.substring(startIdxForParentSubmenu.lastIndexOf(",") + 1));
                                    startIdxForParentSubmenu = startIdxForParentSubmenu.substring(0, startIdxForParentSubmenu.lastIndexOf(","));
                                }
                                else {
                                    if (startIdxForParentSubmenu.length() > 0) {
                                        subMenuIdx = Integer.parseInt(startIdxForParentSubmenu);
                                        startIdxForParentSubmenu = "";
                                    }
                                }

                            }
                        }
                        // Generate closing tag for each sub menu - END.

                        if (StringUtils.isBlank(parentId)) {
                            tabMenuItemNo = tabMenuItemNo + 1;
                            subMenuIdx = 0;
                            startIdxForParentSubmenu = "";
                            menuBreadCrumbs = "";
                        }

                        lastProcessedParentId = parentId;

                        //Generate Global Navigation Menu Item
                        if (StringUtils.isBlank(menuBreadCrumbs)) {    //Tab Menu
                            menu.append(generateTabMenu(menuBean, (tabMenuItemNo == 1 ? true : false),useJqxGrid));
                        }
                        else {
                            subMenuIdx = subMenuIdx + 1;
                            menu.append(generateTabSubMenu(menuBean, useJqxGrid));
                        }

                        // If the current menu has sub menu, generate the required tags for sub menus
                        if (menuBean.isSubMenuExists()) {
                            menuBreadCrumbs = (StringUtils.isBlank(menuBreadCrumbs) ? "" : menuBreadCrumbs + ",") + menuBean.getId();
                            if (subMenuIdx > 0) {
                                startIdxForParentSubmenu = (StringUtils.isBlank(startIdxForParentSubmenu) ? "" : startIdxForParentSubmenu + ",") + subMenuIdx;
                                subMenuIdx = 0;
                            }
                            //Create tags for new sub menu
                            menu.append("\t\t\t\t<ul id='tabSubMenuFor").append(menuBean.getId()).append("'>").append("\r\n");
                            isSubMenuTagEnded = false;

                        } else {
                            menu.append("\t\t\t</li>").append("\r\n");
                        }
                    }
                }

                // Finally, close all unclosed tags.
                if (tabMenuItemNo > 0) {  //If any menu items are written out, make sure to end them appropriately.
                    if (!isSubMenuTagEnded) {

                        menu.append("\t\t\t\t</ul>").append("\r\n");
                        menu.append("\t\t\t</li>").append("\r\n");
                    }
                    menu.append("\t\t<li class='lastBlankTab'><span> &nbsp;</span></li>").append("\r\n");  //Finish-off the menu item <li> tag
                }

                menu.append("\t</ul>").append("\r\n");
                //Close the div tag that holds menu
                menu.append("</div>").append("\r\n");
                menu.append("<!-- END: Tab Menu Group '").append(getMenuGroupId()).append("' -->").append("\r\n");

                util.write(pageContext, menu.toString());
            }
            else {
                l.logp(Level.WARNING, getClass().getName(), "doStartTag", "getMenuGroupId:" + getMenuGroupId() + " not found in the menu group collection.");
            }
        }
        rc = super.doStartTag();
        l.exiting(getClass().getName(), "doStartTag");
        return rc;
    }

    /**
     * generate tab menu
     * @param menuBean
     * @param isFirstTab
     * @return
     */
    private StringBuffer generateTabMenu(MenuBean menuBean, boolean isFirstTab, boolean useJqxGrid) {
        String requestURI = (String) pageContext.getRequest().getAttribute(RequestIds.REQUEST_URI);
        StringBuffer menu = new StringBuffer("");
        menu.append("\t\t<li");

        boolean hidden = (menuBean.isHidden() && (selectedMenuIds.indexOf(menuBean.getId() + ",") == -1));

        if (selectedMenuIds.indexOf(menuBean.getId() + ",") != -1) {
            if (isFirstTab) {
                if(useJqxGrid)
                    menu.append(" class='firstSelectedTab").append(hidden ? " dti-hide" : "").append("' \r\n");
                else
                    menu.append(" class='firstSelectedTab' ").append("\r\n");
            }
            else {
                if(useJqxGrid)
                    menu.append(" class='selectedTab").append(hidden ? " dti-hide" : "").append("' \r\n");
                else
                    menu.append(" class='selectedTab' ").append("\r\n");
            }
        }
        else {
            if(useJqxGrid)
                menu.append(" class='tab").append(hidden ? " dti-hide" : "").append("' \r\n");
            else
                menu.append(" class='tab' ").append("\r\n");
        }
        if(!useJqxGrid) {
            if (hidden) {
                menu.append(" style='display: none;' ");
            }
        }
        menu.append(" >");

        menu.append("\t\t\t<a ");

        if (selectedMenuIds.indexOf(menuBean.getId() + ",") != -1) {
            if (isFirstTab) {
                menu.append(" class = 'firstSelectedTab' ");
            }
            else {
                menu.append(" class = 'selectedTab' ");
            }
            menu.append(" href='javascript:void(0)'>").append("\r\n");

            if (menuBean.isSubMenuExists()) {
                menu.append("\t\t\t\t<span class='selectedTabWithDropDownImage'>");
            }
            else {
                menu.append("\t\t\t\t<span class='selectedTabWithNoDropDownImage'>");
            }

        }
        else {
            if (isFirstTab) {
                if(useJqxGrid)
                    menu.append(" class='firstTab").append(menuBean.isHidden() ? " dti-hide" : "").append("' ");
                else
                    menu.append(" class='firstTab' ");
            }
            else {
                if(useJqxGrid)
                    menu.append(" class='tab").append(menuBean.isHidden() ? " dti-hide" : "").append("' ");
                else
                    menu.append(" class='tab' ");
            }
            menu.append(" id='").append(menuBean.getId()).append("'");

            if (menuBean.isLink(requestURI)) {
                String href = menuBean.getUrl();
                if (href.toUpperCase().startsWith("JAVASCRIPT")) {
                    menu.append(" href='#'");
                    menu.append(" onclick=\" return ");
                    if (!StringUtils.isBlank(menuBean.getLabel())) {
                        menu.append(menuBean.getUrl().substring("JAVASCRIPT".length() + 1)).append(";");
                    }
                    else {
                        menu.append(" javascript:void(0);");
                    }
                    menu.append(" \"");
                }
                else {
                    href = "javascript:doMenuItem('" + menuBean.getId() + "','" + menuBean.getUrl() + "')";
                    menu.append(" href=\"").append(href).append("\"");
                }
            }
            else {
                menu.append(" href='javascript:void(0)'");
            }
            if(!useJqxGrid) {
                if (menuBean.isHidden()) {
                    menu.append(" style=\"display: none;\" ");
                }
            }
            menu.append(">").append("\r\n");

            if (menuBean.isSubMenuExists()) {
                menu.append("\t\t\t\t<span class='tabWithDropDownImage' >");
            }
            else {
                menu.append("\t\t\t\t<span class='tabWithNoDropDownImage' >");
            }
        }
        menu.append(menuBean.getLabel());
        menu.append("</span>").append("\r\n");
        menu.append("\t\t\t</a>").append("\r\n");
        return menu;
    }

    /**
     * generate Tab sub menu
     * @param menuBean
     * @return
     */
    private StringBuffer generateTabSubMenu(MenuBean menuBean, boolean useJqxGrid) {
        String requestURI = (String) pageContext.getRequest().getAttribute(RequestIds.REQUEST_URI);
        StringBuffer menu = new StringBuffer("");
        menu.append("\t\t\t\t\t<li ").append(" id='").append(menuBean.getId()).append("' ");
        if (menuBean.isSubMenuExists()) {
            if (menuBean.isHidden()) {
                if(useJqxGrid)
                    menu.append(" class=\"dti-hide\" >").append("\r\n");
                else
                    menu.append(" style=\"display: none;\" >").append("\r\n");
            }
            else {
                menu.append(" >").append("\r\n");
            }
        }
        else {
            if (menuBean.isHidden()) {
                if(useJqxGrid)
                    menu.append(" class=\"dti-hide\"  > ").append("\r\n");
                else
                    menu.append(" style=\"display: none;\"  > ").append("\r\n");
            }
            else {
                menu.append(" > ").append("\r\n");
            }
        }
        menu.append("\t\t\t\t\t\t<a ");
        if (menuBean.isSubMenuExists()) {
            menu.append(" class = 'subMenuLinks' ");
        }
        if (menuBean.isLink(requestURI)) {
            if (menuBean.getUrl() != null && menuBean.getUrl().toUpperCase().startsWith("JAVASCRIPT")) {
                menu.append(" href='#'");
                menu.append(" onclick=\" return ");
                if (!StringUtils.isBlank(menuBean.getLabel())) {
                    menu.append(menuBean.getUrl().substring("JAVASCRIPT".length() + 1)).append(";");
                }
                else {
                    menu.append(" javascript:void(0);");
                }
                menu.append(" \"");
            }
            else {
                String href = menuBean.getUrl();
                href = new StringBuffer().append("javascript:doMenuItem('").append(menuBean.getId()).append("','").append(menuBean.getUrl()).append("')").toString();
                menu.append(" href=\"").append(href).append("\"");
            }
        }
        else {
            menu.append(" href='javascript:void(0)'");
        }
        menu.append(">");
        if (menuBean.isSubMenuExists()) {
            menu.append("\t\t\t\t\t\t\t<span class='tabWithDropDownImage'  >");
        }
        else {
            menu.append("\t\t\t\t\t\t\t<span class='tabWithNoDropDownImage'  >");
        }
        menu.append(menuBean.getLabel());
        menu.append("</span>").append("\r\n");
        menu.append("\t\t\t\t\t\t</a>").append("\r\n");

        return menu;
    }

    /**
     * get selected menu id
     * @param menuId
     * @param requestURI
     * @param tabMenus
     * @return
     */
    private String getSelectedMenuIds(String menuId, String requestURI, ArrayList tabMenus) {
        boolean isFound = false;
        Iterator it = tabMenus.iterator();
        while (it.hasNext() && !isFound) {
            MenuBean menuBean = (MenuBean) it.next();
            // If the menuGroupId is blank, then find the menu for the current requested uri.
            if (StringUtils.isBlank(menuId)) {
                if (!StringUtils.isBlank(menuBean.getUrl())) {
                    isFound = !menuBean.isLink(requestURI);
                }
            }
            else {
                isFound = menuBean.getId().equalsIgnoreCase(menuId);
            }
            if (isFound) {
                if (menuBean.hasParentId()) {
                    menuId = getSelectedMenuIds(menuBean.getParentId(), requestURI, tabMenus) + (StringUtils.isBlank(menuId) ? "" : menuId + ",");
                }
                else {
                    menuId = menuBean.getId() + ",";
                }
            }
        }
        return menuId;
    }

    private String getCacheIdForMenuGroupId(String menuGroupId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCacheIdForMenuGroupId", new Object[]{menuGroupId});
        }
        String cacheIdForMenuGroupId = "";
        try {
            menuGroupId = menuGroupId + "For" + ActionHelper.getDbPoolId((HttpServletRequest) pageContext.getRequest())
                + "_" + ActionHelper.getCurrentUserId((HttpServletRequest) pageContext.getRequest());
        }
        catch (Exception e) {
            l.logp(Level.SEVERE, getClass().getName(), "getCacheIdForMenuGroupId", "Unable to retreive dbPoolId/UserId for the request");
            cacheIdForMenuGroupId = menuGroupId;
        }
        l.exiting(getClass().getName(), "getCacheIdForMenuGroupId", cacheIdForMenuGroupId);
        return menuGroupId;
    }

    public String getMenuGroupId() {
        return menuGroupId;
    }

    public void setMenuGroupId(String menuGroupId) {
        this.menuGroupId = menuGroupId;
    }

    public String getSelectedMenuIds() {
        return selectedMenuIds;
    }

    public void setSelectedMenuIds(String selectedMenuIds) {
        this.selectedMenuIds = selectedMenuIds;
    }

    public String getMenuIdsToExclude() {
        return menuIdsToExclude;
    }

    public void setMenuIdsToExclude(String menuIdsToExclude) {
        this.menuIdsToExclude = menuIdsToExclude;
    }

    private String menuGroupId;
    private String selectedMenuIds;
    private String menuIdsToExclude;
    private final Logger l = LogUtils.getLogger(getClass());
}
