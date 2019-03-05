/*
 Return all columns, not just the changed columns as listed in the ROW.col attribute.
 */
//-----------------------------------------------------------------------------
// javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:  Michael Li
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//  04/12/2011       Michael Li    for issue 119392
//  04/12/2011       Michael Li    for issue 116895
//  02/21/2017       dzhang        Issue 179102: Detail form should not be displayed when the grid is empty.
//  10/29/2018       dpang         Issue 196632: Change getYear to getFullYear.
//  10/31/2018       dzhang        Issue 195835: Support to get specific filter condition
//-----------------------------------------------------------------------------
function handleOnFilterChange(field) {
    if (field.name == 'ListFilter') {
        filterList();
    }
    return true;
}

function filterList() {
    var grid ;
    if(window["getGridId"]) {
        grid = getGridId();
    } else if (window["getCurrentlySelectedGridId"]) {
        grid = getCurrentlySelectedGridId();
    }

    if (!grid) {
        grid = 'testgrid';
    }

    var gridDiv = getObject("DIV_" + grid);
    gridDiv.style.display = "block";
    var filter = '';

    var functionEixts = eval("window.handleGetFilter");
    if (functionEixts) {
        filter = handleGetFilter();
    }

    if (dti.oasis.string.isEmpty(filter)) {
        var filterValue = getRadioButtonValue(getObject("ListFilter"));
        var today = getToday();
        var mday = '01/01/3000';
        if (filterValue == 'ACTIVE') {
            filter = "( ";
            //CI_ADDRESS_LIST,CI_CONTACT,CI_RELAT,CI_DISABILITY,CI_DENOMINATOR
            filter = filter + "(concat(substring(substring(concat(CEFFECTIVETODATE,'01/01/3000'),1,10),7,4),substring(substring(concat(CEFFECTIVETODATE,'01/01/3000'),1,10),1,2),substring(substring(concat(CEFFECTIVETODATE,'01/01/3000'),1,10),4,2)) > " + today;
            filter = filter + " and concat(substring(substring(concat(CEFFECTIVEFROMDATE,'01/01/1900'),1,10),7,4),substring(substring(concat(CEFFECTIVEFROMDATE,'01/01/1900'),1,10),1,2),substring(substring(concat(CEFFECTIVEFROMDATE,'01/01/1900'),1,10),4,2)) <= " + today + " )";
            //CI_ENTITY_ROLE,CI_ENTITY_CLASS_LIST
            filter = filter + " and "
            filter = filter + "  (concat(substring(substring(concat(CEFFECTIVE_TO_DATE,'01/01/3000'),1,10)  ,7,4),substring(substring(concat(CEFFECTIVE_TO_DATE,'01/01/3000'),1,10)  ,1,2),substring(substring(concat(CEFFECTIVE_TO_DATE,'01/01/3000'),1,10)  ,4,2)) > " + today;
            filter = filter + " and concat(substring(substring(concat(CEFFECTIVE_FROM_DATE,'01/01/1900'),1,10),7,4),substring(substring(concat(CEFFECTIVE_FROM_DATE,'01/01/1900'),1,10),1,2),substring(substring(concat(CEFFECTIVE_FROM_DATE,'01/01/1900'),1,10),4,2)) <= " + today + " )";
            //CI_CERTFD
            filter = filter + " and "
            filter = filter + "  (concat(substring(substring(concat(CELIGEXPRDATE,'01/01/3000'),1,10),7,4),substring(substring(concat(CELIGEXPRDATE,'01/01/3000'),1,10),1,2),substring(substring(concat(CELIGEXPRDATE,'01/01/3000'),1,10),4,2)) > " + today;
            filter = filter + " and concat(substring(substring(concat(CCERTIFIEDDATE,'01/01/1900'),1,10),7,4),substring(substring(concat(CCERTIFIEDDATE,'01/01/1900'),1,10),1,2),substring(substring(concat(CCERTIFIEDDATE,'01/01/1900'),1,10),4,2)) <= " + today + " )";
            //CI_LICENSE
            filter = filter + " and "
            filter = filter + "  (concat(substring(substring(concat(CEXPIRATIONDATE,'01/01/3000'),1,10)  ,7,4),substring(substring(concat(CEXPIRATIONDATE,'01/01/3000'),1,10)  ,1,2),substring(substring(concat(CEXPIRATIONDATE,'01/01/3000'),1,10)  ,4,2)) > " + today;
            filter = filter + " and concat(substring(substring(concat(CDATELICENSED,'01/01/1900'),1,10),7,4),substring(substring(concat(CDATELICENSED,'01/01/1900'),1,10),1,2),substring(substring(concat(CDATELICENSED,'01/01/1900'),1,10),4,2)) <= " + today + " )";
            filter = filter + " )";
        } else if (filterValue == 'EXPIRED') {
            filter = "( ";
            //CI_ADDRESS_LIST,CI_CONTACT,CI_RELAT,CI_DISABILITY,CI_DENOMINATOR
            filter = filter + " concat(substring(substring(concat(CEFFECTIVETODATE,'01/01/3000'),1,10),7,4),substring(substring(concat(CEFFECTIVETODATE,'01/01/3000'),1,10),1,2),substring(substring(concat(CEFFECTIVETODATE,'01/01/3000'),1,10),4,2)) <= " + today;
            //CI_ENTITY_ROLE,CI_ENTITY_CLASS_LIST
            filter = filter + " or ";
            filter = filter + "  concat(substring(substring(concat(CEFFECTIVE_TO_DATE,'01/01/3000'),1,10),7,4),substring(substring(concat(CEFFECTIVE_TO_DATE,'01/01/3000'),1,10),1,2),substring(substring(concat(CEFFECTIVE_TO_DATE,'01/01/3000'),1,10),4,2)) <= " + today;
            //CI_CERTFD
            filter = filter + " or ";
            filter = filter + "  concat(substring(substring(concat(CELIGEXPRDATE,'01/01/3000'),1,10),7,4),substring(substring(concat(CELIGEXPRDATE,'01/01/3000'),1,10),1,2),substring(substring(concat(CELIGEXPRDATE,'01/01/3000'),1,10),4,2)) <= " + today;
            //CI_LICENSE
            filter = filter + " or ";
            filter = filter + "  concat(substring(substring(concat(CEXPIRATIONDATE,'01/01/3000'),1,10),7,4),substring(substring(concat(CEXPIRATIONDATE,'01/01/3000'),1,10),1,2),substring(substring(concat(CEXPIRATIONDATE,'01/01/3000'),1,10),4,2)) <= " + today;
            filter = filter + " )";
        }
    }
    eval(grid + '_filter(filter)');
}

function getToday() {
    var today = new Date();
    var d = today.getDate();
    var day = (d < 10) ? '0' + d : d;
    var m = today.getMonth() + 1;
    var month = (m < 10) ? '0' + m : m;
    var year = today.getFullYear();
    return year.toString() + month.toString() + day.toString();
}
