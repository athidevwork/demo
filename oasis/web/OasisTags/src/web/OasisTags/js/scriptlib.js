//-----------------------------------------------------------------------------
// NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!!
//
// THIS FILE IS MAINTAINED IN THE OasisTags PROJECT!!!
// IT IS PROPAGATED FROM THE OasisTags PROJECT TO THE OTHER PROJECTS!!!
// IT SHOULD ONLY BE MODIFIED IN THE OasisTags PROJECT!!!
// IT SHOULD NEVER BE MODIFIED IN THE CM PROJECT OR THE CIS PROJECT OR ANY
// PROJECT OTHER THAN OasisTags!!!
//
// NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!!
//-----------------------------------------------------------------------------

// scriptlib.js
// Many of the below functions take an optional parameter eok (for "emptyOK")
// which determines whether the empty string will return true or false.
// Default behavior is controlled by global variable defaultEmptyOK.
//
// BASIC DATA VALIDATION FUNCTIONS:
//
// isWhitespace (s)                    Check whether string s is empty or whitespace.
// isLetter (c)                        Check whether character c is an English letter
// isDigit (c)                         Check whether character c is a digit
// isLetterOrDigit (c)                 Check whether character c is a letter or digit.
// isInteger (s [,eok])                True if all characters in string s are numbers.
// isSignedInteger (s [,eok])          True if all characters in string s are numbers; leading + or - allowed.
// isPositiveInteger (s [,eok])        True if string s is an integer > 0.
// isNonnegativeInteger (s [,eok])     True if string s is an integer >= 0.
// isNegativeInteger (s [,eok])        True if s is an integer < 0.
// isNonpositiveInteger (s [,eok])     True if s is an integer <= 0.
// isFloat (s [,eok])                  True if string s is an unsigned floating point (real) number. (Integers also OK.)
// isSignedFloat (s [,eok])            True if string s is a floating point number; leading + or - allowed. (Integers also OK.)
// isAlphabetic (s [,eok])             True if string s is English letters
// isAlphanumeric (s [,eok])           True if string s is English letters and numbers only.
//
// isSSN (s [,eok])                    True if string s is a valid U.S. Social Security Number.
// isUSPhoneNumber (s [,eok])          True if string s is a valid U.S. Phone Number.
// isInternationalPhoneNumber (s [,eok]) True if string s is a valid international phone number.
// isZIPCode (s [,eok])                True if string s is a valid U.S. ZIP code.
// isStateCode (s [,eok])              True if string s is a valid U.S. Postal Code
// isEmail (s [,eok])                  True if string s is a valid email address.
// isYear (s [,eok])                   True if string s is a valid Year number.
// isIntegerInRange (s, a, b [,eok])   True if string s is an integer between a and b, inclusive.
// isMonth (s [,eok])                  True if string s is a valid month between 1 and 12.
// isDay (s [,eok])                    True if string s is a valid day between 1 and 31.
// daysInFebruary (year)               Returns number of days in February of that year.
// isDate (year, month, day)           True if string arguments form a valid date.
// isValueDate (str)				   True if string argument forms a vali date.
// clearFormFields (theForm)           Clears fields in a form
// isDate2OnOrAfterDate1 (date1Value,  Validate two dates to make sure the second date is
//  date2Value)                        on or after the first date.
// isStringValue(value[, isCode])      Determines if a string value is empty or not
// isStringCodeValue(value)            Convenience method to check code string value
// isNum2GrtThanOrEqToNum1 (num1Value, Validate two numbers to make sure the second number is
//  num2Value)                         greater than or equal to the first number.

// startWith(substr)                   true/false if string starts with substr,
// endsWith (substr)                   true/false if string ends with substr

// FUNCTIONS TO REFORMAT DATA:
//
// stripCharsInBag (s, bag)            Removes all characters in string bag from string s.
// stripCharsNotInBag (s, bag)         Removes all characters NOT in string bag from string s.
// stripWhitespace (s)                 Removes all whitespace characters from s.
// stripInitialWhitespace (s)          Removes initial (leading) whitespace characters from s.
// reformat (TARGETSTRING, STRING,     Function for inserting formatting characters or
//   INTEGER, STRING, INTEGER ... )       delimiters into TARGETSTRING.
// reformatZIPCode (ZIPString)         If 9 digits, inserts separator hyphen.
// reformatSSN (SSN)                   Reformats as 123-45-6789.
// reformatUSPhone (USPhone)           Reformats as (123) 456-789.


// FUNCTIONS TO PROMPT USER:
//
// promptEntry (s)                     Display data entry prompt string s in status bar.
// warnEmpty (theField, s)             Notify user that required field theField is empty.
// warnInvalid (theField, s)           Notify user that contents of field theField are invalid.


// FUNCTIONS TO INTERACTIVELY CHECK FIELD CONTENTS:
//
// checkString (theField, s [,eok])    Check that theField.value is not empty or all whitespace.
// checkStateCode (theField)           Check that theField.value is a valid U.S. state code.
// checkZIPCode (theField [,eok])      Check that theField.value is a valid ZIP code.
// checkUSPhone (theField [,eok])      Check that theField.value is a valid US Phone.
// checkInternationalPhone (theField [,eok])  Check that theField.value is a valid International Phone.
// checkEmail (theField [,eok])        Check that theField.value is a valid Email.
// checkSSN (theField [,eok])          Check that theField.value is a valid SSN.
// checkYear (theField [,eok])         Check that theField.value is a valid Year.
// checkMonth (theField [,eok])        Check that theField.value is a valid Month.
// checkDay (theField [,eok])          Check that theField.value is a valid Day.
// checkDate (yearField, monthField, dayField, labelString, OKtoOmitDay)
//                                     Check that field values form a valid date.
// getRadioButtonValue (radio)         Get checked value from radio button.
// checkCreditCard (radio, theField)   Validate credit card info.


// CREDIT CARD DATA VALIDATION FUNCTIONS
//
// isCreditCard (st)              True if credit card number passes the Luhn Mod-10 test.
// isVisa (cc)                    True if string cc is a valid VISA number.
// isMasterCard (cc)              True if string cc is a valid MasterCard number.
// isAmericanExpress (cc)         True if string cc is a valid American Express number.
// isDiscover (cc)                True if string cc is a valid Discover card number.
// isAnyCard (cc)                 True if string cc is a valid card number for any of the accepted types.
// isCardMatch (Type, Number)     True if Number is valid for credic card of type Type.

// MISC FUNCTIONS
//
// getAppContext ()					Returns the appcontext portion of the current URL as "/appcontext"
// clearFormFields(frm,clearHidden) Clears all input fields in form

/*
Revision Date    Revised By  Description
-----------------------------------------------------------------------------

01/10/2007       GCC         Eliminated prompt function;  conflicts with
                             IE function of same name.
07/28/2007       MLM         Added new functions to handle data formatting & masking.
07/10/2008       Fred        Added function isReasonableDate(obj)
08/21/2008       Guang       extended String object with startsWith, endsWith methods
10/08/2008       Jacky      Fix date string check
04/13/2018       cesar       192259 - created dti.inpututils to handle event.keyCode
*/

// VARIABLE DECLARATIONS

var digits = "0123456789";

var lowercaseLetters = "abcdefghijklmnopqrstuvwxyz"

var uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"


// whitespace characters
var whitespace = " \t\n\r";


// decimal point character differs by language and culture
var decimalPointDelimiter = "."


// non-digit characters which are allowed in phone numbers
var phoneNumberDelimiters = "()- ";


// characters which are allowed in US phone numbers
var validUSPhoneChars = digits + phoneNumberDelimiters;


// characters which are allowed in international phone numbers
// (a leading + is OK)
var validWorldPhoneChars = digits + phoneNumberDelimiters + "+";


// non-digit characters which are allowed in
// Social Security Numbers
var SSNDelimiters = "- ";



// characters which are allowed in Social Security Numbers
var validSSNChars = digits + SSNDelimiters;



// U.S. Social Security Numbers have 9 digits.
// They are formatted as 123-45-6789.
var digitsInSocialSecurityNumber = 9;



// U.S. phone numbers have 10 digits.
// They are formatted as 123 456 7890 or (123) 456-7890.
var digitsInUSPhoneNumber = 10;



// non-digit characters which are allowed in ZIP Codes
var ZIPCodeDelimiters = "-";



// our preferred delimiter for reformatting ZIP Codes
var ZIPCodeDelimeter = "-"


// characters which are allowed in Social Security Numbers
var validZIPCodeChars = digits + ZIPCodeDelimiters



// U.S. ZIP codes have 5 or 9 digits.
// They are formatted as 12345 or 12345-6789.
var digitsInZIPCode1 = 5
var digitsInZIPCode2 = 9


// non-digit characters which are allowed in credit card numbers
var creditCardDelimiters = " "


// CONSTANT STRING DECLARATIONS
// (grouped for ease of translation and localization)

// m is an abbreviation for "missing"

var mPrefix = "You did not enter a value into the "
var mSuffix = " field. This is a required field. Please enter it now."

// s is an abbreviation for "string"

var sUSLastName = "Last Name"
var sUSFirstName = "First Name"
var sWorldLastName = "Family Name"
var sWorldFirstName = "Given Name"
var sTitle = "Title"
var sCompanyName = "Company Name"
var sUSAddress = "Street Address"
var sWorldAddress = "Address"
var sCity = "City"
var sStateCode = "State Code"
var sWorldState = "State, Province, or Prefecture"
var sCountry = "Country"
var sZIPCode = "ZIP Code"
var sWorldPostalCode = "Postal Code"
var sPhone = "Phone Number"
var sFax = "Fax Number"
var sDateOfBirth = "Date of Birth"
var sExpirationDate = "Expiration Date"
var sEmail = "Email"
var sSSN = "Social Security Number"
var sCreditCardNumber = "Credit Card Number"
var sOtherInfo = "Other Information"




// i is an abbreviation for "invalid"

var iStateCode = "This field must be a valid two character U.S. state abbreviation (like CA for California). Please reenter it now."
var iZIPCode = "This field must be a 5 or 9 digit U.S. ZIP Code (like 94043). Please reenter it now."
var iUSPhone = "This field must be a 10 digit U.S. phone number (like 415 555 1212). Please reenter it now."
var iWorldPhone = "This field must be a valid international phone number. Please reenter it now."
var iSSN = "This field must be a 9 digit U.S. social security number (like 123 45 6789). Please reenter it now."
var iEmail = "This field must be a valid email address (like foo@bar.com). Please reenter it now."
var iCreditCardPrefix = "This is not a valid "
var iCreditCardSuffix = " credit card number. (Click the link on this form to see a list of sample numbers.) Please reenter it now."
var iDay = "This field must be a day number between 1 and 31.  Please reenter it now."
var iMonth = "This field must be a month number between 1 and 12.  Please reenter it now."
var iYear = "This field must be a 2 or 4 digit year number.  Please reenter it now."
var iDatePrefix = "The Day, Month, and Year for "
var iDateSuffix = " do not form a valid date.  Please reenter them now."



// p is an abbreviation for "prompt"

var pEntryPrompt = "Please enter a "
var pStateCode = "2 character code (like CA)."
var pZIPCode = "5 or 9 digit U.S. ZIP Code (like 94043)."
var pUSPhone = "10 digit U.S. phone number (like 415 555 1212)."
var pWorldPhone = "international phone number."
var pSSN = "9 digit U.S. social security number (like 123 45 6789)."
var pEmail = "valid email address (like foo@bar.com)."
var pCreditCard = "valid credit card number."
var pDay = "day number between 1 and 31."
var pMonth = "month number between 1 and 12."
var pYear = "2 or 4 digit year number."


