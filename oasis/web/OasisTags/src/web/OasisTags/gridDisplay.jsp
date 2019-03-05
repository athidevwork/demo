<%--
  Description:

  Write the Form Fields and Layer fields from the OasisFields collection.

  The following JSTL variables can be used to alter this behavior:

    "gridId" - default to "", it is used when multiple grids exist to correctly related the grid id defined in the JSP
       to the particular set of data.  This will typically be used in conjunction with setting the dataBean
       and gridHeaderBean java variables in the JSP for the additional grids.

   "gridSortable" - true / false, default to true, it controls if sorting on a grid is allowed.
      for example: <c:set var = "gridSortable" value="false"> will disable the grid's sorting ability.

 --%>

<%@ page import="java.util.StringTokenizer"%>
<%@ page import="dti.oasis.recordset.RecordComparator"%>
<%@ page import="dti.oasis.recordset.SortOrder"%>
<%@ page import="dti.oasis.converter.Converter"%>
<%@ page import="dti.oasis.app.ConfigurationException"%>
<%@ page import="dti.oasis.converter.ConverterFactory"%>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.tags.WebLayer" %>
<%@ page import="java.util.List" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.tags.OasisFormField" %>
<%@ page import="dti.oasis.util.DisconnectedResultSet" %>
<%@ page import="dti.oasis.busobjs.YesNoFlag" %>
<%@ page import="java.util.Comparator" %>
<%--
  Description:  Encapsulate all grid related functionality to provide common logic

  Author: jmpotosky
  Date: Jan 26, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  05/16/2007       sxm         Added Excel attributes
  10/04/2007       fcb         trim() added when parsing fieldName.
  10/10/2007       fcb         Fixed problem with the way gridSortable is set.
  04/16/2009       fcb/wre     Added brackets after set and before remove variables
                               in order to create different scopes for the variables
                               created in the java class generated from jsp when
                               gridDisplay.jsp is included multiple times on the page.
  08/05/2009       Fred        Added showRowCntOnePage attribute
  01/22/2010       kshen       Add selectable property.
  03/22/2010       James       Issue#105489 The latest change made in gridDisplay.jsp is causing
                               two problems in ePolicy:  1) Maintain Coverage page got crashed when
                               page reloaded with validation error  2) The order of rows in grid
                               was wrong when the page reloaded with validation error. Therefore,
                               the row number in error messages was not consistent with the row
                               order in the grid.
  12/29/2012       kshen       Added codes to support sorting DisconnectedResultSet records.
  12/30/2016       dpang       181349 - Change to support sorting DisconnectedResultSet records for grid without grid xml file.
                                        Add isGridSortOrderConfigured to check if grid sort order is configured.
  07/14/2017       Elvin       Issue 186453: set divGrid width to 99%
  12/21/2017       jdingle     Issue 190245: Add try/catch around call to getColNum so that page can continue to load.
  01/12/2018       kshen       Grid replacement. Changed to set jqx grid height to default height if grid height setting
                               is empty in WebWB or grid height setting is smaller than default hegight.
  1/22/2018        kshen       190939. Correct the codes about getting default jqxGrid height.
  11/13/2018       wreeder     196147 - Support deferredLoadDataProcess, virtualPaging and virtualScrolling
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<c:if test="${empty gridSortable }">
    <c:set var="gridSortable" value="true"></c:set>
</c:if>

<c:if test="${empty gridId }">
    <c:set var="gridId" value=""></c:set>
</c:if>

