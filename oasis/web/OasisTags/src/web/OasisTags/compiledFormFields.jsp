<%@ page language="java" %>
<%@ page import="dti.oasis.util.DisconnectedResultSet" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="dti.oasis.recordset.BaseResultSetRecordSetAdaptor" %>
<%@ page import="java.util.Properties" %>
<%@ page import="dti.oasis.struts.ActionHelper" %>
<%@ page import="dti.oasis.tags.*" %>
<%--
  Description:

  Write the Form Fields and Layer fields from the OasisFields collection.

  The following request parameters can be used to alter this behavior:

  Controlling the DIV id:
    "divId" - By default, the id for the DIV tag is set to "detailDiv".
        If the gridDetailDivId variable is set and "divId" is empty, it defaults to the value of gridDetailDivId.
        Set the "divId" variable to override this.

  Controlling the visibility of the Detail DIV based on if defined "dataBean" has rows:
    "isGridBased" - By default, this variable is true, meaning that the Detail DIV is hidden if the "dataBean" has no rows.
        Set the "isGridBased" variable to false to ignore this behavior.

  Controling what fields to display:
    By default, all Page fields and layer fields are written to the page.
    "includeLayersWithPrefix" - write only Layers with the LayerId starting with the specified prefix.
        Unless "excludePageFields" is specified, page fields are still written in addition to these layers.
        If a layer matches this prefix and also matches the Grid Header Suffix, it will not be written.
        By default, all layers are written.
    "isLayerVisibleByDefault" - if set to true, sets the visibility of all written Layers to Visible.
        Defaults to false, hidding all written Layers.
    "excludePageFields" - if set to true, the Page fields are not written.
        Defaults to false, writing the Page fields.
    "excludeGridHeaderLayers" - if set to true, excludes all Grid Header Layers.
        Defaults to true.
    "excludeAllLayers" - if set to true, excludes all Layer fields.
        Defaults to false, writing all non-excluded Layers.
    "excludeLayerIds" - a comma separated list of Layer Ids to exclude.
        All other layers WILL be written.
        When specifying excludeLayerIds, there is no need to specify excludeLayerIds. If both are specified, includeLayerIds takes precedence.
        By default, all layers are written.
    "includeLayerIds" - a comma separated list of Layer Ids to write.
        All other layers will NOT be written.
        When specifying includeLayerIds, there is no need to specify excludeLayerIds.
        This variable takes precedence over excludeLayerIds and excludeGridHeaderLayers.
        By default, all layers not in the excludeLayerIds list are written.

    "removeFieldPrefix" - Set this parameter value to true, if the WebWB configuration has a standard field prefix and
        it needs to be removed before publishing the fieldId to request attribute. Default is false.

    "fieldXmlMapFileName" - If the field Id needs to be loaded from a fieldMapFile, provide this parameter with
        appropriate field map file name. Default is empty string.

    "dataBeanName" - The name of the dataBean to lookup in the request. Default is "dataBean".

  Controlling the action group:
    "actionItemGroupId" - Id for the actionItemGroup to be rendered.
                        Default is empty string, which means no action items will be displayed.

    "actionItemGroupIdLayout" - The layout for the action item - horizontal or vertical. Default value is horizontal.

    "actionItemGroupIdCss" - Action Item CSS name - blue or gray. Default is blue.

    "actionItemGroupLocation" - Location of the action items - top or bottom. Default is bottom.

    "actionItemGroupAlign" - The alignment for the action item group - left, right or center. Default is center.

  Controlling the panel:
    "displayAsPanel" - By default, this variable is true.
    
    "panelId" - By default, the id for the panel is set to "panel".

    "hasPanelTitle" - A boolean parameter that overrides the default behavior of hasTitle property of Panel.
                      Default is true.

    "headerText" - By default, this variable is set to "".
                   Set the "headerText" variable to change the panel Title.
    "headerTextLayerId" - By default, this variable is set to "".
                   Set the "headerTextLayerId" variable to set the panel title with the associated Description defined for the specified Layer.

    "hasTitleBorder" - Indicates whether to add border for panel title - Default is true.

    "isTogglableTitle" - Indicates whether the panel is togglable - Default is true.

    "isPanelCollaspedByDefault" - true or false; true means collapse the panel.

    "collaspeTitleForPanel" - Title used for collapsed panel.

    "isPanelHiddenByDefault" - true or false; true means hide the panel.

  Controlling others:

     "isCWBOverride"    The first field on eacg row is disabled.
                        Default is false.  (Currently used only by Cust WebWB)

     "hideSetToNull"    Hide the "Set To Null" column when isCWBOverride is true
                        Default is false.  (Currently used only by Cust WebWB)
  Author: mmanickam
  Date: Aug 2, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  08/21/2007       sxm         Added isPanelCollaspedByDefault and collaspeTitleForPanel
  03/18/2008       wer         Added headerTextLayerId to define the layer who's description will be used to set the header text
  04/09/2008       yhyang      Merge with pmcore/compiledFormFields.jsp
  05/05/2010       syang       106550 - If the layer is hidden, system should only loop twice to hide it's panel title
                                        and associated grid as well.
  10/15/2010       dzhang      112064 - Added the attribute isPanelHiddenByDefault to oweb:panel.
  10/26/2010       James       Issue#112299 Added parameter displayAsPanel
  11/02/2010       James       Issue#112301 Added parameter hideSetToNull
  09/27/2018       kshen       195835. Changed to support panelTitleLayerId
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<%
    String dataBeanName = (request.getParameter("dataBeanName") == null ? "dataBean" : (String) request.getParameter("dataBeanName"));
    String excludeLayerIds = (String) pageContext.findAttribute("excludeLayerIds");
    String includeLayerIds = (String) pageContext.findAttribute("includeLayerIds");
    String includeLayersWithPrefix = (String) pageContext.findAttribute("includeLayersWithPrefix");
    String gridHeaderSuffix = GridHelper.getGridHeaderOasisFieldNameSuffix();
    boolean hasVisibleLayer = false;

    if (StringUtils.isBlank(excludeLayerIds)) {
        excludeLayerIds = (request.getParameter("excludeLayerIds") == null ? null : (String) request.getParameter("excludeLayerIds"));
    }
    if (StringUtils.isBlank(includeLayerIds)) {
        includeLayerIds = (request.getParameter("includeLayerIds") == null ? null : (String) request.getParameter("includeLayerIds"));
    }
    if (StringUtils.isBlank(includeLayersWithPrefix)) {
        includeLayersWithPrefix = (request.getParameter("includeLayersWithPrefix") == null ? null : (String) request.getParameter("includeLayersWithPrefix"));
    }

    //Use Java scriptlets here because indexOf is not available
    //until JSTL 1.1 and WL9.1
    if (!StringUtils.isBlank(excludeLayerIds)) {
        excludeLayerIds = "," + excludeLayerIds + ",";
    } else {
        excludeLayerIds = ",XYZ123,";
    }
    if (!StringUtils.isBlank(includeLayerIds)) {
        includeLayerIds = "," + includeLayerIds + ",";
    }

    boolean isGridBased = Boolean.valueOf((request.getParameter("isGridBased") == null ? "true" : (String) request.getParameter("isGridBased"))).booleanValue();
    String priorDataSrcInRequest = null;
    if (!isGridBased) {
        if (request.getAttribute("datasrc") != null) {
            priorDataSrcInRequest = (String) request.getAttribute("datasrc");
        }
        request.removeAttribute("datasrc");
    }

    String divId = (request.getParameter("divId") == null ? "detailDiv" : request.getParameter("divId"));
    if (request.getParameter("divId") == null) {
        if (!StringUtils.isBlank(request.getParameter("gridDetailDivId"))) {
            divId = (String) request.getParameter("gridDetailDivId");
        } else {
            if (request.getAttribute("gridDetailDivId") != null) {
                divId = (String) request.getAttribute("gridDetailDivId");
            } else {
                if (pageContext.getAttribute("gridDetailDivId") != null) {
                    divId = (String) pageContext.getAttribute("gridDetailDivId");
                }
            }
        }
    }

    String gridDetailDivId = request.getParameter("gridDetailDivId");
    if (StringUtils.isBlank(gridDetailDivId)) {
        if (request.getAttribute("gridDetailDivId") != null) {
            gridDetailDivId = (String) request.getAttribute("gridDetailDivId");
        } else {
            if (pageContext.getAttribute("gridDetailDivId") != null) {
                gridDetailDivId = (String) pageContext.getAttribute("gridDetailDivId");
            }
        }
    }
    if (StringUtils.isBlank(gridDetailDivId))
        gridDetailDivId = divId;

    String panelId = (request.getParameter("panelId") == null ? "panel" : request.getParameter("panelId"));

    String headerText = (request.getParameter("headerText") == null ? "" : (String) request.getParameter("headerText"));
    String headerTextLayerId = (request.getParameter("headerTextLayerId") == null ?
        "" : (String) request.getParameter("headerTextLayerId"));
