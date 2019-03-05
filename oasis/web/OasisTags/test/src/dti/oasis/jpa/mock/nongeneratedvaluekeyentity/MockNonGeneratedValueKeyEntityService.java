package dti.oasis.jpa.mock.nongeneratedvaluekeyentity;

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
public class MockNonGeneratedValueKeyEntityService extends BaseMockService<NonGeneratedValueKeyEntity> {
    @Override
    protected BaseService<NonGeneratedValueKeyEntity> getBaseServiceDAOImpl() {
        return baseServiceDao;
    }

    private BaseService<NonGeneratedValueKeyEntity> baseServiceDao = new MockNonGeneratedValueKeyEntityDao();
}
