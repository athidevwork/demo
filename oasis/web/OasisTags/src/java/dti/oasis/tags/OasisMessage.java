package dti.oasis.tags;

import dti.oasis.messagemgr.Message;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Extends the struts BodyTagSupport to form a new tag for displaying error, warning, information,
 * and confirmation messages in jsp.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 5, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/07/2007       sma         Added handling of confirmation prompts
 * 06/11/2007       sma         Added handling of new attrbute showAllMessages
 * 06/28/2007       sma         Added handling of new attrbute messageValue for confirmations
 * 08/29/2007       sxm         Modified doStartTag to handle related field/row IDs for error messages
 * 09/08/2011       kshen       Added codes to handle message grid id.
 * 03/10/2017       eyin        Re-design oasis message tag for new UI change.
 * 08/04/2017       dpang       Issue 187323: escape single quote in message.
 * 12/27/2017       eyin        Issue 190583: replace Enter - '\r' with html tag '<br/>' in message.
 * 01/11/2018       eyin        Issue 190583: Changed to ignore '\n' && '\r' if they are included in the message info,
 *                                            unless they are included in tag <pre/>.
 * ---------------------------------------------------
 */
public class OasisMessage extends javax.servlet.jsp.tagext.BodyTagSupport {

    /** Returns a string that represents the stylesheet class set for error message */
    public String getErrorStyleClass() {
        return errorStyleClass;
    }

    /** Method that sets the stylesheet class for error message type */
    public void setErrorStyleClass(String errorStyleClass) {
        this.errorStyleClass = errorStyleClass;
    }

    /** Returns a string that represents the stylesheet class set for warning message */
    public String getWarningStyleClass() {
        return warningStyleClass;
    }

    /** Method that sets the stylesheet class for warning message type */
    public void setWarningStyleClass(String warningStyleClass) {
        this.warningStyleClass = warningStyleClass;
    }

    /** Returns a string that represents the stylesheet class set for information message */
    public String getInformationStyleClass() {
        return informationStyleClass;
    }

    /** Method that sets the stylesheet class for information message type */
    public void setInformationStyleClass(String informationStyleClass) {
        this.informationStyleClass = informationStyleClass;
    }

    /** Returns a string that represents the stylesheet class set for confirmation message */
    public String getConfirmationStyleClass() {
        return confirmationStyleClass;
    }

    /** Method that sets the stylesheet class for confirmation message type */
    public void setConfirmationStyleClass(String confirmationStyleClass) {
        this.confirmationStyleClass = confirmationStyleClass;
    }

    /** Returns a boolean that indicates whether the error messages are set to be hidden */
    public boolean isHideError() {
        return hideError;
    }

    /** Method that sets a boolean value indicating whether to hide the error message */
    public void setHideError(boolean hideError) {
        this.hideError = hideError;
    }

    /** Returns a boolean that indicates whether the warning messages are set to be hidden */
    public boolean isHideWarning() {
        return hideWarning;
    }

    /** Method that sets a boolean value indicating whether to hide the warning message */
    public void setHideWarning(boolean hideWarning) {
        this.hideWarning = hideWarning;
    }

    /** Returns a boolean that indicates whether the information messages are set to be hidden */
    public boolean isHideInformation() {
        return hideInformation;
    }

    /** Method that sets a boolean value indicating whether to hide the information message */
    public void setHideInformation(boolean hideInformation) {
        this.hideInformation = hideInformation;
    }

    /** Returns a boolean that indicates whether the confirmation messages are set to be hidden */
    public boolean isHideConfirmation() {
        return hideConfirmation;
    }

    /** Method that sets a boolean value indicating whether to hide the confirmation message */
    public void setHideConfirmation(boolean hideConfirmation) {
        this.hideConfirmation = hideConfirmation;
    }

    /** Returns a boolean that indicates whether all messages are shown regardless errord */
    public boolean isShowAllMessages() {
        return showAllMessages;
    }

    /** Method that sets a boolean value indicating whether all messages are shown regardless errord */
    public void setShowAllMessages(boolean showAllMessages) {
        this.showAllMessages = showAllMessages;
    }

    /** Returns a boolean that indicates whether all messages are shown on parent screen */
    public boolean isDisplayMessagesOnParent() {
        return displayMessagesOnParent;
    }

    /** Method that sets a boolean value indicating whether all messages are shown on parent screen */
    public void setDisplayMessagesOnParent(boolean displayMessagesOnParent) {
        this.displayMessagesOnParent = displayMessagesOnParent;
    }

