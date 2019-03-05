<%--
  Description:

  Author: Joe Shen
  Date: May 18, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/30/2011       wqfu        103810 - include policy header to populate field policy type.
  03/20/2017       eyin        180675 - Made change to get the correct parent window based on subFrameId for UI change.
  05/23/2017       lzhang      185079 - pass parameter when call getParentWindow()
  07/12/2017       kxiang      185483 - Modified to check if IFrameWindow is defined before being called.
  07/17/2018       cesar       193651 - check if selectComponentGrid1 is not undefined.
  08/13/2018       wrong       194999 - Added <oweb:message/> to display error message in page.
  11/13/2018       tyang       194100 - Add buildNumber Parameter
  -----------------------------------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<script type="text/javascript" src="js/selectComponent.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<script type="text/javascript">
   var subFrameId = '<%=request.getParameter("subFrameId")==null ? "" : request.getParameter("subFrameId")%>';
    var oParentWindow;
    if(isEmpty(subFrameId)){
        oParentWindow = getParentWindow(true);
    }else{
        oParentWindow = eval("getParentWindow(true).getIFrameWindow") ? getParentWindow(true).getIFrameWindow() : getParentWindow(true);
    }
</script>

<tr>
    <td colspan=8>
        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td><oweb:message/></td>
            </tr>
        </table>
    </td>
</tr>
<form action="" name ="selectComponentForm">
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
<!-- Display grid -->
    <tr>
        <td>
          <oweb:panel panelTitleId="panelTitleIdForComponent" panelContentId="panelContentIdForComponent" panelTitle="" >
        </td>
     </tr>
     <tr>
        <td colspan="6" align=center><br/>
            <c:set var="gridDisplayFormName" value="selectComponentForm" scope="request"/>
            <c:set var="gridDisplayGridId" value="selectComponentGrid" scope="request"/>
            <c:set var="cacheResultSet" value="false"/>
            <c:set var="selectable" value="true"/>
            <c:set var="gridSortable" value="false"/>
            <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
  </oweb:panel>
<%-- Display buttons --%>
    <tr>
        <td colspan="7" align="center">
             <oweb:actionGroup actionItemGroupId="PM_SELECT_COMP_AIG" />
        </td>
    </tr>

<script type="text/javascript" >
    /* if the grid is empty, open a warning dialog box */
    //todo: change way to check if it is empty
    if (selectComponentGrid1 != "undefined") {
        if (selectComponentGrid1.recordset.Fields.count <= 1) {
            handleError(getMessage("pm.addComponent.nodata.error"));
        }
    }
</script>

<jsp:include page="/core/footerpopup.jsp"/>
