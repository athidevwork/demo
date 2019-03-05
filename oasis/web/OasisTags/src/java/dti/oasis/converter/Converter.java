package dti.oasis.converter;

/**
 * Converts the given value from it's type to an object of the specified target type.
 * <p/>
 * Extends the org.apache.commons.beanutils.Converter to handle providing a nullValue
 * that is used as the target value if the input value is null.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2006
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
public interface Converter extends org.apache.commons.beanutils.Converter {

    /**
     * Converts the given value from it's type to an object of the specified target type.
     * <p/>
     * The provided nullValue is used as the target value if the input value is null.
     */
    Object convert(Class targetType, Object inputValue, Object nullValue);

    /**
     * Converts the given value from it's type to an object of the default target type.
     * <p/>
     * The provided nullValue is used as the target value if the input value is null.
     */
    Object convert(Object inputValue, Object nullValue);

    /**
     * Converts the given value from it's type to an object of the default target type.
     * <p/>
     * If the input value is null, the returned value is null.
     */
    Object convert(Object inputValue);

    /**
     * Returns the default target type handled by this Converter.
     */
    Class getDefaultTargetType();
}

