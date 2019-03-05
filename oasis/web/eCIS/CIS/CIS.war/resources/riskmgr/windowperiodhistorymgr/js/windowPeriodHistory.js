/**
 * The JS file for the Window Period History page.
 *
 * <p>(C) 2014 Delphi Technology, inc. (dti)</p>
 * Date:   2/25/14
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

function handleOnButtonClick(btn) {
    switch (btn) {
        case "clearFilter":
            clearFilter();
            break;
        case "close":
            closeWindow();
            break;
    }
}

function handleOnChange(field) {
    if (field.name.endsWith("Filter")) {
        // Validate and process date fields.
        if (field.name == "windowPeriodStartDateFilter" ||
            field.name == "windowPeriodEndDateFilter" ||
            field.name == "completionDateFilter") {
            datemaskclear();
        }

        filterWindowPeriodHistory();
    }
}

function clearFilter() {
    setObjectValue("windowPeriodStartDateFilter", "");
    setObjectValue("windowPeriodEndDateFilter", "");
    setObjectValue("windowPeriodStatusFilter", "");
    setObjectValue("completionDateFilter", "");
    setObjectValue("programDescriptionFilter", "");
    setObjectValue("wphPolicyNoFilter", "");
    filterWindowPeriodHistory();
}


function filterWindowPeriodHistory() {
    var filterString = '';

    var windowPeriodStartDateFilter = getObjectValue("windowPeriodStartDateFilter");
    if (isStringValue(windowPeriodStartDateFilter)) {
        if (isStringValue(filterString)) {
            filterString += " and ";
        }
        filterString += "(CWINDOWPERIODSTARTDATE = '" + windowPeriodStartDateFilter + "')";
    }

    var windowPeriodEndDateFilter = getObjectValue("windowPeriodEndDateFilter");
    if (isStringValue(windowPeriodEndDateFilter)) {
        if (isStringValue(filterString)) {
            filterString += " and ";
        }
        filterString += "(CWINDOWPERIODENDDATE = '" + windowPeriodEndDateFilter + "')";
    }

    var windowPeriodStatusFilter = trim(getObjectValue("windowPeriodStatusFilter"));
    if (isStringValue(windowPeriodStatusFilter)) {
        if (isStringValue(filterString)) {
            filterString += " and ";
        }
        filterString += "(contains(translate(CWINDOWPERIODSTATUS, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')," +
            " translate('" + windowPeriodStatusFilter + "', 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')))";
    }

    var completionDateFilter = getObjectValue("completionDateFilter");
    if (isStringValue(completionDateFilter)) {
        if (isStringValue(filterString)) {
            filterString += " and ";
        }
        filterString += "(CCOMPLETIONDATE = '" + completionDateFilter + "')";
    }

    var programDescriptionFilter = trim(getObjectValue("programDescriptionFilter"));
    if (isStringValue(programDescriptionFilter)) {
        if (isStringValue(filterString)) {
            filterString += " and ";
        }
        filterString += "(contains(translate(CPROGRAMDESCRIPTION, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')," +
            " translate('" + programDescriptionFilter + "', 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')))";
    }

    var wphPolicyNoFilter = trim(getObjectValue("wphPolicyNoFilter"));
    if (isStringValue(wphPolicyNoFilter)) {
        if (isStringValue(filterString)) {
            filterString += " and ";
        }
        filterString += "(contains(translate(CWPHPOLICYNO, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')," +
            " translate('" + wphPolicyNoFilter + "', 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')))";
    }

    selectFirstRowInGrid("windowPeriodHistoryListGrid");
    eval('windowPeriodHistoryListGrid_filter(filterString)');
    if (!getTableProperty(getTableForGrid("windowPeriodHistoryListGrid"), "hasrows")) {
        hideEmptyTable(windowPeriodHistoryListGrid);
    } else {
        showNonEmptyTable(windowPeriodHistoryListGrid);
        selectFirstRowInGrid("windowPeriodHistoryListGrid");
    }
}