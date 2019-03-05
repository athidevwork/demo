//////////////////////////////////////////////////////////////////////////////
//The JS object holds necessary information for session timeout handeling
//It is active by default
//////////////////////////////////////////////////////////////////////////////
var objTimeout;

function timeoutObj() {
    // Object properties
    this.timeoutID;
    this.active = true;
}

timeoutObj.prototype.startTimer = function () {
    if (this.active) {
        this.timeoutID = setTimeout(this.timeoutHandler, this.timeoutInMilliSeconds);
    }
}

timeoutObj.prototype.stopTimer = function () {
    clearTimeout(this.timeoutID);
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
        objTimeout.timeoutInMilliSeconds = maxInactiveInterval * 1000 - 60000;
        objTimeout.timeoutHandler = "keepSessionAliveTimeoutHandler()";
    }
    else {
        objTimeout.timeoutInMilliSeconds = maxInactiveInterval * 1010;
        objTimeout.timeoutHandler = "sessionTimeoutHandler()";
    }

    if (objTimeout.active) {
        objTimeout.startTimer();
    }
}

//-----------------------------------------------------------------------------
// Handler to keep session alive
//-----------------------------------------------------------------------------
function keepSessionAliveTimeoutHandler() {
    // Confirm with user if keep session alive if promptForSessionKeepAlive is set to true
    if (promptForSessionKeepAlive.toUpperCase() == "TRUE") {
        if (!confirm(sessionTimeoutMessage)) {
            // If user responds no, execute the sessionTimeoutHanlder
            sessionTimeoutHandler();
            return;
        }
    }
    // Go into here, if
    // 1) promptForSessionKeepAlive is false
    // 2) promptForSessionKeepAlive is true, and user responds yes
    // Invoke Ajax call to renew the session, add timestamp at end of url to prevent caching
    var url = getCorePath() + "/sessionKeepAlive.jsp" + "?date=" + new Date();
    new AJAXRequest("get", url, '', handleOnKeepSessionAlive, false);
}

//-----------------------------------------------------------------------------
// Function to check expected keepAlive response text
//-----------------------------------------------------------------------------
function handleOnKeepSessionAlive(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseText;

            // If the response have not expected keepAlive text.
            // Execute the sessionTimeoutHandler
            if (data.indexOf("Session Updated") == -1) {
                sessionTimeoutHandler();
            }
        }
        else {
            // If ajax.status is not 200, execute the sessionTimeoutHandler
            sessionTimeoutHandler();
        }
    }
}

//-----------------------------------------------------------------------------
// Handler for session time out
//-----------------------------------------------------------------------------
function sessionTimeoutHandler() {
    if (!isEmpty(sessionTimeoutUrl)) {
        setWindowLocation(sessionTimeoutUrl);
    }
    else {
        setWindowLocation(getAppContext());
    }
}

//-----------------------------------------------------------------------------
// Reset sessionTimeoutObject
//-----------------------------------------------------------------------------
function resetSessionTimeoutObject() {
    if (!objTimeout) {
        // The objTimeout object is not created in current page
        // Try to find it in parent page, which has "objTimeout"
        var parentWindow = getParentWindow();

        if (parentWindow != null && parentWindow != window) {
            try {
                if (parentWindow.resetSessionTimeoutObject) {
                    // Recursion call the same method
                    parentWindow.resetSessionTimeoutObject();
                }
            }
            catch(ex) {
                // Do noting, only to avoid javascript error when do Ajax prior to page load.
            }
        }
    }
    else {
        // The objTimeout object is created in the current page
        objTimeout.resetTimer();
    }
}
