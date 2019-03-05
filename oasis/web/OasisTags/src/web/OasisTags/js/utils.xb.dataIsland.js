/*
 Revision Date    Revised By  Description
 ----------------------------------------------------------------------------
 ----------------------------------------------------------------------------
 */

if (typeof dti.oasis.dataIsland == "undefined") {
    dti.oasis.dataIsland = (function () {
        return {
            EX_BOF_OR_EOF: "Either BOF or EOF is True, or the current record has been deleted. Requested operation requires a current record.",
            EX_ITEM_NOT_FOUND: "Item cannot be found in the collection corresponding to the requested name or ordinal.",

            /**
             * Create a data island object.
             *
             * @param params A JSON object of parameters.
             */
            create: function (params) {
                var dataIslandObj = Object.create(null);

                Object.defineProperty(dataIslandObj, "isOasisDataIsland", {
                    get: function () {
                        return true;
                    }
                });

                dataIslandObj._protected = {};
                dataIslandObj._protected._gridId = params["gridId"];
                dataIslandObj._protected._isOriginalDataIsland = params["isOriginalDataIsland"] || false;
                dataIslandObj._protected._currentRowId = null;
                dataIslandObj._protected._currentRowIndex = null;
                dataIslandObj._protected._currentJqxRowId = null;
                dataIslandObj._protected._currentJqxRowIndex = null;
                dataIslandObj._protected._currentJqxRowDisplayIndex = null;
                dataIslandObj._protected._BOF = true;
                dataIslandObj._protected._EOF = false;

                dataIslandObj._protected._id = (dataIslandObj._protected._isOriginalDataIsland ? "" : "orig")
                    .concat(dataIslandObj._protected._gridId, "1");

                Object.defineProperty(dataIslandObj, "id", {
                    get: function () {
                        return this._protected._id;
                    }
                });

                dataIslandObj._protected._getRecordCount = function () {
                    if (this._isOriginalDataIsland) {
                        return dti.oasis.grid.getRecordCount(this._gridId);
                    } else  {
                        return dti.oasis.grid.getRawData(this._gridId).length;
                    }
                };

                dataIslandObj._protected._checkRowStatus = function () {
                    if (this._currentRowId != null) {
                        if (this._BOF || this._EOF) {
                            throw dti.oasis.dataIsland.EX_BOF_OR_EOF;
                        }
                    }
                };

                dataIslandObj._protected._getCurrentRow = function () {
                    this._checkRowStatus();

                    return dti.oasis.grid.getRawData(this._gridId)[this._currentRowIndex];
                };

                dataIslandObj.getGridId = function () {
                    return this._protected._gridId;
                };

                dataIslandObj.selectNodes = function (selector) {
                    return this.documentElement.selectNodes(selector);
                };

                dataIslandObj.selectSingleNode = function (selector) {
                    return this.documentElement.selectSingleNode(selector);
                };

                Object.defineProperty(dataIslandObj, "documentElement", {
                    get: function () {
                        return dti.oasis.dataIsland.node.createNode({
                            nodeType: dti.oasis.dataIsland.node.nodeType.ROWS,
                            grid: dataIslandObj
                        });
                    }
                });

                dataIslandObj.recordset = function (item) {
                    return dataIslandObj.recordset.Fields(item);
                };

                dataIslandObj.recordset._protected = {
                    _dataIsland: dataIslandObj
                };

                var _recordset_prop_ = {
                    AbsolutePosition: {
                        get: function() {
                            var dataIslandProperties = dataIslandObj._protected;
                            if (dataIslandProperties._BOF) {
                                return -2;
                            }

                            if (dataIslandProperties._EOF) {
                                return -3;
                            }

                            if (dataIslandProperties._isOriginalDataIsland) {
                                return dataIslandProperties._currentJqxRowDisplayIndex + 1;
                            } else {
                                return dataIslandProperties._currentRowIndex + 1;
                            }
                        },
                        set: function(val) {
                            if (val <= 0) {
                                // BOF
                                this.BeforeFirst();

                            } else if (val > dataIslandObj.recordset.RecordCount) {
                                // EOF
                                this.AfterLast();

                            } else {
                                var dataIslandProperties = dataIslandObj._protected;
                                var gridId = dataIslandProperties._gridId;

                                if (dataIslandProperties._isOriginalDataIsland) {
                                    var jqxRowDisplayIndex = val - 1;
                                    var jqxRowIndex = dti.oasis.grid.getJqxRowIndexByJqxRowDisplayIndex(gridId, jqxRowDisplayIndex);
                                    var jqxRowId = dti.oasis.grid.getJqxRowIdByJqxRowIndex(gridId, jqxRowIndex);
                                    var rowId = dti.oasis.grid.getRowIdByJqxRowIndex(gridId, jqxRowIndex);
                                    var rowIndex = dti.oasis.grid.getRowIndexByJqxRowIndex(gridId, jqxRowIndex);

                                    dataIslandProperties._currentRowId = rowId;
                                    dataIslandProperties._currentRowIndex = rowIndex;
                                    dataIslandProperties._currentJqxRowId = jqxRowId;
                                    dataIslandProperties._currentJqxRowIndex = jqxRowIndex;
                                    dataIslandProperties._currentJqxRowDisplayIndex = jqxRowDisplayIndex;
                                } else {
                                    dataIslandProperties._currentRowId = dti.oasis.grid.getRowIdByRowIndex(gridId, val - 1);
                                    dataIslandProperties._currentRowIndex = val - 1;
                                    dataIslandProperties._currentJqxRowId = null;
                                    dataIslandProperties._currentJqxRowIndex = null;
                                    dataIslandProperties._currentJqxRowDisplayIndex = null;
                                }

                                dataIslandObj._protected._BOF = false;
                                dataIslandObj._protected._EOF = false;
                            }
                        }
                    },

                    BOF: {
                        get: function() {
                            return dataIslandObj._protected._BOF;
                        }
                    },

                    EOF: {
                        get: function() {
                            return dataIslandObj._protected._EOF;
                        }
                    },

                    RecordCount: {
                        get: function() {
                            return dataIslandObj._protected._getRecordCount();
                        }
                    }
                };

                Object.defineProperty(dataIslandObj.recordset, "AbsolutePosition", _recordset_prop_.AbsolutePosition);
                Object.defineProperty(dataIslandObj.recordset, "absolutePosition", _recordset_prop_.AbsolutePosition);
                Object.defineProperty(dataIslandObj.recordset, "absoluteposition", _recordset_prop_.AbsolutePosition);

                Object.defineProperty(dataIslandObj.recordset, "BOF", _recordset_prop_.BOF);
                Object.defineProperty(dataIslandObj.recordset, "bof", _recordset_prop_.BOF);

                Object.defineProperty(dataIslandObj.recordset, "EOF", _recordset_prop_.EOF);
                Object.defineProperty(dataIslandObj.recordset, "eof", _recordset_prop_.EOF);

                Object.defineProperty(dataIslandObj.recordset, "RecordCount", _recordset_prop_.RecordCount);
                Object.defineProperty(dataIslandObj.recordset, "recordCount", _recordset_prop_.RecordCount);
                Object.defineProperty(dataIslandObj.recordset, "recordcount", _recordset_prop_.RecordCount);

                dataIslandObj.recordset.getGridId = function () {
                    return this._protected._dataIsland._protected._gridId;
                };

                dataIslandObj.recordset.GotoRow = function (rowId) {
                    var dataIslandProperties = this._protected._dataIsland._protected;

                    if (dataIslandProperties._isOriginalDataIsland) {
                        var jqxRowDisplayIndex = dti.oasis.grid.getJqxRowDisplayIndexByRowId(dataIslandProperties._gridId, rowId);
                        this.AbsolutePosition = jqxRowDisplayIndex + 1;
                    } else {
                        var rowIndex = dti.oasis.grid.getRowIndexByRowId(dataIslandProperties._gridId, rowId);
                        this.AbsolutePosition = rowIndex + 1;
                    }
                };

                dataIslandObj.recordset.GotoRowByIndex = function (rowIndex) {
                    var dataIslandProperties = this._protected._dataIsland._protected;

                    if (dataIslandProperties._isOriginalDataIsland) {
                        var jqxRowDisplayIndex = dti.oasis.grid.getJqxRowDisplayIndexByRowIndex(dataIslandProperties._gridId, rowIndex);
                        this.AbsolutePosition = jqxRowDisplayIndex + 1;
                    } else {
                        this.AbsolutePosition = rowIndex + 1;
                    }
                };

                dataIslandObj.recordset.Move = function (num, start) {
                    var startPosition = this.AbsolutePosition;
                    if (typeof start == "undefined") {
                        this._protected._dataIsland._protected._checkRowStatus();
                    } else {
                        startPosition = start;
                    }

                    this.AbsolutePosition = startPosition + num;
                };

                dataIslandObj.recordset.move = dataIslandObj.recordset.Move;

                dataIslandObj.recordset.AfterLast = function ()  {
                    // EOF
                    var dataIslandProperties = this._protected._dataIsland._protected;
                    dataIslandProperties._currentRowId = null;
                    dataIslandProperties._currentRowIndex = null;
                    dataIslandProperties._currentJqxRowId = null;
                    dataIslandProperties._currentJqxRowIndex = null;
                    dataIslandProperties._currentJqxRowDisplayIndex = null;
                    dataIslandProperties._BOF = false;
                    dataIslandProperties._EOF = true;
                };

                dataIslandObj.recordset.afterLast = dataIslandObj.recordset.AfterLast;
                dataIslandObj.recordset.afterlast = dataIslandObj.recordset.AfterLast;

                dataIslandObj.recordset.BeforeFirst = function ()  {
                    // BOF
                    var dataIslandProperties = this._protected._dataIsland._protected;
                    dataIslandProperties._currentRowId = null;
                    dataIslandProperties._currentRowIndex = null;
                    dataIslandProperties._currentJqxRowId = null;
                    dataIslandProperties._currentJqxRowIndex = null;
                    dataIslandProperties._currentJqxRowDisplayIndex = null;
                    dataIslandProperties._BOF = true;
                    dataIslandProperties._EOF = false;
                };

                dataIslandObj.recordset.beforeFirst = dataIslandObj.recordset.BeforeFirst;
                dataIslandObj.recordset.beforefirst = dataIslandObj.recordset.BeforeFirst;

                dataIslandObj.recordset.MoveFirst = function () {
                    this.AbsolutePosition = 1;
                };

                dataIslandObj.recordset.moveFirst = dataIslandObj.recordset.MoveFirst;
                dataIslandObj.recordset.movefirst = dataIslandObj.recordset.MoveFirst;

                dataIslandObj.recordset.MoveLast = function () {
                    this.AbsolutePosition = this.RecordCount;
                };

                dataIslandObj.recordset.moveLast = dataIslandObj.recordset.MoveLast;
                dataIslandObj.recordset.movelast = dataIslandObj.recordset.MoveLast;

                dataIslandObj.recordset.MoveNext = function () {
                    dataIslandObj.recordset.Move(1);
                };


                dataIslandObj.recordset.moveNext = dataIslandObj.recordset.MoveNext;
                dataIslandObj.recordset.movenext = dataIslandObj.recordset.MoveNext;

                dataIslandObj.recordset.MovePrevious = function () {
                    dataIslandObj.recordset.Move(-1);
                };

                dataIslandObj.recordset.movePrevious = dataIslandObj.recordset.MovePrevious;
                dataIslandObj.recordset.moveprevious = dataIslandObj.recordset.MovePrevious;

                var _recordset_field_property_ = {
                    Count : {
                        get: function() {
                            var columnNames = dti.oasis.page.getGridInfo(dataIslandObj._protected._gridId)["data"]["columnNames"];

                            return columnNames.length;
                        }
                    }
                };

                dataIslandObj.recordset.Fields = function (item) {
                    return dataIslandObj.recordset.Fields.Item(item);
                };

                dataIslandObj.recordset.fields = dataIslandObj.recordset.Fields;

                Object.defineProperty(dataIslandObj.recordset.Fields, "Count", _recordset_field_property_.Count);
                Object.defineProperty(dataIslandObj.recordset.Fields, "count", _recordset_field_property_.Count);

                dataIslandObj.recordset.Fields.Item = function (item) {
                    dataIslandObj._protected._checkRowStatus();

                    var itemObj = {
                        _protected: {
                            _getRawDataItemName: function (itemNameOrIndex) {
                                if (dti.oasis.number.isInteger(itemNameOrIndex)) {
                                    return this._getRawDataItemNameByIndex(itemNameOrIndex);
                                } else {
                                    return this._getRawDataItemNameByName(itemNameOrIndex);
                                }
                            },

                            _getRawDataItemNameByIndex: function (itemIndex) {
                                var rawDataItemName = "";
                                var attrs = [];
                                var curIdx = 0;
                                var modelRow = dti.oasis.page.getGridInfo(dataIslandObj._protected._gridId)["data"]["MODEL"];

                                for (var p in  modelRow) {
                                    if (modelRow.hasOwnProperty(p)) {
                                        if (p.indexOf("@") == 0) {
                                            attrs[attrs.length] = p;
                                        } else {
                                            if (curIdx == itemIndex) {
                                                rawDataItemName = p;
                                                break;
                                            }

                                            curIdx++;
                                        }
                                    }
                                }

                                if (dti.oasis.string.isEmpty(rawDataItemName)) {
                                    for (var i = 0; i < attrs.length; i++)  {
                                        if (curIdx == itemIndex) {
                                            rawDataItemName = attrs[i];
                                            break;
                                        }

                                        curIdx++;
                                    }
                                }

                                if (dti.oasis.string.isEmpty(rawDataItemName)) {
                                    throw dti.oasis.dataIsland.EX_ITEM_NOT_FOUND;
                                }

                                return rawDataItemName;
                            },

                            _getRawDataItemNameByName: function (itemName) {
                                var actualColumnName = dti.oasis.grid.getActualColumnName(dataIslandObj._protected._gridId, itemName);

                                if (dti.oasis.string.isEmpty(actualColumnName)) {
                                    throw dti.oasis.dataIsland.EX_ITEM_NOT_FOUND;
                                }

                                return actualColumnName;
                            }
                        },

                        init: function () {
                            this._protected._currentRow = dataIslandObj._protected._getCurrentRow();
                            this._protected._name = this._protected._getRawDataItemName(item);

                            delete this.init;

                            return this;
                        }
                    }.init();

                    var _recordset_item_prop_ = {
                        name: {
                            get: function() {
                                if (itemObj._protected._name.indexOf("@") == 0) {
                                    return itemObj._protected._name.substr(1);
                                }

                                return itemObj._protected._name;
                            }
                        },

                        value: {
                            get: function () {
                                if (typeof this._protected._currentRow != "undefined") {
                                    var gridId = dataIslandObj._protected._gridId;
                                    var fieldId = this._protected._name;
                                    var val = this._protected._currentRow[fieldId];

                                    var columnConfig = dti.oasis.grid.getColumnConfig(gridId, dti.oasis.grid.getActualColumnName(gridId, fieldId));
                                    if (columnConfig && columnConfig["dataType"] === "CU") {
                                        val = dti.oasis.dataFormat.formatMoney(val);
                                    }

                                    return val;
                                }
                                return "";
                            },

                            set: function(val) {
                                var gridId = dataIslandObj._protected._gridId;
                                var currentJqxRowIndex = dataIslandObj._protected._currentJqxRowIndex;
                                var currentRowId = dataIslandObj._protected._currentRowId;
                                var columnName = this._protected._name;

                                if (dataIslandObj._protected._isOriginalDataIsland) {
                                    if (dti.oasis.grid.isGridDataField(gridId, columnName)) {
                                        dti.oasis.grid.setGridCellValue(gridId, currentJqxRowIndex, columnName, val, true);
                                    } else {
                                        dti.oasis.grid.setRowDataValueByJqxRowIndex(gridId, currentJqxRowIndex, columnName, val);
                                        var rowId = dti.oasis.grid.getRowIdByJqxRowIndex(gridId, currentJqxRowIndex);
                                        if (dti.oasis.grid.isSelectedRow(gridId, rowId)) {
                                            dti.oasis.grid.syncCellValueToDetailField(gridId, columnName)
                                        }
                                    }
                                } else {
                                    dti.oasis.grid.setRowDataValueByJqxRowIndex(gridId, currentJqxRowIndex, columnName, val);
                                }
                            }
                        }
                    };

                    Object.defineProperty(itemObj, "Name", _recordset_item_prop_.name);
                    Object.defineProperty(itemObj, "name", _recordset_item_prop_.name);

                    Object.defineProperty(itemObj, "Value", _recordset_item_prop_.value);
                    Object.defineProperty(itemObj, "value", _recordset_item_prop_.value);

                    return itemObj;
                };

                dataIslandObj.recordset.Fields.item = dataIslandObj.recordset.Fields.Item;

                dataIslandObj.recordset.AddNew = function () {
                    window[dataIslandObj._protected._gridId.concat("_insertrow")]();
                };

                dataIslandObj.recordset.addNew = dataIslandObj.recordset.AddNew;
                dataIslandObj.recordset.addnew = dataIslandObj.recordset.AddNew;

                dataIslandObj.recordset.Delete = function () {
                    window[dataIslandObj._protected._gridId.concat("_deleterow")]();
                };

                dataIslandObj.recordset["delete"] = dataIslandObj.recordset.Delete;

                dataIslandObj.setProperty = function (propertyName, value) {
                    // To be implemented.
                };

                return dataIslandObj;
            }
        };
    })();
}

