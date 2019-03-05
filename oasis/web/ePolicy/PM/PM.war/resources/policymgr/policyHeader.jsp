<%@ page import="dti.pm.busobjs.PolicyViewMode" %>
<%@ page import="dti.pm.core.http.RequestIds" %>
<%@ page import="dti.pm.policymgr.lockmgr.impl.LockManagerImpl" %>
<%@ page import="dti.oasis.tags.OasisFieldsHeader" %>
<%@ page import="dti.oasis.util.FormatUtils" %>
<%@ page import="dti.oasis.tags.OasisFormField" %>
<%@ page import="org.apache.struts.util.ResponseUtils" %>
<%@ page language="java" %>
<%--
  Description:

  Author: mlmanickam
  Date: Oct 12, 2006


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/14/2007       sma         Make the Select Term and View Mode fields disabled on the popup pages.
  01/15/2008       fcb         offNo and wipNo hidden fields added.
  03/14/2008       sxm         Moved maintainEndQuote.js include from maintainPolicy.jsp
  11/04/2008       yhyang      #87710 Include headerDataSpecialLayout to load the policy information.
  08/27/2010       dzhang      #111441 Make the initial display of policy header section configurable.
  09/10/2010       wfu         111776: Replaced hardcode string with resource definition
  09/15/2010       wfu         111776: Change message format for PolicyHeader information
  01/11/2011       dzhang      114424: Display the navigation section in the Policy Information header, default as visible.                                       
  05/06/2011       fcb         Removed logic related to isNewValue
  08/26/2011       ldong       124449 - Enhancement issue, notes hot key.
  07/05/2013       xnie        145721 - Modified function PolicyHeader to set writtenPremium, curTransactionId, and
                                        offTransactionId.
  04/14/2014       Jyang2      149094 - Handle the special characters before set the policyHolder name into policyHeader.
  10/09/2014       wdang       156038 - 1) Removed hidden fields riskId/coverageId.
                                        2) Added riskHeader/coverageHeader in policyHeader to maintain 
                                           currently selected risk/coverage. 
  03/13/2015       awu         161778 - 1) Changed isSamePolicyHeaderB to isSamePolicyB.
  08/10/2015       wdang       157211 - Added policyTermHistoryId,termBaseRecordId in PolicyHeader object;
                                        riskBaseRecordId in RiskHeader, coverageBaseRecordId in CoverageHeader.
  09/16/2015       Elvin       Issue 160360: add preview functionality
  09/18/2015       eyin        166007 - Added policyExpirationDate in PolicyHeader object.
  01/06/2016       wdang       168069 - Added policyStatus in PolicyHeader object.
  08/26/2016       wdang       167534 - Added quoteCycleCode in PolicyHeader object.
  02/28/2017       mlm         183387 - Refactored to handle preview for long running transactions.
  07/12/2017       lzhang      186847   Reflect grid replacement project changes
  11/07/2017       xnie        188231 - Added two hidden fields riskHeaderRiskId and coverageHeaderCoverageId.
  07/11/2018       wrong       193977 - Make riskHeaderRiskId and coverageHeaderCoverageId fields always exist.
  10/26/2018       wrong       193599 - Added recordModeCode in policyHeader.
  11/15/2018       eyin        194100 - Add buildNumber parameter to static file references to improve performance.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<jsp:useBean id="policyHeaderFieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<script type="text/javascript" src="<%=appPath%>/pmcore/js/policyLockTimer.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=appPath%>/transactionmgr/endorsementquotemgr/js/maintainEndQuote.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=csPath%>/js/csLoadNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<input type="hidden" name="isSamePolicyB" value="<c:out value="${isSamePolicyB}"/>"/>

<script type="text/javascript">

    // Initialize values for policy lock timer
    var policyLockDuration = "<%=new LockManagerImpl().getLockDuration()%>";

    function ToggleControl(ControlToToggle, LinkControl, LinkControlHideText, LinkControlShowText, imgPlus, imgMinus)
    {
        //alert(getSingleObject(LinkControl)) ;
        if (getSingleObject(LinkControl).innerText == LinkControlShowText)
        {
            hideShowElementByClassName(getSingleObject(ControlToToggle), false);
            if (getSingleObject("h" + ControlToToggle) != null)
                hideShowElementByClassName(getSingleObject("h" + ControlToToggle), true);
            else
                getSingleObject(LinkControl).innerText = LinkControlHideText;

            if (imgPlus != '')
                hideShowElementByClassName(getSingleObject(imgPlus), true);
            if (imgMinus != '')
                hideShowElementByClassName(getSingleObject(imgMinus), false);
        }
        else
        {
            hideShowElementByClassName(getSingleObject(ControlToToggle), true);
            if (getSingleObject("h" + ControlToToggle) != null)
                hideShowElementByClassName(getSingleObject("h" + ControlToToggle), false);
            else
                getSingleObject(LinkControl).innerText = LinkControlShowText;

            if (imgPlus != '')
                hideShowElementByClassName(getSingleObject(imgPlus), false);
            if (imgMinus != '')
                hideShowElementByClassName(getSingleObject(imgMinus), true);
        }
        return false;
    }

