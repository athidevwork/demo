package dti.ci.entityclassmgr;

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
import static org.junit.Assert.assertEquals;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/29/2018
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
public class EntityClassListTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @OasisAutoWired
    private EntityClassManager entityClassManager;

    @OasisParameterizedTest("testLoadEntityClassList")
    void testLoadEntityClassList(
            @OasisTestParameter("inputRecord") Record inputRecord,
            @OasisTestParameter("expectedFieldNames") List<String> fieldNames) {
        RecordSet rs = entityClassManager.loadAllEntityClass(inputRecord);

        assertThat(rs, hasField(fieldNames));
        assertEquals(rs.getFieldCount(), fieldNames.size());
    }
}
