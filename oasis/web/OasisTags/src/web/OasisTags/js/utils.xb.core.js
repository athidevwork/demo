/*
 Revision Date    Revised By  Description
 ----------------------------------------------------------------------------
 12/07/2018       dpang       196632 - Modified dispatchEvent() because MouseEvent can cause error in IE.
 ----------------------------------------------------------------------------
 */

if (typeof dti == "undefined") {
    dti = {};
}

if (typeof dti.oasis == "undefined") {
    dti.oasis = {};
}

if (typeof dti.oasis.string == "undefined") {
    dti.oasis.string = (function () {
        return {
            startsWith: function (str, prefix) {
                return (str.indexOf(prefix) == 0);
            },

            endsWith: function (str, suffix) {
                return str.indexOf(suffix, str.length - suffix.length) !== -1;
            },


            isEmpty: function (value) {
                if (typeof value === 'undefined' || value == null) return true;
                return !/\S/.test(value);
            },

            isIntegerValue: function (value) {
                return /^\d+$/.test(value);
            },

            isNumberString: function (value) {
                var numberReSnippet = "(?:NaN|-?(?:(?:\\d+|\\d*\\.\\d+)(?:[E|e][+|-]?\\d+)?|Infinity))";
                var matchOnlyNumberRe = new RegExp("^("+ numberReSnippet + ")$");
                return matchOnlyNumberRe.test(value);
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
            },

            strLeft: function (str, sep) {
                return str.substring(0, str.lastIndexOf(sep));
            },

            strRight: function (str, sep) {
                return str.substring(str.indexOf(sep) + sep.length);
            },
            trim: function (str) {
                return str.replace(/(^[\s\n\r]*)|([\s\n\r]*$)/g, '');
            },

            escapeHtml: function (val) {
                var map = {
                    '&': '&amp;',
                    '<': '&lt;',
                    '>': '&gt;',
                    '"': '&quot;',
                    "'": '&apos;'
                };

                if (typeof val === 'string') {
                    return val.replace(/[&<>"']/g, function(key) {
                        return map[key];
                    });
                } else {
                    return val;
                }
            }
        };
    })();
}

if (typeof dti.oasis.number == "undefined") {
    dti.oasis.number = (function () {
        return {
            isInteger: function (val) {
                return !isNaN(val) && parseInt(Number(val)) == val && !isNaN(parseInt(val, 10));
            },

            isNumber: function (val) {
                return typeof val === "number";
            },

            formatNumber: function (num, pattern) {
                return dti.oasis.numberFormat.formatNumber(num, pattern);
            },

            unformatNumber: function (num, pattern) {
                if (dti.oasis.string.isEmpty(num)) {
                    return "";
                }

                num = num.replace(/[$,]/g, '');
                return dti.oasis.numberFormat.parseNumber(num, pattern).toString();
            }
        };
    })();
}

if (typeof dti.oasis.date == "undefined") {
    dti.oasis.date = (function () {
        return {
            DATE_DISPLAY_FORMAT: "MM/dd/yyyy",
            DEFAULT_DATE_FORMAT: "MM/dd/yyyy",

            formatDate: function (date, dateFormat) {
                if (date == null) {
                    return "";
                }

                if (dateFormat == undefined || dateFormat == null) {
                    dateFormat = dti.oasis.date.DEFAULT_DATE_FORMAT;
                }

                return $.jqx.dataFormat.formatDate(date, dateFormat);
            },

            formatDateForDisplay: function (date) {
                return $.jqx.dataFormat.formatDate(date, this.DATE_DISPLAY_FORMAT);
            },

            parseDate: function (date, dateFormat) {
                if (dti.oasis.string.isEmpty(date)) {
                    return null;
                }

                if (dateFormat == undefined || dateFormat == null) {
                    dateFormat = dti.oasis.date.DEFAULT_DATE_FORMAT;
                }

                return $.jqx.dataFormat.parsedate(date, dateFormat);
            },

            /**
             * The calendarAll.js overrides setFullYear method and only accept yearValue as input parameter.
             * It makes jqWidgets blocked. So we rename and moved the setFullYear method of calendarAll.js to dti.date package.
             *
             * Note: the following are the original setFullYear method of Date:
             * Date.prototype.setFullYear(yearValue[, monthValue[, dayValue]])
             *
             * @param date
             * @param year
             */
            setFullYear: function (date, year) {
                var d = new Date(date);
                d.setFullYear(year);
                if (d.getMonth() != date.getMonth())
                    date.setDate(28);
                date.setFullYear(year);
            },

            isDate: function (obj) {
                return (Object.prototype.toString.call(obj) === '[object Date]');
            }
        };
    })();
}

