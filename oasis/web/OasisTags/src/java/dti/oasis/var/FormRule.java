package dti.oasis.var;

import dti.oasis.util.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p/> FormRule
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
public class FormRule {

    private String formID;
    private ArrayList rules;
    private String ruleType;
    private ArrayList fieldsRows;

    public ArrayList getFieldsRows() {
        return fieldsRows;
    }

    public void setFieldsRows(String xmlValues, String xmlOriginals) throws Exception {
        this.fieldsRows = VarUtil.rowDataXMLtoVARFields(xmlValues, xmlOriginals);
    }

    private ArrayList results = new ArrayList();


    public FormRule(String formID, String ruleType) {
        this.formID = formID;
        this.ruleType = ruleType;
    }

    public void applyRules() throws Exception {
        Logger log = LogUtils.enterLog(this.getClass(), "applyRules");

        int ruleCount = this.rules.size();
        for (int i = 0; i < ruleCount; i++) {
            Rule r = (Rule) this.rules.get(i);
            applyRuleForEachRow(r);
        }
        log.exiting(this.getClass().getName(), "applyRules");
    }

    private void applyRuleForEachRow(Rule r) throws Exception {
        Logger log = LogUtils.enterLog(this.getClass(), "applyRuleForEachRow", r.toString());
        int rowCount = this.fieldsRows.size();
        for (int i = 0; i < rowCount; i++) {
            Map fieldsMap = (Map) this.fieldsRows.get(i);
            try {
                boolean rc = r.checkRule(fieldsMap);
                if (rc) {
                    this.results.add(r.getResult());
                }

            } catch (Exception e) {
                log.throwing(this.getClass().getName(), "applyRuleForEachRow", e);
                throw e;
            }
        }
        log.exiting(this.getClass().getName(), "applyRuleForEachRow");
    }

    public void loadRules(IRuleDAO dao) throws Exception {
        Logger log = LogUtils.enterLog(this.getClass(), "loadRules", new Object[]{dao});
        this.rules = new ArrayList(dao.retrieveRules(this.formID, this.ruleType));
        log.exiting(this.getClass().getName(), "loadRules", "rules.size" + this.rules.size());
    }

    static public RuleInfo findFormRuleInfo(IRuleDAO dao, String formID) throws Exception {
        Logger log = LogUtils.enterLog(FormRule.class, "method", new Object[]{dao, formID});
        RuleInfo ri = dao.retrieveFormRuleInfo(formID);
        log.exiting(FormRule.class.getName(), "findFormRuleInfo", new Object[]{ri});
        return ri;
    }

    static public boolean isRuleAvailable (IRuleDAO dao, String formID  )throws Exception {
        RuleInfo formRuleInfo=findFormRuleInfo(dao, formID);
        return (formRuleInfo!=null && formRuleInfo.getRuleCount()>0 );

    }

    public String resultsToXml() {
        Logger log = LogUtils.enterLog(this.getClass(), "resultsToXml");
        StringBuffer xmlSB = new StringBuffer("<Restult>");
        int resultCount = this.results.size();
        for (int i = 0; i < resultCount; i++) {
            Result r = (Result) this.results.get(i);
            xmlSB.append("<");
            xmlSB.append(r.getRuleType());
            xmlSB.append(">");
            xmlSB.append("<msg>");
            xmlSB.append("<![CDATA[");
            xmlSB.append(r.getMessage());
            xmlSB.append("]]>");
            xmlSB.append("</msg>");
            xmlSB.append("</");
            xmlSB.append(r.getRuleType());
            xmlSB.append(">");
        }
        xmlSB.append("</Restult>");
        log.exiting(this.getClass().getName(), "resultsToXml", new Object[]{xmlSB});
        return xmlSB.toString();
    }

    /**
     * @param formID
     * @param original
     * @param urlVAR
     * @param isActive
     * @param isGrid
     * @param gridXmlID
     * @param processFieldName
     * @param processFieldValue
     * @param needOriginal
     * @return String
     * @throws Exception
     */
    static public String formatVARuleJS(String formID,
                                        ArrayList original,
                                        String urlVAR,
                                        boolean isActive,
                                        boolean isGrid,
                                        String gridXmlID,
                                        String processFieldName,
                                        String processFieldValue,
                                        boolean needOriginal) throws Exception {
        Logger log = LogUtils.enterLog(FormRule.class, "formatVARuleJS",
                new Object[]{formID,
                        original,
                        urlVAR,
                        (isActive) ? Boolean.TRUE : Boolean.FALSE,
                        (isGrid) ? Boolean.TRUE : Boolean.FALSE,
                        gridXmlID,
                        processFieldName,
                        processFieldValue,
                        (needOriginal) ? Boolean.TRUE : Boolean.FALSE});
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream stringStream = new PrintStream(out);
        stringStream.println("<script type=\"text/javascript\">;");

        stringStream.println("var varRule = new VARuleObj();");
        stringStream.println(" varRule.formID='" + formID + "';");
        stringStream.println(" varRule.isActive=" + isActive + ";");
        stringStream.println(" varRule.urlVAR='" + urlVAR + "';");
        stringStream.println(" varRule.isGrid=" + isGrid + ";");
        stringStream.println(" varRule.gridXmlID='" + gridXmlID + "';");
        stringStream.println(" varRule.needOriginal=" + needOriginal + ";");
        stringStream.println(" varRule.processFieldName='" + processFieldName + "';");
        stringStream.println(" varRule.processFieldValue='" + processFieldValue + "';");
        stringStream.println(" varRule.returnValue=false;");

        stringStream.println("varRule.values={\"data\":[{}]};");

        String jsonOriginal = VarUtil.formatMapsToJSON(original);

        stringStream.println("varRule.original=" + jsonOriginal + ";");

/*
        stringStream.println("if(varRule.isActive){");
        stringStream.println("   document.body.attachEvent(\"onload\", attachVARToForm);");
        stringStream.println("}");
*/
        stringStream.println("</script>");

        stringStream.flush();
        log.exiting("FormRule", "formatVARuleJS", new Object[]{out.toString()});

        return out.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString());
        return sb.toString();
    }
}

