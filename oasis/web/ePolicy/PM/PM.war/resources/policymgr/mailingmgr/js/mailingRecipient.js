//-----------------------------------------------------------------------------
// javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 10/14/2010       tzhao      issue#109875 - Modified money format script to support multiple currency.
// 07/18/2017       kxiang     186950 - Modified handleOnButtonClick to get rid of toLowerCase when get value for name.
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'ADD_RECIPIENT':
            commonAddRow("mailingRecipientListGrid");
            break;
        case 'DELETE_RECIPIENT':
            commonDeleteRow("mailingRecipientListGrid");
            break;
        case 'FIND_RECIPIENT':
            policyNo = getObjectValue("policyNoCriteria");
            name = getObjectValue("nameCriteria");
            window.frameElement.document.parentWindow.loadMailingRecipient(policyNo,name);
    }

}
function getChanges(XMLData)
{
    var modXML = XMLData.documentElement.selectNodes("//ROW[(UPDATE_IND='Y') or (UPDATE_IND='D') or (UPDATE_IND='I')]");
    var nodelen = modXML.length;
    var rowNode;
    var columnNode;
    var numColumnNodes;
    var result;
    var ID;
    var displayInd;
    var displayRows = "";
    var nonDisplayRows = "";

    for (var i = 0; i < nodelen; i++) {
        rowNode = modXML.item(i);
        ID = rowNode.getAttribute("id");

        // Exclude rows with id=-9999 only if there is at least one real row because they are newly added rows that were deleted.
        if (ID != "-9999" || nodelen == 1) {
            displayInd = "";

            result = '<ROW id="' + ID + '">'
            if (rowNode.hasChildNodes()) {
                numColumnNodes = rowNode.childNodes.length;
                for (var j = 0; j < numColumnNodes; j++) {
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
function handleOnChange(field) {
    if (field.name == 'policyNo') {
        var policyNo = getObjectValue("policyNo");
        if (!isEmpty(policyNo)) {
            var path = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do?"
                + "policyNo=" + policyNo + "&process=validateMailingRecipient";
            new AJAXRequest("get", path, '', validateMailingRecipient, false);
        } else{
            document.forms[0].name.value="";
        }
    }
   else if (field.name == 'receivedB') {
        var receivedB = getObjectValue("receivedB");
        if (receivedB=='Y') {
             document.forms[0].receivedDate.disabled=false;
             document.forms[0].receivedDate.value=formatDate(new Date(),"mm/dd/yyyy")
        } else if(receivedB=='N') {
             document.forms[0].receivedDate.value="";
            document.forms[0].receivedDate.disabled=true;
        }
    }
}
function validateMailingRecipient(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                document.forms[0].name.value="";
                return false;
            }
            if (data.getElementsByTagName("NAME")[0].firstChild) {
                var name = data.getElementsByTagName("NAME")[0].firstChild.data;
                var policyId=data.getElementsByTagName("POLICYID")[0].firstChild.data;
                document.forms[0].name.value=name;
                document.forms[0].policyId.value=policyId;
            }

        }
    }

}
function mailingRecipientListGrid_setInitialValues() {

    var policyMailingId = window.frameElement.document.parentWindow.policyMailingId;

    var url = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do?" +
              "policyMailingId=" + policyMailingId + "&process=getInitialValuesForMailingRecipient" + "&date=" + new Date();
    // initiate async call
    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}

function handleOnLoad(){
      var generateDate= window.frameElement.document.parentWindow.generateDate;
    if (!isEmpty(generateDate)) {
       hideShowField(getObject('PM_MAILING_RECIPIENT_DEL'), true);
       hideShowField(getObject('PM_MAILING_RECIPIENT_ADD'), true);
    }    
}







