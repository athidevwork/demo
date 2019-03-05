package dti.ci.test.billingmgr;

import dti.ci.billingmgr.CIBillingManager;
import dti.ci.test.extension.CISExtension;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
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

import static dti.oasis.test.matcher.recordsetmatcher.RecordSetMatcher.hasField;
import static dti.oasis.test.matcher.recordsetmatcher.RecordSetMatcher.hasRecord;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/20/2018
 *
 * @author yllu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/20/2018       ylu         Issue 192741: integration test (note: below test is depended on database's data)
 * ---------------------------------------------------
 */
@ExtendWith({OasisExtension.class, CISExtension.class})
@Tag(TestTags.INTEGRATION_VALUE)
public class BillingManagerTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @OasisAutoWired
    private CIBillingManager billingManager;

    @OasisParameterizedTest
    void testBillingLoadAllAccount(@OasisTestParameter("testLoadBilling.inputParameters") Record inputRecord,
                                   @OasisTestParameter("testLoadBilling.expected.fieldNames") List<String> fieldNames
    ) {
        //test load
        RecordSet outputRs = billingManager.loadAllAccount(inputRecord);
        assertThat(outputRs,hasRecord());
        assertThat(outputRs,hasField());
        assertEquals(fieldNames.size(), outputRs.getFieldCount());
    }
}
