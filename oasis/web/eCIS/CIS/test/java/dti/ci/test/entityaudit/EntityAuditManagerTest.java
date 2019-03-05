package dti.ci.test.entityaudit;

import dti.ci.auditmgr.AuditTrailManager;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/16/2018
 *
 * @author yllu
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
public class EntityAuditManagerTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @OasisAutoWired
    private AuditTrailManager auditTrailManager;

    @OasisParameterizedTest
    void testEntityAuditGetDefaultSearchCriteriaValue() {
        //test load defaul value
        Record defaultRecord = auditTrailManager.getDefaultSearchCriteriaValue("dti.ci.auditmgr.struts.MaintainAuditTrailAction");
        assertNotNull(defaultRecord);
    }

    @OasisParameterizedTest
    void testEntityAuditLoadALLDataValue(@OasisTestParameter("EntityAuditManagerTest.testLoadAllAuditData.InputParameter") Record inputRecord,
                                         @OasisTestParameter("EntityAuditManagerTest.testLoadAllAuditData.expected.fieldNames") List<String> fieldNames
                                        ) {
        //test load
        RecordSet outputRs = auditTrailManager.searchAuditTrailData(inputRecord);
        assertThat(outputRs,hasRecord());
        assertThat(outputRs,hasField());
        assertEquals(fieldNames.size(), outputRs.getFieldCount());
    }

    @OasisParameterizedTest
    void testEntityAuditLoadDataForPopup(@OasisTestParameter("EntityAuditManagerTest.testLoadAuditDataForPopup.InputParmsForPM") Record pmInputRecord,
                                         @OasisTestParameter("EntityAuditManagerTest.testLoadAuditDataForPopup.InputParmsForCM") Record cmInputRecord,
                                         @OasisTestParameter("EntityAuditManagerTest.testLoadAuditDataForPopup.InputParmsForOther") Record otherInputRecord,
                                         @OasisTestParameter("EntityAuditManagerTest.testLoadAuditDataForPopup.expected.fieldNames") List<String> fieldNames) {
        //test load
        RecordSet outputRs = auditTrailManager.loadAuditTrailBySource(pmInputRecord);
        assertThat(outputRs,hasRecord());
        assertThat(outputRs,hasField());
        assertEquals(fieldNames.size(), outputRs.getFieldCount());

        outputRs = auditTrailManager.loadAuditTrailBySource(cmInputRecord);
        assertThat(outputRs,hasRecord());
        assertThat(outputRs,hasField());
        assertEquals(fieldNames.size(), outputRs.getFieldCount());

        outputRs = auditTrailManager.loadAuditTrailBySource(otherInputRecord);
        assertThat(outputRs,hasRecord());
        assertThat(outputRs,hasField());
        assertEquals(fieldNames.size(), outputRs.getFieldCount());

    }
}