    /** Method that returns string representation of the class instance member variables */
    public String toString() {
        return "OasisMessage{" +
            "errorStyleClass='" + errorStyleClass + '\'' +
            ", warningStyleClass='" + warningStyleClass + '\'' +
            ", informationStyleClass='" + informationStyleClass + '\'' +
            ", confirmationStyleClass='" + confirmationStyleClass + '\'' +
            ", hideError=" + hideError +
            ", hideWarning=" + hideWarning +
            ", hideInformation=" + hideInformation +
            ", hideConfirmation=" + hideConfirmation +
            ", showAllMessages=" + showAllMessages +
            '}';
    }

    /* (non-Javadoc)
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
    public int doStartTag() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "doStartTag");

        // process error, warning, information and confirmation messages
        boolean isFirstError = true;
        String messageFieldId = "";
        String messageRowId = "";
        String messageGridId = "";

        TagUtils util = TagUtils.getInstance();

        util.write(pageContext,"<div id='oasisMessageBody'><table></table></div>" + "\n");
        util.write(pageContext,"<div id='oasisMessageForFrameBody'><table></table></div>" + "\n");

        Iterator it = MessageManager.getInstance().getMessagesAndConfirmationPrompts();
        StringBuffer sb = new StringBuffer("").
            append("<script type='text/javascript'>\n").
            append("var displayMessagesOnParent = " + isDisplayMessagesOnParent() + ";" + "\n").
            append("function oasisMessages(){" + "\n").
            append("    this.showAllMessages = " + isShowAllMessages() + ";" + "\n").
            append("    this.errorMessages = {messageStyleClass: '" + getErrorStyleClass() + "'," + "\n").
            append("                          messageArray: []};" + "\n").
            append("    this.warningMessages = {messageStyleClass: '" + getWarningStyleClass() + "'," + "\n").
            append("                            messageArray: []};" + "\n").
            append("    this.informationMessages = {messageStyleClass: '" + getInformationStyleClass() + "'," + "\n").
            append("                                messageArray: []};" + "\n").
            append("    this.confirmationMessages = {messageStyleClass: '" + getConfirmationStyleClass() + "'," + "\n").
            append("                                 messageArray: []};" + "\n");

        while (it.hasNext()) {
            Message message = (Message) it.next();
            String messageStr = message.getMessage() == null ? "" : message.getMessage().replaceAll("'", "\\\\'");
            if(!"".equals(messageStr) && messageStr.toLowerCase().contains("<pre>")){
                String [] msgArr = messageStr.split("<(?i)pre>");
                for(int i=0; i<msgArr.length; i++){
                    if(i == 0){
                        messageStr = msgArr[0].replaceAll("\n\r", "").replaceAll("\r\n", "")
                            .replaceAll("\n", "").replaceAll("\r", "");
                    }else{
                        messageStr += "<pre>";
                        String [] subArr = msgArr[i].split("</(?i)pre>");
                        messageStr += subArr[0].replaceAll("\n\r", "<br/>").replaceAll("\r\n",  "<br/>")
                            .replaceAll("\n",  "<br/>").replaceAll("\r", "<br/>") + "</pre>";
                        if(subArr.length > 1){
                            messageStr += subArr[1].replaceAll("\n\r", "").replaceAll("\r\n",  "")
                                .replaceAll("\n",  "").replaceAll("\r", "");
                        }
                    }
                }
            }else{
                messageStr = messageStr.replaceAll("\n\r", "").replaceAll("\r\n", "").replaceAll("\n", "")
                    .replaceAll("\r", "");
            }

            if (!isHideError() && message.getMessageCategory().equals(MessageCategory.ERROR)) {
                sb.append("    this.errorMessages.messageArray.push({messageKey: '" + message.getMessageKey() + "'," + "\n").
                    append("                                         messageCategory: '" + message.getMessageCategory() + "'," + "\n").
                    append("                                         defaultConfirmationValue: '" + message.getDefaultConfirmationValue() + "'," + "\n").
                    append("                                         confirmedAsYRequired: '" + message.getConfirmedAsYRequired() + "'," + "\n").
                    append("                                         message: '" + messageStr + "'," + "\n").
                    append("                                         onClickEvent: '" + message.getOnClickEvent() + "'," + "\n").
                    append("                                         autoClose: '" + message.getAutoClose() + "'," + "\n").
                    append("                                         autoCloseDelay: '" + message.getAutoCloseDelay() + "'," + "\n").
                    append("                                         messageFieldId: '" + message.getMessageFieldId() + "'," + "\n").
                    append("                                         messageRowId: '" + message.getMessageRowId() + "'," + "\n").
                    append("                                         messageGridId: '" + message.getMessageGridId() + "'});" + "\n");

                if (isFirstError) {
                    messageFieldId = message.getMessageFieldId() == null ? "" : message.getMessageFieldId();
                    messageRowId = message.getMessageRowId() == null ? "" : message.getMessageRowId();
                    messageGridId = message.getMessageGridId() == null ? "" : message.getMessageGridId();
                }

                isFirstError = false;
            }else if(!isHideWarning() && message.getMessageCategory().equals(MessageCategory.WARNING)) {
                sb.append("    this.warningMessages.messageArray.push({messageKey: '" + message.getMessageKey() + "'," + "\n").
                    append("                                           messageCategory: '" + message.getMessageCategory() + "'," + "\n").
                    append("                                           defaultConfirmationValue: '" + message.getDefaultConfirmationValue() + "'," + "\n").
                    append("                                           confirmedAsYRequired: '" + message.getConfirmedAsYRequired() + "'," + "\n").
                    append("                                           message: '" + messageStr + "'," + "\n").
                    append("                                           onClickEvent: '" + message.getOnClickEvent() + "'," + "\n").
                    append("                                           autoClose: '" + message.getAutoClose() + "'," + "\n").
                    append("                                           autoCloseDelay: '" + message.getAutoCloseDelay() + "'," + "\n").
                    append("                                           messageFieldId: '" + message.getMessageFieldId() + "'," + "\n").
                    append("                                           messageRowId: '" + message.getMessageRowId() + "'," + "\n").
                    append("                                           messageGridId: '" + message.getMessageGridId() + "'});" + "\n");
            }
            else if(!isHideInformation() && message.getMessageCategory().equals(MessageCategory.INFORMATION)) {
                sb.append("    this.informationMessages.messageArray.push({messageKey: '" + message.getMessageKey() + "'," + "\n").
                    append("                                               messageCategory: '" + message.getMessageCategory() + "'," + "\n").
                    append("                                               defaultConfirmationValue: '" + message.getDefaultConfirmationValue() + "'," + "\n").
                    append("                                               confirmedAsYRequired: '" + message.getConfirmedAsYRequired() + "'," + "\n").
                    append("                                               message: '" + messageStr + "'," + "\n").
                    append("                                               onClickEvent: '" + message.getOnClickEvent() + "'," + "\n").
                    append("                                               autoClose: '" + message.getAutoClose() + "'," + "\n").
                    append("                                               autoCloseDelay: '" + message.getAutoCloseDelay() + "'," + "\n").
                    append("                                               messageFieldId: '" + message.getMessageFieldId() + "'," + "\n").
                    append("                                               messageRowId: '" + message.getMessageRowId() + "'," + "\n").
                    append("                                               messageGridId: '" + message.getMessageGridId() + "'});" + "\n");
            }
            else if (!isHideConfirmation() && message.getMessageCategory().equals(MessageCategory.CONFIRMATION_PROMPT)) {
                sb.append("    this.confirmationMessages.messageArray.push({messageKey: '" + message.getMessageKey() + "'," + "\n").
                    append("                                                messageCategory: '" + message.getMessageCategory() + "'," + "\n").
                    append("                                                defaultConfirmationValue: '" + message.getDefaultConfirmationValue() + "'," + "\n").
                    append("                                                confirmedAsYRequired: '" + message.getConfirmedAsYRequired() + "'," + "\n").
                    append("                                                message: '" + messageStr + "'," + "\n").
                    append("                                                onClickEvent: '" + message.getOnClickEvent() + "'," + "\n").
                    append("                                                autoClose: '" + message.getAutoClose() + "'," + "\n").
                    append("                                                autoCloseDelay: '" + message.getAutoCloseDelay() + "'," + "\n").
                    append("                                                messageFieldId: '" + message.getMessageFieldId() + "'," + "\n").
                    append("                                                messageRowId: '" + message.getMessageRowId() + "'," + "\n").
                    append("                                                messageGridId: '" + message.getMessageGridId() + "'});" + "\n");
            }
        }

        sb.append("}" + "\n").
            append("var oasisMessages = new oasisMessages();" + "\n").
            append("initializeMessages(oasisMessages, displayMessagesOnParent);" + "\n").
            append("var messageFieldId = '" + messageFieldId + "';" + "\n").
            append("var messageRowId = '" + messageRowId + "';" + "\n").
            append("var messageGridId = '" + messageGridId + "';" + "\n");
        util.write(pageContext, sb.append("</script>\n").toString());

        int rc = super.doStartTag();
        l.exiting(getClass().getName(), "doStartTag");
        return rc;
    }

    private String errorStyleClass="errormessage";
    private String warningStyleClass="warningmessage";
    private String informationStyleClass="infomessage";
    private String confirmationStyleClass="confmessage";
    private boolean hideError=false;
    private boolean hideWarning=false;
    private boolean hideInformation=false;
    private boolean hideConfirmation=true;
    private boolean showAllMessages=false;
    private boolean displayMessagesOnParent=false;
}
