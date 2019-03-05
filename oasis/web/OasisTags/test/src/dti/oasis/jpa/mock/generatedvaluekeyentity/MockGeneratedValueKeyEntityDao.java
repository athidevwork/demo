package dti.oasis.jpa.mock.generatedvaluekeyentity;

import dti.oasis.jpa.mock.BaseMockDao;
import dti.oasis.jpa.mock.MockIdGenerator;

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
public class MockGeneratedValueKeyEntityDao extends BaseMockDao<GeneratedValueKeyEntity> {
    @Override
    protected Class<GeneratedValueKeyEntity> getEntityClass() {
        return GeneratedValueKeyEntity.class;
    }

    @Override
    public void addEntity(List<GeneratedValueKeyEntity> entityList) {
        for (GeneratedValueKeyEntity entity: entityList) {

            if (entity != null && entity.getId() == null) {
                entity.setId(MockIdGenerator.getInstance().getNextValue());
            }
        }
    }
}
