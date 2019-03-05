//-----------------------------------------------------------------------------
// Javascript file for processQuickQuote.js.
//
// (C) 2016 Delphi Technology, inc. (dti)
// Date:   Aug 13, 2009
// Author: yyunhua
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 03/27/2016       wdang       161648 - Correct the parameter "addFileInfoB" passed to openFileUpload, 
//                                       it should always be "Y".
// 06/24/2016       ssheng      164927 - add Review Duplicate
//-----------------------------------------------------------------------------
var hasHeaderLine = "Y";
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'IMPORT':
            var hasFileHeader = getSysParmValue("PM_LOAD_FILE_HEADER");
            if (hasFileHeader == "USER") {
                if (confirm(getMessage("pm.quickQuote.hasFileHeader.confirm"))) {
                    hasHeaderLine = "Y";
                }
                else {
                    hasHeaderLine = "N";
                }
            }
            else {
                hasHeaderLine = hasFileHeader;
            }

            openFileUpload("POLICY", policyHeader.policyId, "CSV",
                "Quick quote load file", null, "Y");

            break;
        case 'VIEWFILE':
            var oasisFileId = getObjectValue("oasisFileId");
            if (oasisFileId == 0) {
                alert(getMessage("pm.quickQuote.fileNotExist"));
            }
            else {
                var url = getAppPath() +
                          "/policymgr/quickquotemgr/processQuickQuote.do?process=viewFile&oasisFileId=" + oasisFileId;
                window.open(url, "", "location=no,menubar=no,toolbar=no,directories=no,resizable=yes,opyhistory=no");
            }
            break;
        case 'POP_CIS':
        // Invoke ajax call to perform populate cis
            var url = getAppPath() + "/policymgr/quickquotemgr/processQuickQuote.do";
            postAjaxSubmit(url, "populateCis", false, false, populateCisDone);
            break;
        case 'PRINT_CIS':
            var policyId = policyHeader.policyId;
            var termId = getObjectValue("termBaseRecordId");
            var paramsObj = new Object();
            paramsObj.reportCode = "PM_QUICK_QUOTE_WORKSHEET";
            paramsObj.policyId = policyId;
            paramsObj.termId = termId;
            viewPolicyReport(paramsObj);
            break;
        case 'UNLOAD':
            commonOnSubmit('undoImportQuote', true, true, true, true);
            break;
        case 'CLOSEPAGE':
            window.frameElement.document.parentWindow.refreshPage();
            break;
        case 'CLEAR':
            getObject("lastNameCriteria").value = "";
            getObject("firstNameCriteria").value = "";
            getObject("loadBCriteria").value = "";
            getObject("cisDupBCriteria").value = "";
            getObject("errorBCriteria").value = "";

            commonOnSubmit('display', true, true, true, true);
            break;
        case 'FILTER':
            // Populate filter string
            var filterStr = "CPOLICYLOADEVENTDETAILID >0";

            var lastNameCriteria = getObjectValue("lastNameCriteria");
            if (!isEmpty(lastNameCriteria)) {
                filterStr += " and (CLASTNAME='" + lastNameCriteria + "')";
            }

            var firstNameCriteria = getObjectValue("firstNameCriteria");
            if (!isEmpty(firstNameCriteria)) {
                filterStr += " and (CFIRSTNAME='" + firstNameCriteria + "')";
            }

            var loadBCriteria = getObjectValue("loadBCriteria");
            if (!isEmpty(loadBCriteria)) {
                filterStr += " and (CLOADEDB='" + loadBCriteria + "')";
            }

            var cisDupBCriteria = getObjectValue("cisDupBCriteria");
            if (!isEmpty(cisDupBCriteria)) {
                filterStr += " and (CCISDUPB='" + cisDupBCriteria + "')";
            }

            var errorBCriteria = getObjectValue("errorBCriteria");
            if (!isEmpty(errorBCriteria)) {
                if (errorBCriteria == 'Y') {
                    filterStr += " and (CERRORMSG !='')";
                }
                else {
                    filterStr += " and (CERRORMSG ='')";
                }
            }

            processQuickQuoteGrid_filter(filterStr);
            break;
        case 'REVIEW':
            var policyId = policyHeader.policyId;
            var termId = getObjectValue("termBaseRecordId");
            var url = getAppPath() +
                    "/policymgr/reviewduplicatemgr/maintainReviewDuplicate.do?" + commonGetMenuQueryString();
            var divPopupId = openDivPopup("", url, true, true, "", "", 850, 750, "", "", "", false);
    }
}

function openFileUploadDone(oResult) {
    // If file upload succeeds, import the file into quote, otherwise display error message
    var rc = oResult.isSucceed;
    if (rc == "Y") {
        // Invoke ajax call to perform import
        var url = getAppPath() + "/policymgr/quickquotemgr/processQuickQuote.do";
        setInputFormField("uploadedOasisFileId", oResult.oasisFileId);
        setInputFormField("hasHeader", hasHeaderLine);
        commonOnSubmit('importQuote', true, true, true, true);
    }
    else {
        alert(oResult.errorMessage);
    }
}

function populateCisDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var oValueList = parseXML(data);
            alert(oValueList[0].MSG);
            refreshPage();
        }
    }

}
