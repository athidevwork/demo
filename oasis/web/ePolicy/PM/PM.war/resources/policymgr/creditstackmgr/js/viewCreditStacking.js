//-----------------------------------------------------------------------------
// JavaScript file for viewCreditStacking.jsp.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date: May 26, 2011
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------
function handleOnButtonClick(action) {
    switch (action) {
        case 'SEARCH':
            document.forms[0].action = getAppPath() + "/policymgr/creditstackmgr/viewCreditStacking.do";
            document.forms[0].process.value = "loadAllCreditStacking";
            submitFirstForm();
            break;
        case 'FILTER':
            var filterStr;
            var show = getObjectValue("showType");
            switch (show) {
                case 'APPLIED':
                    filterStr = "CCOMPFACT != '0.000%'";
                    break;
                case 'DISCARDED':
                    filterStr = "CCOMPFACT = '0.000%'";
                    break;
                default:break;
            }
            // must set selectedTableRowNo property to null, else it will go to wrong logic in common.js userReadyStateReady() function.
            setTableProperty(eval("secondGrid"), "selectedTableRowNo", null);
            secondGrid_filter(filterStr);
            break;
        default:break;
    }
}
