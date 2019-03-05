package dti.oasis.navigationmgr.struts;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.http.RequestIds;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.tags.OasisTagHelper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.MenuBean;
import dti.oasis.util.PageBean;
import dti.oasis.util.PageDefLoadProcessor;
import dti.oasis.util.StringUtils;
import org.apache.batik.dom.util.HashTable;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 3, 2007
 *
 * @author mmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/28/2007       sxm         Added title attribute when render action item as a button
 * 06/25/2008       yhchen      Don't add dt/dd/dl for UI 0/1
 * 06/08/2010       James       Issue#105820 add table PF_WEB_PAGE_NAV_XREF
 * 11/26/2010       Kenney      Added to support the "Hidden" property of Action Item when the Action Group is displayed
 *                              as a drop down list
 * 02/15/2012       James       Issue#129150 generate a copy of select element with hidden options.
 * 08/19/2013       mlm         147571 - Enhanced to generate ImageRight Icon in page header section.
 * ---------------------------------------------------
 */
public class OasisActionGroup extends javax.servlet.jsp.tagext.BodyTagSupport {
    /* (non-Javadoc)
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
    public int doStartTag() throws JspException {
        l.entering(getClass().getName(), "doStartTag");
        int rc = EVAL_BODY_BUFFERED;
        TagUtils util = TagUtils.getInstance();

        StringBuffer actionItem = new StringBuffer("");
        PageBean pageBean = (PageBean) pageContext.getRequest().getAttribute("pageBean");
        if (pageBean.getAllowedNavigations() != null) {
            if (!pageBean.getAllowedNavigations().contains(getActionItemGroupId())) {
                String message = new StringBuffer().append("Action group [id=").append(getActionItemGroupId())
                        .append("] is not configed on page").append(" [id=").append(pageBean.getId()).append("].")
                        .toString();
                l.logp(Level.SEVERE, getClass().getName(), "doStartTag", message);
                throw new ConfigurationException(message);
            }
        }
        if (pageBean.getActionItemGroups() != null && pageBean.getActionItemGroups().containsKey(getActionItemGroupId())) {
            ArrayList actionItemGroup = (ArrayList) pageBean.getActionItemGroups().get(getActionItemGroupId());

            PageDefLoadProcessor dataSecurityPageDefaultLoadProcessor = null;
            if (RequestStorageManager.getInstance().has(RequestStorageIds.DATA_SECURITY_PAGE_DEF_LOAD_PROCESSOR)) {
                dataSecurityPageDefaultLoadProcessor = (PageDefLoadProcessor) RequestStorageManager.getInstance().get(RequestStorageIds.DATA_SECURITY_PAGE_DEF_LOAD_PROCESSOR);
            }

            String buttonClass = (getLayoutDirection().toUpperCase().startsWith("V") ? "verticalButtonCollection" : "horizontalButtonCollection");
            String actionItemsToRemove = "";
            if (pageContext.getRequest().getAttribute(RequestIds.ACTION_ITEM_IDS_TO_REMOVE) != null) {
                actionItemsToRemove = (String) pageContext.getRequest().getAttribute(RequestIds.ACTION_ITEM_IDS_TO_REMOVE);
            }
            boolean isActionItemSecured = false;
            boolean useJqxGrid = OasisTagHelper.isUseJqxGrid(pageContext);
            if (getIsDropDownActionItemGroup()) {

                String imageRightNavShortDescList = ApplicationContext.getInstance().getProperty(IMAGERIGHT_NAVIGATION_SHORT_DESCRIPTION_LIST_KEY, "");
                HashMap<String, MenuBean> imageRightNavigationList = new HashMap<String, MenuBean>();
                StringBuffer originalFieldBuffer = new StringBuffer();
                actionItem.append("<!-- BEGIN: Action Item Group '").append(getActionItemGroupId()).append("' -->").append("\r\n");
                actionItem.append("<div id=\"globalDropdownActionItems\">" + "\r\n");
                actionItem.append("\t<select id=\"" + getActionItemGroupId() + "\" class=\"globalActionItemList\" onchange=\"fireGlobalAction(this);\" >" + "\r\n");
                // first option
                String firstOption = "\t\t<option value=''>" + getDropDownSelectFromDesc() + "</option>" + "\r\n";
                actionItem.append(firstOption);
                originalFieldBuffer.append(firstOption);

                String dropdownActionItemIds = "";
                Iterator it = actionItemGroup.iterator();
                while (it.hasNext()) {
                    MenuBean menuBean = (MenuBean) it.next();
                    if (dataSecurityPageDefaultLoadProcessor != null) {
                        // postProcessActionItem returns true, if the item is not secured
                        isActionItemSecured = (dataSecurityPageDefaultLoadProcessor.postProcessActionItem(menuBean) == false);
                    }
                    if (actionItemsToRemove.toUpperCase().indexOf(menuBean.getId().toUpperCase() + ",") == -1 ) {
                        if (!isActionItemSecured) {
                            String optionString1 = new StringBuffer("\t\t<option value=\"").append(menuBean.getUrl()).append("\" ")
                                .append((menuBean.isLink((String) pageContext.getRequest().getAttribute("requestURI")) ? "selected" : "")).toString();
                            String optionString2 = new StringBuffer(">").append(menuBean.getLabel()).append("</option>").append("\r\n").toString();

                            originalFieldBuffer.append(optionString1);
                            // add id
                            originalFieldBuffer.append(" id=\"OPTION_" + menuBean.getId() + "\" ");
                            if (menuBean.isHidden()) {
                                originalFieldBuffer.append(" isHiddenByDefault=\"true\"");
                            } else {
                                actionItem.append(optionString1);
                                actionItem.append(optionString2);
                            }
                            originalFieldBuffer.append(optionString2);

                            if(("," + imageRightNavShortDescList + ",").indexOf(menuBean.getId().toUpperCase()) != -1) {
                                imageRightNavigationList.put(menuBean.getId().toUpperCase(), menuBean);
                            }
                        }
                        dropdownActionItemIds += (StringUtils.isBlank(dropdownActionItemIds) ? "" : ",") + menuBean.getId();
                    }
                }
                actionItem.append("\t</select>" + "\r\n");
                // add a invisible field with all the options
                if(useJqxGrid)
                    actionItem.append("\t<select id=\"" + getActionItemGroupId() + "_OPTIONS\" class=\"dti-hide\">"+ "\r\n");
                else
                    actionItem.append("\t<select id=\"" + getActionItemGroupId() + "_OPTIONS\" style=\"display:none\">"+ "\r\n");
                actionItem.append(originalFieldBuffer.toString());
                actionItem.append("\t</select>" + "\r\n");

                Iterator<String> imageRightNavIterator = imageRightNavigationList.keySet().iterator();
                while (imageRightNavIterator.hasNext()) {
                    String imageRightNavId = imageRightNavIterator.next();

                    actionItem.append("\t<a id='IMG_IMAGE_RIGHT_" + imageRightNavId + "' ");
                    actionItem.append("     name='IMG_IMAGE_RIGHT_" + imageRightNavId + "' ");
                    actionItem.append("     class='imgImageRight' ");
                    actionItem.append("     href='javascript:void(0)' ");
                    actionItem.append("     onclick=\"" + ((MenuBean) imageRightNavigationList.get(imageRightNavId)).getUrl() + "\" ");
                    actionItem.append("     title='" + ((MenuBean) imageRightNavigationList.get(imageRightNavId)).getLabel() + "' ");
                    actionItem.append("     alt='" + ((MenuBean) imageRightNavigationList.get(imageRightNavId)).getLabel() + "' ");
                    if(useJqxGrid)
                        actionItem.append("     style='display:inline;' >" + "\r\n");
                    else
                        actionItem.append("     style='display:none;' >" + "\r\n");
                    actionItem.append("\t<span>&nbsp;</span></a>");

                    if (imageRightNavIterator.hasNext()) {
                        actionItem.append("&nbsp;");
                    }
                    actionItem.append("\r\n");
                }

                actionItem.append("</div>" + "\r\n");
                actionItem.append("<!-- END: Action Item Group '").append(getActionItemGroupId()).append("' -->").append("\r\n");
                //dropdownActionItemIds will contain master list of action items irrespective of data security, which is used by
                //pageEntitlement javascript functions.
                pageContext.getRequest().setAttribute("dropdownActionItemIds", dropdownActionItemIds);
            } else {
                actionItem.append("<!-- BEGIN: Action Item Group '").append(getActionItemGroupId()).append("' -->").append("\r\n");

                actionItem.append("<div  class=\"actionItemGroupHidden " + buttonClass + "\">" + "\r\n");
                actionItem.append("\t<dl>" + "\r\n");

                Iterator it = actionItemGroup.iterator();
                while (it.hasNext()) {
                    MenuBean menuBean = (MenuBean) it.next();
                    if (dataSecurityPageDefaultLoadProcessor != null) {
                        // postProcessActionItem returns true, if the item is not secured
                        isActionItemSecured = (dataSecurityPageDefaultLoadProcessor.postProcessActionItem(menuBean) == false);
                    }
                    if (!isActionItemSecured && actionItemsToRemove.toUpperCase().indexOf(menuBean.getId().toUpperCase() + ",") == -1) {
                        if (menuBean.isHidden()) {
                            if(useJqxGrid)
                                actionItem.append("\t\t\t<dt class=\"dti-hide\">" + "\r\n");
                            else
                                actionItem.append("\t\t\t<dt style=\"display: none;\">" + "\r\n");
                        } else {
                            actionItem.append("\t\t\t<dt>" + "\r\n");
                        }

                        actionItem.append("\t\t\t\t<span class='left" + getCssColorScheme() + "ButtonArea'>" + "\r\n");
                        actionItem.append("\t\t\t\t\t<span class='right" + getCssColorScheme() + "ButtonArea'>" + "\r\n");
                        actionItem.append("\t\t\t\t\t\t<span class='" + getCssColorScheme() + "ButtonHolder'>" + "\r\n");
                        actionItem.append("\t\t\t\t\t\t\t<input type=button ");
                        actionItem.append(" class='" + getCssColorScheme() + "Button' ");
                        actionItem.append(" id='" + menuBean.getId() + "'");
                        if (menuBean.getUrl() != "") {
                            if (menuBean.getUrl().toUpperCase().startsWith("JAVASCRIPT")) {
                                actionItem.append(" onclick=\"" + menuBean.getUrl() + "\"");
                            } else {
                                actionItem.append(" onclick=\"javascript:doMenuItem('" + menuBean.getId() + "','" + menuBean.getUrl() + "')\"");
                            }
                        }
                        actionItem.append(" value=\"" + menuBean.getLabel() + "\"");
                        if (!StringUtils.isBlank(menuBean.getTooltip()))
                            actionItem.append(" title=\"" + menuBean.getTooltip() + "\"");

                        if (!StringUtils.isBlank(getCssWidthInPX())) {
                            String widthString = getCssWidthInPX();
                            String widthInt = null;
                            if (widthString.toUpperCase().endsWith("PX")) {
                                widthInt = widthString.substring(0, widthString.length() - 2);
                            } else {
                                widthInt = widthString;
                                widthString = widthString + "px";
                            }
                            try {
                                int width = Integer.parseInt(widthInt);
                                // add 20 for padding
                                widthString = "" + (width + 20) + "px";
                            } catch (NumberFormatException e) {
                                // parse failed, use parameter directly
                            }
                            actionItem.append(" style='width:" + widthString + "' ");
                        }
                        actionItem.append("/>" + "\r\n");
                        actionItem.append("\t\t\t\t\t\t</span>" + "\r\n");
                        actionItem.append("\t\t\t\t\t</span>" + "\r\n");
                        actionItem.append("\t\t\t\t</span>" + "\r\n");
                        actionItem.append("\t\t\t</dt>" + "\r\n");
                    }
                }
                actionItem.append("\t</dl>" + "\r\n");
                actionItem.append("</div>" + "\r\n");
                actionItem.append("<!-- END: Action Item Group '").append(getActionItemGroupId()).append("' -->").append("\r\n");
            }

        }
        util.write(pageContext, actionItem.toString());
        return rc;
    }


    public String getActionItemGroupId() {
        return actionItemGroupId;
    }

    public void setActionItemGroupId(String actionItemGroupId) {
        this.actionItemGroupId = actionItemGroupId;
    }


    public String getCssColorScheme() {
        String colorScheme = "Blue";
        if (!StringUtils.isBlank(cssColorScheme)) {
            colorScheme = cssColorScheme.toUpperCase().substring(0, 1);
            if (cssColorScheme.length() > 1) {
                colorScheme += cssColorScheme.toLowerCase().substring(1);
            }
        }
        return colorScheme;
    }

    public void setCssColorScheme(String cssColorScheme) {
        this.cssColorScheme = cssColorScheme;
    }

    public String getLayoutDirection() {
        return (StringUtils.isBlank(layoutDirection) ? "Horizontal" : layoutDirection);
    }

    public void setLayoutDirection(String layoutDirection) {
        this.layoutDirection = layoutDirection;
    }

    public String getCssWidthInPX() {
        return cssWidthInPX;
    }

    public void setCssWidthInPX(String cssWidthInPX) {
        this.cssWidthInPX = cssWidthInPX;
    }

    public boolean getIsDropDownActionItemGroup() {
        return isDropDownActionItemGroup;
    }

    public void setIsDropDownActionItemGroup(boolean dropDownActionItemGroup) {
        isDropDownActionItemGroup = dropDownActionItemGroup;
    }

    public String getDropDownSelectFromDesc() {
        return dropDownSelectFromDesc;
    }

    public void setDropDownSelectFromDesc(String dropDownSelectFromDesc) {
        this.dropDownSelectFromDesc = dropDownSelectFromDesc;
    }

    private String cssWidthInPX;
    private String cssColorScheme;
    private String layoutDirection;
    private String actionItemGroupId;
    private boolean isDropDownActionItemGroup = false;
    private String dropDownSelectFromDesc = "-Select-";
    
    private static String IMAGERIGHT_NAVIGATION_SHORT_DESCRIPTION_LIST_KEY="imageRight.navigation.short.description.list";
    private final Logger l = LogUtils.getLogger(getClass());
}
