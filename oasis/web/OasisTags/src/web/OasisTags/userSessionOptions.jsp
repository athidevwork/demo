<%@ page import="dti.oasis.http.RequestIds" %>
<%@ page import="dti.oasis.struts.ActionHelper" %>
<%@ page import="dti.oasis.cachemgr.Cache" %>
<%@ page import="dti.oasis.cachemgr.CacheManager" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="dti.oasis.util.PageBean" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="dti.oasis.http.Module" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="dti.oasis.util.DatabaseUtils" %>
<%@ page import="dti.oasis.navigationmgr.NavigationManager" %>
<%@ page import="dti.oasis.util.DefaultPageDefLoadProcessor" %>
<%@ taglib prefix="html" uri="http://struts.apache.org/tags-html" %>
<%@ page language="java" %>
<%--
  Description:

  Author: kshen
  Date: 5/9/2017


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%
  Map<String, String[]> convertedPages = new HashMap<>();
  convertedPages.put("COMMON", new String[]{
          // Entity Select Search
          "dti.ci.entitysearch.struts.MaintainEntitySelectSearchAction",
          // Workflow Diary
          "dti.cs.struts.action.CSWorkflowDiary"
  });

  convertedPages.put("CS", new String[]{
          // Activity History (Work Center)
          "dti.cs.struts.action.CSWorkCenter",
          // My Workflow Queue
          "dti.cs.oaw.struts.MaintainOAWAction",
          // Renewal Candidate
          "dti.cs.renewalcandidatemgr.struts.MaintainRenewalCandidateAction",
          // Renewal Candidate / Risk List panel
          "dti.cs.renewalcandidatemgr.struts.MaintainRenewalCandidateRiskAction"
  });

  convertedPages.put("CIS", new String[]{
          // Entity Search
          "dti.ci.struts.action.CIEntitySearch",
          // Entity Glance
          "dti.ci.entityglancemgr.struts.MaintainEntityGlanceAction",
          "dti.ci.struts.action.CIEntityList",

          // Non-search pages
          "dti.ci.struts.action.CIEntityPersonModify",
          "dti.ci.struts.action.CIEntityOrgModify",
          "dti.ci.summarymgr.struts.MaintainSummaryAction"
  });

  convertedPages.put("CM", new String[]{
          // Find Claim
          "dti.cm.claimmgr.claimfindmgr.struts.ClaimFindAction",
          "dti.cm.claimmgr.claimfindmgr.struts.ClaimLogFindAction",
          "dti.cm.casemgr.casefindmgr.struts.CaseFindAction",
          "dti.cm.claimmgr.claimfindmgr.struts.ClaimFindAddlSQLAction",
          "dti.cm.cs.diary.struts.WorkflowDiaryAction"
  });

  convertedPages.put("PM", new String[]{
          "dti.pm.cs.diary.struts.WorkflowDiaryAction",
          "dti.pm.policymgr.struts.FindPolicyAction",
          "dti.pm.policymgr.applicationmgr.struts.ManageApplicationAction"
  });

  convertedPages.put("FM", new String[]{
          // Account / Find Account
          "dti.fm.accountmgr.struts.AccountListAction",
          // Find Account popup
          "dti.fm.accountmgr.struts.FindAccountAction",
          // Account / Inquiry / Full Inq - Account / All Transaction
          "dti.fm.fullinquirymgr.struts.ViewAllTransactionsForAccountAction",
          // Account / Inquiry / Full Inq - Account / Invoices
          "dti.fm.fullinquirymgr.struts.ViewInvoiceForAccountAction",
          // Account / Inquiry / Full Inq - Account / Payments
          "dti.fm.fullinquirymgr.struts.ViewPaymentsForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewNSFsForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewPmTransactionsForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewChargesAndFeesForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewRefundsForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewVoidsForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewWriteoffsForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewTransfersForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewSurplusAndDividendsForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewCommissionForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewPcfForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewPolicyTermForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewReceivablePolicyExpandedForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewReceivablePolInstallmentDetailAction",
          "dti.fm.fullinquirymgr.struts.ViewReceivableCoverageExpandedForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewReceivableCoverageExpandedDetailAction",
          "dti.fm.fullinquirymgr.struts.ViewReceivableTransactionExpandedForAccountAction",
          "dti.fm.fullinquirymgr.struts.ViewReceivableTransactionExpandedDetailAction",
          "dti.fm.paymentinquirymgr.struts.ViewAllPaymentAction",
          "dti.fm.paymentinquirymgr.struts.ViewAllPaymentApplicationAction",
          "dti.fm.apinquirymgr.struts.ViewAPTransactionsAction",
          "dti.fm.agentinquirymgr.struts.ViewAgentInformationAction",
          "dti.fm.agentinquirymgr.struts.ViewAgentCommissionAction",
          "dti.fm.groupinquirymgr.struts.ViewGroupCompositionInquiryAction",
          "dti.fm.accountmgr.struts.ShowFindAccountAction",
          "dti.fm.balanceinquirymgr.struts.AgedBalanceAction",
          "dti.fm.balanceinquirymgr.struts.ViewBalanceInquiryAction",
          "dti.fm.balanceinquirymgr.struts.ViewBalanceInquiryTransAction",
          "dti.fm.balanceinquirymgr.struts.ViewBalanceInquiryTransDetailsAction",
          "dti.fm.balanceinquirymgr.struts.ViewBalanceInquiryPaymentDetailsAction",
          "dti.fm.balanceinquirymgr.struts.ViewBalInqStmtDetailsAction",
          // Account / Inquiry / Master Inquiry
          "dti.fm.masterinquirymgr.struts.ViewAccountSummaryAction",
          "dti.fm.masterinquirymgr.struts.ViewAllAccountDetailAction",
          "dti.fm.masterinquirymgr.struts.ViewAllAccountPolicyDetailAction",
          "dti.fm.masterinquirymgr.struts.ViewAllAccountCashDetailAction",
          "dti.fm.masterinquirymgr.struts.ViewAllAccountAcctMaintDetailAction",

          // Non-search pages.
          "dti.fm.fmtransactionmgr.transferacctmgr.struts.AddAccountAction",
          "dti.fm.fmtransactionmgr.transferacctmgr.struts.AddMultiAccountsAction",
          "dti.fm.fmtransactionmgr.transferacctmgr.struts.EnterTransferCriteriaAcctAction",
          "dti.fm.fmtransactionmgr.transferacctmgr.struts.ProcessTransferAcrossAccountAction",
          "dti.fm.fmtransactionmgr.transferacctmgr.struts.ProcessTransferWithinAcctAction",
          "dti.fm.fmtransactionmgr.transferacctmgr.struts.SelectTransferToAcctBalancesAction",

          // Miscellaneous / Re-Process PM FM Trans
          "dti.fm.pmfmtransmgr.struts.ProcessPmFmTransAction",

          //Billing Admin -> Process Audit
          "dti.fm.auditmgr.struts.ViewProcessInstAuditDetailsActionProcess",
          "dti.fm.auditmgr.struts.ViewProcessPendingAuditDetailsAction",
          "dti.fm.auditmgr.struts.ViewProcessAuditAction",
          "dti.fm.auditmgr.struts.ViewProcessAuditParmsAction",
          "dti.fm.auditmgr.struts.ViewProcessOverdueAuditDetailsAction",
          "dti.fm.auditmgr.struts.ViewProcessAuditMessageAction",
          "dti.fm.auditmgr.struts.ViewProcessBillingAuditDetailsAction",
          "dti.fm.auditmgr.struts.ViewProcessRefundAuditDetailsAction",
          "dti.fm.auditmgr.struts.ViewProcessAuditDetailsAction",

          //Process Refund
          "dti.fm.fmtransactionmgr.refundmgr.struts.ProcessRefundRequestAction",

          //Select Accounts
          "dti.fm.fmtransactionmgr.struts.SelectAccountListAction",

          //Process Void
          "dti.fm.fmtransactionmgr.voidmgr.struts.ProcessVoidAction",

          //Process WriteOff
          "dti.fm.fmtransactionmgr.voidmgr.struts.ProcessWriteoffAction"
  });

  String corePath = Module.getCorePath(request);

  // Setting useJqxGrid to user session.
  String process = request.getParameter("process");

  String dbPoolId = (String) ActionHelper.getDbPoolId(request);
  String userId = ActionHelper.getCurrentUserId(request);
  String cacheName = "pageBeans" + "For" + dbPoolId + "_" + userId;
  Cache pageBeans = CacheManager.getInstance().getCache(cacheName);

  if ("enableJqxGrid".equals(process)) {
    session.setAttribute(RequestIds.USE_JQX_GRID, ("Y".equals(request.getParameter("useJqxGrid"))));
  } else if ("changePageUseJqxGridFlag".equals(process)) {
    try {
      if (pageBeans.contains(request.getParameter("strutsClassName"))) {
        PageBean pageBean = (PageBean) pageBeans.get(request.getParameter("strutsClassName"));
        pageBean.setUseJqxGridB(request.getParameter("useJqxGrid"));
      }
    } catch (Exception e) {
      //
    }
  } else if ("enableJqxGridForConvertedPages".equals(process) || "disableJqxGridForConvertedPages".equals(process)) {
    String[] appNames = null;
    String enableDisableB = "enableJqxGridForConvertedPages".equals(process) ? "Y" : "N";

    if (request.getContextPath().contains("/eClaim")) {
      appNames = new String[] {"CM", "COMMON"};
    } else if (request.getContextPath().contains("/ePolicy")) {
      appNames = new String[] {"PM", "COMMON"};
    } else if (request.getContextPath().contains("/FM")) {
      appNames = new String[] {"FM", "COMMON"};
    } else if (request.getContextPath().contains("/CS")) {
      appNames = new String[] {"CS", "COMMON"};
    } else if (request.getContextPath().contains("/CIS")) {
      appNames = new String[] {"CIS", "COMMON"};
    }

    Connection conn = null;
    try {
      for (String appName : appNames) {
        String[] pageActionClassNames = convertedPages.get(appName);

        for (String pageActionClassName : pageActionClassNames) {
          if (!pageBeans.contains(pageActionClassName)) {
            if (conn == null) {
              conn = ActionHelper.getConnection(request);
            }

            NavigationManager.getInstance().getPageBean(conn, request, pageActionClassName, userId, DefaultPageDefLoadProcessor.getInstance());
          }

          PageBean pageBean = (PageBean) pageBeans.get(pageActionClassName);
          pageBean.setUseJqxGridB(enableDisableB);
        }
      }
    } catch (Exception e) {
        //
    } finally {
      DatabaseUtils.close(conn);
    }
  }

  // Get useJqxGrid value.
  boolean jqxGridEnabled = ActionHelper.isJqxGridEnabled(request);

