function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Done':
            first(selectQuoteRiskCovgGrid1);
            var covgList = "";
            while (!selectQuoteRiskCovgGrid1.recordset.eof) {
                if (selectQuoteRiskCovgGrid1.recordset("CSELECT_IND").value == -1) {
                    var covgId = selectQuoteRiskCovgGrid1.recordset("CCOVERAGEBASERECORDID").value;
                    covgList = covgList + covgId + ",";
                }
                next(selectQuoteRiskCovgGrid1);
            }
            var retroDate = getObject("retroDate").value;
            window.frameElement.document.parentWindow.handleOnSelectQuoteRiskCoverage(retroDate, covgList);
            closeThis();
            break;

        case 'Cancel':
            closeThis();
            break;
    }

    return true;
}

function closeThis() {
    var divPopup = window.frameElement.document.parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
    if (divPopup) {
        window.frameElement.document.parentWindow.closeDiv(divPopup);
    }
}

function selectQuoteRiskCovg_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}
