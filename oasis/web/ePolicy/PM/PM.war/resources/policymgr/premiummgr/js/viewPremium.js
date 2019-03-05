//-----------------------------------------------------------------------------
// Javascript file for viewPremium.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   April 26, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 04/26/2010       syang       106549 - Modified userReadyStateReady to handle the empty row in grid.
// 06/17/2010       syang       106845 - Modified handleOnButtonClick to handle the Premium View Print.
// 10/14/2010       tzhao      issue#109875 - Modified money format script to support multiple currency.
// 11/16/2010       wfu        issue 115493 - Fixed defect when determine the change record value.
// 01/06/2011       dzhang     issue 116451 - Added handleOnLoad() to filter the premium detail with the filter conditions.
// 01/07/2011       dzhang     issue 113567 - Add filter logic for field premium type and filterPremiumDetail().
// 03/17/2011       fcb        112664 & 107021 - case EXCEL_WORKSHEET added.
// 04/22/2011       dzhang     issue 117338 - Moved setNegativeRed() to common.js.
// 05/26/2011       syang      issue 118033 - Modified handleOnButtonClick() to open "Credit Stacking View" page.
// 06/15/2011       syang       111676 - Re-implement saveGridAsExcelCsv() to export excel.
// 08/24/2011       lmjiang     124365 - re-size the 'View Rating' grid height to remove the duplicate bar. 
// 10/07/2011       fcb         107021 - passed also the filter values to the report.
// 10/11/2011       fcb         125838 - Additional changes due to move of filtering of data from JS to DB
// 03/23/2012       lmjiang     131471 - Pass the risk base record id from the page to background to export the CSV file.
// 12/07/2012       xnie        139365 - Modified handleOnButtonClick for VIEW_LOG to add a parameter termBaseRecordId
//                                       to request.
// 12/17/2012       xnie        139365 - Roll backed prior version fix.
// 01/29/2013       tcheng      141447 - Modified handleOnButtonClick to continue workflow after closing premium .
// 02/25/2013       adeng       140995 - 1)Modified handleOnButtonClick to pass in riskBaseRecordId and changeRecord when
//                                       click on PRINT button.
//                                       2)Modified saveGridAsExcelCsv() to add Change Record condition.
// 03/06/2013       fcb         142697 - Changed logic to move to the next step in the Workflow when the page is part
//                                       of the workflow, and when it is closed.
// 07/19/2013       xnie        146098 - 1) Modified handleOnButtonClick to pass in premiumType when click on PRINT button.
//                                       2) Modified saveGridAsExcelCsv() to add premiumType condition.
// 12/30/2014       jyang       157750 - Modified handleOnButtonClick() to encode the riskDesc before append it to URL.
// 10/21/2016       kxiang      180685 - Modified handleOnButtonClick for close button to disable the page when it is
//                                       called to avoid duplicate click.
// 11/30/2018       xjli        195889 - Reflect grid replacement project changes.
//-----------------------------------------------------------------------------
function handleOnButtonClick(btn) {
    switch (btn) {
        case 'VIEW_LOG':
            var transactionLogValue = getObjectValue("transactionLogId");
            var transactionClause = "transactionLogId=" + transactionLogValue;
            var viewRatingLogUrl = getAppPath() + "/policymgr/premiummgr/viewRatingLog.do?"
                + commonGetMenuQueryString() + "&process=loadAllRatingLog&" + "showMoreFlag=N&" + transactionClause;
            var divPopupId = openDivPopup("", viewRatingLogUrl, true, true, "", "", 850, 600, "", "", "", false);
            break;

        case 'VIEW_MEMBER_CONT':
            var selectedDataGrid = getXMLDataForGridName("premiumListGrid");
            var riskId = selectedDataGrid.recordset("CRISKID").value;
            if (!(riskId) || riskId.value == "") {
                break;
            }
            var riskDesc = selectedDataGrid.recordset("CHIDDENRISKCODE").value;
            var transactionLogValue = getObjectValue("transactionLogId");
            var termBaseRecordIdValue = getObjectValue("termBaseRecordId");
            var transactionClause = "transactionLogId=" + transactionLogValue;
            var termClause = "termBaseRecordId=" + termBaseRecordIdValue;
            var viewMemContributionUrl = getAppPath() + "/policymgr/premiummgr/viewMemberContribution.do?" + commonGetMenuQueryString()
                    + "&riskId=" + riskId + "&riskDesc=" + encodeURIComponent(riskDesc) + "&process=loadAllMemberContribution&" + transactionClause + "&" + termClause;
            var divPopupId = openDivPopup("", viewMemContributionUrl, true, true, "", "", "", "", "", "", "", false);
            break;

        case 'EXCEL_WORKSHEET':
            var selectedDataGrid = getXMLDataForGridName("premiumListGrid");

            var policyId = policyHeader.policyId;
            var termId = getObjectValue("termBaseRecordId");
            var transactionId = getObjectValue("transactionLogId");
            var premiumType = getObjectValue("premiumType");
            var changeRecord = getObjectValue("changeRecord");
            var riskBaseRecordId = getObjectValue("riskBaseRecordId");

            var paramsObj = new Object();
            paramsObj.reportCode = "PM_VIEW_PREMIUM_EXCEL";
            paramsObj.policyId = policyId;
            paramsObj.termId = termId;
            paramsObj.transactionId = transactionId;
            if (premiumType=='-1' || premiumType.toUpperCase()=='ALL') {
                paramsObj.premiumType = 'ALL';
            }
            else {
                paramsObj.premiumType = premiumType;
            }
            if (changeRecord=='-1' || changeRecord.toUpperCase()=='ALL') {
                paramsObj.changeRecord = 'ALL';
            }
            else {
                paramsObj.changeRecord = changeRecord;
            }

            paramsObj.riskBaseRecordId = riskBaseRecordId;

            if(gridExportExcelCsvType=='XLSX')
                viewPolicyReport(paramsObj, "XLSX");
            else if(gridExportExcelCsvType=='XLS')
                viewPolicyReport(paramsObj, "XLS");
            else
                viewPolicyReport(paramsObj, "CSV");

            break;

        case 'VIEW_LAYER':
            var selectedDataGrid = getXMLDataForGridName("premiumListGrid");
            var coverageId = selectedDataGrid.recordset("CCOVERAGEID").value;
            if (isEmpty(coverageId) || coverageId.length > 10) {
                break;
            }
            var coverageCode = selectedDataGrid.recordset("CHIDDENCOVGCODE").value;
            var transactionLogValue = getObjectValue("transactionLogId");
            var termBaseRecordIdValue = getObjectValue("termBaseRecordId");
            var transactionClause = "transactionLogId=" + transactionLogValue;
            var termClause = "termBaseRecordId=" + termBaseRecordIdValue;
            var viewLayerDetailUrl = getAppPath() + "/policymgr/premiummgr/viewLayerDetail.do?" + commonGetMenuQueryString()
                + "&coverageId=" + coverageId + "&coverageCode=" + coverageCode + "&process=loadAllLayerDetail&" + transactionClause + "&" + termClause;
            var divPopupId = openDivPopup("", viewLayerDetailUrl, true, true, "", "", "", "", "", "", "", false);
            break;
        case "PRINT":
            var policyId = policyHeader.policyId;
            var policyNo = policyHeader.policyNo;
            var termEffDate = policyHeader.termEffectiveFromDate;
            var termExpDate = policyHeader.termEffectiveToDate;
            var termBaseRecordId = getObjectValue("termBaseRecordId");
            var transactionLogId = getObjectValue("transactionLogId");
            var riskBaseRecordId = getObjectValue("riskBaseRecordId");
            var changeRecord = getObjectValue("changeRecord");
            var premiumType = getObjectValue("premiumType");
            var paramsObj = new Object();
            paramsObj.reportCode = "PM_VIEW_PREMIUM_WORKSHEET";
            paramsObj.policyId = policyId;
            paramsObj.policyno = policyNo;
            paramsObj.termEffDate = termEffDate;
            paramsObj.termExpDate = termExpDate;
            paramsObj.termBaseRecordId = termBaseRecordId;
            paramsObj.transactionLogId = transactionLogId;
            paramsObj.riskBaseRecordId = riskBaseRecordId;
            if (changeRecord=='-1' || changeRecord.toUpperCase()=='ALL') {
                paramsObj.changeRecord = 'ALL';
            }
            else {
                paramsObj.changeRecord = changeRecord;
            }
            if (premiumType=='-1' || premiumType.toUpperCase()=='ALL') {
                paramsObj.premiumType = 'ALL';
            }
            else {
                paramsObj.premiumType = premiumType;
            }
            viewPolicyReport(paramsObj);
            break;
        case "CREDIT":
            var termBaseRecordId = getObjectValue("termBaseRecordId");
            var transactionLogId = getObjectValue("transactionLogId");
            var url = getAppPath() + "/policymgr/creditstackmgr/viewCreditStacking.do?" + commonGetMenuQueryString() + "&process=loadAllCreditStacking&"
                    + "&termId=" + termBaseRecordId + "&transId=" + transactionLogId;
            var divPopupId = openDivPopup("", url, true, true, "", "", "870", "580", "", "", "", false);
            break;
        case 'CLOSE':
            if (hasObject("isInWorkflow") && getObjectValue("isInWorkflow") == "Y") {
                showProcessingImgIndicator();
                setWindowLocation(workflowUrl);
            }
            else {
                closeThisDivPopup(false);
            }
            break;
    }

}

