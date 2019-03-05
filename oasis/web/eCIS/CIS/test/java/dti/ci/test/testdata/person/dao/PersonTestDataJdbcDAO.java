package dti.ci.test.testdata.person.dao;

import dti.ci.test.testdata.entity.PersonName;
import dti.oasis.util.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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

@Repository
public class PersonTestDataJdbcDAO implements PersonTestDataDAO {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public synchronized void init(PersonName personName) {
        l.entering(getClass().getName(), "init");

        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        // Check if user exists
        int count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM entity e WHERE e.last_name = ? AND e.first_name = ?",
                Integer.class,
                personName.getLastName(),
                personName.getFirstName()
        );

        if (count == 0) {
            // Add person
            getJdbcTemplate().update(
                    "INSERT INTO entity (ENTITY_PK, ENTITY_TYPE, LAST_NAME, FIRST_NAME) VALUES (OASIS_SEQUENCE.nextval, ?, ?, ?)",
                    "P", personName.getLastName(), personName.getFirstName());
        }

        l.exiting(getClass().getName(), "init");
    }

    private JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    @Qualifier("AppDataSource")
    private DataSource dataSource;
}
