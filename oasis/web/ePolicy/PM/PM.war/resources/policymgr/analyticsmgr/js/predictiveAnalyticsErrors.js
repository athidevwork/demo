function handleOnLoad() {
}

function handleOnButtonClick(action) {
    switch (action) {
        case 'SEARCH':
            setObjectValue("process", "searchScoringError");
            submitFirstForm();
            break;
        case 'CLEAR':
            setObjectValue("process", "clear");
            submitFirstForm();
            break;
        case 'CLOASE':
            closeWindow();
            break;
    }
}


function scoringErrorGrid_selectRow(id) {
    loadScoringErrorDetails(id);
}

//-----------------------------------------------------------------------------
// Load iframe
//-----------------------------------------------------------------------------
function loadScoringErrorDetails(opaScoreReqId) {
    if (isEmpty(opaScoreReqId)) {
        opaScoreReqId = "-9999";
    }
    var url = getAppPath() + "/policymgr/analyticsmgr/opaErrors.do?process=loadAllScoringErrorDetail&scoreReqId=" + opaScoreReqId;
    getObject("iframeOpaScoreErrorLog").src = url;
}