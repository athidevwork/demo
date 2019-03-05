/*
 Revision Date    Revised By  Description
 ----------------------------------------------------------------------------
 ----------------------------------------------------------------------------
 */

if (typeof dti.oasis.filter == "undefined") {
    dti.oasis.filter = (function () {
        return {
            // The constants of filter object types.
            type: {
                LOGICAL: "LOGICAL",
                COMPARE: "COMPARE",
                FUNCTION: "FUNCTION",
                CUSTOM_FUNCTION: "CUSTOM_FUNCTION"
            },

            // The constants of filter conditions.
            condition: {
                EQUAL: "EQUAL",
                NOT_EQUAL: "NOT_EQUAL",
                GREATER_THAN: "GREATER_THAN",
                GREATER_THAN_OR_EQUAL: "GREATER_THAN_OR_EQUAL",
                LESS_THAN: "LESS_THAN",
                LESS_THAN_OR_EQUAL: "LESS_THAN_OR_EQUAL",
                TRUE: "TRUE",
                FALSE: "FALSE",
                AND: "AND",
                OR: "OR",
                NOT: "NOT"
            },

            // The constants of filter value types.
            valueType: {
                COLUMN: "COLUMN",
                CONSTANT: "CONSTANT",
                FUNCTION: "FUNCTION"
            },

            // The available filter functions.
            filterFunction: {
                CONTAINS: "contains",
                STARTS_WITH: "starts-with"
            },

            // The available functions for processing values.
            valueFunction: {
                UPPERCASE: "uppercase",
                LOWERCASE: "lowercase",
                NUMBER: "number",
                CONCAT: "concat",
                SUBSTRING: "substring",
                TRANSLATE: "translate"
            },

            /**
             * Check if a filter is a TRUE filter.
             * @param filter
             * @returns {boolean}
             */
            isTrueFilter: function (filter) {
                return this._protected._isTrueFilter(filter);
            },

            /**
             * Check if a filter a FALSE filter
             * @param filter
             * @returns {boolean}
             */
            isFalseFilter: function (filter) {
                return this._protected._isFalseFilter(filter);
            },

            /**
             * Use "and" condition to combine multi filter objects, and return a new filter object. If all the filter
             * functions of the input filter objects returns true, the filter function of the new filter object returns
             * true. Otherwise, it returns false.
             *
             * The input parameters can be one or more filter objects. For example:
             * var newFilterObj = dti.oasis.filter.and(filterObj1, filterObj2, filterObj3, ...);
             *
             * @returns {*} A new filter object.
             */
            and: function () {
                return this._protected._and(dti.oasis.array.fromArguments(arguments));
            },

            /**
             * Use "or" condition to combine multi filter objects, and return a new filter object. If any of the filter
             * functions of the input filter objects returns true, the filter function of the new filter object returns
             * true. Otherwise, it returns false.
             *
             * The input parameters can be one or more filter objects. For example:
             * var newFilterObj = dti.oasis.filter.or(filterObj1, filterObj2, filterObj3, ...);
             *
             * @returns {*} A new filter object.
             */
            or: function () {
                return this._protected._or(dti.oasis.array.fromArguments(arguments));
            },

            /**
             * Use "not" condition to create a new filter object. The filter function of the new filter object will return
             * the opposite value of the filter function of the original filter object.
             *
             * The input parameters can be one or more filter objects. For example:
             * var newFilterObj = dti.oasis.filter.not(filterObj);
             *
             * @param filter
             * @returns {*} A new filter object
             */
            not: function (filter) {
                return this._protected._not(filter);
            },

            /**
             * Create a filter object by input parameters.
             *
             * The following is an example:
             * var filter = dti.oasis.filter.createFilter({
             *     type: dti.oasis.filter.type.COMPARE,
             *     condition: dti.oasis.filter.condition.EQUAL,
             *     values: [
             *         {value: "CCLAIMNO", type: dti.oasis.filter.valueType.COLUMN},
             *         {value: "101234", type: dti.oasis.filter.valueType.CONSTANT}
             *     ]
             * });
             * Since we have default values for type and condition of a filter object, and we can use string, number
             * values directly as the filter values. The code can be change to the following:
             * var filter = dti.oasis.filter.createFilter({
             *     values: [
             *         {value: "CCLAIMNO", type: dti.oasis.filter.valueType.COLUMN},
             *         "101234"
             *     ]
             * });
             *
             * A filter object will extend the __baseFilter object, and have the function and, or, and not.
             * It will also have a filter function to check if a record meets the filter condition.
             * @param params An object of filter parameters. It can have the following attributes:

             * <ul>
             *     <li><code>type</code> The type of a filter object. It can be "LOGIC", "COMPARE", "FUNCTION", and
             *     "CUSTOM_FUNCTION". The default value of type is "COMPARE".</li>
             *     <li><code>condition</code> The condition used to filter records. Please see the method
             *     {@link dti.oasis.filter.createCompareFilter}, {@link dti.oasis.filter.createFunctionFilter}, and
             *     {@link dti.oasis.filter.createCustomFilter} for the available conditon values for different types of
             *     filter objects.</li>
             *     <li><code>values</code>An array of values used for filter condition for filter records. An element of
             *     the filter values array could be a string, number, or object. If it's an object, it can have the
             *     following elements:
             *     <ul>
             *         <li><code>type</code>The type of a filter value</li>
             *         <li><code>value</code>The value used for filter condition to filter records.</li>
             *         </ul></li>
             *     Please see {@link dti.oasis.filter._protected._getValue} for the details.
             * </ul>
             * @returns {*}
             */
            createFilter: function (params) {
                // Default filter is TRUE filter.
                if (typeof params == "undefined" || params == null) {
                    return this.TRUE;
                }

                // Default filter type is COMPARE.
                if (!params.hasOwnProperty("type") || dti.oasis.string.isEmpty(params.type)) {
                    params.type = this.type.COMPARE;
                }

                switch (params.type) {
                    case this.type.LOGICAL:
                        return this.createLogicFilter(params);
                    case this.type.COMPARE:
                        return this.createCompareFilter(params);
                    case this.type.FUNCTION:
                        return this.createFunctionFilter(params);
                    case this.type.CUSTOM_FUNCTION:
                        return this.createCustomFilter(params);
                    default:
                        throw "Unsupported filter type: " + params.type + ".";
                }
            },

            /**
             * Create logic filter condition.
             *
             * @param params
             * @returns {*}
             */
            createLogicFilter: function (params) {
                if (typeof params == "undefined" || params == null) {
                    return this.TRUE;
                }

                // Default filter condition is AND.
                if (!params.hasOwnProperty("condition") || dti.oasis.string.isEmpty(params.condition)) {
                    params.condition = this.condition.AND;
                }

                switch (params.condition) {
                    case this.condition.TRUE    :
                        return this.TRUE;

                    case this.condition.FALSE:
                        return this.FALSE;

                    case this.condition.AND:
                        return this._protected._and(params.values);

                    case this.condition.OR:
                        return this._protected._or(params.values);

                    case this.condition.NOT:
                        if (typeof params.values == "undefined" || params.values == null) {
                            return this.TRUE;
                        }

                        return this._protected._not(params.values[0]);

                    default:
                        throw "Unsupported logical filter condition: " + params.condition + ".";
                }
            },

            /**
             * A quick method to create a new compare filter object. The type of the filter object is not required for
             * this method. The type will always be "COMPARE".
             *
             * The following are the list of the available filter conditions for a compare filter object:
             * <ul>
             *     <li><code>EQUAL</code> Check if values[0] equals to values[1].</li>
             *     <li><code>NOT_EQUAL</code> Check if values[0] doesn't equal to values[1].</li>
             *     <li><code>GREATER_THAN</code> Check if values[0] is greater than values[1].</li>
             *     <li><code>GREATER_THAN_OR_EQUAL</code> Check if values[0] is greater than or equals to values[1].</li>
             *     <li><code>LESS_THAN</code> Check if values[0] is less than values[1].</li>
             *     <li><code>LESS_THAN_OR_EQUAL</code> Check if values[0] is less than or equals to values[1].</li>
             * </ul>
             *
             * Example:
             * var filterObj = dti.oasis.filter.createCompareFilter({
             *     condition: "NOT_EQUAL",
             *     values: [
             *         {type: "COLUMN", value: "CCLAIMNO"},
             *         "CLOSED"
             *     ]
             * });
             *
             * @param params
             * @returns {*}
             */
            createCompareFilter: function (params) {
                if (typeof params == "undefined" || params == null) {
                    return this.TRUE;
                }

                // Default filter condition is EQUAL.
                if (!params.hasOwnProperty("condition") || dti.oasis.string.isEmpty(params.condition)) {
                    params.condition = this.condition.EQUAL;
                }

                switch (params.condition) {
                    case dti.oasis.filter.condition.EQUAL:
                    case dti.oasis.filter.condition.NOT_EQUAL:
                    case dti.oasis.filter.condition.GREATER_THAN:
                    case dti.oasis.filter.condition.GREATER_THAN_OR_EQUAL:
                    case dti.oasis.filter.condition.LESS_THAN:
                    case dti.oasis.filter.condition.LESS_THAN_OR_EQUAL:
                        break;
                    default:
                        throw "Unsupported compare condition: " + params.condition + ".";
                }

                var values = params.values || [];
                if (values.length != 2) {
                    throw "The length of the values to be compare for a compare filter should be 2.";
                }

                var value1 = values[0];
                var value1Type = value1.type || this.valueType.CONSTANT;
                var value2 = values[1];
                var value2Type = value2.type || this.valueType.CONSTANT;

                params.type = dti.oasis.filter.type.COMPARE;

                var filterObj = this._protected._createFilterObj(params);

                if (value1Type == this.valueType.CONSTANT && value2Type == this.valueType.CONSTANT) {
                    return filterObj.filter({}) ? this.TRUE : this.FALSE;
                }

                return filterObj;
            },

            /**
             * Create a function filter object.
             * Currently, it only support "contains" filter function. It checks if values[0] contains values[1].
             *
             * Example:
             * var filterObj = dti.oasis.filter.createFunctionFilter({
             *     condition: "contains",
             *     values: [
             *         {type: "column", value: "CPOLICYNO"},
             *         "MP2016"
             *     ]
             * });
             *
             * @param params
             * @returns {*}
             */
            createFunctionFilter: function (params) {
                params.type = dti.oasis.filter.type.FUNCTION;

                var fnName = params.condition;

                switch (fnName) {
                    case dti.oasis.filter.filterFunction.CONTAINS:
                    case dti.oasis.filter.filterFunction.STARTS_WITH:
                        var value1Config = params.values[0];
                        var value1Type = value1Config.type || this.valueType.CONSTANT;
                        var value2Config = params.values[1];
                        var value2Type = value2Config.type || this.valueType.CONSTANT;

                        var filterObj = this._protected._createFilterObj(params);

                        if (value1Type == this.valueType.CONSTANT && value2Type == this.valueType.CONSTANT) {
                            return filterObj.filter({}) ? this.TRUE : this.FALSE;
                        }

                        return filterObj;
                    default:
                        throw "Unsupported filter function: " + fnName + ".";
                }
            },

            /**
             * Create a custom filter object.
             * The filter condtion of a custom filter object is a method to filter records.
             *
             * Example:
             * var filterObj = dti.oasis.filter.createCustomFilter({
             *     condition: function (record) {
             *         var claimStatus = record["CCLAIMSTATUS"];
             *         if (claimStatus == "OPEN" ||
             *             claimStatus == "PENDING") {
             *             return true;
             *         }
             *
             *         return false;
             *     }
             * });
             *
             * @param params
             * @returns {*}
             */
            createCustomFilter: function (params) {
                params.type = dti.oasis.filter.type.CUSTOM_FUNCTION;

                return this._protected._createFilterObj(params);
            },

            /**
             * Compile a filter string to a filter object.
             * @param filterString An xPath style filter string.
             *
             * Example, the following filter string:
             * "//ROW[DISPLAY_IND = 'Y' and UPDATE_IND != 'D']"
             *
             * will be compiled to:
             * var filterObj = dti.oasis.filter.createFilter({
             *     values: [
             *         {
             *             type: "column",
             *             value: "DISPLAY_IND"
             *         },
             *         "Y"
             *     ]
             * }).and(dti.oasis.filter.createFilter({
             *     condition: "NOT_EQUAL",
             *     values: [
             *         {
             *             type: "column",
             *             value: "UPDATE_IND"
             *         },
             *         "D"
             *     ]
             * });
             *
             * @returns {*}
             */
            compile: function (filterString) {
                if (typeof filterString == "undefined" || filterString == null) {
                    return this.TRUE;
                }

                if (typeof filterString == "string") {
                    if (dti.oasis.string.isEmpty(filterString)) {
                        return this.TRUE;
                    }

                    var tokens = this._protected._tokenize(filterString);
                    var filterTokenTree = this._protected._processTokens(tokens);
                    return this._protected._compileFilter(filterTokenTree);
                }

                return this.compileFromJson(filterString);
            },

            /**
             * Compile a filter object from the result of filterObj.toJsonValue().
             * @param filterJsonValue
             * @returns {*}
             */
            compileFromJson: function (filterJsonValue) {
                for (var i = 0; i < filterJsonValue.values.length; i++) {
                    if (filterJsonValue.values[i].isFilterObject) {
                        filterJsonValue.values[i] = this.compileFromJson(filterJsonValue.values[i]);
                    }
                }

                if (filterJsonValue.condition == "[Function]") {
                    throw "Cannot recompile a custom function filter. Please use a function name to build your custom filter object instead.";
                }

                return dti.oasis.filter.createFilter(filterJsonValue);
            },

            _protected: (function () {
                return {
                    /**
                     * Create a filter object with parameters.
                     * @param params
                     * @returns {*} A filter object
                     */
                    _createFilterObj: function (params) {
                        /**
                         * The prototype of filter objects.
                         * The base filter object provides the "and", "or", and "not" functions to create a new filter object based on
                         * the current filter object and the input filter objects.
                         * Any concrete filter objects should extend the base filter object.
                         *
                         * @private
                         */
                        function __FilterObj (params) {
                            this._type = params.type;

                            this._condition = params.condition;

                            // Create value config.
                            this._values = dti.oasis.filter._protected._createFilterValuesConfig(params.values);

                            this.getType = function () {
                                return this._type;
                            };

                            this.getCondition = function () {
                                return this._condition;
                            };

                            this.getValues = function () {
                                return this._values;
                            };

                            /**
                             * Use "and" condition to combine the current filter object and the input filter objects, and return a new
                             * filter object. If all the filter functions of the filter objects returns true, the filter function of the
                             * new filter object returns true. Otherwise, it returns false.
                             *
                             * The input parameters can be one or more filter objects. For example:
                             * var newFilterObj = filterObj.and(filterObj1, filterObj2, filterObj3, ...);
                             *
                             * @returns {*} A new filter object.
                             */
                            this.and = function () {
                                return dti.oasis.filter._protected._and(this, dti.oasis.array.fromArguments(arguments));
                            };

                            /**
                             * Use "or" condition to combine the current filter object and the input filter objects, and return a new
                             * filter object. If any filter functions of the filter objects returns true, the filter function of the
                             * new filter object returns true. Otherwise, it returns false.
                             *
                             * The input parameters can be one or more filter objects. For example:
                             * var newFilterObj = filterObj.or(filterObj1, filterObj2, filterObj3, ...);
                             *
                             * @returns {*} A new filter object.
                             */
                            this.or = function () {
                                return dti.oasis.filter._protected._or(this, dti.oasis.array.fromArguments(arguments));
                            };

                            /**
                             * Use "not" condition to return a new filter object with opposite filter result. (If the filter function of
                             * the original filter object returns true, the new filter object returns false. Otherwise, it returns true.)
                             *
                             * Example:
                             * var newFilterObj = filterObj.not();
                             *
                             * @returns {*} A new filter object.
                             */
                            this.not = function () {
                                return dti.oasis.filter._protected._not(this);
                            };

                            /**
                             * Filter a record
                             * @param record
                             * @returns {Boolean} Returns true if the record meets the filter condition. Otherwise, returns false.
                             */
                            this.filter = function (record) {
                                switch (this._type) {
                                    case dti.oasis.filter.type.LOGICAL:
                                        switch (this._condition) {
                                            case dti.oasis.filter.condition.TRUE:
                                                return true;

                                            case dti.oasis.filter.condition.FALSE:
                                                return false;

                                            case dti.oasis.filter.condition.AND:
                                                for (var i = 0; i < this._values.length; i++) {
                                                    if (!this._values[i].filter(record)) {
                                                        return false;
                                                    }
                                                }

                                                return true;

                                            case dti.oasis.filter.condition.OR:
                                                for (var i = 0; i < this._values.length; i++) {
                                                    if (this._values[i].filter(record)) {
                                                        return true;
                                                    }
                                                }

                                                return false;

                                            case dti.oasis.filter.condition.NOT:
                                                return !this._values[0].filter(record);

                                            default:
                                                throw "Unsupported logical filter condition: " + this._condition + ".";
                                        }

                                    case dti.oasis.filter.type.COMPARE:
                                        var compareResult = dti.oasis.filter._protected._compareValues(record, this._values);

                                        switch (this._condition) {
                                            case dti.oasis.filter.condition.EQUAL:
                                                return (compareResult == 0);

                                            case dti.oasis.filter.condition.NOT_EQUAL:
                                                return (compareResult != 0);

                                            case dti.oasis.filter.condition.GREATER_THAN:
                                                return (compareResult > 0);

                                            case dti.oasis.filter.condition.GREATER_THAN_OR_EQUAL:
                                                return (compareResult >= 0);

                                            case dti.oasis.filter.condition.LESS_THAN:
                                                return (compareResult < 0);

                                            case dti.oasis.filter.condition.LESS_THAN_OR_EQUAL:
                                                return (compareResult <= 0);

                                            default:
                                                throw "Unsuportted compare filter condition: " + this._condition + ".";
                                        }

                                    case dti.oasis.filter.type.FUNCTION:
                                        switch (this._condition) {
                                            case dti.oasis.filter.filterFunction.CONTAINS:
                                                var value1 = dti.oasis.filter._protected._getValue(record, this._values[0]);
                                                var value2 = dti.oasis.filter._protected._getValue(record, this._values[1]);

                                                return (value1.indexOf(value2) != -1);

                                            case dti.oasis.filter.filterFunction.STARTS_WITH:
                                                var value1 = dti.oasis.filter._protected._getValue(record, this._values[0]);
                                                var value2 = dti.oasis.filter._protected._getValue(record, this._values[1]);

                                                return (value1.indexOf(value2) === 0);

                                            default:
                                                throw "Unsupported function filter: " + this._condition + ".";
                                        }

                                    case dti.oasis.filter.type.CUSTOM_FUNCTION:
                                        if (typeof this._condition == "string") {
                                            return eval(this._condition)(record);
                                        } else {
                                            return this._condition(record);
                                        }

                                    default:
                                        throw "Unsupported filter type: " + this._type + ".";
                                }

                            };

                            /**
                             * Check if the current filter object is a TRUE filter.
                             * @returns {*}
                             */
                            this.isTrueFilter = function () {
                                return dti.oasis.filter._protected._isTrueFilter(this);
                            };

                            /**
                             * Check if the current filter object is a FALSE filter.
                             * @returns {*}
                             */
                            this.isFalseFilter = function () {
                                return dti.oasis.filter._protected._isFalseFilter(this);
                            };

                            /**
                             * Check if the current object is a filter object.
                             * @returns {boolean}
                             */
                            this.isFilterObject = function () {
                                return true;
                            };

                            /**
                             * Get the properties of a filter object as a JSON object.
                             * @returns {{type: *, condition: *, values: *, isFilterObject: boolean}}
                             */
                            this.toJsonValue = function () {
                                function __valuesToJson(values) {
                                    var result = [];

                                    for (var i = 0; i < values.length; i++) {
                                        result[result.length] = values[i].toJsonValue();
                                    }

                                    return result;
                                }

                                var that = this;

                                return {
                                    "type": that.getType(),
                                    "condition": (typeof that.getCondition() == "function") ? "[Function]" : that.getCondition(),
                                    "values": __valuesToJson(that.getValues()),
                                    "isFilterObject": true
                                };
                            };

                            this.toString = function () {
                                return JSON.stringify(this.toJsonValue());
                            };
                        }

                        return new __FilterObj(params);
                    },

                    /**
                     * Use "and" condition to combine the current filter object and the input filter objects, and return a new
                     * filter object. If all the filter functions of the filter objects returns true, the filter function of the
                     * new filter object returns true. Otherwise, it returns false.
                     *
                     * The input parameters can be one or more filter objects. For example:
                     * var newFilterObj = filterObj.and(filterObj1, filterObj2, filterObj3, ...);
                     *
                     * @returns {*} A new filter object.
                     */
                    _and: function () {
                        var filters = this._processFiltersForAndCondition(arguments);

                        if (filters.length == 0) {
                            return dti.oasis.filter.TRUE;

                        } else if (filters.length == 1) {
                            return filters[0];

                        } else {
                            return this._createFilterObj({type: "LOGICAL", condition: "AND", values: filters});
                        }
                    },

                    /**
                     * Process the filter objects of a AND filter.
                     *
                     * @param filters
                     * @param processedFilters
                     * @returns {*}
                     * @private
                     */
                    _processFiltersForAndCondition: function (filters, processedFilters) {
                        if (typeof processedFilters == "undefined" || processedFilters == null) {
                            processedFilters = [];
                        }


                        for (var i = 0; i < filters.length; i++) {
                            if (Array.isArray(filters[i])) {
                                processedFilters = this._processFiltersForAndCondition(filters[i], processedFilters);

                                if (processedFilters.length > 1 && processedFilters[0].isFalseFilter()) {
                                    return [dti.oasis.filter.FALSE];
                                }

                            } else {
                                if (filters[i].isFalseFilter()) {
                                    return [dti.oasis.filter.FALSE];
                                }

                                if (!filters[i].isTrueFilter()) {
                                    processedFilters[processedFilters.length] = filters[i];
                                }
                            }
                        }

                        return processedFilters;
                    },

                    /**
                     * Use "or" condition to combine the current filter object and the input filter objects, and return a new
                     * filter object. If any filter functions of the filter objects returns true, the filter function of the
                     * new filter object returns true. Otherwise, it returns false.
                     *
                     * The input parameters can be one or more filter objects. For example:
                     * var newFilterObj = filterObj.or(filterObj1, filterObj2, filterObj3, ...);
                     *
                     * @returns {*} A new filter object.
                     */
                    _or: function () {
                        var filters = this._processFiltersForOrCondition(arguments);

                        if (filters.length == 0) {
                            return dti.oasis.filter.TRUE;

                        } else if (filters.length == 1) {
                            return filters[0];

                        } else {
                            return this._createFilterObj({type: "LOGICAL", condition: "OR", values: filters});
                        }
                    },

                    /**
                     * Process filter objects of a OR filter.
                     * @param filters
                     * @param processedFilters
                     * @returns {*}
                     * @private
                     */
                    _processFiltersForOrCondition: function (filters, processedFilters) {
                        if (typeof processedFilters == "undefined" || processedFilters == null) {
                            processedFilters = [];
                        }


                        for (var i = 0; i < filters.length; i++) {
                            if (Array.isArray(filters[i])) {
                                processedFilters = this._processFiltersForOrCondition(filters[i], processedFilters);

                                if (processedFilters.length > 0 && processedFilters[0].isTrueFilter()) {
                                    return [dti.oasis.filter.TRUE];
                                }

                            } else {
                                if (filters[i].isTrueFilter()) {
                                    return [dti.oasis.filter.TRUE];
                                }

                                if (filters[i].isFalseFilter()) {
                                    if (processedFilters.length == 0) {
                                        processedFilters[0] = dti.oasis.filter.FALSE;
                                    }

                                } else {
                                    if (processedFilters.length == 1 && filters[0].isFalseFilter()) {
                                        processedFilters[0] = filters[i];

                                    } else {
                                        processedFilters[processedFilters.length] = filters[i];
                                    }
                                }
                            }
                        }

                        return processedFilters;
                    },

                    _not: function (filter) {
                        if (typeof filter == "undefined" || filter == null) {
                            return dti.oasis.filter.TRUE;
                        }

                        if (filter.isTrueFilter(filter)) {
                            return dti.oasis.filter.FALSE;
                        }

                        if (filter.isFalseFilter(filter)) {
                            return dti.oasis.filter.TRUE;
                        }

                        return this._createFilterObj({type: "LOGICAL", condition: "NOT", values: [filter]});
                    },

                    /**
                     * Check if a filter is a TRUE filter.
                     * @param filter
                     * @returns {boolean}
                     */
                    _isTrueFilter: function (filter) {
                        if (typeof filter == "undefined" || filter == null) {
                            return false;
                        }

                        if (typeof dti.oasis.filter.TRUE != "undefined" && filter == dti.oasis.filter.TRUE) {
                            return true;
                        }

                        if (filter.hasOwnProperty["_type"] && filter._type == dti.oasis.filter.type.LOGICAL &&
                            filter.hasOwnProperty["_condition"] && filter._condition == dti.oasis.filter.condition.TRUE) {
                            return true;
                        }

                        return false;
                    },

                    /**
                     * Check if a filter is a FALSE filter.
                     * @param filter
                     * @returns {boolean}
                     */
                    _isFalseFilter: function (filter) {
                        if (typeof filter == "undefined" || filter == null) {
                            return false;
                        }

                        if (typeof dti.oasis.filter.FALSE != "undefined" && filter == dti.oasis.filter.FALSE) {
                            return true;
                        }

                        if (filter.hasOwnProperty["_type"] && filter._type == dti.oasis.filter.type.LOGICAL &&
                            filter.hasOwnProperty["_condition"] && filter._condition == dti.oasis.filter.condition.FALSE) {
                            return true;
                        }

                        return false;
                    },

                    /**
                     * Create the values config for a filter object.
                     * @param values
                     * @returns {Array}
                     */
                    _createFilterValuesConfig: function (values) {
                        if (typeof values == "undefined" || values == null) {
                            return [];
                        }

                        var filterValues = [];

                        for (var i = 0; i < values.length; i++) {
                            if (values[i].isFilterObject && values[i].isFilterObject()) {
                                filterValues[filterValues.length] = values[i];
                            } else {
                                filterValues[filterValues.length] = this._createFilterValueConfig(values[i]);
                            }
                        }

                        return filterValues;
                    },

                    /**
                     * Create a value config for filter.
                     * @param params
                     * @returns {*}
                     */
                    _createFilterValueConfig: function (params) {
                        function __FilterValueConfig(params) {
                            this._type = params.type;
                            this._functionName = params.functionName || null;
                            this._value = params.value;

                            this.getType = function () {
                                return this._type;
                            };

                            this.getFunctionName = function () {
                                return this._functionName;
                            };

                            this.getValue = function () {
                                return this._value;
                            };

                            this.getFilterValue = function (record) {
                                return dti.oasis.filter._protected._getValue(record, this);
                            };

                            this.isFilterValueConfig = function () {
                                return true;
                            };

                            this.toJsonValue = function () {
                                function __getFilterValue(value) {
                                    if (Array.isArray(value)) {
                                        var result = [];
                                        for (var i = 0; i < value.length; i++) {
                                            result[result.length] = result.toJsonValue();
                                        }
                                        return result;
                                    } else {
                                        return value;
                                    }
                                }

                                var that = this;

                                return {
                                    "type": that.getType(),
                                    "functionName": (typeof that.getFunctionName() == "undefined") ? null : that.getFunctionName(),
                                    "value": __getFilterValue(that.getValue()),
                                    isFilterValueConfig: true
                                }
                            };

                            this.toString = function () {
                                return JSON.stringify(this.toJsonValue());
                            };
                        }

                        if (typeof params == "undefined" || params == null) {
                            return new __FilterValueConfig({type: dti.oasis.filter.valueType.CONSTANT, value: ""});
                        }

                        switch (typeof params) {
                            case "string":
                            case "number":
                            case "boolean":
                                return new __FilterValueConfig({type: dti.oasis.filter.valueType.CONSTANT, value: params});
                        }

                        if (typeof params.type == "undefined" || dti.oasis.string.isEmpty(params.type)) {
                            params.type = dti.oasis.filter.valueType.CONSTANT;
                        }

                        if (params.type == dti.oasis.filter.valueType.FUNCTION) {
                            var values = [];

                            if (typeof params.value != "undefined" && params.value != null) {
                                for (var i = 0; i < params.value.length; i++) {
                                    values[values.length] = this._createFilterValueConfig(params.value[i]);
                                }
                            }

                            params.value = values;
                        }

                        return new __FilterValueConfig(params);
                    },

                    /**
                     * Compare values[0] and values[1] of a filter object.
                     * If values[0] equals to values[1], it returns 0;
                     * If values[0] is greater than values[1], it returns 1;
                     * Otherwise, it returns -1.
                     *
                     * @param record The current record to filter.
                     * @param valueConfigs Value configs.
                     * @returns {number}
                     */
                    _compareValues: function (record, valueConfigs) {
                        var value1 = valueConfigs[0].getFilterValue(record);
                        var value2 = valueConfigs[1].getFilterValue(record);

                        if (value1 == value2) {
                            return 0;
                        } else if (value1 > value2) {
                            return 1
                        } else {
                            return -1;
                        }
                    },

                    /**
                     * Get the value based on the value config of a filter object from a record.
                     * The value config could be a string, number, boolean, or object.
                     * If it's an object, the type attribute of the value config could be "COLUMN", "CONSTANT", or a "FUNCTION".
                     *
                     * Please see {@link dti.oasis.filter._protected._getValueByFunction} for details about "FUNCTION" type of values.
                     *
                     * @param record
                     * @param valueConfig
                     * @returns {*}
                     */
                    _getValue: function (record, valueConfig) {
                        if (typeof valueConfig == "string" ||
                            typeof valueConfig == "number" ||
                            typeof valueConfig == "boolean") {
                            return valueConfig;
                        }

                        switch (valueConfig.getType()) {
                            case dti.oasis.filter.valueType.CONSTANT:
                                return valueConfig.getValue();
                            case dti.oasis.filter.valueType.COLUMN:
                                var columnName = valueConfig.getValue();

                                //Set default column value as empty string
                                var value = "";

                                if (record.hasOwnProperty(columnName)) {
                                    value = record[columnName];
                                } else {
                                    for (var p in record) {
                                        if (record.hasOwnProperty(p) && p.toUpperCase() == columnName.toUpperCase()) {
                                            valueConfig._value = p;
                                            value = record[p];
                                            break;
                                        }
                                    }
                                }

                                return value;
                            case dti.oasis.filter.valueType.FUNCTION:
                                return this._getValueByFunction(record, valueConfig);
                        }
                    },

                    /**
                     * Get value from a record based on a function value config.
                     * A function type value config can have the following attributes:
                     * <ul>
                     *     <li><code>functionName</code> The name of a function. It could be the following values:
                     *         <ul>
                     *             <li><code>uppercase</code>Get the valueConfig.values[0] value, and convert it to uppercase.</li>
                     *             <li><code>lowercase</code>Get the valueConfig.values[0] value, and convert it to lowercase.</li>
                     *             <li><code>number</code>Get the valueConfig.values[0] value, and convert it to number.</li>
                     *             <li><code>concat</code>Get the valueConfig.values value, and combine then to a new string.</li>
                     *             <li><code>substring</code>Get the valueConfig.values[0] value, and get the substring of the value.</li>
                     *             <li><code>translate</code>Get the valueConfig.values[0] value, and replace the occurrence of values[1] value with values[2] value.</li>
                     *         </ul>
                     *     </li>
                     * </ul>
                     *
                     * Example:
                     * var filter1 = dti.oasis.filter.createFilter({
                     *     values: [
                     *         {
                     *             type: dti.oasis.filter.valueType.FUNCTION,
                     *             functionName: dti.oasis.filter.valueFunction.UPPERCASE,
                     *             value: [
                     *                 {
                     *                     type: dti.oasis.filter.valueType.COLUMN,
                     *                     value: "name"
                     *                 }
                     *             ]
                     *         },
                     *         "KYLE"
                     *      ]
                     * });
                     *
                     * @param record
                     * @param valueConfig
                     * @returns {*}
                     */
                    _getValueByFunction: function (record, valueConfig) {
                        var fnName = valueConfig.getFunctionName();

                        switch (fnName) {
                            case dti.oasis.filter.valueFunction.UPPERCASE:
                                var value = this._getValue(record, valueConfig.getValue()[0]);
                                if (dti.oasis.string.isEmpty(value)) {
                                    return "";
                                } else {
                                    return value.toUpperCase();
                                }
                            case dti.oasis.filter.valueFunction.LOWERCASE:
                                var value = this._getValue(record, valueConfig.getValue()[0]);
                                if (dti.oasis.string.isEmpty(value)) {
                                    return "";
                                } else {
                                    return value.toLowerCase();
                                }
                            case dti.oasis.filter.valueFunction.NUMBER:
                                var value = this._getValue(record, valueConfig.getValue()[0]);
                                if (dti.oasis.string.isEmpty(value)) {
                                    return Number.NEGATIVE_INFINITY;
                                } else {
                                    return Number(value);
                                }
                            case dti.oasis.filter.valueFunction.CONCAT:
                                var value = "";
                                for (var i = 0; i < valueConfig.getValue().length; i++) {
                                    value = value.concat(this._getValue(record, valueConfig.getValue()[i]));
                                }
                                return value;
                            case dti.oasis.filter.valueFunction.SUBSTRING:
                                // The xPath substring index starts from 1.
                                var value = this._getValue(record, valueConfig.getValue()[0]);
                                if (valueConfig.getValue().length > 2) {
                                    return value.substr(this._getValue(record, valueConfig.getValue()[1]) - 1, this._getValue(record, valueConfig.getValue()[2]));
                                } else {
                                    return value.substr(this._getValue(record, valueConfig.getValue()[1]) - 1);
                                }
                            case dti.oasis.filter.valueFunction.TRANSLATE:
                                var value1 = this._getValue(record, valueConfig.getValue()[0]);
                                var value2 = this._getValue(record, valueConfig.getValue()[1]);
                                var value3 = this._getValue(record, valueConfig.getValue()[2]);

                                // The version of the xPath we used in oasis javascript doesn't support uppercase and lowercase.
                                // It used translate function to convert string to uppercase or lowercase.
                                // We can simply use toLowerCase and toUpperCase in this case.
                                if (value2 == "ABCDEFGHIJKLMNOPQRSTUVWXYZ" && value3 == "abcdefghijklmnopqrstuvwxyz") {
                                    return value1.toLowerCase();
                                }

                                if (value2 == "abcdefghijklmnopqrstuvwxyz" && value3 == "ABCDEFGHIJKLMNOPQRSTUVWXYZ") {
                                    return value1.toUpperCase();
                                }

                                return value1.split(value2).join(value3);
                        }
                    },

                    /**
                     * Analysis an xPath style filter string, and split them to a tokens array.
                     *
                     * For example, the filter string "//ROW[DISPLAY_IND = 'Y' and UPDATE_IND != 'D']" will be translated
                     * to the following array:
                     * ["DISPLAY_IND", "=", "'Y'", "and", "UPDATE_IND", "!", "=", "'D'"]
                     *
                     * @param filterString
                     * @returns {Array}
                     * @private
                     */
                    _tokenize: function (filterString) {
                        var tokens = [];
                        var length = filterString.length;
                        var startSubstring = -1;
                        var isStartOfPattern = true;
                        var nesting = false;
                        var isNumber = false;
                        var isAttributeName = false;

                        for (var i = 0; i < length; i++) {
                            var c = filterString.charAt(i);

                            switch (c) {
                                case '/':
                                case '[':
                                case ']':
                                case '(':
                                case ')':
                                case '=':
                                case '!':
                                case '>':
                                case '<':
                                case ',':
                                    if (startSubstring == -1) {
                                        if (c == '/') {
                                            if (!nesting) {
                                                isStartOfPattern = false;
                                            }
                                        }
                                    } else {
                                        isNumber = false;
                                        isStartOfPattern = false;
                                        isAttributeName = false;

                                        tokens[tokens.length] = filterString.substring(startSubstring, i);
                                        startSubstring = -1;
                                    }

                                    tokens[tokens.length] = c;
                                    break;
                                case '\'':
                                    if (startSubstring != -1) {
                                        isNumber = false;
                                        isStartOfPattern = false;
                                        isAttributeName = false;

                                        tokens[tokens.length] = filterString.substring(startSubstring, i);
                                        startSubstring = -1;
                                    }

                                    startSubstring = i;

                                    for (i++; i < length; i++) {
                                        c = filterString.charAt(i);
                                        if (c == '\'') {
                                            break;
                                        }
                                    }

                                    if (c == '\'' && i < length) {
                                        tokens[tokens.length] = filterString.substring(startSubstring, i + 1);
                                        startSubstring = -1;
                                    } else {
                                        throw "Invalid filter string: expected single quote.";
                                    }

                                    break;
                                case '"':
                                    if (startSubstring != -1) {
                                        isNumber = false;
                                        isStartOfPattern = false;
                                        isAttributeName = false;

                                        tokens[tokens.length] = filterString.substring(startSubstring, i);
                                        startSubstring = -1;
                                    }

                                    startSubstring = i;

                                    for (i++; i < length; i++) {
                                        c = filterString.charAt(i);
                                        if (c == '"') {
                                            break;
                                        }
                                    }

                                    if (c == '"' && i < length) {
                                        tokens[tokens.length] = filterString.substring(startSubstring, i + 1);
                                        startSubstring = -1;
                                    } else {
                                        throw "Invalid filter string: expected double quote.";
                                    }

                                    break;
                                case ' ':
                                    if (startSubstring != -1) {
                                        isNumber = false;
                                        isStartOfPattern = false;
                                        isAttributeName = false;

                                        tokens[tokens.length] = filterString.substring(startSubstring, i);
                                        startSubstring = -1;
                                    }
                                    break;
                                default:
                                    if (startSubstring == -1) {
                                        startSubstring = i;
                                        isNumber = /^\d$/.test(c);
                                    } else if (isNumber) {
                                        isNumber = /^\d$/.test(c);
                                    }
                            }
                        }

                        if (startSubstring != -1) {
                            tokens[tokens.length] = filterString.substring(startSubstring);
                        }

                        return tokens;
                    },

                    /**
                     * Analysis the filter tokens, and translate it to an operation tree.
                     * The value of a token tree node is an operation or function if it's not a leaf node.
                     * If it's a leaf node, it could be a constant or a column name.
                     *
                     * For example, the following token array:
                     * ["DISPLAY_IND", "=", "'Y'", "and", "UPDATE_IND", "!", "=", "'D'"]
                     *
                     * will be translate it to:
                     * {
                     *     value: "and",
                     *     children: [
                     *         {
                     *             value: "=",
                     *             children: [
                     *                 "DISPLAY_IND",
                     *                 "'Y'"
                     *             ]
                     *         },
                     *         {
                     *             value: "!=",
                     *             children: [
                     *                 "UPDATE_IND",
                     *                 "'D'"
                     *             ]
                     *         }
                     *     ]
                     * }
                     *
                     * @param tokens
                     * @returns {{}}
                     * @private
                     */
                    _processTokens: function (tokens) {
                        var valueStack = [];
                        var operatorStack = [];

                        while (tokens.length > 0) {
                            var currentToken = tokens.shift();

                            if (currentToken === "]") {
                                //Stop processing token in case filterString is like "//ROW[@id ='1234']/CRESERVEB"
                                break;
                            }

                            switch (currentToken) {
                                case "/":
                                case "ROW":
                                case "[":
                                    // The tokens to be skipped.
                                    continue;
                                case "!":
                                case ">":
                                case "<":
                                    if (tokens[0] === "=") {
                                        // To check if it's "!=", ">=", or "<="
                                        tokens.shift();
                                        currentToken = currentToken.concat("=");
                                    }
                            }
                            this._processToken(tokens, currentToken, valueStack, operatorStack);
                        }

                        while (dti.oasis.array.peek(operatorStack)) {
                            this._processOperator(operatorStack.pop(), valueStack);
                        }

                        //Return empty object for filterString like "//ROW"
                        return dti.oasis.array.peek(valueStack) ? valueStack.pop() : {};
                    },

                    /**
                     * Use Shunting-yard algorithm to process token.
                     */
                    _processToken: function (tokens, token, valueStack, operatorStack) {
                        if (this._isFunction(token)) {
                            valueStack.push(this._getValueTokenTree(tokens, token));
                        } else if (this._isOperator(token)) {
                            //Check if top element in operatorStack is operator as it can be left parenthesis
                            while (dti.oasis.array.peek(operatorStack) && (this._isOperator(dti.oasis.array.peek(operatorStack))) &&
                            //Process operator while the token's precedence is less than or equal to that one at the top of the operator stack.
                            this._getOperatorPrecedence(token) <= this._getOperatorPrecedence(dti.oasis.array.peek(operatorStack))) {

                                this._processOperator(operatorStack.pop(), valueStack);
                            }
                            //At the end of iteration, push token onto the operator stack
                            operatorStack.push(token);
                        } else if (token === "(") {
                            operatorStack.push(token);
                        } else if (token === ")") {
                            //Until the token at the top of the stack is a left parenthesis, pop operators off the stack onto the value stack.
                            while (dti.oasis.array.peek(operatorStack) !== "(") {
                                this._processOperator(operatorStack.pop(), valueStack);
                            }

                            if (operatorStack.length === 0 || dti.oasis.array.peek(operatorStack) !== "(") {
                                throw "Unmatched parentheses.";
                            }

                            //Pop the left parenthesis from the stack.
                            operatorStack.pop();
                        } else {
                            //If the token is not function, operator or parenthesis, then push it to the value stack
                            valueStack.push(token);
                        }
                    },

                    _processOperator: function (operator, valueStack) {
                        var tokenTree;
                        if (operator === "not") {
                            //The tokenTree of "not" operator only has one child. Its filterString is like "not(CSELECT_IND = '-1')"
                            tokenTree = {value: operator, children: [valueStack.pop()]};
                        } else {
                            var rightChild = valueStack.pop();
                            var leftChild = valueStack.pop();
                            tokenTree = {value: operator, children: [leftChild, rightChild]};
                        }

                        valueStack.push(tokenTree);
                    },

                    _isOperator: function (token) {
                        switch (token) {
                            case ">":
                            case "<":
                            case "=":
                            case "!=":
                            case ">=":
                            case "<=":
                            case "not":
                            case "and":
                            case "or":
                                return true;
                        }
                        return false;
                    },

                    _isFunction: function (token) {
                        switch (token) {
                            case "contains":
                            case "uppercase":
                            case "lowercase":
                            case "number":
                            case "translate":
                            case "concat":
                            case "substring":
                            case "starts-with":
                                return true;
                        }
                        return false;
                    },

                    _getOperatorPrecedence: function (operator) {
                        var precedence;
                        switch (operator) {
                            case ">":
                            case "<":
                            case "!=":
                            case ">=":
                            case "<=":
                            case "=":
                            case "not":
                                precedence = 3;
                                break;
                            case "and":
                                precedence = 2;
                                break;
                            case "or":
                                precedence = 1;
                                break;
                        }
                        return precedence;
                    },

                    /**
                     * Set the token tree node value.
                     * @param tokenTree
                     * @param value
                     * @returns {*}
                     * @private
                     */
                    _setTokenTreeValue: function (tokenTree, value) {
                        if (tokenTree.hasOwnProperty("value")) {
                            tokenTree = {value: value, children:[tokenTree]};
                        } else {
                            tokenTree.value = value;
                        }

                        return tokenTree;
                    },

                    /**
                     * Add a child node to a filter token tree node.
                     * @param tokenTree
                     * @param childToken
                     * @private
                     */
                    _addChildTokenTree: function (tokenTree, childToken) {
                        if (!tokenTree.hasOwnProperty("children")) {
                            tokenTree.children = [];
                        }

                        if (typeof childToken == "string" || childToken.hasOwnProperty("children")) {
                            tokenTree.children.push(childToken);
                        } else {
                            tokenTree.children.push(childToken.value);
                        }
                    },

                    /**
                     * Read tokens from a filter token array to compile a value node, and return the value token node.
                     *
                     * If the current token if a function name, it will process to get a function node. The following
                     * supported functions are "uppercase", "lowercase", "number", "translate", "concat", and "substring".
                     *
                     * For example, the current tokens is:
                     * ["uppercase", "(", "name", ")", "=", ...]
                     *
                     * The return token node is:
                     * {
                     *     value: "uppercase",
                     *     children: [
                     *         "name"
                     *     ]
                     * }
                     *
                     * The tokens will be changed to:
                     * ["=", ...]
                     *
                     * @param tokens
                     * @param token
                     * @returns {*}
                     * @private
                     */
                    _getValueTokenTree: function (tokens, token) {
                        switch (token) {
                            case "uppercase":
                            case "lowercase":
                            case "number":
                                // Process one argument functions
                                return this._getFunctionTokenTree(tokens, token, 1);
                            case "translate":
                                // Process three argument functions
                                return this._getFunctionTokenTree(tokens, token, 3);
                            case "concat":
                                // Process multi argument functions
                                var valueTokenTree = this._getFunctionTokenTree(tokens, token);

                                if (valueTokenTree.children.length < 2) {
                                    throw "Invalid filter string: illegal arguments of function concat.";
                                }

                                return valueTokenTree;
                            case "substring":
                                // Two or three arguments
                                var valueTokenTree = this._getFunctionTokenTree(tokens, token);

                                if (valueTokenTree.children.length != 2 && valueTokenTree.children.length != 3) {
                                    throw "Invalid filter string: illegal arguments of function substring.";
                                }

                                return valueTokenTree;
                            case "contains":
                                return this._getFunctionTokenTree(tokens, token, 2);
                            case "starts-with":
                                return this._getFunctionTokenTree(tokens, token, 2);
                            default:
                                return token;
                        }
                    },

                    /**
                     * Read tokens from a filter token array to compile a function node, and return the function token node.
                     *
                     * @param tokens
                     * @param token
                     * @param numberOfArguments The expected arguments of the function. If the value of numberOfArguments
                     * is not provided, the function will don't do the argument number check.
                     * @returns {{value: *, children: Array}}
                     * @private
                     */
                    _getFunctionTokenTree: function (tokens, token, numberOfArguments) {
                        var tokenTree = {value: token, children: []};
                        var firstParameter = true;

                        if (tokens.shift() != "(") {
                            throw "Invalid filter string: illegal arguments of function " + token;
                        }

                        while (this._hasNextFunctionParameterToken(tokens, token, firstParameter)) {
                            this._addChildTokenTree(tokenTree, this._getNextFunctionParameter(tokens, token, firstParameter));

                            if (firstParameter) {
                                firstParameter = false;
                            }
                        }

                        if (tokens.shift() != ")") {
                            throw "Invalid filter string: illegal arguments of function " + token;
                        }

                        if (typeof numberOfArguments == "number") {
                            if (tokenTree.children.length != numberOfArguments) {
                                throw "Invalid filter string: illegal arguments of function " + token;
                            }
                        }

                        return tokenTree;
                    },

                    /**
                     * Check if the tokes array has more function parameters.
                     *
                     * @param tokens
                     * @param token
                     * @param firstParameter
                     * @returns {boolean}
                     * @private
                     */
                    _hasNextFunctionParameterToken: function (tokens, token, firstParameter) {
                        var nextToken = tokens[0];

                        if (nextToken == ")") {
                            return false;
                        }

                        if (!firstParameter) {
                            if (nextToken != ",") {
                                throw "Invalid filter string: illegal arguments of function " + token;
                            }

                            nextToken = tokens[1];
                        }

                        if (nextToken == "(" || nextToken == ",") {
                            throw "Invalid filter string: illegal arguments of function " + token;
                        }

                        return (nextToken != ")");
                    },

                    /**
                     * Get the next function parameter in the filter tokens array.
                     *
                     * A function parameter node could also be a function tree node.
                     *
                     * @param tokens
                     * @param token
                     * @param firstParameter
                     * @returns {*}
                     * @private
                     */
                    _getNextFunctionParameter: function (tokens, token, firstParameter) {
                        if (!firstParameter) {
                            tokens.shift();
                        }

                        return this._getValueTokenTree(tokens, tokens.shift());
                    },

                    /**
                     * Compile a filter token tree to a filter object.
                     *
                     * @param tokenTree
                     * @returns {*}
                     * @private
                     */
                    _compileFilter: function (tokenTree) {
                        var operation = tokenTree.value || "";

                        switch (operation) {
                            case "and":
                                var filter = null;

                                for(var i = 0; i < tokenTree.children.length; i++) {
                                    if (filter == null) {
                                        filter = this._compileFilter(tokenTree.children[i]);
                                    } else {
                                        filter = filter.and(this._compileFilter(tokenTree.children[i]));
                                    }
                                }

                                return filter;
                            case "or":
                                var filter = null;

                                for(var i = 0; i < tokenTree.children.length; i++) {
                                    if (filter == null) {
                                        filter = this._compileFilter(tokenTree.children[i]);
                                    } else {
                                        filter = filter.or(this._compileFilter(tokenTree.children[i]));
                                    }
                                }

                                return filter;
                            case "not":
                                return this._compileFilter(tokenTree.children[0]).not();
                            case "=":
                                var value1 = this._getTokenValueForFilter(tokenTree.children[0]);
                                var value2 = this._getTokenValueForFilter(tokenTree.children[1]);

                                return dti.oasis.filter.createFilter({
                                    values: [value1, value2]
                                });
                            case "!=":
                                var value1 = this._getTokenValueForFilter(tokenTree.children[0]);
                                var value2 = this._getTokenValueForFilter(tokenTree.children[1]);

                                return dti.oasis.filter.createFilter({
                                    condition: dti.oasis.filter.condition.NOT_EQUAL,
                                    values: [value1, value2]
                                });
                            case ">":
                                var value1 = this._getTokenValueForFilter(tokenTree.children[0]);
                                var value2 = this._getTokenValueForFilter(tokenTree.children[1]);
                                return dti.oasis.filter.createFilter({
                                    condition: dti.oasis.filter.condition.GREATER_THAN,
                                    values: [value1, value2]
                                });
                            case ">=":
                                var value1 = this._getTokenValueForFilter(tokenTree.children[0]);
                                var value2 = this._getTokenValueForFilter(tokenTree.children[1]);
                                return dti.oasis.filter.createFilter({
                                    condition: dti.oasis.filter.condition.GREATER_THAN_OR_EQUAL,
                                    values: [value1, value2]
                                });
                            case "<":
                                var value1 = this._getTokenValueForFilter(tokenTree.children[0]);
                                var value2 = this._getTokenValueForFilter(tokenTree.children[1]);
                                return dti.oasis.filter.createFilter({
                                    condition: dti.oasis.filter.condition.LESS_THAN,
                                    values: [value1, value2]
                                });
                            case "<=":
                                var value1 = this._getTokenValueForFilter(tokenTree.children[0]);
                                var value2 = this._getTokenValueForFilter(tokenTree.children[1]);
                                return dti.oasis.filter.createFilter({
                                    condition: dti.oasis.filter.condition.LESS_THAN_OR_EQUAL,
                                    values: [value1, value2]
                                });
                            case "contains":
                                var value1 = this._getTokenValueForFilter(tokenTree.children[0]);
                                var value2 = this._getTokenValueForFilter(tokenTree.children[1]);
                                return dti.oasis.filter.createFunctionFilter({
                                    condition: dti.oasis.filter.filterFunction.CONTAINS,
                                    values: [value1, value2]
                                });
                            case "starts-with":
                                var value1 = this._getTokenValueForFilter(tokenTree.children[0]);
                                var value2 = this._getTokenValueForFilter(tokenTree.children[1]);
                                return dti.oasis.filter.createFunctionFilter({
                                    condition: dti.oasis.filter.filterFunction.STARTS_WITH,
                                    values: [value1, value2]
                                });
                        }
                    },

                    /**
                     * Get the filter object value config from a filter value/function token node.
                     *
                     * @param tokenTree
                     * @returns {*}
                     * @private
                     */
                    _getTokenValueForFilter: function (tokenTree) {
                        var val;
                        if (typeof tokenTree == "string") {
                            val = tokenTree;
                        } else {
                            val = tokenTree.value;
                        }

                        switch (val) {
                            case "uppercase":
                            case "lowercase":
                            case "number":
                            case "concat":
                            case "substring":
                            case "translate":
                                // functions
                                var values = [];

                                for (var i = 0; i < tokenTree.children.length; i++) {
                                    values[values.length] = this._getTokenValueForFilter(tokenTree.children[i]);
                                }

                                return {
                                    type: dti.oasis.filter.valueType.FUNCTION,
                                    functionName: val,
                                    value: values
                                };
                            default:
                                if ((dti.oasis.string.startsWith(val, "'") && dti.oasis.string.endsWith(val, "'")) ||
                                    (dti.oasis.string.startsWith(val, "\"") && dti.oasis.string.endsWith(val, "\""))) {
                                    // string
                                    return val.substring(1, val.length -1);
                                } else if (dti.oasis.string.isNumberString(val)) {
                                    // number
                                    return Number(val);
                                } else {
                                    // column
                                    return {value: val, type: dti.oasis.filter.valueType.COLUMN}
                                }
                                break;
                        }
                    }
                };
            })()
        }
    })();
}

if (typeof dti.oasis.filter.TRUE == "undefined") {
    dti.oasis.filter.TRUE = dti.oasis.filter._protected._createFilterObj({type: "LOGICAL", condition: "TRUE"});
}

if (typeof dti.oasis.filter.FALSE == "undefined") {
    dti.oasis.filter.FALSE = dti.oasis.filter._protected._createFilterObj({type: "LOGICAL", condition: "FALSE"});
}

