package dti.ci.test.trainingmgr;

import dti.ci.test.extension.CISExtension;
import dti.ci.trainingmgr.TrainingManager;
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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   3/21/2018
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
public class TrainingTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @OasisAutoWired
    private TrainingManager trainingManager;

    @OasisParameterizedTest
    void testLoadTrainingList(@OasisTestParameter("testLoadTrainingList.inputParameters") Record inputRecord,
                              @OasisTestParameter("testLoadTrainingList.expected.fieldNames") List<String> fieldNames) {

        RecordSet rs = trainingManager.loadTrainingList(inputRecord);
        assertThat(rs, hasRecord());
        assertThat(rs, hasField(fieldNames));
        assertEquals(fieldNames.size(), rs.getFirstRecord().getFieldCount());
    }

//**** This test cannot be run in jUnit currently because
//**** the Save Interceptor uses some request processing
//    @OasisParameterizedTest
//    void saveTrainingData(@OasisTestParameter("testLoadTrainingList.inputParameters") Record inputRecord,
//                          @OasisTestParameter("testLoadTrainingList.expected.fieldNames") List<String> fieldNames) {
//        // get records
//        RecordSet rs = trainingManager.loadTrainingList(inputRecord);
//        assertThat(rs, hasRecord());
//        assertThat(rs, hasField(fieldNames));
//        assertEquals(fieldNames.size(), rs.getFirstRecord().getFieldCount());
//        // Do update
//        Record updateRecord = rs.getRecord(2);
//        updateRecord.setFieldValue("trainingTypeCode","OTHER");
//        rs.replaceRecord(2,updateRecord);
//        trainingManager.saveTrainingData(rs);
//        // read back data to verify updated
//        RecordSet rs2 = trainingManager.loadTrainingList(inputRecord);
//        assertThat(rs2, hasRecord());
//        assertThat(rs2, hasField(fieldNames));
//        String updatedFieldValue = rs2.getRecord(2).getStringValue("trainingTypeCode");
//        // Was it updated?
//        assumeTrue(updatedFieldValue.equals("OTHER")) ;
//    }

}
