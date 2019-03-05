package dti.ci.test.testdata;

import dti.ci.test.testdata.entity.PersonName;
import dti.ci.test.testdata.person.dao.PersonTestDataDAO;
import dti.oasis.util.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

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
@Service
public class TestDataImpl implements TestData {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public void init() {
        getPersonTestDataDAO().init(getDefaultPersonName());
    }

    @Override
    public PersonName getDefaultPersonName() {
        l.entering(getClass().getName(), "getPersonName");

        PersonName personName = new PersonName();
        personName.setLastName("UnitTest");
        personName.setFirstName(System.getProperty("user.name"));  // UnitTest, kshen

        return personName;
    }

    public PersonTestDataDAO getPersonTestDataDAO() {
        return personTestDataDAO;
    }

    public void setPersonTestDataDAO(PersonTestDataDAO personTestDataDAO) {
        this.personTestDataDAO = personTestDataDAO;
    }

    @Autowired
    private PersonTestDataDAO personTestDataDAO;
}