// Global variable defaultEmptyOK defines default return value
// for many functions when they are passed the empty string.
// By default, they will return defaultEmptyOK.
//
// defaultEmptyOK is false, which means that by default,
// these functions will do "strict" validation.  Function
// isInteger, for example, will only return true if it is
// passed a string containing an integer; if it is passed
// the empty string, it will return false.
//
// You can change this default behavior globally (for all
// functions which use defaultEmptyOK) by changing the value
// of defaultEmptyOK.
//
// Most of these functions have an optional argument emptyOK
// which allows you to override the default behavior for
// the duration of a function call.
//
// This functionality is useful because it is possible to
// say "if the user puts anything in this field, it must
// be an integer (or a phone number, or a string, etc.),
// but it's OK to leave the field empty too."
// This is the case for fields which are optional but which
// must have a certain kind of content if filled in.

var defaultEmptyOK = false

var daysInMonth = new Array();
daysInMonth[1] = 31;
daysInMonth[2] = 29;   // must programmatically check this
daysInMonth[3] = 31;
daysInMonth[4] = 30;
daysInMonth[5] = 31;
daysInMonth[6] = 30;
daysInMonth[7] = 31;
daysInMonth[8] = 31;
daysInMonth[9] = 30;
daysInMonth[10] = 31;
daysInMonth[11] = 30;
daysInMonth[12] = 31;




// Valid U.S. Postal Codes for states, territories, armed forces, etc.
// See http://www.usps.gov/ncsc/lookups/abbr_state.txt.

var USStateCodeDelimiter = "|";
var USStateCodes = "AL|AK|AS|AZ|AR|CA|CO|CT|DE|DC|FM|FL|GA|GU|HI|ID|IL|IN|IA|KS|KY|LA|ME|MH|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|MP|OH|OK|OR|PW|PA|PR|RI|SC|SD|TN|TX|UT|VT|VI|VA|WA|WV|WI|WY|AE|AA|AE|AE|AP"




// Check whether string s is empty.

function isEmpty(s)
{   return ((s == null) || (s.length == 0))
}

//----------------------------------------------------------------------------------
// The following Functions come from http://javascript.crockford.com/remedial.html
//----------------------------------------------------------------------------------
/*
    Returns true if a is an array, meaning that it was produced by the Array constructor
    or by using the [ ] array literal notation.
*/
function isArray(a) {
// This is not working, so testing if the length member is not undefined
//    return isObject(a) && a.constructor == Array;
    return isObject(a) && !isUndefined(a.length);
}

/*
    Returns true if a is one of the boolean values, true or false.
*/
function isBoolean(a) {
    return typeof a == 'boolean';
}

/*
    Returns true if a is a function.
    Beware that some native functions in IE were made to look like objects instead of functions.
    This function does not detect that.
*/
function isFunction(a) {
    return typeof a == 'function';
}

/*
    Returns true if a is the null value.
*/
function isNull(a) {
    return a == null;
}

/*
    Returns true if a is an object, and array, or a function.
    It returns false if a is a string, a number, a boolean, or null, or undefined.
*/
function isObject(a) {
    return (a && typeof a == 'object') || isFunction(a);
}

/*
    Returns true if a is a string.
*/
function isString(a) {
    return typeof a == 'string';
}

/*
    Returns true if a is defined.
*/
function isDefined(a) {
    return a != undefined;
}

/*
    Returns true if a is the undefined value.
    You can get the undefined value from an uninitialized variable or from a missing member of an object.
*/
function isUndefined(a) {
    return a == undefined;
}
//----------------------------------------------------------------------------------
// End of code from http://javascript.crockford.com/remedial.html
//----------------------------------------------------------------------------------

function isMultiSelect(a) {
    return a.multiple != undefined && a.multiple == true;
}


// Returns true if string s is empty or
// whitespace characters only.

function isWhitespace (s)

{   var i;

    // Is s empty?
    if (isEmpty(s)) return true;

    // Search through string's characters one by one
    // until we find a non-whitespace character.
    // When we do, return false; if we don't, return true.

    for (i = 0; i < s.length; i++)
    {
        // Check that current character isn't whitespace.
        var c = s.charAt(i);

        if (whitespace.indexOf(c) == -1) return false;
    }

    // All characters are whitespace.
    return true;
}



// Removes all characters which appear in string bag from string s.

function stripCharsInBag (s, bag)

{   var i;
    var returnString = "";

    // Search through string's characters one by one.
    // If character is not in bag, append to returnString.

    for (i = 0; i < s.length; i++)
    {
        // Check that current character isn't whitespace.
        var c = s.charAt(i);
        if (bag.indexOf(c) == -1) returnString += c;
    }

    return returnString;
}



// Removes all characters which do NOT appear in string bag
// from string s.

function stripCharsNotInBag (s, bag)

{   var i;
    var returnString = "";

    // Search through string's characters one by one.
    // If character is in bag, append to returnString.

    for (i = 0; i < s.length; i++)
    {
        // Check that current character isn't whitespace.
        var c = s.charAt(i);
        if (bag.indexOf(c) != -1) returnString += c;
    }

    return returnString;
}



// Removes all whitespace characters from s.
// Global variable whitespace (see above)
// defines which characters are considered whitespace.

function stripWhitespace (s)

{   return stripCharsInBag (s, whitespace)
}


// Removes initial (leading) whitespace characters from s.
// Global variable whitespace (see above)
// defines which characters are considered whitespace.

function stripInitialWhitespace (s)

{   var i = 0;

    while ((i < s.length) && charInString (s.charAt(i), whitespace))
       i++;

    return s.substring (i, s.length);
}


// Returns true if character c is an English letter
// (A .. Z, a..z).

function isLetter (c)
{   return ( ((c >= "a") && (c <= "z")) || ((c >= "A") && (c <= "Z")) )
}


// Returns true if character c is a digit
// (0 .. 9).

function isDigit (c)
{   return ((c >= "0") && (c <= "9"))
}



// Returns true if character c is a letter or digit.

function isLetterOrDigit (c)
{   return (isLetter(c) || isDigit(c))
}



// isInteger (STRING s [, BOOLEAN emptyOK])
//
// Returns true if all characters in string s are numbers.
//
// Accepts non-signed integers only. Does not accept floating
// point, exponential notation, etc.
//
// We don't use parseInt because that would accept a string
// with trailing non-numeric characters.
//
// By default, returns defaultEmptyOK if s is empty.
// There is an optional second argument called emptyOK.
// emptyOK is used to override for a single function call
//      the default behavior which is specified globally by
//      defaultEmptyOK.
// If emptyOK is false (or any value other than true),
//      the function will return false if s is empty.
// If emptyOK is true, the function will return true if s is empty.
//
// EXAMPLE FUNCTION CALL:     RESULT:
// isInteger ("5")            true
// isInteger ("")             defaultEmptyOK
// isInteger ("-5")           false
// isInteger ("", true)       true
// isInteger ("", false)      false
// isInteger ("5", false)     true

function isInteger (s)

{   var i;

    if (isEmpty(s))
       if (isInteger.arguments.length == 1) return defaultEmptyOK;
       else return (isInteger.arguments[1] == true);

    // Search through string's characters one by one
    // until we find a non-numeric character.
    // When we do, return false; if we don't, return true.

    for (i = 0; i < s.length; i++)
    {
        // Check that current character is number.
        var c = s.charAt(i);

        if (!isDigit(c)) return false;
    }

    // All characters are numbers.
    return true;
}


// isSignedInteger (STRING s [, BOOLEAN emptyOK])
//
// Returns true if all characters are numbers;
// first character is allowed to be + or - as well.
//
// Does not accept floating point, exponential notation, etc.
//
// We don't use parseInt because that would accept a string
// with trailing non-numeric characters.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.
//
// EXAMPLE FUNCTION CALL:          RESULT:
// isSignedInteger ("5")           true
// isSignedInteger ("")            defaultEmptyOK
// isSignedInteger ("-5")          true
// isSignedInteger ("+5")          true
// isSignedInteger ("", false)     false
// isSignedInteger ("", true)      true
// isSignedInteger ("-", true)     false
// isSignedInteger ("-", false)    false
// isSignedInteger ("+", true)     false
// isSignedInteger ("+", false)    false

function isSignedInteger (s)

{   if (isEmpty(s))
       if (isSignedInteger.arguments.length == 1) return defaultEmptyOK;
       else return (isSignedInteger.arguments[1] == true);

    else {
        var startPos = 0;
        var secondArg = defaultEmptyOK;

        if (isSignedInteger.arguments.length > 1)
            secondArg = isSignedInteger.arguments[1];

        // skip leading + or -
        if ( (s.charAt(0) == "-") || (s.charAt(0) == "+") ){
           if(s.length==1){   // The sign only is invalid
               return false;
           }
           startPos = 1;
        }
        return (isInteger(s.substring(startPos, s.length), secondArg))
    }
}




// isPositiveInteger (STRING s [, BOOLEAN emptyOK])
//
// Returns true if string s is an integer > 0.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isPositiveInteger (s)
{   var secondArg = defaultEmptyOK;

    if (isPositiveInteger.arguments.length > 1)
        secondArg = isPositiveInteger.arguments[1];

    // The next line is a bit byzantine.  What it means is:
    // a) s must be a signed integer, AND
    // b) one of the following must be true:
    //    i)  s is empty and we are supposed to return true for
    //        empty strings
    //    ii) this is a positive, not negative, number

    return (isSignedInteger(s, secondArg)
         && ( (isEmpty(s) && secondArg)  || (parseInt (s) > 0) ) );
}






// isNonnegativeInteger (STRING s [, BOOLEAN emptyOK])
//
// Returns true if string s is an integer >= 0.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isNonnegativeInteger (s)
{   var secondArg = defaultEmptyOK;

    if (isNonnegativeInteger.arguments.length > 1)
        secondArg = isNonnegativeInteger.arguments[1];

    // The next line is a bit byzantine.  What it means is:
    // a) s must be a signed integer, AND
    // b) one of the following must be true:
    //    i)  s is empty and we are supposed to return true for
    //        empty strings
    //    ii) this is a number >= 0

    return (isSignedInteger(s, secondArg)
         && ( (isEmpty(s) && secondArg)  || (parseInt (s) >= 0) ) );
}






// isNegativeInteger (STRING s [, BOOLEAN emptyOK])
//
// Returns true if string s is an integer < 0.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isNegativeInteger (s)
{   var secondArg = defaultEmptyOK;

    if (isNegativeInteger.arguments.length > 1)
        secondArg = isNegativeInteger.arguments[1];

    // The next line is a bit byzantine.  What it means is:
    // a) s must be a signed integer, AND
    // b) one of the following must be true:
    //    i)  s is empty and we are supposed to return true for
    //        empty strings
    //    ii) this is a negative, not positive, number

    return (isSignedInteger(s, secondArg)
         && ( (isEmpty(s) && secondArg)  || (parseInt (s) < 0) ) );
}






// isNonpositiveInteger (STRING s [, BOOLEAN emptyOK])
//
// Returns true if string s is an integer <= 0.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isNonpositiveInteger (s)
{   var secondArg = defaultEmptyOK;

    if (isNonpositiveInteger.arguments.length > 1)
        secondArg = isNonpositiveInteger.arguments[1];

    // The next line is a bit byzantine.  What it means is:
    // a) s must be a signed integer, AND
    // b) one of the following must be true:
    //    i)  s is empty and we are supposed to return true for
    //        empty strings
    //    ii) this is a number <= 0

    return (isSignedInteger(s, secondArg)
         && ( (isEmpty(s) && secondArg)  || (parseInt (s) <= 0) ) );
}





// isFloat (STRING s [, BOOLEAN emptyOK])
//
// True if string s is an unsigned floating point (real) number.
//
// Also returns true for unsigned integers. If you wish
// to distinguish between integers and floating point numbers,
// first call isInteger, then call isFloat.
//
// Does not accept exponential notation.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isFloat (s)

