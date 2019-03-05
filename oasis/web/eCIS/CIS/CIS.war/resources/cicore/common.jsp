<%@ page import="dti.oasis.http.Module" %>
<%@ page import="dti.oasis.messagemgr.Message" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="dti.oasis.struts.IOasisAction" %>
<%@ page import="dti.oasis.http.RequestIds" %>
<%@ page import="dti.oasis.util.SysParmProvider" %>
<%@ page import="dti.oasis.util.PageBean" %>
<%@ page import="dti.oasis.recordset.Record" %>
<%@ page import="dti.cs.recordexistsmgr.impl.RecordExistsManagerImpl" %>
<%@ page import="dti.cs.recordexistsmgr.RecordExistsPage" %>
<%@ page import="dti.cs.recordexistsmgr.RecordExistsButton" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>

<jsp:include page="/cicore/cisSecurityHeader.jsp"/>
<%--
  Description: The common jsp pages for CIS jsp pages.

  Author: kshen
  Date: Apr 24, 2008


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  02/20/2009       yhyang     Add function isSaveInProgress().
  07/01/2013       hxk        Issue 141480
                              Added entity level security header include.
  09/13/2013       kshen      Issue 144341.
  12/06/2013       Parker     Issue 148036 Refactor maintainRecordExists code to make one call per subsystem to the database.
  05/07/2018        jld        193125: Add npi_no.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>


<%
    // add js messages for CICommon.js
    MessageManager.getInstance().addJsMessage("js.delete.confirmation");
    MessageManager.getInstance().addJsMessage("js.lose.changes.confirmation");
    MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");

    MessageManager.getInstance().addJsMessage("ci.common.error.certifiedDate.after");
    MessageManager.getInstance().addJsMessage("ci.common.error.classCode.required");
    MessageManager.getInstance().addJsMessage("ci.common.error.classDescription.after");
    MessageManager.getInstance().addJsMessage("ci.common.error.existRecords.delete");
    MessageManager.getInstance().addJsMessage("ci.common.error.foreignAddress.invalid");
    MessageManager.getInstance().addJsMessage("ci.common.error.foreignAddress.required");
    MessageManager.getInstance().addJsMessage("ci.common.error.format.ssn");
    MessageManager.getInstance().addJsMessage("ci.common.error.format.email");
    MessageManager.getInstance().addJsMessage("ci.common.error.format.npi.numeric");
    MessageManager.getInstance().addJsMessage("ci.common.error.format.npi.check.digit");
    MessageManager.getInstance().addJsMessage("ci.common.error.foreignAddress.invalid");
    MessageManager.getInstance().addJsMessage("ci.common.error.foreignAddress.required");
    MessageManager.getInstance().addJsMessage("ci.common.error.newRecords.delete");
    MessageManager.getInstance().addJsMessage("ci.common.error.onlyOneRow.noSelect");
    MessageManager.getInstance().addJsMessage("ci.common.error.reference.number");
    MessageManager.getInstance().addJsMessage("ci.common.error.row.noSelect");
    MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");

    MessageManager.getInstance().addJsMessage("ci.credentialRequest.entity.type");

    MessageManager.getInstance().addJsMessage("ci.entity.class.invalidNetworkDiscount");

    MessageManager.getInstance().addJsMessage("ci.entity.message.address.effectiveDate");
    MessageManager.getInstance().addJsMessage("ci.entity.message.attachments.open");
    MessageManager.getInstance().addJsMessage("ci.entity.message.bothValues.required");
    MessageManager.getInstance().addJsMessage("ci.entity.message.entityType.unknown");
    MessageManager.getInstance().addJsMessage("ci.entity.message.formLetters.open");
    MessageManager.getInstance().addJsMessage("ci.entity.message.module.unknown");
    MessageManager.getInstance().addJsMessage("ci.entity.message.postalCode.invalid");
    MessageManager.getInstance().addJsMessage("ci.entity.message.value.verified");
    MessageManager.getInstance().addJsMessage("ci.entity.message.verified.beforeMaking");
    MessageManager.getInstance().addJsMessage("ci.entity.message.zipCode.invalid");
    MessageManager.getInstance().addJsMessage("ci.entity.message.zipCodeExtension.invalid");

    MessageManager.getInstance().addJsMessage("cs.entity.miniPopup.error.noEntityId");
    MessageManager.getInstance().addJsMessage("cs.field.error.undefined");
    MessageManager.getInstance().addJsMessage("cs.getZipLookup.invalidCityForZipCode");
    MessageManager.getInstance().addJsMessage("cs.save.process.notCompleted");

    // add js messages for addressCommon.js
    MessageManager.getInstance().addJsMessage("ci.address.error.invalidZipOrPostalCode");
    MessageManager.getInstance().addJsMessage("ci.address.error.invalidAddressType");
    MessageManager.getInstance().addJsMessage("ci.address.error.wrongEffectiveDate");
    MessageManager.getInstance().addJsMessage("ci.address.error.wrongEffectiveToDate");
    MessageManager.getInstance().addJsMessage("ci.address.error.bothValuesRequired");
