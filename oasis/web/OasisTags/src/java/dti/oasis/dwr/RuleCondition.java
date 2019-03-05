package dti.oasis.dwr;

import bsh.Interpreter;
import bsh.EvalError;

import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Logger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.NumberFormat;

import dti.oasis.error.ValidationException;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.security.Authenticator;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Mar 30, 2009
 * Time: 5:13:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class RuleCondition {
    private String conditionExp;
    private String expType;
    private RuleConditionArgs conditionArgs;


    protected void setExpType(String expType){
       this.expType = expType;
    }
    protected void setConditionExpression(String exp){
          this.conditionExp = exp;
    }
    protected String getConditionExpression(){
        return this.conditionExp;
    }
    protected void setConditionArgs(RuleConditionArgs args){
          this.conditionArgs = args;
    }
    protected RuleConditionArgs getConditionArgs(){
        return this.conditionArgs;
    }

    /**
     *
     * @return
     */
    protected boolean assertCondition() throws ValidationException{
        boolean result=false;
        if(this.expType.equalsIgnoreCase(DwrConstants.BEAN_SHELL_EXP_TYPE)){
            result = assertBShellCondition();
        }
        if(this.expType.equalsIgnoreCase(DwrConstants.JAVA_EXP_TYPE)){
            result = assertJavaCondition();
        }
        return  result;
    }

    private boolean assertBShellCondition() throws ValidationException{
        Logger l = LogUtils.enterLog(getClass(), "AssertBShellCondition");

        boolean result = false;
        Interpreter bsInterpreter = new Interpreter();
        String BShellParams = "";

        ArrayList args = conditionArgs.getRuleConditionArgs();
        Iterator lit = args.iterator();
        try {
            while (lit.hasNext()) {
                LinkedHashMap parms = (LinkedHashMap) lit.next();
                Iterator mit = parms.keySet().iterator();
                while (mit.hasNext()) {
                    String key = (String) mit.next();
                    bsInterpreter.set(key.toString(), ((String) parms.get(key)).trim());
                    BShellParams += key.toString() + ",";

                }
                BShellParams = BShellParams.substring(0, BShellParams.length() - 1);
                conditionExp = "dti.oasis.dwr.RuleCondition." + conditionExp + "(" + BShellParams + ")";
                Object r = bsInterpreter.eval(conditionExp);

                if (r instanceof Boolean) {
                    result = ((Boolean) r).booleanValue();
                }
            }
        } catch (EvalError er) {
            er.printStackTrace();
            throw new ValidationException(er.getMessage());
        }
        return result;
    }

    private boolean assertJavaCondition()  {
    {
        boolean result = false;
        //java expression should consist of fully qualified name of the class and method name
        // e.g dti.oasis.dwr.mycondition.mymethod
        // String classMethod[] = conditionExp.split(".");
        // String methodName=classMethod[classMethod.length-1];
        String methodName = this.conditionExp;
        String className = "dti.oasis.dwr.RuleCondition";
//        for(int i=0;i<classMethod.length-1;i++){
//           className += classMethod[i];
//        }
        try {
            Class exe = Class.forName(className);
            Iterator ait = conditionArgs.getRuleConditionArgs().iterator();
            while (ait.hasNext()) {
                LinkedHashMap parms = (LinkedHashMap) ait.next();
                Object arglist[] = new Object[parms.size()];
                Iterator mit = parms.keySet().iterator();
                int i = 0;
                while (mit.hasNext()) {
                    arglist[i] = mit.next();
                    i++;
                }
                Class[] params = new Class[i];
                //LIMITATION:
                //for now set all arguments to the String type.
                for (int k = 1; k < i; k++) {
                    params[k] = String.class;
                }
                Method meth = exe.getMethod(methodName, params);
                Object obj = exe.newInstance();
                meth.invoke(obj, arglist);
            }
        } catch (Exception e) {
            //TODO: throw new ValidationException
        }
        return result;
    }
  }

        public static void main(String[] args){
            RuleCondition rc = new RuleCondition();
            String ll="0";
            String hl ="99,999,999.99";
            

            rc.conditionExp = "isBetween";
            System.out.println("conditionExpression:"+rc.conditionExp);
            rc.expType=DwrConstants.BEAN_SHELL_EXP_TYPE;

            rc.conditionArgs= new RuleConditionArgs();
            LinkedHashMap templ = new LinkedHashMap();
            templ.put("f1",null);
            templ.put("ll",ll);
            templ.put("hl",hl);
           // templ.put("f2",null);
            String currentXml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<form>\n" +
                "<fields>\n" +
                "<field name=\"f1\" value=\"\"/>\n" +
                "<field name=\"f2\" value=\"n1\"/>\n" +
                "<field name=\"f3\" value=\"n3\"/>\n" +
                "</fields>\n" +
                "<ROWS>\n" +
                "<ROW>\n" +
                "<field name=\"rf1\" value=\"r1n1\"/>\n" +
                "<field name=\"rf2\" value=\"r1n2\"/>\n" +
                "<field name=\"rf3\" value=\"r1n3\"/>\n" +
                "</ROW>\n" +
                "<ROW>\n" +
                "<field name=\"rf1\" value=\"r2n1\"/>\n" +
                "<field name=\"rf2\" value=\"r2n2\"/>\n" +
                "<field name=\"rf3\" value=\"r2n3\"/>\n" +
                "</ROW>\n" +
                "</ROWS>\n" +
                "</form>";
            String origXml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<form>\n" +
                "<fields>\n" +
                "<field name=\"f1\" value=\"n1\"/>\n" +
                "<field name=\"f2\" value=\"n2\"/>\n" +
                "<field name=\"f3\" value=\"n3\"/>\n" +
                "</fields>\n" +
                "<ROWS>\n" +
                "<ROW>\n" +
                "<field name=\"rf1\" value=\"r1n1\"/>\n" +
                "<field name=\"rf2\" value=\"r1n2\"/>\n" +
                "<field name=\"rf3\" value=\"r1n3\"/>\n" +
                "</ROW>\n" +
                "<ROW>\n" +
                "<field name=\"rf1\" value=\"r2n1\"/>\n" +
                "<field name=\"rf2\" value=\"r2n2\"/>\n" +
                "<field name=\"rf3\" value=\"ORIGr2n3\"/>\n" +
                "</ROW>\n" +
                "</ROWS>\n" +
                "</form>";

            rc.conditionArgs.setArgTemplate(templ);
            rc.conditionArgs.setArgs(currentXml, "");
            boolean res = rc.assertCondition();
            System.out.println("result:"+res);
        }

/** check if the value changed
     *
     * @return   boolean
     */

    public static boolean isChanged(Object value, Object original) {
        return !value.equals(original);
    }

/**
     *   caculate number of days from given date to the date on the vaule
     * @param d   formated date string
     * @return
     * @throws java.text.ParseException
     */

    public static int daysAfter(String d, String value) throws ParseException {
        return dti.oasis.util.DateUtils.daysDiff(d, value);
    }

/**
     * check if the value in the given list.
     * also support range of number
     * @param list
     * @return  boolean
     */

    public static boolean isIn(String list, String value) {
        boolean rt = false;
        String[] items = list.split(",");
        int itemCount = items.length;
        /* if is a range item*/

        for (int i = 0; i < itemCount; i++) {
            String[] ranges = items[i].split("-");
            if (ranges.length > 1) {
                String begin = ranges[0];
                String end = ranges[1];
                if (Double.parseDouble(value) >= Double.parseDouble(begin)
                        && Double.parseDouble(value) <= Double.parseDouble(end)) {
                    rt = true;
                    break;
                }
            } else {
                if (value.equals(items[i])) {
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

    public static boolean equals(String value,String val) {
        return value.equals(val);
    }


    /**
         * check if the value is blank
         * @return  boolean
         */

        public static boolean isNull(String value) {
            return dti.oasis.util.StringUtils.isBlank(value);
        }
    /**
         * check if the value is not blank
         * @return  boolean
         */

        public static boolean isNotNull(String value) {
            return ! dti.oasis.util.StringUtils.isBlank(value);
        }

/**
     * check if value is numeric
     * @return   boolean
     */

    public static boolean isNumeric(String value) {
        return dti.oasis.util.StringUtils.isNumeric(value);
    }


/**
     * check if value is decimal
     * @return  boolean
     */

    public static boolean isDecimal(String value) {
        return dti.oasis.util.StringUtils.isDecimal(value);
    }


/**
     * check if the value is in valid date format
     * @return  boolean
     */

    public static boolean isDate(String value) {
        return dti.oasis.util.FormatUtils.isDate(value);
    }

    /**
     * Check if user has given security profile
     * @param value
     * @return
     */
    public static boolean isUserInProfile(String value){
        return dti.oasis.security.Authenticator.isUserInProfile(value);
    }

    /**
     * Similar to isNull but should be used for dropdowns
     * @param value
     * @return
     */
    public static boolean isNotSelected(String value){
         return dti.oasis.util.StringUtils.isBlank(value, true);
    }

    /**
     * Check if  lowLim>= val <= hiLim is true
     * Assumption: Both Hi and Low limit values should be parsable strings
     * having either Curency or Number format and are not null.
     * These values will validated on a front end as all the other entries for DWR setup.
     * @param val
     * @param lowLim
     * @param hiLim
     * @return
     */
    public static boolean  isBetween(String val,String lowLim, String hiLim){
        float valFloat = 0;
        float llFloat = 0;
        float hlFloat = 0;
        if(StringUtils.isBlank(val)){
            return true; //not a number, ignore the rule
        }
        try{
            valFloat = Float.parseFloat(FormatUtils.unformatCurrency(val));
        }catch (Exception ex){
            valFloat = Float.parseFloat(val);
        }
         try{
            llFloat = Float.parseFloat(FormatUtils.unformatCurrency(lowLim));
        }catch (Exception ex){
            valFloat = Float.parseFloat(lowLim);
        }
        try{
            hlFloat = Float.parseFloat(FormatUtils.unformatCurrency(hiLim));
        }catch (Exception ex){
            valFloat = Float.parseFloat(hiLim);
        }
         if(valFloat >= llFloat && valFloat <=hlFloat){
             return true;
         }else{
             return false;
         }
    }
}