//    if (!StringUtils.isBlank(headerTextLayerId)) {
//        headerText = ((WebLayer) fieldsMap.get(headerTextLayerId)).getDescription();
//        StringUtils.replace(headerText, "'", "\\'");
//    }

    boolean isLayerHidden = false;
    if (!StringUtils.isBlank(headerTextLayerId)) {
        WebLayer layer = (WebLayer) fieldsMap.get(headerTextLayerId);
        if (layer != null && layer.isHidden()) {
            isLayerHidden = true;
        }
    }

    boolean useJqxGrid = dti.oasis.tags.OasisTagHelper.isUseJqxGrid(pageContext);
    String formFieldDivDisplayStyle = "display:block";
    boolean isFormFieldDivHidden = false;
    String gridID = "", gridDisplayGridId = "";
    if (isGridBased) {
        gridID = (String) pageContext.findAttribute("gridID");
        if (StringUtils.isBlank(gridID)) {
            gridID = (request.getParameter("gridID") == null ? "" : (String) request.getParameter("gridID"));
        }
        if (!StringUtils.isBlank(gridID)) {
            request.setAttribute("datasrc", new StringBuffer().
                append(gridID).append("1").toString());
        }
        gridDisplayGridId = (String) request.getAttribute("gridDisplayGridId");
        if (StringUtils.isBlank(gridDisplayGridId))
            gridDisplayGridId = gridID;

        if(useJqxGrid)
            isFormFieldDivHidden = true;
        else
            formFieldDivDisplayStyle = "display:none";


        try {
            DisconnectedResultSet dataBean = null;
            if (request.getAttribute(dataBeanName) != null) {
                dataBean = (DisconnectedResultSet) request.getAttribute(dataBeanName);
            }
            if (dataBean != null) {
                if (dataBean.getRowCount() > 0) {
                    if(useJqxGrid)
                        isFormFieldDivHidden = false;
                    else
                        formFieldDivDisplayStyle = "display:block";
                }
            }
        }
        catch (Exception ClassCastException) {
            BaseResultSetRecordSetAdaptor dataBean = null;
            if (request.getAttribute(dataBeanName) != null) {
                dataBean = (BaseResultSetRecordSetAdaptor) request.getAttribute(dataBeanName);
            }
            if (dataBean != null) {
                if (dataBean.getRowCount() > 0) {
                    if(useJqxGrid)
                        isFormFieldDivHidden = false;
                    else
                        formFieldDivDisplayStyle = "display:block";
                }
            }
        }
    }
    boolean excludePageFields = Boolean.valueOf((request.getParameter("excludePageFields") == null ? "false" : (String) request.getParameter("excludePageFields"))).booleanValue();
    boolean excludeGridHeaderLayers = Boolean.valueOf((request.getParameter("excludeGridHeaderLayers") == null ? "true" : (String) request.getParameter("excludeGridHeaderLayers"))).booleanValue();
    boolean excludeAllLayers = Boolean.valueOf((request.getParameter("excludeAllLayers") == null ? "false" : (String) request.getParameter("excludeAllLayers"))).booleanValue();
    boolean isLayerVisibleByDefault = Boolean.valueOf((request.getParameter("isLayerVisibleByDefault") == null ? "false" : (String) request.getParameter("isLayerVisibleByDefault"))).booleanValue();
    String layerDefaultDisplayStyle = (isLayerVisibleByDefault ? "display:block" : "display:none");
