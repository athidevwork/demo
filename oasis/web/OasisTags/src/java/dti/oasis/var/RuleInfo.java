package dti.oasis.var;
/** RuleInfo
 * Rule Summary Information of a form for Validation/Action Rule Engine
 * <p/>
 * <p>(C) 2006 Delphi Technology, inc. (dti)</p>
 * <p/>
 * Date:   Oct 12, 2006
 *
 * @author sjzhu
 */
public class RuleInfo {
    private String formID;
    private int ruleCount;
	private boolean isInvolvedOriginal;
    private String parms;
    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }

    public String getParms() {
        return parms;
    }

    public void setParms(String parms) {
        this.parms = parms;
    }

    public int getRuleCount() {
        return ruleCount;
    }

    public void setRuleCount(int ruleCount) {
        this.ruleCount = ruleCount;
    }

    public boolean isInvolvedOriginal() {
        return isInvolvedOriginal;
    }

    public void setInvolvedOriginal(boolean involvedOriginal) {
        isInvolvedOriginal = involvedOriginal;
    }


}
 
