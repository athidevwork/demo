//-----------------------------------------------------------------------------
// NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!!
//
// THIS FILE IS MAINTAINED IN THE OasisTags PROJECT!!!
// IT IS PROPAGATED FROM THE OasisTags PROJECT TO THE OTHER PROJECTS!!!
// IT SHOULD ONLY BE MODIFIED IN THE OasisTags PROJECT!!!
// IT SHOULD NEVER BE MODIFIED IN ANY PROJECT OTHER THAN
// OasisTags!!!
//
// NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!!
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// Functions to do number formatting from http://www.mredkj.com.
// Author: http://www.mredkj.com
// Date:   10/31/2006
// Modifications:
// GCC     10/31/2006     Added to OasisTags as our file NumberFormatUtil.js from
//                        NumberFormat153.js from http://www.mredkj.com.
// GCC     11/02/2006     Added new functions formatMoneyStrVal and
//                        unFormatMoneyStrVal and formatMoneyFldVal and
//                        unFormatMoneyFldVal.
// Fred    03/24/2008     Added method formatPctFldVal()/unformatPctFldVal()
//                        to support percentage field
// wfu     08/30/2010     Added logic to use oasis default currency symbol
// kshen   09/20/2013     Issue 148095
// kshen   03/27/2018     Issue 191765. Fix the error when parsing number value to the function formatPctStrVal.
// dpang   08/01/2018     Issue 194836. In unformatPctStrVal, remove comma in case value contains comma separator(e.g. 1,234%).
//-----------------------------------------------------------------------------

// VERSION INFORMATION FROM WWW.MREDKJ.COM:

/*
 * NumberFormat 1.5.3
 * v1.5.3 - 29-September-2004
 * v1.5.2 - 27-August-2004
 * v1.5.1 - 13-February-2004
 * v1.5.0 - 20-December-2002
 * v1.0.3 - 23-March-2002
 * v1.0.2 - 13-March-2002
 * v1.0.1 - 20-July-2001
 * v1.0.0 - 13-April-2000
 * http://www.mredkj.com
 */

/*
 * NumberFormat -The constructor
 * num - The number to be formatted.
 *  Also refer to setNumber
 * inputDecimal - (Optional) The decimal character for the input
 *  Also refer to setInputDecimal
 */
