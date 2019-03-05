package dti.ci.test.correspondencemgr;

import dti.ci.correspondencemgr.CorrespondenceManager;
import dti.ci.test.extension.CISExtension;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.test.annotations.OasisAutoWired;
import dti.oasis.test.annotations.OasisParameterizedTest;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.test.junit5.extension.OasisExtension;
import dti.oasis.test.junit5.tag.TestTags;
import dti.oasis.test.matcher.recordmatcher.HasField;
import dti.oasis.test.matcher.recordsetmatcher.RecordSetMatcher;
import dti.oasis.util.LogUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/10/2018
 *
 * @author dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/11/2018       dzhang      Issue 109204: correspondence refactor
 * ---------------------------------------------------
 */
@ExtendWith({OasisExtension.class, CISExtension.class})
@Tag(TestTags.INTEGRATION_VALUE)
public class CorrespondenceManagerTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @OasisAutoWired
    private CorrespondenceManager correspondenceManager;

    @OasisParameterizedTest
    void testLoadCorrespondence(@OasisTestParameter("CorrespondenceManagerTest.testLoadCorrespondence.validInputRecord")Record validInputRecord,
                                @OasisTestParameter("CorrespondenceManagerTest.testLoadCorrespondence.invalidInputRecord")Record invalidInputRecord,
                                @OasisTestParameter("CorrespondenceManagerTest.testLoadCorrespondence.expected.fieldNames")List<String> fieldNames) {
        //valid entity and has correspondence
        RecordSet outputRecordSet = correspondenceManager.loadCorrespondenceList(validInputRecord);
        assertThat(outputRecordSet, RecordSetMatcher.hasField(fieldNames));
        assertThat(outputRecordSet, RecordSetMatcher.hasRecord());
        Assert.assertEquals(fieldNames.size(), outputRecordSet.getFirstRecord().getFieldCount());

        //invalid entity and no correspondence record
        RecordSet outputRecordSet2 = correspondenceManager.loadCorrespondenceList(invalidInputRecord);
        Assert.assertEquals(0, outputRecordSet2.getSize());
    }
}
