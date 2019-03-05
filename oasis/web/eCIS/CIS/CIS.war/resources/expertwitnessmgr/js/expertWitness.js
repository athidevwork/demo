var isChanged = false;

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

//-----------------------------------------------------------------------------
// This function is needed to navi to some tabs(vendor,address...)
//-----------------------------------------------------------------------------
function btnClick(btnID) {
    if (btnID != 'save' && btnID != 'add' && btnID != ' delete' && isChanged) {
       if (btnID == 'refresh') {
            if (!confirm(ciRefreshPageConfirmation)) {
                return;
            }
        } else {
            if (!confirm(ciDataChangedConfirmation)) {
                return;
            }
        }
    }

    if (btnID == 'history') {
        var path = getAppPath() + "/claimcodehistory/claimCodeHistory.do?process=load&isCodeType=EXPWITNESS_STATUS&pk="
                + getObjectValue("pk")+"&entityName="+ encodeURIComponent(getObjectValue("entityName"))
                + "&srcRecId=" + getObjectValue("pk");
        var divPopupId = openDivPopup("View Claim Code History", path, true, true, "", "", "", "400", "", "", "", true);

    } else if (btnID == 'change_status') {
        changeStatus();
    }else if (btnID == 'print') {
        var paramsObj = new Object();
        paramsObj.entityPk = getObjectValue("pk");
        paramsObj.reportCode = "CIS_EXP_WITNESS_WORKSHEET";
        viewCIReport(paramsObj);
    }
}

function changeStatus(){
    var path =  getAppPath() + "/ciExpertWitness.do?" +"process=changeStatus&pk="+getObjectValue("pk");
    new AJAXRequest("POST", path, '', handleOnChangeStatus, true);
}

function handleOnChangeStatus(ajax){
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseText;
            if (data != "Y") {
                alert(getMessage("ci.common.error.status.change"));
                return;
            }
            else {
                //Status is changed
                var currStatus, newStatus;
                currStatus = getSingleObject("entity_char2").value;
                if (currStatus == 'ACTIVE') {
                   newStatus = 'INACTIVE';
                } else {
                   // It was null or Inactive
                   newStatus = 'ACTIVE';
                }
                setObjectValue("entity_char2",newStatus);
            }
        }
    }

}
//-----------------------------------------------------------------------------
// Define following function to avoid javascript error
//-----------------------------------------------------------------------------
function checkForm() {
}

function addressGrid_selectRow(pk) {
}

function phoneGrid_selectRow(pk) {
}

function educationGrid_selectRow(pk) {
}

function classificationGrid_selectRow(pk) {
}

function relationsGrid_selectRow(pk) {
}

function claimsGrid_selectRow(pk) {
}

//-----------------------------------------------------------------------------
// Open notes window by calling function loadNotes in csloadnotes.js.
// This for the claims section
//-----------------------------------------------------------------------------
function openNotesForExpertWitness(pk) {
    if (window.loadNotes) {
        loadNotes(pk, 'ENTITY_ROLE', 'CLAIM_PARTICIPANT');
    }
    else {
        alert(getMessage("js.is.notes.notAvailable"));
    }
}
//-----------------------------------------------------------------------------
// Open notes window by calling function loadNotes in csloadnotes.js.
// This is for the Person Section
//-----------------------------------------------------------------------------
function openNotesForExpertWitnessPerson() {
   var entityPk = getObjectValue("pk");
   if (window.loadNotesWithReloadOption) {
        loadNotesWithReloadOption(entityPk, 'ENTITY', 'EXPWITNESS',true,true);
    }
    else {
        alert(getMessage("js.is.notes.notAvailable"));
    }
}
function handleOnLoad() {
    // Turn off the button group when we when we have no name (which = no data)
    if (getObject("entity_entityNameComputed").value == "" ) {

       // Turn off the button group...
        $(".horizontalButtonCollection")[0].style.display="none";
       getSingleObject("panelContentIdForCIPersonForm").innerHTML = "";
    }
}