//    if(useJqxGrid)
//        layerDefaultDisplayStyle = "";

    String actionItemGroupId = (request.getParameter("actionItemGroupId") == null ? "" : (String) request.getParameter("actionItemGroupId"));
    String actionItemGroupIdLayout = (request.getParameter("actionItemGroupIdLayout") == null ? "horizontal" : (String) request.getParameter("actionItemGroupIdLayout"));
    String actionItemGroupIdCss = (request.getParameter("actionItemGroupIdCss") == null ? "blue" : (String) request.getParameter("actionItemGroupIdCss"));
    String actionItemGroupIdCssWidthInPX = (request.getParameter("actionItemGroupIdCssWidthInPX") == null ? "90" : (String) request.getParameter("actionItemGroupIdCssWidthInPX"));
    String actionItemGroupLocation = (request.getParameter("actionItemGroupLocation") == null ? "bottom" : (String) request.getParameter("actionItemGroupLocation"));
    String actionItemGroupAlign = (request.getParameter("actionItemGroupAlign") == null ? "center" : (String) request.getParameter("actionItemGroupAlign"));
    boolean isTogglableTitle = Boolean.valueOf((request.getParameter("isTogglableTitle") == null ? "true" : (String) request.getParameter("isTogglableTitle"))).booleanValue();

    boolean removeFieldPrefix = Boolean.valueOf((request.getParameter("removeFieldPrefix") == null ? "false" : (String) request.getParameter("removeFieldPrefix"))).booleanValue();
    String fieldXmlMapFileName = request.getParameter("fieldXmlMapFileName");
    Properties fieldXmlMap = new Properties();
    if (!StringUtils.isBlank(fieldXmlMapFileName)) { /* If have a mapping file, load it */
        fieldXmlMap.load(ActionHelper.getResourceAsInputStream(pageContext.getServletContext(), "/" + fieldXmlMapFileName));
    }

    boolean hasPanelTitle = Boolean.valueOf((request.getParameter("hasPanelTitle") == null ? "true" : (String) request.getParameter("hasPanelTitle"))).booleanValue();
    boolean hasTitleBorder = Boolean.valueOf((request.getParameter("hasTitleBorder") == null ? "true" : (String) request.getParameter("hasTitleBorder"))).booleanValue();

    boolean isPanelCollaspedByDefault = Boolean.valueOf(request.getParameter("isPanelCollaspedByDefault") == null ? "false" : request.getParameter("isPanelCollaspedByDefault")).booleanValue();
    boolean isPanelHiddenByDefault = Boolean.valueOf(request.getParameter("isPanelHiddenByDefault") == null ? "false" : request.getParameter("isPanelHiddenByDefault")).booleanValue();
    String collaspeTitleForPanel = (request.getParameter("collaspeTitleForPanel") == null ? "" : request.getParameter("collaspeTitleForPanel"));

    boolean isCWBOverride = Boolean.valueOf((request.getParameter("isCWBOverride") == null ? "false" : (String) request.getParameter("isCWBOverride"))).booleanValue();
    boolean hideSetToNull = Boolean.valueOf((request.getParameter("hideSetToNull") == null ? "false" : (String) request.getParameter("hideSetToNull"))).booleanValue();

    boolean displayAsPanel = Boolean.valueOf((request.getParameter("displayAsPanel") == null ? "true" : (String) request.getParameter("displayAsPanel"))).booleanValue();
