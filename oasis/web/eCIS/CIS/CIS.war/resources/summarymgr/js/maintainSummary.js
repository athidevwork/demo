//-----------------------------------------------------------------------------
// Functions to support Entity Summary
// Modifications:
//-----------------------------------------------------------------------------
// 08/01/2013       Elvin       Issue 145222: first claimsListGrid after polQteListGrid_selectRow
// 11/13/2013       hxk         Issue 149981
//                              1)  Change ID on accountListGrid to be policyId
//                              2)  After filtering account, get acct and pol id from
//                                  accountListGrid before calling loadAccountBilling.
//                              3   Add function showAllAccounts so that we remove the
//                                  accountListGrid filter.
// 04/01/2014       jshen       Issue 153492: Modified filterAccount() function to filter out all rows if the policy
//                              is mini-policy.
// 07/13/2016       Elvin       Issue 177515: change goToSource to commonGoToSource
// 09/27/2017       ylu         Issue 186669: show grid's data
// 12/12/2017       kshen       Grid replacement.
// 10/26/2018       Elvin       Issue 195835: grid replacement
// 11/13/2018       wreeder     196147 - Added $.when(dti.oasis.grid.getLoadingPromise(gridId, …)).then(function() {   …}); surrounding logic that depends on the grid loading to be complete
//-----------------------------------------------------------------------------

var SUMMARY_POLQTE_GRID_ID = "polQteListGrid";
var SUMMARY_COMBINED_POLQTE_GRID_ID = "combinedPolQteListGrid";
var SUMMARY_COMBINED_RISK_GRID_ID = "combinedRiskListGrid";
var SUMMARY_ACCOUNT_GRID_ID = "accountListGrid";
var SUMMARY_BILLING_GRID_ID = "billingListGrid";
var SUMMARY_CLAIMS_GRID_ID = "claimsListGrid";

//definition of if loading cis summary page first time
var isFirstTimeLoadBillings = false;

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
    if (getObjectValue("isPolicyCombined") == 'Y') {
        $.when(dti.oasis.grid.getLoadingPromise("accountListGrid"), dti.oasis.grid.getLoadingPromise("combinedPolQteListGrid")).then(function () {
            if (isEmptyRecordset(eval(SUMMARY_ACCOUNT_GRID_ID + "1.recordset"))
                && isEmptyRecordset(eval(SUMMARY_COMBINED_POLQTE_GRID_ID + "1.recordset"))) {
                loadAccountBilling(-9999, -9999);
            } else if (isEmptyRecordset(eval(SUMMARY_ACCOUNT_GRID_ID + "1.recordset"))) {
                isFirstTimeLoadBillings = false;
                //    selectFirstRowInGrid(SUMMARY_COMBINED_POLQTE_GRID_ID);
            } else if (isEmptyRecordset(eval(SUMMARY_COMBINED_POLQTE_GRID_ID + "1.recordset"))) {
                isFirstTimeLoadBillings = true;
                selectFirstRowInGrid(SUMMARY_ACCOUNT_GRID_ID);
            } else {
                isFirstTimeLoadBillings = true;
                //    selectFirstRowInGrid(SUMMARY_COMBINED_POLQTE_GRID_ID);
                selectFirstRowInGrid(SUMMARY_ACCOUNT_GRID_ID);
            }
        });
    } else {
        $.when(dti.oasis.grid.getLoadingPromise("accountListGrid"), dti.oasis.grid.getLoadingPromise("polQteListGrid")).then(function () {
            if (isEmptyRecordset(eval(SUMMARY_ACCOUNT_GRID_ID + "1.recordset"))
                && isEmptyRecordset(eval(SUMMARY_POLQTE_GRID_ID + "1.recordset"))) {
                loadAccountBilling(-9999, -9999);
            } else if (isEmptyRecordset(eval(SUMMARY_ACCOUNT_GRID_ID + "1.recordset"))) {
                isFirstTimeLoadBillings = false;
                selectFirstRowInGrid(SUMMARY_POLQTE_GRID_ID);
            } else if (isEmptyRecordset(eval(SUMMARY_POLQTE_GRID_ID + "1.recordset"))) {
                isFirstTimeLoadBillings = true;
                selectFirstRowInGrid(SUMMARY_ACCOUNT_GRID_ID);
            } else {
                isFirstTimeLoadBillings = true;
                selectFirstRowInGrid(SUMMARY_POLQTE_GRID_ID);
                selectFirstRowInGrid(SUMMARY_ACCOUNT_GRID_ID);
            }
        });
    }
}

