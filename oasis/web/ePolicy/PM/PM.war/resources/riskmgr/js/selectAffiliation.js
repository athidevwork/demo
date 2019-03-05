// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 11/02/2018       clm         195889 -  Grid replacement using setObjectValue and getParentWindow
//-----------------------------------------------------------------------------
var affiliationListGridtxtXML = "affiliationListGridtxtXML";
function handleOnButtonClick(btn) {
    switch (btn) {
        case "DONE":
            var affiGridId = 'affiliationListGrid';
            var xmlData = getXMLDataForGridName(affiGridId);
            var parentWindow = getParentWindow();


            if (!isEmptyRecordset(xmlData.recordset)) {                
                alternateGrid_update(affiGridId, "CSELECT_IND = '-1' or CSELECT_IND = '0'");
                parentWindow.setInputFormField(affiliationListGridtxtXML, getObjectValue(affiliationListGridtxtXML));
                var selectedNodes = xmlData.documentElement.selectNodes("//ROW[CSELECT_IND = -1]");
                if (selectedNodes.length > 0) {
                    parentWindow.setObjectValue("affiliationSelectedB", "Y");
                }
                else {
                    parentWindow.setObjectValue("affiliationSelectedB", "N");
                }

                var selectedAffiIds = new Array(selectedNodes.length);
                for (var i = 0; i < selectedNodes.length; i++) {
                    selectedAffiIds[i] = selectedNodes.item(i).getAttribute("id");
                }
                parentWindow.selectedAffiIds = selectedAffiIds;

                var affiSeledField = parentWindow.getObject("affiliationSelectedB");
                parentWindow.getObject("affiliationSelectedBLOVLABELSPAN").innerText =
                affiSeledField.options[affiSeledField.selectedIndex].text;

                closeThisDivPopup(true);
            }else{
                closeThisDivPopup(true);
            }
            break;
    }
}

function handleOnLoad() {
    var parentWindow = getParentWindow();
    var selectedAffiIds = parentWindow.selectedAffiIds;
    var XMLData = getXMLDataForGridName("affiliationListGrid");
    if (!isEmptyRecordset(XMLData.recordset)) {
        var absPosition = XMLData.recordset.AbsolutePosition;
        first(XMLData);
        while (!XMLData.recordset.eof) {
            if (isInArray(selectedAffiIds, XMLData.recordset('ID').value)) {
                XMLData.recordset('CSELECT_IND').value = "-1";
            }
            next(XMLData);
        }
        first(XMLData);
        XMLData.recordset.move(absPosition - 1);
    }
}

function selectAffiliationForm_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
    // when clicking the top checkbox to check all records, the form will lost connection with the grid, force to select the first record.
    first(affiliationListGrid1);
    selectFirstRowInGrid("affiliationListGrid");
}

function isInArray(vArray, vStr) {
    for (var i = 0; i < vArray.length; i++) {
        if (vStr == vArray[i]) {
            return true;
        }
    }
    return false;
}

