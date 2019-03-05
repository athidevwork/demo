package dti.oasis.navigationmgr.struts;

import dti.oasis.util.LogUtils;
import dti.oasis.util.PageBean;
import dti.oasis.util.MenuBean;
import dti.oasis.util.StringUtils;
import dti.oasis.http.RequestIds;
import dti.oasis.app.ConfigurationException;

import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.struts.taglib.TagUtils;

/**
 * Extends the struts BodyTagSupport to form a new tag that renders the global navigation menu.
 * 
 * This tag goes hand-in-hand with menu.css and menu.js files.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 19, 2007
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/02/2007       sxm         Added title attribute
 * 06/09/2010       James       Issue#105820 add table PF_WEB_PAGE_NAV_XREF
 * 08/24/2011       MLM         124532 - Added FINER logging messages.
 * ---------------------------------------------------
 */
public class OasisGlobalNav extends javax.servlet.jsp.tagext.BodyTagSupport {

    @Override
    public int doStartTag() throws JspException {
        l.entering(getClass().getName(), "doStartTag");
      int rc = EVAL_BODY_BUFFERED;
      TagUtils util = TagUtils.getInstance();


      PageBean pageBean = null;
      if (pageContext.getRequest().getAttribute("pageBean") == null ) {
        l.logp(Level.WARNING, getClass().getName(), "doStartTag", "pageBean for the current request is NULL");
      } else {
        pageBean = (PageBean) pageContext.getRequest().getAttribute("pageBean");
        if (pageBean.getAllowedNavigations() != null && pageBean.getGlobalMenuId() != null) {
          if (!pageBean.getAllowedNavigations().contains(pageBean.getGlobalMenuId())) {
              String message = new StringBuffer().append("Root Menu [id=").append(pageBean.getGlobalMenuId())
                      .append("] is not configed on page").append(" [id=").append(pageBean.getId()).append("].")
                      .toString();
              l.logp(Level.SEVERE, getClass().getName(), "doStartTag", message);
              throw new ConfigurationException(message);
          }
        }

        String requestURI = (String) pageContext.getRequest().getAttribute(RequestIds.REQUEST_URI);
        ArrayList globalNavMenus = pageBean.getLeftNavMenu();

        selectedMenuIds = getSelectedMenuIds(selectedMenuIds, requestURI, globalNavMenus);
        if(StringUtils.isBlank(selectedMenuIds)) {
          selectedMenuIds = "";
        }

        Iterator it = globalNavMenus.iterator();

        int horizontalMenuItemNo=0;           //Used to position the sub menu under appropriate root menu
        int subMenuIdx=0;                     //Used to define the height of iframe for each sub menu
        int menuDepth = 0;
        String lastProcessedParentId = "";
        String startIdxForParentSubmenu = "";
        String menuBreadCrumbs="";
        boolean isSubMenuTagEnded=false;

        StringBuffer menu = new StringBuffer("");
        menu.append("<!-- BEGIN: Global Navigation Root Menu -->").append("\r\n");
        menu.append("<div class=\"jMenuDiv\">").append("\r\n");
        menu.append("  <ul id=\"jMenu\" class=\"jMenu\">").append("\r\n");

        menuDepth++;
        /*
            Root Menu1 - Root Menu2 - Root Menu3
                             |
                          Sub Menu1
                             |
                              -- Sub Menu1.1
                              -- Sub Menu1.2
                          Sub Menu2
                          Sub Menu3

            The iterator will hold the menus in following order:

            1. Root Menu1
            2. Root Menu2
            3.    Sub Menu1
            4.      Sub Menu1.1
            5.      Sub Menu1.2
            6.    Sub Menu2
            7.    Sub Menu3
            8. Root Menu3

            The menuBreadCrumbs will hold Parent menu path for sub menus like:
                1. Root Menu2 (If you are at Sub Menu1)
                2. Root Menu2 - Sub Menu1  (If you are at Sub Menu1.1 or Sub Menu1.2)
         */
        while (it.hasNext()) {

            MenuBean menuBean = (MenuBean) it.next();

            String parentId = "";
            if (menuBean.hasParentId()) {
                parentId = menuBean.getParentId();
            }

            l.logp(Level.FINER, getClass().getName(), "doStartTag", "parentId = " + parentId + ";lastProcessedParentId = "+lastProcessedParentId);
            // Generate closing tag for each sub menu - BEGIN.
            if(!parentId.equalsIgnoreCase(lastProcessedParentId)) {
                l.logp(Level.FINER, getClass().getName(), "doStartTag", "menuBreadCrumbs (in IF 1) = " + menuBreadCrumbs);
                // StringUtils.isBlank(lastProcessedParentId) - this condition will make sure that we write out the closing tags only for sub menus
                if(menuBreadCrumbs.endsWith(lastProcessedParentId) && StringUtils.isBlank(lastProcessedParentId) == false) {

                  //Close all tags related to the processed sub menu
                  menu.append("\t\t\t</ul>").append("\r\n");
                  menu.append("\t\t</li>").append("\r\n");

                  isSubMenuTagEnded = true;
                  menuDepth--;

                  if(menuBreadCrumbs.indexOf(",")>0) {
                    menuBreadCrumbs = menuBreadCrumbs.substring(0, menuBreadCrumbs.lastIndexOf(","+lastProcessedParentId)) ;
                  } else {
                    menuBreadCrumbs = "";
                  }
                  l.logp(Level.FINER, getClass().getName(), "doStartTag", "menuBreadCrumbs (in IF 2) = " + menuBreadCrumbs);

                  // At this point the menuBreadCrumbs must end with the parent Id, if not close the tags for each sub menu,
                  // until we the menuBreadCrumbs ends with parent Id.
                  if(!menuBreadCrumbs.endsWith(parentId) || (!StringUtils.isBlank(menuBreadCrumbs) && StringUtils.isBlank(parentId)) ) {
                    l.logp(Level.FINER, getClass().getName(), "doStartTag", "menuBreadCrumbs (in IF 3) = " + menuBreadCrumbs);
                    while(!menuBreadCrumbs.endsWith(parentId) || (!StringUtils.isBlank(menuBreadCrumbs) && StringUtils.isBlank(parentId)) ) {
                      lastProcessedParentId = menuBreadCrumbs.substring(menuBreadCrumbs.lastIndexOf(",")+1);
                      if (startIdxForParentSubmenu.lastIndexOf(",") > 0) {
                        subMenuIdx = Integer.parseInt(startIdxForParentSubmenu.substring(startIdxForParentSubmenu.lastIndexOf(",")+1));
                        startIdxForParentSubmenu = startIdxForParentSubmenu.substring(0, startIdxForParentSubmenu.lastIndexOf(","));
                      } else {
                        if (startIdxForParentSubmenu.length()>0) {
                          subMenuIdx = Integer.parseInt(startIdxForParentSubmenu);
                          startIdxForParentSubmenu = "";
                        }
                      }
                      menu.append("\t\t\t\t</ul>").append("\r\n");
                      menu.append("\t\t\t</li>").append("\r\n");
                      menuDepth--;

                      if(menuBreadCrumbs.indexOf(",")>0) {
                        menuBreadCrumbs = menuBreadCrumbs.substring(0, menuBreadCrumbs.lastIndexOf(",")) ;
                      } else {
                        menuBreadCrumbs = "";
                      }

                    }
                  }

                  l.logp(Level.FINER, getClass().getName(), "doStartTag", "startIdxForParentSubmenu = " + startIdxForParentSubmenu);
                  if (startIdxForParentSubmenu.lastIndexOf(",") > 0) {
                    subMenuIdx = Integer.parseInt(startIdxForParentSubmenu.substring(startIdxForParentSubmenu.lastIndexOf(",")+1));
                    startIdxForParentSubmenu = startIdxForParentSubmenu.substring(0, startIdxForParentSubmenu.lastIndexOf(","));
                  } else {
                    if (startIdxForParentSubmenu.length()>0) {
                      subMenuIdx = Integer.parseInt(startIdxForParentSubmenu);
                      startIdxForParentSubmenu = "";
                    }
                  }

                }
            }
            // Generate closing tag for each sub menu - END.

            if(StringUtils.isBlank(parentId)) {
              horizontalMenuItemNo = horizontalMenuItemNo + 1;
              subMenuIdx = 0;
              startIdxForParentSubmenu = "";
              menuBreadCrumbs = "";
              l.logp(Level.FINER, getClass().getName(), "doStartTag", "parentId = " + parentId + ";horizontalMenuItemNo = " + horizontalMenuItemNo);
            }

            lastProcessedParentId = parentId;
            l.logp(Level.FINER, getClass().getName(), "doStartTag", "menuBreadCrumbs (out IF 1) = " + menuBreadCrumbs + ";subMenuIdx = " + subMenuIdx);

            HttpServletRequest req = ((HttpServletRequest)pageContext.getRequest());
            boolean isEmployee = req.isUserInRole("EMPLOYEE") || req.isUserInRole("EMPLOYEEROLE");
            boolean isAdmin = req.isUserInRole("OASISSYSADMIN") || req.isUserInRole("OASISSYSADMINROLE");
            boolean isCMAAdmin = req.isUserInRole("OASISCMAADMIN") || req.isUserInRole("OASISCMAADMINROLE");
            boolean isOasisUser = req.isUserInRole("OASISUSER") || req.isUserInRole("OASISUSERROLE");
            boolean isPMUser = req.isUserInRole("PMUSER") || req.isUserInRole("PMUSERROLE");
            boolean isCMUser = req.isUserInRole("CMUSER") || req.isUserInRole("CMUSERROLE");
            boolean isCISUser = req.isUserInRole("CISUSER") || req.isUserInRole("CISUSERROLE");
            boolean isFMUser = req.isUserInRole("FMUSER") || req.isUserInRole("FMUSERROLE");
            boolean isRMUser = req.isUserInRole("RMUSER") || req.isUserInRole("RMUSERROLE");
            boolean isMenuVisible = true;
            if(!isEmployee && !isAdmin) {
                if (!isCMAAdmin && "ClaimsAdmin".equalsIgnoreCase(menuBean.getId())) {
                    isMenuVisible = false;
                }
            }
            if (isMenuVisible && !isOasisUser) {
                if ((!isPMUser && "Policy".equalsIgnoreCase(menuBean.getId())) ||
                    (!isCMUser && "Claims".equalsIgnoreCase(menuBean.getId())) ||
                    (!isCISUser && "CIS".equalsIgnoreCase(menuBean.getId())) ||
                    (!isFMUser && "FM".equalsIgnoreCase(menuBean.getId())) ||
                    (!isRMUser && "RM".equalsIgnoreCase(menuBean.getId())) ||
                    (!isCISUser && !isPMUser && !isCMUser && !isFMUser && !isRMUser && "CS".equalsIgnoreCase(menuBean.getId()))) {
                    isMenuVisible = false;
                }
            }

            if (isMenuVisible) {
                //Generate Global Navigation Menu Item
                if (StringUtils.isBlank(menuBreadCrumbs)) {  //Horizontal Menu Bar - Menus from Root
                    menu.append(generateHorizontalMenuItem(menuBean));
                } else {
                    subMenuIdx = subMenuIdx + 1;
                    menu.append(generateSubMenuItem(menuBean));
                }

                // If the current menu has sub menu, generate the required tags for sub menus
                if (menuBean.isSubMenuExists()) {
                    menuBreadCrumbs = (StringUtils.isBlank(menuBreadCrumbs) ? "" : menuBreadCrumbs + ",") + menuBean.getId();
                    if (subMenuIdx > 0) {
                        startIdxForParentSubmenu = (StringUtils.isBlank(startIdxForParentSubmenu) ? "" : startIdxForParentSubmenu + ",") + subMenuIdx;
                        subMenuIdx = 0;
                    }
                    //Create tags for new sub menu
                    menu.append("\t\t\t<ul id='").append("subMenuFor").append(menuBean.getId()).append("' >").append("\r\n");
                    menuDepth++;
                    isSubMenuTagEnded = false;
                } else {
                    menu.append("\t\t\t</li>").append("\r\n");
                }
            }
        }

        // Finally, close all unclosed tags.
        if (horizontalMenuItemNo > 0) {  //If any menu items are written out, make sure to end them appropriately.
          while (menuDepth > 1) {
              menu.append("\t\t\t</ul>").append("\r\n");
              menu.append("\t</li>").append("\r\n");  //Finish-off the menu item <li> tag
              menuDepth--;
          }
        }

        //Close the div tag that holds menu
        menu.append("  </ul>").append("\r\n");
        menu.append("</div>").append("\r\n");
        menuDepth--;
        menu.append("<!-- END: Global Navigation Root Menu -->").append("\r\n");

        util.write(pageContext, menu.toString());
      }

      rc = super.doStartTag();
      l.exiting(getClass().getName(), "doStartTag");
      return rc;
    }