function handleOnChange(field) {
    var fieldName = field.name;
    var fieldValue = field.value;
    switch (fieldName) {
    //changeRecord's value=-1 or others, !=0
        case "changeRecord":
        case "riskBaseRecordId":
        case "premiumType":
        case "transactionLogId":
            var transactionLogValue = getObjectValue("transactionLogId");
            if (transactionLogValue == -1) {
                var transactionClause = "transactionId=0"
            }
            else {
                transactionClause = "transactionId=" + transactionLogValue;
            }
            var premiumType = getObjectValue("premiumType");
            var premiumTypeClause = "";
            if (premiumType!='-1' && premiumType.toUpperCase()!='ALL') {
                premiumTypeClause = "&premiumType=" + premiumType;
            }

            var changeRecord = getObjectValue("changeRecord");
            var changeRecordClause = "";
            if (changeRecord!='-1' && changeRecord.toUpperCase()!='ALL') {
                changeRecordClause = "&changeRecord=" + changeRecord;
            }

            var riskBaseRecordId = getObjectValue("riskBaseRecordId");
            var riskBaseRecordIdClause = "";
            if (riskBaseRecordId>0) {
               riskBaseRecordIdClause = "&riskBaseRecordId=" + riskBaseRecordId;
            }

            document.forms[0].action = getAppPath() + "/policymgr/premiummgr/viewPremium.do?" + commonGetMenuQueryString()
                + "&process=loadAllPremium&" + transactionClause + premiumTypeClause + changeRecordClause + riskBaseRecordIdClause;
            showProcessingDivPopup();
            submitFirstForm();

            break;

    }
}