{   var i;
    var seenDecimalPoint = false;

    if (isEmpty(s))
       if (isFloat.arguments.length == 1) return defaultEmptyOK;
       else return (isFloat.arguments[1] == true);

    if (s == decimalPointDelimiter) return false;

    // Search through string's characters one by one
    // until we find a non-numeric character.
    // When we do, return false; if we don't, return true.

    for (i = 0; i < s.length; i++)
    {
        // Check that current character is number.
        var c = s.charAt(i);

        if ((c == decimalPointDelimiter) && !seenDecimalPoint) seenDecimalPoint = true;
        else if (!isDigit(c)) return false;
    }

    // All characters are numbers.
    return true;
}







// isSignedFloat (STRING s [, BOOLEAN emptyOK])
//
// True if string s is a signed or unsigned floating point
// (real) number. First character is allowed to be + or -.
//
// Also returns true for unsigned integers. If you wish
// to distinguish between integers and floating point numbers,
// first call isSignedInteger, then call isSignedFloat.
//
// Does not accept exponential notation.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isSignedFloat (s)

{   if (isEmpty(s))
       if (isSignedFloat.arguments.length == 1) return defaultEmptyOK;
       else return (isSignedFloat.arguments[1] == true);

    else {
        var startPos = 0;
        var secondArg = defaultEmptyOK;

        if (isSignedFloat.arguments.length > 1)
            secondArg = isSignedFloat.arguments[1];

        // skip leading + or -
        if ( (s.charAt(0) == "-") || (s.charAt(0) == "+") ) {
           if(s.length==1){   // The sign only is invalid
               return false;
           }
           startPos = 1;
        }
        return (isFloat(s.substring(startPos, s.length), secondArg))
    }
}




// isAlphabetic (STRING s [, BOOLEAN emptyOK])
//
// Returns true if string s is English letters
// (A .. Z, a..z) only.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.
//
// NOTE: Need i18n version to support European characters.
// This could be tricky due to different character
// sets and orderings for various languages and platforms.

function isAlphabetic (s)

{   var i;

    if (isEmpty(s))
       if (isAlphabetic.arguments.length == 1) return defaultEmptyOK;
       else return (isAlphabetic.arguments[1] == true);

    // Search through string's characters one by one
    // until we find a non-alphabetic character.
    // When we do, return false; if we don't, return true.

    for (i = 0; i < s.length; i++)
    {
        // Check that current character is letter.
        var c = s.charAt(i);

        if (!isLetter(c))
        return false;
    }

    // All characters are letters.
    return true;
}




// isAlphanumeric (STRING s [, BOOLEAN emptyOK])
//
// Returns true if string s is English letters
// (A .. Z, a..z) and numbers only.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.
//
// NOTE: Need i18n version to support European characters.
// This could be tricky due to different character
// sets and orderings for various languages and platforms.

function isAlphanumeric (s)

{   var i;

    if (isEmpty(s))
       if (isAlphanumeric.arguments.length == 1) return defaultEmptyOK;
       else return (isAlphanumeric.arguments[1] == true);

    // Search through string's characters one by one
    // until we find a non-alphanumeric character.
    // When we do, return false; if we don't, return true.

    for (i = 0; i < s.length; i++)
    {
        // Check that current character is number or letter.
        var c = s.charAt(i);

        if (! (isLetter(c) || isDigit(c) ) )
        return false;
    }

    // All characters are numbers or letters.
    return true;
}




// reformat (TARGETSTRING, STRING, INTEGER, STRING, INTEGER ... )
//
// Handy function for arbitrarily inserting formatting characters
// or delimiters of various kinds within TARGETSTRING.
//
// reformat takes one named argument, a string s, and any number
// of other arguments.  The other arguments must be integers or
// strings.  These other arguments specify how string s is to be
// reformatted and how and where other strings are to be inserted
// into it.
//
// reformat processes the other arguments in order one by one.
// * If the argument is an integer, reformat appends that number
//   of sequential characters from s to the resultString.
// * If the argument is a string, reformat appends the string
//   to the resultString.
//
// NOTE: The first argument after TARGETSTRING must be a string.
// (It can be empty.)  The second argument must be an integer.
// Thereafter, integers and strings must alternate.  This is to
// provide backward compatibility to Navigator 2.0.2 JavaScript
// by avoiding use of the typeof operator.
//
// It is the caller's responsibility to make sure that we do not
// try to copy more characters from s than s.length.
//
// EXAMPLES:
//
// * To reformat a 10-digit U.S. phone number from "1234567890"
//   to "(123) 456-7890" make this function call:
//   reformat("1234567890", "(", 3, ") ", 3, "-", 4)
//
// * To reformat a 9-digit U.S. Social Security number from
//   "123456789" to "123-45-6789" make this function call:
//   reformat("123456789", "", 3, "-", 2, "-", 4)
//
// HINT:
//
// If you have a string which is already delimited in one way
// (example: a phone number delimited with spaces as "123 456 7890")
// and you want to delimit it in another way using function reformat,
// call function stripCharsNotInBag to remove the unwanted
// characters, THEN call function reformat to delimit as desired.
//
// EXAMPLE:
//
// reformat (stripCharsNotInBag ("123 456 7890", digits),
//           "(", 3, ") ", 3, "-", 4)

function reformat (s)

{   var arg;
    var sPos = 0;
    var resultString = "";

    for (var i = 1; i < reformat.arguments.length; i++) {
       arg = reformat.arguments[i];
       if (i % 2 == 1) resultString += arg;
       else {
           resultString += s.substring(sPos, sPos + arg);
           sPos += arg;
       }
    }
    return resultString;
}




// isSSN (STRING s [, BOOLEAN emptyOK])
//
// isSSN returns true if string s is a valid U.S. Social
// Security Number.  Must be 9 digits.
//
// NOTE: Strip out any delimiters (spaces, hyphens, etc.)
// from string s before calling this function.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isSSN (s)
{   if (isEmpty(s))
       if (isSSN.arguments.length == 1) return defaultEmptyOK;
       else return (isSSN.arguments[1] == true);
    return (isInteger(s) && s.length == digitsInSocialSecurityNumber)
}




// isUSPhoneNumber (STRING s [, BOOLEAN emptyOK])
//
// isUSPhoneNumber returns true if string s is a valid U.S. Phone
// Number.  Must be 10 digits.
//
// NOTE: Strip out any delimiters (spaces, hyphens, parentheses, etc.)
// from string s before calling this function.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isUSPhoneNumber (s)
{   if (isEmpty(s))
       if (isUSPhoneNumber.arguments.length == 1) return defaultEmptyOK;
       else return (isUSPhoneNumber.arguments[1] == true);
    return (isInteger(s) && s.length == digitsInUSPhoneNumber)
}




// isInternationalPhoneNumber (STRING s [, BOOLEAN emptyOK])
//
// isInternationalPhoneNumber returns true if string s is a valid
// international phone number.  Must be digits only; any length OK.
// May be prefixed by + character.
//
// NOTE: A phone number of all zeros would not be accepted.
// I don't think that is a valid phone number anyway.
//
// NOTE: Strip out any delimiters (spaces, hyphens, parentheses, etc.)
// from string s before calling this function.  You may leave in
// leading + character if you wish.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isInternationalPhoneNumber (s)
{   if (isEmpty(s))
       if (isInternationalPhoneNumber.arguments.length == 1) return defaultEmptyOK;
       else return (isInternationalPhoneNumber.arguments[1] == true);
    return (isPositiveInteger(s))
}




// isZIPCode (STRING s [, BOOLEAN emptyOK])
//
// isZIPCode returns true if string s is a valid
// U.S. ZIP code.  Must be 5 or 9 digits only.
//
// NOTE: Strip out any delimiters (spaces, hyphens, etc.)
// from string s before calling this function.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isZIPCode (s)
{  if (isEmpty(s))
       if (isZIPCode.arguments.length == 1) return defaultEmptyOK;
       else return (isZIPCode.arguments[1] == true);
   return (isInteger(s) &&
            ((s.length == digitsInZIPCode1) ||
             (s.length == digitsInZIPCode2)))
}





// isStateCode (STRING s [, BOOLEAN emptyOK])
//
// Return true if s is a valid U.S. Postal Code
// (abbreviation for state).
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isStateCode(s)
{   if (isEmpty(s))
       if (isStateCode.arguments.length == 1) return defaultEmptyOK;
       else return (isStateCode.arguments[1] == true);
    return ( (USStateCodes.indexOf(s) != -1) &&
             (s.indexOf(USStateCodeDelimiter) == -1) )
}




// isEmail (STRING s [, BOOLEAN emptyOK])
//
// Email address must be of form a@b.c -- in other words:
// * there must be at least one character before the @
// * there must be at least one character before and after the .
// * the characters @ and . are both required
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isEmail (s)
{   if (isEmpty(s))
       if (isEmail.arguments.length == 1) return defaultEmptyOK;
       else return (isEmail.arguments[1] == true);

    // is s whitespace?
    if (isWhitespace(s)) return false;

    // there must be >= 1 character before @, so we
    // start looking at character position 1
    // (i.e. second character)
    var i = 1;
    var sLength = s.length;

    // look for @
    while ((i < sLength) && (s.charAt(i) != "@"))
    { i++
    }

    if ((i >= sLength) || (s.charAt(i) != "@")) return false;
    else i += 2;

    // look for .
    while ((i < sLength) && (s.charAt(i) != "."))
    { i++
    }

    // there must be at least one character after the .
    if ((i >= sLength - 1) || (s.charAt(i) != ".")) return false;
    else return true;
}





// isYear (STRING s [, BOOLEAN emptyOK])
//
// isYear returns true if string s is a valid
// Year number.  Must be 2 or 4 digits only.
//
// For Year 2000 compliance, you are advised
// to use 4-digit year numbers everywhere.
//
// And yes, this function is not Year 10000 compliant, but
// because I am giving you 8003 years of advance notice,
// I don't feel very guilty about this ...
//
// For B.C. compliance, write your own function. ;->
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isYear (s)
{   if (isEmpty(s))
       if (isYear.arguments.length == 1) return defaultEmptyOK;
       else return (isYear.arguments[1] == true);
    if (!isNonnegativeInteger(s)) return false;
    return ((s.length == 2) || (s.length == 4));
}



// isIntegerInRange (STRING s, INTEGER a, INTEGER b [, BOOLEAN emptyOK])
//
// isIntegerInRange returns true if string s is an integer
// within the range of integer arguments a and b, inclusive.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.


function isIntegerInRange (s, a, b)
{   if (isEmpty(s))
       if (isIntegerInRange.arguments.length == 1) return defaultEmptyOK;
       else return (isIntegerInRange.arguments[1] == true);

    // Catch non-integer strings to avoid creating a NaN below,
    // which isn't available on JavaScript 1.0 for Windows.
    if (!isInteger(s, false)) return false;

    // Now, explicitly change the type to integer via parseInt
    // so that the comparison code below will work both on
    // JavaScript 1.2 (which typechecks in equality comparisons)
    // and JavaScript 1.1 and before (which doesn't).
    if ( s == '08') s = '8';
    if ( s == '09') s = '9';
    var num = parseInt (s);
   return ((num >= a) && (num <= b));
}



// isMonth (STRING s [, BOOLEAN emptyOK])
//
// isMonth returns true if string s is a valid
// month number between 1 and 12.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isMonth (s)
{   if (isEmpty(s))
       if (isMonth.arguments.length == 1) return defaultEmptyOK;
       else return (isMonth.arguments[1] == true);
    return isIntegerInRange (s, 1, 12);
}



// isDay (STRING s [, BOOLEAN emptyOK])
//
// isDay returns true if string s is a valid
// day number between 1 and 31.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function isDay (s)
{   if (isEmpty(s))
       if (isDay.arguments.length == 1) return defaultEmptyOK;
       else return (isDay.arguments[1] == true);
    return isIntegerInRange (s, 1, 31);
}



// daysInFebruary (INTEGER year)
//
// Given integer argument year,
// returns number of days in February of that year.