%>

<div id='<%= divId %>' style="<%= formFieldDivDisplayStyle %>"  class="<%=isFormFieldDivHidden ? "dti-hide" : ""%>">
<%
    if (displayAsPanel) {
%>
    <oweb:panel panelId="<%=panelId%>"
                panelContentId="<%= \"panelContentIdFor\" + divId %>"
                hasTitle="<%= hasPanelTitle %>"
                panelTitleId="<%= \"paneltitleIdFor\" + divId %>"
                panelTitle="<%= headerText%>"
                panelTitleLayerId="<%= headerTextLayerId%>"
                isTogglableTitle="<%= isTogglableTitle %>"
                panelCollapseTitle="<%= collaspeTitleForPanel %>"
                hasTitleBorder="<%=hasTitleBorder%>"
                isPanelCollaspedByDefault="<%= isPanelCollaspedByDefault %>"
                isPanelHiddenByDefault="<%= isPanelHiddenByDefault %>"
                gridId="<%= gridID %>"
    >
        <%@ include file="formFieldsContent.jsp" %>
    </oweb:panel>
<%
    } else if (isGridBased || !StringUtils.isBlank(gridDisplayGridId)) {
%>
    <table id="formFieldsTableFor<%=gridDetailDivId%>" class='formFields gridFormFieldsHidden' width="99%">
        <%@ include file="formFieldsContent.jsp" %>
    </table>
<%
    } else if (isLayerHidden) {
        if (OasisTagHelper.isUseJqxGrid(pageContext)) {
%>
    <table id="formFieldsTableFor<%=divId%>" class='formFields dti-hide' width="99%">
        <%@ include file="formFieldsContent.jsp" %>
    </table>
<%
        } else {
%>
    <table id="formFieldsTableFor<%=divId%>" style="display:none" class='formFields' width="99%">
        <%@ include file="formFieldsContent.jsp" %>
    </table>
<%
        }
    } else {
%>
    <table id="formFieldsTableFor<%=divId%>" class='formFields' width="99%">
        <%@ include file="formFieldsContent.jsp" %>
    </table>
<%
    }
    Boolean isGridCached = (Boolean) request.getAttribute("formFieldsTableForpanelContentIdFor"+gridDetailDivId+"IsCachedGrid");
    if (isGridCached != null && isGridCached.booleanValue() &&
        (isGridBased || !StringUtils.isBlank(gridDetailDivId))) {
%>
    <script type='text/javascript'>
        $('#formFieldsTableForpanelContentIdFor<%=gridDetailDivId%>').addClass('cachedGridFormFieldsHidden');
    </script>
<%
    }
