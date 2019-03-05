// Description:
//
// Author:  bzhu
// Date: 4/11/13
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 06/24/2013       bzhu        Issue 145614
// 07/19/2013       Elvin       Issue 146633
//                                  - Add common form validation when submit
//                                  - Use isPageDataChanged instead of isChanged field
// 09/21/2018      dzou        Issue 195835: Grid replacement.
//-----------------------------------------------------------------------------
//(C) 2013 Delphi Technology, inc. (dti)

var emailFldID = "emailAddress";
var isChanged = false;

//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    isChanged = true;
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'add':
            isChanged = true;
            commonAddRow("electronicDistributionEmailListGrid");
            break;
        case 'delete':
            isChanged = true;
            commonDeleteRow("electronicDistributionEmailListGrid");
            break;
        case 'save':
            if (!validate(document.forms[0])) {
                return;
            }
            setObjectValue("process", "saveEntityAddlEmail");
            submitForm(false);
            break;
        case 'close':
            if (isPageDataChanged()) {
                if (!confirm(getMessage("ci.common.error.changes.lost", new Array(""))))
                    return;
            }
            closeWindow();
            break;
    }
}

function electronicDistributionEmailListGrid_setInitialValues() {
    setObjectValue("moduleCode", "ALL");
    electronicDistributionEmailListGrid1.recordset("CENTITYID").value = getObjectValue("pk");
    electronicDistributionEmailListGrid1.recordset("CISDELETEAVAILABLE").value = "Y";
}