%>
<%
    response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    // If we raised a validation exception, we have re-forwarded to the load and reset the JS variables
    // Reset here so if we later try to leave the page, we receive the is ok to change pages logic.
    if (!(request.getAttribute(IOasisAction.KEY_ERROR) == null)) {
        if ((request.getAttribute(IOasisAction.KEY_ERROR) instanceof dti.oasis.error.ValidationException)) {
%>
            <script type="text/javascript">
                 isChanged = true;
            </script>
<%      }
    }
%>

<script type="text/javascript">
    <%
        MessageManager jsMessageManager = MessageManager.getInstance();
        if (jsMessageManager.hasJsMessages()) {
            Iterator iter = jsMessageManager.getJsMessages();
            while (iter.hasNext()) {
                Message message = (Message) iter.next();
    %>
                setMessage("<%=message.getMessageKey()%>", "<%=message.getMessage()%>");
    <%
            }
        }
    %>
</script>

<script type="text/javascript">
    function isSaveInProgress() {
        var isInProgress='false';
        if(getObject("<%= RequestIds.SAVE_INPROGRESS %>")) {
            isInProgress = getObject("<%= RequestIds.SAVE_INPROGRESS %>").value;
        }
        return (isInProgress.toUpperCase()=='TRUE');
    }

    var hasErrorMessages = <%=MessageManager.getInstance().hasErrorMessages()%>;
</script>
<%
    // Initialize RecordExistsManager for italics logic
    RecordExistsManagerImpl recordExistsManager = (RecordExistsManagerImpl) dti.oasis.app.ApplicationContext.getInstance().getBean("RecordExistsManager");
    HashMap existsMap = recordExistsManager.getExistsMap();

    PageBean pageCommonBean = (PageBean) request.getAttribute("pageBean");
    String pageId = pageCommonBean.getId();
    if ("CI_ENTITY_PERSON_MODIFY".equals(pageId) || "CI_ENTITY_ORG_MODIFY".equals(pageId)) {
        pageId = "CI_ENTITY_MODIFY";
    }

    if (existsMap.containsKey(pageId)) {
        RecordExistsPage existsPage = (RecordExistsPage) existsMap.get(pageId);
        HashMap buttonMap = existsPage.getButtonMap();

        Iterator iterator = buttonMap.keySet().iterator();
        Integer idx = 0;
        Record valueRecord = null;
        if (request.getAttribute("recordExistResult") != null) {
            valueRecord = (Record) request.getAttribute("recordExistResult");
        }
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            RecordExistsButton button = (RecordExistsButton) buttonMap.get(key);
            String buttonItalicsFlag = "N";
            if (valueRecord != null)
                buttonItalicsFlag = valueRecord.getStringValue(key);
%>

<script type="text/javascript">
    italicsArrayInPageLevel.push(new italicsInfo('<%=key%>', '<%=button.getActionItemId()%>', '<%=buttonItalicsFlag%>'));
</script>

<%
            idx = idx + 1;
        }
%>

<%
    }
%>
<script type='text/javascript' src="<%=Module.getCISPath(request)%>/js/CICommon.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script language='javascript' src="<%=Module.getCISPath(request)%>/cicore/js/common.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>