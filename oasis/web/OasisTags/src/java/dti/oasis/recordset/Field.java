package dti.oasis.recordset;

import dti.oasis.converter.Converter;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.util.ByteArray;
import dti.oasis.util.StringUtils;

import java.io.Reader;
import java.io.Serializable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/10/2008       yhchen      use BigDecimalConverter to convert BigDecimal type values
 * 07/23/2012       tcheng      135128 - added getReaderValue support for Oracle Clob type
 * 11/12/2018       wreeder     196160 - Add a field name and ability to pass a null value to getStringValue()
 * ---------------------------------------------------
 */
public class Field implements Serializable {

    public Field() {
    }

    public Field(Object value) {
        m_value = value;
    }

    public Field(String name, Object value) {
        m_name = name;
        m_value = value;
    }

    public void setName(String name) {
        this.m_name = name;
    }

    public String getName() {
        return m_name;
    }

    /**
     * Returns the value.
     */
    public Object getValue() {
        return m_value;
    }

    public boolean hasValue() {
        return m_value != null;
    }

    /**
     * Returns the string representation of the value. In other words, value.toString().
     */
    public String getStringValue() {
        if (c_stringConverter == null) {
            c_stringConverter = ConverterFactory.getInstance().getConverter(String.class);
        }
        return getStringValue(c_stringConverter);
    }

    /**
     * Get the String value of the field.
     * If the field's value is null or an empty string, the given nullValue is returned.
     *
     * @param nullValue value to return if the field does not exist, or the field's value is null or an empty string
     */
    public String getStringValue(String nullValue) {
        String stringValue = getStringValue();
        return StringUtils.isBlank(stringValue) ? nullValue : stringValue;
    }

    public String getStringValue(Converter converter) {
        return (String) converter.convert(String.class, m_value);
    }

    public String getStringValue(Converter converter, String nullValue) {
        String stringValue = (String) converter.convert(String.class, m_value);
        return StringUtils.isBlank(stringValue) ? nullValue : stringValue;
    }

    public void setStringValue(String value) {
        setValue(value);
    }

    /**
     * Returns the Byte representation of the value. In other words, value.toByte().
     */
    public Byte getByteValue() {
        if (c_byteConverter == null) {
            c_byteConverter = ConverterFactory.getInstance().getConverter(Byte.class);
        }
        return (Byte) c_byteConverter.convert(Byte.class, m_value);
    }

    public Byte getByteValue(Converter converter) {
        return (Byte) converter.convert(Byte.class, m_value);
    }

    public void setByteValue(Byte value) {
        setValue(value);
    }

    /**
     * Returns the value cast as an Integer.
     */
    public Short getShortValue() {
        if (c_shortConverter == null) {
            c_shortConverter = ConverterFactory.getInstance().getConverter(Short.class);
        }
        return (Short) c_shortConverter.convert(Short.class, m_value);
    }

    public Short getShortValue(Converter converter) {
        return (Short) converter.convert(Short.class, m_value);
    }

    /**
     * Returns the value cast as an Integer.
     */
    public Integer getIntegerValue() {
        if (c_integerConverter == null) {
            c_integerConverter = ConverterFactory.getInstance().getConverter(Integer.class);
        }
        return (Integer) c_integerConverter.convert(Integer.class, m_value);
    }

    public Integer getIntegerValue(Converter converter) {
        return (Integer) converter.convert(Integer.class, m_value);
    }

    /**
     * Returns the value cast as a Long.
     */
    public Long getLongValue() {
        if (c_longConverter == null) {
            c_longConverter = ConverterFactory.getInstance().getConverter(Long.class);
        }
        return (Long) c_longConverter.convert(Long.class, m_value);
    }

    public Long getLongValue(Converter converter) {
        return (Long) converter.convert(Long.class, m_value);
    }

    /**
     * Returns the value cast as a Float.
     */
    public Float getFloatValue() {
        if (c_floatConverter == null) {
            c_floatConverter = ConverterFactory.getInstance().getConverter(Float.class);
        }
        return (Float) c_floatConverter.convert(Float.class, m_value);
    }