function NumberFormat(num, inputDecimal)
{
	// constants
	this.COMMA = ',';
	this.PERIOD = '.';
	this.DASH = '-'; // v1.5.0 - new - used internally
	this.LEFT_PAREN = '('; // v1.5.0 - new - used internally
	this.RIGHT_PAREN = ')'; // v1.5.0 - new - used internally
	this.LEFT_OUTSIDE = 0; // v1.5.0 - new - currency
	this.LEFT_INSIDE = 1;  // v1.5.0 - new - currency
	this.RIGHT_INSIDE = 2;  // v1.5.0 - new - currency
	this.RIGHT_OUTSIDE = 3;  // v1.5.0 - new - currency
	this.LEFT_DASH = 0; // v1.5.0 - new - negative
	this.RIGHT_DASH = 1; // v1.5.0 - new - negative
	this.PARENTHESIS = 2; // v1.5.0 - new - negative
	this.NO_ROUNDING = -1 // v1.5.1 - new

	// member variables
	this.num;
	this.numOriginal;
	this.hasSeparators = false;  // v1.5.0 - new
	this.separatorValue;  // v1.5.0 - new
	this.inputDecimalValue; // v1.5.0 - new
	this.decimalValue;  // v1.5.0 - new
	this.negativeFormat; // v1.5.0 - new
	this.negativeRed; // v1.5.0 - new
	this.hasCurrency;  // v1.5.0 - modified
	this.currencyPosition;  // v1.5.0 - new
	this.currencyValue;  // v1.5.0 - modified
	this.places;
	this.roundToPlaces; // v1.5.1 - new

	// external methods
	this.setNumber = setNumberNF;
	this.toUnformatted = toUnformattedNF;
	this.setInputDecimal = setInputDecimalNF; // v1.5.0 - new
	this.setSeparators = setSeparatorsNF; // v1.5.0 - new - for separators and decimals
	this.setCommas = setCommasNF;
	this.setNegativeFormat = setNegativeFormatNF; // v1.5.0 - new
	this.setNegativeRed = setNegativeRedNF; // v1.5.0 - new
	this.setCurrency = setCurrencyNF;
	this.setCurrencyPrefix = setCurrencyPrefixNF;
	this.setCurrencyValue = setCurrencyValueNF; // v1.5.0 - new - setCurrencyPrefix uses this
	this.setCurrencyPosition = setCurrencyPositionNF; // v1.5.0 - new - setCurrencyPrefix uses this
	this.setPlaces = setPlacesNF;
	this.toFormatted = toFormattedNF;
	this.toPercentage = toPercentageNF;
	this.getOriginal = getOriginalNF;
	this.moveDecimalRight = moveDecimalRightNF;
	this.moveDecimalLeft = moveDecimalLeftNF;

	// internal methods
	this.getRounded = getRoundedNF;
	this.preserveZeros = preserveZerosNF;
	this.justNumber = justNumberNF;
	this.expandExponential = expandExponentialNF;
	this.getZeros = getZerosNF;
	this.moveDecimalAsString = moveDecimalAsStringNF;
	this.moveDecimal = moveDecimalNF;
	this.addSeparators = addSeparatorsNF;

	// setup defaults
	if (inputDecimal == null) {
		this.setNumber(num, this.PERIOD);
	} else {
		this.setNumber(num, inputDecimal); // v.1.5.1 - new
	}
	this.setCommas(true);
	this.setNegativeFormat(this.LEFT_DASH); // v1.5.0 - new
	this.setNegativeRed(false); // v1.5.0 - new
	this.setCurrency(false); // v1.5.1 - false by default
	this.setCurrencyPrefix(currency_symbol);
	this.setPlaces(2);
}

/*
 * setInputDecimal
 * val - The decimal value for the input.
 *
 * v1.5.0 - new
 */
function setInputDecimalNF(val)
{
	this.inputDecimalValue = val;
}

/*
 * setNumber - Sets the number
 * num - The number to be formatted
 * inputDecimal - (Optional) The decimal character for the input
 *  Also refer to setInputDecimal
 *
 * If there is a non-period decimal format for the input,
 * setInputDecimal should be called before calling setNumber.
 *
 * v1.5.0 - modified
 */
function setNumberNF(num, inputDecimal)
{
	if (inputDecimal != null) {
		this.setInputDecimal(inputDecimal); // v.1.5.1 - new
	}

	this.numOriginal = num;
	this.num = this.justNumber(num);
}

/*
 * toUnformatted - Returns the number as just a number.
 * If the original value was '100,000', then this method will return the number 100000
 * v1.0.2 - Modified comments, because this method no longer returns the original value.
 */
function toUnformattedNF()
{
	return (this.num);
}

/*
 * getOriginal - Returns the number as it was passed in, which may include non-number characters.
 * This function is new in v1.0.2
 */
function getOriginalNF()
{
	return (this.numOriginal);
}

/*
 * setNegativeFormat - How to format a negative number.
 *
 * format - The format. Use one of the following constants.
 * LEFT_DASH   example: -1000
 * RIGHT_DASH  example: 1000-
 * PARENTHESIS example: (1000)
 *
 * v1.5.0 - new
 */
function setNegativeFormatNF(format)
{
	this.negativeFormat = format;
}

/*
 * setNegativeRed - Format the number red if it's negative.
 *
 * isRed - true, to format the number red if negative, black if positive;
 *  false, for it to always be black font.
 *
 * v1.5.0 - new
 */
function setNegativeRedNF(isRed)
{
	this.negativeRed = isRed;
}

