<%--
  Description: view all insured history.

  Author: yhyang
  Date: Noc 17, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  03/20/2017       eyin        180675 - Changed message tag for UI change.
  -----------------------------------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
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
<form name="insuredHistoryList" action="viewInsuredHistory.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <tr>
        <td colspan=8>
            <%
                if (pmUIStyle.equals("T")) {
            %>
            <oweb:message displayMessagesOnParent="true"/>
            <%
                }
            %>
            <%
                if (pmUIStyle.equals("B")) {
            %>
            <oweb:message/>
            <%
                }
            %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <oweb:panel panelTitleId="panelTitleIdForSearchRisk" panelContentId="panelContentIdForSearchRisk"
                        panelTitle="">
                <tr>
                    <td colspan="6" align=center><br/>
                        <c:set var="gridDisplayFormName" value="insuredHistoryList" scope="request"/>
                        <c:set var="gridDisplayGridId" value="insuredHistoryListGrid" scope="request"/>
                        <c:set var="gridSortable" value="false" scope="request"/>
                        <%@ include file="/pmcore/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
            <tr>
                <td colspan="6" align=center>
                    <oweb:actionGroup actionItemGroupId="PM_VIEW_INSURED_HIS_AIG"/>
                </td>
            </tr>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>