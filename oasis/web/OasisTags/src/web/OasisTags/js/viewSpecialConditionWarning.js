// GUI functions (viewSpecialConditionWarning.js)
// Purpose: It Checks whether the load all special condition warning function is available.
//-----------------------------------------------------------------------------
/*
  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------

  -----------------------------------------------------------------------------
*/
var v_subsystem;
var v_specialConditionType;
var v_sourceType;
var v_sourceId;
function viewSpecialConditionMessages(subsystem, specialConditionType,sourceType,sourceId){
    // If subsystem is empty, defaults "OASIS PM" to subsystem.
    if(isEmpty(subsystem)){
       subsystem = "OASIS PM"; 
    }
    v_subsystem = subsystem;
    v_specialConditionType = specialConditionType;
    v_sourceType = sourceType;
    v_sourceId = sourceId;
    var url = getCSPath() + "/warning/viewSpecialConditionWarning.do?process=validateAvailabelForWarning"
            + "&subsystem=" + v_subsystem + "&specialConditionType=" + v_specialConditionType
            + "&sourceType=" + v_sourceType + "&sourceId=" + v_sourceId
            + "&currectTime=" + Date.parse(new Date());
    new AJAXRequest("get", url, '', validateAvailabelForWarningDone, false);
}

function validateAvailabelForWarningDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                if(oValueList[0]['ISWARNINGMESSAGE'] == 'Y'){
                   displaySpecialConditionWarningPage(); 
                }
            }
        }
    }
}

function displaySpecialConditionWarningPage(){
   var warningUrl = getCSPath() + "/warning/viewSpecialConditionWarning.do?process=loadAllSpecialConditionWarning"
        + "&subsystem=" + v_subsystem + "&specialConditionType=" + v_specialConditionType
        + "&sourceType=" + v_sourceType + "&sourceId=" + v_sourceId;
   var divPopupId = openDivPopup("", warningUrl, true, true, "", "", "800", "", "", "", "", false);
}