    /**
     * generate root menu item
     * @param menuBean
     * @return
     */
    private StringBuffer generateHorizontalMenuItem(MenuBean menuBean) {
      String requestURI = (String) pageContext.getRequest().getAttribute(RequestIds.REQUEST_URI);
      StringBuffer menu = new StringBuffer("");

      menu.append("\t<li id='").append(menuBean.getId()).append("'>").append("\r\n");
      menu.append("\t\t<a ") ;

      if(selectedMenuIds.indexOf(menuBean.getId()+",")!=-1) {
          menu.append(" class = 'selectedMenu'");
      }
      if(menuBean.isLink(requestURI)) {
          if (!menuBean.getUrl().toUpperCase().startsWith("javascript:doMenuItem".toUpperCase())) {
              if(menuBean.getUrl().toUpperCase().startsWith("javascript".toUpperCase())) {
                  menuBean.setUrl("javascript:" + menuBean.getUrl());
              } else {
                  menuBean.setUrl("javascript:doMenuItem('" + menuBean.getId() + "','" + menuBean.getUrl() + "')");
              }
          }
          if(menuBean.getUrl().toUpperCase().substring(0, "JAVASCRIPT".length()).equalsIgnoreCase("JAVASCRIPT")) {
              menu.append(" href='#'");
              menu.append(" onclick=\"");
              if(!StringUtils.isBlank(menuBean.getLabel())) {
                menu.append(menuBean.getUrl().substring("JAVASCRIPT".length()+1));
              } else {
                  menu.append(" javascript:void(0);");
              }
              menu.append(" \"");
          } else {
            menu.append(" href=\"").append(menuBean.getUrl()).append("\"");
          }
      } else {
          menu.append(" href='javascript:void(0)'");
      }
      if (!StringUtils.isBlank(menuBean.getTooltip()))
          menu.append(" title=\"" + menuBean.getTooltip() + "\"");
      menu.append(">");

      menu.append("<span>");
      menu.append(menuBean.getLabel());
      menu.append("</span>");
      menu.append("\t\t</a>").append("\r\n");
      return menu;
    }

