<%--
  Description:

  Write the Form Fields and Layer fields from the OasisFields collection.

  The following JSTL variables can be used to alter this behavior:

    "gridId" - default to "", it is used when multiple grids exist to correctly related the grid id defined in the JSP
       to the particular set of data.  This will typically be used in conjunction with setting the dataBean
       and gridHeaderBean java variables in the JSP for the additional grids.

    "gridSizeFieldIdPrefix" - default to "", it is used to associate the alternate fieldIds configured in workbench
       with a specific grid. this allows a grid have its own configuration on a multi-grid page.
       for example, a calling jsp page has a parent and a child grids, before calling gridDisplay.jsp for
       the child grid,set the prefix to child: <c:set var="gridSizeFieldIdPrefix" value="child_">
       in work bench for th child grid, if the field gridHeight with the alternate field id of child_gridHeight
       is configured, the child grid will use the default value from this field to diplay.
       
   "gridSortable" - true / false, default to true, it controls if sorting on a grid is allowed.
      for example: <c:set var = "gridSortable" value="false"> will disable the grid's sorting ability.

 --%>

<%--
  Description:  Encapsulate all grid related functionality to provide common logic

  Author: jmpotosky
  Date: Jan 26, 2007

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  05/16/2007       sxm         Added Excel attributes
  10/04/2007       fcb         trim() added when parsing fieldName.
  10/10/2007       fcb         Fixed problem with the way gridSortable is set.
  09/10/2009       fcb         98097: oweb:grid - added logic for showRowCntOnePage
  01/27/2010       James       Issue#100713 merge all gridDisplay.jsp into one
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ include file="/core/gridDisplay.jsp" %>