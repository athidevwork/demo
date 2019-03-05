//////////////////////////////////////////////////////////////////////////////
//The JS object holds necessary information for session timeout handeling
//It is active by default
//////////////////////////////////////////////////////////////////////////////
var TIMEOUT_ID = "TIMEOUT_ID";

var TIMEOUT_CLOSE_WARNING_ID = "TIMEOUT_CLOSE_WARNING_ID";

var objTimeout;

function timeoutObj() {
    // Object properties
    this.active = true;
}

timeoutObj.prototype.startTimer = function () {
    if (this.active) {
        $.doTimeout( TIMEOUT_ID, this.timeoutInMilliSeconds,this.timeoutHandler);
    }
}

timeoutObj.prototype.stopTimer = function () {
    // cancel the preceding doTimeout
    $.doTimeout(TIMEOUT_ID);
}

timeoutObj.prototype.resetTimer = function () {
    if (this.active) {
        this.stopTimer();
        this.startTimer();
    }
}

//-----------------------------------------------------------------------------
// Initialize session time out object
//-----------------------------------------------------------------------------
function initializeSessionTimeout() {
    objTimeout = new timeoutObj();

    // if the keepSessionAlive property is true
    if (keepSessionAlive.toUpperCase() == "TRUE") {
        // Set timeoutInMilliSeconds to be 1 minute less than the session timeout
        objTimeout.timeoutInMilliSeconds = maxInactiveInterval * 1000 - 600000;
        objTimeout.timeoutHandler = window.keepSessionAliveTimeoutHandler;
    } else {
        objTimeout.timeoutInMilliSeconds = maxInactiveInterval * 1010;
        objTimeout.timeoutHandler = window.sessionTimeoutHandler;
    }

    if (objTimeout.active) {
        objTimeout.startTimer();
    }
}

function setAutoClose() {
    $.doTimeout(TIMEOUT_CLOSE_WARNING_ID, 660000, window.closeWarningDialog);
}

function cancelAutoClose(){
    $.doTimeout(TIMEOUT_CLOSE_WARNING_ID);
}

function closeWarningDialog() {
    PF('idleDialog').hide();
    PF('pageExpiredDialog').show();
}

//-----------------------------------------------------------------------------
// Handler to keep session alive
//-----------------------------------------------------------------------------
function keepSessionAliveTimeoutHandler() {
    // Confirm with user if keep session alive if promptForSessionKeepAlive is set to true
    if (promptForSessionKeepAlive.toUpperCase() == "TRUE") {
        PF('idleDialog').show();
    }
}

//-----------------------------------------------------------------------------
// Handler for session time out
//-----------------------------------------------------------------------------
function sessionTimeoutHandler() {
    PF('pageExpiredDialog').show();
}

//-----------------------------------------------------------------------------
// Reset sessionTimeoutObject
//-----------------------------------------------------------------------------
function resetSessionTimeoutObject() {
    if (!objTimeout) {
        // The objTimeout object is not created in current page
        // Try to find it in parent page, which has "objTimeout"
        if (window.parent) {
            // It's div popup window
            if (window.parent.resetSessionTimeoutObject) {
                // Recursion call the same method
                window.parent.resetSessionTimeoutObject();
            }
        }
    } else {
        // The objTimeout object is created in the current page
        objTimeout.resetTimer();
    }
}
