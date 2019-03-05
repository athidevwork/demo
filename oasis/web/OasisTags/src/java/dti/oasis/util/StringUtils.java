package dti.oasis.util;

import dti.oasis.app.AppException;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String utility class
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date: Feb 20, 2004
 *
 * @author jbe
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 7/15/2004        jbe     Added isDecimal and isNumeric
 * 11/30/2004       jbe     Added fast replace method
 * 7/7/2005         jbe     Add delimiter methods
 * 01/23/2007       wer     Added methods for translate a database column name format into a java property name format
 * 09/12/2007       sxm     Added methods for checking if two values are the same
 * 03/07/2009       Fred    Added method trimTail()
 * 06/25/2009       James   Added method htmlToText
 * 12/11/2009       Fred    Added formatDBErrorForHtml
 * 03/02/2011      Michael  Issue 116714 change the display message format
 * 09/22/2011      ryzhao   Issue 124862 Added decimalPlaceCheck().
 * 10/12/2011       clm     Issue 121454 add getQuotationString method.
 * 11/21/2014       jxgu    Issue 121454 OBR: Return true for "null" string because of a bug
 *                          in MVEL
 * 09/14/2016       dzhang  Issue 179052: add contains method to check if value contains the specific sub string.
 * ---------------------------------------------------
 */

public class StringUtils {
    private static final Logger l = LogUtils.getLogger(StringUtils.class);

    /**
     * Returns true if value is fundamentally blank
     *
     * @param val value to check
     * @return true if blank
     * @see #isBlank
     */
    public static boolean isBlank(String val) {
        return isBlank(val, false);
    }

    /**
     * Returns true if value is fundamentally blank
     *
     * @param val      value to check
     * @param dropDown used by dropdown? if so, then "-1" is considered a blank as well
     * @return true if blank
     */
    public static boolean isBlank(String val, boolean dropDown) {
        return (val == null || val.trim().length() == 0 || "null".equals(val) || (dropDown && val.equals("-1")));
    }

    public static boolean isBlank(Object val, boolean dropDown) {
        boolean result = false;
        if(val == null){
          result = true;

        }else{
            if(val instanceof String){
                result = StringUtils.isBlank((String)val, true);
            }else{
                if (val instanceof String[]){
                    if(((String[])val).length == 0){
                       result = true;
                    }else{
                         if(((String[])val).length == 1){
                             result = StringUtils.isBlank(((String[])val)[0], true);
                         }
                    }
                }
            }
        }
        return  result;
    }

    /**
     * Determines if a value is numeric, returning true if value is blank or null
     *
     * @param val     value
     * @param blankOk if true, this method returns true if the value is blank or null
     */
    public static boolean isNumeric(String val, boolean blankOk) {
        if (StringUtils.isBlank(val))
            return blankOk;
        boolean rc = FormatUtils.isLong(val);
        return (rc) ? rc : FormatUtils.isDouble(val);

    }

    /**
     * Determines if a value is numeric
     *
     * @param val
     * @return true if numeric, false if not
     */
    public static boolean isNumeric(String val) {
        return isNumeric(val, false);
    }

    /**
     * Validates whether the input is a numeric value, and returns it. 
     *
     * @param val     value
     */
    public static String validateNumeric(String val) {
        if (!isNumeric(val, true)) {
            AppException ae = new AppException("core.invalid.input.number", "", new String[]{val});
            throw ae;
        }
        return val;
    }
    
    /**
     * Determines if a value is a decimal
     *
     * @param val
     * @return true if numeric, false if not
     */
    public static boolean isDecimal(String val) {
        return isDecimal(val, false);
    }

    /**
     * Determines if a value is decimal, returning true if value is blank or null
     *
     * @param val     value
     * @param blankOk if true, this method returns true if the value is blank or null
     */
    public static boolean isDecimal(String val, boolean blankOk) {
        if (StringUtils.isBlank(val))
            return blankOk;
        return FormatUtils.isDouble(val);
    }

    /**
     * Fast replace method
     *
     * @param s       String
     * @param oldText Old text to find and replace
     * @param newText New text to replace old text
     * @return String
     */
    public static String replace(String s, String oldText, String newText) {
        if (s == null) {
            return s;
        }
        else {
            try {
                StringBuffer stringBuffer = new StringBuffer(s);

                int index = s.length();
                int offset = oldText.length();

                while ((index = s.lastIndexOf(oldText, index - 1)) > -1) {
                    stringBuffer.replace(index, index + offset, newText);
                }

                return stringBuffer.toString();
            }
            catch (StringIndexOutOfBoundsException e) {
                return s;
            }
        }

    }

    /**
     * Converts a comma delimited String to an Array of Strings, each element
     * containing one of the delimited tokens.
     *
     * @param value Comma Delimited String.
     * @return String array.
     */
    public static String[] csvStringToArray(String value) {
        return delimitedToArray(value, ",");
    }

