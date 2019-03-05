// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 11/02/2018       clm         195889 -  Grid replacement
//-----------------------------------------------------------------------------
var coiListGridtxtXML = "coiListGridtxtXML";
function handleOnButtonClick(btn) {
    switch (btn) {
        case "DONE":
            var coiGridId = 'coiListGrid';
            var xmlData = getXMLDataForGridName(coiGridId);
            var parentWindow = getParentWindow();
            
            if (!isEmptyRecordset(xmlData.recordset)) {
                alternateGrid_update(coiGridId, "CSELECT_IND = '-1' or CSELECT_IND = '0'");
                parentWindow.setInputFormField(coiListGridtxtXML, getObjectValue(coiListGridtxtXML));
                var selectedNodes = xmlData.documentElement.selectNodes("//ROW[CSELECT_IND = -1]");
                if (selectedNodes.length > 0) {
                    parentWindow.getObject("coiSelectedB").value = "Y"
                }
                else {
                    parentWindow.getObject("coiSelectedB").value = "N"
                }

                var selectedCoiIds = new Array(selectedNodes.length);
                for (var i = 0; i < selectedNodes.length; i++) {
                    selectedCoiIds[i] = selectedNodes.item(i).getAttribute("id");
                }
                parentWindow.selectedCoiIds = selectedCoiIds;

                var coiSeledField = parentWindow.getObject("coiSelectedB");
                parentWindow.getObject("coiSelectedBLOVLABELSPAN").innerText =
                coiSeledField.options[coiSeledField.selectedIndex].text;

                closeThisDivPopup();
            }else{
                closeThisDivPopup();
            }
            break;
    }
}

function handleOnLoad() {
    var parentWindow = getParentWindow();
    var selectedCoiIds = parentWindow.selectedCoiIds;
    var XMLData = getXMLDataForGridName("coiListGrid");
    if (!isEmptyRecordset(XMLData.recordset)) {
        var absPosition = XMLData.recordset.AbsolutePosition;
        first(XMLData);
        while (!XMLData.recordset.eof) {
            if (isInArray(selectedCoiIds, XMLData.recordset('ID').value)) {
                XMLData.recordset('CSELECT_IND').value = "-1";
            }
            next(XMLData);
        }
        first(XMLData);
        XMLData.recordset.move(absPosition - 1);
    }
}


function selectCoiForm_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
    // when clicking the top checkbox to check all records, the form will lost connection with the grid, force to select the first record.
    first(coiListGrid1);
    selectFirstRowInGrid("coiListGrid");
}


function isInArray(vArray, vStr) {
    for (var i = 0; i < vArray.length; i++) {
        if (vStr == vArray[i]) {
            return true;
        }
    }
    return false;
}


