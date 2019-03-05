package dti.ci.test.entitysearch;

import dti.ci.entitysearch.EntitySearchManager;
import dti.ci.test.extension.CISExtension;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.test.annotations.OasisAutoWired;
import dti.oasis.test.annotations.OasisParameterizedTest;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.test.junit5.extension.OasisExtension;
import dti.oasis.test.junit5.tag.TestTags;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static dti.oasis.test.matcher.messagematcher.MessageMatcher.hasMessage;
import static dti.oasis.test.matcher.recordsetmatcher.RecordSetMatcher.hasField;
import static dti.oasis.test.matcher.recordsetmatcher.RecordSetMatcher.hasRecord;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/14/2018
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
@ExtendWith({OasisExtension.class, CISExtension.class})
@Tag(TestTags.INTEGRATION_VALUE)
public class EntitySearchManagerTest {

    @OasisAutoWired
    private EntitySearchManager entitySearchManager;

    @OasisParameterizedTest
    void testSearchEntity(@OasisTestParameter("testSearchEntity.searchCriteria") Record inputRecord,
                          @OasisTestParameter("testSearchEntity.expected.fieldNames") List<String> fieldNames) {
        RecordSet rs = entitySearchManager.searchEntities(inputRecord);

        assertThat(rs, hasRecord());
        assertThat(rs, hasField(fieldNames));
        assertEquals(fieldNames.size(), rs.getFirstRecord().getFieldCount());
    }

    @OasisParameterizedTest
    void testSearchEntity_tooManyRecords(
            @OasisTestParameter("testSearchEntity_tooManyRecord.searchCriteria") Record inputRecord,
            @OasisTestParameter("testSearchEntity_tooManyRecord.infoMessageKeys") List<String> infoMessageKeys) {
        int maxSize = entitySearchManager.getEntitySearchMaxNum();

        RecordSet rs = entitySearchManager.searchEntities(inputRecord);

        // Invalid test case.
        assumeTrue((rs != null && maxSize == rs.getSize()), "the search criteria doesn't return too many records.");

        assertThat(MessageManager.getInstance(), hasMessage(MessageCategory.INFORMATION, infoMessageKeys));
    }

    //The test data of below two tests reply on specific db. This should be avoided.
/*
    @OasisParameterizedTest
    void testSearchEntity_isPolicyNoIncludedWithinSearch(@OasisTestParameter("testSearchEntity_isPolicyNoIncludedWithinSearch.searchCriteria") Record inputRecord) {
        boolean included = entitySearchManager.isPolicyNoIncludedWithinSearch(inputRecord);
        assertEquals(true, included);
    }

    @OasisParameterizedTest
    void testSearchEntity_getEntityClaims(@OasisTestParameter("testSearchEntity_getEntityClaims.searchCriteria") Record inputRecord,
                                          @OasisTestParameter("testSearchEntity_getEntityClaims.expected.fieldNames") List<String> fieldNames) {
        RecordSet rs = entitySearchManager.getEntityClaims(inputRecord);
        assertThat(rs, hasRecord());
        assertThat(rs, hasField(fieldNames));
    }*/
}