function daysInFebruary (year)
{   // February has 29 days in any year evenly divisible by four,
    // EXCEPT for centurial years which are not also divisible by 400.
    return (  ((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0) ) ) ? 29 : 28 );
}



// isValidDate (STRING year, STRING month, STRING day)
//
// isDate returns true if string arguments year, month, and day
// form a valid date.
//

function isValidDate (year, month, day)
{   // catch invalid years (not 2- or 4-digit) and invalid months and days.
    if (! (isYear(year, false) && isMonth(month, false) && isDay(day, false))) return false;
    if (day.charAt(0)=="0" && day.length>1) day=day.substring(1);
    if (month.charAt(0)=="0" && month.length>1) month=month.substring(1);

    // Explicitly change type to integer to make code work in both
    // JavaScript 1.1 and JavaScript 1.2.
    var intYear = parseInt(year);
    var intMonth = parseInt(month);
    var intDay = parseInt(day);


    // catch invalid days, except for February
    if (intDay > daysInMonth[intMonth]) return false;

    if ((intMonth == 2) && (intDay > daysInFebruary(intYear))) return false;

    return true;
}

// returns false if not a date or if value is blank
function isValueDate(datestr) {
	if(datestr=="") return false;
	index1=datestr.indexOf("/");
	if (index1<1 || index1>2) return false;

	mth=datestr.substring(0,index1);
	if (!checknum(mth)) return false;

	if ( mth.length == 1 )
		mth1 = '0' + mth;
	else
		mth1 = mth;

	mth=parseInt(mth,10);

	if(mth<1 || mth>12)  return false;

	index2=datestr.indexOf("/",index1+1);
	if (index2<3 || index2>5) return false;

	dt=datestr.substring(index1+1,index2);
	if (!checknum(dt)) return false;

	if ( dt.length == 1 )
		dt1 = '0' + dt;
	else
		dt1 = dt;

	dt=parseInt(dt,10);

	yr=datestr.substring(index2+1,datestr.length);
	if (!checknum(yr))  return false;

	if (yr.length!=4) {
		if (yr.length == 2 ) {
			 yr=parseInt(yr,10);
			 if ( yr < 35 ) {
				yr = '20' + datestr.substring(index2+1,datestr.length);
			 }
			 else {
				 yr = '19' + datestr.substring(index2+1,datestr.length);
			}
		}
		else {
			return false;
		}
	}

	yr=parseInt(yr,10);

	if (yr%4==0){
		maxdays=new Array(31,31,29,31,30,31,30,31,31,30,31,30,31);
		fleapyr=true;
	}
	else
		maxdays=new Array(31,31,28,31,30,31,30,31,31,30,31,30,31);

	if (dt>maxdays[mth]||dt<1) return false;

	return true;

}
function isDate(fieldnm) {
 var obj=getObject(fieldnm)
 var index1,index2
 var dt,mth,yr,dt1,mth1
 var fleapyr=false
 var datestr=obj.value
 var label=getLabel(fieldnm)

 if (datestr=="") return true // we validate only if there is a value

 index1=datestr.indexOf("/")
 if (index1<1 || index1>2){
	alert("Enter the date value in mm/dd/yyyy format in "+label+" field")
	return false;
 }
 mth=datestr.substring(0,index1)
 if (!checknum(mth)){
	alert("Month must be numeric "+label+" field")
	obj.focus();
	return false;
 }

 if ( mth.length == 1 ) {
	mth1 = '0' + mth
 }
 else {
	mth1 = mth
}
  mth=parseInt(mth,10)

 if(mth<1 || mth>12) {
	alert("Month must be between 1 and 12 in "+label+" field")
	obj.focus();
	return false
 }

 index2=datestr.indexOf("/",index1+1)
 if (index2<3 || index2>5){
	alert("Enter date value in mm/dd/yyyy format in "+label+" field")
	obj.focus();
	return false;
	}

 dt=datestr.substring(index1+1,index2)
 if (!checknum(dt)){
	alert("Date in "+label+" field")
	obj.focus();
	return false;
 }

 if ( dt.length == 1 ) {
	dt1 = '0' + dt
 }
 else {
	dt1 = dt
 }
 dt=parseInt(dt,10)

 yr=datestr.substring(index2+1,datestr.length)
 if (!checknum(yr)) {
	alert("Year in "+label+" field")
	obj.focus();
	return false
 }

 if (yr.length!=4) {
    if (yr.length == 2 ) {
		 yr=parseInt(yr,10)
		 if ( yr < 35 ) {
			yr = '20' + datestr.substring(index2+1,datestr.length)
		 }
		 else {
			 yr = '19' + datestr.substring(index2+1,datestr.length)
		}
	}
	else {
		alert("Enter date value in mm/dd/yyyy format in "+label+" field")
		obj.focus();
		return false;
	}
 }


 yr=parseInt(yr,10)

 if (yr%4==0){
  maxdays=new Array(31,31,29,31,30,31,30,31,31,30,31,30,31)
  fleapyr=true
 } else {
  maxdays=new Array(31,31,28,31,30,31,30,31,31,30,31,30,31)
 }

 if (dt>maxdays[mth]||dt<1){
  alert("Day must be between 1 and " + maxdays[mth] + " in "+ label + " field");
  obj.focus();
  return false
 }

  obj.value= mth1 + '/' + dt1 + '/' + yr

 return true;
}

