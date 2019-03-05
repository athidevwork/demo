//-----------------------------------------------------------------------------
// Javascript file for maintainReinsurance.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/02/2011       ryzhao      123408 - Check if participationPct is valid percentage value.
// 11/25/2014       kxiang      158853 -
//                              1. Modified reinsuranceListGrid_setInitialValues to change call function
//                              2. Added handleOnGetInitialValuesForReinsurance.
//                              3. Added handlePostAddRow to set href to grid reinsurance.
//-----------------------------------------------------------------------------
function validateGrid() {
    return true;
}

function handleOnSubmit(action) {
    var proceed = false;

    switch (action) {
        case 'SAVE':
            var participationPct = getObjectValue("participationPct");
            if (!isEmpty(participationPct)) {
                var floatValue = parseFloat(participationPct);
                if (floatValue > 100 || floatValue < 0) {
                    alert(getMessage("pm.maintainReinsurance.invalidParticipationPercent"));
                    break;
                }
            }
            document.forms[0].process.value = "saveAllReinsurance";
            document.forms[0].action = getAppPath() + "/policymgr/reinsurancemgr/maintainReinsurance.do";
            proceed = true;
            break;
    }
    return proceed;
}

function reinsuranceListGrid_setInitialValues() {
    var path = getAppPath() + "/policymgr/reinsurancemgr/maintainReinsurance.do?"
        + commonGetMenuQueryString() + "&process=getInitialValuesForReinsurance";
    new AJAXRequest("get", path, '', handleOnGetInitialValuesForReinsurance, false);
}

function handleOnGetInitialValuesForReinsurance(ajax) {
    commonHandleOnGetInitialValues(ajax, "REINSURERENTITYIDHREF");
}

//-----------------------------------------------------------------------------
// Set  grid value from XML data and handle risk name value for nameHref.
//-----------------------------------------------------------------------------
function handlePostAddRow(table) {
    if (table.id == "reinsuranceListGrid") {
        var xmlData = getXMLDataForGridName("reinsuranceListGrid");
        var fieldCount = xmlData.recordset.Fields.count;
        var reinsuranceCount;
        for (var i = 0; i < fieldCount; i++) {
            if (xmlData.recordset.Fields.Item(i).name == "CREINSURERENTITYID") {
                reinsuranceCount = i;
            }
            if (xmlData.recordset.Fields.Item(i).name.substr(4) == "" + reinsuranceCount) {
                var href = "javascript:void(0);";
                if (!isEmpty(getObjectValue("REINSURERENTITYIDHREF"))) {
                    href = "javascript:handleOnGridHref('reinsuranceListGrid', '"
                            + getObjectValue("REINSURERENTITYIDHREF") + "');";
                }
                xmlData.recordset.Fields.Item(i).value = href;
            }
        }
    }
}
