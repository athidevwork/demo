function goToSource(externalId, sourceTblName) {
    var actionUrl = '';
    if (sourceTblName == 'OCCURRENCE') {
        actionUrl = getTopNavApplicationUrl("Claims") + "/cmCaseSearch.do?process=globalSearch"
                                                      + "&occurrence_occurrenceNo=" + externalId
                                                      + "&date=" + new Date();
    } else if (sourceTblName == 'CLAIM') {
        actionUrl = getTopNavApplicationUrl("Claims") + "/cmClaimSearch.do?process=globalSearch"
                                                      + "&claimNo=" + externalId
                                                      + "&date=" + new Date();
    } else {
        return;
    }
    var mainwin = window.open(actionUrl, 'CM', 'width=1000,height=650,resizable=yes,scrollbars=yes,status=yes,top=5,left=5');
    mainwin.focus();
}