/*
 * setSeparators - One purpose of this method is to set a
 *  switch that indicates if there should be separators between groups of numbers.
 *  Also, can use it to set the values for the separator and decimal.
 *  For example, in the value 1,000.00
 *   The comma (,) is the separator and the period (.) is the decimal.
 *
 * Both separator or decimal are not required.
 * The separator and decimal cannot be the same value. If they are, decimal with be changed.
 * Can use the following constants (via the instantiated object) for separator or decimal:
 *  COMMA
 *  PERIOD
 *
 * isC - true, if there should be separators; false, if there should be no separators
 * separator - the value of the separator.
 * decimal - the value of the decimal.
 *
 * v1.5.0 - new
 */
function setSeparatorsNF(isC, separator, decimal)
{
	this.hasSeparators = isC;

	// Make sure a separator was passed in
	if (separator == null) separator = this.COMMA;

	// Make sure a decimal was passed in
	if (decimal == null) decimal = this.PERIOD;

	// Additionally, make sure the values aren't the same.
	//  When the separator and decimal both are periods, make the decimal a comma.
	//  When the separator and decimal both are any other value, make the decimal a period.
	if (separator == decimal) {
		this.decimalValue = (decimal == this.PERIOD) ? this.COMMA : this.PERIOD;
	} else {
		this.decimalValue = decimal;
	}

	// Since the decimal value changes if decimal and separator are the same,
	// the separator value can keep its setting.
	this.separatorValue = separator;
}

/*
 * setCommas - Sets a switch that indicates if there should be commas.
 * The separator value is set to a comma and the decimal value is set to a period.
 * isC - true, if the number should be formatted with separators (commas); false, if no separators
 *
 * v1.5.0 - modified
 */
function setCommasNF(isC)
{
	this.setSeparators(isC, this.COMMA, this.PERIOD);
}

/*
 * setCurrency - Sets a switch that indicates if should be displayed as currency
 * isC - true, if should be currency; false, if not currency
 */
function setCurrencyNF(isC)
{
	this.hasCurrency = isC;
}

/*
 * setCurrencyPrefix - Sets the symbol for currency.
 * val - The symbol
 */
function setCurrencyValueNF(val)
{
	this.currencyValue = val;
}

/*
 * setCurrencyPrefix - Sets the symbol for currency.
 * The symbol will show up on the left of the numbers and outside a negative sign.
 * cp - The symbol
 *
 * v1.5.0 - modified - This now calls setCurrencyValue and setCurrencyPosition(this.LEFT_OUTSIDE)
 */
function setCurrencyPrefixNF(cp)
{
	this.setCurrencyValue(cp);
	this.setCurrencyPosition(this.LEFT_OUTSIDE);
}

/*
 * setCurrencyPosition - Sets the position for currency,
 *  which includes position relative to the numbers and negative sign.
 * cp - The position. Use one of the following constants.
 *  This method does not automatically put the negative sign at the left or right.
 *  They are left by default, and would need to be set right with setNegativeFormat.
 *	LEFT_OUTSIDE  example: $-1.00
 *	LEFT_INSIDE   example: -$1.00
 *	RIGHT_INSIDE  example: 1.00$-
 *	RIGHT_OUTSIDE example: 1.00-$
 *
 * v1.5.0 - new
 */
function setCurrencyPositionNF(cp)
{
	this.currencyPosition = cp
}

/*
 * setPlaces - Sets the precision of decimal places
 * p - The number of places.
 *  -1 or the constant NO_ROUNDING turns off rounding to a set number of places.
 *  Any other number of places less than or equal to zero is considered zero.
 *
 * v1.5.1 - modified
 */
function setPlacesNF(p)
{
	this.roundToPlaces = !(p == this.NO_ROUNDING); // v1.5.1
	this.places = (p < 0) ? 0 : p; // v1.5.1 - Don't leave negatives.
}

