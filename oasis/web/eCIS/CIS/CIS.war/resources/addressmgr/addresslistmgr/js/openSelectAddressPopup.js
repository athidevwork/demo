//----------------------------------------------------------------------------------------------------
// This structure is used to set parameters before invoking openSelectAddressWindow or openSelectAddressDivPopup
// in the Parent Window
// For Example:
//     OPEN_SELECT_ADDRESS_PARAMETERS.addressId = "123456";
//----------------------------------------------------------------------------------------------------
var OPEN_SELECT_ADDRESS_PARAMETERS =
{
    "addressId":"",
    "entityId":"",
    "callbackEvent":""
}

//----------------------------------------------------------------------------------------------------
// This structure is used to get selected address information at callbackEvent in the Parent Window
// For Example:
//  function handleAddressSelected(){
//      alert(SELECTED_ADDRESS_INFO.addressId);
//      alert(SELECTED_ADDRESS_INFO.addressSingleLine);
//  }
//----------------------------------------------------------------------------------------------------
var SELECTED_ADDRESS_INFO =
{
    "addressId":"",
    "addressSingleLine":""
}

//-----------------------------------------------------------------------------
// This function is to open Address Select Window
//-----------------------------------------------------------------------------
function openSelectAddressWindow() {
    var parametersURL = getSelectAddressParametersUrl();
    if (!isWhitespace(parametersURL))
        url = getCISPath() + "/ciAddressPickList.do?withInitialParameters=Y&" + parametersURL.substring(0, parametersURL.length - 1);
    var mainwin = window.open(url, 'AddressSelect', 'width=900,height=700,innerHeight=700,innerWidth=875,scrollbars');
    mainwin.focus();
}

//-----------------------------------------------------------------------------
// This function is to open Address Select Div Popup
//-----------------------------------------------------------------------------
function openSelectAddressDivPopup() {
    var parametersURL = getSelectAddressParametersUrl();
    var url = "";
    if (!isWhitespace(parametersURL))
        url = getCISPath() + "/ciAddressPickList.do?withInitialParameters=Y&" + parametersURL.substring(0, parametersURL.length - 1);
    var divPopupId = openDivPopup("", url, true, true, "10", "10", "680", "510", "655", "485", "", true);
}

//-----------------------------------------------------------------------------
// This function is to parse parameters before opening Address Select Page
//-----------------------------------------------------------------------------
function getSelectAddressParametersUrl() {
    // Set parameters into URL if there are any, except callbackEvent
    var parametersURL = "";
    if (!isEmpty(OPEN_SELECT_ADDRESS_PARAMETERS.addressId)) {
        parametersURL += "addressId=" + OPEN_SELECT_ADDRESS_PARAMETERS.addressId + "&";
    }
    if (!isEmpty(OPEN_SELECT_ADDRESS_PARAMETERS.entityId)) {
        parametersURL += "entityId=" + OPEN_SELECT_ADDRESS_PARAMETERS.entityId + "&";
    }
    return parametersURL;
}