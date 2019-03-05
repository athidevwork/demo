//-----------------------------------------------------------------------------
// javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 10/14/2010        tzhao      issue#109875 - Modified money format script to support multiple currency.
// 03/07/2013        adeng      issue#138243 - Added handleOnLoad() to call the parent window's function hideShowButton()
//                                             to hide/show Issue, Batch Print, ReRate, and Delete WIP buttons when the
//                                             exclusion indicator is on for all policies or not in selected event.
//08/13/2014         kxiang     issue#156446 - a.Added batchRenewalDetailProcessForm_btnClick() to active select-all/
//                                             de-select-all checkbox.
//                                             b.Added label "Exclude" behind gridHd checkbox when handleOnLoad() called.
//08/27/2014        kxiang      issue#156446 - Modified handleOnLoad():
//                                             a.When isAllExcluded is 'Y', selected SELECT_ALL checkbox.
//                                             b.When process code is not  PRERENEWAL, disabled HCSELECT_IND.
//                                             Modified userRowchange(), when one row is changed, set UPDATE_IND to 'Y'.
//                                             Modified batchRenewalDetailProcessForm_btnClick():
//                                             a.If SELECT_ALL checkbox is changed, set all records UPDATE_IND to 'Y'.
//                                             b.Located to the first record.
//                                             c.Set parentWindow detailUpdated to 'Y', as changes happened in this
//                                             window.
//-----------------------------------------------------------------------------
function handleOnLoad() {
    //add label "Exclude" for select-all checkbox.
    var gridHd_Checkbox = "";
    var	excludeLabel = "<a class =\"gridheader\" href=\"javascript:void(0)\"> Exclude </a>";
    if(hasObject("HCSELECT_IND")){
        gridHd_Checkbox = getObject("HCSELECT_IND").innerHTML;
        getObject("HCSELECT_IND").innerHTML =  gridHd_Checkbox + excludeLabel;
    }

    //only when process code is PRERENEWAL, "isAllExcluded" is not empty.
    var isAllExcluded = getObjectValue("isAllExcluded");
    if (!isEmpty(isAllExcluded)) {
        window.frameElement.document.parentWindow.hideShowButtons(isAllExcluded);
        if (isAllExcluded == 'Y') {
            if(hasObject("chkCSELECT_ALL")){
                getObject("chkCSELECT_ALL").checked = true;
            }
        }
    }
    else {
        if(hasObject("HCSELECT_IND")){
            getObject("HCSELECT_IND").disabled = true;
        }
    }
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'FILTER':
            var policyNo = getObjectValue("policyNoFilter");
            var viewBy = getObjectValue("viewByFilter");
            window.frameElement.document.parentWindow.loadBatchRenewalDetails(policyNo, viewBy);
            break;
        case 'SAVE':
            if (isChanged || isPageGridsDataChanged()) {
                getObject("txtXML").value = getChanges(batchRenewalDetailListGrid1);
                document.forms[0].process.value = "saveAllExcludePolicy";
                submitFirstForm();
                window.frameElement.document.parentWindow.setDetailUpdate("N");
                //clear unsave flag of parent page
                window.frameElement.document.parentWindow.setUnchanged();
                window.frameElement.document.parentWindow.setForceReload();
            }
            break;
    }
}

function userRowchange(c) {
    window.frameElement.document.parentWindow.setDetailUpdate("Y");
    window.frameElement.document.parentWindow.isChanged = true;

    var objName = c.name;
    if (objName == 'chkCSELECT_IND') {
        if (batchRenewalDetailListGrid1.recordset("UPDATE_IND").value == "N"){
            batchRenewalDetailListGrid1.recordset("UPDATE_IND").value = "Y";
        }
    }
}

/**
  Get updated records only
*/
function getChanges(XMLData)
{
    var modXML = XMLData.documentElement.selectNodes("//ROW[UPDATE_IND='Y']");
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
//--------------------------------------------------------
//  select all/ de-select all Exclude checkbox
//--------------------------------------------------------
function batchRenewalDetailProcessForm_btnClick(asBtn){
    updateAllSelectInd(asBtn);
    batchRenewalDetailListGrid_updatenode('UPDATE_IND', 'Y');
    first(batchRenewalDetailListGrid1);
    window.frameElement.document.parentWindow.setDetailUpdate("Y");
}
