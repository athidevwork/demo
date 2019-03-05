<%@ page language="java" %>
<%@ page import="dti.oasis.util.BaseResultSet" %>
<%@ page import="dti.oasis.tags.XMLGridHeader" %>
<%--
  Description: Opa Errors Details iframe

  Author: kshen
  Date: June 14, 2011


  Revision Date    Revised By  Description
  ---------------------------------------------------
  09/05/2011       ryzhao      124622 - For pages with multiple grids, update the name of data bean
                               and grid header bean for all but the first grid.
                               The name of data bean should be gridId + "DataBean".
                               The name of grid header bean should be gridId + "HeaderBean".
  ---------------------------------------------------
  (C) 2011 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<jsp:useBean id="opaScoreErrorLogGridDataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="opaScoreErrorLogGridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<c:set var="isForDivPopup" value="true"></c:set>
<c:set var="skipHeaderFooterContent" value="true"></c:set>
<%@ include file="/core/headerpopup.jsp" %>
<%@ include file="/pmcore/common.jsp" %>

<form name="opaErrorDetailForm" action="opaErrors.do" method=post>
    <%@ include file="/pmcore/commonFormHeader.jsp" %>
    <%
       BaseResultSet dataBean = opaScoreErrorLogGridDataBean;
       XMLGridHeader gridHeaderBean = opaScoreErrorLogGridHeaderBean;
    %>
    <tr>
        <td align=center>
            <tr>
                <td colspan="6" align=center>
                    <c:set var="gridDisplayFormName" value="scoringErrorForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="opaScoreErrorLogGrid" scope="request"/>
                    <c:set var="gridSizeFieldIdPrefix" value="opaScoreReq_"/>
                    <%@ include file="/pmcore/gridDisplay.jsp" %>
                </td>
            </tr>
        </td>
    </tr>
<jsp:include page="/core/footerpopup.jsp"/>