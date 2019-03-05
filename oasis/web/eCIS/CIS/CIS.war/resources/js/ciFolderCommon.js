//-----------------------------------------------------------------------------
// Functions to support cis folder tab pages.
// Author:
// Date:
// Modifications:
//-----------------------------------------------------------------------------
// 06/04/2009   Kenney     For issue 94437. eCIS Form Letter, the source table name should be ENTITY instead of CIS
// 09/14/2009   kshen      Added method cisFolderOpenAttacchs().
// 09/30/2010   wfu        111776: Replaced hardcode string with resource definition
// 08/22/2013   Parker     Issue#142990 default externalId to clientId in File Notes Page
// 03/29/2016   jld        Issue 167866. Add Credential Letter.
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// Opens the Form Letters popup
//-----------------------------------------------------------------------------
function cisFolderOpenFormLetters() {
  if (document.forms[0].pk) {
    // claimPK is a field on the form.
    openPopup(getCSPath() + "/csFormLetter.do?sourceTableName=ENTITY&sourceRecordFk="+document.forms[0].pk.value, "Forms", 720, 620, 10, 10,'yes');
  } else {
    alert(getMessage("ci.entity.message.formLetters.open"));
  }
}


//-----------------------------------------------------------------------------
// Opens the Managerment Attachment window.
//-----------------------------------------------------------------------------
function cisFolderOpenAttacchs() {
    if (document.forms[0].pk) {
        var url = getCSPath() + "/csAttach.do?sourceTableName=ENTITY&sourceRecordFk=" + document.forms[0].pk.value;
        openDivPopup("", url, true, true, 10, 10, 850, 700, "", "", "", true, "", "", true);
    } else {
        alert(getMessage("ci.entity.message.attachments.open"));
    }
}

//-----------------------------------------------------------------------------
// Opens the Managerment File Notes window.
//-----------------------------------------------------------------------------
function cisFolderOpenFileActivityNotes() {
    var notesUrl = getCSPath() + "/fileactivitynotesmgr/maintainFileNotes.do?";
    notesUrl += "search_entityId=" + getObjectValue("pk");
    notesUrl += "&search_subsystemCode=CIS";
 //  notesUrl += "&search_noteCategoryCode=ALL";
    notesUrl += "&search_externalId=";
    if (getObject("cisHeaderClientId")) {
        notesUrl += getObjectValue("cisHeaderClientId");
    }
    openDivPopup("", notesUrl, true, true, "", "", 850, 700, "", "", "", true, "", "", true);
}

//-----------------------------------------------------------------------------
// Opens the Credential Request window.
//-----------------------------------------------------------------------------
function cisFolderCredentialRequest() {
    if (getObjectValue("entityType")!="O") {
        alert(getMessage("ci.credentialRequest.entity.type"));
        return;
    }
    var crUrl = getAppPath() + "/ciCredentialRequest.do?";
    crUrl += "pk=" + getObjectValue("pk");
    crUrl += "&entityType="+ getObjectValue("entityType");
    openPopup(crUrl, "CredentialRequest", 1400, 800,10,10,true);
}

// Invoked handleFilterCisList in commonOnLoad of common.js
// function commonOnLoad() {
//     var functionExists = eval("window.handleFilterCisList");
//     if (functionExists) {
//         handleFilterCisList();
//     }
// }

function commonOnRefresh() {
    setInputFormField("__REFRESH_CIS_HEADER_FIELDS", "Y")
}

function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}
