//-----------------------------------------------------------------------------
// Modifications:
//-----------------------------------------------------------------------------
// 10/19/2018       Elvin       Issue 195835: grid replacement
//-----------------------------------------------------------------------------

var GRID_CENTITYID = "CENTITYID";
var GRID_CVEHICLEDESCRIPTION = "CVEHICLEDESCRIPTION";
var GRID_CVEHICLENO = "CVEHICLENO";
var GRID_CSERIALNO = "CSERIALNO";
var GRID_CTAGNO = "CTAGNO";
var GRID_CVIN = "CVIN";
var GRID_CVEHICLETYPE = "CVEHICLETYPE";
var GRID_CMAKE = "CMAKE";
var GRID_CYEAR = "CYEAR";
var GRID_CLICENSESTATE = "CLICENSESTATE";
var GRID_CGARAGESTATE = "CGARAGESTATE";

//-----------------------------------------------------------------------------
// This function is to handle button click
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'FILTER':
            setObjectValue("process", "loadEntityVehicleList");
            submitFirstForm();
            break;
        case 'CLEAR':
            clearFormFields(document.forms[0], false, true);
            setObjectValue("process", "loadEntityVehicleList");
            submitFirstForm();
            break;
        case 'SEL':
            if (isDefined(getObject("vehicleListGrid1"))) {
                if (getParentWindow()) {
                    //Set selected vehicle information
                    getParentWindow().SELECTED_VEHICLE_INFO.vehicleId = vehicleListGrid1.recordset("ID").value;
                    getParentWindow().SELECTED_VEHICLE_INFO.entityId = vehicleListGrid1.recordset(GRID_CENTITYID).value;
                    getParentWindow().SELECTED_VEHICLE_INFO.vehicleDescription = vehicleListGrid1.recordset(GRID_CVEHICLEDESCRIPTION).value;
                    getParentWindow().SELECTED_VEHICLE_INFO.vehicleNo = vehicleListGrid1.recordset(GRID_CVEHICLENO).value;
                    getParentWindow().SELECTED_VEHICLE_INFO.serialNo = vehicleListGrid1.recordset(GRID_CSERIALNO).value;
                    getParentWindow().SELECTED_VEHICLE_INFO.tagNo = vehicleListGrid1.recordset(GRID_CTAGNO).value;
                    getParentWindow().SELECTED_VEHICLE_INFO.vin = vehicleListGrid1.recordset(GRID_CVIN).value;
                    getParentWindow().SELECTED_VEHICLE_INFO.vehicleType = vehicleListGrid1.recordset(GRID_CVEHICLETYPE).value;
                    getParentWindow().SELECTED_VEHICLE_INFO.make = vehicleListGrid1.recordset(GRID_CMAKE).value;
                    getParentWindow().SELECTED_VEHICLE_INFO.YEAR = vehicleListGrid1.recordset(GRID_CYEAR).value;
                    getParentWindow().SELECTED_VEHICLE_INFO.licenseState = vehicleListGrid1.recordset(GRID_CLICENSESTATE).value;
                    getParentWindow().SELECTED_VEHICLE_INFO.garageState = vehicleListGrid1.recordset(GRID_CGARAGESTATE).value;
                    closeWindow(getParentWindow().OPEN_SELECT_VEHICLE_PARAMETERS.callbackEvent);
                }
            } else {
                alert(getMessage("ci.vehicle.searchSelect.msg.warning.vehicleNotFound", new Array(getObjectValue(vehiclefields.ENTITY_DESCRIPTION))));
            }
            break;
        case 'CANCEL':
            closeWindow();
            break;
        default: break;
    }
}
