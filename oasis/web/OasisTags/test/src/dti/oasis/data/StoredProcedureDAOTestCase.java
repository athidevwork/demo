package dti.oasis.data;

import dti.oasis.test.TestCase;
import dti.oasis.app.ApplicationContext;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DBPool;

import java.sql.SQLException;
import java.util.Date;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 9, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class StoredProcedureDAOTestCase extends TestCase {

    public StoredProcedureDAOTestCase(String testCaseName) {
        super(testCaseName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        initializeApplicationConfiguration("OasisTags", "dti/oasisTagsTestConfig.xml");
    }

    public void testGetSchemaOwner() {
    }

    public void testLoadProcedureColumnMetaData() throws SQLException {
        DBPool.resetConnectionPools();
        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("test1");

        Record input = new Record();
//        input.setFieldValue("binaryInt", new Integer(1));
//        input.setFieldValue("char", new Character('c'));
//        input.setFieldValue("jmp", new Double(1.1));
//        input.setFieldValue("date", new Date(System.currentTimeMillis()));
//        input.setFieldValue("float", new Float(1.2));
//        input.setFieldValue("long", new Long(1));
    //        input.setFieldValue("longRaw", "Long Raw string");
//        input.setFieldValue("number", new Long(1));
//        input.setFieldValue("number", new Double(1.3));
//        input.setFieldValue("raw", "Raw string");
    //        input.setFieldValue("refCursor", "not supported"); // Not Supported as input
//        input.setFieldValue("varchar2", "VARCHAR2 string");

        // http://www.oracle.com/technology/tech/java/sqlj_jdbc/htdocs/jdbc_faq.htm#34_05
        // It is not feasible for Oracle JDBC drivers to support calling arguments or return values of the
        //   PL/SQL types TABLE (now known as indexed-by tables), RESULT SET, RECORD, or BOOLEAN
//        input.setFieldValue("boolean", Boolean.TRUE);

//            input.setFieldValue("rowid", "1"); // Not Supported in Oracle JDBC as a procedure parameter

        RecordSet result = spDAO.execute(input);
        System.out.println("result.getSize() = " + result.getSize());

    }
}