if (typeof dti.oasis.array == "undefined") {
    dti.oasis.array = (function () {
        return {
            /**
             * Get a new array value from an Arguments object
             * @param argumentsObj
             * @returns {*}
             */
            fromArguments: function (argumentsObj) {
                // IE doesn't support Array.from
                if (typeof Array.from == "undefined") {
                    var result = [];

                    for (var i = 0; i < argumentsObj.length; i++) {
                        result[result.length] = argumentsObj[i];
                    }

                    return result;

                } else {
                    return Array.from(argumentsObj);
                }
            },

            /**
             * Looks at the last element of an array without removing it.
             *
             * @param array
             */
            peek: function (array) {
                var len = array.length;
                return len > 0 ? array[len - 1] : undefined;
            }
        };
    })();
}

if (typeof dti.oasis.node == "undefined") {
    dti.oasis.node = (function () {
        return {
            nodeType: {
                ELEMENT_NODE: 1,
                TEXT_NODE: 3,
                PROCESSING_INSTRUCTION_NODE: 7,
                COMMENT_NODE: 8,
                DOCUMENT_NODE: 9,
                DOCUMENT_TYPE_NODE: 10,
                DOCUMENT_FRAGMENT_NODE: 11
            },

            isElementNode: function (node) {
                return (node.nodeType == this.nodeType.ELEMENT_NODE);
            },

            isTextNode: function (node) {
                return (node.nodeType == this.nodeType.TEXT_NODE);
            },

            isDocumentNode: function (node) {
                return (node.nodeType == this.nodeType.DOCUMENT_NODE);
            }
        };
    })();
}

