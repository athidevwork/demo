package dti.oasis.tags;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.LogUtils;
import dti.oasis.util.PageBean;
import dti.oasis.util.PanelBean;
import dti.oasis.util.StringUtils;
import org.apache.struts.util.ResponseUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 1, 2007
 *
 * @author mmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/01/2011       dzhang      Issue 114424 - display of the navigation section in the Policy Information header.
 * 11/21/2012       Parker      137533 - Fix panel hidden logic to ensure the required validation when panel hidden
 * 04/23/2015       jyang2      162727 - Escape the special character in panelTitle.
 * 09/27/2018       kshen       195835. Changed to use StringUtils.isBlank to check if panel title layer id is blank.
 * 10/22/2018       dpang       195835. Changed getPanelCollapseTitle in case panel title hasn't been set yet.
 * ---------------------------------------------------
 */
public class OasisPanel extends BodyTagSupport {


    public String getPanelId() {
        return m_panelId;
    }

    public void setPanelId(String panelId) {
        this.m_panelId = panelId;
    }

    public boolean getIsPanelWithBorder() {
        return m_isPanelWithBorder;
    }

    public void setIsPanelWithBorder(boolean panelWithBorder) {
        m_isPanelWithBorder = panelWithBorder;
    }

    public boolean getHasTitle() {
        return m_hasTitle;
    }

    public void setHasTitle(boolean hasTitle) {
        this.m_hasTitle = hasTitle;
    }

    public String getPanelTitle() {
        if (StringUtils.isBlank(m_panelTitle)){
            if (!StringUtils.isBlank(getPanelTitleLayerId())) {
                OasisFields fields = (OasisFields)((HttpServletRequest) pageContext.getRequest()).getAttribute(IOasisAction.KEY_FIELDS);
                setPanelTitle(((WebLayer) fields.get(getPanelTitleLayerId())).getDescription());
            }
            if (StringUtils.isBlank(m_panelTitle)) {
                //get panels defined in PageBean
                PageBean pageBean = (PageBean) ((HttpServletRequest) pageContext.getRequest()).getAttribute(IOasisAction.KEY_PAGEBEAN);
                PanelBean panelBean = (PanelBean) pageBean.getPanels().get(getPanelId());
                if (panelBean != null) {
                    setPanelTitle(panelBean.getTitle());
                }
            }
        }
        return m_panelTitle;
    }

    public void setPanelTitle(String panelTitle) {
        this.m_panelTitle = StringUtils.replace(panelTitle, "'", "\\'");
    }


    public String getPanelTitleLayerId() {
        return m_panelTitleLayerId;
    }

    public void setPanelTitleLayerId(String panelTitleLayerId) {
        this.m_panelTitleLayerId = panelTitleLayerId;
    }

    public String getPanelTitleId() {
        return m_panelTitleId;
    }

    public void setPanelTitleId(String panelTitleId) {
        this.m_panelTitleId = panelTitleId;
    }

    public boolean getHasTitleBorder() {
        return m_hasTitleBorder;
    }

    public void setHasTitleBorder(boolean hasTitleBorder) {
        this.m_hasTitleBorder = hasTitleBorder;
    }

    public boolean getIsTogglableTitle() {
        return m_isTogglableTitle;
    }

    public void setIsTogglableTitle(boolean togglableTitle) {
        m_isTogglableTitle = togglableTitle;
    }

    public String getPanelContentId() {
        return m_panelContentId;
    }

    public void setPanelContentId(String panelContentId) {
        this.m_panelContentId = panelContentId;
    }

    public String getPanelCollapseTitle() {
        if(m_panelCollapseTitle == null || "".equals(m_panelCollapseTitle)){
           m_panelCollapseTitle = getPanelTitle();
        }
        return m_panelCollapseTitle;
    }

    public void setPanelCollapseTitle(String panelCollapseTitle) {
        this.m_panelCollapseTitle = StringUtils.replace(panelCollapseTitle, "'", "\\'");
    }

    public boolean getIsPanelCollaspedByDefault() {
        if(this.getPanelContentId() != null){
            OasisFields fields = (OasisFields)((HttpServletRequest) pageContext.getRequest()).getAttribute(IOasisAction.KEY_FIELDS);
            String fieldName = this.getPanelContentId() + "IsPanelCollaspedByDefault";
            if(fields != null){
                OasisFormField field = (OasisFormField)fields.get(fieldName);
                if(field != null){
                    if(YesNoFlag.getInstance(field.getDefaultValue()).booleanValue()){
                        m_isPanelCollaspedByDefault = true;
                    }
                    fields.remove(field.getFieldId());
                    ((HttpServletRequest) pageContext.getRequest()).setAttribute(IOasisAction.KEY_FIELDS, fields);
                }    
            }
        }
        return m_isPanelCollaspedByDefault;
    }

