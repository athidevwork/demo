// Add domain specific methods and overrides here
//dti.ui.newMethod = function() {...};


//var productURLArray = ["product", "/:productId", "filing"];
//var contentURLArray = ["contentVersion", "/:contentId", "/isoErcUpload", "/CoverageDetails"];
//var maintenanceURLArray = ["/UserSetup", "/coverageCategory"];

var utilitiesURLArray = ["/lookupcodes", "/systemparameters"];
var factorsURLArray = ["/algorithms", "/algorithmDetails", "/factors"];

$(window).off("routing.dti.utils").on("routing.dti.utils", function (event, currentRoute) {
    if (currentRoute && currentRoute.url) {
        var highlightId = "products";
        var requestPath = currentRoute.url.toLowerCase();
        if (checkTheBaseMenu(requestPath, utilitiesURLArray)) {
            highlightId = "utility";
        } else if (checkTheBaseMenu(requestPath, factorsURLArray)) {
            highlightId = "factorRating";
        }
        $("#" + highlightId).addClass("active");
    }
    else {
        console.log("routing.dti.utils: Warning: currentRoute is not defined");
    }
});

function checkTheBaseMenu(url, urlArray) {
    var isFound = false;
    for (var i = 0; i < urlArray.length; i++) {
        if (url.indexOf(urlArray[i].toLowerCase()) > -1) {
            isFound = true;
            break;
        }
    }
    return isFound;
}

// Override the default select option in the utils.js
var getSelectOption = function (code, description) {
    var selectRec = {};
    selectRec[code] = "";
    selectRec[description] = "-SELECT-";
    return selectRec;
};

