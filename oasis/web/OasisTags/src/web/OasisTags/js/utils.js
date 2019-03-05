function toggle(divId) {
    //alert(divId);
    var obj = document.getElementById(divId);
    //alert(obj.style.display);
    obj.style.display = (obj.style.display == 'none' ? 'block' : 'none');
    //alert(obj.style.display);
    var objMin = document.getElementById(divId + '-toggle');
    //alert(objMin.style.display);
    objMin.style.display = (obj.style.display == 'none' ? 'block' : 'none');
    //alert(objMin.style.display);
}

var isGridDirty  = function ($scope, gridName) {
    var gridRowInScope = getGridRowInScope($scope, gridName);
    return gridRowInScope.isDirty;
};

var getGridRowNameForScopeCaching = function (gridName) {
    var rowName = gridName + "Row";
    return rowName;
};

var getGridRowInScope = function ($scope, gridName) {
    console.log("getGridRowInScope:");
    var rowName = getGridRowNameForScopeCaching(gridName);
    if ($scope[rowName] === undefined) {
        console.warn(rowName + " does not exists in scope...");
        $scope[rowName] = {};
        $scope[rowName].isDirty = false;
        $scope[rowName].rowIndex = -1;
    }
    console.log($scope[rowName]);
    return $scope[rowName];
};

var InitializeGridRowInScope = function ($scope, gridName, rowIndex, primaryColumnName) {
    console.log("InitializeGridRowInScope:");

    var gridRowInScope = getGridRowInScope($scope, gridName);
    var currentlySelectedRow = $("#" + gridName).jqxGrid('getrowdata',rowIndex);

    console.log(primaryColumnName);
    console.log(gridRowInScope[primaryColumnName]);
    console.log(currentlySelectedRow[primaryColumnName]);
    console.log(gridRowInScope[primaryColumnName] === currentlySelectedRow[primaryColumnName]);

    if (!(gridRowInScope[primaryColumnName] === currentlySelectedRow[primaryColumnName])) {
        /*
         console.log("isDirty?")
         console.log(gridRowInScope);
         console.log(gridRowInScope.isDirty);
         */
        if (gridRowInScope.isDirty) {
            //save the prior row
            console.log("Save Row = ");
            console.log(gridRowInScope);
            $scope["update" + gridName.substring(0,1).toUpperCase() + gridName.substr(1)](gridRowInScope);
        }

        //init the current row as edit row.
        gridRowInScope = currentlySelectedRow;
        gridRowInScope.rowIndex = rowIndex;
        gridRowInScope.isDirty = false;
        gridRowInScope.gridId = gridName;

        console.log("New row = ");
        console.log(gridRowInScope);
        $scope[getGridRowNameForScopeCaching(gridName)]  = gridRowInScope;
        $scope.$apply();
    }
};

var updateGridRowInScope = function ($scope, gridName, rowIndex, columnName, oldValue, newValue) {
    console.log("updateGridRowInScope:");
    var gridRowInScope = getGridRowInScope($scope, gridName);

    console.log(gridRowInScope.rowIndex);
    console.log(rowIndex);
    console.log(gridRowInScope.rowIndex===rowIndex);
    if (gridRowInScope.rowIndex === rowIndex || gridRowInScope.rowIndex == -1) {
        gridRowInScope.rowIndex = rowIndex;
        console.log("oldValue:");
        console.log(oldValue);
        console.log("newValue:");
        console.log(newValue);
        console.log(oldValue===newValue);
        if (!(oldValue === newValue)) {
//            gridRowInScope[columnName + "_Updated"] = newValue;
            gridRowInScope[columnName] = newValue;
            gridRowInScope.isDirty = true;

            var selectedRowId = $('#' + gridName).jqxGrid('getrowid', gridRowInScope.rowIndex);
            $("#" + gridName).jqxGrid('updaterow', selectedRowId, gridRowInScope);
        }
        /*
         if (gridRowInScope.isDirty ){
         console.log("Dirty row = ");
         console.log(gridRowInScope);
         }
         */
    }
};

var getSelectOption = function (code, description) {
    var selectRec = {};
    selectRec[code] = -1;
    selectRec[description] = "-Select-";
    return selectRec;
};

var getFormattedToday = function () {
    var date = new Date();
    getFormattedDay(date);
};

var getFormattedDay = function (date){
    var date =date;
    var year = date.getFullYear();
    var month = (1 + date.getMonth()).toString();
    month = month.length > 1 ? month : '0' + month;
    var day = date.getDate().toString();
    day = day.length > 1 ? day : '0' + day;
    return month + '/' + day + '/' + year;
}

// JSON (start)
// if not supported then create the function
var JSON = JSON || {};

// implement JSON.stringify serialization
JSON.stringify = JSON.stringify || function (obj) {

    var t = typeof (obj);
    if (t != "object" || obj === null) {

        // simple data type
        if (t == "string") obj = '"'+obj+'"';
        return String(obj);

    }
    else {

        // recurse array or object
        var n, v, json = [], arr = (obj && obj.constructor == Array);

        for (n in obj) {
            v = obj[n]; t = typeof(v);

            if (t == "string") v = '"'+v+'"';
            else if (t == "object" && v !== null) v = JSON.stringify(v);

            json.push((arr ? "" : '"' + n + '":') + String(v));
        }

        return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
    }
};

// implement JSON.parse de-serialization
JSON.parse = JSON.parse || function (str) {
    if (str === "") str = '""';
    eval("var p=" + str + ";");
    return p;
};
// JSON (end)

// categorized utility functions
var utils = (function() {
    return {
        routing : {
            getCurrentRoute: function($state) {
                return ($state) ? $state.current : {};
            },
            getName: function($state) {
                return $state.current.name;
            },
            getPath: function($state) {
                return $state.$current.url.sourcePath;
            }
        }
    }

})();

if (typeof dti == "undefined") {
    dti = {};
}

if (typeof dti.application == "undefined") {
    dti.application = {};
}

// manage jqx timers that are still running sometimes
if (typeof window.oldSetInterval == "undefined") {
    dti.intervalsUsed = [];
    window.oldSetInterval = window.setInterval;
    window.setInterval = function(fn, interval) {
        var interval = window.oldSetInterval(fn, interval);
        var fnBody = fn.toString();
        if (fnBody.indexOf("jqx") > -1 ||
            fnBody.indexOf("_updatesize") > -1) {
            dti.intervalsUsed.push(interval);
        }

        return interval;
    };

// clear jqx timers that are still running sometimes
    dti.clearIntervals = function() {
        var intervalsUsed = dti.intervalsUsed;
        for (var i=0; i<intervalsUsed.length; i++) {
            if (intervalsUsed[i]) {
                window.clearInterval(intervalsUsed[i]);
            }
        }

        dti.intervalsUsed = [];
    }
}


dti.utils = utils;  //backward compatibility;

if (typeof dti.routing == "undefined") {
    dti.routing = (function() {
        return {
            _protected: {
                _popStateListeners: []
            },
            getCurrentRoute: function ($state) {
                return $state.current;
            },
            getName: function ($state) {
                return $state.current.name;
            },
            getPath: function ($state) {
                return $state.$current.url.sourcePath;
            },
            go: function($state, to, toParams, options) {
                $state.go(to, toParams, options);
            },
            transitionTo: function($state, to, toParams, options) {
                $state.transitionTo(to, toParams, options);
            },
            getHistorySequence: function() {
                return (sessionStorage["dti.history.sequence"]) ? +(sessionStorage["dti.history.sequence"]) : 0;
            },
            nextvalHistorySequence: function(value) {
                var currentSequence = this.getHistorySequence();
                sessionStorage["dti.history.sequence"] = ++currentSequence;

                return currentSequence;
            },
            addToHistory: function(stateObj, title, url) {
                if (stateObj) {
                    stateObj.recordedBy = "dti.routing";
                    stateObj.sequence = this.nextvalHistorySequence();
                }

                window.history.pushState(stateObj, title, url);
            },
            addHistoryListener: function(listener) {
                dti.routing._protected._popStateListeners.push(listener);
            },
            onPopState: function(event) {
                var st = event.state;
                if (st) {
                    console.log("navigating to: ".concat(st));

                    if (st.recordedBy == "dti.routing") {
                        var listeners = dti.routing._protected._popStateListeners;
                        for (var i = 0; i < listeners.length; i++) {
                            var listener = listeners[i];
                            if (listener.onChange) {
                                listener.onChange(event, st);
                            }
                        }
                    }
                }


                if (window.oldPopState) {
                    window.oldPopState(event);
                }
            }
        }
    })();

    window.oldPopState = window.onpopstate;
    window.onpopstate = dti.routing.onPopState;
}


if (typeof dti.http == "undefined") {
    dti.http = (function () {
        return {
            transformRequest: function(data) {
                var formStr = "";
                for (var p in data) {
                    if (data.hasOwnProperty(p) && data[p] != null) {
                        if ($.isArray(data[p])) {
                            for (var i = 0; i < data[p].length; i++) {
                                formStr += "&" + encodeURIComponent(p) + "=" + encodeURIComponent(data[p][i]);
                            }
                        } else {
                            formStr += "&" + encodeURIComponent(p) + "=" + encodeURIComponent(data[p]);
                        }
                    }
                }

                return formStr;
            },

            configureRequest: function (requestConfig) {
                var fullConfig = {
                    method: requestConfig.method,
                    dataType: requestConfig.dataType || "json",
                    contentType: requestConfig.contentType || "application/json; charset=utf-8",
                    url: requestConfig.url,
                    header: {
                        'Access-Control-Allow-Origin': '*',
                        'Access-Control-Allow-Headers': 'Content-Type,X-Requested-With',
                        'Access-Control-Allow-Methods': 'GET,POST,PUT,HEAD,DELETE,OPTIONS'
                    }
                };

                if (requestConfig.data) {
                    fullConfig.data = JSON.stringify(requestConfig.data);
                }

                if (requestConfig.params) {
                    fullConfig.url = dti.http.addQueryParams(fullConfig.url, requestConfig.params);
                }

                if (requestConfig.transformRequest) {
                    fullConfig.transformRequest = requestConfig.transformRequest;
                }

                return fullConfig;
            },
            addQueryParams: function(url,params) {
                if (!url) {
                    console.error("dti.http.addQueryParams: url parameter is empty");
                    return "";
                }

                if (params) {
                    var url = url.concat("?");
                    var i = 0;
                    for (name in params) {
                        if (i++) {      // add param to second or more parameters
                            url = url.concat("&");
                        }
                        var val = params[name] || "";
                        url = url.concat(name, "=", encodeURIComponent(val));
                    }
                }

                return url;
            }
        }
    })();
}


if (typeof dti.string == "undefined") {
    dti.string = (function () {
        return {
            isEmpty: function (value) {
                if (typeof value === 'undefined' || value == null) return true;
                return !/\S/.test(value);
            },
            tokenReplace: function() {
                var args = Array.prototype.slice.call(arguments);
                var template = args[0];

                if (args.length > 1) {
                    for (var i = 0, j = 1; j < args.length; i++, j++) {
                        var pattern = new RegExp("\{[" + i + "]\}", "g");   // ex. {0}, {1}, etc.
                        template = template.replace(pattern,args[j]);
                    }
                }

                return template;
            },
            capitalizeFirstLetter: function (value) {
                var allLower = value.toLowerCase();
                var replaced = allLower.replace(/^./, function (match) {    // replace first letter
                    return match.toUpperCase();
                });

                return replaced;
            }
        }
    })();
}

if (typeof dti.popup == "undefined") {
    dti.popup = (function() {
        return {
            create: function() {
                return (function ( $ ) {

                    var shade = "#556b2f";

                    $.fn.tmWindow = function(info) {
                        if (typeof info === "string") {
                            var cmd = info.toLowerCase();
                            switch (cmd) {
                                case "open":
                                case "show":
                                    this.css( "display", "block" );
                                    this.removeClass("dti-hide");
                                    break;
                                case "hide":
                                case "close":
                                    this.addClass("dti-hide");
                                    break;
                            }
                        }
                        else {
                            var options = $.extend({
                                height : "250",
                                width : "500",
                                title:"Title",
                                description: "data",
                                top: "20%",
                                left: "30%",
                                zIndex: 8000,
                                border: ""
                            },info);

                            this.css( "top", options.top);
                            this.css( "left", options.left);
                            this.css( "height", options.height);
                            this.css( "width", options.width);

                            this.addClass("jqx-widget");
                            this.addClass("jqx-widget-content");
                            this.addClass("jqx-rc-all");


                        }


                        return this;
                    };

                    $.fn.dtiWindow = $.fn.tmWindow;

                }( jQuery ));
            }
        }
    })();
}