    public void setIsPanelCollaspedByDefault(boolean panelCollaspedByDefault) {
        m_isPanelCollaspedByDefault = panelCollaspedByDefault;
    }

    public boolean getIsPanelHiddenByDefault() {
        OasisFields fields = (OasisFields)((HttpServletRequest) pageContext.getRequest()).getAttribute(IOasisAction.KEY_FIELDS);
        if(this.getPanelContentId() != null){
            String fieldName = this.getPanelContentId() + "IsPanelHiddenByDefault";
            if(fields != null){
                OasisFormField field = (OasisFormField)fields.get(fieldName);
                if(field != null){
                    if(YesNoFlag.getInstance(field.getDefaultValue()).booleanValue()){
                        m_isPanelHiddenByDefault = true;
                    }
                    fields.remove(field.getFieldId());
                    ((HttpServletRequest) pageContext.getRequest()).setAttribute(IOasisAction.KEY_FIELDS, fields);
                }
            }
        }
        // if layer is hidden and panel is not yet hidden, hide the panel
        String layerId = this.getPanelTitleLayerId();
        if (m_isPanelHiddenByDefault == false && !StringUtils.isBlank(layerId)) {
            WebLayer layer = fields.getLayerFieldsMap(layerId);
            if (layer.isHidden()) {
                m_isPanelHiddenByDefault = true;
            }
        }
        return m_isPanelHiddenByDefault;
    }

    public void setIsPanelHiddenByDefault(boolean panelHiddenByDefault) {
        m_isPanelHiddenByDefault = panelHiddenByDefault;
    }

    public boolean getIsPanelHeaderDisplayNavigation() {
        if (getPanelContentId() != null) {
            OasisFields fields = (OasisFields) ((HttpServletRequest) pageContext.getRequest()).getAttribute(IOasisAction.KEY_FIELDS);
            String fieldName = getPanelContentId() + "IsPanelHeaderDisplayNavigation";
            if (fields != null) {
                OasisFormField field = (OasisFormField) fields.get(fieldName);
                if (field != null) {
                    if (YesNoFlag.getInstance(field.getDefaultValue()).booleanValue()) {
                        m_isPanelHeaderDisplayNavigation = true;
                    }
                    fields.remove(field.getFieldId());
                    ((HttpServletRequest) pageContext.getRequest()).setAttribute(IOasisAction.KEY_FIELDS, fields);
                }
            }
        }
        return m_isPanelHeaderDisplayNavigation;
    }

    public void setIsPanelHeaderDisplayNavigation(boolean isPanelHeaderDisplayNavigation) {
        m_isPanelHeaderDisplayNavigation = isPanelHeaderDisplayNavigation;
    }

    public String getNavigationTitle() {
        return m_navigationTitle;
    }

    public void setNavigationTitle(String navigationTitle) {
        this.m_navigationTitle = navigationTitle;
    }

    public String getGridId() {
        return m_gridId;
    }

    public void setGridId(String gridId) {
        m_gridId = gridId;
    }

