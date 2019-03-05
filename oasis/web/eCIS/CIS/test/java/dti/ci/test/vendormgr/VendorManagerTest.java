package dti.ci.test.vendormgr;

import dti.ci.test.extension.CISExtension;
import dti.ci.vendormgr.VendorManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.test.annotations.OasisAutoWired;
import dti.oasis.test.annotations.OasisParameterizedTest;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.test.junit5.extension.OasisExtension;
import dti.oasis.test.junit5.tag.TestTags;
import dti.oasis.test.matcher.recordmatcher.RecordMatcher;
import dti.oasis.util.LogUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.logging.Logger;

import static dti.oasis.test.matcher.recordsetmatcher.RecordSetMatcher.hasField;
import static dti.oasis.test.matcher.recordsetmatcher.RecordSetMatcher.hasRecord;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   4/11/2018
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
@ExtendWith({OasisExtension.class, CISExtension.class})
@Tag(TestTags.INTEGRATION_VALUE)
public class VendorManagerTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @OasisAutoWired
    private VendorManager vendorManager;

    @OasisParameterizedTest
    void testLoadVendor(@OasisTestParameter("testLoadVendor.inputParameters") Record inputRecord,
                        @OasisTestParameter("testLoadVendor.expected.fieldNames") List<String> fieldNames) {

        RecordSet rs = vendorManager.loadVendor(inputRecord);
        assertThat(rs, hasRecord());
        assertThat(rs, hasField(fieldNames));
        assertEquals(fieldNames.size(), rs.getFirstRecord().getFieldCount());

    }

    @OasisParameterizedTest
    void testLoadVendorAddress(@OasisTestParameter("testLoadVendorAddress.inputParameters") Record inputRecord,
                               @OasisTestParameter("testLoadVendorAddress.expected.fieldNames") List<String> fieldNames) {

        RecordSet rs = vendorManager.loadVendorAddress(inputRecord);
        assertThat(rs, hasRecord());
        assertThat(rs, hasField(fieldNames));
        assertEquals(fieldNames.size(), rs.getFirstRecord().getFieldCount());

    }

    @OasisParameterizedTest
    void testLoadVendorPayment(@OasisTestParameter("testLoadVendorPayment.inputParameters") Record inputRecord,
                               @OasisTestParameter("testLoadVendorPayment.expected.fieldNames") List<String> fieldNames) {

        RecordSet rs = vendorManager.loadVendorPayment(inputRecord);
        assertThat(rs, hasRecord());
        assertThat(rs, hasField(fieldNames));
        assertEquals(fieldNames.size(), rs.getFirstRecord().getFieldCount());
    }
}
