<%@ page import="dti.oasis.session.UserSessionManager" %>
<%@ page import="dti.pm.core.session.UserSessionIds" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="dti.pm.policymgr.struts.PolicyList" %>
<%@ page import="dti.pm.policymgr.struts.PolicyListElement" %>
<%--
  Description:

  Author: sxm

  Date: Aug 23, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/15/2010       wfu         111776: Replaced hardcode string with resource definition
  08/28/2017       wrong       187744: 1) Added a logic to update policy list in session.
                                       2) Modified function handleOnPageBack to add parameter policyTermHistoryId.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<c:set var="showPreviousRecordLink" value="false"></c:set>
<c:set var="showNextRecordLink" value="false"></c:set>
<c:set var="recordLocationDescription" value=""></c:set>
<%
    String prevPolicyNo = "";
    String prevTermId = "";
    String nextPolicyNo = "";
    String nextTermId = "";
    String recordLocationDescription = "";
    String policyTermHistoryId = "";

    PolicyList policyList = (PolicyList) UserSessionManager.getInstance().getUserSession().get(UserSessionIds.POLICY_LIST);
    if (policyList != null) {
        policyTermHistoryId = policyHeader.getPolicyTermHistoryId();
        String policyTermBaseHistoryId = policyHeader.getTermBaseRecordId();
        String policyNo = policyHeader.getPolicyNo();
        if (!policyList.has(policyTermHistoryId)) {
            PolicyListElement policyListElement = new PolicyListElement();
            policyListElement.setPolicyNo(policyNo);
            policyListElement.setPolicyTermHistoryId(policyTermHistoryId);
            policyList.add(policyListElement);
        }
        if (policyList.has(policyTermHistoryId)) {
            PolicyListElement policyListElement = policyList.get(policyTermHistoryId);
            int i = policyListElement.getListIndex();
            int size = policyList.getSize();
            recordLocationDescription = MessageManager.getInstance().formatMessage("pm.common.list.navigation.direction",new String[]{(i+1)+"",size+""});
            if (i > 0) {
                policyListElement = policyList.get(i - 1);
                prevPolicyNo = policyListElement.getPolicyNo();
                prevTermId = policyListElement.getPolicyTermHistoryId();
%>
                <c:set var="recordLocationDescription"><%=recordLocationDescription%></c:set>
                <c:set var="showPreviousRecordLink" value="true"></c:set>
                <script type="text/javascript">
                    var navigatePrevPolicyNo = "<%=prevPolicyNo%>";
                    var navigatePrevTermId = "<%=prevTermId%>";
                </script>
<%
            }
            if (i < size -1) {
                policyListElement = policyList.get(i + 1);
                nextPolicyNo = policyListElement.getPolicyNo();
                nextTermId = policyListElement.getPolicyTermHistoryId();
%>
                <c:set var="recordLocationDescription"><%=recordLocationDescription%></c:set>
                <c:set var="showNextRecordLink" value="true"></c:set>
                <script type="text/javascript">
                    var navigateNextPolicyNo = "<%=nextPolicyNo%>";
                    var navigateNextTermId = "<%=nextTermId%>";
                </script>
<%
            }
        }
        else {
            UserSessionManager.getInstance().getUserSession().remove(UserSessionIds.POLICY_LIST);
        }
    }
%>
<c:set var="recordLocationDescription"><%=recordLocationDescription%></c:set>

<c:set var="pageBackLink" value=""></c:set>
<c:if test="${not empty recordLocationDescription}">
    <c:set var="pageBackLink" value="Back to Policy List"></c:set>

    <script type="text/javascript">
        function handleOnPageBack() {
            var newURL = getAppPath() + "/policymgr/findPolicy.do";
            var policyTermHistoryId = "<%=policyTermHistoryId%>";

            showProcessingImgIndicator();

            setWindowLocation(newURL + "?process=returnToList&policyTermHistoryId=" + policyTermHistoryId);
        }
    </script>
</c:if>

