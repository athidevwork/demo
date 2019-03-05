<%@ page language="java" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="dti.oasis.tags.OasisFormField" %>
<%@ page import="dti.oasis.tags.WebLayer" %>
<%@ page import="dti.oasis.util.PageBean" %>
<%--
  Description:

  Write the Form Fields and Layer fields from the OasisFields collection.
  This file is only used in compiledFormFields.jsp

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/01/2011         MLM       Refactored for autoHideTR - OBR.
  09/08/2015         Parker    Issue#164715 Add Usages dialog and set label to green italic for fields customized in the parent.
  -----------------------------------------------------------------------------
  (C) 2010 Delphi Technology, inc. (dti)
--%>

<%
  if (!StringUtils.isBlank(actionItemGroupId) && actionItemGroupLocation.equalsIgnoreCase("top")) {
%>
        <tr>
            <td colspan=9 align="<%=actionItemGroupAlign%>">
                <oweb:actionGroup actionItemGroupId="<%= actionItemGroupId %>"
                                  layoutDirection="<%=actionItemGroupIdLayout%>"
                                  cssColorScheme="<%=actionItemGroupIdCss%>"
                                  cssWidthInPX="<%=actionItemGroupIdCssWidthInPX%>"/>
            </td>
        </tr>
<% } %>

<%  Iterator it;
    String row = "-999999";
    boolean first = true;
    String trStyle = "style=\"display:inline;\"";
    PageBean pageBean = (PageBean) request.getAttribute("pageBean");
    String pageCode = (pageBean.getId()==null ? "pageFieldLayer" : pageBean.getId()) ;
    String trId = "PAGE_FIELDS_LAYER";
    Boolean isAutoHideTR = true;
    if(!useJqxGrid) {
    if (!excludePageFields) {
        row = "-999999";
        first = true;
        it = fieldsMap.getPageFields().iterator();
        while (it.hasNext()) {
            OasisFormField field = (OasisFormField) it.next();

            trStyle = "style=\"display:inline;\"";

            request.setAttribute("field", field);   // Used in compiledTagFactory
            if (isGridBased) {
                String dataFdID = "";
                if(!StringUtils.isBlank(fieldXmlMapFileName)){ /* If have a mapping file, use it */
                    dataFdID="C"+fieldXmlMap.get(field.getFieldId());
                } else {
                    dataFdID = "C" + (removeFieldPrefix ?
                                      field.getFieldId().substring(field.getFieldId().indexOf('_')+1) :
                                      field.getFieldId().toUpperCase());
                }
                request.setAttribute("datafld",dataFdID);
            }

            if (!field.getRowNum().equalsIgnoreCase(row)) {
                if (first) {
                    first = false;  %>
                    <tr name='<%= trId %>' onPropertyChange="baseOnPropertyChange()">
    <%            } else {
                      if (isAutoHideTR) { %>
                        <div name="autoHideTR" class="hide" style="display:none"></div>
    <%                } %>
                    </tr>
                    <tr name='<%= trId%>' onPropertyChange="baseOnPropertyChange()">
    <%          }
                row = field.getRowNum();
                isAutoHideTR = true;
            }
            if (isAutoHideTR && field.getIsVisible()) {
                isAutoHideTR = false;
            }
    %>
            <jsp:include page="compiledTagFactory.jsp" ></jsp:include>
    <%
        }
        if (isGridBased) {
            request.removeAttribute("datafld");
        }

        if (!first) {   //If atleast one <tr> is rendered, then render the closing <tr> tag
            if (isAutoHideTR) { %>
                <div name="autoHideTR" class="hide" style="display:none"></div>
<%          } %>
        </tr>
<%      }
   }
    } else {
    if (!excludePageFields) {
        row = "-999999";
        first = true;
        it = fieldsMap.getPageFields().iterator();
        while (it.hasNext()) {
            OasisFormField field = (OasisFormField) it.next();

            trStyle = "style=\"display:inline;\"";

            request.setAttribute("field", field);   // Used in compiledTagFactory
            if (isGridBased) {
                String dataFdID = "";
                if(!StringUtils.isBlank(fieldXmlMapFileName)){ /* If have a mapping file, use it */
                    dataFdID="C"+fieldXmlMap.get(field.getFieldId());
                } else {
                    dataFdID = "C" + (removeFieldPrefix ?
                            field.getFieldId().substring(field.getFieldId().indexOf('_')+1) :
                            field.getFieldId().toUpperCase());
                }
                request.setAttribute("datafld",dataFdID);
            }

            String currentRow = StringUtils.isBlank(field.getRowNum()) ? "-9999" : field.getRowNum();

            if (!currentRow.equalsIgnoreCase(row)) {
                if (first) {
                    first = false;  %>
<tr id='<%= trId %>' name='<%= trId %>' data-dti-layer-id="<%=trId%>" data-dti-field-row-num="<%=currentRow%>" onPropertyChange="baseOnPropertyChange()">
    <%          } else { %>
</tr>
<%              if (isAutoHideTR) { %>
<script type="text/javascript">
    $("tr[data-dti-layer-id='<%=trId%>'][data-dti-field-row-num='<%=row%>']").addClass("dti-autoHideTR");
</script>
<%              } %>
<tr id='<%= trId %>' name='<%= trId %>' data-dti-layer-id="<%=trId%>" data-dti-field-row-num="<%=currentRow%>" onPropertyChange="baseOnPropertyChange()">
    <%          }
        row = currentRow;
        isAutoHideTR = true;
    }
        if (isAutoHideTR && field.getIsVisible()) {
            isAutoHideTR = false;
        }
    %>
    <jsp:include page="compiledTagFactory.jsp" ></jsp:include>
    <%
        }
        if (isGridBased) {
            request.removeAttribute("datafld");
        }

        if (!first) {   //If atleast one <tr> is rendered, then render the closing <tr> tag
    %>
</tr>
<%
    if (isAutoHideTR) {
%>
<script type="text/javascript">
    $("tr[data-dti-layer-id='<%=trId%>'][data-dti-field-row-num='<%=row%>']").addClass("dti-autoHideTR");
</script>
<%          }
}
}

    }