function handleOnButtonClick(btnId) {
    switch(btnId) {
        case 'REFRESH':
            if (isOkToChangePages()) {
                reloadWindowLocation();
            }
            break;
        case 'SHOWALLACOUNT':
            showAllAccounts();
            break;
        case 'SHOWALLCLAIM':
            filterClaims('all');
            break;
        default:
            break;
    }
}

function polQteListGrid_selectRow(id) {
    var policyNo = eval(SUMMARY_POLQTE_GRID_ID + "1").recordset("CPOLICYNO").value;
    $.when(dti.oasis.grid.getLoadingPromise("claimsListGrid"), dti.oasis.grid.getLoadingPromise("accountListGrid")).then(function () {
        filterClaims("policy", policyNo);
        filterAccount(policyNo);

        // We are now getting the data from the account grid after we filter
        var accountNo = eval(SUMMARY_ACCOUNT_GRID_ID + "1").recordset("CBILLINGACCOUNTID").value;
        var policyId = eval(SUMMARY_ACCOUNT_GRID_ID + "1").recordset("ID").value;

        //Don't load the billing when load page, avoid multiple load in very short time
        if (!isFirstTimeLoadBillings) {
            loadAccountBilling(accountNo, policyId);
        }else{
            isFirstTimeLoadBillings = false;
        }
    });
}

function combinedPolQteListGrid_selectRow(id) {
    var polId = eval(SUMMARY_COMBINED_POLQTE_GRID_ID + "1").recordset("CPOLICYID").value;
    var policyFromDate =eval(SUMMARY_COMBINED_POLQTE_GRID_ID + "1").recordset("CCOMBINEDPOLICYEXPFROM").value;
    var policyToDate= eval(SUMMARY_COMBINED_POLQTE_GRID_ID + "1").recordset("CCOMBINEDPOLICYEXPTO").value;
    var policyNo = eval(SUMMARY_COMBINED_POLQTE_GRID_ID + "1").recordset("CCOMBINEDPOLICYNO").value;

    $.when(dti.oasis.grid.getLoadingPromise("combinedRiskListGrid")).then(function () {
        filterRiskData(polId, policyFromDate, policyToDate);
    });

    $.when(dti.oasis.grid.getLoadingPromise("accountListGrid")).then(function () {
        filterAccount(policyNo);

        // We are now getting the data from the account grid after we filter
        var accountNo = "";
        var policyId = "-9999";

        if (!isEmptyRecordset(eval(SUMMARY_ACCOUNT_GRID_ID + "1").recordset)) {
            accountNo = eval(SUMMARY_ACCOUNT_GRID_ID + "1").recordset("CBILLINGACCOUNTID").value;
            policyId = eval(SUMMARY_ACCOUNT_GRID_ID + "1").recordset("ID").value;
        }

        //Don't load the billing when load page, avoid multiple load in very short time
        if (!isFirstTimeLoadBillings) {
            loadAccountBilling(accountNo, policyId);
        }else{
            isFirstTimeLoadBillings = false;
        }
    });
}

function combinedRiskListGrid_selectRow(id) {
    var coverageId = eval(SUMMARY_COMBINED_RISK_GRID_ID + "1").recordset("ID").value;
    $.when(dti.oasis.grid.getLoadingPromise("claimsListGrid")).then(function () {
        filterClaims("coverage", coverageId);
    });
}

function accountListGrid_selectRow(id) {
    // The policyId is not the anchor column, not the billingAccountId...
    var billingAccountId = eval(SUMMARY_ACCOUNT_GRID_ID + "1").recordset("CBILLINGACCOUNTID").value;
    loadAccountBilling(billingAccountId, id);
}

function billingListGrid_selectRow(id) {
}

function claimsListGrid_selectRow(id) {
}