%>
</div>

<!-- Hide the panel and all fields in the panel,
if there's no visible layers -->
<% if (excludePageFields && !hasVisibleLayer) { %>
<script type='text/javascript'>
    // Hide fields
    var oDivContent = getObject("panelContentIdFor" + "<%= divId %>");
    if (oDivContent) {
        if(window["useJqxGrid"])
            hideShowElementByClassName(oDivContent, true);
        else
            oDivContent.style.display = "none";
        // Since the layer is hidden, we want to hide it's panel title and associated grid as well.
        // The approach is try to find all parent element whose name is "panel" and hide them all.
        // This works as long as all pages follow DTI Web UI standard.
        var oParent = oDivContent.parentElement;
        var count = 0;
        while (oParent != null) {
            if (oParent.id == "panel") {
                if(window["useJqxGrid"])
                    hideShowElementByClassName(oParent, true);
                else
                    oParent.style.display = "none";
                count++;
            }
            if (count == 2) {
                break;
            }
            else {
                oParent = oParent.parentElement;
            }
        }
    }
</script>
<% }%>

<script type='text/javascript'>
  if ("<%= gridID %>"!="") {
      if (hasXMLDataForGridName("<%= gridID %>")) {
          var XMLData = getXMLDataForGridName("<%= gridID %>");
          if (XMLData.recordset && isEmptyRecordset(XMLData.recordset))
          {
              var detailDiv = getObject("<%= divId %>");
              if(detailDiv!=null) {
                  if(window["useJqxGrid"])
                    hideShowElementByClassName(detailDiv, true);
                  else
                      detailDiv.style.display = "none";
              }
          }else {
              var detailDiv = getObject("<%= divId %>");
              if(detailDiv!=null) {
                  if(window["useJqxGrid"]) {
                      hideShowElementByClassName(detailDiv, false);
                  } else {
                      detailDiv.style.display = "block";
                  }
              }
          }
      }
  }
</script>
<%
    if (priorDataSrcInRequest != null && !isGridBased) {
        request.setAttribute("datasrc", priorDataSrcInRequest);
    }
%>