package dti.oasis.obr.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.obr.Context;
import dti.oasis.obr.KnowledgeBaseManager;
import dti.oasis.obr.PackageMetaData;
import dti.oasis.obr.ProcessingEvents;
import dti.oasis.obr.RuleFields;
import dti.oasis.obr.RuleMetaData;
import dti.oasis.obr.SysParm;
import dti.oasis.obr.TrackingAgendaEventListener;
import dti.oasis.obr.TriggeredField;
import dti.oasis.obr.UserProfile;
import dti.oasis.obr.dao.PageRuleDAO;
import dti.oasis.obr.event.RuleEvent;
import dti.oasis.obr.log.RuleLogFactory;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.OasisUser;
import dti.oasis.util.SysParmProvider;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.drools.lang.dsl.DefaultExpander;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.runtime.StatelessKnowledgeSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides the implementation details for KnowledgeBaseManager, which is used for rule engine.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 29, 2011
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/08/2011       jxgu        Issue#128980 Allow a rule to add "end" to the end of the Rule Text.
 * 05/17/2012       jxgu        Issue#133379 Label name containing "#" is not allowed in rule
 * 08/03/2012       jxgu        Issue#134837 Enhance OBR to expose the fieldId that triggered the change event.
 * 07/17/2013       jxgu        Issue#133379 Fix the bug: similar rules don't work in Drools
 * 01/07/2014       wer         Issue#149634 Fix creation of KnowledgeBuilder to work with Java 7 by setting the property "drools.dialect.java.compiler.lnglevel" to "1.6
 * 11/19/2014       jxgu        Issue#158594 OBR cache header fields if it is accessed
 * 10/13/2015       wdang       Issue#156364 Modify c_profilePatternStr to support UserProfile with whitespace.
 * ---------------------------------------------------
 */
public class KnowledgeBaseManagerImpl extends KnowledgeBaseManager {

    public KnowledgeBaseManagerImpl() {

    }

    /**
     * has knowledge or not
     * @param pageCode
     * @return
     */
    public boolean hasKnowledgeBase(String pageCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasKnowledgeBase", new Object[]{pageCode});
        }

        boolean hasKnowledgeBase = false;
        RequestStorageManager rsm = RequestStorageManager.getInstance();
        if(rsm.has(pageCode+"hasKnowledgeBase")) {
            hasKnowledgeBase = YesNoFlag.getInstance((Boolean)rsm.get(pageCode+"hasKnowledgeBase")).booleanValue();
        } else {
            if (getLastModifiedTime(pageCode) >= 0) {
                hasKnowledgeBase = true;
            }
        }