//-----------------------------------------------------------------------------
// Load iframe
//-----------------------------------------------------------------------------
function loadAccountBilling(accountNo, policyId) {
    if (isEmpty(accountNo)) {
        accountNo = "-9999";
    }
    if (isEmpty(policyId)) {
        policyId = "-9999";
    }

    var url = getAppPath() + "/ciSummary.do?" + "process=loadAllAccountBillings" + "&mBactId=" + accountNo + "&policyId=" + policyId;
    getObject("iframeAccountBillingDetails").src = url;
}

function handleGoToSource(sourceNo, sourceTableName, sourcePk) {
    var isOkToProceed = true;
    if (sourceTableName == 'OCCURRENCE') {
        if ("N" == claimsListGrid1.recordset("CCLAIMACCESSIBLEFLAG").value) {
            alert(getMessage("ci.claim.restrict.message.noAuthority.case"));
            isOkToProceed = false;
        }
    } else if(sourceTableName == 'CLAIM') {
        if ("N" == claimsListGrid1.recordset("CCLAIMACCESSIBLEFLAG").value) {
            alert(getMessage("ci.claim.restrict.message.noAuthority.claim"));
            isOkToProceed = false;
        }
    }
    return isOkToProceed;
}

//-----------------------------------------------------------------------------
// Filter Risk by policyNo , policy(term)'s fromDate and policy(term)'s toDate
//-----------------------------------------------------------------------------
function filterRiskData(policyNo,policyFromDate, policyToDate) {
    // combinedRiskListGrid_filter("//ROW[1=1]");
    var riskTable = getTableForXMLData(getXMLDataForGridName(SUMMARY_COMBINED_RISK_GRID_ID));
    setTableProperty(riskTable, "selectedTableRowNo", null);
    first(combinedRiskListGrid1);
    var filter = "CPOLICYID='" + policyNo +"'";
    if (getObjectValue("getAllTerms") == "Y") {
        // We need to get the dates in yyyymmdd format to do an effective compare
        var policyFromDateAsNumber = policyFromDate.substr(6, 4) + policyFromDate.substr(0, 2) + policyFromDate.substr(3, 2);
        var policyToDateAsNumber = policyToDate.substr(6, 4) + policyToDate.substr(0, 2) + policyToDate.substr(3, 2);

        // XPATH version of substring starts from 1, javascript substring starts from 0
        var riskFromDate = "number(concat(substring(CCOVERAGEEXPFROM,7,4),substring(CCOVERAGEEXPFROM,1,2),substring(CCOVERAGEEXPFROM,4,2)))";
        var riskToDate = "number(concat(substring(CCOVERAGEEXPTO,7,4),substring(CCOVERAGEEXPTO,1,2),substring(CCOVERAGEEXPTO,4,2)))";

        filter += " and " + riskToDate + ">" + policyFromDateAsNumber + " and " + riskFromDate + "<" + policyToDateAsNumber;
    }
    combinedRiskListGrid_filter(filter);

    if (isEmptyRecordset(combinedRiskListGrid1.recordset)) {
        hideEmptyTable(combinedRiskListGrid);
    } else {
        showNonEmptyTable(combinedRiskListGrid);
        selectRow('combinedRiskListGrid', '');
    }

    // there is a bug with page numbers. using gotopage to reset the count and set rows correctly
    // this should be revisited when this code is refactored
    gotopage(combinedRiskListGrid, 'L');
    gotopage(combinedRiskListGrid, 'F');
}

