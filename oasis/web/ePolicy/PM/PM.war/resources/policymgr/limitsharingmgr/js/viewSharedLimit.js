//-----------------------------------------------------------------------------
// Javascript file for viewPremium.jsp.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:   January 12, 2011
// Author: dzhang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'FILTER':
            var viewOptionValue = getObjectValue("viewOption");
            var filterClause = "1=1 ";
            if (viewOptionValue != "ALL") {
                filterClause = filterClause + "and CRISKRELATIONTYPECODE='" + viewOptionValue + "'";
            }
            sharedLimitListGrid_filter(filterClause);
            break;
    }
}


function userReadyStateReady(tbl) {
    if (isEmptyRecordset(sharedLimitListGrid1.recordset)) {
        hideEmptyTable(tbl);
    }
    else {
        showNonEmptyTable(tbl);
    }
}