if (typeof dti.oasis.dataIsland.node == "undefined") {
    dti.oasis.dataIsland.node = (function () {

        return {
            nodeType: {
                GRID: "GRID",
                ROWS: "ROWS",
                ROW: "ROW",
                FIELD: "FIELD",
                TEXT: "TEXT"
            },

            _protected: (function () {
                return {
                    _getPath: function (node, selector) {
                        var rowsNode = this._getRowsNode(node);

                        if (rowsNode != null) {
                            var pathString = selector.toUpperCase();

                            if (pathString.indexOf("[") > -1) {
                                pathString = dti.oasis.string.strLeft(pathString, "[").concat(dti.oasis.string.strRight(pathString, "]"));
                            }

                            if (pathString == "//ROW" || pathString == "ROW") {
                                return {
                                    nodeType: dti.oasis.dataIsland.node.nodeType.ROW
                                }
                            } else {
                                var columnName = pathString;
                                if (columnName.indexOf("//ROW/") > -1) {
                                    columnName = dti.oasis.string.strRight(columnName, "//ROW/");
                                } else if (columnName.indexOf("./") == 0) {
                                    columnName = dti.oasis.string.strRight(columnName, "./");
                                } else if (columnName.indexOf("//") == 0) {
                                    columnName = dti.oasis.string.strRight(columnName, "//");
                                }

                                if (!dti.oasis.string.isEmpty(columnName)) {
                                    var upperCaseColumnNames = this._getUpperCaseColumnNames(rowsNode);
                                    if (upperCaseColumnNames.hasOwnProperty(columnName)) {
                                        columnName = upperCaseColumnNames[columnName];

                                        return {
                                            nodeType: dti.oasis.dataIsland.node.nodeType.FIELD,
                                            columnName: columnName
                                        };
                                    }
                                }
                            }
                        }

                        return null;
                    },

                    _getFilter: function (selector) {
                        if (!dti.oasis.string.isEmpty(selector)) {
                            return dti.oasis.filter.compile(selector);
                        }
                        return null;
                    },

                    _filterGrid: function (grid, filterObj) {
                        var gridId = grid._protected._gridId;
                        var rawData = null;
                        if (grid._protected._isOriginalDataIsland) {
                            rawData = dti.oasis.grid.getDisplayingRawData(gridId);
                        } else {
                            rawData = dti.oasis.grid.getRawData(gridId);
                        }

                        if (filterObj) {
                            var filteredRows = [];

                            for (var i = 0; i < rawData.length; i++) {
                                if (filterObj.filter(rawData[i])) {
                                    filteredRows[filteredRows.length] = rawData[i];
                                }
                            }

                            return filteredRows;
                        } else {
                            return rawData;
                        }
                    },

                    _getFirstRow: function (grid, filterObj) {
                        var gridId = grid._protected._gridId;
                        var rawData = null;
                        if (grid._protected._isOriginalDataIsland) {
                            rawData = dti.oasis.grid.getDisplayingRawData(gridId);
                        } else {
                            rawData = dti.oasis.grid.getRawData(gridId);
                        }

                        if (rawData.length > 0) {
                            if (filterObj) {
                                for (var i = 0; i < rawData.length; i++) {
                                    if (filterObj.filter(rawData[i])) {
                                        return rawData[i];
                                    }
                                }
                            } else {
                                return rawData[0];
                            }
                        }

                        return null;
                    },

                    _getAllColumnNames: function (node) {
                        var rowsNode = this._getRowsNode(node);

                        if (rowsNode != null) {
                            if (rowsNode._protected._allColumnNames) {
                                return rowsNode._protected._allColumnNames;
                            }

                            var gridId = rowsNode._protected._grid._protected._gridId;
                            var allColumnNames = dti.oasis.grid.getColumnNames(gridId);

                            rowsNode._protected._allColumnNames = allColumnNames;

                            return allColumnNames;
                        }

                        return [];
                    },

                    _getAttributeColumnNames: function (node) {
                        var rowsNode = this._getRowsNode(node);

                        if (rowsNode != null) {
                            if (rowsNode._protected._attributeColumnNames) {
                                return rowsNode._protected._attributeColumnNames;
                            }

                            var attributeColumnNames = [];
                            var uppercaseAttributeColumnNames = {};
                            var allColumnNames = this._getAllColumnNames(rowsNode);

                            for (var i = 0; i < allColumnNames.length; i++) {
                                if (allColumnNames[i].indexOf("@") == 0) {
                                    attributeColumnNames[attributeColumnNames.length] = allColumnNames[i];
                                    uppercaseAttributeColumnNames[allColumnNames[i].toUpperCase()] = allColumnNames[i];
                                }
                            }

                            rowsNode._protected._attributeColumnNames = attributeColumnNames;
                            rowsNode._protected._uppercaseAttributeColumnNames = uppercaseAttributeColumnNames;

                            return attributeColumnNames;
                        }

                        return [];
                    },

                    _getUpperCaseAttributeColumnNames: function (node) {
                        var rowsNode = this._getRowsNode(node);
                        if (rowsNode != null) {
                            if (rowsNode._protected._uppercaseAttributeColumnNames) {
                                return rowsNode._protected._uppercaseAttributeColumnNames;
                            }

                            this._getAttributeColumnNames(rowsNode);

                            if (rowsNode._protected._uppercaseAttributeColumnNames) {
                                return rowsNode._protected._uppercaseAttributeColumnNames;
                            }
                        }
                        return [];
                    },

                    _getRowsNode: function (node) {
                        if (node._protected._nodeType == dti.oasis.dataIsland.node.nodeType.ROWS) {
                            return node;
                        }

                        if (node._protected._parentNode != null) {
                            return this._getRowsNode(node._protected._parentNode);
                        }

                        return null;
                    },

                    _getColumnNames: function (node) {
                        var rowsNode = this._getRowsNode(node);

                        if (rowsNode != null) {
                            if (rowsNode._protected._columnNames) {
                                return rowsNode._protected._columnNames;
                            }

                            var columnNames = [];
                            var uppercaseColumnNames = [];
                            var allColumnNames = this._getAllColumnNames(rowsNode);

                            for (var i = 0; i < allColumnNames.length; i++) {
                                if (allColumnNames[i].indexOf("@") != 0) {
                                    columnNames[columnNames.length] = allColumnNames[i];
                                    uppercaseColumnNames[allColumnNames[i].toUpperCase()] = allColumnNames[i];
                                }
                            }

                            rowsNode._protected._columnNames = columnNames;
                            rowsNode._protected._uppercaseColumnNames = uppercaseColumnNames;

                            return columnNames;
                        }

                        return [];
                    },

                    _getUpperCaseColumnNames: function (node) {
                        var rowsNode = this._getRowsNode(node);
                        if (rowsNode != null) {
                            if (rowsNode._protected._uppercaseColumnNames) {
                                return rowsNode._protected._uppercaseColumnNames;
                            }

                            this._getColumnNames(rowsNode);

                            if (rowsNode._protected._uppercaseColumnNames) {
                                return rowsNode._protected._uppercaseColumnNames;
                            }
                        }
                        return [];
                    }
                };
            })(),

            createNode: function (params) {
                var nodeType = params.nodeType || dti.oasis.dataIsland.node.nodeType.GRID;
                var grid = params.grid || null;
                var row = params.row || null;
                var columnName = params.columnName || null;
                var val = params.value || null;
                var parentNode = params.parentNode || null;

                var nodeObj = {
                    _protected: {
                        _nodeType: nodeType,
                        _grid: grid,
                        _row: row,
                        _columnName: columnName,
                        _value: val,
                        _parentNode: parentNode
                    },

                    selectNodes: function (selector) {
                        var nodes = [];
                        var path = dti.oasis.dataIsland.node._protected._getPath(nodeObj, selector);

                        switch (this._protected._nodeType) {
                            case dti.oasis.dataIsland.node.nodeType.GRID:
                                // Not implemented.
                                break;
                            case dti.oasis.dataIsland.node.nodeType.ROWS:
                                if (path != null) {
                                    var filterObj = dti.oasis.dataIsland.node._protected._getFilter(selector);
                                    var filteredRows = dti.oasis.dataIsland.node._protected._filterGrid(this._protected._grid, filterObj);

                                    for (var i = 0; i < filteredRows.length; i++) {
                                        nodes[nodes.length] = dti.oasis.dataIsland.node.createNode({
                                            nodeType: path.nodeType,
                                            grid: this._protected._grid,
                                            row: filteredRows[i],
                                            columnName: path.columnName,
                                            parentNode: this
                                        });
                                    }
                                }

                                break;
                            case dti.oasis.dataIsland.node.nodeType.ROW:
                                if (path != null && !dti.oasis.string.isEmpty(path.columnName)) {
                                    nodes[nodes.length] = dti.oasis.dataIsland.node.createNode({
                                        nodeType: path.nodeType,
                                        grid: this._protected._grid,
                                        row: this._protected._row,
                                        columnName: path.columnName,
                                        parentNode: this
                                    });
                                }

                                break;
                            case dti.oasis.dataIsland.node.nodeType.FIELD:
                            case dti.oasis.dataIsland.node.nodeType.TEXT:
                                // Return empty node list for field and text node.
                                break;
                        }

                        return dti.oasis.dataIsland.node.createNodeList(nodes);
                    },

                    selectSingleNode: function (selector) {
                        var path = dti.oasis.dataIsland.node._protected._getPath(nodeObj, selector);

                        switch (this._protected._nodeType) {
                            case dti.oasis.dataIsland.node.nodeType.GRID:
                                // Not implemented.
                                break;
                            case dti.oasis.dataIsland.node.nodeType.ROWS:
                                var filterObj = dti.oasis.dataIsland.node._protected._getFilter(selector);
                                var firstRow = dti.oasis.dataIsland.node._protected._getFirstRow(this._protected._grid, filterObj);

                                if (firstRow != null) {
                                    return dti.oasis.dataIsland.node.createNode({
                                        nodeType: path.nodeType,
                                        grid: this._protected._grid,
                                        row: firstRow,
                                        columnName: path.columnName,
                                        parentNode: this
                                    });
                                }
                                break;
                            case dti.oasis.dataIsland.node.nodeType.ROW:
                                return dti.oasis.dataIsland.node.createNode({
                                    nodeType: path.nodeType,
                                    grid: this._protected._grid,
                                    row: this._protected._row,
                                    columnName: path.columnName,
                                    parentNode: this
                                });
                            case dti.oasis.dataIsland.node.nodeType.FIELD:
                            case dti.oasis.dataIsland.node.nodeType.TEXT:
                                // Not implemented.
                                break;
                        }

                        return null;
                    },

                    getElementsByTagName: function (tagName) {
                        if (tagName === "*") {
                            return this.childNodes;
                        }

                        if (this.nodeType == dti.oasis.dataIsland.node.nodeType.ROWS && tagName == "ROW") {
                            return this.childNodes;
                        }

                        return this.selectNodes(tagName);
                    },

                    hasChildNodes: function () {
                        var childNodes = this._protected._childNodes;
                        if (childNodes != null) {
                            return (childNodes.length > 0);
                        }

                        switch (this._protected._nodeType) {
                            case dti.oasis.dataIsland.node.nodeType.ROWS:
                                return (this._protected._grid.recordset.RecordCount > 0);
                            case dti.oasis.dataIsland.node.nodeType.ROW:
                                var columnNames = dti.oasis.dataIsland.node._protected._getColumnNames(this);
                                return (columnNames.length > 0);
                            case dti.oasis.dataIsland.node.nodeType.FIELD:
                                return true;
                        }

                        return false;
                    },

                    getAttribute: function (attributeName) {
                        switch (this._protected._nodeType) {
                            case dti.oasis.dataIsland.node.nodeType.ROW:
                                var attributeNameUpper = "@".concat(attributeName).toUpperCase();
                                var upperCaseAttributeNames = dti.oasis.dataIsland.node._protected._getUpperCaseAttributeColumnNames(this);

                                if (upperCaseAttributeNames.hasOwnProperty(attributeNameUpper)) {
                                    var attributeColumnName = upperCaseAttributeNames[attributeNameUpper];

                                    if (!dti.oasis.string.isEmpty(attributeColumnName)) {
                                        if (this._protected._row.hasOwnProperty(attributeColumnName)) {
                                            return this._protected._row[attributeColumnName];
                                        } else {
                                            return "";
                                        }
                                    }
                                }
                        }

                        // Implement other method later.
                        return null;
                    },

                    setAttribute: function (attributeName, val) {
                        switch (this._protected._nodeType) {
                            case dti.oasis.dataIsland.node.nodeType.ROW:
                                var attributeNameUpper = "@".concat(attributeName).toUpperCase();
                                var upperCaseAttributeNames = dti.oasis.dataIsland.node._protected._getUpperCaseAttributeColumnNames(this);

                                if (upperCaseAttributeNames.hasOwnProperty(attributeNameUpper)) {
                                    var attributeColumnName = upperCaseAttributeNames[attributeNameUpper];

                                    if (!dti.oasis.string.isEmpty(attributeColumnName)) {
                                        if (this._protected._row.hasOwnProperty(attributeColumnName)) {
                                            this._protected._row[attributeColumnName] = val;
                                        }
                                    }
                                }
                        }
                    }
                };

                Object.defineProperty(nodeObj, "nodeName", {
                    get: function () {
                        switch (nodeObj._protected._nodeType) {
                            case dti.oasis.dataIsland.node.nodeType.GRID:
                                return "#document";

                            case dti.oasis.dataIsland.node.nodeType.ROWS:
                                return "ROWS";

                            case dti.oasis.dataIsland.node.nodeType.ROW:
                                return "ROW";

                            case dti.oasis.dataIsland.node.nodeType.FIELD:
                                return nodeObj._protected._columnName;

                            case dti.oasis.dataIsland.node.nodeType.TEXT:
                                return "#text";
                        }
                    }
                });

                Object.defineProperty(nodeObj, "tagName", {
                    get: function () {
                        return nodeObj.nodeName;
                    }
                });

                Object.defineProperty(nodeObj, "childNodes", {
                    get: function () {
                        if (nodeObj._protected._childNodes != null) {
                            return nodeObj._protected._childNodes;
                        }

                        switch (nodeObj._protected._nodeType) {
                            case dti.oasis.dataIsland.node.nodeType.GRID:
                                throw "Unsupported property \"childNodes\".";

                            case dti.oasis.dataIsland.node.nodeType.ROWS:
                                var childNodes = nodeObj.selectNodes("//ROW");
                                nodeObj._protected._childNodes = childNodes;
                                return childNodes;

                            case dti.oasis.dataIsland.node.nodeType.ROW:
                                var nodes = [];
                                var columnNames = dti.oasis.dataIsland.node._protected._getColumnNames(this);

                                for (var i = 0; i < columnNames.length; i++) {
                                    nodes[nodes.length] = dti.oasis.dataIsland.node.createNode({
                                        nodeType: dti.oasis.dataIsland.node.nodeType.FIELD,
                                        grid: nodeObj._protected._grid,
                                        row: nodeObj._protected._row,
                                        columnName: columnNames[i],
                                        parentNode: nodeObj
                                    });
                                }

                                var childNodes = dti.oasis.dataIsland.node.createNodeList(nodes);
                                nodeObj._protected._childNodes = childNodes;
                                return childNodes;

                            case dti.oasis.dataIsland.node.nodeType.FIELD:
                                var childNodes = dti.oasis.dataIsland.node.createNodeList([
                                    dti.oasis.dataIsland.node.createNode({
                                        nodeType: dti.oasis.dataIsland.node.nodeType.TEXT,
                                        grid: nodeObj._protected._grid,
                                        row: nodeObj._protected._row,
                                        columnName: columnName,
                                        value: nodeObj.text,
                                        parentNode: this
                                    })
                                ]);

                                nodeObj._protected._childNodes = childNodes;
                                return childNodes;

                            case dti.oasis.dataIsland.node.nodeType.TEXT:
                                var childNodes = dti.oasis.dataIsland.node.createNodeList([]);
                                nodeObj._protected._childNodes = childNodes;
                                return childNodes;
                        }
                    }
                });

                Object.defineProperty(nodeObj, "text", {
                    get: function() {
                        switch (nodeObj._protected._nodeType) {
                            case dti.oasis.dataIsland.node.nodeType.FIELD:
                                return nodeObj._protected._row[nodeObj._protected._columnName];

                            case dti.oasis.dataIsland.node.nodeType.TEXT:
                                return nodeObj._protected._value;
                        }

                        // The text property of other nodes are not supported for now.
                        throw "Unsupported property: text.";
                    },

                    set: function (val) {
                        var gridId = nodeObj._protected._grid.getGridId();
                        var rowId = nodeObj._protected._row["@id"];
                        var columnName = nodeObj._protected._columnName;

                        dti.oasis.grid.setCellValue(gridId, rowId, columnName, val, true)
                    }
                });

                Object.defineProperty(nodeObj, "textContent", {
                    get: function() {
                        return nodeObj.text;
                    }
                });

                Object.defineProperty(nodeObj, "firstChild", {
                    get: function () {
                        return nodeObj.childNodes.item(0);
                    }
                });

                if (nodeObj._protected._nodeType === dti.oasis.dataIsland.node.nodeType.TEXT) {
                    Object.defineProperty(nodeObj, "nodeValue", {
                        get: function () {
                            return nodeObj.text;
                        },
                        set: function (val) {
                            nodeObj.text = val;
                        }
                    });
                }

                return nodeObj;
            },

            createNodeList: function (nodes) {
                nodes.item = function (idx) {
                    if (idx >= this.length) {
                        return null;
                    }
                    return this[idx];
                };

                return nodes;
            }
        };
    })();
}

