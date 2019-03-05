package dti.ci.test.example;

import org.springframework.stereotype.Repository;

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
@Repository
public class FooDummyDAO implements FooDAO {
    @Override
    public String foo() {
        return "Foo from DAO";
    }
}
