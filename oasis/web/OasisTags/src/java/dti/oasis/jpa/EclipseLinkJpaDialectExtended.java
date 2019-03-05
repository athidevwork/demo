package dti.oasis.jpa;

import dti.oasis.util.DatabaseUtils;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.jdbc.datasource.SimpleConnectionHandle;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   5/15/12
 *
 * @author mgitelman
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EclipseLinkJpaDialectExtended extends EclipseLinkJpaDialect {

    /**
     * The default implementation does nothing, assuming that the Connection
     * will implicitly be closed with the EntityManager.
     * <p>If the JPA implementation returns a Connection handle that it expects
     * the application to close after use, the dialect implementation needs to invoke
     * <code>Connection.close()</code> (or some other method with similar effect) here.
     * @see java.sql.Connection#close()
     */
    public void releaseJdbcConnection(ConnectionHandle conHandle, EntityManager em)
            throws PersistenceException, SQLException {
        DatabaseUtils.close(conHandle.getConnection());
    }

    /**
     * Override the method getJdbcConnection with the version 4.0.3 of Spring.
     * Spring 4.3.1 used EclipseLinkConnectionHandle to lazy load connection. But it cannot get connection correctly for some reason.
     * TODO Test if the EclipseLinkConnectionHandle works in the next version of Spring or EclipseLink if they are upgraded.
     */
    @Override
    public ConnectionHandle getJdbcConnection(EntityManager entityManager, boolean readOnly)
            throws PersistenceException, SQLException {

        Connection con = entityManager.unwrap(Connection.class);
        return (con != null ? new SimpleConnectionHandle(con) : null);
    }
}
