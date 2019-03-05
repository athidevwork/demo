//-----------------------------------------------------------------------------
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 10/31/2014       Elvin       Issue 157727: clear button should re-display default value
// 06/12/2018       dpang       Issue 193846: Refactor Org/Group page.
// 10/15/2018       dmeng       Issue 195835: grid replacement
//-----------------------------------------------------------------------------
var isChanged = false;
var rowid = -1;

//-----------------------------------------------------------------------------
// Button handler
//-----------------------------------------------------------------------------
function btnClick(btnID) {
    if (btnID == 'clear') {
        setObjectValue("process", "clear");
        submitFirstForm();
    } else if (btnID == 'query') {
        setObjectValue("process", "loadOrgGroup");
        submitFirstForm();
    } else if (btnID == 'print') {
        var url = getAppPath() + "/orgGroupView.do?process=printOrgGroup";
        var divPopupId = openDivPopup("", url, true, true, "", "", 500, 250, "", "", "", true);
    } else {
        alert(getMessage("ci.common.error.button.unknown", [btnID]));
    }
}

//-----------------------------------------------------------------------------
// Determines if OK to change pages.
//-----------------------------------------------------------------------------
function isOkToChangePages(id, url) {
    if (isChanged) {
        if (!confirm(ciDataChangedConfirmation)) {
            return false;
        }
    }
    return cisEntityFolderIsOkToChangePages(id, url);
}

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

function testgrid_selectRow(pk) {
    rowid = pk;
    getRow(testgrid1, pk);
    setFormFieldOnPolicyNo();
}

function testgrid_setInitialValues() {
}

function getDisplayText(field) {
    var index = field.selectedIndex;
    var displayText = '';
    if (index != -1) {
        displayText = field.options[field.selectedIndex].text;
    }
    return displayText;
}

function handleOnLoad() {
    hideShowElementByClassName(getSingleObject("summaryGridRowId"), true);
    //Correct grid id is "AddressGridRowId" not "addressGridRowId"
    hideShowElementByClassName(getSingleObject("AddressGridRowId"), true);
}

function printOrgGroup(printType) {
    var url = "orgGroupView.do?process=generatePDFforOrgGroup&printType=" + printType
        + "&pk=" + getObjectValue("pk")
        + "&memberStatus=" + getObjectValue("memberStatus")
        + "&memberType=" + getObjectValue("memberType")
        + "&asOfDate=" + getObjectValue("asOfDate");
    window.open(url, 'NEW_WINDOW', 'resizable=yes,width=800,height=600')
}


