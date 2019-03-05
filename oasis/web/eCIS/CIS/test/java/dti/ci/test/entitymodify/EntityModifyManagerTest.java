package dti.ci.test.entitymodify;

import dti.ci.entitymgr.EntityManager;
import dti.ci.entitymodify.EntityModifyManager;
import dti.ci.entitymodify.EntityModifyInfo;
import dti.ci.test.extension.CISExtension;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.test.annotations.OasisAutoWired;
import dti.oasis.test.annotations.OasisParameterizedTest;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.test.junit5.extension.OasisExtension;
import dti.oasis.test.junit5.tag.TestTags;
import dti.oasis.test.matcher.recordmatcher.RecordMatcher;
import dti.oasis.test.matcher.recordsetmatcher.RecordSetMatcher;
import dti.oasis.util.LogUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.logging.Logger;

import static dti.oasis.test.matcher.recordsetmatcher.RecordSetMatcher.hasField;
import static dti.oasis.test.matcher.recordsetmatcher.RecordSetMatcher.hasRecord;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/28/2018
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
public class EntityModifyManagerTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @OasisAutoWired
    private EntityModifyManager entityModifyManager;
    @OasisAutoWired
    private EntityManager       entityManager;

    @OasisParameterizedTest
    void testEntityModifyFieldsLoading(@OasisTestParameter("testLoadEntityModify.inputParameters4Per") Record inputPer,
                                      @OasisTestParameter("testLoadEntityModify.inputParameters4Org") Record inputOrg,
                                      @OasisTestParameter("testLoadEntityModify.expected.fieldNames") List<String> fieldNames) {

        //test for Person
        Record personRecord = entityManager.loadEntityData(inputPer);
        assertThat(personRecord, RecordMatcher.hasField(fieldNames.toArray(new String[0])));
        assertEquals(fieldNames.size(), personRecord.getFieldCount());

        //test for Org
        Record orgRecord = entityManager.loadEntityData(inputOrg);
        assertThat(orgRecord, RecordMatcher.hasField(fieldNames.toArray(new String[0])));
        assertEquals(fieldNames.size(), orgRecord.getFieldCount());

    }

    @OasisParameterizedTest
    void testEntityModifyGridsLoading(@OasisTestParameter("testLoadEntityModify.inputParameters4Per") Record inputParam,
                                      @OasisTestParameter("testLoadEntityModify.expected.gridColumnNameHistory") List<String> fieldNames,
                                      @OasisTestParameter("testLoadEntityModify.expected.gridColumnTaxHistory") List<String> taxNames,
                                      @OasisTestParameter("testLoadEntityModify.expected.gridColumnLossHistory") List<String> lossNames,
                                      @OasisTestParameter("testLoadEntityModify.expected.gridColumnDbaHistory") List<String> dbaNames,
                                      @OasisTestParameter("testLoadEntityModify.expected.gridColumnEtdHistory") List<String> etdNames
                                   ) {

        //test for load grid data
        RecordSet nameRs = entityModifyManager.loadNameHistory(inputParam);
        RecordSet taxRs = entityModifyManager.loadTaxHistory(inputParam);
        RecordSet lossRs = entityModifyManager.loadLossHistory(inputParam);
        RecordSet dbaRs = entityModifyManager.loadDbaHistory(inputParam);
        RecordSet etdRs = entityModifyManager.loadEtdHistory(inputParam);

        assertThat(nameRs,hasRecord());
        assertThat(nameRs,hasField());
        assertEquals(fieldNames,nameRs.getFieldNameList());

        assertThat(taxRs,hasRecord());
        assertThat(taxRs,hasField());
        assertEquals(taxNames,taxRs.getFieldNameList());

        assertThat(lossRs,hasRecord());
        assertThat(lossRs,hasField());
        assertEquals(lossNames,lossRs.getFieldNameList());

        assertThat(dbaRs,hasRecord());
        assertThat(dbaRs,hasField());
        assertEquals(dbaNames,dbaRs.getFieldNameList());

        assertThat(etdRs,hasRecord());
        assertThat(etdRs,hasField());
        assertEquals(etdNames,etdRs.getFieldNameList());

    }

    //comment: this test case is depended on database, so comment out
    void testEntityModifySaveData(@OasisTestParameter("testLoadEntityModify.inputParameters4Per") Record inputPer,
                                  @OasisTestParameter("testLoadEntityModify.inputParameters4Org") Record inputOrg,
                                  @OasisTestParameter("testLoadEntityModify.expected.fieldNames") List<String> fieldNames) throws Exception{

        //set key in RequestStorageManager
        RequestStorageManager requestStorageManager = RequestStorageManager.getInstance();
        requestStorageManager.set(RequestStorageIds.IS_PROCESS_EXCLUDED_FOR_OBR, true);

        //save for Person
        Record personRecord = entityManager.loadEntityData(inputPer);
        assertThat(personRecord, RecordMatcher.hasField(fieldNames.toArray(new String[0])));
        assertEquals(fieldNames.size(), personRecord.getFieldCount());

        personRecord.setFieldValue("entity_lastName","Lu2");
        personRecord.setFieldValue("entity_firstName","Chen2");
        EntityModifyInfo modifyInfo = null;
        entityModifyManager.saveEntityData(personRecord);
        assertTrue(modifyInfo.isEntityUpdated());

        //save for Org
        Record orgRecord = entityManager.loadEntityData(inputOrg);
        assertThat(orgRecord, RecordMatcher.hasField(fieldNames.toArray(new String[0])));
        assertEquals(fieldNames.size(), orgRecord.getFieldCount());

        orgRecord.setFieldValue("entity_organizationName","byIntergrationTest");
        entityModifyManager.saveEntityData(orgRecord);
        assertTrue(modifyInfo.isEntityUpdated());

    }

    //comment: this test case is depended on database, so comment out
    void testChangeEntityType(@OasisTestParameter("testChangeEntityType.changePersonToOrg") Record inputPer,
                              @OasisTestParameter("testChangeEntityType.changeOrgToPerson") Record inputOrg) {

        //change person to Org type
        String entityNewName = entityModifyManager.changeEntityType(inputPer);
        assertNotNull(entityNewName);
        //change Org back to Person
        entityNewName = entityModifyManager.changeEntityType(inputOrg);
        assertNotNull(entityNewName);
    }

    @OasisParameterizedTest
    void testValidateReferenceNumber(@OasisTestParameter("testValidateReferenceNumber.validReferrenceNumber") Record inputValid,
                                     @OasisTestParameter("testValidateReferenceNumber.invalidReferrenceNumber") Record inputInValid) {
        //check valid Ref number
        String returnValue = entityModifyManager.validateReferenceNumberAsStr(inputValid);
        boolean isValidateB = YesNoFlag.getInstance(returnValue).booleanValue();
        assertTrue(isValidateB);

        //check invalid Ref number
        returnValue = entityModifyManager.validateReferenceNumberAsStr(inputInValid);
        isValidateB = YesNoFlag.getInstance(returnValue).booleanValue();
        assertFalse(isValidateB);
    }

    @OasisParameterizedTest
    void testGetClientDiscardMessage(@OasisTestParameter("testClientDiscardPolCheck.nonPolicyholder") Record nonPolicyholder,
                                     @OasisTestParameter("testClientDiscardPolCheck.Policyholder") Record policyholder) {

        String clientDiscardMessageKey = entityModifyManager.getClientDiscardPolCheck(nonPolicyholder);
        assertNotNull(clientDiscardMessageKey);
        assertEquals("clientDiscardedMsg", clientDiscardMessageKey);

        clientDiscardMessageKey = entityModifyManager.getClientDiscardPolCheck(policyholder);
        assertNotNull(clientDiscardMessageKey);
        assertEquals("clientDiscardedMsgERROR", clientDiscardMessageKey);

    }

    @OasisParameterizedTest
    void testClientHasExpertWitnessClass(@OasisTestParameter("testClientHasExperWitnessClass.hasExpWitClass") Record hasExpWitClass,
                                         @OasisTestParameter("testClientHasExperWitnessClass.hasNoExpWitness") Record hasNoExpWitClass) {

        boolean isHasClassB = entityModifyManager.getExpWitTabVisibilityflag(hasExpWitClass);
        assertTrue(isHasClassB);

        isHasClassB = entityModifyManager.getExpWitTabVisibilityflag(hasNoExpWitClass);
        assertFalse(isHasClassB);

    }
}
