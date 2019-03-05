//-----------------------------------------------------------------------------
// Javascript file for reRateResult.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   September 27, 2012
// Author: xnie
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/27/2012       xnie       133766 - Initial version.
// 11/16/2012       xnie       138948 - 1) Added massReRateResultGrid_selectRow
//                                      to call loadMassReRateResultDetail.
//                                      2) Added loadMassReRateResultDetail to
//                                      retrieve iframe detail.
//-----------------------------------------------------------------------------

function handleOnButtonClick(asBtn) {


    switch (asBtn) {

        case 'SEARCH':
            var submitFromDate = getObjectValue("submitFromDate");
            var submitToDate = getObjectValue("submitToDate");
            var requestId = getObjectValue("requestId");
            document.forms[0].action = getAppPath() +
                                       "/policymgr/massReRate.do?process=loadAllReRateResult&date=" + new Date() +
                                       "&submitFromDate=" + submitFromDate +
                                       "&submitToDate=" + submitToDate +
                                       "&requestId=" + requestId;
            submitFirstForm();
            break;

        case 'CLOSE':
            closeWindow();
            break;
    }
}

function massReRateResultGrid_selectRow(id) {
    loadMassReRateResultDetail(id);
}

function loadMassReRateResultDetail(id) {
    var url = getAppPath() + "/policymgr/massReRateDetail.do?process=loadAllReRateResultDetail&date=" + new Date() +
            "&batchLogId=" + id;
    getObject("iframeMassReRateResultDetail").src = url;
}