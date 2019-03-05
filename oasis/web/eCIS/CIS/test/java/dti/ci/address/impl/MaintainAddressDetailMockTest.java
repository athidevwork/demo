package dti.ci.address.impl;

import dti.ci.addressmgr.dao.AddressJdbcDAO;
import dti.ci.addressmgr.impl.AddressManagerImpl;
import dti.oasis.app.ConfigurationException;
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
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/26/2018
 *
 * @author eouyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
@Tag(TestTags.MOCK_VALUE)
public class MaintainAddressDetailMockTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @Spy
    @InjectMocks
    private AddressManagerImpl spiedAddressManager;

    @Mock
    private AddressJdbcDAO mockedAddressDao;

    @Mock
    private MessageManager mockedMessageManager;

    @Mock
    private SysParmProvider mockSysParmProvider;

    @BeforeEach
    void setupMock() {
        MockitoAnnotations.initMocks(this);
    }

    @OasisParameterizedTest("testLoadAddressDetailInfo")
    void testLoadAddressDetailInfo(@OasisTestParameter("inputParameters") Record inputRecord) {

        Record mockRecord = mock(Record.class);

        doReturn(mockRecord).when(mockedAddressDao).loadVendorAddressTypeInfo();

        doReturn(mockRecord).when(mockedAddressDao).loadAddressDetailInfo(inputRecord, null);

        Record outRecord = spiedAddressManager.loadAddressDetailInfo(inputRecord);

        assertNotNull(outRecord.getStringValue("addressId"));
    }

    @OasisParameterizedTest("testSaveAddressDetail")
    void testSaveAddressDetail(@OasisTestParameter("inputRecord") Record inputRecord) {

    }

    @OasisParameterizedTest("testCheckCountyCode")
    void testCheckCountyCode(@OasisTestParameter("inputRecord") Record inputRecord) {
        doReturn("Y").when(mockSysParmProvider).getSysParm("CI_CHK_PREMISE_ADDR");

        ValidationException ve = assertThrows(ValidationException.class, () -> spiedAddressManager.checkCountyCode(inputRecord));

        assertEquals(ve.getDebugMessage(), "Invalid Address Save.");
    }

    @Test
    void testVerifyConfig() {
        doReturn(null).when(spiedAddressManager).getAddressDAO();

        ConfigurationException ce = assertThrows(ConfigurationException.class, () -> spiedAddressManager.verifyConfig());

        assertTrue(ce.getMessage().matches(".*The required property .* is missing.*"));
    }
}
