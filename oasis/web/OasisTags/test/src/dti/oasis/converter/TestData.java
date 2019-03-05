package dti.oasis.converter;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 25, 2007
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
public class TestData {

    public TestData() {
    }

    public TestData(String stringVal1) {
        m_stringVal1 = stringVal1;
    }

    public TestData(String stringVal1, String stringVal2) {
        m_stringVal1 = stringVal1;
        m_stringVal2 = stringVal2;
    }

    public String getStringVal1() {
        return m_stringVal1;
    }

    public void setStringVal1(String stringVal1) {
        m_stringVal1 = stringVal1;
    }

    public String getStringVal2() {
        return m_stringVal2;
    }

    public void setStringVal2(String stringVal2) {
        m_stringVal2 = stringVal2;
    }

    public String toString() {
        return "TestData{" +
            "m_stringVal1='" + m_stringVal1 + '\'' +
            ", m_stringVal2='" + m_stringVal2 + '\'' +
            '}';
    }

    private String m_stringVal1;
    private String m_stringVal2;
}
