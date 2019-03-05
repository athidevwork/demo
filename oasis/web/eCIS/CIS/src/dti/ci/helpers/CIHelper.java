package dti.ci.helpers;

import dti.oasis.util.FormatUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * Superclass for CI Helper classes.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * @author Gerald C. Carney
 * Date:   Jan 14, 2004
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 *
 * ---------------------------------------------------
 */

public abstract class CIHelper implements ICIConstants {

  /**
   * Determines if a string represents a date in mm/dd/yyy format.
   * @param input    Input string.
   * @return boolean
   */
  public static boolean isDate(String input) {
    return FormatUtils.isDate(input);
  }

  /**
   * Determines if a string can be converted into a long.
   * @param input    Input string.
   * @return boolean
   */
  public static boolean isLong(String input) {
    return FormatUtils.isLong(input);
  }

  /**
   * Determines if a string can be converted into a float.
   * @param input    Input string.
   * @return boolean
   */
  public static boolean isFloat(String input) {
    return FormatUtils.isFloat(input);
  }

  public String removePrefixFromFieldID (String fieldID, String prefix) {
    String methodName = "removePrefixFromFieldID";
//    String methodDesc = "Class " + this.getClass().getName() +
//      ", method " + methodName;
    Logger lggr = LogUtils.enterLog(this.getClass(),
      methodName, new Object[] { fieldID, prefix });
    try {
      if (StringUtils.isBlank(fieldID)) {
        return "";
      }
      else if (StringUtils.isBlank(prefix)) {
        return fieldID;
      }
      else {
//        lggr.fine(methodDesc + ":  field ID = " + fieldID +
//          ";  field ID length = " + fieldID.length() +
//          ":  prefix = " + prefix +
//          ";  prefix length = " + prefix.length());
        if (fieldID.length() > prefix.length() && fieldID.substring(0, prefix.length()).equals(prefix))  {
          return fieldID.substring(prefix.length());
        }
        else {
          return fieldID;
        }
      }
    }
    finally {
      lggr.exiting(this.getClass().getName(), methodName);
    }
  }
}