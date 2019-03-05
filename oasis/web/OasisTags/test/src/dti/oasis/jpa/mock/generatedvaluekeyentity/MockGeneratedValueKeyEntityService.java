package dti.oasis.jpa.mock.generatedvaluekeyentity;

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
public class MockGeneratedValueKeyEntityService extends BaseMockService<GeneratedValueKeyEntity> {
    @Override
    protected BaseService<GeneratedValueKeyEntity> getBaseServiceDAOImpl() {
        return baseServiceDAO;
    }

    private BaseService<GeneratedValueKeyEntity> baseServiceDAO = new MockGeneratedValueKeyEntityDao();
}
