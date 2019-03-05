//-----------------------------------------------------------------------------
// Javascript file for selectAssociatedRisk.jsp.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:   Mar 07, 2011
// Author: Dzhang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 11/25/2011       dzhang     127324 - If the Select Associated Risk action invoked by user manully not invoked by
//                                      workflow automatically, no need to refresh the parent window to invoke workflow.
//-----------------------------------------------------------------------------

function handleOnLoad() {
    if (hasObject("isInWorkflow") && getObjectValue("isInWorkflow") == "Y") {
        hideShowField(getObject('PM_SEL_ASSO_RISKC'), true);
    }
}

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'SELECT':
            if (!isEmptyRecordset(selectAssociatedRiskGrid1.recordset)) {
                // Opened by work flow automatically, after selected done, refresh the parent page to invoke work flow to
                // display the IBNR main page.
                if (hasObject("isInWorkflow") && getObjectValue("isInWorkflow") == "Y" && !hasObject("notInvokeWorkFlow")) {
                    document.forms[0].action = getAppPath() + "/riskmgr/ibnrriskmgr/selectAssociatedRisk.do?"
                            + "toAssociatedRiskId=" + selectAssociatedRiskGrid1.recordset("ID").value
                            + "&date=" + new Date();
                    document.forms[0].process.value = 'processCancelIbnrRisk';
                    submitFirstForm();
                    closeThisDivPopup(true);
                    window.frameElement.document.parentWindow.refreshPage();
                }
                // Opened by Click Change button in the top section of IBNR main page.
                else if (hasObject("openFrom") && (getObjectValue("openFrom") == "CHANGEASSO")) {
                    var url = getAppPath() + "/riskmgr/ibnrriskmgr/maintainIbnrRisk.do?"
                            + "&process=processChangeAssociatedRisk"
                            + "&fromAssociatedRiskId=" + getObjectValue("orgAssociatedRiskId")
                            + "&toAssociatedRiskId=" + selectAssociatedRiskGrid1.recordset("ID").value
                            + "&transactionLogId=" + getObjectValue("transactionLogId")
                            + "&transEffDate=" + getObjectValue("transEffDate")
                            + "&date=" + new Date();
                    new AJAXRequest("get", url, '', handleChangeAssociatedRiskDone, false);
                }
                // Opened by Add button or name field's search button in the top section of IBNR main page.
                else {
                    var riskName = selectAssociatedRiskGrid1.recordset("CRISKNAME").value;
                    var riskBaseRecordId = selectAssociatedRiskGrid1.recordset("ID").value;
                    var riskEffectiveFromDate = selectAssociatedRiskGrid1.recordset("CRISKEFFECTIVEFROMDATE").value;
                    var riskEffectiveToDate = selectAssociatedRiskGrid1.recordset("CRISKEFFECTIVETODATE").value;
                    var entityId = selectAssociatedRiskGrid1.recordset("CENTITYID").value;
                    var productCoverageCode = selectAssociatedRiskGrid1.recordset("CPRODUCTCOVERAGECODE").value;
                    window.frameElement.document.parentWindow.handleOnSelectAssociatedRiskType(riskName, riskBaseRecordId,
                            riskEffectiveFromDate, riskEffectiveToDate, entityId, productCoverageCode);
                    closeThisDivPopup(true);
                }
            }

            break;

        case 'CANCEL':
            closeThisDivPopup(true);
            break;
    }

    return true;
}

function handleChangeAssociatedRiskDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            //closeThis();
            closeThisDivPopup(true);
            window.frameElement.document.parentWindow.refreshPage();
        }
    }
}