</script>

<%
    OasisFormField configValue = (OasisFormField) fieldsMap.get("policyHeaderDisplay");
    if (configValue != null && !StringUtils.isBlank(configValue.getDefaultValue()) &&
            (configValue.getDefaultValue().equalsIgnoreCase("hide") ||
                    configValue.getDefaultValue().equalsIgnoreCase("visible") ||
                    configValue.getDefaultValue().equalsIgnoreCase("invisible"))) {
        //Override the policyHeaderDisplayMode's value by WebWB's configured value
        pageContext.setAttribute("policyHeaderDisplayMode", configValue.getDefaultValue().toLowerCase());
    }

    Iterator itr = policyHeader.getPolicyTerms();
    int termcount = 0;
    while (itr.hasNext()) {
        itr.next();
        termcount++;
    }
    String panelHeaderDisplayNavigation = "false";
    OasisFormField isDisplayNavigation = (OasisFormField) fieldsMap.get("isDisplayNavigation");
    if ((termcount >= 2) && isDisplayNavigation != null &&
            !StringUtils.isBlank(isDisplayNavigation.getDefaultValue()) &&
            (YesNoFlag.getInstance(isDisplayNavigation.getDefaultValue()).booleanValue())) {
        panelHeaderDisplayNavigation = "true";
    }
    pageContext.setAttribute("isPanelHeaderDisplayNavigation", panelHeaderDisplayNavigation);

%>

<c:if test="${policyHeaderDisplayMode=='hide'}">
    <c:set var="isPanelCollaspedByDefault" value="true"></c:set>
</c:if>

<c:if test="${policyHeaderDisplayMode=='invisible'}">
    <c:set var="isPanelHiddenByDefault" value="true"></c:set>
</c:if>

<jsp:include page="/core/fieldlayerdep.jsp"/>

<fmt:message key="pm.policyHeader.header" var="panelTitleForPolicyHeader" scope="page"/>
<%
    String panelTitleForPolicyHeader = (String) pageContext.getAttribute("panelTitleForPolicyHeader");
    boolean isPanelCollaspedByDefault = Boolean.valueOf((String) pageContext.getAttribute("isPanelCollaspedByDefault")).booleanValue();
    boolean isPanelHiddenByDefault = Boolean.valueOf((String) pageContext.getAttribute("isPanelHiddenByDefault")).booleanValue();
    String collaspeTitleForPolicyHeader = panelTitleForPolicyHeader;
    collaspeTitleForPolicyHeader += " - " + MessageManager.getInstance().formatMessage("pm.common.policy.header.information",
                                             new String[]{policyHeader.getPolicyNo(),
                                                  FormatUtils.formatDateForDisplay(policyHeader.getTermEffectiveFromDate()),
                                                  FormatUtils.formatDateForDisplay(policyHeader.getTermEffectiveToDate())});

    OasisFieldsHeader headerFieldsMap = OasisFieldsHeader.createInstance(policyHeaderFieldsMap);
    boolean isPanelHeaderDisplayNavigation = Boolean.valueOf((String) pageContext.getAttribute("isPanelHeaderDisplayNavigation")).booleanValue();
    String navigationTitle  =  MessageManager.getInstance().formatMessage("pm.common.policy.header.navigation.information");
    boolean invokeODS = false;
    if (UserSessionManager.getInstance().getUserSession().has(RequestIds.INVOKE_ODS)) {
        invokeODS = ((YesNoFlag) UserSessionManager.getInstance().getUserSession().get(RequestIds.INVOKE_ODS)).booleanValue();
        UserSessionManager.getInstance().getUserSession().remove(RequestIds.INVOKE_ODS);
    }
