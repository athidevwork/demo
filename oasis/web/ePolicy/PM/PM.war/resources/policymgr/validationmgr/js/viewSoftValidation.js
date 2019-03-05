//-----------------------------------------------------------------------------
// Javascript file for viewSoftValidation.jsp.
//
// (C) 2015 Delphi Technology, inc. (dti)
// Date:   Dec 07, 2016
// Author: tzeng
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 12/07/2016       tzeng       166929 - Initial version.
//-----------------------------------------------------------------------------

function handleOnChange(field) {
    var fieldName = field.name;
    switch (fieldName) {
        case "transactionLogId":
            var transactionLogValue = getObjectValue("transactionLogId");
            var transactionClause;
            if (transactionLogValue == -1) {
                transactionClause = "transactionLogId=0";
            }
            else {
                transactionClause = "transactionLogId=" + transactionLogValue;
            }
            document.forms[0].action = getAppPath() + "/policymgr/validationmgr/viewsoftvalidation.do?" +
                                       commonGetMenuQueryString() + "&process=loadSoftValidation&" + transactionClause;
            showProcessingDivPopup();
            submitFirstForm();
            break;
    }
}

function handleOnLoad() {
    $("#transactionLogId_LABEL_CONTAINER").attr("colSpan", 32);
}

