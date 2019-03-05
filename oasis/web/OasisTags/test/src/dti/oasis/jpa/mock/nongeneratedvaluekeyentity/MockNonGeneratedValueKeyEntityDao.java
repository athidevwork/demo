package dti.oasis.jpa.mock.nongeneratedvaluekeyentity;

import dti.oasis.jpa.mock.BaseMockDao;

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
public class MockNonGeneratedValueKeyEntityDao extends BaseMockDao<NonGeneratedValueKeyEntity> {
    @Override
    protected Class<NonGeneratedValueKeyEntity> getEntityClass() {
        return NonGeneratedValueKeyEntity.class;
    }
}
