package dti.oasis.var;

import java.text.ParseException;

/** Field class for Validation/Action Rule Engine
 * Hold the name, current value and original value with some basic operations
 * <p/>
 * <p>(C) 2006 Delphi Technology, inc. (dti)</p>
 * <p/>
 * Date:   Oct 12, 2006
 *
 * @author sjzhu
 *
 * */

/* Revision Date    Revised By  Description
* ---------------------------------------------------
*
* ---------------------------------------------------
*/
public class Field {
    private String name;
    private String value;
    private String original;

    /**   Constructor with field name, current value and original value
     * @param name
     * @param currentValue
     * @param originalValue
     */
    public Field(String name, String currentValue, String originalValue) {
        this.name = name;
        this.value = currentValue;
        this.original = originalValue;
    }

    public String getName() {
        return name;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String currentValue) {
        this.value = currentValue;
    }

    public String getOriginalValue() {
        return original;
    }

    public void setOriginalValue(String originalValue) {
        this.original = originalValue;
    }

    public String toString() {
        return value;
    }


    /** check if the value changed
     *
     * @return   boolean
     */
    public boolean isChanged() {
        return !value.equals(original);
    }

    /**
     *   caculate number of days from given date to the date on the vaule
     * @param d   formated date string
     * @return
     * @throws ParseException
     */
    public int daysAfter(String d) throws ParseException {
        return dti.oasis.util.DateUtils.daysDiff(d, this.value);
    }


    /**
     * check if the value in the given list.
     * also support range of number
     * @param list
     * @return  boolean
     */
    public boolean isIn(String list) {
        boolean rt = false;
        String[] items = list.split(",");
        int itemCount = items.length;
        /* if is a range item*/
        for (int i = 0; i < itemCount; i++) {
            String[] ranges = items[i].split("-");
            if (ranges.length > 1) {
                String begin = ranges[0];
                String end = ranges[1];
                if (Double.parseDouble(this.value) >= Double.parseDouble(begin)
                        && Double.parseDouble(this.value) <= Double.parseDouble(end)) {
                    rt = true;
                    break;
                }
            } else {
                if (this.value.equals(items[i])) {
                    rt = true;
                    break;
                }
            }
        }
        return rt;
    }

    /**
     *  check if the value equals the given value.
     * @param val
     * @return  boolean
     */
    public boolean equals(String val) {
        return this.value.equals(val);
    }

    /**
     * check if the value is blank
     * @return  boolean
     */
    public boolean isNull() {
        return dti.oasis.util.StringUtils.isBlank(this.value);
    }

    /**
     * check if value is numeric
     * @return   boolean
     */
    public boolean isNumeric() {
        return dti.oasis.util.StringUtils.isNumeric(this.value);
    }

    /**
     * check if value is decimal
     * @return  boolean
     */
    public boolean isDecimal() {
        return dti.oasis.util.StringUtils.isDecimal(this.value);
    }

    /**
     * check if the value is in valid date format
     * @return  boolean
     */
    public boolean isDate() {
        return dti.oasis.util.FormatUtils.isDate(this.value);
    }
}