function userReadyStateReady(tbl) {
    $.when(dti.oasis.grid.getLoadingPromise("premiumListGrid")).then(function () {
        if(!dti.oasis.page.useJqxGrid()){
            setNegativeRed(tbl);
        }else{
            handleGetCustomPageOptions();
        }

        // Fix issue 106549, system shouldn't display the empty row in grid.
        if(isEmptyRecordset(premiumListGrid1.recordset)){
            hideEmptyTable(tbl);
        }
        else{
            showNonEmptyTable(tbl);
        }
    });
}
function handleGetCustomPageOptions() {
    return dti.oasis.page.newCustomPageOptions()
            .addGetCellStyleFunctionForNegativeNumber("premiumListGrid", ["CDELTAAMOUNT","CWRITTENPREMIUM"]);
}

function handleOnLoad() {
}

function filterPremiumDetail() {
}

function saveGridAsExcelCsv(gridId, dispType) {
    var exportType = eval(gridId+"_getExportType()");
    var pageName = pageTitle+'('+pageCode+')';
    pageName = encodeURIComponent(pageName);

    var url = '';
    if(exportType=='CSV')
        url = getAppPath() + "/policymgr/premiummgr/viewPremium.do?" + commonGetMenuQueryString()
            + "&process=exportExcelCSV&transactionId=" + getObjectValue("transactionLogId")
            + "&changeRecord=" + getObjectValue("changeRecord")
            + "&riskBaseRecordId=" + getObjectValue("riskBaseRecordId")
            + "&premiumType=" + getObjectValue("premiumType")
            + "&gridId="+gridId;
    else
        url = getAppPath() + "/policymgr/premiummgr/viewPremium.do?" + commonGetMenuQueryString()
                + "&process=exportExcelXLS&transactionId=" + getObjectValue("transactionLogId")
                + "&changeRecord=" + getObjectValue("changeRecord")
                + "&riskBaseRecordId=" + getObjectValue("riskBaseRecordId")
                + "&premiumType=" + getObjectValue("premiumType")
                + "&exportType="+exportType+"&gridId="+gridId+"&pageName="+pageName;

    window.open(url, "ExportExcel", "resizable=yes,width=800,height=600");
}