/*
 * v1.5.2 - new
 *
 * addSeparators
 * The value to be formatted shouldn't have any formatting already.
 *
 * nStr - A number or number as a string
 * inD - Input decimal (string value). Example: '.'
 * outD - Output decimal (string value). Example: '.'
 * sep - Output separator (string value). Example: ','
 */
function addSeparatorsNF(nStr, inD, outD, sep)
{
	nStr += '';
	var dpos = nStr.indexOf(inD);
	var nStrEnd = '';
	if (dpos != -1) {
		nStrEnd = outD + nStr.substring(dpos + 1, nStr.length);
		nStr = nStr.substring(0, dpos);
	}
	var rgx = /(\d+)(\d{3})/;
	while (rgx.test(nStr)) {
		nStr = nStr.replace(rgx, '$1' + sep + '$2');
	}
	return nStr + nStrEnd;
}

/*
 * toFormatted - Returns the number formatted according to the settings (a string)
 *
 * v1.5.0 - modified
 * v1.5.1 - modified
 */
function toFormattedNF()
{
	var pos;
	var nNum = this.num; // v1.0.1 - number as a number
	var nStr;            // v1.0.1 - number as a string
	var splitString = new Array(2);   // v1.5.0

	// round decimal places - modified v1.5.1
	// Note: Take away negative temporarily with Math.abs
	if (this.roundToPlaces) {
		nNum = this.getRounded(nNum);
		nStr = this.preserveZeros(Math.abs(nNum)); // this step makes nNum into a string. v1.0.1 Math.abs
	} else {
		nStr = this.expandExponential(Math.abs(nNum)); // expandExponential is called in preserveZeros, so call it here too
	}

	// v1.5.3 - lost the if in 1.5.2, so putting it back
	if (this.hasSeparators) {
		// v1.5.2
		// Note that the argument being passed in for inD is this.PERIOD
		//  That's because the toFormatted method is working with an unformatted number
		nStr = this.addSeparators(nStr, this.PERIOD, this.decimalValue, this.separatorValue);
	}

	// negative and currency
	// $[c0] -[n0] $[c1] -[n1] #.#[nStr] -[n2] $[c2] -[n3] $[c3]
	var c0 = '';
	var n0 = '';
	var c1 = '';
	var n1 = '';
	var n2 = '';
	var c2 = '';
	var n3 = '';
	var c3 = '';
	var negSignL = (this.negativeFormat == this.PARENTHESIS) ? this.LEFT_PAREN : this.DASH;
	var negSignR = (this.negativeFormat == this.PARENTHESIS) ? this.RIGHT_PAREN : this.DASH;

	if (this.currencyPosition == this.LEFT_OUTSIDE) {
		// add currency sign in front, outside of any negative. example: $-1.00
		if (nNum < 0) {
			if (this.negativeFormat == this.LEFT_DASH || this.negativeFormat == this.PARENTHESIS) n1 = negSignL;
			if (this.negativeFormat == this.RIGHT_DASH || this.negativeFormat == this.PARENTHESIS) n2 = negSignR;
		}
		if (this.hasCurrency) c0 = this.currencyValue;
	} else if (this.currencyPosition == this.LEFT_INSIDE) {
		// add currency sign in front, inside of any negative. example: -$1.00
		if (nNum < 0) {
			if (this.negativeFormat == this.LEFT_DASH || this.negativeFormat == this.PARENTHESIS) n0 = negSignL;
			if (this.negativeFormat == this.RIGHT_DASH || this.negativeFormat == this.PARENTHESIS) n3 = negSignR;
		}
		if (this.hasCurrency) c1 = this.currencyValue;
	}
	else if (this.currencyPosition == this.RIGHT_INSIDE) {
		// add currency sign at the end, inside of any negative. example: 1.00$-
		if (nNum < 0) {
			if (this.negativeFormat == this.LEFT_DASH || this.negativeFormat == this.PARENTHESIS) n0 = negSignL;
			if (this.negativeFormat == this.RIGHT_DASH || this.negativeFormat == this.PARENTHESIS) n3 = negSignR;
		}
		if (this.hasCurrency) c2 = this.currencyValue;
	}
	else if (this.currencyPosition == this.RIGHT_OUTSIDE) {
		// add currency sign at the end, outside of any negative. example: 1.00-$
		if (nNum < 0) {
			if (this.negativeFormat == this.LEFT_DASH || this.negativeFormat == this.PARENTHESIS) n1 = negSignL;
			if (this.negativeFormat == this.RIGHT_DASH || this.negativeFormat == this.PARENTHESIS) n2 = negSignR;
		}
		if (this.hasCurrency) c3 = this.currencyValue;
	}

	nStr = c0 + n0 + c1 + n1 + nStr + n2 + c2 + n3 + c3;

	// negative red
	if (this.negativeRed && nNum < 0) {
		nStr = '<font color="red">' + nStr + '</font>';
	}

	return (nStr);
}

