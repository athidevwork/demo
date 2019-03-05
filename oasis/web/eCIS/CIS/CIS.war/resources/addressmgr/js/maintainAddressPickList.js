//-----------------------------------------------------------------------------
//  Description: Javascript of address pick list
//  Revision Date   Revised By  Description
//  10/08/2018      Elvin       Issue 195835: grid replacement
//  ---------------------------------------------------------------------------

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SEL':
            if (!isEmptyRecordset(addressPickListGrid1.recordset)) {
                closeWindow(function () {
                    if (getParentWindow()) {
                        //Set selected address information
                        getParentWindow().SELECTED_ADDRESS_INFO.addressId = addressPickListGrid1.recordset("ID").value;
                        getParentWindow().SELECTED_ADDRESS_INFO.addressSingleLine = addressPickListGrid1.recordset("CADDRESSSINGLELINE").value;
                        getParentWindow().eval(getParentWindow().OPEN_SELECT_ADDRESS_PARAMETERS.callbackEvent + "()");
                    }
                });
            } else {
                alert(getMessage("ci.address.addressListMgr.msg.warning.addressNotFound"));
            }
            break;
        case 'CANCEL':
            closeWindow();
            break;
        default:
            break;
    }
}

//-----------------------------------------------------------------------------
// This function is to handle Address URL click in Grid
//-----------------------------------------------------------------------------
function selectAddressInList(pk){
    commonOnButtonClick('SEL');
}
