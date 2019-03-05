//-----------------------------------------------------------------------------
// Java script file for reissuePolicy.jsp.
//
// (C) 2011 Delphi Technology, inc. (dti)
// Date:   April 07, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 04/18/2011       syang       116360 - Modified sendAJAXRequestToGetDefaultExpirationDate() to validate
//                                       termEffectiveFromDate before getting expiration date.
// 07/25/2014       awu         152034 - Modified to send 'ALL' to refreshPage after reissue.
//-----------------------------------------------------------------------------
var isTermEffectiveToDateChanged = 'N';

function handleOnChange(field) {
    var fieldName = field.name;
    if (fieldName == "termEffectiveFromDate" || fieldName == "termEffectiveToDate" ||
        fieldName == "issueCompanyEntityId" ||
        fieldName == "issueStateCode" || fieldName ==  "practiceStateCode") {
        sendAJAXRequestToGetDefaultExpirationDate(fieldName);                          
    }
}

function sendAJAXRequestToGetDefaultExpirationDate(fieldName) {
    // Validate termEffectiveFromDate
    if(!isValueDate(getObjectValue("termEffectiveFromDate"))){
        return;
    }
    // set flag
    if (fieldName == "termEffectiveToDate")
       isTermEffectiveToDateChanged = 'Y';

    // set url
    var url = "reissuePolicy.do?"+ commonGetMenuQueryString("PM_REISSUE_POLICY","")+
              "&process=getExpirationDateForReissuePolicy" +
              "&termEffectiveFromDate=" + getObjectValue("termEffectiveFromDate") +
              "&termEffectiveToDate=" + getObjectValue("termEffectiveToDate") +
              "&isTermEffectiveToDateChanged=" +isTermEffectiveToDateChanged+
              "&issueCompanyEntityId=" + getObjectValue("issueCompanyEntityId") +
              "&issueStateCode=" + getObjectValue("issueStateCode") ;
    // initiate call
    new AJAXRequest("get", url, "", processAJAXResponse, false);
}

function processAJAXResponse(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // reset field values regardless if we got messages or not
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setFormFieldValuesByObject(oValueList[0]);
            }
            handleAjaxMessages(data, null);
        }
    }
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'DONE':
           setInputFormField("isTermEffectiveToDateChanged",isTermEffectiveToDateChanged);
           postAjaxSubmit("/transactionmgr/reissueprocessmgr/reissuePolicy.do", "reissuePolicy",true,false,refreshParentPage);
           break;
   }
}

function refreshParentPage(ajax){
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            if (isDefined(window.frameElement)) {
                window.frameElement.document.parentWindow.refreshPage("ALL");
            }
            else {
                refreshPage("ALL");
            }
        }
    }
}
