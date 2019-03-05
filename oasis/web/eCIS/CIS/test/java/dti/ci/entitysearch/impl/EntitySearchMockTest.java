package dti.ci.entitysearch.impl;

import dti.ci.entitysearch.dao.EntitySearchDAO;
import dti.cs.securitymgr.ClaimSecurityManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.test.annotations.OasisParameterizedTest;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.test.junit5.tag.TestTags;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/14/2018
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
@Tag(TestTags.MOCK_VALUE)
public class EntitySearchMockTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @Spy
    @InjectMocks
    private EntitySearchManagerImpl spiedEntitySearchManager;

    @Mock
    private MessageManager mockedMessageManager;

    @Mock
    private ClaimSecurityManager mockClaimSecurityManager;

    @Mock
    private EntitySearchDAO mockEntitySearchDAO;

    @Mock
    private SysParmProvider mockSysParmProvider;

    @BeforeEach
    void setupMock() {
        MockitoAnnotations.initMocks(this);
    }

    @OasisParameterizedTest("testSearchEntity_mockTooManyRecords")
    void testSearchEntity_mockTooManyRecords(@OasisTestParameter("maxRow") int maxRow,
                                             @OasisTestParameter("entityDetailMsgKey") String entityDetailMsgKey,
                                             @OasisTestParameter("tooManyDataMsgKey") String tooManyDataMsgKey) {
        // 1. Arrange
        // Create spy on EntitySearchManagerImpl
        doReturn(maxRow).when(spiedEntitySearchManager).getEntitySearchMaxNum();
        doReturn(false).when(spiedEntitySearchManager).shouldSearchSkipAsPerClaimSecurity(any(Record.class));
        doNothing().when(spiedEntitySearchManager).filterResultAsPerClaimSecurity(any(Record.class), any(RecordSet.class));

        // Mock search result.
        RecordSet mockedRs = mock(RecordSet.class);
        when(mockedRs.getSize()).thenReturn(maxRow);
        doReturn(mockedRs).when(spiedEntitySearchManager).retrieveEntityList(any(Record.class));

        // Mock message manager
        doNothing().when(mockedMessageManager).addInfoMessage(anyString(), any(Object[].class));

        // 2. Act
        RecordSet rs = spiedEntitySearchManager.searchEntities(new Record());

        // 3. Assert
        assertEquals(maxRow, rs.getSize());

        verify(mockedMessageManager, times(1)).addInfoMessage(eq(entityDetailMsgKey));
        verify(mockedMessageManager, times(1)).addInfoMessage(eq(tooManyDataMsgKey), any(Object[].class));
    }

    /**
     * 1. Skip search according to claim security
     * 2. Return empty recordSet and add message ci.entity.search.result.noData
     */
    @OasisParameterizedTest
    void testSkipSearchAsPerClaimSecurity(@OasisTestParameter("testSkipSearchAsPerClaimSecurity.sourceTableName") String sourceTableName,
                                          @OasisTestParameter("testSkipSearchAsPerClaimSecurity.searchCriteria") Record inputRecord,
                                          @OasisTestParameter("testSkipSearchAsPerClaimSecurity.expected.resultCount") int resultCount,
                                          @OasisTestParameter("testSearchEntity_message.expected.noDataMsgKey") String noDataMsgKey) {
        //1. Arrange
        // Return true so security filter in Claims will be needed
        doReturn(true).when(mockClaimSecurityManager).isFilterConfigured();

        // Return false so the current claim won't be acceptable according to the claim#
        doReturn(false).when(mockClaimSecurityManager).isAccepted(eq(sourceTableName), anyString());

        //2. Act
        RecordSet rs = spiedEntitySearchManager.searchEntities(inputRecord);

        //3. Assert
        assertEquals(resultCount, rs.getSize());
        verify(mockedMessageManager, times(1)).addInfoMessage(eq(noDataMsgKey));
    }

    /**
     * 1. Filter and remove all the entity recordSet according to claim security
     * 2. Return empty recordSet and add message ci.entity.search.result.noData
     */
    @OasisParameterizedTest
    void testFilterAllResultAsPerClaimSecurity(@OasisTestParameter("testFilterAllResultAsPerClaimSecurity.claimRecord") Record claimRecord,
                                               @OasisTestParameter("testFilterAllResultAsPerClaimSecurity.entityRecord") Record entityRecord,
                                               @OasisTestParameter("testFilterAllResultAsPerClaimSecurity.searchCriteria") Record inputRecord,
                                               @OasisTestParameter("testFilterAllResultAsPerClaimSecurity.expected.resultCount") int resultCount,
                                               @OasisTestParameter("testSearchEntity_message.expected.noDataMsgKey") String noDataMsgKey) {
        //1. Arrange
        // Return true so security filter in Claims will be needed
        doReturn(true).when(mockClaimSecurityManager).isFilterConfigured();

        // Return false so entity search won't be skipped according to claim security
        doReturn(false).when(spiedEntitySearchManager).shouldSearchSkipAsPerClaimSecurity(any(Record.class));

        RecordSet claimRecordSet = new RecordSet();
        claimRecordSet.addRecord(claimRecord);
        // Return one claim that the entity participates
        doReturn(claimRecordSet).when(spiedEntitySearchManager).getEntityClaims(any(Record.class));

        RecordSet entityRecordSet = new RecordSet();
        entityRecordSet.addRecord(entityRecord);
        //Return one entity to be filtered
        doReturn(entityRecordSet).when(spiedEntitySearchManager).retrieveEntityList(any(Record.class));

        //2. Act
        RecordSet rs = spiedEntitySearchManager.searchEntities(inputRecord);

        //3. Assert
        assertEquals(resultCount, rs.getSize());
        verify(mockedMessageManager, times(1)).addInfoMessage(eq(noDataMsgKey));
    }

    /**
     * Test when policy number is included within search
     */
    @OasisParameterizedTest
    void testPolicyNoIsIncludedWithinSearch(@OasisTestParameter("testPolicyNoIsIncludedWithinSearch.searchCriteria") Record inputRecord) {
        //1. Arrange
        //Return policy count as 1, so policy number can be included within search
        doReturn(1).when(mockEntitySearchDAO).getPolicyCnt(any(Record.class));

        //2. Act
        boolean isIncluded = spiedEntitySearchManager.isPolicyNoIncludedWithinSearch(inputRecord);

        //3. Assert
        assertTrue(isIncluded);
    }

    /**
     * Test getting SearchCriteriaRecord and retrieve entity list
     */
    @OasisParameterizedTest
    void testSearchEntity(@OasisTestParameter("testSearchEntity.searchCriteria") Record inputRecord,
                          @OasisTestParameter("testSearchEntity.ciEntlistFldSqlSysParm") String ciEntlistFldSqlSysParm,
                          @OasisTestParameter("testSearchEntity.ciEntlistFldSqlSysParmDefaultVal") String ciEntlistFldSqlSysParmDefaultVal,
                          @OasisTestParameter("testSearchEntity.ciEntlistFldSqlSysParmVal") String ciEntlistFldSqlSysParmVal,
                          @OasisTestParameter("testSearchEntity_message.expected.noDataMsgKey") String noDataMsgKey) {
        //1. Arrange
        //Return false so search won't be skipped
        doReturn(false).when(spiedEntitySearchManager).shouldSearchSkipAsPerClaimSecurity(any(Record.class));

        RecordSet mockedRs = mock(RecordSet.class);
        when(mockedRs.getSize()).thenReturn(0);
        //No entity is found by the search criteria
        doReturn(mockedRs).when(mockEntitySearchDAO).getEntityList(any(Record.class), any(RecordLoadProcessor.class));

        //Return the value of system parameter CI_ENTLIST_FLD_SQL
        doReturn(ciEntlistFldSqlSysParmVal).when(mockSysParmProvider).getSysParm(eq(ciEntlistFldSqlSysParm), eq(ciEntlistFldSqlSysParmDefaultVal));

        //2. Act
        spiedEntitySearchManager.searchEntities(inputRecord);

        //3. Assert
        verify(mockedMessageManager, times(1)).addInfoMessage(eq(noDataMsgKey));
    }

    /**
     * Test global search
     */
    @OasisParameterizedTest
    void testGlobalSearch(@OasisTestParameter("testGlobalSearch.searchCriteria") Record inputRecord,
                          @OasisTestParameter("testSearchEntity_message.expected.entityDetailMsgKey") String entityDetailMsgKey) {
        //1. Arrange
        //Return false so search won't be skipped
        doReturn(false).when(spiedEntitySearchManager).shouldSearchSkipAsPerClaimSecurity(any(Record.class));

        RecordSet mockedRs = mock(RecordSet.class);
        when(mockedRs.getSize()).thenReturn(1);
        //Only one entity is found by the search criteria
        doReturn(mockedRs).when(mockEntitySearchDAO).getEntityList(any(Record.class), any(RecordLoadProcessor.class));

        //2. Act
        spiedEntitySearchManager.searchEntities(inputRecord);

        //3. Assert
        verify(mockedMessageManager, times(0)).addInfoMessage(eq(entityDetailMsgKey));
    }

    /**
     * Test throwing exception when getting max num. The default max num should return.
     */
    @OasisParameterizedTest
    void testThrowExceptionInGetEntitySearchMaxNum(@OasisTestParameter("testThrowExceptionInGetEntitySearchMaxNum.maxNumSysParm") String maxNumSysParm,
                                                   @OasisTestParameter("testThrowExceptionInGetEntitySearchMaxNum.maxNumSysParmDefaultVal") int maxNumSysParmDefaultVal) {
        //1. Arrange
        //Throw exception when getting max num.
        doThrow(RuntimeException.class).when(mockSysParmProvider).getSysParmAsInt(eq(maxNumSysParm), eq(maxNumSysParmDefaultVal));

        //2. Act
        int maxNum = spiedEntitySearchManager.getEntitySearchMaxNum();

        //3. Assert
        assertEquals(maxNumSysParmDefaultVal, maxNum);
    }

    @Test
    void throwExceptionExample() {
        //1. Arrange
        doReturn(null).when(spiedEntitySearchManager).getEntitySearchDAO();

        //2. Act
        ConfigurationException ce = assertThrows(ConfigurationException.class, () -> spiedEntitySearchManager.verifyConfig());

        //3. Assert
        assertTrue(ce.getMessage().matches(".*The required property .* is missing.*"));
    }

    @OasisParameterizedTest("testEntitySelectSearch_mockTooManyRecords")
    void testEntitySelectSearch_mockTooManyRecords(@OasisTestParameter("maxRow") int maxRow,
                                                   @OasisTestParameter("entityNoSelectMsgKey") String entityNoSelectMsgKey,
                                                   @OasisTestParameter("tooManyDataMsgKey") String tooManyDataMsgKey) {
        //1.Arrange
        // Mock search result.
        RecordSet mockedRs = mock(RecordSet.class);
        when(mockedRs.getSize()).thenReturn(maxRow);
        doReturn(mockedRs).when(spiedEntitySearchManager).retrieveEntityList(any(Record.class));

        // Mock message manager
        doNothing().when(mockedMessageManager).addInfoMessage(anyString(), any(Object[].class));

        // 2. Act
        RecordSet rs = spiedEntitySearchManager.searchEntitiesForPopup(new Record());

        // 3. Assert
        assertEquals(maxRow, rs.getSize());

        verify(mockedMessageManager, times(1)).addInfoMessage(eq(entityNoSelectMsgKey));
        verify(mockedMessageManager, times(1)).addInfoMessage(eq(tooManyDataMsgKey), any(Object[].class));
    }

    @OasisParameterizedTest("testEntitySelectSearch_mockNoRecord")
    void testEntitySelectSearch_mockNoRecord(@OasisTestParameter("entityNoRecordMsgKey") String entityNoRecordMsgKey) {
        //1.Arrange
        // Mock search result.
        RecordSet mockedRs = mock(RecordSet.class);
        when(mockedRs.getSize()).thenReturn(0);
        doReturn(mockedRs).when(spiedEntitySearchManager).retrieveEntityList(any(Record.class));

        // Mock message manager
        doNothing().when(mockedMessageManager).addErrorMessage(anyString(), any(Object[].class));

        // 2. Act
        RecordSet rs = spiedEntitySearchManager.searchEntitiesForPopup(new Record());

        // 3. Assert
        assertEquals(0, rs.getSize());

        verify(mockedMessageManager, times(1)).addInfoMessage(eq(entityNoRecordMsgKey));
    }

    @OasisParameterizedTest("testEntitySelectSearch")
    void testEntitySelectSearch(@OasisTestParameter("inputRecord") Record inputRecord, @OasisTestParameter("entityNoSelectMsgKey") String entityNoSelectMsgKey) {
        //1. Arrange
        RecordSet mockedRs1 = mock(RecordSet.class);
        when(mockedRs1.getSize()).thenReturn(2);

        RecordSet mockedRs2 = mock(RecordSet.class);
        when(mockedRs2.getSize()).thenReturn(1);

        //getEntityList will be called twice: 1st return mockedRs1, 2nd return mockedRs2
        doReturn(mockedRs1).doReturn(mockedRs2).when(mockEntitySearchDAO).getEntityList(any(Record.class), any(RecordLoadProcessor.class));

        doReturn("searchCriteria_clientId").when(mockSysParmProvider).getSysParm("CI_ENTITY_PEEK_FLD", "");

        //2. Act
        RecordSet rs = spiedEntitySearchManager.searchEntitiesForPopup(inputRecord);

        //3. Assert
        assertEquals(1, rs.getSize());
        verify(mockedMessageManager, times(0)).addInfoMessage(eq(entityNoSelectMsgKey));
    }
}
