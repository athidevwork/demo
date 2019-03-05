package dti.oasis.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
/**
 * This is a utility class to determine Browser Version
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Oct. 7, 2009
 *
 * @author mgitelman
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/12/2016       Parker Xu   Issue#176822 Fix the version problem for IE11.
 *
 * ---------------------------------------------------
 */
public class BrowserUtils {

    public static boolean isIE(String userAgent) {
        return test(userAgent, Pattern_IE);
    }

    public static boolean isIE8AndAbove(String userAgent) {
        return test(userAgent, Pattern_IE8_AndAbove);
    }

    public static boolean isIE7(String userAgent) {
        return test(userAgent, Version_7);
    }

    public static boolean isIE8(String userAgent) {
        return test(userAgent, Version_8);
    }

    public static boolean isIE9(String userAgent) {
        return test(userAgent, Version_9);
    }

    public static boolean isIE10(String userAgent) {
        return test(userAgent, Version_10);
    }

    public static boolean isIE11(String userAgent) {
        return test(userAgent, Version_11);
    }

    public static String getIEVersion(String userAgent){
        String result = "LESS THAN 8 OR MORE THAN 11";

        if(BrowserUtils.isIE8(userAgent)) {
            result = "IE 8";
            return result;
        }

        if(BrowserUtils.isIE9(userAgent)) {
            result = "IE 9";
            return result;
        }

        if(BrowserUtils.isIE10(userAgent)) {
            result = "IE 10";
            return result;
        }

        if(BrowserUtils.isIE11(userAgent)) {
            result = "IE 11";
            return result;
        }

        return result;
    }

    private static boolean test(String stringToSearch, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(stringToSearch);
        return m.find();
    }

    private static String extract(String stringToSearch, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(stringToSearch);
        String result = null;
        if(m.find())
            result = m.group();

        return result;
    }

    private static final String Pattern_IE = "MSIE ([0-9]{1,}[\\.0-9]{0,})";
    private static final String Pattern_IE8_AndAbove = "Trident\\/([0-9]{1,}[\\.0-9]{0,})";
    private static final String Version_7 = "MSIE 7.0";
    private static final String Version_8 = "MSIE 8.0";
    private static final String Version_9 = "MSIE 9.0";
    private static final String Version_10 = "MSIE 10.0";
    private static final String Version_11 = "Trident/7.0;.*rv:11.0";
}