    public Float getFloatValue(Converter converter) {
        return (Float) converter.convert(Float.class, m_value);
    }

    /**
     * Returns the value cast as a Double.
     */
    public Double getDoubleValue() {
        if (c_doubleConverter == null) {
            c_doubleConverter = ConverterFactory.getInstance().getConverter(Double.class);
        }

        return (Double) c_doubleConverter.convert(Double.class, m_value);
    }

    public Double getDoubleValue(Converter converter) {
        return (Double) converter.convert(Double.class, m_value);
    }

    public void setDoubleValue(Double value) {
        setValue(value);
    }

    /**
     * Returns the value cast as a BigDecimal.
     */
    public BigDecimal getBigDecimalValue() {
        if (c_bigDecimalConverter == null) {
            c_bigDecimalConverter = ConverterFactory.getInstance().getConverter(BigDecimal.class);
        }
        return (BigDecimal) c_bigDecimalConverter.convert(BigDecimal.class, m_value);
    }

    public BigDecimal getBigDecimalValue(Converter converter) {
        return (BigDecimal) converter.convert(BigDecimal.class, m_value);
    }

    public void setBigDecimalValue(BigDecimal value) {
        setValue(value);
    }

    /**
     * It is used for OBR.
     * @return
     */
    public BigDecimal getNumberValue() {
        return getBigDecimalValue();
    }

    /**
     * Returns the value cast as a Boolean.
     */
    public Boolean getBooleanValue() {
        if (c_booleanConverter == null) {
            c_booleanConverter = ConverterFactory.getInstance().getConverter(Boolean.class);
        }
        return (Boolean) c_booleanConverter.convert(Boolean.class, m_value);
    }

    public Boolean getBooleanValue(Converter converter) {
        return (Boolean) converter.convert(Boolean.class, m_value);
    }

    /**
     * Returns the value cast as a Date.
     */
    public Date getDateValue() {
        if (c_dateConverter == null) {
            c_dateConverter = ConverterFactory.getInstance().getConverter(Date.class);
        }
        return (Date) c_dateConverter.convert(Date.class, m_value);
    }

    public Date getDateValue(Converter converter) {
        return (Date) converter.convert(Date.class, m_value);
    }

    /**
     * Returns the value cast as a ByteArray.
     */
    public ByteArray getByteArrayValue() {
        return (ByteArray) m_value;
    }

    /**
     * Returns the value cast as a InputStream.
     */
    public InputStream getInputStreamValue() {
        return (InputStream) m_value;
    }

    /**
     * Returns the value cast as a OutputStream.
     */
    public OutputStream getOutputStreamValue() {
        return (OutputStream) m_value;
    }

    /**
     * Set the value of this Field.
     *
     * @param value
     */
    public void setValue(Object value) {
        m_value = value;
    }

    /**
     * Returns a hint indicating if this Field should be editable.
     * Note that this does not effect the behavior of the setValue method.
     */
    public boolean isEditable() {
        return m_isEditable;
    }

    /**
     * Sets the editable hint for use by users of this field.
     */
    public void setEditable(boolean editableHint) {
        m_isEditable = editableHint;
    }

    /**
     * Returns the value cast as a Reader.
     */
    public Reader getReaderValue() {
        return (Reader) m_value;
    }

    /**
     * Returns a string representation of this field.
     */
    public String toString() {
        return getStringValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return m_isEditable == field.m_isEditable &&
            Objects.equals(m_name, field.m_name) &&
            Objects.equals(m_value, field.m_value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_name, m_value, m_isEditable);
    }

    protected String m_name;
    private Object m_value;
    private boolean m_isEditable = false;

    private static Converter c_stringConverter;
    private static Converter c_byteConverter;
    private static Converter c_shortConverter;
    private static Converter c_integerConverter;
    private static Converter c_longConverter;
    private static Converter c_floatConverter;
    private static Converter c_doubleConverter;
    private static Converter c_booleanConverter;
    private static Converter c_dateConverter;
    private static Converter c_bigDecimalConverter;
}
