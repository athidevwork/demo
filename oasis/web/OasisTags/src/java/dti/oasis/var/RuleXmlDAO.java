package dti.oasis.var;

/** Implenmentaion of IRuleDAO with XML source for Validation/Action Rule Engine
 * Need construct the object with XML data source. It can be xml text string, xml document or xml InputStream
 * <p/>
 * <p>(C) 2006 Delphi Technology, inc. (dti)</p>
 * <p/>
 * Date:   Oct 12, 2006
 *
 * @author sjzhu
 */

import dti.oasis.util.LogUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

public class RuleXmlDAO implements IRuleDAO {
	Document doc = null;

	String xpathSelectValidationFormRules = "//FORM[FORM_ID='%0']/RULE[ACTION_TYPE='ERROR' or 'WARNING' or 'MESSAGE']";

	String xpathSelectActionFormRules = "//FORM[FORM_ID='%0']/RULE[ACTION_TYPE!='ERROR' and 'WARNING' and 'MESSAGE']";

	/*
	 * String sqlSelectIsOriginalInvolved = "select 'Y' from web_va_rule r,
	 * web_va_rule_cond c\n" + "where r.web_va_rule_pk = c.web_va_rule_fk\n" +
	 * "and c.condition_expr like '%.original%'";
	 */
	String xpathSelectRuleConditions = "//FORM/RULE[RULE_PK='%0']/CONDITION";

	String xpahSelectFormRuleInfo = "//FORM[FORM_ID='%0']/RULE";

	public RuleXmlDAO(Document doc) {
		this.doc = doc;
	}
	public RuleXmlDAO(String xmlText) throws ParserConfigurationException, SAXException, IOException {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    this.doc = builder.parse(new ByteArrayInputStream(xmlText.getBytes()));
	}
	public RuleXmlDAO(InputStream xmlStreamInput) throws Exception {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    this.doc = builder.parse(xmlStreamInput);
	}
	public RuleInfo retrieveFormRuleInfo(String webFormID) throws Exception {
		Logger log = LogUtils.enterLog(this.getClass(), "retrieveFormRuleInfo",
				new Object[] { webFormID });
		RuleInfo ri = new RuleInfo();

        log.exiting(this.getClass().getName(), "retrieveFormRuleInfo",
				new Object[] { ri });
		return ri;
	}

	public ArrayList retrieveRules(String webFormID, String ruleType)
			throws Exception {
		Logger log = LogUtils.enterLog(this.getClass(), "retrieveRules",
				new Object[] { webFormID, ruleType });
		String path ;
		if (ruleType.equalsIgnoreCase("ACTION")) {
			path = MessageFormat.format(this.xpathSelectActionFormRules,
					new Object[] {webFormID });
		} else {
			path = MessageFormat.format(this.xpathSelectValidationFormRules,
					new Object[] { webFormID });

		}
		NodeIterator nlRules = XPathAPI.selectNodeIterator(doc, path);
		ArrayList ruleList = new ArrayList();
		Node ruleNode;
		while ((ruleNode = nlRules.nextNode()) != null) {
			Node nPK = XPathAPI.selectSingleNode(ruleNode, "RULE_PK");
			Node nDESCR = XPathAPI.selectSingleNode(ruleNode, "DESCR");
			Node nACTION_TYPE = XPathAPI.selectSingleNode(ruleNode,
					"ACTION_TYPE");
			Node nACTION_MESSAGE = XPathAPI.selectSingleNode(ruleNode,
					"ACTION_MESSAGE");
            Node nPARMS = XPathAPI.selectSingleNode(ruleNode, "PARMS");
			String sRulePK = nPK.getNodeValue();
			long rulePK = Long.parseLong(sRulePK);
			String descr = nDESCR.getNodeValue();
			String action_type = nACTION_TYPE.getNodeValue();
			String action_message = nACTION_MESSAGE.getNodeValue();
			String ruleClassName = "dti.oasis.var.RuleApply" + action_type;
            String parms = nPARMS.getNodeValue();
			Class ruleClass = Class.forName(ruleClassName);
			Rule r = (Rule) ruleClass.newInstance();
			r.setFormID(webFormID);
			r.setRuleID(rulePK);
			r.setActionType(action_type);
			r.setMessage(action_message);
			r.setDescr(descr);
            r.setParms(parms);
            r.setConditions(this.retrieveRuleCondition(rulePK));
			ruleList.add(r);

		}

		log.exiting(this.getClass().getName(), "retrieveRules", new Object[] {
				"ruleList.size()=" + ruleList.size(), ruleList });

		return ruleList;
	}

	public ArrayList retrieveRuleCondition(long rulePK) throws Exception {
		Logger log = LogUtils.enterLog(this.getClass(),
				"retrieveRuleCondition", new Object[] { new Long(rulePK) });
		String xPath = MessageFormat.format(this.xpathSelectActionFormRules,
				new Object[] { new Long(rulePK) });
		NodeIterator nlConditions = XPathAPI.selectNodeIterator(doc, xPath);
		ArrayList conditions = new ArrayList();
		Node condNode;
		while ((condNode = nlConditions.nextNode()) != null) {
			Node nExpr = XPathAPI.selectSingleNode(condNode, "EXPR");
			Node nSyntaxCode = XPathAPI
					.selectSingleNode(condNode, "SyntaxCode");
			String expr = nExpr.getNodeValue();
			String syntaxCode = nSyntaxCode.getNodeValue();
			Condition c = new Condition(expr, syntaxCode);
			conditions.add(c);
		}

		log.exiting(this.getClass().getName(), "retrieveRuleCondition",
				new Object[] { conditions });
		return conditions;
	}

}

