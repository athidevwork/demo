//-----------------------------------------------------------------------------
// Maintain product mailing javascript file.
//
// (C) 2013 Delphi Technology, inc. (dti)
// Date:   Oct 14, 2013
// Author: awu
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'ADD_PRO_MAILING':
            commonAddRow("productMailingListGrid");
            break;
    }
}

function productMailingListGrid_setInitialValues() {
    var url = getAppPath() + "/policymgr/mailingmgr/maintainProductMailing.do?"
            + commonGetMenuQueryString() + "&process=getInitialValuesForProductMailing";

    new AJAXRequest("get", url, '', setInitialValuesForProductMailing, false);
}

function setInitialValuesForProductMailing(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                /* no default value found */
                return;
            }
            /* Parse xml and get inital values(s) */
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName("productMailingListGrid");
                setRecordsetByObject(selectedDataGrid, oValueList[0]);
            }
        }
    }
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            if (commonValidateGrid("productMailingListGrid")) {
                document.forms[0].process.value = "saveProductMailing";
            }
            else {
                proceed = false;
            }
            break;
        default:
            proceed = false;
    }
    return proceed;
}