    /**
     * generate sub menu item
     * @param menuBean
     * @return
     */
    private StringBuffer generateSubMenuItem(MenuBean menuBean) {
      String requestURI = (String) pageContext.getRequest().getAttribute(RequestIds.REQUEST_URI);
      StringBuffer menu = new StringBuffer("");

      menu.append("\t\t\t\t<li id='").append(menuBean.getId()).append("'>").append("\r\n");

      menu.append("\t\t\t\t\t<a ") ;
      if(menuBean.isSubMenuExists()) {
          menu.append(" class = 'subMenuLinks' ");
      }
      if(menuBean.isLink(requestURI)) {
          if (!menuBean.getUrl().toUpperCase().startsWith("javascript:doMenuItem".toUpperCase())) {
              if(menuBean.getUrl().toUpperCase().startsWith("javascript".toUpperCase())) {
                  menuBean.setUrl("javascript:" + menuBean.getUrl());
              } else {
                  menuBean.setUrl("javascript:doMenuItem('" + menuBean.getId() + "','" + menuBean.getUrl() + "')");
              }
          }
          if(menuBean.getUrl().toUpperCase().substring(0, "JAVASCRIPT".length()).equalsIgnoreCase("JAVASCRIPT")) {
              menu.append(" href='#'");
              menu.append(" onclick=\"");
              if(!StringUtils.isBlank(menuBean.getLabel())) {
                  menu.append(menuBean.getUrl().substring("JAVASCRIPT".length()+1));
              } else {
                  menu.append(" javascript:void(0);");
              }
              menu.append(" \"");
          } else {
              menu.append(" href=\"" + menuBean.getUrl() + "\"");
          }
      } else {
          menu.append(" href='javascript:void(0)'");
      }
      if (!StringUtils.isBlank(menuBean.getTooltip())){
          menu.append(" title=\"" + menuBean.getTooltip() + "\"");
      }
      menu.append(">");
      menu.append("<span>");
      menu.append(menuBean.getLabel());
      menu.append("</span>");
      menu.append("</a>").append("\r\n");

      return menu;
    }

    /**
     * get selected menu ids
     * @param menuId
     * @param requestURI
     * @param globalNavMenus
     * @return
     */
    private String getSelectedMenuIds(String menuId, String requestURI, ArrayList globalNavMenus) {
        boolean isFound=false;
        Iterator it = globalNavMenus.iterator();
        while (it.hasNext() && !isFound) {
            MenuBean menuBean = (MenuBean) it.next();
            // If the menuId is blank, then find the menu for the current requested uri.
            if(StringUtils.isBlank(menuId)) {
                if(!StringUtils.isBlank(menuBean.getUrl())) {
                    isFound = !menuBean.isLink(requestURI);
                }
            } else {
                isFound = menuBean.getId().equalsIgnoreCase(menuId);
            }
            if(isFound) {
              if(menuBean.hasParentId()) {
                  menuId = getSelectedMenuIds(menuBean.getParentId(), requestURI, globalNavMenus) + (StringUtils.isBlank(menuId)? "" : menuId + ",");
              } else {
                  menuId = menuBean.getId() + ",";
              }
            }
        }
        return menuId;
    }

    private String selectedMenuIds;
    private final Logger l = LogUtils.getLogger(getClass());
}
