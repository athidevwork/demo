//-----------------------------------------------------------------------------
// Functions to support Address Add page.
// Author: unknown
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 04/18/2007    kshen       Modified mini popup window name.
// 09/05/2008    kshen       Added codes to add email icon beside full name field.
// 04/16/2010    kshen       Deleted the codes about adding email icon and send mail
// 09/01/2011       Michael Li  issue 121133
// 04/17/2018    dzhang      Issue 192649: entity mini popup refactor
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// Button handler
//-----------------------------------------------------------------------------
function btnClick(btnID) {
  if (btnID == 'close') {
      closeWindow();
  }
}

/**
 *  On clicking "Go to Client" button on enity mini popup page, it is directed to entity modify page.
 */
function goToEntity(btnID, entityRootContext,goToGlance) {
  if (btnID == 'goToClient') {
    var entPkElm = getSingleObject('pk');
    var entTypeElm = getSingleObject('entityType');
    var entNameElm = getSingleObject('entityName');
    goToEntityModify(entPkElm.value,entTypeElm.value,entNameElm.value,entityRootContext,goToGlance);
  }
}

/**
 *  Build up entity modify page URL
 */
function goToEntityModify(pk, type,name, entityRootContext,goToGlance) {

  entityRootContext = generateURL();
  var url = "?pk=" + pk + "&entityType=" + type;
  // Don't modify the popup window name "CISPopup"!!!!!!!!!!!!!!
  // In cclaims.jsp, we use the window name to check if this is a popup page.
  if(goToGlance){
    url = entityRootContext+"/ciEntityGlance.do" + url+ "&entityName=" + encodeURIComponent(name);
    openEntityModifyWindow(url);
  }
  else if (type.substr(0, 1) == 'P' || type.substr(0, 1) == 'O') {
    url = entityRootContext+ (type.substr(0, 1) == 'P' ? "/ciEntityPersonModify.do" : "/ciEntityOrgModify.do")+ url;
    openEntityModifyWindow(url);
  }
  else {
    alert('Unknown entity type.');
  }
}

/*
 * go to entity modify window
 */
function openEntityModifyWindow(url) {
    var mainwin = window.open(url ,'CISPopup','width=1000,height=650,resizable=yes,scrollbars=yes,status=yes,top=5,left=5');
    if(mainwin)
        mainwin.focus();
    closeWindow();
}

/*
 * Generate CIS URL via current path
 */
function generateURL() {
    var cisRoot = getTopNavApplicationUrl("CIS");
    if (!cisRoot) {
        // retrieve the web context root for current application
         var appPath = getAppPath();
        // pick up the parent context root
         var parentContextRoot = appPath.replace(/\/[^\/]+$/,'');
        // infer the context url for eClaim
         cisRoot = parentContextRoot + "/" + "CIS";
    }
    return cisRoot;
}




