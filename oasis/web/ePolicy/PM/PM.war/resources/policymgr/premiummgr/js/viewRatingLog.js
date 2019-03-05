//-----------------------------------------------------------------------------
// Javascript file for viewRatingLog.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Dec 08, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 12/08/2010       syang       111672 - Added handleOnLoad() to filter the log with the filter conditions.
// 06/15/2011       syang       111676 - Re-implement saveGridAsExcelCsv() to export excel.
// 04/30/2012       jshen       132219 - Reset the Coverage dropdown field if Risk dropdown is changed.
// 07/09/2012       jshen       133114 - Reset the Risk and Coverage dropdown field if Term is changed.
// 12/07/2012       xnie        139365 - Modified handleOnChange(): when transactionLogId is changed, data should be
//                                       reload.
// 12/17/2012       xnie        139365 - Roll backed prior version fix.
// 04/11/2014       jyang       153678 - Updated handleOnChange function to roll back 139365's change correctly.
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    var fieldName = field.name;

    // We need to reset risk when user change value of Term. Because risks may be different in different terms.
    if ("termBaseRecordId" == fieldName && "-1" != getObjectValue("termBaseRecordId")) {
        if (getObjectValue("riskBaseRecordId") != "-1") {
            getObject("riskBaseRecordId").value = "-1";
        }
        if (getObjectValue("coverageBaseRecordId") != "-1") {
            getObject("coverageBaseRecordId").value = "-1";
        }
    }

    // If coverageBaseRecordId has value which means the Coverage dropdown has option selected, we need to reset it when user change value of Risk.
    if ("riskBaseRecordId" == fieldName && "-1" != getObjectValue("coverageBaseRecordId")) {
        getObject("coverageBaseRecordId").value = "-1";
    }

    if ("termBaseRecordId" == fieldName || "riskBaseRecordId" == fieldName
            || "coverageBaseRecordId" == fieldName || "showMoreFlag" == fieldName) {
        var termBaseRecordId = getObjectValue("termBaseRecordId");
        var riskBaseRecordId = getObjectValue("riskBaseRecordId");
        var coverageBaseRecordId = getObjectValue("coverageBaseRecordId");
        var transactionLogId = getObjectValue("transactionLogId");
        document.forms[0].action = getAppPath() + "/policymgr/premiummgr/viewRatingLog.do?" + commonGetMenuQueryString()
                + "&process=loadAllRatingLog&transactionLogId=" + transactionLogId + "&termId=" + termBaseRecordId
                + "&riskId=" + riskBaseRecordId + "&coverageId=" + coverageBaseRecordId;
        submitFirstForm();
    }
}

function saveGridAsExcelCsv(gridId, dispType) {
    var termBaseRecordId = getObjectValue("termBaseRecordId");
    var riskBaseRecordId = getObjectValue("riskBaseRecordId");
    var coverageBaseRecordId = getObjectValue("coverageBaseRecordId");
    var showMoreFlag = getObjectValue("showMoreFlag");
    var transactionLogId = getObjectValue("transactionLogId");
    var exportType = eval(gridId+"_getExportType()");
    var pageName = pageTitle+'('+pageCode+')';
    pageName = encodeURIComponent(pageName);

    var url = '';
    if(exportType=='CSV')
        var url = getAppPath() + "/policymgr/premiummgr/viewRatingLog.do?" + commonGetMenuQueryString()
            + "&process=exportExcelCSV&termId=" + termBaseRecordId + "&riskId=" + riskBaseRecordId
            + "&coverageId=" + coverageBaseRecordId + "&showMoreFlag=" + showMoreFlag + "&transactionLogId=" + transactionLogId
            + "&gridId="+gridId;
    else
        var url = getAppPath() + "/policymgr/premiummgr/viewRatingLog.do?" + commonGetMenuQueryString()
            + "&process=exportExcelXLS&termId=" + termBaseRecordId + "&riskId=" + riskBaseRecordId
            + "&coverageId=" + coverageBaseRecordId + "&showMoreFlag=" + showMoreFlag + "&transactionLogId=" + transactionLogId
            + "&exportType="+exportType+"&gridId="+gridId+"&pageName="+pageName;
    window.open(url, "ExportExcel", "resizable=yes,width=800,height=600");
}