if(!useJqxGrid) {
  if (!excludeAllLayers) {

    if (isCWBOverride) {
       %>
        <tr id="CWBHeaderRow" style="<%= trStyle %>">
            <td></td>
            <td align="left"><span class="oasis_formlabel" id="baseColumnLabel">Base</span></td>
            <td align="left"><span class="oasis_formlabel" id="custColumnLabel">Custom Override</span></td>
            <td align="left" style="<%=hideSetToNull?"display:none":""%>">
                <span class="oasis_formlabel">Set To Null</span>
            </td>
        </tr>
       <%
    }


    it = fieldsMap.getLayers().iterator();
    while (it.hasNext()) {
      WebLayer layer = (WebLayer) it.next();
      // By default, all layers are displayed
      boolean displayLayer = true;

      if (!StringUtils.isBlank(includeLayersWithPrefix) || !StringUtils.isBlank(includeLayerIds)) {
        // If either includeLayersWithPrefix or includeLayerIds is specified, all layers are hidden By default
        displayLayer = false;

        if (!StringUtils.isBlank(includeLayersWithPrefix) && layer.getLayerId().startsWith(includeLayersWithPrefix)) {
          // Include layers with matching prefix
          displayLayer = true;
        }
      }

      if (excludeGridHeaderLayers) {
        if (!StringUtils.isBlank(gridHeaderSuffix) && layer.getLayerId().endsWith(gridHeaderSuffix)) {
          displayLayer = false;
        }
      }
      if (!StringUtils.isBlank(includeLayerIds)) {
        String layerCommaId = "," + layer.getLayerId() + ",";
        if (includeLayerIds.indexOf(layerCommaId) >= 0) {
          displayLayer = true;
        }
      } else if (!StringUtils.isBlank(excludeLayerIds)) {
        String layerCommaId = "," + layer.getLayerId() + ",";
        if (excludeLayerIds.indexOf(layerCommaId) >= 0) {
          displayLayer = false;
        }
      }

        // If the layer is not hidden, then there's visible layer.
        if (displayLayer && !layer.isHidden()) {
            hasVisibleLayer = true;
        }

        if (displayLayer) {
        String layerId = layer.getLayerId();
        if (StringUtils.isBlank(headerText)) {
            headerText = layer.getDescription();
        }

        row = "-999999";
        first = true;
        Iterator fieldIt = fieldsMap.getLayerFields(layer.getLayerId()).iterator();
        while (fieldIt.hasNext()) {
          trStyle = layerDefaultDisplayStyle;
          OasisFormField field = (OasisFormField) fieldIt.next();

          request.setAttribute("field", field);   // Used in compiledTagFactory
          if (isGridBased) {
              String layerDataFdID = "";
              if (!StringUtils.isBlank(fieldXmlMapFileName)) { /* If have a mapping file, use it */
                layerDataFdID = "C" + fieldXmlMap.get(field.getFieldId());
              } else {
                layerDataFdID = "C" + (removeFieldPrefix ?
                                        field.getFieldId().substring(field.getFieldId().indexOf('_')+1) :
                                        field.getFieldId().toUpperCase());
              }
              request.setAttribute("datafld", layerDataFdID);
          }


          boolean isFirstFieldOnRow = false;
          if (!field.getRowNum().equalsIgnoreCase(row) && field.getIsVisible()) {
            isFirstFieldOnRow = true;
            if (first) {
              first = false; %>
              <tr id="<%= layer.getLayerId()%>" style="<%= trStyle %>" onPropertyChange="baseOnPropertyChange()">
            <%} else {
                if (isAutoHideTR) { %>
                  <div name="autoHideTR" class="hide" style="display:none"></div>
  <%            } %>
              </tr>
              <tr id="<%= layer.getLayerId()%>" style="<%= trStyle %>" onPropertyChange="baseOnPropertyChange()">
            <%}
              row = field.getRowNum();
              isAutoHideTR = true;
          }else{
            if (isCWBOverride) {
                //remove labels for custom fields
                request.setAttribute(field.getFieldId() + "showLabel", "false");
            }
          }
          if (isAutoHideTR && field.getIsVisible()) {
              isAutoHideTR = false;
          }
            %>
          <jsp:include page="compiledTagFactory.jsp" ></jsp:include>
    <%
        if (isCWBOverride && isFirstFieldOnRow && field.getIsVisible()) {
    %>
    <script type="text/javascript">
        // Disable base fields in Cust WebWB
        enableDisableField(getObject("<%=field.getFieldId()%>"), true);
    </script>
    <%
          }
        }
        if (isGridBased) {
            request.removeAttribute("datafld");
        }
        if (!first) {   //If atleast one <tr> is rendered, then render the closing <tr> tag
            if (isAutoHideTR) { %>
              <div name="autoHideTR" class="hide" style="display:none"></div>
  <%        } %>
          </tr>
  <%    }
      }
    }
  }
      } else {
      if (!excludeAllLayers) {

          if (isCWBOverride) {
  %>
<tr id="CWBHeaderRow" style="<%= trStyle %>">
    <td></td>
    <td align="left"><span class="oasis_formlabel" id="baseColumnLabel">Base</span></td>
    <td align="left"><span class="oasis_formlabel" id="custColumnLabel">Custom Override</span></td>
    <td align="left" style="<%=hideSetToNull?"display:none":""%>">
        <span class="oasis_formlabel">Set To Null</span>
    </td>
</tr>
<%
    }


    it = fieldsMap.getLayers().iterator();
    while (it.hasNext()) {
        WebLayer layer = (WebLayer) it.next();
        // By default, all layers are displayed
        boolean displayLayer = true;

        if (!StringUtils.isBlank(includeLayersWithPrefix) || !StringUtils.isBlank(includeLayerIds)) {
            // If either includeLayersWithPrefix or includeLayerIds is specified, all layers are hidden By default
            displayLayer = false;

            if (!StringUtils.isBlank(includeLayersWithPrefix) && layer.getLayerId().startsWith(includeLayersWithPrefix)) {
                // Include layers with matching prefix
                displayLayer = true;
            }
        }

        if (excludeGridHeaderLayers) {
            if (!StringUtils.isBlank(gridHeaderSuffix) && layer.getLayerId().endsWith(gridHeaderSuffix)) {
                displayLayer = false;
            }
        }
        if (!StringUtils.isBlank(includeLayerIds)) {
            String layerCommaId = "," + layer.getLayerId() + ",";
            if (includeLayerIds.indexOf(layerCommaId) >= 0) {
                displayLayer = true;
            }
        } else if (!StringUtils.isBlank(excludeLayerIds)) {
            String layerCommaId = "," + layer.getLayerId() + ",";
            if (excludeLayerIds.indexOf(layerCommaId) >= 0) {
                displayLayer = false;
            }
        }

        // If the layer is not hidden, then there's visible layer.
        if (displayLayer && !layer.isHidden()) {
            hasVisibleLayer = true;
        }

        if (displayLayer) {
            String layerId = layer.getLayerId();
            if (StringUtils.isBlank(headerText)) {
                headerText = layer.getDescription();
            }

            row = "-999999";
            first = true;
            Iterator fieldIt = fieldsMap.getLayerFields(layer.getLayerId()).iterator();
            while (fieldIt.hasNext()) {
                trStyle = layerDefaultDisplayStyle;
                OasisFormField field = (OasisFormField) fieldIt.next();

                request.setAttribute("field", field);   // Used in compiledTagFactory
                if (isGridBased) {
                    String layerDataFdID = "";
                    if (!StringUtils.isBlank(fieldXmlMapFileName)) { /* If have a mapping file, use it */
                        layerDataFdID = "C" + fieldXmlMap.get(field.getFieldId());
                    } else {
                        layerDataFdID = "C" + (removeFieldPrefix ?
                                field.getFieldId().substring(field.getFieldId().indexOf('_')+1) :
                                field.getFieldId().toUpperCase());
                    }
                    request.setAttribute("datafld", layerDataFdID);
                }


                boolean isFirstFieldOnRow = false;
                String currentRow = StringUtils.isBlank(field.getRowNum()) ? "-9999" : field.getRowNum();
                if (!currentRow.equalsIgnoreCase(row) && field.getIsVisible()) {
                    isFirstFieldOnRow = true;
                    if (first) {
                        first = false; %>
<tr id="<%= layer.getLayerId()%>" name="<%= layer.getLayerId()%>" class="<%=isLayerVisibleByDefault ? "" : "dti-hide"%>" data-dti-layer-id="<%=layer.getLayerId()%>" data-dti-field-row-num="<%=currentRow%>" onPropertyChange="baseOnPropertyChange()">
    <%} else {
        if (isAutoHideTR) { %>
    <script type="text/javascript">
        $("tr[data-dti-layer-id='<%=layer.getLayerId()%>'][data-dti-field-row-num='<%=row%>']").addClass("dti-autoHideTR");
    </script>
    <%            } %>
</tr>
<tr id="<%= layer.getLayerId()%>" name="<%= layer.getLayerId()%>" class="<%=isLayerVisibleByDefault ? "" : "dti-hide"%>" data-dti-layer-id="<%=layer.getLayerId()%>" data-dti-field-row-num="<%=currentRow%>" onPropertyChange="baseOnPropertyChange()">
    <%}
        row = currentRow;
        isAutoHideTR = true;
    }else{
        if (isCWBOverride) {
            //remove labels for custom fields
            request.setAttribute(field.getFieldId() + "showLabel", "false");
        }
    }
        if (isAutoHideTR && field.getIsVisible()) {
            isAutoHideTR = false;
        }
    %>
    <jsp:include page="compiledTagFactory.jsp" ></jsp:include>
    <%
        if (isCWBOverride && isFirstFieldOnRow && field.getIsVisible()) {
    %>
    <script type="text/javascript">
        // Disable base fields in Cust WebWB
        enableDisableField(getObject("<%=field.getFieldId()%>"), true);
    </script>
    <%
            }
        }
        if (isGridBased) {
            request.removeAttribute("datafld");
        }
        if (!first) {   //If atleast one <tr> is rendered, then render the closing <tr> tag
            if (isAutoHideTR) {
    %>
    <script type="text/javascript">
        $("tr[data-dti-layer-id='<%=layer.getLayerId()%>'][data-dti-field-row-num='<%=row%>']").addClass("dti-autoHideTR");
    </script>
    <%        } %>
</tr>
<%    }
}
}
}
  }

  if (!StringUtils.isBlank(actionItemGroupId) && actionItemGroupLocation.equalsIgnoreCase("bottom")) {
  %>
    <tr>
        <td colspan="12" align="<%=actionItemGroupAlign%>">
            <oweb:actionGroup actionItemGroupId="<%= actionItemGroupId %>"
                              layoutDirection="<%=actionItemGroupIdLayout%>"
                              cssColorScheme="<%=actionItemGroupIdCss%>"
                              cssWidthInPX="<%=actionItemGroupIdCssWidthInPX%>"/>
        </td>
    </tr>
<% } %>
