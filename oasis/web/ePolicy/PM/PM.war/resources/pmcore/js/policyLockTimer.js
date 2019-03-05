//-----------------------------------------------------------------------------
// Common javascript file.
//
// (C) 2007 Delphi Technology, inc. (dti)
// Date:   Oct 10, 2007
// Author: lmm
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 12/31/2014       wdang       158738 - 1) Modified policyLockTimeoutHandler() to remove term id from parameters 
//                                          in case of purge.
//                                       2) Modified handleOnRefreshPolicyLock() to call refreshWithNewPolicyTermHistory
//                                          if the term is unspecified in policyLockTimeoutHandler().
// 09/13/2017       kshen       Grid replacement. Changed for handleOnRefreshPolicyLock for HTML5 support.
// 12/05/2018       xjli        195889 - Reflect grid replacement project changes.
//-----------------------------------------------------------------------------
var objPolicyLockTimer;

function policyLockTimerObj() {
    // Object properties
    this.timeoutID;
    this.active = true;
}

policyLockTimerObj.prototype.startTimer = function () {
    if (this.active) {
        this.timeoutID = setTimeout(this.timeoutHandler, this.timeoutInMilliSeconds);
    }
}

policyLockTimerObj.prototype.stopTimer = function () {
    clearTimeout(this.timeoutID);
}

policyLockTimerObj.prototype.resetTimer = function () {
    if (this.active) {
        this.stopTimer();
        this.startTimer();
    }
}

//-----------------------------------------------------------------------------
// Check the object tree to see if the objPolicyLockTimer has been initialized.
// If resetTimer is true, reset found Policy Lock Timer.
//-----------------------------------------------------------------------------
function isPolicyLockTimerInitialized(resetTimer) {
    var isInitialized = false;
    if (objPolicyLockTimer) {
        // objPolicyLockTimer has been initialized
        isInitialized = true;
        // Reset Policy Lock Timer
        if (resetTimer) {
            objPolicyLockTimer.resetTimer();
        }
    }
    else {
        // Try to find objPolicyLockTimer in parent page
        var curDivParentWindow;
        try {
            curDivParentWindow =  getParentWindow();
        }
        catch (ex) {
            curDivParentWindow = null;
        }
        var curPopupParentWindow = window.opener;

        if (curDivParentWindow) {
            // It's div popup window
            if (curDivParentWindow.isPolicyLockTimerInitialized) {
                // Recursion call the same method
                isInitialized = curDivParentWindow.isPolicyLockTimerInitialized(resetTimer);
            }
        }
        else if (curPopupParentWindow) {
            // It's normal popup window
            try {
                if (curPopupParentWindow.isPolicyLockTimerInitialized) {
                    // Recursion call the same method
                    isInitialized = curPopupParentWindow.isPolicyLockTimerInitialized(resetTimer);
                }
            }
            catch(ex) {
                // Do noting, only to avoid javascript error when do Ajax prior to page load.
            }
        }
    }
    return isInitialized;
}

//-----------------------------------------------------------------------------
// Initialize Policy Lock Timer
//-----------------------------------------------------------------------------
function initializePolicyLockTimer() {
    // Create timer object
    objPolicyLockTimer = new policyLockTimerObj();
    objPolicyLockTimer.timeoutInMilliSeconds = policyLockDuration * 60 * 1000;
    objPolicyLockTimer.timeoutHandler = "policyLockTimeoutHandler()";

    // Start the timer
    if (objPolicyLockTimer.active) {
        objPolicyLockTimer.startTimer();
    }
}

//-----------------------------------------------------------------------------
// Reset Policy Lock Timer
//-----------------------------------------------------------------------------
function resetPolicyLockTimer() {
    isPolicyLockTimerInitialized(true);
}

//-----------------------------------------------------------------------------
// Policy Lock Timer Handler
//-----------------------------------------------------------------------------
function policyLockTimeoutHandler() {
    var policyNo = getObjectValue("policyNo");
    var policyTermHistoryId = getObjectValue("policyTermHistoryId");
    var policyViewMode = getObjectValue("policyViewMode");

    // Invoke Ajax call to refresh the Policy Lock
    var url = getAppPath() + "/policymgr/lockmgr/maintainLock.do" + "?process=refreshPolicyLock&policyNo=" +
              policyNo + "&policyViewMode=" + policyViewMode + "&date=" + new Date();
    // If the current WIP transaction code is "PURGE", do not pass term id.
    if (policyHeader.lastTransactionInfo.transactionCode != "PURGE") {
        url += url + "&policyTermHistoryId=" + policyTermHistoryId;
    }   
    
    new AJAXRequest("get", url, '', handleOnRefreshPolicyLock, false);

}

//-----------------------------------------------------------------------------
// Handle on Refresh Policy Lock
//-----------------------------------------------------------------------------
function handleOnRefreshPolicyLock(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            var isRefreshSucceed = false;
            var isTermSpecifed = false;
            var root = data.documentElement;
            if (root) {
                // Check if policy lock refresh sucessfully
                var message = root.getElementsByTagName("REFRESHPOLICYLOCK");
                if (message && message.length > 0) {
                    var result = $(message[0]).text();
                    if (result == "Y") {
                        isRefreshSucceed = true;
                    }
                }
                if ($('TERMSPECIFIED', root).first().text() == "Y" ) {
                    isTermSpecifed = true;
                }
            }

            if (isRefreshSucceed) {
                // Reset Policy Lock Timer
                resetPolicyLockTimer();
            }
            else {
                // Refresh current page
                if (isTermSpecifed) {
                    refreshPage();    
                } 
                else {
                    refreshWithNewPolicyTermHistory();
                }
            }
        }
        else {
            refreshPage();
        }
    }
}

function handleOnLoadPolicyHeader() {
    // Only initialize/reset Policy Lock Timer when policy is in WIP View Mode and owns policy lock
    if (getObjectValue("policyViewMode") == "WIP" && policyHeader.ownLock) {
        if (isPolicyLockTimerInitialized()) {
            // If Policy Lock Timer has been initialized,
            // reset current Policy Lock Timer
            resetPolicyLockTimer();
        }
        else {
            // If no policy Lock Timer has been initialized,
            // initialize a new Policy Lock Timer
            initializePolicyLockTimer();
        }
    }

}
