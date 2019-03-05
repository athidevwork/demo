//-----------------------------------------------------------------------------
// Javascript file for maintainSurchargePoints.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Aug 04, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/04/2010       syang       103793 - Modified handleOnLoad() to hide the form section if PM_CUST_SURCG_POINTS is "Y".
//-----------------------------------------------------------------------------
function handleOnSubmit(action) {
    var proceed = false;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllSurchargePoint";
            proceed = true;
            break;
    }

    return proceed;
}

//-----------------------------------------------------------------------------
// Get total value by column Id in data island
//-----------------------------------------------------------------------------
function getTotalValue(columnId) {
    var result = 0;
    var rowid = surchargePointsGrid1.recordset("ID").value;
    first(surchargePointsGrid1);
    while (!surchargePointsGrid1.recordset.eof) {
        var sValue = surchargePointsGrid1.recordset(columnId).value;
        if (!isEmpty(sValue) && isSignedInteger(sValue)) {
            result = result + parseInt(sValue);
        }
        next(surchargePointsGrid1);
    }
    first(surchargePointsGrid1);
    selectRow("surchargePointsGrid", rowid);
    return result;
}

function handleOnChange(obj) {
    if (obj.name == "overridePoints") {
        var oTotalOverridePoints = getObject("overridePointsTotalROSPAN");
        oTotalOverridePoints.innerText = getTotalValue("COVERRIDEPOINTS");
        return true;
    }
    return true;
}

//-----------------------------------------------------------------------------
// Set initial values for total fields
//-----------------------------------------------------------------------------
function setInitailTotalValue() {
    var oTotalCalPoints = getObject("calculatedPointsTotalROSPAN");
    var oTotalOverridePoints = getObject("overridePointsTotalROSPAN");
    if (oTotalCalPoints) {
        oTotalCalPoints.innerText = getTotalValue("CCALCULATEDPOINTS");
    }
    if (oTotalOverridePoints) {
        oTotalOverridePoints.innerText = getTotalValue("COVERRIDEPOINTS");
    }
}

function handleOnLoad() {
    if (!isEmptyRecordset(surchargePointsGrid1.recordset)) {
        setInitailTotalValue();
    }
    // If PM_CUST_SURCG_POINTS is "Y", the form section should be hidden.
    var pmCustSurgPoints = getSysParmValue("PM_CUST_SURCG_POINTS");
    if(pmCustSurgPoints == "Y"){
       hideGridDetailDiv("surchargePointsGrid"); 
    }
}