%>
<% request.setAttribute("headerFieldsMap",headerFieldsMap);%>
<div id=policyHeaderInfo>
<oweb:panel panelTitleId="panelTitleIdForPolicyHeader"
            panelContentId="panelContentIdForPolicyHeader"
            panelTitle="<%= panelTitleForPolicyHeader %>"
            panelCollapseTitle="<%= collaspeTitleForPolicyHeader %>"
            isPanelCollaspedByDefault="<%= isPanelCollaspedByDefault %>"
            isPanelHiddenByDefault="<%= isPanelHiddenByDefault %>"
            isPanelHeaderDisplayNavigation="<%=isPanelHeaderDisplayNavigation%>"
            navigationTitle="<%=navigationTitle%>">

<tr>
    <td colspan=2>
        <input type=hidden name=riskHeaderRiskId value="<%=policyHeader.hasRiskHeader() ?
                                                           policyHeader.getRiskHeader().getRiskId() : ""%>"/>
        <input type=hidden name=coverageHeaderCoverageId value="<%=policyHeader.hasCoverageHeader() ?
                                                                   policyHeader.getCoverageHeader().getCoverageId() : ""%>"/>
        <input type=hidden name=offNo value="<%=policyHeader.getPolicyIdentifier().getPolicyOffNumber()%>"/>
        <input type=hidden name=wipNo value="<%=policyHeader.getPolicyIdentifier().getPolicyWipNumber()%>"/>
        <input type=hidden name=endQuoteId value="<%=policyHeader.getLastTransactionInfo().getEndorsementQuoteId()%>"/>

        <table cellpadding=0 cellspacing=0 width=100%>
            <tr>
                <td>
                <table cellpadding=0 cellspacing=0 width=100%>
                    <c:set var="row" value="-999999"></c:set>
                    <c:set var="first" value="true"></c:set>
                    <jsp:include page="/core/headerDataSpecialLayout.jsp" />
                 </table>
                 </td>
            </tr>
        </table>
    </td>
