package dti.ci.clientmgr.impl;

import dti.ci.addressmgr.dao.AddressDAO;
import dti.ci.clientmgr.EntityAddInfo;
import dti.ci.entitymgr.dao.EntityDAO;
import dti.cs.activityhistorymgr.ActivityHistoryManager;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.test.annotations.OasisParameterizedTest;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.test.junit5.tag.TestTags;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   5/21/2018
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
@Tag(TestTags.MOCK_VALUE)
public class EntityAddManagerMockTest {

    private final Logger l = LogUtils.getLogger(getClass());

    @Spy
    @InjectMocks
    private EntityAddManagerImpl spiedEntityAddManager;

    @Mock
    private EntityDAO mockedEntityDAO;

    @Mock
    private AddressDAO mockedAddressDAO;

    @Mock
    private ActivityHistoryManager mockedActivityHistoryManager;

    @Mock
    private MessageManager mockedMessageManager;

    @Mock
    private SysParmProvider mockSysParmProvider;

    @BeforeEach
    void setupMock() {
        MockitoAnnotations.initMocks(this);
    }

    @OasisParameterizedTest("testSaveEntity_duplicateEntityFound")
    void testSaveEntity_duplicateEntityFound(@OasisTestParameter("duplicateEntityMsgKey") String duplicateEntityMsgKey,
                                             @OasisTestParameter("saveEntityResultRecord") Record saveEntityResultRecord) {

        // 1. Arrange
        when(mockedEntityDAO.saveEntity(any(Record.class))).thenReturn(saveEntityResultRecord);
        doReturn("Y").when(mockSysParmProvider).getSysParm(eq("CI_WEB_HIDE_TAX_ID"));

        // 2. Act
        spiedEntityAddManager.saveEntity(new Record());

        // 3. Verify
        verify(mockedMessageManager, times(1)).addInfoMessage(eq(duplicateEntityMsgKey), any(String[].class));
        verify(mockedMessageManager, times(1)).formatMessage(eq("ci.entity.message.data.not.saved"));
        verify(mockedMessageManager, times(1)).formatMessage(eq("ci.entity.message.display.duplicates.after.truncate"), any(Integer[].class));
        verify(mockedMessageManager, times(1)).formatMessage(eq("ci.entity.message.one.taxId.duplicate.found"));
    }

    @OasisParameterizedTest("testSaveEntity_entityAddSuccess")
    void testSaveEntity_entityAddSuccess(@OasisTestParameter("duplicateEntityMsgKey") String duplicateEntityMsgKey,
                                         @OasisTestParameter("saveEntityResultRecord") Record saveEntityResultRecord) {
        // 1. Arrange
        when(mockedEntityDAO.saveEntity(any(Record.class))).thenReturn(saveEntityResultRecord);
        doReturn("Y").when(mockSysParmProvider).getSysParm(eq("CI_WEB_HIDE_TAX_ID"));

        // 2. Act
        spiedEntityAddManager.saveEntity(new Record());

        // 3. Verify
        verify(mockedMessageManager, times(0)).addInfoMessage(eq(duplicateEntityMsgKey), any(String[].class));
        verify(spiedEntityAddManager, times(0)).addMessage(any(EntityAddInfo.class));
    }

    @OasisParameterizedTest("testValidateAddrAndSaveEntity_validateAddrFail")
    void testValidateAddrAndSaveEntity_validateAddrFail(@OasisTestParameter("addressRecord") Record addressRecord,
                                                        @OasisTestParameter("countyCodeRecord") Record countyCodeRecord,
                                                        @OasisTestParameter("errorAddressTypePremiseMsgKey") String errorAddressTypePremiseMsgKey) {
        // 1. Arrange
        when(mockedAddressDAO.loadCountyCode(any(Record.class))).thenReturn(countyCodeRecord);

        // 2. Act
        ValidationException ve = assertThrows(ValidationException.class, () -> spiedEntityAddManager.validateAddrAndSaveEntity(addressRecord, true));

        // 3. Verify
        verify(mockedMessageManager, times(1)).addErrorMessage(eq(errorAddressTypePremiseMsgKey));
    }

    @OasisParameterizedTest("testValidateAddrAndSaveEntity_saveSuccess")
    void testValidateAddrAndSaveEntity_saveSuccess(@OasisTestParameter("inputRecord") Record inputRecord,
                                                   @OasisTestParameter("saveEntityResultRecord") Record saveEntityResultRecord,
                                                   @OasisTestParameter("duplicateEntityMsgKey") String duplicateEntityMsgKey) {
        // 1. Arrange
        when(mockedEntityDAO.saveEntity(any(Record.class))).thenReturn(saveEntityResultRecord);
        when(mockedEntityDAO.loadEntityData(any(Record.class))).thenReturn(new Record());
        doNothing().when(spiedEntityAddManager).saveActivityHistForAddEntity(any(Record.class));

        // 2. Act
        spiedEntityAddManager.validateAddrAndSaveEntity(inputRecord, true);

        // 3. Verify
        verify(mockedMessageManager, times(0)).addInfoMessage(eq(duplicateEntityMsgKey));
        verify(spiedEntityAddManager, times(1)).saveActivityHistForAddEntity(any(Record.class));
    }

    /**
     * This will be executed twice for person and org separately.
     *
     * @param inputRecord
     */
    @OasisParameterizedTest("testSaveActivityHistForAddEntity_personAndOrg")
    void testSaveActivityHistForAddEntity_personAndOrg(@OasisTestParameter("inputRecord") Record inputRecord) {
        // 1. Arrange
        when(mockedActivityHistoryManager.recordActivityHistory(eq("CIS"), eq("ENTITY"), any(String.class), any(String.class), eq(""),
                any(String.class), eq(""))).thenReturn(1);

        // 2. Act
        spiedEntityAddManager.saveActivityHistForAddEntity(inputRecord);

        // 3. Verify
        verify(mockedMessageManager, times(1)).formatMessage(eq("cs.cis.activityHistory.displayInformation"), any(String[].class));
    }
}


























