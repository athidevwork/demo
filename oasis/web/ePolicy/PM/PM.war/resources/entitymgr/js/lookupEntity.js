//-----------------------------------------------------------------------------
// Javascript file for lookupEntity.js
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   May 2, 2007
// Author: sxm
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 03/18/2011       jshen       Add handleOnLoad function to hide Select button if grid doesn't exist
// 09/09/2013       adeng       Modified handleOnButtonClick() to see if there are single or double quotes in the
//                              entityName field, and use different way to to make it allow for the single or double
//                              quotes in the entityName field, but don't allow it has both single and double quotes.
// 03/10/2017       eyin        Modified to invoke call back function in proper parent window for Tab style.
// 11/02/2018       clm         195889 -  Grid replacement using getParentWindow, add deferred logic in handleOnLoad,
//                                        and change handleOnKeyDown.
//-----------------------------------------------------------------------------
function handleOnLoad() {
    if (isDefined(getObject("lookupEntityGrid1"))) {
        $.when(dti.oasis.grid.getLoadingPromise("lookupEntityGrid")).then(function () {
            if (typeof lookupEntityGrid1 == "undefined" || isEmptyRecordset(lookupEntityGrid1.recordset)) {
                getObject("PM_SELENTITYS").disabled = true;
            }
        });
    }
}

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Filter':
            var entityName = getObjectValue("entityName").toUpperCase();
            if (entityName.length > 0) {
                if (entityName.indexOf("\'") > -1 && entityName.indexOf("\"") > -1) {
                    alert(getMessage("pm.lookupEntity.quotation.error"));
                    return;
                }
                else if (entityName.indexOf("\'") > -1) {
                    var filter = 'starts-with(translate(CENTITYNAME, "abcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZ"), "' + entityName + '")';
                    setTableProperty(lookupEntityGrid, "filterHasSingleQt", true);
                }
                else {
                    var filter = "starts-with(translate(CENTITYNAME, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), '" + entityName + "')";
                    if (entityName.indexOf("\"") > -1) {
                        setTableProperty(lookupEntityGrid, "filterHasDoubleQt", true);
                    }
                }
                lookupEntityGrid_filter(filter);
            } else {
                lookupEntityGrid_filter("");
            }
            if (isEmptyRecordset(lookupEntityGrid1.recordset)) {
                getObject("PM_SELENTITYS").disabled = true;
                handleError(getMessage("pm.lookupEntity.NoDataFound"));
                // Can't hide empty table before handleError.
                hideEmptyTable(getTableForXMLData(lookupEntityGrid1));
            }
            else {
                showNonEmptyTable(getTableForXMLData(lookupEntityGrid1));
                selectFirstRowInGrid("lookupEntityGrid");
                getObject("PM_SELENTITYS").disabled = false;
            }
            break;

        case 'Select':
            var entityId = lookupEntityGrid1.recordset("ID").value;
            var entityName = lookupEntityGrid1.recordset("CENTITYNAME").value;
            oParentWindow.handleOnLookupEntity(btn, entityId, entityName);
            closeThisDivPopup();
            break;

        case 'Cancel':
            closeThisDivPopup();
            break;
    }

    return true;
}

//-----------------------------------------------------------------------------
// The handleOnKeyDown function will submit the form automatically,
// so system returns false to prevent submitting when the underwriter enters 'Enter' key.
//-----------------------------------------------------------------------------
function handleOnKeyDown(field, event){
    var evt = fixEvent(event);

    var code = evt.keyCode;
    if (code == 13) {
        return false;
    }
    return true;
}