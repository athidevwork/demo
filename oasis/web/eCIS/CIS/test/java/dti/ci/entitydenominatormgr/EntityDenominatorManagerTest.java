package dti.ci.entitydenominatormgr;

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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/11/2018
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
@ExtendWith(OasisExtension.class)
@Tag(TestTags.INTEGRATION_VALUE)
public class EntityDenominatorManagerTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @OasisAutoWired
    private EntityDenominatorManager entityDenominatorManager;

    @OasisParameterizedTest("testLoadAllEntityDenominator")
    void testLoadAllEntityDenominator(
            @OasisTestParameter("inputRecord") Record inputRecord,
            @OasisTestParameter("fieldNames") List<String> fieldNames) {
        RecordSet rs = entityDenominatorManager.loadAllEntityDenominator(inputRecord);

        assertEquals(rs.getFieldCount(), fieldNames.size());

        assertThat(rs, hasField(fieldNames));
    }
}
