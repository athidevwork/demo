//-----------------------------------------------------------------------------
// Javascript file for processQuickPay.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   August 03, 2010
// Author: dzhang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/19/2010       dzhang      Update per Bill's comments.
//-----------------------------------------------------------------------------


//-----------------------------------------------------------------------------
// To handle the on load event.
// Disable the "Accounting Date" field.
// If chose Give QP [%] or Remove QP option, disable the "Amount" field and calculate the related data in risks/coverages grid.
//-----------------------------------------------------------------------------
function handleOnLoad() {

    if (hasObject("qpAccountingDate")) {
        getObject("qpAccountingDate").disabled = true;
    }
    if (hasObject("openMode") && (getObjectValue("openMode") == "GIVEQPPERCENT" || (getObjectValue("openMode") == "REMOVE") )) {
        if (hasObject("qpAmount")) {
            getObject("qpAmount").disabled = true;
        }
        calculateRelatedData();
    }
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllRiskCoverageForOriginalTransaction";
            break;
        default:
            proceed = false;
    }
    return proceed;
}

//-----------------------------------------------------------------------------
// To handle the on change event.
// If open the Div by selects Give QP [$] option.
// When change "Amount" field value, do validation and calculate related data in risks/coverages gird.
//-----------------------------------------------------------------------------
function handleOnChange(obj) {
    if (obj.name == "qpAmount") {
        if (getObjectValue("openMode") == "GIVEQPDISCOUNT") {
            if (validateForAddQuickPayDiscount(obj)) {
                calculateRelatedData();
            }
            else {
                obj.value = "";
            }
        }
    }
}

//-----------------------------------------------------------------------------
// To handle the validation for add quick pay discount
//-----------------------------------------------------------------------------
function validateForAddQuickPayDiscount(obj) {

    var returnValue = true;
    if (isEmpty(obj.value) || unformatMoneyStrValAsObj(obj.value) == 0) {
        handleError(getMessage("pm.processQuickPay.qpAmountBlankOrZero.error"));
        return false;
    }

    if ((Math.abs(getObjectValue("transactionAmount")) - unformatMoneyStrValAsObj(Math.abs(obj.value))) < 0) {
        handleError(getMessage("pm.processQuickPay.qpAmountGreaterThanTransAmount.error"));
        return false;
    }
    var qpAmount = unformatMoneyStrValAsObj(obj.value);
    var eligibleRiskNum = getObjectValue("eligibleCount");
    var indvQpAmt = Math.round(qpAmount / eligibleRiskNum * 100) / 100;

    if (getObjectValue("transactionAmount") > 0) {
        indvQpAmt = Math.abs(indvQpAmt) * -1;
    }
    else {
        indvQpAmt = Math.abs(indvQpAmt);
    }

    var XMLData = getXMLDataForGridName("riskCoverageGrid");
    if (!isEmptyRecordset(XMLData.recordset)) {
        var absPosition = XMLData.recordset.AbsolutePosition;
        first(XMLData);
        while (!XMLData.recordset.eof) {
            if (Math.abs(unformatMoneyStrValAsObj(XMLData.recordset("CTRANSAMOUNT").value)) > 0 && Math.abs(indvQpAmt) > 0 &&
                    (Math.abs(indvQpAmt) - Math.abs(unformatMoneyStrValAsObj(XMLData.recordset("CTRANSAMOUNT").value))) > 0) {
                returnValue = false;
                handleError(getMessage("pm.processQuickPay.indDiscountGreaterThanIndAmount.error"));
                break;
            }
            next(XMLData);
        }
        first(XMLData);
        XMLData.recordset.move(absPosition - 1);
    }

    return returnValue;
}

