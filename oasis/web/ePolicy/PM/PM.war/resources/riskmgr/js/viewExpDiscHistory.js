//-----------------------------------------------------------------------------
// Javascript file for viewExpDiscHistory.jsp.
//
// (C) 2018 Delphi Technology, inc. (dti)
// Date:   Aug 23, 2018
// Author: ryzhao
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/31/2018       ryzhao      188891 - Initial version.
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// Load claim information of the entity for a specific risk period.
//-----------------------------------------------------------------------------
function loadClaimInfo() {
    var url = getAppPath() + "/riskmgr/viewClaimInfo.do?"
            + "process=loadClaimInfo"
            + "&entityId=" + expDiscHistoryGrid1.recordset("CENTITYID").value
            + "&riskEffDate=" + expDiscHistoryGrid1.recordset("CRISKEFFDATE").value
            + "&riskExpDate=" + expDiscHistoryGrid1.recordset("CRISKEXPDATE").value
            + "&date=" + new Date();
    var divPopupId = openDivPopup("", url, true, true, "", "", 700, 400, "", "", "", false);
}