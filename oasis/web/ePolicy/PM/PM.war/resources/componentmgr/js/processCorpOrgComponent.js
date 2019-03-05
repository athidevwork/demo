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
//-----------------------------------------------------------------------------
var parentOrganizationName;
var parentOrganizationEntityId;

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'SEARCH':
            if (commonValidateForm()) {
                document.forms[0].process.value = "loadAllCorpOrgDiscountMember";
                setInputFormField("searchMember", "Y");
                showProcessingDivPopup();
                submitFirstForm();
            }
            break;
        case 'HISTORY':
            var corporgEntId = getObject("parentOrganizationEntityId").value;
            if (!isEmpty(corporgEntId)) {
                var historyUrl = getAppPath() + "/componentmgr/viewCorpOrgComponentHistory.do?corporgEntId=" + corporgEntId
                    + commonGetMenuQueryString() + "&process=loadAllCorpOrgComponentHistory";
                var divPopupId = openDivPopup("", historyUrl, true, true, "", "", 800, 600, "", "", "", false);
            }
            break;
        case 'PROCESS':
            alternateGrid_update('discountMemberListGrid', "CSELECT_IND = '-1' or CSELECT_IND = '0'");
            if (validateSelectedMember(discountMemberListGrid1)) {
                document.forms[0].txtXML.value = getChanges(discountMemberListGrid1);
                document.forms[0].process.value = "processDiscount";
                showProcessingDivPopup();
                submitFirstForm();
            }
            break;
        default: break;
    }
}

//-----------------------------------------------------------------------------
// This function won't be called when change the parentOrganizationName.
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    if (obj.name == "discountType") {
        // Use filter to clear the member list.
        discountMemberListGrid_filter("CPOLICYID = 0");
        var memberXmlData = getXMLDataForGridName("discountMemberListGrid");
        if (isEmptyRecordset(memberXmlData.recordset)) {
            hideEmptyTable(getTableForXMLData(memberXmlData));
        }
        else {
            showNonEmptyTable(getTableForXMLData(memberXmlData));
        }
        return true;
    }
    return true;
}

function find() {
    parentOrganizationName = getObject("parentOrganizationName").value;
    parentOrganizationEntityId = getObject("parentOrganizationEntityId").value;
    //create two fields to store the result from openEntitySelectWinFullName,then you can use it
    setInputFormField("newEntityId", 0);
    setInputFormField("newEntityName", "");
    openEntitySelectWinFullName("newEntityId", "newEntityName", "handleOnSelectEntity()");
}

function validateSelectedMember(xmlData) {
    var selectedRows = xmlData.documentElement.selectNodes("//ROW[CSELECT_IND=-1]");
    if (selectedRows.length <= 0) {
        return false;
    }
    else {
        return true;
    }
}

function discountMemberList_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}

function handleOnSelectEntity() {
    var changed = false;
    if (parentOrganizationName != getObjectValue("newEntityName")) {
        changed = true;
    }
    // Set the new parentOrganizationName.
    getObject("parentOrganizationName").value = getObjectValue("newEntityName");
    getObject("parentOrganizationEntityId").value = getObjectValue("newEntityId");
    // If the parentOrganizationName is changed, system research the member.
    if (changed) {
        setInputFormField("isHistoryAvailable", "Y");
        var functionExists = eval("window.pageEntitlements");
        if (functionExists) {
            pageEntitlements(false);
        }
        if (!isRequiredFieldEmpty()) {
            commonOnButtonClick("SEARCH");
        }
    }
}

function isRequiredFieldEmpty() {
    if (isEmpty(getObject("discountType").value)) {
        return true;
    }
    if (isEmpty(getObject("transactionEffectiveDate").value)) {
        return true;
    }
    if (isEmpty(getObject("parentOrganizationName").value)) {
        return true;
    }
}

// Get the selected member.
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