<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%--
  Description:

  Author: rlli
  Date: Nov 21, 2007
  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/16/2010       dzhang      Issue 114336 - Used separateLimitListGridDataBean instead of databean.
  -----------------------------------------------------------------------------

  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
 <%@ include file="/core/headerpopup.jsp" %>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="policyHeader" class="dti.pm.policymgr.PolicyHeader" scope="request"/>
<jsp:useBean id="separateLimitListGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="separateLimitListGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<form action="javascript:void();" method=post name="dummyForm">
    <c:if test="${separateLimitListGridDataBean != null}">
    <tr>
        <td align=center><br/>
                    <c:set var="gridDisplayFormName" value="separateLimitList" scope="request"/>
                    <c:set var="gridDisplayGridId" value="separateLimitListGrid" scope="request"/>
                    <c:set var="gridSizeFieldIdPrefix" value="detail_"/>
                    <% XMLGridHeader gridHeaderBean = separateLimitListGridHeaderBean;
                        BaseResultSet dataBean= separateLimitListGridDataBean;
                    %>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
        </td>
    </tr>
    </c:if>
<jsp:include page="/core/footerpopup.jsp"/>
