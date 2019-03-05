//-----------------------------------------------------------------------------
// Functions to support Address Add Popup page.
// Author: dpang
// Date:   05/04/2018
// Modifications:
//-----------------------------------------------------------------------------
// 05/04/2018   dpang      issue 192743 -  eCS-eCIS Refactoring: Add Person/ Add Organization
//-----------------------------------------------------------------------------
function afterSave() {
    var entityPk = getObjectValue("pk");
    if (entityPk != "null" && entityPk != "") {

        var entityRootContext = generateURL();
        var url = "";
        if (getObjectValue("entityType") == 'P') {
            url = entityRootContext + "/ciEntityPersonModify.do?process=loadEntityData&entityType=P&pk=" + entityPk;
        } else {
            url = entityRootContext + "/ciEntityOrgModify.do?process=loadEntityData&entityType=O&pk=" + entityPk;
        }

        setWindowLocation(url);
    }
}
