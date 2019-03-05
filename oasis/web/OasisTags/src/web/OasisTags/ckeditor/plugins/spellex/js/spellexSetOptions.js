//*********************************************************************
// spellexSetOptions.js
// Purpose: JavaScript code executed on the client to save, retrieve, and edit
// spelling options stored in a browser cookie.
//*********************************************************************

/**
 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------

**/

var CASE_SENSITIVE_OPT = 0x0001;
var IGNORE_ALL_CAPS_WORD_OPT = 0x0002;
var IGNORE_CAPPED_WORD_OPT = 0x0004;
var IGNORE_MIXED_CASE_OPT = 0x0008;
var IGNORE_MIXED_DIGITS_OPT = 0x0010;
var REPORT_DOUBLED_WORD_OPT = 0x0040;
var REPORT_UNCAPPED_OPT = 0x0400;
var SUGGEST_SPLIT_WORDS_OPT = 0x8000;
var IGNORE_DOMAIN_NAMES_OPT = 0x10000;

// Retrieve the spelling options cookie.
var cookies = document.cookie;
var optionsCookieName = "SpellingOptions";
var keyStr = optionsCookieName + "=";
var options = CASE_SENSITIVE_OPT | REPORT_DOUBLED_WORD_OPT | REPORT_UNCAPPED_OPT |
  IGNORE_DOMAIN_NAMES_OPT | SUGGEST_SPLIT_WORDS_OPT;    // defaults

function setOptions() {
    var pos = cookies.indexOf(keyStr);
    if (pos >= 0) {
        var start = pos + keyStr.length;
        var end = cookies.indexOf(";", start);
        if (end < 0) {
            end = cookies.length;
        }
        options = cookies.substring(start, end);
    }
    options &= (CASE_SENSITIVE_OPT | IGNORE_ALL_CAPS_WORD_OPT |
      IGNORE_CAPPED_WORD_OPT | IGNORE_MIXED_CASE_OPT |
      IGNORE_MIXED_DIGITS_OPT | REPORT_DOUBLED_WORD_OPT |
      REPORT_UNCAPPED_OPT | SUGGEST_SPLIT_WORDS_OPT |
      IGNORE_DOMAIN_NAMES_OPT);

    // Set the check boxes with the initial option values

    document.setOptions.caseSensitiveBtn.checked = (options & CASE_SENSITIVE_OPT);
    document.setOptions.ignoreAllCapsWordsBtn.checked = (options & IGNORE_ALL_CAPS_WORD_OPT);
    document.setOptions.ignoreCappedWordsBtn.checked = (options & IGNORE_CAPPED_WORD_OPT);
    document.setOptions.ignoreMixedCaseBtn.checked = (options & IGNORE_MIXED_CASE_OPT);
    document.setOptions.ignoreMixedDigitsBtn.checked = (options & IGNORE_MIXED_DIGITS_OPT);
    document.setOptions.reportDoubledWordsBtn.checked = (options & REPORT_DOUBLED_WORD_OPT);
    document.setOptions.suggestSplitWordsBtn.checked = (options & SUGGEST_SPLIT_WORDS_OPT);
    document.setOptions.ignoreDomainNamesBtn.checked = (options & IGNORE_DOMAIN_NAMES_OPT);
    document.setOptions.reportCapitalizationBtn.checked = (options & REPORT_UNCAPPED_OPT);
}
// Respond to an OK button press by saving the options
// and returning to the previous page
function onOkBtn() {
//    alert('onOkBtn()');
    // Build an option mask
    if (document.setOptions.caseSensitiveBtn.checked) {
        options |= CASE_SENSITIVE_OPT;
    }
    else {
        options &= ~CASE_SENSITIVE_OPT;
    }
    if (document.setOptions.ignoreAllCapsWordsBtn.checked) {
        options |= IGNORE_ALL_CAPS_WORD_OPT;
    }
    else {
        options &= ~IGNORE_ALL_CAPS_WORD_OPT;
    }
    if (document.setOptions.ignoreCappedWordsBtn.checked) {
        options |= IGNORE_CAPPED_WORD_OPT;
    }
    else {
        options &= ~IGNORE_CAPPED_WORD_OPT;
    }
    if (document.setOptions.ignoreMixedCaseBtn.checked) {
        options |= IGNORE_MIXED_CASE_OPT;
    }
    else {
        options &= ~IGNORE_MIXED_CASE_OPT;
    }
    if (document.setOptions.ignoreMixedDigitsBtn.checked) {
        options |= IGNORE_MIXED_DIGITS_OPT;
    }
    else {
        options &= ~IGNORE_MIXED_DIGITS_OPT;
    }
    if (document.setOptions.reportDoubledWordsBtn.checked) {
        options |= REPORT_DOUBLED_WORD_OPT;
    }
    else {
        options &= ~REPORT_DOUBLED_WORD_OPT;
    }
    if (document.setOptions.suggestSplitWordsBtn.checked) {
        options |= SUGGEST_SPLIT_WORDS_OPT;
    }
    else {
        options &= ~SUGGEST_SPLIT_WORDS_OPT;
    }
    if (document.setOptions.ignoreDomainNamesBtn.checked) {
        options |= IGNORE_DOMAIN_NAMES_OPT;
    }
    else {
        options &= ~IGNORE_DOMAIN_NAMES_OPT;
    }
    if (document.setOptions.reportCapitalizationBtn.checked) {
        options |= REPORT_UNCAPPED_OPT;
    }
    else {
        options &= ~REPORT_UNCAPPED_OPT;
    }

    var nextYear = new Date();
    nextYear.setFullYear(nextYear.getFullYear() + 1);
    document.cookie = keyStr + options +
      "; expires=" + nextYear.toGMTString() + "; path=/";

    try {
        if (window.opener &&  !window.opener.closed)
            window.opener.parent.parent.location.reload();
    } catch (e) {
        //alert('Exception Thrown');
    }
    window.close();
}

// Return to the previous page without saving the options
function onCloseBtn() {
    window.close();
}

//Opens help window
function openHelpWindow(url) {
//    openWindow(url, 'OptionsHelp', 450, 515);
    var link = window.open(url,'OptionsHelp',"toolbar=0,location=no,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,width=450,height=515");
    link.focus();
}