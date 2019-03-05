var rowid = "-1";
var binserting = false;
//var deleteq = 'Are you sure you want to delete the selected record(s)?';
function frmGrid_btnClick(asBtn) {


    switch (asBtn) {
        case 'SELECT':
            testgrid_updatenode("CSELECT_IND", -1);
            first(testgrid1);
            gotopage(testgrid,'F');
            selectFirstRowInGrid("testgrid");
            break;
        case 'DESELECT':
            testgrid_updatenode("CSELECT_IND", 0);
            first(testgrid1);
            gotopage(testgrid,'F');
            selectFirstRowInGrid("testgrid");
            break;
        case 'DELETE':
        // get selected keys
            var dataArray = getSelectedKeys(testgrid1);
            if (dataArray.length == 0) {
                alert(getMessage("ci.common.error.rowSelect.delete"));
                return;
            }
            if (confirm(getMessage("js.delete.confirmation"))) {
                first(testgrid1);
                gotopage(testgrid,'F');
                var len = dataArray.length;
                beginDeleteMultipleRow("testgrid");
                for (var i = 0; i < len; i++) {
                    selectRow("testgrid", dataArray[i]);
                    if (rowid == dataArray[i] || testgrid1.recordset.recordcount == 1)
                        rowid = "-1";
                    testgrid_deleterow();
                    checkForm();
                }
                endDeleteMultipleRow("testgrid");
            }
            break;
        case 'SAVE':
            first(testgrid1);
            while (!testgrid1.recordset.eof) {
                var upd = testgrid1.recordset("UPDATE_IND").value;
                if (upd == 'I' || upd == 'Y')
                    if (!validate(document.forms[0]) || !validateGrid())
                        return;
                next(testgrid1);
            }
            document.forms[0].process.value = "SAVE";
            testgrid_update();
            break;
        case 'ADD':
            testgrid_insertrow();
            getRow(testgrid1, lastInsertedId);
            checkForm();
            setFocusToFirstEditableFormField("testgrid");
            break;
        case 'REFRESH':
            if (isPageGridsDataChanged()) {
                if (!confirm(getMessage("js.refresh.lose.changes.confirmation"))) {
                    return;
                }
            }
            document.forms[0].process.value = "";
            submitFirstForm();
            break;
    }
}
function handleOnChange(obj) {
    if (testgrid1.recordset("UPDATE_IND").value != "I")
        testgrid1.recordset("UPDATE_IND").value = "Y";

    gridDataChange = true;
    if (window.postOnChange) {
        postOnChange(obj);
    }
}
function testgrid_selectRow(pk) {
    rowid = pk;
    getRow(testgrid1, pk);
}
function validateGrid() {
    return true;
}

function checkForm() {
    if (document.all("formfields"))
        hideShowElementByClassName(getObject("formfields"), !(tblPropArray[0] && tblPropArray[0].hasrows));
}

function testgrid_setInitialValues() {
}