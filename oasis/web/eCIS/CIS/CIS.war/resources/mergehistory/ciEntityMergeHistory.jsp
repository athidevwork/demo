<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.tags.WebLayer" %>
<%@ page import="org.apache.struts.Globals" %>
<%@ page import="org.apache.struts.taglib.html.Constants" %>
<%@ page import="dti.oasis.messagemgr.Message" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="java.util.Iterator" %>
<%@ page language="java" %>
<%--
  Description:

  Author: ldong
  Date: Mar 19, 2008


  Revision Date    Revised By  Description
  ---------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance

  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<script type="text/javascript" src="js/ciEntityMergeHistory.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<form name="maintainEntityMergeHistoryForm" action="ciMaintainEntityMergeHistory.do" method=post>
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <input type="hidden" name="entityMergeHistoryId" value="">
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>
    <tr>
        <td colspan="6" align=center>
            <oweb:panel panelContentId="panelContentForList" panelTitleLayerId="Entity_Merge_Grid_List">
                <tr>
                    <td>
                    <c:set var="gridDisplayFormName" value="EntityMergeHistoryForm" scope="request"/>
                    <c:set var="gridDisplayGridId" value="historyListGrid" scope="request"/>
                    <c:set var="datasrc" value="#historyListGrid1" scope="request"/>
                    <c:set var="cacheResultSet" value="false"/>
                    <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
</form>
<tr>
    <td align=center>
        <oweb:actionGroup actionItemGroupId="CI_MERGE_HISTORY_AIG" layoutDirection="horizontal"/>
    </td>
</tr>
<jsp:include page="/core/footerpopup.jsp"/>