if (typeof dti.oasis.ui == "undefined") {
    dti.oasis.ui = (function () {
        return {
            ConfirmDialog: {
                btnYes: 1,    // 0b1
                btnNo: 2,     // 0b10
                btnCancel: 4  // 0b100
            },

            dispatchEvent: function (eventTarget, eventName) {
                switch (eventName) {
                    case "blur":
                        if ("createEvent" in document) {
                            var evt = document.createEvent("HTMLEvents");
                            evt.initEvent("blur", false, true);
                            eventTarget.dispatchEvent(evt);
                            return fixEvent(evt).returnValue;
                        } else {
                            // IE8
                            eventTarget.fireEvent("onblur");
                        }
                        break;
                    case "change":
                        if ("createEvent" in document) {
                            var evt = document.createEvent("HTMLEvents");
                            evt.initEvent("change", false, true);
                            eventTarget.dispatchEvent(evt);
                            return fixEvent(evt).returnValue;
                        } else {
                            // IE8
                            eventTarget.fireEvent("onchange");
                        }
                        break;
                    case "click":
                        if ("createEvent" in document) {
                            var evt = document.createEvent("HTMLEvents");
                            evt.initEvent("click", false, true);
                            eventTarget.dispatchEvent(evt);
                            return fixEvent(evt).returnValue;
                        } else {
                            // IE8
                            eventTarget.fireEvent("onclick");
                        }
                        break;
                }
            },

            //Don't use this function add event directly. use addElementEventListener in gui.js
            addEventListener: function (element, eventName, callBack) {
                if (element.addEventListener) {
                    element.addEventListener(eventName, callBack);
                } else {
                    // IE8
                    element.attachEvent("on" + eventName, callBack);
                }
            },

            //Don't use this function add event directly. use addElementEventListener in gui.js
            addEventListenerToElements: function (elements, eventName, callBack) {
                if (elements && elements.length) {
                    for (var i = 0; i < elements.length; i++) {
                        dti.oasis.ui.addEventListener(elements[i], eventName, callBack);
                    }
                } else {
                    dti.oasis.ui.addEventListener(elements, eventName, callBack);
                }
            },

            //Don't use this function add event directly. use addElementEventListener in gui.js
            removeEventListener: function (element, eventName, callBack) {
                if (element.removeEventListener) {
                    element.removeEventListener(eventName, callBack);
                } else {
                    // IE8
                    element.detachEvent("on" + eventName, callBack);
                }
            },

            //Don't use this function add event directly. use addElementEventListener in gui.js
            removeEventListenerToElements: function (elements, eventName, callBack) {
                if (elements && elements.length) {
                    for (var i = 0; i < elements.length; i++) {
                        dti.oasis.ui.removeEventListener(elements[i], eventName, callBack);
                    }
                } else {
                    dti.oasis.ui.removeEventListener(elements, eventName, callBack);
                }
            },

            showHideElement: function (element, isHidden) {
                if (isHidden) {
                    $(element).addClass("dti-hide");
                } else {
                    $(element).removeClass("dti-hide");
                }
            },

            getElementDataType: function (element) {
                if (element.hasAttribute("datatype")) {
                    return element.getAttribute("datatype");
                }

                return null;
            },

            getParentWindow: function (allowCrossOrigin) {
                if (window.frameElement) {//it's a popup div
                    return this.getParentFrame(allowCrossOrigin);
                } else {//it's a child window
                    return this.getOpenerWindow(allowCrossOrigin);
                }

            },

            getParentFrame: function (allowCrossOrigin) {
                allowCrossOrigin = (typeof allowCrossOrigin ==  "undefined") ? false : allowCrossOrigin;

                if (window.frameElement && window != window.parent) {
                    var parentWindow = window.parent;

                    //cross-origin solution section
                    if (!allowCrossOrigin) {
                        try {
                            parentWindow.document;
                        } catch (e) {
                            return null;
                        }
                    }
                    return parentWindow;
                }
                return null;
            },

            getOpenerWindow: function (allowCrossOrigin) {
                allowCrossOrigin = (typeof allowCrossOrigin ==  "undefined") ? false : allowCrossOrigin;
                //it's a child window
                if (window.opener && !window.opener.closed) {
                    var parentWindow = window.opener;
                    //cross-origin solution section
                    if (!allowCrossOrigin) {
                        try {
                            parentWindow.document;
                        } catch (e) {
                            return null;
                        }
                    }
                    return parentWindow;
                }
                return null;
            },

            getParentWindowOfDivPopupFrame: function (divPopFrame, allowCrossOrigin) {
                allowCrossOrigin = (typeof allowCrossOrigin == "undefined") ? false : allowCrossOrigin;

                var parentWindow = divPopFrame.contentWindow.parent;

                if (!allowCrossOrigin) {
                    try {
                        parentWindow.document;
                    } catch (e) {
                        return null;
                    }
                }

                return parentWindow;
            },

            /**
             * Check if it's a event of enter key.
             * @param event
             * @returns {*|boolean}
             */
            isEnterKeyEvent: function (event) {
                return event.keyCode && event.keyCode == 13;
            },

            /**
             * Init search on enter.
             * @param searchFn
             */
            initSearchOnEnter: function (searchFn) {
                function __isEventOkForSearchOnEnter(event) {
                    return dti.oasis.ui.isEnterKeyEvent(event) &&
                        !dti.oasis.grid.hasOpenedJqxGridMenu() &&
                        !dti.oasis.grid.isJqxGridMenuElement(event.target) &&
                        !dti.oasis.ui.isConfirmDialogOpen();
                }

                // Since the keypress event target on firefox is body when press enter on firefox is body, use keydown to init search on enter.
                $(document).off("keydown.dti.oasis.page.searchOnEnter")
                    .on("keydown.dti.oasis.page.searchOnEnter", function (event) {
                        if (__isEventOkForSearchOnEnter(event)) {

                            // Add Search on enter event.
                            $(document).off("keypress.dti.oasis.page.searchOnEnter")
                                .on("keypress.dti.oasis.page.searchOnEnter", function (event) {
                                    // Process search on enter.
                                    if (__isEventOkForSearchOnEnter(event)) {

                                        searchFn();
                                        event.stopPropagation();
                                    }
                                });
                        } else {
                            // Clear search on enter event for key press.
                            $(document).off("keypress.dti.oasis.page.searchOnEnter");
                        }
                    });
            },

            /**
             * Show confirm dialog.
             * @options A message string or an option object.
             * Option object: {
             *        message: "...", // The message to display
             *        fnYes: function () {
             *                // The callback function to handle Yes button clicked. Optional.
             *        },
             *        fnNo: function () {
             *                // The callback function to handle No button clicked. Optional.
             *        },
             *        fnCancel: function () {
             *                // The callback function to handle Cancel button clicked. Optional.
             *        },
             *        showCancel: true|false
             *        // True or false value to indicate if the cancel button needs to be displayed.
             *        // Optional. False by default. If fnCancel parameter is provided, Cancel button will always be displayed.
             * }
             * @return Return a Promise object of jQuery.
             * If user clicked [Yes] button, the function will resolve a deferred object with value dti.oasis.ui.ConfirmDialog.btnYes.
             * If user clicked [No] button, the function will reject a deferred object with value dti.oasis.ui.ConfirmDialog.btnNo.
             * If user clicked [Cancel] button, the function will reject a deferred object with value dti.oasis.ui.ConfirmDialog.btnCancel.
             *
             * We can use fnYes, fnNo, and fnCancel callback function to handle the result or the call back function in
             * then, done, and fail to handle result.
             *
             */
            showConfirmDialog: function (options) {
                var defer = $.Deferred();

                // Confirm message.
                var message;
                var fnYes = null;
                var fnNo = null;
                var fnCancel = null;

                if (typeof arguments[0] == "string") {
                    message = arguments[0];
                } else {
                    message = options.message;

                    if (options["fnYes"]) {
                        fnYes = options["fnYes"];
                    }

                    if (options["fnNo"]) {
                        fnYes = options["fnNo"];
                    }

                    if (options["fnCancel"]) {
                        fnCancel = options["fnCancel"];
                    }
                }

                var buttons = {};

                buttons["Yes"] = function () {
                    if (fnYes != null) {
                        fnYes();
                    }

                    defer.resolve(dti.oasis.ui.ConfirmDialog.btnYes);

                    $(this).dialog("close");
                };

                buttons["No"] = function () {
                    if (fnNo != null) {
                        fnNo();
                    }

                    defer.reject(dti.oasis.ui.ConfirmDialog.btnNo);

                    $(this).dialog("close");
                };

                if (options["showCancel"] || fnCancel != null) {
                    buttons["Cancel"] = function () {
                        if (fnCancel != null) {
                            fnCancel();
                        }

                        defer.reject(dti.oasis.ui.ConfirmDialog.btnCancel);

                        $(this).dialog("close");
                    };
                }

                $("#confirmDialogText").html(message);
                $("#confirmDialog").dialog({
                    height: "auto",
                    modal: true,
                    buttons: buttons
                }).dialog("open");

                return defer.promise();
            },

            isConfirmDialogOpen: function () {
                return $("#confirmDialog").dialog("isOpen");
            }
        }
    })();
}