if (typeof dti.message == "undefined") {
    dti.message = (function() {
        var cachedMessages = {};

        var category = {
            "ERROR_MESSAGE": "error",
            "SYSTEM_ERROR_MESSAGE": "systemError",
            "WARNING_MESSAGE": "warning",
            "INFORMATION_MESSAGE": "info",
            "CONFIRMATION_PROMPT": "confirm",
            "SUCCESSFUL_MESSAGE": "success",
            "JS_MESSAGE": "js"
        };

        /**
         * Display JS message
         * @param messageObj A message object. The following are the applicable attributes:
         * htmlMessage               : the html message being displayed on jqxNotification widget
         * onClickEvent              : the onClickEvent event on the message
         * category                  : REQUIRED, a category where the message applied in
         * autoClose                 : the autoClose attribute of jqxNotification widget
         * autoCloseDelay            : the autoCloseDelay attribute of jqxNotification widget
         */
        var _displayMessage = function (messageObj) {
            if(messageObj.htmlMessage != "") {
                var msgCategory = category[messageObj.category];

                var autoClose;
                if (!messageObj.hasOwnProperty("autoClose") || messageObj.autoClose == undefined) {
                    autoClose = (msgCategory == "info" || msgCategory == "success");
                }
                else {
                    autoClose = messageObj.autoClose;
                }

                var autoCloseDelay;
                if (!messageObj.hasOwnProperty("autoCloseDelay") || messageObj.autoCloseDelay == undefined) {
                    autoCloseDelay = 5000;
                }
                else {
                    autoCloseDelay = messageObj.autoCloseDelay;
                }

                var config = $.extend({}, dti.configData.global.notification[msgCategory]);

                var $globalNotification = $("#globalNotification");

                $globalNotification.jqxNotification("template", config.template);
                $globalNotification.jqxNotification(config);

                $globalNotification.off("close.dti.utils.message.global").on("close.dti.utils.message.global", function() {
                    $(this).off("open.dti.utils.message.global");
                    $(this).off("close.dti.utils.message.global");
                });
                $globalNotification.off("open.dti.utils.message.global").on("open.dti.utils.message.global", function() {
                    var parentId = "jqxNotificationDefaultContainer".concat("-", config.position);
                    var $parent = $("#".concat(parentId));
                    $parent.css("z-index", 10001); // place on top of jqxWindow
                    $(window).trigger("message.global.dti.utils", messageObj);  // forward the notification
                });

                $("#globalNotificationContent").html(generateHTMLMessage(messageObj, false));

                $globalNotification.jqxNotification('open');
            }
        };

        var _displayStatusMessage = function (messageObj) {
            if(messageObj.htmlMessage != "") {
                var $consoleWindow = $("#consoleWindow");
                $consoleWindow.removeClass("dti-hide");

                var cssClassName = "dti-information-message";
                if (messageObj.category) {
                    cssClassName = "dti-" + messageObj.category.toLowerCase().replace(/_/g,"-");
                }

                var windowConfig = $.extend({}, dti.configData.global.windows.consoleWindow);

                var msg = generateHTMLMessage(messageObj, false);
                var msgWrapper = "<span class='".concat(cssClassName.concat(" jqx-widget-content"), "'>", msg, "</span>");

                var divClasses = "";
                if (windowConfig.icons && windowConfig.icons[cssClassName]) {
                    divClasses = "class='".concat(windowConfig.icons[cssClassName].classes," dti-message-entry'");
                }
                msgWrapper = "<div ".concat(divClasses, ">", msgWrapper, "</div>");

                var $consoleWindowContents = $("#consoleWindowContents");

                switch(messageObj.cmd) {
                    case "set":
                        $consoleWindowContents.html(msgWrapper);
                        break;
                    case "add":
                    default:
                        $consoleWindowContents.append(msgWrapper);
                        break;
                }

                if (typeof messageObj.isModal != 'undefined') {
                    windowConfig.settings.isModal = messageObj.isModal;
                }

                $consoleWindow.jqxWindow(windowConfig.settings);
                $consoleWindow.off("close.dti.utils.message.status").on("close.dti.utils.message.status", function() {
                    var $consoleWindowContents = $("#consoleWindowContents");
                    $consoleWindowContents.text(""); // clean up
                });

                $consoleWindow.jqxWindow('open');

                setTimeout(function() { // added content
                    $(window).trigger("message.global.dti.utils", messageObj);
                }, 1000);

                // resize
                var newHeight = $('#consoleWindowEndOfContent').position().top + 50;
                $consoleWindow.jqxWindow({height: newHeight});

                var msgCategory = category[messageObj.category];

                var autoClose;
                if (!messageObj.hasOwnProperty("autoClose") || messageObj.autoClose == undefined) {
                    autoClose = (msgCategory == "info" || msgCategory == "success");
                }
                else {
                    autoClose = messageObj.autoClose;
                }

                var autoCloseDelay;
                if (!messageObj.hasOwnProperty("autoCloseDelay") || messageObj.autoCloseDelay == undefined) {
                    //autoCloseDelay = 5000;  // Note: Don't default to auto close at this time.
                }
                else {
                    autoCloseDelay = messageObj.autoCloseDelay;
                }

                if (autoClose && autoCloseDelay) {
                    setTimeout(function() {
                        dti.message.status.closeStatusMessage();
                    }, autoCloseDelay);
                }

            }
        };

        var _displayProgressMessage = function (messageObj) {
            if(messageObj.htmlMessage != "") {
                var $statusWindow = $("#statusWindow");
                $statusWindow.removeClass("dti-hide");

                var cssClassName = "dti-information-message";
                if (messageObj.category) {
                    cssClassName = "dti-" + messageObj.category.toLowerCase().replace(/_/g,"-");
                }

                var windowConfig = $.extend({}, dti.configData.global.windows.statusWindow);

                if (!$statusWindow.jqxWindow("isOpen")) {
                    if (typeof messageObj.isModal != 'undefined') {
                        windowConfig.settings.isModal = messageObj.isModal;
                    }

                    $statusWindow.jqxWindow(windowConfig.settings);
                    $statusWindow.off("close.dti.utils.message.status").on("close.dti.utils.message.status", function() {
                        var $statusWindowContents = $("#statusWindowContents");
                        $statusWindowContents.text(""); // clean up
                        dti.message.status.stopStatusProgressIndicator();
                    });

                    $statusWindow.jqxWindow('open');
                }
            }
        };

        var generateHTMLMessageList = function(messages){
            var htmlMessage = "";
            if(typeof(messages) == "object") {
                var isShowBulletPoint = (messages.length > 1);

                if (isShowBulletPoint) {
                    htmlMessage += "<ui>";
                    for (var i = 0; i < messages.length; i++) {
                        htmlMessage += generateHTMLMessage(messages[i], true);
                    }
                    htmlMessage += "</ui>";
                } else {
                    htmlMessage = generateHTMLMessage(messages[0], false);
                }
            } else if(typeof(messages) == "string") {
                htmlMessage = generateHTMLMessage(messages, false);
            }

            return htmlMessage;
        };

        var generateHTMLMessage = function(message, showBulletPoint){
            var tempHTMLMessage = "";
            if(showBulletPoint){
                tempHTMLMessage = "<li>";
            }

            var htmlMessage = message.htmlMessage;
            if (message.replace) {
                htmlMessage = htmlMessage.replace(message.replace.from, message.replace.to);
            }

            if (typeof(message) == "string") {
                tempHTMLMessage += message;
            } else if (typeof(message) == "object") {
                if(message.onClickEvent == undefined){
                    tempHTMLMessage += htmlMessage;
                } else {
                    tempHTMLMessage += "<a onclick='" + message.onClickEvent + "'>" + htmlMessage + "</a>";
                }
            }

            if(showBulletPoint){
                tempHTMLMessage += "</li>";
            }

            return tempHTMLMessage;
        };

        /**
         * Get message by messageKey
         * @param messageKey
         * @param messageParameters
         * @param additionalParms
         */
        var _getAndDisplayMessageByKey = function (messageKey, messageParameters, additionalParms) {
            if (messageKey != "") {
                _getMessage(messageKey, messageParameters, additionalParms).then(function(messageObj){
                    if (additionalParms) {
                        messageObj.onClickEvent = additionalParms.onClickEvent || messageObj.onClickEvent;
                        messageObj.category = additionalParms.category || messageObj.category;
                        messageObj.autoClose = (typeof additionalParms.autoClose != "undefined") ? additionalParms.autoClose : messageObj.autoClose;
                        messageObj.autoCloseDelay = additionalParms.autoCloseDelay || messageObj.autoCloseDelay;
                        messageObj.errorInfo = additionalParms.errorInfo || messageObj.errorInfo;
                        messageObj.replace = additionalParms.replace || messageObj.replace;
                    }
                    _displayMessage(messageObj);
                });
            }
        };

        var _getMessage = function (messageKey, messageParameters) {
            var defer = $.Deferred();
            if (sessionStorage[messageKey]) {
                var cachedMessage = JSON.parse(sessionStorage[messageKey]);
                console.log("Get message from cache directly..." + cachedMessage);

                var msg = $.extend({}, cachedMessage);
                if (messageParameters && messageParameters.length) {        // replace tokens
                    msg.htmlMessage = dti.string.tokenReplace(msg.htmlMessage, messageParameters);
                }

                defer.resolve(msg);
            } else {
                console.log("Getting message through service...");
                var getMessageURI = getContextPath() + "/rest/Messages";
                $.ajax(
                    dti.http.configureRequest({
                        method: "POST",
                        url: getMessageURI,
                        data: {
                            key: messageKey,
                            parameters: messageParameters == null ? [] : messageParameters
                        }
                    })
                )
                    .done(function (data) {
                        var messageObj = {};
                        messageObj.key = messageKey;
                        messageObj.messageParameters = messageParameters;
                        messageObj.htmlMessage = data.htmlMessage;

                        if (messageParameters == null) {
                            sessionStorage[messageKey] = JSON.stringify(messageObj);
                        }
                        defer.resolve(messageObj);
                    })
                    .fail(function (res) {
                        console.log("Failed to get message from server. key:" + messageKey);
                        defer.reject("Failed to get message from server. key:" + messageKey, res);
                    });
            }
            return defer.promise();
        };


        var _getAllMessagesFromServer = function () {
            var defer = $.Deferred();
            var getMessageURI = getContextPath() + "/rest/Property/Messages";
            $.ajax(
                dti.http.configureRequest({
                    method: "GET",
                    url: getMessageURI
                })
            )
                .done(function (messages) {
                    for (var key in messages) {
                        sessionStorage[key] = JSON.stringify({key: key, htmlMessage: messages[key]});
                    }

                    defer.resolve(messages);
                })
                .fail(function (res) {
                    console.log("Failed to get messages from server");
                    defer.reject("Failed to get messages from server");
                });

            return defer.promise();
        };

        return {
            displayInfoMessage: function (messageKey, messageParameters, additionalParms) {
                if (additionalParms == undefined) {
                    additionalParms = {};
                }
                additionalParms.category = additionalParms.category || "INFORMATION_MESSAGE";
                _getAndDisplayMessageByKey(messageKey, messageParameters, additionalParms);
            },
            displaySuccessfulMessage:  function (messageKey, messageParameters, additionalParms) {
                if (additionalParms == undefined) {
                    additionalParms = {};
                }
                additionalParms.category = additionalParms.category || "SUCCESSFUL_MESSAGE";
                _getAndDisplayMessageByKey(messageKey, messageParameters, additionalParms);
            },
            displayWarningMessage:  function (messageKey, messageParameters, additionalParms) {
                if (additionalParms == undefined) {
                    additionalParms = {};
                }
                additionalParms.category = additionalParms.category || "WARNING_MESSAGE";
                _getAndDisplayMessageByKey(messageKey, messageParameters, additionalParms);
            },
            displayErrorMessage:  function (messageKey, messageParameters, additionalParms) {
                if (additionalParms == undefined) {
                    additionalParms = {};
                }
                additionalParms.category = additionalParms.category || "ERROR_MESSAGE";
                _getAndDisplayMessageByKey(messageKey, messageParameters, additionalParms);
            },
            getMessage: function (messageKey, messageParameters) {
                var messagePromise = _getMessage(messageKey, messageParameters);
                return messagePromise;
            },
            getAllMessagesFromServer: function () {
                _getAllMessagesFromServer();
            },
            displayMessages:  function (messageObjects) {

                var warningMessageQueue = [];
                var errorMessageQueue = [];
                var infoMessageQueue = [];
                var successMessageQueue = [];

                for (var i = 0; i < messageObjects.length; i++) {
                    var currentMessageObject = messageObjects[i];
                    var categoryName = currentMessageObject.messageCategory.category;
                    if (categoryName == "WARNING_MESSAGE") {
                        warningMessageQueue.push(currentMessageObject);
                    } else if (categoryName == "ERROR_MESSAGE") {
                        errorMessageQueue.push(currentMessageObject);
                    } else if (categoryName == "INFORMATION_MESSAGE") {
                        infoMessageQueue.push(currentMessageObject);
                    } else if (categoryName == "SUCCESSFUL_MESSAGE") {
                        successMessageQueue.push(currentMessageObject);
                    }
                }

                var messageObj = {};

                if (warningMessageQueue.length > 0) {
                    messageObj.category = warningMessageQueue[0].messageCategory.category;
                    messageObj.htmlMessage = generateHTMLMessageList(warningMessageQueue);
                    _displayMessage(messageObj);
                }

                if (errorMessageQueue.length > 0) {
                    messageObj.category = errorMessageQueue[0].messageCategory.category;
                    messageObj.htmlMessage = generateHTMLMessageList(errorMessageQueue);
                    _displayMessage(messageObj);
                }

                if (infoMessageQueue.length > 0) {
                    messageObj.category = infoMessageQueue[0].messageCategory.category;
                    messageObj.htmlMessage = generateHTMLMessageList(infoMessageQueue);
                    _displayMessage(messageObj);
                }

                if (successMessageQueue.length > 0) {
                    messageObj.category = successMessageQueue[0].messageCategory.category;
                    messageObj.htmlMessage = generateHTMLMessageList(successMessageQueue);
                    _displayMessage(messageObj);
                }
            },
            closeGlobalNotification: function(){
                $("#globalNotification").jqxNotification("closeAll");
            },
            addLocalMessage: function(messageKey, messageTemplate) {
                messageTemplate.key = messageKey;
                sessionStorage[messageKey] = JSON.stringify(messageTemplate);
            },
            isMessageLoadedLocally: function(messageKey) {
                return !dti.string.isEmpty(sessionStorage[messageKey]);
            },
            status: {
                count: 0,
                queue: [],
                displayProgressMessage: function (args) {
                    dti.message.status.setup(args, "PROGRESS_MESSAGE");
                    dti.message.status.displayMessage(args);
                },
                displayInfoMessage: function (args) {
                    dti.message.status.setup(args, "INFORMATION_MESSAGE");
                    dti.message.status.displayMessage(args);
                },
                displaySuccessfulMessage:  function (args) {
                    dti.message.status.setup(args, "SUCCESSFUL_MESSAGE");
                    dti.message.status.displayMessage(args);
                },
                displayWarningMessage:  function (args) {
                    dti.message.status.setup(args, "WARNING_MESSAGE");
                    dti.message.status.displayMessage(args);
                },
                displayErrorMessage:  function (args) {
                    dti.message.status.setup(args, "ERROR_MESSAGE");
                    dti.message.status.displayMessage(args);
                },
                displaySystemErrorMessage:  function (args) {
                    dti.message.status.setup(args, "SYSTEM_ERROR_MESSAGE");
                    dti.message.status.displayMessage(args);
                },
                displayMessage: function(args) {
                    var messageArgs = args;
                    if (typeof args == "string") {   // backward compatibility
                        messageArgs = {
                            cmd: "set",
                            messageKey: arguments[0],
                            messageParameters: arguments[1],
                            additionalParms: arguments[2]
                        };
                    }

                    messageArgs.id = dti.message.status.count++;    // unique id
                    $(window).trigger("message.status.dti",messageArgs);
                },
                displayMessages:  function (messageObjects, messageArgs) {
                    var command = "add";
                    if (messageArgs) {
                        if (messageArgs.additionalParms) {
                            if (messageArgs.additionalParms.progressStatus == "progress-end") {
                                dti.message.status.stopStatusProgressIndicator();
                            }
                        }

                        command = messageArgs.cmd || command;
                    }

                    var warningMessageQueue = [];
                    var errorMessageQueue = [];
                    var infoMessageQueue = [];
                    var successMessageQueue = [];

                    for (var i = 0; i < messageObjects.length; i++) {
                        var currentMessageObject = messageObjects[i];
                        var categoryName = currentMessageObject.messageCategory.category;
                        if (categoryName == "WARNING_MESSAGE") {
                            warningMessageQueue.push(currentMessageObject);
                        } else if (categoryName == "ERROR_MESSAGE") {
                            errorMessageQueue.push(currentMessageObject);
                        } else if (categoryName == "INFORMATION_MESSAGE") {
                            infoMessageQueue.push(currentMessageObject);
                        } else if (categoryName == "SUCCESSFUL_MESSAGE") {
                            successMessageQueue.push(currentMessageObject);
                        }
                    }

                    var messageObj = {};

                    if (warningMessageQueue.length > 0) {
                        messageObj.category = warningMessageQueue[0].messageCategory.category;
                        messageObj.htmlMessage = generateHTMLMessageList(warningMessageQueue);
                        var args = $.extend({cmd: command}, messageObj);
                        dti.message.status.setup(args, "WARNING_MESSAGE");
                        dti.message.status.displayMessage(args);
                    }

                    if (errorMessageQueue.length > 0) {
                        messageObj.category = errorMessageQueue[0].messageCategory.category;
                        messageObj.htmlMessage = generateHTMLMessageList(errorMessageQueue);
                        var args = $.extend({cmd: command}, messageObj);
                        dti.message.status.setup(args, "ERROR_MESSAGE");
                        dti.message.status.displayMessage(args);
                    }

                    if (infoMessageQueue.length > 0) {
                        messageObj.category = infoMessageQueue[0].messageCategory.category;
                        messageObj.htmlMessage = generateHTMLMessageList(infoMessageQueue);
                        var args = $.extend({cmd: command}, messageObj);
                        dti.message.status.setup(args, "INFORMATION_MESSAGE");
                        dti.message.status.displayMessage(args);
                    }

                    if (successMessageQueue.length > 0) {
                        messageObj.category = successMessageQueue[0].messageCategory.category;
                        messageObj.htmlMessage = generateHTMLMessageList(successMessageQueue);
                        var args = $.extend({cmd: command}, messageObj);
                        dti.message.status.setup(args, "SUCCESSFUL_MESSAGE");
                        dti.message.status.displayMessage(args);
                    }
                },
                processStatusMessage: function(messageArgs) {
                    function mapAdditionalParams(messageObj, messageArgs) {
                        if (messageArgs.additionalParms) {
                            messageObj.category = messageArgs.additionalParms.category || messageObj.category;
                            messageObj.autoClose = (typeof messageArgs.additionalParms.autoClose != "undefined") ? messageArgs.additionalParms.autoClose : messageObj.autoClose;
                            messageObj.autoCloseDelay = messageArgs.additionalParms.autoCloseDelay || messageObj.autoCloseDelay;
                            messageObj.isModal = (typeof messageArgs.additionalParms.isModal != "undefined") ? messageArgs.additionalParms.isModal : messageObj.isModal;
                            messageObj.progressStatus = (typeof messageArgs.additionalParms.progressStatus != "undefined") ? messageArgs.additionalParms.progressStatus : messageObj.progressStatus;
                            messageObj.errorInfo = messageArgs.additionalParms.errorInfo || messageObj.errorInfo;
                            messageObj.replace = messageArgs.additionalParms.replace || messageObj.replace;
                        }

                        messageObj.cmd = messageArgs.cmd;
                        messageObj.id = messageArgs.id;
                        messageObj.messageKey = messageArgs.messageKey;
                        messageObj.messageParameters = messageArgs.messageParameters;
                    }

                    if (messageArgs.additionalParms.category == "PROGRESS_MESSAGE") {
                        _getMessage(messageArgs.messageKey, messageArgs.messageParameters, messageArgs.additionalParms).then(function(messageObj){
                            mapAdditionalParams(messageObj, messageArgs);
                            _displayProgressMessage(messageObj);
                        });
                        return;
                    }

                    if (messageArgs.priority == "high") {
                        if (messageArgs.messageKey) {
                            _getMessage(messageArgs.messageKey, messageArgs.messageParameters, messageArgs.additionalParms).then(function(messageObj){
                                mapAdditionalParams(messageObj, messageArgs);
                                _displayStatusMessage(messageObj);
                            });
                        }
                        else {
                            var messageObj = {htmlMessage: messageArgs.htmlMessage};
                            mapAdditionalParams(messageObj, messageArgs);
                            _displayStatusMessage(messageObj);
                        }

                        return;
                    }

                    // queue the message
                    var messageObj = {htmlMessage: messageArgs.htmlMessage};
                    mapAdditionalParams(messageObj, messageArgs);
                    dti.message.status.queueMessage(messageObj);

                    if (messageArgs.messageKey) {
                        _getMessage(messageArgs.messageKey, messageArgs.messageParameters, messageArgs.additionalParms).then(function(messageObj){
                            var rowsFound = $.grep(dti.message.status.queue, function(item, index) {
                                if (item.id == messageArgs.id) {
                                    return true;
                                }

                                return false;
                            });

                            if (rowsFound.length) {
                                rowsFound[0].htmlMessage = messageObj.htmlMessage;
                                rowsFound[0].messageParameters = messageObj.messageParameters;
                            }
                        });
                    }
                },
                queueMessage: function(messageObj) {
                    var statusObj = dti.message.status;
                    var queue = statusObj.queue;
                    queue.push(messageObj);
                    queue.sort(function(a, b) {
                        return a.id - b.id;
                    });

                    if (!statusObj.queueProcessing) {
                        statusObj.dispatchMessages();
                    }
                },
                dispatchMessages: function() {
                    // send completed messages out.  preserve order
                    var statusObj = dti.message.status;
                    statusObj.queueProcessing = true;

                    var queue = statusObj.queue;
                    var queueItem;
                    while (queue.length > 0) {
                        queueItem = queue[0];
                        if (!queueItem.htmlMessage) {
                            break;
                        }
                        else {
                            _displayStatusMessage(queueItem);
                            queue.shift();
                        }
                    }

                    if (queue.length > 0) {
                        setTimeout(function() {
                            dti.message.status.dispatchMessages();
                        }, 1000);
                    }

                    statusObj.queueProcessing = false;
                },
                closeStatusMessage: function(args) {
                    if (dti.message.status.queue.length == 0 || (args && args.forceClose)) { // leave open for unprocessed messages
                        console.log("dti.message.status.closeStatusWindow()");
                        var $statusWindow = $("#statusWindow");
                        $statusWindow.addClass("dti-hide");

                        var $statusWindowContents = $("#statusWindowContents");
                        $statusWindowContents.text(""); // clean up

                        // delay close to allow for timing differences
                        setTimeout(function(statusWin) {
                            $statusWindow.jqxWindow('close');
                        }, 1000, $statusWindow);
                    }
                },
                closeProgressMessage: function(args) {
                    if (dti.message.status.queue.length == 0 || (args && args.forceClose)) { // leave open for unprocessed messages
                        console.log("dti.message.status.closeStatusWindow()");
                        var $statusWindow = $("#statusWindow");
                        $statusWindow.addClass("dti-hide");

                        var $statusWindowContents = $("#statusWindowContents");
                        $statusWindowContents.text(""); // clean up

                        // delay close to allow for timing differences
                        setTimeout(function(statusWin) {
                            $statusWindow.jqxWindow('close');
                        }, 1000, $statusWindow);
                    }
                },
                stopStatusProgressIndicator: function() {
                    $("#statusWindowProgress").addClass("dti-hide");
                },
                setup: function(args, category) {
                    args.additionalParms = args.additionalParms || {};
                    args.additionalParms.category = args.additionalParms.category || category;
                }
            }

        }
    })();

}

if (typeof dti.excel == "undefined") {
    dti.excel = (function() {
//-----------------------------------------------------------------------------
// Sends grid contents to server to be opened in Excel
//-----------------------------------------------------------------------------
        var sendGridToServerAsExcel = function (grid, url, dispType) {
            //get xml string
            var gridTbl = $("#" + grid);
            var rowCount = gridTbl.jqxGrid('getdatainformation').rowscount;
            if (rowCount > 0){
                var xmlString = gridTbl.jqxGrid('exportdata', 'xls');

                var formName = "__form_sending_csv_html_to_excel__";
                var aNewInnerHTML = '<form id="' + formName + '" method="post" >' +
                    '<input type="hidden" name="textForFile" />' +
                    '<input type="hidden" name="dispositionType" />' +
                    '</form>';
                var alreadyHasForm = document.forms[formName] ? true : false;
                if (!alreadyHasForm) {
                    document.body.insertAdjacentHTML("BeforeEnd", aNewInnerHTML);
                }
                document.forms[formName].elements["textForFile"].value = xmlString;
                document.forms[formName].elements["dispositionType"].value = dispType;
                document.forms[formName].action = url;
                document.forms[formName].method = "post";
                document.forms[formName].target = "_blank";
                document.forms[formName].submit();
            } else {
                dti.message.displayWarningMessage("core.export.excel.nodata");
            }
        };

        return {
            saveGridAsExcel: function(gridId,pageName, dispType) {

                event.stopPropagation();

                var exportType ='XLSX';

                sendGridToServerAsExcel(gridId, getContextPath() + getContext().getProperty("commonJspRoot") +"/jqxGridToExcelXLS.jsp?exportType="+exportType+"&gridId="+gridId+"&date=" + new Date()+"&pageName="+pageName, dispType);

                return false;
            },
            importGridFromExcel: function(gridId,pageName, dispType) {

                event.stopPropagation();

                //Publish event
                var eventData = { gridId: gridId, pageName: pageName, dispType: dispType };
                $(window).trigger("import.dti.utils.grid", eventData);

                return false;
            }
        }
    })();
}

if (typeof dti.messages == "undefined") {
    dti.messages = (function () {
        return {};
    })();
}