/*
 * toPercentage - Format the current number as a percentage.
 * This is separate from most of the regular formatting settings.
 * The exception is the number of decimal places.
 * If a number is 0.123 it will be formatted as 12.3%
 *
 * !! This is an initial version, so it doesn't use many settings.
 * !! should use some of the formatting settings that toFormatted uses.
 * !! probably won't want to use settings like currency.
 *
 * v1.5.0 - new
 */
function toPercentageNF()
{
	nNum = this.num * 100;

	// round decimal places
	nNum = this.getRounded(nNum);

	return nNum + '%';
}

/*
 * Return concatenated zeros as a string. Used to pad a number.
 * It might be extra if already have many decimal places
 * but is needed if the number doesn't have enough decimals.
 */
function getZerosNF(places)
{
		var extraZ = '';
		var i;
		for (i=0; i<places; i++) {
			extraZ += '0';
		}
		return extraZ;
}

/*
 * Takes a number that JavaScript expresses in notational format
 * and makes it the full number (as a string).
 * e.g. Makes -1e-21 into -0.000000000000000000001
 *
 * If the value passed in is not a number (as determined by isNaN),
 * this function just returns the original value.
 *
 * Exponential number formats can include 1e21 1e+21 1e-21
 *  where 1e21 and 1e+21 are the same thing.
 *
 * If an exponential number is evaluated by JavaScript,
 * it will change 12.34e-9 to 1.234e-8,
 * which is a benefit to this method, because
 * it prevents extra zeros that occur for certain numbers
 * when using moveDecimalAsString
 *
 * Returns a string.
 *
 * v1.5.1 - new
 */
function expandExponentialNF(origVal)
{
	if (isNaN(origVal)) return origVal;

	var newVal = parseFloat(origVal) + ''; // parseFloat to let JavaScript evaluate number
	var eLoc = newVal.toLowerCase().indexOf('e');

	if (eLoc != -1) {
		var plusLoc = newVal.toLowerCase().indexOf('+');
		var negLoc = newVal.toLowerCase().indexOf('-', eLoc); // search for - after the e
		var justNumber = newVal.substring(0, eLoc);

		if (negLoc != -1) {
			// shift decimal to the left
			var places = newVal.substring(negLoc + 1, newVal.length);
			justNumber = this.moveDecimalAsString(justNumber, true, parseInt(places));
		} else {
			// shift decimal to the right
			// Check if there's a plus sign, and if not refer to where the e is.
			// This is to account for either formatting 1e21 or 1e+21
			if (plusLoc == -1) plusLoc = eLoc;
			var places = newVal.substring(plusLoc + 1, newVal.length);
			justNumber = this.moveDecimalAsString(justNumber, false, parseInt(places));
		}

		newVal = justNumber;
	}

	return newVal;
}

/*
 * Move decimal right.
 * Returns a number.
 *
 * v1.5.1 - new
 */
function moveDecimalRightNF(val, places)
{
	var newVal = '';

	if (places == null) {
		newVal = this.moveDecimal(val, false);
	} else {
		newVal = this.moveDecimal(val, false, places);
	}

	return newVal;
}

