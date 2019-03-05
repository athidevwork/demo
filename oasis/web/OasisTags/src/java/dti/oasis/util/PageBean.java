package dti.oasis.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JavaBean containing details about a web page.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * @author jbe
 * Date:   Jul 3, 2003
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  2/6/2004        jbe         Added toString();
 *  01/23/2007      mlm         Added support for help url;
 *  05/03/2007      mlm         Added menuGroups and actionItemGroups
 *                              for new Navigation.
 *  07/21/2008      sxm         issue 84782 - Initialize the topNavMenu to avoid NULL point error in header JSP
 *                              since some pages (like querynewpop.jsp) creates PageBean without all values. 
 * 02/27/2008       mlm         90311 - Added support for jumpNavigations menus.
 * 01/12/2010       James       Added id, access_trail_b when loading page bean.
 * 06/08/2010       James       Issue#105820 add table PF_WEB_PAGE_NAV_XREF
 * 09/11/2017       kshen       Added new property useJqxGridB.
 * ---------------------------------------------------
 */
public class PageBean implements Serializable, Cloneable {

  /**
   *  Member variable declaration
   *  NOTE : If any new variable is added to the PageBean Class, make sure to clone the new variable as part of
   *         clone method.
   */
    private String id;
    private String title;
    private String accessTrailB;
    private ArrayList leftNavMenu;
    private ArrayList leftNavActions;
    private ArrayList jumpNavigations;
    private List allowedNavigations;
    private String globalMenuId;
    private String helpUrl;
    private String useJqxGridB;
    private Map menuGroups;
    private Map actionItemGroups;
    private Map panels = new HashMap();

    // Initialize top nav menu
    private ArrayList topNavMenu = new ArrayList();

    // Initialize the width, height, top, left to a negative value.
    private int width = -9999;
    private int height = -9999;
    private int top = -9999;
    private int left = -9999;


  /**
   * Returns a deep copy clone of PageBean.
   * @return PageBean
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException {
    PageBean clonedPageBean = new PageBean();
    clonedPageBean.setId(this.getId());
    clonedPageBean.setTitle(this.getTitle());
    clonedPageBean.setAccessTrailB(this.getAccessTrailB());
    clonedPageBean.setLeftNavMenu(getClonedMenuBeans(this.getLeftNavMenu()));
    clonedPageBean.setLeftNavActions(getClonedMenuBeans(this.getLeftNavActions()));
    clonedPageBean.setTopNavMenu(getClonedMenuBeans(this.getTopNavMenu()));
    clonedPageBean.setJumpNavigations(getClonedMenuBeans(this.getJumpNavigations()));
    if (this.getAllowedNavigations() != null) {
      clonedPageBean.setAllowedNavigations(new ArrayList(this.getAllowedNavigations()));
    }
    clonedPageBean.setGlobalMenuId(this.getGlobalMenuId());
    clonedPageBean.setHelpUrl(this.getHelpUrl());
    clonedPageBean.setUseJqxGridB(this.getUseJqxGridB());
    clonedPageBean.setWidth(this.getWidth());
    clonedPageBean.setHeight(this.getHeight());
    clonedPageBean.setLeft(this.getLeft());
    clonedPageBean.setTop(this.getTop());

    Map clonedMenuGroups = new HashMap();
    Iterator it ;
    if (this.menuGroups!=null) {
      it = this.menuGroups.entrySet().iterator() ;
      while (it.hasNext()) {
        Map.Entry menuEntry = (Map.Entry) it.next();
        clonedMenuGroups.put( menuEntry.getKey(), getClonedMenuBeans((ArrayList) menuEntry.getValue()) );
      }
    }
    clonedPageBean.setMenuGroups(clonedMenuGroups);

    Map clonedActionItemGroups = new HashMap();
    if (this.actionItemGroups!=null) {
      it = this.actionItemGroups.entrySet().iterator() ;
      while (it.hasNext()) {
          Map.Entry actionItemEntry = (Map.Entry) it.next();
          clonedActionItemGroups.put( actionItemEntry.getKey(), getClonedMenuBeans((ArrayList) actionItemEntry.getValue()) );
      }
    }
    clonedPageBean.setActionItemGroups(clonedActionItemGroups);

    Map clonedPanels = new HashMap();
    if (this.panels!=null) {
      it = this.panels.entrySet().iterator() ;
      while (it.hasNext()) {
        Map.Entry panelEntry = (Map.Entry) it.next();
        clonedPanels.put( panelEntry.getKey(), ((PanelBean) panelEntry.getValue()).clone() );
      }
    }
    clonedPageBean.setPanels(clonedPanels);

    return clonedPageBean;
  }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
	 * @return string
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param string
	 */
	public void setTitle(String string) {
		title = string;
	}


