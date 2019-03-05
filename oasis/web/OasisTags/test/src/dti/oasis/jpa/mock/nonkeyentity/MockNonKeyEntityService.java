package dti.oasis.jpa.mock.nonkeyentity;

import dti.oasis.jpa.BaseService;
import dti.oasis.jpa.mock.BaseMockService;

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
public class MockNonKeyEntityService extends BaseMockService<NonKeyEntity> {
    @Override
    protected BaseService<NonKeyEntity> getBaseServiceDAOImpl() {
        return baseServiceDao;
    }

    private BaseService<NonKeyEntity> baseServiceDao = new MockNonKeyEntityDao();
}
