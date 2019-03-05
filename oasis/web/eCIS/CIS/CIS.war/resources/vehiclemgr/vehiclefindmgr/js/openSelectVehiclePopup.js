//----------------------------------------------------------------------------------------------------
// This structure is used to set parameters before invoking openSelectVehicleWindow or openSelectVehicleDivPopup
// in the Parent Window
// For Example:
//     OPEN_SELECT_VEHICLE_PARAMETERS.entityId = "123456";
//----------------------------------------------------------------------------------------------------
var OPEN_SELECT_VEHICLE_PARAMETERS =
{
    "entityId":"",
    "entityDescription":"Client",
    "callbackEvent":""
}

//----------------------------------------------------------------------------------------------------
// This structure is used to get selected vehicle information at callbackEvent in the Parent Window
// For Example:
//  function handleVehicleSelected(){
//      alert(SELECTED_VEHICLE_INFO.vehicleDescription);
//      alert(SELECTED_VEHICLE_INFO.vehicleNo);
//  }
//----------------------------------------------------------------------------------------------------
var SELECTED_VEHICLE_INFO =
{
    "vehicleId":"",
    "entityId":"",
    "vehicleDescription":"",
    "vehicleNo":"",
    "serialNo":"",
    "tagNo":"",
    "vin":"",
    "vehicleType":"",
    "make":"",
    "YEAR":"",
    "licenseState":"",
    "garageState":""
}

//-----------------------------------------------------------------------------
// This function is to open Vehicle Select Window
//-----------------------------------------------------------------------------
function openSelectVehicleWindow() {
    var parametersURL = getSelectVehicleParametersUrl();
    if (!isWhitespace(parametersURL))
        url = getCISPath() + "/ciVehicleSearchPop.do?withInitialParameters=Y&" + parametersURL.substring(0, parametersURL.length - 1);
    var mainwin = window.open(url, 'VehicleSelect', 'width=900,height=700,innerHeight=700,innerWidth=875,scrollbars');
    mainwin.focus();
}

//-----------------------------------------------------------------------------
// This function is to open Vehicle Select Div Popup
//-----------------------------------------------------------------------------
function openSelectVehicleDivPopup() {
    var parametersURL = getSelectVehicleParametersUrl();
    var url = "";
    if (!isWhitespace(parametersURL))
        url = getCISPath() + "/ciVehicleSearchPop.do?withInitialParameters=Y&" + parametersURL.substring(0, parametersURL.length - 1);
    var divPopupId = openDivPopup("", url, true, true, "10", "10", "1030", "600", "1005", "575", "", true);
}

//-----------------------------------------------------------------------------
// This function is to parse parameters before opening Vehicle Select Page
//-----------------------------------------------------------------------------
function getSelectVehicleParametersUrl() {
    // Set parameters into URL if there are any, except callbackEvent
    var parametersURL = "";
    if (!isEmpty(OPEN_SELECT_VEHICLE_PARAMETERS.entityId)) {
        parametersURL += "pk=" + OPEN_SELECT_VEHICLE_PARAMETERS.entityId + "&";
    }
    if (!isEmpty(OPEN_SELECT_VEHICLE_PARAMETERS.entityDescription)) {
        parametersURL += "entityDescription=" + OPEN_SELECT_VEHICLE_PARAMETERS.entityDescription + "&";
    }
    return parametersURL;
}