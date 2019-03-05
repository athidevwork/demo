package dti.oasis.obr;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.http.RequestIds;
import dti.oasis.obr.event.OnLoadAddOrChangeEvent;
import dti.oasis.obr.event.OnSaveEvent;
import dti.oasis.obr.event.RuleEvent;
import dti.oasis.recordset.DelegateMap;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.PageViewStateAdmin;
import dti.oasis.session.UserSessionManager;
import dti.oasis.session.pageviewstate.PageViewStateManager;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.BaseAction;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.OasisFieldsUtility;
import dti.oasis.util.PageBean;
import dti.oasis.util.StringUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.struts.action.Action;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for request
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 18, 2011
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/24/03         James Gu    Issue#127633 Leave field as a Header field if it's defined as
 *                              a Header field and it's not defined as a field on this page
 * 04/12/2012       James Gu    Issue#127633 add logic to set change in RecordSet back to
 *                              DisconnectedResultSet after executing OBR rule.
 * 04/13/2012       James Gu    Issue#131950 allow customer to determine the NonGrid Fields.
 * 08/28/2012       James Gu    Issue#136968 fix the logic to get RecordSet in request.
 * 02/28/2014       hxk         Issue 152165
 *                              1)  Add check in generateDisconnectedResultSetMap method so that if we have an
 *                                  empty recordset, we will not reference it.
 * 11/19/2014       jxgu        Issue#158594 OBR cache header fields if it is accessed
 * 9/19/2015        Parker      Issue#162759 Generate new pageViewStateId at the beginning of the executeOnPageLoad.
 * 05/17/2017       cesar       182477 - Added copyMaskedFields(). this method will copy the masked field names into the new UWID.
 * 02/28/2018       cesar       191524 - Modify processFieldValue() to decode fields so that OBR can perform the page rul validation.
 * 03/02/2018       cesar       189605 - Modified modified getPageViewCacheMap() from protected to public to allow CSRFInterceptor instantiate the class
 * 05/14/2018       cesar       192983 - Moved copyMaskedFields to OasisFields and call storeOasisFieldsXssOverrides in OasisFields
 * 10/03/2018       jdingle     193748 - Moved fireRules to after the grid OBR fields are set, so that they are set on the correct grid.
 * ---------------------------------------------------
 */
public class RequestHelper {
    public static final String SEPARATOR_FOR_VALUE = "~:~";
    public static final String SEPARATOR_FOR_ATTRIBUTE = "~;~";
    public static final String SEPARATOR_FOR_FIELD = "~,~";
    public static final String OBR_RESULT_EQUAL = "~=~";

    public static final String IS_EXECUTE_BEFORE_SAVE_DONE = "IS_EXECUTE_BEFORE_SAVE_DONE";
    public static final String IS_NONGRID_FIELD_CHANGED = "_isNonGridFieldChanged";
    public static final String IS_SET_PAGE_TO_READ_ONLY_IN_RULE = "IS_SET_PAGE_TO_READ_ONLY_IN_RULE";

    public static Map<String, Record> c_pageRecordMap = Collections.synchronizedMap(new HashMap<String, Record>());
    public static Map<String, String> c_pageAnchorColumnNameMap = Collections.synchronizedMap(new HashMap<String, String>());