//-----------------------------------------------------------------------------
// To handle set related data for process quick pay.
//-----------------------------------------------------------------------------
function calculateRelatedData() {
    if (getObjectValue("openMode") == "GIVEQPDISCOUNT") {
        var qpAmount = unformatMoneyStrValAsObj(getObjectValue("qpAmount"));
        var eligibleRiskNum = getObjectValue("eligibleCount");
        var indvQpAmt = Math.round(qpAmount / eligibleRiskNum * 100) / 100;

        if (getObjectValue("transactionAmount") > 0) {
            indvQpAmt = Math.abs(indvQpAmt) * -1;
        }
        else {
            indvQpAmt = Math.abs(indvQpAmt);
        }
        var XMLData = getXMLDataForGridName("riskCoverageGrid");
        if (!isEmptyRecordset(XMLData.recordset)) {
            var absPosition = XMLData.recordset.AbsolutePosition;
            first(XMLData);
            while (!XMLData.recordset.eof) {
                if (Math.abs(unformatMoneyStrValAsObj(XMLData.recordset("CTRANSAMOUNT").value)) > 0 && Math.abs(indvQpAmt) > 0) {
                    XMLData.recordset("CQUICKPAYPERCENT").value = 0;
                    XMLData.recordset("CQUICKPAYAMOUNT").value = indvQpAmt;
                    XMLData.recordset("CDELTAAMOUNT").value = XMLData.recordset("CTRANSAMOUNT").value;
                    XMLData.recordset("UPDATE_IND").value = "Y";
                }
                next(XMLData);
            }
            first(XMLData);
            XMLData.recordset.move(absPosition - 1);
        }
    }

    if (getObjectValue("openMode") == "GIVEQPPERCENT") {
        var XMLData = getXMLDataForGridName("riskCoverageGrid");
        if (!isEmptyRecordset(XMLData.recordset)) {
            var absPosition = XMLData.recordset.AbsolutePosition;
            first(XMLData);
            while (!XMLData.recordset.eof) {
                if (Math.abs(unformatMoneyStrValAsObj(XMLData.recordset("CTRANSAMOUNT").value)) > 0) {
                    var defQpPercent = XMLData.recordset("CDEFQPPERCENT").value;
                    var transAmount = unformatMoneyStrValAsObj(XMLData.recordset("CTRANSAMOUNT").value);
                    XMLData.recordset("CQUICKPAYPERCENT").value = XMLData.recordset("CDEFQPPERCENT").value;
                    XMLData.recordset("CQUICKPAYAMOUNT").value = formatMoneyStrValAsObj(Math.round(transAmount * (defQpPercent * -0.01) * 100) / 100);
                    XMLData.recordset("CDELTAAMOUNT").value = formatMoneyStrValAsObj(transAmount);
                    XMLData.recordset("UPDATE_IND").value = "Y";
                }
                next(XMLData);
            }
            first(XMLData);
            XMLData.recordset.move(absPosition - 1);
        }
    }

    if (getObjectValue("openMode") == "REMOVE") {
        var XMLData = getXMLDataForGridName("riskCoverageGrid");
        if (!isEmptyRecordset(XMLData.recordset)) {
            var absPosition = XMLData.recordset.AbsolutePosition;
            first(XMLData);
            while (!XMLData.recordset.eof) {
                if (Math.abs(unformatMoneyStrValAsObj(XMLData.recordset("CTRANSAMOUNT").value)) > 0) {
                    XMLData.recordset("CQUICKPAYPERCENT").value = 0;
                    XMLData.recordset("CQUICKPAYAMOUNT").value = formatMoneyStrValAsObj(unformatMoneyStrValAsObj(XMLData.recordset("CQUICKPAYAMOUNT").value) * -1);
                     if(isEmpty(getObjectValue("hasAlreadySubmitted"))){
                        XMLData.recordset("UPDATE_IND").value = "Y"; 
                     }
                }
                next(XMLData);
            }
            first(XMLData);
            XMLData.recordset.move(absPosition - 1);
        }
    }

    setTableProperty(riskCoverageGrid1, "gridDataChange", true);
    if (window.postOnChange) {
        postOnChange(field);
    }
}

