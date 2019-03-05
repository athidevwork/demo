package dti.ci.test.addressmgr;

import dti.ci.addressmgr.AddressManager;
import dti.ci.test.extension.CISExtension;
import dti.oasis.recordset.Record;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.test.annotations.OasisAutoWired;
import dti.oasis.test.annotations.OasisParameterizedTest;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.test.junit5.extension.OasisExtension;
import dti.oasis.test.junit5.tag.TestTags;
import dti.oasis.util.LogUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.logging.Logger;

import static dti.oasis.test.matcher.recordmatcher.RecordMatcher.hasField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/22/2018
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
@ExtendWith({OasisExtension.class, CISExtension.class})
@Tag(TestTags.INTEGRATION_VALUE)
public class AddressManagerTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @OasisAutoWired
    private AddressManager addressManager;

    @OasisParameterizedTest
    void testLoadAddressDetail(@OasisTestParameter("testLoadAddressDetail.inputParameters") Record inputRecord,
                              @OasisTestParameter("testLoadAddressDetail.expected.fieldNames") List<String> fieldNames) {

        Record outRecord = addressManager.loadAddressDetailInfo(inputRecord);
        assertThat(outRecord, hasField(fieldNames.toArray(new String[0])));
        assertEquals(fieldNames.size(), outRecord.getFieldCount());
    }

    @OasisParameterizedTest
    void testSaveAddressDetail(@OasisTestParameter("testLoadAddressDetail.inputParameters") Record inputRecord,
                              @OasisTestParameter("testLoadAddressDetail.expected.fieldNames") List<String> fieldNames) {

        Record outRecord = addressManager.loadAddressDetailInfo(inputRecord);
        assertThat(outRecord, hasField(fieldNames.toArray(new String[0])));
        assertEquals(fieldNames.size(), outRecord.getFieldCount());

        RequestStorageManager rsm = RequestStorageManager.getInstance();
        rsm.set(RequestStorageIds.IS_PROCESS_EXCLUDED_FOR_OBR, true);

        outRecord.setFieldValue("addressLine1", "address line 1 updated by test");
        outRecord.setFieldValue("addressLine2", "address line 2 updated by test");
        outRecord.setFieldValue("addressTypeCode", "BILLING");
        outRecord.setFieldValue("effectiveFromDate", "03/22/2018");
        outRecord.setFieldValue("effectiveToDate", "01/01/3000");
        outRecord.setFieldValue("addressId", "");
        Record updateRecord = addressManager.updateAddressDetailInfo(outRecord);
        String newAddressId = updateRecord.getStringValue("newAddressId");

        inputRecord.setFieldValue("addressId", newAddressId);
        outRecord = addressManager.loadAddressDetailInfo(inputRecord);
        assertEquals(outRecord.getStringValue("addressLine1"), "address line 1 updated by test");
        assertEquals(outRecord.getStringValue("addressLine2"), "address line 2 updated by test");
        assertEquals(outRecord.getStringValue("addressTypeCode"), "BILLING");
    }
}
