/**
 * Created by kshen on 4/14/2016.
 */

/*
 Revision Date    Revised By  Description
 ----------------------------------------------------------------------------
 12/27/2017       kshen       Grid replacement. 1. Enforce display column title in one line.
                              2. Use the cell width * 8 as column width for jqxGrid.
 01/12/2018       kshen       Grid replacemnet. 1. Add new function initAndLoadGrids to support load grids after page entitlement.
                              2. Add function to support display grouping image.
                              3. Added function _adjustGridHeaderHeightForContent to support multi-lie grid header.
                              4. Removed the function about using text field width setting to set column width.
 02/02/2018       dpang       Issue 190988 - 1. Change to return null as selectIndColumnName for grids without selectIndColumn.
                                           - 2. Change to make column mini width customizable.
 02/06/2018       dzhang      Issue 191299 - check the selectIndColumnName when grids without selectIndColum.
 04/04/2018       cesar       #191962 - added col_width and min_col_width
 05/09/2018       mlm         192504 - added rendered callback reference.
 05/14/2018       mlm         193214 - Refactored to fix save failure on process adjustment page
 06/01/2018       mlm         193448 - Refactored to replace the reference of getChanges with getGridChanges.
 06/08/2018       mlm         193723 - Added getJqxGridId
 06/01/2018       htwang      193767 - Modified onColumnClick() to call handleOnSelectAll() method to handle additional logic
                              for the click event on the checkbox column of grid header.
 06/11/2018       fhuang      193793 - Modified deleteRow() to call selectRowByJqxRowIndex() to move to the next row if delete
                              multiple rows.
 06/11/2018       cesar       193651 - 1 - modified syncCellValueToDetailFieldsForSelectedRow() to set the value of html elements
                                           that are hooked to the data-src.
                                       2 - modified getGridChanges() to return an empty node with all the grid columns included.
                                       3 - modified _checkRowStatus() to check first if the grid is empty.
                                       4 - modified getRawDataItemNameByIndex() to return "" if the column name is not defined.
 06/19/2018       htwang      192933 - Modified onColumnClick() to pass selectAll checked status to handleOnSelectAll() method.
 06/20/2018       cesar       193772 - 1) Modified __getCellsRenderer() to include border for editable cells.
                                       2) Added updateEditableCell() to update the cell when clicking anywhere on the grid.
                                       3) - Modified cellbeginedit and cellendedit to setup updateEditableCell().
 06/27/2018       mxm         191837 - Grid replacement
                                       1) Initialize the 'defaultID'
                                       2) Add an attribute 'isOasisDataIsland', to determine if the object is a data island object
 06/21/2018       cesar       194019 - 1) added __getCompiledColumnsWidth() to set the width and minwidth for each of the column.
 07/05/2018       dpang       194134 - Changed to check gridConfig sortable not selectable when setting column sortable property.
 07/05/2018       cesar       194019 - 1) remove default "5%" for width in  __getCompiledColumnsWidth()
 07/09/2018       kshen       194134. Suuport grid filterable custom option.
 07/12/2018       kshen       194134. Correct the formatter for radio box.
 07/13/2018       cesar       194021 - Refactor commonAddRow(), postCommonAddRow(), setInitialUrlInGrid() for all web apps
 07/17/2018       Elvin       Issue 194134: correct onCellEndEdit val is null problem
 07/17/2018       dzhang      194134 - remove concat _DISP_ONLY in dti.oasis.grid.hasDisplayOnlyColumn to avoid adding _DISP_ONLY twice.
 07/17/2018       dpang       194134 - Set checked value as "-1" and unchecked value as "0" for grid detail checkbox field.
 07/17/2018       cesar       193651 - modified onCellValueChanged() to set the update_ind if the value in rowdata has also changed.
 07/18/2018       dpang       194134 - Set filterflag property for grid after filtering raw data.
 07/18/2018       cesar       193651 - rollback changes to onCellValueChanged()
 07/24/2018       Elvin       Issue 194713: add addIsRowEditableFunction, set editableBorderStyle to all editable columns (except checkbox)
 07/25/2018       dzhang      194134: addEventListenerToElements and removeEventListenerToElements can be supported multiple elements
 07/18/2018       cesar       194022 - Refactor commonDeleteRow().
 07/30/2018       jdingle     194134 - getColumnLabel() add support for old grid.
 07/26/2018       cesar       194428 - modified __getCompiledColumnsWidth when content data is too large.
 07/31/2018       dpang       194134 - 1) Change to use unformatted value when setting cell value.
                                       2) Clone rowData when adding new row.
 07/31/2018       cesar       194428 - modified __getCompiledColumnsWidth to remove checking for dti.oasis.grid.config.dataType.NUMBER
 08/01/2018       kshen       194876. Changed to call userRowchange in postCellEndEdit event.
 08/02/2018       fhuang      194134 - 1) modified selectNodes to check if path is not null before create node
                                       2) modified _getPath to handle columnName which starts with '//'
 08/03/2018       dpang       194836. Format percentage value to be without comma separator. For example, 12.34 will be formatted as 1234.00%, not 1,234.00%.
 08/06/2018       kshen       194134. Changed syncDetailFieldToGrid to support passing object as fieldId parameter.
 08/06/2018       dzhang      Issue 194134 - add function to get field url column.
 08/09/2018       dzhang      Issue 194134 - before add row to jqxgrid object. re-construct the row info according the compiled columns to exclude
                                             the URL_X, DISPLAY_IND, UPDATE_IND. we only keep this fields info in oasis raw data.
 08/10/2018       fhuang      194708: Modified syncDetailFieldToGrid to handle properly if fieldId is not string.
 08/10/2018       dpang       194641. Added __setColumnAgg and __columnAggRenderer for column aggregate.
                                      Added __calculateSum to fix float point calculation problem and changed to hide/show label configurablely.
 08/13/2018       kshen       194134. Fix the function endsWith and syncDetailFieldToGrid
 08/14/2018       kshen       194134. Support depedency dropdown list column.
 08/15/2018       fhuang      194134. Modified onPostCellEndEdit to handle field.value expression correctly.
 08/17/2018       dpang       195155 - 1) Added setGridCellValue() to set cell value with unformatted value and sync to detail.
                                       2) Added setRowDataValue() to set row data value with formatted value.
                                       3) Added parameter needSync to setCellValue() and setGridCellValue() to avoid redundant sync.
                                       4) Refactored getCellFormatterForSyncCellValueToRowData() and getCellFormatterForCellValue()
 08/17/2018       jdingle     194134. Add setCellFocus method.
 08/23/2018       dzhang      194134. set the obj value to trigger sync the changed cell value to detail field.
 08/24/2018        kshen      194134. Fix the bug of updateEditableCell.
 09/03/2018        dpang      194134. Fix filter issue by considering precedence, e.g. for filterString "a and b or c", it will be handled as "(a and b) or c", not "a and (b or c)".
 09/05/2018       htwang      195634. Added setRowDataValueByRowIndex to set the recordset row value by row index
 09/11/2018        dpang      194134. Change to return formatted currency when getting currency value from recordset.
 09/11/2018        cesar      194886 - export masked fields to excel. Added isFieldMasked() to check if the column name is maksed.
 09/17/2018        dpang      194134. Deselect selectInd column only when it's visible and is displayed as checkbox.
 09/18/2018        Elvin      195343. Change editableBorderStyle to be more conspicuous
 09/19/2018        dpang      195835. Change to sync detail field only if currentRow is selectedRow when setting value by recordset(ColumnName).value.
 09/19/2018       dzhang      194134. Add setSelectIndColumnName function.
 09/21/2018        dpang      195835. Add onBeforeGridSourceLoadComplete() which can be used to change original rowData or data to be displayed in grid.
 09/26/2018        dpang      195835. Add isSelectedRow() to check if current row is the selected row, if it is, then sync to detail, otherwise not.
 09/26/2018        dpang      195173. Change dataIslandObj.recordset.delete to dataIslandObj.recordset["delete"] to avoid unexpected identifier error in IE8.
 09/26/2018        ylu        195961. modify getRowIdByJqxRowId to return '' instead of null to match that old grid.
 09/27/2018        dpang      195835. Add dateFormatter() to format cell value whose display type is "datetimeinput".
 10/05/2018        dpang      195835. Set onGridReady() function to gridSetting["ready"]. It will be called when the grid is initialized and the binding is complete.
 10/09/2018        dzhang     195883. 1) Set select all to middle alignment.
                                      2) If cell value has format, the cell width should be calculated according to the formatted value.
                                      3) Set editable cell value to vertical middle alignment when the cell has not focus.
 10/10/2018        ylu        195883: item18 - support to display the formatted datetime column value.
 10/11/2019        cesar      193937 - Modified __getCellsRenderer() to implement setRowStyle() in jqxGrid
                                     - added getCellRowStyle()
 10/12/2018        Elvin      195835: pass in gridId into handleOnSelectAll
 10/12/2018        dzhang     195883: Support custom pageable property
 10/15/2018        dpang      195835: Add getCellEditableFunction and setRowDataValueByJqxRowIndex functions.
 09/04/2018        cesar      194821 - improvements to load page faster
                                        1) - modified  _processSyncCellValueToDetailField() to check for undefined in val.
                                        4) - modified isGridDataField() to check if the dataField exist.
                                        5) - modified _hasColumn() to use getUpperCaseColumnNameMap() to improve performance.
                                        6) - modified setGridCellValue() to either set the cell value or the raw data.
 10/17/2018        cesar      196161 - implement setAllNumbersColorInGrid() with jqxGrid by adding getAllNumbersColorInGrid()
 10/17/2018        cesar      194821 - removed __compileColumns() and __compileDataFields() performance because it causes an error when adding a row.
 10/22/2018        dpang      195835 - Change to only check if the grids that are not hidden are ready in areAllGridsReady.
 10/22/2018        cesar      195712 - Modified __getCompiledColumnsWidth() to check for masked fields.
 10/24/2018        dpang      195835 - Modified _processSyncCellValueToDetailField() to set value for "MultiSelectText" detail field.
 11/02/2018        dpang      195835 - Changed areAllGridsReady to skip checking if grid is ready when the panel is hidden or the panel content is collapsed.
 11/02/2018        htwang     195884 - Added validateBeforeSelectAll() to check whether it is ready to select the checkbox of the grid header.
 11/02/2018        fhuang     195884 - Modified __getCellsRenderer() to add color style to hyperlink.
 11/06/2018        clm        195889 - 1) remove the logic to set UPDATE_IND in onCellValueChanged and move the logic to rowchange
                                       2) add the logic in _selectAll to disable the checkbox of the grid header.
                                       3) add the logic to handle enable/disable the checkbox of the grid header in __compileColumns
                                       4) add new pageOption addSelectAllEnabledFunction in dti.oasis.page.newCustomPageOptions
 11/06/2018        clm        195889 - using dti.oasis.grid.getCustomGridOption instead of dti.oasis.page.getCustomPageOptions
 11/07/2018        kshen      196632. Fix the bug about depedency column label.
 11/11/2018        dpang      196632 - Changed to set inEditorCellMode and editorCellColumnName as grid property.
 10/26/2018        cesar      194821 - Modified __compileColumns() and __compileDataFields() to improve performance. It will only load visible columns.
 11/13/2018        dpang      196632 - Add onDropdownListItemSelect, dropdownListRenderer and dropdownListSelectionRenderer for dropdownlist grid cell.
 11/13/2018        cesar      194821 - Modified _recordset_item_prop_(),_processSyncCellValueToDetailField() to sync form datafield with grid and rawdata
 11/12/2018        wreeder    196147/195909 - Supporting async loading of data to the grid via deferredLoadDataProcess, cacheResultSet and calling load() with a new url and post parameters
                                            - Moved various __compileXXX methods from the grid init() section to the grid.config section to support recompiling grid columns for new data set
                                            - Added promises for each grid so a consumer can wait for 1 or more grids to complete loading before executing particular logic
                                            - Added method to wait for loading of all grids (or all visible grids) before executing particular logic
                                            - Added a sorting promise so a consumer can wait for sorting to complete before executing particular logic
 11/14/2018        cesar      194821 - continue with grid performance. Modified _recordset_item_prop_ get() to return formatted val if it is currency.
 11/16/2018        kshen      196922 - Added preSelectRowByUser and postSelectRowByUser callback function.
 11/21/2018        kshen      196922 - 1) Change the funciton isFieldValueChanged to compare unformatted values.
                                       2) Added preSelectRowByUser and postSelectRowByUser callback functions for selecting row by up/down key.
                                       3) Fixed the function __columnAggRenderer.
                                       4) Support starts-with filter condition.
 11/21/2018        kshen      196922 - Changed dispatchEvent to use fixEvent to support IE8 api of event.
 11/22/2018        kshen      195835. Use "isChangedEventSuccess !== false" to check if change event successes.
 11/26/2018        kshen      194134. Changed to remain the value of select ind when filtering grid.
                                      Changed the funciton getSelectedKeys to only get the selected rows of visible rows.
                                      (When loading the claim entry log page, the page would filter the grid to only
                                       display the selected coverages for the current claim entry log.)
 11/27/2018        dpang      195835. Compile currentFilterValue to object if it's of type string.
 11/27/2018        cesar      195636 - 1: Modified _adjustGridHeaderHeightForContent() to call resizeGridToFitIFrame()
                                       2: Modified __compileGridSetting() to include columnmenuopening() and columnmenuclosing() to
                                          expand the filtering content menu.
                                       3: Added resizeGridToFitIFrame() to make sure the grid is fully displayed within the grid.
                                       4: Added resizeIFrameForMenuFiltering() to make sure the filtering content is fully displayed within an iframe.
 11/29/2018        xjli       195889. To add _DISP_ONLY field for data type Number with format pattern.
 12/06/2018        kshen      194134. Add protected ID attribute to grid property.
 12/06/2018        cesar      196543 - 1) Modified __getCompiledColumnsWidth() not to include deleted records to calculate column width.
                                          Also refactor how to configure the minwidth and width length
                                       2) Modified deleteRow() to add recompileColumns() to refresh columns grid.
                                       3) Modified setGridCellValue() to call resizeColumnWidth() to refresh column width when adding a new row.
                                       4) Added resizeColumnWidth() to refresh column width.
                                       5) Added calculateColumnWidth() to calculate column width.
 12/10/2018        dzou        195887 - Modified _adjustGridHeaderHeightForContent, set the height of jqx-icon-arrow-down icon same with gridHeader height.
 12/10/2018        cesar       195350 - Modified onGridRendered() to add a setTimeout() when adding a new row.
 ----------------------------------------------------------------------------
 */

