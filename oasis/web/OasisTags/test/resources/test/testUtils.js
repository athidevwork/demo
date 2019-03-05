/**
 * Created by kshen on 9/18/2015.
 */

var getParentWindow;

/**
 * The methods which are supported to be mocked.
 * The method after with a comment "true" means that the function would be mocked by default when dti.test.page.init()
 * is called.
 */
var closeWindow,
    getAppPath,
    getObjectValue, // true
    openDivPopup,
    setObjectValue; // true

if (typeof dti == "undefined") {
    dti = {};
}

if (typeof dti.test == "undefined") {
    dti.test = {};
}

if (typeof dti.test.page == "undefined") {
    dti.test.page = (function() {
        var createWindow = function(currentWindow) {
            var window = {
                currentWindow: true,
                pageContext: {},

                mockGetAppPath: function (setting) {
                    if (setting == undefined ||
                        (setting != null && setting.hasOwnProperty("getAppPath") && setting["getAppPath"])) {
                        this.getAppPath = jasmine.createSpy("getAppPath").and.callFake(function () {
                            return "/odev20161/eClaim/CM/";
                        });

                        if (this.currentWindow) {
                            getAppPath = this.getAppPath;
                        }
                    }
                    return this;
                },

                mockGetObjectValue: function(setting) {
                    if (setting == undefined || setting == null || !setting.hasOwnProperty("getObjectValue") || setting["getObjectValue"]) {
                        var pageContext = this.pageContext;

                        this.getObjectValue = jasmine.createSpy("getObjectValue").and.callFake(function(fieldName){
                            if (!isStringValue(fieldName) && !pageContext.hasOwnProperty(fieldName)) {
                                return "";
                            }

                            return pageContext[fieldName];
                        });

                        if (this.currentWindow) {
                            getObjectValue = this.getObjectValue;
                        }
                    }
                    return this;
                },

                mockSetObjectValue: function(setting) {
                    if (setting == undefined || setting == null || !setting.hasOwnProperty("setObjectValue") || setting["setObjectValue"]) {
                        var pageContext = this.pageContext;

                        this.setObjectValue = jasmine.createSpy("setObjectValue").and.callFake(function(fieldName, fieldValue){
                            if (isStringValue(fieldName)) {
                                pageContext[fieldName] = fieldValue;
                            }
                        });

                        if (this.currentWindow) {
                            setObjectValue = this.setObjectValue;
                        }
                    }
                    return this;
                },

                mockOpenDivPopup: function(setting) {
                    if (setting == undefined ||
                        (setting != null && setting.hasOwnProperty("openDivPopup") && setting["openDivPopup"])) {
                        this.openDivPopup = jasmine.createSpy("openDivPopup");

                        if (this.currentWindow) {
                            openDivPopup = this.openDivPopup;
                        }
                    }
                    return this;
                },

                mockCloseWindow: function(setting) {
                    if (setting == undefined ||
                        (setting != null && setting.hasOwnProperty("closeWindow") && setting["closeWindow"])) {
                        this.closeWindow = jasmine.createSpy("closeWindow");

                        if (this.currentWindow) {
                            closeWindow = this.closeWindow;
                        }
                    }
                    return this;
                }
            };

            if (currentWindow != undefined && currentWindow != null && !currentWindow) {
                window.currentWindow = false;
            }

            return window;
        };

        var mockParentWindow = function(window) {
            var parentWindow = createWindow(false);

            getParentWindow = jasmine.createSpy("getParentWindow").and.callFake(function(){
                return parentWindow;
            });

            return parentWindow;
        };

        return {
            init: function(setting) {
                var window = createWindow();

                this._initWindow(window, setting);

                return window;
            },

            initParentWindow: function(setting) {
                var parentWindow = mockParentWindow();

                this._initWindow(parentWindow, setting);

                return parentWindow;
            },

            _initWindow: function(window, setting) {
                // If setting is undefined, create an empty setting object. So we can skip to mock the methods which is
                // not supposed to be mocked by default.
                if (setting == undefined) {
                    setting = {};
                }

                window.mockCloseWindow(setting)
                    .mockGetAppPath(setting)
                    .mockGetObjectValue(setting)
                    .mockSetObjectValue(setting)
                    .mockOpenDivPopup(setting);

                return window;
            }
        }
    })();
}
