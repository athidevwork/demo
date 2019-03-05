//-----------------------------------------------------------------------------
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 06/12/2018       dpang       Issue 193846: Refactor Org/Group page.
// 10/15/2018       dmeng       Issue 195835: grid replacement
//-----------------------------------------------------------------------------
function btnClick(btnID) {
    if (btnID == 'cancel') {
        closeArgWin(this);
    } else if (btnID == 'ok') {
        var val = "";
        if (getObject("print_type")[0].checked) {
            val = getObject("print_type")[0].value;
        }
        if (getObject("print_type")[1].checked) {
            val = getObject("print_type")[1].value;
        }
        if (getObject("print_type")[0].checked || getObject("print_type")[1].checked) {
            printOrgGroup(val);
            closeThisDivPopup(true);
        }
    } else {
        alert(getMessage("ci.common.error.button.unknown", [btnID]));
    }
}

function printOrgGroup(val) {
    getParentWindow().printOrgGroup(val);
}
