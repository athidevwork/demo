//-----------------------------------------------------------------------------
// Functions to support Prir Carrier History page.
// Author: jdingle
// Date:   04/14/2010
// Modifications:
//-----------------------------------------------------------------------------
// mm/dd/yyyy    who         change
//-----------------------------------------------------------------------------
var isChanged = false;
//-----------------------------------------------------------------------------
// do on page load
//-----------------------------------------------------------------------------
function handleOnLoad() {
    var classes;
    classes = $(".divGridHolder");
    if (classes.length > 0) {
        classes[0].style.height = "400px";
    }
    classes = $("#DIV_priorCarrierHistoryGrid");
    if (classes.length > 0) {
        classes[0].style.height = "400px";
    }
}

//-----------------------------------------------------------------------------
// click on button
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SAVE':
            // Save function is not currently used
            commonOnSubmit('savePriorCarrierHistory', true, true, true);
            break;
        case 'CLOSE':
                baseCloseWindow();
            break;
    }
}

//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function userRowchange(obj) {
    isChanged = true;
}

//-----------------------------------------------------------------------------
// some divs are only identified by a class in framework. use this to get a handle
//-----------------------------------------------------------------------------
function getElementsByClass( searchClass, domNode, tagName) {
	if (domNode == null) domNode = document;
	if (tagName == null) tagName = '*';
	var el = new Array();
	var tags = domNode.getElementsByTagName(tagName);
	var tcl = " "+searchClass+" ";
	for(i=0,j=0; i<tags.length; i++) {
		var test = " " + tags[i].className + " ";
		if (test.indexOf(tcl) != -1)
			el[j++] = tags[i];
	}
	return el;
}
