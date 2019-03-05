/*
 Revision Date    Revised By  Description
 ----------------------------------------------------------------------------
 ----------------------------------------------------------------------------
 */

if (typeof dti.oasis.page == "undefined") {
    dti.oasis.page = (function () {
        return {
            scope: {
                "pageProperty": {},

                "gridIds": [],

                /**
                 * Grid info map:
                 * key: "#" + gridId
                 * value: gridInfo
                 */
                "grids": {},

                /**
                 * An array of grid properties
                 */
                "gridPropertyArray": []
            },

            _protected: (function () {
                return {
                    _addGridId: function (gridId) {
                        var gridIds = dti.oasis.page.getProperty("gridIds");

                        if (gridIds == null) {
                            gridIds = [];
                            dti.oasis.page.setProperty("gridIds", gridIds);
                        }

                        for (var i = 0; i < gridIds.length; i++) {
                            if (gridId == gridIds[i]) {
                                return;
                            }
                        }

                        gridIds[gridIds.length] = gridId;
                    }
                };
            })(),

            getJqxGridId: function (gridId) {
                // JqxGridId value is not supposed to have the suffix "1".
                // However some of the framework function that gets the dataSrc from the linked form field gets the gridId
                // with a suffix of "1". For such cases, remove the suffix "1" for JqxGrid implementation.
                if (gridId.substring(gridId.length-1) == "1") {
                    return gridId.substring(0, gridId.length-1);
                } else {
                    return gridId;
                }
            },

            getGridInfo: function (gridId) {
                return this.scope["grids"]["#".concat(dti.oasis.page.getJqxGridId(gridId))] || null;
            },

            setGridInfo: function (gridId, gridInfo) {
                gridId = dti.oasis.page.getJqxGridId(gridId);
                this._protected._addGridId(gridId);

                this.scope["grids"]["#".concat(gridId)] = gridInfo;
            },

            getProperty: function (property) {
                if (this.scope.hasOwnProperty("pageProperty") &&
                    this.scope["pageProperty"].hasOwnProperty(property)) {
                    return this.scope["pageProperty"][property];
                }
                return null;
            },

            setProperty: function (property, value) {
                if (!this.scope.hasOwnProperty("pageProperty")) {
                    this.scope["pageProperty"] = {};
                }

                this.scope["pageProperty"][property] = value;
            },

            /**
             * Initialize and load the grids on the page. It's called in footer.jsp/footerpopup.jsp.
             */
            initAndLoadGrids: function() {
                var gridIds = this.getProperty("gridIds");
                if (gridIds) {
                    for (var i = 0; i < gridIds.length; i++) {
                        dti.oasis.grid.initAndLoadGrid({"gridInfo": dti.oasis.page.getGridInfo(gridIds[i])});
                    }
                }
            },

            getGridPropertyArray: function () {
                return this.scope["gridPropertyArray"];
            },

            addGridIdToFireSelectFirstRowInGrid: function (gridId) {
                var gridIds = this.getGridIdToFireSelectFirstRowInGrid();

                for (var i = 0; i <gridIds.length; i++) {
                    if (gridIds[i] == gridId) {
                        return;
                    }
                }

                gridIds[gridIds.length] = gridId;
            },

            getGridIdToFireSelectFirstRowInGrid: function () {
                var gridIds = this.getProperty("gridIdToFireSelectFirstRowInGrid");

                if (gridIds == null) {
                    gridIds = [];
                    this.setProperty("gridIdToFireSelectFirstRowInGrid", gridIds);
                }
                return gridIds;
            },

            isPageGridsDataChanged: function () {
                if (this.scope.hasOwnProperty("grids")) {
                    var grids = this.scope["grids"];

                    for (var p in grids) {
                        if (grids.hasOwnProperty(p) && grids[p] != null) {
                            var gridId = this.scope["grids"][p]["id"];

                            if (dti.oasis.grid.isGridDataChanged(gridId)) {
                                return true;
                            }
                        }
                    }
                }

                return false;
            },

            useJqxGrid: function () {
                return (typeof useJqxGrid != "undefined" && useJqxGrid);
            },

            getCustomPageOption: function (propertyName) {
                var customPageOptions = this.getCustomPageOptions();

                if (customPageOptions && customPageOptions["#pageOptions"] && customPageOptions["#pageOptions"].hasOwnProperty(propertyName)) {
                    return customPageOptions["#pageOptions"][propertyName];
                }

                return null;
            },

            getCustomPageOptions: function () {
                var customPageOptions = this.getProperty("customPageOptions");

                if (!customPageOptions) {
                    if (window["handleGetCustomPageOptions"]) {
                        customPageOptions = handleGetCustomPageOptions();
                        this.setProperty("customPageOptions", customPageOptions);
                    }
                }

                return customPageOptions;
            },

            /**
             *
             * @param gridIds The grids to be initialized.
             * @returns A custom page option object with helper functions.
             */
            newCustomPageOptions: function (gridIds) {
                var pageOptions = {
                    _protected: {
                        _getGridOptions: function (pageOptions, gridId) {
                            var gridOptions;

                            if (pageOptions["#" + gridId]) {
                                gridOptions = pageOptions["#" + gridId];
                            } else {
                                pageOptions["#" + gridId] = gridOptions = {"columns": {}};
                            }

                            return gridOptions;
                        },

                        _getColumnOptions: function (pageOptions, gridId, columnName) {
                            var gridOptions = this._getGridOptions(pageOptions, gridId),
                                gridColumnOptions,
                                columnOptions;

                            if (gridOptions["columns"]) {
                                gridColumnOptions = gridOptions["columns"];
                            } else {
                                gridOptions["columns"] = gridColumnOptions = {};
                            }

                            if (gridColumnOptions.hasOwnProperty(columnName)) {
                                columnOptions = gridColumnOptions[columnName];
                            } else {
                                gridColumnOptions[columnName] = columnOptions = {};
                            }

                            return columnOptions;
                        }
                    },

                    /**
                     *
                     * @param gridId The grid to be handle.
                     * @param columns The column to be displayed as negative number style.
                     * @param indColumns Optional. If the parameter {indColumns} is provided, the system will check if
                     * the cell value of the column {indColumns} is negative to display the corresponding {columns}
                     * as negative number style.
                     * @returns {pageOptions}
                     */
                    addGetCellStyleFunctionForNegativeNumber: function (gridId, columns, indColumns) {
                        function __getCellStyleFn(gridId, columnName, indColumn) {
                            return function (jqxRowId, cellValue) {
                                return dti.oasis.grid.getCellStyleForNegativeNumber(
                                    gridId, jqxRowId, columnName, cellValue, indColumn
                                );
                            };
                        }

                        for (var i = 0; i < columns.length; i++) {
                            var columnOptions = this._protected._getColumnOptions(this, gridId, columns[i]);

                            if (typeof indColumns === "undefined") {
                                columnOptions["getCellStyle"] = __getCellStyleFn(gridId, columns[i]);
                            } else {
                                columnOptions["getCellStyle"] = __getCellStyleFn(gridId, columns[i], indColumns[i]);
                            }
                        }

                        return this;
                    },

                    addAggregateSetting: function (gridId, columnName, aggregates, aggregatesRenderer) {
                        var columnOptions = this._protected._getColumnOptions(this, gridId, columnName);

                        columnOptions["aggregates"] = aggregates;
                        if (aggregatesRenderer) {
                            columnOptions["aggregatesRenderer"] = aggregatesRenderer;
                        }

                        return this;
                    },

                    onBeforeGridSourceLoadComplete: function (gridId, callbackFn) {
                        this.addGridProperty(gridId, "onBeforeSourceLoadComplete", callbackFn);

                        return this;
                    },

                    onGridSourceLoadComplete: function (gridId, callbackFn) {
                        this.addGridProperty(gridId, "onSourceLoadComplete", callbackFn);

                        return this;
                    },

                    addIsRowEditableFunction: function (gridId, callbackFn) {
                        this.addGridProperty(gridId, "isRowEditable", callbackFn);

                        return this;
                    },

                    cellBeginEdit: function (gridId, columnName, callbackFn) {
                        this.addColumnProperty(gridId, columnName, "cellBeginEdit", callbackFn);

                        return this;
                    },

                    cellsRenderer: function (gridId, columnName, callbackFn) {
                        this.addColumnProperty(gridId, columnName, "cellsRenderer", callbackFn);

                        return this;
                    },

                    addIsCellEditableFunction: function (gridId, columnName, callbackFn) {
                        this.addColumnProperty(gridId, columnName, "isCellEditable", callbackFn);

                        return this;
                    },

                    addGetCellStyleFunction: function (gridId, columnName, callbackFn) {
                        this.addColumnProperty(gridId, columnName, "getCellStyle", callbackFn);

                        return this;
                    },

                    addGroupingSetting: function(gridId, columnName) {
                        this.addColumnProperty(gridId, columnName, "cellValueFormatterForCellsRender", {
                            format: function  (gridId, jqxRowIndex, columnName, cellValue) {
                                if (cellValue === "-") {
                                    return "<IMG src=\"" + getCorePath() + "/images/minus.gif\" border=0>";
                                } else if (cellValue === "+") {
                                    return "<IMG src=\"" + getCorePath() + "/images/plus.gif\" border=0>";
                                } else {
                                    return "";
                                }
                            }
                        })
                            .addColumnProperty(gridId, columnName, "enabletooltips", false)
                            .addColumnProperty(gridId, columnName, "menu", false)
                            .addColumnProperty(gridId, columnName, "filterable", false)
                            .addColumnProperty(gridId, columnName, "sortable", false)
                            .addColumnProperty(gridId, columnName, "width", 30);

                        return this;
                    },

                    addGridProperty: function (gridId, propertyName, val) {
                        var gridOptions = this._protected._getGridOptions(this, gridId);

                        gridOptions[propertyName] = val;

                        return this;
                    },

                    setSelectIndColumnName: function (gridId, selectIndColumnName) {
                        this.addGridProperty(gridId, "CSELECT_IND", selectIndColumnName);
                        return this;
                    },

                    addColumnProperty: function (gridId, columnName, propertyName, val) {
                        var columnOptions = this._protected._getColumnOptions(this, gridId, columnName);

                        columnOptions[propertyName] = val;

                        return this;
                    },

                    withGridFilterable: function (gridId, filterable) {
                        this.addGridProperty(gridId, "filterable", filterable);

                        return this;
                    },

                    addCustomPageOption: function (propertyName, val) {
                        var customPageOption = this["#pageOptions"];
                        if (!customPageOption) {
                            customPageOption = {};
                            this["#pageOptions"] = customPageOption;
                        }
                        customPageOption[propertyName] = val;
                    },

                    enableDependencyDropdownListColumn:  function () {
                        this.addCustomPageOption("dependencyDropdownListColumnEnabled", true);

                        return this;
                    },

                    addSelectAllEnabledFunction: function (gridId, callbackFn) {
                        this.addGridProperty(gridId, "selectAllEnabled", callbackFn);

                        return this;
                    },

                    onDropdownListItemSelect:  function (gridId, callbackFn) {
                        this.addGridProperty(gridId, "onDropdownListItemSelect", callbackFn);
                        return this;
                    },

                    addDropdownListRenderer: function (gridId, callbackFn) {
                        this.addGridProperty(gridId, "dropdownListRenderer", callbackFn);
                        return this;
                    },

                    addDropdownListSelectionRenderer: function (gridId, callbackFn) {
                        this.addGridProperty(gridId, "dropdownListSelectionRenderer", callbackFn);
                        return this;
                    },

                    addPreSelectRowByUserFunction: function (gridId, callbackFn) {
                        this.addGridProperty(gridId, "preSelectRowByUser", callbackFn);

                        return this;
                    },

                    addPostSelectRowByUserFunction: function (gridId, callbackFn) {
                        this.addGridProperty(gridId, "postSelectRowByUser", callbackFn);

                        return this;
                    }
                };

                // Init grid columns.
                if (typeof gridIds !== "undefined") {
                    if (typeof gridIds === "string") {
                        pageOptions["#" + gridIds] = {"columns": {}};
                    } else if ($.isArray(gridIds)) {
                        for (var i = 0; i < gridIds.length; i++) {
                            pageOptions["#" + gridIds[i]] = {"columns": {}};
                        }
                    }
                }

                return pageOptions;
            }
        };
    })();
}