if (typeof dti.oasis.grid == "undefined") {
    dti.oasis.grid = (function () {
        return {

            DEFAULT_COLUMN_MIN_WITH: 100,
            DEFAULT_FORMATTED_MASKED_VALUE: "********",
            DEFAULT_IFRAME_MIN_HEIGHT: 200,
            DEFAULT_MENU_FILTERING_HEIGHT: 230,
            DEFAULT_MAX_COLUMN_WIDTH_DATA_LENGTH: 75,
            _protected: {
                _createGridPropertyObj: function (gridId) {
                    function __initGridProperties(gridId) {
                        var gridInfo = dti.oasis.page.getGridInfo(gridId);
                        if (gridInfo && gridInfo.properties) {
                            var properties = gridInfo.properties;
                            // Initialize properties
                            $.extend(properties, {
                                filterflag: false,
                                filtering: false,
                                currentFilterValue: "",
                                lastInsertedId: null,
                                selectedRowId: null,
                                previousSelectedRowId: null,
                                isInCommonAddRow: false,
                                isInSelectRowById: false,
                                isAddMultipleRow: false,
                                isDeleteMultipleRow: false,
                                DeletedMultipleRowsCount: 0,
                                firstAddedMulitpleRowId: null,
                                tablePropertyCacheKeyCode: null,
                                tablePropertyFocusFieldName: null,
                                autoSelectFirstRow: true,
                                isSelectingFirstRowInGrid: false
                            });

                            Object.defineProperties(properties, {
                                id: {
                                    get: function () {
                                        return gridId;
                                    }
                                },

                                hasrows: {
                                    get: function () {
                                        return dti.oasis.grid.hasRows(this.id);
                                    }
                                },

                                pageno: {
                                    get: function () {
                                        return dti.oasis.grid.getPageNo(this.id);
                                    }
                                },

                                nrec: {
                                    get: function () {
                                        return dti.oasis.grid.getRecordCount(this.id);
                                    }
                                },

                                pagesize: {
                                    get: function () {
                                        return dti.oasis.grid.getPageSize(this.id);
                                    }
                                },

                                pages: {
                                    get: function () {
                                        return dti.oasis.grid.getPageCount(this.id);
                                    }
                                },

                                lastpagevisited: {
                                    get: function () {
                                        var pageNo = dti.oasis.grid.getPageNo(this.id);
                                        var pageCount = dti.oasis.grid.getPageCount(this.id);
                                        return ((pageNo + 1) === pageCount);
                                    }
                                },

                                sortOrder: {
                                    get: function () {
                                        return dti.oasis.grid.getSortOrder(this.id);
                                    }
                                }
                            });
                        }
                        return properties;
                    }

                    return __initGridProperties(gridId);
                },

                _generateDataIsland: function (gridInfo) {
                    if (dti.oasis.grid.getDataIsland(gridInfo.id) == null) {
                        var dataIsland = dti.oasis.dataIsland.create({
                            gridId: gridInfo.id,
                            isOriginalDataIsland: true
                        });
                        window[dataIsland.id] = dataIsland;

                        var origDataIsland = dti.oasis.dataIsland.create({
                            gridId: gridInfo.id,
                            isOriginalDataIsland: false
                        });
                        window[origDataIsland.id] = origDataIsland;

                        window[gridInfo.id + "1StyleSheet"] = null;
                    }
                },

                _generateGridFunctions: function (gridId) {
                    window[gridId.concat("_getvalues")] = function (nodename) {
                        return getValues(eval(gridId.concat("1")), nodename);
                    };

                    window[gridId.concat("_getExportType")] = function () {
                        return dti.oasis.page.getGridInfo(gridId)["config"]["gridConfig"]["exportType"];
                    };

                    window[gridId.concat("_insertrow")] = function () {
                        return insertRow(eval(gridId));
                    };

                    window[gridId.concat("_deleterow")] = function () {
                        return deleteRow(eval(gridId));
                    };

                    window[gridId.concat("_filter")] = function (filterString) {
                        return filter(eval(gridId), filterString);
                    };

                    window[gridId.concat("_updatenode")] = function (nodename, nodevalue) {
                        return updateNode(eval(gridId.concat("1")), nodename, nodevalue);
                    };

                    // The third parameter "order" is supported only for jqxGrid.
                    window[gridId.concat("_sort")] = function (field, fieldtype, order) {
                        if (typeof order != "undefined" && order == "-") {
                            order = "desc";
                        }

                        dti.oasis.grid.sort(gridId, field, order);
                    };

                    window[gridId.concat("_updatefilternode")] = function (filternodename, filternodevalue, nodename, nodevalue) {
                        updateFilterNode(eval(gridId.concat("1")), filternodename, filternodevalue, nodename, nodevalue);
                    };

                    window[gridId.concat("_update")] = function () {
                        var formName = dti.oasis.page.getGridInfo(gridId)["config"].gridConfig.formName;
                        var modValue = dti.oasis.grid.getGridChanges(gridId);
                        document.forms[formName].txtXML.value = modValue;
                        baseOnSubmit(document.forms[formName]);
                    };

                    window[gridId.concat("_getchanges")] = function () {
                        return dti.oasis.grid.getGridChanges(gridId);
                    };

                    window[gridId.concat("_getOBREnforcingFieldList")] = function () {
                        return dti.oasis.grid.getProperty(gridId, "OBREnforcingFieldList");
                    };

                    window[gridId.concat("_getOBRConsequenceFieldList")] = function () {
                        return dti.oasis.grid.getProperty(gridId, "OBRConsequenceFieldList");
                    };

                    window[gridId.concat("_getOBRAllAccessedFieldList")] = function () {
                        return dti.oasis.grid.getProperty(gridId, "OBRAllAccessedFieldList");
                    };

                    window[gridId.concat("_getOBREnforcingUpdateIndicator")] = function () {
                        return dti.oasis.grid.getProperty(gridId, "OBREnforcingUpdateIndicator");
                    };
                },

                _generateJqxGridSource: function (gridInfo, params) {
                    var gridId = gridInfo.id;
                    var grid = $("#".concat(gridId));

                    var source = {
                        datatype: "array",
                        id: "@index",
                        datafields: dti.oasis.grid.config.getDataFields(gridId),
                        // Sync the jqxGrid call values to rawData when cell value is changed.
                        // So we can use raw data value in the cells renderer.
                        updaterow: function (jqxRowId, newData, commit) {
                            var rowIndex = dti.oasis.grid.getRowIndexByJqxRowId(gridId, jqxRowId);

                            for (var p in newData) {
                                if (p != "uid" && p != "@id" && p != "boundindex") {
                                    dti.oasis.grid._protected._syncCellValueToRawData(gridId, rowIndex, p, newData[p]);
                                }
                            }
                        }
                    };

                    var hasBeenInitialized = dti.oasis.grid.getProperty(gridId, "hasBeenInitialized");
                    if (params.url) {
                        dti.oasis.grid._protected._prepareSourceForUrlLoading(gridInfo, gridId, grid, params, source);
                    }
                    else if ((dti.oasis.grid.getProperty(gridId, "cacheKey") || dti.oasis.grid.getProperty(gridId, "deferredLoadDataProcess")) &&
                        (dti.oasis.string.isEmpty(hasBeenInitialized) || hasBeenInitialized === false)) {
                        // Only load the cached or deferred data from the remote data source on initial load, not if the grid is reloaded.
                        dti.oasis.grid._protected._prepareSourceForCachedOrDeferredLoading(gridInfo, gridId, grid, params, source);
                    } else {
                        source.localdata = dti.oasis.grid._protected._applyCurrentFilterToRawData(gridInfo, gridId);
                    }

                    // Setup download methods in case of remote data loading
                    source.downloadComplete = function(data, status, xhr) {
                        var records = dti.oasis.grid._protected._loadDataViaAjaxDone(gridInfo, data, status, xhr);
                        if (dti.oasis.grid.getProperty(gridId, "cacheKey") || dti.oasis.grid.getProperty(gridId, "deferredLoadDataProcess")) {
                            // Clear the url, cacheKey and defered loading and properties so it doesn't try to reload when programatically filtered or sorted
                            dti.oasis.grid.setProperty(gridId, "cacheKey", undefined);
                            dti.oasis.grid.setProperty(gridId, "deferredLoadDataProcess", undefined);
                            delete source.url;
                            // Save the data to localdata so the grid treats it as a local data source from here on out
                            source.localdata = records;
                        }
                    };
                    source.loadError = function (xhr, status, error) {
                        dti.oasis.grid._protected._loadDataViaAjaxFail(xhr, status, error);
                    };

                    // Prepare the source to handle virtual mode
                    dti.oasis.grid._protected._prepareSourceForVirtualMode(gridInfo, gridId, grid, params, source);

                    var options = {};
                    var onBeforeSourceLoadComplete = dti.oasis.grid.getCustomGridOption(gridId, "onBeforeSourceLoadComplete");
                    if (onBeforeSourceLoadComplete != undefined) {
                        options.beforeLoadComplete = onBeforeSourceLoadComplete;
                    }

                    var onSourceLoadComplete = dti.oasis.grid.getCustomGridOption(gridId, "onSourceLoadComplete");
                    if (onSourceLoadComplete != undefined) {
                        options.loadComplete = onSourceLoadComplete;
                    }

                    var dataAdapter;
                    if ($.isEmptyObject(options)) {
                        dataAdapter = new $.jqx.dataAdapter(source);
                    } else {
                        dataAdapter =  new $.jqx.dataAdapter(source, options);
                    }
                    return dataAdapter;
                },

                _prepareSourceForUrlLoading: function(gridInfo, gridId, grid, params, source) {
                    // When the URL is provided, setup the source for Ajax loading of the data
                    source.datatype = "json";
                    source.url = params.url;
                    if (params.postData) {
                        source.type = "POST";
                        source.data = params.postData;
                    }
                },

                _prepareSourceForCachedOrDeferredLoading: function(gridInfo, gridId, grid, params, source) {
                    // Only load the cached or deferred data from the remote data source on initial load, not if the grid is reloaded.
                    // When reloaded, the data or a url will be provided directly, so don't load from remote data source.
                    source.datatype = "json";
                    if (dti.oasis.grid.getProperty(gridId, "cacheKey")) {
                        source.url = prepareUrlForAjax(getAppPath() + "/getOasisGridData.do?process=loadOasisGridData&gridId=" + gridId + "&key=" + dti.oasis.grid.getProperty(gridId, "cacheKey"));
                    } else {
                        source.url = prepareUrlForAjax(getAppPath() + "/" + getFormActionAttribute() + "?process=" + dti.oasis.grid.getProperty(gridId, "deferredLoadDataProcess"));
                    }
                },

                _prepareSourceForVirtualMode: function(gridInfo, gridId, grid, params, source) {
                    if (dti.oasis.grid.isVirtualMode(gridId)) {
                        // When in virtual mode, we need to handle sorting and filtering by telling the grid to update,
                        // causing it to load the data via Ajax with the configured url and post data
                        source.sort = function () {
                            // update the grid and send a request to the server.
                            grid.jqxGrid('updatebounddata', 'sort');
                        };
                        source.filter = function () {
                            // Get the total record count when the grid is filtered
                            params.postData._getTotalRecordCount = true;
                            // update the grid and send a request to the server.
                            grid.jqxGrid('updatebounddata', 'filter');
                            // Reset the request for the total record count property
                            params.postData._getTotalRecordCount = false;
                        };
                        // In virtual mode, we need to tell the grid how many total records to expect while loading, sorting or filtering.
                        source.beforeprocessing = function (data) {
                            if (data.totalRecords) {
                                // Update the totalrecords from the data if it is provided
                                source.totalrecords = data.totalRecords;
                            }
                        };
                    }
                    else {
                        source.processData = function(data) {
                            // Remove the record start and end indexes when not in virtual mode so we get the full set of data
                            delete data.recordstartindex;
                            delete data.recordendindex;
                        };
                    }
                },

                // Filter the data with the current filter, or with the default filter if there is no current filter
                _applyCurrentFilterToRawData: function(gridInfo, gridId) {
                    var currentFilterValue = dti.oasis.grid.getProperty(gridId, "currentFilterValue");

                    var filterValueObj = currentFilterValue;
                    if (!currentFilterValue || $.isEmptyObject(currentFilterValue)) {
                        filterValueObj = dti.oasis.grid.getDefaultFilterObj();

                    } else if (typeof currentFilterValue === "string") {
                        filterValueObj = dti.oasis.filter.compile(currentFilterValue);
                    }

                    if (filterValueObj !== currentFilterValue) {
                        dti.oasis.grid.setProperty(gridId, "currentFilterValue", filterValueObj);
                    }

                    gridInfo.data.filteredRawData = dti.oasis.grid.filterRawData(gridId, filterValueObj);
                    return gridInfo.data.filteredRawData;
                },

                _loadDataViaAjax: function(gridInfo, url, data, async) {

                    var defer = $.Deferred();
                    var type = data ? "POST" : "GET";
                    var isAsync = async ? async : true;
                    $.ajax({
                        method:     type,
                        dataType:   "json",
                        url:        url,
                        async:      isAsync
                    })
                        .done(function(data, status, xhr) {
                            defer.resolve(dti.oasis.grid._protected._loadDataViaAjaxDone(gridInfo, data, status, xhr));
                        })
                        .fail(function(xhr, status, errorThrown) {
                            defer.reject(dti.oasis.grid._protected._loadDataViaAjaxFail(gridInfo, xhr, status, errorThrown))
                        });

                    return defer;
                },

                _loadDataViaAjaxDone: function(gridInfo, data, status, xhr) {
                    var gridId = gridInfo.id;

                    // Handle messages
                    var proceed = handleAjaxJsonMessages(data.message);
                    if (!proceed) {
                        var grid = $("#".concat(gridId));
                        grid.jqxGrid('hideloadelement')
                    }
                    // Handle CSRF token update
                    updatePageTokenForJson(data);

                    // Update the gridInfo rawData
                    gridInfo.data.rawData = data.rawData;

                    data.records = dti.oasis.grid._protected._applyCurrentFilterToRawData(gridInfo, gridId);

                    return data.records;
                },

                _loadDataViaAjaxFail: function(gridInfo, xhr, status, errorThrown) {
                    var gridId = gridInfo.id;
                    var msg = "Failed to load " + gridId + " Data: " + status;
                    console.error(msg, errorThrown);
                    return msg;
                },

                _selectRow: function (gridId, jqxRowIndex, jqxRowDisplayIndex) {
                    dti.oasis.grid.clearRowSelection(gridId);

                    var grid = $("#".concat(gridId));

                    dti.oasis.grid.setProperty(gridId, "isSelectingRow", true);

                    grid.jqxGrid('ensurerowvisible', jqxRowDisplayIndex);
                    grid.jqxGrid("selectrow", jqxRowIndex);

                    var pageSize = grid.jqxGrid("pagesize");
                    dti.oasis.grid.setProperty(gridId, "selectedTableRowNo", jqxRowDisplayIndex % pageSize + 1);

                    dti.oasis.grid.setProperty(gridId, "isSelectingRow", false);
                },

                _syncCellValueToRawData: function(gridId, rowIndex, dataField, value){
                    var rowData = dti.oasis.grid.getRowDataByRowIndex(gridId, rowIndex);
                    var val = dti.oasis.grid.syncCellValueToRowDataFormat.getSyncCellValueToRowDataFormatter(gridId, dataField).format(value);

                    rowData[dataField] = val;
                },

                /**
                 * If return false, we should stop selecting row.
                 * @param gridId
                 * @param jqxRowIndex
                 * @param columnName
                 * @returns {boolean}
                 * @private
                 */
                _syncRowDataForSelectRow: function (gridId, jqxRowIndex, columnName) {
                    var isSyncedSuccess = true;

                    // Sync previous selected row data.
                    var currentSelectedGridId = getCurrentlySelectedGridId();
                    if (!dti.oasis.string.isEmpty(currentSelectedGridId) && currentSelectedGridId != gridId) {
                        isSyncedSuccess = dti.oasis.grid.syncChangedValueForRowSelected(currentSelectedGridId, dti.oasis.grid.getSelectedRowIndex(currentSelectedGridId));
                    } else {
                        var previousSelectedRowIndex = dti.oasis.grid.getSelectedRowIndex(gridId);
                        if (previousSelectedRowIndex != null && previousSelectedRowIndex != jqxRowIndex) {
                            isSyncedSuccess = dti.oasis.grid.syncChangedValueForRowSelected(gridId, previousSelectedRowIndex);
                        }
                    }

                    return isSyncedSuccess;
                },

                _adjustGridHeaderHeightForContent: function (gridId) {
                    var gridHeader = $("#" + gridId).find(".jqx-grid-header");
                    var maxHeight = 0;

                    // Get the max height of grid header columns.
                    gridHeader.find(".jqx-grid-column-header").each(function (index, value) {
                        maxHeight = Math.max(maxHeight, value.firstChild.firstChild.firstChild.offsetHeight);
                    });

                    if (gridHeader.height() < maxHeight + 13) {
                        // Add 13px for grid header padding...
                        gridHeader.height(maxHeight + 13);

                        //Set the height of jqx-icon-arrow-down icon and filtered flag icon same with gridHeader height.
                        gridHeader.find(".jqx-grid-column-header").each(function (index, value) {
                            if($(value.firstChild.lastChild).find(".jqx-icon-arrow-down").length > 0) {
                                $(value.firstChild.lastChild).height(maxHeight + 13);
                            }
                            var filterIcon = $(value.firstChild).find(".iconscontainer").find(".filtericon");
                            if(filterIcon.length > 0){
                                filterIcon.height(maxHeight + 13);
                            }
                        });
                    }

                    dti.oasis.grid.resizeGridToFitIFrame(gridId);
                },

                //In below function, don't add code to sync detail value to cell, or else it may cause endless loop.
                _processSyncCellValueToDetailField: function (gridId, jqxRowIndex, element) {
                    var gridObj = $("#".concat(gridId));
                    var dataFld = $(element).data("dtiDatafld");
                    var displayOnlyField = false;

                    if (dti.oasis.string.endsWith(dataFld, "_DISP_ONLY")) {
                        displayOnlyField = true;
                        dataFld = dti.oasis.string.strLeft(dataFld, "_DISP_ONLY");
                    }

                    var columnName = dti.oasis.grid.getActualColumnName(gridId, dataFld);

                    if (dti.oasis.string.isEmpty(columnName) && dti.oasis.string.endsWith(dataFld, "LOVLABEL")) {
                        dataFld = dti.oasis.string.strLeft(dataFld, "LOVLABEL");
                        columnName = dti.oasis.grid.getActualColumnName(gridId, dataFld);
                    }

                    var val = "";
                    if (!dti.oasis.string.isEmpty(columnName)) {
                        if (!dti.oasis.grid.isGridDataField(gridId, columnName)) {
                            var rowId = dti.oasis.grid.getRowIdByJqxRowIndex(gridId, jqxRowIndex);
                            var rawData = dti.oasis.grid.getRowDataById(gridId, rowId);
                            val = rawData[columnName];
                        } else {
                            val = gridObj.jqxGrid("getcellvalue", jqxRowIndex, columnName);
                        }
                    }

                    if (typeof val == "undefined") {
                        val = "";
                    }

                    //check for html elements hooked up to jqxGrided but not part of the list grid
                    if (columnName == null) {
                        if(element.tagName.toUpperCase() == "INPUT") {
                            val = element.defaultValue;
                        }else if (element.tagName.toUpperCase() == "SPAN") {
                            val = element.innerText;
                        }
                    }

                    var formattedVal = val;

                    var cellFormatter = null;
                    if (!dti.oasis.string.isEmpty(columnName)) {
                        if (displayOnlyField) {
                            cellFormatter = dti.oasis.grid.cellFormat.getCellFormatterForDetailDisplayOnlyField(gridId, columnName);
                        } else {
                            cellFormatter = dti.oasis.grid.cellFormat.getCellFormatterForDetailField(gridId, columnName);
                        }
                    }

                    if (cellFormatter != null) {
                        formattedVal = cellFormatter.format(gridId, jqxRowIndex, columnName, val);
                    }

                    if (element.tagName.toUpperCase() == "SPAN") {
                        $(element).text(formattedVal);
                    } else {
                        if (element.type) {
                            switch (element.type) {
                                case "hidden":
                                    element.value = formattedVal;
                                    break;
                                case "text":
                                    element.value = formattedVal;
                                    break;
                                case "textarea":
                                    element.value = formattedVal;
                                    break;
                                case "select-one":
                                    element.value = val;
                                    break;
                                case "select-multiple":
                                    var values = null;
                                    if (typeof val == "string" && !dti.oasis.string.isEmpty(val)) {
                                        values = val.split(",");
                                    } else {
                                        values = val;
                                    }

                                    for (var i = 0; i < element.length; i++) {
                                        var selected = false;
                                        for (var j = 0; j < values.length; j++) {
                                            if (!dti.oasis.string.isEmpty(element[i].value) && element[i].value == values[j]) {
                                                selected = true;
                                                break;
                                            }
                                        }
                                        element[i].selected = selected;
                                    }

                                    var fieldId = $(element).attr('name');
                                    var multiSelectTextField = $('input[name=' + fieldId + 'MultiSelectText]');

                                    if (multiSelectTextField.length > 0) {
                                        multiSelectTextField.val(gridObj.jqxGrid("getcellvalue", jqxRowIndex, "C" + fieldId.toUpperCase() + "LOVLABEL"));
                                    }
                                    break;
                                case "checkbox":
                                    if (!dti.oasis.string.isEmpty(element.value)) {
                                        var checked = false;

                                        //For checkbox, val should either be of type "string" or "boolean".
                                        if (typeof val == "string") {
                                            if (!dti.oasis.string.isEmpty(val)) {
                                                var values = val.split(",");

                                                for (var i = 0; i < values.length; i++) {
                                                    if (!dti.oasis.string.isEmpty(values[i]) && values[i] != "0") {
                                                        checked = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        } else {
                                            if (val) {
                                                checked = true;
                                            }
                                        }

                                        element.checked = checked;
                                    }

                                    break;
                                case "radio":
                                    element.checked = (!dti.oasis.string.isEmpty(element.value) && element.value == val);
                                    break;
                            }
                        }
                    }

                },

                _selectPreviousRowByUpKey: function (gridId) {
                    var grid = $("#".concat(gridId));

                    var selectedJqxRowIndex = grid.jqxGrid('getselectedrowindex');
                    if (selectedJqxRowIndex != -1) {
                        var selectedJqxRowDisplayIndex = parseInt(grid.jqxGrid("getrowdisplayindex", selectedJqxRowIndex));

                        if (selectedJqxRowDisplayIndex != 0) {
                            var rowId = dti.oasis.grid.getRowIdByJqxRowDisplayIndex(gridId, selectedJqxRowDisplayIndex - 1);

                            dti.oasis.grid.setProperty(gridId, "isSelectingRowByUser", true);
                            try {
                                var preSelectRowFn = dti.oasis.grid.getCustomGridOption(gridId, "preSelectRowByUser");
                                if (preSelectRowFn != null &&
                                    preSelectRowFn({
                                        "gridId": gridId,
                                        "rowId": rowId,
                                        "origEvent": "onUpKeyDown"
                                    }) === false) {
                                    return;
                                }

                                dti.oasis.grid.selectRowByRowDisplayIndex(gridId, selectedJqxRowDisplayIndex - 1);

                                var postSelectRowFn = dti.oasis.grid.getCustomGridOption(gridId, "postSelectRowByUser");
                                if (postSelectRowFn != null) {
                                    postSelectRowFn({
                                        "gridId": gridId,
                                        "rowId": rowId,
                                        "origEvent": "onUpKeyDown"
                                    });
                                }
                            } finally {
                                dti.oasis.grid.setProperty(gridId, "isSelectingRowByUser", false);
                            }
                        }
                    }
                },

                _selectNextRowByDownKey: function (gridId) {
                    var grid = $("#".concat(gridId));

                    var selectedJqxRowIndex = grid.jqxGrid('getselectedrowindex');
                    if (selectedJqxRowIndex != -1) {
                        var selectedJqxRowDisplayIndex = parseInt(grid.jqxGrid("getrowdisplayindex", selectedJqxRowIndex));

                        var nextJqxRowIndex = grid.jqxGrid("getrowboundindex", selectedJqxRowDisplayIndex + 1);

                        if (nextJqxRowIndex != -1) {
                            var rowId = dti.oasis.grid.getRowIdByJqxRowDisplayIndex(gridId, nextJqxRowIndex);

                            dti.oasis.grid.setProperty(gridId, "isSelectingRowByUser", true);
                            try {
                                var preSelectRowFn = dti.oasis.grid.getCustomGridOption(gridId, "preSelectRowByUser");
                                if (preSelectRowFn != null &&
                                    preSelectRowFn({
                                        "gridId": gridId,
                                        "rowId": rowId,
                                        "origEvent": "onDownKeyDown"
                                    }) === false) {
                                    return;
                                }

                                dti.oasis.grid.selectRowByJqxRowIndex(gridId, nextJqxRowIndex);

                                var postSelectRowFn = dti.oasis.grid.getCustomGridOption(gridId, "postSelectRowByUser");
                                if (postSelectRowFn != null) {
                                    postSelectRowFn({
                                        "gridId": gridId,
                                        "rowId": rowId,
                                        "origEvent": "onDownKeyDown"
                                    });
                                }
                            } finally {
                                dti.oasis.grid.setProperty(gridId, "isSelectingRowByUser", false);
                            }
                        }
                    }
                },

                resolveFilteringDeferredObj: function(gridId) {
                    var filteringDeferredObjQueue = dti.oasis.grid._protected.getFilteringDeferredObjQueue(gridId);
                    if (filteringDeferredObjQueue.length === 0) {
                        // Initialize the deferred object
                        dti.oasis.grid._protected.addFilteringDeferredObj(gridId);
                    }
                    // Remove the oldest filteringDeferredObj and resolve it
                    filteringDeferredObjQueue.shift().resolve();
                },

                getFilteringDeferredObjQueue: function(gridId) {
                    var filteringDeferredObjQueue = dti.oasis.grid.getProperty(gridId, "filteringDeferredObjQueue");
                    if (!filteringDeferredObjQueue) {
                        filteringDeferredObjQueue = [];
                        dti.oasis.grid.setProperty(gridId, "filteringDeferredObjQueue", filteringDeferredObjQueue);
                    }
                    return filteringDeferredObjQueue;
                },

                getFilteringDeferredObj: function(gridId) {
                    var filteringDeferredObjQueue = dti.oasis.grid._protected.getFilteringDeferredObjQueue(gridId);
                    if (filteringDeferredObjQueue.length === 0) {
                        // Initialize with a resolved deferred object
                        dti.oasis.grid._protected.addFilteringDeferredObj(gridId).resolve();
                    }
                    return filteringDeferredObjQueue[0];
                },

                addFilteringDeferredObj: function(gridId) {
                    // Add the filteringDeferredObj to the end of the array
                    var filteringDeferredObj = $.Deferred();
                    dti.oasis.grid._protected.getFilteringDeferredObjQueue(gridId).push(filteringDeferredObj);

                    return filteringDeferredObj;
                }

            },

            cellEditor: (function () {
                return {
                    generateCellEditor: function (gridInfo, columnConfig, compiledColumnConfig) {
                        var dataType = columnConfig["dataType"] || "";
                        var displayFormat = columnConfig["displayFormat"] || "";

                        switch (dataType) {
                            //case dti.oasis.grid.config.dataType.CURRENCY:
                            //case dti.oasis.grid.config.dataType.CURRENCY_FORMATTED:
                            //    compiledColumnConfig["cellsformat"] = "c2";
                            //    break;
                            case dti.oasis.grid.config.dataType.DATE:
                                if (dti.oasis.dataFormat.getJqxDateDisplayFormat() != null) {
                                    compiledColumnConfig["cellsformat"] = dti.oasis.dataFormat.getJqxDateDisplayFormat();
                                } else {
                                    compiledColumnConfig["cellsformat"] = dti.oasis.dataFormat.getDefaultDateFormat();
                                }
                                compiledColumnConfig["filtertype"] = "date";
                                break;
                            case dti.oasis.grid.config.dataType.DATE_TIME:
                                if (dti.oasis.dataFormat.getJqxDateTimeDisplayFormat() != null) {
                                    compiledColumnConfig["cellsformat"] = dti.oasis.dataFormat.getJqxDateTimeDisplayFormat();
                                } else {
                                    compiledColumnConfig["cellsformat"] = dti.oasis.dataFormat.getDefaultDateTimeFormat();
                                }
                                compiledColumnConfig["filtertype"] = "date";
                                break;
                            case dti.oasis.grid.config.dataType.PERCENTAGE:
                                compiledColumnConfig["cellsformat"] = "p2";
                                break;
                        }

                        if (compiledColumnConfig["columntype"]) {
                            switch (compiledColumnConfig["columntype"]) {
                                case "checkbox":
                                    this._protected._generateCheckBoxEditor(gridInfo, columnConfig, compiledColumnConfig);
                                    return;
                                case "datetimeinput":
                                    this._protected._generateDateInputEditor(gridInfo, columnConfig, compiledColumnConfig);
                                    return;
                                case "dropdownlist":
                                    this._protected._generateDropdownListEditor(gridInfo, columnConfig, compiledColumnConfig);
                                    return;
                                case "combobox":
                                    compiledColumnConfig["columntype"] = "dropdownlist";
                                    this._protected._generateMultiSelectDropdownListEditor(gridInfo, columnConfig, compiledColumnConfig);
                                    return;
                                case "numberinput":
                                    this._protected._generateNumberInputEditor(gridInfo, columnConfig, compiledColumnConfig);
                                    return;
                                //Treating textarea as text. No visible and editable instances in eOasis. Will add, if required
                                case "text":
                                case "textarea":
                                    this._protected._generateTextBoxEditor(gridInfo, columnConfig, compiledColumnConfig);
                                    return;
                            }
                        }

                        switch (dataType) {
                            case dti.oasis.grid.config.dataType.PHONE_NUMBER:
                                this._protected._addDisplayOnlyColumn(gridInfo, columnConfig, compiledColumnConfig);
                                return;
                            case dti.oasis.grid.config.dataType.NUMBER:
                                if(!dti.oasis.string.isEmpty(displayFormat)){
                                    this._protected._addDisplayOnlyColumn(gridInfo, columnConfig, compiledColumnConfig);
                                    return;
                                }

                        }
                    },

                    _protected: {
                        _generateTextBoxEditor: function (gridInfo, columnConfig, compiledColumnConfig) {

                            compiledColumnConfig["createeditor"] = function (row, cellvalue, editor) {
                                editor.attr('maxlength', columnConfig["maxLength"]);
                            };
                        },
                        _generateCheckBoxEditor: function (gridInfo, columnConfig, compiledColumnConfig) {
                            compiledColumnConfig["cellvaluechanging"] = function (jqxRowIndex, column, columntype, oldvalue, newvalue) {
                                return (oldvalue == null || oldvalue == false || oldvalue == "" || oldvalue == 0 || oldvalue == "0");
                            };
                        },

                        _generateDateInputEditor: function (gridInfo, columnConfig, compiledColumnConfig) {
                            if (columnConfig["displayFormat"]) {
                                this._addDisplayOnlyColumn(gridInfo, columnConfig, compiledColumnConfig)
                            }

                            compiledColumnConfig["createeditor"] = function (row, cellvalue, editor) {
                                editor.jqxDateTimeInput({max: new Date(3000, 0, 1)});
                            };
                        },

                        _generateDropdownListEditor: function (gridInfo, columnConfig, compiledColumnConfig) {
                            this._addLovLabelColumn(gridInfo, columnConfig, compiledColumnConfig);

                            var ajaxUrlInfo = null;
                            // If dependency DropdownList Column is enabled, try to get ajax url info for the current column.
                            var dependencyDropdownListColumnEnabled = dti.oasis.page.getCustomPageOption("dependencyDropdownListColumnEnabled");
                            if (dependencyDropdownListColumnEnabled) {
                                ajaxUrlInfo = this._getAjaxUrlInfo(gridInfo.id, columnConfig);
                                if (ajaxUrlInfo !== null) {
                                    // Process LOVLabel for ajaxUrl
                                    this._processDependencyDropdownColumn(gridInfo.id, columnConfig.id);
                                }
                            }

                            if (compiledColumnConfig["editable"]) {
                                if (columnConfig.hasOwnProperty("listData")) {
                                    compiledColumnConfig["createeditor"] = function (row, cellvalue, editor) {
                                        editor.jqxDropDownList({
                                            selectedIndex: 0,
                                            source: columnConfig["listData"],
                                            displayMember: "label",
                                            valueMember: "code",
                                            placeHolder: "-SELECT-"
                                        });

                                        var dropdownListRenderer = dti.oasis.grid.getCustomGridOption(gridInfo.id, "dropdownListRenderer");
                                        if (dropdownListRenderer) {
                                            editor.jqxDropDownList({renderer: dropdownListRenderer});
                                        }

                                        var dropdownListSelectionRenderer = dti.oasis.grid.getCustomGridOption(gridInfo.id, "dropdownListSelectionRenderer");
                                        if (dropdownListSelectionRenderer) {
                                            editor.jqxDropDownList({selectionRenderer: dropdownListSelectionRenderer});
                                        }

                                        var onDropdownListItemSelect = dti.oasis.grid.getCustomGridOption(gridInfo.id, "onDropdownListItemSelect");
                                        if (onDropdownListItemSelect) {
                                            editor.change(function (event) {
                                                onDropdownListItemSelect(gridInfo.id, columnConfig.id, editor, event)
                                            });
                                        }
                                    }
                                } else {
                                    if (ajaxUrlInfo) {
                                        // If the current column has ajaxUrlInfo, use "initeditor" to create different dropdown list for different cell.
                                        compiledColumnConfig["initeditor"] = function (row, cellvalue, editor) {
                                            // Get row data
                                            var rowData = dti.oasis.grid.getRowDataByJqxRowIndex(gridInfo.id, row);
                                            var listData = dti.oasis.grid.cellEditor._protected._getDependencyColumnListData(gridInfo.id, rowData, columnConfig.id);
                                            var val = rowData[columnConfig.id];

                                            // Get selected index in dropdown
                                            var selectedIndex = -1;
                                            for (var i = 0; i < listData.length; i++) {
                                                if (val === listData[i]["code"]) {
                                                    selectedIndex = i;
                                                    break;
                                                }
                                            }

                                            // Create drop down.
                                            editor.jqxDropDownList({
                                                selectedIndex: selectedIndex,
                                                source: listData,
                                                displayMember: "label",
                                                valueMember: "code",
                                                placeHolder: "-SELECT-"
                                            });

                                            editor.jqxDropDownList("val", val);
                                        };
                                    }
                                }

                                compiledColumnConfig["cellvaluechanging"] = function (jqxRowIndex, column, columntype, oldvalue, newvalue) {
                                    // Sync lov label to rawData
                                    var gridId = gridInfo["id"];
                                    var rowIndex = dti.oasis.grid.getRowIndexByJqxRowIndex(gridId, jqxRowIndex);
                                    var rowData = dti.oasis.grid.getRowDataByRowIndex(gridId, rowIndex);
                                    var label = "";
                                    var value = newvalue;

                                    if (newvalue && newvalue["label"] &&
                                        !dti.oasis.string.isEmpty(newvalue["label"]) &&
                                        newvalue["label"] != "-SELECT-") {
                                        label = newvalue["label"];
                                    } else {
                                        value = {value: "", label: ""};
                                    }

                                    rowData[column.concat("LOVLABEL")] = label;

                                    return value;
                                };
                            }
                        },

                        _generateMultiSelectDropdownListEditor: function (gridInfo, columnConfig, compiledColumnConfig) {
                            this._addLovLabelColumn(gridInfo, columnConfig, compiledColumnConfig);

                            if (compiledColumnConfig["editable"] && columnConfig.hasOwnProperty("listData")) {
                                compiledColumnConfig["createeditor"] = function (row, cellvalue, editor) {
                                    editor.jqxDropDownList({
                                        selectedIndex: 0,
                                        source: columnConfig["listData"],
                                        displayMember: "label",
                                        valueMember: "code",
                                        placeHolder: "-SELECT-",
                                        checkboxes: true
                                    });
                                };

                                compiledColumnConfig["initeditor"] = function (row, cellvalue, editor) {
                                    var items = editor.jqxDropDownList('getItems');
                                    editor.jqxDropDownList('uncheckAll');

                                    var codes = $("#".concat(gridInfo["id"])).jqxGrid("getcellvalue", row, columnConfig["id"]);
                                    var values;

                                    if (typeof codes == "string") {
                                        values = codes.split(",");
                                    } else {
                                        values = codes;
                                    }

                                    for (var j = 0; j < values.length; j++) {
                                        if (!dti.oasis.string.isEmpty(values[j]) && values[j] != "-1") {
                                            for (var i = 0; i < items.length; i++) {
                                                if (items[i].value === values[j]) {
                                                    editor.jqxDropDownList('checkIndex', i);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                };

                                compiledColumnConfig["cellvaluechanging"] = function (jqxRowIndex, column, columntype, oldvalue, newvalue) {
                                    var values = [];
                                    var labels = [];

                                    for (var i = 0; i < newvalue.length; i++) {
                                        if (!dti.oasis.string.isEmpty(newvalue[i].value) &&
                                            newvalue[i].value != "-1") {
                                            var index = values.length;
                                            values[index] = newvalue[i].value;
                                            labels[index] = newvalue[i].label;
                                        }
                                    }

                                    $("#".concat(gridInfo["id"])).jqxGrid("setcellvalue", jqxRowIndex, column.concat("LOVLABEL"), labels.join(","));

                                    return values.join(",");
                                };

                                compiledColumnConfig["geteditorvalue"] = function (row, cellvalue, editor) {
                                    return editor.jqxDropDownList("getCheckedItems");
                                };
                            }
                        },

                        _generateNumberInputEditor: function (gridInfo, columnConfig, compiledColumnConfig) {
                            if (dti.oasis.grid.config._protected._hasColumn(
                                gridInfo, compiledColumnConfig["datafield"].concat("_DISP_ONLY"))) {
                                this._addDisplayOnlyColumn(gridInfo, columnConfig, compiledColumnConfig);
                            }

                            compiledColumnConfig["createeditor"] = function (row, cellvalue, editor) {
                                //Consider configuring the # of decimals
                                var decimals = 2;
                                var digits = 10;
                                var maxLength = columnConfig["maxLength"];
                                var allowedNonDecimalDigits = maxLength-decimals-1;
                                if(digits>allowedNonDecimalDigits)
                                    digits = allowedNonDecimalDigits;
                                editor.jqxNumberInput({
                                    digits: digits,
                                    decimalDigits: decimals,
                                    max: 9999999999.99,
                                    min: -9999999999.99
                                });
                            };
                        },

                        _addDisplayOnlyColumn: function (gridInfo, columnConfig, compiledColumnConfig) {
                            var compiledColumns = gridInfo["compiledConfig"]["columns"];
                            var displayOnlyColumnName = compiledColumnConfig["datafield"].concat("_DISP_ONLY");

                            compiledColumns[compiledColumns.length] = {
                                "datafield": displayOnlyColumnName,
                                "text": displayOnlyColumnName,
                                "hidden": true
                            };
                        },

                        _addLovLabelColumn: function (gridInfo, columnConfig, compiledColumnConfig) {
                            var compiledColumns = gridInfo["compiledConfig"]["columns"];
                            var lovLabelColumnName = columnConfig["id"].concat("LOVLABEL");

                            compiledColumns[compiledColumns.length] = {
                                "text": lovLabelColumnName,
                                "datafield": lovLabelColumnName,
                                "hidden": true
                            };
                            compiledColumnConfig["displayfield"] = lovLabelColumnName;
                        },

                        _getAjaxUrlInfo: function (gridId, columnConfig) {
                            var fieldId = columnConfig["fieldId"] || null;
                            if (fieldId === null) {
                                return null;
                            }

                            var ajaxUrl = this._getOrigAjaxUrl(gridId, fieldId);
                            if (ajaxUrl === null) {
                                return null;
                            }

                            // Create a ajaxUrl object.
                            var ajaxUrlInfo = {"params" : [], "useCache": true};
                            var columnName = columnConfig["id"];
                            var delim = this._getDelim(ajaxUrl);
                            var url = "";

                            // Process url parts.
                            var tokens = ajaxUrl.split("&");
                            for (var i = 0; i < tokens.length; i++) {
                                var param = tokens[i].split("=");

                                if (param.length > 1) {
                                    var paramName = param[0];
                                    var paramValue = param[1];

                                    if (paramValue !== delim) {
                                        // Check if the current part has delim
                                        if (dti.oasis.string.startsWith(paramValue, delim) &&
                                            dti.oasis.string.endsWith(paramValue, delim)) {
                                            if (paramName === fieldId) {
                                                // Add current column to the params.
                                                ajaxUrlInfo["params"].push(columnName);
                                            } else {
                                                var parentColumnName = this._getColumnNameByFieldId(gridId, paramName);
                                                if (parentColumnName === null) {
                                                    // Cannot find parent column.
                                                    return null;
                                                }

                                                ajaxUrlInfo["params"].push(parentColumnName);
                                            }
                                        } else {
                                            // Token doesn't have delim, add it to url.
                                            url += tokens[i] + "&";
                                        }
                                    }
                                } else {
                                    // Token doesn't have "=", add it to url
                                    url += tokens[i] + "&";
                                }
                            }

                            if (dti.oasis.string.endsWith(url, "&")) {
                                url = dti.oasis.string.strLeft(url, "&");
                            }
                            ajaxUrlInfo["url"] = url;

                            // Set ajaxUrlInfo to column
                            columnConfig["ajaxUrlInfo"] = ajaxUrlInfo;

                            // Cache dependencyDropdownColumns
                            var dependencyDropdownColumns = this._getDependencyDropdownColumns(gridId);
                            for (var i = 0; i < ajaxUrlInfo["params"].length; i++) {
                                var parentColumnName = ajaxUrlInfo["params"][i];
                                if (dependencyDropdownColumns[parentColumnName]) {
                                    if (!$.inArray(columnName, dependencyDropdownColumns[parentColumnName])) {
                                        dependencyDropdownColumns[parentColumnName].push(columnName);
                                    }
                                } else {
                                    dependencyDropdownColumns[parentColumnName] = [columnName];
                                }
                            }

                            return ajaxUrl;
                        },

                        _getOrigAjaxUrl: function (gridId, fieldId) {
                            // Get all ajax urls.
                            var ajaxUrls = $("#ajaxUrls").attr("value").split("URL");

                            for (var i = 0; i < ajaxUrls.length; i++) {
                                var ajaxUrl = ajaxUrls[i];

                                if (ajaxUrl.indexOf("fieldId=" + fieldId) > -1) {
                                    return ajaxUrl.substring(ajaxUrl.indexOf("]") + 1).trim();
                                }
                            }

                            return null;
                        },

                        _getDelim: function (ajaxUrl) {
                            var delim = /&_delim=([^&])*/i.exec(ajaxUrl);

                            return delim ? delim[1] : "^";
                        },

                        _getDependencyDropdownColumns: function (gridId) {
                            var dependencyDropdownColumns = dti.oasis.grid.getProperty(gridId, "dependencyDropdownColumns");

                            if (!dependencyDropdownColumns) {
                                dependencyDropdownColumns = {};
                                dti.oasis.grid.setProperty(gridId, "dependencyDropdownColumns", dependencyDropdownColumns);
                            }

                            return dependencyDropdownColumns;
                        },

                        _getColumnNameByFieldId: function (gridId, fieldId) {
                            var columnsConfig = dti.oasis.grid.getColumnsConfig(gridId);

                            for (var i = 0; i < columnsConfig.length; i++) {
                                if (columnsConfig[i]["fieldId"] === fieldId) {
                                    return columnsConfig[i]["id"];
                                }
                            }

                            return null;
                        },

                        _processDependencyDropdownColumn: function (gridId, columnName) {
                            var rawData = dti.oasis.grid.getRawData(gridId);

                            for (var i = 0; i < rawData.length; i++) {
                                var rowData = rawData[i];
                                var label = this._getDependencyColumnLabel(gridId, rowData, columnName);
                                rowData[columnName + "LOVLABEL"] = label;
                            }
                        },

                        _getDependencyColumnListData: function (gridId, rowData, columnName) {
                            var columnConfig = dti.oasis.grid.getColumnConfig(gridId, columnName);
                            var ajaxUrlInfo = columnConfig["ajaxUrlInfo"];
                            var params = ajaxUrlInfo["params"];

                            // Get listData from cache.
                            var listData = ajaxUrlInfo["listData"];
                            if (listData) {
                                for (var i = 0; i < params.length; i++) {
                                    listData = listData[params[i]];

                                    if (!listData) {
                                        listData = null;
                                        break;
                                    }
                                }
                            }

                            if (listData) {
                                return listData;
                            }

                            // Get ajax URL
                            var url = ajaxUrlInfo["url"] + "&process=loadListOfValuesForJqxGrid";
                            for (var i = 0; i < params.length; i++) {
                                var parentColumnName = params[i];
                                var parentColumnConfig = dti.oasis.grid.getColumnConfig(gridId, parentColumnName);
                                url += "&" + parentColumnConfig["fieldId"] + "=" + encodeURIComponent(rowData[parentColumnName]);
                            }

                            var oValueList = null;
                            // Call Ajax to get label.
                            new AJAXRequest("GET", url, "", function(ajax) {
                                if (ajax.readyState == 4) {
                                    if (ajax.status == 200) {
                                        var data = ajax.responseXML;
                                        if (!handleAjaxMessages(data, null)) {
                                            return;
                                        }

                                        oValueList = parseXML(data);

                                        // Cache list data.
                                        if (ajaxUrlInfo["useCache"]) {
                                            var listData = ajaxUrlInfo["listData"] || null;
                                            if (listData === null) {
                                                listData = {};
                                                ajaxUrlInfo["listData"] = listData;
                                            }

                                            for (var i = 0; i < params.length; i++) {
                                                var parentVal = rowData[params[i]];
                                                if (listData[parentVal]) {
                                                    listData = listData[parentVal];
                                                } else {
                                                    if (i === params.length - 1) {
                                                        listData[parentVal] = oValueList;
                                                    } else {
                                                        listData[parentVal] = {};
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }, false);

                            return oValueList;
                        },

                        _getDependencyColumnLabel: function (gridId, rowData, columnName) {
                            if (dti.oasis.string.isEmpty(rowData[columnName])) {
                                return "";
                            }

                            var columnConfig = dti.oasis.grid.getColumnConfig(gridId, columnName);
                            var ajaxUrlInfo = columnConfig["ajaxUrlInfo"];
                            var val = rowData[columnName];

                            // Get listData from cache.
                            var listData = this._getDependencyColumnListData(gridId, rowData, columnName);
                            if (listData) {
                                // If cached data is not empty, get label in list data.
                                for (var i = 0; i < listData.length; i++) {
                                    if (val === listData[i]["code"]) {
                                        return listData[i]["label"];
                                    }
                                }
                            }

                            return "";
                        }
                    }
                }
            })(),

            cellFormat: (function () {
                return {
                    getCellFormatterForCellsRenderer: function (gridId, dataField, isAgg) {
                        var cellFormatter = null;

                        // Get page level custom option for formatting cell value.
                        var customCellValueFormatterForCellsRender = dti.oasis.grid.getCustomColumnOption(gridId, dataField, "cellValueFormatterForCellsRender");
                        if (customCellValueFormatterForCellsRender != null) {
                            return customCellValueFormatterForCellsRender;
                        }

                        var columnConfig = dti.oasis.grid.config.getOasisColumnConfig(gridId, dataField);

                        if (columnConfig != null) {
                            if (dti.oasis.grid.hasColumn(gridId, dataField + "_DISP_ONLY") && !isAgg) {
                                return {
                                    format: function (gridId, jqxRowIndex, dataField, value) {
                                        if (dti.oasis.string.isEmpty(value)) {
                                            return "";
                                        }

                                        var rowId = dti.oasis.grid.getRowIdByJqxRowIndex(gridId, jqxRowIndex);
                                        return dti.oasis.grid.getCellValue(gridId, rowId, dataField + "_DISP_ONLY");
                                    }
                                };
                            } else {
                                if (columnConfig != null && columnConfig["dataType"]) {
                                    switch (columnConfig["dataType"]) {
                                        case dti.oasis.grid.config.dataType.CURRENCY:
                                        case dti.oasis.grid.config.dataType.CURRENCY_FORMATTED:
                                            return this.moneyFormatter();

                                        case dti.oasis.grid.config.dataType.DATE:
                                            return this.dateFormatter("MM/dd/yyyy");

                                        case dti.oasis.grid.config.dataType.NUMBER:
                                            if (columnConfig.hasOwnProperty("displayFormat") &&
                                                !dti.oasis.string.isEmpty(columnConfig["displayFormat"])) {
                                                return this.numberFormatter(columnConfig["displayFormat"]);
                                            }
                                            return null;

                                        case dti.oasis.grid.config.dataType.PHONE_NUMBER:
                                            return this.phoneNumberFormatter();

                                        case dti.oasis.grid.config.dataType.PERCENTAGE:
                                            return this.percentageFormatter();
                                    }
                                }
                            }
                        }

                        return null;
                    },

                    getCellFormatterForDetailField: function (gridId, dataField) {
                        var columnConfig = dti.oasis.grid.config.getOasisColumnConfig(gridId, dataField);

                        if (columnConfig != null && columnConfig["dataType"]) {
                            switch (columnConfig["dataType"]) {
                                case dti.oasis.grid.config.dataType.CURRENCY:
                                case dti.oasis.grid.config.dataType.CURRENCY_FORMATTED:
                                    return this.moneyFormatter();
                                case dti.oasis.grid.config.dataType.DATE:
                                    return this.dateFormatter();
                                case dti.oasis.grid.config.dataType.DATE_TIME:
                                    return this.dateTimeFormatter(dti.oasis.dataFormat.getDefaultDateTimeFormat());
                                case dti.oasis.grid.config.dataType.PERCENTAGE:
                                    return this.jqxNumberFormatter("p2");
                            }

                            if (columnConfig["displayType"] == "checkbox") {
                                return this.checkboxValueFormatter();
                            }
                        }

                        return null;
                    },

                    getCellFormatterForDetailDisplayOnlyField: function (gridId, dataField) {
                        var columnConfig = dti.oasis.grid.config.getOasisColumnConfig(gridId, dataField);

                        if (columnConfig != null && columnConfig["dataType"]) {
                            switch (columnConfig["dataType"]) {
                                case dti.oasis.grid.config.dataType.DATE:
                                    if (dti.oasis.dataFormat.getJqxDateDisplayFormat() == null) {
                                        return this.dateFormatter();
                                    } else {
                                        return this.dateFormatter(dti.oasis.dataFormat.getJqxDateDisplayFormat());
                                    }
                                case dti.oasis.grid.config.dataType.DATE_TIME:
                                    if (dti.oasis.dataFormat.getJqxDateTimeDisplayFormat() == null) {
                                        return this.dateFormatter(dti.oasis.dataFormat.getDefaultDateTimeFormat());
                                    } else {
                                        return this.dateFormatter(dti.oasis.dataFormat.getJqxDateTimeDisplayFormat());
                                    }
                                case dti.oasis.grid.config.dataType.NUMBER:
                                    if (columnConfig.hasOwnProperty("displayFormat") &&
                                        !dti.oasis.string.isEmpty(columnConfig["displayFormat"]) &&
                                        columnConfig["displayFormat"].toUpperCase().indexOf("C") != 0 &&
                                        columnConfig["displayFormat"].toUpperCase().indexOf("P") != 0) {

                                        return this.numberFormatter(columnConfig["displayFormat"]);
                                    }
                                    return null;
                                case dti.oasis.grid.config.dataType.PHONE_NUMBER:
                                    return this.phoneNumberFormatter();
                            }
                        }

                        return null;
                    },

                    /**
                     * Format cell value to rawData value.
                     * @param gridID
                     * @param dataField
                     */


                    dateFormatter: function (pattern) {
                        return {
                            format: function (gridId, jqxRowIndex, dataField, value) {
                                return dti.oasis.dataFormat.formatDate(value, pattern);
                            }
                        }
                    },

                    dateTimeFormatter: function (pattern) {
                        return {
                            format: function (gridId, jqxRowIndex, dataField, value) {
                                if (value.toUpperCase().lastIndexOf('AM') == -1 && value.toUpperCase().lastIndexOf('PM') == -1) {
                                    return dti.oasis.dataFormat.formatDate(value, pattern);
                                } else {
                                    return value;
                                }
                            }
                        }
                    },

                    moneyFormatter: function () {
                        return {
                            format: function (gridId, jqxRowIndex, dataField, value) {
                                if (dti.oasis.string.isEmpty(value)) {
                                    return "";
                                }

                                return dti.oasis.dataFormat.formatMoney(value);
                            }
                        }
                    },

                    numberFormatter: function (pattern) {
                        return {
                            format: function (gridId, jqxRowIndex, dataField, value) {
                                if (dti.oasis.string.isEmpty(value)) {
                                    return "";
                                }

                                return dti.oasis.dataFormat.formatNumber(value, pattern);
                            }
                        }
                    },

                    percentageFormatter: function () {
                        return {
                            format: function (gridId, jqxRowIndex, dataField, value) {
                                if (dti.oasis.string.isEmpty(value)) {
                                    return "";
                                }

                                return formatPctStrVal(value);
                            }
                        }
                    },

                    phoneNumberFormatter: function () {
                        return {
                            format: function (gridId, jqxRowIndex, dataField, value) {
                                return formatPhoneNumberForDisplay(value);
                            }
                        };
                    },

                    jqxNumberFormatter: function (pattern) {
                        return {
                            format: function (gridId, jqxRowIndex, dataField, value) {
                                if (dti.oasis.string.isEmpty(value)) {
                                    return "";
                                }

                                var calendar = $.jqx.dataFormat.defaultcalendar();

                                if (pattern && pattern.charAt(0).toUpperCase() == "P") {
                                    //Remove comma separator for percentage pattern.
                                    calendar.thousandsseparator = "";
                                }
                                return $.jqx.dataFormat.formatNumber(value, pattern, calendar);
                            }
                        };
                    },

                    checkboxValueFormatter: function () {
                        return {
                            format: function (gridId, jqxRowIndex, dataField, value) {
                                return dti.oasis.dataFormat.formatCheckboxCellValue(value);
                            }
                        };
                    }
                };
            })(),

            rowDataFormat: (function () {
                return {
                    _protected: {
                        _createDefaultFormatter: function () {
                            return {
                                format: function (val) {
                                    return val;
                                }
                            }
                        }
                    },

                    getRowDataFormatter: function (gridId, dataField) {
                        var columnConfig = dti.oasis.grid.getColumnConfig(gridId, dti.oasis.grid.getActualColumnName(gridId, dataField));

                        if (columnConfig) {
                            switch (columnConfig["dataType"]) {
                                case "PT":
                                    return this.percentageFormatter();
                                case "CU":
                                    return this.moneyFormatter();
                            }

                            if (columnConfig["displayType"] == "checkbox") {
                                return this.checkboxValueFormatter();
                            }
                        }
                        return this._protected._createDefaultFormatter();
                    },

                    checkboxValueFormatter: function () {
                        return {
                            format: function (val) {
                                return dti.oasis.dataFormat.formatCheckboxCellValue(val);
                            }
                        }
                    },

                    percentageFormatter: function () {
                        return {
                            format: function (val) {
                                if (dti.oasis.string.isEmpty(val)) {
                                    return "";
                                }

                                return dti.oasis.dataFormat.formatPercentage(val.toString());
                            }
                        }
                    },

                    moneyFormatter: function () {
                        return {
                            format: function (val) {
                                if (dti.oasis.string.isEmpty(val)) {
                                    return "";
                                }
                                return dti.oasis.dataFormat.unformatMoney(val.toString());
                            }
                        }
                    }
                }
            })(),

            formatForSetCellValue: (function () {
                return {
                    _protected: {
                        _createDefaultFormatter: function () {
                            return {
                                unformat: function (val) {
                                    return val;
                                }
                            }
                        }
                    },

                    getFormatForSetCellValueFormatter: function (gridId, dataField) {
                        var columnConfig = dti.oasis.grid.getColumnConfig(gridId, dti.oasis.grid.getActualColumnName(gridId, dataField));

                        if (columnConfig) {
                            switch (columnConfig["dataType"]) {
                                case "PT":
                                    return this.percentageFormatter();
                                case "CU":
                                    return this.moneyFormatter();
                            }

                            if (columnConfig["displayType"] == "checkbox") {
                                return this.checkboxValueFormatter();
                            }
                        }
                        return this._protected._createDefaultFormatter();
                    },

                    checkboxValueFormatter: function () {
                        return {
                            unformat: function (val) {
                                return dti.oasis.dataFormat.unformatCheckboxCellValue(val);
                            }
                        }
                    },

                    percentageFormatter: function () {
                        return {
                            unformat: function (val) {
                                if (dti.oasis.string.isEmpty(val)) {
                                    return "";
                                }

                                return dti.oasis.dataFormat.unformatPercentage(val.toString());
                            }
                        }
                    },

                    moneyFormatter: function () {
                        return {
                            unformat: function (val) {
                                if (dti.oasis.string.isEmpty(val)) {
                                    return "";
                                }

                                return dti.oasis.dataFormat.unformatMoney(val.toString());
                            }
                        }
                    }
                }
            })(),

            syncCellValueToRowDataFormat: (function () {
                return {
                    _protected: {
                        _createDefaultFormatter: function (dataField) {
                            return {
                                format: function (val) {
                                    if (typeof val == "number" && dataField != "@index") {
                                        return val.toString();
                                    } else {
                                        //There may exist field whose displayType is not "datetimeinput", but its value is of date type?
                                        if (dti.oasis.date.isDate(val)) {
                                            return dti.oasis.date.formatDate(val);
                                        }
                                    }
                                    return val;
                                }
                            }
                        }
                    },

                    getSyncCellValueToRowDataFormatter: function (gridId, dataField) {
                        var columnConfig = dti.oasis.grid.getColumnConfig(gridId, dti.oasis.grid.getActualColumnName(gridId, dataField));

                        if (columnConfig) {
                            if (columnConfig["dataType"] == "PT") {
                                return this.percentageFormatter();
                            }

                            switch (columnConfig["displayType"]) {
                                case "checkbox":
                                    return this.checkboxValueFormatter();
                                case "datetimeinput":
                                    return this.dateFormatter();
                            }
                        }

                        return this._protected._createDefaultFormatter(dataField);
                    },

                    percentageFormatter: function () {
                        return {
                            format: function (val) {
                                if (dti.oasis.string.isEmpty(val)) {
                                    return "";
                                }

                                return dti.oasis.dataFormat.formatPercentage(val.toString());
                            }
                        }
                    },

                    checkboxValueFormatter: function () {
                        return {
                            format: function (val) {
                                return dti.oasis.dataFormat.formatCheckboxCellValue(val);
                            }
                        }
                    },

                    dateFormatter: function () {
                        return {
                            format: function (val) {
                                return dti.oasis.date.formatDate(val);
                            }
                        }
                    }
                }
            })(),

            cellRenderer: (function () {

            })(),

            config: (function () {
                return {
                    _protected: (function () {
                        return {
                            _hasColumn: function (gridInfo, dataFieldId) {
                                if (dti.oasis.string.isEmpty(dataFieldId)) {
                                    return false;
                                }

                                var upperCaseColumnNameMap;
                                if (gridInfo["data"].hasOwnProperty("upperCaseColumnNameMap")) {
                                    upperCaseColumnNameMap = gridInfo["data"]["upperCaseColumnNameMap"];
                                } else {
                                    upperCaseColumnNameMap = dti.oasis.grid.getUpperCaseColumnNameMap(gridInfo.id);
                                }
                                if (upperCaseColumnNameMap.hasOwnProperty(dataFieldId.toUpperCase()) ||
                                    upperCaseColumnNameMap.hasOwnProperty("@".concat(dataFieldId.toUpperCase()))) {
                                    return true;
                                }

                                return false;
                            },

                            _getJqxColumnsConfig: function (gridInfo) {
                                return gridInfo["compiledConfig"]["columns"];
                            },

                            _getJqxColumnConfig: function (gridInfo, columnName) {
                                var columns = this._getJqxColumnsConfig(gridInfo);

                                for (var i = 0; i < columns.length; i++) {
                                    if (columns[i]["datafield"].toUpperCase() == columnName.toUpperCase()) {
                                        return columns[i];
                                    }
                                }

                                return null;
                            },

                            _getOasisColumnsConfig: function (gridInfo) {
                                return gridInfo["config"]["columnsConfig"];
                            },

                            _getOasisColumnConfig: function (gridInfo, columnName) {
                                var columnsConfig = this._getOasisColumnsConfig(gridInfo);

                                for (var i = 0; i < columnsConfig.length; i++) {
                                    if (columnName.toUpperCase() == columnsConfig[i]["id"].toUpperCase()) {
                                        return columnsConfig[i];
                                    }
                                }

                                return null;
                            },

                            _getOasisColumnDataType: function (gridInfo, columnName) {
                                var columnConfig = this._getOasisColumnConfig(gridInfo, columnName);

                                if (columnConfig == null || !columnConfig.hasOwnProperty("dataType")) {
                                    return null;
                                }

                                return columnConfig["dataType"];
                            },

                            /**
                             * Add hidden columns to grid columns config for the data fields which are in columns config.
                             * @param gridInfo
                             * @private
                             */
                            __addHiddenColumns: function (gridInfo) {
                                var dataFields = gridInfo["compiledConfig"]["dataFields"];
                                var columns = gridInfo["compiledConfig"]["columns"];

                                for (var i = 0; i < dataFields.length; i++) {
                                    var dataFieldName = dataFields[i].name;
                                    var found = false;
                                    for (var j = 0; j < columns.length; j++) {
                                        if (dataFieldName == columns[j].datafield) {
                                            found = true;
                                            break;
                                        }
                                    }

                                    if (!found) {
                                        columns[columns.length] = {
                                            "text": dataFieldName,
                                            "datafield": dataFieldName,
                                            "hidden": true
                                        };
                                    }
                                }
                            },

                            __compileGridSetting: function(gridInfo) {
                                // The default config of a grid.
                                var gridSetting = {
                                    width: "100%",
                                    height: 200,
                                    pagesizeoptions: [5, 10, 20],
                                    pagesize: 10,
                                    pageable: true,
                                    editable: true,
                                    sortable: true,
                                    filterable: true,
                                    columnsresize: true,
                                    enabletooltips: true,
                                    selectionmode: 'none',
                                    altrows: true
                                };

                                if (gridInfo.hasOwnProperty("config") && gridInfo["config"].hasOwnProperty("gridConfig")) {
                                    // Width
                                    if (gridInfo["config"]["gridConfig"].hasOwnProperty("width")) {
                                        var width = gridInfo["config"]["gridConfig"]["width"];

                                        if (dti.oasis.string.isIntegerValue(width)) {
                                            gridSetting["width"] = parseInt(width, 10);
                                        } else if (dti.oasis.string.endsWith(width, "px")) {
                                            gridSetting["width"] = parseInt(dti.oasis.string.strLeft(width, "px"), 10);
                                        } else {
                                            gridSetting["width"] = width;
                                        }
                                    }

                                    // Height
                                    // if (gridInfo["config"]["gridConfig"].hasOwnProperty("height")) {
                                    //     var height = gridInfo["config"]["gridConfig"]["height"];
                                    //
                                    //     if (dti.oasis.string.isIntegerValue(height)) {
                                    //         gridSetting["height"] = parseInt(height, 10) + 30;
                                    //     } else if (dti.oasis.string.endsWith(height, "px")) {
                                    //         gridSetting["height"] = parseInt(dti.oasis.string.strLeft(height, "px"), 10) + 30;
                                    //     } else {
                                    //         gridSetting["height"] = height;
                                    //     }
                                    // }

                                    // Set grid height to grid holder height - grid margin.
                                    gridSetting["height"] = $("#DIV_" + gridInfo.id).closest(".divGridHolder").height() - 16;

                                    //Pageable
                                    var customPageable = dti.oasis.grid.getCustomGridOption(gridInfo.id, "pageable");
                                    if (customPageable !== null) {
                                        gridSetting["pageable"] = customPageable;
                                    }

                                    // Page size
                                    if (gridSetting["pageable"] == true) {
                                        if (gridInfo["config"]["gridConfig"].hasOwnProperty("pageSize")) {
                                            gridSetting["pagesize"] = gridInfo["config"]["gridConfig"]["pageSize"];
                                        }

                                        // Add page size option
                                        var pageSize = parseInt(gridSetting["pagesize"]);
                                        var pageSizeOptions = gridSetting["pagesizeoptions"];

                                        for (var i = 0; i < pageSizeOptions.length; i++) {
                                            var pageSizeI = parseInt(pageSizeOptions[i]);
                                            if (pageSize == pageSizeI) {
                                                // Exists in page size options.
                                                break;

                                            } else if (pageSize < pageSizeI) {
                                                // Not exists in page size option, add it to the correct positon.
                                                pageSizeOptions.splice(i, 0, pageSize);
                                                break;

                                            } else if (i == pageSizeOptions.length - 1) {
                                                // Greater than the last page size, add it to the end of the page size options.
                                                pageSizeOptions[pageSizeOptions.length] = pageSize;
                                                break;
                                            }
                                        }
                                    }

                                    // Editable
                                    if (gridInfo["config"]["gridConfig"].hasOwnProperty("editable")) {
                                        gridSetting["editable"] = gridInfo["config"]["gridConfig"]["editable"];
                                    }

                                    // Sortable
                                    if (gridInfo["config"]["gridConfig"].hasOwnProperty("sortable")) {
                                        gridSetting["sortable"] = gridInfo["config"]["gridConfig"]["sortable"];
                                    }
                                }

                                var filterable = dti.oasis.grid.getCustomGridOption(gridInfo.id, "filterable");
                                if (filterable !== null) {
                                    gridSetting["filterable"] = filterable;
                                }

                                gridSetting["ready"] = function() {
                                    dti.oasis.grid.onGridReady(gridInfo);
                                };

                                gridSetting["columnmenuopening"] = function (menu, datafield, height) {
                                    dti.oasis.grid.resizeIFrameForMenuFiltering(datafield, gridInfo.id, true);
                                };

                                gridSetting["columnmenuclosing"] = function (menu, datafield, height) {
                                    dti.oasis.grid.resizeIFrameForMenuFiltering(datafield, gridInfo.id, false);
                                };

                                gridInfo["compiledConfig"]["gridSetting"] = gridSetting;
                            },

                            /**
                             * Compile columns setting
                             *
                             * Callback function handleGetGridAggregateSetting(gridId, columnId) for columns:
                             * If the function handleGetGridAggregateSetting is implemented on page, and the function doesn't return
                             * null for the current column, the aggregates setting will be added to the column.
                             *
                             * @param gridInfo
                             * @private
                             */
                            __compileColumns: function(gridInfo) {
                                var columns = [];
                                gridInfo["compiledConfig"]["columns"] = columns;

                                var widthSettings = this.__getCompiledColumnsWidth(gridInfo);

                                var columnsConfig = dti.oasis.grid.config.getOasisColumnsConfig(gridInfo.id);
                                for (var i = 0; i < columnsConfig.length; i++) {
                                    var column = {};

                                    var visible =  columnsConfig[i]["visible"];

                                    if (visible) {
                                        columns[columns.length] = column;

                                        column["text"] = columnsConfig[i]["label"] || columnsConfig[i]["id"];

                                        column["datafield"] = columnsConfig[i]["id"];

                                        var masked = columnsConfig[i]["masked"];
                                        column["editable"] = (columnsConfig[i]["editable"]) ? true : false;
                                        column["enabletooltips"] = true;

                                        if (widthSettings[column["datafield"]]["width"]) {
                                            column["width"] = widthSettings[column["datafield"]]["width"];
                                        }
                                        if (widthSettings[column["datafield"]]["minwidth"]) {
                                            column["minwidth"] = widthSettings[column["datafield"]]["minwidth"];
                                        }

                                        var displayType = this.__getColumnDisplayType(columnsConfig[i]);

                                        var customDisplayType = dti.oasis.grid.getCustomColumnOption(gridInfo.id, column["datafield"], "displayType");
                                        if (customDisplayType != null) {
                                            displayType = customDisplayType;
                                        }

                                        if (displayType != null) {
                                            column["columntype"] = displayType;
                                        }

                                        var fnGetEditorValue = dti.oasis.grid.getCustomColumnOption(gridInfo.id, column["datafield"], "getEditorValue");
                                        if (fnGetEditorValue != null) {
                                            column["geteditorvalue"] = fnGetEditorValue;
                                        }

                                        // The width config in webwb is from the text field width (numbers of characters) configuration in webeb.
                                        // Since the column width of jqxGrid should be px, disabled this function for now.
                                        // if (columnsConfig[i]["width"] && !dti.oasis.string.isEmpty(columnsConfig[i]["width"])) {
                                        //     if (dti.oasis.string.isIntegerValue(columnsConfig[i]["width"])) {
                                        //         column["width"] = parseInt(columnsConfig[i]["width"], 10) * 8;
                                        //     } else {
                                        //         column["width"] = columnsConfig[i]["width"];
                                        //     }
                                        // }

                                        // Handle the width of Select all
                                        var selectIndColumnName = null;
                                        // Check if the selectIndColumnName is valid. not null or ""
                                        var hasSelectIndColumnName = dti.oasis.grid.hasSelectIndColumnName(gridInfo.id);
                                        if (hasSelectIndColumnName) {
                                            selectIndColumnName = dti.oasis.grid.getSelectIndColumnName(gridInfo.id);
                                        }
                                        if (hasSelectIndColumnName && column["datafield"] == selectIndColumnName) {
                                            if (gridInfo["config"]["gridConfig"]["selectable"]) {
                                                column["width"] = 20;
                                                var customGridOption = dti.oasis.grid.getCustomGridOption(gridInfo.id, "selectAllEnabled");
                                                if (customGridOption == null || customGridOption(gridInfo)) {
                                                    column["renderer"] = function () {
                                                        return "<div style=\"padding-bottom: 2px; overflow: hidden; text-overflow: ellipsis; text-align: left; margin-left: 4px; margin-right: 2px; margin-bottom: 4px; margin-top: 4px;\">" +
                                                            "<div class=\"jqx-checkbox-default jqx-fill-state-normal jqx-rc-all\" style=\"float:none; margin: auto; width: 13px; height: 13px;\">" +
                                                            "<div title=\"Select or de-select all\" style=\"width: 13px; height: 13px;\">" +
                                                            "<span id=\"" + gridInfo.id + "_chkCSELECT_ALL\" style=\"width: 13px; height: 13px;\"></span>" +
                                                            "</div>" +
                                                            "</div>" +
                                                            "</div>";
                                                    };
                                                } else {
                                                    column["renderer"] = function () {
                                                        return "<div style=\"padding-bottom: 2px; overflow: hidden; text-overflow: ellipsis; text-align: left; margin-left: 4px; margin-right: 2px; margin-bottom: 4px; margin-top: 4px;\">" +
                                                            "<div class=\"jqx-checkbox-default jqx-fill-state-normal jqx-rc-all\" style=\"float:none; margin: auto; width: 13px; height: 13px;\">" +
                                                            "<div title=\"Select or de-select all\" style=\"width: 13px; height: 13px;\">" +
                                                            "<span id=\"" + gridInfo.id + "_chkCSELECT_ALL\" style=\"width: 13px; height: 13px;\" class=\"jqx-checkbox-disabled\"></span>" +
                                                            "</div>" +
                                                            "</div>" +
                                                            "</div>";
                                                    };
                                                }
                                            } else {
                                                //column["width"] = 80;
                                            }

                                        } else if (displayType == "checkbox") {
                                            // TODO Maybe change the width property later.
                                            //column["width"] = 80;
                                        }

                                        var displayedAsCheckbox = (hasSelectIndColumnName && column["datafield"] == selectIndColumnName) || displayType == "checkbox";

                                        column["filterable"] = !(displayedAsCheckbox || masked);
                                        column["sortable"] = !(hasSelectIndColumnName && column["datafield"] == selectIndColumnName)
                                            && gridInfo["config"]["gridConfig"]["sortable"] && !masked;
                                        column["menu"] = !(displayedAsCheckbox || masked);
                                        column["enabletooltips"] = !(displayedAsCheckbox || masked);
                                        column["cellsalign"] = columnsConfig[i]["align"];

                                        // Set the column title align to be center
                                        column["align"] = "center";

                                        column["cellsrenderer"] = this.__getCellsRenderer(gridInfo.id, column["datafield"], columnsConfig[i], masked);

                                        // Get page level custom options.
                                        var customColumnOptions = dti.oasis.grid.getCustomColumnOptions(gridInfo.id, column["datafield"]);
                                        if (customColumnOptions !== null) {
                                            var customColumnOptionNames = ["cellBeginEdit", "cellEndEdit", "initEditor", "aggregates", "aggregatesRenderer", "enabletooltips", "menu"];
                                            for (var j = 0; j < customColumnOptionNames.length; j++) {
                                                var lowerCaseColumnOptionName = customColumnOptionNames[j].toLowerCase();
                                                var customColumnOption = null;

                                                if (customColumnOptions.hasOwnProperty(customColumnOptionNames[j])) {
                                                    customColumnOption = customColumnOptions[customColumnOptionNames[j]];
                                                } else if (customColumnOptions.hasOwnProperty(lowerCaseColumnOptionName)) {
                                                    customColumnOption = customColumnOptions[lowerCaseColumnOptionName];
                                                }

                                                if (customColumnOption !== null) {
                                                    column[lowerCaseColumnOptionName] = customColumnOption;
                                                }
                                            }
                                        }

                                        this.__setColumnAgg(columnsConfig[i]["aggregates"], column, gridInfo);

                                        // If the width is empty or percentage value and min-width is not set, set the default value of min-width.
                                        if (!column.hasOwnProperty("minwidth")) {
                                            if (!column.hasOwnProperty("width") ||
                                                (typeof column["width"] == "string" &&
                                                    dti.oasis.string.endsWith(column["width"], "%"))) {
                                                column["minwidth"] = dti.oasis.grid.DEFAULT_COLUMN_MIN_WITH;
                                            }
                                        }

                                        if (!column["hidden"] && column["editable"] && !column["cellbeginedit"]) {
                                            var cellEditableFunction = dti.oasis.grid.getCellEditableFunction(gridInfo.id, column["datafield"]);
                                            if (cellEditableFunction) {
                                                column["cellbeginedit"] = cellEditableFunction;
                                            }
                                        }

                                        dti.oasis.grid.cellEditor.generateCellEditor(gridInfo, columnsConfig[i], column);
                                    }

                                }

                                var columns = dti.oasis.page.getGridInfo(gridInfo.id).compiledConfig.columns;
                                var columnDataFields = {};
                                //add @id and @index to be used when adding a new row
                                columns[columns.length] = {
                                    "text":"@index",
                                    "datafield": "@index",
                                    "hidden": true
                                };
                                columns[columns.length] = {
                                    "text":"@id",
                                    "datafield": "@id",
                                    "hidden": true
                                };
                                for (var i = 0; i< columns.length; i++) {
                                    columnDataFields[columns[i].datafield] = 1;
                                }
                                gridInfo["compiledConfig"]["columnDataFields"] = columnDataFields;

                            },

                            __setColumnAgg: function(configuredAgg, column, gridInfo) {
                                if (!column["aggregates"] && configuredAgg) {
                                    var agg = [];

                                    if (configuredAgg.indexOf("SUM") > -1) {
                                        agg.push({"sum": this.__calculateSum});
                                    }

                                    if (configuredAgg.indexOf("COUNT") > -1) {
                                        //Keep "count" in the end of array, or else count may get formatted, which is incorrect.
                                        agg.push("count");
                                    }

                                    column["aggregates"] = agg;
                                }

                                if (column["aggregates"] && !column["aggregatesrenderer"]) {
                                    column["aggregatesrenderer"] = this.__columnAggRenderer(gridInfo.id, configuredAgg);
                                }

                                if (column["aggregates"]) {
                                    gridInfo["compiledConfig"]["gridSetting"]["showaggregates"] = true;
                                    gridInfo["compiledConfig"]["gridSetting"]["showstatusbar"] = true;
                                }
                            },

                            __calculateSum: function(aggVal, val) {
                                var val = parseFloat(val);
                                if (isNaN(val) || val === 0) {
                                    return aggVal;
                                }

                                var valDecimalLen = Math.floor(val) === val ? 0 : val.toString().split(".")[1].length;
                                var aggValDecimalLen = Math.floor(aggVal) === aggVal ? 0 : aggVal.toString().split(".")[1].length;

                                var times = Math.pow(10, Math.max(valDecimalLen, aggValDecimalLen));
                                return (val * times + aggVal * times) / times;
                            },

                            __columnAggRenderer: function(gridId, configuredAgg) {
                                var that = this;

                                return function (aggData, column) {
                                    if (!configuredAgg) {
                                        return;
                                    }
                                    var configAgg = configuredAgg.split(",");

                                    var sumContent = that.__getAggLabel("SUM", configAgg, "core.label.column.agg.total") + that.__getAggValue("SUM", configAgg, aggData, column, gridId);
                                    var cntContent = that.__getAggLabel("COUNT", configAgg, "core.label.column.agg.count") + that.__getAggValue("COUNT", configAgg, aggData);

                                    var aggContent = sumContent + (sumContent && cntContent ? "<br>" : "") + cntContent;
                                    return dti.oasis.grid.setColumnAggStyle(aggContent);
                                }
                            },

                            __getAggLabel: function(agg, configAgg, labelMsgKey) {
                                if (configAgg.indexOf(agg + "_LABEL") == -1) {
                                    return "";
                                }

                                //Allow user to show aggregate label with or without aggregate data under a column.
                                var aggLabel = getMessage(labelMsgKey);

                                if (configAgg.toString().indexOf(agg)> -1) {
                                    aggLabel += ": ";
                                }

                                return aggLabel;
                            },

                            __getAggValue: function(agg, configAgg, aggData, column, gridId) {
                                if (configAgg.indexOf(agg) == -1) {
                                    return "";
                                }

                                var originalAggValue = aggData[agg.toLowerCase()];
                                var aggValue = (originalAggValue || 0).toString();
                                if (column) {
                                    //If exists cellsformat and originalAggValue, then the aggregate data should be formatted already.
                                    if (column.cellsformat && originalAggValue != undefined) {
                                        if (column.cellsformat.charAt(0).toUpperCase() == "P") {
                                            //Remove comma if exists for column of percentage type
                                            if (aggValue.indexOf(",") > -1) {
                                                aggValue = aggValue.replace(/,/g, "");
                                            }
                                        }
                                    } else {
                                        var cellsFormatter = dti.oasis.grid.cellFormat.getCellFormatterForCellsRenderer(gridId, column.datafield, true);
                                        if (cellsFormatter) {
                                            aggValue = cellsFormatter.format(null, null, null, aggValue);
                                        }
                                    }
                                }
                                return aggValue;
                            },

                            __getCompiledColumnsWidth: function(gridInfo) {
                                var columnsConfig = dti.oasis.grid.config.getOasisColumnsConfig(gridInfo.id);

                                var rawData = gridInfo.data.rawData;
                                var LOV_LABEL = "LOVLABEL";
                                var compiledColumnWidth = 0;
                                var widthSettings = [];
                                var selectIndColName = dti.oasis.grid.getSelectIndColumnName(gridInfo.id);

                                //get the last visible column so that only the minimum width can be set.
                                for (var i = 0; i < columnsConfig.length; i++) {
                                    if (columnsConfig[i]["visible"]) {
                                        widthSettings["lastColumn"] = columnsConfig[i]["id"];
                                    }
                                }

                                for (var i = 0; i < columnsConfig.length; i++) {
                                    if (columnsConfig[i]["visible"]) {
                                        var columnHeader = columnsConfig[i]["id"];
                                        var widthValues = [];
                                        var columnHeaderLength = 100;
                                        var columnsWidth = 0;
                                        var formatFunc = null;

                                        var headerText = columnsConfig[i]["label"];
                                        columnHeaderLength = dti.oasis.grid.calculateColumnWidth(headerText);

                                        //get the formatted function for the field.
                                        if (!columnsConfig[i]["masked"]) {
                                            if (columnsConfig[i]["dataType"] == dti.oasis.grid.config.dataType.CURRENCY) {
                                                formatFunc = function (val) {
                                                    return dti.oasis.dataFormat.formatMoney(val);
                                                }
                                            }
                                            else if (columnsConfig[i]["dataType"] == dti.oasis.grid.config.dataType.PHONE_NUMBER) {
                                                formatFunc = function (val) {
                                                    return dti.oasis.dataFormat.formatPhoneNumber(val);
                                                }
                                            }
                                            else if (columnsConfig[i]["dataType"] == dti.oasis.grid.config.dataType.PERCENTAGE) {
                                                formatFunc = function (val) {
                                                    return dti.oasis.dataFormat.formatPercentage(val);
                                                }
                                            }
                                            else if (columnsConfig[i]["dataType"] == dti.oasis.grid.config.dataType.DATE) {
                                                formatFunc = function (val) {
                                                    return dti.oasis.dataFormat.formatDate(val);
                                                }
                                            }
                                        }

                                        //get the maximun content length
                                        if (columnHeader === selectIndColName) {
                                            columnsWidth = columnHeaderLength;
                                        }
                                        else {
                                            for (var x = 0; x < rawData.length; x++) {
                                                var updateInd = rawData[x]["UPDATE_IND"];
                                                if (updateInd.indexOf("D") == -1) {
                                                    var val = rawData[x][columnHeader];
                                                    if (columnsConfig[i]["displayType"] == "dropdownlist" && (typeof rawData[x][columnHeader + LOV_LABEL] != "undefined")) {
                                                        val = rawData[x][columnHeader + LOV_LABEL];
                                                    }

                                                    //call the format function to get the exact length of the field value.
                                                    if (formatFunc) {
                                                        val = formatFunc(val);
                                                    }

                                                    var cellWidthLen = dti.oasis.grid.calculateColumnWidth(val);

                                                    if (cellWidthLen > columnHeaderLength && cellWidthLen > columnsWidth) {
                                                        columnsWidth = cellWidthLen;
                                                    } else {
                                                        if (columnsWidth == 0) {
                                                            columnsWidth = columnHeaderLength;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (columnHeaderLength < columnsWidth) {
                                            compiledColumnWidth = columnsWidth;
                                        } else {
                                            compiledColumnWidth = columnHeaderLength;
                                        }

                                        //in order for jqxGrid adjust the last column, only the minwidth must be set and
                                        //the width must not be included when configuring the column.
                                        widthValues["minwidth"] = (columnsConfig[i]["minWidth"]) ? columnsConfig[i]["minWidth"] : compiledColumnWidth;

                                        if (columnHeader != widthSettings["lastColumn"]) {
                                            if (columnsConfig[i]["width"]) {
                                                widthValues["width"] = columnsConfig[i]["width"];
                                            }
                                        } else {
                                            //for the last column, ignore the width setting, otherwise
                                            //the grid will not expand to fit the grid.
                                            if (columnsConfig[i]["width"]) {
                                                widthValues["minwidth"] = columnsConfig[i]["width"];
                                            }
                                        }

                                        widthSettings[columnHeader] = widthValues;
                                    }
                                }

                                return widthSettings;
                            },

                            __compileDataFields: function(gridInfo) {
                                var dataFields = [];
                                var dataFieldNames = {};

                                var columnsConfig = dti.oasis.grid.config.getOasisColumnsConfig(gridInfo.id);
                                for (var i = 0; i < columnsConfig.length; i++) {
                                    var columnConfig = columnsConfig[i];

                                    if(columnConfig.visible) {
                                        var dataField = {};
                                        var fieldName = columnConfig.id.toUpperCase();
                                        dataFieldNames[fieldName] = 1;

                                        dataField["name"] = columnConfig.id;
                                        dataField["type"] = this.__getDataType(columnConfig);
                                        dataField["format"] = this.__getDataFormat(columnConfig);

                                        dataFields[dataFields.length] = dataField;

                                        // Add the LOVLABEL column if exists.
                                        if (this.__hasLovLabelColumn(gridInfo, columnsConfig[i]["id"])) {
                                            var lovLabelField = {
                                                "name": dataField["name"].concat("LOVLABEL"),
                                                "type": "string"
                                            };

                                            dataFields[dataFields.length] = lovLabelField;
                                            dataFieldNames[fieldName.concat("LOVLABEL")] = 1;
                                        }
                                        else if (this.__hasDisplayOnlyColumn(gridInfo, columnsConfig[i]["id"])) {
                                            var displayOnlyField = {
                                                "name": dataField["name"].concat("_DISP_ONLY"),
                                                "type": "string"
                                            };

                                            dataFields[dataFields.length] = displayOnlyField;
                                            dataFieldNames[fieldName.concat("_DISP_ONLY")] = 1;
                                        }
                                    }
                                }


                                // Add @id
                                if (dti.oasis.grid.config._protected._hasColumn(gridInfo, "@id")) {
                                    dataFields[dataFields.length] = {"name": "@id", "type": "string"};
                                    dataFieldNames["@ID"] = 1;
                                    dataFieldNames["@id"] = 1;
                                }

                                // Add @index
                                if (dti.oasis.grid.config._protected._hasColumn(gridInfo, "@index")) {
                                    dataFields[dataFields.length] = {"name": "@index", "type": "number"};
                                    dataFieldNames["@INDEX"] = 1;
                                    dataFieldNames["@index"] = 1;
                                }

                                gridInfo["compiledConfig"]["dataFields"] = dataFields;
                                gridInfo["compiledConfig"]["dataFieldNames"] = dataFieldNames;
                            },

                            __getCellsRenderer: function(gridId, dataField, columnConfig, masked) {
                                var currentGridId = gridId;
                                if (masked) {
                                    // The column is protected.
                                    return function (jqxRowIndex, dataField, value, defaultHtml, columnSetting, rowData) {
                                        // Using config?
                                        return "<div class=\"dti-jqx-gird-cell\">" + dti.oasis.grid.DEFAULT_FORMATTED_MASKED_VALUE + "</div>";
                                    };
                                } else {
                                    var columnName = dti.oasis.grid.getActualColumnName(gridId, dataField);

                                    var cellsRenderer = dti.oasis.grid.getCustomColumnOption(gridId, columnName, "cellsRenderer");
                                    if (cellsRenderer != null) {
                                        return cellsRenderer;
                                    }

                                    var hasHref = columnConfig.hasOwnProperty("href") && !dti.oasis.string.isEmpty(columnConfig["href"]);
                                    var cellsFormatter = dti.oasis.grid.cellFormat.getCellFormatterForCellsRenderer(gridId, columnName);
                                    var fnGetCellStyle = dti.oasis.grid.getCustomColumnOption(gridId, columnName, "getCellStyle");
                                    var fnGetCellClasses = dti.oasis.grid.getCustomColumnOption(gridId, columnName, "getCellClasses");
                                    var columnEditable = columnConfig['editable'] && columnConfig["displayType"] != "checkbox";

                                    if (hasHref || cellsFormatter != null || fnGetCellStyle != null || fnGetCellClasses != null || columnEditable) {
                                        return function (jqxRowIndex, dataField, value, defaultHtml, columnSetting, rowData) {
                                            var rowIndex = dti.oasis.grid.getRowIndexByJqxRowIndex(gridId, jqxRowIndex);
                                            var cellValue = dti.oasis.grid.getCellValueByRowIndex(gridId, rowIndex, columnName);

                                            var editableCellClass = "";
                                            if (!hasHref && columnEditable) {
                                                var cellEditableFunction = dti.oasis.grid.getCellEditableFunction(gridId, columnName);

                                                if (!cellEditableFunction || cellEditableFunction(jqxRowIndex, columnName)) {
                                                    editableCellClass = "dti-jqx-grid-cell-editable";
                                                }
                                            }
                                            // Get cell styles
                                            var cellStyles = "";
                                            var hyperlinkColorStyle = "";

                                            if (fnGetCellStyle != null) {
                                                cellStyles = fnGetCellStyle(jqxRowIndex, cellValue);
                                            }

                                            var cellColorRowStyle = dti.oasis.grid.getCellRowStyle(currentGridId, dataField, jqxRowIndex);

                                            var formatFieldColor = dti.oasis.grid.getAllNumbersColorInGrid(currentGridId, dataField, value);

                                            if (formatFieldColor != "") {
                                                cellColorRowStyle = formatFieldColor;
                                            }

                                            if (cellColorRowStyle != "") {
                                                cellStyles = " color:" + cellColorRowStyle;
                                            }

                                            // Get call content.
                                            var cellContent = "";

                                            if (cellsFormatter != null) {
                                                cellContent = cellsFormatter.format(gridId, jqxRowIndex, columnName, cellValue)
                                            } else if (columnSetting["columntype"] && columnSetting["columntype"] == "dropdownlist") {
                                                cellContent = value;
                                            } else {
                                                cellContent = cellValue;
                                            }

                                            cellContent = dti.oasis.string.escapeHtml(cellContent);

                                            var linkEnabled = hasHref;
                                            if (linkEnabled) {
                                                var fnIsCellLinkEnabled = dti.oasis.grid.getCustomColumnOption(gridId, columnName, "isCellLinkEnabled");
                                                if (fnIsCellLinkEnabled != null) {
                                                    linkEnabled = fnIsCellLinkEnabled(jqxRowIndex, cellValue);
                                                }
                                            }

                                            if (!dti.oasis.string.isEmpty(cellStyles)) {
                                                if (linkEnabled && cellStyles.indexOf("color:") > -1) {
                                                    hyperlinkColorStyle = " style=\"".concat(cellStyles, "\"");
                                                }
                                                cellStyles = " style=\"".concat(cellStyles, "\"");
                                            }

                                            if (linkEnabled) {
                                                // Add href
                                                cellContent = "".concat(
                                                    "<a id=\"", gridId, "_", columnName, "_", jqxRowIndex, "_HREF\"",
                                                    hyperlinkColorStyle,
                                                    " onmousedown=\"dti.oasis.grid.onCellLinkClick(event, '" + gridId + "', " + jqxRowIndex + ", '" + columnSetting["datafield"] + "')\"",
                                                    " href=\"javascript:void(0);\">",
                                                    cellContent,
                                                    "</a>"
                                                );
                                            }

                                            // Get cell classes
                                            var cellClasses = [];

                                            switch (columnSetting["cellsalign"]) {
                                                case "right":
                                                    cellClasses[cellClasses.length] = "jqx-grid-cell-right-align";
                                                    break;
                                                case "middle":
                                                case "center":
                                                    cellClasses[cellClasses.length] = "jqx-grid-cell-middle-align";
                                                    break;
                                                default:
                                                    cellClasses[cellClasses.length] = "jqx-grid-cell-left-align";
                                            }

                                            if (linkEnabled) {
                                                cellClasses[cellClasses.length] = "dti-jqx-gird-href-cell";
                                            }

                                            if (fnGetCellClasses != null) {
                                                cellClasses = cellClasses.concat(fnGetCellClasses(jqxRowIndex, cellValue));
                                            }

                                            if (editableCellClass) {
                                                cellClasses[cellClasses.length] = editableCellClass;
                                            }

                                            var cellClassesStr = (cellClasses.length > 0) ? (" class=\"".concat(cellClasses.join(" "), "\"")) : "";

                                            return "<div".concat(
                                                cellClassesStr,
                                                cellStyles,
                                                ">",
                                                cellContent,
                                                "</div>"
                                            );
                                        };
                                    } else {
                                        if (columnConfig["displayType"] != "checkbox") {
                                            return function (jqxRowIndex, dataField, value, defaultHtml, columnSetting, rowData) {
                                                var div = defaultHtml;
                                                var cellStyle = dti.oasis.grid.getCellRowStyle(currentGridId, dataField, jqxRowIndex);
                                                if (cellStyle != "") {
                                                    var index = defaultHtml.indexOf("style=");
                                                    if (index > 0) {
                                                        var endStr = defaultHtml.substring(index + 7);
                                                        div = defaultHtml.substring(0, index + 6) + "\" color:" + cellStyle + ";" + endStr;
                                                    }
                                                    return div;
                                                }
                                            }
                                        }
                                    }
                                }

                                return null;
                            },

                            __getColumnDisplayType: function(columnConfig) {
                                var columnDisplayType = null;
                                var dataType = columnConfig["dataType"] || "";
                                var displayType = columnConfig["displayType"] || "";
                                var editable = columnConfig["editable"];

                                switch (displayType) {
                                    case "checkbox":
                                        columnDisplayType = displayType;
                                        break;
                                    case "datetimeinput":
                                    case "dropdownlist":
                                    case "combobox":
                                    case "text":
                                    case "textarea":
                                        columnDisplayType = displayType;
                                        break;
                                    case "numberinput":
                                        //columnDisplayType = displayType;
                                        columnDisplayType = null;
                                        break;
                                }

                                return columnDisplayType;
                            },

                            __getDataFormat: function(columnConfig) {
                                var oasisDataType = columnConfig["dataType"] || "ST";
                                var dataFormat = columnConfig["format"] || "";

                                if (dti.oasis.string.isEmpty(dataFormat)) {
                                    switch (oasisDataType) {
                                        case "DT":
                                            dataFormat = "MM/dd/yyyy";
                                            break;
                                        case "TM":
                                            dataFormat = "MM/dd/yyyy HH:mm:ss";
                                            break;
                                    }
                                }

                                return dataFormat;
                            },

                            __getDataType: function(columnConfig) {
                                var dataType = "string";
                                var oasisDataType = columnConfig["dataType"] || "ST";

                                switch (oasisDataType) {
                                    case "CU": // Currency
                                    case "CF": // Currency formatted
                                    case "NM": // Number
                                    case "PT": // Percentage
                                        dataType = "number";
                                        break;
                                    case "ST": // String
                                    case "PH": // Phone number
                                    case "UT": // Uppercase string
                                    case "LT": // Lowercase string
                                        dataType = "string";
                                        break;
                                    case "DT": // Date
                                        dataType = "date";
                                        break;
                                    case "TM": // Date time
                                        dataType = "datetime";
                                        break;
                                }

                                return dataType;
                            },

                            __hasDisplayOnlyColumn: function(gridInfo, dataFieldId) {
                                return dti.oasis.grid.config._protected._hasColumn(gridInfo, dataFieldId.concat("_DISP_ONLY"));
                            },

                            __hasLovLabelColumn: function(gridInfo, dataFieldId) {
                                return dti.oasis.grid.config._protected._hasColumn(gridInfo, dataFieldId.concat("LOVLABEL"));
                            },

                            __initTableProperty: function(gridId) {
                                var tableProperties = dti.oasis.grid._protected._createGridPropertyObj(gridId);

                                // TODO Add it to page scope.

                                // Replace the existing table property if found.
                                for (var i = 0; i < tblCount; i++) {
                                    if (tblPropArray[i].id == gridId) {
                                        tblPropArray[i] = tableProperties;
                                        return;
                                    }
                                }

                                // Add a new table property if not found.
                                tblPropArray[tblCount++] = tableProperties;
                            },

                            __compileConfig: function(gridId) {
                                var gridInfo = dti.oasis.page.getGridInfo(gridId);

                                // Initial the properties of the grid.
                                this.__initTableProperty(gridId);

                                // Init the compiled grid config.
                                gridInfo["compiledConfig"] = {};

                                this.__compileGridSetting(gridInfo);

                                this.__compileDataFields(gridInfo);

                                this.__compileColumns(gridInfo);
                            }

                        };
                    })(),

                    dataType: {
                        CURRENCY: "CU",
                        CURRENCY_FORMATTED: "CF",
                        DATE: "DT",
                        DATE_TIME: "TM",
                        NUMBER: "NM",
                        PERCENTAGE: "PT",
                        PHONE_NUMBER: "PH"
                    },

                    hasColumn: function (gridId, columnName) {
                        return this._protected._hasColumn(dti.oasis.page.getGridInfo(gridId), columnName);
                    },

                    hasDisplayOnlyColumn: function (gridId, columnName) {
                        return this.hasColumn(gridId, columnName.concat("_DISP_ONLY"));
                    },

                    getJqxColumnsConfig: function (gridId) {
                        return this._protected._getJqxColumnsConfig(dti.oasis.page.getGridInfo(gridId));
                    },

                    getJqxColumnConfig: function (gridId, columnName) {
                        return this._protected._getJqxColumnConfig(dti.oasis.page.getGridInfo(gridId), columnName);
                    },

                    getOasisColumnsConfig: function (gridId) {
                        return this._protected._getOasisColumnsConfig(dti.oasis.page.getGridInfo(gridId));
                    },

                    getOasisColumnConfig: function (gridId, columnName) {
                        return this._protected._getOasisColumnConfig(dti.oasis.page.getGridInfo(gridId), columnName);
                    },

                    getOasisColumnDataType: function (gridId, columnName) {
                        return this._protected._getOasisColumnDataType(dti.oasis.page.getGridInfo(gridId), columnName);
                    },

                    getDataFields: function (gridId) {
                        return dti.oasis.page.getGridInfo(gridId)["compiledConfig"]["dataFields"];
                    },

                    compileConfig: function(gridId) {
                        this._protected.__compileConfig(gridId);
                    },

                    recompileColumns: function(gridId) {
                        var grid = $("#".concat(gridId));
                        var gridInfo = dti.oasis.page.getGridInfo(gridId);

                        this._protected.__compileColumns(gridInfo);

                        grid.jqxGrid('columns', gridInfo.compiledConfig.columns)
                    }
                }
            })(),

            /**
             * The values of params:
             * gridInfo: Grid Info.
             *
             * @param params
             */
            initAndLoadGrid: function (params) {
                var gridInfo = params["gridInfo"];
                var gridId = gridInfo.id;

                // Set grid info to page scope.
                dti.oasis.page.setGridInfo(gridId, gridInfo);

                //Call page level Grid Initialization, if defined
                if (window["handleOnGridInitialization"]) {
                    handleOnGridInitialization(gridId);
                }

                /* Setup default values for grid properties */
                // by default auto-select the 1s row in the grid.
                dti.oasis.grid.setProperty(gridId, "autoSelectFirstRow", dti.oasis.grid.getProperty(gridId, "autoSelectFirstRow", true));

                this.init(gridId);

                this._protected._generateGridFunctions(gridId);

                this.load({"gridId": gridId});
            },

            /**
             * @param gridId
             */
            init: function (gridId) {
                // Compile config.
                dti.oasis.grid.config.compileConfig(gridId);
            },

            load: function (params) {
                var gridId = params["gridId"];
                var hasBeenInitialized = dti.oasis.grid.getProperty(gridId, "hasBeenInitialized");

                // In case of loading on demand based on a row selected in another grid,
                // need to keep track of the correct load order in case the user is using the arrow keys to quickly iterate over the grid rows
                if (!params.retry) {
                    var loadParamsQueue = dti.oasis.grid.getProperty(gridId, "loadParamsQueue", []);
                    // Add the params to the tail of the queue
                    loadParamsQueue.push(params);
                    dti.oasis.grid.setProperty(gridId, "loadParamsQueue", loadParamsQueue);
                }

                if (!hasBeenInitialized) {
                    if (dti.oasis.grid.getProperty(gridId, "loadingDeferredObj").state() !== "pending") {
                        dti.oasis.grid.setProperty(gridId, "loadingDeferredObj", $.Deferred());
                    }
                    __load(gridId);
                }
                else {
                    $.when(dti.oasis.grid.getLoadingPromise(gridId)).then(function () {
                        if (dti.oasis.grid.getProperty(gridId, "loadingDeferredObj").state() === "pending") {
                            // Another request is loading; retry to wait until loading is complete
                            dti.oasis.grid.load({gridId: gridId, retry: true});
                        }
                        else {
                            dti.oasis.grid.setProperty(gridId, "loadingDeferredObj", $.Deferred());
                            __load(gridId);
                        }
                    });
                }

                function __load(gridId) {
                    var loadParamsQueue = dti.oasis.grid.getProperty(gridId, "loadParamsQueue");
                    // pull the next params from the head of the queue
                    var params = loadParamsQueue.shift();
                    var gridId = params["gridId"];
                    var gridInfo = dti.oasis.page.getGridInfo(gridId);
                    var grid = $("#".concat(gridId));
                    var hasBeenInitialized = dti.oasis.grid.getProperty(gridId, "hasBeenInitialized");
                    var source = grid.jqxGrid("source");
                    var hasBoundedData = (hasBeenInitialized && source && gridInfo.data.rawData &&
                        Array.isArray(gridInfo.data.rawData) && gridInfo.data.rawData.length > 0) ? true : false;

                    __preRenderGrid(grid, gridInfo, gridId, params, hasBeenInitialized);

                    if (hasBeenInitialized) {
                        __reRenderGrid(grid, gridInfo, gridId, params);
                    }
                    else {
                        __renderGrid(grid, gridInfo, gridId);
                    }

                    __postRenderGrid(grid, gridInfo, gridId, hasBeenInitialized);
                }

                function __attachGridEvent(grid, gridInfo) {
                    grid.off("bindingcomplete.dti.oasis.grid").on("bindingcomplete.dti.oasis.grid", function (event) {
                        dti.oasis.grid.onGridDataBindingComplete(gridInfo);
                    });

                    grid.off("cellvaluechanged.dti.oasis.grid").on("cellvaluechanged.dti.oasis.grid", function (event) {
                        var args = event.args;
                        var columnName = event.args.datafield;
                        var jqxRowIndex = args.rowindex;
                        var rowIndex = dti.oasis.grid.getRowIndexByJqxRowIndex(gridId, jqxRowIndex);

                        //If it's dropdown cell, the values in args will be objects and we need to get real values from property.
                        var value = dti.oasis.dataFormat.formatDropdownCellValue(args.newvalue);
                        var oldValue = dti.oasis.dataFormat.formatDropdownCellValue(args.oldvalue);

                        if (dti.oasis.date.isDate(value)) {
                            value = dti.oasis.date.formatDate(value);
                        }

                        if (dti.oasis.date.isDate(oldValue)) {
                            oldValue = dti.oasis.date.formatDate(oldValue);
                        }

                        dti.oasis.grid.onCellValueChanged(gridId, rowIndex, columnName, value, oldValue);
                    });

                    grid.off("sort.dti.oasis.grid").on("sort.dti.oasis.grid", function (event) {
                        dti.oasis.grid.onGridSort(gridInfo);
                    });

                    grid.off("pagechanged.dti.oasis.grid").on("pagechanged.dti.oasis.grid", function (event) {
                        dti.oasis.grid.onGridPageChanged(gridInfo);
                    });

                    grid.off("pagesizechanged.dti.oasis.grid").on("pagesizechanged.dti.oasis.grid", function (event) {
                        dti.oasis.grid.onGridPageSizeChanged(gridInfo);
                    });

                    grid.off("rowselect.dti.oasis.grid").on("rowselect.dti.oasis.grid", function (event) {
                        var args = event.args;
                        var jqxRowIndex = args.rowindex;
                        var rowIndex = dti.oasis.grid.getRowIndexByJqxRowIndex(gridId, jqxRowIndex);
                        var rowId = dti.oasis.grid.getRowIdByRowIndex(gridId, rowIndex);

                        dti.oasis.grid.onRowSelected(gridId, rowIndex, rowId);
                    });

                    grid.off("cellclick.dti.oasis.grid").on("cellclick.dti.oasis.grid", function (event) {
                        var args = event.args;
                        var jqxRowIndex = args.rowindex;
                        var jqxRowId = grid.jqxGrid("getrowid", jqxRowIndex);
                        var rowIndex = dti.oasis.grid.getRowIndexByJqxRowId(gridId, jqxRowId);
                        var columnName = args.datafield;

                        dti.oasis.grid.updateEditableCell(grid, gridId, columnName);

                        dti.oasis.grid.onCellClick(gridId, rowIndex, columnName);
                    });

                    grid.off("keydown.dti.oasis.grid").on("keydown.dti.oasis.grid", function (event) {
                        if (event.keyCode == "38") {
                            dti.oasis.grid.updateEditableCell(grid, gridId,"");
                            dti.oasis.grid._protected._selectPreviousRowByUpKey(gridId);
                            event.preventDefault();
                        } else if (event.keyCode == "40") {
                            dti.oasis.grid.updateEditableCell(grid, gridId,"");
                            dti.oasis.grid._protected._selectNextRowByDownKey(gridId);
                            event.preventDefault();
                        }
                    });

                    grid.off("filter.dti.oasis.grid").on("filter.dti.oasis.grid", function (event) {
                        dti.oasis.grid.onGridFilter(gridInfo);
                    });

                    grid.off("columnclick.dti.oasis.grid").on("columnclick.dti.oasis.grid", function (event) {
                        var columnName = event.args.datafield;

                        dti.oasis.grid.onColumnClick(gridId, columnName);
                    });

                    grid.off("cellbeginedit.dti.oasis.grid").on("cellbeginedit.dti.oasis.grid", function (event) {
                        // Selecting the row.
                        var jqxRowIndex = event.args.rowindex;
                        var rowIndex = dti.oasis.grid.getRowIndexByJqxRowIndex(gridId, jqxRowIndex);

                        dti.oasis.grid.setProperty(gridId, "inEditorCellMode", true);
                        dti.oasis.grid.setProperty(gridId, "editorCellColumnName", event.args.datafield);

                        dti.oasis.grid.onCellBeginEdit(gridId, rowIndex);
                    });

                    grid.off("cellendedit.dti.oasis.grid").on("cellendedit.dti.oasis.grid", function (event) {
                        dti.oasis.grid.setProperty(gridId, "inEditorCellMode", false);
                        dti.oasis.grid.setProperty(gridId, "editorCellColumnName", "");

                        // Column Name
                        var columnName = event.args.datafield;

                        // Row Id
                        var jqxRowIndex = event.args.rowindex;
                        var rowIndex = dti.oasis.grid.getRowIndexByJqxRowIndex(gridId, jqxRowIndex);

                        // Value
                        var value = event.args.value;
                        var oldValue = event.args.oldvalue;

                        var columnDisplayType = dti.oasis.grid.getColumnDisplayType(gridId, columnName);
                        if (columnDisplayType === "checkbox") {
                            var rowId = dti.oasis.grid.getRowIdByJqxRowIndex(gridId, jqxRowIndex);
                            var currentRowId = window[gridId + "1"].recordset("ID").value;
                            if (rowId !== currentRowId) {
                                dti.oasis.grid.moveToRow(gridId, rowId);
                            }

                            dti.oasis.grid.onPostCellEndEdit(gridId, rowIndex, columnName, value, oldValue);
                        } else {
                            dti.oasis.grid.onCellEndEdit(gridId, rowIndex, columnName, value, oldValue);
                        }
                    });

                    grid.off("postcellendedit.dti.oasis.grid").on("postcellendedit.dti.oasis.grid", function (event) {
                        // Column Name
                        var columnName = event.args.datafield;

                        // Row Id
                        var jqxRowIndex = event.args.rowindex;
                        var rowIndex = dti.oasis.grid.getRowIndexByJqxRowIndex(gridId, jqxRowIndex);
                        var rowId = dti.oasis.grid.getRowIdByJqxRowIndex(gridId, jqxRowIndex);
                        var currentRowId = window[gridId + "1"].recordset("ID").value;

                        // Value
                        var value = event.args.value;
                        var oldValue = event.args.oldvalue;

                        if (rowId !== currentRowId) {
                            dti.oasis.grid.moveToRow(gridId, rowId);
                        }

                        try {
                            dti.oasis.grid.onPostCellEndEdit(gridId, rowIndex, columnName, value, oldValue);
                        } finally {
                            if (rowId !== currentRowId) {
                                selectRowById(gridId, rowId);
                            }
                        }
                    });

                    var customGridOptions = dti.oasis.grid.getCustomGridOptions(gridId);
                    if (customGridOptions != null) {
                        var fnCellValueChanged = customGridOptions["cellValueChanged"] || null;

                        if (fnCellValueChanged != null) {
                            grid.off("cellvaluechanged.dti.oasis.customGridOption").on("cellvaluechanged.dti.oasis.customGridOption", fnCellValueChanged);
                        }
                    }

                    //grid.off("beforesort_dti.dti.oasis.grid").on("beforesort_dti.dti.oasis.grid", function (event) {
                    //    // event arguments.
                    //    var args = event.args;
                    //    // sorting information.
                    //    var sortInfo = event.args.sortinformation;
                    //    // sort direction.
                    //    var sortdirection = sortInfo.sortdirection.ascending ? "ascending" : "descending";
                    //    // column data field.
                    //    var sortColumnDataField = sortInfo.sortcolumn;
                    //
                    //    baseOnBeforeSort(window[gridId], window[gridId.concat("1")], sortColumnDataField, null);
                    //});
                    //
                    //grid.off("aftersort_dti.dti.oasis.grid").on("aftersort_dti.dti.oasis.grid", function (event) {
                    //    // event arguments.
                    //    var args = event.args;
                    //    // sorting information.
                    //    var sortInfo = event.args.sortinformation;
                    //    // sort direction.
                    //    var sortdirection = sortInfo.sortdirection.ascending ? "ascending" : "descending";
                    //    // column data field.
                    //    var sortColumnDataField = sortInfo.sortcolumn;
                    //
                    //    baseOnAfterSort(window[gridId], window[gridId.concat("1")], sortColumnDataField, null);
                    //});
                }

                function __preRenderGrid(grid, gridInfo, gridId, params, hasBeenInitialized) {

                    // Clear the selectedTableRowNo property.
                    dti.oasis.grid.setProperty(gridId, "selectedTableRowNo", null);

                    var loadingFromRemoteSource = params.url ||
                    dti.oasis.grid.getProperty(gridId, "cacheKey") ||
                    dti.oasis.grid.getProperty(gridId, "deferredLoadDataProcess") ? true : false;
                    dti.oasis.grid.setProperty(gridId, "loadingFromRemoteSource", loadingFromRemoteSource);
                    dti.oasis.grid.setProperty(gridInfo.id, "isGridDataLoaded", false);

                    // Add postData to gridInfo
                    if (typeof params.postData === "undefined") {
                        params.postData = {};
                    }
                    gridInfo.postData = params.postData;

                    // Get the total record count when the grid is loaded and in virturalmode
                    if (dti.oasis.grid.isVirtualMode(gridId)) {
                        gridInfo.postData._getTotalRecordCount = true;
                    }

                    // Update the gridInfo rawData with the provided rawData
                    if (params.rawData) {
                        gridInfo.data.rawData = params.rawData;
                    }

                    // Create data island.
                    if (!hasBeenInitialized) {

                        $(gridInfo).off("complete.dti.oasis.grid").on("complete.dti.oasis.grid", function (event) {
                            dti.oasis.grid.onGridComplete(this);
                        });

                        dti.oasis.grid._protected._generateDataIsland(gridInfo);
                    }
                }

                function __renderGrid(grid, gridInfo, gridId) {
                    // Get setting.
                    var setting = $.extend({}, gridInfo["compiledConfig"]["gridSetting"]);

                    // Update the pageable setting if it is in virtual mode
                    if (dti.oasis.grid.isVirtualMode(gridId)) {
                        setting.virtualmode = true;
                        setting.rendergridrows = function (obj) {
                            return obj.data;
                        };
                        setting.pageable = dti.oasis.grid.getProperty(gridId, "virtualScrolling") ? false : true;
                    }

                    dti.oasis.grid.setProperty(gridId, "pageable", setting.pageable)
                    if (!dti.oasis.grid.isVirtualMode(gridId)) {
                        // Disable paging until the grid is rendered so it doesn't show the partial paging bar while loading a cached or deferred loading grid
                        setting["pageable"] = false;
                    }

                    setting["rendered"] = function (type) {
                        dti.oasis.grid.onGridRendered(grid, gridInfo, type);
                    };
                    setting["source"] = dti.oasis.grid._protected._generateJqxGridSource(gridInfo, params);
                    setting["columns"] = gridInfo["compiledConfig"]["columns"];

                    // Load grid.
                    grid.jqxGrid(setting);
                }

                function __reRenderGrid(grid, gridInfo, gridId, params) {
                    if (dti.oasis.grid.getProperty(gridId, "filtering")) {
                        // Apply the current filter
                        var source = grid.jqxGrid("source");
                        source._source.localdata = dti.oasis.grid._protected._applyCurrentFilterToRawData(gridInfo, gridId);

                        // *** Patch for jqxGrid bug***
                        // The grid is using source.sortcolumn and source.sortdirection to sort the data, but is not updating those values after a user sorts/filters the grid.
                        // The fix is to update the sortcolumn and sortdirection in source to the latest values from the instance data attached to the grid element.
                        // TODO: This is still not fixing the problem if sort the grid through column header, filter a column through column header, and then filter using dti.oasis.grid.filterByString(); it loses the user's sort/filter settings. Requires more investigation
                        var gridData = grid.data('jqxGrid');
                        if (gridData) {
                            gridData.instance.source.sortcolumn = gridData.instance.sortcolumn;
                            if (gridData.instance.sortdirection) {
                                gridData.instance.source.sortdirection = gridData.instance.sortdirection.ascending ? 'asc' : 'desc';
                            }
                        }

                        // Update the data without reloading the data source
                        grid.jqxGrid('updatebounddata', 'filter');
                    }
                    else {
                        // Recreate the data source, assuming the url and/or post parameters have changed.
                        // Updating the "source" causes the grid to load the data from the new data source.
                        grid.jqxGrid("source", dti.oasis.grid._protected._generateJqxGridSource(gridInfo, params));
                    }
                }

                function __postRenderGrid(grid, gridInfo, gridId, hasBeenInitialized) {

                    if (!hasBeenInitialized) {
                        __attachGridEvent(grid, gridInfo);
                    }

                    // Reset the request for the total record count property if in virtualmode
                    if (dti.oasis.grid.isVirtualMode(gridId)) {
                        gridInfo.postData._getTotalRecordCount = false;
                    }
                }

            },

            isVirtualMode: function(gridId) {
                return (dti.oasis.grid.getProperty(gridId, "virtualPaging") || dti.oasis.grid.getProperty(gridId, "virtualScrolling"));
            },

            whenAllGridsLoadingComplete: function() {
                return $.when.apply($, dti.oasis.grid.getAllLoadingPromises())
            },

            whenAllVisibleGridsLoadingComplete: function() {
                return $.when.apply($, dti.oasis.grid.getAllLoadingPromisesForVisibleGrids())
            },

            getAllLoadingPromises: function() {
                var gridIds = dti.oasis.page.getProperty("gridIds");
                var promises = [];
                if (gridIds) {
                    for (var i = 0; i < gridIds.length; i++) {
                        promises.push(this.getLoadingPromise(gridIds[i]));
                    }
                }
                return promises;
            },

            getAllLoadingPromisesForVisibleGrids: function() {
                var visibleGridIds = dti.oasis.grid.getGridIdsForVisibleGrids();
                var promises = [];
                if (visibleGridIds) {
                    for (var i = 0; i < visibleGridIds.length; i++) {
                        promises.push(this.getLoadingPromise(visibleGridIds[i]));
                    }
                }
                return promises;
            },

            getLoadingPromise: function(gridId) {
                return dti.oasis.grid.getProperty(gridId, "loadingDeferredObj").promise();
            },

            getSortingPromise: function(gridId) {
                return dti.oasis.grid.getProperty(gridId, "sortingDeferredObj").promise();
            },

            getFilteringPromise: function(gridId) {
                return dti.oasis.grid._protected.getFilteringDeferredObj().promise();
            },

            addRow: function (gridId, rowId, rowInfo) {
                var rawData = this.getRawData(gridId);

                rowInfo = rowInfo || {};

                function __generateRowId(rowInfo) {
                    if (!rowInfo.hasOwnProperty("@id")) {
                        if (dti.oasis.string.isEmpty(rowId)) {
                            var temp_id = this.getLastInsertedRowId(gridId);

                            if (temp_id != null) {
                                if (temp_id <= -3000) {
                                    temp_id = parseInt(temp_id);
                                    temp_id = (temp_id - 1).toString();
                                } else {
                                    // Initialize the first inserted row id to -3000
                                    temp_id = '-3000';
                                }
                            } else {
                                temp_id = '-3000';
                            }

                            rowInfo["@id"] = temp_id;
                        } else {
                            rowInfo["@id"] = rowId;
                        }
                    }
                }

                function __addEmptyValues(gridId, rowInfo) {
                    var modelRow = dti.oasis.grid.getModelRow(gridId);
                    for (var p in modelRow) {
                        if (modelRow.hasOwnProperty(p) && !rowInfo.hasOwnProperty(p)) {
                            if (p == "@col") {
                                rowInfo[p] = modelRow[p];
                            } else {
                                rowInfo[p] = "";
                            }
                        }
                    }
                }

                function __preAddRow(gridId, rawData, rowInfo) {
                    // Generate row index.
                    rowInfo["@index"] = rawData.length;

                    __generateRowId(rowInfo);

                    __addEmptyValues(gridId, rowInfo);

                    // Set update ind.
                    rowInfo["UPDATE_IND"] = "I";

                    var selectIndColumnName = dti.oasis.grid.getSelectIndColumnName(gridId);
                    if (dti.oasis.grid.hasColumn(gridId, selectIndColumnName)) {
                        // Set the default value of check box to "0"
                        rowInfo[selectIndColumnName] = "0";
                    }

                    showNonEmptyTable(getObject(gridId));
                }

                function __addRawData(gridId, rawData, rowInfo) {
                    rawData[rawData.length] = rowInfo;

                    var filteredRawData = dti.oasis.grid.getFilteredRawData(gridId);
                    filteredRawData[filteredRawData.length] = rowInfo;
                }

                function __addRow(gridId, rowInfo) {
                    var gridInfo = dti.oasis.page.getGridInfo(gridId);
                    var columns = gridInfo["compiledConfig"]["columns"];
                    var jqxRowInfo = {};
                    for (var i = 0; i < columns.length; i++) {
                        jqxRowInfo[columns[i].datafield] = rowInfo[columns[i].datafield];
                    }

                    $("#".concat(gridId)).jqxGrid("addrow", dti.oasis.grid.getRowJqxRowIdByRowData(gridId, jqxRowInfo), jqxRowInfo, "last");

                    //setup indicator for setTimeout() in onGridRendered().
                    dti.oasis.grid.setProperty(gridId, "addRowInvoked", true);

                    lastInsertedId = rowInfo["@id"];
                    if (isMultiGridSupported) {
                        dti.oasis.grid.setProperty(gridId, "lastInsertedId", dti.oasis.grid.getRowIdByRowData(gridId, rowInfo));
                        dti.oasis.grid.setProperty(gridId, "lastInsertedRowIndex", dti.oasis.grid.getRowIndexByRowData(gridId, rowInfo));
                    }

                    window[gridId.concat("1")].recordset.MoveLast();
                    // this.selectRowById(gridId, rowInfo["@id"]);

                    window[gridId.concat("_setInitialValues")]();

                    if (window[gridId.concat("1")].recordset.EOF) {
                        // The _setInitialValues() sometimes causes the recordset to move past the end, so reset it to the end.
                        window[gridId.concat("1")].recordset.MoveLast();
                    }

                    window[gridId.concat("1")].recordset("UPDATE_IND").value = "I";

                    if (!window[gridId.concat("1")].recordset("DISPLAY_IND").value) {
                        window[gridId.concat("1")].recordset("DISPLAY_IND").value = "Y";
                    }
                    // Set the EDIT_IND to the "Y" default if the _initialValues() function didn't set it
                    if (!window[gridId.concat("1")].recordset("EDIT_IND").value) {
                        window[gridId.concat("1")].recordset("EDIT_IND").value = "Y";
                    }
                }

                function __postAddRow(gridId, rowInfo) {
                    // TODO OBR functions
                    //fireOBROnAdd(gridId);

                    var isAddMultipleRow = dti.oasis.grid.getProperty(gridId, "isAddMultipleRow");
                    var firstAddedMulitpleRowId = dti.oasis.grid.getProperty(gridId, "firstAddedMulitpleRowId");
                    //if it is inserting multiple row, and is inserting the first row
                    if (isAddMultipleRow && firstAddedMulitpleRowId == null) {
                        //set firstAddedMulitpleRowId flag to current row id
                        dti.oasis.grid.setProperty(gridId, "firstAddedMulitpleRowId", window[gridId.concat("1")].recordset("ID").value);
                    }

                    dti.oasis.grid.selectRowByIndex(gridId, dti.oasis.grid.getRowIndexByRowData(gridId, rowInfo));

                    var isUserReadyStateReadyComplete = dti.oasis.grid.getProperty(gridId, "isUserReadyStateReadyComplete");
                    if (!isUserReadyStateReadyComplete) {
                        // TODO setRowStyleForNewRow
                        dti.oasis.grid.setProperty(gridId, "isUserReadyStateReadyComplete", true);
                    }

                    // TODO set setRowStyleForNewRow
                }

                __preAddRow(gridId, rawData, rowInfo);

                __addRawData(gridId, rawData, rowInfo);

                __addRow(gridId, rowInfo);

                __postAddRow(gridId, rowInfo);
            },

            deleteRow: function (gridId) {
                // Delete row in jqxGrid.
                var jqxRowId = this.getSelectedJqxRowId(gridId);
                var rowIndex = this.getRowIndexByJqxRowId(jqxRowId, jqxRowId);
                $('#'.concat(gridId)).jqxGrid('deleterow', jqxRowId);

                // Set update ind.
                var rowData = this.getRowDataByRowIndex(gridId, rowIndex);
                if (rowData["UPDATE_IND"] == "I") {
                    rowData["UPDATE_IND"] = "I-D";
                } else {
                    rowData["UPDATE_IND"] = "D";
                }

                // Set select ind.
                if (dti.oasis.grid.hasSelectIndColumnName(gridId)) {
                    var selectIndColumn = this.getSelectIndColumnName(gridId);
                    rowData[selectIndColumn] = "0";
                }

                // Show/hide empty table, and reset selected row no.
                var recordCount = this.getRecordCount(gridId);
                var gonnaHide = (recordCount == 0);

                if (gonnaHide) {
                    this.setProperty(gridId, "selectedTableRowNo", null);
                    hideEmptyTable(getObject(gridId));
                }

                this.setProperty(gridId, "selectedTableRowNo", null);

                // Select row.
                if (this.getProperty(gridId, "isDeleteMultipleRow") == true) {
                    var count = this.getProperty(gridId, "DeletedMultipleRowsCount");
                    this.setProperty(gridId, "DeletedMultipleRowsCount", count + 1);
                    this.selectRowByJqxRowIndex(gridId, rowIndex + 1);
                    // Do nothing, developer should call endMultipleDelete after deleting all.
                } else {
                    this.selectFirstRowInGrid(gridId);
                }

                dti.oasis.grid.config.recompileColumns(gridId);

                return gonnaHide;
            },

            ensureRowVisible: function(gridId, rowId) {
                var jqxRowIndex = this.getJqxRowIndexByRowId(gridId, rowId);

                if (jqxRowIndex >= 0) {
                    $("#".concat(gridId)).jqxGrid('ensurerowvisible', jqxRowIndex);
                }
            },

            clearRowSelection: function (gridId) {
                if (this.getSelectedRowId(gridId) != null) {
                    this.setProperty(gridId, "isClearingSelection", true);

                    $("#".concat(gridId)).jqxGrid("clearselection");

                    this.onClearRowSelection(gridId);

                    this.setProperty(gridId, "isClearingSelection", false);
                }
            },

            /**
             * Filter a grid by a filter object.
             * Please see {@link dti.oasis.filter} for details about filter object.
             *
             * Example:
             * dti.oasis.grid.filter("claimListGrid", dti.oasis.filter.createFilter({
             *     values: [
             *         {type: "COLUMN", value: "CCLAIMNO"},
             *         "123456"
             *     ]
             * }));
             *
             * @param gridId
             * @param filterObj
             * @return filtering promise
             */
            filter: function (gridId, filterObj) {
                var filteringDeferredObj = dti.oasis.grid._protected.addFilteringDeferredObj(gridId);
                var hasBeenInitialized = dti.oasis.grid.getProperty(gridId, "hasBeenInitialized");
                if (!hasBeenInitialized) {
                    $.when(dti.oasis.grid.getLoadingPromise(gridId)).then(function () {
                        __filter(gridId, filterObj);
                    });
                }
                else {
                    __filter(gridId, filterObj);
                }
                // return filteringDeferredObj.promise();

                function __filter(gridId, filterObj) {
                    dti.oasis.grid.setProperty(gridId, "currentFilterValue", filterObj);
                    dti.oasis.grid.setProperty(gridId, "filterflag", true);
                    dti.oasis.grid.setProperty(gridId, "filtering", true);
                    dti.oasis.grid.load({gridId: gridId});
                }

                return filteringDeferredObj.promise();
            },

            /**
             * Filter a grid by an xPath style filter string.
             *
             * It will call {@link dti.oasis.filter.compile} to compile an xPath style filter string to a filter object.
             * And call {@link dti.oasis.grid.filter} to filter a grid.
             *
             * Example:
             * dti.oasis.grid.filterByString("claimListGrid", "CCLAIMNO = '123456'");
             *
             * @param gridId
             * @param filterString
             */
            filterByString: function (gridId, filterString) {
                var filterObj = dti.oasis.filter.compile(filterString);
                this.filter(gridId, filterObj);
            },

            /**
             * Filter rawData with filter object.
             *
             * @param gridId
             * @param filterObj
             * @param callback The optional callback function for processing a filtered object.
             * @returns {*}
             */
            filterRawData: function (gridId, filterObj, callback) {
                var rawData = this.getRawData(gridId);
                return $.grep(rawData, function(record) {
                    var filterResult = filterObj.filter(record);

                    if (filterResult && callback) {
                        callback(record);
                    }

                    return filterResult;
                });
            },

            selectRawDataColumnValues: function (gridId, columnName, filterObj) {
                var columnValues = [];
                var rawData = this.getRawData(gridId);
                var actualColumnName = this.getActualColumnName(gridId, columnName);

                $.grep(rawData, function(record) {
                    if(filterObj && !filterObj.filter(record)) {
                        return false;
                    }

                    columnValues[columnValues.length] = record[actualColumnName];

                    return true;
                });

                return columnValues;
            },

            /**
             * Page no is starting from 0.
             * @param gridId
             * @param pageNo
             */
            gotoPage: function (gridId, pageNo) {
                var gridObj = $("#".concat(gridId));
                var pagesCount = gridObj.jqxGrid("getpaginginformation").pagescount;

                if (pageNo <= pagesCount - 1) {
                    gridObj.jqxGrid('gotopage', pageNo);
                }
            },

            gotoPageByType: function (gridId, type) {
                var pagingInfo = $("#".concat(gridId)).jqxGrid("getpaginginformation");
                var pageNo = pagingInfo.pagenum;
                var pagesCount = pagingInfo.pagescount;

                if (pagesCount > 0) {
                    switch (type) {
                        // First
                        case 'F':
                            this.gotoPage(gridId, 0);
                            break;
                        // Next
                        case 'N':
                            if (pageNo < pagesCount - 1) {
                                this.gotoPage(gridId, pageNo + 1);
                            }
                            break;
                        // Previous
                        case 'P':
                            if (pageNo > 1) {
                                this.gotoPage(gridId, pageNo - 1);
                            }
                            break;
                        // Last
                        case 'L':
                            this.gotoPage(gridId, pagesCount - 1);
                            break;
                    }
                }
            },

            hasColumn: function (gridId, columnName) {
                return dti.oasis.grid.config.hasColumn(gridId, columnName);
            },

            hasDisplayOnlyColumn: function (gridId, dataField) {
                return dti.oasis.grid.config.hasDisplayOnlyColumn(gridId, dataField);
            },

            hasRow: function (gridId, rowId) {
                var jqxGridRowId = this.getJqxRowIdByRowId(gridId, rowId);
                return ($("#".concat(gridId)).jqxGrid("getrowboundindexbyid", jqxGridRowId) != -1);
            },

            hasRows: function (gridId) {
                return (this.getRecordCount(gridId) > 0);
            },

            hasRowByDisplayIndex: function (gridId, jqxRowDisplayIndex) {
                var rowsCount = this.getRecordCount(gridId);
                return (jqxRowDisplayIndex <= rowsCount);
            },

            hasRowByJqxRowIndex: function (gridId, jqxRowIndex) {
                return ($("#".concat(gridId)).jqxGrid("getrowid", jqxRowIndex) != null);
            },

            /**
             * Check if there are opened jqxGrid menu.
             * @returns {boolean}
             */
            hasOpenedJqxGridMenu: function () {
                var result = false;

                if (dti.oasis.page.useJqxGrid()) {
                    $('[id^="gridmenu"]').each(function (index, element) {
                        if ($(element).data("contextMenuOpened" + element.id)) {
                            result = true;
                            return false;
                        }
                    });
                }

                return result;
            },

            isGridDataChanged: function (gridId) {
                var rawData = this.getRawData(gridId);

                for (var i = 0; i < rawData.length; i++) {
                    var updateInd = rawData[i]["UPDATE_IND"];

                    // TODO Skip I-D?
                    if (!dti.oasis.string.isEmpty(updateInd) && updateInd != "N") {
                        return true;
                    }
                }

                return false;
            },

            isGridDataField: function (gridId, columnName) {
                var dataFiledNames = dti.oasis.page.getGridInfo(gridId).compiledConfig.dataFieldNames;
                return (dataFiledNames[columnName.toUpperCase()] === 1);
            },

            isColumnInGrid: function (gridId, columnName) {
                var columnDataFields = dti.oasis.page.getGridInfo(gridId).compiledConfig.columnDataFields;
                return (columnDataFields[columnName.toUpperCase()] === 1);
            },

            /**
             * Check if this is a jqxGrid menu element or a child element of jqxGrid menu.
             *
             * @param element
             * @returns {*|jQuery|boolean}
             */
            isJqxGridMenuElement: function (element) {
                return dti.oasis.page.useJqxGrid() &&
                    ($(element).hasClass("jqx-menu-wrapper") && /^menuWrappergridmenu/.test($(element).attr("id"))
                        || $(element).parents('.jqx-menu-wrapper[id^="menuWrappergridmenu"]').length > 0);
            },

            isRecordSelected: function (gridId) {
                var rawData = this.getRawData(gridId);
                var selectIndColumnName = this.getSelectIndColumnName(gridId);

                for (var i = 0; i < rawData.length; i++) {
                    if (rawData[i].hasOwnProperty(selectIndColumnName) &&
                        rawData[i][selectIndColumnName] == "-1") {
                        return true;
                    }
                }

                return false;
            },

            isSelectedRow: function (gridId, rowId) {
                var selectedRowIndex = dti.oasis.grid.getRowIndexByRowId(gridId, dti.oasis.grid.getSelectedRowId(gridId));
                var currentRowIndex = dti.oasis.grid.getRowIndexByRowId(gridId, rowId);
                return selectedRowIndex == currentRowIndex;
            },

            moveToRow: function (gridId, rowId) {
                var recordSet = window[gridId + "1"].recordset;

                if (recordSet("ID").value !== rowId) {
                    recordSet.MoveFirst();

                    while (!recordSet.EOF) {
                        if (recordSet("ID").value === rowId) {
                            break;
                        }

                        recordSet.MoveNext();
                    }                }

            },

            notifyOfGridComplete: function (gridInfo) {
                gridInfo.readyState = "complete";
                $(gridInfo).trigger("complete.dti.grid");
            },

            onGridComplete: function (gridInfo) {

                var gridId = gridInfo.id;

                try {
                    if (dti.oasis.grid.getProperty(gridId, "hasBeenInitialized") || dti.oasis.grid.getProperty(gridId, "loadingFromRemoteSource")) {
                        // Only recompile the columns if this is not the first time we are loading the grid, and if we are loading data from a remote source
                        dti.oasis.grid.config.recompileColumns(gridId);
                    }
                    dti.oasis.grid.setProperty(gridId, "hasBeenInitialized", true);

                    // Adjust grid header height after grid is loaded.
                    this._protected._adjustGridHeaderHeightForContent(gridId);

                    if (dti.oasis.grid.getProperty(gridId, "filtering")) {
                        // Skip readyStateReady since onGridFilter calls it
                        this.onGridFilter(gridInfo);
                    } else {
                        try {
                            readyStateReady(gridInfo);
                        } catch (e) {
                            // Do nothing
                        }
                    }
                } finally {
                    dti.oasis.grid.getProperty(gridId, "loadingDeferredObj").resolve();
                }
            },

            onGridReady: function (gridInfo) {
                dti.oasis.grid.setProperty(gridInfo.id, "isGridReady", true);

                // Notify of complete if we are not loading from a remote source, or if we are loading remotely and the data has been loaded
                if (!dti.oasis.grid.getProperty(gridInfo.id, "loadingFromRemoteSource") ||
                    (dti.oasis.grid.getProperty(gridInfo.id, "loadingFromRemoteSource") && dti.oasis.grid.getProperty(gridInfo.id, "isGridDataLoaded"))) {
                    dti.oasis.grid.notifyOfGridComplete(gridInfo);
                }
            },

            onGridDataBindingComplete: function(gridInfo) {
                dti.oasis.grid.setProperty(gridInfo.id, "isGridDataLoaded", true);

                // Only notify of complete if the grid is ready. If it's not ready, then the onGridReady method will handle it
                if (dti.oasis.grid.getProperty(gridInfo.id, "isGridReady")) {
                    dti.oasis.grid.notifyOfGridComplete(gridInfo);
                }
            },

            areAllGridsReady: function () {
                var visibleGridIds = dti.oasis.grid.getGridIdsForVisibleGrids();
                for (var i = 0; i < visibleGridIds.length; i++) {
                    if (!dti.oasis.grid.getProperty(gridIds[i], "isGridReady")) {
                        return false;
                    }
                }
                return true;
            },

            getGridIdsForVisibleGrids: function() {
                var gridIds = dti.oasis.page.getProperty("gridIds");
                var visibleGridIds = [];
                if (gridIds) {
                    for (var i = 0; i < gridIds.length; i++) {
                        var grid = $("#" + gridIds[i]);
                        var panel = grid.closest('.panel');

                        //Skip checking if grid is ready under below cases:
                        // 1) The panel is hidden
                        if ((panel.length > 0 && panel.is(":hidden"))

                            // 2) The content of the panel or any ancestor panel is collapsed
                            || grid.closest('div.collapsePanel').length > 0) {
                            continue;
                        }
                        visibleGridIds.push(gridIds[i]);
                    }
                }
                return visibleGridIds;
            },

            onGridRendered : function (grid, gridInfo, type) {
                var gridId = gridInfo.id;

                // Ensure the selected row is in focus in cases the page was refreshed and showing the top of the page instead of the selected row
                var selectedIndex = grid.jqxGrid('selectedRowIndex');
                var scrollPosition = grid.jqxGrid('scrollposition');
                if (scrollPosition) {
                    var scrollTopPosition = (scrollPosition ? scrollPosition.top : 0);
                    //Selected row is not in focus, enforce the focus
                    if (selectedIndex > 0 && scrollTopPosition == 0) {
                        var addRowInvoked = dti.oasis.grid.getProperty(gridId, "addRowInvoked");
                        if(addRowInvoked) {
                            dti.oasis.grid.setProperty(gridId, "addRowInvoked", false);
                            setTimeout(function(){
                                if (dti.oasis.grid.getPageCount(gridId) > 1) {
                                    grid.jqxGrid('gotoprevpage');
                                    grid.jqxGrid('gotonextpage');
                                    grid.jqxGrid('selectrow', selectedIndex);
                                    grid.jqxGrid('ensurerowvisible', selectedIndex);
                                } else {
                                    grid.jqxGrid('selectrow',selectedIndex);
                                    grid.jqxGrid('ensurerowvisible', selectedIndex);
                                }
                            },50);
                        }
                        grid.jqxGrid('ensurerowvisible', selectedIndex);
                    }
                }

                // Restore the saved pageable setting
                var pageableSetting = dti.oasis.grid.getProperty(gridId, "pageable")
                grid.jqxGrid({ pageable: pageableSetting});

                __displayExportExcelButtons();

                // Adding export excel button if they don't already exist
                function __displayExportExcelButtons() {
                    var gridInfo = dti.oasis.page.getGridInfo(gridId);
                    var saveGridAsExcelCsv = gridInfo["config"]["gridConfig"]["saveGridAsExcelCsv"];
                    var dispositionTypeExcelCsvFile = gridInfo["config"]["gridConfig"]["dispositionTypeExcelCsvFile"];
                    var saveGridAsExcelHtml = gridInfo["config"]["gridConfig"]["saveGridAsExcelHtml"];
                    var dispositionTypeExcelHtmlFile = gridInfo["config"]["gridConfig"]["dispositionTypeExcelHtmlFile"];

                    if (saveGridAsExcelCsv || saveGridAsExcelHtml) {
                        if ($("#exportToExcel_".concat(gridId)).length == 0) {
                            var gridObj = $("#".concat(gridId));

                            var exportToExcelDiv = $("<div id='exportToExcel_" + gridId + "' class='dti-jqx-grid-exportDiv'></div>");
                            var exportToExcelSpan = $("<span class='dti-jqx-grid-exportSpan'></span>");

                            if (saveGridAsExcelCsv) {
                                var exportToExcelButton = $("<input name='btnSaveAsCSV' class='dti-jqx-grid-excelExportButton' id=\"btnSaveAsCSV_" + gridId + "\"  type=button  value=' Export (Excel)'  onclick=\"saveGridAsExcelCsv('" + gridId + "', '" + dispositionTypeExcelCsvFile + "');\"/>");
                                exportToExcelButton.appendTo(exportToExcelSpan);

                                if (enableExportAll) {
                                    var exportAllToExcelButton = $("<input name='btnSaveAllAsCSV' class='dti-jqx-grid-excelExportAllButton' id=\"btnSaveAllAsCSV_" + gridId + "\"  type=button  value=' Export All'  onclick=\"saveGridAsExcelCsv('" + gridId + "', '" + dispositionTypeExcelCsvFile + "', true);\"/>");
                                    exportAllToExcelButton.appendTo(exportToExcelSpan);
                                }
                            }

                            if (saveGridAsExcelHtml) {
                                var saveAsExcelHtmlButton = $("<input name='btnSaveAsExcel' class='dti-jqx-grid-htmlExportButton' id=\"btnSaveAsExcel_" + gridId + "\"  type=button  value=' Export (HTML)'  onclick=\"saveGridAsExcelHtml('" + gridId + "', '" + dispositionTypeExcelHtmlFile + "');\"/>");
                                saveAsExcelHtmlButton.appendTo(exportToExcelSpan);
                            }

                            exportToExcelSpan.appendTo(exportToExcelDiv);
                            gridObj.find(".jqx-grid-pager").children().prepend(exportToExcelDiv);
                        }
                    }
                }

            },

            onGridSort: function (gridInfo) {
                // Clear selection for selected row.
                this.clearRowSelection(gridInfo.id);

                // Set sorting property to trigger the on sorting logic in readyStateReady.
                this.setProperty(gridInfo.id, "sorting", true);
                if (dti.oasis.grid.getProperty(gridInfo.id, "sortingDeferredObj").state() !== "pending") {
                    dti.oasis.grid.setProperty(gridInfo.id, "sortingDeferredObj", $.Deferred());
                }

                if (!dti.oasis.page.getProperty("sortingGridOnPageLoad")) {
                    try {
                        readyStateReady(gridInfo);
                    } catch (e) {
                        // Do nothing
                    }
                }

                if (window.commonOnAfterSort) {
                    commonOnAfterSort(getTableForGrid(gridInfo.id), dti.oasis.grid.getDataIsland(gridInfo.id));
                }

                if (window.handleOnAfterSort) {
                    handleOnAfterSort(getTableForGrid(gridInfo.id), dti.oasis.grid.getDataIsland(gridInfo.id));
                }
            },

            onGridPageChanged: function (gridInfo) {
                dti.oasis.grid.setProperty(gridId, "isInGridPageChanged", true);
                try {
                    if (!dti.oasis.page.getProperty("sortingGridOnPageLoad")) {
                        var gridId = gridInfo.id;
                        var isSelectingRow = dti.oasis.grid.getProperty(gridId, "isSelectingRow");
                        var isClearingSelection = dti.oasis.grid.getProperty(gridId, "isClearingSelection");

                        if ((isSelectingRow == null || !isSelectingRow) &&
                            (isClearingSelection == null || !isClearingSelection)) {

                            if (dti.oasis.grid.getProperty(gridId, "autoSelectFirstRow")) {
                                dti.oasis.grid.selectFirstRowInCurrentPage(gridId);
                            }

                            try {
                                readyStateReady(gridInfo);
                            } catch (e) {
                                // Do nothing
                            }
                        }
                    }
                } finally {
                    dti.oasis.grid.removeProperty(gridId, "isInGridPageChanged");
                }
            },

            onGridPageSizeChanged: function (gridInfo) {
            },

            onGridFilter: function (gridInfo) {
                var gridId = gridInfo.id;

                // if (this.getSelectIndColumnName(gridId)) {
                //     // Clear select ind.
                //     this.deselectAll(gridId);
                // }
                // Clear row selection.
                this.clearRowSelection(gridId);

                if (this.hasRows(gridId)) {
                    showGridDetailDiv(gridId);
                } else {
                    hideGridDetailDiv(gridId);
                }

                try {
                    readyStateReady(gridInfo);
                } catch (e) {
                    // Do nothing
                }
            },

            onRowSelected: function (gridId, rowIndex, rowId) {
                function __isInvokeFieldDepsAndPageEntitlementAfter_selectRow() {
                    if (window["isFieldDepsAndPageEntitlementsAfter_selectRow"]) {
                        return window["isFieldDepsAndPageEntitlementsAfter_selectRow"](gridId);
                    }

                    return false;
                }

                function __invokeFieldDep() {
                    if (window["processFieldDeps"]) {
                        window["processFieldDeps"]();
                    }
                }

                function __enforceOBR(gridId, rowId) {
                    if (window.document.readyState == "complete") {
                        // This baseOnRowSelected gets called as soon as the table is ready irrespective of the document is ready or not.
                        // Called by selectFirstRowInGrid via alternate_colors->readyStateReady->userStateReady->commonStateReady
                        // These events gets called as soon as the table is ready.
                        if (window["enforceOBRForGridFields"]) {
                            enforceOBRForGridFields(gridId, rowId);
                        }
                    }
                }

                function __invokePageEntitleEments(gridId) {
                    if (window["pageEntitlements"]) {
                        window["pageEntitlements"](true, gridId);
                    }
                }

                function __invokeCommonOnRowSelected(gridId, rowId) {
                    if (window["commonOnRowSelected"]) {
                        var returnVal = window["commonOnRowSelected"](gridId, rowId);

                        if (returnVal === false) {
                            return false;
                        }
                    }

                    return true;
                }

                function __invokeSelectRow(gridId, rowId) {
                    var fnName = gridId.concat("_selectRow");
                    if (window[fnName]) {
                        var returnVal = window[fnName](rowId);

                        if (returnVal === false) {
                            return false;
                        }
                    }

                    return true;
                }

                this.setSelectedRowIndex(gridId, rowIndex);

                this.syncCellValueToDetailFieldsForSelectedRow(gridId);

                if (!hasObject("CWBHeaderRow")) {
                    // Do not enable all fields based on EDIT_IND, if the page is for custWebWb.
                    // CustWebWB has specific logic implemented to disable BASE fields and those fields shouldn't get enabled.
                    baseEnableDisableGridDetailFields('PRE', gridId);
                }

                // maintainValueForAllMultiSelectFields(gridId);

                fireAjaxForSelectedRow(gridId, rowId);

                maintainNoteImageForAllNoteFields();

                maintainValueForAllMultiSelectFields(gridId);

                var invokeFieldDepsAndPageEntitlementAfter_selectRow = __isInvokeFieldDepsAndPageEntitlementAfter_selectRow(gridId);

                if (!invokeFieldDepsAndPageEntitlementAfter_selectRow) {
                    __invokeFieldDep();
                    __enforceOBR(gridId, rowId);
                    __invokePageEntitleEments(gridId);
                }

                var isOnSelectRowEventSuccess = __invokeCommonOnRowSelected(gridId, rowId);

                if (isOnSelectRowEventSuccess) {
                    isOnSelectRowEventSuccess = __invokeSelectRow(gridId, rowId);
                }

                if (isOnSelectRowEventSuccess && invokeFieldDepsAndPageEntitlementAfter_selectRow) {
                    __invokeFieldDep();
                    __enforceOBR(gridId, rowId);
                    __invokePageEntitleEments(gridId);
                }

                baseEnableDisableGridDetailFields('POST', gridId);

                showGridDetailDiv(gridId);

                if (dti.oasis.grid.getProperty(gridId, "isSelectingRowByUser")) {
                    setFocusToFirstFieldInGrid(gridId);
                }
            },

            onClearRowSelection: function (gridId) {
                // Sync current selected row data.
                this.syncChangedValueForRowSelected(gridId);

                // fix the problem when clicking select_ind check_box in grid the focus field doesn't lose focus
                // which will cause a problem that the ATTRIBUTE_ORIGINAL_VALUE is still the previous one
                // in a result it will trigger onChange event even no change is made
                var element = document.activeElement;
                if (element && $(element).data("dtiDatasrc") && $(element).data("dtiDatasrc") == "#".concat(gridId, "1")) {
                    element.blur();
                }

                // Clear selected row number.
                this.setProperty(gridId, "selectedTableRowNo", null);

                // Goto last.
                var dataIslandObj = dti.oasis.grid.getDataIsland(gridId);
                dataIslandObj.recordset.AfterLast();
            },

            onColumnClick: function (gridId, columnName) {
                // Select / dis-select all.
                var selectIndColumnName = this.getSelectIndColumnName(gridId);

                if (columnName == selectIndColumnName && $("#".concat(gridId, "_chkCSELECT_ALL")).length) {
                    function __selectAll() {
                        if (dti.oasis.grid.isSelectAllDisabled(gridId)) {
                            return;
                        }

                        var grid = $("#".concat(gridId));
                        var checked = $("#".concat(gridId, "_chkCSELECT_ALL")).hasClass("jqx-checkbox-check-checked");

                        if (checked) {
                            $("#".concat(gridId, "_chkCSELECT_ALL")).removeClass("jqx-checkbox-check-checked");
                        } else {
                            $("#".concat(gridId, "_chkCSELECT_ALL")).addClass("jqx-checkbox-check-checked");
                        }

                        var rowCount = grid.jqxGrid('getdatainformation').rowscount;
                        for (var i = 0; i < rowCount; i++) {
                            var fnIsCellEditable = dti.oasis.grid.getCustomColumnOption(gridId, columnName, "isCellEditable");

                            if (fnIsCellEditable == null || fnIsCellEditable(grid.jqxGrid("getrowboundindex", i))) {
                                grid.jqxGrid("setcellvalue", grid.jqxGrid("getrowboundindex", i), selectIndColumnName, !checked, false);
                            }
                        }

                        if (window["handleOnSelectAll"]) {
                            handleOnSelectAll(gridId, !checked);
                        }

                        // Select the first row after selected/deselected all.
                        dti.oasis.grid.selectFirstRowInGrid(gridId);
                    }

                    if (window["handleOnBeforeSelectAll"]) {
                        var returnVal = handleOnBeforeSelectAll(gridId);
                        if(returnVal==false){
                            // If the page is not ready to select all rows on the grid, do nothing and return.
                            // Otherwise, we should continue to execute the next steps below.
                            return false;
                        }
                    }

                    var rowCount = this.getRecordCount(gridId);
                    if (rowCount >= 1000) {
                        showProcessingImgIndicatorWithPromise().then(function () {
                            __selectAll();
                            hideProcessingImgIndicator();
                        });
                    } else {
                        __selectAll();
                    }
                }
            },

            onCellBeginEdit: function (gridId, rowIndex, columnName) {
                // Select the current row.
                var selectedRowIndex = dti.oasis.grid.getSelectedRowIndex(gridId);

                if (rowIndex != selectedRowIndex) {
                    dti.oasis.grid.selectRowByIndex(gridId, rowIndex);
                }
            },

            onCellEndEdit: function (gridId, rowIndex, columnName, value, oldValue) {
            },

            onPostCellEndEdit: function (gridId, rowIndex, columnName, value, oldValue) {
                function __formatEditorValue(val, columnName) {
                    return dti.oasis.grid.syncCellValueToRowDataFormat.getSyncCellValueToRowDataFormatter(gridId, columnName).format(val);
                }

                function __getEditorName(dataField, columnDisplayType) {
                    switch(columnDisplayType) {
                        case "checkbox":
                            return "chk" + dataField;
                            break;
                        case "dropdownlist":
                        case "combobox":
                            return "cbo" + dataField;
                            break;
                        default:
                            return "txt" + dataField;
                    }
                }

                var columnDisplayType = dti.oasis.grid.getColumnDisplayType(gridId, columnName);

                var editorName = __getEditorName(columnName, columnDisplayType);

                var rowId = dti.oasis.grid.getRowIdByRowIndex(gridId, rowIndex);

                var val = __formatEditorValue(value, columnName);
                var oldVal = __formatEditorValue(oldValue, columnName);

                if (val === null) {
                    val = "";
                }

                if (oldVal === null) {
                    oldVal = "";
                }

                var obj = {
                    _protected: {
                        value: val
                    },
                    name: editorName,
                    title: dti.oasis.grid.getColumnLabel(gridId, columnName),
                    gridId: gridId,
                    rowIndex: rowIndex,
                    rowId: rowId,
                    dataField: columnName,
                    oldValue: oldVal,
                    hasAttribute: function (attr) {
                        return this.hasOwnProperty(attr);
                    },
                    getAttribute: function (attr) {
                        return this[attr];
                    },
                    setAttribute: function (attr, val) {
                        this[attr] = val;
                    }
                };

                Object.defineProperty(obj, "value", {
                    get: function() {
                        return obj._protected.value;
                    },
                    set: function(val) {
                        obj._protected.value = val;
                        window[gridId.concat("1")].recordset(columnName).value = val;
                    }
                });

                Object.defineProperty(obj, "checked", {
                    get: function() {
                        return (obj._protected.value === true || obj._protected.value === "-1");
                    },
                    set: function(val) {
                        obj._protected.value = val ? "-1" : "0";
                        window[gridId.concat("1")].recordset(columnName).value = obj._protected.value;
                    }
                });

                //this can let entered cell value sync to detail field.
                obj.value = val;

                // Handle checkbox
                if (columnDisplayType === "checkbox") {
                    obj.checked = value;
                }

                // Invoke row change event.
                if (window.rowchange) {
                    try {
                        rowchange(obj);
                    } catch (e) {
                        //avoid endTime = new date() can't be executed when pop page closed in userRowChange function.
                        //refer to use case: case claim list transfer claim page.
                    }
                }
            },

            onCellClick: function (gridId, rowIndex, columnName) {
                // Sync previous selected row data.
                var success = dti.oasis.grid._protected._syncRowDataForSelectRow(gridId, rowIndex, columnName);

                if (success) {
                    var selectedRowIndex = dti.oasis.grid.getSelectedRowIndex(gridId);

                    if (rowIndex != selectedRowIndex) {
                        var rowId = this.getRowIdByRowIndex(rowIndex);

                        dti.oasis.grid.setProperty(gridId, "isSelectingRowByUser", true);
                        try {
                            var preSelectRowFn = dti.oasis.grid.getCustomGridOption(gridId, "preSelectRowByUser");
                            if (preSelectRowFn != null &&
                                preSelectRowFn({
                                    "gridId": gridId,
                                    "rowId": rowId,
                                    "origEvent": "onCellClick"
                                }) === false) {
                                return;
                            }

                            // If the current row is not selected, select the current row.
                            dti.oasis.grid.selectRowByIndex(gridId, rowIndex);

                            var postSelectRowFn = dti.oasis.grid.getCustomGridOption(gridId, "postSelectRowByUser");
                            if (postSelectRowFn != null) {
                                postSelectRowFn({
                                    "gridId": gridId,
                                    "rowId": rowId,
                                    "origEvent": "onCellClick"
                                });
                            }
                        } finally {
                            dti.oasis.grid.setProperty(gridId, "isSelectingRowByUser", false);
                        }
                    }
                }
            },

            onCellLinkClick: function (event, gridId, jqxRowIndex, columnName) {
                var fnHandleCellLink = function (gridId, jqxRowIndex, columnName) {
                    var value = window[gridId.concat("1")].recordset(columnName).value;
                    var href = dti.oasis.grid.getCellHref(gridId, jqxRowIndex, columnName);

                    if (!dti.oasis.string.isEmpty(value) && !dti.oasis.string.isEmpty(href)) {
                        handleOnGridHref(gridId, href);
                    }
                };

                // Sync data.
                var success = dti.oasis.grid._protected._syncRowDataForSelectRow(gridId, jqxRowIndex, columnName);

                if (success) {
                    var selectedJqxRowRowIndex = dti.oasis.grid.getSelectedJqxRowIndex(gridId);

                    // Handle Link.
                    if (jqxRowIndex == selectedJqxRowRowIndex) {
                        fnHandleCellLink(gridId, jqxRowIndex, columnName);
                    } else {
                        // If the current row is not selected, select the current row.
                        dti.oasis.grid.selectRowByJqxRowIndexWithProcessingDlg(gridId, jqxRowIndex).then(function () {
                            fnHandleCellLink(gridId, jqxRowIndex, columnName);
                        });
                    }
                }

                event.stopPropagation();
            },

            onCellValueChanged: function (gridId, rowIndex, columnName, value, oldValue) {
                if (value !== oldValue) {
                    var dependencyDropdownColumns = dti.oasis.grid.getProperty(gridId, "dependencyDropdownColumns");
                    if (dependencyDropdownColumns && dependencyDropdownColumns[columnName]) {
                        // Set the code and label of the child dropdown list column to be empty if parent dropdown list column is changed.
                        var childColumns = dependencyDropdownColumns[columnName];
                        for (var i = 0; i < childColumns.length; i++) {
                            var rowId = dti.oasis.grid.getRowIdByRowIndex(gridId, rowIndex);

                            dti.oasis.grid.setCellValue(gridId, rowId, childColumns[i], "", true);
                            dti.oasis.grid.setCellValue(gridId, rowId, childColumns[i] + "LOVLABEL", "", true);
                        }
                    }
                }

                if (dti.oasis.grid.hasDisplayOnlyColumn(gridId, columnName)) {
                    // Change display only column value.
                    var cellFormatter = dti.oasis.grid.cellFormat.getCellFormatterForDetailDisplayOnlyField(gridId, columnName);

                    if (cellFormatter != null) {
                        window[gridId.concat("1")].recordset(columnName.concat("_DISP_ONLY")).value = cellFormatter.format(gridId, rowIndex, columnName, value);
                    }
                }

                if (columnName == this.getSelectIndColumnName(gridId)) {
                    //Call page level Grid Initialization, if defined
                    var fnName = gridId.concat("_handleOnSelectIndicatorChanged");
                    if (window[fnName] && !dti.oasis.grid.getProperty(gridId, "isIn" + fnName, false)) {
                        dti.oasis.grid.setProperty(gridId, "isIn" + fnName, true);
                        var returnVal = window[fnName](rowIndex);
                        dti.oasis.grid.removeProperty(gridId, "isIn" + fnName);
                    }
                }
            },

            saveGridAsExcel: function (gridId) {
                function __sendGridToServerAsExcel(grid, url, dispType) {
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
                }

                event.stopPropagation();

                var dispositionTypeExcelHtmlFile = dti.oasis.page.getGridInfo(gridId)["config"]["gridConfig"]["dispositionTypeExcelHtmlFile"];

                __sendGridToServerAsExcel(gridId, getCorePath() +"/jsp/jqxGridToExcelXLS.jsp?exportType=XLSX&gridId="+gridId+"&date=" + new Date()+"&pageName=ClaimSearch", dispositionTypeExcelHtmlFile);

                return false;
            },

            selectFirstRowInGrid: function (gridId) {
                dti.oasis.grid.setProperty(gridId, "isInSelectingFirstRowInGrid", true);
                try {
                    this.clearRowSelection(gridId);

                    var grid = $("#".concat(gridId));
                    var jqxRowIndex = grid.jqxGrid("getrowboundindex", 0);

                    if (jqxRowIndex == -1) {
                        this.setSelectedRowIndex(gridId, null);

                        hideEmptyTable(grid);
                        hideGridDetailDiv(gridId);

                        var functionExists = eval("window.pageEntitlements");
                        if (functionExists) {
                            pageEntitlements(true, gridId);
                        }
                    } else {
                        var rowIndex = this.getRowIndexByJqxRowIndex(gridId, jqxRowIndex);
                        this.selectRowByIndex(gridId, rowIndex, 0);
                    }
                } finally {
                    dti.oasis.grid.removeProperty(gridId, "isInSelectingFirstRowInGrid");
                }
            },

            selectFirstRowInCurrentPage: function (gridId) {
                dti.oasis.grid.setProperty(gridId, "isInSelectingFirstRowInGrid", true);
                try {
                    this.clearRowSelection(gridId);

                    var grid = $("#".concat(gridId));
                    var dataInfo = grid.jqxGrid("getdatainformation");

                    if (dataInfo.rowscount > 0) {
                        var pageSize = dataInfo.paginginformation.pagesize;
                        var pageNum = dataInfo.paginginformation.pagenum;
                        var rowToSelected = pageSize * pageNum;

                        this.selectRowByRowDisplayIndex(gridId, rowToSelected);
                    } else {
                        hideEmptyTable(grid);
                        hideGridDetailDiv(gridId);
                    }
                } finally {
                    dti.oasis.grid.removeProperty(gridId, "isInSelectingFirstRowInGrid");
                }
            },

            selectRowById: function (gridId, rowId) {
                if (!dti.oasis.string.isEmpty(rowId)) {
                    var rowIndex = this.getRowIndexByRowId(gridId, rowId);
                    this.selectRowByIndex(gridId, rowIndex);
                }
            },

            selectRowByIndex: function (gridId, rowIndex) {
                this.clearRowSelection(gridId);

                var jqxRowId = this.getJqxRowIdByRowIndex(gridId, rowIndex);
                var jqxRowIndex = this.getJqxRowIndexByJqxRowId(gridId, jqxRowId);

                if (jqxRowIndex == -1) {
                    this.selectFirstRowInGrid(gridId);
                } else {
                    var jqxRowDisplayIndex = this.getJqxRowDisplayIndexByJqxRowIndex(gridId, jqxRowIndex);

                    if (jqxRowDisplayIndex == -1) {
                        this.selectFirstRowInGrid(gridId);
                    } else {
                        this._protected._selectRow(gridId, jqxRowIndex, jqxRowDisplayIndex);
                    }
                }
            },

            selectRowByRowDisplayIndex: function (gridId, jqxRowDisplayIndex) {
                this.clearRowSelection(gridId);

                var grid = $("#".concat(gridId));
                var jqxRowIndex = grid.jqxGrid("getrowboundindex", jqxRowDisplayIndex);

                if (jqxRowIndex == -1) {
                    this.selectFirstRowInGrid(gridId);
                } else {
                    this._protected._selectRow(gridId, jqxRowIndex, jqxRowDisplayIndex);
                }
            },

            selectRowByJqxRowIndex: function (gridId, jqxRowIndex) {
                this.clearRowSelection(gridId);

                var grid = $("#".concat(gridId));
                var jqxRowDisplayIndex = parseInt(grid.jqxGrid("getrowdisplayindex", jqxRowIndex));

                if (jqxRowDisplayIndex == -1) {
                    this.selectFirstRowInGrid(gridId);
                } else {
                    this._protected._selectRow(gridId, jqxRowIndex, jqxRowDisplayIndex);
                }
            },

            selectRowWithProcessingDlg: function (gridId, rowId) {
                var rowIndex = this.getRowIndexByRowId(gridId, rowId);

                var defer = $.Deferred();

                this.selectRowByIndexWithProcessingDlg(gridId, rowIndex).then(function () {
                    defer.resolve();
                });

                return defer.promise();
            },

            selectRowByIndexWithProcessingDlg: function (gridId, rowIndex) {
                var defer = $.Deferred();
                var needProcessingDialog = false;
                if (window["shouldDisplayProcessingDlgOnRowSelect"]) {
                    needProcessingDialog = window["shouldDisplayProcessingDlgOnRowSelect"](gridId);
                }

                if (needProcessingDialog) {
                    showProcessingImgIndicatorWithPromise().then(function () {
                        dti.oasis.grid.selectRowByIndex(gridId, rowIndex);

                        var rowId = dti.oasis.grid.getRowIdByRowIndex(gridId, rowIndex);

                        if (window.handleOnSelectRowAsyncComplete) {
                            window.handleOnSelectRowAsyncComplete(gridId, rowId);
                        }
                        hideProcessingImgIndicator();
                        defer.resolve();
                    });
                } else {
                    this.selectRowByIndex(gridId, rowIndex);
                    defer.resolve()
                }

                return defer.promise();
            },

            selectRowByJqxRowIndexWithProcessingDlg: function (gridId, jqxRowIndex) {
                var defer = $.Deferred();
                var needProcessingDialog = false;
                if (window["shouldDisplayProcessingDlgOnRowSelect"]) {
                    needProcessingDialog = window["shouldDisplayProcessingDlgOnRowSelect"](gridId);
                }

                if (needProcessingDialog) {
                    showProcessingImgIndicatorWithPromise().then(function () {
                        dti.oasis.grid.selectRowByJqxRowIndex(gridId, jqxRowIndex);

                        var rowId = dti.oasis.grid.getRowIdByJqxRowIndex(gridId, jqxRowIndex);

                        if (window.handleOnSelectRowAsyncComplete) {
                            window.handleOnSelectRowAsyncComplete(gridId, rowId);
                        }
                        hideProcessingImgIndicator();
                        defer.resolve();
                    });
                } else {
                    this.selectRowByJqxRowIndex(gridId, jqxRowIndex);
                    defer.resolve()
                }

                return defer.promise();
            },

            selectAll: function (gridId) {
                // TODO Change the select column name to be configure able.
                var grid = $("#".concat(gridId));
                var checked = $("#".concat(gridId, "_chkCSELECT_ALL")).hasClass("jqx-checkbox-check-checked");

                if (!checked) {
                    $("#".concat(gridId, "_chkCSELECT_ALL")).addClass("jqx-checkbox-check-checked");
                }

                if (this.hasSelectIndColumnName(gridId)) {
                    var selectIndColumnName = this.getSelectIndColumnName(gridId);
                    var rowCount = grid.jqxGrid('getdatainformation').rowscount;
                    for (var i = 0; i < rowCount; i++) {
                        var fnIsCellEditable = dti.oasis.grid.getCustomColumnOption(gridId, selectIndColumnName, "isCellEditable");

                        if (fnIsCellEditable == null || fnIsCellEditable(grid.jqxGrid("getrowboundindex", i))) {
                            grid.jqxGrid("setcellvalue", grid.jqxGrid("getrowboundindex", i), selectIndColumnName, true, false);
                        }
                    }
                }

                // grid.jqxGrid("refresh");
                dti.oasis.grid.selectFirstRowInGrid(gridId);
            },

            deselectAll: function (gridId) {
                // TODO Change the select column name to be configure able.
                var grid = $("#".concat(gridId));
                var checked = $("#".concat(gridId, "_chkCSELECT_ALL")).hasClass("jqx-checkbox-check-checked");

                if (checked) {
                    $("#".concat(gridId, "_chkCSELECT_ALL")).removeClass("jqx-checkbox-check-checked");
                }

                if (this.hasSelectIndColumnName(gridId)) {
                    var selectIndColumn = this.getSelectIndColumnName(gridId);

                    var selectIndColConfig = dti.oasis.grid.getColumnConfig(gridId, dti.oasis.grid.getActualColumnName(gridId, selectIndColumn));

                    if (selectIndColConfig.visible && selectIndColConfig.displayType == "checkbox") {
                        var rowCount = grid.jqxGrid('getdatainformation').rowscount;
                        for (var i = 0; i < rowCount; i++) {
                            grid.jqxGrid("setcellvalue", grid.jqxGrid("getrowboundindex", i), selectIndColumn, false, false);
                        }
                    }
                }

                // grid.jqxGrid("refresh");
                dti.oasis.grid.selectFirstRowInGrid(gridId);
            },

            sort: function (gridId, field, order) {
                var selector = "#".concat(gridId);
                var grid = $(selector);
                var sortDirection = "asc";

                var sortInfo = grid.jqxGrid("getsortinformation");
                var sortColumn = sortInfo.sortcolumn;

                if (typeof order == "undefined") {
                    if (sortColumn != "" && sortColumn == field) {
                        if (sortInfo.sortdirection.ascending) {
                            sortDirection = "desc";
                        }
                    }
                } else {
                    if (order == "desc") {
                        sortDirection = "desc";
                    }
                }
                dti.oasis.grid.setProperty(gridId, "sorting", true);
                dti.oasis.grid.setProperty(gridId, "sortingDeferredObj", $.Deferred());

                grid.jqxGrid('sortby', field, sortDirection);
            },

            syncDetailFieldToGrid: function (fieldId) {
                var field;
                if (typeof fieldId === "string") {
                    field = getSingleObject(fieldId);
                } else {
                    field = fieldId;
                    fieldId = fieldId.name || fieldId.id;
                }

                var dataSrc = $(field).data("dtiDatasrc");
                var dataField = $(field).data("dtiDatafld");

                if (!dti.oasis.string.isEmpty(dataSrc) && !dti.oasis.string.isEmpty(dataField)) {
                    // Check if the grid have the column.
                    var columnName = dti.oasis.grid.getActualColumnName(dataSrc.substring(1, dataSrc.length - 1), dataField);
                    if (columnName !== null) {
                        var formatter = dti.oasis.field.fieldFormat.getFieldFormat(fieldId);
                        var gridId = dataSrc.substring(1, dataSrc.length - 1);
                        var currentJqxRowIndex = window[dataSrc.substring(1)]._protected._currentJqxRowIndex;

                        if (dti.oasis.string.endsWith(fieldId, "_DISP_ONLY")) {
                            dti.oasis.grid.setGridCellValue(gridId, currentJqxRowIndex, columnName, formatter.formattedValue());
                        } else {
                            if (field.type == "checkbox") {
                                //Set checked value as "-1" and unchecked value as "0" to be backward compatible with IE data island.
                                if (dti.oasis.string.isEmpty(formatter.unformattedValue()) || formatter.unformattedValue() == "0") {
                                    dti.oasis.grid.setGridCellValue(gridId, currentJqxRowIndex, columnName, "0");
                                } else {
                                    dti.oasis.grid.setGridCellValue(gridId, currentJqxRowIndex, columnName, "-1");
                                }
                            } else {
                                if (this.hasDisplayOnlyColumn(gridId, dataField)) {
                                    dti.oasis.grid.setGridCellValue(gridId, currentJqxRowIndex, columnName, formatter.formattedValue());
                                } else {
                                    dti.oasis.grid.setGridCellValue(gridId, currentJqxRowIndex, columnName, formatter.unformattedValue());
                                }
                            }
                        }
                    }
                }
            },

            syncCellValueToDetailField: function(gridId, columnName) {
                var rowIndex = window[gridId.concat("1")].recordset("@index").value;
                var jqxGridRowId = this.getJqxRowIdByRowIndex(gridId, rowIndex);
                var jqxRowIndex = this.getJqxRowIndexByJqxRowId(gridId, jqxGridRowId);

                //TODO - as per kyle
                //In some case the data-dti-datafld or columnName can be not in uppercase, so we have some codes to check
                // if a column is in grid by converting the data field and column name to uppercase. So the jQuery selector may not work.
                //Performance can be improved by searching "dtiDatafld" first with the columnName being passed, however in claims system the column name
                //may not be in uppercase as other pages.
                $("[data-dti-datasrc='#" + gridId + "1']").each(function (index, element) {
                    var dataFld = $(element).data("dtiDatafld");

                    if (columnName.toUpperCase() === dataFld.toUpperCase()) {
                        dti.oasis.grid._protected._processSyncCellValueToDetailField(gridId, jqxRowIndex, element);
                    }
                });
            },

            syncCellValueToDetailFieldsForSelectedRow: function (gridId) {
                var rowIndex = window[gridId.concat("1")].recordset("@index").value;
                var jqxGridRowId = this.getJqxRowIdByRowIndex(gridId, rowIndex);
                var jqxRowIndex = this.getJqxRowIndexByJqxRowId(gridId, jqxGridRowId);

                $("[data-dti-datasrc='#" + gridId + "1']").each(function (index, element) {
                    dti.oasis.grid._protected._processSyncCellValueToDetailField(gridId, jqxRowIndex, element);
                });
            },

            syncChangedValueForRowSelected: function (gridId, rowIndex) {
                // Check if there is an active text or text area detail field.
                var element = document.activeElement;
                if (element && element.type && (element.type == "text" || element.type == "textarea")) {
                    if ($(element).data("dtiDatasrc") &&
                        $(element).data("dtiDatasrc") == "#".concat(gridId, "1") &&
                        $(element).data("dtiDatafld") &&
                        dti.oasis.field.isFieldValueChanged(element)) {

                        var isChangedEventSuccess = dti.oasis.ui.dispatchEvent(document.activeElement, "change");

                        if (isChangedEventSuccess !== false) {
                            isChangedEventSuccess = true;
                        }

                        // Change the value of the current element to cancel the next onchange.
                        var tempValue = document.activeElement.value;
                        document.activeElement.value = tempValue + "__";
                        document.activeElement.value = tempValue;

                        return isChangedEventSuccess;
                    }
                }

                return true;
            },

            getCellHref: function (gridId, jqxRowIndex, columnName) {
                var gridInfo = dti.oasis.page.getGridInfo(gridId);
                var columnIndex = gridInfo.data.columnNames.indexOf(columnName);
                if (columnIndex > -1) {
                    var urlColumnName = "URL_".concat(columnIndex);
                    if (gridInfo.data.columnNames.indexOf(urlColumnName) > -1) {
                        var rowData = this.getRowDataByJqxRowIndex(gridId, jqxRowIndex);
                        return rowData[urlColumnName];
                    }
                }

                return null;
            },

            setCellHref: function (gridId, jqxRowIndex, columnName, href) {
                var gridInfo = dti.oasis.page.getGridInfo(gridId);
                var columnIndex = gridInfo.data.columnNames.indexOf(columnName);
                if (columnIndex > -1) {
                    var urlColumnName = "URL_".concat(columnIndex);
                    if (gridInfo.data.columnNames.indexOf(urlColumnName) > -1) {
                        var rowData = this.getRowDataByJqxRowIndex(gridId, jqxRowIndex);
                        rowData[urlColumnName] = href;
                    }
                }
            },

            getUrlColumn: function (gridId, columnName) {
                var gridInfo = dti.oasis.page.getGridInfo(gridId);
                var columnIndex = gridInfo.data.columnNames.indexOf(columnName);
                if (columnIndex > -1) {
                    var urlColumnName = "URL_".concat(columnIndex);
                    if (gridInfo.data.columnNames.indexOf(urlColumnName) > -1) {
                        return urlColumnName;
                    }
                }

                return null;
            },

            getCellEditableFunction: function (gridId, dataField) {
                var isRowEditableFunction = dti.oasis.grid.getCustomGridOption(gridId, "isRowEditable");
                var isCellEditableFunction = dti.oasis.grid.getCustomColumnOption(gridId, dataField, "isCellEditable");

                if (isRowEditableFunction || isCellEditableFunction) {
                    return function (jqxRowIndex, dataField) {
                        var editable = true;

                        if (isCellEditableFunction) {
                            editable = isCellEditableFunction(jqxRowIndex, dataField)
                        } else if (isRowEditableFunction) {
                            editable = isRowEditableFunction(jqxRowIndex, dataField);
                        }

                        return editable;
                    };
                }

                return null;
            },

            getCellValue: function (gridId, rowId, columnName) {
                var rowIndex = this.getRowIndexByRowId(gridId, rowId);
                return this.getCellValueByRowIndex(gridId, rowIndex, columnName);
            },

            getCellValueByRowIndex: function (gridId, rowIndex, columnName) {
                var rowData = this.getRowDataByRowIndex(gridId, rowIndex);
                var actualColumnName = this.getActualColumnName(gridId, columnName);

                if (rowData.hasOwnProperty(actualColumnName) && rowData[actualColumnName] != null) {
                    return rowData[actualColumnName];
                } else {
                    return "";
                }
            },

            setCellFocus: function (gridId, rowId, columnName) {
                // This should only be called if jqxGrid unless modified
                var rowIndex = this.getJqxRowIndexByRowId(gridId, rowId);
                this.setCellFocusByRowIndex(gridId, rowIndex, columnName);
            },

            setCellFocusByRowDisplayIndex: function (gridId, jqxRowDisplayIndex, columnName) {
                var rowIndex = jqxRowDisplayIndex; // if called by old grid, this is really TR index
                if (dti.oasis.page.useJqxGrid()) {
                    rowIndex = this.getRowIndexByJqxRowDisplayIndex(gridId,jqxRowDisplayIndex);
                }
                this.setCellFocusByRowIndex(gridId, rowIndex, columnName);
            },

            setCellFocusByRowIndex: function (gridId, rowIndex, columnName) {
                if (dti.oasis.page.useJqxGrid()) {
                    // rowIndex is jqxRowIndex
                    this.selectRowByJqxRowIndex(gridId, rowIndex);
                    $("#"+gridId).jqxGrid('begincelledit', rowIndex, columnName);
                } else {
                    // rowIndex is from TR index
                    var rowId = $( "[DataFld='ID']" )[rowIndex].value;
                    selectRow(gridId,rowId);
                    $( "[DataFld='"+columnName+"']" )[rowIndex].focus();
                }
            },

            getColumnConfig: function (gridId, columnName) {
                var columnsConfig = this.getColumnsConfig(gridId);

                for (var i = 0; i < columnsConfig.length; i++) {
                    if (columnsConfig[i]["id"] == columnName) {
                        return columnsConfig[i];
                    }
                }

                return null;
            },

            getColumnsConfig: function (gridId) {
                var gridInfo = dti.oasis.page.getGridInfo(gridId);
                return gridInfo["config"]["columnsConfig"] || [];
            },

            getCompiledColumnConfig: function (gridId, columnName) {
                var gridInfo = dti.oasis.page.getGridInfo(gridId);
                var compiledColumnsConfig = gridInfo["compiledConfig"]["columns"] || [];

                for (var i = 0; i < compiledColumnsConfig.length; i++) {
                    if (compiledColumnsConfig[i]["datafield"] == columnName) {
                        return compiledColumnsConfig[i];
                    }
                }

                return null;
            },

            getColumnLabel: function (gridId, columnName) {
                var colLabel = "";
                if (dti.oasis.page.useJqxGrid()) {
                    var columnConfig = this.getCompiledColumnConfig(gridId, columnName);
                    if (columnConfig != null) {
                        colLabel = columnConfig.text;
                    }
                } else {
                    try {
                        colLabel = getObject("H" + columnName).innerText;
                    } catch(e) {
                        colLabel = "";
                    }
                }

                return colLabel;
            },

            getColumnDataType: function (gridId, columnName) {
                var columnConfig = this.getColumnConfig(gridId, columnName);

                if (columnConfig != null) {
                    return columnConfig.dataType || "ST";
                }

                return null;
            },

            getColumnDisplayType: function (gridId, columnName) {
                var columnConfig = this.getCompiledColumnConfig(gridId, columnName);

                if (columnConfig != null && columnConfig.hasOwnProperty("columntype")) {
                    return columnConfig.columntype;
                }

                return null;
            },

            getColumnNames: function (gridId) {
                return dti.oasis.page.getGridInfo(gridId)["data"]["columnNames"];
            },

            getCustomGridOptions: function (gridId) {
                var customPageOptions = dti.oasis.page.getCustomPageOptions();

                if (customPageOptions) {
                    return customPageOptions["#" + gridId] || null;
                }

                return null;
            },

            getCustomGridOption: function (gridId, optionName) {
                var customGridOptions = this.getCustomGridOptions(gridId);

                if (customGridOptions !== null && customGridOptions.hasOwnProperty(optionName)) {
                    return customGridOptions[optionName];
                }

                return null;
            },

            getCustomColumnsOptions: function (gridId) {
                var gridOptions = this.getCustomGridOptions(gridId);

                if (gridOptions != null) {
                    return gridOptions["columns"] || null;
                }

                return null;
            },

            getCustomColumnOptions: function (gridId, columnName) {
                var columnsOptions = this.getCustomColumnsOptions(gridId);

                if (columnsOptions != null) {
                    return columnsOptions[columnName] || null;
                }

                return null;
            },

            getCustomColumnOption: function (gridId, columnName, optionName) {
                var columnOptions = this.getCustomColumnOptions(gridId, columnName);

                if (columnOptions !== null && columnOptions.hasOwnProperty(optionName)) {
                    return columnOptions[optionName];
                }

                return null;
            },

            getActualColumnName: function (gridId, columnName) {
                var uppercaseColumnName = columnName.toUpperCase();
                var upperCaseColumnNameMap = this.getUpperCaseColumnNameMap(gridId);

                if (upperCaseColumnNameMap.hasOwnProperty(uppercaseColumnName)) {
                    return upperCaseColumnNameMap[uppercaseColumnName];
                }

                var uppercaseAttributeName = "@".concat(uppercaseColumnName);
                if (upperCaseColumnNameMap.hasOwnProperty(uppercaseAttributeName)) {
                    return upperCaseColumnNameMap[uppercaseAttributeName];
                }

                return null;
            },

            getUpperCaseColumnNameMap: function (gridId) {
                var gridData = dti.oasis.page.getGridInfo(gridId)["data"];

                if (gridData.hasOwnProperty("upperCaseColumnNameMap")) {
                    return gridData["upperCaseColumnNameMap"];
                } else {
                    var columnNameMap = {};
                    var columnNames = dti.oasis.page.getGridInfo(gridId)["data"]["columnNames"];

                    for (var i = 0; i < columnNames.length; i++) {
                        var columnName = columnNames[i];

                        columnNameMap[columnName.toUpperCase()] = columnName;
                    }

                    gridData["upperCaseColumnNameMap"] = columnNameMap;

                    return columnNameMap;
                }
            },

            getDataIsland: function (gridId) {
                if (window[gridId.concat("1")]) {
                    return window[gridId.concat("1")];
                }

                return null;
            },

            getDefaultFilterObj: function () {
                return dti.oasis.filter.compile("DISPLAY_IND = \"Y\" and UPDATE_IND != \"D\" and UPDATE_IND != \"I-D\"");
            },

            getGridChanges: function (gridId) {
                var displayInd;
                var displayRows = "";
                var nonDisplayRows = "";
                var defaultID = "-9999";

                var rawData = this.getRawData(gridId);
                var columnNames = this.getColumnNames(gridId);

                if (rawData.length == 0) {
                    var result = "<ROW id='" + defaultID + "'>";

                    //just get the column names with empty values.
                    for (var j = 0; j < columnNames.length; j++) {
                        var columnName = columnNames[j];
                        nodeValue = "";
                        if (columnName.indexOf("@") != 0) {
                            if (columnName == "UPDATE_IND" || columnName == "DISPLAY_IND" || columnName == "EDIT_IND") {
                                nodeValue = "N";
                            }
                            result += "<" + columnName + ">" + nodeValue + "</" + columnName + ">";
                        }
                    }

                    result+= "</ROW>";

                    return "<ROWS>" + result + "</ROWS>";
                }

                for (var i = 0; i < rawData.length; i++) {
                    var rowData = rawData[i];

                    var ID = rowData["@id"];
                    var updateInd = rowData["UPDATE_IND"];

                    if (ID != "-9999" && updateInd != "I-D") {
                        var result = '<ROW id="' + ID + '">';

                        for (var j = 0; j < columnNames.length; j++) {
                            var columnName = columnNames[j];

                            if (columnName.indexOf("@") != 0) {
                                var nodeValue;

                                if (rowData.hasOwnProperty(columnName)) {
                                    nodeValue = rowData[columnName];
                                } else {
                                    nodeValue = "";
                                }

                                nodeValue = encodeXMLChar(nodeValue);

                                if (moneyFormatPattern.test(nodeValue)) {
                                    // deal the case the negative number
                                    if (paraPattern.test(nodeValue)) {
                                        nodeValue = nodeValue.replace(/\(/g, '');
                                        nodeValue = nodeValue.replace(/\)/g, '');
                                        nodeValue = "-" + nodeValue;
                                    }
                                    // remove '$"
                                    nodeValue = nodeValue.replace(/\$/g, '');
                                    // remove ","
                                    nodeValue = nodeValue.replace(/,/g, '');
                                } else if (percentagePattern.test(nodeValue)) {
                                    nodeValue = convertPctToNumber(nodeValue);
                                }

                                result += "<" + columnName + ">" + nodeValue + "</" + columnName + ">";

                                if (columnName == "DISPLAY_IND")
                                    displayInd = nodeValue;
                            }
                        }

                        result +=  "</ROW>";

                        if (displayInd == "Y")
                            displayRows += result;
                        else
                            nonDisplayRows += result;
                    }
                }

                return "<ROWS>" + displayRows + nonDisplayRows + "</ROWS>";
            },

            getGridChangesByColIndex: function (gridId) {
                var result = "<RS>";
                var rawData = this.getRawData(gridId);
                var updateColumnNames = this.getUpdateColumnNames(gridId);

                for (var i = 0; i < rawData.length; i++) {
                    var rowData = rawData[i];
                    var updateInd = rowData["UPDATE_IND"];

                    if (updateInd == "I" || updateInd == "Y" || updateInd == "D") {
                        // ID
                        result += '<R id="' + rowData["@id"] + '"';

                        for (var j = 0; j < updateColumnNames.length; j++) {
                            var nodeName = updateColumnNames[j];
                            var nodeValue = "";
                            if (rowData.hasOwnProperty(nodeName) && !dti.oasis.string.isEmpty(rowData[nodeName])) {
                                nodeValue = rowData[nodeName];
                            }

                            nodeValue = encodeXMLChar(nodeValue);

                            if (moneyFormatPattern.test(nodeValue)) {
                                nodeValue = unformatMoneyStrValAsStr(nodeValue);
                            }

                            result += " c" + j + '="' + nodeValue + '"';
                        }

                        result += ' update_ind="' + updateInd + '" />';
                    }
                }

                result += "</RS>";
                return result;
            },

            getGridChangesOnly: function (gridId) {
                var displayInd;
                var displayRows = "";
                var nonDisplayRows = "";

                var rawData = this.getRawData(gridId);
                var columnNames = this.getColumnNames(gridId);

                for (var i = 0; i < rawData.length; i++) {
                    var rowData = rawData[i];

                    var ID = rowData["@id"];
                    var updateInd = rowData["UPDATE_IND"];

                    if (ID != "-9999" && (updateInd == "I" || updateInd == "Y" || updateInd == "D")) {
                        var result = '<ROW id="' + ID + '">';

                        for (var j = 0; j < columnNames.length; j++) {
                            var columnName = columnNames[j];

                            if (columnName.indexOf("@") != 0) {
                                var nodeValue;

                                if (rowData.hasOwnProperty(columnName)) {
                                    nodeValue = rowData[columnName];
                                } else {
                                    nodeValue = "";
                                }

                                nodeValue = encodeXMLChar(nodeValue);

                                if (moneyFormatPattern.test(nodeValue)) {
                                    // deal the case the negative number
                                    if (paraPattern.test(nodeValue)) {
                                        nodeValue = nodeValue.replace(/\(/g, '');
                                        nodeValue = nodeValue.replace(/\)/g, '');
                                        nodeValue = "-" + nodeValue;
                                    }
                                    // remove '$"
                                    nodeValue = nodeValue.replace(/\$/g, '');
                                    // remove ","
                                    nodeValue = nodeValue.replace(/,/g, '');
                                } else if (percentagePattern.test(nodeValue)) {
                                    nodeValue = convertPctToNumber(nodeValue);
                                }

                                result += "<" + columnName + ">" + nodeValue + "</" + columnName + ">";

                                if (columnName == "DISPLAY_IND")
                                    displayInd = nodeValue;
                            }
                        }

                        result +=  "</ROW>";

                        if (displayInd == "Y")
                            displayRows += result;
                        else
                            nonDisplayRows += result;
                    }
                }

                return "<ROWS>" + displayRows + nonDisplayRows + "</ROWS>";
            },

            getUpdateColumnNames: function (gridId) {
                // TODO Change it to be cached.
                var modelRow = this.getModelRow(gridId);
                var updateCols = modelRow["@col"].split(",");
                var updateColumnNames = [];
                var columnNames = this.getColumnNames(gridId);

                for (var i = 0; i < updateCols.length; i++) {
                    var columnIndex = parseInt(updateCols[i]);
                    updateColumnNames[updateColumnNames.length] = columnNames[columnIndex];
                }

                return updateColumnNames;
            },

            getLastIndex: function (gridId) {
                var rawData = this.getRawData(gridId);
                var lastIndex = 0;

                for (var i = 0; i < rawData.length; i++) {
                    var index = parseInt(rawData[i]["@index"]);

                    if (index > lastIndex) {
                        lastIndex = index;
                    }
                }

                return lastIndex;
            },

            getLastInsertedRowId: function (gridId) {
                //var rawData = this.getRawData(gridId);
                //var temp_id = null;

                var __lastInsertedId = this.getProperty(gridId, "lastInsertedId");
                // if we've already inserted a row, then decrement the last id so we sequence the rows properly
                //if (__lastInsertedId != null && parseInt(__lastInsertedId) < 0)
                //    temp_id = parseInt(__lastInsertedId);
                //else //first time insert -3000, now there's a nice looking number!
                //{
                //    temp_id = 0;
                //    for (var i = 0; i < rawData.length; i++) {
                //        var temRowId = parseInt(rawData[i]["@id"]);
                //
                //        if (temRowId < temp_id && temRowId != -9999) {
                //            temp_id = temRowId;
                //        }
                //    }
                //}

                return __lastInsertedId;
            },

            getModelRow: function (gridId) {
                var gridInfo = dti.oasis.page.getGridInfo(gridId);
                return gridInfo.data["MODEL"];
            },

            getPageCount: function (gridId) {
                return $("#".concat(gridId)).jqxGrid("getpaginginformation").pagescount;
            },

            getPageNo: function (gridId) {
                return $("#".concat(gridId)).jqxGrid("getpaginginformation").pagenum + 1;
            },

            getPageSize: function (gridId) {
                return $("#".concat(gridId)).jqxGrid("getpaginginformation").pagesize;
            },

            /**
             * Get propery of a grid.
             * @param gridId
             * @param propName
             * @param defaultValue
             * isSelectingRow - If the grid is selecting a row.
             * selectedRowId - The current selected row id.
             * selectedTableRowNo - The row number of a selected row in a the table of a grid.
             * autoSelectFirstRow - true/false indicator - whether to select the 1st row in the grid automatically. Default is true.
             * @returns {*}
             */
            getProperty: function (gridId, propName, defaultValue) {
                var propertyValue = undefined;
                if (isMultiGridSupported || isUndefined(window[propName])) {
                    var gridInfo = dti.oasis.page.getGridInfo(gridId);
                    try {
                        propertyValue = gridInfo.properties[propName];
                    } catch (e) {
                    }
                } else {
                    propertyValue = window[propName];
                }
                return (isUndefined(propertyValue) ? (isUndefined(defaultValue) ? "" : defaultValue) : propertyValue);
            },

            setProperty: function (gridId, propName, value) {
                if (dti.oasis.string.isEmpty(gridId) ||
                    dti.oasis.string.isEmpty(propName)) {
                    return;
                }

                if (isMultiGridSupported) {
                    var gridInfo = dti.oasis.page.getGridInfo(gridId);

                    if (gridInfo == null) {
                        gridInfo = {};
                        dti.oasis.page.setGridInfo(gridId, gridInfo);
                    }

                    var gridProperties = null;
                    if (gridInfo.hasOwnProperty("properties")) {
                        gridProperties = gridInfo.properties;
                    } else {
                        gridProperties = {};
                        gridInfo.properties = gridProperties;
                    }

                    gridProperties[propName] = value;
                }
                else {
                    window[propName] = value;
                }
            },

            showLoadingImage: function(gridId) {
                $("#".concat(gridId)).jqxGrid("showloadelement");
            },

            hideLoadingImage: function(gridId) {
                $("#".concat(gridId)).jqxGrid("hideloadelement");
            },

            removeProperty: function (gridId, propName) {
                if (isMultiGridSupported || isUndefined(window[propName])) {
                    var gridInfo = dti.oasis.page.getGridInfo(gridId);

                    if (gridInfo != null && gridInfo.hasOwnProperty("properties") && gridInfo.properties.hasOwnProperty(propName)) {
                        delete gridInfo.properties[propName]
                    }

                } else {
                    delete window[propName];
                }
                return;
            },

            getRawData: function (gridId) {
                var gridInfo = dti.oasis.page.getGridInfo(gridId);

                if (gridInfo != null && gridInfo["data"] && gridInfo["data"]["rawData"]) {
                    return gridInfo["data"]["rawData"];
                }

                return null;
            },

            getFilteredRawData: function (gridId) {
                var gridInfo = dti.oasis.page.getGridInfo(gridId);

                if (gridInfo["data"]["filteredRawData"]) {
                    return gridInfo["data"]["filteredRawData"];
                }

                if (gridInfo["data"]["rawData"]) {
                    return gridInfo["data"]["rawData"];
                }

                return null;
            },

            getDisplayingRawData: function (gridId) {
                var rows = [];
                var recordCount = this.getRecordCount(gridId);

                for (var i = 0; i < recordCount; i++) {
                    rows[rows.length] = this.getRowDataByJqxDisplayIndex(gridId, i);
                }

                return rows;
            },

            getRecordCount: function (gridId) {
                return $("#".concat(gridId)).jqxGrid("getdatainformation")["rowscount"];
            },

            getRowData: function (gridId, rowId) {
                var rawData = this.getRawData(gridId);

                for (var i = 0; i < rawData.length; i++) {
                    if (rawData[i]["@id"] == rowId) {
                        return rawData[i];
                    }
                }

                return null;
            },

            getRowDataById: function (gridId, rowId) {
                if (rowId == null) {
                    return null;
                }

                return this.getRowData(gridId, rowId);
            },

            getRowDataByRowIndex: function (gridId, rowIndex) {
                if (rowIndex == null || rowIndex == -1) {
                    return null;
                }

                return this.getRawData(gridId)[rowIndex];
            },

            getRowDataByJqxRowIndex: function (gridId, jqxRowIndex) {
                if (jqxRowIndex == null || jqxRowIndex == -1) {
                    return null;
                }

                var rowIndex = this.getRowIndexByJqxRowIndex(gridId, jqxRowIndex);

                return this.getRawData(gridId)[rowIndex];
            },

            getRowDataByJqxDisplayIndex: function (gridId, jqxDisplayIndex) {
                if (jqxDisplayIndex == null || jqxDisplayIndex == -1) {
                    return null;
                }

                var rowIndex = this.getRowIndexByJqxRowDisplayIndex(gridId, jqxDisplayIndex);
                return this.getRowDataByRowIndex(gridId, rowIndex);
            },

            getRowIdByRowData: function (gridId, rowData) {
                return rowData["@id"];
            },

            getRowIndexByRowData: function (gridId, rowData) {
                return rowData["@index"];
            },

            getRowJqxRowIdByRowData: function (gridId, rowData) {
                return rowData["@index"].toString();
            },

            /**
             * Get rowId of a rowData by rowIndex.
             *
             * The following are the details about rowId and rowIndex.
             * rawData: It represents all records of a grid. It contains the filtered and deleted rows.
             *
             * rowData: It's a data record.
             *
             * rowId: It's the id property (@id) of a row data.
             *
             * rowIndex: It's the index of a row in rawData. Same with the @index property of a rowData.
             *
             * jqxRowId: It's the row id of a jxGrid record. Because the rowId of a rowData could be duplicated with
             * another row, use rowIndex as jqxRowId.
             *
             * jqxRowIndex: It's the row bound index of a jqxGrid record. Because the records in jqxGrid could be a
             * subset of the rawData if some records are filtered or deleted, the jqxRowIndex doesn't equal to rowIndex.
             *
             * jqxRowDisplayIndex: It's the row display index of a jqxGrid record. Because a jqxGrid could be filtered
             * or sorted, the jqxRowDisplayIndex could be different with jqxRowIndex.
             *
             * @param gridId
             * @param rowIndex The row index of a rowData.
             * @returns {*} The row id of a rowData.
             */
            getRowIdByRowIndex: function (gridId, rowIndex) {
                if (rowIndex == null || rowIndex == -1) {
                    return null;
                }

                // Get rowData and get id property.
                var rowData = this.getRowDataByRowIndex(gridId, rowIndex);
                return rowData["@id"];
            },

            /**
             * Get rowId of a rowData by the rowId of a jqxGrid record.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param jqxRowId The row id of a jqxGrid record.
             * @returns {*}
             */
            getRowIdByJqxRowId: function (gridId, jqxRowId) {
                if (jqxRowId == null) {
                    return '';
                }

                // jqxGridRowId ==> rowIndex ==> rowId
                var rowIndex = this.getRowIndexByJqxRowId(gridId, jqxRowId);
                return this.getRowIdByRowIndex(gridId, rowIndex);
            },

            /**
             * Get rowId of a rowData by the rowIndex of a jqxGrid record.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param jqxRowIndex The row bound index of a jqxGrid record.
             * @returns {*}
             */
            getRowIdByJqxRowIndex: function (gridId, jqxRowIndex) {
                if (jqxRowIndex == null || jqxRowIndex == -1) {
                    return null;
                }

                // jqxRowIndex ==> rowIndex ==> rowId
                var rowIndex = this.getRowIndexByJqxRowIndex(gridId, jqxRowIndex);
                return this.getRowIdByRowIndex(gridId, rowIndex);
            },

            /**
             * Get rowId of a rowData by the rowDisplayIndex of a jqxGrid record.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param jqxRowDisplayIndex The display index of a jqxGrid record.
             * @returns {*}
             */
            getRowIdByJqxRowDisplayIndex: function (gridId, jqxRowDisplayIndex) {
                if (jqxRowDisplayIndex == null || jqxRowDisplayIndex == -1) {
                    return null;
                }

                // jqxRowDisplayIndex ==> jqxRowIndex (==> jqxRowId ==> rowIndex) ==> rowId
                var jqxRowIndex = this.getJqxRowIndexByJqxRowDisplayIndex(gridId, jqxRowDisplayIndex);
                return this.getRowIdByJqxRowIndex(gridId, jqxRowIndex);
            },

            /**
             * Get rowIndex of a rowData by rowId.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param rowId
             * @returns {*}
             */
            getRowIndexByRowId: function (gridId, rowId) {
                if (rowId == null) {
                    return null;
                }

                var rowData = this.getRowDataById(gridId, rowId);
                if (rowData == null) {
                    return null;
                }

                // rowId ==> rowData ==> rowIndex
                return rowData["@index"];
            },

            /**
             * Get rowIndex of a rowData by the rowId of a jqxGrid record.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param jqxRowId
             * @returns {*}
             */
            getRowIndexByJqxRowId: function (gridId, jqxRowId) {
                if (jqxRowId == null) {
                    return null;
                }

                // jqxRowId == rowIndex
                return parseInt(jqxRowId);
            },

            /**
             * Get rowIndex of a rowData by the rowIndex of a jqxGrid record.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param jqxRowIndex
             * @returns {*}
             */
            getRowIndexByJqxRowIndex: function (gridId, jqxRowIndex) {
                if (jqxRowIndex == null || jqxRowIndex == -1) {
                    return null;
                }

                // jqxRowIndex ==> jqxRowId ==> rowIndex
                var jqxRowId = this.getJqxRowIdByJqxRowIndex(gridId, jqxRowIndex);
                return this.getRowIndexByJqxRowId(gridId, jqxRowId);
            },

            /**
             * Get rowIndex of a rowData by the rowDisplayIndex of a jqxGrid record.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param jqxRowDisplayIndex
             * @returns {*}
             */
            getRowIndexByJqxRowDisplayIndex: function (gridId, jqxRowDisplayIndex) {
                if (jqxRowDisplayIndex == null || jqxRowDisplayIndex == -1) {
                    return null;
                }

                // jqxRowDisplayIndex ==> jqxRowIndex (==> jqxRowId) == rowIndex
                var jqxRowIndex = this.getJqxRowIndexByJqxRowDisplayIndex(gridId, jqxRowDisplayIndex);
                return this.getRowIndexByJqxRowIndex(gridId, jqxRowIndex);
            },

            /**
             * Get the rowId of a jqxGrid record by the rowId of a rowData.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param rowId
             * @returns {*}
             */
            getJqxRowIdByRowId: function (gridId, rowId) {
                if (rowId == null) {
                    return null;
                }

                // rowId ==> rowIndex ==> jqxRowId
                var rowIndex = this.getRowIndexByRowId(gridId, rowId);
                return this.getJqxRowIdByRowIndex(gridId, rowIndex);
            },

            /**
             * Get the rowId of a jqxGrid record by the rowIndex of a rowData.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param rowIndex
             * @returns {*}
             */
            getJqxRowIdByRowIndex: function (gridId, rowIndex) {
                if (rowIndex == null || rowIndex == -1) {
                    return null;
                }

                // rowIndex == jqxRowId
                var jqxRowId = rowIndex.toString();

                if ($("#".concat(gridId)).jqxGrid("getrowboundindexbyid", jqxRowId) != -1) {
                    return jqxRowId;
                }

                return null;
            },

            /**
             * Get the rowId of a jqxGrid record by rowIndex.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param jqxRowIndex
             * @returns {*}
             */
            getJqxRowIdByJqxRowIndex: function (gridId, jqxRowIndex) {
                if (jqxRowIndex == null || jqxRowIndex == -1) {
                    return null;
                }

                // jqxRowIndex ==> jqxRowId
                return $("#".concat(gridId)).jqxGrid("getrowid", jqxRowIndex);
            },

            /**
             * Get the rowId of a jqxGrid record by rowDisplayIndex.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param jqxRowDisplayIndex
             * @returns {*}
             */
            getJqxRowIdByJqxRowDisplayIndex: function (gridId, jqxRowDisplayIndex) {
                if (jqxRowDisplayIndex == null || jqxRowDisplayIndex == -1) {
                    return null;
                }

                // jqxRowDisplayIndex ==> jqxRowIndex ==> jqxRowId
                var jqxRowIndex = this.getJqxRowIndexByJqxRowDisplayIndex(gridId, jqxRowDisplayIndex);
                return this.getJqxRowIdByJqxRowIndex(gridId, jqxRowIndex);
            },

            /**
             * Get the rowId of a jqxGrid record by the rowId of a rowData.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param rowId
             * @returns {*}
             */
            getJqxRowIndexByRowId: function (gridId, rowId) {
                if (rowId == null) {
                    return null;
                }

                // rowId ==> rowIndex (==> jqxRowId) ==> jqxRowIndex
                var jqxRowId = this.getJqxRowIdByRowId(gridId, rowId);
                return this.getJqxRowIndexByJqxRowId(gridId, jqxRowId);
            },

            /**
             * Get the rowIndex of a jqxGrid record by the rowIndex of a rowData.
             *
             * @param gridId
             * @param rowIndex
             * @returns {*}
             */
            getJqxRowIndexByRowIndex: function (gridId, rowIndex) {
                if (rowIndex == null || rowIndex == -1) {
                    return null;
                }

                // rowIndex ==> jqxRowId ==> jqxRowIndex
                var jqxRowId = this.getJqxRowIdByRowIndex(gridId, rowIndex);
                return this.getJqxRowIndexByJqxRowId(gridId, jqxRowId);
            },

            /**
             * Get the rowIndex of a jqxGrid record by rowId.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param jqxRowId
             * @returns {*}
             */
            getJqxRowIndexByJqxRowId: function (gridId, jqxRowId) {
                if (jqxRowId == null) {
                    return -1;
                }

                // jqxRowId ==> jqxRowIndex
                return parseInt($("#".concat(gridId)).jqxGrid("getrowboundindexbyid", jqxRowId));
            },

            /**
             * Get the rowIndex of a jqxGrid record by rowDisplayIndex.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param jqxRowDisplayIndex
             * @returns {*}
             */
            getJqxRowIndexByJqxRowDisplayIndex: function (gridId, jqxRowDisplayIndex) {
                if (jqxRowDisplayIndex == null || jqxRowDisplayIndex == -1) {
                    return null;
                }

                // jqxRowDisplayIndex ==> jqxRowIndex
                return $("#".concat(gridId)).jqxGrid("getrowboundindex", jqxRowDisplayIndex);
            },

            /**
             * Get the rowDisplayIndex of a jqxGrid record by the rowId of a rowData.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param rowId
             * @returns {*}
             */
            getJqxRowDisplayIndexByRowId: function (gridId, rowId) {
                if (rowId == null) {
                    return null;
                }

                // rowId ==> rowIndex (==> jqxRowId ==> jqxRowIndex) ==> jqxRowDisplayIndex
                var rowIndex = this.getRowIndexByRowId(gridId, rowId);
                return this.getJqxRowDisplayIndexByRowIndex(gridId, rowIndex);
            },

            /**
             * Get the rowDisplayIndex of a jqxGrid record by the rowIndex of a rowData.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param rowIndex
             * @returns {*}
             */
            getJqxRowDisplayIndexByRowIndex: function (gridId, rowIndex) {
                if (rowIndex == null || rowIndex == -1) {
                    return null;
                }

                // rowIndex ==> jqxRowId (==> jqxRowIndex) ==> jqxRowDisplayIndex
                var jqxRowId = this.getJqxRowIdByRowIndex(gridId, rowIndex);
                return this.getJqxRowDisplayIndexByJqxRowId(gridId, jqxRowId);
            },

            /**
             * Get the rowDisplayIndex of a jqxGrid record by rowId.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param jqxRowId
             * @returns {*}
             */
            getJqxRowDisplayIndexByJqxRowId: function (gridId, jqxRowId) {
                if (jqxRowId == null) {
                    return null;
                }

                // jqxRowId ==> jqxRowIndex ==> jqxRowDisplayIndex
                var jqxRowIndex = this.getJqxRowIndexByJqxRowId(gridId, jqxRowId);
                return this.getJqxRowDisplayIndexByJqxRowIndex(gridId, jqxRowIndex);
            },

            /**
             * Get rowDisplayIndex of a jqxGrid record by rowIndex.
             *
             * See {@link dti.oasis.grid.getRowIdByRowIndex} for the details of rowId and rowIndex.
             *
             * @param gridId
             * @param jqxRowIndex
             * @returns {*}
             */
            getJqxRowDisplayIndexByJqxRowIndex: function (gridId, jqxRowIndex) {
                if (jqxRowIndex == null || jqxRowIndex == -1) {
                    return -1;
                }

                // jqxRowIndex ==> jqxRowDisplayIndex
                return parseInt($("#".concat(gridId)).jqxGrid("getrowdisplayindex", jqxRowIndex));
            },

            getSelectedDataArray: function (gridId) {
                var selectedDataArray = [];

                var rawData = this.getRawData(gridId);
                var gridInfo = dti.oasis.page.getGridInfo(gridId);
                var model = gridInfo.data["MODEL"];
                var selectIndColumnName = this.getSelectIndColumnName(gridId);

                for (var i = 0; i < rawData.length; i++) {
                    if (rawData[i].hasOwnProperty(selectIndColumnName) &&
                        rawData[i][selectIndColumnName] == "-1") {
                        var selectedRowData = [];
                        selectedDataArray.push(selectedRowData);
                        var j = 0;

                        selectedRowData[j] = rawData[i]["@id"];


                        for (var p in model) {
                            if (model.hasOwnProperty(p) && p.indexOf("@") != 0) {
                                j++;
                                if (rawData[i].hasOwnProperty(p)) {
                                    selectedRowData[j] = rawData[i][p];
                                } else {
                                    selectedRowData[j] = "";
                                }
                            }
                        }
                    }
                }

                return selectedDataArray;
            },

            getSelectedDataString: function (gridId, rowDelim, colDelim) {
                var selectedDataString = "";

                var rawData = this.getRawData(gridId);
                var gridInfo = dti.oasis.page.getGridInfo(gridId);
                var model = gridInfo.data["MODEL"];
                var selectIndColumnName = this.getSelectIndColumnName(gridId);

                for (var i = 0; i < rawData.length; i++) {
                    if (rawData[i].hasOwnProperty(selectIndColumnName) &&
                        rawData[i][selectIndColumnName] == "-1") {

                        selectedDataString = selectedDataString.concat(rawData[i]["@id"], colDelim);

                        for (var p in model) {
                            if (model.hasOwnProperty(p) && p.indexOf("@") != 0) {
                                if (rawData[i].hasOwnProperty(p)) {
                                    selectedDataString = selectedDataString.concat(rawData[i][p]);
                                }
                                selectedDataString = selectedDataString.concat(colDelim);
                            }
                        }

                        selectedDataString = selectedDataString.concat(rowDelim);
                    }
                }

                return selectedDataString;
            },

            getSelectedKeys: function (gridId) {
                var selectedKeys = [];
                var selectIndColumnName = this.getSelectIndColumnName(gridId);

                var selectedRows = window[gridId + "1"].documentElement.selectNodes("//ROW[" + selectIndColumnName + " = '-1']");
                for (var i = 0; i < selectedRows.length; i++) {
                    selectedKeys.push(selectedRows.item(i).getAttribute("id"));
                }

                return selectedKeys;
            },

            getSelectedKeysString: function (gridId, selectCol, rowDelim) {
                var selectedKeys = "";

                selectCol = selectCol || this.getSelectIndColumnName(gridId);
                rowDelim = rowDelim || ",";

                var selectedRows = window[gridId + "1"].documentElement.selectNodes("//ROW[" + selectCol + " = '-1']");
                for (var i = 0; i < selectedRows.length; i++) {
                    selectedKeys = selectedKeys.concat(selectedRows.item(i).getAttribute("id"), rowDelim);
                }

                return selectedKeys;
            },

            getSelectedRowId: function (gridId) {
                return this.getRowIdByJqxRowId(gridId, this.getSelectedJqxRowId(gridId));
            },

            getSelectedJqxRowId: function (gridId) {
                var jqxRowIndex = $("#".concat(gridId)).jqxGrid("selectedrowindex");

                return this.getJqxRowIdByJqxRowIndex(gridId, jqxRowIndex);
            },

            getSelectedJqxRowIndex: function (gridId) {
                return $("#".concat(gridId)).jqxGrid("selectedrowindex");
            },

            setSelectedRowId: function (gridId, rowId) {
                var dataIslandObj = dti.oasis.grid.getDataIsland(gridId);
                //dataIslandObj._protected._currentRowId = rowId;
                dataIslandObj.recordset.GotoRow(rowId);

                this.setProperty(gridId, "selectedRowId", rowId);

                currentlySelectedGridId = gridId;

                dti.oasis.page.setProperty("currentlySelectedGridId", gridId);
            },

            getSelectedRowIndex: function (gridId) {
                var jqxRowIndex = $("#".concat(gridId)).jqxGrid('getselectedrowindex');
                return this.getRowIndexByJqxRowIndex(gridId, jqxRowIndex);
            },

            setSelectedRowIndex: function (gridId, rowIndex) {
                var dataIslandObj = dti.oasis.grid.getDataIsland(gridId);
                //dataIslandObj._protected._currentRowId = rowId;
                dataIslandObj.recordset.GotoRowByIndex(rowIndex);

                var rowId = this.getRowIdByRowIndex(gridId, rowIndex);

                this.setProperty(gridId, "selectedRowId", rowId);
                this.setProperty(gridId, "selectedRowIndex", rowIndex);

                currentlySelectedGridId = gridId;

                dti.oasis.page.setProperty("currentlySelectedGridId", gridId);
            },

            getSortColumn: function (gridId) {
                var sortInfo = $("#".concat(gridId)).jqxGrid("getsortinformation");

                if (sortInfo != null && sortInfo["sortcolumn"] && (sortInfo["sortdirection"]["ascending"] || sortInfo["sortdirection"]["descending"])) {
                    return sortInfo["sortcolumn"];
                }

                return null;
            },

            getSortOrder: function (gridId) {
                var sortInfo  = $("#".concat(gridId)).jqxGrid("getsortinformation");

                if (sortInfo != null && sortInfo["sortdirection"]) {
                    var sortDirection  = sortInfo["sortdirection"];

                    if (sortDirection["ascending"]) {
                        return "+";
                    } else if (sortDirection["descending"]) {
                        return "-";
                    }
                }

                return null;
            },

            setCellValue: function (gridId, rowId, columnName, val, needSync) {
                var jqxRowIndex = null;

                // Check if the current column name is column in the grid.
                if (dti.oasis.grid.isGridDataField(gridId, columnName)) {
                    //jqxRowIndex will be -1 if the rowData is not in grid, like when UPDATE_IND is N.
                    jqxRowIndex = this.getJqxRowIndexByRowId(gridId, rowId);
                }

                if (jqxRowIndex != null && jqxRowIndex != -1) {
                    this.setGridCellValue(gridId, jqxRowIndex, columnName, val, needSync);
                } else {
                    this.setRowDataValue(gridId, rowId, columnName, val);
                }
            },

            //If sync detail value to grid cell, pass false as needSync, so we won't sync back to detail again,
            setGridCellValue: function (gridId, jqxRowIndex, columnName, val, needSync) {
                var unformattedValue = dti.oasis.grid.formatForSetCellValue.getFormatForSetCellValueFormatter(gridId, columnName).unformat(val);

                if (dti.oasis.grid.isColumnInGrid(gridId, columnName)) {
                    $("#".concat(gridId)).jqxGrid(
                        "setcellvalue",
                        jqxRowIndex,
                        columnName,
                        unformattedValue);

                    var rowId = dti.oasis.grid.getRowIdByJqxRowIndex(gridId, jqxRowIndex);

                    if (needSync && dti.oasis.grid.isSelectedRow(gridId, rowId)) {
                        dti.oasis.grid.syncCellValueToDetailField(gridId, columnName);
                    }

                    dti.oasis.grid.resizeColumnWidth(gridId, columnName, val, "minwidth");
                } else {
                    var currentRowIndex = dti.oasis.grid.getRowIndexByJqxRowIndex(gridId, jqxRowIndex);
                    dti.oasis.grid.setRowDataValueByRowIndex(gridId, currentRowIndex, columnName, unformattedValue);
                }
            },

            setRowDataValue: function (gridId, rowId, columnName, val) {
                var rowData = this.getRowData(gridId, rowId);

                if (!rowData) {
                    return;
                }

                var formattedValue = dti.oasis.grid.rowDataFormat.getRowDataFormatter(gridId, columnName).format(val);
                rowData[columnName] = formattedValue;
            },

            setRowDataValueByRowIndex: function (gridId, rowIndex, columnName, val) {
                var rowData = this.getRowDataByRowIndex(gridId, rowIndex);

                if (!rowData) {
                    return;
                }

                var formattedValue = dti.oasis.grid.rowDataFormat.getRowDataFormatter(gridId, columnName).format(val);
                rowData[columnName] = formattedValue;
            },

            setRowDataValueByJqxRowIndex: function (gridId, jqxRowIndex, columnName, val) {
                var rowData = this.getRowDataByJqxRowIndex(gridId, jqxRowIndex);

                if (!rowData) {
                    return;
                }

                var formattedValue = dti.oasis.grid.rowDataFormat.getRowDataFormatter(gridId, columnName).format(val);
                rowData[columnName] = formattedValue;
            },

            setColumnAggStyle: function(aggContent) {
                return "<div class='dti-jqx-grid-aggregate-div'>" + aggContent + "</div>"
            },

            getCellStyleForNegativeNumber: function(gridId, jqxRowId, columnName, cellValue, indColumnName) {
                if (!dti.oasis.string.isEmpty(indColumnName)) {
                    cellValue = dti.oasis.grid.getRowDataByJqxRowIndex(gridId, jqxRowId)[indColumnName];
                }

                if (cellValue != null && cellValue < 0) {
                    return "color:#C30";
                }

                return "";
            },

            getSelectIndColumnName: function (gridId) {
                //TODO 1. Check if the selectIndColumn is hidden.
                //     2. Check if use CSELECTIND as selectIndColumnName.
                var selectColName = "CSELECT_IND";
                var custSelectColName = this.getCustomGridOption(gridId, selectColName);

                if (custSelectColName) {
                    return custSelectColName;
                }

                if (this.hasColumn(gridId, selectColName)) {
                    return selectColName;
                }
                return null;
            },

            hasSelectIndColumnName: function (gridId) {
                var selectIndColumn = this.getSelectIndColumnName(gridId);
                if (selectIndColumn) {
                    return true;
                }
                return false;
            },

            updateEditableCell: function(grid, gridId, currentColumnName){
                if (dti.oasis.grid.getProperty(gridId, "inEditorCellMode")) {
                    var selectedJqxRowIndex = dti.oasis.grid.getSelectedJqxRowIndex(gridId);

                    var columnName = dti.oasis.grid.getProperty(gridId, "editorCellColumnName");
                    if (columnName && currentColumnName != columnName) {
                        grid.jqxGrid('endcelledit',selectedJqxRowIndex, columnName, false);

                        dti.oasis.grid.setProperty(gridId, "inEditorCellMode", false);
                        dti.oasis.grid.setProperty(gridId, "editorCellColumnName", "");
                    }
                }
            },

            commonAddRow: function(gridId) {
                // set flags to prevent commonReadyStateReady() being triggered
                var table = getTableForGrid(gridId);
                setTableProperty(table, "isInCommonAddRow", true);
                setTableProperty(table, "isUserReadyStateReadyComplete", false);
                // insert a new row and set initial values in it
                eval(gridId + "_insertrow();");
                setSelectedRow(gridId, getTableProperty(table, "lastInsertedId"));
                // re-connect fields if this is the first row
                //   CAUTION: have to do this before invoke postCommonAddRow(), or we may not get
                //            the initial values into fields for the selectRow event
                var xmlData = getXMLDataForGridName(gridId);
                if (xmlData.recordset.recordcount == 1) {
                    reconnectAllFields(document.forms[0]);
                }

                // wait for table ready state to complete before invoke postCommonAddRow()
                var testCode = 'getTableProperty(getTableForGrid(\"' + gridId + '\"), "isUserReadyStateReadyComplete")';
                var callbackCode = 'postCommonAddRow(\"' + gridId + '\");';
                executeWhenTestSucceeds(testCode, callbackCode, 50);
            },

            postCommonAddRow: function (gridId, initialURL) {
                var table = getTableForGrid(gridId);

                // do the common ready state complete stuff
                commonReadyStateReady(table);
                // do the rest
                var priorRowId = -1;

                if(typeof initialURL == "undefined" || initialURL) {
                    var xmlData = getXMLDataForGridName(gridId);
                    setInitialUrlInGrid(xmlData);
                }

                hideShowForm(gridId);
                // reset flag
                setTableProperty(table, "isInCommonAddRow", false);
                return priorRowId;
            },

            setInitialUrlInGrid: function(xmlData) {
                var fieldCount = xmlData.recordset.Fields.count;
                for (var i = 0; i < fieldCount; i++) {
                    if (xmlData.recordset.Fields.Item(i).name.substr(0, 4) == "URL_")
                        xmlData.recordset.Fields.Item(i).value = "javascript:void(0);";
                }
            },

            commonDeleteRow: function (gridId, obrReset) {
                var rs = getXMLDataForGridName(gridId).recordset;
                var xmlData = getXMLDataForGridName(gridId);
                var origXMLData = getOrigXMLData(xmlData);
                var officalRecordId = null;

                if (isFieldExistsInRecordset(rs, "COFFICIALRECORDID")) {
                    officalRecordId = rs.Fields("COFFICIALRECORDID").value;
                }
                if (isEmpty(officalRecordId)) {
                    var functionExist = eval("window." + gridId + "_deleteDependentRow");
                    if (functionExist != null) {
                        eval(gridId + "_deleteDependentRow();");
                    }
                    eval(gridId + "_deleterow();");
                }
                else {
                    var officalNode = origXMLData.documentElement.selectSingleNode("//ROW[@id='" + officalRecordId + "']");
                    if (officalNode != null) {
                        officalNode.selectSingleNode("./DISPLAY_IND").text = "Y";
                        eval(gridId + "_filter(\"UPDATE_IND != 'D'\")");
                    }

                    // Use xmlData.recordset instead of rs to avoid infinite loop as rs does NOT move along with xmlData
                    first(xmlData);
                    beginDeleteMultipleRow(gridId);
                    while (!xmlData.recordset.eof) {
                        if (xmlData.recordset.Fields("COFFICIALRECORDID").value == officalRecordId) {
                            setSelectedRow(gridId, xmlData.recordset("ID").value);
                            eval(gridId + "_deleterow();");
                        }
                        next(xmlData);
                    }
                    endDeleteMultipleRow(gridId);
                }

                hideShowForm(gridId);
                if(typeof obrReset == "undefined" || obrReset) {
                    var currentSelectedRowId = getSelectedRow(gridId);
                    OBRResetToOriginalInEmptyGrid(currentSelectedRowId, gridId);
                }

                return true;
            },
            isFieldMasked: function (gridId, fieldName) {
                var bRc = false;
                var gridInfo = dti.oasis.page.getGridInfo(gridId);
                var columnsConfig = gridInfo["config"]["columnsConfig"];

                for (var i = 0; i < columnsConfig.length; i++) {
                    if (columnsConfig[i].id.toUpperCase() == fieldName.toUpperCase()) {
                        bRc = columnsConfig[i].masked;
                        break;
                    }
                }
                return bRc;
            },
            getCellRowStyle: function(gridId, dataField, jqxRowIndex) {
                var rawData = dti.oasis.grid.getRowDataByJqxRowIndex(gridId, jqxRowIndex);
                var cellStyle = rawData["C" + gridId.toUpperCase() + "ROWSTYLE"];
                var cellColor = "";

                if (typeof cellStyle != "undefined" && cellStyle != ""){
                    cellColor = " " + cellStyle.replace("color"," ").replace(":", " ").trim();
                }
                return cellColor;
            },
            getAllNumbersColorInGrid: function(gridId, dataField, value) {
                var columnConfig = dti.oasis.grid.config.getOasisColumnConfig(gridId, dataField);
                var color = "";
                var index = columnConfig.displayFormat.indexOf("[");
                if (columnConfig.dataType == "NM" && index >0) {
                    if (value <0) {
                        var index2 = columnConfig.displayFormat.indexOf("]");
                        color = columnConfig.displayFormat.substring(index + 1, index2);
                    }
                }
                return color;
            },
            isSelectAllDisabled: function (gridId) {
                return $("#".concat(gridId, "_chkCSELECT_ALL")).hasClass("jqx-checkbox-disabled");
            },

            enableDisableSelectAll: function (gridId, isEnabled) {
                if(!isEnabled)
                    $("#".concat(gridId, "_chkCSELECT_ALL")).addClass("jqx-checkbox-disabled");
                else
                    $("#".concat(gridId, "_chkCSELECT_ALL")).removeClass("jqx-checkbox-disabled");
            },
            resizeGridToFitIFrame: function(gridId) {
                var p = getParentWindow();
                if(p) {
                    $(p.document).find("iframe").each(function(index,frame){
                        if ($(frame.contentDocument).find("#" + gridId).length > 0) {
                            if (dti.oasis.grid.isIFrameInDivPopup(frame)) {
                                //just exit if it is a div pop
                                return;
                            }

                            if(frame.height < dti.oasis.grid.DEFAULT_IFRAME_MIN_HEIGHT) {
                                frame.height = dti.oasis.grid.DEFAULT_IFRAME_MIN_HEIGHT;
                            }
                        }
                    });
                }
            },
            resizeIFrameForMenuFiltering: function(datafield, gridId, filterOpen) {
                if(datafield) {
                    var p = dti.oasis.ui.getParentFrame();
                    if (p) {
                        $(p.document).find("iframe").each(function (index, frame) {
                            if ($(frame.contentDocument).find("#" + gridId).length > 0) {
                                var filterHeight = (filterOpen) ? dti.oasis.grid.DEFAULT_MENU_FILTERING_HEIGHT : -dti.oasis.grid.DEFAULT_MENU_FILTERING_HEIGHT;
                                if (dti.oasis.grid.isIFrameInDivPopup(frame)) {
                                    //just exit if it is a div pop
                                    return;
                                }

                                var frameHeight = frame.contentWindow.innerHeight;
                                var gridHeight = $("#" + gridId).height();

                                //just return if the filter menu items will be ok to be shown.
                                if (((gridHeight + filterHeight) < frameHeight) && !dti.oasis.menufiltering.isInitialize()) {
                                    return;
                                }

                                if (filterOpen) {
                                    //save origial height
                                    dti.oasis.menufiltering.setFrameHeight(frameHeight);
                                    frameHeight += filterHeight;
                                } else {
                                    //set the height to original values.
                                    frameHeight = dti.oasis.menufiltering.getFrameHeight();
                                    dti.oasis.menufiltering.clear();
                                }
                                frame.style.height = frameHeight + "px";
                            }
                        });
                    }
                }
            },
            isIFrameInDivPopup: function(frame){
                var $closetDivs = $(frame).closest("div");
                if ($closetDivs.length) {
                    var $closetDiv = $closetDivs[0];
                    if ($closetDiv.id.indexOf("popup") > 0) {
                        return true;
                    }
                }
                return false;
            },
            resizeColumnWidth: function(gridId, columnName, val, widthType){
                var columnsConfig = dti.oasis.grid.config.getOasisColumnsConfig(gridId);
                var len = columnsConfig.length;

                for (var i = 0; i < len; i++) {
                    var colConfig = columnsConfig[i];
                    if (!colConfig.masked && (colConfig.displayType != "checkbox" && colConfig.displayType != "dropdownlist") &&
                        colConfig.id == columnName) {

                        var cellWidthLen = dti.oasis.grid.calculateColumnWidth(val);

                        var column = $("#".concat(gridId)).jqxGrid('getcolumn', columnName);
                        if (cellWidthLen > column.width) {
                            $("#".concat(gridId)).jqxGrid("setcolumnproperty", columnName, widthType, cellWidthLen);
                            $("#".concat(gridId)).jqxGrid("autoresizecolumn", columnName, "column");
                        }
                        break;
                    }
                }
            },
            calculateColumnWidth: function(str) {
                var colLen = 100;
                if (!isUndefined(str)) {
                    colLen = str.toString().length;
                    if (colLen > dti.oasis.grid.DEFAULT_MAX_COLUMN_WIDTH_DATA_LENGTH) {
                        colLen = dti.oasis.grid.DEFAULT_MAX_COLUMN_WIDTH_DATA_LENGTH;
                    }
                    colLen = (colLen * 8) + 10;
                }
                return colLen;
            }

        };
    })();
}

if (typeof dti.oasis.menufiltering == "undefined") {
    dti.oasis.menufiltering = (function () {
        var frameHeight = 0;
        var gridHeight = 0;
        var initialize = false;
        return {
            clear:function(){
                frameHeight = 0;
                gridHeight = 0;
                initialize = false;
            },
            isInitialize: function(){
                return initialize;
            },
            setFrameHeight: function (val) {
                frameHeight = val;
                initialize = true;
            },
            getFrameHeight: function(){
                return frameHeight;
            }
        }
    })();
}