    public String getAccessTrailB() {
        return accessTrailB;
    }

    public void setAccessTrailB(String accessTrailB) {
        this.accessTrailB = accessTrailB;
    }

    public ArrayList getLeftNavMenu() {
        return leftNavMenu;
    }

    public void setLeftNavMenu(ArrayList leftNavMenu) {
        this.leftNavMenu = leftNavMenu;
    }

    public ArrayList getLeftNavActions() {
        return leftNavActions;
    }

    public void setLeftNavActions(ArrayList leftNavActions) {
        this.leftNavActions = leftNavActions;
    }

    public ArrayList getJumpNavigations() {
      return jumpNavigations;
    }

    public void setJumpNavigations(ArrayList jumpNavigations) {
      this.jumpNavigations = jumpNavigations;
    }

  public ArrayList getTopNavMenu() {
        return topNavMenu;
    }

    public void setTopNavMenu(ArrayList topNavMenu) {
        this.topNavMenu = topNavMenu;
    }

    public void setHelpUrl(String helpUrl) {
        this.helpUrl = helpUrl;
    }

    public String getHelpUrl() {
        return this.helpUrl;
    }

    public String getUseJqxGridB() {
        return useJqxGridB;
    }

    public void setUseJqxGridB(String useJqxGridB) {
        this.useJqxGridB = useJqxGridB;
    }

    public Map getMenuGroups() {
      return menuGroups;
    }

    public void setMenuGroups(Map menuGroups) {
      this.menuGroups = menuGroups;
    }

    public Map getActionItemGroups() {
      return actionItemGroups;
    }

    public void setActionItemGroups(Map actionItemGroups) {
      this.actionItemGroups = actionItemGroups;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public List getAllowedNavigations() {
        return allowedNavigations;
    }

    public void setAllowedNavigations(List allowedNavigations) {
        this.allowedNavigations = allowedNavigations;
    }

    public Map getPanels() {
        return panels;
    }

    public void setPanels(Map panels) {
        this.panels = panels;
    }

    public String getGlobalMenuId() {
        return globalMenuId;
    }

    public void setGlobalMenuId(String globalMenuId) {
        this.globalMenuId = globalMenuId;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.util.PageBean");
        buf.append("{id=").append(id);
        buf.append(",title=").append(title);
        buf.append(",accessTrailB=").append(accessTrailB);
        buf.append(",leftNavMenu=").append(leftNavMenu);
        buf.append(",leftNavActions=").append(leftNavActions);
        buf.append(",topNavMenu=").append(topNavMenu);
        buf.append(",menuGroups=").append(menuGroups);
        buf.append(",actionItemGroups=").append(actionItemGroups);
        buf.append(",allowedNavigations=").append(allowedNavigations);
        buf.append(",globalMenuId=").append(globalMenuId);
        buf.append(",useJqxGridB=").append(useJqxGridB);
        buf.append(",height=").append(height);
        buf.append(",width=").append(width);
        buf.append(",top=").append(top);
        buf.append(",left=").append(left);
        buf.append('}');
        return buf.toString();
    }

  /**
   * Method that returns an array list containing a deep copy of each menu item that exists in the source array list.
   */
  private ArrayList getClonedMenuBeans(ArrayList sourceMenuBeans) throws CloneNotSupportedException {
    ArrayList clonedMenuBeans = null;
    if(sourceMenuBeans!=null) {
        clonedMenuBeans = new ArrayList(sourceMenuBeans.size());
        Iterator it = sourceMenuBeans.iterator();
        while (it.hasNext()) {
            clonedMenuBeans.add( ((MenuBean) it.next()).clone() ) ;
        }
    }
    return clonedMenuBeans;
  }
}
