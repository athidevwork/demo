package dti.ci.entitysearch.impl;

import dti.ci.entitymgr.EntityFields;
import dti.ci.entitysearch.EntitySearchFields;
import dti.ci.entitysearch.EntitySearchManager;
import dti.ci.entitysearch.dao.EntitySearchDAO;
import dti.cs.securitymgr.ClaimSecurityManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static dti.ci.entitysearch.EntitySearchFields.*;

/**
 * Helper class for Entity List.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 * Date:   Oct 14, 2003
 * <p/>
 * Revision Date    Revised By  Description
 * -------------------------------------------------------------
 * 03/30/2005       HXY         Removed singleton implementation.
 * 04/20/2005       HXY         Created one instance DAO.
 * 10/16/2009       Jacky       Add 'Jurisdiction' logic for issue #97673
 * 09/18/2013       kshen       Issue 147194.
 * 06/14/2018       dpang       Issue 109216. Refactor "Entity Select Search" popup.
 * -------------------------------------------------------------
 */

public class EntitySearchManagerImpl implements EntitySearchManager {

    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public int getEntitySearchMaxNum() {
        String methodName = "getEntitySearchMaxNum";
        l.entering(this.getClass().getName(), methodName);

        int maxNum = ENTITY_SEARCH_DEFAULT_MAX_NUM;
        try {
            maxNum = getSysParmProvider().getSysParmAsInt(ENTITY_SEARCH_MAX_NUM_SYS_PARM_NAME, ENTITY_SEARCH_DEFAULT_MAX_NUM);
        } catch (Exception e) {
            l.fine("Class " + this.getClass().getName() + ", " + methodName + ":  " + "exception occurred ':  " + e.toString());
            maxNum = ENTITY_SEARCH_DEFAULT_MAX_NUM;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, maxNum);
        }
        return maxNum;

    }

    @Override
    public boolean isPolicyNoIncludedWithinSearch(Record inputRecord) {
        String methodName = "isPolicyNoIncludedWithinSearch";
        if (l.isLoggable(Level.FINER)) {
            l.entering(this.getClass().getName(), methodName, new Object[]{inputRecord});
        }

        boolean isPolicyNoIncluded = false;
        String roleExternalId = inputRecord.getStringValueDefaultEmpty(SEARCH_CRITERIA_ROLE_EXTERNAL_ID);

        if (!StringUtils.isBlank(roleExternalId)) {
            Record searchRecord = new Record();
            EntitySearchFields.setPolicyNo(searchRecord, roleExternalId);
            isPolicyNoIncluded = getEntitySearchDAO().getPolicyCnt(searchRecord) != 0;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, isPolicyNoIncluded);
        }
        return isPolicyNoIncluded;
    }

    @Override
    public RecordSet searchEntities(Record inputRecord) {
        String methodName = "searchEntities";
        if (l.isLoggable(Level.FINER)) {
            l.entering(this.getClass().getName(), methodName, inputRecord);
        }

        RecordSet rs = null;

        if (shouldSearchSkipAsPerClaimSecurity(inputRecord)) {
            rs = new RecordSet();
        } else {
            rs = retrieveEntityList(inputRecord);

            if (rs.getSize() > 0) {
                filterResultAsPerClaimSecurity(inputRecord, rs);
            }
        }

        addMessageForEntitySearch(rs, inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
    }

    @Override
    public RecordSet searchEntitiesForPopup(Record inputRecord) {
        String methodName = "searchEntitiesForPopup";
        if (l.isLoggable(Level.FINER)) {
            l.entering(this.getClass().getName(), methodName, inputRecord);
        }

        RecordSet rs = retrieveEntityList(inputRecord);
        // If the process is peekAtSearchResult, and the returned record set's size is not 1,
        // try to retrieve entity with other search criteria in CI_ENTITY_PEEK_FLD.
        if (rs.getSize() != 1 && isPeekAtSearchResult(inputRecord)) {
            String peekFields = getSysParmProvider().getSysParm("CI_ENTITY_PEEK_FLD", "");

            if (!StringUtils.isBlank(peekFields)) {
                String[] fields = peekFields.split(",");
                for (int i = 0; i < fields.length; i++) {
                    String fieldName = fields[i];
                    if (!StringUtils.isBlank(fieldName)) {
                        if (inputRecord.hasField(fieldName)) {
                            Record searchRecord = new Record();
                            searchRecord.setFields(inputRecord);

                            String lastOrOrgName = (String) inputRecord.getStringValueDefaultEmpty(EntitySearchFields.SEARCH_CRITERIA_LAST_OR_ORG_NAME);

                            searchRecord.setFieldValue(EntitySearchFields.SEARCH_CRITERIA_LAST_OR_ORG_NAME, "");
                            searchRecord.setFieldValue(RequestIds.GLOBAL_SEARCH_ENTITY_FIRSTNAME, "");
                            searchRecord.setFieldValue(fieldName, lastOrOrgName);

                            RecordSet tempRs = retrieveEntityList(searchRecord);
                            if (tempRs.getSize() == 1) {
                                rs = tempRs;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (!isPeekAtSearchResult(inputRecord)) {
            addMessageForEntitySelectSearch(rs);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
    }

    @Override
    public RecordSet getEntityClaims(Record inputRecord) {
        String methodName = "getEntityClaims";
        if (l.isLoggable(Level.FINER)) {
            l.entering(this.getClass().getName(), methodName, inputRecord);
        }

        RecordSet recordSet = getEntitySearchDAO().getEntityClaims(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, recordSet);
        }
        return recordSet;
    }

    protected boolean shouldSearchSkipAsPerClaimSecurity(Record inputRecord) {
        String methodName = "shouldSearchSkipAsPerClaimSecurity";
        if (l.isLoggable(Level.FINER)) {
            l.entering(this.getClass().getName(), methodName, inputRecord);
        }

        //If we need Secure Filter in Claims, for issue 97673(HIROC)
        boolean isNeedFilterResult = getClaimSecurityManager().isFilterConfigured();
        boolean skipSearch = false;
        if (isNeedFilterResult) {
            String sourceNo = inputRecord.getStringValueDefaultEmpty(SEARCH_CRITERIA_ROLE_EXTERNAL_ID);

            if (!StringUtils.isBlank(sourceNo)) {
                //Skip search if the claim is restricted
                if (!getClaimSecurityManager().isAccepted(CLAIM_SOURCE_TBL_NAME, sourceNo)) {
                    skipSearch = true;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, skipSearch);
        }
        return skipSearch;
    }

    protected void filterResultAsPerClaimSecurity(Record inputRecord, RecordSet rs) {
        String methodName = "filterResultAsPerClaimSecurity";
        if (l.isLoggable(Level.FINER)) {
            l.entering(this.getClass().getName(), methodName, new Object[]{inputRecord, rs});
        }

        String roleTypeCode = inputRecord.getStringValueDefaultEmpty(SEARCH_CRITERIA_ROLE_TYPE_CODE); // roleTypeCode
        //Criteria role type is not null
        if (!StringUtils.isBlank(roleTypeCode, true)) {
            //Check if we need Secure Filter in Claims, for issue 97673(HIROC)
            boolean isNeedFilterResult = getClaimSecurityManager().isFilterConfigured();

            if (isNeedFilterResult) {
                l.finer("Entity recordSet size before filtering: " + rs.getSize());
                Iterator recordIt = rs.getRecords();

                while (recordIt.hasNext()) {
                    Record srchCritRecord = new Record();
                    EntityFields.setEntityId(srchCritRecord, EntityFields.getEntityId((Record) recordIt.next()));

                    RecordSet rsClaims = getEntityClaims(srchCritRecord);

                    boolean isRestrictedRole = false;
                    Iterator rsClaimsIt = rsClaims.getRecords();
                    while (rsClaimsIt.hasNext()) {
                        String claimNo = ((Record) rsClaimsIt.next()).getStringValueDefaultEmpty("claimNo"); // claim no

                        if (!getClaimSecurityManager().isAccepted(CLAIM_SOURCE_TBL_NAME, claimNo)) {
                            isRestrictedRole = true;
                            break;
                        }
                    }
                    if (isRestrictedRole) {
                        recordIt.remove();
                    }
                }
            }

            l.finer("Entity recordSet size after filtering: " + rs.getSize());
        }

        l.exiting(getClass().getName(), methodName);
    }

    /**
     * This method getAdditionalSearchCriteria is to load the additional sql (configured as CI_ENTSRCH_ADDL_SQL),
     * merge the searchCriteria_addlField value into the aditional sql if desired
     * <p>
     * addl_sql must be a valid sql snippet, optionally it can contain :searchCriteria_addlField
     * to signify it expects value from searchCriteria_addlField parameter either entered by user,
     * or configured a default value by default, even if the field is invisible.
     * addl_sql can also join 4 pre-defined tables: entity, address, license_profile and vendor
     * <p>
     * 2 examples are given below:
     * and entity.client_id like '%aBcDeF%'
     * and upper(entity.char1) like upper('%:searchCriteria_addlField%') and entity.sys_update_time>sysdate -10
     * <p>
     * to summarize:
     * <p>
     * addlField value exists,     addl_sql not configured => return ""
     * addlField value exists,     addl_sql configured => return merged String
     * addlField value not exist,  addl_sql not configured => return ""
     * addlField value not exist,  addl_sql configured, contains :searchCriteria_addlField => return ""
     * addlField value not exist,  addl_sql configured, does not contains:searchCriteria_addlField => return merged string
     *
     * @param inputRecord record containing all search parameter values
     * @return addlitional sql configured in oasis, the value is merged with search parameter value if required
     */
    private String getAdditionalSearchCriteria(Record inputRecord) {
        String methodName = "getdAdditionalSearchCriteria";
        if (l.isLoggable(Level.FINER)) {
            l.entering(this.getClass().getName(), methodName, new Object[]{inputRecord});
        }

        String addlFieldValue = inputRecord.getStringValueDefaultEmpty(SEARCH_CRITERIA_ADDL_FIELD);
        String additionalSqlConfigured = getSysParmProvider().getSysParm("CI_ENTSRCH_ADDL_SQL", " ");
        String sqlPlaceHolder = ":" + SEARCH_CRITERIA_ADDL_FIELD;

        if (StringUtils.isBlank(additionalSqlConfigured)) {
            return ""; // not configured.
        } else if (additionalSqlConfigured.contains(sqlPlaceHolder) && StringUtils.isBlank(addlFieldValue)) {
            return "";
        } else {
            additionalSqlConfigured = replaceTableNameInAdditionalField(additionalSqlConfigured);
        }

        additionalSqlConfigured = additionalSqlConfigured.replaceAll(sqlPlaceHolder, addlFieldValue);
        l.exiting(getClass().getName(), methodName, additionalSqlConfigured);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(this.getClass().getName(), methodName, additionalSqlConfigured);
        }
        return additionalSqlConfigured;
    }

    /**
     * Add message to display according to record set on Entity Search page.
     *
     * @param rs
     */
    private void addMessageForEntitySearch(RecordSet rs, Record inputRecord) {
        String methodName = "addMessageForEntitySearch";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, rs);
        }

        //If it's globalSearch and only one entity is found, the page will be forwarded to entity modify page and the following messages should not be added.
        if (EntitySearchFields.GLOBAL_SEARCH_PROCESS.equalsIgnoreCase(inputRecord.getStringValueDefaultEmpty(RequestIds.PROCESS))
                && (rs.getSize() == 1)) {
            return;
        }

        if (rs.getSize() == 0) {
            getMessageManager().addInfoMessage("ci.entity.search.result.noData");
        } else {
            getMessageManager().addInfoMessage("ci.entity.search.info.detail");

            int maxEntitySearchNum = getEntitySearchMaxNum();
            if (rs.getSize() >= maxEntitySearchNum) {
                getMessageManager().addInfoMessage("ci.entity.search.result.tooManyData", new Object[]{maxEntitySearchNum});
            }
        }

        l.exiting(this.getClass().getName(), methodName);
    }

    /**
     * Add message to display according to record set on Entity Select Search page.
     *
     * @param rs
     */
    private void addMessageForEntitySelectSearch(RecordSet rs) {
        String methodName = "addMessageForEntitySelectSearch";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, rs);
        }

        if (rs.getSize() == 0) {
            getMessageManager().addInfoMessage("ci.entity.search.result.noData");
        } else {
            getMessageManager().addInfoMessage("ci.entity.search.error.NoSelect");

            int maxEntitySearchNum = getEntitySearchMaxNum();
            if (rs.getSize() >= maxEntitySearchNum) {
                getMessageManager().addInfoMessage("ci.entity.search.result.tooManyData", new Object[]{maxEntitySearchNum});
            }
        }

        l.exiting(this.getClass().getName(), methodName);
    }

    /**
     * Method to get additional field returned from search
     * This additional Field should be configured by system parameter CI_ENTLIST_FLD_SQL.
     * It is the configurator's responsibility to ensure the sql snippet to return 1 single
     * string-presentable data, or the entire SQL will fail.
     * <p>
     * Optionally the sql snippet can join the 4 pre-defined tables in order to return the data dynamically:
     * entity, address, license_profile and vendor
     * <p>
     * 2 configuration examples are given below:
     * entity.char1
     * (SELECT v.vehicle_no FROM vehicle v WHERE v.entity_fk = entity.entity_pk)
     *
     * @return the String presentation of the logic for the additional field
     */
    private String getAdditionalFieldToReturn() {
        String methodName = "getAdditionalFieldToReturn";
        if (l.isLoggable(Level.FINER)) {
            l.entering(this.getClass().getName(), methodName);
        }

        String additionalFieldConfigured = getSysParmProvider().getSysParm("CI_ENTLIST_FLD_SQL", "null");

        if (StringUtils.isBlank(additionalFieldConfigured)) {
            additionalFieldConfigured = "null";
        } else {
            additionalFieldConfigured = replaceTableNameInAdditionalField(additionalFieldConfigured);
        }

        l.exiting(getClass().getName(), methodName, additionalFieldConfigured);
        return additionalFieldConfigured;
    }

    private String replaceTableNameInAdditionalField(String additionalFieldValue) {
        return additionalFieldValue.replaceAll("entity\\.", "ent.")
                .replaceAll("address\\.", "addr.")
                .replaceAll("license_profile\\.", "lic.")
                .replaceAll("vendor\\.", "ven.");
    }

    /**
     * Retrieve entity list.
     *
     * @param inputRecord
     * @return
     */
    protected RecordSet retrieveEntityList(Record inputRecord) {
        String methodName = "retrieveEntityList";
        if (l.isLoggable(Level.FINER)) {
            l.entering(this.getClass().getName(), methodName, inputRecord);
        }

        Record searchCriteriaRecord = getSearchCriteriaRecord(inputRecord);
        RecordSet rs = getEntitySearchDAO().getEntityList(searchCriteriaRecord, AddSelectIndLoadProcessor.getInstance());

        l.exiting(this.getClass().getName(), methodName, rs);
        return rs;
    }

    /**
     * Get search criteria record to retrieve entity list.
     *
     * @param inputRecord
     * @return
     */
    private Record getSearchCriteriaRecord(Record inputRecord) {
        String methodName = "getSearchCriteriaRecord";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, inputRecord);
        }

        Record searchCriteriaRecord = new Record();

        //1. Set common data fields
        List<String> fieldNames = inputRecord.getFieldNameList();

        for (String fieldName : fieldNames) {
            if (fieldName.startsWith(SEARCH_CRITERIA_PREFIX)) {
                searchCriteriaRecord.setFieldValue(EntitySearchFields.removeSearchCriteriaPrefix(fieldName),
                        inputRecord.getStringValueDefaultEmpty(fieldName));
            }
        }

        //2. Set other calculated data fields
        if (StringUtils.isBlank(EntitySearchFields.getIncludedAddlData(inputRecord))) {
            EntitySearchFields.setAddlFieldToReturn(searchCriteriaRecord, getAdditionalFieldToReturn());
        } else {
            EntitySearchFields.setAddlFieldToReturn(searchCriteriaRecord, EntitySearchFields.getIncludedAddlData(inputRecord));
        }

        EntitySearchFields.setEntityPkValue(searchCriteriaRecord, EntitySearchFields.getEntityPkValue(inputRecord));

        EntitySearchFields.setIncludedPolnoForSearch(searchCriteriaRecord, YesNoFlag.getInstance(EntitySearchFields.getIncludedPolicyNo(inputRecord)).getName());

        String process = inputRecord.getStringValueDefaultEmpty(RequestIds.PROCESS);
        EntitySearchFields.setIsGlobalSearch(searchCriteriaRecord, YesNoFlag.getInstance(GLOBAL_SEARCH_PROCESS.equalsIgnoreCase(process)).getName());

        EntitySearchFields.setAdditionalSearchSql(searchCriteriaRecord, getAdditionalSearchCriteria(inputRecord));

        String entityTypeCode = inputRecord.getStringValueDefaultEmpty(SEARCH_CRITERIA_ENTITY_TYPE_CODE);
        EntitySearchFields.setEntityTypeCode(searchCriteriaRecord, StringUtils.isBlank(entityTypeCode, true) ? "" : entityTypeCode);

        String entityClassCode = inputRecord.getStringValueDefaultEmpty(SEARCH_CRITERIA_ENTITY_CLASS_CODE);
        EntitySearchFields.setEntityClassCode(searchCriteriaRecord, StringUtils.isBlank(entityClassCode, true) ? "" : entityClassCode);

        EntitySearchFields.setMaxRow(searchCriteriaRecord, getEntitySearchMaxNum());

        setClientIdField(inputRecord, searchCriteriaRecord);
        setIsFindInvoicerField(inputRecord, searchCriteriaRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, searchCriteriaRecord);
        }
        return searchCriteriaRecord;
    }

    /**
     * Check if need to append client id format prefix.
     *
     * @param inputRecord
     * @param searchCriteriaRecord
     */
    private void setClientIdField(Record inputRecord, Record searchCriteriaRecord) {
        String methodName = "setClientIdField";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord, searchCriteriaRecord});
        }

        String clientIdFmt = getSysParmProvider().getSysParm("CS_CLIENT_ID_FORMAT");
        String clientID = inputRecord.getStringValueDefaultEmpty(SEARCH_CRITERIA_CLIENT_ID);

        if (!StringUtils.isBlank(clientID)) {
            if (!StringUtils.isBlank(clientIdFmt) && CS_CLIENT_ID_FORMAT_VALUE.equals(clientIdFmt)) {
                EntityFields.setClientId(searchCriteriaRecord, CS_CLIENT_ID_FORMAT_PREFIX.concat(clientID.trim()).substring(clientID.trim().length()));
            }
        }

        l.exiting(getClass().getName(), methodName);
    }

    /**
     * Set value for isFindInvoicer. This is used by entity select search popup page.
     *
     * @param inputRecord
     * @param searchCriteriaRecord
     */
    private void setIsFindInvoicerField(Record inputRecord, Record searchCriteriaRecord) {
        String methodName = "setIsFindInvoicerField";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord, searchCriteriaRecord});
        }

        String lastOrOrgName = inputRecord.getStringValueDefaultEmpty(SEARCH_CRITERIA_LAST_OR_ORG_NAME);
        if (!StringUtils.isBlank(lastOrOrgName)) {
            String eventName = inputRecord.getStringValueDefaultEmpty(EVENT_NAME_PROPERTY);
            if ("handleOnFindInvoicer()".equals(eventName)) {
                EntitySearchFields.setIsFindInvoicer(searchCriteriaRecord, "Y");
            }
        }

        l.exiting(getClass().getName(), methodName);
    }

    private boolean isPeekAtSearchResult(Record inputRecord) {
        return "peekAtSearchResult".equals(inputRecord.getStringValueDefaultEmpty("process"));
    }

    public void verifyConfig() {
        if (getEntitySearchDAO() == null) {
            throw new ConfigurationException("The required property 'entitySearchDAO' is missing.");
        }

        if (getClaimSecurityManager() == null) {
            throw new ConfigurationException("The required property 'claimSecurityManager' is missing.");
        }

        if (getMessageManager() == null) {
            throw new ConfigurationException("The required property 'messageManager' is missing.");
        }

        if (getSysParmProvider() == null) {
            throw new ConfigurationException("The required property 'sysParmProvider' is missing.");
        }
    }

    public MessageManager getMessageManager() {
        return m_messageManager;
    }

    public void setMessageManager(MessageManager messageManager) {
        this.m_messageManager = messageManager;
    }

    public EntitySearchDAO getEntitySearchDAO() {
        return m_entitySearchDAO;
    }

    public void setEntitySearchDAO(EntitySearchDAO EntitySearchDAO) {
        this.m_entitySearchDAO = EntitySearchDAO;
    }

    public ClaimSecurityManager getClaimSecurityManager() {
        return m_claimSecurityManager;
    }

    public void setClaimSecurityManager(ClaimSecurityManager claimSecurityManager) {
        this.m_claimSecurityManager = claimSecurityManager;
    }

    public SysParmProvider getSysParmProvider() {
        return m_sysParmProvider;
    }

    public void setSysParmProvider(SysParmProvider sysParmProvider) {
        this.m_sysParmProvider = sysParmProvider;
    }

    private EntitySearchDAO m_entitySearchDAO;
    private ClaimSecurityManager m_claimSecurityManager;
    private MessageManager m_messageManager;
    private SysParmProvider m_sysParmProvider;

    private static final String ENTITY_SEARCH_MAX_NUM_SYS_PARM_NAME = "MAX_ENTITY_SEARCH";
    private static final int ENTITY_SEARCH_DEFAULT_MAX_NUM = 50;
}
