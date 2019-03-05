/*
 Revision Date    Revised By  Description
 ----------------------------------------------------------------------------
 09/21/2018        dpang      195835 - grid replacement.
                                       1) Add customPageOptions to replace grid fields which failed to be loaded by LOV with riskName.
 12/03/2018        dpang      195835 - grid replacement - Modified according to new framework change.
 ----------------------------------------------------------------------------
 */
var hasErrorMessages = "";
var currentSelectedNoteContent = "";
var isSourcePolicy = true;
var isForNoteDivPopup = false;

function handleOnButtonClick(btn) {
    //No need to replace riskDescription when clicking "ADD" btn
    if (btn == 'DELETE') {
        if (!useJqxGrid) {
            replaceRiskDescription();
        }
    }
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            setObjectValue("process", "saveAllAmalgamation");
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function amalgamationListGrid_setInitialValues() {
    var url = getAppPath() + "/amalgamationmgr/maintainAmalgamation.do?"
            + commonGetMenuQueryString() + "&process=getInitialValuesForAmalgamation";

    new AJAXRequest("get", url, '', setInitialValuesForAmalgamation, false);
}

function setInitialValuesForAmalgamation(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                /* no default value found */
                return;
            }
            var selectedDataGrid = getXMLDataForGridName("amalgamationListGrid");
            /* Parse xml and get inital values(s) */
            var oValueList = parseXML(data);
            setRecordsetByObject(selectedDataGrid, oValueList[0]);
        }
    }
}

function handleOnLoad() {
    if (!useJqxGrid) {
        replaceRiskDescription();
        // Goes to the incorrect row if system throws exception.
        if (validateRowId && validateRowId > 0) {
            selectRowById("amalgamationListGrid", validateRowId);
        }
    }
}
//-----------------------------------------------------------------------------
// System passes source/dest policy no to the LOV which is used to load the source/dest risk name,
// but system can't loads all the risk name correctly beacase of framework defect.  
// For this use case,system loops the recordset to replace the source/dest risk name
// by risk description when the page is loaded everytime, and then system goes to the first row.
// When system throws exception, the page is loaded and the first row is selected,
// in fact,the incorrect row should be selected.
//-----------------------------------------------------------------------------
function replaceRiskDescription() {
    first(amalgamationListGrid1);
    while (!amalgamationListGrid1.recordset.eof) {
        amalgamationListGrid1.recordset("CSOURCERISKBASERECORDIDLOVLABEL").value = amalgamationListGrid1.recordset("CSOURCERISKNAMEDESC").value;
        amalgamationListGrid1.recordset("CDESTRISKBASERECORDIDLOVLABEL").value = amalgamationListGrid1.recordset("CDESTRISKNAMEDESC").value;
        next(amalgamationListGrid1);
    }
    first(amalgamationListGrid1);
}

function handleGetCustomPageOptions() {
    return dti.oasis.page.newCustomPageOptions()
        .onBeforeGridSourceLoadComplete("amalgamationListGrid", function (records, originalRecords) {
            for (var i = 0; i < records.length; i++) {
                //Change both the loaded record and the original record
                var originalRecord = originalRecords[i];
                replaceWithRiskNameDesc(originalRecord, originalRecord);
                replaceWithRiskNameDesc(records[i], originalRecord);
            }
            return records;
        })
}

function replaceWithRiskNameDesc(record1, record2) {
    record1["CSOURCERISKBASERECORDIDLOVLABEL"] = record2["CSOURCERISKNAMEDESC"];
    record1["CDESTRISKBASERECORDIDLOVLABEL"] = record2["CDESTRISKNAMEDESC"];
}