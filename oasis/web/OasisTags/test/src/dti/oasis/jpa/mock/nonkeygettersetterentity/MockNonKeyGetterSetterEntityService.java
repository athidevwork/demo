package dti.oasis.jpa.mock.nonkeygettersetterentity;

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
public class MockNonKeyGetterSetterEntityService extends BaseMockService {
    @Override
    protected BaseService getBaseServiceDAOImpl() {
        return baseServiceDao;
    }

    private BaseService baseServiceDao = new MockNonKeyGetterSetterEntityDao();
}
