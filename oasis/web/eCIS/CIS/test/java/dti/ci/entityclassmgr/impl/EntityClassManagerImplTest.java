package dti.ci.entityclassmgr.impl;

import dti.ci.entityclassmgr.EntityClassFields;
import dti.ci.entityclassmgr.dao.EntityClassDAO;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.test.annotations.OasisParameterizedTest;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.test.junit5.tag.TestTags;
import dti.oasis.test.util.Counter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.logging.Logger;

import static dti.oasis.test.mockito.argumentmatcher.RecordArgumentMatchers.containFields;
import static dti.oasis.test.mockito.argumentmatcher.RecordSetArgumentMatchers.containRecords;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/29/2018
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
public class EntityClassManagerImplTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @Spy
    @InjectMocks
    private EntityClassManagerImpl spiedEntityClassManager;

    @Mock
    private EntityClassDAO mockedEntityClassDAO;

    @Mock
    private SysParmProvider mockedSysParmProvider;

    @Mock
    private MessageManager mockedMessageManager;

    @BeforeEach
    void setupMock() {
        MockitoAnnotations.initMocks(this);
    }

    @OasisParameterizedTest("testProcessSearchEntityClassCriteria")
    void testProcessSearchEntityClassCriteria(
            @OasisTestParameter("inputRecord") Record inputRecord,
            @OasisTestParameter("expectedProcessedInputRecord") Record processedInputRecord) {
        // Arrange
        doReturn(new RecordSet()).when(mockedEntityClassDAO).loadAllEntityClass(any(Record.class), any(RecordLoadProcessor.class));

        // Action
        spiedEntityClassManager.loadAllEntityClass(inputRecord);

        // Assert
        // Check if the record passed to DAO equals to the
        // It's an example of custom argument matcher. We can optional implement the "equals" method of Record and use "eq" argument matcher.
        verify(mockedEntityClassDAO, times(1)).loadAllEntityClass(
                argThat(containFields(processedInputRecord)),
                any(RecordLoadProcessor.class));
    }

    @OasisParameterizedTest("testAddEntityClass")
    void testAddEntityClass(@OasisTestParameter("inputRecord") Record inputRecord,
                            @OasisTestParameter("newEntityClassRs") RecordSet newEntityClassRs) {
        doNothing().when(mockedEntityClassDAO).saveEntityClass(any(RecordSet.class));

        spiedEntityClassManager.addEntityClass(inputRecord);

        verify(mockedEntityClassDAO, times(1)).saveEntityClass(argThat(containRecords(newEntityClassRs)));
    }

    @OasisParameterizedTest("testModifyEntityClass")
    void testModifyEntityClass(@OasisTestParameter("inputRecord") Record inputRecord,
                               @OasisTestParameter("modifiedEntityClassRs") RecordSet modifiedEntityClassRs) {
        doNothing().when(mockedEntityClassDAO).saveEntityClass(any(RecordSet.class));
        doReturn("N").when(mockedSysParmProvider).getSysParm(eq(EntityClassFields.CI_ENABLE_NETWORK_DISCOUNT), anyString());

        spiedEntityClassManager.modifyEntityClass(inputRecord);

        verify(mockedEntityClassDAO, times(1)).saveEntityClass(argThat(containRecords(modifiedEntityClassRs)));
    }

    @OasisParameterizedTest("testModifyEntityClass_netWorkDiscountEnabled")
    void testModifyEntityClass_netWorkDiscountEnabled(@OasisTestParameter("inputRecord") Record inputRecord,
                                                      @OasisTestParameter("modifiedEntityClassRs") RecordSet modifiedEntityClassRs) {
        doNothing().when(mockedEntityClassDAO).saveEntityClass(any(RecordSet.class));
        doReturn("Y").when(mockedSysParmProvider).getSysParm(eq(EntityClassFields.CI_ENABLE_NETWORK_DISCOUNT), anyString());

        spiedEntityClassManager.modifyEntityClass(inputRecord);

        verify(mockedEntityClassDAO, times(1)).saveEntityClass(argThat(containRecords(modifiedEntityClassRs)));
    }

    @OasisParameterizedTest("testAddEntityClassFailed_overlapEntityClass")
    void testAddEntityClassFailed_overlapEntityClass(@OasisTestParameter("inputRecord") Record inputRecord) {
        Counter errorMessageCounter = new Counter();
        doReturn(true).when(spiedEntityClassManager).hasOverlapEntityClass(any(Record.class));
        doAnswer(invocation -> {
            errorMessageCounter.increase();
            return null;
        }).when(mockedMessageManager).addErrorMessage(anyString(), any(Object[].class));
        doAnswer(invocation -> errorMessageCounter.getCount()).when(mockedMessageManager).getErrorMessageCount();

        assertThrows(ValidationException.class, () -> spiedEntityClassManager.addEntityClass(inputRecord));

        verify(mockedMessageManager, atLeastOnce()).addErrorMessage(eq("ci.entity.class.overlapClass"), any(Object[].class));
    }

    @OasisParameterizedTest("testAddEntityClassFailed_invalidNetworkDiscount")
    void testAddEntityClassFailed_invalidNetworkDiscount(@OasisTestParameter("inputRecord") Record inputRecord) {
        Counter errorMessageCounter = new Counter();

        doAnswer(invocation -> {
            errorMessageCounter.increase();
            return null;
        }).when(mockedMessageManager).addErrorMessage(anyString());
        doAnswer(invocation -> errorMessageCounter.getCount()).when(mockedMessageManager).getErrorMessageCount();

        assertThrows(ValidationException.class, () -> spiedEntityClassManager.addEntityClass(inputRecord));

        verify(mockedMessageManager, atLeastOnce()).addErrorMessage(eq("ci.entity.class.invalidNetworkDiscount"));
    }
}
