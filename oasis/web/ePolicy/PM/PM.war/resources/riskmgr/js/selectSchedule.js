var scheduleListGridtxtXML = "scheduleListGridtxtXML";
function handleOnButtonClick(btn) {
    switch (btn) {
        case "DONE":
            var scheduleGridId = 'scheduleListGrid';
            var xmlData = getXMLDataForGridName(scheduleGridId);
            var parentWindow = getParentWindow();
            var divPopup = parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
            if (!isEmptyRecordset(xmlData.recordset)) {
                alternateGrid_update(scheduleGridId, "CSELECT_IND = '-1' or CSELECT_IND = '0'");
                parentWindow.setInputFormField(scheduleListGridtxtXML, getObjectValue(scheduleListGridtxtXML));
                var selectedNodes = xmlData.documentElement.selectNodes("//ROW[CSELECT_IND = -1]");
                if (selectedNodes.length > 0) {
                    parentWindow.getObject("scheduleSelectedB").value = "Y"
                }
                else {
                    parentWindow.getObject("scheduleSelectedB").value = "N"
                }

                var selectedScheduleIds = new Array(selectedNodes.length);
                for (var i = 0; i < selectedNodes.length; i++) {
                    selectedScheduleIds[i] = selectedNodes.item(i).getAttribute("id");
                }
                parentWindow.selectedScheduleIds = selectedScheduleIds;
                var scheduleSelectedField = parentWindow.getObject("scheduleSelectedB");
                parentWindow.getObject("scheduleSelectedBLOVLABELSPAN").innerText =
                scheduleSelectedField.options[scheduleSelectedField.selectedIndex].text
                parentWindow.closeDiv(divPopup);
            }else{
                parentWindow.closeDiv(divPopup);
            }
            break;
    }
}

function handleOnLoad() {
    var parentWindow = getParentWindow();
    var selectedScheduleIds = parentWindow.selectedScheduleIds;
    var XMLData = getXMLDataForGridName("scheduleListGrid");
    if (!isEmptyRecordset(XMLData.recordset)) {
        var absPosition = XMLData.recordset.AbsolutePosition;
        first(XMLData);
        while (!XMLData.recordset.eof) {
            if (isInArray(selectedScheduleIds, XMLData.recordset('ID').value)) {
                XMLData.recordset('CSELECT_IND').value = -1;
            }
            next(XMLData);
        }
        first(XMLData);
        XMLData.recordset.move(absPosition - 1);
    }
}


function selectScheduleForm_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
    // when clicking the top checkbox to check all records, the form will lost connection with the grid, force to select the first record.
    first(scheduleListGrid1);
    selectFirstRowInGrid("scheduleListGrid");
}


function isInArray(vArray, vStr) {
    for (var i = 0; i < vArray.length; i++) {
        if (vStr == vArray[i]) {
            return true;
        }
    }
    return false;
}


