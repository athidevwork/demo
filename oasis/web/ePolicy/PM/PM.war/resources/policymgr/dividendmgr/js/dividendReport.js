//-----------------------------------------------------------------------------
// JavaScript file for dividend report.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:    Mar 30, 2011
// Author:  wfu
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//11/30/2018          xgong       195889 - Updated handleOnButtonClick for gird replacement
//-----------------------------------------------------------------------------

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'FILTER':
            var dividendEventId = dividendReportListGrid1.recordset("ID").value;
            var policyNo = getObjectValue("policyNoCriteria");
            var filterStr;
            if (dti.oasis.page.useJqxGrid()) {
                filterStr = "CDIVIDENDEVENTID='" + dividendEventId + "' and " +
                        " contains(CPOLICYNO,'" + policyNo +"')";
            } else {
                filterStr = "CDIVIDENDEVENTID='" + dividendEventId + "' and " +
                        "CPOLICYNO[contains(.,'" + policyNo + "')]";
            }

            dividendReportDetailListGrid_filter(filterStr);
            if (isEmptyRecordset(dividendReportDetailListGrid1.recordset)) {
                hideEmptyTable(getTableForXMLData(dividendReportDetailListGrid1));
            } else {
                showNonEmptyTable(getTableForXMLData(dividendReportDetailListGrid1));
            }
            break;
        case 'SEARCH':
            document.forms[0].process.value = "loadAllDividendReport";
            submitFirstForm();
            break;
        default:break;
    }
}

function dividendReportListGrid_selectRow(id) {
    var dividendEventId = dividendReportListGrid1.recordset("ID").value;
    // Filter data
    dividendReportDetailListGrid_filter("CDIVIDENDEVENTID='" + dividendEventId + "'");

    if (isEmptyRecordset(dividendReportDetailListGrid1.recordset)) {
        hideEmptyTable(getTableForXMLData(dividendReportDetailListGrid1));
    } else {
        showNonEmptyTable(getTableForXMLData(dividendReportDetailListGrid1));
    }
}
