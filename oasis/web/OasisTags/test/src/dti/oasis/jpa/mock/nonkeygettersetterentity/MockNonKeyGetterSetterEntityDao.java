package dti.oasis.jpa.mock.nonkeygettersetterentity;

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
public class MockNonKeyGetterSetterEntityDao extends BaseMockDao<NonKeyGetterSetterEntity> {
    @Override
    protected Class<NonKeyGetterSetterEntity> getEntityClass() {
        return NonKeyGetterSetterEntity.class;
    }
}
