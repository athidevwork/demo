package dti.oasis.test.junit5.provider.impl;

import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/8/2018
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
public interface ParameterValueResolver<T> {
    T resolve(List<URL> resources, Parameter parameter);

    T resolve(Map parentNode, Parameter parameter);
}
