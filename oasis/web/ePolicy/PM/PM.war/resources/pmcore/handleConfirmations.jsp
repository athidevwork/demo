<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.oasis.messagemgr.Message" %>
<%@ page import="java.util.Iterator" %>

<%--
  Description:

  Author: wreeder
  Date: Mar 29, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/12/2007       sxm         Added handling of confirmedAsYRequired
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<c:if test="${empty submitAction}">
    <c:set var="submitAction" value="SAVE"/>
</c:if>

<script type="text/javascript">
function handleConfirmations() {
    var confirmed = true;
    var index = 0;
<%
    MessageManager messageManager = MessageManager.getInstance();
    if (messageManager.hasConfirmationPrompts()) {
        Iterator iter = messageManager.getConfirmationPrompts();
        while(iter.hasNext()){
            Message message = (Message) iter.next();
%>
    if (confirmed) {
        confirmed = confirm('<%= message.getMessage()%>');

        // set confirmation fields and responses array first
        var oMessage = new Object();
        oMessage.messageKey = "<%= message.getMessageKey()%>";
        oMessage.message = "<%= message.getMessage()%>";
        confirmMessages[index] = oMessage;
        confirmResponses[index] = confirmed ? 'Y' : 'N';

        // reset confirmed to true if the response need not be "yes" in order to continue
        if (!confirmed && !<%=message.getConfirmedAsYRequired()%>) {
            confirmed = true;
        }

        index ++;
    }
<%
        }
%>
    if (confirmed) {
        // set confirmation fields and responses in request before submit
        for (var i = 0; i < confirmMessages.length; i++) {
            setInputFormField(confirmMessages[i].messageKey+".confirmed", confirmResponses[i]);
        }
        
        commonOnSubmit('<c:out value="${submitAction}"/>');
    }
<%
    }
%>
}
</script>