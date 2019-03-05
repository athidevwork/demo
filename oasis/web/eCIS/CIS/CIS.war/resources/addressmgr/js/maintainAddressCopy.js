//-----------------------------------------------------------------------------
//  Description: Javascript of address copy
//  Revision Date   Revised By  Description
//  10/08/2018      Elvin       Issue 195835: grid replacement, remove useless code
//  ---------------------------------------------------------------------------

function handleOnButtonClick(btnId) {
    if (btnId == 'CANCEL') {
        if (isPageDataChanged() && !confirm(getMessage("js.lose.changes.confirmation"))) {
            return;
        } else {
            closeWindow();
            return;
        }
    } else if (btnId == 'SAVE') {
        var selectedKeys = getSelectedKeys(testgrid1);
        if (selectedKeys.length == 0) {
            alert(getMessage("ci.common.error.exit.noSelect"));
            return;
        }

        //generate dataXML
        var entityId = getObjectValue("entityId");
        var addressId = getObjectValue("addressId");
        var dataXML = "<ROWS>";
        var entityChildId, entityParentId, targetEntityId;
        for (var i = 0; i < selectedKeys.length; i++) {
            selectRow("testgrid", selectedKeys[i]);

            entityChildId = testgrid1.recordset("CENTITYCHILDID").value;
            entityParentId = testgrid1.recordset("CENTITYPARENTID").value;
            if (entityChildId == entityId) {
                targetEntityId = entityParentId;
            } else {
                targetEntityId = entityChildId;
            }

            dataXML += "<ROW id=\"" + i + "\">" +
                "<CADDRESSID>" + addressId + "</CADDRESSID>" +
                "<CENTITYID>" + targetEntityId + "</CENTITYID>" +
                "<CPRIMARYADDRESSB>" + ('0' == testgrid1.recordset("CPRIMARYADDRESSB").value ? '0' : '1') + "</CPRIMARYADDRESSB>" +
                "<UPDATE_IND>I</UPDATE_IND>" +
                "</ROW>";
        }
        dataXML += "</ROWS>";

        var url = "ciAddressCopy.do?process=copyAddress";
        var data = "txtXML=" + dataXML;
        new AJAXRequest("POST", url, data, afterCopyEntityAddress, false);
    }
}

function afterCopyEntityAddress(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseText;
            if (data != "Y") {
                var xml = ajax.responseXML;
                if (!handleAjaxMessages(xml, null)) {
                    return;
                }
            } else {
                alert(getMessage("ci.entity.message.address.copied"));
                closeWindow();
            }
        }
    }
}

function addressCopyGrid_btnClick(asBtn) {
    switch (asBtn) {
        case 'SELECT':
            testgrid_updatenode("CSELECT_IND", -1);
            first(testgrid1);
            selectFirstRowInGrid("testgrid");
            break;
        case 'DESELECT':
            testgrid_updatenode("CSELECT_IND", 0);
            first(testgrid1);
            selectFirstRowInGrid("testgrid");
            break;
    }
}