    /**
     * Converts a delimited String to an Array of Strings, each element
     * containing one of the delimited tokens.
     *
     * @param value     Delimited String.
     * @param delimiter The delimiter between which elements are found.  If null,
     *                  a comma will be used.
     * @return String array.  If the delimited String was null, this will return null.
     */
    public static String[] delimitedToArray(String value, String delimiter) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(StringUtils.class.getName(), "delimitedToArray", new Object[]{value, delimiter});
        }
        String[] list = null;
        if (value != null) {
            if (delimiter == null)
                delimiter = ",";
            StringTokenizer tok = new StringTokenizer(value.toString(), delimiter);
            list = new String[tok.countTokens()];
            int count = 0;
            while (tok.hasMoreTokens()) {
                String v = tok.nextToken();
                if (!StringUtils.isBlank(v)) {
                    list[count++] = v;
                }
            }
        }
        l.exiting(StringUtils.class.getName(), "delStringToArray", list);
        return list;

    }

    /**
     * Converts an String Array to a comma delimited String, each token between the
     * comma delimiter being an entry in the array.  By default, the commas will
     * appear at the beginning and end of the String. e.g. ,VAL,VAL1,VAL2,
     *
     * @param value String Array to be delimited
     * @return Comma Delimited String.  If the String array is null, this will return null.
     *         If an empty array, this will return an empty String.
     */

    public static String arrayToCSV(String[] value) {
        return arrayToDelimited(value, ",", true, true);
    }

    /**
     * Converts an String Array to a delimited String, each token between the
     * delimiter being an entry in the array.  By default, the delimiters will
     * appear at the beginning and end of the String. e.g. (given ","
     * for delimiter): ,VAL,VAL1,VAL2,
     *
     * @param value     String Array to be delimited
     * @param delimiter The delimiter to use in the returning String.  If null,
     *                  a comma will be used.
     * @return Delimited String.  If the String array is null, this will return null.
     *         If an empty array, this will return an empty String.
     */

    public static String arrayToDelimited(String[] value, String delimiter) {
        return arrayToDelimited(value, delimiter, true, true);
    }

    /**
     * Converts an String Array to a delimited String, each token between the
     * delimiter being an entry in the array.
     *
     * @param value     String Array to be delimited
     * @param delimiter The delimiter to use in the returning String.  If null,
     *                  a comma will be used.
     * @param prepend   TRUE to prepend the String with the delimiter.
     * @param append    TRUE to append the String with the delimiter.
     * @return Delimited String.  If the String array is null, this will return null.
     *         If an empty array, this will return an empty String.
     */
    public static String arrayToDelimited(String[] value, String delimiter,
                                          boolean prepend, boolean append) {
        return arrayToDelimited(value, delimiter, prepend, append, false);
    }

    /**
     * Converts an String Array to a delimited String, each token between the
     * delimiter being an entry in the array.
     *
     * @param value     String Array to be delimited
     * @param delimiter The delimiter to use in the returning String.  If null,
     *                  a comma will be used.
     * @param prepend   TRUE to prepend the String with the delimiter.
     * @param append    TRUE to append the String with the delimiter.
     * @param eliminateDuplicates TRUE will eliminate duplicate values.
     * @return Delimited String.  If the String array is null, this will return null.
     *         If an empty array, this will return an empty String.
     */
    public static String arrayToDelimited(String[] value, String delimiter,
                                          boolean prepend, boolean append, boolean eliminateDuplicates) {
        l.entering(StringUtils.class.getName(), "arrayToDelimited");
        if (delimiter == null)
            delimiter = ",";
        String retVal = null;
        if (value != null) {
            StringBuffer buff = new StringBuffer();
            int length = value.length;
            if (length > 0) {
                if (prepend)
                    buff.append(delimiter);
                boolean isDuplicateValue = false;
                buff.append(delimiter); //Always make sure the buff starts with a delimiter for duplicate checking
                for (int i = 0; i < length; i++) {
                    isDuplicateValue = (eliminateDuplicates ? (buff.indexOf(delimiter + value[i] + delimiter) != -1) : false) ;
                    if (!isDuplicateValue) {
                        buff.append(value[i]);
                        if (i < length-1)
                            buff.append(delimiter);
                    }
                }
                buff.deleteCharAt(0);   //remove the delimiter added for checking duplicates
                //If the last value is a duplicate value, remove the delimiter added to the end of the string
                if (isDuplicateValue) {
                    buff.deleteCharAt(buff.length()-1);
                }
                if (append)
                    buff.append(delimiter);
            }
            retVal = buff.toString();
        }

        l.exiting(StringUtils.class.getName(), "arrayToDelimited", retVal);
        return retVal;
    }

    /**
     * Capitalize the first letter if the propertyName.
     */
    public static String capitalizeFirstLetter(String propertyName) {
        if (propertyName != null) {
            propertyName = (propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length()));
        }
        return propertyName;
    }

    /**
     * Capitalizes all the delimiter separated words in a String, removing the delimiter character.
     * Uses '_' as the delimiter character.
     * NOTE: This method converts the input string to lower case before proceeding.
     *
     * @param input
     * @return the input string with words capitalized.
     */
    public static String capitalizeRemovingDelimiter(String input) {
      char[] delimiters = {'_'};
      return capitalizeRemovingDelimiter(input, delimiters);
    }

    /**
     * Capitalizes all the delimiter separated words in a String, removing the delimiter character.
     * Uses '_' as the delimiter character.
     * If capitalizeFirstCharacter is set to true, the first character is capitalized.
     * By default, the first character is set to lower case.
     * NOTE: This method converts the input string to lower case before proceeding.
     *
     * @param input
     * @param capitalizeFirstCharacter specify if the first character should be capitalized
     * @return the input string with words capitalized.
     */
    public static String capitalizeRemovingDelimiter(String input, boolean capitalizeFirstCharacter) {
      char[] delimiters = {'_'};
      return capitalizeRemovingDelimiter(input, delimiters, capitalizeFirstCharacter);
    }

    /**
     * Capitalizes all the delimiter separated words in a String, removing the delimiter character.
     * NOTE: This method converts the input string to lower case before proceeding.
     *
     * @param input
     * @param delimiters an array of character delimiters.
     * @return the input string with words capitalized.
     */
    public static String capitalizeRemovingDelimiter(String input, char[] delimiters) {
        return capitalizeRemovingDelimiter(input, delimiters, false);
    }

    /**
     * Capitalizes all the delimiter separated words in a String, removing the delimiter character.
     * If capitalizeFirstCharacter is set to true, the first character is capitalized.
     * By default, the first character is set to lower case.
     * NOTE: This method converts the input string to lower case before proceeding.
     *
     * @param input
     * @param delimiters an array of character delimiters.
     * @param capitalizeFirstCharacter specify if the first character should be capitalized
     * @return the input string with words capitalized.
     */
    public static String capitalizeRemovingDelimiter(String input, char[] delimiters, boolean capitalizeFirstCharacter) {

      if (input == null) {
        return input;
      }

      StringBuffer retStr = new StringBuffer();
      boolean skip = false;
      String str = input.toLowerCase();

      for (int i = 0; i < str.length(); i++) {

        if (skip) {
          skip = false;
          continue;
        }

        char c = str.charAt(i);

        if (capitalizeFirstCharacter && i == 0) {
            retStr.append(String.valueOf(str.charAt(i)).toUpperCase());
        }
        else if (ArrayUtils.contains(delimiters, c)) {
          if (str.length() > i + 1 && !ArrayUtils.contains(delimiters, str.charAt(i + 1))) {
            retStr.append(String.valueOf(str.charAt(i + 1)).toUpperCase());
            skip = true;
          }
        } else {
          retStr.append(c);
        }
      }

      return retStr.toString();
    }

    public static String chompIgnoreCase(String input, String suffix) {
      String retStr = input.toLowerCase();

      if (retStr.endsWith(suffix) && input.length() > suffix.length()) {
        return input.substring(0, input.length() - suffix.length());
      } else {
        return input;
      }
    }

    /**
     * Check if two values are the same
     *
     * @param val1      value to check
     * @param val2      value to check
     * @return true if both values are null or equal to each other
     */
    public static boolean isSame(String val1, String val2) {
        return isSame(val1, val2, false);
    }

    /**
     * Check if two values are the same
     *
     * @param val1       value to check
     * @param val2       value to check
     * @param ignoreCase true if ignore case
     * @return true if both values are null or equal to each other
     */
    public static boolean isSame(String val1, String val2, boolean ignoreCase) {
        return (val1 == null && val2 == null ||
                val1 != null && (ignoreCase && val1.equalsIgnoreCase(val2) || !ignoreCase && val1.equals(val2)) ||
                val2 != null && (ignoreCase && val2.equalsIgnoreCase(val1) || !ignoreCase && val2.equals(val1)));
    }

    /**
     * Remove the blank spaces at the end of the input string.
     * If the string is null keep it intact.
     *
     * @param input
     * @return
     */
    public static String trimTail(String input) {
        if (input == null) {
            return input;
        }
        char[] val = input.toCharArray();
        int len = val.length;
        while (len > 0 && val[len - 1] == ' ') {
            len--;
        }
        return input.substring(0, len);
    }

    /**
     * extract text from html string
     *
     * @param html
     * @return
     */
    public static String htmlToText(String html) {
        String text = html.replaceAll("\\<.*?>", "").replaceAll("&rsquo;","'").replaceAll("&lsquo;", "'").replaceAll("&ldquo;","\"").replaceAll("&rdquo;","\"");
        text = HtmlUtils.htmlUnescape(text);
        return text;
    }

    /**
     * Method to trim db error message
     *
     * @param input
     * @return
     */
    public static String formatDBErrorForHtml(String input) {
        if (StringUtils.isBlank(input)) {
            return "";
        }
        String msg = "";
        int pos = input.lastIndexOf("ORA-20");
        if (pos > -1) {
            int pos1 = input.indexOf("ORA-", pos + 1);
            msg = (pos1 < 0) ? input.substring(pos + 11) :
                    input.substring(pos + 11, pos1 - 1);
        } else {
            //Find the second ORA-//d{5}
            int index = input.indexOf("ORA-", 10);
            if (index > 0) {
               msg = input.substring(0, index);
            } else {
                msg = input;
            }
            msg = msg.replaceAll("ORA-\\d{5}", "");
        }
        int ind=msg.lastIndexOf(">");
        if(ind!=-1){
         msg = msg.substring(ind+1);
        }
        return htmlToText(msg);
    }

    /**
     * Determine whether the decimal place less than or equal the length.
     *
     * @param decimal
     * @param length
     * @return
     */
    public static boolean decimalPlaceCheck(String decimal, int length){
        boolean returnValue = true;
        if(isDecimal(decimal)){
           int pos = decimal.indexOf(".");
           if(pos > 0 && decimal.substring(pos+1).length() > length){
               returnValue = false;
           }
        }
        return returnValue;
    }

    /**
     * Add quotation for each word delimited by comma, For example: abc,def will become 'abc','def'
     * @param str
     * @return
     */
    public static String getQuotationString(String str) {
        String[] s = str.split(",");
        for (int i = 0; i != s.length; i++) {
            s[i] = "'" + s[i] + "'";
        }
        return arrayToDelimited(s, ",", false, false);
    }

    public static boolean isMatchPattern(String str, String pattern) {
        return str.matches(pattern);
    }


    /**
     * Is valid email address
     *
     * @param emailAddress
     * @return
     */
    public static boolean isValidEmailAddress(String emailAddress) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(StringUtils.class.getName(), "isValidEmailAddress", new Object[]{emailAddress});
        }

        String strRex = "([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*)@([a-zA-Z0-9]+(?:[.-][a-zA-Z0-9]+)*[.][a-zA-Z]{2,})";
        Pattern p = Pattern.compile(strRex);
        Matcher m = p.matcher(emailAddress);

        boolean isValid = m.matches();

        l.exiting(StringUtils.class.getName(), "isValidEmailAddress", isValid);
        return isValid;
    }

    /**
     * Check if a string is contained in another delimited string
     *
     * @param val           value to check
     * @param str           delimited string
     * @param delimiter     delimiter
     * @return true string exists in the delimited string
     */
    public static boolean isInDelimitedString(String val, String str, String delimiter){
        boolean isInString = false;
        List<String> list = new ArrayList<String>(Arrays.asList(str.split(delimiter)));
        if(list.contains(val))
            isInString = true;
        return isInString;
    }

    /**
     * check if a string contains the specific sub string.
     *
     * @param value
     * @param searchString
     * @return
     */
    public static boolean contains(String value, String searchString) {
        boolean isContains = false;
        if (l.isLoggable(Level.FINER)) {
            l.entering(StringUtils.class.getName(), "contains", new Object[]{value, searchString});
        }

        if (!isBlank(value) && !isBlank(searchString) && value.contains(searchString)) {
            isContains = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(StringUtils.class.getName(), "contains", isContains);
        }
        return isContains;
    }

    /**
     * Get the right part of a string.
     *
     * @param val
     * @param prefix
     * @return
     */
    public static String strRight(String val, String prefix) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(StringUtils.class.getName(), "strRight", new Object[]{val, prefix});
        }

        if (isBlank(val) || isBlank(prefix) || !val.startsWith(prefix)) {
            if (l.isLoggable(Level.FINER)) {
                l.exiting(StringUtils.class.getName(), "strRight", val);
            }
            return val;
        }

        String result = val.substring(prefix.length());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(StringUtils.class.getName(), "strRight", result);
        }
        return result;
    }

    /**
     *  Substr the string after the separater
     * @param str
     * @param separator
     * @return
     */
    public static String substringAfter(String str, String separator) {
        if (StringUtils.isBlank(str)) {
            return str;
        } else if (separator == null) {
            return "";
        } else {
            int pos = str.indexOf(separator);
            return pos == -1 ? "" : str.substring(pos + separator.length());
        }
    }
}