function showAllAccounts() {
    accountListGrid_filter();
    if (isEmptyRecordset(accountListGrid1.recordset)) {
        hideEmptyTable(accountListGrid);
    } else {
        showNonEmptyTable(accountListGrid);
        selectFirstRowInGrid('accountListGrid');
    }
}
//-----------------------------------------------------------------------------
// Filter Claims by policyNo or CoverageId
//-----------------------------------------------------------------------------
function filterClaims(filterBy, policyNoOrCoverageId) {
    // claimsListGrid_filter("//ROW[1=1]");
    var claimTable = getTableForXMLData(getXMLDataForGridName(SUMMARY_CLAIMS_GRID_ID));
    setTableProperty(claimTable, "selectedTableRowNo", null);
    first(claimsListGrid1);
    switch (filterBy) {
        case "policy":
            claimsListGrid_filter("CPOLICYNO='" + policyNoOrCoverageId +"'");
            break;
        case "coverage":
            claimsListGrid_filter("contains(CCOVERAGEPKS,'" + policyNoOrCoverageId +"')");
            break;
        case "all":
            claimsListGrid_filter();
            break;
        default:
            alert(getMessage("ci.entity.message.filterBy.invalid"));
            break;
    }

    if (isEmptyRecordset(claimsListGrid1.recordset)) {
        hideEmptyTable(claimsListGrid);
    } else {
        showNonEmptyTable(claimsListGrid);
        selectFirstRowInGrid('claimsListGrid');
    }

    var totIndPaid =0;
    var totOutInd  =0;
    var totExpPaid =0;
    var totOutExp  =0;
    if (isEmptyRecordset(eval(SUMMARY_CLAIMS_GRID_ID + "1").recordset)) {
        getObject(totalClaimsCountROSPAN).innerText = '0';
        getObject(totalIndPaidROSPAN).innerText = '$0.00';
        getObject(totalOutIndROSPAN).innerText = '$0.00';
        getObject(totalExpPaidROSPAN).innerText = '$0.00';
        getObject(totalOutExpROSPAN).innerText = '$0.00';
    } else {
        getObject(totalClaimsCountROSPAN).innerText = eval(SUMMARY_CLAIMS_GRID_ID + '1.recordset.RecordCount');
        eval(SUMMARY_CLAIMS_GRID_ID + '1.recordset').movefirst();
        while (!eval(SUMMARY_CLAIMS_GRID_ID + '1.recordset.eof')) {
            totIndPaid += parseFloat(unformatMoneyStrValAsStr(eval(SUMMARY_CLAIMS_GRID_ID + '1.recordset("CINDPAID").value')));
            totOutInd += parseFloat(unformatMoneyStrValAsStr(eval(SUMMARY_CLAIMS_GRID_ID + '1.recordset("COUTIND").value')));
            totExpPaid += parseFloat(unformatMoneyStrValAsStr(eval(SUMMARY_CLAIMS_GRID_ID + '1.recordset("CEXPPAID").value')));
            totOutExp += parseFloat(unformatMoneyStrValAsStr(eval(SUMMARY_CLAIMS_GRID_ID + '1.recordset("COUTEXP").value')));
            eval(SUMMARY_CLAIMS_GRID_ID + '1.recordset').movenext();
        }
        first(claimsListGrid1);
        getObject(totalIndPaidROSPAN).innerText = formatMoneyStrValAsStr(totIndPaid);
        getObject(totalOutIndROSPAN).innerText = formatMoneyStrValAsStr(totOutInd);
        getObject(totalExpPaidROSPAN).innerText = formatMoneyStrValAsStr(totExpPaid);
        getObject(totalOutExpROSPAN).innerText = formatMoneyStrValAsStr(totOutExp);
    }
}

//-----------------------------------------------------------------------------
// Filter Claims by coverageId
//-----------------------------------------------------------------------------
function filterAccount(policyNo) {
    // accountListGrid_filter("//ROW[1=1]");
    var accountBillingTable = getTableForXMLData(getXMLDataForGridName(SUMMARY_ACCOUNT_GRID_ID));
    setTableProperty(accountBillingTable, "selectedTableRowNo", null);
    first(accountListGrid1);
    var polSource='POLICY';
    if(getObject(SUMMARY_COMBINED_POLQTE_GRID_ID )){
        polSource = eval(SUMMARY_COMBINED_POLQTE_GRID_ID + "1").recordset("CCOMBINEDPOLICYSOURCETABLE").value;
    }
    // Only filter the account grid if we have an Oasis PM sourced policy (ie. not mini-policy)
    //   We could have the same policy no in mini policy and Oasis PM policy.
    var filterStr = "";
    if (polSource == "POLICY") {
        filterStr = addFilterCondition(filterStr, "CPOLICYNO", "=", policyNo);
    } else {
        filterStr = addFilterCondition(filterStr, "CPOLICYNO", "=", "-99999999");
    }
    accountListGrid_filter(filterStr);
    if (isEmptyRecordset(accountListGrid1.recordset)) {
        hideEmptyTable(accountListGrid);
    } else {
        showNonEmptyTable(accountListGrid);
        selectFirstRowInGrid('accountListGrid');
    }
}