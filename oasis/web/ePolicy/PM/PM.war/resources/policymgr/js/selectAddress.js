//-----------------------------------------------------------------------------
// Javascript file for selectAddress.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Aug 17, 2012
// Author: xnie
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/17/2012       xnie       Issue 120683 - Modified handleOnButtonClick() to add button 'Done' logic.
// 03/10/2017       wli        Issue 180675 - Initialized the parameter which named "autoSaveResultType" and call syncResultToParent in the
//                                            method which named "handleOnButtonClick" for UI change, used "getReturnCtxOfDivPopUp()" to
//                                            call setAddressChanges() for UI change.
// 09/21/2017       eyin       Issue 169483 - Modified handleOnButtonClick() to set address info for Exposure.
// 11/02/2018       clm        Issue 195889 - Add handleGetCustomPageOptions to disable the checkbox in the grid header
//-----------------------------------------------------------------------------

function handleOnLoad() {
    // disable selectAll checkbox
    if (hasObject("HCSELECT_IND")) {
        getObject("HCSELECT_IND").disabled = true;
    }
}

//-----------------------------------------------------------------------------
// To handle onclick event for the select checkbox
//-----------------------------------------------------------------------------
function userRowchange(c) {
    var isSelected = availableAddressListGrid1.recordset("CSELECT_IND").value == -1;
    if (isSelected) {
        var addressId = availableAddressListGrid1.recordset("CADDRESSID").value;
        var nodes = availableAddressListGrid1.documentElement.selectNodes("//ROW[CSELECT_IND='-1']");
        var length = nodes.length;
        if (length > 0) {
            for (var i = 0; i < length; i++) {
                var node = nodes.item(i);
                if (addressId == node.selectNodes("CADDRESSID").item(0).text) {
                    continue;
                }
                node.selectNodes("CSELECT_IND").item(0).text = "0";
            }
        }
    }
    syncChanges(origavailableAddressListGrid1, availableAddressListGrid1, "CSELECT_IND = '-1'  or CSELECT_IND='0'");
    isChanged = true;
}


function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SAVE':
            // 180663: User selects one address and save, then system prefill the State/County
            if(isFromExposure == "Y"){
                var locations = new Array();
                var locationCount = 0;
                first(availableAddressListGrid1);
                while (!availableAddressListGrid1.recordset.eof) {
                    if (availableAddressListGrid1.recordset("CSELECT_IND").value == -1) {
                        locations[locationCount] = {addressId:availableAddressListGrid1.recordset("ID").value,
                            stateCode:availableAddressListGrid1.recordset("CSTATECODE").value,
                            countyCode:availableAddressListGrid1.recordset("CCOUNTYCODE").value};
                        locationCount ++;
                    }
                    next(availableAddressListGrid1);
                }
                first(availableAddressListGrid1);
                if (locationCount == 0) {
                    alert(getMessage("pm.selectAddress.NoSelection.error"));
                    return false;
                }else if(locationCount > 1){
                    alert(getMessage("pm.selectAddress.singleSelect.error"));
                    return false;
                }
                getReturnCtxOfDivPopUp().handleOnSelectLocation(locations);
                closeThisDivPopup();
                break;
            }

            // submit the form only when isChanged is true or it is the first time to link the address to entity role,
            // and at same time there must has one record selected.
            var selNodes = availableAddressListGrid1.documentElement.selectNodes("//ROW[CSELECT_IND='-1']");
            var nodes = availableAddressListGrid1.documentElement.selectNodes("//ROW[CTRANSACTIONLOGID='']");
            if (selNodes.length > 0 && (availableAddressListGrid1.recordset.recordCount == nodes.length || isChanged)) {
                document.forms[0].process.value = "saveEntityRoleAddress";
                autoSaveResultType = commonOnSubmitReturnTypes.submitSuccessfully;
                submitForm(true);
            }else{
                autoSaveResultType = commonOnSubmitReturnTypes.noDataChange;
            }
            syncResultToParent(autoSaveResultType);
            break;
        case 'DONE':
            // Pass address information only when isChanged is true or it is the first time to link the address to entity role,
            // and at same time there must has one record selected.
            var nodes = availableAddressListGrid1.documentElement.selectNodes("//ROW[CTRANSACTIONLOGID='']");
            var selNodes = availableAddressListGrid1.documentElement.selectNodes("//ROW[CSELECT_IND='-1']");
            if (selNodes.length > 0 && (availableAddressListGrid1.recordset.recordCount == nodes.length || isChanged)) {
                getReturnCtxOfDivPopUp().setAddressChanges(getChanges(availableAddressListGrid1));
            }
            isChanged = false;
            commonOnButtonClick('CLOSE_DIV');
            break;
    }
}

function handleGetCustomPageOptions() {
    function __isSelectAllEnabled(gridInfo) {
        if (gridInfo.id == "availableAddressListGrid") {
            return false;
        }
    }

    return dti.oasis.page.newCustomPageOptions()
            .addSelectAllEnabledFunction("availableAddressListGrid", __isSelectAllEnabled);
}