if (typeof dti.page == "undefined") {
    /**
     * The data type of page object would be:
     * <code>{"isChanged": true, "grids": [{"gridId", "gridId1", "records": records, "deletedRecords": deletedRecords}, {...}]}</code>
     */
    dti.page = (function () {

        return {
            promise: null,
            http: null,
            widgetType: "jqWidgets",
            currentInstance: null,
            isReloading: false,
            sessionHandler: null,
            setSessionHandler: function(handler) {
                dti.page.sessionHandler = handler;
            },
            checkForDuplicates: function(selector) {
                if (selector.indexOf(".") > -1 ) {
                    return;     // no check on classes
                }

                var id = (selector.indexOf("#") > -1) ? selector.substring(1) : selector;
                var duplicateCheck = "[id=".concat(id,"]");
                var $instances = $(duplicateCheck);
                if ($instances.length > 1) {
                    console.error("Duplicate elements found for id: ".concat(id));

                    $instances.each(function(index, element) {
                        var html = element.outerHTML || element.innerHTML;

                        var htmlSnippet = (html.length > 500) ? html.substring(0,500).concat("...") : html;
                        console.log("Instance: ".concat(htmlSnippet));
                    });

                    return $instances.length;
                }

                return 1;
            },

            create: function() {
                var args = {};
                if (arguments.length == 1) {
                    args = arguments[0];
                    dti.page.promise = args.promise;
                    dti.page.http = args.http;
                }
                else {      // backward compatibility
                    dti.page.promise = arguments[0];
                    dti.page.http = arguments[1];
                }


                return {
                    name: args.name,
                    type: args.type || "Page",
                    state: args.state,
                    version: args.version,
                    trackChanges: args.trackChanges || false,
                    debug: args.debug,
                    initInParallel: args.initInParallel || false,
                    configArgs: args,   // capture initial args,
                    options: {},
                    popupWindows: {},
                    unprocessedItems: {},
                    scopeListenerDeregisters: {},
                    hasComponents: false,
                    componentInitPromises: [],

                    _changeTracking: null,
                    _changeTrackingFactory: {
                        create: function(page) {
                            return {
                                addChangeTracking: function (selector) {
                                    $(selector).addClass("dti-change-tracking");
                                },
                                trackChanges: function () {
                                    $(".dti-ignore-changes").find(".dti-change-tracking").addBack().removeClass("dti-change-tracking");

                                    $(".dti-change-tracking").each(function () {
                                        $(this).off("change.dti.utils.page").on("change.dti.utils.page", function (event, payload) {
                                            if ($(this).hasClass("dti-grid")) {
                                                if (!payload) {     // ignore grid change not containing a payload
                                                    return;
                                                }
                                            }

                                            $(this).addClass("dti-dirty");

                                            if (!page.isDirtyNotificationSent) {
                                                page.isDirtyNotificationSent = true;
                                                if (page.options.onPageDirty) {
                                                    page.options.onPageDirty(event);
                                                }
                                            }
                                        });
                                    });
                                },
                                isDirty: function () {
                                    return page.isDirty();
                                },
                                resetDirty: function () {
                                    page.resetDirty();
                                }
                            };
                        }
                    },

                    _onConfigInit: function() {
                        this.pageContext = {
                            config: {
                                defer: $.Deferred()
                            }

                        };

                        this.onBeforeConfigInit();

                        // Process configuration data
                        var $context = this.pageContext;
                        var $page = this;
                        require(['configService', 'configServiceData'], function(configService){
                            var service = new configService($context);
                            service.getConfig(utils.routing.getCurrentRoute($page.state), $page).then(function (data) {
                                $page.applyConfig({target: $page}, data);

                                $page.onAfterConfigInit();

                                $context.config.defer.resolve();   // notify of ConfigInit completion
                            });

                        });

                        return this.pageContext.config.defer.promise();

                    },

                    _onPageInit: function() {
                        this.pageContext = this.pageContext || {};
                        this.pageContext.defer = $.Deferred();

                        this.onBeforePageInit();

                        if (this.options.onPageInit) {
                            this.options.onPageInit(this, this.pageContext);
                        }

                        var ths = this;
                        var initPromise = this.pageContext.defer.promise();

                        $.when(initPromise).done(function() {
                            console.log("onPageInit completed");
                        });

                        this.pageContext.onPageInit = {
                            promise: initPromise,
                            defer: this.pageContext.defer,
                            ready: function(ths, fn) {
                                $.when(initPromise).done(function() {
                                    if (fn) {
                                        fn.call(ths);
                                    }
                                });
                            },
                            resolve: function() {
                                var arr = Array.prototype.slice.call(arguments);
                                this.defer.resolve.apply(this, arr);
                            },
                            reject: function() {
                                var arr = Array.prototype.slice.call(arguments);
                                this.defer.reject.apply(this, arr);
                            }
                        };

                        var pageStartDefer = $.Deferred();
                        var startPromise = pageStartDefer.promise();

                        $.when(startPromise).done(function() {
                            console.log("onPageStart completed");
                        });

                        this.pageContext.onPageStart = {
                            promise: startPromise,
                            defer: pageStartDefer,
                            ready: function(ths, fn) {
                                $.when(startPromise).done(function() {
                                    if (fn) {
                                        fn.call(ths);
                                    }
                                });
                            },
                            resolve: function() {
                                var arr = Array.prototype.slice.call(arguments);
                                this.defer.resolve.apply(this, arr);
                            },
                            reject: function() {
                                var arr = Array.prototype.slice.call(arguments);
                                this.defer.reject.apply(this, arr);
                            }
                        };

                        return initPromise;
                    },

                    _onPageStart: function() {
                        this.pageContext.defer = this.pageContext.onPageStart.defer;

                        this.onAfterPageInit();
                        this.onBeforePageStart();
                        if (this.options.onPageStart) {
                            this.options.onPageStart(this, this.pageContext);
                        }

                        return this.pageContext.onPageStart.promise;
                    },

                    _onPageEnd: function() {
                        this.onBeforePageEnd();

                        this.autoUnWireEvents();
                        if (this.options.onPageEnd) {
                            this.options.onPageEnd(this, this.pageContext);
                        }

                        this.onAfterPageEnd();

                        dti.page.currentInstance = null;
                    },

                    configure: function(options) {
                        this.options = $.extend(this.options, options);

                        // cleanup page
                        if (!this._configureWasCalled) {
                            if (this.options.scope) {
                                this._configureWasCalled = true;
                                var ths = this;
                                this.options.scope.$on('$destroy', function () {
                                    ths._onPageEnd();
                                });

                                this.scopeListenerDeregisters['$stateChangeStart'] = this.options.scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {

                                    function isOkToProcess(toState, fromState, options) {
                                        if (toState) {
                                            if (((toState.name != fromState.name) ||    // page transition
                                                ((toState.name == fromState.name) && options))) {  // reload
                                                return true;
                                            }

                                            return false;
                                        }

                                        return true;    // support unit testing
                                    }

                                    if (!ths.isTransitioning) {
                                        if (!isOkToProcess(toState, fromState, ths.state.current.options)) {
                                            event.preventDefault();
                                            ths.isTransitioning = false;
                                        }
                                        else {
                                            ths.onBeforePageTransition(args).then(function(isOkToLeavePage) {
                                                if (isOkToLeavePage) {
                                                    ths.isTransitioning = true;

                                                    ths.resetDirty();
                                                    $(window).off("beforeunload.dti.utils.page");
                                                    dti.routing.go(ths.state, toState.name, toParams, ths.state.current.options);
                                                }
                                                else {
                                                    event.preventDefault();
                                                    ths.isTransitioning = false;
                                                }
                                            });
                                        }

                                    }

                                    if (ths.isDirty()) {
                                        event.preventDefault();
                                        ths.isTransitioning = false;
                                    }
                                });

                                this.scopeListenerDeregisters['$stateChangeSuccess'] = this.options.scope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
                                    ths.state.current.options = undefined;
                                });

                                this.scopeListenerDeregisters['$viewContentLoaded'] = this.options.scope.$on('$viewContentLoaded', function(){
                                    if (dti.page.sessionHandler) {
                                        var viewContents = $("#dynamic-view").html();
                                        if (dti.page.sessionHandler.hasExpiredBasedOnText(viewContents)) {
                                            dti.page.sessionHandler.notifyOfLoggedOut(dti.page);
                                        }
                                    }
                                });
                            }
                        }

                        if (this.options.configSettings && !this.controllerConfig) {
                            this.controllerConfig = this.options.configSettings;
                        }

                        return this;
                    },
                    run: function() {
                        $(window).off("beforeunload.dti.utils.page"); // clear page event

                        dti.page.currentInstance = this;
                        if (this.configArgs) {
                            this.configure(this.configArgs);
                        }

                        this.configArgs = null; // release temporary reference

                        if (this.options.initInParallel) {
                            console.error("dti.page: initInParallel is not supported"); // remove support
                        }
                        else {
                            var ths = this;
                            $.when(this._onConfigInit())
                                .then(this._onPageInit.bind(this))
                                .then(function() {
                                    if (ths.hasComponents) {
                                        ths.monitor.checkDependentComponents(ths);
                                        $.when.apply($, ths.componentInitPromises).done(function () {
                                            $.when(ths._onPageStart())
                                                .then(function() {
                                                    ths.onAfterPageStart();
                                                })
                                        })
                                    }
                                    else {
                                        $.when(ths._onPageStart())
                                            .then(function() {
                                                ths.onAfterPageStart();
                                            });
                                    }
                                });
                        }

                    },
                    monitor: {
                        allComponentInitsAreResolved: function(page) {
                            for (var i = 0; i < page.componentInitPromises.length; i++) {
                                var componentInitPromise = page.componentInitPromises[i];
                                var state = componentInitPromise.state();
                                if (state != "resolved" ) {
                                    return false;
                                }
                            }

                            return true;
                        },
                        listStatesOfPromises: function(page, attempt, name) {
                            console.log("dti.page.monitor for: ".concat(name, " attempt: " , attempt, " (start)"));
                            for (var i = 0; i < page.componentInitPromises.length; i++) {
                                var componentInitPromise = page.componentInitPromises[i];
                                var state = componentInitPromise.state();
                                console.log(componentInitPromise.regarding.concat(": ", state));
                            }

                            console.log("dti.page.monitor for: ".concat(name, " attempt: " , attempt, " (end)"));
                        },
                        checkDependentComponents: function(page) {
                            var ths = this;
                            dti.task.waitUntil({
                                name: "checkDependentComponents",
                                onCondition: function(attempt, name) {
                                    return ths.allComponentInitsAreResolved(page, attempt, name);
                                },
                                onWait: function(attempt, name) {
                                    ths.listStatesOfPromises(page, attempt, name);
                                },
                                onConditionMet: function(attempt, name) {
                                    console.log("dti.page.monitor: condition met for: ".concat(name));
                                    ths.listStatesOfPromises(page, attempt, name);
                                },
                                maxAttempts: 20,
                                interval: 500
                            });
                        }
                    },
                    compilation: {
                        _compilation: {},
                        get: function(name) {
                            return this._compilation[name];
                        },
                        set: function(name, compilation) {
                            return this._compilation[name] = compilation;
                        },
                        clear: function(name) {
                            this._compilation[name] = null;
                        }
                    },
                    applyConfig: function(args, data) {
                        args.target.config = data;
                        args.page = args.page || args.target;       // default page to target

                        this.compileConfig(args);

                        if (this.debug) {
                            var compilation = "var compilation[".concat(args.target.name, "] = ",JSON.stringify(this.compilation.get(args.target.name)));
                            console.log(compilation);
                        }

                        this._changeTracking = this._changeTrackingFactory.create(this);
                        this.applyWidgets(this.compilation.get(args.target.name), false, args.target.type);  // apply page widgets
                        this.autoWireEvents();
                    },
                    applyConfigFor: function(item) {    // apply for one element only
                        function buildCompilation (page, type, selector, componentInfo, name) {
                            if (!page.unprocessedItems) {
                                return null;
                            }

                            var data = page.unprocessedItems[selector];
                            if (data) {
                                componentInfo[type] = {};
                                componentInfo[type][name] = data.itemInfo;
                            }

                            return data;
                        }

                        var type = item.type;
                        var data;

                        var componentInfo = {};

                        var name;
                        var selector;
                        switch(type) {
                            case "id":
                                name = item.selector;
                                selector = "#".concat(name);
                                data = buildCompilation(this, "ids", selector, componentInfo, name);
                                if (!data) {
                                    return;
                                }
                                break;
                            case "class":
                                name = item.selector;
                                selector = ".".concat(name);
                                data = buildCompilation(this, "classes", selector, componentInfo, name);
                                if (!data) {
                                    return;
                                }
                                break;
                            case "selector":
                                selector = name = item.selector;
                                data = buildCompilation(this, "selectors", selector, componentInfo, name);
                                if (!data) {
                                    return;
                                }
                                break;
                        }

                        this.applyWidgets(componentInfo, false, this.type);  // apply page widgets
                        this.autoUnWireEvents();
                        this.autoWireEvents();
                        this.autoWireWidgetSources(componentInfo);

                        data.isProcessed = true;
                    },
                    compileConfig: function(args) {  // merge common elements based on priority
                        /* Priority
                         1.	Server configuration
                         1a.	Customer overrides
                         1b.	Base configuration
                         2.	Page level configuration
                         3.	Global level configuration
                         */

                        var target = args.target;
                        var targetName = target.name;
                        var targetType = target.type;
                        var page = args.page;

                        var configSource = targetType.toLowerCase();
                        var pageFromServer = target.config[configSource];      // customer overrides
                        var globalFromServer = target.config.global;  // base configuration

                        var controllerConfigSource = configSource.concat("s");
                        var controllerConfig = (targetType == "Component")
                            ? target.componentConfig    // page level configuration
                            : target.controllerConfig;    // component level configuration
                        var pageFromController = controllerConfig[controllerConfigSource][targetName];
                        var globalFromController = controllerConfig.global;            // global level configuration

                        var compilationContents = {
                            classes: {},
                            ids: {},
                            selectors: {},
                            properties: {}
                        };
                        page.compilation.set(targetName, compilationContents);

                        // Get Global configuration from local page
                        this.acquireDependencies(pageFromController.dependencies, globalFromController, compilationContents);

                        // Combine common page configuration from local page
                        this.acquirePageConfig(pageFromController, compilationContents);

                        // Get Global configurations from Server
                        this.acquireDependencies(pageFromController.dependencies, globalFromServer, compilationContents);

                        // Combine Server level configuration and overrides
                        this.acquirePageConfig(pageFromServer, compilationContents);
                    },
                    acquireDependencies: function(dependencies, base, compilation) {
                        if (!base) {
                            return;
                        }

                        var acquireSpecificDependencies = function(type, names, configInfo) {
                            if (names) {
                                for (var i = 0; i < names.length; i++) {
                                    var name = names[i];
                                    var info = configInfo[name];
                                    compilation[type][name] = info;
                                }
                            }
                        };

                        if (dependencies){
                            acquireSpecificDependencies("classes", dependencies.classes, base.classes);
                            acquireSpecificDependencies("ids", dependencies.ids, base.ids);
                            acquireSpecificDependencies("selectors", dependencies.selectors, base.selectors);
                            acquireSpecificDependencies("properties", dependencies.properties, base.properties);
                        }
                    },
                    acquirePageConfig: function(base, compilation) {
                        if (!base) {
                            return;
                        }

                        var acquireSpecificPageItems = function(type, configInfo) {
                            if (configInfo) {
                                for (var name in configInfo) {
                                    var info = configInfo[name];
                                    var compilationItem = compilation[type][name];
                                    if (compilationItem) {
                                        switch (info.type.toLowerCase()) {
                                            case "grid":
                                                mergeGridProperties(compilationItem, info);
                                                break;
                                            case "window":
                                                mergeWindowProperties(compilationItem, info);
                                                break;
                                            default:
                                                $.extend(true, compilationItem, info);  // overlay new item over old
                                                break;
                                        }
                                    }
                                    else {
                                        compilation[type][name] = info;
                                    }
                                }
                            }
                        };

                        var mergeWindowProperties = function(compilationItem, windowInfo) {
                            compilationItem.settings = $.extend(compilationItem.settings, windowInfo.settings);
                        };

                        var mergeGridProperties = function(compilationItem, gridInfo) {
                            function findColumn(id, columns) {
                                for (var j=0; j<columns.length; j++) {
                                    var column = columns[j];
                                    if (column.id == id) {
                                        return column;
                                    }
                                }

                                return null;
                            }

                            var compiledColumns = compilationItem.settings.columns;
                            var newColumns = gridInfo.settings.columns;

                            compilationItem.type = compilationItem.type || "Grid";

                            // support for grid properties (start)
                            compilationItem.settings.init = $.extend(compilationItem.settings.init, gridInfo.settings.init);
                            compilationItem.settings.source = $.extend(compilationItem.settings.source, gridInfo.settings.source);
                            compilationItem.settings.options = $.extend(compilationItem.settings.options, gridInfo.settings.options);
                            // support for grid properties (end)

                            // Reset column order to go with the Server order.
                            compilationItem.settings.columns = [];

                            for (var i=0; i<newColumns.length; i++ ) {
                                var newColumn = newColumns[i];
                                var compiledColumn = findColumn(newColumn.id, compiledColumns);
                                if (compiledColumn) {
                                    $.extend(compiledColumn, newColumn);
                                    compilationItem.settings.columns.push(compiledColumn);
                                }
                                else {
                                    compilationItem.settings.columns.push(newColumn);
                                }
                            }
                        };

                        acquireSpecificPageItems("selectors", base.selectors);
                        acquireSpecificPageItems("classes", base.classes);
                        acquireSpecificPageItems("ids", base.ids);
                        acquireSpecificPageItems("properties", base.properties);
                    },
                    unapplyWidgets: function(base, componentType) {
                        this.applyWidgets(base, true, componentType);
                    },
                    applyWidgets: function(base, removeWidgets, componentType) {
                        var action = (removeWidgets) ? "unapplyWidget" : "applyWidget";

                        // don't remove potentially shared components if not in the page object
                        if (componentType == "Page" || action != "unapplyWidget") {
                            // apply selectors
                            if (base.selectors) {
                                for (var selector in base.selectors) {
                                    var selectorInfo = base.selectors[selector];
                                    this[action](selector, selectorInfo, removeWidgets);
                                }
                            }

                            // apply classes
                            if (base.classes) {
                                for (var cssclass in base.classes) {
                                    var classInfo = base.classes[cssclass];
                                    this[action](".".concat(cssclass), classInfo, removeWidgets);
                                }
                            }
                        }

                        // apply ids
                        if (base.ids) {
                            for (var id in base.ids) {
                                var idInfo = base.ids[id];
                                this[action]("#".concat(id), idInfo, removeWidgets);
                            }
                        }

                    },
                    applyWidget: function(selector, itemInfo, removeWidget) {
                        if (itemInfo.classes) { // apply classes
                            var classList = itemInfo.classes.join(' ');
                            $(selector).addClass(classList);
                        }

                        if (itemInfo.type) {    // apply settings
                            if (this.isSupported(itemInfo.type)) {
                                switch (itemInfo.type.toLowerCase()) {
                                    case "grid":
                                        this.configureGrid(selector, itemInfo);
                                        break;
                                    case "panel":
                                        this.configurePanel(selector, itemInfo);
                                        break;
                                    case "tabs":
                                        this.configureTabs(selector, itemInfo);
                                        break;
                                    default:
                                        this.createWidget(selector, itemInfo);
                                        break;
                                }
                            }
                        }

                        if (itemInfo.label) {   // apply label
                            var labelSelector = selector.concat("-label");
                            $(labelSelector).text(itemInfo.label);
                        }

                        // TODO: log errors
                        /*else {
                         console.log("dti.page.error: ".concat(selector, " missing a type definition"));
                         }*/


                    },
                    unapplyWidget: function(selector, itemInfo, removeWidget) {
                        if (itemInfo.classes) { // apply classes
                            var classList = itemInfo.classes.join(' ');
                            $(selector).removeClass(classList);
                        }

                        this.removeWidget(selector, itemInfo);
                    },
                    createWidget: function(selector, itemInfo, itemType) {
                        try {
                            dti.page.checkForDuplicates(selector);

                            var type = itemInfo.type || itemType;
                            var widgetName = this.mapWidgetType(type);
                            $(selector)[widgetName](itemInfo.settings || {});
                            $(selector).addClass("dti-page-managed");

                            if (this.trackChanges) {
                                switch (type) {
                                    case "Button":
                                    case "Panel":
                                    case "Window":
                                    case "Popover":
                                        break;
                                    default:
                                        this._changeTracking.addChangeTracking(selector);
                                        break;
                                }

                            }
                        }
                        catch(ex) {
                            var msg = "Warning: ".concat(selector, " not processed: ", ex.message);
                            console.error(msg);
                            this.unprocessedItems[selector] = ({selector: selector, itemInfo: itemInfo, message: msg})
                        }
                    },
                    removeWidget: function(selector, itemInfo, itemType) {
                        try {
                            var type = itemInfo.type || itemType;
                            if (type) {
                                var widgetName = this.mapWidgetType(itemInfo.type || itemType);
                                var $widget = $(selector);
                                if ($widget.length) {
                                    $widget[widgetName]("destroy");
                                }
                            }
                        }
                        catch(ex) {
                            var msg = "Info: ".concat(selector, " may have been removed in an earlier step or not not found: ", ex.message);
                            console.log(msg);
                        }
                    },
                    configurePanel: function(selector, panelInfo) {
                        var settings = panelInfo.settings;

                        if (!settings) {
                            settings = panelInfo.settings = {};
                        }

                        if (typeof(settings.collapsible) != "undefined") {
                            if (!settings.collapsible) {
                                settings.toggleMode = "none";
                                settings.showArrow = false;
                            }
                            delete settings.collapsible;  // delete jqx unsupported property
                        }

                        this.createWidget(selector, panelInfo);
                    },
                    configureTabs: function(selector, tabsInfo) {
                        var settings = tabsInfo.settings;

                        if (!settings) {
                            settings = tabsInfo.settings = {};
                        }

                        $(selector).removeClass("invisible");       // backward-compatible support
                        $(selector).removeClass("dti-hide");

                        this.createWidget(selector, tabsInfo);
                    },
                    configureGrid: function(selector, gridInfo) {
                        function setInitDefaults(initInfo) {
                            initInfo.pageable = (typeof initInfo.pageable != "undefined") ? initInfo.pageable : true;
                            initInfo.autoheight = (typeof initInfo.autoheight != "undefined") ? initInfo.autoheight : true;
                            initInfo.altrows = (typeof initInfo.altrows != "undefined") ? initInfo.altrows : true;
                            initInfo.enabletooltips = (typeof initInfo.enabletooltips != "undefined") ? initInfo.enabletooltips : true;
                            initInfo.columnsresize = (typeof initInfo.columnsresize != "undefined") ? initInfo.columnsresize : true;
                            initInfo.sortable = (typeof initInfo.sortable != "undefined") ? initInfo.sortable : true;
                            initInfo.filterable = (typeof initInfo.filterable != "undefined") ? initInfo.filterable : true;
                            initInfo.autoshowfiltericon = (typeof initInfo.autoshowfiltericon != "undefined") ? initInfo.autoshowfiltericon : false;
                            initInfo.pagesize = (typeof initInfo.pagesize != "undefined") ? initInfo.pagesize : 20;
                            initInfo.pagesizeoptions = (typeof initInfo.pagesizeoptions != "undefined") ? initInfo.pagesizeoptions : ['5', '10', '20', '30'];
                        }

                        function generateSchema(columns) {
                            var schema = [];

                            var alreadyHasRowId = false;
                            for (var j=0; j<columns.length; j++) {
                                if (columns[j].datafield == "rowId") {
                                    alreadyHasRowId = true;
                                }
                            }

                            if (!alreadyHasRowId) {
                                columns.push({id: "rowId", datatype: "int", visible: false});
                            }

                            for (var i=0; i<columns.length; i++) {
                                var column = columns[i];
                                var schemaItem = {
                                    name: column.id,
                                    type: column.datatype
                                };

                                if (column.map) {
                                    schemaItem.map = column.map;
                                }

                                // Set default format for parsing date.
                                if (column.format) {
                                    schemaItem.format = column.format;
                                } else if (column.datatype == "date") {
                                    schemaItem.format = dti.dataFormat.XML_DATE_FORMAT;
                                }

                                schema.push(schemaItem);
                            }

                            return schema;
                        }

                        function generateWrapper(selector, message) {
                            var idPart = selector.replace('#','');


                            var iconInfo = "glyphicon glyphicon-refresh animate";
                            try {
                                var iconInfo = dti.configData.global.windows.statusWindow.icons["dti-progress"].classes;
                                var snippet = [
                                    '<div id="'.concat(idPart,'-loading-status" class="strong text-danger dti-grid-message" style="display:none">'),
                                    '<span class="'.concat(iconInfo, '"></span>&nbsp; '),
                                    message,
                                    '</div>',
                                ].join('\n');

                                $(snippet).insertBefore(selector);
                            }
                            catch(err) {

                            }
                        }

                        function prepareExcelExport(selector, options) {
                            if (options && options.exportToExcel && options.exportToExcel == true) {
                                $(selector).addClass("dti-excel-export");
                            }
                            else {  // default to supporting excel export
                                $(selector).addClass("dti-excel-export");
                            }

                            if (options && options.importFromExcel && options.importFromExcel == true) {
                                $(selector).addClass("dti-excel-import");
                            }
                        }

                        var gridSetupCompletEventName = "dti-page:gridsetupcomplete";

                        function registerGridListeners(selector, page, eventName) {
                            var id = selector.replace('#','');
                            dti.grid.register(id, page, eventName);
                        }

                        function notifyOfGridComplete(selector, page, eventName) {
                            $(page).trigger(eventName, [$(selector), page]);
                        }

                        setInitDefaults(gridInfo.settings.init);

                        this[selector] = {};

                        // Generate Schema
                        this[selector].dataFields = generateSchema(gridInfo.settings.columns);

                        this[selector].displayColumns = gridInfo.settings.columns;

                        var jqxSource = {
                            datatype: "array",
                            datafields: this[selector].dataFields,
                            localdata: []
                        };

                        var source = gridInfo.settings.source;
                        if (source) {
                            $.extend(jqxSource, source);
                        }
                        this[selector].source = jqxSource;

                        if (gridInfo.settings.init.filterable == false && !this[selector].source.filter) {
                            this[selector].source.filter = function(filter, records, count) {return records.slice();};  // default a value
                        }

                        var adapterOptions = { autoBind: true};
                        var eventConfig = gridInfo.settings.events;
                        if (eventConfig) {
                            for (var event in eventConfig) {
                                adapterOptions[event] = eventConfig[event]; // Note: potentially make a copy
                            }
                        }

                        var dataAdapter = new $.jqx.dataAdapter(jqxSource, adapterOptions);

                        gridInfo.settings.init.columns = this[selector].displayColumns.slice();    // copy
                        var mappedColumns = gridInfo.settings.init.columns;
                        for (var i=0; i<mappedColumns.length; i++) {
                            var col = mappedColumns[i];
                            if (col.label) {
                                col.text = col.label;
                            }

                            if (col.id) {
                                switch (col.datatype) {
                                    case "number":
                                        col.datafield = "";
                                        break;
                                    default:
                                        col.datafield = col.id;
                                        break;
                                }
                            }

                            if (col.formatpattern) {
                                col.cellsformat = col.formatpattern;
                            } else if (col.datatype == "date") {
                                col.cellsformat = dti.dataFormat.DISPLAY_DATE_FORMAT;
                            }

                            if (typeof(col.editable) == "undefined") {
                                if (typeof(col.readonly) !== "undefined") {
                                    col.editable = !col.readonly;
                                    if (col.editable) {     // if one column is editable then the grid is editable
                                        gridInfo.settings.init.editable = true;
                                    }
                                }
                                else {  // default to read only
                                    col.editable = false;
                                }
                            }
                            else {  // if one column is editable then the grid is editable
                                gridInfo.settings.init.editable = true;
                            }


                            if (typeof(col.visible) !== "undefined") {
                                col.hidden = !col.visible;
                            }

                            // TODO: Display error on missing required fields
                        }

                        generateWrapper(selector,"Loading. Please wait...");

                        prepareExcelExport(selector, gridInfo.settings.options);

                        registerGridListeners(selector, this, gridSetupCompletEventName);

                        gridInfo.settings.init.source = dataAdapter;
                        this.createWidget(selector, {type: gridInfo.type, settings: gridInfo.settings.init });

                        notifyOfGridComplete(selector, this, gridSetupCompletEventName);
                    },
                    isSupported: function(type) {
                        if (!type) {
                            return false;
                        }

                        if (type == "jqxWindow" || type == "dtiWindow" || type == "Popover") {
                            return false;   // support externally in the controller
                        }

                        return true;
                    },
                    mapWidgetType: function(type) { // returns widget type
                        if (dti.page.widgetType == "jqWidgets") {
                            switch(type.toLowerCase()) {    // mapping table
                                case "panel":
                                    return "jqxExpander";
                                case "dtiwindow":
                                    return "dtiWindow";
                            }

                            return "jqx".concat(type);
                        }

                        return type;
                    },
                    autoWireEvents: function() {
                        this.autoWireButtons();
                    },
                    autoWireButtons: function() {
                        var pg = this;
                        dti.button.wire(".dti-button", pg);
                    },
                    autoUnWireEvents: function() {
                        this.autoUnWireButtons();
                    },
                    autoUnWireButtons: function() {
                        var pg = this;
                        dti.button.unwire(".dti-button", pg);
                    },
                    buttonClick: function($button, fn, data) {
                        if (this.options.onBeforeButtonClick) {
                            this.options.onBeforeButtonClick($button);
                        }

                        if (fn) {
                            fn($button, data);
                        }

                        if (this.options.onAfterButtonClick) {
                            this.options.onAfterButtonClick($button);
                        }
                    },
                    autoWireWidgetSources: function(base) {
                        var pg = this;

                        // apply ids
                        if (base && base.ids) {
                            for (var id in base.ids) {
                                var idInfo = base.ids[id];
                                switch(idInfo.type) {
                                    case "ComboBox":
                                    case "DropDownList":
                                    case "ListBox":
                                    case "Input":
                                    case "Tree":
                                        if (pg.source && pg.source[id]) {
                                            if (pg.source[id].data) {
                                                var widgetName = this.mapWidgetType(idInfo.type);
                                                $("#".concat(id))[widgetName]({source: pg.source[id].data});
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    },
                    waitOnComponentInitPromise : function(id, componentInitPromise) {
                        componentInitPromise.regarding = id.concat("_onComponentInit");
                        this.componentInitPromises.push(componentInitPromise);
                    },
                    onBeforeConfigInit: function() {
                        if (this.options.onBeforeConfigInit) {
                            this.options.onBeforeConfigInit(this, this.pageContext);
                        }

                        // Add default behavior here

                        if (this.options.onBeforeConfigInitCompleted) {
                            this.options.onBeforeConfigInitCompleted(this, this.pageContext);
                        }
                    },
                    onAfterConfigInit: function() {
                        if (this.options.onAfterConfigInit) {
                            this.options.onAfterConfigInit(this, this.pageContext);
                        }

                        $(".panel-body").removeClass("hidden");

                        var ths = this;
                        var $components = $(".dti-component");
                        if ($components.length) {
                            this.hasComponents = true;
                            this.pageContext.componentInitDefers = {};
                        }

                        $(".dti-component").each(function() {
                            var $this = $(this);
                            var componentId = $this.prop("id");
                            var sourceName = $this.attr("data-name");
                            ths.pageContext.page = ths;

                            var componentInitDefer = $.Deferred();

                            var componentInitPromise = componentInitDefer.promise();
                            ths.pageContext.componentInitDefers[componentId] = componentInitDefer;
                            componentInitDefer.regarding = componentId.concat("_onComponentInit");
                            ths.waitOnComponentInitPromise(componentId, componentInitPromise);

                            dti.component.load(ths.pageContext, componentId, sourceName);
                        });
                        this.notifyOfEvent("afterConfigInit.dti"); // pub/sub

                        if (this.options.onAfterConfigInitCompleted) {
                            this.options.onAfterConfigInitCompleted(this, this.pageContext);
                        }
                    },
                    onBeforePageInit: function() {
                        if (this.options.onBeforePageInit) {
                            this.options.onBeforePageInit(this, this.pageContext);
                        }

                        // Add default behavior here
                        var $state = this.state;
                        $(".dti-excel-export").each(function() {
                            dti.grid.setupExcelExport($(this), $state);
                        });
                        $(".dti-excel-import").each(function() {
                            dti.grid.setupExcelImport($(this), $state);
                        });

                        // subscribe to errors event
                        var pg = this;
                        $(window).off("errors.dti.utils.page").on("errors.dti.utils.page", function(errors) {
                            var args = Array.prototype.slice.call(arguments);

                            var errorInfo = args[1];

                            pg.notifyOfErrors(args);

                            if (dti.page.sessionHandler) {
                                if (dti.page.sessionHandler.hasExpiredBasedOnText(errorInfo.responseText)) {
                                    dti.page.sessionHandler.notifyOfLoggedOut(dti.page);
                                    return;
                                }
                            }

                            var customMessage = {};
                            var msgKey = "core.error";
                            var msgParams = errorInfo.messageParams;
                            var stackTrace = errorInfo.stackTrace;
                            var targetUrl = errorInfo.url;

                            if (errorInfo.category == "system" && errorInfo.additionalInfo) {
                                var line = errorInfo.additionalInfo.line;
                                if (line) {
                                    targetUrl = targetUrl.concat(":",line);
                                }

                                var col = errorInfo.additionalInfo.col;
                                if (col) {
                                    targetUrl = targetUrl.concat(":",col);
                                }

                            }

                            if (errorInfo.type == "dti.errorInfo") {
                                msgKey = errorInfo.messageKey || msgKey;
                            }

                            dti.errors.addErrorInfo(targetUrl, errorInfo);

                            dti.message.status.displaySystemErrorMessage({messageKey: msgKey, messageParameters: msgParams, additionalParms: {progressStatus: "progress-end", errorInfo: errorInfo,
                                replace: {from: 'class="dti-mail-link"', to: 'class="dti-mail-link"'.concat(" data-url='",targetUrl,"'")}}});

                            // wire up tech support link when message is displayed
                            $(window).off("message.global.dti.utils").on("message.global.dti.utils", function(messageObj) {
                                var emailSupported = getContext().getProperty("dti.technicalSupport.enableEmailing");
                                if (emailSupported == "false") {    // defaults to true
                                    $(".dti-mail-link").remove();
                                }

                                $(".dti-mail-link").off("click.dti.utils.page").on("click.dti.utils.page", function() {
                                    var targetUrl = $(this).attr("data-url");
                                    var errorInfo = dti.errors.getErrorInfo(targetUrl);

                                    dti.message.getMessage(errorInfo.messageKey, errorInfo.messageParams).then(function(msg) {
                                        function emailMessagePart(fullMessage, errorInfo) {
                                            var contentsTruncated = false;
                                            var EMAIL_BODY_INTERNAL_SIZE_LIMIT = 1000;  // leave room for additional info

                                            var msgFirstPart =  fullMessage.split("].")[0];  // Dependency: expects [{0}].
                                            msgFirstPart = msgFirstPart
                                                .concat("].\n")
                                                .concat("\nURI: ", errorInfo.url);

                                            if (errorInfo.data) {
                                                msgFirstPart = msgFirstPart
                                                    .concat("\nData: ", errorInfo.data);
                                            }

                                            if (errorInfo.publicUser) {
                                                msgFirstPart = msgFirstPart
                                                    .concat("\nUser: ", errorInfo.publicUser.userName, " <", errorInfo.publicUser.userId, ">");
                                            }

                                            msgFirstPart = msgFirstPart
                                                .concat("\nTime: ", errorInfo.timeStamp);

                                            if (errorInfo.stackTrace) {
                                                var stackTraceChunkSize = (EMAIL_BODY_INTERNAL_SIZE_LIMIT - msgFirstPart.length);
                                                if (stackTraceChunkSize > errorInfo.stackTrace.length) {
                                                    stackTraceChunkSize = errorInfo.stackTrace.length;
                                                }
                                                else {
                                                    contentsTruncated = true;
                                                }

                                                msgFirstPart = msgFirstPart.concat("\n\nStack Trace: \n", errorInfo.stackTrace.substr(0,stackTraceChunkSize));
                                            }

                                            if (contentsTruncated)  {
                                                msgFirstPart = msgFirstPart.concat("\n. . .");
                                            }

                                            return msgFirstPart.concat("\n\nPlease add additional comments below:\n");
                                        }

                                        var to = getContext().getProperty("dti.technicalSupport.emailAddress");
                                        var subject = encodeURIComponent(getContext().getProperty("dti.technicalSupport.subject"));
                                        var body = encodeURIComponent(emailMessagePart(msg.htmlMessage, errorInfo));
                                        var mailInfo = "mailto:".concat(to,"?subject=",subject,"&body=",body);

                                        if (window.chrome) {
                                            $('<iframe>', {
                                                src: mailInfo,
                                                id: 'ifrmIndex-actual',
                                                frameborder: 0,
                                                scrolling: 'no',
                                                class: 'doc'
                                            }).appendTo('#ifrmIndex-contents');
                                        }
                                        else {
                                            dtiPopup = window.open(mailInfo, 'mailPopup','toolbar=no,height=50,width=50,left:700');

                                            setTimeout(function(popup) {
                                                if (popup) {
                                                    popup.close();
                                                }
                                            }, 1000, dtiPopup);
                                        }
                                    });
                                });
                            });

                        });

                        if (this.options.onBeforePageInitCompleted) {
                            this.options.onBeforePageInitCompleted(this, this.pageContext);
                        }
                    },
                    onAfterPageInit: function() {
                        if (this.options.onAfterPageInit) {
                            this.options.onAfterPageInit(this, this.pageContext);
                        }

                        // Add default behavior here
                        this.notifyOfEvent("afterPageInit.dti"); // pub/sub

                        if (this.options.onAfterPageInitCompleted) {
                            this.options.onAfterPageInitCompleted(this, this.pageContext);
                        }
                    },
                    onBeforePageStart: function() {
                        if (this.options.onBeforePageStart) {
                            this.options.onBeforePageStart(this, this.pageContext);
                        }

                        // default behavior
                        this.notifyOfEvent("routing.dti", utils.routing.getCurrentRoute(this.state)); // pub/sub

                        if (this.options.onBeforePageStartCompleted) {
                            this.options.onBeforePageStartCompleted(this, this.pageContext);
                        }
                    },
                    onAfterPageStart: function() {
                        if (this.options.onAfterPageStart) {
                            this.options.onAfterPageStart(this, this.pageContext);
                        }

                        var pg = this;

                        // default behavior
                        this.autoWireWidgetSources(this.compilation.get(this.name));
                        if (this.trackChanges) {
                            this._changeTracking.trackChanges();

                            dti.message.getMessage("common.change.lost.confirm").then(function(messageObj) {
                                pg.confirmMessage = messageObj.htmlMessage;
                            });

                            $(window).off("beforeunload.dti.utils.page").on("beforeunload.dti.utils.page", function() {
                                if (pg.isDirty()) {
                                    return pg.confirmMessage;
                                }
                            });
                        }

                        var helpPageName = pg.name.toLowerCase();
                        var path = getContext().getProperty("dti.module.rootHelpUrl").concat("/", helpPageName, ".htm");

                        $(".page-help").off("click.dti.utils.page").on("click.dti.utils.page", function() {
                            var left = screen.width - 600;
                            var helpWindow = window.open(path, "dti.module.help", "toolbar=no, scrollbars=yes, resizable=yes, top=50, width=500, height=600, left=".concat(left));
                            helpWindow.focus();
                        });

                        this.notifyOfEvent("afterPageStart.dti"); // pub/sub

                        if (this.options.onAfterPageStartCompleted) {
                            this.options.onAfterPageStartCompleted(this, this.pageContext);
                        }
                    },
                    onBeforePageEnd: function() {
                        if (this.options.onBeforePageEnd) {
                            this.options.onBeforePageEnd(this, this.pageContext);
                        }

                        // Add default behavior here

                        if (this.options.onBeforePageEndCompleted) {
                            this.options.onBeforePageEndCompleted(this, this.pageContext);
                        }
                    },
                    onAfterPageEnd: function() {
                        if (this.options.onAfterPageEnd) {
                            this.options.onAfterPageEnd(this, this.pageContext);
                        }

                        this.notifyOfEvent("afterPageEnd.dti"); // notify listeners first

                        this.unapplyWidgets(this.compilation.get(this.name), this.type);  // apply page widgets
                        $(".dti-event-monitored").off();        // clear all events
                        this.cleanUpWindows();
                        dti.clearIntervals();
                        this.clearScopeListeners();

                        $(".page-help").off("click.dti.utils.page");
                        $(window).off("errors.dti");
                        dti.logging.clear();
                        dti.errors.clear();
                        $("#globalNotification").jqxNotification("closeAll");
                        dti.message.status.closeStatusMessage();
                        this.compilation.clear(this.name);
                        dti.eventManager.clear();
                        console.log("onPageEnd completed");


                        if (this.options.onAfterPageEndCompleted) {
                            this.options.onAfterPageEndCompleted(this, this.pageContext);
                        }
                    },
                    clearScopeListeners: function() {
                        for (var key in this.scopeListenerDeregisters) {
                            var fn = this.scopeListenerDeregisters[key];
                            if (fn) {
                                fn();
                            }
                        }
                    },
                    registerWindow: function(windowId, registerArgs) {
                        this.registerObject(windowId, registerArgs, "Window");
                    },
                    registerObject: function(objectId, registerArgs, objectType) {
                        this.popupWindows[objectId] = objectId;
                        var configSettings = {};
                        var compilationName = this.name;
                        if (registerArgs) {
                            if(registerArgs.targetName) {
                                compilationName = registerArgs.targetName;
                            }

                            configSettings = this.compilation.get(compilationName).ids[objectId].settings;
                            if (registerArgs.settings) {
                                configSettings = $.extend({}, configSettings, registerArgs.settings);
                            }
                        } else {
                            configSettings = this.compilation.get(compilationName).ids[objectId].settings;
                        }

                        var widgetName = this.mapWidgetType(objectType);
                        $("#".concat(objectId))[widgetName](configSettings);
                    },
                    isWindowRegistered: function(windowId) {
                        return typeof this.popupWindows[windowId] !== 'undefined';
                    },
                    cleanUpWindows: function() {
                        for (var id in this.popupWindows) {
                            dti.window.destroy(id);
                        }
                        this.popupWindows = {};
                    },
                    loadView: function(id) {
                        var defer = $.Deferred();
                        var $div = $("#".concat(id));
                        var path = $div.attr("data-src");

                        if (path) {
                            path = path.replace(/'/g,"");
                        }
                        else {
                            console.error("Error: data-src attribute is missing from element ".concat(id));
                        }

                        $div.load(path, function(responseTxt, statusTxt, xhr){
                            if(statusTxt == "error") {
                                var msg = "Error: ".concat(path, "; ", xhr.status, ": ", xhr.statusText);
                                console.error(msg);
                                defer.reject(msg);
                                return;
                            }

                            defer.resolve(responseTxt);
                        });

                        return defer.promise();
                    },
                    loadController: function(name, onLoad) {
                        var controller;

                        require([name], function(controllerConstructor) {
                            controller = new controllerConstructor();
                            if (onLoad) {
                                onLoad(controller);
                            }
                        });

                        return controller;
                    },
                    isDirty: function(selector) {
                        if (selector) {
                            return $(selector).find(".dti-dirty").length > 0;
                        }

                        return $(".dti-dirty").length > 0;
                    },
                    resetDirty: function(selector) {
                        if (selector) {
                            $(selector).find(".dti-dirty").removeClass("dti-dirty");
                            $(selector).removeClass("dti-dirty");
                        }
                        else {
                            $(".dti-dirty").removeClass("dti-dirty");
                            if (this.options.onPageResetDirty) {
                                this.options.onPageResetDirty();
                            }
                        }

                        this.isDirtyNotificationSent = false
                    },
                    goToPage: function(transitionFn, args) {
                        // perform default page behavior
                        var pg = this;
                        if (transitionFn) {
                            pg.state.current.options = args.options;    // tunnel the options in the dirty scenario
                            transitionFn(pg.state, args.to, args.toParams, args.options);
                        }
                    },
                    go: function(to, toParams, options) {
                        this.goToPage(dti.routing.go, {to: to, toParams: toParams, options: options});
                    },
                    transitionTo: function(to, toParams, options) {
                        this.goToPage(dti.routing.transitionTo, {to: to, toParams: toParams, options: options});
                    },
                    goBack: function() {
                        window.history.back();
                    },
                    addToHistory: function(stateObj, title, url) {
                        if (stateObj) {
                            stateObj.addedByPage = true;
                        }
                        dti.routing.addToHistory(stateObj, title, url);
                    },
                    onBeforePageTransition: function(args) {
                        var defer = $.Deferred();

                        if (this.options.onBeforePageTransition) {      // perform controller behavior
                            isOkToLeavePage = this.options.onBeforePageTransition(args);
                            if (!isOkToLeavePage) {
                                return false;
                            }
                        }

                        this.isTransitioning = true;

                        if (this.isDirty()) {
                            dti.dialog.openConfirmation({
                                messageKey: "common.change.lost.confirm",
                                height: 150,
                                okHandler: function(params) {
                                    defer.resolve(true);
                                },
                                cancelHandler: function() {
                                    defer.resolve(false);
                                }
                            });
                        }
                        else {
                            defer.resolve(true);
                        }

                        return defer.promise();
                    },
                    on: function(selector, eventName, fn) {
                        $selector = $(selector);
                        $selector.off(eventName);
                        $selector.addClass("dti-event-monitored");
                        $selector.on(eventName, fn);
                    },
                    notifyOfErrors: function(errors) {
                        if (this.options.onPageError) {
                            this.options.onPageError(errors);
                        }
                        console.log(errors);
                    },
                    notifyOfEvent: function(name, args) {   // support pub/sub
                        $(window).trigger(name, args);
                    },
                    setListSource: function(name, source) {
                        this.source = this.source || {};
                        this.source[name] = source;
                    },
                    getListSource: function(name) {
                        return this.source[name];
                    }
                }
            }
        };
    })();
}


if (typeof dti.component == "undefined") {
    dti.component = (function () {

        return {
            create: function(args) {
                if (!args.page) {
                    console.error("No page defined for: ".concat(args.name));
                }

                return {
                    name: args.name,
                    type: args.type || "Component",
                    id: args.id || args.name,
                    version: args.version,
                    debug: args.debug,
                    page: args.page,
                    configArgs: args,   // capture initial args,
                    options: {},
                    unprocessedItems: {},
                    events: {
                        EVENT_AFTER_PAGE_END: "afterPageEnd.dti.utils.component".concat(".",args.name)
                    },

                    _onConfigInit: function() {
                        this.componentContext = {
                            config: {
                                defer: $.Deferred()
                            }

                        };

                        this.onBeforeConfigInit();

                        // Process configuration data
                        var $context = this.componentContext;
                        var $page = this.page;
                        var $component = this;
                        var componentName = this.name;
                        require(['configService', 'configServiceData'], function(configService){
                            var service = new configService($context);
                            service.getConfig({name: componentName, type: $component.type}, $page).then(function (data) {

                                if ($component.options.trackChanges) {
                                    $page.trackChanges = true;
                                }

                                $page.applyConfig({target: $component, page: $page}, data);

                                $component.onAfterConfigInit();

                                $context.config.defer.resolve();   // notify of ConfigInit completion
                            });

                        });

                        return this.componentContext.config.defer.promise();

                    },

                    _onComponentInit: function() {
                        var defer = this.page.pageContext.componentInitDefers[this.name];
                        this.componentContext.defer = defer;

                        var componentInitPromise = defer.promise();

                        this.page.pageContext.onPageInit.ready(this, function() {
                            this.componentContext = this.componentContext || {};

                            this.componentContext.page = this.page;

                            this.onBeforeComponentInit();

                            if (this.options.onComponentInit) {
                                this.options.onComponentInit(this, this.componentContext);
                            }

                            console.log("onComponentInit completed for: ".concat(this.name));
                        });

                        var ths = this;
                        componentInitPromise.done(function() {
                            console.log(ths.name.concat(" onComponentInit was resolved"));
                        });

                        return componentInitPromise;
                    },

                    _onComponentStart: function() {
                        this.componentContext.defer = $.Deferred();   // reset deferred.

                        this.page.pageContext.onPageStart.ready(this, function() {

                            this.onAfterComponentInit();
                            this.onBeforeComponentStart();
                            if (this.options.onComponentStart) {
                                this.options.onComponentStart(this, this.componentContext);
                            }

                        });

                        return this.componentContext.defer.promise();
                    },

                    _onComponentEnd: function() {
                        this.onBeforeComponentEnd();

                        if (this.options.onComponentEnd) {
                            this.options.onComponentEnd(this, this.componentContext);
                        }

                        this.onAfterComponentEnd();
                    },

                    configure: function(options) {
                        this.options = $.extend(this.options, options);

                        if (this.options.configSettings && !this.componentConfig) {
                            this.componentConfig = this.options.configSettings;
                        }

                        return this;
                    },
                    run: function() {
                        if (this.configArgs) {
                            this.configure(this.configArgs);
                        }

                        this.configArgs = null; // release temporary reference

                        $.when(this._onConfigInit())
                            .then(this._onComponentInit.bind(this))
                            .then(this._onComponentStart.bind(this))
                            .then(this.onAfterComponentStart.bind(this));

                    },
                    onBeforeConfigInit: function() {
                        if (this.options.onBeforeConfigInit) {
                            this.options.onBeforeConfigInit(this, this.componentContext);
                        }

                        // Add default behavior here

                        if (this.options.onBeforeConfigInitCompleted) {
                            this.options.onBeforeConfigInitCompleted(this, this.componentContext);
                        }
                    },
                    onAfterConfigInit: function() {
                        if (this.options.onAfterConfigInit) {
                            this.options.onAfterConfigInit(this, this.componentContext);
                        }

                        // default behavior
                        var eventName = "dti.component.configured".concat(".",this.name);
                        dti.eventManager.trigger(eventName, {component: this});

                        if (this.options.onAfterConfigInitCompleted) {
                            this.options.onAfterConfigInitCompleted(this, this.componentContext);
                        }
                    },
                    onBeforeComponentInit: function() {
                        if (this.options.onBeforeComponentInit) {
                            this.options.onBeforeComponentInit(this, this.componentContext);
                        }

                        // Add default behavior here
                        var ths = this;
                        var subscription = ths.events["EVENT_AFTER_PAGE_END"];
                        $(window).off(subscription).on(subscription, function() {
                            ths._onComponentEnd();
                        });

                        if (this.options.onBeforeComponentInitCompleted) {
                            this.options.onBeforeComponentInitCompleted(this, this.componentContext);
                        }
                    },
                    onAfterComponentInit: function() {
                        if (this.options.onAfterComponentInit) {
                            this.options.onAfterComponentInit(this, this.componentContext);
                        }

                        // default behavior
                        var eventName = "dti.component.initialized".concat(".",this.name);
                        dti.eventManager.trigger(eventName, {component: this});

                        if (this.options.onAfterComponentInitCompleted) {
                            this.options.onAfterComponentInitCompleted(this, this.componentContext);
                        }
                    },
                    onBeforeComponentStart: function() {
                        if (this.options.onBeforeComponentStart) {
                            this.options.onBeforeComponentStart(this, this.componentContext);
                        }

                        // default behavior
                        if (this.page) {
                            var pageContextCopy = $.extend({}, this.page.pageContext);
                            delete pageContextCopy.config;
                            delete pageContextCopy.defer;
                            $.extend(this.componentContext, pageContextCopy);
                        }
                        else {
                            console.error("No page defined for component: ".concat(this.name));
                        }

                        if (this.options.onBeforeComponentStartCompleted) {
                            this.options.onBeforeComponentStartCompleted(this, this.componentContext);
                        }
                    },
                    onAfterComponentStart: function() {
                        if (this.options.onAfterComponentStart) {
                            this.options.onAfterComponentStart(this, this.componentContext);
                        }

                        // Add default behavior here
                        // default behavior
                        this.page.autoWireWidgetSources(this.page.compilation.get(this.name));
                        if (this.options.trackChanges) {
                            this.page._changeTracking.trackChanges();
                        }
                        var eventName = "dti.component.started".concat(".",this.name);
                        dti.eventManager.trigger(eventName, {component: this});
                        console.log("onComponentStart completed for: ".concat(this.name));

                        if (this.options.onAfterComponentStartCompleted) {
                            this.options.onAfterComponentStartCompleted(this, this.componentContext);
                        }
                    },
                    onBeforeComponentEnd: function() {
                        if (this.options.onBeforeComponentEnd) {
                            this.options.onBeforeComponentEnd(this, this.componentContext);
                        }

                        // Add default behavior here

                        if (this.options.onBeforeComponentEndCompleted) {
                            this.options.onBeforeComponentEndCompleted(this, this.componentContext);
                        }
                    },
                    onAfterComponentEnd: function() {
                        if (this.options.onAfterComponentEnd) {
                            this.options.onAfterComponentEnd(this, this.componentContext);
                        }

                        // Add default behavior here
                        this.page.unapplyWidgets(this.page.compilation.get(this.name), this.type);  // apply page widgets

                        dti.candidateFinder.cleanup(this.name);

                        $(window).off(this.events["EVENT_AFTER_PAGE_END"]);

                        var eventName = "dti.component.ended".concat(".",this.name);
                        dti.eventManager.trigger(eventName, {component: this});
                        console.log("onComponentEnd completed for: ".concat(this.name));

                        if (this.options.onAfterComponentEndCompleted) {
                            this.options.onAfterComponentEndCompleted(this, this.componentContext);
                        }
                    }
                }
            },
            _registeredComponents: {},
            register: function(id, dep) {
                var component = dep.pop(); // last item is the component
                dti.component._registeredComponents[id] = {
                    dependencies: dep
                };
            },
            load: function(context, id, name) {
                dti.page.checkForDuplicates(id);

                var locator = name || id;
                this.loadComponent(context, id, locator);
            },
            loadComponent: function(context, id, locator) {
                var construct = function(constructor, args) {
                    function F() {
                        return constructor.apply(this, args);
                    }
                    F.prototype = constructor.prototype;
                    return new F();
                };

                var ths = this;
                require([locator], function(component){ // ** pull in the component; let define pull in dependencies
                    try {
                        var dependencies = dti.component._registeredComponents[id].dependencies;
                        context.page.loadView(id).then(function () {
                            ths.notify("component.view.loaded:" + id);

                            require(dependencies, function() {     // ** load component dependencies

                                // create new instances of each service
                                var args = Array.prototype.slice.call(arguments);
                                for (var i=0; i<args.length; i++) {
                                    var fn = args[i];
                                    var constr = new fn();
                                    args[i] = constr;
                                }

                                // add context to arguments
                                args.unshift(context);

                                // send to constructor
                                var componentInstance = construct(component, args);
                            });
                        });
                    }
                    catch(err) {
                        console.error(err.message);
                    }
                });
            },
            registerWindow: function(windowId) {
                this.page.registerWindow(windowId);
            },
            notify: function(notification) {
                $(window).trigger(notification);
            }
        };
    })();
}


if (typeof dti.eventManager == "undefined") {
    dti.eventManager = (function() {
        return {
            _queue: [],
            _subscribers: [],
            _lastId: 0,
            _getNextId: function() {
                return this._lastId++;
            },
            _isProcessing: false,
            _currentEvent: null,
            _processedEvents: {},
            trigger: function (eventName, args) {
                var event = {
                    id: this._getNextId(),
                    name: eventName,
                    args: args
                };

                return this.queueEvent(event);
            },
            on: function (eventName, fn) {
                if (!this._subscribers[eventName]) {
                    this._subscribers[eventName] = fn;
                    console.log("subscribed to: ".concat(eventName));

                    return $(window).on(eventName, fn);
                }
                else {
                    console.log("already a subscriptions for: ".concat(eventName));
                }

                return null;
            },
            off: function (eventName, fn) {
                this._subscribers[eventName] = fn;
                console.log("unsubscribed from: ".concat(eventName));

                return $(window).off(eventName, fn);
            },
            queueEvent: function(event) {
                var args = event.args;
                if (args) {
                    if (args.dependsOnCompletion) {
                        if (!this._processedEvents[args.dependsOnCompletion.concat(".COMPLETED")]) {
                            console.log("dti.eventManager: cancelled event: ".concat(event.name, " due to cancelled dependency: ", args.dependsOnCompletion));
                            return false;
                        }
                    }
                }

                var queue = this._queue;
                queue.push(event);
                queue.sort(function(a, b) {
                    return a.id - b.id;
                });

                if (!this._isProcessing) {
                    this.dispatchEvents();
                }

                return true;
            },
            dispatchEvents: function() {
                this._isProcessing = true;
                this._processedEvents = {};

                // send completed events out.  preserve order
                var queueItem;
                while (dti.eventManager._queue.length > 0) {
                    queueItem = dti.eventManager._queue[0];
                    if (queueItem) {
                        queueItem.isProcessing = true;
                        this._currentEvent = queueItem;

                        try {
                            console.log("started event: ".concat(queueItem.name));
                            $(window).trigger(queueItem.name, {eventItem: queueItem, args:queueItem.args});
                            console.log("completed event: ".concat(queueItem.name));
                        }
                        catch(err) {
                            console.error(err);
                            dti.message.status.closeStatusMessage();

                            dti.eventManager._isProcessing = false;
                            console.error("failed event: ".concat(queueItem.name));

                            throw err;
                        }

                        dti.eventManager._queue.shift();
                        this._currentEvent = null;

                        var suffix = ".COMPLETED";
                        if (queueItem.isCancelled) {
                            suffix = ".CANCELLED";
                        }
                        this._processedEvents[queueItem.name.concat(suffix)] = queueItem;
                    }
                }

                if (dti.eventManager._queue.length > 0) {
                    setTimeout(function() {
                        dti.eventManager.dispatchEvents();
                    }, 1000);
                }

                this._isProcessing = false;
            },
            cancelEvent: function(cancelArgs) {
                var queue = dti.eventManager._queue;

                if (cancelArgs.event) {
                    cancelArgs.event.preventDefault();
                    cancelArgs.event.stopImmediatePropagation();

                    cancelArgs.args.eventItem.isCancelled = true;
                    this.cancelDependentEvents(queue, cancelArgs.args.eventItem.name);
                }
                else if (cancelArgs.eventName) {

                    var eventsFound = $.grep(queue, function(item, index) {
                        if (item.cancelArgs.name == cancelArgs.eventName) {
                            return true;
                        }

                        return false;
                    });

                    if (eventsFound.length) {
                        var event = eventsFound[0];
                        this.cancelDependentEvents(queue, cancelArgs.eventName);
                    }

                }

                var activeEventsFound = $.grep(queue, function(item, index) {
                    if (!item.isCancelled) {
                        return true;
                    }

                    return false;
                });

                dti.eventManager._queue = activeEventsFound;

                return activeEventsFound;
            },
            cancelDependentEvents: function(queue, name) {
                var eventsFound = $.grep(queue, function(item, index) {
                    if (item.args.dependsOn == name) {
                        return true;
                    }

                    return false;
                });

                for (var i=0; i<eventsFound.length; i++) {
                    var event = eventsFound[i];
                    event.isCancelled = true
                    this.cancelDependentEvents(queue, event.name);
                }
            },
            clear: function() {
                var $window = $(window);

                for (var key in this._subscribers) {
                    $window.off(key);
                    this._subscribers[key] = null;
                }
            }
        }
    })();
}


if (typeof dti.grid == "undefined") {
    dti.grid = (function () {
        var _getRow = function(rawData, rowId) {
            var rowsFound = $.grep(rawData, function(item, index) {
                if (item.rowId == rowId) {
                    return true;
                }

                return false;
            });

            return rowsFound[0];
        };

        var _getRowByPrimaryKey = function(rawData, pk, primaryKeyId) {
            var rowsFound = $.grep(rawData, function(item, index) {
                if (item[primaryKeyId] == pk) {
                    return true;
                }

                return false;
            });

            return rowsFound[0];
        };

        var _getRowIndex = function(rawData, rowId) {
            var rowIndex;
            var rowsFound = $.grep(rawData, function(item, index) {
                if (item.rowId == rowId) {
                    rowIndex = index;
                    return true;
                }

                return false;
            });

            return rowIndex;
        };

        var _mapData = function(column, rowInfo, newValue) {
            var mapParts = column.map.split(">");
            var node = rowInfo[mapParts[0]];

            var val = newValue;
            if (newValue.hasOwnProperty("value")) {
                val = newValue["value"];
            }

            if(typeof node != "object"){
                // column's map has no ">"
                rowInfo[mapParts[0]] = val;
            } else {
                var mapPartsLength = mapParts.length;
                if (mapPartsLength > 1) {
                    for (var i = 1; i < mapPartsLength; i++) {
                        if (i < mapPartsLength - 1) {
                            node = node[mapParts[i]];
                        }
                    }
                }

                node[mapParts[mapPartsLength - 1]] = val;
            }
        };

        var _mapDisplayColumn = function(displayColumn, rowInfo, newValue) {
            var mapParts = displayColumn.map.split(">");
            var node = rowInfo[mapParts[0]];

            var lbl = newValue;
            if (newValue.hasOwnProperty("label")) {
                lbl = newValue["label"];
            }

            if(typeof node != "object"){
                // column's map has no ">"
                rowInfo[mapParts[0]] = lbl;
            } else {
                var mapPartsLength = mapParts.length;
                if (mapPartsLength > 1) {
                    for (var i = 1; i < mapPartsLength; i++) {
                        if (i < mapPartsLength - 1) {
                            node = node[mapParts[i]];
                        }
                    }
                }
                node[mapParts[mapPartsLength - 1]] = lbl;
            }
        };


        var _handleMapping = function(gridId, rowIndex, dataField, newValue, page, options){

            var selector = "#".concat(gridId);
            var gridObj = $(selector);

            var targetName = (options && options.targetName) ? options.targetName : page.name;

            var rawData = page[selector].rawData;

            var gridRow = gridObj.jqxGrid('getrowdata', rowIndex);
            var rowInfo = _getRow(rawData, gridRow.rowId);
            var rowStatus = (rowInfo) ? rowInfo.updateIndicator : undefined;
            // handle mapping
            var columnsFound = $.grep(page.compilation.get(targetName).ids[gridId].settings.columns, function(col) {
                return col.id == dataField;
            });

            var column = columnsFound[0] || {};

            if (column.map) {
                _mapData(column, rowInfo, newValue);
            }
            else {
                rowInfo[dataField] = newValue;
            }

            if (column.displayfield) {
                var displayColumnsFound = $.grep(page.compilation.get(targetName).ids[gridId].settings.columns, function(col) {
                    return col.id == column.displayfield;
                });

                var displayColumn = displayColumnsFound[0];

                _mapDisplayColumn(displayColumn, rowInfo, newValue);
            }

            if (!rowStatus || rowStatus == this.ROW_STATUS_UNMODIFIED) {
                rowInfo.updateIndicator = dti.grid.ROW_STATUS_MODIFIED;
            }
        };


        return {
            UPDATE_INDICATOR: "updateIndicator",

            ROW_STATUS_NEW: "I",
            ROW_STATUS_MODIFIED: "U", // QUESTION: Should this be "Y"?
            ROW_STATUS_DELETED: "D",
            ROW_STATUS_UNMODIFIED: undefined, // QUESTION: Should this be "N"?

            START_TEMPORARY_KEY: -30000,

            init: function(gridId, eventTarget, options) {
                var selector = "#".concat(gridId);

                if (options) {
                    if (options.page) {
                        var page = options.page;
                        page[selector] = page[selector] || {};
                        if (page[selector].rawData) {
                            this.resetUpdateIndicator(page, gridId);
                        }
                    }
                }

                var completedEventName = "bindingcomplete.dti.utils.grid";

                $(selector).off(completedEventName).on(completedEventName, function () {
                    console.log(completedEventName);
                    dti.message.status.closeProgressMessage();
                    var statusName = gridId.concat("-loading-status");
                    console.log("completed event: dti.grid: ".concat(statusName));

                    $("#".concat(statusName)).hide();
                    if (eventTarget) {

                        if (eventTarget.register) {
                            eventTarget.register("Grid", this);
                        }

                        var event = gridId.concat(":bindingcomplete");
                        $(eventTarget).trigger(event);
                    }

                });

                var startedEventName = "bindingstarted";
                var statusName = gridId.concat("-loading-status");
                console.log(statusName.concat(":",startedEventName));

                dti.message.status.displayProgressMessage({cmd: "set", messageKey: "core.grid.loading", priority:"high", additionalParms: {progressStatus: "progress-start"}});
                if (eventTarget) {
                    var event = gridId.concat(":",startedEventName);
                    console.log("started event: dti.grid: ".concat(event));
                    $(eventTarget).trigger(event);
                    console.log("completed event: dti.grid: ".concat(event));
                }

                // subscribe to errors event
                $(window).off("errors.dti.utils.grid").on("errors.dti.utils.grid", function() {
                    $("#".concat(statusName)).hide();
                });

                return this;
            },

            load: function(gridId, data, page, options) {
                var selector = "#".concat(gridId);
                page[selector] = page[selector] || {};
                page[selector].source = page[selector].source || {};
                var filteredData = setupRowIds(data);
                page[selector].rawData = filteredData || page[selector].rawData;  // initialize once
                page[selector].autoRefresh = false;  // refresh on CRUD

                options = options || {};
                page[selector].primaryKeyId = page[selector].primaryKeyId || options.primaryKeyId;
                page[selector].generateTemporaryPrimaryKey = options.generateTemporaryPrimaryKey || this.nextTempKey;

                var gridObj = $(selector);

                // Un-select the previous selected row.
                var selectedRow = gridObj.jqxGrid('getselectedrowindex');

                if (selectedRow > -1) {
                    gridObj.jqxGrid('unselectrow', selectedRow);
                }

                var dataAdapter;

                function setupRowIds(data) {
                    if (data.length && !data[0].rowId) {  // if no rowId on first row then assign automatically
                        for (var i = 0; i < data.length; i++) {
                            data[i].rowId = i + 1;
                        }
                    }

                    return data;
                }

                if (!options.showDeletedData) {
                    filteredData = $.grep(filteredData, function(item) {
                        return item.updateIndicator != dti.grid.ROW_STATUS_DELETED;
                    });
                }

                page[selector].source.localdata = filteredData;

                if(options.onSourceLoadComplete){
                    dataAdapter = new $.jqx.dataAdapter(page[selector].source, {
                        loadComplete: options.onSourceLoadComplete,
                        beforeLoadComplete: setupRowIds
                    });
                }else{
                    dataAdapter = new $.jqx.dataAdapter(page[selector].source, {
                        beforeLoadComplete: setupRowIds
                    });
                }

                gridObj.jqxGrid({source: dataAdapter});

                if (options.cellValueChangedEvent != undefined) {
                    gridObj.off("cellvaluechanged.dti.utils.grid").on('cellvaluechanged.dti.utils.grid', options.cellValueChangedEvent);
                } else {
                    // Add handle update indicator on change.
                    if (gridObj.jqxGrid('editable')) {
                        gridObj.off("cellvaluechanged.dti.utils.grid").on('cellvaluechanged.dti.utils.grid', function (event) {
                            dti.grid.handleUpdateIndicatorOnCellValueChange(event, page, gridId, options);
                        });
                    }
                }
            },

            nextTempKey: function(page, gridId) {
                var selector = "#".concat(gridId);
                if (!page[selector].lastTempKey) {
                    page[selector].lastTempKey = dti.grid.START_TEMPORARY_KEY;
                }
                else {
                    page[selector].lastTempKey += 1;
                }

                return page[selector].lastTempKey;
            },

            mapPrimaryKeys: function(args) {
                if (args.mapPrimaryKeys) {      // allow for replacement function
                    args.mapPrimaryKeys(args);
                }
                else {
                    var selector = "#".concat(args.gridId);
                    var page = args.page;
                    var data = args.data;
                    var rawData = page[selector].rawData;
                    var primaryKeyId = page[selector].primaryKeyId;
                    var newKeyId = args.newKeyId || "new_".concat(primaryKeyId);

                    for (var i=0; i<data.length; i++) {
                        var rowItem = _getRowByPrimaryKey(rawData,data[i][primaryKeyId], primaryKeyId);
                        rowItem[primaryKeyId] = data[i][newKeyId];
                    }
                }
            },

            notifyOfChanges: function(gridId) {
                var gridSelector = "#".concat(gridId);
                $(gridSelector).trigger("change", {hasGridChanged: true});   // notify the environment
            },

            notifyOfErrors: function(gridId) {
                var statusName = gridId.concat("-loading-status");
                $("#".concat(statusName)).hide();

                if (!dti.page.isReloading) {
                    var gridSelector = "#".concat(gridId);
                    $(gridSelector).trigger("errors.dti.utils");    // notify the page
                }
            },

            register: function(gridId, page, eventName) {
                // placeholder method
            },

            setupExcelExport: function(element, $state) {
                var id = element.get(0).id;
                //Current we must get the expander in the page to add the xls button into Expander title. So while loop is to find the expander
                if (document.getElementById("importToExcel_" + id) == null) {
                    var expanderElement = element.closest(".jqx-expander,.dti-window");
                    if (expanderElement.length > 0 && expanderElement.hasClass("jqx-expander")) {
                        expanderElement = $(expanderElement[0]);
                        var expanderHeaderElement = $(expanderElement).find(".jqx-expander-header");
                        if (expanderHeaderElement.length > 0) {
                            expanderHeaderElement = $(expanderHeaderElement[0]);
                            var pageName  = dti.utils.routing.getName($state);
                            var exportButton = "<span class='exportSpan' ><INPUT name=btnSaveAsCSV class='excelExportButton' id=\"importToExcel_"
                                + id + "\"  type=button  value=' Export'  onclick=\"javascript:dti.excel.saveGridAsExcel('"
                                + id + "','" + pageName + "', 'attachment');\"/></span>";
                            expanderHeaderElement.append(exportButton);
                        }
                    }
                }
            },

            setupExcelImport: function(element, $state) {
                var id = element.get(0).id;
                //Current we must get the expander in the page to add the xls button into Expander title. So while loop is to find the expander
                if (document.getElementById("importFromExcel_" + id) == null) {
                    var expanderElement = element.closest(".jqx-expander,.dti-window");
                    if (expanderElement.length > 0 && expanderElement.hasClass("jqx-expander")) {
                        expanderElement = $(expanderElement[0]);
                        var expanderHeaderElement = $(expanderElement).find(".jqx-expander-header");
                        if (expanderHeaderElement.length > 0) {
                            expanderHeaderElement = $(expanderHeaderElement[0]);
                            var pageName  = dti.utils.routing.getName($state);
                            var exportButton = "<span class='importSpan' ><INPUT name=btnImportFromExcel class='excelImportButton dti-hide' id=\"importFromExcel_"
                                + id + "\"  type=button  value=' Import'  onclick=\"javascript:dti.excel.importGridFromExcel('"
                                + id + "','" + pageName + "', 'attachment');\"/></span>";
                            expanderHeaderElement.append(exportButton);
                        }
                    }
                }
            },

            /**
             * Get the status of a row.
             *
             * @param gridId
             * @param rowId
             * @returns rowStatus: grid.ROW_STATUS_NEW, grid.ROW_STATUS_MODIFIED, grid.ROW_STATUS_DELETED, or grid.ROW_STATUS_NEW.
             */
            getRowStatus: function (gridId, rowIndex, page) {
                var selector = "#" + gridId;
                var rawData = page[selector].rawData;
                var gridObj = $(selector);

                var gridRow = gridObj.jqxGrid('getrowdata', rowIndex);
                var rowInfo = _getRow(rawData, gridRow.rowId);
                return (rowInfo) ? rowInfo.updateIndicator : undefined;
            },

            getRow: function (gridId, rowId, page) {
                var selector = "#" + gridId;
                var rawData = page[selector].rawData;
                var gridObj = $(selector);

                return _getRow(rawData, rowId);
            },

            getSelectedRow: function (gridId, rowIndex, page) {
                var selector = "#" + gridId;
                var rawData = page[selector].rawData;
                var gridObj = $(selector);

                var gridRow = gridObj.jqxGrid('getrowdata', rowIndex);
                var rowInfo = _getRow(rawData, gridRow.rowId);
                return rowInfo;
            },

            getCellText: function (gridId, rowBoundIndex, fieldId, page, targetName) {
                var selector = "#" + gridId;
                var gridObj = $(selector);
                var rawData = page[selector].rawData;
                var gridRow = gridObj.jqxGrid('getrowdata', rowBoundIndex);
                var rowInfo = _getRow(rawData, gridRow.rowId);

                // handle mapping
                var columnsFound = $.grep(page.compilation.get(targetName).ids[gridId].settings.columns, function(col) {
                    return col.id == fieldId;
                });

                var column = columnsFound[0] || {};
                var label = "";

                if (column.displayfield) {
                    var displayColumnsFound = $.grep(page.compilation.get(targetName).ids[gridId].settings.columns, function(col) {
                        return col.id == column.displayfield;
                    });

                    var displayColumn = displayColumnsFound[0];

                    var mapParts = displayColumn.map.split(">");
                    var node = rowInfo[mapParts[0]];

                    if(typeof node != "object"){
                        label = node;
                    } else {
                        var mapPartsLength = mapParts.length;
                        if (mapPartsLength > 1) {
                            for (var i = 1; i < mapPartsLength; i++) {
                                if (i < mapPartsLength - 1) {
                                    node = node[mapParts[i]];
                                }
                            }
                        }
                        label = node[mapParts[mapPartsLength - 1]];
                    }
                } else if (column.map) {
                    var mapParts = column.map.split(">");
                    var node = rowInfo[mapParts[0]];

                    if(typeof node != "object"){
                        label = node;
                    } else {
                        var mapPartsLength = mapParts.length;
                        if (mapPartsLength > 1) {
                            for (var i = 1; i < mapPartsLength; i++) {
                                if (i < mapPartsLength - 1) {
                                    node = node[mapParts[i]];
                                }
                            }
                            label = node[mapParts[mapPartsLength - 1]];
                        }
                    }
                } else {
                    label = gridRow[fieldId];
                }

                return label;
            },

            setCellValue: function (gridId, rowIndex, fieldId, value, page, options) {

                _handleMapping(gridId, rowIndex, fieldId, value, page, options);
            },

            getSelectedRowIndex: function (gridId) {
                var selector = "#" + gridId;
                var selectedRowIndex = $(selector).jqxGrid('getselectedrowindex');

                return selectedRowIndex;
            },

            setSelectedRowIndex: function (gridId, rowIndex){
                var selector = "#" + gridId;
                $(selector).jqxGrid({ selectedrowindex: rowIndex});
            },

            selectRowByIndex: function (gridId, rowIndex) {
                // TODO To be implemented.
            },

            selectRowById: function (gridId, rowId) {
                // TODO To be implemented.
            },

            /**
             * Add a new row to grid.
             * @param page
             * @param gridId
             * @param rowId
             * @param rowData
             * @param position
             * @returns {*}
             */
            addRow: function (args) {
                if (args.onBeforeAddRow) {
                    if (!args.onBeforeAddRow()) {
                        return;
                    }
                }

                var page = args.page;
                var selector = "#".concat(args.gridId);
                var rawData = page[selector].rawData;
                var autoRefresh = (typeof args.autoRefresh != "undefined") ? args.autoRefresh : page[selector].autoRefresh;

                var rowInfo = args.rowInfo;

                if (!rowInfo.rowId) {
                    rowInfo.rowId = this.nextVal(page, args.gridId);
                }
                
                var primaryKeyId = page[selector].primaryKeyId;
                if (primaryKeyId) {
                    var primaryKey = rowInfo[primaryKeyId];
                    if (!primaryKey) {
                        rowInfo[primaryKeyId] = this.nextTempKey(page, args.gridId);
                    }
                }

                rowInfo.updateIndicator = dti.grid.ROW_STATUS_NEW;

                if (args.position) {
                    var position = +(args.position);
                    switch(args.position) {
                        case "first":
                            position = 0;
                            break;
                        case "last":
                            position = rawData.length;
                    }
                    rawData.splice(position, 0, rowInfo);
                    $(selector).jqxGrid("addrow", rowInfo.rowId, rowInfo, args.position);
                }
                else {
                    rawData.unshift(rowInfo);
                    $(selector).jqxGrid("addrow", rowInfo.rowId, rowInfo, "first");
                }

                if (!args.doNotCallNotifyOfChanges) {
                    dti.grid.notifyOfChanges(args.gridId);
                }

                if (args.onBeforeLoad) {
                    args.onBeforeLoad().then(function() {
                        if (autoRefresh) {
                            dti.grid.load(args.gridId, rawData, page);
                        }

                        if (args.onAfterAddRow) {
                            args.onAfterAddRow(rowInfo);
                        }
                    })
                }
                else {
                    if (autoRefresh) {
                        dti.grid.load(args.gridId, rawData, page);
                    }

                    if (args.onAfterAddRow) {
                        args.onAfterAddRow(rowInfo);
                    }
                }
            },


            /**
             * Update a row in the grid.
             * @param page
             * @param gridId
             * @param rowId
             * @param rowData
             * @returns {*}
             */
            updateRow: function (args) {
                if (args.onBeforeUpdateRow) {
                    if (!args.onBeforeUpdateRow()) {
                        return;
                    }
                }

                var page = args.page;
                var selector = "#".concat(args.gridId);
                var rawData = page[selector].rawData;
                var autoRefresh = (typeof args.autoRefresh != "undefined") ? args.autoRefresh : page[selector].autoRefresh;

                var rowInfo = args.rowInfo;

                if (rowInfo.updateIndicator != dti.grid.ROW_STATUS_NEW) {
                    rowInfo.updateIndicator = dti.grid.ROW_STATUS_MODIFIED ;
                }

                var rawDataRow = _getRow(rawData, rowInfo.rowId);
                if (rawDataRow != rowInfo) {
                    $.extend(rawDataRow, rowInfo); // copy
                }

                if (!args.doNotCallNotifyOfChanges) {
                    dti.grid.notifyOfChanges(args.gridId);
                }

                if (args.onBeforeLoad) {
                    args.onBeforeLoad().then(function() {
                        if (autoRefresh) {
                            dti.grid.load(args.gridId, rawData, page);
                        }

                        if (args.onAfterUpdateRow) {
                            args.onAfterUpdateRow(rowInfo);
                        }
                    })
                }
                else {
                    if (autoRefresh) {
                        dti.grid.load(args.gridId, rawData, page);
                    }

                    if (args.onAfterUpdateRow) {
                        args.onAfterUpdateRow(rowInfo);
                    }
                }
            },

            /**
             * Delete the selected rows in grid.
             *
             * @param page
             * @param gridId
             */
            deleteRows: function (args) {
                if (args.onBeforeDeleteRows) {
                    if (!args.onBeforeDeleteRows()) {
                        return;
                    }
                }

                var page = args.page;
                var gridId = args.gridId;
                var selector = "#".concat(args.gridId);
                var rawData = page[selector].rawData;
                var autoRefresh = args.autoRefresh || page[selector].autoRefresh;
                var rowInfo;

                var jqxGridObj = $(selector);
                var allSelectedRowIndexArray = jqxGridObj.jqxGrid('getselectedrowindexes');
                if (allSelectedRowIndexArray.length > 0) {
                    // Open confirm deleting row dialog
                    dti.dialog.openConfirmation({
                        messageKey: "common.delete.confirm",
                        okHandler: function (parms) {
                            var totalRowCount = rawData.length;
                            /**
                             * Need to clone an array for unselectrow and deleterow.
                             * Because the allSelectedRowIndexArray point to getselectedrowindexes.
                             * The unselectrow function would change the length of the allSelectedRowIndexArray immediately.
                             * So clone is needed.
                             */
                            var tempArray = allSelectedRowIndexArray.slice(0);
                            var idsArray = new Array(tempArray.length);

                            for (var index = 0; index < tempArray.length; index++) {
                                var currentSelectedRowIndex = tempArray[index];
                                if (currentSelectedRowIndex < totalRowCount) {
                                    // Get selected row
                                    var gridRowId = jqxGridObj.jqxGrid('getrowid', currentSelectedRowIndex);
                                    idsArray[index] = gridRowId;
                                    var gridRow = jqxGridObj.jqxGrid('getrowdata', currentSelectedRowIndex);

                                    rowInfo = _getRow(rawData, gridRow.rowId);

                                    if (rowInfo.updateIndicator == dti.grid.ROW_STATUS_NEW) {
                                        var rawDataIndex = _getRowIndex(rawData, gridRow.rowId);
                                        rawData.splice(rawDataIndex, 1);
                                    }
                                    else {
                                        rowInfo.updateIndicator = dti.grid.ROW_STATUS_DELETED;
                                    }
                                }

                                //Use the row index to unselect row.
                                jqxGridObj.jqxGrid('unselectrow',currentSelectedRowIndex);
                            }

                            for (var i = 0; i < idsArray.length; i++) {
                                var uId = idsArray[i];
                                jqxGridObj.jqxGrid('deleterow', uId);

                                if (args.onAfterDeleteRow) {
                                    args.onAfterDeleteRow(rowInfo);
                                }
                            }

                            if (!args.doNotCallNotifyOfChanges) {
                                dti.grid.notifyOfChanges(args.gridId);
                            }

                            if (args.onBeforeLoad) {
                                args.onBeforeLoad().then(function() {
                                    if (autoRefresh) {
                                        dti.grid.load(args.gridId, rawData, page);
                                    }

                                    if (args.onAfterDeleteRowsCompleted) {
                                        args.onAfterDeleteRowsCompleted();
                                    }
                                })
                            }
                            else {
                                if (autoRefresh) {
                                    dti.grid.load(args.gridId, rawData, page);
                                }

                                if (args.onAfterDeleteRowsCompleted) {
                                    args.onAfterDeleteRowsCompleted();
                                }
                            }

                        }
                    });
                } else {
                    dti.message.displayWarningMessage("common.noRecordSelected");
                }

            },


            /**
             * Delete the selected rows in grid.
             *
             * @param page
             * @param gridId
             */
            deleteSingleRowNoNeedConfirm: function (args) {
                if (args.onBeforeDeleteRows) {
                    if (!args.onBeforeDeleteRows()) {
                        return;
                    }
                }

                var page = args.page;
                var selector = "#".concat(args.gridId);
                var rawData = page[selector].rawData;
                var autoRefresh = args.autoRefresh || page[selector].autoRefresh;
                var rowInfo;

                if(args.rowId) {
                    rowInfo = _getRow(rawData, args.rowId);

                    if (rowInfo.updateIndicator == dti.grid.ROW_STATUS_NEW) {
                        var rawDataIndex = _getRowIndex(rawData, args.rowId);
                        rawData.splice(rawDataIndex, 1);
                    }
                    else {
                        rowInfo.updateIndicator = dti.grid.ROW_STATUS_DELETED;
                    }

                    if (!args.doNotCallNotifyOfChanges) {
                        dti.grid.notifyOfChanges(args.gridId);
                    }

                    if (args.onBeforeLoad) {
                        args.onBeforeLoad().then(function() {
                            if (autoRefresh) {
                                dti.grid.load(args.gridId, rawData, page);
                            }

                            if (args.onAfterDeleteRowsCompleted) {
                                args.onAfterDeleteRowsCompleted();
                            }
                        })
                    }
                    else {
                        if (autoRefresh) {
                            dti.grid.load(args.gridId, rawData, page);
                        }

                        if (args.onAfterDeleteRowsCompleted) {
                            args.onAfterDeleteRowsCompleted();
                        }
                    }

                } else {
                    dti.message.displayWarningMessage("common.noRecordSelected");
                }

            },

            /**
             * The common function to handle update indicator when the value of a row is changed.
             * @param event Cell value change event.
             * @param page
             * @param gridId
             */
            handleUpdateIndicatorOnCellValueChange: function (event, page, gridId, options) {
                var eventArgs = event.args;
                var targetName = (options && options.targetName) ? options.targetName : page.name;

                if (eventArgs.datafield != this.UPDATE_INDICATOR && dti.grid.isCellValueChanged(eventArgs.value, eventArgs.oldvalue)) {

                    _handleMapping(gridId, eventArgs.rowindex, eventArgs.datafield, eventArgs.newvalue, page, options);

                    var columnsFound = $.grep(page.compilation.get(targetName).ids[gridId].settings.columns, function(col) {
                        return col.id == eventArgs.datafield;
                    });

                    var column = columnsFound[0] || {};

                    if (!column.ignorechanges) {
                        dti.grid.notifyOfChanges(gridId);
                    }

                }
            },

            /**
             * Check if the value of a cell is changed.
             * @param newValue
             * @param oldValue
             * @returns {boolean}
             */
            isCellValueChanged: function(newValue, oldValue) {
                if (newValue != undefined && newValue != null &&
                    newValue.hasOwnProperty(dti.selectOption.CODE_PROPERTY) &&
                    newValue.hasOwnProperty(dti.selectOption.LABEL_PROPERTY)) {
                    newValue = newValue[dti.selectOption.CODE_PROPERTY];
                }

                // Handle new value is empty.
                if (newValue == undefined || newValue == null || newValue === "") {
                    return !(oldValue == undefined || oldValue == null || oldValue === "");
                }

                // Handle old value is empty.
                if (oldValue == undefined || oldValue == null || oldValue === "") {
                    return !(newValue == undefined || newValue == null || newValue === "");
                }

                // Handle Date value.
                if (newValue instanceof Date && oldValue instanceof Date) {
                    return (newValue.getTime() != oldValue.getTime());
                }

                // Rest cases.
                return (newValue != oldValue);
            },

            /**
             * Get the changes of a grid.
             * @param page
             * @param gridId
             * @returns {*}
             */
            getChanges: function (page, gridId, recordProcessor) {
                var _preProcessRecords = function(records, recordProcessor) {
                    var processedRecords = [];
                    if(records){
                        for (var i = 0; i < records.length; i++) {
                            var record = records[i];
                            if (record.updateIndicator) {
                                if (recordProcessor) {
                                    processedRecords.push(recordProcessor(record));
                                }
                                else {
                                    processedRecords.push(_getRow(rawData, record.rowId));
                                }
                            }
                        }
                    }

                    return processedRecords;
                };
                var selector = "#".concat(gridId);
                page[selector] = page[selector] || {};
                var rawData = page[selector].rawData;

                return _preProcessRecords(rawData, recordProcessor);
            },

            /**
             * Check if a gird is changed.
             * @param page
             * @param gridId
             * @returns {boolean}
             */
            hasChanges: function (page, gridId) {
                return (this.getChanges(page, gridId).length > 0);
            },

            /**
             * Reset update indicators.
             * @param page
             * @param gridId
             */
            resetUpdateIndicator: function (page, gridId) {
                var selector = "#".concat(gridId);
                var rawData = page[selector].rawData || [];

                for (var i = 0; i < rawData.length; i++) {
                    if (rawData[i].updateIndicator) {
                        rawData[i].updateIndicator = undefined;
                    }
                }

                dti.grid.load(gridId, rawData, page);
            },

            unSelectRow: function(page, gridId, rowIndex) {
                $('#'.concat(gridId)).jqxGrid('unselectrow', rowIndex);
            },

            nextVal: function(page, gridId) {
                var selector = "#".concat(gridId);
                var rawData = page[selector].rawData;

                if (!rawData.length) {
                    return 1;
                }

                var maxRowId = Math.max.apply(Math,
                    rawData.map(function(rowInfo){
                        return rowInfo.rowId;
                    })
                );

                if (!isFinite(maxRowId)) {
                    return 1;
                }

                return maxRowId + 1;
            }
        }
    })();
}

if (typeof dti.dialog == "undefined") {
    dti.dialog = (function () {

        var _loadConfirmDialogDiv = function () {
            var defer = $.Deferred();
            $("#dialogContainerDiv").load(getContextPath() + getContext().getProperty("commonRoot") + "/confirmDialog.html", null, function () {
                defer.resolve(null);
            });
            return defer.promise();
        };

        return {
            CONFIRMATION_DIALOG_DEFAULT_TITLE: "Confirmation",
            CONFIRMATION_DIALOG_DEFAULT_WIDTH: 400,
            CONFIRMATION_DIALOG_DEFAULT_HEIGHT: 120,
            CONFIRMATION_DIALOG_DEFAULT_MODAL_OPACITY: 0.3,

            /**
             * Open the confirmation dialog.
             * @param parms A json object. The following are the parameters:
             * <ul>
             * <li>title               : Optional. The title of the confirmation dialog;</li>
             * <li>messageKey          : <b>Required.</b> The message key;</li>
             * <li>messageParameters   : <b>Required.</b> The message key;</li>
             * <li>message             : <b>Optional.</b> The message to be showed in the dialog; Try to use messageKey</li>
             * <li>okHandler           : <b>Required.</b> The event handle for OK button clicked;</li>
             * <li>cancelHandler       : Optional. The event handle for Cancel button clicked;</li>
             * <li>height              : Optional. The height of the confirmation dialog. The default value is 400;</li>
             *                           I'm not sure why auto-height not work for a dialog. Accept a height parameter alternatively;</li>
             * <li>width               : Optional. The width of the confirmation dialog. The default value is 120;</li>
             * <li>modalOpacity        : Optional. The opacity of the confirmation dialog.The default value is 0.3.
             *                           You can set it to 0.01, if you don't want the background be grey;</li>
             * </ul>
             */
            openConfirmation: function(parms) {

                var messageKey = parms.messageKey;
                var messageParameters = parms.messageParameters;

                var loadPromise =  _loadConfirmDialogDiv();
                var messagePromise = dti.message.getMessage(messageKey, messageParameters);

                $.when(messagePromise, loadPromise).done(function(messageObj){

                    var confirmDialog = $("#dialogContainerDiv .dialog");

                    var OKButton = $("#confirmDialogOK");
                    var cancelButton = $("#confirmDialogCancel");

                    // Unbind and bind event.
                    OKButton.unbind('click').on('click', function () {
                        parms.okHandler(parms);
                    });
                    cancelButton.unbind('click').on('click', function () {
                        if (parms.hasOwnProperty("cancelHandler")) {
                            parms.cancelHandler(parms);
                        }
                    });

                    confirmDialog.on('close', function (event) {
                        confirmDialog.remove();
                    });

                    // Set dialog title.
                    if (parms.hasOwnProperty("title")) {
                        confirmDialog.find(".dialog-title").html(parms.title);
                    } else {
                        confirmDialog.find(".dialog-title").html(dti.dialog.CONFIRMATION_DIALOG_DEFAULT_TITLE);
                    }

                    // Set dialog message.
                    confirmDialog.find(".dialog-contents").html("<p>" + messageObj.htmlMessage + "</p>");

                    // Get height and width of the dialog.
                    var width = dti.dialog.CONFIRMATION_DIALOG_DEFAULT_WIDTH;
                    var height = dti.dialog.CONFIRMATION_DIALOG_DEFAULT_HEIGHT;

                    if (parms.hasOwnProperty("width")) {
                        width = parms.width;
                    }
                    if (parms.hasOwnProperty("height")) {
                        height = parms.height;
                    }

                    if (parms.hasOwnProperty("labels")) {
                        var labels = parms.labels;
                        for (name in labels) {
                            $("#".concat(name)).text(labels[name]);
                        }
                    }

                    // Get modal opacity
                    var modalOpacity = dti.dialog.CONFIRMATION_DIALOG_DEFAULT_MODAL_OPACITY;
                    if (parms.hasOwnProperty("modalOpacity")) {
                        modalOpacity = parms.modalOpacity;
                    }

                    confirmDialog.jqxWindow({
                        height: height, width: width, resizable: false, theme: '', isModal: true, modalOpacity: modalOpacity,
                        okButton: OKButton,
                        cancelButton: cancelButton,
                        initContent: function () {
                            cancelButton.focus();
                        }
                    });

                    confirmDialog.jqxWindow("show");

                });

            },

            /**
             * Open the dialog change lost confirm dialog.
             * @param parms
             */
            openChangesLostConfirmation: function(parms) {
                // TODO Need to be enhanced to support handling page form fields.
                if (parms["page"].hasChanges()) {
                    // Add change lost message.
                    parms.messageKey = "common.change.lost.confirm";
                    parms.width = 450;

                    dti.dialog.openConfirmation(parms);
                } else {
                    parms.okHandler(parms);
                }
            }
        };
    })();
}

if (typeof dti.comboBox == "undefined") {
    dti.comboBox = (function () {
        return {
            getSelectedValues : function(comboBoxId) {
                // TODO How about entered text?
                var values = [];
                var items = $("#" + comboBoxId).jqxComboBox('getSelectedItems');
                if (items) {
                    $.each(items, function () {
                        values.push(this.value);
                    });
                }

                return values;
            },

            getSelectedValuesStr : function(comboBoxId) {
                // TODO How about entered text?
                var valuesStr = "";
                var items = $("#" + comboBoxId).jqxComboBox('getSelectedItems');
                if (items) {
                    $.each(items, function () {
                        if (valuesStr != "") {
                            valuesStr += ",";
                        }
                        valuesStr += this.value;
                    });
                }
                return valuesStr;
            }
        };
    })();
}

if(typeof dti.data == "undefined"){
    dti.data = (function(){
        var by = function(name)
        {
            return function(o, p)
            {
                var a, b;
                if (typeof o === "object" && typeof p === "object" && o && p)
                {
                    a = o[name];
                    b = p[name];
                    if (a === b) {return 0;}
                    if (typeof a === typeof b) { return a < b ? -1 : 1;}
                    return typeof a < typeof b ? -1 : 1;
                }
                else {throw ("error"); }
            }
        };
        return{
            sort: function(object, name){
                return object.sort(by(name));
            }
        }
    })();
}

if (typeof dti.dataFormat == "undefined") {
    dti.dataFormat = (function () {
        return {
            DEFAULT_DATE_FORMAT : "MM/dd/yyyy",
            XML_DATE_FORMAT : "yyyy-MM-dd",
            DISPLAY_DATE_FORMAT : 'MM/dd/yyyy',
            DATE_ONLY_FORMAT : 'yyyy-MM-dd',

            formatDate: function(date, dateFormat) {
                if (date == null) {
                    return "";
                }

                if (dateFormat == undefined || dateFormat == null) {
                    dateFormat = dti.dataFormat.DEFAULT_DATE_FORMAT;
                }

                return $.jqx.dataFormat.formatDate(date, dateFormat);
            },

            formatXmlDate: function(date) {
                return dti.dataFormat.formatDate(date, dti.dataFormat.XML_DATE_FORMAT);
            },

            formatDateStr: function(dateStr, dateFormat) {
                if (dateStr == null || dateStr == "") {
                    return null;
                }
                var date = dti.dataFormat.parseXmlDate(dateStr);

                return dti.dataFormat.formatDate(date, dateFormat);
            },

            getDateOnly: function(date, dateFormat) {
                if (dateFormat == undefined || dateFormat == null) {
                    return dti.dataFormat.formatDate(date, dti.dataFormat.DATE_ONLY_FORMAT);
                }

                return dti.dataFormat.formatDate(date, dateFormat);
            },

            parseDate: function(dateStr, dateFormat) {
                if (dateStr == null || dateStr == "") {
                    return null;
                }

                if (dateFormat == undefined || dateFormat == null || dateFormat == "") {
                    dateFormat = dti.dataFormat.DEFAULT_DATE_FORMAT;
                }

                return $.jqx.dataFormat.parsedate(dateStr, dateFormat);
            },

            parseXmlDate: function(dateStr) {
                return dti.dataFormat.parseDate(dateStr, dti.dataFormat.XML_DATE_FORMAT);
            }
        }
    })();
}

if (typeof dti.selectOption == "undefined") {
    dti.selectOption = (function(){
        return {
            DEFAULT_SELECT_OPTION_CODE: "",
            DEFAULT_SELECT_OPTION_LABEL: "--",
            CODE_PROPERTY: "value",
            LABEL_PROPERTY: "label",

            isEmptyValue: function(value, emptyValue) {
                if (value.hasOwnProperty(this.CODE_PROPERTY)) {
                    value = value[this.CODE_PROPERTY];
                }

                if (emptyValue == undefined) {
                    emptyValue = this.DEFAULT_SELECT_OPTION_CODE;
                }

                if (value == undefined || value == null || value == "" || value == emptyValue) {
                    return true;
                }

                return false;
            },

            getDefaultOption: function() {
                var selectOption = {};
                selectOption[this.CODE_PROPERTY] = this.DEFAULT_SELECT_OPTION_CODE;
                selectOption[this.LABEL_PROPERTY] = this.DEFAULT_SELECT_OPTION_LABEL;

                return selectOption;
            },

            computeParamsURI : function (params) {
                var paramsURI = "";
                if (params != undefined) {
                    if (typeof params == 'string') {
                        paramsURI += "&params=" + encodeURIComponent(params);
                    } else {
                        for (var i = 0; i < params.length; i++) {
                            paramsURI += "&params=" + encodeURIComponent(params[i]);
                        }
                    }
                }

                return paramsURI;
            }
        };
    })();
}

if (typeof dti.ui == "undefined") {
    dti.ui = (function () {
        var innerGetField = function (fieldId) {
            var field = $("#" + fieldId);
            return field;
        };
        return {
            getField: function (fieldId) {
                return innerGetField(fieldId);
            },
            getComboBoxValue: function (fieldId) {
                var values = [];
                var items = innerGetField(fieldId).jqxComboBox('getSelectedItems');
                if (items.length == 0) {
                    items = innerGetField(fieldId).jqxComboBox('getCheckedItems');
                }
                if (items) {
                    $.each(items, function (index) {
                        values[index] = this.value;
                    });
                }
                return values;
            },
            getComboBoxEntry: function (fieldId) {
                var typedInInput = innerGetField(fieldId + " input").val().trim()
                return typedInInput;
            },
            getInputFieldValue: function (fieldId) {
                var value = innerGetField(fieldId).val();
                return value;
            },
            getDateFieldValue: function (fieldId) {
                var value = dti.dataFormat.getDateOnly(innerGetField(fieldId).jqxDateTimeInput('value'));
                return value;
            },
            getDropDownFieldValue: function (fieldId) {
                var item = innerGetField(fieldId).jqxDropDownList('getSelectedItem');
                if (!item) {
                    console.log("Warning: Value is empty for ".concat(fieldId, ". "));
                    return "";
                }

                return item.value;
            },
            getDropDownFieldValueAsNumber: function (fieldId) {
                var value = dti.ui.getDropDownFieldValue(fieldId);

                if (value == "") {
                    return 0;
                }

                return value;
            },
            getCheckBoxValue: function (fieldId) {
                var checked = innerGetField(fieldId).jqxCheckBox('checked');
                return checked;
            },
            clearComboBoxSelectedItems: function (fieldId) {
                innerGetField(fieldId).jqxComboBox('clearSelection');
                innerGetField(fieldId).jqxComboBox('uncheckAll');
            },
            selectComboBoxItemByValue: function (fieldId, value) {
                var fieldObject = innerGetField(fieldId);
                var item = fieldObject.jqxComboBox('getItemByValue', value);
                fieldObject.jqxComboBox('selectItem', item);
            },
            selectDropDownListItemByValue: function (fieldId, value) {
                var fieldObject = innerGetField(fieldId);
                var item = fieldObject.jqxDropDownList('getItemByValue', value);
                fieldObject.jqxDropDownList('selectItem', item);
            },
            setCheckboxOn: function (fieldId) {
                var checked = $("#" + fieldId).jqxCheckBox('checked');
                if(!checked){
                    $("#" + fieldId).jqxCheckBox('check');
                }
            },
            setCheckboxOff: function (fieldId) {
                var checked = $("#" + fieldId).jqxCheckBox('checked');
                if(checked){
                    $("#" + fieldId).jqxCheckBox('uncheck');
                }
            },
            getRadioBoxValue: function (fieldId) {
                var checked = innerGetField(fieldId)[0].checked;
                return checked;
            },
            setRadioBoxOn: function (fieldId) {
                var checked = innerGetField(fieldId).checked;
                if(!checked){
                    $("#" + fieldId).prop('checked', true);
                }
            },
            setRadioBoxOff: function (fieldId) {
                var checked = innerGetField(fieldId).checked;
                if(checked){
                    $("#" + fieldId).prop('checked', false);
                }
            },
            setDateFieldValue: function (fieldId,value) {
                innerGetField(fieldId).jqxDateTimeInput('value', dti.dataFormat.parseXmlDate(value));
            },
            getJoinValue: function (arrayObject, propertyName, allowDuplicate) {
                var list = [];
                for (var i = 0; i < arrayObject.length; i++) {
                    var element = arrayObject[i];
                    if (propertyName.indexOf('.') != -1) {
                        var names = propertyName.split(".");
                        value = element;
                        for (var j = 0; j < names.length; j++) {
                            value = value[names[j]];
                        }
                    } else {
                        value = element[propertyName];
                    }
                    var value = eval("element." + propertyName);
                    if (allowDuplicate) {
                        list.push(value)
                    } else {
                        if (list.indexOf(value) == -1) {
                            list.push(value)
                        }
                    }
                }
                var joinValue = list.join(", ");
                return joinValue;
            },
            disableButton: function (buttonId) {
                $("#" + buttonId).jqxButton({disabled: true});
            },
            enableButton: function (buttonId) {
                $("#" + buttonId).jqxButton({disabled: false});
            },
            hide: function (id) {
                $("#" + id).show();
            },
            show: function (id) {
                $("#" + id).hide();
            },
            setInputFieldValue: function (fieldId, fieldValue) {
                innerGetField(fieldId).val(fieldValue);
            }
        };
    })();
}


if (typeof dti.window == "undefined") {
    dti.window = (function() {
        return {
            init: function (windowId, page, initArgs) {
                if (page && !page.isWindowRegistered(windowId)) {
                    page.registerWindow(windowId, initArgs);
                }
            },
            open: function(windowId, page, openArgs) {
                dti.window.init(windowId, page, openArgs);

                var selector = "#".concat(windowId);
                var $selector = $(selector);
                $selector.removeClass("invisible");         // backward-compatible support
                $selector.removeClass("dti-hide");
                $selector.jqxWindow('open');

                // open at a location
                if (openArgs) {
                    if (openArgs.openNear) {
                        var offset = $(openArgs.openNear.selector).offset();
                        $selector.css("top",offset.top + 24);
                        $selector.css("left",offset.left);
                    }

                    if (openArgs.aboveThisElement) {
                        var thatZindex = $("#".concat(openArgs.aboveThisElement)).css("z-index");
                        var zIndex = (thatZindex == "auto") ? 99999 : +(thatZindex) + 1000;

                        $selector.css("z-index", zIndex);
                    }

                    if (openArgs.zIndex) {
                        $selector.css("z-index", openArgs.zIndex);
                    }
                }

            },
            close: function(windowId, page) {
                var selector = "#".concat(windowId);
                $(selector).jqxWindow('close');

                if (page) {
                    page.resetDirty(selector);
                }
            },
            destroy: function(windowId) {
                try {
                    $("#".concat(windowId)).jqxWindow('destroy');
                }
                catch (ex) {
                    console.log("Window: ".concat(windowId," has already been destroyed.  ",ex.message));
                }
            }
        }
    })();
}


if (typeof dti.button == "undefined") {
    dti.button = (function() {
        return {
            DISABLED_DURATION: 1000,    // milliseconds
            wire: function (selector, page) {
                // Look for any buttons by class
                $(selector).each(function() {
                    var ths = $(this);
                    var id = ths.prop("id");
                    ths.on("click.dti",

                        function() {
                            // disable button
                            $this = $(this);
                            var isImmediateButton = $this.hasClass("dti-immediate-button");
                            var isDisabled = $this.data("isDisabled");
                            if (!isDisabled) {
                                if (!isImmediateButton) {
                                    $this.data("isDisabled", true);
                                }

                                var buttonData = {};
                                var isToggleButton = $this.hasClass("dti-toggle-button");
                                if (isToggleButton) {
                                    var deferButton = $.Deferred();
                                    buttonData.deferButton = deferButton;

                                    var promise = deferButton.promise();
                                    promise.done(function() {
                                        $this.data("isDisabled", false);
                                    });
                                }

                                var id = $this.prop("id");
                                page.buttonClick($this, page[id], buttonData);

                                if (!isToggleButton && !isImmediateButton) {
                                    setTimeout(function(ths) {
                                        ths.data("isDisabled", false);
                                    }, dti.button.DISABLED_DURATION, $this);
                                }
                            }

                        }
                    );    // ID name must match function name
                });
            },
            unwire: function(selector, page) {
                // Look for any buttons by class
                $(selector).each(function() {
                    var ths = $(this);
                    var id = ths.prop("id");
                    ths.off("click.dti", page[id]);     // ID name must match function name
                    ths.removeData();                   // clear all data attributes (including jqx related)
                });
            }
        }
    })();
}


if (typeof dti.logging == "undefined") {
    dti.logging = (function() {
        return {
            entries: [],
            addEntry: function(entryArgs) {

                function interrogateArrayElements(arr) {
                    var outMsg = "";

                    for (var j=0; j<arr.length; j++) {
                        var item = arr[j];
                        if (item.target) {
                            continue; // skip jquery event
                        }
                        else if (item.status && item.readyState) {
                            outMsg = outMsg.concat("\t status code: ", item.status, "\n");
                        }
                        else if (item.uri) {
                            outMsg = outMsg.concat("\t URI: ", item.uri, "\n");
                        }
                        else {
                            outMsg = outMsg.concat(item, "\n");
                        }
                    }

                    return (outMsg.length) ? outMsg.concat("\n") : outMsg;
                }

                var newEntry = entryArgs.entry;
                var msg = "";
                for (var i=0; i<newEntry.length; i++) {
                    var item = newEntry[i];
                    var entryType = $.type(item);
                    switch(entryType) {
                        case "array":
                            msg = interrogateArrayElements(item);
                            break;
                        default:
                            msg = msg.concat(item, "\n");
                            break;
                    }
                }

                this.entries.push(msg);
            },
            log: function(entry) {
                this.addEntry({entry: entry, type: "log"});
            },
            warn: function(entry) {
                this.addEntry({entry: entry, type: "warn"});
            },
            error: function(entry) {
                this.addEntry({entry: entry, type: "error"});
            },
            clear: function() {
                this.entries = [];
            }
        }
    })();

}

if (typeof dti.errors == "undefined") {
    dti.errors = (function() {
        return {
            ERROR_PARAMS: { // enum
                TARGET: 0,
                TEMPLATE: 1,
                ERROR_OBJECT: 2,
                ERROR_TYPE: 3,
                ERROR_MESSAGE: 4
            },
            stackTrace: {},
            errorInfo: {},
            isWired: false,
            handleFailure: function(failArgs) {
                return function(jqxhr, type, thrownError) {
                    var args = Array.prototype.slice.call(arguments);
                    if (failArgs) {
                        args.unshift(failArgs);
                    }
                    else {
                        args.unshift({messageKey: "core.error"});
                    }

                    if (failArgs) {
                        if (failArgs.onError) {
                            failArgs.onError.apply(this, args);
                        }
                    }

                    if (!dti.errors.isWired) {
                        var settings = failArgs.settings || {};

                        var errorInfo = {
                            type: "dti.errorInfo",
                            category: "ajax",
                            url: settings.url,
                            messageKey: "core.error",
                            messageParams: [jqxhr.status + " - " + jqxhr.statusText],
                            responseText: jqxhr.responseText,
                            stackTrace: dti.errors.getStackTraceFor(settings.url),
                            timeStamp: new Date(),
                            publicUser: dti.applicationContext.getProperty("publicUser"),
                            additionalInfo: {
                                jqxhr: jqxhr,
                                settings: settings,
                                thrownError: thrownError
                            }
                        };

                        dti.errors.notifyOfErrors(errorInfo);
                    }

                }.bind(this);
            },
            notifyOfErrors: function(errors) {
                $(window).trigger("errors.dti.utils", errors);
            },
            addStackTrace: function(key, stackTrace) {
                dti.errors.stackTrace[key] = stackTrace;
            },
            getStackTraceFor: function(key) {
                return dti.errors.stackTrace[key];
            },
            addErrorInfo: function(key, errorInfo) {
                dti.errors.errorInfo[key] = errorInfo;
            },
            getErrorInfo: function(key) {
                return dti.errors.errorInfo[key];
            },
            clear: function() {
                dti.errors.stackTrace = {};
                dti.errors.errorInfo = {};
            }
        }
    })();

    // Handle global uncaught errors
    if (window.onerror) {
        window.oldOnerror = window.onerror;
    }
    window.onerror = function(msg, url, line, col, error) {
        var partiallySupported = "".concat((col) ? ", column: ".concat(col) : "");

        console.error("Error: ".concat(msg, ", url: ", url, ", line: ", line, partiallySupported));

        if (error) {
            console.log("Stack Trace: ".concat(error.stack));
        }

        if (window.oldOnerror) {
            return window.oldOnerror(msg, url, line, col, error);
        }

        var stackTraceLines = [];

        if (error && error.stack) {
            stackTraceLines = error.stack.split("\n");
            for (var i = 0; i < stackTraceLines.length; i++) {
                stackTraceLines[i] = stackTraceLines[i]
                    .replace("Function.","")
                    .replace("Object.","");
            }
        }

        var errorInfo = {
            type: "dti.errorInfo",
            category: "system",
            url: url,
            messageKey: "core.error",
            messageParams: [msg],
            responseText: "",
            stackTrace: stackTraceLines.join("\n"),
            timeStamp: new Date(),
            publicUser: dti.applicationContext.getProperty("publicUser"),
            additionalInfo: {
                line: line,
                col: col,
                url: url,
                thrownError: error
            }
        };

        dti.errors.notifyOfErrors(errorInfo);

        return false;   // false = don't prevent the normal browser error handling
    };

    require(['jquery'], function(){
        // catch global AJAX errors
        $(document).ajaxError(function( event, jqxhr, settings, thrownError ) {
            var errorInfo = {
                type: "dti.errorInfo",
                category: "ajax",
                url: settings.url,
                messageKey: "core.error",
                messageParams: [jqxhr.status + " - " + jqxhr.statusText],
                responseText: jqxhr.responseText,
                stackTrace: dti.errors.getStackTraceFor(settings.url),
                data: settings.data,
                timeStamp: new Date(),
                publicUser: dti.applicationContext.getProperty("publicUser"),
                additionalInfo: {
                    event: event,
                    jqxhr: jqxhr,
                    settings: settings,
                    thrownError: thrownError
                }
            };

            dti.errors.notifyOfErrors(errorInfo);
        });

        $.oldAjax = $.ajax;
        $.ajax = function(url, options) {       // override $.ajax
            if (dti.page.sessionHandler) {
                if (!dti.page.sessionHandler.isLoggedIn()) {
                    return $.Deferred();
                }
            }

            var err = new Error();
            if (err.stack) {        // NOTE: no stack in IE in this case
                var stackTraceLines = err.stack.split("\n");        // remove fake error line
                if (stackTraceLines[0].indexOf("Error") > -1) {
                    stackTraceLines.shift();
                }

                for (var i = 0; i < stackTraceLines.length; i++) {
                    stackTraceLines[i] = stackTraceLines[i]
                        .replace("Function.","")
                        .replace("Object.","");
                }

                dti.errors.addStackTrace(url.url, stackTraceLines.join("\n"));  // capture stack trace in case an error occurs
            }
            else {
                var stackTraceLines = [];

                try {
                    var a = b;  // force fake error
                }
                catch (err) {
                    var stackTraceLines = err.stack.split("\n");
                    if (stackTraceLines[0].indexOf("Error") > -1) {     // remove fake error line
                        stackTraceLines.shift();
                    }

                    for (var j = 0; i < stackTraceLines.length; j++) {
                        stackTraceLines[j] = stackTraceLines[j]
                            .replace("Function.","")
                            .replace("Object.","");
                    }
                }

                dti.errors.addStackTrace(url.url, stackTraceLines.join("\n"));  // capture stack trace in case an error occurs
            }

            if(!$.oldAjax){
                $.oldAjax=jQuery.ajax;
            }
            return $.oldAjax(url, options);
        };


        dti.errors.isWired = true;


        // get system messages
        if (typeof $ != "undefined" && typeof dti.applicationContext != "undefined") {
            // load core messages
            if (typeof dtiCoreErrorTemplate != "undefined") {
                dti.message.addLocalMessage("core.error", {htmlMessage: dtiCoreErrorTemplate});
                dti.message.addLocalMessage("core.service.error", {htmlMessage: dtiCoreServiceErrorTemplate});
            }
            else {
                dti.message.getMessage("core.error").then(function(data) {
                        dti.message.getMessage("core.service.error");
                    }
                );
            }
        }

        if (!dti.message.isMessageLoadedLocally("appException.unexpected.error")) {
            dti.message.getAllMessagesFromServer();
        }

        $(window).off("message.status.dti.utils.message").on("message.status.dti.utils.message", function(event, messageArgs) {
            dti.message.status.processStatusMessage(messageArgs);
        });

    });

}


if (typeof dti.candidateFinder == "undefined") {
    dti.candidateFinder = (function() {
        return {
            _owners: {},
            DEFAULT_SEARCH_FOR_ALL: "",
            getOwner: function(ownerName) {
                if (!this._owners[ownerName]) {
                    this._owners[ownerName] = {};
                }

                return this._owners[ownerName];
            },
            isCandidateIncluded: function(itemValue, keyToCheck) {
                var re = new RegExp(".*".concat(keyToCheck, ".*"), "ig");
                return re.test(itemValue);
            },
            findCandidatesInCache: function(data, cacheKey, onCompare) {
                var rowsFound = $.grep(data, onCompare);

                return rowsFound;
            },
            findCandidates: function(serviceFn, serviceFnArgs, options) {
                var defer = $.Deferred();

                var owner;

                function getCachedDataFromSource(defer, cacheKey, newServiceFnArgs, options) {
                    serviceFn.apply(null, newServiceFnArgs).then(function (data) {
                        if (options.cache && options.cache.shouldCache) {
                            dti.cache.setItem(options.ownerName, dti.candidateFinder.DEFAULT_SEARCH_FOR_ALL, data);
                        }

                        var filteredData = dti.candidateFinder.findCandidatesInCache(data, cacheKey, options.cache.onCompare);

                        if (filteredData.length) {
                            return defer.resolve(filteredData);
                        }
                    });
                }

                function getDataFromSource(defer, cacheKey, newServiceFnArgs,  options) {
                    serviceFn.apply(null, newServiceFnArgs).then(function (data) {
                        return defer.resolve(data);
                    });
                }

                if (options) {
                    if (options.cache && options.cache.shouldCache) {
                        owner = dti.candidateFinder.getOwner(options.ownerName);
                        var newServiceFnArgs = [];
                        newServiceFnArgs.push(dti.candidateFinder.DEFAULT_SEARCH_FOR_ALL);

                        var cacheKey = serviceFnArgs[0] || "";
                        cacheKey = cacheKey.toString();

                        for (var i=1; i<serviceFnArgs.length; i++) {
                            newServiceFnArgs.push(serviceFnArgs[i]);
                        }

                        var item = dti.cache.getItem(options.ownerName, cacheKey);
                        if (item) {
                            return defer.resolve(item);
                        }

                        // search for the default cache
                        var item = dti.cache.getItem(options.ownerName, dti.candidateFinder.DEFAULT_SEARCH_FOR_ALL);
                        if (item) {   // search though default cache
                            var filteredData = dti.candidateFinder.findCandidatesInCache(item, cacheKey, options.cache.onCompare);

                            if (filteredData.length) {
                                return defer.resolve(filteredData);
                            }

                            newServiceFnArgs[0] = cacheKey;
                            getDataFromSource(defer, cacheKey, newServiceFnArgs, options);
                            return defer.promise();
                        }
                        else {
                            getCachedDataFromSource(defer, cacheKey, newServiceFnArgs, options);
                            return defer.promise();
                        }
                    }
                }
                else {
                    getDataFromSource(defer, "", serviceFnArgs, options);
                }

                return defer.promise();
            },
            getRecentChoices: function(ownerName, options) {
                var options = options || {};
                var choices = [];

                var recentChoices = dti.cache.getItem(ownerName, "recentChoices");
                if (!recentChoices) {
                    dti.cache.setItem(ownerName, "recentChoices", {});
                }
                else {
                    for (var key in recentChoices){
                        choices.push(recentChoices[key]);
                    }

                    if (options.onCompare) {
                        choices = choices.sort(options.onCompare);
                    }

                }

                return choices;
            },
            addRecentChoice: function(ownerName, key, entry) {
                var entries = dti.cache.getItem(ownerName, "recentChoices");
                if (entries) {
                    entries[key] = entry;
                }
                else {
                    entries = {};
                    entries[key] = entry;
                    dti.cache.setItem(ownerName, "recentChoices", entries);
                }
            },
            cleanup: function(ownerName) {
                if (ownerName) {
                    var item = dti.cache.removeItems(ownerName);

                    return item;
                }
                else {
                    this._owners = {};
                    console.log("dti.candidateFinder all cache cleared");
                }

            }
        }
    })();

}


if (typeof dti.cache == "undefined") {
    dti.cache = (function() {
        return {
            _protected: {
                _cacheOwners: {},
                _getOwner: function(ownerName) {
                    if (!this._cacheOwners[ownerName]) {
                        this._cacheOwners[ownerName] = {};
                    }

                    return this._cacheOwners[ownerName];
                },
                _getCache: function(ownerName) {
                    var owner = this._getOwner(ownerName);
                    if (!owner.cache) {
                        owner.cache = {};
                    }

                    return owner.cache;
                }
            },
            cacheHandlers: {
                sessionHandler: {
                    _protected: {
                        _getCache: function(ownerName) {
                            var cacheKey = ownerName.concat(".cache");
                            var storedCache = sessionStorage[cacheKey];
                            if (!storedCache) {
                                return {};
                            }

                            var cache = JSON.parse(storedCache);
                            return cache;
                        }
                    },
                    getItem: function(ownerName, key) {
                        var cache = this._protected._getCache(ownerName);

                        return cache[key];
                    },
                    setItem: function(ownerName, key, value) {
                        var cacheKey = ownerName.concat(".cache");
                        var cache = this._protected._getCache(ownerName);

                        cache[key] = value;

                        sessionStorage[cacheKey] = JSON.stringify(cache);

                        return cache[key];
                    },
                    removeItem: function(ownerName, key) {
                        var cacheKey = ownerName.concat(".cache");
                        var cache = this._protected._getCache(ownerName);

                        var item = cache[key];

                        if (item) {
                            delete cache[key];
                        }

                        sessionStorage[cacheKey] = JSON.stringify(cache);

                        return item;
                    },
                    removeItems: function(ownerName) {
                        var cacheKey = ownerName.concat(".cache");
                        var cache = this._protected._getCache(ownerName);

                        if (cache) {
                            delete sessionStorage[cacheKey];
                        }

                        return cache;
                    }
                }

            },
            register: function(args) {
                if (args.ownerName) {
                    var owner = this._protected._getOwner(args.ownerName);
                    if (args.settings) {
                        owner.settings = $.extend(true, {}, args.settings);
                    }
                }
            },
            getItem: function(ownerName, key) {
                var owner = this._protected._getOwner(ownerName);
                if (owner.settings) {
                    switch (owner.settings.duration) {
                        case "session":
                            return this.cacheHandlers.sessionHandler.getItem(ownerName, key);
                            break;
                    }
                }

                var cache = this._protected._getCache(ownerName);

                return cache[key];
            },
            setItem: function(ownerName, key, value) {
                var owner = this._protected._getOwner(ownerName);
                if (owner.settings) {
                    switch (owner.settings.duration) {
                        case "session":
                            return this.cacheHandlers.sessionHandler.setItem(ownerName, key, value);
                            break;
                    }
                }

                var cache = this._protected._getCache(ownerName);

                cache[key] = value;

                return cache[key];
            },
            removeItem: function(ownerName, key) {
                var owner = this._protected._getOwner(ownerName);
                if (owner.settings) {
                    switch (owner.settings.duration) {
                        case "session":
                            return this.cacheHandlers.sessionHandler.removeItem(ownerName, key);
                            break;
                    }
                }

                var cache = this._protected._getCache(ownerName);

                var item = cache[key];

                if (item) {
                    delete cache[key];
                }

                return item;
            },
            removeItems: function(ownerName) {
                var owner = this._protected._getOwner(ownerName);
                if (owner.settings) {
                    switch (owner.settings.duration) {
                        case "session":
                            return this.cacheHandlers.sessionHandler.removeItems(ownerName);
                            break;
                    }
                }

                var owner = this._protected._getOwner(ownerName);

                var cache = owner.cache;

                if (cache) {
                    owner.cache = {};
                }

                return cache;
            }
        }
    })();

}


if (typeof dti.task == "undefined") {
    dti.task = (function () {
        return {
            _protected: {
            },
            /**
             * waits until a condition
             * @param waitArgs
             *          onCondition: [required] a condition function to call that will wait if returns false
             *          maxAttempts: [required] the maximum number of attempts to wait for (-1 = infinite)
             *          interval: [required] the milliseconds to wait until between checks of the condition
             *          onWait: a function to call during the wait period
             *          onConditionMet: a function to call when condition was met
             *          onReachedMaxAttempts: al function to call when the number of attempts have reached the maximum
             *          name: the name of the task
             */
            waitUntil: function (waitArgs) {
                waitArgs.attempt = waitArgs.attempt || 0;

                waitArgs.attempt++;

                if (waitArgs.maxAttempts == -1 || waitArgs.attempt <= waitArgs.maxAttempts) {
                    if (!waitArgs.onCondition()) {
                        setTimeout(function () {
                            if (!waitArgs.onCondition(attempt, waitArgs.name)) {
                                if (waitArgs.onWait) {
                                    var attempt = waitArgs.attempt;
                                    waitArgs.onWait(attempt, waitArgs.name);
                                }
                                dti.task.waitUntil(waitArgs);
                            }
                            else {
                                if (waitArgs.onConditionMet) {
                                    waitArgs.onConditionMet(waitArgs.attempt, waitArgs.name);
                                }
                            }
                        }, waitArgs.interval)
                    }
                    else {
                        if (waitArgs.onConditionMet) {
                            waitArgs.onConditionMet(waitArgs.attempt, waitArgs.name);
                        }
                    }
                }
                else {
                    if (waitArgs.onReachedMaxAttempts) {
                        waitArgs.onReachedMaxAttempts(waitArgs.attempt, waitArgs.name);
                    }
                }


            }
        }
    })();

}