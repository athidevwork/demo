package dti.oasis.util;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaBean containing Menu data
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author jbe
 *         Date:   Aug 5, 2003
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ---------------------------------------------------
 *         2/6/2004        jbe         Added toString & Logging
 *         01/23/2007       wer         Changed usage of new Boolean(x) in logging to String.valueOf(x);
 *         05/03/2007       mlm         Added parentId and isSubMenuExists for new Navigation.
 *         09/28/2007       sxm         Added property tooltip
 *         03/18/2008       wer         Set the order of action items based on the sequence
 *         09/17/2013       Parker      148113 - The 'Set to NULL' logic isn't work in page/jump navigation.
 *         10/29/2018       jdingle     189165 - detect selected tab for entity modify.
 *         ---------------------------------------------------
 */
public class MenuBean implements Serializable, Cloneable {

    /**
     * Member variable declaration
     * NOTE : If any new variable is added to the PageBean Class, make sure to clone the new variable as part of
     * clone method.
     */
    private boolean isLink;
    private String url;
    private String id;
    private String label;
    private boolean openInNewBrowser;
    private String parentId;
    private boolean isSubMenuExists;
    private String tooltip;
    private Integer sequence;
    private boolean isHidden;

    public boolean isOpenInNewBrowser() {
        return openInNewBrowser;
    }

    public void setOpenInNewBrowser(boolean openInNewBrowser) {
        this.openInNewBrowser = openInNewBrowser;
    }

    public boolean isLink() {
        return isLink;
    }

    public boolean isLink(String requestURI) {
        Logger l = LogUtils.enterLog(getClass(), "isLink", new Object[]{requestURI});
        // The url is not a link, if it is blank. 
        boolean isUrlIsALink = !StringUtils.isBlank(getUrl());
        if (isUrlIsALink) {
            // The url is a link, if it is a javascript function -
            // We are trying to find, which menu item has been selected. Also, the url will be a javascript function, only
            // in the case of action item.
            if (!getUrl().toUpperCase().startsWith("JAVASCRIPT")) {
                // If this is the currently requested url, then this is not a link,
                // meaning the user cannot click on the menu item again.
                String Url = getUrl();
                if (Url.startsWith("~")) {
                    Url = Url.substring(2);  //Remove both ~/ characters
                }
                // This is necessary because the requestURI is modified from the original ciEntityModify.do and then
                // does not match for Org or Person.
                if (requestURI.contains("ciEntityOrgModify.do") || requestURI.contains("ciEntityPersonModify.do")) {
                    requestURI = requestURI.replace("Org","");
                    requestURI = requestURI.replace("Person","");
                }
                isUrlIsALink = !(requestURI.contains("/" + Url));
                l.logp(Level.FINE, getClass().getName(), "isLink", "requestURI:" + requestURI + "/Url:" + Url + "/isUrlIsALink?" + isUrlIsALink);
            }
        }
        return isUrlIsALink;
    }

    public void setIsLink(boolean link) {
        isLink = link;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getParentId() {
        if (parentId == null) {
            throw new IllegalArgumentException("Parent Menu Id is NULL for Id:" + getId());
        }
        return parentId;
    }

    public boolean hasParentId() {
        return (parentId != null);
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isSubMenuExists() {
        return isSubMenuExists;
    }

    public void setSubMenuExists(boolean subMenuExists) {
        isSubMenuExists = subMenuExists;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }


    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }


    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    /**
     * Constructor
     *
     * @param isLink true if this is a URL, false if text
     * @param url    url
     * @param label  text to display
     */
    public MenuBean(boolean isLink, String url, String label) {
        Logger l = LogUtils.enterLog(getClass(), "constructor", new Object[]{String.valueOf(isLink), url, label});
        this.isLink = isLink;
        this.url = url;
        this.label = label == null ? "" : label;
        l.exiting(getClass().getName(), "constructor", this);
    }

    /**
     * Constructor
     *
     * @param id
     * @param url
     * @param label
     */
    public MenuBean(String id, String url, String label) {
        Logger l = LogUtils.enterLog(getClass(), "constructor", new Object[]{id, url, label});
        this.id = id;
        this.url = url;
        this.label = label == null ? "" : label;
        this.isLink = true;
        l.exiting(getClass().getName(), "constructor", this);
    }

    /**
     * Constructor
     *
     * @param isLink
     * @param id
     * @param url
     * @param label
     * @param openInNewBrowser
     */
    public MenuBean(boolean isLink, String id, String url, String label, boolean openInNewBrowser) {
        Logger l = LogUtils.enterLog(getClass(), "constructor", new Object[]
            {String.valueOf(isLink), id, url, label, String.valueOf(openInNewBrowser)});
        this.isLink = isLink;
        this.id = id;
        this.url = url;
        this.label = label == null ? "" : label;
        this.openInNewBrowser = openInNewBrowser;
        l.exiting(getClass().getName(), "constructor", this);
    }

    /**
     * Constructor
     *
     * @param isLink
     * @param id
     * @param url
     * @param label
     * @param openInNewBrowser
     * @param parentId
     * @param tooltip
     */
    public MenuBean(boolean isLink, String id, String url, String label, boolean openInNewBrowser, String parentId,
                    boolean isSubMenuExists, String tooltip, boolean isHidden) {
        Logger l = LogUtils.enterLog(getClass(), "constructor", new Object[]
            {String.valueOf(isLink), id, url, label, String.valueOf(openInNewBrowser), tooltip});
        this.isLink = isLink;
        this.id = id;
        this.url = url;
        this.label = label == null ? "" : label;
        this.openInNewBrowser = openInNewBrowser;
        this.parentId = parentId;
        this.isSubMenuExists = isSubMenuExists;
        this.tooltip = tooltip;
        this.isHidden = isHidden;
        l.exiting(getClass().getName(), "constructor", this);
    }

    /**
     * Method that returns a deep copy clone of MenuBean
     *
     * @return MenuBean
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {
        MenuBean clonedMenuBean = new MenuBean(this.isLink, this.id, this.url, this.label, this.openInNewBrowser,
            this.parentId, this.isSubMenuExists, this.tooltip, this.isHidden);
        return clonedMenuBean;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.util.MenuBean");
        buf.append("{isLink=").append(isLink);
        buf.append(",url=").append(url);
        buf.append(",id=").append(id);
        buf.append(",label=").append(label);
        buf.append(",openInNewBrowser=").append(openInNewBrowser);
        buf.append(",parentId=").append(parentId);
        buf.append(",isSubMenuExists=").append(isSubMenuExists);
        buf.append(",tooltip=").append(tooltip);
        buf.append(",isHidden=").append(tooltip);
        buf.append('}');
        return buf.toString();
    }

}
