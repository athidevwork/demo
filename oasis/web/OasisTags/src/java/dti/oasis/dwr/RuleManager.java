package dti.oasis.dwr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Created by IntelliJ IDEA.
 * User: gjlong
 * Date: Apr 6, 2009
 * Time: 3:03:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RuleManager {
    public RecordSet loadAllArguments(Record inputRecord);

    public RecordSet getRules(Record inputRecord);

    public RecordSet getRuleConditions(Record inputRecord);

    public RecordSet getRuleCondFunctions(Record inputRecord);

    public RecordSet getFuncionArgs(Record inputRecord);

    public RecordSet getRuleActions(Record inputRecord);

    public RecordSet getActionArgs(Record inputRecord);

    public void insertDiaryItem(Record inputRecord);
    
    public static String BEAN_NAME ="RuleManager";
}
