//-----------------------------------------------------------------------------
// for cancellation detail page
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 01/11/2011       ryzhao      113558 - Add a new carrier field in the cancellation page.
// 01/14/2011       syang       105832 - Added handleOnChange() to handle discipline decline list.
// 06/21/2013       adeng       117011 - 1) Modified handleOnValidationDone() to pass one more parameter "transactionComment2".
//                                       2) Modified validateCancellationDetail() to set the new object
//                                          "transactionComment2" to input form field "newTransactionComment2".
// 03/10/2017       wrong       180675 - Modified to use getReturnCtxOfDivPopUp function to get parent window for tab style.
// 07/12/2017       lzhang      186847 - Reflect grid replacement project changes
// 11/02/2018       clm         195889 -  Grid replacement using getParentWindow and closeWindow
//-----------------------------------------------------------------------------
var divPopup = getParentWindow().getDivPopupFromDivPopupControl(this.frameElement);
var divPopupId = divPopup.id;

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'DONE':
        // Implement logics: Alternate Flow:  Amalgamate to New Policy
        // If Amalgamate is enabled and the amalgamate method is NEW
            var isAmalgamateEnabled = getObjectValue("amalgamationB");
            var method = getObjectValue("amalgamationMethod");

            if (isAmalgamateEnabled == "Y" && method == "NEW") {
                // execute amalgamate to new policy logics
                selectPolicyHolderForCreatePolicy("PM", "POLICY");
            }
            else {
                validateCancellationDetail();
            }

            break;
    }
}

function handleOnValidationDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            // Refresh parent token since it uses parent page's token value to submit the action
            var functionExists = eval("getReturnCtxOfDivPopUp(divPopupId).refreshStrutsToken");
            if (functionExists) {
                getReturnCtxOfDivPopUp(divPopupId).refreshStrutsToken();
            }

            var amalgamationB;
            if (getObject("amalgamationB") && getObjectValue("amalgamationB") != "Y") {
                amalgamationB = "N";
            }
            else {
                amalgamationB = "Y";
            }

            var successMessage = getMessage("pm.amalgamation.success.info", new Array(getObjectValue("amalgamationTo")));
            closeWindow(function () {getReturnCtxOfDivPopUp(divPopup.id).handleOnCaptureCancellationDone(
                getObject("cancellationDate").value,
                getObject("accountingDate").value,
                getObject("cancellationType").value,
                getObject("cancellationReason").value,
                getObject("cancellationMethod").value,
                getObject("cancellationAddOccupant").value,
                getObject("cancellationComments").value,
                getObject("transactionComment2").value,
                amalgamationB,
                getObject("amalgamationMethod").value,
                getObject("amalgamationTo").value,
                getObject("claimsAccessIndicator").value,
                successMessage,
                getObject("carrier").value,
                getObject("markAsDdl").value,
                getObject("ddlReasonForRisk").value,
                getObject("ddlCommentsForRisk").value,
                ddlListGrid1
                )});

        }
    }
}

function validateCancellationDetail() {
    if (divPopup && validate(document.forms[0], true)) {
        var parentWindow = getReturnCtxOfDivPopUp(divPopup.id);

        setInputFormField("cancellationDate", getObjectValue("cancellationDate"));
        setInputFormField("accountingDate", getObjectValue("accountingDate"));
        setInputFormField("cancellationType", getObjectValue("cancellationType"));
        setInputFormField("cancellationReason", getObjectValue("cancellationReason"));
        setInputFormField("cancellationMethod", getObjectValue("cancellationMethod"));
        setInputFormField("cancellationAddOccupant", getObjectValue("cancellationAddOccupant"));
        setInputFormField("cancellationComments", getObjectValue("cancellationComments"));
        setInputFormField("newTransactionComment2", getObjectValue("transactionComment2"));
        setInputFormField("cancelItemEffDate", parentWindow.getObjectValue("cancelItemEffDate"));
        setInputFormField("cancelItemExpDate", parentWindow.getObjectValue("cancelItemExpDate"));
        setInputFormField("carrier", getObjectValue("carrier"));
        var url = getAppPath() + "/transactionmgr/cancelprocessmgr/performCancellation.do?";
        postAjaxSubmit(url, "validateCancellationDetail", false, false, handleOnValidationDone);

    }
}

//-----------------------------------------------------------------------------
// Get policy no from "create policy" page
//-----------------------------------------------------------------------------
function amalgamateToNewPolicyDone(policyNo) {
    setObjectValue("amalgamationTo", policyNo);
    validateCancellationDetail();
}

//-----------------------------------------------------------------------------
// System displays discipline decline list div while markAsDdl is "Y".
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    if (obj.name == "markAsDdl") {
        if (obj.value == "Y") {
            var cancelLevel = getObjectValue("cancellationLevel");
            if ("POLICY" == cancelLevel) {
                hideShowElementByClassName(getObject("disciplineDeclineListDiv"), false);
                hideShowElementByClassName(getObject("disciplineDeclineListDivForRisk"), true);
            } else if ("RISK" == cancelLevel) {
                hideShowElementByClassName(getObject("disciplineDeclineListDiv"), true);
                hideShowElementByClassName(getObject("disciplineDeclineListDivForRisk"), false);
            }
        }
        else {
            hideShowElementByClassName(getObject("disciplineDeclineListDiv"), true);
            hideShowElementByClassName(getObject("disciplineDeclineListDivForRisk"), true);
        }
    }
}

function ddlList_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}