/*
 * Move decimal left.
 * Returns a number.
 *
 * v1.5.1 - new
 */
function moveDecimalLeftNF(val, places)
{
	var newVal = '';

	if (places == null) {
		newVal = this.moveDecimal(val, true);
	} else {
		newVal = this.moveDecimal(val, true, places);
	}

	return newVal;
}

/*
 * moveDecimalAsString
 * This is used by moveDecimal, and does not run parseFloat on the return value.
 *
 * Normally a decimal place is moved by multiplying by powers of 10
 * Multiplication and division in JavaScript can result in floating point limitations.
 * So use this method to move a decimal place left or right.
 *
 * Parameters:
 * val - The value to be shifted. Can be a number or a string,
 *  but don't include special characters. It should evaluate to a number.
 * left - If true, then move decimal left. If false, move right.
 * places - (optional) If not included, then use the objects this.places
 *  The purpose is so this method can be used independent of the state of the object.
 *
 * The regular expressions:
 * re1
 * Pad with zeros in case there aren't enough numbers to cover the spaces shift.
 * A left shift pads to the left, and a right shift pads to the right.
 * Can't just concatenate. There might be a negative sign or the value could be an exponential.
 *
 * re2
 * Switch the decimal.
 * Need the first [0-9]+ to force the search to start rightmost.
 * The \.? and [0-9]{} criteria are the pieces that will be switched
 *
 * Other notes:
 * This method works on exponential numbers, e.g. 1.7e-12
 * because the regular expressions only modify the number and decimal parts.
 *
 * Mozilla can't handle [0-9]{0} in the regular expression.
 *  Fix: Since nothing changes when the decimal is shifted zero places, return the original value.
 *
 * IE is incorrect if exponential ends in .
 *  e.g. -8500000000000000000000. should be -8.5e+21
 *  IE counts it as -8.5e+22
 *	Fix: Replace trailing period, if there is one, using replace(/\.$/, '').
 *
 * Netscape 4.74 cannot handle a leading - in the string being searched for the re2 expressions.
 *  e.g. /([0-9]*)(\.?)([0-9]{2})/ should match everything in -100.00 except the -
 *  but it matches nothing using Netscape 4.74.
 *  It might be a combination of the * ? special characters.
 *  Fix: (-?) was added to each of the re2 expressions to look for - one or zero times.
 *
 * Returns a string.
 *
 * v1.5.1 - new
 * v1.5.2 - modified
 */
function moveDecimalAsStringNF(val, left, places)
{
	var spaces = (arguments.length < 3) ? this.places : places;
	if (spaces <= 0) return val; // to avoid Mozilla limitation

	var newVal = val + '';
	var extraZ = this.getZeros(spaces);
	var re1 = new RegExp('([0-9.]+)');
	if (left) {
		newVal = newVal.replace(re1, extraZ + '$1');
		var re2 = new RegExp('(-?)([0-9]*)([0-9]{' + spaces + '})(\\.?)');
		newVal = newVal.replace(re2, '$1$2.$3');
	} else {
		var reArray = re1.exec(newVal); // v1.5.2
		if (reArray != null) {
			newVal = newVal.substring(0,reArray.index) + reArray[1] + extraZ + newVal.substring(reArray.index + reArray[0].length); // v1.5.2
		}
		var re2 = new RegExp('(-?)([0-9]*)(\\.?)([0-9]{' + spaces + '})');
		newVal = newVal.replace(re2, '$1$2$4.');
	}
	newVal = newVal.replace(/\.$/, ''); // to avoid IE flaw

	return newVal;
}

/*
 * moveDecimal
 * Refer to notes in moveDecimalAsString
 * parseFloat is called here to clear away the padded zeros.
 *
 * Returns a number.
 *
 * v1.5.1 - new
 */
