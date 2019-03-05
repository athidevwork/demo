<%@ page import="dti.oasis.tags.GridHelper" %>
<%--
  Description:

  Write the Form Fields and Layer fields from the OasisFields collection.

  The following JSTL variables can be used to alter this behavior:

  Controlling the DIV id:
    "divId" - By default, the id for the DIV tag is set to "detailDiv".
        If the gridDetailDivId variable is set and "divId" is empty, it defaults to the value of gridDetailDivId.
        Set the "divId" variable to override this.

  Controlling the visibility of the Detail DIV based on if defined "dataBean" has rows:
    "isGridBased" - By default, this variable is true, meaning that the Detail DIV is hidden if the "dataBean" has no rows.
        Set the "isGridBased" variable to false to ignore this behavior.

  Controlling the Header Text for the Detail DIV if the Page fields are being written:
    "headerText" - By default, this variable is set to "".
        Set the "headerText" variable to change the Header Text when writing the Page fields

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

  Author: mlmanickam
  Date: Dec 13, 2006


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/12/2017       lzhang      186847   Reflect grid replacement project changes
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%
    {
    String excludeLayerIds = (String) pageContext.findAttribute("excludeLayerIds");
    String includeLayerIds = (String) pageContext.findAttribute("includeLayerIds");
    String includeLayersWithPrefix = (String) pageContext.findAttribute("includeLayersWithPrefix");
    String gridHeaderSuffix = GridHelper.getGridHeaderOasisFieldNameSuffix();

//Use Java scriptlets here because indexOf is not available
//until JSTL 1.1 and WL9.1
    if (excludeLayerIds != null) {
        excludeLayerIds = "," + excludeLayerIds + ",";
    }
    else {
        excludeLayerIds = ",XYZ123,";
    }
    if (includeLayerIds != null) {
        includeLayerIds = "," + includeLayerIds + ",";
    }
%>

<c:if test="${empty isGridBased}">
    <c:set var="isGridBased" value="true"></c:set>
</c:if>

<c:if test="${empty divId}">
    <c:set var="divId" value="detailDiv"></c:set>
    <c:if test="${gridDetailDivId!=''}">
        <c:set var="divId" value="${gridDetailDivId}"></c:set>
    </c:if>
</c:if>

<c:if test="${empty headerText}">
    <c:set var="headerText" value=""></c:set>
</c:if>

<c:set var="formFieldDivDisplayStyle" value="display:block"></c:set>
<c:set var="formFieldDivDisplayStyleCss" value=""></c:set>

<c:if test="${isGridBased == true}">
    <c:when test="${useJqxGrid}">
        <c:set var="formFieldDivDisplayStyleCss" value="dti-hide"></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="formFieldDivDisplayStyle" value="display:none"></c:set>
    </c:otherwise>
    <c:if test="${dataBean != null}">
        <c:if test="${dataBean.rowCount > 0}">
            <c:set var="formFieldDivDisplayStyle" value="display:block"></c:set>
        </c:if>
    </c:if>
</c:if>

<c:if test="${empty excludePageFields}">
    <c:set var="excludePageFields" value="false"></c:set>
</c:if>
<c:if test="${empty excludeGridHeaderLayers}">
    <c:set var="excludeGridHeaderLayers" value="true"></c:set>
</c:if>
<c:if test="${empty excludeAllLayers}">
    <c:set var="excludeAllLayers" value="false"></c:set>
</c:if>
<c:if test="${empty isLayerVisibleByDefault}">
    <c:set var="isLayerVisibleByDefault" value="false"></c:set>
</c:if>

<c:when test="${useJqxGrid}">
    <c:set var="layerDefaultDisplayStyle" value=""></c:set>
    <c:set var="layerDefaultDisplayStyleCss" value="dti-hide"></c:set>
</c:when>
<c:otherwise>
    <c:set var="layerDefaultDisplayStyle" value="display:none"></c:set>
    <c:set var="layerDefaultDisplayStyleCss" value=""></c:set>
</c:otherwise>
<c:if test="${isLayerVisibleByDefault==true}">
    <c:set var="layerDefaultDisplayStyle" value="display:block"></c:set>
</c:if>

<%
    String divId = (String) pageContext.getAttribute("divId");
    String headerText = (String) pageContext.getAttribute("headerText");
%>

<div id='<c:out value="${divId}"></c:out>' style="<c:out value="${formFieldDivDisplayStyle}"> class="<c:out value="${formFieldDivDisplayStyleCss}">">
    <oweb:panel panelContentId="<%= \"panelContentIdFor\" + divId %>"
                    panelTitleId="<%= \"paneltitleIdFor\" + divId %>"
                    panelTitle = "<%= headerText %>">

<tr><td>&nbsp;</td></tr>

<c:if test="${!excludePageFields}">
        <c:set var="row" value="0"></c:set>
        <c:set var="first" value="true"></c:set>
        <c:set var="isVisible" value="true"></c:set>
        <logic:iterate id="field" collection="<%=fieldsMap.getPageFields()%>" type="dti.oasis.tags.OasisFormField">
            <c:if test="${isGridBased==true}">
                <%  String dataFdID = "C"+field.getFieldId().toUpperCase();
                    request.setAttribute("datafld",dataFdID);
                %>
            </c:if>
            <c:set var="isVisible">
                <%= field.getIsVisible() %>
            </c:set>
            <c:set var="currentRow">
                <%= field.getRowNum() %>
            </c:set>
            <c:if test="${currentRow!=row && isVisible}">
                <c:choose>
                    <c:when test="${first}">
                        <c:set var="first" value="false"></c:set>
                        <tr>
                    </c:when>
                    <c:otherwise>
                        </tr><tr>
                    </c:otherwise>
                </c:choose>
                <c:set var="row" value="${currentRow}"></c:set>
            </c:if>
            <%@include file="/core/tagfactory.jsp"%>
        </logic:iterate>
        </tr>
</c:if>
<c:if test="${!excludeAllLayers}">
        <logic:iterate id="layer" collection="<%=fieldsMap.getLayers()%>" type="dti.oasis.tags.WebLayer">
            <%
                // By default, all layers are displayed
                boolean displayLayer = true;

                if (includeLayersWithPrefix != null || includeLayerIds != null) {
                    // If either includeLayersWithPrefix or includeLayerIds is specified, all layers are hidden By default
                    displayLayer = false;

                    if (includeLayersWithPrefix != null && layer.getLayerId().startsWith(includeLayersWithPrefix)) {
                        // Include layers with matching prefix
                        displayLayer = true;
                    }
                }

             %>
            <c:if test="${excludeGridHeaderLayers == true}">
                <%
                    // Exclude Grid Header layers
                    if (!StringUtils.isBlank(gridHeaderSuffix) && layer.getLayerId().endsWith(gridHeaderSuffix)) {
                        displayLayer = false;
                    }
                %>
            </c:if>
            <%
                if (includeLayerIds != null) {
                    String layerCommaId = "," + layer.getLayerId() + ",";
                    if(includeLayerIds.indexOf(layerCommaId) >= 0) {
                        displayLayer = true;
                    }
                } else if (excludeLayerIds != null) {
                    String layerCommaId = "," + layer.getLayerId() + ",";
                    if(excludeLayerIds.indexOf(layerCommaId) >= 0) {
                        displayLayer = false;
                    }
                }

            %>
            <%
                if(displayLayer) {
            %>
            <c:set var="layerId"> <%= layer.getLayerId() %> </c:set>
            <c:set var="row" value="0"></c:set>
            <c:set var="first" value="true"></c:set>

            <logic:iterate id="field" type="dti.oasis.tags.OasisFormField" collection="<%=fieldsMap.getLayerFields(layer.getLayerId())%>">
                <c:if test="${isGridBased == true}">
                    <%  String dataFdID = "C"+field.getFieldId().toUpperCase();
                        request.setAttribute("datafld",dataFdID);
                    %>
                </c:if>
                <c:set var="isVisible">
                    <%= field.getIsVisible() %>
                </c:set>
                <c:set var="currentRow">
                    <%= field.getRowNum() %>
                </c:set>
                <c:if test="${currentRow!=row && isVisible}">
                    <c:choose>
                        <c:when test="${first}">
                            <c:set var="first" value="false"></c:set>
                            <tr id="<%= layer.getLayerId()%>" style="<c:out value="${layerDefaultDisplayStyle}" class="<c:out value="${layerDefaultDisplayStyleCss}"></c:out>">
                        </c:when>
                        <c:otherwise>
                           </tr><tr id="<%= layer.getLayerId()%>" style="<c:out value="${layerDefaultDisplayStyle}" class="<c:out value="${layerDefaultDisplayStyleCss}"></c:out>">
                        </c:otherwise>
                    </c:choose>
                    <c:set var="row" value="${currentRow}"></c:set>
                </c:if>
                <%@include file="/core/tagfactory.jsp"%>
            </logic:iterate>

            <%  }   %>
        </logic:iterate>
</c:if>
    <tr><td>&nbsp;</td></tr>
    </oweb:panel>
</div>


<!-- Reset the Default values for local variables and remove the local JSTL variables from this scope. -->
<c:remove var="isGridBased"/>
<c:remove var="divId"/>
<c:remove var="gridDetailDivId"/>
<c:remove var="headerText"/>
<c:remove var="excludePageFields"/>
<c:remove var="excludeGridHeaderLayers"/>
<c:remove var="excludeAllLayers"/>
<c:remove var="isLayerVisibleByDefault"/>
<c:remove var="includeLayersWithPrefix"/>
<c:remove var="excludeLayerIds"/>
<c:remove var="includeLayerIds"/>
<%
    }
%>