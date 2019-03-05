//-----------------------------------------------------------------------------
// Javascript file for maintainInsuredTrackingListGrid.jsp.
//
// (C) 2015 Delphi Technology, inc. (dti)
// Date:   April 08, 2015
// Author: wdang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 03/16/2016       tzeng       167532 - Add audit for renewal flag.
//-----------------------------------------------------------------------------

function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Done':
            var contextId = getObjectValue("contextId");
            document.forms[0].action = getAppPath() + "/transactionmgr/auditmgr/viewAudit.do?"
                + commonGetMenuQueryString() + "&process=loadAllAudit&"+ "&contextId="+contextId;
            submitFirstForm();
            break;
    }

}
function handleOnLoad(){
    var fromPage=getObjectValue("fromPage");
    var obj = getSingleObject("auditLevel");
    switch(fromPage){
    case "policy-policy":
       obj.options[2]=null;
       break;
    case "policy-component":
      obj.options[2].text = "Selected Component";
      break;
    case "risk-risk":
      obj.options[2].text = "Selected Risk";
      break;
    case "coverage-coverage":
       obj.options[2].text = "Selected Coverage";
      break;
     case "coverage-component":
       obj.options[2].text = "Selected Component";
     break;
     case "coverageclass-class":
       obj.options[2].text = "Selected ";
     case "renewalFlag-renewalFlag":
       obj.options[2].text = "Selected Renewal Flag";
     break;
    }
}