        rsm.set(pageCode+"hasKnowledgeBase", new Boolean(hasKnowledgeBase));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasKnowledgeBase", hasKnowledgeBase);
        }
        return hasKnowledgeBase;
    }

    /**
     * get package meta data
     * @param pageCode
     * @return
     */
    public PackageMetaData getPackageMetaData(String pageCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPackageMetaData", new Object[]{pageCode});
        }
        PackageMetaData packageMetaData = null;
        if (hasKnowledgeBase(pageCode)) {
            KnowledgeBaseInfo knowledgeBaseInfo = getKnowledgeBaseInfo(pageCode);
            packageMetaData = knowledgeBaseInfo.getPackageMetaData();
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPackageMetaData", packageMetaData);
        }
        return packageMetaData;
    }


    /**
     * Fire the rules in the knowledge base for the page.
     * @param pageCode
     * @param context
     * @param factData
     * @param ruleEvents
     * @param processingEvents
     */
    public void fireRules(String pageCode, Context context, Object[] factData, RuleEvent[] ruleEvents,
                          ProcessingEvents processingEvents) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "fireRules", new Object[]{pageCode, context, factData, ruleEvents, processingEvents});
        }

        if (hasKnowledgeBase(pageCode)) {
            KnowledgeBaseInfo knowledgeBaseInfo = getKnowledgeBaseInfo(pageCode);
            //execute rule
            List objectList = new ArrayList();
            for (Object fact : factData) {
                objectList.add(fact);
            }
            for (RuleEvent ruleEvent : ruleEvents) {
                objectList.add(ruleEvent);
            }
            //get system parameter
            PackageMetaData metaData =  knowledgeBaseInfo.getPackageMetaData();
            List<String> sysParmList = metaData.getRelatedSystemParamList();
            for (String sysParmCode: sysParmList) {
                String sysParamValue = SysParmProvider.getInstance().getSysParm(sysParmCode);
                SysParm sysParm = new SysParm(sysParmCode, sysParamValue);
                objectList.add(sysParm);
            }
            // get profiles
            if (metaData.getRelatedProfileList().size()>0) {
                OasisUser userBean = (OasisUser) context.getSession().getAttribute(IOasisAction.KEY_OASISUSER);
                for (String profile : metaData.getRelatedProfileList()) {
                    if (userBean.hasProfile(profile)) {
                        UserProfile userProfile = new UserProfile(profile);
                        objectList.add(userProfile);
                    }
                }
            }
            if (knowledgeBaseInfo.getRemoveRecordKnowledgeBase() != null && ProcessingEvents.OnLoad == processingEvents) {
                KnowledgeBase knowledgeBase = knowledgeBaseInfo.getRemoveRecordKnowledgeBase();
                executeRules(knowledgeBase, context, objectList);
                // removed objects
                objectList.removeAll(context.getRemovedRecordList());
                context.getRemovedRecordList().clear();
                // reset row number
                for (RecordSet recordSet : context.getRecordSetsEffectedByRemoveRecord()) {
                    recordSet.resetRecordNumber();
                }
                context.getRecordSetsEffectedByRemoveRecord().clear();
            }
            if (ProcessingEvents.OnChange == processingEvents) {
                // add triggered field into fact data
                String triggeredFieldId = context.getRequest().getParameter("_FieldIdTriggeredEvent");
                TriggeredField triggeredField = new TriggeredField(triggeredFieldId);
                objectList.add(triggeredField);
            }
            if (knowledgeBaseInfo.getKnowledgeBase() != null) {
                KnowledgeBase knowledgeBase = knowledgeBaseInfo.getKnowledgeBase();
                executeRules(knowledgeBase, context, objectList);
            }
        }
        l.exiting(getClass().getName(), "fireRules");
        return;
    }


    /**
     * validate rule
     *
     * @param pageCode
     * @param record
     * @return
     */
    public String validateRule(String pageCode, Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateRule", new Object[]{pageCode, record});
        }
        String ruleId = RuleFields.getRuleId(record);
        StringBuffer validationMessageBuffer = new StringBuffer();

        buildRuleCode(record);

        RecordSet recordSet = new RecordSet();
        recordSet.addRecord(record);

        String ruleString = buildRuleString(pageCode, recordSet.getRecordList());

        // check syntax error
        Properties properties = new Properties();
        properties.setProperty( "drools.dialect.java.compiler.lnglevel","1.6" );
        PackageBuilderConfiguration cfg =
            new PackageBuilderConfiguration( properties );
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder(cfg);
        builder.add(ResourceFactory.newReaderResource(new StringReader(ruleString)), ResourceType.DRL);
        KnowledgeBuilderErrors errors = builder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error : errors) {
                addErrorValidationMessage(validationMessageBuffer, error.getMessage());
            }
        } else {
            // check field is defined on page or not
            PackageMetaData packageMetaData = parsePackageMetaData(pageCode, recordSet.getRecordList());
            RuleMetaData ruleMetaData = packageMetaData.getRuleMetaDataMap().get(ruleId);
            Map<String, Set<String>> pageFieldMap = getPageFields(pageCode);
            for (String fieldId : ruleMetaData.getEnforcingFieldSet()) {
                boolean found = false;
                for (Set<String> layerFieldSet : pageFieldMap.values()) {
                    if (layerFieldSet.contains(fieldId) || layerFieldSet.contains(fieldId + "_GH")) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    addWarningValidationMessage(validationMessageBuffer, "field:[" + fieldId + "] is not defined on Page");
                }
            }
            // check event
            if (!ruleMetaData.isOnLoadAddOrChangeEvent() && !ruleMetaData.isOnSaveEvent()) {
                addErrorValidationMessage(validationMessageBuffer, "Either OnLoadAddOrChangeEvent or OnSaveEvent is required");
            } else if (ruleMetaData.isOnLoadAddOrChangeEvent() && ruleMetaData.isOnSaveEvent()) {
                addErrorValidationMessage(validationMessageBuffer, "Both OnLoadAddOrChangeEvent and OnSaveEvent are not allowed");
            } else {
                // rule for Load/Add/Change
                if (ruleMetaData.isOnLoadAddOrChangeEvent()) {
                    // Rule for Load/Add/Change should match on exactly one NonGrid or Grid record
                    if (ruleMetaData.getNonGridRecordCount() + ruleMetaData.getGridRecordCount() != 1) {
                        addErrorValidationMessage(validationMessageBuffer, "Rule for Load/Add/Change should match on exactly one NonGrid or Grid record");
                    } else if (ruleMetaData.getGridRecordCount() == 1) {
                        checkGridFieldFromSameLayer(ruleMetaData, pageFieldMap, validationMessageBuffer);
                    }
                    for (String fieldId : ruleMetaData.getAllAccessedFieldSet()) {
                        if (fieldId.endsWith("_GH")) {
                            String warningMessage = new StringBuffer("Field:[").append(fieldId).
                                    append("] has a \"_GH\" suffix. It doesn't match fields for Add and Change processing, ").
                                    append("causing this rule to be skipped for those events.").toString();
                            addWarningValidationMessage(validationMessageBuffer, warningMessage);
                        }
                    }
                }
                // rule for save
                if (ruleMetaData.isOnSaveEvent()) {
                    if (ruleMetaData.getNonGridRecordCount() + ruleMetaData.getGridRecordCount() == 0) {
                        addErrorValidationMessage(validationMessageBuffer, "Rule for Save should matches on at least one NonGrid or Grid record");
                    } else if (ruleMetaData.getGridRecordCount() == 1) {
                        checkGridFieldFromSameLayer(ruleMetaData, pageFieldMap, validationMessageBuffer);
                    }
                    if (ruleMetaData.hasSetValueMethod() && (ruleMetaData.getNonGridRecordCount() + ruleMetaData.getGridRecordCount() > 1)) {
                        addErrorValidationMessage(validationMessageBuffer, "setRecordValue method must be used when multiple non-Header Records are defined as conditions for a rule");
                    }
                }
                Set enforcingSet = ruleMetaData.getEnforcingFieldSet();
                for (String fieldId : ruleMetaData.getFieldIdSetInSetValueMethod()) {
                    if (!enforcingSet.contains(fieldId)) {
                        addErrorValidationMessage(validationMessageBuffer, "Field id [" + fieldId +
                                "] is being changed but is not checked in the when clause. This may cause an infinite loop");
                    }
                }
                if (ruleMetaData.hasActionHideRow() && !ruleMetaData.hasConditionDisplayIndicator()) {
                    addErrorValidationMessage(validationMessageBuffer, "Condition \"Row is displayed\" is required if there is action to hide row");
                }
            }
        }
        String validationMessage = validationMessageBuffer.toString();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateRule", validationMessage);
        }
        return validationMessage;
    }

    /**
     * get rule source code
     *
     * @param record
     * @return
     */
    public String getRuleSourceCode(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRuleSourceCode", new Object[]{record});
        }
        buildRuleCode(record);
        String ruleCode = RuleFields.getRuleCode(record);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRuleSourceCode", ruleCode);
        }
        return ruleCode;
    }



    /**
     *  fields checked in the Grid record of the when clause are not from different Layers (meaning they are from different grids).
     *
     * @param ruleMetaData
     * @param pageFieldMap
     * @param validationMessageBuffer
     */
    protected void checkGridFieldFromSameLayer(RuleMetaData ruleMetaData, Map<String, Set<String>> pageFieldMap,
                                               StringBuffer validationMessageBuffer) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkGridFieldFromSameLayer", new Object[]{ruleMetaData, pageFieldMap, validationMessageBuffer});
        }

        boolean isCheckDone = false;
        String foundLayerId = null;
        for (String fieldId : ruleMetaData.getGridEnforcingFieldSet()) {
            for (String layerId : pageFieldMap.keySet()) {
                Set<String> layerFieldSet = pageFieldMap.get(layerId);
                if (layerFieldSet.contains(fieldId) || layerFieldSet.contains(fieldId + "_GH")) {
                    if (foundLayerId == null) {
                        // found one layer
                        foundLayerId = layerId;
                        break;
                    } else if (!foundLayerId.equals(layerId)) {
                        // found another layer
                        addWarningValidationMessage(validationMessageBuffer, "Fields checked in the Grid record are from different Layers");
                        isCheckDone = true;
                        break;
                    }
                }
            }
            if (isCheckDone) {
                break;
            }
        }
        l.exiting(getClass().getName(), "checkGridFieldFromSameLayer");
    }

    /**
     * execute rules
     * @param knowledgeBase
     * @param context
     * @param factData
     */
    protected void executeRules(KnowledgeBase knowledgeBase, Context context, List factData) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "executeRules", new Object[]{knowledgeBase, context, factData});
        }
        StatelessKnowledgeSession ksession = knowledgeBase.newStatelessKnowledgeSession();
        // add log
        KnowledgeRuntimeLogger logger = RuleLogFactory.getInstance().newLogger(ksession);
        // add listener
        TrackingAgendaEventListener trackingAgendaEventListener = new TrackingAgendaEventListener();
        trackingAgendaEventListener.setContext(context);
        ksession.addEventListener(trackingAgendaEventListener);
        //add global variable
        ksession.setGlobal("c", context);
        ksession.execute(factData);
        //close log
        logger.close();
        l.exiting(getClass().getName(), "executeRules");
    }

    /**
     * Returns the knowledge base info for the page, or null if none exists for the page
     * @param pageCode
     * @return
     */
    protected synchronized KnowledgeBaseInfo getKnowledgeBaseInfo(String pageCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getKnowledgeBase", new Object[]{pageCode});
        }

        KnowledgeBaseInfo knowledgeBaseInfo = m_knowledgeBaseInfoMap.get(pageCode);
        long lastModifiedTime = getLastModifiedTime(pageCode);
        if (knowledgeBaseInfo != null && lastModifiedTime == -1) {
            // Remove the cached knowledge base because the rules don't exist for the page anymore
            m_knowledgeBaseInfoMap.remove(pageCode);
            knowledgeBaseInfo = null;
        }
        else if (knowledgeBaseInfo == null || knowledgeBaseInfo.getLastModifiedTime() < lastModifiedTime ) {
            knowledgeBaseInfo = buildKnowledgeBaseInfo(pageCode);
            m_knowledgeBaseInfoMap.put(pageCode, knowledgeBaseInfo);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getKnowledgeBase", knowledgeBaseInfo);
        }
        return knowledgeBaseInfo;
    }

    /**
     * Build knowledge base for page
     * @return A KnowledgeBaseInfo object for the specifiec page or null if there was no knowledge base found.
     * @throws Exception
     */
    protected KnowledgeBaseInfo buildKnowledgeBaseInfo(String pageCode) throws ConfigurationException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "buildKnowledgeBaseInfo", new Object[]{pageCode});
        }

        KnowledgeBaseInfo knowledgeBaseInfo = new KnowledgeBaseInfo();

        List<Record> normalRuleList = new ArrayList();
        List<Record> removeRecordRuleList = new ArrayList();

        Record paramRecord = new Record();
        paramRecord.setFieldValue("pageCode", pageCode);

        RecordSet recordSet = getPageRuleDAO().loadAllPageRule(paramRecord, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
        // ignore rule with error message
        RecordSet filteredRecordSet = new RecordSet();
        for (Record record : recordSet.getRecordList()) {
            String validationMessage = RuleFields.getValidationMessage(record);
            if (validationMessage != null && validationMessage.indexOf("Error: ") >= 0) {
                String ruleId = RuleFields.getRuleId(record);
                l.logp(Level.WARNING, getClass().getName(), "buildKnowledgeBaseInfo", "Ignore Rule [" + ruleId + "] " +
                        "on page [" + pageCode + "] because it has error message");
            } else {
                filteredRecordSet.addRecord(record);
            }
        }
        
        for (Record record : filteredRecordSet.getRecordList()) {
            buildRuleCode(record);
            String ruleCode = RuleFields.getRuleCode(record);
            if (ruleCode.indexOf("c.removeRow(") > 0) {
                removeRecordRuleList.add(record);
            } else {
                normalRuleList.add(record);
            }
        }
        if (removeRecordRuleList.size()>0){
            String ruleString = buildRuleString(pageCode, removeRecordRuleList);
            KnowledgeBase knowledgeBase = buildKnowledgeBase(new StringReader(ruleString));
            knowledgeBaseInfo.setRemoveRecordKnowledgeBase(knowledgeBase);
        }
        if (normalRuleList.size()>0){
            String ruleString = buildRuleString(pageCode, normalRuleList);
            KnowledgeBase knowledgeBase = buildKnowledgeBase(new StringReader(ruleString));
            knowledgeBaseInfo.setKnowledgeBase(knowledgeBase);
        }

        // Parse the rule meta data
        knowledgeBaseInfo.setPackageMetaData(parsePackageMetaData(pageCode, filteredRecordSet.getRecordList()));

        // Store the last modified time
        knowledgeBaseInfo.setLastModifiedTime(getLastModifiedTime(pageCode));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "buildKnowledgeBaseInfo", knowledgeBaseInfo);
        }
        return knowledgeBaseInfo;
    }

    /**
     * build knowledge base
     * @param reader
     * @return
     */
    protected KnowledgeBase buildKnowledgeBase(Reader reader) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "buildKnowledgeBase", new Object[]{reader});
        }
        // Build the knowledge base
        Properties properties = new Properties();
        properties.setProperty( "drools.dialect.java.compiler.lnglevel","1.6" );
        PackageBuilderConfiguration cfg =
            new PackageBuilderConfiguration( properties );
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(cfg);
        kbuilder.add(ResourceFactory.newReaderResource(reader), ResourceType.DRL);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            StringBuffer errorStringBuffer = new StringBuffer("Could not parse the knowledge base rules.\n");
            for (KnowledgeBuilderError error : errors) {
                l.severe(error.getMessage());
                errorStringBuffer.append(error.getMessage()).append("\n");
            }
            ConfigurationException e = new ConfigurationException(errorStringBuffer.toString());
            l.throwing(this.getClass().getName(), "buildKnowledgeBase", e);
            throw e;
        }
        // create knowledge base
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "buildKnowledgeBase", knowledgeBase);
        }
        return knowledgeBase;
    }

    /**
     * Return the last modified time of the rules for the page if there are any rule.
     * If there are no rules for the page, return -1.
     * @param pageCode
     * @return
     */
    protected long getLastModifiedTime(String pageCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLastModifiedTime", new Object[]{pageCode});
        }

        RequestStorageManager rsm = RequestStorageManager.getInstance();
        Date date = null;
        if(rsm.has(pageCode+"KnowledgeBase.LastModifiedTime")) {
            date = new Date((Long)rsm.get(pageCode+"KnowledgeBase.LastModifiedTime"));
        } else {
            date = getPageRuleDAO().getLastModifiedTime(pageCode);
        }

        long lastModifiedTime = date == null ? -1 : date.getTime();

        rsm.set(pageCode+"KnowledgeBase.LastModifiedTime", new Long(lastModifiedTime));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLastModifiedTime", lastModifiedTime);
        }
        return lastModifiedTime;
    }

    /**
     * parse rule code and get package meta data
     * @param pageCode
     * @param ruleList
     * @return
     */
    protected PackageMetaData parsePackageMetaData(String pageCode, List<Record> ruleList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "parsePackageMetaData", new Object[]{pageCode, ruleList});
        }
        PackageMetaData packageMetaData = new PackageMetaData();
        for (Record record : ruleList) {
            RuleMetaData metaData = new RuleMetaData();
            String ruleId = RuleFields.getRuleId(record);
            metaData.setRuleId(ruleId);
            packageMetaData.addRuleMetaData(ruleId, metaData);
            String ruleCode = RuleFields.getRuleCode(record);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new StringReader(ruleCode));

                boolean isWhen = false;
                boolean isThen = false;

                String oneLine;
                while ((oneLine = reader.readLine()) != null) {
                    if (!oneLine.matches("^\\s*$")) {
                        if (oneLine.matches("^\\s*when\\s*$")) {
                            isWhen = true;
                        } else if (oneLine.matches("^\\s*then\\s*$")) {
                            isWhen = false;
                            isThen = true;
                        } else {
                            String recordType = null;
                            if (isWhen) {
                                //event
                                if (oneLine.indexOf("OnLoadAddOrChangeEvent") >= 0) {
                                    metaData.setOnLoadAddOrChangeEvent(true);
                                } else if (oneLine.indexOf("OnSaveEvent") >= 0) {
                                    metaData.setOnSaveEvent(true);
                                } else {
                                    // match record
                                    Matcher recordMatcher = c_recordPattern.matcher(oneLine);
                                    if (recordMatcher.matches()) {
                                        recordType = recordMatcher.group(1);
                                        metaData.addRecord(recordType);
                                        if (Record.TYPE_GRID.equals(recordType)) {
                                            Matcher displayIndicatorMatcher = c_displayIndicatorPattern.matcher(oneLine);
                                            if (displayIndicatorMatcher.matches()) {
                                                metaData.setConditionDisplayIndicator(true);
                                            }
                                        } else if (Record.TYPE_HEADER.equals(recordType)) {
                                            metaData.setAccessHeaderRecord(true);
                                        }
                                    } else {
                                        // retrieve the System parameter name
                                        Matcher systemParamMatcher = c_systemParamPattern.matcher(oneLine);
                                        if (systemParamMatcher.matches()) {
                                            metaData.getRelatedSystemParamSet().add(systemParamMatcher.group(1));
                                        } else {
                                            // retrieve the profile name
                                            Matcher profileMatcher = c_profilePattern.matcher(oneLine);
                                            if (profileMatcher.matches()) {
                                                metaData.getRelatedProfileSet().add(profileMatcher.group(1));
                                            }
                                        }
                                    }
                                }
                                //match update indicator for a record type (Grid or Non Grid)
                                Matcher updateIndicatorMatcher = c_updateIndicatorPattern.matcher(oneLine);
                                while (updateIndicatorMatcher.find()) {
                                    if (!recordType.equals(Record.TYPE_HEADER))
                                        metaData.setUpdateIndicator(recordType);
                                }
                                //match TriggeredField
                                Matcher triggeredFieldMatcher = c_triggeredFieldPattern.matcher(oneLine);
                                while (triggeredFieldMatcher.find()) {
                                    String fieldId = triggeredFieldMatcher.group(1);
                                    if (isWhen) {
                                        metaData.addEnforcingFieldId(fieldId, Record.TYPE_NONGRID);
                                        metaData.addEnforcingFieldId(fieldId, Record.TYPE_GRID);
                                    }
                                }
                            }
                            if (isWhen || isThen) {
                                // match all field id
                                Matcher fieldIdMatcher = c_fieldIdPattern.matcher(oneLine);
                                while (fieldIdMatcher.find()) {
                                    String fieldId = fieldIdMatcher.group(1);
                                    if (isWhen) {
                                        metaData.addEnforcingFieldId(fieldId, recordType);
                                    } else {
                                        metaData.addConsequenceFieldId(fieldId);
                                    }
                                }
                                // match all field id in original map
                                Matcher originalFieldIdMatcher = c_originalFieldIdPattern.matcher(oneLine);
                                while (originalFieldIdMatcher.find()) {
                                    String fieldId = originalFieldIdMatcher.group(1);
                                    if (isWhen) {
                                      metaData.addEnforcingFieldId(fieldId, recordType, true);
                                    } else {
                                      metaData.addConsequenceFieldId(fieldId, true);
                                    }
                                }
                            }
                            if (isThen){
                                // match field id in method setValue
                                Matcher setValueMatcher = c_setValuePattern.matcher(oneLine);
                                if (setValueMatcher.matches()) {
                                    String methodName = setValueMatcher.group(1);
                                    String fieldId = setValueMatcher.group(2);
                                    metaData.addFieldIdInSetValueMethod(methodName, fieldId);
                                } else {
                                    Matcher hideRowMatcher = c_hideRowPattern.matcher(oneLine);
                                    if (hideRowMatcher.matches()) {
                                          metaData.setActionHideRow(true);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                ConfigurationException e1 = new ConfigurationException("Failed to parse the rules on page '" + pageCode + "' for meta data.", e);
                l.throwing(this.getClass().getName(), "parsePackageMetaData", e1);
                throw e1;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        l.logp(Level.SEVERE, getClass().getName(), "parsePackageMetaData", "Failed to close the file reader", e);
                    }
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "parsePackageMetaData", packageMetaData);
        }
        return packageMetaData;
    }

    /**
     * build rule string
     * @param pageCode
     * @param ruleList
     * @return
     */
    protected String buildRuleString(String pageCode, List<Record> ruleList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "buildRuleString", new Object[]{pageCode, ruleList});
        }
        StringBuilder ruleBuffer = new StringBuilder();
        String subsystemCode = ApplicationContext.getInstance().getProperty("applicationId", "");
        // package
        ruleBuffer.append("package ").append(subsystemCode.replace(" ", "")).append(".").append(pageCode).append(";\n\n");
        // imports
        Set<String> set = getRuleImports();
        for (String importClass : set) {
            ruleBuffer.append("import ").append(importClass).append(";\n");
        }
        // global
        ruleBuffer.append(GLOBAL_VARIABLE);

        int recordConditionCount = 0;

        for (Record record : ruleList) {
            String ruleText =  RuleFields.getRuleCode(record);
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "buildRuleString", "Rule Text Before:" + ruleText);
            }
            // add a common condition for each Record to make them different
            StringBuilder currentRuleBuffer = new StringBuilder();
            String oneLine = null;
            boolean reachWhen = false;
            boolean reachThen = false;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new StringReader(ruleText));
                while ((oneLine = reader.readLine()) != null) {
                    if (reachThen) {
                        currentRuleBuffer.append(oneLine).append("\n");
                    } else if (reachWhen) {
                        Matcher recordMatcher = c_recordPattern.matcher(oneLine);
                        if (recordMatcher.matches()) {
                            recordConditionCount++;
                            int position = oneLine.indexOf("(");
                            String beforeAndBracket = oneLine.substring(0, position + 1);
                            String after = oneLine.substring(position + 1);
                            currentRuleBuffer.append(beforeAndBracket);
                            currentRuleBuffer.append(recordConditionCount);
                            currentRuleBuffer.append("==");
                            currentRuleBuffer.append(recordConditionCount);
                            currentRuleBuffer.append(", ");
                            currentRuleBuffer.append(after).append("\n");
                        } else {
                            currentRuleBuffer.append(oneLine).append("\n");
                        }
                        if (oneLine.matches("^\\s*then\\s*$")) {
                            reachThen = true;
                        }
                    } else {
                        currentRuleBuffer.append(oneLine).append("\n");
                        if (oneLine.matches("^\\s*when\\s*$")) {
                            reachWhen = true;
                        }
                    }
                }
            } catch (IOException e) {
                ConfigurationException e1 = new ConfigurationException("Failed to process rule text.", e);
                l.throwing(this.getClass().getName(), "buildRuleString", e1);
                throw e1;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        l.logp(Level.SEVERE, getClass().getName(), "buildRuleString", "Failed to close the file reader", e);
                    }
                }
            }
            String ruleTextAfter = currentRuleBuffer.toString();
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "buildRuleString", "Rule Text after:" + ruleTextAfter);
            }
            
            ruleBuffer.append(ruleTextAfter).append("\n");
        }
        String ruleString = ruleBuffer.toString();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "buildRuleString", ruleString);
        }
        return ruleString;
    }

    /**
     * build rule code
     * @param record
     */
    protected void buildRuleCode(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "buildRuleCode", new Object[]{record});
        }
        String ruleId = RuleFields.getRuleId(record);
        String ruleText = RuleFields.getRuletText(record);
        ruleText = removeCommentedLines(ruleText);
        StringBuffer currentRuleBuffer = new StringBuffer();
        currentRuleBuffer.append("rule \"");
        if (RuleFields.isRuleFromBase(record)) {
            currentRuleBuffer.append("Base Rule: ");
        } else {
            currentRuleBuffer.append("Cust Rule: ");
        }
        currentRuleBuffer.append(ruleId).append("\"\n");
        currentRuleBuffer.append(ruleText);
        // get last line of the rule
        String lastValidLine = null;
        int position = ruleText.lastIndexOf("\n");
        if (position >= 0) {
            lastValidLine = ruleText.substring(position + 1);
        } else {
            lastValidLine = ruleText;
        }
        if (!lastValidLine.matches("^\\s*end\\s*$")) {
            currentRuleBuffer.append("\nend\n\n");
        }
        String currentRule = currentRuleBuffer.toString();
        if (RuleFields.isBusinessRule(record)) {
            currentRule = convertBusinessRuleToTechnicalRule(currentRule);
        }
        currentRule = processRuleCode(currentRule);
        RuleFields.setRuleCode(record, currentRule);
        l.exiting(getClass().getName(), "buildRuleCode");
    }

    /**
     * process rule parameter
     *
     * Add the "no-loop true" option if setValue() is called and this option is not already specified in the rule
     * Add the dialect "mvel" option if no dialect option is specified in the rule
     *
     * @param rule
     * @return
     */
    protected String processRuleCode(String rule) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processRuleCode", new Object[]{rule});
        }
        StringBuffer ruleBuffer = new StringBuffer();
        StringBuffer ruleBodyBuffer = new StringBuffer();
        String firstLine = null;
        boolean reachWhen = false;
        boolean reachThen = false;
        String oneLine = null;
        BufferedReader reader = null;
        boolean hasDialect = false;
        boolean hasNoLoop = false;
        boolean hasSetValueMethod = false;
        try {
            reader = new BufferedReader(new StringReader(rule));
            firstLine = reader.readLine();
            while ((oneLine = reader.readLine()) != null) {
                ruleBodyBuffer.append(oneLine).append("\n");
                if (reachThen) {
                    Matcher setValueMatcher = c_setValuePattern.matcher(oneLine);
                    if (setValueMatcher.matches()) {
                        hasSetValueMethod = true;
                    }
                } else if (reachWhen) {
                    if (oneLine.matches("^\\s*then\\s*$")) {
                        reachThen = true;
                    }
                } else {
                    if (oneLine.matches("^\\s*when\\s*$")) {
                        reachWhen = true;
                    } else {
                        // rule parameter
                        if (oneLine.indexOf("dialect") >= 0) {
                            hasDialect = true;
                        } else if (oneLine.indexOf("no-loop") >= 0) {
                            hasNoLoop = true;
                        }
                    }
                }
            }
            ruleBuffer.append(firstLine).append("\n");
            if (!hasDialect) {
                ruleBuffer.append("dialect \"mvel\"\n");
            }
            if (hasSetValueMethod && !hasNoLoop) {
                ruleBuffer.append("no-loop true\n");
            }
            ruleBuffer.append(ruleBodyBuffer.toString());
        } catch (IOException e) {
            ConfigurationException e1 = new ConfigurationException("Failed to process rule code.", e);
            l.throwing(this.getClass().getName(), "processRuleCode", e1);
            throw e1;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    l.logp(Level.SEVERE, getClass().getName(), "processRuleCode", "Failed to close the file reader", e);
                }
            }
        }
        String result = ruleBuffer.toString();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processRuleCode", result);
        }
        return result;
    }

    /**
     * Remove empty and commented lines
     *
     * @param rule
     * @return
     */
    protected String removeCommentedLines(String rule) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "removeCommentedLines", new Object[]{rule});
        }
        StringBuffer ruleBuffer = new StringBuffer();
        String oneLine = null;
        BufferedReader reader = null;
        int commentPosition;
        boolean isMultipleLineComment = false;
        try {
            reader = new BufferedReader(new StringReader(rule));
            while ((oneLine = reader.readLine()) != null) {
                // ignore commented lines
                if (isMultipleLineComment) {
                    commentPosition = oneLine.indexOf("*/");
                    if (commentPosition >= 0) {
                        // multiple line comment ends
                        isMultipleLineComment = false;
                        oneLine = oneLine.substring(commentPosition + 2);
                    } else {
                        continue;
                    }
                }
                commentPosition = oneLine.indexOf("/*");
                if (commentPosition >= 0) {
                    // multiple line comment ends
                    String before = oneLine.substring(0, commentPosition);
                    String after = oneLine.substring(commentPosition + 2);
                    commentPosition = after.indexOf("*/");
                    if (commentPosition >= 0) {
                        // comment ends in the same line
                        after = after.substring(commentPosition + 2);
                        oneLine = before + after;
                    } else {
                        isMultipleLineComment = true;
                        oneLine = before;
                    }
                }
                // Drools won't support # in the future release
                if (oneLine.trim().startsWith("#")) {
                    continue;
                }
                commentPosition = oneLine.indexOf("//");
                if (commentPosition >= 0) {
                    oneLine = oneLine.substring(0, commentPosition);
                }
                if (oneLine.trim().length() == 0) {
                    continue;
                }
                // valid lines
                ruleBuffer.append(oneLine).append("\n");
            }
        } catch (IOException e) {
            ConfigurationException e1 = new ConfigurationException("Failed to remove commented lines in.", e);
            l.throwing(this.getClass().getName(), "removeCommentedLines", e1);
            throw e1;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    l.logp(Level.SEVERE, getClass().getName(), "removeCommentedLines", "Failed to close the file reader", e);
                }
            }
        }
        String result = ruleBuffer.toString();
        if (result.charAt(result.length() - 1) == '\n') {
            result = result.substring(0, result.length() - 1);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "removeCommentedLines", result);
        }
        return result;
    }

    /**
     * add erroe validation message
     * @param buffer
     * @param message
     */
    protected void addErrorValidationMessage(StringBuffer buffer, String message) {
        buffer.append("Error: ").append(message).append("\n");

    }

    /**
     * add warning validation message
     * @param buffer
     * @param message
     */
    protected void addWarningValidationMessage(StringBuffer buffer, String message) {
        buffer.append("Warning: ").append(message).append("\n");
    }

    /**
     * get page fields
     * @return
     */
    protected Map<String, Set<String>> getPageFields(String pageCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPageFields", new Object[]{pageCode});
        }
        Map<String, Set<String>> pageFieldMap = new HashMap<String, Set<String>>();
        RecordSet recordSet = getPageRuleDAO().loadAllPageFields(pageCode);
        for (Record record : recordSet.getRecordList()) {
            String layerId = record.getStringValue("layerId");
            String fieldId = record.getStringValue("fieldId");
            Set layerFieldSet = null;
            if (pageFieldMap.containsKey(layerId)) {
                layerFieldSet = pageFieldMap.get(layerId);
            } else {
                layerFieldSet = new HashSet<String>();
                pageFieldMap.put(layerId, layerFieldSet);
            }
            layerFieldSet.add(fieldId);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPageFields", pageFieldMap);
        }
        return pageFieldMap;
    }

    /**
     * get rule imports Set
     * @return
     */
    protected Set<String> getRuleImports() {
        l.entering(getClass().getName(), "getRuleImports");
        Set<String> ruleImports = new HashSet<String>();
        RecordSet recordSet = getPageRuleDAO().loadAllRuleImport();
        for (Record record : recordSet.getRecordList()) {
            String importClass = record.getStringValue("importClass");
            ruleImports.add(importClass);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRuleImports", ruleImports);
        }
        return ruleImports;
    }

    /**
     * get rule mapping string
     * @return
     */
    protected String getRuleMapping() {
        l.entering(getClass().getName(), "getRuleMapping");
        // create dsl string
        RecordSet recordSet = getPageRuleDAO().loadAllRuleMapping();
        StringBuffer dslStringBuffer = new StringBuffer();
        for (Record mapping : recordSet.getRecordList()) {
            String mappingScope = mapping.getStringValue("mappingScope");
            String languageExpression = mapping.getStringValue("languageExpression");
            String ruleMapping = mapping.getStringValue("ruleMapping");
            dslStringBuffer.append("[").append(mappingScope).append("]");
            dslStringBuffer.append(languageExpression).append("=").append(ruleMapping).append("\n");
        }
        String dslString = dslStringBuffer.toString();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRuleMapping", dslString);
        }
        return dslString;
    }

    /**
     * convert business rule to technical rule
     * @param businessRuleString
     * @return
     */
    protected String convertBusinessRuleToTechnicalRule(String businessRuleString) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "convertBusinessRuleToTechnicalRule", new Object[]{businessRuleString});
        }
        // get expander
        DefaultExpander expander = new DefaultExpander();
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String technicalRuleString = null;
        try {
            boolean success = file.parseAndLoad(new StringReader(getRuleMapping()));
            if (!success) {
                throw new ConfigurationException("There is error when creating rule expander. " + file.getErrors());
            } else {
                expander.addDSLMapping(file.getMapping());
            }
            //convert
            technicalRuleString = expander.expand(new StringReader(businessRuleString));
        } catch (IOException e) {

            throw new ConfigurationException("There is error when coverting business rule to technical rule." + e.getMessage());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "convertBusinessRuleToTechnicalRule", technicalRuleString);
        }
        return technicalRuleString;
    }


    /**
     * init
     */
    public void initialize() {
        // set date format for rules
        System.setProperty("drools.dateformat", DateUtils.DATE_FORMAT_PATTERN);
    }

    /**
     * verify config
     */
    public void verifyConfig() {

    }

    public PageRuleDAO getPageRuleDAO() {
        return m_pageRuleDAO;
    }

    public void setPageRuleDAO(PageRuleDAO pageRuleDAO) {
        m_pageRuleDAO = pageRuleDAO;
    }

    private PageRuleDAO m_pageRuleDAO;
    private Map<String, KnowledgeBaseInfo> m_knowledgeBaseInfoMap = new HashMap<String, KnowledgeBaseInfo>();
    private final Logger l = LogUtils.getLogger(getClass());

    private static final String c_profilePatternStr = "^.*UserProfile *\\(.*name *== *\"([-_a-zA-Z0-9 ]+)\".*$";
    private static final Pattern c_profilePattern = Pattern.compile(c_profilePatternStr);

    private static final String c_systemParamPatternStr = "^.*SysParm *\\(.*name *== *\"([-_a-zA-Z0-9]+)\".*$";
    private static final Pattern c_systemParamPattern = Pattern.compile(c_systemParamPatternStr);

    private static final String c_triggeredFieldPatternStr = "TriggeredField\\(.*id *== *\"([-_a-zA-Z0-9]+)\"\\)";
    private static final Pattern c_triggeredFieldPattern = Pattern.compile(c_triggeredFieldPatternStr);

    private static final String c_fieldIdPatternStr = "fieldMap\\[\"([-_a-zA-Z0-9]+)\"\\]";
    private static final Pattern c_fieldIdPattern = Pattern.compile(c_fieldIdPatternStr);

    private static final String c_originalFieldIdPatternStr = "originalFieldMap\\[\"([-_a-zA-Z0-9]+)\"\\]";
    private static final Pattern c_originalFieldIdPattern = Pattern.compile(c_originalFieldIdPatternStr);

    private static final String c_recordPatternStr = "^.*Record *\\(.*type *== *\"([-_a-zA-Z0-9]+)\".*$";
    private static final Pattern c_recordPattern = Pattern.compile(c_recordPatternStr);

    private static final String c_setValuePatternStr = "^.*(setRecordValue|setValue) *\\( *\"([-_a-zA-Z0-9]+)\".*$";
    private static final Pattern c_setValuePattern = Pattern.compile(c_setValuePatternStr);

    private static final String c_displayIndicatorPatternStr = "^.*displayIndicator *!= *\"N\".*$";
    private static final Pattern c_displayIndicatorPattern = Pattern.compile(c_displayIndicatorPatternStr);

    private static final String c_updateIndicatorPatternStr = "^.*updateIndicator.*$";
    private static final Pattern c_updateIndicatorPattern = Pattern.compile(c_updateIndicatorPatternStr);

    private static final String c_hideRowPatternStr = "^.*c.hideRow *( *).*$";
    private static final Pattern c_hideRowPattern = Pattern.compile(c_hideRowPatternStr);

    private static final String GLOBAL_VARIABLE = "\nglobal Context c\n\n";

}
