

/* handle page action item */
function contactListProc(btnId) {

    /* get selected */
    var dataArray = getSelectedKeys(testgrid1);

    /* check if at least one is selected */
	if(dataArray.length == 0) {
		alert(getMessage("ci.entity.message.contact.select"));
		return;
	}

   if (btnId == "IMPORT") {
       /* confirm deletion */
        if (!confirm(getMessage("ci.entity.message.contact.import")))
            return;
    }

    /* set selected */
    var dataString = dataArray[0];
    if (btnId == "IMPORT") {
        for (var i = 1; i < dataArray.length; i++)
            dataString = dataString + "," + dataArray[i];
    }

    /* set selected */
    document.forms[0].contactID.value = dataString;

    btnClick(btnId);
}

/* override */
function btnClick(btnId) {

    document.forms[0].process.value = btnId;
    submitFirstForm();
}