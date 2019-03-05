//-----------------------------------------------------------------------------
// Javascript file for renewalMailingEventQuestionnaire.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Sep 14, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/10/2010       syang       Issue 106500 - Changed Received and Resend values from "Y/N" to "-1/0" to display them in checkbox. 
// 10/14/2010        tzhao      issue#109875 - Modified money format script to support multiple currency.
// 09/10/2012       tcheng      Issue 137095 - Modified findRenewalMailingEventQuestion to handle single quote query
// 10/31/2013       xnie        Issue 148240 - Added function displayPolicy() to open policy information page with
//                                             passed in term.
// 11/06/2014       kxiang      Issue 158411 - Modified function displayPolicy() to change the send url.
// 07/12/2017       lzhang      Issue 186847 - Reflect grid replacement project changes
//-----------------------------------------------------------------------------
var parentWindow = window.frameElement.document.parentWindow;
function handleOnButtonClick(action) {
    switch (action) {
        case 'FIND':
            var policyNo = parentWindow.getObject("findPolicyNo").value;
            var name = parentWindow.getObject("findName").value;
            var event = parentWindow.getObject("event").value;
            findRenewalMailingEventQuestion(policyNo, name, event);
            break;
        case 'MARKALL':
            handleAllResend('MARKALL');
            break;
        case 'CLEARALL':
            handleAllResend('CLEARALL');
            break;
        case 'ADD':
            parentWindow.handleOnButtonClick(action);
            break;
        case 'RESPONSE':
            parentWindow.handleOnButtonClick(action);
            break;
        case 'FILES':
            parentWindow.handleOnButtonClick(action);
            break;
       case 'PTNOTES':
            var sPolicyNo = renewalMailingEventQuestionListGrid1.recordset("CPOLICYNO").value;
            var sRiskBaseRecordId = renewalMailingEventQuestionListGrid1.recordset("CRISKBASERECORDID").value;
           var url = getAppPath() + "/notesmgr/maintainPartTimeNotes.do?"
               + commonGetMenuQueryString() + "&process=loadAllPartTimeNotes&policyNumber=" +
                     sPolicyNo + "&riskBaseRecordId=" + sRiskBaseRecordId + "&refreshNoteStatus=Y";
           // Have to invoke divp popuup from parent window to get better UI
            var processingDivId = parentWindow.openDivPopup("", url, true, true, "", "", 900, 600, "", "", "", false);
            break;
        default:break;
    }
}
//Find the questionnaires for the current mailing event.Actually, system invokes grid filter to find questionnaires.
function findRenewalMailingEventQuestion(policyNo, name, event) {
    var filter_parameter = "";
    var policyNoExist = false;
    var nameExist = false;
    if (!isEmpty(policyNo)) {
        policyNoExist = true;
        filter_parameter = "CPOLICYNO='" + escape(policyNo) + "'";
    }
    if (!isEmpty(name)) {
        nameExist = true;
        if (policyNoExist) {
            filter_parameter = filter_parameter + " and ";
        }
        filter_parameter = filter_parameter + "CPOLICYHOLDERNAMEUPCASE[contains(.,'" + escape(name.toUpperCase()) + "')]";
    }
    if (!isEmpty(event)) {
        if (event != 'ALL') {
            if (nameExist || policyNoExist) {
                filter_parameter = filter_parameter + " and ";
            }
            switch (event) {
                case 'RECEIVED':
                    filter_parameter = filter_parameter + "CRECEIVEDB = '-1'";
                    break;
                case 'CAPTURED':
                    filter_parameter = filter_parameter + "CCAPTUREDB = '-1'";
                    break;
                case 'NOTRECEIVED':
                    filter_parameter = filter_parameter + "CRECEIVEDB = '0'";
                    break;
                case 'NOTCAPTURED':
                    filter_parameter = filter_parameter + "CCAPTUREDB = '0'";
                    break;
                default:break;
            }
        }
    }
    // Filter out the questionnaires.
    setTableProperty(eval("renewalMailingEventQuestionListGrid"), "selectedTableRowNo", null);
    renewalMailingEventQuestionListGrid_filter(filter_parameter);
    var selectedDataGrid = getXMLDataForGridName("renewalMailingEventQuestionListGrid");
    if (isEmptyRecordset(selectedDataGrid.recordset)) {
        hideEmptyTable(getTableForXMLData(selectedDataGrid));
        hideShowElementByClassName(getObject("renewQuestionFields"), true);
    }
    else {
        hideShowElementByClassName(getObject("renewQuestionFields"), false);
        showNonEmptyTable(getTableForXMLData(selectedDataGrid));
    }
}
// Handle for mark all and clear all options.
function handleAllResend(action) {
    var selectedDataGrid = getXMLDataForGridName("renewalMailingEventQuestionListGrid");
    if (!isEmptyRecordset(selectedDataGrid.recordset)) {
        var selectedId = selectedDataGrid.recordset("ID").value;
        if (action == 'MARKALL') {
            first(renewalMailingEventQuestionListGrid1);
            while (!renewalMailingEventQuestionListGrid1.recordset.eof) {
                renewalMailingEventQuestionListGrid1.recordset("UPDATE_IND").value = 'Y';
                renewalMailingEventQuestionListGrid1.recordset("CRESENDB").value = '-1';
                next(renewalMailingEventQuestionListGrid1);
            }
        }
        else if (action == 'CLEARALL') {
            first(renewalMailingEventQuestionListGrid1);
            while (!renewalMailingEventQuestionListGrid1.recordset.eof) {
                renewalMailingEventQuestionListGrid1.recordset("UPDATE_IND").value = 'Y';
                renewalMailingEventQuestionListGrid1.recordset("CRESENDB").value = '0';
                next(renewalMailingEventQuestionListGrid1);
            }
        }
        selectRowById('renewalMailingEventQuestionListGrid', selectedId);
    }
}
// Get changed RecordSet.
function getChangesList() {
    var modXML = renewalMailingEventQuestionListGrid1.documentElement.selectNodes("//ROW[UPDATE_IND='Y']");
    var nodelen = modXML.length;
    var i;
    var j;
    var rowNode;
    var columnNode;
    var numColumnNodes;
    var result;
    var ID;
    var displayInd;
    var displayRows = "";
    var nonDisplayRows = "";
    for (i = 0; i < nodelen; i++) {
        rowNode = modXML.item(i);
        ID = rowNode.getAttribute("id");
        // Exclude rows with id=-9999 only if there is at least one real row because they are newly added rows that were deleted.
        if (ID != "-9999" || nodelen == 1) {
            displayInd = "";
            result = '<ROW id="' + ID + '">'
            if (rowNode.hasChildNodes() == true) {
                numColumnNodes = rowNode.childNodes.length;
                for (j = 0; j < numColumnNodes; j++) {
                    columnNode = rowNode.childNodes.item(j);
                    var nodeValue = encodeXMLChar(columnNode.text);
                    if(moneyFormatPattern.test(nodeValue)){
                        nodeValue = unformatMoneyStrValAsStr(nodeValue);
                    }
                    result += "<" + columnNode.nodeName + ">" + nodeValue + "</" + columnNode.nodeName + ">";
                    if (columnNode.nodeName == "DISPLAY_IND")
                        displayInd = nodeValue;
                }
            }
            result += "</ROW>";
            if (displayInd == "Y")
                displayRows += result;
            else
                nonDisplayRows += result;
        }
    }
    result = "<ROWS>" + displayRows + nonDisplayRows + "</ROWS>";
    return result;
}
// Check whether this grid date is changed.
function isQuestionnaireGridDataChanged() {
    return isGridDataChanged("renewalMailingEventQuestionListGrid");
}
// When underwriter selects RESEND in print option page, system should set the resentB of all policy record from N to Y.
// And capture the max last resend number.
function changeReceivedBToYES() {
    var maxLastResendNumber = 0;
    first(renewalMailingEventQuestionListGrid1);
    while (!renewalMailingEventQuestionListGrid1.recordset.eof) {
        if (parseInt(renewalMailingEventQuestionListGrid1.recordset("CRESENTNO").value) > parseInt(maxLastResendNumber)) {
            maxLastResendNumber = renewalMailingEventQuestionListGrid1.recordset("CRESENTNO").value;
        }
        if (renewalMailingEventQuestionListGrid1.recordset("CRECEIVEDB").value == "0") {
            renewalMailingEventQuestionListGrid1.recordset("UPDATE_IND").value = 'Y';
            renewalMailingEventQuestionListGrid1.recordset("CRESENDB").value = '-1';
        }
        next(renewalMailingEventQuestionListGrid1);
    }
    return maxLastResendNumber;
}
// When underwriter selects Files option, system determines the policy renew form id of current renewal questionnaire.
function currentPolicyRenewFormId() {
    var selectedDataGrid = getXMLDataForGridName("renewalMailingEventQuestionListGrid");
    if (!isEmptyRecordset(selectedDataGrid.recordset)) {
        return selectedDataGrid.recordset("CPOLICYRENEWFORMID").value;
    }
    else {
        return 0;
    }
}
// This function is used to check whether all the received indication is 'Y'
function isAllQuestionnaireReceived() {
    first(renewalMailingEventQuestionListGrid1);
    while (!renewalMailingEventQuestionListGrid1.recordset.eof) {
        if (renewalMailingEventQuestionListGrid1.recordset("CRECEIVEDB").value != '-1') {
            return false;
        }
        next(renewalMailingEventQuestionListGrid1);
    }
    return true;
}
// Get the url of response.
function getResponseURL(){
   var url = "";
   var selectedDataGrid = getXMLDataForGridName("renewalMailingEventQuestionListGrid");
    if (!isEmptyRecordset(selectedDataGrid.recordset)) {
        var policyNo = selectedDataGrid.recordset("CPOLICYNO").value;
        var riskId = selectedDataGrid.recordset("CRISKBASERECORDID").value;
        var policyRenewFormId = selectedDataGrid.recordset("CPOLICYRENEWFORMID").value;
        url = "&policyNoCriteria="+policyNo+"&riskId="+riskId+"&policyRenewFormId="+policyRenewFormId;
    }
    return url;
}