    public int doStartTag() throws JspException {
        try {
             StringBuffer panelHeader= new StringBuffer();

/*
             panelHeader.append("<script language=javascript>" + "\n");
             panelHeader.append("    function togglePanel(src, panelToToggle, m_panelTitleId, expandTitle, collapseTitle) {" + "\n");
             panelHeader.append("       var panelTitleObj = null;" + "\n");
             panelHeader.append("       if (m_panelTitleId) {" + "\n");
             panelHeader.append("           panelTitleObj = getSingleObject(m_panelTitleId);" + "\n");
             panelHeader.append("       }" + "\n");
             panelHeader.append("       var panelObjToToggle = getSingleObject(panelToToggle);" + "\n");
             panelHeader.append("       panelObjToToggle.style.display = (panelObjToToggle.style.display==\"none\"?\"block\":\"none\");" + "\n");
             panelHeader.append("       if(panelObjToToggle.style.display==\"none\") {" + "\n");
             panelHeader.append("          src.className = \"panelDownTitle\";" + "\n");
             panelHeader.append("          if (panelTitleObj && collapseTitle) {" + "\n");
             panelHeader.append("              panelTitleObj.innerText = collapseTitle;" + "\n");
             panelHeader.append("          }" + "\n");
             panelHeader.append("       } else {" + "\n");
             panelHeader.append("          src.className = \"panelUpTitle\";" + "\n");
             panelHeader.append("           if (panelTitleObj && expandTitle) {" + "\n");
             panelHeader.append("               panelTitleObj.innerText = expandTitle;" + "\n");
             panelHeader.append("           }" + "\n");
             panelHeader.append("       }" + "\n");
             panelHeader.append("       return true;" + "\n");
             panelHeader.append("    }" + "\n");
             panelHeader.append("" + "\n");
             panelHeader.append("    function getPanel(childControl) {" + "\n");
             panelHeader.append("        var panelControl = null;" + "\n");
             panelHeader.append("        if (childControl) {" + "\n");
             panelHeader.append("            while (childControl.parentElement) {" + "\n");
             panelHeader.append("                panelControl = childControl.parentElement;" + "\n");
             panelHeader.append("                if (childControl.parentElement.id==\"" + getPanelId() + "\") {" + "\n");
             panelHeader.append("                  break;" + "\n");
             panelHeader.append("                }" + "\n");
             panelHeader.append("                childControl = panelControl;" + "\n");
             panelHeader.append("            }" + "\n");
             panelHeader.append("        }" + "\n");
             panelHeader.append("        return panelControl;" + "\n");
             panelHeader.append("    }" + "\n");
             panelHeader.append("</script>" + "\n");
*/
            boolean useJqxGrid = OasisTagHelper.isUseJqxGrid(pageContext);
             String panelDefaultStyle = "display:block;";
             if (!getIsPanelWithBorder()) {
                 panelDefaultStyle  += "border:none;";
             }

             panelHeader.append("<!-- Panel : Start -->\n");
             if(useJqxGrid){
                 String panelDefaultClass = "panel";
                 if (getIsPanelHiddenByDefault()) {
                     panelDefaultClass += " dti-hide";
                 }
                 panelHeader.append("<div id=\"" + m_panelId + "\" class=\"" + panelDefaultClass + "\" style=\"" + panelDefaultStyle + "\">" + "\n");
             } else {
                 if (getIsPanelHiddenByDefault()) {
                     panelDefaultStyle = "display:none;";
                 }
                 panelHeader.append("<div id=\"" + m_panelId + "\" class=\"panel\" style=\"" + panelDefaultStyle + "\">" + "\n");
             }
             if (getHasTitle()) {
                 panelHeader.append("<!-- Panel Title : Start -->\n");
                 panelHeader.append("\t<div id=\"" + (m_hasTitleBorder ?"panelTitleWithBorder":"panelTitleWithoutBorder") + "\" style=\"margin:0; padding-bottom: 0px; margin-bottom:0px;\" >" + "\n");

                 panelHeader.append("\t\t<table border='0' width=\"97%\"><tr>");
                 panelHeader.append("\t\t<td align=\"left\" width=\"60%\">");
                 if (getIsTogglableTitle()) {
                     String collapseIndicatorClassName = (getIsPanelCollaspedByDefault() ? "panelDownTitle" : "panelUpTitle");
                     String panelTitleToDisplay = (getIsPanelCollaspedByDefault() ? getPanelCollapseTitle() : getPanelTitle());
                     panelHeader.append("\t\t<a href=\"javascript:void(0);\" class=\"" + collapseIndicatorClassName + "\" ");
                     panelHeader.append("onclick=\"return togglePanel(this, '" + getPanelContentId() + "', '" + getPanelTitleId() + "', '" + ResponseUtils.filter(getPanelTitle()) + "', '" + ResponseUtils.filter(getPanelCollapseTitle()) + "');\" >" + "\n");
                     panelHeader.append("\t\t\t<span id=\"" + getPanelTitleId() + "\" class=\"panelTitle\">" + (StringUtils.isBlank(panelTitleToDisplay) ? "&nbsp;" : StringUtils.replace(panelTitleToDisplay, "\\'", "'")) + "</span>" + "\n");
                     panelHeader.append("\t\t</a>" + "\n");
                 } else {
                     panelHeader.append("\t\t<span id=\"" + getPanelTitleId() + "\" class=\"panelTitle\">" + (StringUtils.isBlank(getPanelTitle()) ? "&nbsp;" : StringUtils.replace(getPanelTitle(), "\\'", "'")) + "</span>" + "\n");
                 }
                 panelHeader.append("\t\t</td>" + "\n");
                 if (getIsPanelHeaderDisplayNavigation()) {
                     panelHeader.append("\t\t<td align=\"right\" width=\"37%\">");
                     panelHeader.append("<div id='navigateTerms'>");
                     panelHeader.append("\t\t<a id=\"previousTerm\" class=\"pagePreviousLink\" href=\"javascript:navigateTerms('previous')\">");
                     panelHeader.append("\t\t</a>" + "\n");
                     panelHeader.append("\t\t\t<span class=\"navigationTitle\">" + (StringUtils.isBlank(getNavigationTitle()) ? "&nbsp;" : StringUtils.replace(getNavigationTitle(), "\\'", "'")) + "</span>" + "\n");
                     panelHeader.append("\t\t<a id=\"nextTerm\" class=\"pageNextLink\" href=\"javascript:navigateTerms('next')\">");
                     panelHeader.append("\t\t</a>" + "\n");
                     panelHeader.append("</div>");
                     panelHeader.append("\t\t</td>" + "\n");
                 }
                 panelHeader.append("\t\t</tr></table>");
                 panelHeader.append("\t</div>" + "\n");
                 panelHeader.append("<!-- Panel Title : End -->" + "\n");
             }

             String panelContentClass = (getIsPanelCollaspedByDefault()? OasisTagHelper.COLLAPSE_PANEL_CLASS : "");
             panelHeader.append("\t<div id=\"" + getPanelContentId() + "\" class=\"" + panelContentClass  + "\" style=\"width:100%;\" >" + "\n");

             ServletRequest request = pageContext.getRequest();
             boolean isGridBased = Boolean.valueOf((request.getParameter("isGridBased") == null ? "true" : (String) request.getParameter("isGridBased"))).booleanValue();

             if (isGridBased || !StringUtils.isBlank(m_gridId)) {  
                 panelHeader.append("\t\t<table id=\"formFieldsTableFor" + getPanelContentId() + "\" class=\"formFields gridFormFieldsHidden\" width=\"99%\" >" + "\n");
             } else {
                 panelHeader.append("\t\t<table id=\"formFieldsTableFor" + getPanelContentId() + "\" class='formFields' width=\"99%\" >" + "\n");
             }

             this.pageContext.getOut().println(panelHeader.toString());
        } catch (IOException e) {
            throw new JspException("Error: IOException while writing to client" + e.getMessage());
        }
        return EVAL_BODY_INCLUDE;
    }

