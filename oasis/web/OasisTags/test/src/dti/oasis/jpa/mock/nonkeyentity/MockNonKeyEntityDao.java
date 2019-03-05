package dti.oasis.jpa.mock.nonkeyentity;

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
public class MockNonKeyEntityDao extends BaseMockDao<NonKeyEntity> {
    @Override
    protected Class<NonKeyEntity> getEntityClass() {
        return NonKeyEntity.class;
    }
}