if (typeof dti.session == "undefined") {
    dti.session = (function() {
        return {
            loggingInInProcess: false,
            loginWindowOpen: false,
            loggedIn: true,
            init: function() {
                dti.page.setSessionHandler(this);

                $(window).off("loggedOut.dti.utils.session").on("loggedOut.dti.utils.session", function(evt, dtiPage) {
                    dti.session.loggedIn = false;

                    if (dtiPage) {
                        if (!dtiPage.isReloading) {
                            window.location.reload();
                        }
                        dtiPage.isReloading = true;
                    }
                });

                $(window).off("loggedIn.dti.utils.session").on("loggedIn.dti.utils.session", function(evt, dtiPage) {
                    dti.session.loggedIn = true;
                    if (dtiPage) {
                        dtiPage.isReloading = false;
                    }

                    dti.session.showUserName();
                });
            },
            isLoggedIn: function() {
                return dti.session.loggedIn;
            },
            showUserName: function() {
                $(".dti-user-name").text(dti.applicationContext.properties.publicUser.userName);
            },
            hasExpiredBasedOnText: function (responseText) {
                var regex = /<form.*id=\\*['"]*loginform\\*['"]*/i;
                if (regex.test(responseText)) {
                    return true;
                }

                return false;
            },
            handleExpiration: function() {
                $(window).trigger("session.expired.dti");
            },
            handleExpirationViaWindow: function() {     // DO NOT USE: This is left for potential future behavior
                $(window).trigger("session.expired.dti");

                $('#loginWindow').off('open.dti.utils.session').on('open.dti.utils.session', function (event) {
                    $(this).removeClass("invisible");
                    dti.session.loginWindowOpen = true;

                    var loginSnippet = [
                        '<div class="login-center">',
                        '  <div class="login-top">',
                        '        <div id="dti-login-error" class="login-error invisible">',
                        '            <span class="header-txt"></span> <span class="txtBdRgt">Unable to log in with username and password</span>',
                        '        </div>',
                        '  </div>',
                        '  <div class="login-main">',
                        '    <form id="dti-login-form" method="post" action="j_security_check">',
                        '        <div class="login-left invisible">',
                        '            <img id="dti-logo" border="0" width="167" height="98"/>',
                        '        </div>',
                        '        <table>',
                        '            <tr>',
                        '                <td></td>',
                        '                <td><img id="dti-login-header" border="0" width="101" height="21"/></td>',
                        '            </tr>',
                        '            <tr>',
                        '                <td class="txtRight">User ID:</td>',
                        '                <td class="txtLeft"><input id="dti-username" type="text" name="j_username" class="mainForm" style="width: 170px"/></td>',
                        '            </tr>',
                        '            <tr>',
                        '                <td class="txtRight">Password:</td>',
                        '                <td class="txtLeft"><input id="dti-password" type="password" name="j_password" class="mainForm" style="width: 170px"/></td>',
                        '            </tr>',
                        '        </table>',
                        '        <div class="dti-login-button-container"><button id="dti-login-button" type="button" value="Login" class="btn-blueMedium">Log in</button></div>',
                        '    </form>',
                        '  </div>',
                        '</div>'
                    ];

                    var loginView = loginSnippet.join("\n");
                    $("#ifrmLogin-contents").html(loginView);

                    $("#dti-logo")
                        .attr("src", getContextPath().concat("/core/images/login-dtilogo.gif?build.number=1.0"))
                        .off("keydown.dti.utils.login")
                        .on("keydown.dti.utils.login", login_handleOnKeyDown);
                    $("#dti-login-header")
                        .attr("src", getContextPath().concat("/core/images/loginTxt.gif?build.number=2016.1.0"))
                        .off("keydown.dti.utils.login")
                        .on("keydown.dti.utils.login", login_handleOnKeyDown);

                    $("#dti-login-button").off("click.dti.utils.login").on("click.dti.utils.login", function(evt) {
                        dti.session.notifyOfLoggingIn();

                        $.ajax({
                            type: "POST",
                            url: "j_security_check",
                            dataType: "text",
                            data: {
                                j_username: $("#dti-username").val(),
                                j_password: $("#dti-password").val()
                            },
                            done: function(data, textStatus, xhr) {

                            },
                            fail: function(jqXHR, textStatus, errorThrown) {
                                console.log(textStatus, errorThrown);
                            }

                        });
                    });

                    $(window).off("logInFailure.dti.utils.session").on("logInFailure.dti.utils.session", function(evt) {
                        $("#dti-login-error").removeClass("invisible");
                    });

                    function login_handleOnKeyDown(evt) {
                        var code = evt.which;
                        if (code == 13) {
                            login();
                            evt.preventDefault();
                        }
                    }
                });

                $('#loginWindow').off('close.dti.utils.session').on('close.dti.utils.session', function (event) {
                    dti.session.loginWindowOpen = false;
                });

                $('#loginWindow').jqxWindow({
                    theme: 'energyblue',
                    autoOpen: false,
                    height: 250,
                    width: 600
                });
                $('#loginWindow').jqxWindow('open');

                $(window).off("loggedIn.dti.utils.session").on("loggedIn.dti.utils.session", function() {
                    $('#loginWindow').jqxWindow('close');
                });
            },
            notifyOfLoggingIn: function(payload) {
                dti.session.loggingInInProcess = true;
                $(window).trigger("loggingIn.dti", payload);
            },
            notifyOfLoggedIn: function(payload) {
                dti.session.loggingInInProcess = false;
                $(window).trigger("loggedIn.dti", payload);
            },
            notifyOfLoggedOut: function(payload) {
                dti.session.loggingInInProcess = false;
                $(window).trigger("loggedOut.dti", payload);
                // Temporary workaround since something de-register "loggedOut.dti.utils.session" event
                dti.session.loggedIn = false;
                window.location.reload();
                //
            },
            notifyOfLogInFailure: function(payload) {
                dti.session.loggingInInProcess = false;
                $(window).trigger("logInFailure.dti", payload);
            },
            shouldOpenWindow: function() {
                return !dti.session.loginWindowOpen && $("#loginform").length == 0;
            }
        }
    })();

    dti.session.init();

    require(['jquery'], function(){
        // catch global AJAX errors
        $.ajaxSetup({complete:  function(jqxhr,textStatus){
            if (dti.session.loggingInInProcess) {
                if (jqxhr.status == 302 || jqxhr.status == 200) {
                    dti.session.notifyOfLoggedIn();
                }
                else {  // jqxhr.status == 403
                    dti.session.notifyOfLogInFailure();
                }
                return;
            }

            if (dti.page.sessionHandler.hasExpiredBasedOnText(jqxhr.responseText)) {
                dti.page.sessionHandler.notifyOfLoggedOut(dti.page);
                return;
            }
        }
        });

    });
}

dti.grid.mapPrimaryKeys = function(args) {
    // TODO start---
    // Copied the method _getRowByPrimaryKey from utils.js since it's a private method.
    // This method should be removed when merging the method mapPrimaryKeys to utils.js
    var _getRowByPrimaryKey = function(rawData, pk, primaryKeyId) {
        var rowsFound = $.grep(rawData, function(item, index) {
            if (item[primaryKeyId] == pk) {
                return true;
            }

            return false;
        });

        return rowsFound[0];
    };
    // TODO end----

    if (args.mapPrimaryKeys) {      // allow for replacement function
        args.mapPrimaryKeys(args);
    }
    else {
        var selector = "#".concat(args.gridId);
        var page = args.page;
        var data = args.data;
        var rawData = page[selector].rawData;
        var primaryKeyId = page[selector].primaryKeyId;

        for (var i=0; i<data.length; i++) {
            // Changed the property name of entity PK and new entity PK field.
            var rowItem = _getRowByPrimaryKey(rawData,data[i]["entityKey"], primaryKeyId);
            rowItem[primaryKeyId] = data[i]["newEntityKey"];
        }
    }
};

dti.grid.getChanges = function (page, gridId, recordProcessor) {
    var _preProcessRecords = function(records, recordProcessor) {
        var processedRecords = [];
        for (var i = 0; i < records.length; i++) {
            var record = records[i];
            if (record.updateIndicator) {
                if (recordProcessor) {
                    processedRecords.push(recordProcessor(record));
                }
                else {
                    processedRecords.push(record);
                }
            }
        }

        return processedRecords;
    };
    var selector = "#".concat(gridId);
    page[selector] = page[selector] || {};
    // Use empty array as default.
    page[selector].rawData = page[selector].rawData || [];

    return _preProcessRecords(page[selector].rawData, recordProcessor);
};

// Added parameter removeDeletedRows
dti.grid.resetUpdateIndicator = function (page, gridId, removeDeletedRows) {
    var selector = "#".concat(gridId);

    if (page[selector] && page[selector].rawData) {
        var rawData = page[selector].rawData;

        // Remove the deleted raw data.
        if (removeDeletedRows) {
            rawData = $.grep(rawData, function(item) {
                return item.updateIndicator != dti.grid.ROW_STATUS_DELETED;
            });
        }

        // Reset update ind.
        for (var i = 0; i < rawData.length; i++) {
            if (rawData[i].updateIndicator) {
                rawData[i].updateIndicator = undefined;
            }
        }

        // Reload grid.
        dti.grid.load(gridId, rawData, page);
    }
};

dti.selectOption.DEFAULT_SELECT_OPTION_LABEL = "-SELECT-";