function moveDecimalNF(val, left, places)
{
	var newVal = '';

	if (places == null) {
		newVal = this.moveDecimalAsString(val, left);
	} else {
		newVal = this.moveDecimalAsString(val, left, places);
	}

	return parseFloat(newVal);
}

/*
 * getRounded - Used internally to round a value
 * val - The number to be rounded
 *
 *  To round to a certain decimal precision,
 *  all that should need to be done is
 *  multiply by a power of 10, round, then divide by the same power of 10.
 *  However, occasional numbers don't get exact results in most browsers.
 *  e.g. 0.295 multiplied by 10 yields 2.9499999999999997 instead of 2.95
 *  Instead of adjusting the incorrect multiplication,
 *  this function uses string manipulation to shift the decimal.
 *
 * Returns a number.
 *
 * v1.5.1 - modified
 */
function getRoundedNF(val)
{
	val = this.moveDecimalRight(val);
	val = Math.round(val);
	val = this.moveDecimalLeft(val);

	return val;
}

/*
 * preserveZeros - Used internally to make the number a string
 * 	that preserves zeros at the end of the number
 * val - The number
 */
function preserveZerosNF(val)
{
	var i;

	// make a string - to preserve the zeros at the end
	val = this.expandExponential(val);

	if (this.places <= 0) return val; // leave now. no zeros are necessary - v1.0.1 less than or equal

	var decimalPos = val.indexOf('.');
	if (decimalPos == -1) {
		val += '.';
		for (i=0; i<this.places; i++) {
			val += '0';
		}
	} else {
		var actualDecimals = (val.length - 1) - decimalPos;
		var difference = this.places - actualDecimals;
		for (i=0; i<difference; i++) {
			val += '0';
		}
	}

	return val;
}

/*
 * justNumber - Used internally to parse the value into a floating point number.
 * Replace all characters that are not 0-9, a decimal point, or a negative sign.
 *
 *  A number can be entered using special notation.
 *  For example, the following is a valid number: 0.0314E+2
 *
 * v1.0.2 - new
 * v1.5.0 - modified
 * v1.5.1 - modified
 * v1.5.2 - modified
 */
function justNumberNF(val)
{
	newVal = val + '';

	var isPercentage = false;

	// check for percentage
	// v1.5.0
	if (newVal.indexOf('%') != -1) {
		newVal = newVal.replace(/\%/g, '');
		isPercentage = true; // mark a flag
	}

	// Replace everything but digits - + ( ) e E
	var re = new RegExp('[^\\' + this.inputDecimalValue + '\\d\\-\\+\\(\\)eE]', 'g');	// v1.5.2
	newVal = newVal.replace(re, '');
	// Replace the first decimal with a period and the rest with blank
	// The regular expression will only break if a special character
	//  is used as the inputDecimalValue
	//  e.g. \ but not .
	var tempRe = new RegExp('[' + this.inputDecimalValue + ']', 'g');
	var treArray = tempRe.exec(newVal); // v1.5.2
	if (treArray != null) {
	  var tempRight = newVal.substring(treArray.index + treArray[0].length); // v1.5.2
		newVal = newVal.substring(0,treArray.index) + this.PERIOD + tempRight.replace(tempRe, ''); // v1.5.2
	}

	// If negative, get it in -n format
	if (newVal.charAt(newVal.length - 1) == this.DASH ) {
		newVal = newVal.substring(0, newVal.length - 1);
		newVal = '-' + newVal;
	}
	else if (newVal.charAt(0) == this.LEFT_PAREN
	 && newVal.charAt(newVal.length - 1) == this.RIGHT_PAREN) {
		newVal = newVal.substring(1, newVal.length - 1);
		newVal = '-' + newVal;
	}

	newVal = parseFloat(newVal);

	if (!isFinite(newVal)) {
		newVal = 0;
  }

	// now that it's a number, adjust for percentage, if applicable.
  // example. if the number was formatted 24%, then move decimal left to get 0.24
  // v1.5.0 - updated v1.5.1
  if (isPercentage) {
  	newVal = this.moveDecimalLeft(newVal, 2);
  }

	return newVal;
}

