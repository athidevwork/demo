<%--
  Description:   view rating log jsp

  Author: rlli
  Date: June 28, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<c:set var="isForDivPopup" value="true"></c:set>

<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript" src="js/viewRatingLog.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>

<style type="text/css">
    TABLE.clsGrid a* {
        text-align:left;
        font-family: courier, sans-serif;
    }
</style>

<form name="ratingLogList" action="viewRatingLog.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>

    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <c:set var="policyHeaderDisplayMode" value="invisible"/>
    <tr>
        <td colspan=8 align=center>
            <%@ include file="/policymgr/policyHeader.jsp" %>
        </td>
    </tr>
    <tr>
        <td align=center>
            <table class="table" cellpadding=0 cellspacing=0 width=98%>
                <tr>
                    <td align=center>
                        <fmt:message key="pm.viewRatingLog.ratingLogFilter.header" var="ratingLogFilterHeader" scope="request"/>
                        <% String ratingLogFilterHeader = (String) request.getAttribute("ratingLogFilterHeader"); %>
                        <jsp:include page="/core/compiledFormFields.jsp">
                            <jsp:param name="headerText" value="<%=  ratingLogFilterHeader %>" />
                            <jsp:param name="divId" value="ratingLogFilter" />
                            <jsp:param name="isGridBased" value="false" />
                            <jsp:param name="excludeAllLayers" value="true" />
                        </jsp:include>
                    </td>

                </tr>

                <c:if test="${dataBean.rowCount!=0}">
                    <tr>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                        <td colspan="6" align=center>
                            <c:set var="gridDisplayFormName" value="ratingLogList" scope="request"/>
                            <c:set var="gridDisplayGridId" value="ratingLogListGrid" scope="request"/>
                            <c:set var="datasrc" value="#ratingLogListGrid1" scope="request"/>
                            <c:set var="cacheResultSet" value="false"/>
                            <%@ include file="/pmcore/gridDisplay.jsp" %>
                        </td>
                    </tr>

                </c:if>
                <tr>
                    <td colspan="6" align=center>
                        <oweb:actionGroup actionItemGroupId="PM_VIEW_RATE_LOG_AIG"/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>