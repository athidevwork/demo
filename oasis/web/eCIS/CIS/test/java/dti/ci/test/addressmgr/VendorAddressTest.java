package dti.ci.test.addressmgr;

import dti.ci.addressmgr.AddressManager;
import dti.oasis.recordset.Record;
import dti.oasis.test.annotations.OasisAutoWired;
import dti.oasis.test.annotations.OasisParameterizedTest;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.test.junit5.extension.OasisExtension;
import dti.oasis.test.junit5.tag.TestTags;
import dti.oasis.test.matcher.recordmatcher.RecordMatcher;
import dti.oasis.util.LogUtils;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.List;
import java.util.logging.Logger;


/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/2/2018
 *
 * @author dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/05/2018       dzhang      Issue 109177: vendor address refactor
 * ---------------------------------------------------
 */
@ExtendWith(OasisExtension.class)
@Tag(TestTags.INTEGRATION_VALUE)
public class VendorAddressTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @OasisAutoWired
    private AddressManager addressManager;

    @Nested
    class AddressManagerIntegrateTest {

        @OasisParameterizedTest
        void testLoadVendorAddress(@OasisTestParameter("IntegrateTest.testLoadVendorAddress.recordWithValidId") Record recordWithValidId,
                                   @OasisTestParameter("IntegrateTest.testLoadVendorAddress.recordWithInvalidId") Record recordWithInvalidId,
                                   @OasisTestParameter("IntegrateTest.testLoadVendorAddress.expected.expectedFieldsWithAddress")List<String> expectedFieldsWithAddress,
                                   @OasisTestParameter("IntegrateTest.testLoadVendorAddress.expected.expectedFieldsWithoutAddress")List<String> expectedFieldsWithoutAddress) {
            //entity has vendor address
            Record outputRecord = addressManager.loadVendorAddress(recordWithValidId);

            assertThat(outputRecord, RecordMatcher.hasField(expectedFieldsWithAddress));
            Assert.assertEquals(expectedFieldsWithAddress.size(), outputRecord.getFieldCount());

            //entity doesn't have vendor address
            Record defaultOutputRecord = addressManager.loadVendorAddress(recordWithInvalidId);

            assertThat(defaultOutputRecord, RecordMatcher.hasField(expectedFieldsWithoutAddress));
            Assert.assertEquals(expectedFieldsWithoutAddress.size(), defaultOutputRecord.getFieldCount());

        }

        @OasisParameterizedTest
        void testSaveVendorAddress(@OasisTestParameter("IntegrateTest.testLoadVendorAddress.recordWithValidId") Record recordWithValidId) {
            Record addressRecord = addressManager.loadVendorAddress(recordWithValidId);
            addressRecord.setFieldValue("address_addressName", "test" );

            //save the address info
            Record resultRecord = addressManager.saveVendorAddress(addressRecord);
            Assert.assertNotNull(resultRecord);
        }
    }

}
