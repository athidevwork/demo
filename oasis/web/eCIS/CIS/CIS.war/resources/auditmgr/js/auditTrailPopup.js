//-----------------------------------------------------------------------------
// Functions to support Audit History popup pages.
// Author:
// Date:
// Modifications:
//-----------------------------------------------------------------------------
// 04/13/2018       ylu         109179: refactor Audit history popup page
//-----------------------------------------------------------------------------
var filter_fromDateFldID = "filterCriteria_fromDate";
var filter_toDateFldID = "filterCriteria_toDate";

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'query':
            if (isDate2OnOrAfterDate1(getObjectValue(filter_fromDateFldID), getObjectValue(filter_toDateFldID))=='N') {
                alert(getMessage("ci.common.error.certifiedDate.after", new Array(getLabel(filter_toDateFldID), getLabel(filter_fromDateFldID))));
                proceed = false;
            } else {
                setInputFormField("process", "loadAuditTrailBySource");
            }
            break;
    }
    return proceed;
}
