package dti.oasis.converter;

/**
 * Converts the given value from it's type to an object of the specified target type.
 * <p/>
 * By default, passes <t>null</t> as the nullValue to use if the input value is null.
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
public abstract class BaseConverter implements Converter {

    /**
     * This method passes <t>null</t> as the nullValue to use if the input value is null.
     */
    public Object convert(Class targetType, Object inputValue) {
        return convert(targetType, inputValue, null);
    }

    /**
     * Converts the given value from it's type to an object of the default target type.
     * <p/>
     * The provided nullValue is used as the target value if the input value is null.
     */
    public Object convert(Object inputValue, Object nullValue) {
        return convert(getDefaultTargetType(), inputValue, nullValue);
    }

    /**
     * Converts the given value from it's type to an object of the default target type.
     * <p/>
     * If the input value is null, the returned value is null.
     */
    public Object convert(Object inputValue) {
        return convert(getDefaultTargetType(), inputValue, null);
    }
}
