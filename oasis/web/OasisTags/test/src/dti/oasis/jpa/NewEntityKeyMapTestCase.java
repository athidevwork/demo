package dti.oasis.jpa;

import dti.oasis.app.AppException;
import dti.oasis.jpa.mock.generatedvaluekeyentity.GeneratedValueKeyEntity;
import dti.oasis.jpa.mock.generatedvaluekeyentity.MockGeneratedValueKeyEntityService;
import dti.oasis.jpa.mock.nongeneratedvaluekeyentity.MockNonGeneratedValueKeyEntityService;
import dti.oasis.jpa.mock.nongeneratedvaluekeyentity.NonGeneratedValueKeyEntity;
import dti.oasis.jpa.mock.nonkeyentity.MockNonKeyEntityService;
import dti.oasis.jpa.mock.nonkeyentity.NonKeyEntity;
import dti.oasis.jpa.mock.nonkeygettersetterentity.MockNonKeyGetterSetterEntityService;
import dti.oasis.jpa.mock.nonkeygettersetterentity.NonKeyGetterSetterEntity;
import dti.oasis.test.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/10/2015
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
public class NewEntityKeyMapTestCase extends TestCase {
    private BaseService service = null;

    public NewEntityKeyMapTestCase(String testCaseName) {
        super(testCaseName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testAddGeneratedValueKeyEntity() {
        service = new MockGeneratedValueKeyEntityService();

        List<GeneratedValueKeyEntity> entityList = new ArrayList<GeneratedValueKeyEntity>();

        GeneratedValueKeyEntity entityA = new GeneratedValueKeyEntity(Long.valueOf(-30000l), "A", "I");
        GeneratedValueKeyEntity entityB = new GeneratedValueKeyEntity(Long.valueOf(-30001l), "B", "I");
        GeneratedValueKeyEntity entityC = new GeneratedValueKeyEntity(Long.valueOf(-30002l), "C", "I");

        entityList.add(entityA);
        entityList.add(entityB);
        entityList.add(entityC);

        @SuppressWarnings("unchecked")
        List<NewEntityKeyMap> newEntityKeyMapList = service.saveEntity(entityList);

        assertEquals(3, newEntityKeyMapList.size());

        for (NewEntityKeyMap newEntityKeyMap: newEntityKeyMapList) {
            assertNotNull(newEntityKeyMap.getNewEntityKey());
            assertTrue(newEntityKeyMap.getNewEntityKey().longValue() > 0l);

            assertNotNull(newEntityKeyMap.getEntityKey());
            if (entityA == newEntityKeyMap.getEntity()) {
                assertEquals(-30000l, newEntityKeyMap.getEntityKey().longValue());
            } else if (entityB == newEntityKeyMap.getEntity()) {
                assertEquals(-30001l, newEntityKeyMap.getEntityKey().longValue());
            } else if (entityC == newEntityKeyMap.getEntity()) {
                assertEquals(-30002l, newEntityKeyMap.getEntityKey().longValue());
            } else {
                fail("Cannot find the entity of the New Entity Key Map in the new entity list.");
            }
        }
    }

    public void testAddNonGeneratedValueKeyEntity() {
        service = new MockNonGeneratedValueKeyEntityService();

        List<NonGeneratedValueKeyEntity> entityList = new ArrayList<NonGeneratedValueKeyEntity>();

        NonGeneratedValueKeyEntity entityA = new NonGeneratedValueKeyEntity(Long.valueOf(-30000l), "A", "I");
        NonGeneratedValueKeyEntity entityB = new NonGeneratedValueKeyEntity(Long.valueOf(-30001l), "B", "I");
        NonGeneratedValueKeyEntity entityC = new NonGeneratedValueKeyEntity(Long.valueOf(-30002l), "C", "I");

        entityList.add(entityA);
        entityList.add(entityB);
        entityList.add(entityC);

        @SuppressWarnings("unchecked")
        List<NewEntityKeyMap> newEntityKeyMapList = service.saveEntity(entityList);

        assertEquals(0, newEntityKeyMapList.size());

        assertEquals(-30000l, entityA.getId().longValue());
        assertEquals(-30001l, entityB.getId().longValue());
        assertEquals(-30002l, entityC.getId().longValue());
    }

    public void testAddNonKeyEntity() {
        service = new MockNonKeyEntityService();

        List<NonKeyEntity> entityList = new ArrayList<NonKeyEntity>();

        NonKeyEntity entityA = new NonKeyEntity(Long.valueOf(-30000l), "A", "I");
        NonKeyEntity entityB = new NonKeyEntity(Long.valueOf(-30001l), "B", "I");
        NonKeyEntity entityC = new NonKeyEntity(Long.valueOf(-30002l), "C", "I");

        entityList.add(entityA);
        entityList.add(entityB);
        entityList.add(entityC);

        @SuppressWarnings("unchecked")
        List<NewEntityKeyMap> newEntityKeyMapList = service.saveEntity(entityList);

        assertEquals(0, newEntityKeyMapList.size());

        assertEquals(-30000l, entityA.getId().longValue());
        assertEquals(-30001l, entityB.getId().longValue());
        assertEquals(-30002l, entityC.getId().longValue());
    }

    public void testAddNonGetterSetterKeyEntity() {
        service = new MockNonKeyGetterSetterEntityService();

        List<NonKeyGetterSetterEntity> entityList = new ArrayList<NonKeyGetterSetterEntity>();

        NonKeyGetterSetterEntity entityA = new NonKeyGetterSetterEntity(Long.valueOf(-30000l), "A", "I");
        NonKeyGetterSetterEntity entityB = new NonKeyGetterSetterEntity(Long.valueOf(-30001l), "B", "I");
        NonKeyGetterSetterEntity entityC = new NonKeyGetterSetterEntity(Long.valueOf(-30002l), "C", "I");

        entityList.add(entityA);
        entityList.add(entityB);
        entityList.add(entityC);

        try {
            @SuppressWarnings("unchecked")
            List<NewEntityKeyMap> newEntityKeyMapList = service.saveEntity(entityList);

            fail("The system should throw exception for saving entities which have no getter/setter method for entity key.");
        } catch (AppException ae) {
            // fine
        }
    }
}
