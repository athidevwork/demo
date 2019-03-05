package dti.ci.test.agentmgr;

import dti.ci.agentmgr.AgentManager;
import dti.ci.test.extension.CISExtension;
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
 * Date:   3/29/2018
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
public class AgentManagerTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @OasisAutoWired
    private AgentManager agentManager;

    @OasisParameterizedTest
    void testLoadAllAgent(@OasisTestParameter("testLoadAllAgent.inputParameters") Record inputRecord,
                          @OasisTestParameter("testLoadAllAgent.expected.fieldNames") List<String> fieldNames) {

        Record outRecord = agentManager.loadAllAgent(inputRecord);
        assertThat(outRecord, RecordMatcher.hasField(fieldNames.toArray(new String[0])));
        assertEquals(fieldNames.size(), outRecord.getFieldCount());
    }

    @OasisParameterizedTest
    void testLoadAllAgentPayCommission(@OasisTestParameter("testLoadAllAgentPayCommission.inputParameters") Record inputRecord,
                                       @OasisTestParameter("testLoadAllAgentPayCommission.expected.fieldNames") List<String> fieldNames) {

        RecordSet rs = agentManager.loadAllAgentPayCommission(inputRecord);
        assertThat(rs, hasRecord());
        assertThat(rs, hasField(fieldNames));
        assertEquals(fieldNames.size(), rs.getFirstRecord().getFieldCount());
    }

    @OasisParameterizedTest
    void testLoadAllAgentContract(@OasisTestParameter("testLoadAllAgentContract.inputParameters") Record inputRecord,
                                  @OasisTestParameter("testLoadAllAgentContract.expected.fieldNames") List<String> fieldNames) {

        RecordSet rs = agentManager.loadAllAgentContract(inputRecord);
        assertThat(rs, hasRecord());
        assertThat(rs, hasField(fieldNames));
        assertEquals(fieldNames.size(), rs.getFirstRecord().getFieldCount());
    }

    @OasisParameterizedTest
    void testLoadAllAgentContractCommission(@OasisTestParameter("testLoadAllAgentContractCommission.inputParameters") Record inputRecord,
                                            @OasisTestParameter("testLoadAllAgentContractCommission.expected.fieldNames") List<String> fieldNames) {

        RecordSet rs = agentManager.loadAllAgentContractCommission(inputRecord);
        assertThat(rs, hasRecord());
        assertThat(rs, hasField(fieldNames));
        assertEquals(fieldNames.size(), rs.getFirstRecord().getFieldCount());
    }

    @OasisParameterizedTest
    void testLoadAllAgentStaff(@OasisTestParameter("testLoadAllAgentStaff.inputParameters") Record inputRecord,
                               @OasisTestParameter("testLoadAllAgentStaff.expected.fieldNames") List<String> fieldNames) {

        RecordSet rs = agentManager.loadAllAgentStaff(inputRecord);
        assertThat(rs, hasField(fieldNames));
        // note: there are no records in odev20191
   //     assertThat(rs, hasRecord());
   //     assertEquals(fieldNames.size(), rs.getFirstRecord().getFieldCount());
    }

    @OasisParameterizedTest
    void testLoadAllAgentStaffOverride(@OasisTestParameter("testLoadAllAgentStaffOverride.inputParameters") Record inputRecord,
                                       @OasisTestParameter("testLoadAllAgentStaffOverride.expected.fieldNames") List<String> fieldNames) {

        RecordSet rs = agentManager.loadAllAgentStaffOverride(inputRecord);
        assertThat(rs, hasField(fieldNames));
        // note: there are no records in odev20191
      //  assertThat(rs, hasRecord());
      //  assertEquals(fieldNames.size(), rs.getFirstRecord().getFieldCount());
    }
}
