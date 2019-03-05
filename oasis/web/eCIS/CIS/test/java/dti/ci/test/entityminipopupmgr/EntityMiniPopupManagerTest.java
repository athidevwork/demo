package dti.ci.test.entityminipopupmgr;

import dti.ci.entityminipopupmgr.EntityMiniPopupManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.test.annotations.OasisAutoWired;
import dti.oasis.test.annotations.OasisParameterizedTest;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.test.junit5.extension.OasisExtension;
import dti.oasis.test.junit5.tag.TestTags;
import dti.oasis.test.matcher.recordsetmatcher.RecordSetMatcher;
import dti.oasis.util.LogUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.logging.Logger;

import static dti.oasis.test.matcher.recordmatcher.RecordMatcher.hasField;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/23/2018
 *
 * @author dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/23/2018       dzhang      Issue 192649: entity mini popup refactor
 * ---------------------------------------------------
 */
@ExtendWith(OasisExtension.class)
@Tag(TestTags.INTEGRATION_VALUE)
public class EntityMiniPopupManagerTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @OasisAutoWired
    private EntityMiniPopupManager entityMiniPopupManager;

    /**
     * Test load entity
     * @param inputRecord
     * @param fieldNames
     */
    @OasisParameterizedTest
    void testLoadEntity(@OasisTestParameter("EntityMiniPopupManagerTest.inputRecord")Record inputRecord,
                        @OasisTestParameter("EntityMiniPopupManagerTest.testLoadEntity.expected.fieldNames")List<String> fieldNames) {

        Record outRecord = entityMiniPopupManager.loadEntity(inputRecord);
        assertThat(outRecord, hasField(fieldNames.toArray(new String[0])));
    }

    /**
     * Test load entity address
     * @param inputRecord
     * @param fieldNames
     */
    @OasisParameterizedTest
    void testLoadEntityAddressList(@OasisTestParameter("EntityMiniPopupManagerTest.inputRecord")Record inputRecord,
                                   @OasisTestParameter("EntityMiniPopupManagerTest.testLoadEntityAddressList.expected.fieldNames")List<String> fieldNames) {
        RecordSet entityAddressRS = entityMiniPopupManager.loadEntityAddressList(inputRecord);
        assertThat(entityAddressRS, RecordSetMatcher.hasField(fieldNames));
        assertThat(entityAddressRS, RecordSetMatcher.hasRecord());
        Assert.assertEquals(fieldNames.size(), entityAddressRS.getFirstRecord().getFieldCount());
    }

    /**
     * Test load entity address phone
     * @param inputRecord
     * @param fieldNames
     */
    @OasisParameterizedTest
    void testLoadAddressPhoneList(@OasisTestParameter("EntityMiniPopupManagerTest.inputRecord")Record inputRecord,
                                  @OasisTestParameter("EntityMiniPopupManagerTest.testLoadAddressPhoneList.expected.fieldNames")List<String> fieldNames) {
        RecordSet addressPhoneRS = entityMiniPopupManager.loadAddressPhoneList(inputRecord);
        assertThat(addressPhoneRS, RecordSetMatcher.hasField(fieldNames));
        assertThat(addressPhoneRS, RecordSetMatcher.hasRecord());
        Assert.assertEquals(fieldNames.size(), addressPhoneRS.getFirstRecord().getFieldCount());
    }

    /**
     * Test load contact list
     * @param inputRecord
     * @param fieldNames
     */
    @OasisParameterizedTest
    void testGetContactList(@OasisTestParameter("EntityMiniPopupManagerTest.inputRecord")Record inputRecord,
                            @OasisTestParameter("EntityMiniPopupManagerTest.testGetContactList.expected.fieldNames")List<String> fieldNames) {
        RecordSet contactList = entityMiniPopupManager.getContactList(inputRecord);
        assertThat(contactList, RecordSetMatcher.hasField(fieldNames));
        assertThat(contactList, RecordSetMatcher.hasRecord());
    }

    /**
     * Test load general phone list
     * @param inputRecord
     * @param fieldNames
     */
    @OasisParameterizedTest
    void testLoadEntityGeneralPhoneList(@OasisTestParameter("EntityMiniPopupManagerTest.inputRecord")Record inputRecord,
                                        @OasisTestParameter("EntityMiniPopupManagerTest.testLoadEntityGeneralPhoneList.expected.fieldNames")List<String> fieldNames) {
        RecordSet generalPhoneList = entityMiniPopupManager.loadEntityGeneralPhoneList(inputRecord);
        assertThat(generalPhoneList, RecordSetMatcher.hasField(fieldNames));
        assertThat(generalPhoneList, RecordSetMatcher.hasRecord());
        Assert.assertEquals(fieldNames.size(), generalPhoneList.getFirstRecord().getFieldCount());
    }

}