//-----------------------------------------------------------------------------
// Formats the currency string value of a field by calling
// formatMoneyStrValAsStr.
//-----------------------------------------------------------------------------
function formatMoneyFldVal(field)
{
    field.value = formatMoneyStrValAsStr(field.value);
}

//-----------------------------------------------------------------------------
// Unformats a currency string value of a field by calling
// unformatMoneyStrValAsStr.
//-----------------------------------------------------------------------------
function unformatMoneyFldVal(field)
{
    field.value = unformatMoneyStrValAsStr(field.value);
}
function formatPctFldVal(field) {
    field.value = formatPctStrVal(field.value);
}

function formatPctStrVal (val) {
    if (isStringValue(val)) {
        if (typeof val === "string" && val.indexOf("%") == val.length - 1) {
            val = val.substr(0, val.length - 1);
        }
        if (!isFloat(val)) {
            val = 0;
        }
        return parseFloat(val).toFixed(2) + "%";
    } else {
        return val;
    }
}

function unformatPctFldVal(field) {
    field.value = unformatPctStrVal(field.value);
}

function unformatPctStrVal (val) {
    if (isStringValue(val)) {
        if (val.indexOf(",") > 0) {
            val = val.replace(/,/g, "");
        }

        if (val.indexOf("%") > 0) {
            return val.substring(0, val.indexOf("%"));
        } else {
            return val;
        }
    } else {
        return val;
    }
}

//-----------------------------------------------------------------------------
// Formats a currency string value by calling formatMoneyStrValAsObj and
// returning the value as a string.
//-----------------------------------------------------------------------------
function formatMoneyStrValAsStr(inputValue)
{
    var formattedObj = formatMoneyStrValAsObj(inputValue);
    return formattedObj.toString();
}

//-----------------------------------------------------------------------------
// Unformats a currency string value by calling unformatMoneyStrValAsObj and
// returning the value as a string.
//-----------------------------------------------------------------------------
function unformatMoneyStrValAsStr(inputValue)
{
    var unformattedObj = unformatMoneyStrValAsObj(inputValue);
    return unformattedObj.toString();
}

//-----------------------------------------------------------------------------
// Single entry point function for formatting a currency string.
// Returns NumberFormat object.
//-----------------------------------------------------------------------------
function formatMoneyStrValAsObj(inputValue) {
    // Call isStringValue in scriptlib.js to make sure we do not have a
    // null or empty string.
    if (isStringValue(inputValue)) {
        var num = new NumberFormat();
        num.setInputDecimal('.');
        num.setNumber(inputValue);
        num.setPlaces('2');
        num.setCurrencyValue(currency_symbol);
        num.setCurrency(true);
        num.setCurrencyPosition(num.LEFT_INSIDE);
        num.setNegativeFormat(num.PARENTHESIS);
        num.setNegativeRed(false);
        num.setSeparators(true, ',', ',');
        return num.toFormatted();
    }
    else {
        return inputValue;
    }
}

//-----------------------------------------------------------------------------
// Single entry point function for unformatting a currency string.
// Returns NumberFormat object.
//-----------------------------------------------------------------------------
function unformatMoneyStrValAsObj(inputValue) {
    // Call isStringValue in scriptlib.js to make sure we do not have a
    // null or empty string.
    if (isStringValue(inputValue)) {
        var num = new NumberFormat();
        num.setInputDecimal('.');
        num.setNumber(inputValue);
        num.setPlaces('2');
        num.setCurrencyValue(currency_symbol);
        num.setCurrency(true);
        num.setCurrencyPosition(num.LEFT_INSIDE);
        num.setNegativeFormat(num.PARENTHESIS);
        num.setNegativeRed(false);
        num.setSeparators(true, ',', ',');
        return num.toUnformatted();
    }
    else {
        return inputValue;
    }
}
