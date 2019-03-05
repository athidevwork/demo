
//-----------------------------------------------------------------------------
// Javascript file for viewFund.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 10/27/2010       gzeng       113516 - Fix a bug for filtering in grid.
// 04/22/2011       dzhang      117338 - Added handleReadyStateReady.
// 08/01/2011       ryzhao      118806 - Added a new case "detailType" to function handleOnChange().
// 07/19/2013       adeng       146439 - 1) Added a new function calTransTotal() to recalculate transaction total when
//                                       change the drop down.
//                                       2) Modified handleOnChange() to always display Transaction Total.
// 05/14/2014       adeng       154274 - Modified calTransTotal() to convert
//                                       resultWrittenPremium & resultDeltaAmount to String before formatting.
// 03/10/2017       wli         180675 - Used getOpenCtxOfDivPopUp() to call openDivPopup().
//-----------------------------------------------------------------------------
function handleOnButtonClick(btn) {
    switch (btn) {
        case 'VIEW_LOG':
            var transactionLogValue = getObjectValue("transactionLogId");
            var transactionClause = "transactionLogId=" + transactionLogValue;
            var viewRatingLogUrl = getAppPath() + "/policymgr/premiummgr/viewRatingLog.do?"
                + commonGetMenuQueryString() + "&process=loadAllRatingLog&" + "showMoreFlag=N&" + transactionClause;
            var divPopupId = getOpenCtxOfDivPopUp().openDivPopup("", viewRatingLogUrl, true, true, "", "", "", "", "", "", "", false);
            break;
    }
}

function handleOnChange(field) {
    var fieldName = field.name;
    var fieldValue = field.value;
    switch (fieldName) {
    //changeRecord's value=-1 or others, !=0
        case "changeRecord":
            var riskValue = getObjectValue("riskBaseRecordId");
            var changeRecordValue = getObjectValue("changeRecord");
            setTableProperty(eval("fundListGrid"), "selectedTableRowNo", null);
            if (changeRecordValue == -1) {
                if (riskValue == -1) {
                    fundListGrid_filter("");
                }
                else {
                    fundListGrid_filter("CRISKID='' or CRISKID=" + riskValue);
                }
            }
            else {
                if (riskValue == -1) {
                    fundListGrid_filter("CDELTAB='' or CDELTAB='" + changeRecordValue + "'");
                }
                else {
                    fundListGrid_filter("CDELTAB='' or CDELTAB='" + changeRecordValue + "' and CRISKID =" + riskValue);
                }
            }
            if (isEmptyRecordset(fundListGrid1.recordset)) {
                hideEmptyTable(getTableForXMLData(fundListGrid1));
            }
            else {
                 showNonEmptyTable(getTableForXMLData(fundListGrid1));
            }
            calTransTotal();
            break;
        case "riskBaseRecordId":
            var riskValue = getObjectValue("riskBaseRecordId");
            var changeRecordValue = getObjectValue("changeRecord");
            setTableProperty(eval("fundListGrid"), "selectedTableRowNo", null);
            if (changeRecordValue == -1) {
                if (riskValue == -1) {
                    fundListGrid_filter("");
                }
                else {
                    fundListGrid_filter("CRISKID='' or CRISKID=" + riskValue);
                }
            }
            else {
                if (riskValue == -1) {
                    fundListGrid_filter("CDELTAB='' or CDELTAB='" + changeRecordValue + "'");
                }
                else {
                    fundListGrid_filter("CDELTAB='' or CDELTAB='" + changeRecordValue + "' and CRISKID =" + riskValue);
                }
            }
            calTransTotal();
            if (isEmptyRecordset(fundListGrid1.recordset)) {
                hideEmptyTable(getTableForXMLData(fundListGrid1));
            }
            else {
                 showNonEmptyTable(getTableForXMLData(fundListGrid1));
            }

            break;
        case "transactionLogId":
            var transactionLogValue = getObjectValue("transactionLogId");
            var transactionClause;
            if (transactionLogValue == -1) {
                transactionClause = "transactionId=0"
            }
            else {
                transactionClause = "transactionId=" + transactionLogValue;
            }
            document.forms[0].action = getAppPath() + "/policymgr/premiummgr/viewFund.do?" + commonGetMenuQueryString()
                + "&process=loadAllFund&" + transactionClause;
            submitFirstForm();
            break;
        case "detailType":
            var detailType = getObjectValue("detailType");
            var transactionLogId = getObjectValue("transactionLogId");
            document.forms[0].action = getAppPath() + "/policymgr/premiummgr/viewFund.do?" + commonGetMenuQueryString()
                    + "&process=loadAllFund&detailType=" + detailType + "&transactionId=" + transactionLogId;
            submitFirstForm();
            break;
    }
}

function handleReadyStateReady() {
    setNegativeRed(getTableForXMLData(fundListGrid1));
}

//-----------------------------------------------------------------------------
// Calculate transaction total  in data island.
//-----------------------------------------------------------------------------
function calTransTotal() {
    var resultWrittenPremium = 0;
    var resultDeltaAmount = 0;
    var records = fundListGrid1.documentElement.selectNodes("//ROW[DISPLAY_IND = 'Y']");
    var hasRiskTotal = false;
    for (i = 0; i < records.length; i++) {
        currentRecord = records.item(i);
        var theString = currentRecord.selectNodes("CCOMPONENTCODE")(0).text;
        if (theString == "Risk Total") {
            hasRiskTotal = true;
            var sValueWrittenPremium = unformatMoneyStrValAsStr(currentRecord.selectNodes("CWRITTENPREMIUM")(0).text);
            var sValueDeltaAmount = unformatMoneyStrValAsStr(currentRecord.selectNodes("CDELTAAMOUNT")(0).text);
            if (!isEmpty(sValueWrittenPremium) && isSignedFloat(sValueWrittenPremium)) {
                resultWrittenPremium = resultWrittenPremium + parseFloat(sValueWrittenPremium);
            }
            if (!isEmpty(sValueDeltaAmount) && isSignedFloat(sValueDeltaAmount)) {
                resultDeltaAmount = resultDeltaAmount + parseFloat(sValueDeltaAmount);
            }
        }
        if (theString == "Transaction Total") {
            currentRecord.selectNodes("CWRITTENPREMIUM")(0).text = formatMoneyStrValAsStr(resultWrittenPremium.toString());
            currentRecord.selectNodes("CDELTAAMOUNT")(0).text = formatMoneyStrValAsStr(resultDeltaAmount.toString());
        }
    }
    //hide Transaction Total if has no Risk Total.
    if(!hasRiskTotal){
        fundListGrid_filter("CRISKID='-1'");
    }
}