function isReasonableDate(obj) {
    var index1,index2
    var dt,mth,yr,dt1,mth1
    var fleapyr = false
    var datestr = obj.value
    var label = getLabel(obj.name)

    if (!datestr || datestr == ""){
        return true // we validate only if there is a value
    }
    index1 = datestr.indexOf("/")
    if (index1 < 1 || index1 > 2) {
        alert("Enter the value in mm/dd/yyyy format in " + label + " field")
        return false;
    }
    mth = datestr.substring(0, index1)
    if (!checknum(mth)) {
        alert("Month must be numeric " + label + " field")
        obj.focus();
        return false;
    }
    if (mth.length == 1) {
        mth1 = '0' + mth
    } else {
        mth1 = mth
    }
    mth = parseInt(mth, 10)

    if (mth < 1 || mth > 12) {
        alert("Month must be between 1 and 12 in " + label + " field")
        obj.focus();
        return false
    }

    index2 = datestr.indexOf("/", index1 + 1)
    if (index2 < 3 || index2 > 5) {
        alert("Enter value in mm/dd/yyyy format in " + label + " field")
        obj.focus();
        return false;
    }

    dt = datestr.substring(index1 + 1, index2)
    if (!checknum(dt)) {
        alert("Day must be numeric in " + label + " field")
        obj.focus();
        return false;
    }

    if (dt.length == 1) {
        dt1 = '0' + dt
    }
    else {
        dt1 = dt
    }
    dt = parseInt(dt, 10)

    yr = datestr.substring(index2 + 1, index2 + 5)
    if (!checknum(yr)) {
        alert("Year must be numveric in " + label + " field")
        obj.focus();
        return false
    }

    if (yr.length != 4) {
        alert("Enter date value in mm/dd/yyyy format in " + label + " field")
        obj.focus();
        return false;
    }
    yr = parseInt(yr, 10)
    if (yr < 1800) {
        alert("Year in " + label + " field is unreasonable.")
        obj.focus();
        return false
    }
    if (yr % 4 == 0 && yr % 400 != 0) {
        maxdays = new Array(31, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        fleapyr = true
    } else {
        maxdays = new Array(31, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    }
    if (dt > maxdays[mth] || dt < 1) {
        alert("Day must be between 1 and " + maxdays[mth] + " in " + label + " field")
        obj.focus();
        return false
    }
    return true;
}

function checknum(numstr) {
 if (isNaN(parseInt(Number(numstr),10))){
	  return false
 } else {
	return true
 }
}
function isNumber(fieldnm) {
 var numstr=getObject(fieldnm).value
 if (isNaN(parseInt(Number(numstr),10))){
	alert("Enter a numeric value in "+getLabel(fieldnm)+" field")
	return false
  }
 return true
}

function isPosInteger(fieldnm) {
 val=getObject(fieldnm).value
 if(isNumber(fieldnm)) {
   if(parseInt(Number(val),10)>=0) {
	return true
    } else {
	alert("Enter a positive numeric value in "+getLabel(fieldnm)+" field")
	return false
    }
  }
}
function isGT(fieldnm1,fieldnm2) {
 var val1=parseInt(Number(getObject(fieldnm1).value),10)
 var val2=parseInt(Number(getObject(fieldnm2).value),10)
 if (val1==0 && val2==0) return false
 if (val1<=val2){
	alert("The value in  "+getLabel(fieldnm1)+" field should be greater than "+getLabel(fieldnm2)+" field")
	return false
  }
 return true
}

function isEQ(fieldnm1,fieldnm2) {
 var val1=parseInt(Number(getObject(fieldnm1).value),10)
 var val2=parseInt(Number(getObject(fieldnm2).value),10)
 if (val1==0 && val2==0) return true
 if (val1!=val2){
	alert("The value in "+getLabel(fieldnm1)+" field should be same as "+getLabel(fieldnm2)+" field")
	return false
  }
 return true
}

function isNEQ(fieldnm1,fieldnm2) {
 var val1=parseInt(Number(getObject(fieldnm1).value),10)
 var val2=parseInt(Number(getObject(fieldnm2).value),10)
 if (val1==0 && val2==0) return false
 if (val1==val2){
	alert("The value in "+getLabel(fieldnm1)+" field should not be the same as "+getLabel(fieldnm2)+" field")
	return false
  }
 return true
}

function isSame(fieldnm1,fieldnm2) {
 var val1=getObject(fieldnm1).value
 var val2=getObject(fieldnm2).value
 if (val1!=val2){
	alert("The value in "+getLabel(fieldnm1)+" field should be same as "+getLabel(fieldnm2)+" field")
	return false
  }
 return true
}
function isEmail(name){
	var obj=getObject(name)
	if ( obj.value != "" )
	{
		if (obj.value.indexOf("@",1) == -1){
		 alert("Enter a valid e-mail address.");
		 return false;
		}
		if (obj.value.indexOf(".",obj.value.indexOf("@")+1) == -1){
  		 alert("Enter a valid e-mail address.");
  		 return false;
		}
	}
	return true;
}
function isPhone(fieldnm) {
	var checkOK = "0123456789-- \t\r\n\f";
	var checkStr = getObject(fieldnm).value;
	var allValid = true;
	var len=checkStr.length

	for (k = 0;  k < len;  k++) {
	  ch = checkStr.charAt(k);
     if (checkOK.indexOf(ch)!=-1) {
 	     allValid=true
	  } else {
	    allValid=false
		 break
	  }
    }
    if (!allValid) {
		alert("Enter only digit, whitespace and - characters in " + getLabel(fieldnm) + " field.");
	   return false
	 }
   return true
}


/* FUNCTIONS TO NOTIFY USER OF INPUT REQUIREMENTS OR MISTAKES. */


// Display data entry prompt string s in status bar.

function promptEntry (s)
{   window.status = pEntryPrompt + s
}




// Notify user that required field theField is empty.
// String s describes expected contents of theField.value.
// Put focus in theField and return false.

function warnEmpty (theField, s)
{   theField.focus()
    alert(mPrefix + s + mSuffix)
    return false
}



// Notify user that contents of field theField are invalid.
// String s describes expected contents of theField.value.
// Put select theField, pu focus in it, and return false.

function warnInvalid (theField, s)
{   theField.focus()
    theField.select()
    alert(s)
    return false
}




/* FUNCTIONS TO INTERACTIVELY CHECK VARIOUS FIELDS. */

// checkString (TEXTFIELD theField, STRING s, [, BOOLEAN emptyOK==false])
//
// Check that string theField.value is not all whitespace.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function checkString (theField, s, emptyOK)
{   // Next line is needed on NN3 to avoid "undefined is not a number" error
    // in equality comparison below.
    if (checkString.arguments.length == 2) emptyOK = defaultEmptyOK;
    if ((emptyOK == true) && (isEmpty(theField.value))) return true;
    if (isWhitespace(theField.value))
       return warnEmpty (theField, s);
    else return true;
}



// checkStateCode (TEXTFIELD theField [, BOOLEAN emptyOK==false])
//
// Check that string theField.value is a valid U.S. state code.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function checkStateCode (theField, emptyOK)
{   if (checkStateCode.arguments.length == 1) emptyOK = defaultEmptyOK;
    if ((emptyOK == true) && (isEmpty(theField.value))) return true;
    else
    {  theField.value = theField.value.toUpperCase();
       if (!isStateCode(theField.value, false))
          return warnInvalid (theField, iStateCode);
       else return true;
    }
}



// takes ZIPString, a string of 5 or 9 digits;
// if 9 digits, inserts separator hyphen

function reformatZIPCode (ZIPString)
{   if (ZIPString.length == 5) return ZIPString;
    else return (reformat (ZIPString, "", 5, "-", 4));
}




// checkZIPCode (TEXTFIELD theField [, BOOLEAN emptyOK==false])
//
// Check that string theField.value is a valid ZIP code.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function checkZIPCode (theField, emptyOK)
{   if (checkZIPCode.arguments.length == 1) emptyOK = defaultEmptyOK;
    if ((emptyOK == true) && (isEmpty(theField.value))) return true;
    else
    { var normalizedZIP = stripCharsInBag(theField.value, ZIPCodeDelimiters)
      if (!isZIPCode(normalizedZIP, false))
         return warnInvalid (theField, iZIPCode);
      else
      {  // if you don't want to insert a hyphen, comment next line out
         theField.value = reformatZIPCode(normalizedZIP)
         return true;
      }
    }
}



// takes USPhone, a string of 10 digits
// and reformats as (123) 456-789

function reformatUSPhone (USPhone)
{   return (reformat (USPhone, "(", 3, ") ", 3, "-", 4))
}



// checkUSPhone (TEXTFIELD theField [, BOOLEAN emptyOK==false])
//
// Check that string theField.value is a valid US Phone.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function checkUSPhone (theField, emptyOK)
{   if (checkUSPhone.arguments.length == 1) emptyOK = defaultEmptyOK;
    if ((emptyOK == true) && (isEmpty(theField.value))) return true;
    else
    {  var normalizedPhone = stripCharsInBag(theField.value, phoneNumberDelimiters)
       if (!isUSPhoneNumber(normalizedPhone, false))
          return warnInvalid (theField, iUSPhone);
       else
       {  // if you don't want to reformat as (123) 456-789, comment next line out
          theField.value = reformatUSPhone(normalizedPhone)
          return true;
       }
    }
}



// checkInternationalPhone (TEXTFIELD theField [, BOOLEAN emptyOK==false])
//
// Check that string theField.value is a valid International Phone.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function checkInternationalPhone (theField, emptyOK)
{   if (checkInternationalPhone.arguments.length == 1) emptyOK = defaultEmptyOK;
    if ((emptyOK == true) && (isEmpty(theField.value))) return true;
    else
    {  if (!isInternationalPhoneNumber(theField.value, false))
          return warnInvalid (theField, iWorldPhone);
       else return true;
    }
}



// checkEmail (TEXTFIELD theField [, BOOLEAN emptyOK==false])
//
// Check that string theField.value is a valid Email.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function checkEmail (theField, emptyOK)
{   if (checkEmail.arguments.length == 1) emptyOK = defaultEmptyOK;
    if ((emptyOK == true) && (isEmpty(theField.value))) return true;
    else if (!isEmail(theField.value, false))
       return warnInvalid (theField, iEmail);
    else return true;
}



// takes SSN, a string of 9 digits
// and reformats as 123-45-6789

function reformatSSN (SSN)
{   return (reformat (SSN, "", 3, "-", 2, "-", 4))
}


// Check that string theField.value is a valid SSN.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function checkSSN (theField, emptyOK)
{   if (checkSSN.arguments.length == 1) emptyOK = defaultEmptyOK;
    if ((emptyOK == true) && (isEmpty(theField.value))) return true;
    else
    {  var normalizedSSN = stripCharsInBag(theField.value, SSNDelimiters)
       if (!isSSN(normalizedSSN, false))
          return warnInvalid (theField, iSSN);
       else
       {  // if you don't want to reformats as 123-456-7890, comment next line out
          theField.value = reformatSSN(normalizedSSN)
          return true;
       }
    }
}




// Check that string theField.value is a valid Year.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function checkYear (theField, emptyOK)
{   if (checkYear.arguments.length == 1) emptyOK = defaultEmptyOK;
    if ((emptyOK == true) && (isEmpty(theField.value))) return true;
    if (!isYear(theField.value, false))
       return warnInvalid (theField, iYear);
    else return true;
}


// Check that string theField.value is a valid Month.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function checkMonth (theField, emptyOK)
{   if (checkMonth.arguments.length == 1) emptyOK = defaultEmptyOK;
    if ((emptyOK == true) && (isEmpty(theField.value))) return true;
    if (!isMonth(theField.value, false))
       return warnInvalid (theField, iMonth);
    else return true;
}


// Check that string theField.value is a valid Day.
//
// For explanation of optional argument emptyOK,
// see comments of function isInteger.

function checkDay (theField, emptyOK)
{   if (checkDay.arguments.length == 1) emptyOK = defaultEmptyOK;
    if ((emptyOK == true) && (isEmpty(theField.value))) return true;
    if (!isDay(theField.value, false))
       return warnInvalid (theField, iDay);
    else return true;
}



// checkDate (yearField, monthField, dayField, STRING labelString [, OKtoOmitDay==false])
//
// Check that yearField.value, monthField.value, and dayField.value
// form a valid date.
//
// If they don't, labelString (the name of the date, like "Birth Date")
// is displayed to tell the user which date field is invalid.
//
// If it is OK for the day field to be empty, set optional argument
// OKtoOmitDay to true.  It defaults to false.

function checkDate (yearField, monthField, dayField, labelString, OKtoOmitDay)
{   // Next line is needed on NN3 to avoid "undefined is not a number" error
    // in equality comparison below.
    if (checkDate.arguments.length == 4) OKtoOmitDay = false;
    if (!isYear(yearField.value)) return warnInvalid (yearField, iYear);
    if (!isMonth(monthField.value)) return warnInvalid (monthField, iMonth);
    if ( (OKtoOmitDay == true) && isEmpty(dayField.value) ) return true;
    else if (!isDay(dayField.value))
       return warnInvalid (dayField, iDay);
    if (isDate (yearField.value, monthField.value, dayField.value))
       return true;
    alert (iDatePrefix + labelString + iDateSuffix)
    return false
}

/**
 Checks if the string starts with the specified sub string.

 @param str the sub string to check for.
 @returns true if the string starts with the sub string, otherwise
  false.

"H141500.startsWith("H1415") would return true
"H141500".startsWith("H1416") would return false .
*/
String.prototype.startsWith = function (str) {
  return this.indexOf(str) === 0;
};

/**
 Checks if the string ends with the specified sub string.

 @param str the sub string to check for.
 @returns true if the string ends with the sub string, otherwise
  false.

  "H001415".endsWith("1415") would return true
  "H001415".endsWith("1416") would return false
*/
String.prototype.endsWith = function (str) {
  var offset = this.length - str.length;
  return offset >= 0 && this.lastIndexOf(str) === offset;
};

// Polyfill for the String trim() method
if (!String.prototype.trim) {
    String.prototype.trim = function () {
        return this.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, '');
    };
}

function ltrim(str) {
 var strlen=str.length
 if(strlen>0) {
  if(str.charCodeAt(0)==32) {
   return ltrim(str.substr(1))
  } else {
   return str
  }
 } else {
  return str
 }
}

function rtrim(str) {
 var strlen=str.length
 if(strlen>0) {
  if(str.charCodeAt(strlen-1)==32) {
   return rtrim(str.slice(0,-1))
  } else {
   return str
  }
 } else {
  return str
 }
}

function trim(str) {
 return ltrim(rtrim(str))
}




// Validate credit card info.

function checkCreditCard (radio, theField)
{   var cardType = getRadioButtonValue (radio)
    var normalizedCCN = stripCharsInBag(theField.value, creditCardDelimiters)
    if (!isCardMatch(cardType, normalizedCCN))
       return warnInvalid (theField, iCreditCardPrefix + cardType + iCreditCardSuffix);
    else
    {  theField.value = normalizedCCN
       return true
    }
}



/*  ================================================================
    Credit card verification functions
    Originally included as Starter Application 1.0.0 in LivePayment.
    20 Feb 1997 modified by egk:
           changed naming convention to initial lowercase
                  (isMasterCard instead of IsMasterCard, etc.)
           changed isCC to isCreditCard
           retained functions named with older conventions from
                  LivePayment as stub functions for backward
                  compatibility only
           added "AMERICANEXPRESS" as equivalent of "AMEX"
                  for naming consistency
    ================================================================ */


/*  ================================================================
    FUNCTION:  isCreditCard(st)

    INPUT:     st - a string representing a credit card number

    RETURNS:  true, if the credit card number passes the Luhn Mod-10
		    test.
	      false, otherwise
    ================================================================ */

function isCreditCard(st) {
  // Encoding only works on cards with less than 19 digits
  if (st.length > 19)
    return (false);

  sum = 0; mul = 1; l = st.length;
  for (i = 0; i < l; i++) {
    digit = st.substring(l-i-1,l-i);
    tproduct = parseInt(digit ,10)*mul;
    if (tproduct >= 10)
      sum += (tproduct % 10) + 1;
    else
      sum += tproduct;
    if (mul == 1)
      mul++;
    else
      mul--;
  }
// Uncomment the following line to help create credit card numbers
// 1. Create a dummy number with a 0 as the last digit
// 2. Examine the sum written out
// 3. Replace the last digit with the difference between the sum and
//    the next multiple of 10.

//  document.writeln("<BR>Sum      = ",sum,"<BR>");
//  alert("Sum      = " + sum);

  if ((sum % 10) == 0)
    return (true);
  else
    return (false);

} // END FUNCTION isCreditCard()



/*  ================================================================
    FUNCTION:  isVisa()

    INPUT:     cc - a string representing a credit card number

    RETURNS:  true, if the credit card number is a valid VISA number.

	      false, otherwise

    Sample number: 4111 1111 1111 1111 (16 digits)
    ================================================================ */

function isVisa(cc)
{
  if (((cc.length == 16) || (cc.length == 13)) &&
      (cc.substring(0,1) == 4))
    return isCreditCard(cc);
  return false;
}  // END FUNCTION isVisa()




/*  ================================================================
    FUNCTION:  isMasterCard()

    INPUT:     cc - a string representing a credit card number

    RETURNS:  true, if the credit card number is a valid MasterCard
		    number.

	      false, otherwise

    Sample number: 5500 0000 0000 0004 (16 digits)
    ================================================================ */

function isMasterCard(cc)
{
  firstdig = cc.substring(0,1);
  seconddig = cc.substring(1,2);
  if ((cc.length == 16) && (firstdig == 5) &&
      ((seconddig >= 1) && (seconddig <= 5)))
    return isCreditCard(cc);
  return false;

} // END FUNCTION isMasterCard()





/*  ================================================================
    FUNCTION:  isAmericanExpress()

    INPUT:     cc - a string representing a credit card number

    RETURNS:  true, if the credit card number is a valid American
		    Express number.

	      false, otherwise

    Sample number: 340000000000009 (15 digits)
    ================================================================ */

function isAmericanExpress(cc)
{
  firstdig = cc.substring(0,1);
  seconddig = cc.substring(1,2);
  if ((cc.length == 15) && (firstdig == 3) &&
      ((seconddig == 4) || (seconddig == 7)))
    return isCreditCard(cc);
  return false;

} // END FUNCTION isAmericanExpress()




/*  ================================================================
    FUNCTION:  isDinersClub()

    INPUT:     cc - a string representing a credit card number

    RETURNS:  true, if the credit card number is a valid Diner's
		    Club number.

	      false, otherwise

    Sample number: 30000000000004 (14 digits)
    ================================================================ */

function isDinersClub(cc)
{
  firstdig = cc.substring(0,1);
  seconddig = cc.substring(1,2);
  if ((cc.length == 14) && (firstdig == 3) &&
      ((seconddig == 0) || (seconddig == 6) || (seconddig == 8)))
    return isCreditCard(cc);
  return false;
}



/*  ================================================================
    FUNCTION:  isCarteBlanche()

    INPUT:     cc - a string representing a credit card number

    RETURNS:  true, if the credit card number is a valid Carte
		    Blanche number.

	      false, otherwise
    ================================================================ */

function isCarteBlanche(cc)
{
  return isDinersClub(cc);
}




/*  ================================================================
    FUNCTION:  isDiscover()

    INPUT:     cc - a string representing a credit card number

    RETURNS:  true, if the credit card number is a valid Discover
		    card number.

	      false, otherwise

    Sample number: 6011000000000004 (16 digits)
    ================================================================ */

function isDiscover(cc)
{
  first4digs = cc.substring(0,4);
  if ((cc.length == 16) && (first4digs == "6011"))
    return isCreditCard(cc);
  return false;

} // END FUNCTION isDiscover()





/*  ================================================================
    FUNCTION:  isEnRoute()

    INPUT:     cc - a string representing a credit card number

    RETURNS:  true, if the credit card number is a valid enRoute
		    card number.

	      false, otherwise

    Sample number: 201400000000009 (15 digits)
    ================================================================ */

function isEnRoute(cc)
{
  first4digs = cc.substring(0,4);
  if ((cc.length == 15) &&
      ((first4digs == "2014") ||
       (first4digs == "2149")))
    return isCreditCard(cc);
  return false;
}



/*  ================================================================
    FUNCTION:  isJCB()

    INPUT:     cc - a string representing a credit card number

    RETURNS:  true, if the credit card number is a valid JCB
		    card number.

	      false, otherwise
    ================================================================ */

function isJCB(cc)
{
  first4digs = cc.substring(0,4);
  if ((cc.length == 16) &&
      ((first4digs == "3088") ||
       (first4digs == "3096") ||
       (first4digs == "3112") ||
       (first4digs == "3158") ||
       (first4digs == "3337") ||
       (first4digs == "3528")))
    return isCreditCard(cc);
  return false;

} // END FUNCTION isJCB()



/*  ================================================================
    FUNCTION:  isAnyCard()

    INPUT:     cc - a string representing a credit card number

    RETURNS:  true, if the credit card number is any valid credit
		    card number for any of the accepted card types.

	      false, otherwise
    ================================================================ */

function isAnyCard(cc)
{
  if (!isCreditCard(cc))
    return false;
  if (!isMasterCard(cc) && !isVisa(cc) && !isAmericanExpress(cc) && !isDinersClub(cc) &&
      !isDiscover(cc) && !isEnRoute(cc) && !isJCB(cc)) {
    return false;
  }
  return true;

} // END FUNCTION isAnyCard()



/*  ================================================================
    FUNCTION:  isCardMatch()

    INPUT:    cardType - a string representing the credit card type
	      cardNumber - a string representing a credit card number

    RETURNS:  true, if the credit card number is valid for the particular
	      credit card type given in "cardType".

	      false, otherwise
    ================================================================ */

function isCardMatch (cardType, cardNumber)
{

	cardType = cardType.toUpperCase();
	var doesMatch = true;

	if ((cardType == "VISA") && (!isVisa(cardNumber)))
		doesMatch = false;
	if ((cardType == "MASTERCARD") && (!isMasterCard(cardNumber)))
		doesMatch = false;
	if ( ( (cardType == "AMERICANEXPRESS") || (cardType == "AMEX") )
                && (!isAmericanExpress(cardNumber))) doesMatch = false;
	if ((cardType == "DISCOVER") && (!isDiscover(cardNumber)))
		doesMatch = false;
	if ((cardType == "JCB") && (!isJCB(cardNumber)))
		doesMatch = false;
	if ((cardType == "DINERS") && (!isDinersClub(cardNumber)))
		doesMatch = false;
	if ((cardType == "CARTEBLANCHE") && (!isCarteBlanche(cardNumber)))
		doesMatch = false;
	if ((cardType == "ENROUTE") && (!isEnRoute(cardNumber)))
		doesMatch = false;
	return doesMatch;

}  // END FUNCTION CardMatch()




/*  ================================================================
    The below stub functions are retained for backward compatibility
    with the original LivePayment code so that it should be possible
    in principle to swap in this new module as a replacement for the
    older module without breaking existing code.  (There are no
    guarantees, of course, but it should work.)

    When writing new code, do not use these stub functions; use the
    functions defined above.
    ================================================================ */

function IsCC (st) {
    return isCreditCard(st);
}

function IsVisa (cc)  {
  return isVisa(cc);
}

function IsVISA (cc)  {
  return isVisa(cc);
}

function IsMasterCard (cc)  {
  return isMasterCard(cc);
}

function IsMastercard (cc)  {
  return isMasterCard(cc);
}

function IsMC (cc)  {
  return isMasterCard(cc);
}

function IsAmericanExpress (cc)  {
  return isAmericanExpress(cc);
}

function IsAmEx (cc)  {
  return isAmericanExpress(cc);
}

function IsDinersClub (cc)  {
  return isDinersClub(cc);
}

function IsDC (cc)  {
  return isDinersClub(cc);
}

function IsDiners (cc)  {
  return isDinersClub(cc);
}

function IsCarteBlanche (cc)  {
  return isCarteBlanche(cc);
}

function IsCB (cc)  {
  return isCarteBlanche(cc);
}

function IsDiscover (cc)  {
  return isDiscover(cc);
}

function IsEnRoute (cc)  {
  return isEnRoute(cc);
}

function IsenRoute (cc)  {
  return isEnRoute(cc);
}

function IsJCB (cc)  {
  return isJCB(cc);
}

function IsAnyCard(cc)  {
  return isAnyCard(cc);
}

function IsCardMatch (cardType, cardNumber)  {
  return isCardMatch (cardType, cardNumber);
}

// clearFormFields(FORM OBJECT theForm, TRUE/FALSE clear hidden fields, TRUE/FALSE clear disabled/readonly fields)
//
// Clears all text, select-one, select-multiple, textarea, radiobutton and checkbox fields in a form.
//
function clearFormFields(theForm, clearHidden, clearReadOnly) {
    if (window.clearFormFields.arguments.length < 2)
        clearHidden = false;
    if (window.clearFormFields.arguments.length < 3)
        clearReadOnly = false;
    /* Clear all fields. */
    var coll = theForm.elements;
    var i = 0;
    var list = '';
    for (i = 0; i < coll.length; i++) {
        var obj = coll[i];
        list += 'type for obj ' + i + ' = ' + obj.type + ';  ';
        var checkHidden = (obj.style.display != 'none');
        if(window["useJqxGrid"])
            checkHidden = (!$(obj).hasClass("dti-hide"));
        if ((!obj.disabled && checkHidden) || (clearReadOnly && clearReadOnly == true )) {
            if (isFieldHidden(coll[i].name) && !clearHidden) {
                continue;
            }

            if (!isFieldEditable(coll[i].name) && !clearReadOnly) {
                continue;
            }

            switch (coll[i].type) {
                case 'select-multiple':
                    coll[i].value = '';
                    var x = coll[i].options.length;
                    for (var y = 0; y < x; y++)
                        coll[i].options[y].selected = false;
                    break;
                case 'text':
                case 'textarea':
                case 'file':
                    coll[i].value = '';
                    break;
                case 'select-one':
                    coll[i].value = '-1';
                    break;
                case 'checkbox':
                case 'radio':
                    coll[i].checked = false;
                    break;
                case 'hidden':
                    if (clearHidden) coll[i].value = '';
                    break;
            }
        }
    }
}

// isDate2OnOrAfterDate1 (STRING date1Value, STRING date2Value)
//
// Returns "Y" if strings date1Value and date2Value are both dates (mm/dd/yyyy)
// and date2Value is on or after date1Value.
// Returns "N" if strings date1Value and date2Value are both dates (mm/dd/yyyy)
// and date2Value is before date1Value.
// Returns "U" if either string date1Value or string date2Value
// is not a date or both are not dates.
//
function isDate2OnOrAfterDate1(date1Value, date2Value) {
  if (date1Value == null || date1Value == '') {
    return 'U';
  }
  if (date2Value == null || date2Value == '') {
    return 'U';
  }
  // Make sure the date is in mm/dd/yyyy format.
  var reTestDate = /[0-9]{2,2}\/[0-9]{2,2}\/[0-9]{4,4}/;
  if (!reTestDate.test(date1Value) || !reTestDate.test(date2Value)) {
    return 'U';
  }
  // Put the dates in yyyymmdd format for the comparison.
  var newDate1Value = date1Value.substr(6, 4) + date1Value.substr(0, 2) + date1Value.substr(3, 2);
  var newDate2Value = date2Value.substr(6, 4) + date2Value.substr(0, 2) + date2Value.substr(3, 2);
  if (newDate1Value > newDate2Value) {
    return 'N';
  }
  else {
    return 'Y';
  }
}

// isStringValue(STRING value[, BOOLEAN isCode])
//
// Returns true if a string value contains at least one non-space character.
// If isCode is true, then returns false if value is "-1".
//
function isStringValue(value) {
  return isStringValue(value, false);
}

function isStringValue(value, isCode) {
  if (value == null || value == '') {
    return false;
  }
  if (isCode == true && value == '-1') {
    return false;
  }
//  alert('value = ' + value);
  var reTestForSpace = /[  ]+/;
  var reTestForNonSpace = /[^ ]+/;
  if (reTestForSpace.test(value) && !reTestForNonSpace.test(value)) {
    return false;
  }
  return true;
}

// isStringCodeValue(STRING value)
//
// Returns true if a string value representing a code
// contains at least one non-space character and is not "-1".
//
function isStringCodeValue(value) {
  return isStringValue(value, true);
}

// isNum2GrtThanOrEqToNum1 (STRING num1Value, STRING num2Value)
//
// Returns "Y" if strings num1Value and num2Value are both numbers
// and num2Value is greater than or equal to num1Value.
// Returns "N" if strings num1Value and num2Value are both numbers
// and num2Value is less than num1Value.
// Returns "U" if either string num1Value or string num2Value
// is not a number or both are not numbers.
//
function isNum2GrtThanOrEqToNum1(num1Value, num2Value) {
  if (!isStringValue(num1Value, false) || !isStringValue(num2Value, false)) {
    return 'U';
  }
  else if (!isFloat(num1Value) || !isFloat(num2Value)) {
    return 'U';
  }
  else if (isNaN(num1Value) || isNaN(num2Value)) {
    return 'U';
  }
  var num1 = parseFloat(num1Value);
  var num2 = parseFloat(num2Value);
  if (num2 >= num1) {
    return 'Y';
  }
  else {
    return 'N';
  }
}
//
// Returns the app context in the form /appcontext
//
function getAppContext() {
    var appContext="";
    if(__appContextRoot){  /* Use it if we defined this already  */
      appContext=__appContextRoot;
    }
    else {
    var href = document.location.href;
	var i1 = href.indexOf("//");
	var i2 = href.indexOf("/",i1+2);
	var i3 = href.indexOf("/",i2+1);
      appContext=href.substring(i2,i3);
    }
    return appContext;

}
/* This function is for copy the selected description to the XML Data Island, then what user see is the description, not the code itself.

fieldSelected  is field selected

xmlGrid is the the xml data island representation of the grid

dispFieldName is the column name(in <ROW .. >... </ROW>) in xml data island for the displayed description (Either Long or Short description, not both), it begins with upper "C" in DTI web framework

**/

function copySelectedTextToXMLDataIsland(fieldSelected , xmlGrid , dispFieldName){

var dispText=fieldSelected [fieldSelected.selectedIndex].text;
if(isStringCodeValue(fieldSelected.value)) {
  xmlGrid.recordset(dispFieldName).value=dispText;
} else {
  dispText='';
  xmlGrid.recordset(dispFieldName).value=dispText;
}
return dispText;
}

function alphaWithSpecial(id, addlCharsAllowed)
{
  if(addlCharsAllowed==null)
     return checkKeyPress("ALPHASPL", id);
  else
     return checkKeyPress("ALPHASPL",id,addlCharsAllowed);
}

function alphaLowerWithSpecial(id, addlCharsAllowed)
{
  if(addlCharsAllowed==null)
     return checkKeyPress("ALPHALOWERSPL", id);
  else
     return checkKeyPress("ALPHALOWERSPL",id,addlCharsAllowed);
}

function alphaUpperWithSpecial(id, addlCharsAllowed)
{
  if(addlCharsAllowed==null)
     return checkKeyPress("ALPHAUPPERSPL", id);
  else
     return checkKeyPress("ALPHAUPPERSPL",id,addlCharsAllowed);
}

function alpha(id, addlCharsAllowed)
{
  if(addlCharsAllowed==null)
     return checkKeyPress("ALPHA", id);
  else
     return checkKeyPress("ALPHA",id,addlCharsAllowed);
}

function alphaLower(id, addlCharsAllowed)
{
  if(addlCharsAllowed==null)
     return checkKeyPress("ALPHALOWER", id);
  else
     return checkKeyPress("ALPHALOWER",id,addlCharsAllowed);
}

function alphaUpper(id, addlCharsAllowed)
{
  if(addlCharsAllowed==null)
     return checkKeyPress("ALPHAUPPER", id);
  else
     return checkKeyPress("ALPHAUPPER",id,addlCharsAllowed);
}

function alphaNumeric(id, addlCharsAllowed)
{
  if(addlCharsAllowed==null)
     return checkKeyPress("ALPHANUM", id);
  else
     return checkKeyPress("ALPHANUM",id,addlCharsAllowed);
}

function alphaNumericLower(id, addlCharsAllowed)
{
  if(addlCharsAllowed==null)
     return checkKeyPress("ALPHANUMLOWER", id);
  else
     return checkKeyPress("ALPHANUMLOWER",id,addlCharsAllowed);
}

function alphaNumericUpper(id, addlCharsAllowed)
{
  if(addlCharsAllowed==null)
     return checkKeyPress("ALPHANUMUPPER", id);
  else
     return checkKeyPress("ALPHANUMUPPER",id,addlCharsAllowed);
}

function alphaNumericWithSpecial(id)
{
   return checkKeyPress("ALPHANUMSPL", id);
}

function alphaNumericLowerWithSpecial(id)
{
   return checkKeyPress("ALPHANUMLOWERSPL", id);
}

function alphaNumericUpperWithSpecial(id)
{
   return checkKeyPress("ALPHANUMUPPERSPL", id);
}

function number(id)
{
  return checkKeyPress("NUMBER", id);
}

function number(id, addlCharsAllowed)
{
  if(addlCharsAllowed==null)
     return checkKeyPress("NUMBER", id);
  else
     return checkKeyPress("NUMBER", id, addlCharsAllowed);
}

function negativeNumber(id)
{
  return checkKeyPress("NEGATIVENUMBER", id);
}

function currency(id)
{
  return checkKeyPress("CURRENCY",id);
}

function negativeCurrency(id)
{
  return checkKeyPress("NEGATIVECURRENCY",id);
}

function phone(id)
{
  return checkKeyPress("PHONE", id);
}

function zip(id)
{
  return checkKeyPress("ZIP", id);
}

function dateField(id)
{
  return checkKeyPress("DATE", id)
}

function checkKeyPress(TextMode,id,AddlCharsAllowed)
{
  var UnderScore = "_";
  var AllSplChars = "\\~!@#$%^&*()-_=+/?>.<,:;'|[]{}\"";
  var PhoneSplChars = "()-xX";
  var ZipCodeSplChars = "-";
  var DecimalSplChars = ".";
  var NegativeSign = "-";
  var DateSplChars = "/";
  var NumberChars = digits;
  var AlphaChars = lowercaseLetters + uppercaseLetters;
  var retvalue;
  var evt = dti.inpututils.getWindowEvent();
  var KeyPressChar = String.fromCharCode(evt.keyCode) ;
  var isTextSelected = "N";
  var isCharTypedModified = false;

  if(id!=null)
  {
     if(id.getAttribute("isSelected")=='Y')
     {
       isTextSelected = 'Y';
       id.removeAttribute("isSelected")
     }
  }

  //alert(evt.keyCode + ' ' + TextMode + ' ' + TextMode.substr(0,5))
  //alert(KeyPressChar+ ' ' + TextMode + ' ' + TextMode.substr(TextMode.length-1,1))
  //alert(event.layerX + '  ' + event.pageX + '   ' + event.screenX);
  retvalue = false ;
  if (TextMode=='DATE' && (id.value=='00/00/0000' || id.value=='mm/dd/yyyy'))
      id.value ='';

  if(AddlCharsAllowed==null)
     AddlCharsAllowed="";

  if (evt.keyCode==13 || evt.keyCode==8)
      retvalue=true;
  if (AddlCharsAllowed.indexOf(KeyPressChar) >= 0)
      retvalue = true ;

  if (NumberChars.indexOf(KeyPressChar) >= 0 && (TextMode=='DATE' || TextMode=='NUMBER' || TextMode=='CURRENCY' || TextMode=='PHONE' || TextMode=='ZIP' || TextMode.substr(0,8) == 'ALPHANUM') )
      retvalue = true ;

  if(DateSplChars.indexOf(KeyPressChar) >= 0 && (TextMode=='DATE'))
     retvalue = true ;

  if((AlphaChars.indexOf(KeyPressChar) >= 0) && (TextMode.substr(0,5)=='ALPHA'))
     retvalue = true ;

  if((AllSplChars.indexOf(KeyPressChar) >= 0) && (TextMode.substr(TextMode.length-3,TextMode.length)=='SPL'))
     retvalue = true ;

  //alert(DecimalSplChars.indexOf(KeyPressChar))
  if(DecimalSplChars.indexOf(KeyPressChar) >= 0 && TextMode=='CURRENCY')
  {
   if(id.value.indexOf('.')==-1)
      retvalue = true ;
  }
  if(NegativeSign.indexOf(KeyPressChar) >= 0 && (TextMode=='NEGATIVECURRENCY' || TextMode=='NEGATIVENUMBER'))
  {
    if(id.value.indexOf('-') == -1 || isTextSelected=='Y')
    {
     retvalue = true;
    }
  }

  if(PhoneSplChars.indexOf(KeyPressChar) >= 0 && TextMode=='PHONE')
      retvalue = true ;
  if(ZipCodeSplChars.indexOf(KeyPressChar) >= 0 && TextMode=='ZIP')
      retvalue = true ;
  //alert(evt.keyCode + '  ' + TextMode.substr(0,9))
  if((evt.keyCode >= 97 && evt.keyCode <= 122) && (TextMode.substr(0,10)=='ALPHAUPPER' || TextMode.substr(0,13)=='ALPHANUMUPPER')) {
      var retVal = dti.inpututils.replaceTypedChar(evt, dti.stringutils.toUpperCase);
      if (typeof retVal != "undefined") {
          isCharTypedModified = true;
      }
  }

  if((evt.keyCode >= 65 && evt.keyCode <= 90) && (TextMode.substr(0,10) == 'ALPHALOWER' || TextMode.substr(0,13)=='ALPHANUMLOWER')) {
      var retVal = dti.inpututils.replaceTypedChar(evt, dti.stringutils.toLowerCase);
      if (typeof retVal != "undefined") {
          isCharTypedModified = true;
      }
  }

  if(retvalue && (TextMode=='DATE') && isTextSelected=='N')
  {
     if(DateSplChars.indexOf(KeyPressChar)==-1)
     {
       if(id.value.length>=2)
       {
         //First Slash
         if(id.value.indexOf("/") == -1)
         {
           id.value = id.value + "/"
         }
         else
         {
           if(id.value.substr(id.value.indexOf("/",0)+1).length>=2)
           {
              if(id.value.substr(id.value.indexOf("/",0)+1).indexOf("/")==-1)
              {
                 id.value = id.value + "/"
              }
              else
              {
                 var SlashLoc = id.value.indexOf("/",0) + 1;
                 SlashLoc = (id.value.substr(SlashLoc).indexOf("/") + 1) + SlashLoc
                 //Check For Year Length After 2 Slashes
                 if(id.value.substr(SlashLoc).length > 4 && !(id.value.substr(SlashLoc)=="yyyy"))
                 {
                    alert(id.value.substr(SlashLoc))
                    retvalue = false;
                 }
              }
           }
         }
       }
     }
     else
     {
        var SlashLoc = id.value.indexOf("/")
        //Check For First Char Not Be "/"
        //alert('check for > 2 slashes')
        if(SlashLoc==-1 && id.value.length==0)
           retvalue = false ;
        if(SlashLoc>0 && retvalue )
        {
           //alert('check for continous slashes')
           //Check For Continuous Slashes
           if(id.value.substr(SlashLoc+1).indexOf("/")==-1 && id.value.substr(SlashLoc+1).length==0)
              retvalue = false ;
           SlashLoc = id.value.substr(SlashLoc+1).indexOf("/")
           if(SlashLoc>0 && retvalue)
           {
              //alert('check for > 2 slashes')
              SlashLoc = id.value.substr(SlashLoc+1).indexOf("/")
              //Check For More Than 2 Slashes
              if(SlashLoc>0)
                 retvalue = false ;
           }
        }
     }
  }
  if (isCharTypedModified) {
      retvalue = false;
  }
  return retvalue;
}

function formatPercentage(id)
{
  if(id.value=='')
      id.value = '0';
  if(id.value.substr(id.value.length-1,1)!='%')
     id.value = id.value + '%';
}

function formatZip(id,format,errClassName,validClassName)
{
  var i=0;
  var j=0
  var finalvalue = '' ;
  //alert(format)
  if(id.value.length==0)
     id.value = format ;
  while(i <= id.value.length-1)
  {
    //alert(id.value.substr(i,1))
    if(j<=format.length-1)
    {
      if(format.substr(j,1)=='#' || format.substr(j,1)==id.value.substr(i,1))
      {
       finalvalue = finalvalue + id.value.substr(i,1)
       i=i+1
      }
      else
      {
       finalvalue = finalvalue + format.substr(j,1)
      }
      j=j+1;
    }
    else
      break;  //do the while only until we scan the format specification
    //alert(finalvalue)
  }
  if(j<format.length-1)
  {
    for(i=j;i<=format.length-1;i++)
    {
      if(format.substr(i,1)=='#')
         finalvalue = finalvalue + '#'
      else
         finalvalue = finalvalue + format.substr(i,1)
    }
  }
  if(finalvalue==format)
  {
    finalvalue='';
  }
  //alert(finalvalue)
  if(finalvalue.indexOf("#")>=0)
  {
     if(errClassName)
        id.className = errClassName ;
     else
        id.style.backgroundColor='red';
  }
  else
  {
     if(validClassName)
        id.className = validClassName ;
     else
        id.style.backgroundColor='white';
  }
  id.value = finalvalue ;
}

function formatMask(isNumberField, field, format) {
    var isNegNumber = false;
    var isNegNumberByParen = false;
    var isNegNumberByLeft = false;

    if(field.value!='')
    {
       if(field.value.length > 0 && isNumberField)
       {
         if(field.value.substr(0,1)=='-' || field.value.substr(field.value.length-1,1)=='-')
          {
             field.value = field.value.replace('-','');
             isNegNumber = true;
             isNegNumberByLeft = (field.value.substr(0,1)=='-');
             // If the format mask begins and ends with "(" and ")" respectively, then replace the negative sign (-)
             // with "(" and ")" mask.
             if (format.substr(0,1)=="(" && format.substr(format.length-1,1)==")") {
                 isNegNumberByParen = true;
                 format = format.substr(1, format.length-1); //remove the "(" & ")" from the format mask.
             }
          }
       }
    }
    var finalvalue = "";
    if (isNumberField) {
      finalvalue = formatMaskForNumberField(field, format);
    } else {
      finalvalue = formatMaskForAlphaNumericField(field, format);
    }
    //alert(finalvalue)
    if (finalvalue) {
       if(isNegNumber) {
         if (isNegNumberByParen) {
             field.value = "(" + finalvalue + ")";
         } else {
             if (isNegNumberByLeft) {
               field.value = "-" + finalvalue;
             } else {
                 field.value = finalvalue + "-";
             }
         }
       }
       else {
          field.value = finalvalue ;
       }
      return true;
   } else {
      return false;
   }
}

function formatMaskForAlphaNumericField(field, format) {
  var ActualFormat = format;
  var NumberChars = digits;
  var AlphaChars = lowercaseLetters + uppercaseLetters;

  var finalvalue = applyAlphaNumericFormat(ActualFormat, field.value, false)

  return finalvalue;
}

function applyAlphaNumericFormat(ActualFormat, fieldValue, isApplyOnlyUptoFieldContent) {
  var j=0;
  var NumberChars = digits;
  var AlphaChars = lowercaseLetters + uppercaseLetters;
  var finalvalue='';
  var tempvalue = fieldValue;
  for(var i=0; i<=ActualFormat.length-1; i++) {
     //alert(ActualFormat.substr(i,1) + " / " + tempvalue.substr(j,1))
    if(j<=tempvalue.length-1) {
       if(ActualFormat.substr(i,1)=='X' || ActualFormat.substr(i,1)=='A' ||
          ActualFormat.substr(i,1)=='9' || ActualFormat.substr(i,1)=='0') {

          if (ActualFormat.substr(i,1)=='9' || ActualFormat.substr(i,1)=='0') {
              if (ActualFormat.substr(i,1)=='0') {
                  //If non numeric character found and the format character is "0",
                  // append "0" and continue (only if the format is applied fully; otherwise, ignore this format location
                  // and continue)
                  if (!isApplyOnlyUptoFieldContent) {
                      finalvalue = finalvalue + "0";
                  }
                  continue;
              } else {
                  if(NumberChars.indexOf(tempvalue.substr(j,1)) == -1) {
                    // Non Numeric character found - error in data entry
                    finalvalue="";
                    break;
                  }
              }
          } else {
              if( (ActualFormat.substr(i,1)=='A' || NumberChars.indexOf(tempvalue.substr(j,1)) == -1) &&
                  AlphaChars.indexOf(tempvalue.substr(j,1)) == -1) {
                // Non AlphaNumeric/Alphabetic character found - error in data entry
                finalvalue="";
                break;
              }
          }
          finalvalue = finalvalue + tempvalue.substr(j,1);
          j=j+1;
       } else {
           if(NumberChars.indexOf(tempvalue.substr(j,1)) == -1 &&
               AlphaChars.indexOf(tempvalue.substr(j,1)) == -1 &&
               tempvalue.substr(j,1) != ActualFormat.substr(i,1) ) {
              // Non AlphaNumeric, Non mask format character found - error in data entry
              finalvalue="";
              break;
           } else {
              //accept the format mask
              finalvalue = finalvalue + ActualFormat.substr(i,1);
           }
       }
    } else {
        if (isApplyOnlyUptoFieldContent) {
            break;
        } else {
            if (ActualFormat.substr(i,1)=='0') {
                finalvalue = finalvalue + ActualFormat.substr(i,1);
            } else if (i<ActualFormat.length-1) {
              if (ActualFormat.substr(i+1,1)=='0') {
                  finalvalue = finalvalue + ActualFormat.substr(i,1);
              }
            }
        }
    }
    //alert(ActualFormat.substr(i,1));
    //alert(finalvalue);
  }
  return finalvalue;
}

function formatMaskForNumberField (field, format) {
  var NoOfDigitsToExtract=0;
  var decindex=0;
  var decpart=''
  var intpart=''
  var tempvalue = '';
  var finalvalue = '' ;
  var zeros=''
  var ActualFormat = format;
  var defaultCommaGrouping = -1;

  var tempvalue = field.value;
  for(var i=0;i<=tempvalue.length-1;i++) {
     if(isNaN(tempvalue.substr(i,1)) && tempvalue.substr(i,1)!='.') {
        //Non-Numeric entry found - Data Entry Issue!
        return "";
     }
  }
  tempvalue = '';

  //format will contain only #,0,. - other masks are ignored as they are irrelavent to number field
  format = '';
  for(var i=0;i<=ActualFormat.length-1;i++) {
     if(ActualFormat.substr(i,1)=='0' || ActualFormat.substr(i,1)=='#' || ActualFormat.substr(i,1)=='.') {
        if (ActualFormat.substr(i,1)=='0') {
            format = format + "#";
        } else {
            format = format + ActualFormat.substr(i,1);
        }
     }
  }

  if (format.indexOf(",")>0) {
      defaultCommaGrouping = (format.indexOf(".") > 0 ? format.lastIndexOf(".") : format.length) - format.lastIndexOf(",") ;
  }

  var j=0;
  decindex=format.indexOf(".");
  j=(format.length-1)-decindex;   //j will hold the number of decimal places allowed from the mask
  //alert('pt loc : ' + decindex + ' pt. cnt : ' + j)

  if(field.value.indexOf('.')>=0) { //if the provided field value is a decimal number

    if(field.value.indexOf('.')>decindex) {
        return ""; //Invalid data entry - user entered decimal location is invalid
    }
    else
    {
      NoOfDigitsToExtract = field.value.indexOf('.')
      intpart = field.value.substr(0,NoOfDigitsToExtract)
      decpart = field.value.substr(NoOfDigitsToExtract+1,j+1)  //always take one extra digit to round off
      if(decpart.length > j) { // decimal places entered is more than allowed decimal places, so round off the decimal part
         //Form the decimal part from the decimal part
         //eg. 23.3464 for mask ##.##, decpart will contain 346 and roundOffDecPart will contain 34.6
         var roundOffDecPart = decpart.substr(0, j) + "." + decpart.substr(j+1);
         //following statement will produce 35 for decpart variable
         decpart = Math.round(roundOffDecPart);
      }
    }
  } else {
     //If the format and user entered value doesnt contain decimal part, then take the user entered value as is.
     decindex=format.indexOf(".")
     if(decindex==-1) {
        finalvalue=field.value
     } else {
       // User entered value doesnt have decimal number, but format mask has it.
       // Form the decimal value automatically based on mask.
       if(j==0 && decindex==0)	{ //format mask is .
          finalvalue = '0.0'
       } else {
          NoOfDigitsToExtract = (format.length-j) - 1 //substract 1 for decimal pt.
          if(field.value.length > j)
          {
            intpart = field.value.substr(0,NoOfDigitsToExtract)
            decpart = field.value.substr(NoOfDigitsToExtract,j+1)  //always take one extra digit to round off
            if(decpart.length > j) { // decimal places entered is more than allowed decimal places, so round off the decimal part
               //Form the decimal part from the decimal part
               //eg. 23.3464 for mask ##.##, decpart will contain 346 and roundOffDecPart will contain 34.6
               var roundOffDecPart = decpart.substr(0, j) + "." + decpart.substr(j+1);
               //following statement will produce 35 for decpart variable
               decpart = Math.round(roundOffDecPart);
            }
            //alert(decpart)
          } else {  // User entered value covers only whole number part of the mask, so no decpart to worry about.
              intpart = field.value
              decpart = ''
              /*intpart='0'
             //only decimal part
             //find no. of zeros to pad
             for(i=0;i<j-id.value.length;i++)
                 decpart = decpart + '0';
             decpart = decpart + id.value */
        }
      }
    }
  }
  if(intpart.length==0) {
     intpart='0'
  }
  //Pad the decimal part, if empty
  if(decpart.length<j) {
     for(var i=0;i<(j-decpart.length);i++) {
         zeros = zeros + '0'
     }
     decpart = decpart + zeros ;
  } else {
     //rounding 99.99 will give 100, that means add 1 to intpart
     intpart = parseInt(intpart) + 1;
     decpart = decpart.substr(1);
  }
  //alert(decpart)
  finalvalue = intpart + '.' + decpart ;
  //alert(finalvalue);

  //By now, we should have the decimal location in-sync with the format decimal location and the value should be numeric
  if(finalvalue.indexOf('.')>decindex || isNaN(finalvalue)) {
     //Invalid data entry
     return "";
  } else {
    // Do the final formatting for any zeros based on format mask
    tempvalue = finalvalue;
    j=tempvalue.length-1;
    finalvalue='';
    var lastCommaOccurrence = -1;
    for(var i=ActualFormat.length-1;i>=0;i--) {
       //alert(ActualFormat.substr(i,1) + " / " + tempvalue.substr(j,1))
       if(ActualFormat.substr(i,1)=='#' || ActualFormat.substr(i,1)=='.') {
          if(j>=0) {
            finalvalue = tempvalue.substr(j,1) + finalvalue;
            j=j-1;
          }
       } else {
          if(j>=0) {
             if (ActualFormat.substr(i,1)=='0') {
                 if (parseInt(tempvalue.substr(j,1)) > parseInt(ActualFormat.substr(i,1))) {
                     finalvalue = tempvalue.substr(j,1) + finalvalue;
                 } else {
                     finalvalue = "0" + finalvalue;
                 }
                  j=j-1;
             } else {
                 finalvalue = ActualFormat.substr(i,1) + finalvalue;
                  if(ActualFormat.substr(i,1)==',') {
                      lastCommaOccurrence = 0;
                  }
             }
          } else {
             if(ActualFormat.substr(i,1)!=',') {
                finalvalue = ActualFormat.substr(i,1) + finalvalue;
             } else {
                lastCommaOccurrence = 0;
             }
          }
       }
       if (lastCommaOccurrence!=-1) {
          lastCommaOccurrence = lastCommaOccurrence + 1;
       }
       //alert(ActualFormat.substr(i,1));
       //alert(finalvalue);
    }

    //append rest of the user entered data, if any.
    while (j>=0) {
        finalvalue = finalvalue + tempvalue.substr(j,1);
        j=j-1;
    }
  }
  return finalvalue;
}

/** compare an array against the secondArray, and return the index of the first different element from this array if any.
** return -1 if no differnce between 2 arrays or if 2 arrays are null/empty
** 
**  for example:
**   ['a','b'].compare(['a','b'])   == -1
**   ['a','b','c'].compare(['a','b'])   == 2
**   ['a','b'].compare(['a','b','c'])   == -1
**/
Array.prototype.indexOfDiff = function(secondArray) {
    var indexOfDifference = -1;
    if (secondArray && secondArray.length > 0 && this.length > 0) {
        for (var i = 0; i < this.length && i < secondArray.length; i++) {
            if (this[i] != secondArray[i]) {
                indexOfDifference = i;
                break;
            }
        }
        // if have not found the difference, check the length
        if (indexOfDifference === -1 && this.length > secondArray.length) {
            indexOfDifference = secondArray.length;
        }
    }
    return indexOfDifference;
}
