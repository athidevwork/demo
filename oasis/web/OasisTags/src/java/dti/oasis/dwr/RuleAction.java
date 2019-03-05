package dti.oasis.dwr;

import dti.oasis.error.ValidationException;

import java.util.StringTokenizer;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;


/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Mar 31, 2009
 * Time: 4:15:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class RuleAction {
    private String actionExp;
    private String actionType;
    private LinkedHashMap args;
    private RuleActionArgs actionArgs;
    private Connection conn;

    private HashMap actionTypeEnum;

    protected RuleAction() {
        actionTypeEnum = new HashMap();{
        actionTypeEnum.put(DwrConstants.ACTION_ERROR, new Integer(1));
        actionTypeEnum.put(DwrConstants.ACTION_WARNING, new Integer(2));
        actionTypeEnum.put(DwrConstants.ACTION_DIARY, new Integer(3));
        actionTypeEnum.put(DwrConstants.ACTION_PROC_CALL, new Integer(4));
        actionTypeEnum.put(DwrConstants.ACTION_MESSAGE, new Integer(5));
        actionTypeEnum.put(DwrConstants.ACTION_FORM, new Integer(6));
    }
    }


    protected void setActionArgs(RuleActionArgs actArgs){
        actionArgs = actArgs;
        args = actionArgs.getActionArgs();

    }
    protected void setActionExp(String actionExp) {
        this.actionExp = actionExp;
    }
    protected void setActionType(String type){
        actionType = type;
    }


    protected String getActionExp() {
        return actionExp;
    }

    protected String getActionType() {
        return actionType;
    }

    protected LinkedHashMap getArgs() {
        return args;
    }

    protected RuleActionArgs getActionArgs() {
        return actionArgs;
    }

    protected boolean executeAction() throws ValidationException {
        IExecuteAction act = null;
        boolean result = false;//indicate if exception should be thrown later as a reult of errors
        int currActionType;

        if (actionTypeEnum.containsKey(actionType)) {
            currActionType = ((Integer) (actionTypeEnum.get(actionType))).intValue();
        } else {
            throw new ValidationException("RuleAction:Unknow Action Type");
        }
        try {
            switch (currActionType) {
                case 1:
                    act = new ActionExecuteError();
                    result = true;
                    break;
                case 2:
                    act = new ActionExecuteWarning();
                    break;
                case 3:
                    act = new ActionExecuteDiary();
                    break;
                case 4:
                    throw new ValidationException("Execute Pocedure Action is not implemented");
                case 5:
                    act = new ActionExecuteMessage();
                    break;
                case 6:
                    throw new ValidationException("Execute Form Action is not implemented");
                default:
                    executeUnknown(args); //try excute java class

            }
            act.exec(actionArgs, conn);

        } catch (Exception ce) {
            throw new ValidationException("Error executing rule action:" + this.actionExp);
        }

        return result;
    }
    /**
     * This method will be called if no match found between expression and
     * predefine names of actions (see DwrConstants)
     * Split expression into class and method. Using reflection execute method
     */
    protected void executeUnknown(LinkedHashMap args) throws ClassNotFoundException,
                                    NoSuchMethodException,
                                    InstantiationException,
                                    IllegalAccessException,
                                    InvocationTargetException {
        //derive class and method names from actionExp
        String[] buff = actionExp.split("[.]");
        //the last element is method, the rest is fully qualifed class name
        String methodName = buff[buff.length -1];
        String className = actionExp.substring(0,actionExp.indexOf(methodName)-1);
        Class exe = Class.forName(className);
  
        Object arglist[]= new Object[1];
        arglist[0] = args;
        Class[] parms = new Class[1];
        parms[0] = LinkedHashMap.class;
        Method meth = exe.getMethod(methodName,parms);
        Object obj = exe.newInstance();
        meth.invoke(obj,arglist);

    }
    protected void setConnection(Connection con){
         conn = con;
    }

    public static void main (String[] args){
        RuleAction ra = new RuleAction();
        ra.actionExp = "dti.oasis.dwr.ActionExecuteDiary.exec";
        ra.args = new LinkedHashMap();
        ra.args.put("1","Hi");

       try{
        ra.executeAction();
       }catch (Exception e){
           e.printStackTrace();
       }
    }
}