//-----------------------------------------------------------------------------
// update note status
//-----------------------------------------------------------------------------
function refreshNoteStatus(hasNotes) {
    if (hasNotes) {
        renewalMailingEventQuestionListGrid1.recordset("CSPECIALHANDLINGNOTE").value = "X";
    }
    else {
        renewalMailingEventQuestionListGrid1.recordset("CSPECIALHANDLINGNOTE").value = "";
    }
    maintainNoteImageForAllNoteFields();
}

//-----------------------------------------------------------------------------
// Update the field's value when the checkbox is checked or unchecked.
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    var fieldName = field.name;
    if ((fieldName == "receivedB" || fieldName == "resendB") && "checkbox" == field.type) {
        if (field.checked) {
            if (fieldName == "receivedB") {
                renewalMailingEventQuestionListGrid1.recordset("CRECEIVEDB").value = '-1';
            }
            if (fieldName == "resendB") {
                renewalMailingEventQuestionListGrid1.recordset("CRESENDB").value = '-1';
            }
        }
        else {
            if (fieldName == "receivedB") {
                renewalMailingEventQuestionListGrid1.recordset("CRECEIVEDB").value = '0';
            }
            if (fieldName == "resendB") {
                renewalMailingEventQuestionListGrid1.recordset("CRESENDB").value = '0';
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Handle case when user click policy number, system should go to policy page
// and display relevant term information.
//-----------------------------------------------------------------------------
function displayPolicy(pk) {
    var selectedDataGrid = getXMLDataForGridName("renewalMailingEventQuestionListGrid");
    var selectedPolicyNo = selectedDataGrid.recordset("CPOLICYNO").value;
    var policyURL = getAppPath() + "/policymgr/findPolicy.do?isGlobalSearch=Y&policyNoCriteria=" + selectedPolicyNo +
            "&policyTermHistoryId=" + pk + "&termStatusCode=ALL&process=findAllPolicy";
    window.open(policyURL, "", "location=yes,menubar=yes,toolbar=yes,scrollbars=yes,directories=no,resizable=yes,opyhistory=no");
}