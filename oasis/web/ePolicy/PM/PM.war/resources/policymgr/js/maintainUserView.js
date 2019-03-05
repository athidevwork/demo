function isUserViewExisted(userviewName) {
    var oUserView = getObject("pmUserViewId");
    var isExisted = false;
    var len = oUserView.length;
    for (var i = 0; i < len; i++) {
        if (oUserView.options[i].text == userViewName) {
            isExisted = true;
            break;
        }
    }
    return isExisted;
}
function saveUserView() {
    var oUserView = getObject("pmUserViewId");
    addedUserView = oUserView.options[oUserView.selectedIndex].text;

    if (oUserView.value == -1) {
        addedUserView = "";
    }

    var userViewName = prompt("User view name :", addedUserView);
    if (userViewName == null || trim(userViewName) == "") {
        return;
    }
    addedUserView = trim(userViewName);

     if (addedUserView.length > 30) {
         handleError("pm.maintainUserView.lengthCheck.warning");
     return;
      }

    if (isUserViewExisted(addedUserView)) {
        if (!confirm("'" + addedUserView + "' already exists, overwrite?")) {
            return;
        }
        //update old user view with same name.
        updateind = "Y";
    }
    else {
        //create a new user view
        updateind = "I";
    }
    document.forms[0].action = getAppPath() + "/policymgr/findPolicy.do?"
        + "&process=saveAllUserView" + "&userViewName=" + addedUserView;
    submitFirstForm();

}

function deleteUserView() {
    var oUserView = getObject("pmUserViewId");
    if (oUserView.value == -1) {
        return;
    }
    else {
        document.forms[0].action = getAppPath() + "/policymgr/findPolicy.do?"
            + "&process=deleteAllUserView";
        submitFirstForm();
    }
}