//-----------------------------------------------------------------------------
// JavaScript file for process distribution.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:    Mar 12, 2011
// Author:  wfu
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/25/2011       Jerry       issue #123180 remove the check of 'page data changed'.
// 12/06/2013       xnie        142674 - Added logic for processing Catch up function.
//-----------------------------------------------------------------------------

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'FILTER':
            if (hasObject("distriSearchDate") && !isEmpty(getObjectValue("distriSearchDate"))) {
                document.forms[0].process.value = "loadAllDistribution";
                submitFirstForm();
            }
            break;
        case 'CLEAR':
            setObjectValue("distriSearchDate", "");
            document.forms[0].process.value = "loadAllDistribution";
            submitFirstForm();
            break;
        default:break;
    }
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllDistribution";
            break;
        case 'PROCESS':
            if (isEmptyRecordset(distributionListGrid1.recordset)) {
                return false;
            }
                
            document.forms[0].process.value = "processDistribution";
            setInputFormField("dividendRuleId", distributionListGrid1.recordset("ID").value);
            setInputFormField("isSaved", distributionListGrid1.recordset("UPDATE_IND").value);
            break;
        case 'REFRESH':
            document.forms[0].process.value = "loadAllDistribution";
            break;
        case 'CATCH_UP':
            var calendarYear = getObjectValue("distriSearchDate");
            if (isEmpty(calendarYear)) {
                alert(getMessage("pm.process.distribution.catchUp.calendarYear.null.error"));
                return false;
            }
            showProcessingDivPopup();
            document.forms[0].process.value = "processCatchUp";
            setInputFormField("calendarYear", calendarYear.substr(6));
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function distributionListGrid_setInitialValues() {
    var url = getAppPath() + "/policymgr/distributionmgr/processDistribution.do"
                           + "?process=getInitialValuesForAddDistribution"
                           + "&date=" + new Date();

    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
}