if (typeof dti.oasis.numberFormat == "undefined") {
    dti.oasis.numberFormat = (function () {
        return {
            _protected: {
                _DEFAULT_GROUPING_SEPARATOR: ",",
                _DEFAULT_DECIMAL_POINT: ".",

                _DEFAULT_CURRENCY_FORMAT: "$#,##0.00;($#,##0.00)",
                _NUMBER_ONLY_FORMAT_REGEX: /^[0#.,]+$/,

                _tokenType: {
                    PREFIX: 0,
                    DIGITAL: 1,
                    SUFFIX: 2,
                    NEGATIVE_SEPARATOR: 3,
                    COLOR:4
                },

                _compileNumberFormat: function (pattern, options) {
                    var compiledOptions = this._compileNumberFormatOptions(pattern, options);

                    return this._createNumberFormat(compiledOptions);
                },

                _createNumberFormat: function (compiledOptions) {
                    // Get a jquery.numberformatter-1.2.2.js instance for number formatting.
                    var delegateNumberFormat = this._getDelegateNumberFormat(compiledOptions);

                    return {
                        formatNumber: function (value) {
                            return delegateNumberFormat.formatNumber(value);
                        },

                        parseNumber: function (value) {
                            return delegateNumberFormat.parseNumber(value);
                        }
                    }
                },

                _getDelegateNumberFormat: function (compiledOptions) {
                    var pattern = compiledOptions.pattern;
                    if (compiledOptions.isPercentage && compiledOptions.pattern.indexOf("%") == "-1") {
                        pattern += "%";
                    }

                    var options = getOptions(pattern);

                    return {
                        formatNumber: function (value) {
                            var formattedNumberStr = $.formatNumber(value, options);
                            var isNegative = false;

                            if (formattedNumberStr.indexOf("-") > -1) {
                                isNegative = true;
                            }

                            // Move negative and percent sign, they will be added by prefix and suffix if required.
                            formattedNumberStr = replace(formattedNumberStr, "-", "");
                            formattedNumberStr = replace(formattedNumberStr, "%", "");

                            if (isNegative) {
                                return compiledOptions.negativePrefix + formattedNumberStr + compiledOptions.negativeSuffix;
                            } else {
                                return compiledOptions.positivePrefix + formattedNumberStr + compiledOptions.positiveSuffix;
                            }
                        },

                        parseNumber: function (value) {
                            if (value.indexOf("-") == -1) {
                                if ((compiledOptions.negativePrefix != "" &&
                                    compiledOptions.negativePrefix != compiledOptions.positivePrefix &&
                                    value.indexOf(compiledOptions.negativePrefix) > -1) ||
                                    (compiledOptions.negativeSuffix != "" &&
                                        compiledOptions.negativeSuffix != compiledOptions.positiveSuffix &&
                                        value.indexOf(compiledOptions.negativeSuffix) > -1)) {

                                    // If it's a negative number, add negative sign.
                                    value = "-" + value;
                                }
                            }

                            // Remove none-numeric chars since it doens't matter to have them in the formatted number for parseNumber.
                            value = value.replace(/[^0-9-.,%]/g, '');

                            return $.parseNumber(value, options).valueOf();
                        }
                    }
                },

                _compileNumberFormatOptions: function (pattern, options) {
                    if (pattern == this._DEFAULT_CURRENCY_FORMAT) {
                        return this._getCurrencyFormatOptions(pattern, options);
                    } else if (pattern.match(this._NUMBER_ONLY_FORMAT_REGEX)) {
                        return this._getNumberOnlyFormatOptions(pattern, options);
                    } else {
                        var compiledOptions = {
                            positivePrefix: "",
                            positiveSuffix: "",
                            negativePrefix: "",
                            negativeSuffix: "",
                            isGrouping: false,
                            groupingSeparator: ",",
                            decimalPoint: ".",
                            isPercentage: false,
                            pattern: "",
                            color:""
                        };

                        var tokens = this._tokenize(pattern, options);

                        var isNegativeFormat = false;
                        for (var i = 0; i < tokens.length; i++) {
                            var token = tokens[i];

                            switch (token.type) {
                                case this._tokenType.PREFIX:
                                    this._compilePrefix(token.value, isNegativeFormat, options, compiledOptions);
                                    break;

                                case this._tokenType.DIGITAL:
                                    this._compileDigital(token.value, isNegativeFormat, options, compiledOptions);
                                    break;

                                case this._tokenType.SUFFIX:
                                    this._compileSuffix(token.value, isNegativeFormat, options, compiledOptions);
                                    break;

                                case this._tokenType.NEGATIVE_SEPARATOR:
                                    isNegativeFormat = true;
                                    break;
                                case this._tokenType.COLOR:
                                    compiledOptions.color = token.value;
                                    break;
                            }
                        }

                        // If nagative prefix is null, change it to "-" + positivePrefix
                        if (compiledOptions.negativePrefix == "") {
                            compiledOptions.negativePrefix = "-" + compiledOptions.positivePrefix;
                        }

                        // If nagetive suffix is null, change it to be same with positiveSuffix
                        if (compiledOptions.negativeSuffix == "") {
                            compiledOptions.negativeSuffix = compiledOptions.positiveSuffix;
                        }

                        return compiledOptions;
                    }
                },

                _getCurrencyFormatOptions: function (pattern, options) {
                    return {
                        positivePrefix: "$",
                        positiveSuffix: "",
                        negativePrefix: "($",
                        negativeSuffix: ")",
                        isGrouping: true,
                        groupingSeparator: ",",
                        decimalPoint: ".",
                        isPercentage: false,
                        pattern: "#,##0.00"
                    };
                },

                _getNumberOnlyFormatOptions: function (pattern, options) {
                    return {
                        positivePrefix: "",
                        positiveSuffix: "",
                        negativePrefix: "-",
                        negativeSuffix: "",
                        isGrouping: pattern.indexOf(",") > -1,
                        groupingSeparator: ",",
                        decimalPoint: ".",
                        isPercentage: false,
                        pattern: pattern
                    };
                },

                _compilePrefix: function (prefix, isNegativeFormat, options, compiledOptions) {
                    if (isNegativeFormat) {
                        compiledOptions.negativePrefix = prefix;

                    } else {
                        compiledOptions.positivePrefix = prefix;

                        // Check if format to percent
                        if (prefix.indexOf("%") > -1) {
                            compiledOptions.isPercentage = true;
                        }
                    }
                },

                _compileDigital: function (pattern, isNegativeFormat, options, compiledOptions) {
                    if (!isNegativeFormat) {
                        compiledOptions.pattern = pattern;

                        // Check if we should grouping number.
                        if (pattern.indexOf(compiledOptions.groupingSeparator) > -1) {
                            compiledOptions.isGrouping = true;
                        }
                    }
                },

                _compileSuffix: function (suffix, isNegativeFormat, options, compiledOptions) {
                    if (isNegativeFormat) {
                        compiledOptions.negativeSuffix = suffix;

                    } else {
                        compiledOptions.positiveSuffix = suffix;

                        // Check if format the number to percent
                        if (suffix.indexOf("%") > -1) {
                            compiledOptions.isPercentage = true;
                        }
                    }
                },

                /**
                 * Get patter element tokens.
                 *
                 * @param pattern
                 * @param options
                 * @returns {Array}
                 * @private
                 */
                _tokenize: function (pattern, options) {
                    var tokens = [];
                    var currentToken = {type: this._tokenType.PREFIX, value: ""};

                    var color ="";
                    var index = pattern.indexOf("[");
                    if (index >0) {
                        var str = pattern.substring(0,index);
                        var index2 = pattern.indexOf("]");
                        str+= pattern.substring(index2+1);
                        color = pattern.substring(index+1, index2);
                        pattern = str;
                        currentToken = {type: this._tokenType.COLOR, value: color};
                        tokens[tokens.length] = currentToken;
                        currentToken = {type: this._tokenType.PREFIX, value: ""};
                    }

                    for(var i = 0; i < pattern.length; i++) {
                        var c = pattern.charAt(i);

                        switch (c) {
                            // Number
                            case '#':
                            case '0':
                            // Grouping
                            case options.groupingSeparator:
                            // Decimal
                            case options.decimalPoint:
                                if (currentToken.type != this._tokenType.DIGITAL) {
                                    if (currentToken.value != "") {
                                        tokens[tokens.length] = currentToken;
                                    }

                                    currentToken = {type: this._tokenType.DIGITAL, value: ""};
                                }

                                currentToken.value += c;
                                break;

                            // Negative separator
                            case ';':
                                if (currentToken.value != "") {
                                    tokens[tokens.length] =  currentToken;
                                }

                                currentToken = {type: this._tokenType.NEGATIVE_SEPARATOR, value: c};
                                break;

                            default:
                                switch (currentToken.type) {
                                    // If the current token is prefix or suffix, append it to the current token.
                                    case this._tokenType.PREFIX:
                                    case this._tokenType.SUFFIX:
                                        currentToken.value += c;
                                        break;

                                    // If it's after number token, it's a suffix token.
                                    case this._tokenType.DIGITAL:
                                        if (currentToken.value != "") {
                                            tokens[tokens.length] = currentToken;
                                        }

                                        currentToken = {type: this._tokenType.SUFFIX, value: c};
                                        break;

                                    // If it's after the negative separator, it's a prefix token.
                                    case this._tokenType.NEGATIVE_SEPARATOR:
                                        if (currentToken.value != "") {
                                            tokens[tokens.length] = currentToken;
                                        }

                                        currentToken = {type: this._tokenType.PREFIX, value: c};
                                        break;
                                }
                        }
                    }

                    // Add the last token.
                    if (currentToken.value != "") {
                        tokens[tokens.length] = currentToken;
                    }

                    return tokens;
                }
            },

            /**
             * Get number format instance
             *
             * @param pattern <positive prefix><number format><positive suffix>;<negative prefix><number format><negative suffix>
             * @param options Optional options for future use.
             * @returns {*}
             */
            getInstance: function(pattern, options) {
                if (typeof options == "undefined") {
                    options = {};
                }
                options.groupingSeparator = options.groupingSeparator || this._protected._DEFAULT_GROUPING_SEPARATOR;
                options.decimalPoint = options.decimalPoint || this._protected._DEFAULT_DECIMAL_POINT;

                return this._protected._compileNumberFormat(pattern, options);
            },

            /**
             * Format number.
             *
             * @param value
             * @param pattern
             * @param options
             * @returns {*}
             */
            formatNumber: function(value, pattern, options) {
                return this.getInstance(pattern, options).formatNumber(value);
            },

            /**
             * Parse a number string to a number value.
             * @param value
             * @param pattern
             * @param options
             * @returns {*}
             */
            parseNumber: function(value, pattern, options) {
                return this.getInstance(pattern, options).parseNumber(value);
            }
        };
    })();
}

if (typeof dti.oasis.dataFormat == "undefined") {
    dti.oasis.dataFormat = (function () {
        return {
            DATE_FORMAT: "MM/dd/yyyy",
            DATE_TIME_FORMAT: "MM/dd/yyyy hh:mm tt",

            formatCheckboxCellValue: function (val) {
                return (dti.oasis.string.isEmpty(val) || val == false || val == "false" || val == "0") ? "0" : "-1";
            },

            unformatCheckboxCellValue: function (val) {
                return !(dti.oasis.string.isEmpty(val) || val == false || val == "false" || val == "0");
            },

            formatDropdownCellValue: function(val) {
                if (val && val.hasOwnProperty("label") && val.hasOwnProperty("value")) {
                    val = val["value"];
                }
                return val;
            },

            formatDate: function (val, pattern) {
                if (dti.oasis.string.isEmpty(val)) {
                    return "";
                }

                if (typeof val == "string") {
                    val = this.parseDate(val);
                }

                if (pattern == undefined || dti.oasis.string.isEmpty(pattern)) {
                    pattern = this.getDefaultDateFormat();
                }

                return $.jqx.dataFormat.formatDate(val, pattern);
            },

            formatDateForDisplay: function (val) {
                if (val == null) {
                    return "";
                }

                if (typeof val == "string") {
                    val = this.parseDate(val);
                }

                return this.formatDate(val,
                    (dti.oasis.string.isEmpty(this.getJqxDateDisplayFormat()) ?
                        this.getDefaultDateFormat() : this.getJqxDateDisplayFormat()));
            },

            parseDate: function (val, pattern) {
                if (dti.oasis.string.isEmpty(val)) {
                    return null;
                }

                if (pattern == undefined || dti.oasis.string.isEmpty(pattern)) {
                    pattern = this.getDefaultDateFormat();
                }

                return $.jqx.dataFormat.parsedate(val, pattern);
            },

            formatMoney: function (val) {
                if (typeof val == "number") {
                    val = val.toString();
                }
                return formatMoneyStrValAsStr(val);
            },

            unformatMoney: function (val) {
                return unformatMoneyStrValAsStr(val);
            },

            formatNumber: function (val, pattern) {
                var jNumberFormat = dti.oasis.numberFormat.getInstance(pattern);

                if (typeof val == "number") {
                    return jNumberFormat.formatNumber(val)
                }

                if (dti.oasis.string.isEmpty(val)) {
                    return "";
                }

                return jNumberFormat.formatNumber(jNumberFormat.parseNumber(val));
            },

            unformatNumber: function (val, pattern) {
                if (dti.oasis.string.isEmpty(val)) {
                    return null;
                }

                return dti.oasis.numberFormat.parseNumber(val, pattern);
            },

            unformatNumberToStr: function (val, pattern) {
                if (dti.oasis.string.isEmpty(val)) {
                    return "";
                }

                return dti.oasis.numberFormat.parseNumber(val, pattern).toString();
            },

            formatPercentage: function (val) {
                if (dti.oasis.string.isEmpty(val)) {
                    return "";
                }

                return formatPctStrVal(val);
            },

            unformatPercentage: function (val) {
                if (dti.oasis.string.isEmpty(val)) {
                    return "";
                }

                return unformatPctStrVal(formatPctStrVal(val));
            },

            formatPhoneNumber: function (val) {
                if (dti.oasis.string.isEmpty(val) || val == PHONE_MASK) {
                    return "";
                }

                return formatPhoneNumberForDisplay(val);
            },

            unformatPhoneNumber: function (val) {
                if (dti.oasis.string.isEmpty(val) || val == PHONE_MASK) {
                    return "";
                }

                return val.replace(/[^\d]/g, '');
            },

            getDefaultDateFormat: function () {
                return this.DATE_FORMAT;
            },

            getDefaultDateTimeFormat: function () {
                return this.DATE_TIME_FORMAT;
            },

            getLocalDateFormat: function () {
                if (typeof localeDataMask == "undefined") {
                    return this.getDefaultDateFormat();
                }

                return localeDataMask;
            },

            getJqxDateDisplayFormat: function () {
                if (typeof localeDataMask == "undefined" ||
                    dti.oasis.string.isEmpty(localeDataMask)) {
                    return null;
                }

                return localeDataMask;
            },

            getJqxDateTimeDisplayFormat: function () {
                if (typeof localeDataMask == "undefined" ||
                    dti.oasis.string.isEmpty(localeDataMask)) {
                    return null;
                }

                return localeDataMask + " HH:mm:ss";
            }
        }
    })();
}