%>
<html>
<head>
  <title>User session options</title>
  <script type="text/javascript" src="<%=corePath%>/js/jquery-1.11.2.min.js"></script>
  <script type="text/javascript">
    function submitForm() {
      var useJqxGrid = document.getElementById("jqxGridEnabledChk").checked ? "Y" : "N";
      window.location = "userSessionOptions.jsp?process=enableJqxGrid&useJqxGrid=" + useJqxGrid;
    }

    function reloadPage() {
      window.location = "userSessionOptions.jsp";
    }

    function changePageUseJqxGridFlag(strutsClassName, useJqxGrid) {
      window.location = "userSessionOptions.jsp?process=changePageUseJqxGridFlag&strutsClassName=" + strutsClassName + "&useJqxGrid=" + useJqxGrid;
    }

    function getEnvPath() {
        return "<%=Module.getEnvPath(request)%>";
    }

    function getContextPath() {
//      return $("#contextPath").val();
        return getEnvPath();
    }

    function enableDisableJqx(appCorePath, className, useJqxGrid) {
      $.ajax({
        url: appCorePath + "/userSessionOptions.jsp?process=changePageUseJqxGridFlag&useJqxGrid=" + useJqxGrid + "&strutsClassName=" + className,
        async: false
      });
    }

    function enableDisableJqxGridForConvertedPages(enable) {
      var process = enable ? "enableJqxGridForConvertedPages" : "disableJqxGridForConvertedPages";
      // CS
      $.ajax({
        url: getContextPath() + "/CS/userSessionOptions.jsp?process=" + process,
        async: false
      });

      // CIS
      $.ajax({
        url: getContextPath() + "/CIS/userSessionOptions.jsp?process=" + process,
        async: false
      });

      // CM
      $.ajax({
        url: getContextPath() + "/eClaim/CM/core/userSessionOptions.jsp?process=" + process,
        async: false
      });

      // PM
      $.ajax({
        url: getContextPath() + "/ePolicy/PM/core/userSessionOptions.jsp?process=" + process,
        async: false
      });

      // FM
      $.ajax({
        url: getContextPath() + "/eFM/FM/core/userSessionOptions.jsp?process=" + process,
        async: false
      });

      reloadPage();
    }

    $(function () {
      $("#btnEnableConvertedPages").click(function () {
        enableDisableJqxGridForConvertedPages(true);

        reloadPage();
      });

      $("#btnDisableConvertedPages").click(function () {
        enableDisableJqxGridForConvertedPages(false);

        reloadPage();
      });
    });
  </script>
