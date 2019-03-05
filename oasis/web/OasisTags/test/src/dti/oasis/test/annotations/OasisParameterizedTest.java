package dti.oasis.test.annotations;

import dti.oasis.test.junit5.provider.OasisArgumentsProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/20/2018
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ParameterizedTest
@ArgumentsSource(OasisArgumentsProvider.class)
public @interface OasisParameterizedTest {
    /**
     * The parent path for getting test parameters in config file.
     * @return
     */
    String value() default "";
}