    public void setBodyContent(BodyContent bodyContent) {
        super.setBodyContent(bodyContent);
    }

    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {

        try {
            StringBuffer panelFooter= new StringBuffer();

            panelFooter.append("\t\t</table>" + "\n");
            panelFooter.append("\t</div>" + "\n");
            panelFooter.append("<!-- Panel Content: End -->" + "\n");

            panelFooter.append("</div>" + "\n");
            panelFooter.append("<!-- Panel : End -->" + "\n");

            this.pageContext.getOut().println(panelFooter.toString());
        } catch (IOException e) {
            throw new JspException("Error: IOException while writing to client" + e.getMessage());
        }
        return EVAL_PAGE;
    }

    /**
     * Release
     */
    public void release() {
        Logger l = LogUtils.enterLog(getClass(), "release");
        super.release();
        // Reset values
        m_panelTitle = "";
        m_panelCollapseTitle = "";
        l.exiting(getClass().getName(), "release");
    }
    
    private String m_panelId = "panel";
    private boolean m_isPanelWithBorder = true;
    private boolean m_hasTitle = true;
    private boolean m_isTogglableTitle = true;
    private String m_panelTitleId = "m_panelTitle";
    private String m_panelTitle = "";
    private String m_panelTitleLayerId = null;
    private String m_panelCollapseTitle = "";
    private boolean m_hasTitleBorder = true;
    private String m_panelContentId = "panelContent";
    private boolean m_isPanelCollaspedByDefault = false;
    private boolean m_isPanelHiddenByDefault = false;
    private boolean m_isPanelHeaderDisplayNavigation = false;
    private String m_navigationTitle = "";
    private String m_gridId = "";
}
