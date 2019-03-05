var addManualTailSuccess = false;
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SELECT':
            addManualTailSuccess = true;
            showProcessingDivPopup();
            var XMLData = getXMLDataForGridName("tailListGrid");
            if (!isEmptyRecordset(XMLData.recordset)) {
                var absPosition = XMLData.recordset.AbsolutePosition;
                first(XMLData);
                var selectCount = 0;
                while (!XMLData.recordset.eof) {
                    if (XMLData.recordset("CSELECT_IND").value == '-1') {
                        selectCount ++;
                        var tailCoverageCode = XMLData.recordset("CPRODUCTCOVCHILDID").value;
                        var relationTypeCode = XMLData.recordset("ID").value;
                        addManualTail(tailCoverageCode, relationTypeCode);
                    }
                    next(XMLData);
                }
                first(XMLData);
                XMLData.recordset.move(absPosition - 1);
            }

            if (selectCount > 0 && addManualTailSuccess == true) {
                window.frameElement.document.parentWindow.refreshPage();
            }
            else if (selectCount == 0) {
                closeProcessingDivPopup();
                handleError(getMessage("pm.selectTail.noSelectTail.error"));
            }
            else {
                closeProcessingDivPopup();
            }

            break;
    }
}

function tailList_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
    // when clicking the top checkbox to check all records,
    //the form will lost connection with the grid, force to select the first record.
    first(tailListGrid1);
    selectFirstRowInGrid("tailListGrid");
}

//-----------------------------------------------------------------------------
// add selected manual tail coverage
//-----------------------------------------------------------------------------
function addManualTail(tailCoverageCode, relationTypeCode) {
    var tailXmlData = window.frameElement.document.parentWindow.getXMLDataForGridName("coverageListGrid");
    var url = getAppPath() + "/tailmgr/maintainTail.do?"
        + commonGetMenuQueryString()
        + "&riskBaseRecordId=" + tailXmlData.recordset("CRISKBASERECORDID").value
        + "&coverageBaseRecordId=" + tailXmlData.recordset("CCOVERAGEBASERECORDID").value
        + "&productCovRelTypeCode=" + tailXmlData.recordset("CPRODUCTCOVRELTYPECODE").value
        + "&subCoverageB=" + tailXmlData.recordset("CSUBCOVERAGEB").value
        + "&tailCoverageCode=" + tailCoverageCode
        + "&relationTypeCode=" + relationTypeCode
        + "&process=addManualTail";
    new AJAXRequest("get", url, '', handleOnAddManualTailDone, false);
}

function handleOnAddManualTailDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                addManualTailSuccess = false;
                return;
            }
        }
    }
}
