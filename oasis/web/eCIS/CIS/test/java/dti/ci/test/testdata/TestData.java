package dti.ci.test.testdata;

import dti.ci.test.testdata.entity.PersonName;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/14/2018
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

public interface TestData {
    void init();

    PersonName getDefaultPersonName();
}
