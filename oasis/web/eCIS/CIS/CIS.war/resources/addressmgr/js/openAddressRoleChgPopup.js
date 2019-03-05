//-----------------------------------------------------------------------------
// Functions to support Address List page.
// Author: kshen
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 05/15/2013    Elvin       Issue 144575: encode address description when it is passing into request url
//-----------------------------------------------------------------------------
var OPEN_ADDRESS_ROLE_CHG_PARAMETERS =
{
    "addressId":"",
    "addressDesc":"",
    "entityId":"",
    "isUsedForChange":"",
    "isPrimaryAddrChange":"",
    "callbackEvent":""
}


var SELECTED_ADDRESS_ROLE_CHG_INFO =
{
    "isTransRolesToNewAddress":""
}

function openAddressRoleChgPopup() {
    var url = getAppPath() + "/ciChgAddressRole.do?process=loadAddressRoleChg&" + getAddressRoleChgParametersUrl();
    openDivPopup("", url, true, true, "", "", "850", "620", "", "", "", true);
}

function openAddressRoleChgWarningDivPopup() {
    var url = getAppPath() + "/ciChgAddressRole.do?process=loadAddressRoleChgWarning&" + getAddressRoleChgParametersUrl();
    openDivPopup("", url, true, true, "", "", "750", "510", "", "", "", true);
}

//-----------------------------------------------------------------------------
// This function is to parse parameters
//-----------------------------------------------------------------------------
function getAddressRoleChgParametersUrl() {
    // Set parameters into URL if there are any, except callbackEvent
    var parametersURL = "useForWarning=Y&";
    if (!isEmpty(OPEN_ADDRESS_ROLE_CHG_PARAMETERS.entityId)) {
        parametersURL += "entityId=" + OPEN_ADDRESS_ROLE_CHG_PARAMETERS.entityId + "&";
    }
    if (!isEmpty(OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressId)) {
        parametersURL += "addressId=" + OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressId + "&";
    }
    if (!isEmpty(OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressDesc)) {
        parametersURL += "fromAddressDesc=" + encodeUrl(OPEN_ADDRESS_ROLE_CHG_PARAMETERS.addressDesc) + "&";
    }
    if (!isEmpty(OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isUsedForChange)) {
        parametersURL += "useForChange=" + OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isUsedForChange + "&";
    }
    if (!isEmpty(OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isPrimaryAddrChange)) {
        parametersURL += "primaryAddrChange=" + OPEN_ADDRESS_ROLE_CHG_PARAMETERS.isPrimaryAddrChange + "&";
    }
    return parametersURL.substring(0, parametersURL.length - 1);
}