    /**
     * add header field id
     * @param fieldId
     */
    public static void addHeaderFieldId(String fieldId) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(RequestHelper.class.getName(), "addHeaderFieldId", new Object[]{fieldId});
        }
        Set fieldIdSet = null;
        RequestStorageManager requestStorageManager = RequestStorageManager.getInstance();
        if (requestStorageManager.has(XML_GRID_HEADER_FIELD_ID_SET)) {
            fieldIdSet = (Set) requestStorageManager.get(XML_GRID_HEADER_FIELD_ID_SET);
        } else {
            fieldIdSet = new HashSet();
            requestStorageManager.set(XML_GRID_HEADER_FIELD_ID_SET, fieldIdSet);
        }
        fieldIdSet.add(fieldId);
        c_l.exiting(RequestHelper.class.getName(), "addHeaderFieldId");
    }

    /**
     * Convert a DisconnectedResultSet to a RecordSet.
     * @param disconnectedResultSet
     * @return
     */
    public static RecordSet convertDisconnectedResultSetToRecordSet(DisconnectedResultSet disconnectedResultSet) {
        int columnCount = disconnectedResultSet.getColumnCount();
        RecordSet rs = new RecordSet();

        List<String> filedNames = new ArrayList<String>();
        for (int i = 1; i <= columnCount; i++) {
            filedNames.add(disconnectedResultSet.getColumnName(i));
        }
        rs.addFieldNameCollection(filedNames);

        disconnectedResultSet.beforeFirst();
        while (disconnectedResultSet.next()) {
            Record record = new Record();
            for (int i = 1; i <= columnCount; i++) {
                record.setFieldValue(disconnectedResultSet.getColumnName(i), disconnectedResultSet.get(i));
            }

            record.setDisplayIndicator(String.valueOf(disconnectedResultSet.getDisplayInd()));
            record.setEditIndicator(String.valueOf(disconnectedResultSet.getEditInd()));
            record.setUpdateIndicator(String.valueOf(disconnectedResultSet.getUpdateInd()));

            rs.addRecord(record);
        }
        disconnectedResultSet.first();
        return rs;
    }

    /**
     * execute rule on page load
     * @param request
     * @param useMapWithoutPrefixes
     * @param action
     */
    public void executeOnPageLoad(HttpServletRequest request, boolean useMapWithoutPrefixes, Action action) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "executeOnPageLoad", new Object[]{request, useMapWithoutPrefixes});
        }

        String currentPageViewStateId = (String)request.getAttribute(RequestIds.CACHE_ID_FOR_PAGE_VIEW);

        //Re_Generate the Page View Id
        PageViewStateManager.getInstance().replaceWithNewPageView(request);

        // copy any base64 masked field to new page id.
        OasisFields.copyMaskedFields(currentPageViewStateId);

        OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);

        OasisFields.storeOasisFieldsXssOverrides(fields);

        PageBean bean = (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN);
        KnowledgeBaseManager knowledgeBaseManager = KnowledgeBaseManager.getInstance();
        if (fields != null && bean != null && knowledgeBaseManager.hasKnowledgeBase(bean.getId())) {
            Map pageViewCachedData = getPageViewCacheMap();
            // check DisconnectedResultSet
            String className = action.getClass().getName();
            Map<DisconnectedResultSet, RecordSet> mapping = generateDisconnectedResultSetMap(request, className);
            // get record for non-grid fields
            Record nonGridFieldRecord = (Record) request.getAttribute(RequestIds.NON_GRID_FIELDS_RECORD);
            HashMap recordSetMap = (HashMap) request.getAttribute(RequestIds.RECORDSET_MAP);

            String forceNonGridFields = null;
            if (action instanceof BaseAction) {
                forceNonGridFields = ((BaseAction) action).getObrNonGridFields();
            }
            if (nonGridFieldRecord != null) {
                // remove grid related fields in NONE_GRID_FIELDS_RECORD
                if (recordSetMap != null) {
                    Iterator it = recordSetMap.keySet().iterator();
                    while (it.hasNext()) {
                        String gridId = (String) it.next();
                        RecordSet recordSet = (RecordSet) recordSetMap.get(gridId);
                        List listCopy = new ArrayList();
                        listCopy.addAll(nonGridFieldRecord.getFieldNameList());
                        for (Object object : listCopy) {
                            String fieldName = (String) object;
                            if (!StringUtils.isBlank(forceNonGridFields)) {
                                // there is override for determining the NonGrid fields.
                                if (("," + forceNonGridFields + ",").indexOf("," + fieldName + ",") >= 0) {
                                    continue;
                                }
                            }
                            if (isFieldNameInSet(fieldName, recordSet.getUpperCaseFieldNameSet(true), useMapWithoutPrefixes)) {
                                // this field name is for grid
                                nonGridFieldRecord.remove(fieldName);
                            }
                        }
                    }
                }
                processNonGridRecordForOldCode(request, nonGridFieldRecord, className);
            } else {
                nonGridFieldRecord = new Record();
            }
            Record newRecord = new Record();
            newRecord.setFields(nonGridFieldRecord);
            c_pageRecordMap.put(className + RequestIds.NON_GRID_FIELDS_RECORD, newRecord);

            // get record for header fields
            Record headerFieldRecord = (Record) request.getAttribute(RequestIds.HEADER_FIELDS_RECORD);
            if (headerFieldRecord != null) {
                newRecord = new Record();
                newRecord.setFields(headerFieldRecord);
                c_pageRecordMap.put(className + RequestIds.HEADER_FIELDS_RECORD, newRecord);
            } else {
                c_pageRecordMap.put(className + RequestIds.HEADER_FIELDS_RECORD, null);
            }

            // get package meta mata
            PackageMetaData packageMetaData = knowledgeBaseManager.getPackageMetaData(bean.getId());
            // validation at runtime
            if (!packageMetaData.isValidateComplete()) {
                validatePackageAtRuntime(bean.getId(), packageMetaData, headerFieldRecord, nonGridFieldRecord, recordSetMap, useMapWithoutPrefixes);
                packageMetaData.setValidateComplete(true);
            }
            List<String> enforcingFieldList = packageMetaData.getEnforcingFieldList();
            List<String> consequenceFieldList = packageMetaData.getConsequenceFieldList();
            List<String> allAccessedFieldList = packageMetaData.getAllAccessedFieldList();
            List<String> originalFieldList = packageMetaData.getOriginalFieldList();

            boolean hasOriginalFieldReference = false;
            String pageViewStateId = (String) RequestStorageManager.getInstance().get(RequestIds.CACHE_ID_FOR_PAGE_VIEW);
            // leave field as a Header field if it's defined as a Header field and it's not defined as a field on this page
            // For example: policyTypeCode on Additional Insured page in ePolicy is a Header field
            List<String> headerEnforcingFieldList = extractHeaderFields(enforcingFieldList, headerFieldRecord, fields);
            List<String> headerConsequenceFieldList = extractHeaderFields(consequenceFieldList, headerFieldRecord, fields);
            List<String> headerAllAccessedFieldList = extractHeaderFields(allAccessedFieldList, headerFieldRecord, fields);

            // create context
            Context context = new Context();
            context.setPageBean(bean);
            context.setRequest(request);
            context.setSession(request.getSession());

            // get fact data
            List factDataList = new ArrayList();
            // non-grid fields

            Map maskedFields = ((BaseAction)action).getAllMaskedFields(fields);
            processFieldValue(request, nonGridFieldRecord, maskedFields);
            nonGridFieldRecord.setType(Record.TYPE_NONGRID);

            if (packageMetaData.isAccessOriginal()) {
                hasOriginalFieldReference = cacheReferencedOriginalFields(dti.oasis.http.RequestIds.NON_GRID_FIELDS_RECORD, nonGridFieldRecord, pageViewStateId, pageViewCachedData, originalFieldList);
                if (hasOriginalFieldReference) {
                    addOriginalFieldsToRecord(nonGridFieldRecord, RequestIds.NON_GRID_FIELDS_RECORD, pageViewCachedData);
                }
            }

            factDataList.add(nonGridFieldRecord);

            // header fields
            if (headerFieldRecord != null) {
                processFieldValue(request, headerFieldRecord, maskedFields);
                headerFieldRecord.setType(Record.TYPE_HEADER);

                if (packageMetaData.isAccessOriginal() || packageMetaData.isAccessHeaderRecord()) {
                    List<String> fieldsToCache = new ArrayList<String>();
                    fieldsToCache.addAll(originalFieldList);
                    if (packageMetaData.isAccessHeaderRecord()) {
                        // if the rule is accessing header fields, we need to cache these header fields for executeOnChange.
                        fieldsToCache.addAll(packageMetaData.getEnforcingFieldList(Record.TYPE_HEADER));
                    }
                    hasOriginalFieldReference = cacheReferencedOriginalFields(dti.oasis.http.RequestIds.HEADER_FIELDS_RECORD, headerFieldRecord, pageViewStateId, pageViewCachedData, fieldsToCache);
                    if (hasOriginalFieldReference) {
                        addOriginalFieldsToRecord(headerFieldRecord, RequestIds.HEADER_FIELDS_RECORD, pageViewCachedData);
                    }
                }

                factDataList.add(headerFieldRecord);
            }

            // fire rule for header field and non-grid fields
            fireRules(context, factDataList.toArray(), new RuleEvent[]{new OnLoadAddOrChangeEvent()}, ProcessingEvents.OnLoad);

            // publish field value changed in NonGrid record in rule
            if (nonGridFieldRecord.getChangedFieldsInRule().size() > 0) {
                try {
                    Record changedFields = getRecordForChangedField(nonGridFieldRecord);
                    ActionHelper.recordToBeans(request, changedFields, fields);
                } catch (Exception e) {
                    AppException exception = new AppException("Fail to publish field value changed in rule. " + e.getMessage());
                    c_l.throwing(getClass().getName(), "executeOnPageLoad", exception);
                    throw exception;
                }
            }

            // fire rule for grid data with header field
            List gridIdList = new ArrayList();
            if (recordSetMap != null) {
                HashMap <String, RecordSet> originalRecordSetMap;
                boolean isOriginalRecordSetMapIsFromRSM = false;
                // If the initial request raises an validation exception, use the original field/recordset information
                // from the request storage manager and cache that.
                if (RequestStorageManager.getInstance().has(RequestIds.CACHE_ID_FOR_PAGE_VIEW_DATA)) {
                    Map cachePageViewData = (Map) RequestStorageManager.getInstance().get(RequestIds.CACHE_ID_FOR_PAGE_VIEW_DATA);
                    if (cachePageViewData.get(dti.oasis.http.RequestIds.RECORDSET_MAP) != null) {
                        c_l.logp(Level.FINER, getClass().getName(), "executeOnPageLoad", "Retrieving Cache for [" + dti.oasis.http.RequestIds.RECORDSET_MAP
                                + " from RequestStorageManager and cached into page view state [" + pageViewStateId + "]");
                        originalRecordSetMap = (HashMap) cachePageViewData.get(dti.oasis.http.RequestIds.RECORDSET_MAP);
                        pageViewCachedData.put(dti.oasis.http.RequestIds.RECORDSET_MAP, originalRecordSetMap);
                        isOriginalRecordSetMapIsFromRSM = true;
                    } else {
                        originalRecordSetMap = new HashMap();
                    }
                } else {
                    originalRecordSetMap = new HashMap();
                }
                Iterator it = recordSetMap.keySet().iterator();
                while (it.hasNext()) {
                    hasOriginalFieldReference = false;
                    boolean isFirstTimeInLoop = true;
                    String gridId = (String) it.next();
                    gridIdList.add(gridId);

                    List originalGridFieldList = new ArrayList();
                    RecordSet originalRecordSet;
                    if (isOriginalRecordSetMapIsFromRSM) {
                        originalRecordSet = (RecordSet) originalRecordSetMap.get(gridId);
                    } else {
                        originalRecordSet = new RecordSet();
                    }

                    RecordSet recordSet = (RecordSet) recordSetMap.get(gridId);
                    List gridFactDataList = new ArrayList();
                    for (Record record : recordSet.getRecordList()) {
                        record.setType(Record.TYPE_GRID);
                        ((DelegateMap) record.getFieldMap()).setTryFieldIdWithoutPrefix(useMapWithoutPrefixes);

                        if (isOriginalRecordSetMapIsFromRSM) {
                            addOriginalFieldMap(record, originalRecordSet);
                        } else {
                            if (packageMetaData.isAccessOriginal()) {
                                if (isFirstTimeInLoop) {
                                    it = originalFieldList.iterator();
                                    while (it.hasNext()) {
                                        String fieldId = (String) it.next();
                                        if (record.hasField(fieldId)) {
                                            hasOriginalFieldReference = true;
                                            originalGridFieldList.add(fieldId);
                                        }
                                    }
                                }

                                if (hasOriginalFieldReference) {
                                    newRecord = new Record();
                                    newRecord.setRowId(record.getRowId());
                                    it = originalGridFieldList.iterator();
                                    while (it.hasNext()) {
                                        String fieldId = (String) it.next();
                                        newRecord.setField(fieldId, record.getField(fieldId));
                                    }
                                    originalRecordSet.addRecord(newRecord);
                                    record.setOriginalFieldMap(newRecord.getFieldMap());
                                  }
                            }
                        }
                        gridFactDataList.add(record);
                    }


                    if (hasOriginalFieldReference && !isOriginalRecordSetMapIsFromRSM) {
                        originalRecordSetMap.put(gridId, originalRecordSet);
                    }

                    if (headerFieldRecord != null) {
                        gridFactDataList.add(headerFieldRecord);
                    }
                    // extract grid enforcing field list
                    String gridEnforcingFieldList = extractGridFields(enforcingFieldList, recordSet.getUpperCaseFieldNameSet(true), useMapWithoutPrefixes, forceNonGridFields);
                    recordSet.setOBREnforcingFieldList(gridEnforcingFieldList);
                    // extract grid consequence field list
                    String gridConsequenceFieldList = extractGridFields(consequenceFieldList, recordSet.getUpperCaseFieldNameSet(true), useMapWithoutPrefixes, forceNonGridFields);
                    recordSet.setOBRConsequenceFieldList(gridConsequenceFieldList);
                    // extract grid all accessed field list
                    String gridAllAccessedFieldList = extractGridFields(allAccessedFieldList, recordSet.getUpperCaseFieldNameSet(true), useMapWithoutPrefixes, forceNonGridFields);
                    recordSet.setOBRAllAccessedFieldList(gridAllAccessedFieldList);
                    // fire rule for each grid
                    fireRules(context, gridFactDataList.toArray(), new RuleEvent[]{new OnLoadAddOrChangeEvent()}, ProcessingEvents.OnLoad);
                    // set update indicator for grid. For non grid it is set in request attribute
                    if (packageMetaData.isGridUpdateIndSet())
                        recordSet.setOBREnforcingGridUpdateIndicator("Y");
                }

                if (!isOriginalRecordSetMapIsFromRSM && originalRecordSetMap.size() > 0) {
                    if (!pageViewCachedData.containsKey(dti.oasis.http.RequestIds.RECORDSET_MAP)) {
                        c_l.logp(Level.FINE, getClass().getName(), "executeOnPageLoad", "Caching original grid record set for page view state id:" + pageViewStateId);
                        pageViewCachedData.put(dti.oasis.http.RequestIds.RECORDSET_MAP, originalRecordSetMap) ;
                        if (c_l.isLoggable(Level.FINEST)) {
                            c_l.logp(Level.FINEST, getClass().getName(), "executeOnPageLoad", "Cached original grid record set count is " + originalRecordSetMap.size() + " for page view state id:" + pageViewStateId );
                            it = originalRecordSetMap.keySet().iterator();
                            String gridId = "";
                            while (it.hasNext()) {
                                gridId = (String) it.next();
                                c_l.logp(Level.FINEST, getClass().getName(), "executeOnPageLoad", "Cached original grid [" + gridId + "] record count is " + originalRecordSetMap.get(gridId).getRecordList().size() + " for page view state id:" + pageViewStateId);
                                c_l.logp(Level.FINEST, getClass().getName(), "executeOnPageLoad", "Cached original grid content (only referenced original field list) is " + originalRecordSetMap.get(gridId).getRecordList().toString() + " for page view state id:" + pageViewStateId);
                            }
                        }
                    }
                }
            }

            // add back header Fields
            enforcingFieldList.addAll(headerEnforcingFieldList);
            consequenceFieldList.addAll(headerConsequenceFieldList);
            allAccessedFieldList.addAll(headerAllAccessedFieldList);

            // generate OBREnforcingFieldList for non-grid fields
            String[] enforcingFieldArray = (String[]) enforcingFieldList.toArray(new String[enforcingFieldList.size()]);
            request.setAttribute("OBREnforcingFieldList", StringUtils.arrayToDelimited(enforcingFieldArray, ",", false, false));

            // generate OBRConsequenceFieldList for non-grid fields
            String[] consequenceFieldArray = (String[]) consequenceFieldList.toArray(new String[consequenceFieldList.size()]);
            request.setAttribute("OBRConsequenceFieldList", StringUtils.arrayToDelimited(consequenceFieldArray, ",", false, false));

            // generate OBRAllAccessedFieldList for non-grid fields
            String[] allAccessedFieldArray = (String[]) allAccessedFieldList.toArray(new String[allAccessedFieldList.size()]);
            request.setAttribute("OBRAllAccessedFieldList", StringUtils.arrayToDelimited(allAccessedFieldArray, ",", false, false));

            // generate OBREnforcedResult for non-grid fields
            Map enforcedResultMap = (HashMap) request.getAttribute(Context.OBR_ENFORCED_RESULT_MAP);
            request.setAttribute("OBREnforcedResult", enforcedResultMapToString(enforcedResultMap));

            //generate OBREnforcingUpdateIndicator for non-grid fields
            if (packageMetaData.isNonGridUpdateIndSet())
                request.setAttribute("OBREnforcingUpdateIndicator", packageMetaData.isNonGridUpdateIndSet()?"Y":"");

            // generate gridId List
            String[] gridIdArray = (String[]) gridIdList.toArray(new String[gridIdList.size()]);
            request.setAttribute("OBRGridIdList", StringUtils.arrayToDelimited(gridIdArray, ",", false, false));

            // Check if there are rules for the save event
            request.setAttribute("OBRhasRuleForSave", packageMetaData.hasRuleForSave());

            writeDataBackToDisconnectedResultSet(mapping);

            if ("true".equals(request.getAttribute(IS_SET_PAGE_TO_READ_ONLY_IN_RULE))) {
                // set all page fields to read-only
                OasisFieldsUtility.setFieldsToReadOnly(fields, true);
            }
        }
        c_l.exiting(getClass().getName(), "executeOnPageLoad");
    }


    /**
     * execute rule before save method
     * <P>
     * Note: This method will only execute the processing logic once for each HTTP request
     * </P>
     * @param request
     * @param action
     */
    public void executeBeforeSave(HttpServletRequest request, Action action) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "executeBeforeSave", new Object[]{request, action});
        }
        try {
            Map pageViewCachedData = getPageViewCacheMap();
            // do this for only once for each request
            if (request.getAttribute(IS_EXECUTE_BEFORE_SAVE_DONE) == null) {
                request.setAttribute(IS_EXECUTE_BEFORE_SAVE_DONE, true);

                PageBean bean = (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN);
                KnowledgeBaseManager knowledgeBaseManager = KnowledgeBaseManager.getInstance();
                if (bean != null && knowledgeBaseManager.hasKnowledgeBase(bean.getId())) {
                    BaseAction baseAction = (BaseAction) action;
                    boolean useMapWithoutPrefixes = baseAction.getUseMapWithoutPrefixes();
                    Record inputRecord = baseAction.getInputRecord(request);

                    // get package meta mata
                    PackageMetaData packageMetaData = knowledgeBaseManager.getPackageMetaData(bean.getId());
                    if (packageMetaData.hasRuleForSave()) {
                        // create context
                        Context context = new Context();
                        context.setPageBean(bean);
                        context.setRequest(request);
                        context.setSession(request.getSession());

                        List<String> originalFieldList = packageMetaData.getOriginalFieldList();
                        List factDataList = new ArrayList();
                        //non-grid fields
                        Record nonGridFieldRecord = new Record();
                        Record cachedRecord = c_pageRecordMap.get(action.getClass().getName() + RequestIds.NON_GRID_FIELDS_RECORD);
                        if (cachedRecord != null) {
                            nonGridFieldRecord.setFields(cachedRecord);
                        }
                        // set update indicator
                        if ("true".equals(request.getParameter(IS_NONGRID_FIELD_CHANGED))) {
                            nonGridFieldRecord.setUpdateIndicator(UpdateIndicator.UPDATED);
                        }
                        processFieldValue(nonGridFieldRecord, inputRecord);
                        nonGridFieldRecord.setType(Record.TYPE_NONGRID);
                        if (packageMetaData.isAccessOriginal()) {
                            // Check whether the original fields are from nonGridFieldRecord. If so, perform a match on OriginalRecord
                            Record referencedOriginalFields = getReferencedOriginalFields(nonGridFieldRecord, originalFieldList);
                            if (referencedOriginalFields.getFieldCount() > 0) {
                                addOriginalFieldsToRecord(nonGridFieldRecord, RequestIds.NON_GRID_FIELDS_RECORD, pageViewCachedData);
                            }
                        }
                        factDataList.add(nonGridFieldRecord);

                        //header fields
                        Record headerFieldRecord = null;
                        cachedRecord = c_pageRecordMap.get(action.getClass().getName() + RequestIds.HEADER_FIELDS_RECORD);
                        if (cachedRecord != null) {
                            headerFieldRecord = new Record();
                            headerFieldRecord.setFields(cachedRecord);
                            processFieldValue(headerFieldRecord, inputRecord);
                            headerFieldRecord.setType(Record.TYPE_HEADER);
                            if (packageMetaData.isAccessOriginal()) {
                                // Check whether the original fields are from headerFieldRecord. If so, perform a match on OriginalRecord
                                Record referencedOriginalFields = getReferencedOriginalFields(headerFieldRecord, originalFieldList);
                                if (referencedOriginalFields.getFieldCount() > 0) {
                                    addOriginalFieldsToRecord(headerFieldRecord, RequestIds.HEADER_FIELDS_RECORD, pageViewCachedData);
                                }
                            }
                            factDataList.add(headerFieldRecord);
                        }
                        // RecordSet in request
                        boolean isFirstTimeInLoop = false;
                        List<RecordSet> list = getRecordSetFromRequest(request, baseAction);
                        for (RecordSet recordSet : list) {
                            isFirstTimeInLoop = true;
                            Record referencedOriginalFields = new Record();
                            for (Record record : recordSet.getRecordList()) {
                                record.setType(Record.TYPE_GRID);
                                ((DelegateMap) record.getFieldMap()).setTryFieldIdWithoutPrefix(useMapWithoutPrefixes);
                                // Check whether the original fields are from the grid record.
                                // If so, perform a match on OriginalRecord with respective grid record.
                                if (packageMetaData.isAccessOriginal() && isFirstTimeInLoop) {
                                    referencedOriginalFields = getReferencedOriginalFields(record, originalFieldList);
                                    isFirstTimeInLoop = false;
                                }
                                if (referencedOriginalFields.getFieldCount() > 0) {
                                    addOriginalFieldsToRecord(record, pageViewCachedData);
                                }
                                factDataList.add(record);
                            }
                        }
                        // fire rule
                        fireRules(context, factDataList.toArray(), new RuleEvent[]{new OnSaveEvent()}, ProcessingEvents.OnSave);

                        // set field value changed in NonGrid record back to inputRecord
                        if (nonGridFieldRecord.getChangedFieldsInRule().size() > 0) {
                            Record changedFields = getRecordForChangedField(nonGridFieldRecord);
                            inputRecord.setFields(changedFields);
                        }
                    }
                }
            }
        } catch (Exception e) {
            c_l.throwing(getClass().getName(), "executeBeforeSave", e);
            throw new AppException("Fail to execute rule before save action." + e.toString());
        }
        c_l.exiting(getClass().getName(), "executeBeforeSave");
    }

    /**
     * execute rule for change event
     *
     * @param request
     * @param action
     */
    public void executeOnChange(HttpServletRequest request, Action action) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "executeOnChange", new Object[]{request, action});
        }
        try {
            Map pageViewCachedData = getPageViewCacheMap();
            PageBean bean = (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN);
            KnowledgeBaseManager knowledgeBaseManager = KnowledgeBaseManager.getInstance();
            if (bean != null && knowledgeBaseManager.hasKnowledgeBase(bean.getId())) {
                BaseAction baseAction = (BaseAction) action;
                boolean useMapWithoutPrefixes = baseAction.getUseMapWithoutPrefixes();
                Record inputRecord = baseAction.getInputRecord(request);

                // get package meta mata
                PackageMetaData packageMetaData = knowledgeBaseManager.getPackageMetaData(bean.getId());

                // create context
                Context context = new Context();
                context.setPageBean(bean);
                context.setRequest(request);
                context.setSession(request.getSession());

                List factDataList = new ArrayList();
                //header fields
                Record headerFieldRecord = null;
                Record cachedRecord = c_pageRecordMap.get(action.getClass().getName() + RequestIds.HEADER_FIELDS_RECORD);
                if (cachedRecord != null) {
                    headerFieldRecord = new Record();
                    // use original header
                    Record originalRecord = getOriginalHeaderRecord(pageViewCachedData);
                    if (originalRecord != null) {
                        headerFieldRecord.setFields(originalRecord);
                    }
                    headerFieldRecord.setType(Record.TYPE_HEADER);
                    factDataList.add(headerFieldRecord);
                    // get original data
                    if (packageMetaData.isAccessOriginal()) {
                        addOriginalFieldsToRecord(headerFieldRecord, RequestIds.HEADER_FIELDS_RECORD, pageViewCachedData);
                    }
                }

                // RecordSet in request
                List<RecordSet> list = getRecordSetFromRequest(request, baseAction);
                Record nonGridFieldRecord = null;
                if (list.size() > 0) {
                    // fire rule for header record and grid records
                    // only support one grid
                    RecordSet recordSet = list.get(0);
                    for (Record record : recordSet.getRecordList()) {
                        record.setType(Record.TYPE_GRID);
                        ((DelegateMap) record.getFieldMap()).setTryFieldIdWithoutPrefix(useMapWithoutPrefixes);
                        factDataList.add(record);
                        if (packageMetaData.isAccessOriginal()) {
                            addOriginalFieldsToRecord(record, pageViewCachedData);
                        }
                    }
                } else {
                    // fire rule for header record and non-grid record
                    nonGridFieldRecord = new Record();
                    cachedRecord = c_pageRecordMap.get(action.getClass().getName() + RequestIds.NON_GRID_FIELDS_RECORD);
                    if (cachedRecord != null) {
                        nonGridFieldRecord.setFields(cachedRecord);
                    }
                    // set update indicator
                    if ("true".equals(request.getParameter(IS_NONGRID_FIELD_CHANGED))) {
                        nonGridFieldRecord.setUpdateIndicator(UpdateIndicator.UPDATED);
                    }
                    processFieldValue(nonGridFieldRecord, inputRecord);
                    nonGridFieldRecord.setType(Record.TYPE_NONGRID);
                    factDataList.add(nonGridFieldRecord);
                    if (packageMetaData.isAccessOriginal()) {
                        addOriginalFieldsToRecord(nonGridFieldRecord, RequestIds.NON_GRID_FIELDS_RECORD, pageViewCachedData);
                    }

                }

                // fire rule
                fireRules(context, factDataList.toArray(), new RuleEvent[]{new OnLoadAddOrChangeEvent()}, ProcessingEvents.OnChange);

                //write the data back to 
                if (list.size() > 0) {
                    request.setAttribute("OBREnforcedRecordSet", list.get(0));
                } else {
                    // generate OBREnforcedResult for non-grid fields
                    Map enforcedResultMap = (HashMap) request.getAttribute(Context.OBR_ENFORCED_RESULT_MAP);
                    RequestStorageManager.getInstance().set("OBREnforcedResult", enforcedResultMapToString(enforcedResultMap));
                    // for changed value
                    Record changedFieldRecord = getRecordForChangedField(nonGridFieldRecord);
                    RequestStorageManager.getInstance().set("OBRChangedFieldRecord", changedFieldRecord);
                }
            }
        } catch (Exception e) {
            c_l.throwing(getClass().getName(), "executeOnChange", e);
            throw new AppException("Fail to execute rule for change event." + e.toString());
        }
        c_l.exiting(getClass().getName(), "executeOnChange");
    }


    /**
     * fire rules
     *
     * @param factData
     * @param ruleEvents
     * @param processingEvents
     */
    public void fireRules(Context context, Object[] factData, RuleEvent[] ruleEvents, ProcessingEvents processingEvents) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "fireRules", new Object[]{context, factData, ruleEvents, processingEvents});
        }

        KnowledgeBaseManager knowledgeBaseManager = KnowledgeBaseManager.getInstance();
        String pageCode = context.getPageBean().getId();
        if (knowledgeBaseManager.hasKnowledgeBase(pageCode)) {
            for (Object object : factData) {
                if (object instanceof Record) {
                    ((Record) object).setUseForRule(true);
                }
            }
            knowledgeBaseManager.fireRules(pageCode, context, factData, ruleEvents, processingEvents);
            for (Object object : factData) {
                if (object instanceof Record) {
                    ((Record) object).setUseForRule(false);
                }
            }
        }
        c_l.exiting(getClass().getName(), "fireRules");
    }

    /**
     * get field value from request attribute
     *
     * @param request
     * @param record
     * @return
     */
    private void processFieldValue(HttpServletRequest request, Record record, Map maskedFields) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "processFieldValue", new Object[]{request, record});
        }
        for (Object object : record.getFieldNameList()) {
            String fieldName = (String) object;
            Object value = null;
            value = request.getAttribute(fieldName);
            if (value instanceof DynaBean) {
                value = ((DynaBean) value).get(fieldName);
                value = (value == null ? "" : value);

                if (maskedFields.containsKey(fieldName.toLowerCase()) && !StringUtils.isBlank(String.valueOf(value))){
                    value = ActionHelper.decodeField(value);
                }
            }
            record.setFieldValue(fieldName, value);
        }
        c_l.exiting(getClass().getName(), "processFieldValue");
    }

    /**
     * get field value from inputRecord
     * @param record
     * @param inputRecord
     */
    private void processFieldValue(Record record, Record inputRecord) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "processFieldValue", new Object[]{record, inputRecord});
        }
        for (Object object : record.getFieldNameList()) {
            String fieldName = (String) object;
            if (inputRecord.hasField(fieldName)) {
                record.setFieldValue(fieldName, inputRecord.getFieldValue(fieldName));
            }
        }
        c_l.exiting(getClass().getName(), "processFieldValue");
    }

    /**
     * get record set from request
     * @param request
     * @param baseAction
     * @return
     */
    private List<RecordSet> getRecordSetFromRequest(HttpServletRequest request, BaseAction baseAction) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "getRecordSetFromRequest", new Object[]{request, baseAction});
        }
        List<RecordSet> list = new ArrayList<RecordSet>();
        Enumeration en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();
            if (paramName.endsWith(RequestIds.TEXT_XML)) {
                String paramNameResult = request.getParameter(paramName);
                if (StringUtils.isBlank(paramNameResult)) {
                    continue;
                }
                String gridId = paramName.substring(0, paramName.length() - 6);
                RecordSet recordset = null;
                if (StringUtils.isBlank(gridId)) {
                    if (StringUtils.isBlank(request.getParameter(RequestIds.TEXT_XML))) {
                        continue;
                    }
                    // set anchor column name for DisconnectedResultSet
                    if (c_pageAnchorColumnNameMap.containsKey(baseAction.getClass().getName()) && baseAction.getAnchorColumnName() == null) {
                        baseAction.setAnchorColumnName(c_pageAnchorColumnNameMap.get(baseAction.getClass().getName()));
                    }
                    recordset = baseAction.getInputRecordSet(request);
                } else {
                    recordset = baseAction.getInputRecordSet(request, gridId);
                }
                list.add(recordset);
            }
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(getClass().getName(), "getRecordSetFromRequest", list);
        }
        return list;
    }

    /**
     * Extract grid fields from fieldList. Remove grid fields from fieldList
     * @param fieldList
     * @param uppercaseFieldNameSet
     * @param useMapWithoutPrefixes
     * @param forceNonGridFields
     * @return
     */
    private String extractGridFields(List<String> fieldList, Set uppercaseFieldNameSet, boolean useMapWithoutPrefixes, String forceNonGridFields) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "extractGridFields", new Object[]{fieldList, uppercaseFieldNameSet,
                    useMapWithoutPrefixes});
        }
        List<String> gridFieldList = new ArrayList<String>();
        for (String fieldName : fieldList) {
            if (!StringUtils.isBlank(forceNonGridFields)) {
                // there is override for determining the NonGrid fields.
                if (("," + forceNonGridFields + ",").indexOf("," + fieldName + ",") >= 0) {
                    continue;
                }
            }
            if (isFieldNameInSet(fieldName, uppercaseFieldNameSet, useMapWithoutPrefixes)) {
                gridFieldList.add(fieldName);
            }
        }
        // remove grid fields from field list
        fieldList.removeAll(gridFieldList);
        String[] fieldArray = gridFieldList.toArray(new String[gridFieldList.size()]);
        String gridFieldsString = StringUtils.arrayToDelimited(fieldArray, ",", false, false);
        c_l.exiting(getClass().getName(), "extractGridFields", gridFieldsString);
        return gridFieldsString;
    }

    /**
     * check whether fieldName exists in set,
     * @param fieldName
     * @param uppercaseFieldNameSet
     * @param useMapWithoutPrefixes
     * @return
     */
    public boolean isFieldNameInSet(String fieldName, Set uppercaseFieldNameSet, boolean useMapWithoutPrefixes) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "isFieldNameInSet", new Object[]{fieldName, useMapWithoutPrefixes});
        }
        boolean hasFieldName = false;
        fieldName = fieldName.toUpperCase();
        if (uppercaseFieldNameSet.contains(fieldName)) {
            hasFieldName = true;
        } else {
            if (useMapWithoutPrefixes && fieldName.indexOf("_") > 0) {
                // try fieldName without prefix
                fieldName = fieldName.substring(fieldName.indexOf("_") + 1);
                if (uppercaseFieldNameSet.contains(fieldName)) {
                    hasFieldName = true;
                }
            }
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(getClass().getName(), "isFieldNameInSet", hasFieldName);
        }
        return hasFieldName;
    }

    /**
     * convert DisconnectedResultSet to RecordSet and add it to gird field records
     * @param request
     * @param className
     */
    public Map<DisconnectedResultSet, RecordSet> generateDisconnectedResultSetMap(HttpServletRequest request, String className) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "generateDisconnectedResultSetMap", new Object[]{request, className});
        }
        Map<String, DisconnectedResultSet> map = new HashMap<String, DisconnectedResultSet>();
        Enumeration enumeration = request.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            Object object = request.getAttribute(key);
            if (object instanceof DisconnectedResultSet && !map.containsValue(object)) {
                map.put(key, (DisconnectedResultSet) object);
            }
        }
        Map<DisconnectedResultSet, RecordSet> disconnectedResultSetMap = new HashMap<DisconnectedResultSet, RecordSet>();
        for (String key : map.keySet()) {
            DisconnectedResultSet resultSet = map.get(key);
            RecordSet recordSet = convertDisconnectedResultSetToRecordSet(resultSet);
            if (recordSet.getSize() != 0 )   {
                ActionHelper.addGridFieldRecords(request, key, recordSet);
                disconnectedResultSetMap.put(resultSet, recordSet);
                // record anchor column name
                c_pageAnchorColumnNameMap.put(className, resultSet.getColumnName(1));
            }
        }
        if (c_l.isLoggable(Level.FINER))
            c_l.exiting(getClass().getName(), "generateDisconnectedResultSetMap", disconnectedResultSetMap);
        return disconnectedResultSetMap;
    }

    /**
     * set OBR information from RecordSet back to DisconnectedResultSet
     * @param mapping
     */
    public void writeDataBackToDisconnectedResultSet(Map<DisconnectedResultSet, RecordSet> mapping) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "writeDataBackToDisconnectedResultSet", new Object[]{mapping});
        }
        for (DisconnectedResultSet resultSet : mapping.keySet()) {
            RecordSet recordSet = mapping.get(resultSet);
            resultSet.setOBRAllAccessedFieldList(recordSet.getOBRAllAccessedFieldList());
            resultSet.setOBRConsequenceFieldList(recordSet.getOBRConsequenceFieldList());
            resultSet.setOBREnforcingFieldList(recordSet.getOBREnforcingFieldList());
            resultSet.setOBREnforcingUpdateIndicator(recordSet.getOBREnforcingGridUpdateIndicator());
            int position = 0;
            int recordSetSize = recordSet.getSize();
            resultSet.beforeFirst();
            while (resultSet.next()) {
                if (position > recordSetSize - 1){
                    int currentBeforeDelete = resultSet.getCurrent();
                    resultSet.deleteRow();
                    int currentAfterDelete = resultSet.getCurrent();
                    if (currentBeforeDelete != currentAfterDelete) {
                        // delete the last one
                        break;
                    } else {
                        resultSet.previous();
                        continue;
                    }
                } else {
                    Record record = recordSet.getRecord(position);
                    resultSet.setOBREnforcedResult(record.getOBREnforcedResult());
                    resultSet.setUpdateInd(record.getUpdateIndicator().charAt(0));
                    resultSet.setEditInd(record.getEditIndicator().charAt(0));
                    resultSet.setDisplayInd(record.getDisplayIndicator().charAt(0));
                    for (int i = 1; i <= resultSet.getColumnCount(); i++) {
                        String columnName = resultSet.getColumnName(i);
                        Object value = record.getFieldValue(columnName);
                        if (value instanceof Date) {
                            resultSet.setDate(i, (Date) value);
                        } else {
                            resultSet.setString(i, record.getStringValue(columnName));
                        }
                    }
                }
                position++;
            }
            resultSet.first();
        }
        c_l.exiting(getClass().getName(), "writeDataBackToDisconnectedResultSet");
    }

    /**
     * remove grid related field in non-grid Record
     * @param nonGridFieldRecord
     */
    public void processNonGridRecordForOldCode(HttpServletRequest request, Record nonGridFieldRecord, String className) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "processNonGridRecordForOldCode", new Object[]{request, nonGridFieldRecord, className});
        }
        // remove header fields defined in xml header file
        RequestStorageManager requestStorageManager = RequestStorageManager.getInstance();
        if (requestStorageManager.has(XML_GRID_HEADER_FIELD_ID_SET)) {
            Set fieldIdSet = (Set) requestStorageManager.get(XML_GRID_HEADER_FIELD_ID_SET);
            for (Object object : fieldIdSet) {
                String headerFieldId = (String) object;
                if (nonGridFieldRecord.hasField(headerFieldId)) {
                    nonGridFieldRecord.remove(headerFieldId);
                }
            }
        }
        c_l.exiting(getClass().getName(), "processNonGridRecordForOldCode");
    }

    /**
     * convert enforced map to string
     *
     * @param enforcedResultMap
     * @return
     */
    public static String enforcedResultMapToString(Map enforcedResultMap) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(Record.class.getName(), "enforcedResultMapToString", new Object[]{enforcedResultMap});
        }
        String result = "";
        if (enforcedResultMap != null) {
            String sep = "";
            StringBuffer enforcedResultBuffer = new StringBuffer();
            for (Object fieldId : enforcedResultMap.keySet()) {
                enforcedResultBuffer.append(sep);
                enforcedResultBuffer.append(fieldId).append(OBR_RESULT_EQUAL);
                String sep1 = "";
                for (Object expression : (List) enforcedResultMap.get(fieldId)) {
                    enforcedResultBuffer.append(sep1);
                    enforcedResultBuffer.append(expression);
                    sep1 = SEPARATOR_FOR_ATTRIBUTE;
                }
                sep = SEPARATOR_FOR_FIELD;
            }
            result = enforcedResultBuffer.toString();
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(Record.class.getName(), "enforcedResultMapToString", result);
        }
        return result;
    }

    /**
     * validate rule at runtime
     *
     * @param pageCode
     * @param packageMetaData
     * @param nonGridFieldRecord
     * @param recordSetMap
     */
    protected void validatePackageAtRuntime(String pageCode, PackageMetaData packageMetaData, Record headerFieldRecord,
                                            Record nonGridFieldRecord, HashMap recordSetMap, boolean useMapWithoutPrefixes) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "validatePackageAtRuntime", new Object[]{pageCode,
                    packageMetaData, headerFieldRecord, nonGridFieldRecord, recordSetMap});
        }

        Map<String, RuleMetaData> map = packageMetaData.getRuleMetaDataMap();
        for (String ruleId : map.keySet()) {
            RuleMetaData ruleMetaData = map.get(ruleId);
            String messageHeader = new StringBuffer().append("Page [").append(pageCode).append("], Rule [").
                    append(ruleId).append("], ").toString();
            if (ruleMetaData.isOnLoadAddOrChangeEvent()) {
                if (ruleMetaData.getHeaderRecordCount() == 1) {
                    // rule for header fields
                    if (headerFieldRecord == null) {
                        c_l.warning(messageHeader + "Header record doesn't exist.");
                    } else {
                        for (String fieldId : ruleMetaData.getHeaderEnforcingFieldSet()) {
                            if (!headerFieldRecord.hasField(fieldId)) {
                                String message = new StringBuffer(messageHeader).append("fieldId [").append(fieldId)
                                        .append("] doesn't exist in Header record.").toString();
                                c_l.warning(message);
                            }
                        }
                    }
                }
                if (ruleMetaData.getNonGridRecordCount() == 1) {
                    // rule for non grid
                    for (String fieldId : ruleMetaData.getNonGridEnforcingFieldSet()) {
                        if (!nonGridFieldRecord.hasField(fieldId)) {
                            String message = new StringBuffer(messageHeader).append("fieldId [").append(fieldId)
                                    .append("] doesn't exist in NonGrid record.").toString();
                            c_l.warning(message);
                        }
                    }
                } else if (ruleMetaData.getGridRecordCount() == 1) {
                    // rule for grid
                    if (recordSetMap != null) {
                        Set foundGrid = new HashSet();
                        StringBuffer warningMessage = new StringBuffer(messageHeader).append("Fields are from different grid. ");
                        for (String fieldId : ruleMetaData.getGridEnforcingFieldSet()) {
                            boolean found = false;
                            Iterator it = recordSetMap.keySet().iterator();
                            while (it.hasNext()) {
                                String gridId = (String) it.next();
                                RecordSet recordSet = (RecordSet) recordSetMap.get(gridId);
                                if (isFieldNameInSet(fieldId, recordSet.getUpperCaseFieldNameSet(true), useMapWithoutPrefixes)) {
                                     found = true;
                                    if (!foundGrid.contains(gridId)) {
                                        foundGrid.add(gridId);
                                        warningMessage.append("Field [").append(fieldId).append("] is from grid [").append(gridId).append("]. ");
                                    }
                                }
                            }
                            if (!found) {
                                String message = new StringBuffer(messageHeader).append("fieldId [").append(fieldId)
                                        .append("] doesn't exist in Grid record.").toString();
                                c_l.warning(message);
                            }
                        }
                        if (foundGrid.size() > 1) {
                            // fields are from different grid
                            c_l.warning(warningMessage.toString());
                        }
                    }
                }
            }
        }
    }

    /**
     * get record for changed field in rule.
     * @param record
     * @return
     */
    protected Record getRecordForChangedField(Record record) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "getRecordForChangedField", new Object[]{record});
        }
        Record changedFieldRecord = new Record();
        for (String fieldId : record.getChangedFieldsInRule()) {
            changedFieldRecord.setFieldValue(fieldId, record.getFieldValue(fieldId));
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(getClass().getName(), "getRecordForChangedField", changedFieldRecord);
        }
        return changedFieldRecord;
    }

    /**
     * Extract header fields from fieldList. The header fields will be removed from fieldList
     *
     * Field is a Header field if it's defined as a Header field and it's not defined as a field on this page
     *
     * @param fieldList
     * @param headerFieldRecord
     * @param fields
     * @return
     */
    protected List<String> extractHeaderFields(List<String> fieldList, Record headerFieldRecord, OasisFields fields) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "extractHeaderFields", new Object[]{fieldList, headerFieldRecord, fields});
        }
        List<String> headerFieldList = new ArrayList();
        if (headerFieldRecord != null) {
            for (String fieldId : fieldList) {
                if (headerFieldRecord.hasField(fieldId) && !fields.hasField(fieldId)) {
                    headerFieldList.add(fieldId);
                }
            }
        }
        // remove it from fieldList
        fieldList.removeAll(headerFieldList);
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(getClass().getName(), "extractHeaderFields", headerFieldList);
        }
        return headerFieldList;
    }

    /**
     * get page view cache map
     * @return
     */
    public Map getPageViewCacheMap() {
        c_l.entering(getClass().getName(), "getPageViewCacheMap");
        RequestStorageManager requestStorageManager = RequestStorageManager.getInstance();
        PageViewStateAdmin pageViewStateAdmin = (PageViewStateAdmin) UserSessionManager.getInstance().getUserSession();
        String key = (String) requestStorageManager.get(RequestIds.CACHE_ID_FOR_PAGE_VIEW);
        if (c_l.isLoggable(Level.FINE)) {
          c_l.logp(Level.FINE, getClass().getName(), "getPageViewCacheMap", "Retrieved Page View Cache Id:" + key);
        }
        Map pageViewCachedData = pageViewStateAdmin.getPageViewData(key);
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(getClass().getName(), "getPageViewCacheMap", pageViewCachedData);
        }
        return pageViewCachedData;
    }

    /**
     * get original header record
     *
     * @return
     */
    private Record getOriginalHeaderRecord(Map pageViewCachedData) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "getOriginalHeaderRecord", new Object[]{pageViewCachedData});
        }
        Record originalRecord = null;
        if (pageViewCachedData.containsKey(RequestIds.HEADER_FIELDS_RECORD)) {
            originalRecord = (Record) pageViewCachedData.get(RequestIds.HEADER_FIELDS_RECORD);
        } else {
            c_l.warning("Original header record not found");
        }
        if (c_l.isLoggable(Level.FINER))
            c_l.exiting(getClass().getName(), "getOriginalHeaderRecord", originalRecord);
        return originalRecord;
    }

    /**
     * match original record
     * @param record
     * @param key
     */
    private void addOriginalFieldsToRecord(Record record, String key, Map pageViewCachedData) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "addOriginalFieldsToRecord", new Object[]{record, key, pageViewCachedData});
        }
        if (pageViewCachedData.containsKey(key)) {
            Record originalRecord = (Record) pageViewCachedData.get(key);
            record.setOriginalFieldMap(originalRecord.getFieldMap());
        } else {
            c_l.warning("Original record not found");
        }
        c_l.exiting(getClass().getName(), "addOriginalFieldsToRecord");
    }


    /**
     * match the cached original record
     * @param record
     */
    private void addOriginalFieldsToRecord(Record record, Map pageViewCachedData) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "addOriginalFieldsToRecord", new Object[]{record, pageViewCachedData});
        }
        if (pageViewCachedData.containsKey(RequestIds.RECORDSET_MAP)) {
            HashMap<String, RecordSet> recordSetMap = (HashMap) pageViewCachedData.get(RequestIds.RECORDSET_MAP);
            boolean found = false;
            for (RecordSet rs : recordSetMap.values()) {
              found = addOriginalFieldMap (record, rs);
              if (found) {
                  break;
              }
            }
        } else {
            c_l.warning("original grid record list not found");
        }
        c_l.exiting(getClass().getName(), "addOriginalFieldsToRecord");
    }

    /**
     * Adds original record field map into record.
     * @param record
     * @param originalRecordSet
     */
    private boolean addOriginalFieldMap (Record record, RecordSet originalRecordSet) {
        boolean isAdded=false;
        RecordSet rs = originalRecordSet;
        for (Record r : rs.getRecordList()) {
            if (record.getRowId().equals(r.getRowId())) {
                record.setOriginalFieldMap(r.getFieldMap());
                isAdded = true;
                break;
            }
        }
        return isAdded;
    }


    private boolean cacheReferencedOriginalFields(String recordType, Record record, String pageViewStateId, Map pageViewCachedData, List<String> originalFieldList) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "cacheReferencedOriginalFields", new Object[]{recordType, record, pageViewStateId, pageViewCachedData, originalFieldList});
        }
        boolean hasOriginalFieldReference = false;
        // If the initial request raises an validation exception, use the original field/recordset information
        // from the request storage manager and cache that.
        if (RequestStorageManager.getInstance().has(RequestIds.CACHE_ID_FOR_PAGE_VIEW_DATA)) {
            if (((Map) RequestStorageManager.getInstance().get(RequestIds.CACHE_ID_FOR_PAGE_VIEW_DATA)).get(recordType) != null) {
                c_l.logp(Level.FINER, getClass().getName(), "cachedReferencedOriginalFields", "Retrieving Cache for [" +  recordType + "] from RequestStorageManager and cached into page view state [" + pageViewStateId + "]");
                pageViewCachedData.put(recordType, ((Map) RequestStorageManager.getInstance().get(RequestIds.CACHE_ID_FOR_PAGE_VIEW_DATA)).get(recordType));
                hasOriginalFieldReference = true;
            }
        } else {
            if (!pageViewCachedData.containsKey(recordType)) {

                Record originalFieldsToCache = getReferencedOriginalFields (record, originalFieldList) ;

                if (originalFieldsToCache.getFieldCount() > 0) {
                    hasOriginalFieldReference = true;
                    pageViewCachedData.put(recordType, originalFieldsToCache) ;
                    if (c_l.isLoggable(Level.FINEST)) {
                        c_l.logp(Level.FINEST, getClass().getName(), "cacheReferencedOriginalFields", "Cached " + recordType + " (only referenced original fields) is :" + originalFieldsToCache.toString() + " for page view state id:" + pageViewStateId);
                    } else {
                        c_l.logp(Level.FINE, getClass().getName(), "cacheReferencedOriginalFields", "Cached " + recordType + " (only referenced original fields) for page view state id:" + pageViewStateId);
                    }
                }
            }
        }
        c_l.exiting(getClass().getName(), "cacheReferencedOriginalFields", hasOriginalFieldReference);
        return hasOriginalFieldReference;
    }

    private Record getReferencedOriginalFields(Record sourceRecordForSearch, List<String> originalFieldList) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "getReferencedOriginalFields", new Object[]{sourceRecordForSearch, originalFieldList});
        }
        Record referencedOriginalFields = new Record();
        Iterator it = originalFieldList.iterator();
        while (it.hasNext()) {
            String fieldId = (String) it.next();
            if (sourceRecordForSearch.hasField(fieldId)) {
                 referencedOriginalFields.setField(fieldId, sourceRecordForSearch.getField(fieldId));
            }
        }
        if (c_l.isLoggable(Level.FINER))
            c_l.exiting(getClass().getName(), "getReferencedOriginalFields", referencedOriginalFields);
        return referencedOriginalFields;
    }

    private static final String XML_GRID_HEADER_FIELD_ID_SET = "XML_GRID_HEADER_FIELD_ID_SET";

    private static final Logger c_l = LogUtils.getLogger(RequestHelper.class);
}
