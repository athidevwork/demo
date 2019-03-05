var visibleMenuIds = "";
var hoverStatus=false;
var timeoutId;

function showMenuUpto(menuId, currentMenuId) {
  if(menuId=="") {
    hideAllSubMenus();
  }
  if(visibleMenuIds!="") {
      var isMenuIdFound=false;

      visibleMenuIds = visibleMenuIds.substr(0, visibleMenuIds.length-1);   //Remove the last comma
      var startIdx = visibleMenuIds.lastIndexOf(",");
      while (!isMenuIdFound && visibleMenuIds) {
          var menuToHide = "";
          if(startIdx==-1) {
            menuToHide = visibleMenuIds;
            visibleMenuIds = "";
          } else {
            menuToHide = visibleMenuIds.substring(startIdx+1);
            visibleMenuIds = visibleMenuIds.substr(0, startIdx);
          }
          if(menuToHide==menuId) {
            isMenuIdFound=true;
            visibleMenuIds = (visibleMenuIds=="" ? "" : visibleMenuIds + ",") + menuToHide + ",";
          } else {
            var menuObj = getSingleObject("subMenuFor" + menuToHide);
            if(menuObj) {
                hideShowElementByClassName(menuObj, true);
            } else {
              menuObj = getSingleObject("tabSubMenuFor" + menuToHide);
              if(menuObj) {
                  hideShowElementByClassName(menuObj, true);
              }
            }
            startIdx = visibleMenuIds.lastIndexOf(",");
          }
      }
  }

  var menuObj = getSingleObject("subMenuFor" + currentMenuId);
  if(menuObj) {
      hideShowElementByClassName(menuObj, false);
  } else {
    menuObj = getSingleObject("tabSubMenuFor" + currentMenuId);
    if(menuObj) {
        hideShowElementByClassName(menuObj, false);
    }
  }
  visibleMenuIds = visibleMenuIds + currentMenuId + ",";
  return true;
}

function hideAllSubMenus() {
    if(visibleMenuIds!="") {
        visibleMenuIds = visibleMenuIds.substr(0, visibleMenuIds.length-1);   //Remove the last comma
        var startIdx = visibleMenuIds.lastIndexOf(",");
        while (visibleMenuIds) {
          var menuToHide = "";
          if(startIdx==-1) {
            menuToHide = visibleMenuIds;
            visibleMenuIds = "";
          } else {
            menuToHide = visibleMenuIds.substring(startIdx+1);
            visibleMenuIds = visibleMenuIds.substr(0, startIdx);
          }
          var menuObj = getSingleObject("subMenuFor" + menuToHide);
          if(menuObj) {
              hideShowElementByClassName(menuObj, true);
          } else {
            menuObj = getSingleObject("tabSubMenuFor" + menuToHide);
            if(menuObj) {
                hideShowElementByClassName(menuObj, true);
            }
          }
          startIdx = visibleMenuIds.lastIndexOf(",");
        }
    }
    visibleMenuIds = "";
}

function triggerTimeout(isCalledByTimeout) {
    if(hoverStatus && !isCalledByTimeout) {
        hoverStatus = false;
        timeoutId = window.setTimeout("triggerTimeout(true);", 1000);
    }
    if(isCalledByTimeout) {
        if(timeoutId) {
            window.clearTimeout(timeoutId);
            timeoutId = "";
            if(!hoverStatus) {
              hideAllSubMenus();
            }
        }
    }
}

function setMenuStatusToActive() {
  hoverStatus=true;
}

function showMenu(menuId, parentId) {
    hoverStatus=true;
    event.cancelBubble=true;
//            alert("menuId:"+menuId+" parentId:"+parentId+" visibleMenuIds:"+visibleMenuIds)
    showMenuUpto(parentId, menuId);
}

function getCSSProperty(cssPropertyName) {
    var cssStyle=null;
    if(document.styleSheets.length > 0) {
        var cssRules;
        for(var i = 0; i< document.styleSheets.length && cssStyle==null ; i++) {
            if(document.styleSheets[i].rules) {
                cssRules = document.styleSheets[i].rules;
            } else {
                cssRules = document.styleSheets[i].cssRules;
            }
            if(cssRules) {
                for(var j=0; j<cssRules.length && cssStyle==null ; j ++) {
                    if(cssRules[j].selectorText.toUpperCase()==cssPropertyName.toUpperCase()) {
                        cssStyle = cssRules[j].style;
                    }
                }
            }
        }
    }
    return cssStyle;
}
