//-----------------------------------------------------------------------------
// Javascript file for generateRenewalQuestionnaire.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   May 19, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 05/19/2010       syang       107547 - Added userReadyStateReady to handle the empty row in grid.
// 10/14/2010       tzhao      issue#109875 - Modified money format script to support multiple currency.
// 04/20/2011       dzhang      119777 - Modified handleOnChange() to avoid js error.
// 06/26/2014       xnie        151631 - Reverted 107547 fix due to 107547 is fixed by 108878.
//-----------------------------------------------------------------------------
function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SEARCH':
            document.forms[0].process.value = "loadAllRenewalQuestionnaire";
            break;
        default : proceed = false;
    }
    return proceed;
}
function handleOnButtonClick(action) {
    switch (action) {
        case 'SEARCH':
            if (validateSelectedQuestionnaire(renewalQuestionnaireListGrid1)) {
                if (confirm(getMessage("pm.generateRenewalQuestionnaire.abandonGenerateQuestionnaire"))) {
                    handleOnButtonClick('GENERATE');
                    break;
                }
            }
            commonOnSubmit('SEARCH');
            break;
        case 'GENERATE':
            alternateGrid_update('renewalQuestionnaireListGrid', "CSELECT_IND = '-1' or CSELECT_IND = '0'");
            document.forms[0].txtXML.value = getChanges(renewalQuestionnaireListGrid1);
            document.forms[0].process.value = "generateRenewalQuestionnaire";
            postAjaxSubmit("/renewalquestionnairemgr/generateRenewalQuestionnaire.do", "generateRenewalQuestionnaire", false, false, handleOnGetInitialValues);
            break;
        default:break;
    }
}
function generateQuestionnaireInformation() {
    if (document.forms[0].generateQuestionnaire.value == "Y") {
        document.forms[0].generateQuestionnaire.value = "N";
        // Deselect the generate questionnaire.
        renewalQuestionnaireList_btnClick("DESELECT");
        // Popup page.
        var generateUrl = getAppPath() + "/renewalquestionnairemgr/generateRenewalQuestionnaireInfo.do?"
            + commonGetMenuQueryString() + "&process=loadAllRenewalQuestionnaireInformation";
        var divPopupId = openDivPopup("", generateUrl, true, true, "", "", 800, 500, "", "", "", false);
    }
}
function handleOnChange(sField) {
    var fieldName = sField.name;
    var fieldValue = sField.value;
    switch (fieldName) {
        case 'endSearchDate':
            if (hasObject("deadlineDateAvailable")) {
                var url = getAppPath() + "/renewalquestionnairemgr/generateRenewalQuestionnaire.do?"
                        + "process=getDefaultDeadlineDate" + "&deadlineDateAvailable=" + getObjectValue("deadlineDateAvailable")
                        + "&endSearchDate=" + fieldValue;
                new AJAXRequest("get", url, '', handleOnResetDeadlineDate, false);
            }
            break;
        default : return;
    }
}
function validateSelectedQuestionnaire(xmlData) {
    var selectedRows = xmlData.documentElement.selectNodes("//ROW[CSELECT_IND=-1]");
    if (selectedRows.length <= 0) {
        return false;
    }
    else {
        return true;
    }
}
function renewalQuestionnaireList_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}
// Reset Deadline Date.
function handleOnResetDeadlineDate(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setFormFieldValuesByObject(oValueList[0]);
            }
        }
    }
}
// Reset Deadline Date.
function handleOnGetInitialValues(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data)) {
                return;
            }
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                document.forms[0].generateQuestionnaire.value = oValueList[0]["generateQuestionnaire"];
                generateQuestionnaireInformation();
            }
        }
    }
}
// Get the selected questionnaires.
function getChanges(ReferenceXML) {
    var modXML = ReferenceXML.documentElement.selectNodes("//ROW[CSELECT_IND='-1']");
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
