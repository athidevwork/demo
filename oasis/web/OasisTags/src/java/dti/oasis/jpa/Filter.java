package dti.oasis.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is used to define filter type on each entity class.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/18/12
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Filter {

    public enum Type {EQUAL, LIKE, GREATER, GREATEROREQUAL, LESS, LESSOREQUAL, STARTSWITH, IN, NOTIN, JOIN, LEFTJOIN, RIGHTJOIN};

    Type type();

    Type subType() default Type.EQUAL;

    Class[] foreignTable() default String.class;

    java.lang.String foreignColumn() default "";

    java.lang.String[] sourceColumn() default "";

    java.lang.String returnColumn() default "";
}
