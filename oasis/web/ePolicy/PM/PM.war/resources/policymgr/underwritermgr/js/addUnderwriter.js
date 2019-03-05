//-----------------------------------------------------------------------------
// Javascript file for addUnderwriter.jsp.
//
// (C) 2013 Delphi Technology, inc. (dti)
// Date:   05/24/2013
// Author: awu
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 12/08/14         jyang      158577 - Modified handleOnChange to disable/enable renewal field and change its value when
//                                      the 'End Date' field was changed.
// 03/10/17         wli        180675 - Changed "window.frameElement.document.parentWindow" to "getReturnCtxOfDivPopUp()"
//                                      in the function named handleOnButtonClick for UI change.
// 10/16/18         xgong      195889 - Updated handleOnButtonClick and handleOnChange for grid replacement
//-----------------------------------------------------------------------------

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'ADD_UDR_DONE':
            var addTeamB = getObjectValue("addTeamB");
            var entityId = getObjectValue('entityId');
            var effectiveFromDate = getObjectValue('effectiveFromDate');
            var effectiveToDate = getObjectValue('effectiveToDate');
            var renewalB = getObjectValue('renewalB');
            var teamCode = getObjectValue('regionalTeamCode');
            if (teamCode.length == 0) {
                addTeamB = "N";
            }
            var underwriterArray = new Array();
            underwriterArray[0] = entityId;
            underwriterArray[1] = addTeamB;
            underwriterArray[2] = effectiveFromDate;
            underwriterArray[3] = effectiveToDate;
            underwriterArray[4] = renewalB;
            underwriterArray[5] = teamCode;
            closeWindow(function () {
                getReturnCtxOfDivPopUp().addUnderwriterTeam(underwriterArray);
            });
            break;
    }
}

function handleOnChange(obj) {
    if (obj.name == 'addTeamB') {
        if (obj.value == 'Y') {
            var teamCode = getObjectValue('regionalTeamCode');
            if (teamCode.length == 0) {
                alert(getMessage("pm.maintainUnderwriter.addUnderwriter.addTeamB"));
                obj.value = 'N';
            }
        }
    }// Issue 158577, handle Renew indicator.
    else if (obj.name == "effectiveToDate") {
        var effectiveToDate = getObjectValue("effectiveToDate");
        var termExpirationDate = getParentWindow().policyHeader.termEffectiveToDate;
        enableDisableRenewIndicatorWithoutGrid(effectiveToDate, termExpirationDate, "renewalB");
    }
}