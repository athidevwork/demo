//-----------------------------------------------------------------------------
// Modifications:
//-----------------------------------------------------------------------------
// 07/19/2013    Elvin    Issue 144761: Add goToSource to response clicking source no
// 02/13/2015    bzhu     Issue 160886: unauthorised claim can't be viewed.
// 07/13/2016    Elvin    Issue 177515: change goToSource to commonGoToSource
// 04/17/2018    dpang    Issue 192648: Refactor WIP Inquiry.
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// Determines if OK to change pages.
//-----------------------------------------------------------------------------
function isOkToChangePages(id, url) {
    return cisEntityFolderIsOkToChangePages(id, url);
}

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

function handleOnLoad() {
    $.when(dti.oasis.grid.getLoadingPromise("testgrid")).then(function () {
        filterWIPInquiryList();
    });
}

function filterWIPInquiryList() {
    var userId = getObjectValue("searchCriteria_userId");
    var createDate = getObjectValue("searchCriteria_createDate");
    var sourceNo = getObjectValue("searchCriteria_sourceNo");
    var wipTypeCode = getObjectValue("searchCriteria_wipTypeCode");
    var subSystemTypeCode = getObjectValue("searchCriteria_subSystemTypeCode");

    var filterInit = "1=1";
    var filter = filterInit + getFilterCriteria(userId, "CCREATEDBYUSERID") +
        getFilterCriteria(createDate, "CCREATEDATE") +
        getFilterCriteria(sourceNo, 'CSOURCENO') +
        getFilterCriteria(wipTypeCode, 'CSOURCETYPE') +
        getFilterCriteria(subSystemTypeCode, 'CSUBSYSTEMTYPECODE');

    if (filter != filterInit) {
        testgrid_filter(filter);
    } else {
        testgrid_filter(filterInit);
    }
}

function handleReadyStateReady() {
    var isGridEmpty = !getTableProperty(getTableForGrid('testgrid'), "hasrows");

    var rowCount = 0;
    if (!isGridEmpty) {
        rowCount = testgrid1.recordset.RecordCount;
    }

    checkIfEnableOrDisableBtn(isGridEmpty);
    $('#wipInquiryListLegend').text(rowCount + ' Items');
}

function getFilterCriteria(criteria, fieldId) {
    var filter = "";
    if (isStringValue(criteria)) {
        filter = " and " + fieldId + " = '" + criteria + "'";
    }
    return filter;
}

function goToAuditHistory() {
    var sourceNo = testgrid1.recordset('CSOURCENO').value;
    if (!isStringValue(sourceNo)) {
        alert(getMessage("ci.entity.message.sourceRecord.invalid"));
        return;
    }

    var subSystemTypeCode = testgrid1.recordset("CSUBSYSTEMTYPECODE").value;
    switch (subSystemTypeCode) {
        case 'PMS':
            openAuditTrailPopup('PMAuditTrail', 'transactionHistory', sourceNo);
            break;

        case 'CMS':
            openAuditTrailPopup('CMAuditTrail', 'claimTransactionHistory', sourceNo);
            break;

        case 'FMS':
            alert(getMessage("ci.entity.message.auditHistory.forRecord"));
            break;
    }
}

function btnClick(btnID) {
    switch (btnID) {
        case 'refresh':
            setObjectValue("searchCriteria_clientEntityFK", '');
            setObjectValue("process", "loadWIPInquiryList");
            clearCriteria();
            submitFirstForm();
            break;

        case 'search':
            filterWIPInquiryList();
            break;

        case 'history':
            goToAuditHistory();
            break;

        case 'edit':
            var sourceType = testgrid1.recordset("CSOURCETYPE").value;
            if ('Note' == sourceType) {
                handleNote();
            } else if ('Payment' == sourceType) {
                handlePayment(testgrid1.recordset('CSOURCENO').value);
            }
            break;
    }
}

function checkIfEnableOrDisableBtn(isGridEmpty) {
    enableDisableField(getObject("wipHistory"), isGridEmpty);
    enableDisableField(getObject("wipEdit"), isGridEmpty);
}

function handleNote() {
    var sourceRecordFK = testgrid1.recordset("CADDLINFO3").value;
    var sourceTableName = testgrid1.recordset("CADDLINFO1").value;
    var noteGroupCode = testgrid1.recordset("CADDLINFO2").value;
    if (window.loadNotes) {
        loadNotes(sourceRecordFK, sourceTableName, noteGroupCode);
    } else {
        alert(getMessage("ci.entity.message.notesError.notAvailable"));
    }
}

function handlePayment(sourceNo) {
    var claimPK = testgrid1.recordset("CADDLINFO2").value;
    if (isEmpty(claimPK)) {
        alert(getMessage("ci.common.error.pk.invalid"));
        return;
    }

    if (getObjectValue("restrictSourceList").indexOf("," + sourceNo + ",") >= 0) {
        alert(getMessage("ci.claim.restrict.message.noAuthority.claim"));
        return;
    }

    var actionUrl = getTopNavApplicationUrl("Claims") + "/cmClaimTransaction.do?&process=init&claimPK=" + claimPK;
    openSourceWindow(actionUrl);
}

function handleGoToSource(sourceNo, sourceTableName, sourcePk) {
    var isOkToProceed = true;

    switch (sourceTableName) {
        case 'PAYMENT':
            handlePayment(sourceNo);
            isOkToProceed = false;
            break;
        case 'NOTE':
            handleNote();
            isOkToProceed = false;
            break;
        case 'CASH BATCH':
            isOkToProceed = false;
            break;
    }
    return isOkToProceed;
}

function getWIPInquiryList() {
    setObjectValue("process", "loadWIPInquiryList");
    submitFirstForm();
}

function clearCriteria() {
    setObjectValue("searchCriteria_userId", "");
    setObjectValue("searchCriteria_createDate", "");
    setObjectValue("searchCriteria_sourceNo", "");
    setObjectValue("searchCriteria_wipTypeCode", "");
    setObjectValue("searchCriteria_subSystemTypeCode", "");
}

//this function it used to handle findertext field
function find(findId) {
    if (findId == "searchCriteria_clientEntityName") {
        clearCriteria();
        openEntitySelectWinFullName("searchCriteria_clientEntityFK", "searchCriteria_clientEntityName", "getWIPInquiryList()");
    }
}