</tr>
<script type="text/javascript">

    if (getObject("availablePolicyTerms") != null)
    {
        var obj = getObject("availablePolicyTerms");
        if (obj.options != null) {
            var selectedTerm = "<%= policyHeader.getSelectedPolicyTermId(policyHeader.getPolicyTermHistoryId()) %>";
            var i;
            for (i = 0; i < obj.options.length; i++)
            {
                if (obj.options[i].value == selectedTerm)
                {
                    obj.options[i].selected = true;
                }
            }
        }

        if ("<c:out value='${isForDivPopup}' />" == "true") {
            obj.disabled = true;
        }
    }

    if (getObject("policyViewMode") != null)
    {
        var obj = getObject("policyViewMode");
        if (obj.options != null) {

            var selectedMode = "<%= ((PolicyViewMode) request.getAttribute(RequestIds.SELECTED_POLICY_VIEW_MODE)).getName() %>"  ;
            if (selectedMode == "ENDQUOTE") {
                var endQuoteId = "<%= policyHeader.getLastTransactionInfo().getEndorsementQuoteId()%>";
                for (var i = 0; i < obj.options.length; i++)
                {
                    if (obj.options[i].text.indexOf(endQuoteId)>0)
                    {
                        obj.options[i].selected = true;
                    }
                }

            }
            else {               
                for (var i = 0; i < obj.options.length; i++)
                {
                    if (obj.options[i].value == selectedMode)
                    {
                        obj.options[i].selected = true;
                    }
                }
            }
        }

        if ("<c:out value='${isForDivPopup}' />" == "true") {
            obj.disabled = true;
        }
    }

    function Transaction() {
        this.endorsementCode = "<%= policyHeader.getLastTransactionInfo().getEndorsementCode()%>";
        this.endorsementQuoteId = "<%= policyHeader.getLastTransactionInfo().getEndorsementQuoteId()%>";
        this.transAccountingDate = "<%= policyHeader.getLastTransactionInfo().getTransAccountingDate()%>";
        this.transactionCode = "<%= policyHeader.getLastTransactionInfo().getTransactionCode()%>";
        this.transactionLogId = "<%= policyHeader.getLastTransactionInfo().getTransactionLogId()%>";
        this.transactionStatusCode = "<%= policyHeader.getLastTransactionInfo().getTransactionStatusCode()%>";
        this.transactionTypeCode = "<%= policyHeader.getLastTransactionInfo().getTransactionTypeCode()%>";
        this.transEffectiveFromDate = "<%= policyHeader.getLastTransactionInfo().getTransEffectiveFromDate()%>";
    }
    
    <% if (policyHeader.hasRiskHeader()) {%>;
    function RiskHeader() {
    	this.riskId = "<%= policyHeader.getRiskHeader().getRiskId()%>";
    	this.riskBaseRecordId = "<%= policyHeader.getRiskHeader().getRiskBaseRecordId()%>";
    }
    <% }%>	
    
    <% if (policyHeader.hasCoverageHeader()) {%>;
    function CoverageHeader() {
    	this.coverageId = "<%= policyHeader.getCoverageHeader().getCoverageId()%>";
    	this.coverageBaseRecordId = "<%= policyHeader.getCoverageHeader().getCoverageBaseRecordId()%>";
    }
    <% }%>	
    
    function PolicyHeader() {
        this.policyId = "<%= policyHeader.getPolicyId()%>";
        this.policyNo = "<%= policyHeader.getPolicyNo()%>";
        this.policyHolderName = "<%= ResponseUtils.filter(policyHeader.getPolicyHolderName())%>";
        this.policyHolderNameEntityId = "<%= policyHeader.getPolicyHolderNameEntityId()%>";
        this.policyCycleCode = "<%= policyHeader.getPolicyCycleCode()%>";
        this.recordModeCode = "<%= policyHeader.getRecordMode().toString()%>";
        this.quoteCycleCode = "<%= policyHeader.getQuoteCycleCode()%>";
        this.policyTypeCode = "<%= policyHeader.getPolicyTypeCode()%>";
        this.wipB = <%= policyHeader.isWipB()%>;
        this.issueStateCode = "<%= policyHeader.getIssueStateCode()%>";
        this.regionalOffice = "<%= policyHeader.getRegionalOffice()%>";
        this.shortTermB = <%= policyHeader.isShortTermB()%>;
        this.issueCompanyEntityId = "<%= policyHeader.getIssueCompanyEntityId()%>";
        this.policyTermHistoryId = "<%= policyHeader.getPolicyTermHistoryId() %>";
        this.termBaseRecordId = "<%= policyHeader.getTermBaseRecordId() %>";
        this.termEffectiveFromDate = "<%= policyHeader.getTermEffectiveFromDate()%>";
        this.termEffectiveToDate = "<%= policyHeader.getTermEffectiveToDate()%>";
        this.policyExpirationDate = "<%= policyHeader.getPolicyExpirationDate()%>";
        this.termWrittenPremium = "<%= policyHeader.getTermWrittenPremium()%>";
        this.writtenPremium = "<%= policyHeader.getWrittenPremium()%>";
        this.curTransactionId = "<%= policyHeader.getCurTransactionId()%>";
        this.offTransactionId = "<%= policyHeader.getOffTransactionId()%>";
        this.lastTransactionId = "<%= policyHeader.getLastTransactionId()%>";
        this.showViewMode = <%= policyHeader.isShowViewMode()%>;
        this.coveragePartConfigured = <%= policyHeader.isCoveragePartConfigured()%>;
        this.coverageClassConfigured = <%= policyHeader.isCoverageClassConfigured()%>;
        this.ownLock = <%= policyHeader.getPolicyIdentifier().ownLock()%>;
        this.policyStatus = "<%= policyHeader.getPolicyStatus()%>";
        this.isPreviewRequest = <%= policyHeader.isPreviewRequest()%>;
        this.isInvokeODS = <%= invokeODS %>;
        this.lastTransactionInfo = new Transaction();
        <% if (policyHeader.hasRiskHeader()) {%>
        	 this.riskHeader = new RiskHeader();
        <% }%>	
        <% if (policyHeader.hasCoverageHeader()) {%>
        	 this.coverageHeader = new CoverageHeader();
        <% }%>	
    }

    var policyHeader = new PolicyHeader();

    // Initialize policy lock Timer
    handleOnLoadPolicyHeader();
</script>
</oweb:panel>
</div>
<!-- View special conditiona warning -->
<script type="text/javascript">
    function preViewSpecialConditionMessages() {
        var isSamePolicyB = getObjectValue("isSamePolicyB");
        if (isSamePolicyB == "N") {
            var showSpecialWarning = false;
            var functionExists = eval("window.showSpecialWarning");
            if (functionExists) {
                showSpecialWarning = window.showSpecialWarning();
            }
            if (showSpecialWarning) {
                functionExists = eval("window.viewSpecialConditionMessages");
                if (functionExists) {
                    window.viewSpecialConditionMessages("OASIS PM", "POLICY", "POLICY_NO", policyHeader.policyId);
                }
            }
        }
    }
</script>
