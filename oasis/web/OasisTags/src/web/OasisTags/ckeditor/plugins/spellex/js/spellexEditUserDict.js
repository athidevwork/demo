//*********************************************************************
// spellexEditUserDict.js
// Purpose: JavaScript code executed on the client to save, retrieve, and edit
// spelling options stored in a browser cookie.
//*********************************************************************

/**
 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------

**/

var udCookieName = "UserDict";
var keyStr = udCookieName + "=";

function retrieveUserDictionary() {
// Retrieve the user dictionary cookie.
var cookies = document.cookie;
var pos = cookies.indexOf(keyStr);
var s;
var start;
var end;
var userDictStr = "";
if (pos >= 0) {
    start = pos + keyStr.length;
    end = cookies.indexOf(";", start);
    if (end < 0) {
        end = cookies.length;
    }
    var userDictStr = unescape(cookies.substring(start, end));
}

// Fill the list box with words in the user dictionary
start = 0;
document.editUserDict.userDict.options.length = 0;
do {
    end = userDictStr.indexOf(",", start);
    if (end < 0) {
        end = userDictStr.length;
    }
    if (start < end) {
        var word = userDictStr.substring(start, end);
        var newWord = new Option;
        newWord.text = word;
        document.editUserDict.userDict.options[document.editUserDict.userDict.options.length] = newWord;
    }
    start = end + 1;
} while (end < userDictStr.length);
}
// Add a word to the user dictionary
function onAddWordBtn() {
    var word = document.editUserDict.word.value
    if (word.length == 0) {
        alert("Please enter a word in the 'Word' field.");
        return;
    }

    // Make sure the word isn't a duplicate
    for (var i = 0; i < document.editUserDict.userDict.options.length; ++i) {
        if (document.editUserDict.userDict.options[i].text == word) {
            return;
        }
    }

    var newWord = new Option;
    newWord.text = word;
    document.editUserDict.userDict.options[document.editUserDict.userDict.options.length] = newWord;
}

// Remove a word from the user dictionary
function onRemoveWordBtn() {
    if (document.editUserDict.userDict.selectedIndex < 0) {
        alert("Please select a word to delete.");
        return;
    }
    document.editUserDict.userDict.options[document.editUserDict.userDict.selectedIndex] = null;
}

// Respond to an OK button press by saving the user dictionary contents
// and returning to the previous page
function onOkBtn() {
    // Build a string containing the user dictionary.
    var userDict = "";
    for (var i = 0; i < document.editUserDict.userDict.options.length; ++i) {
        userDict += document.editUserDict.userDict.options[i].text;
        if (i < document.editUserDict.userDict.options.length - 1) {
            userDict += ",";
        }
    }

    var nextYear = new Date();
    nextYear.setFullYear(nextYear.getFullYear() + 1);
    document.cookie = keyStr + userDict +
      "; expires=" + nextYear.toGMTString() + "; path=/";

    try {
        if (window.opener &&  !window.opener.closed)
            window.opener.parent.parent.location.reload();
    } catch (e) {
        //alert('Exception Thrown');
    }
    window.close();    
}

// Return to the previous page without saving the dictionary contents
function onCloseBtn() {
//    history.back();
    window.close();
}

//Opens help window
function openHelpWindow(url)
{
//var link = window.open(url,"Link","toolbar=0,location=no,directories=0,status=0,menubar=0,scrollbars=yes,resizable=1,width=450,height=515");
//link.focus()
//    alert(url);
//    openWindow(url, 'DictHelp');
            var link = window.open(url,'DictHelp',"toolbar=0,location=no,directories=0,status=0,menubar=0,scrollbars=yes,resizable=1,width=450,height=515");
            link.focus();
}