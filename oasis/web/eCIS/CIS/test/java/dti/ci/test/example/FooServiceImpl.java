package dti.ci.test.example;

import dti.oasis.util.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/1/2018
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
@Service
public class FooServiceImpl implements FooService {
    private final Logger l = LogUtils.getLogger(getClass());
    @Autowired
    private FooDAO fooDAO;

    @Override
    public String foo() {
        return getFooDAO().foo();
    }

    @Override
    public String fooFromMessageManager() {
        // Use getFooMessageProvider() instead of FooMessageProvider.getInstance() to enable mock.
        return getFooMessageProvider().foo();
    }

    public FooDAO getFooDAO() {
        return fooDAO;
    }

    public void setFooDAO(FooDAO fooDAO) {
        this.fooDAO = fooDAO;
    }

    public FooMessageProvider getFooMessageProvider() {
        return FooMessageProvider.getInstance();
    }
}
