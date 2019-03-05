package dti.ci.test.claimsmgr;

import dti.ci.claimsmgr.ClaimsManager;
import dti.ci.test.extension.CISExtension;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.IOasisAction;
import dti.oasis.test.annotations.OasisAutoWired;
import dti.oasis.test.annotations.OasisParameterizedTest;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.test.junit5.extension.OasisExtension;
import dti.oasis.test.junit5.tag.TestTags;
import dti.oasis.test.matcher.recordmatcher.RecordMatcher;
import dti.oasis.util.LogUtils;
import dti.oasis.util.OasisUser;
import org.apache.struts.mock.MockHttpServletRequest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.logging.Logger;

import static dti.oasis.test.matcher.recordsetmatcher.RecordSetMatcher.hasField;
import static dti.oasis.test.matcher.recordsetmatcher.RecordSetMatcher.hasRecord;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   4/23/2018
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
public class ClaimsManagerTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @OasisAutoWired
    private ClaimsManager claimsManager;

    @OasisParameterizedTest
    void testLoadFirstClaim(@OasisTestParameter("testLoadFirstClaim.inputParameters") Record inputRecord,
                            @OasisTestParameter("testLoadFirstClaim.expected.fieldNames") List<String> fieldNames) {

        Record outRecord = claimsManager.loadFirstClaim(inputRecord);
        assertThat(outRecord, RecordMatcher.hasField(fieldNames.toArray(new String[0])));
        assertEquals(fieldNames.size(), outRecord.getFieldCount());
    }

    @OasisParameterizedTest
    void testLoadClaimInfo(@OasisTestParameter("testLoadClaimInfo.inputParameters") Record inputRecord,
                           @OasisTestParameter("testLoadClaimInfo.expected.fieldNames") List<String> fieldNames) {

        RecordSet rs = claimsManager.loadClaimInfo(inputRecord);
        assertThat(rs, hasRecord());
        assertThat(rs, hasField(fieldNames));
        assertEquals(fieldNames.size(), rs.getFirstRecord().getFieldCount());
    }

    @OasisParameterizedTest
    void testLoadClaimParticipants(@OasisTestParameter("testLoadClaimParticipants.inputParameters") Record inputRecord,
                                   @OasisTestParameter("testLoadClaimParticipants.expected.fieldNames") List<String> fieldNames) {

        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpSession session = request.getSession();
        OasisUser user = new OasisUser();
        user.setUserId("ODEV20191");
        session.setAttribute(IOasisAction.KEY_OASISUSER,user);
        RecordSet rs = claimsManager.loadClaimParticipants(inputRecord, request);
        assertThat(rs, hasRecord());
        assertThat(rs, hasField(fieldNames));
        assertEquals(fieldNames.size(), rs.getFirstRecord().getFieldCount());
    }

    @OasisParameterizedTest
    void testLoadCompanion(@OasisTestParameter("testLoadCompanion.inputParameters") Record inputRecord,
                           @OasisTestParameter("testLoadCompanion.expected.fieldNames") List<String> fieldNames) {

        MockHttpServletRequest request = new MockHttpServletRequest();
        RecordSet rs = claimsManager.loadCompanion(inputRecord, request);
        assertThat(rs, hasRecord());
        assertThat(rs, hasField(fieldNames));
        assertEquals(fieldNames.size(), rs.getFirstRecord().getFieldCount());
    }
}