<%
    {
        String gridId = (String) pageContext.findAttribute("gridId");
        if (StringUtils.isBlank(gridId)) {
            gridId = (String) pageContext.getRequest().getAttribute("gridDisplayGridId");
        }
        String deferredLoadDataProcess = (String) pageContext.findAttribute("deferredLoadDataProcess");
        boolean virtualPaging = YesNoFlag.getInstance((String) pageContext.findAttribute("virtualPaging")).booleanValue();
        boolean virtualScrolling = YesNoFlag.getInstance((String) pageContext.findAttribute("virtualScrolling")).booleanValue();
        String girdSortOrder = null;
        String gridHeight = ApplicationContext.getInstance().getProperty("grid.default.height");
        String gridHolderDivWidth = ApplicationContext.getInstance().getProperty("grid.default.container.width");
        String gridHolderDivHeight = null;
        String gridHolderDivDefaultHeight = ApplicationContext.getInstance().getProperty("grid.default.container.height");
        String jqxGridHolderDivDefaultHeight = ApplicationContext.getInstance().getProperty("jqxGrid.default.container.height");
        String gridPageSize = ApplicationContext.getInstance().getProperty("grid.default.page.size");
        boolean isGridSortOrderConfigured = false;

        // Check if grid sort order and grid dimensions are configed on page layer.
        // Check all the layers, try to find a layer field whose id matches a column name in dataBean.
        WebLayer gridHeaderLayer = null;
        List layerList = fieldsMap.getLayers();
        if (gridHeaderBean.hadGridHeaderLayerId()) {
            //if layer Id is specified in grid header bean, use it
            if (fieldsMap.hasLayer(gridHeaderBean.getGridHeaderLayerId())) {
                gridHeaderLayer = fieldsMap.getLayerFieldsMap(gridHeaderBean.getGridHeaderLayerId());
            } else {
                throw new IllegalStateException("Can not find layer " + gridHeaderBean.getGridHeaderLayerId());
            }
        }
        if (gridHeaderLayer == null) {
            for (int i = 0; i < layerList.size(); i++) {
                WebLayer webLayer = (WebLayer) layerList.get(i);
                List layerFieldList = fieldsMap.getLayerFields(webLayer.getLayerId());
                for (int j = 0; j < layerFieldList.size(); j++) {
                    OasisFormField field = (OasisFormField) layerFieldList.get(j);
                    for (int k = 0; k < dataBean.getColumnCount(); k++) {
                        String columnName = dataBean.getColumnName(k + 1);
                        if ( field.getFieldId().toUpperCase().equalsIgnoreCase(columnName + gridHeaderBean.getGridHeaderFieldnameSuffix())) {
                            gridHeaderLayer = webLayer;
                            break;
                        }
                    }
                    if (gridHeaderLayer != null) {
                        break;
                    }
                }
                if (gridHeaderLayer != null) {
                    break;
                }
            }
        }
        if (gridHeaderLayer == null) {
            // For grid which is still using xml header file
            // Try to find a layer field whose id is defined as grid header.
            for (int i = 0; i < layerList.size(); i++) {
                WebLayer webLayer = (WebLayer) layerList.get(i);
                List layerFieldList = fieldsMap.getLayerFields(webLayer.getLayerId());
                for (int j = 0; j < layerFieldList.size(); j++) {
                    OasisFormField field = (OasisFormField) layerFieldList.get(j);
                    if (gridHeaderBean.hasHeader(field.getFieldId())) {
                        gridHeaderLayer = webLayer;
                        break;
                    }
                }
                if (gridHeaderLayer != null) {
                    break;
                }
            }
        }

        if (gridHeaderLayer != null) {
            girdSortOrder = gridHeaderLayer.getGridSortOrder();
            if (!StringUtils.isBlank(gridHeaderLayer.getGridContainerWidth())) {
                gridHolderDivWidth = gridHeaderLayer.getGridContainerWidth();
                if (StringUtils.isNumeric(gridHolderDivWidth)) {
                    gridHolderDivWidth = gridHolderDivWidth + "px";
                }
            }
            if (!StringUtils.isBlank(gridHeaderLayer.getGridPageSize())) {
                gridPageSize = gridHeaderLayer.getGridPageSize();
            }
            if (!StringUtils.isBlank(gridHeaderLayer.getGridHeight())
                    && !StringUtils.isBlank(gridHeaderLayer.getGridContainerHeight())) {
                //both have values
                gridHeight = gridHeaderLayer.getGridHeight();
                gridHolderDivHeight = gridHeaderLayer.getGridContainerHeight();
            } else if (!StringUtils.isBlank(gridHeaderLayer.getGridHeight())
                    && StringUtils.isBlank(gridHeaderLayer.getGridContainerHeight())) {
                //only grid height is set, try to calculate grid container height
                gridHeight = gridHeaderLayer.getGridHeight();
                if (gridHeaderLayer.getGridHeight().toUpperCase().endsWith("PX")) {
                    String gridHeightSize = gridHeaderLayer.getGridHeight().substring(0,
                            gridHeaderLayer.getGridHeight().length() - 2);
                    if (StringUtils.isNumeric(gridHeightSize)) {
                        int gridContainerHeightSize = Integer.parseInt(gridHeightSize) + 30;
                        gridHolderDivHeight = gridContainerHeightSize + "px";
                    }
                }
            } else if (StringUtils.isBlank(gridHeaderLayer.getGridHeight())
                    && !StringUtils.isBlank(gridHeaderLayer.getGridContainerHeight())) {
                //only grid container height is set, try to calculate grid height
                gridHolderDivHeight = gridHeaderLayer.getGridContainerHeight();
                if (gridHeaderLayer.getGridContainerHeight().toUpperCase().endsWith("PX")) {
                    String gridContainerHeightSize = gridHeaderLayer.getGridContainerHeight().substring(0,
                            gridHeaderLayer.getGridContainerHeight().length() - 2);
                    if (StringUtils.isNumeric(gridContainerHeightSize)) {
                        int gridHeightSize = Integer.parseInt(gridContainerHeightSize) - 30;
                        gridHeight = gridHeightSize + "px";
                    }
                }
            }
        }
        // Issue#133818 adjust height setting
        String heightString = gridHeight;
        if (gridHeight.toUpperCase().endsWith("PX")) {
            heightString = gridHeight.substring(0, gridHeight.length() - 2);
        }
        try {
            int height = Integer.parseInt(heightString);
            // minus 13 for padding on grid div
            gridHeight = "" + (height - 13) + "px";
        } catch (NumberFormatException e) {
            // parse failed, do nothing
        }

        // If grid holder height setting is empty, set the grid holder height to the default grid height.
        if (StringUtils.isBlank(gridHolderDivHeight)) {
            if (dti.oasis.tags.OasisTagHelper.isUseJqxGrid(pageContext)) {
                gridHolderDivHeight = jqxGridHolderDivDefaultHeight;
            } else {
                gridHolderDivHeight = gridHolderDivDefaultHeight;
            }
        }

        heightString = gridHolderDivHeight;
        if (gridHolderDivHeight.toUpperCase().endsWith("PX")) {
            heightString = gridHolderDivHeight.substring(0, gridHolderDivHeight.length() - 2);
        }
        try {
            int height = Integer.parseInt(heightString);

            // The height of jqxGrid should not be less than the default jqx grid height setting.
            // TODO Also adjust jqxGrid height with the current grid height setting in webwb, and resize iFrame, divpoup.
            if (dti.oasis.tags.OasisTagHelper.isUseJqxGrid(pageContext)) {
                if (jqxGridHolderDivDefaultHeight.toUpperCase().endsWith("PX")) {
                    jqxGridHolderDivDefaultHeight = jqxGridHolderDivDefaultHeight.substring(0, jqxGridHolderDivDefaultHeight.length() - 2);
                }
                int jqxGridHolderDivDefaultHeightInt = Integer.parseInt(jqxGridHolderDivDefaultHeight);

                if (height < jqxGridHolderDivDefaultHeightInt) {
                    height = jqxGridHolderDivDefaultHeightInt;
                }
            }

            // minus 2 for border on grid holder div
            gridHolderDivHeight = "" + (height - 2) + "px";
        } catch (NumberFormatException e) {
            // parse failed, do nothing
        }


        if (!StringUtils.isBlank(girdSortOrder) && !dataBean.isDataFromClient()) {
            int sortCount = 0;
            Comparator rc = null;
            String sortString;

            // Parse by comma to split the comma-delimited converter list
            StringTokenizer commaList = new StringTokenizer(girdSortOrder, ",");
            while (commaList.hasMoreTokens()) {
                 sortString = commaList.nextToken();

                // Parse the sort information by [ to get the field name
                StringTokenizer tok = new StringTokenizer(sortString, "[");

                String fieldName;
                if (tok.countTokens() > 0) {
                    // Assume first token is the fieldName
                    fieldName = tok.nextToken().trim();
                    sortCount++;
                } else {
                    throw new ConfigurationException("Invalid fieldName format for grid sorting.");
                }

                // Determine sort order, default ASC
                SortOrder sortOrder = SortOrder.ASC;
                if (sortString.indexOf("SORT_ORD_DESC") > -1) {
                    sortOrder = SortOrder.DESC;
                }

                // Determine null equivalent, default NULL's greater
                boolean nullsAreGreater = true;
                if (sortString.indexOf("NULLS_LOWER") > -1) {
                    nullsAreGreater = false;
                }

                // Check for any converters specified
                Converter converter = null;
                if (sortString.indexOf("[CONVERTER=") > -1) {
                    String converterName = sortString.substring(sortString.indexOf("[CONVERTER=") + 11);
                    converterName = converterName.substring(0, converterName.length() - 1);
                    ConverterFactory converterFactory = new ConverterFactory();
                    converter = converterFactory.getConverterByName(converterName);
                }

                if (rc == null) {
                    if (dataBean instanceof DisconnectedResultSet) {
                        if (gridHeaderBean.hasHeader(fieldName)) {
                            rc = new DisconnectedResultSet.DataRowComparator(gridHeaderBean.getHeaderIndex(fieldName), nullsAreGreater, sortOrder, converter);
                        } else {
                            //If grid xml doesn't exist, try to get dataColumnIndex from dataBean
                            try {
                                rc = new DisconnectedResultSet.DataRowComparator(((DisconnectedResultSet) dataBean).getColNum(fieldName), nullsAreGreater, sortOrder, converter);
                            } catch (Exception e) {
                                // ignore error. getColNum can fail for some grid xml based pages.
                            }
                        }
                    } else {
                        // All the pieces exist so create the RecordComparator and perform the sort
                        rc = new RecordComparator(fieldName, nullsAreGreater, sortOrder, converter);
                    }

                } else {
                    if (dataBean instanceof DisconnectedResultSet) {
                        if (gridHeaderBean.hasHeader(fieldName)) {
                            ((DisconnectedResultSet.DataRowComparator)rc).addDataRowColumnComparator(gridHeaderBean.getHeaderIndex(fieldName), nullsAreGreater, sortOrder, converter);
                        } else {
                            //If grid xml doesn't exist, try to get dataColumnIndex from dataBean
                            try {
                                ((DisconnectedResultSet.DataRowComparator)rc).addDataRowColumnComparator(((DisconnectedResultSet) dataBean).getColNum(fieldName), nullsAreGreater, sortOrder, converter);
                            } catch (Exception e) {
                                // ignore error. getColNum can fail for some grid xml based pages.
                            }
                        }
                    } else {
                        // Add additional comparator
                        ((RecordComparator)rc).addFieldComparator(fieldName, nullsAreGreater, sortOrder, converter);
                    }
                }
            }
            if (rc != null) {
                isGridSortOrderConfigured = sortCount > 0;
                dataBean.sort(rc);
            }
        }

        // Setup the defaults
        boolean cacheResultSetValue = false;
        boolean saveAsExcelCsv = false;
        boolean saveAsExcelHtml = false;
        boolean gridSortable = true;
        boolean showRowCntOnePage = true; //such a nice feature to eOASIS, default to true unless dev overwrites: 104993 
        boolean selectable = true;

        String gridWidth = dti.oasis.tags.OasisTagHelper.isUseJqxGrid(pageContext) ? "100%" : "99%";
%>

<c:if test="${cacheResultSet==true}">
    <%
        // updated value if it has been specifically set
        cacheResultSetValue = true;
    %>
</c:if>
<c:if test="${saveAsExcelCsv==true}">
    <% saveAsExcelCsv = true; %>
</c:if>
<c:if test="${saveAsExcelHtml==true}">
    <% saveAsExcelHtml = true; %>
</c:if>

<c:if test= "${gridSortable == false}" >
    <% gridSortable = false; %>
</c:if>

<c:if test="${showRowCntOnePage == false}">
    <% showRowCntOnePage =false;  %>
</c:if>

<c:if test="${selectable == false}">
    <% selectable = false; %>
</c:if>

<DIV class="divGridHolder" style="<%=new StringBuffer("height:").append(gridHolderDivHeight).append(";width:").append(gridHolderDivWidth).toString()%>">
      <oweb:grid formName=""
                 gridId="<%=gridId%>"
                 data="<%=dataBean%>"
                 header="<%=gridHeaderBean%>"
                 cacheResultset="<%=cacheResultSetValue%>"
                 deferredLoadDataProcess="<%=deferredLoadDataProcess%>"
                 virtualPaging="<%=virtualPaging%>"
                 virtualScrolling="<%=virtualScrolling%>"
                 pageSize="<%=gridPageSize%>"
                 gridWidth="<%=gridWidth%>"
                 gridHeight="<%=gridHeight%>"
                 tableWidth="100%"
                 sortable="<%=gridSortable%>"
                 selectable="<%=selectable%>"
                 gridInsert="true"
                 showRowCntOnePage="<%=showRowCntOnePage%>"
                 saveAsExcelCsv="<%=saveAsExcelCsv%>" dispositionTypeExcelCsvFile="attachment"
                 saveAsExcelHtml="<%=saveAsExcelHtml%>" dispositionTypeExcelHtmlFile="attachment"/>
</DIV>
<script type="text/javascript">
    if (OBR_GridIdInRenderingOrder) {
        OBR_GridIdInRenderingOrder += "," + "<%= gridId %>"
    } else {
        OBR_GridIdInRenderingOrder += "<%= gridId %>"
    }

    var isGridSortOrderConfigured = <%=isGridSortOrderConfigured%>
    $("div.divGrid").last().hide();
</script>
<%
    }
%>

<c:remove var="gridSortable"/>
<c:remove var="gridId"/>
<c:remove var="cacheResultSet"/>
<c:remove var="deferredLoadDataProcess"/>
<c:remove var="virtualPaging"/>
<c:remove var="virtualScrolling"/>
