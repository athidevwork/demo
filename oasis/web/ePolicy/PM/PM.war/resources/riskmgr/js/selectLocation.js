//-----------------------------------------------------------------------------
// Javascript file for selectLocation.jsp.
//
// (C) 2003 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 4/26/2011        ryzhao      118219 - Add function refreshPage().
//                                       Move function content from setAddressPKField() to refreshPage().
// 05/01/2012       sxm         133179 - Reset the record set to first after looping through it.
// 03/10/2017       eyin        180675 - Added oParentWindow variable to call handleOnSelectLocation()
//                                       function in iframe in new UI tab style.
// 05/23/2017       lzhang      185079 - use getReturnCtxOfDivPopUp to get parent window
// 07/18/2017       eyin        186988 - modify the parameter value of getReturnCtxOfDivPopUp().
// 10/17/18         xgong       195889 - Updated handleOnButtonClick and remove closeThis for grid replacement
// 11/15/18         xgong       195889 - Updated closeWindow for grid replacement
//-----------------------------------------------------------------------------
function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Add':
            var path = getCISPath() + "/ciAddressAdd.do?pk=-1&sourceTableName=ENTITY&sourceRecordFK=" +
                       getObjectValue("entityId") + "&sqlOperation=INSERT&expiredAddressFK=-1&addressTypeCode=PREMISE";
            var mainwin = window.open(path, 'AddressDetail', 'width=800,height=400,innerHeight=400,innerWidth=800,scrollbars');
            mainwin.focus();
            break;

        case 'Select':
            var locations = new Array();
            var locationCount = 0;
            first(selectLocationGrid1);
            while (!selectLocationGrid1.recordset.eof) {
                if (selectLocationGrid1.recordset("CSELECT_IND").value == -1) {
                    locations[locationCount] = {locationId:selectLocationGrid1.recordset("ID").value,
                        addressId:selectLocationGrid1.recordset("CADDRESSID").value,
                        address:selectLocationGrid1.recordset("CADDRESS").value};
                    locationCount ++;
                }
                next(selectLocationGrid1);
            }
            first(selectLocationGrid1);
            if (locationCount == 0) {
                handleError(getMessage("pm.selectLocation.NoSelection.error"));
                return false;
            }else if(getObjectValue("singleSelect")=='Y' && locationCount > 1){
                handleError(getMessage("pm.selectLocation.singleSelect.error"));
                return false;
            }
            closeWindow(function(){
                var divPopup = getParentWindow().getDivPopupFromDivPopupControl(this.frameElement);
                getReturnCtxOfDivPopUp(divPopup.id).handleOnSelectLocation(btn, locations);
            });
            break;

        case 'Cancel':
            closeWindow();
            break;
    }

    return true;
}

function setAddressPKField(addressPK) {
}

function refreshPage() {
    setObjectValue("process", "loadAllLocation");
    submitFirstForm();
}


function selectLocationList_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}
