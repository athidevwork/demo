package dti.oasis.guidedrulemgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.guidedrulemgr.GuidedRuleManager;
import dti.oasis.guidedrulemgr.dao.GuidedRuleDAO;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.obr.Context;
import dti.oasis.obr.KnowledgeBaseManager;
import dti.oasis.obr.RuleFields;
import dti.oasis.obr.RuleMappingFields;
import dti.oasis.obr.dao.PageRuleDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.drools.lang.dsl.DSLMappingEntry;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manager class implementation for guided rule
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2011
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class GuidedRuleManagerImpl implements GuidedRuleManager {

    public static final String RULE_XML = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r").
            append("<GuidedRule>\r").
            append("\t<RuleMapping>\r").
//            append("\t\t<MappingItem>").
//            append("\t\t\t<Scope/>").
//            append("\t\t\t<LanguageExpression/>").
//            append("\t\t\t<Mapping/>").
//            append("\t\t</MappingItem>").
            append("\t</RuleMapping>\r").
            append("\t<Rule>\r").
            append("\t\t<Option/>\r").
            append("\t\t<When/>\r").
            append("\t\t<Then/>\r").
            append("\t</Rule>\r").
            append("</GuidedRule>\r").
            toString();

    public static final String PAGE_FIELDS_XML = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r").
            append("<Page/>").toString();


    @Override
    public String generateRuleXML(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateRuleXML", new Object[]{inputRecord});
        }
        String xmlString = null;
        try {
            String ruleText = inputRecord.getStringValue("ruleText");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(RULE_XML)));

            Node optionNode = document.getElementsByTagName(TAG_NAME_OPTION).item(0);
            Node whenNode = document.getElementsByTagName(TAG_NAME_WHEN).item(0);
            Node thenNode = document.getElementsByTagName(TAG_NAME_THEN).item(0);

            RecordSet mappingRecordSet = getPageRuleDAO().loadAllRuleMapping();
            List<Record> whenList = new ArrayList<Record>();
            List<Record> thenList = new ArrayList<Record>();
            RecordSet validMappingRecordSet = new RecordSet();
            for (Record record : mappingRecordSet.getRecordList()) {
                String scope = RuleMappingFields.getMappingScope(record);
                String languageExpression = RuleMappingFields.getLanguageExpression(record);
                String ruleMapping = RuleMappingFields.getRuleMapping(record);
                String mappingLine = "["+scope + "]" +languageExpression+"="+ruleMapping;
                DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
                boolean success = file.parseAndLoad(new StringReader(mappingLine));
                if (!success) {
                    String warningMessage = new StringBuffer("There is error when creating rule expander. ").
                            append(mappingLine).append(". Error: ").append(file.getErrors()).toString();
                    l.logp(Level.WARNING, getClass().getName(), "generateXML", warningMessage);
                    MessageManager.getInstance().addErrorMessage("core.guidedrule.mapping.invalid", new String[]{mappingLine});
                } else {
                    RuleMappingFields.setDSLMappingEntry(record,  file.getMapping().getEntries().get(0));
                    validMappingRecordSet.addRecord(record);
                    if (RuleMappingFields.isMappingScopeForWhen(record)) {
                        whenList.add(record);
                    } else {
                        thenList.add(record);
                    }
                }
            }

            BufferedReader reader = null;
            String oneLine = null;
            boolean reachWhen = false;
            boolean reachThen = false;
            int sequence = 0;
            try {
                reader = new BufferedReader(new StringReader(ruleText));
                while ((oneLine = reader.readLine()) != null) {
                    if (!oneLine.matches("^\\s*$")) {
                        if (reachThen) {
                            appendLineItem(document, thenNode, oneLine, thenList, ++sequence);
                        } else if (reachWhen) {
                            if (oneLine.matches("^\\s*then\\s*$")) {
                                reachThen = true;
                            } else {
                                appendLineItem(document, whenNode, oneLine, whenList, ++sequence);
                            }
                        } else if (oneLine.matches("^\\s*when\\s*$")) {
                            reachWhen = true;
                        } else {
                            // add header
                            appendOptionItem(document, optionNode, oneLine, ++sequence);
                        }
                    }
                }
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        l.logp(Level.SEVERE, getClass().getName(), "generateRuleXML", "Failed to close the string reader", e);
                    }
                }
            }
            generateRuleMapping(document, mappingRecordSet);
            xmlString = documentToString(document);
        } catch (Exception e) {
            ConfigurationException e1 = new ConfigurationException("Failed to process rule text.", e);
            l.throwing(this.getClass().getName(), "generateRuleXML", e1);
            throw e1;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateRuleXML", xmlString);
        }
        return xmlString;
    }

    @Override
    public String getValidateMessage(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getValidateMessage", new Object[]{inputRecord});
        }
        Record ruleRecord = buildRuleRecord(inputRecord);
        String pageId = inputRecord.getStringValue("pageId");
        Record pageRecord = getGuidedRuleDAO().loadPage(pageId);
        String pageCode = pageRecord.getStringValue("code");
        String validationMessage = KnowledgeBaseManager.getInstance().validateRule(pageCode, ruleRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getValidateMessage", validationMessage);
        }
        return validationMessage;
    }

    @Override
    public String getRuleSourceCode(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRuleSourceCode", new Object[]{inputRecord});
        }
        Record ruleRecord = buildRuleRecord(inputRecord);
        String ruleSourceCode = KnowledgeBaseManager.getInstance().getRuleSourceCode(ruleRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRuleSourceCode", ruleSourceCode);
        }
        return ruleSourceCode;
    }

    @Override
    public String getPageFieldsXML(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPageFieldsXML", new Object[]{inputRecord});
        }
        String xmlString = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(PAGE_FIELDS_XML)));
            Element root = document.getDocumentElement();
            RecordSet recordSet = getGuidedRuleDAO().loadAllPageFields(inputRecord);
            for (Record record : recordSet.getRecordList()) {
                Element element = document.createElement(TAG_NAME_FIELD);
                element.setAttribute("layerId", record.getStringValue("layerId"));
                element.setAttribute("displayType", record.getStringValue("displayType"));
                element.setAttribute("dataType", record.getStringValue("datatype"));
                element.setAttribute("label", record.getStringValue("label"));
                element.setAttribute("visibleB", record.getStringValue("visibleB"));
                element.setTextContent(record.getStringValue("fieldId"));
                root.appendChild(element);
            }
            recordSet = getGuidedRuleDAO().loadAllPageNavigationItem(inputRecord);
            for (Record record : recordSet.getRecordList()) {
                Element element = document.createElement(TAG_NAME_NAVIGATION);
                element.setAttribute("type", record.getStringValue("type"));
                element.setAttribute("label", record.getStringValue("longDescription"));
                element.setTextContent(record.getStringValue("shortDescription"));
                root.appendChild(element);
            }
            xmlString = documentToString(document);
        } catch (Exception e) {
            ConfigurationException e1 = new ConfigurationException("Failed to get page field xml.", e);
            l.throwing(this.getClass().getName(), "getPageFieldsXML", e1);
            throw e1;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPageFieldsXML", xmlString);
        }
        return xmlString;
    }

    /**
     * build rule record
     * @param inputRecord
     * @return
     */
    protected Record buildRuleRecord(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "buildRuleRecord", new Object[]{inputRecord});
        }
        String ruleId = inputRecord.getStringValue("ruleId");
        String ruleText = inputRecord.getStringValue("ruleText");
        Record ruleRecord = new Record();
        RuleFields.setRuleId(ruleRecord, ruleId);
        RuleFields.setRuleDesc(ruleRecord, "");
        RuleFields.setRuleText(ruleRecord, ruleText);
        RuleFields.setRuleType(ruleRecord, RuleFields.RULE_TYPE_BUSINESS);
        RuleFields.setRuleFrom(ruleRecord, RuleFields.RULE_FROM_CUST);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "buildRuleRecord", ruleRecord);
        }
        return ruleRecord;
    }

    /**
     * append Line item
     * @param document
     * @param parentNode
     * @param oneLine
     * @param mappingList
     */
    protected void appendLineItem(Document document, Node parentNode, String oneLine, List<Record> mappingList, int sequence) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "appendLineItem", new Object[]{document, parentNode, oneLine, mappingList, sequence});
        }
        Element lineItem = document.createElement(TAG_NAME_LINE);
        String lineId = LINE_ID_PREFIX + sequence;
        lineItem.setAttribute("id", lineId);
        parentNode.appendChild(lineItem);

        boolean match = false;
        for (Record record : mappingList) {
            DSLMappingEntry entry = RuleMappingFields.getDSLMappingEntry(record);
            Matcher matcher = entry.getKeyPattern().matcher(oneLine.trim());
            if (matcher.matches()) {
                match = true;
                String languageExpression = RuleMappingFields.getLanguageExpression(record);
                int count = 0;
                int startPosition = languageExpression.indexOf("{");
                while (startPosition != -1) {
                    count ++ ;
                    if (startPosition > 0) {
                        String text = languageExpression.substring(0, startPosition);
                        Element element = document.createElement(TAG_NAME_TEXT);
                        element.setTextContent(text);
                        lineItem.appendChild(element);
                    }
                    int endPosition = languageExpression.indexOf("}");

                    String inputType = INPUT_TYPE_NORMAL;
                    String placeholderString = languageExpression.substring(startPosition + 1, endPosition);
                    int position = placeholderString.indexOf("_");
                    if (position >= 0) {
                        inputType = placeholderString.substring(position + 1);
                    }
                    Element element = document.createElement(TAG_NAME_INPUT);
                    element.setAttribute("id", lineId + INPUT_NAME_STRING + count);
                    element.setAttribute("type", inputType);
                    String inputValue = matcher.group(count);
                    if (PLACE_HOLDER_FOR_BLANK_VALUE.equals(inputValue)) {
                        inputValue = "";
                    }
                    element.setTextContent(inputValue);
                    lineItem.appendChild(element);

                    languageExpression = languageExpression.substring(endPosition + 1);
                    startPosition = languageExpression.indexOf("{");
                }
                if (!StringUtils.isBlank(languageExpression)) {
                    Element element = document.createElement(TAG_NAME_TEXT);
                    element.setTextContent(languageExpression);
                    lineItem.appendChild(element);
                }
                break;
            }
        }
        if (!match) {
            // no mapping
            Element element = document.createElement(TAG_NAME_TEXT);
            element.setTextContent(oneLine);
            lineItem.appendChild(element);
        }
        l.exiting(getClass().getName(), "appendLineItem");
    }

    /**
     * append option item
     * @param document
     * @param parentNode
     * @param oneLine
     * @param sequence
     */
    protected void appendOptionItem(Document document, Node parentNode, String oneLine, int sequence) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "appendOptionItem", new Object[]{document, parentNode, oneLine, sequence});
        }
        Element lineItem = document.createElement(TAG_NAME_LINE);
        String lineId = LINE_ID_PREFIX + sequence;
        lineItem.setAttribute("id", lineId);
        parentNode.appendChild(lineItem);

        Element element = document.createElement(TAG_NAME_INPUT);
        element.setAttribute("id", lineId + INPUT_NAME_STRING + "1");
        element.setAttribute("type", INPUT_TYPE_NORMAL);
        element.setTextContent(oneLine);
        lineItem.appendChild(element);
        l.exiting(getClass().getName(), "appendOptionItem");
    }


    /**
     * generate rule mapping in xml
     * @param document
     * @param recordSet
     */
    protected void generateRuleMapping(Document document, RecordSet recordSet) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateRuleMapping", new Object[]{document, recordSet});
        }

        Node ruleMappingNode = document.getElementsByTagName(TAG_NAME_RULE_MAPPING).item(0);
        for (Record record : recordSet.getRecordList()) {
            String mappingScope = RuleMappingFields.getMappingScope(record);
            String languageExpression = RuleMappingFields.getLanguageExpression(record);
            String ruleMapping = RuleMappingFields.getRuleMapping(record);
            Element item = document.createElement(TAG_NAME_MAPPING_ITEM);
            Element scopeNode = document.createElement(TAG_NAME_SCOPE);
            Element languageExpressionNode = document.createElement(TAG_NAME_LANGUAGE_EXPRESSION);
            Element mappingNode = document.createElement(TAG_NAME_MAPPING);
            scopeNode.setTextContent(mappingScope);
            languageExpressionNode.setTextContent(languageExpression);
            mappingNode.setTextContent(ruleMapping);
            item.appendChild(scopeNode);
            item.appendChild(languageExpressionNode);
            item.appendChild(mappingNode);
            if (RuleMappingFields.isMappingScopeForThen(record)) {
                boolean isForSaveEvent = false;
                Matcher methodMatcher = c_methodPattern.matcher(ruleMapping);
                if (methodMatcher.matches()) {
                    String methodName = methodMatcher.group(1);
                    if (Context.METHODS_FOR_SAVE_EVENT.contains(methodName)) {
                        isForSaveEvent = true;
                    }
                }
                Element forSaveEvent = document.createElement(TAG_NAME_IS_FOR_SAVE_EVENT);
                forSaveEvent.setTextContent(Boolean.toString(isForSaveEvent));
                item.appendChild(forSaveEvent);
            }
            ruleMappingNode.appendChild(item);
        }
        l.exiting(getClass().getName(), "generateRuleMapping");
    }

    /**
     * convert xml document to string
     * @param document
     * @return
     * @throws TransformerException
     */
    protected String documentToString(Document document) throws TransformerException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "documentToString", new Object[]{document});
        }

        StringWriter stringOut = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(stringOut));
        String xml = stringOut.toString();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "documentToString", xml);
        }
        return xml;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getGuidedRuleDAO() == null)
            throw new ConfigurationException("The required property 'guidedRuleDAO' is missing.");
    }

    public GuidedRuleManagerImpl() {

    }

    public GuidedRuleDAO getGuidedRuleDAO() {
        return m_guidedRuleDAO;
    }

    public void setGuidedRuleDAO(GuidedRuleDAO guidedRuleDAO) {
        this.m_guidedRuleDAO = guidedRuleDAO;
    }

    public PageRuleDAO getPageRuleDAO() {
        return m_pageRuleDAO;
    }

    public void setPageRuleDAO(PageRuleDAO pageRuleDAO) {
        this.m_pageRuleDAO = pageRuleDAO;
    }

    private GuidedRuleDAO m_guidedRuleDAO;

    private PageRuleDAO m_pageRuleDAO;
    private final Logger l = LogUtils.getLogger(getClass());

    private static final String c_methodPatternStr = "^.*c\\.([-_a-zA-Z0-9]+) *\\(.*$";
    private static final Pattern c_methodPattern = Pattern.compile(c_methodPatternStr);

}