if (typeof dti.oasis.field == "undefined") {
    dti.oasis.field = (function () {
        return {
            fieldFormat: (function () {
                return {
                    _protected: {
                        _createNullFormatter: function (field) {
                            return {
                                formatStringValue: function (val) {
                                    return val;
                                },

                                formattedValue: function () {
                                    return dti.oasis.field.getFieldValue(field);
                                },

                                format: function () {
                                },

                                unformatStringValue: function (val) {
                                    return val;
                                },

                                unformattedValue: function () {
                                    return dti.oasis.field.getFieldValue(field);
                                },

                                unformat: function () {
                                }
                            }
                        }
                    },

                    getFieldFormat: function (field) {
                        if (typeof field == "string") {
                            field = getObject(field);
                        }

                        if (window["handleGetFieldFormatter"]) {
                            var formatter = window["handleGetFieldFormatter"](field);
                            if (formatter != null) {
                                return formatter;
                            }
                        }

                        if ($(field).is(":radio")) {
                            return this._protected._createNullFormatter(field);
                        }

                        var dataType = dti.oasis.ui.getElementDataType(field) || "";

                        if (dataType) {
                            switch (dataType) {
                                case dti.oasis.grid.config.dataType.CURRENCY:
                                case dti.oasis.grid.config.dataType.CURRENCY_FORMATTED:
                                    return this.moneyFormatter(field);
                                case dti.oasis.grid.config.dataType.DATE:
                                    return this.dateFormatter(field);
                                case dti.oasis.grid.config.dataType.NUMBER:
                                    return this.numberFormatter(field);
                                case dti.oasis.grid.config.dataType.PERCENTAGE:
                                    return this.percentageFormatter(field);
                                case dti.oasis.grid.config.dataType.PHONE_NUMBER:
                                    return this.phoneFormatter(field);
                            }
                        }

                        return this._protected._createNullFormatter(field);
                    },

                    dateFormatter: function (field) {
                        return {
                            formatStringValue: function (val) {
                                if (val == DATE_MASK_INTERNATIONAL || val == DATE_MASK) {
                                    return "";
                                }

                                if (dti.oasis.string.endsWith(field.name, "_DISP_ONLY")) {
                                    return dti.oasis.dataFormat.formatDate(
                                        dti.oasis.dataFormat.parseDate(val, dti.oasis.dataFormat.getLocalDateFormat()),
                                        dti.oasis.dataFormat.getLocalDateFormat());
                                } else {
                                    return dti.oasis.dataFormat.formatDate(val);
                                }
                            },

                            formattedValue: function () {
                                return this.formatStringValue(dti.oasis.field.getFieldValue(field));
                            },

                            format: function () {
                                dti.oasis.field.setFieldValue(field, this.formattedValue());
                            },

                            unformatStringValue: function (val) {
                                if (dti.oasis.string.isEmpty(val) || val == DATE_MASK_INTERNATIONAL || val == DATE_MASK) {
                                    return "";
                                }

                                if (dti.oasis.string.endsWith(field.name, "_DISP_ONLY")) {
                                    return dti.oasis.dataFormat.formatDate(
                                        dti.oasis.dataFormat.parseDate(val, dti.oasis.dataFormat.getLocalDateFormat()));
                                } else {
                                    return dti.oasis.dataFormat.formatDate(val);
                                }
                            },

                            unformattedValue: function () {
                                return this.unformatStringValue(dti.oasis.field.getFieldValue(field));
                            },

                            unformat: function () {
                                dti.oasis.field.setFieldValue(field, this.unformattedValue());
                            }
                        }
                    },

                    moneyFormatter: function (field) {
                        return {
                            formatStringValue: function (val) {
                                if (dti.oasis.string.isEmpty(val)) {
                                    return "";
                                }

                                return dti.oasis.dataFormat.formatMoney(val);
                            },

                            formattedValue: function () {
                                return this.formatStringValue(dti.oasis.field.getFieldValue(field));
                            },

                            format: function () {
                                dti.oasis.field.setFieldValue(field, this.formattedValue());
                            },

                            unformatStringValue: function (val) {
                                if (dti.oasis.string.isEmpty(val)) {
                                    return "";
                                }

                                return dti.oasis.dataFormat.unformatMoney(val);
                            },

                            unformattedValue: function () {
                                return this.unformatStringValue(dti.oasis.field.getFieldValue(field));
                            },

                            unformat: function () {
                                dti.oasis.field.setFieldValue(field, this.unformattedValue());
                            }
                        };
                    },

                    numberFormatter: function (field) {
                        if (dti.oasis.string.endsWith(field.name, "_DISP_ONLY") &&
                            !dti.oasis.string.isEmpty(field.getAttribute("formatpattern"))) {
                            var formatPattern = field.getAttribute("formatpattern");

                            return {
                                formatStringValue: function (val) {
                                    if (dti.oasis.string.isEmpty(val)) {
                                        return "";
                                    }

                                    return dti.oasis.dataFormat.formatNumber(val, formatPattern);
                                },

                                formattedValue: function () {
                                    return this.formatStringValue(dti.oasis.field.getFieldValue(field));
                                },

                                format: function () {
                                    var displayOnlyVal = dti.oasis.field.getFieldValue(field);
                                    var formattedVal = dti.oasis.dataFormat.formatNumber(displayOnlyVal, formatPattern);

                                    if (displayOnlyVal != formattedVal) {
                                        dti.oasis.field.setFieldValue(
                                            dti.oasis.string.strLeft(field.name, "_DISP_ONLY"),
                                            this.unformattedValue());
                                        dti.oasis.field.setFieldValue(field, this.unformattedValue());
                                    }
                                },

                                unformatStringValue: function (val) {
                                    var formattedVal = dti.oasis.dataFormat.formatNumber(val, formatPattern);

                                    if (val == formattedVal) {
                                        // If display only value equals formatted value, returns hidden field value.
                                        return dti.oasis.field.getFieldValue(dti.oasis.string.strLeft(field.name, "_DISP_ONLY"));
                                    }

                                    return dti.oasis.dataFormat.unformatNumberToStr(val, formatPattern);
                                },

                                unformattedValue: function () {
                                    return this.unformatStringValue(dti.oasis.field.getFieldValue(field));
                                },

                                unformat: function () {
                                    dti.oasis.field.setFieldValue(field, this.unformattedValue());
                                }
                            }
                        }

                        return this._protected._createNullFormatter(field);
                    },

                    percentageFormatter: function (field) {
                        return {
                            formatStringValue: function (val) {
                                if (dti.oasis.string.isEmpty(val)) {
                                    return "";
                                }

                                return dti.oasis.dataFormat.formatPercentage(val);
                            },

                            formattedValue: function () {
                                return this.formatStringValue(dti.oasis.field.getFieldValue(field));
                            },

                            format: function () {
                                dti.oasis.field.setFieldValue(field, this.formattedValue());
                            },

                            unformatStringValue: function (val) {
                                if (dti.oasis.string.isEmpty(val)) {
                                    return "";
                                }

                                return dti.oasis.dataFormat.unformatPercentage(val);
                            },

                            unformattedValue: function () {
                                return this.unformatStringValue(dti.oasis.field.getFieldValue(field));
                            },

                            unformat: function () {
                                dti.oasis.field.setFieldValue(field, this.unformattedValue());
                            }
                        }
                    },

                    phoneFormatter: function (field) {
                        if (dti.oasis.string.endsWith(field.name, "_DISP_ONLY")) {
                            return {
                                formatStringValue: function (val) {
                                    if (dti.oasis.string.isEmpty(val) || val == PHONE_MASK) {
                                        return "";
                                    }

                                    return dti.oasis.dataFormat.formatPhoneNumber(val);
                                },

                                formattedValue: function () {
                                    return this.formatStringValue(dti.oasis.field.getFieldValue(field));
                                },

                                format: function () {
                                    dti.oasis.field.setFieldValue(dti.oasis.string.strLeft(field.name, "_DISP_ONLY"), this.unformattedValue());
                                    dti.oasis.field.setFieldValue(field, this.formattedValue());
                                },

                                unformatStringValue: function (val) {
                                    if (dti.oasis.string.isEmpty(val) || val == PHONE_MASK) {
                                        return "";
                                    }

                                    return dti.oasis.dataFormat.unformatPhoneNumber(val);
                                },

                                unformattedValue: function () {
                                    return this.unformatStringValue(dti.oasis.field.getFieldValue(field));
                                },

                                unformat: function () {
                                    dti.oasis.field.setFieldValue(field, this.unformattedValue());
                                }
                            }
                        }

                        return this._protected._createNullFormatter(field);
                    }
                }
            })(),

            isFieldValueChanged: function (field) {
                if (typeof field == "string") {
                    field = getObject(field);
                }

                var originalValue = field.getAttribute(ATTRIBUTE_ORIGINAL_VALUE);

                if (originalValue == field.value) {
                    return false;
                }

                if (dti.oasis.string.endsWith(field.name, "_DISP_ONLY")) {
                    originalValue = this.getFieldValue(dti.oasis.string.strLeft(field.name, "_DISP_ONLY"));
                }

                var unformattedOriginalValue = dti.oasis.field.fieldFormat.getFieldFormat(field).unformatStringValue(originalValue);
                var unformattedValue = dti.oasis.field.fieldFormat.getFieldFormat(field).unformatStringValue(field.value);

                if (unformattedOriginalValue == unformattedValue) {
                    return false;
                }

                return true;
            },

            getFieldValue: function (field) {
                return getObjectValue(field);
            },

            setFieldValue: function (field, value, fireOnChange) {
                setObjectValue(field, value, fireOnChange);
            }
        };
    })();
}