</head>

<body>
<form name="userSessionOptionForm" method="post" action="userSessionOptions.jsp">
  <button id="btnReload" onclick="reloadPage()">Reload</button>

  <table border="0" cellspacing="2" cellpadding="2" width="1200px">
    <tr>
      <td>
        <fieldset>
          <legend><b>User session options:</b></legend>
          <table border="0" cellspacing="2" cellpadding="2" width="100%">
            <tr>
              <td><label id="jqxGridEnabledLabel" for="jqxGridEnabledChk">Use jqxGrid</label>
                <input id="jqxGridEnabledChk" <%= jqxGridEnabled ? "checked" : ""%> type="checkbox" name="useJqxGrid"
                       value="Y" onclick="submitForm()"></td>
            </tr>
          </table>
        </fieldset>
      </td>
    </tr>

    <tr>
      <td>
        <fieldset>
          <legend><b>User session options:</b></legend>
          <table border="0" cellspacing="2" cellpadding="2" width="100%">
            <tr>
              <th align="left">Action</th>
              <th align="left">Title</th>
              <th align="left">Use jqxGrid</th>
            </tr>

            <%
              Iterator it = pageBeans.keyIterator();
              while (it.hasNext()) {
                String strutsActionName = (String) it.next();
                PageBean pageBean = (PageBean) pageBeans.get(strutsActionName);
            %>
            <tr>
              <td><%=strutsActionName%></td>
              <td><%=pageBean.getTitle()%></td>
              <td><a href="javascript:void(0)"
                     onclick="changePageUseJqxGridFlag('<%=strutsActionName%>', '<%="Y".equals(pageBean.getUseJqxGridB()) ? "N" : "Y" %>')">
                <%=StringUtils.isBlank(pageBean.getUseJqxGridB())? "NULL" : pageBean.getUseJqxGridB()%>
              </a></td>
            </tr>
            <%
              }
            %>
          </table>
        </fieldset>
      </td>
    </tr>

    <tr>
      <td>
        <fieldset>
          <legend><b>Pages converted to use jqxGrid:</b></legend>
          <table border="0" cellspacing="2" cellpadding="2" width="100%">
            <tr>
              <td>
                <span><button id="btnEnableConvertedPages" type="button">Enable jqxGrid</button></span>
                <span><button id="btnDisableConvertedPages" type="button">Disable jqxGrid</button></span>
                <%--<span><label for="contextPath">Context Path:</label><input type="text" id="contextPath" value="/odev20181"></span>--%>
              </td>
            </tr>

            <tr>
              <td>
                <fieldset>
                  <table id="commonUseJqxGridPages">
                    <tr><th align="left">Common Pages</th></tr>
                    <%
                      for (String pageAction : convertedPages.get("COMMON")) {
                    %>
                    <tr><td><%=pageAction%></td></tr>
                    <%
                      }
                    %>
                  </table>
                </fieldset>
              </td>
            </tr>

            <tr>
              <td>
                <fieldset>
                  <table id="csUseJqxGridPages">
                    <tr><th align="left">CS</th></tr>
                    <%
                      for (String pageAction : convertedPages.get("CS")) {
                    %>
                    <tr><td><%=pageAction%></td></tr>
                    <%
                      }
                    %>
                  </table>
                </fieldset>
              </td>
            </tr>

            <tr>
              <td>
                <fieldset>
                  <table id="cisUseJqxGridPages">
                    <tr><th align="left">CIS</th></tr>
                    <%
                      for (String pageAction : convertedPages.get("CIS")) {
                    %>
                    <tr><td><%=pageAction%></td></tr>
                    <%
                      }
                    %>
                  </table>
                </fieldset>
              </td>
            </tr>

            <tr>
              <td>
                <fieldset>
                  <table id="cmUseJqxGridPages">
                    <tr><th align="left">CM</th></tr>
                    <%
                      for (String pageAction : convertedPages.get("CM")) {
                    %>
                    <tr><td><%=pageAction%></td></tr>
                    <%
                      }
                    %>
                  </table>
                </fieldset>
              </td>
            </tr>

            <tr>
              <td>
                <fieldset>
                  <table id="pmUseJqxGridPages">
                    <tr><th align="left">PM</th></tr>
                    <%
                      for (String pageAction : convertedPages.get("PM")) {
                    %>
                    <tr><td><%=pageAction%></td></tr>
                    <%
                      }
                    %>
                  </table>
                </fieldset>
              </td>
            </tr>

            <tr>
              <td>
                <fieldset>
                  <table id="fmUseJqxGridPages">
                    <tr><th align="left">FM</th></tr>
                    <%
                      for (String pageAction : convertedPages.get("FM")) {
                    %>
                    <tr><td><%=pageAction%></td></tr>
                    <%
                      }
                    %>
                  </table>
                </fieldset>
              </td>
            </tr>
          </table>
        </fieldset>
      </td>
    </tr>
  </table>

</form>
</body>
</html>
