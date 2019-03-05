package dti.oasis.data;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * This class provides a simple data structure for holding column description meta data.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 18, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/23/2009       James       Issue#102265 Fix the StoredProcedureDAOHelper to leave
 *                              stored procedure parameters unset if there is not Field
 *                              in the inputRecord and the parameter has a default value
 *                              setting in the stored procedure definition
 * 09/21/2010       fcb         111824 - added support for Oracle XMLType
 * 06/24/2016       wdang       170303 - override equals().
 * ---------------------------------------------------
 */
public class ColumnDesc {

    public interface ColumnType {
        public static final long IN = 1;
        public static final long INOUT = 2;
        public static final long OUT = 4;
        public static final long RETURNVALUE = 5;
    }
    public interface DataTypeName {
        public static final String CHAR = "CHAR";
        public static final String VARCHAR2 = "VARCHAR2";
        public static final String LONGVARCHAR = "LONGVARCHAR";
        public static final String LONG = LONGVARCHAR;
        public static final String FLOAT = "FLOAT";
        public static final String DOUBLE = "DOUBLE";
        public static final String NUMBER = "NUMBER";
        public static final String NUMERIC = "NUMERIC";
        public static final String DECIMAL = NUMBER;
        public static final String DATE = "DATE";
        public static final String BOOLEAN = "PL/SQL BOOLEAN";
        public static final String CLOB = "CLOB";
        public static final String BLOB = "BLOB";
        public static final String RAW = "RAW";
        public static final String LONG_RAW = "LONG RAW";
        public static final String LONGVARBINARY = "LONGVARBINARY";
        public static final String REF_CURSOR = "REF CURSOR";
        public static final String TABLE = "PL/SQL TABLE";
        public static final String ROWID = "ROWID";
        public static final String BINARY_INTEGER = "BINARY_INTEGER"; 
        public static final String BINARY = "BINARY";
        public static final String OPAQUE = "58";        
    }

    public interface OracleDataType {
        public static final int OPAQUE = 58;
    }

    public interface DefaultValue {
        public static final int YES = 1;
        public static final int NO = 0;
    }

    public ColumnDesc() {
    }

    public boolean hasDefaultValue() {
        return DefaultValue.YES == defaultValue;
    }

    public ColumnDesc getCopy() {
        ColumnDesc columnDesc = new ColumnDesc();
        columnDesc.columnName = this.columnName;
        columnDesc.javaColumnName = this.javaColumnName;
        columnDesc.columnType = this.columnType;
        columnDesc.dataType = this.dataType;
        columnDesc.dataTypeName = this.dataTypeName;
        columnDesc.javaTypeName = this.javaTypeName;
        columnDesc.overLoadValue = this.overLoadValue;
        columnDesc.precision = this.precision;
        columnDesc.scale = this.scale;
        columnDesc.colNumber = this.colNumber;
        columnDesc.defaultValue = this.defaultValue;
        return columnDesc;
    }

    public boolean equals(Object o){
        if (o instanceof ColumnDesc){
            ColumnDesc e = ((ColumnDesc)o);
            return new EqualsBuilder()
                .append(this.columnName, e.columnName)
                .append(this.javaColumnName, e.javaColumnName)
                .append(this.columnType, e.columnType)
                .append(this.dataType, e.dataType)
                .append(this.dataTypeName, e.dataTypeName)
                .append(this.javaTypeName, e.javaTypeName)
                .append(this.overLoadValue, e.overLoadValue)
                .append(this.precision, e.precision)
                .append(this.scale, e.scale)
                .append(this.colNumber, e.colNumber)
                .append(this.defaultValue, e.defaultValue)
                .isEquals();
        }
        else {
            return false;
        }
    }

    public String columnName;
    public String javaColumnName;
    public int columnType; // 1 for in and 2 for inOut and 4 for out
    public int dataType; // OracleTypes.XXX
    public String dataTypeName; // REF CURSOR, NUMBER etc.
    public String javaTypeName;
    public String overLoadValue;
    public long precision;
    public long scale;
    public int colNumber;
    public int defaultValue;

    public String toString() {
        return new StringBuffer("ColumnDesc{")
            .append("columnName='").append(columnName).append("'")
            .append("; javaColumnName=").append(javaColumnName)
            .append("; columnType=").append(columnType)
            .append("; dataType=").append(dataType)
            .append("; dataTypeName='").append(dataTypeName).append("'")
            .append("; javaTypeName='").append(javaTypeName).append("'")
            .append("; overLoadValue='").append(overLoadValue).append("'")
            .append("; precision=").append(precision)
            .append("; scale=").append(scale)
            .append("; defaultValue=").append(defaultValue)
            .append("}").toString();
    }
}
