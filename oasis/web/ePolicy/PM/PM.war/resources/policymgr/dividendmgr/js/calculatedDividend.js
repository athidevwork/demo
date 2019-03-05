//-----------------------------------------------------------------------------
// JavaScript file for calculated dividend.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:    Mar 30, 2011
// Author:  wfu
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------

var priorDividendGridList = null;
var dividendId = null;
var dividendDate = null;
var policyType = null;
var dividendPercent = null;
var transAccountingDate = null;

function handleOnLoad() {
    // Get the value of selected prior dividend
    dividendId = calculatedDividendListGrid1.recordset("CDIVIDENDID").value;
    if (isDefined(window.frameElement)) {
        window.frameElement.document.parentWindow.selectRow("priorDividendListGrid", dividendId);
        priorDividendGridList = window.frameElement.document.
                parentWindow.getXMLDataForGridName("priorDividendListGrid");
        dividendDate = priorDividendGridList.recordset("CDIVIDENDDATE").value;
        transAccountingDate = priorDividendGridList.recordset("CTRANSACCOUNTINGDATE").value;
        dividendPercent = priorDividendGridList.recordset("CDIVIDENDPERCENT").value;
        policyType = priorDividendGridList.recordset("CPOLICYTYPE").value;
    }
}

function filterDividend(policyNo, status) {
    // filter calculated dividend
    var filterStr = "";
    if (!isEmpty(status) && status!="ALL") {
        filterStr += "CPOLRELSTATTYPECODE='" + status + "'";
    }

    if (!isEmpty(policyNo)) {
        if (!isEmpty(filterStr)) {
            filterStr += " and ";
        }
        filterStr += "CPOLICYNO[contains(.,'" + policyNo + "')]";
    }

    calculatedDividendListGrid_filter(filterStr);
    if (isEmptyRecordset(calculatedDividendListGrid1.recordset)) {
        hideEmptyTable(getTableForXMLData(calculatedDividendListGrid1));
    } else {
        showNonEmptyTable(getTableForXMLData(calculatedDividendListGrid1));
    }
}

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'SELECTALL':
            updateSelectInd(-1);
            break;
        case 'DESELECTALL':
            updateSelectInd(0);
            break;
        case 'EXCLUDE':
            calculatedDividendListGrid_deleterow();
            break;
        case 'POST':
            if (confirm(getMessage("pm.dividend.process.post.confirm"))) {
                if (validatePostDividend()) {
                    var url = getAppPath() + "/policymgr/dividendmgr/processDividend.do";
                    var process = "postDividend";
                    postAjaxSubmit(url, process, true, false, handleOnPostDividend, false);
                }
            }
            break;
        case 'PRINT':
            if (isDefined(window.frameElement)) {
                var url = getAppPath() + "/policyreportmgr/maintainPolicyReport.do?process=generatePolicyReport"
                          + "&reportCode=PM_DIVIDEND_CALCULATION_WORKSHEET"
                          + "&dividendId=" + dividendId
                          + "&dividendDate=" + dividendDate
                          + "&transAccountingDate=" + transAccountingDate
                          + "&dividendPercent=" + dividendPercent
                          + "&policyType=" + policyType
                          + "&date=" + new Date();
                var divPopupId = window.frameElement.document.parentWindow.
                        openDivPopup("", url, true, true, "", "", "900", "700", "", "", "", true);
            }
            break;
        default:break;
    }
}

function handleOnPostDividend(ajax){
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }

            alert(getMessage("pm.dividend.process.post.success.info"));
            // To retrieve prior dividend list
            if (isDefined(window.frameElement)) {
                window.frameElement.document.parentWindow.document.forms[0].process.value = "loadAllPriorDividend";
                window.frameElement.document.parentWindow.submitFirstForm();
                window.frameElement.document.parentWindow.showProcessingImgIndicator();
            }
        }
    }
}

function validatePostDividend() {
    var selectPolicyIds = "";
    var isSelected = false;
    var count = 0;
    if (!isEmptyRecordset(calculatedDividendListGrid1.recordset)) {
        first(calculatedDividendListGrid1);
        while (!calculatedDividendListGrid1.recordset.eof) {
            var isPost = calculatedDividendListGrid1.recordset("CSELECT_IND").value;
            if (isPost == "-1") {
                isSelected = true;
                selectPolicyIds += calculatedDividendListGrid1.recordset("CPOLICYID").value + ",";
                count ++;
            }
            next(calculatedDividendListGrid1);
        }
        first(calculatedDividendListGrid1);
        if (selectPolicyIds.length > 0) {
            selectPolicyIds = selectPolicyIds.substring(0, selectPolicyIds.length - 1);
            setInputFormField("policyId", selectPolicyIds);
            setInputFormField("noOfPols", count);
            setInputFormField("rpId", dividendId);
            setInputFormField("inforceDt", dividendDate);
            setInputFormField("divPct", dividendPercent);
            setInputFormField("transDt", transAccountingDate);
            setInputFormField("policyType", policyType);
        }
        if (!isSelected) {
            handleError(getMessage("pm.dividend.process.post.noRecord.select"));
        }
    }
    return isSelected;
}

function calculatedDividendList_btnClick(asBtn) {
    switch (asBtn) {
        case 'SELECT':
            updateSelectInd(-1);
            break;
        case 'DESELECT':
            updateSelectInd(0);
            break;
    }
}

function updateSelectInd(selectValue) {
    var XMLData = calculatedDividendListGrid1;
    if (!isEmptyRecordset(XMLData.recordset)) {
        var absPosition = XMLData.recordset.AbsolutePosition;
        first(XMLData);
        while (!XMLData.recordset.eof) {
            XMLData.recordset('CSELECT_IND').value = selectValue;
            next(XMLData);
        }
        first(XMLData);
        XMLData.recordset.move(absPosition - 1);
    }
}