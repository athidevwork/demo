<%@ page import="dti.oasis.messagemgr.Message" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%--
  Description: The common jsp pages for CIS jsp pages.

  Author: wfu
  Date: Oct 06, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/06/2010       wfu         111776: Replaced hardcode string with resource definition
  02/16/2011       Blake       Modified for issue 112690:Make Identifier Prominent.
  02/24/2011       kshen       Removed duplicate logic for getting note image icon.
  07/20/2012       kshen       Added js message for zip code.
  03/29/2016       jld         Added js message for credential letter.
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%
    // add js messages for CICommon.js
    MessageManager.getInstance().addJsMessage("js.lose.changes.confirmation");
    MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");

    MessageManager.getInstance().addJsMessage("ci.entity.message.entityType.unknown");
    MessageManager.getInstance().addJsMessage("ci.entity.message.module.unknown");
    MessageManager.getInstance().addJsMessage("ci.common.error.format.ssn");
    MessageManager.getInstance().addJsMessage("ci.common.error.format.email");

    MessageManager.getInstance().addJsMessage("ci.common.error.classCode.required");
    MessageManager.getInstance().addJsMessage("ci.entity.message.zipCode.invalid");
    MessageManager.getInstance().addJsMessage("ci.entity.message.zipCodeExtension.invalid");
    MessageManager.getInstance().addJsMessage("ci.entity.message.address.effectiveDate");
    MessageManager.getInstance().addJsMessage("ci.entity.message.postalCode.invalid");
    MessageManager.getInstance().addJsMessage("ci.entity.message.bothValues.required");

    MessageManager.getInstance().addJsMessage("ci.common.error.foreignAddress.invalid");
    MessageManager.getInstance().addJsMessage("ci.common.error.foreignAddress.required");
    MessageManager.getInstance().addJsMessage("ci.common.error.classDescription.after");
    MessageManager.getInstance().addJsMessage("ci.common.error.onlyOneRow.noSelect");
    MessageManager.getInstance().addJsMessage("ci.common.error.row.noSelect");

    MessageManager.getInstance().addJsMessage("ci.entity.message.value.verified");
    MessageManager.getInstance().addJsMessage("ci.entity.message.verified.beforeMaking");

    // add js messages for ciFolderCommon.js
    MessageManager.getInstance().addJsMessage("ci.entity.message.formLetters.open");
    MessageManager.getInstance().addJsMessage("ci.entity.message.attachments.open");
    MessageManager.getInstance().addJsMessage("ci.common.error.certifiedDate.after");

    MessageManager.getInstance().addJsMessage("ci.common.error.reference.number");
    MessageManager.getInstance().addJsMessage("cs.getZipLookup.invalidCityForZipCode");
    MessageManager.getInstance().addJsMessage("ci.entity.class.invalidNetworkDiscount");
    MessageManager.getInstance().addJsMessage("ci.credentialRequest.entity.type");
%>

<script type="text/javascript">
    <%
        MessageManager jsMessageManager = MessageManager.getInstance();
        if (jsMessageManager.hasJsMessages()) {
            Iterator iter = jsMessageManager.getJsMessages();
            while (iter.hasNext()) {
                Message dmessage = (Message) iter.next();
    %>
                setMessage("<%=dmessage.getMessageKey()%>", "<%=dmessage.getMessage()%>");
    <%
            }
        }
    %>
</script>
<script type="text/javascript" src="js/CICommon.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
