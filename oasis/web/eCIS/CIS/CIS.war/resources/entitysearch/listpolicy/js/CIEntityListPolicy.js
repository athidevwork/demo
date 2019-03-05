//-----------------------------------------------------------------------------
// Modifications:
//-----------------------------------------------------------------------------
// 07/18/2013    Elvin    Issue 143449: Add goToSource to display Policy information when clicking on policy no
// 07/13/2016    Elvin    Issue 177515: change goToSource to commonGoToSource
// 11/29/2018    htwang   Issue 197302: modified to call getParentWindow() to support jqxGrid.
//-----------------------------------------------------------------------------
function btnClick(btn) {
    switch (btn) {
        case 'close':
            var accntObj = getParentWindow().document.forms[0].AccountNo;
            if(accntObj) {
                accntObj.value = '';
            }
            closeThisDivPopup(true);
            break;
        case 'select':
            closeThisDivPopup(true);
            break;
    }
}

//-----------------------------------------------------------------------------
// Fix issue 105023, system should retrieve the field's value by field's name rather than its index
// since the index may be changed in other issue.
//-----------------------------------------------------------------------------
function polQteListGrid_selectRow(pk) {
    var selectedRecordset = getXMLDataForGridName('polQteListGrid').recordset;
    if(selectedRecordset.Fields('CPOLICYNO')) {
        var pnObj = getParentWindow().document.forms[0].PolicyNo;
        if(pnObj) {
            var policyNo = selectedRecordset.Fields('CPOLICYNO').Value;
            getParentWindow().setObjectValue("PolicyNo", policyNo);
        }
    }
}