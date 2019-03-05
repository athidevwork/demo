package dti.oasis.app.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.util.LogUtils;
import org.aopalliance.aop.Advice;
import org.springframework.aop.TargetSource;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Auto proxy creator that identifies beans to proxy via a list of names.
 * For each define bean name, only the specified mapped method names are proxied.
 * Checks for direct, "xxx*", and "*xxx" matches for both bean and method names.
 *
 * <p>For configuration details, see the javadoc of the parent class
 * AbstractAutoProxyCreator. Typically, you will specify a list of
 * interceptor names to apply to all identified beans, via the
 * "interceptorNames" property.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 27, 2008
 *
 * @author wreeder
 * @see #setBeanNames
 * @see #setMappedNames(String[])
 * @see #isMatch
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/05/2008       Joe         Add property "m_excludeMethodNames" and use NameMatchMethodWithExclusionsPointcutAdvisor as advisor
 * ---------------------------------------------------
 */
public class BeanMethodNameAutoProxyCreator extends AbstractAutoProxyCreator {

    /**
     * Identify as bean to proxy if the bean name is in the configured list of names.
     */
    protected Object[] getAdvicesAndAdvisorsForBean(Class beanClass, String beanName, TargetSource targetSource) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAdvicesAndAdvisorsForBean", new Object[]{beanClass, beanName, targetSource});
        }

        Object[] advisors = DO_NOT_PROXY;

        if (m_beanNames != null && m_inteceptorNames != null) {
            for (Iterator it = m_beanNames.iterator(); it.hasNext();) {
                String mappedName = (String) it.next();
                if (FactoryBean.class.isAssignableFrom(beanClass)) {
                    if (!mappedName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
                        continue;
                    }
                    mappedName = mappedName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
                }
                if (isMatch(beanName, mappedName)) {
                    List advisorList = new ArrayList(m_inteceptorNames.size());

                    // Add the list of Inteceptors to the advisor
                    Iterator iter = m_inteceptorNames.iterator();
                    while (iter.hasNext()) {

                        // Create the Inteceptor
                        String interceptorName = (String) iter.next();
                        Advice interceptor = (Advice) ApplicationContext.getInstance().getBean(interceptorName);

                        // Invoke the setBeanName method on the Inteceptor if it exists.
                        try {
                            Method setBeanNameMethod = interceptor.getClass().getMethod("setBeanName", new Class[]{String.class});
                            setBeanNameMethod.invoke(interceptor, beanName);
                        }
                        catch (NoSuchMethodException e) {
                            // The interceptor does not accept the beanName property; skip setting the beanName property for this bean
                        }
                        catch (Exception e) {
                            ConfigurationException ce = new ConfigurationException("Failed to invoke setBeanName() on interceptor bean '" + interceptorName + "'", e);
                            l.throwing(getClass().getName(), "getAdvicesAndAdvisorsForBean", ce);
                            throw ce;
                        }

                        // Create a NameMatchMethodWithExclusionsPointcutAdvisor to proxy only the matching method names with the inteceptor
                        NameMatchMethodWithExclusionsPointcutAdvisor advisor = new NameMatchMethodWithExclusionsPointcutAdvisor();
                        advisor.setMappedNames(m_mappedNames);
                        advisor.setExcludeMethodNames(m_excludeMethodNames);
                        advisor.setAdvice(interceptor);

                        advisorList.add(advisor);
                    }

                    advisors = advisorList.toArray(new Advisor[advisorList.size()]);
                    break;
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAdvicesAndAdvisorsForBean", advisors);
        }
        return advisors;
    }

    /**
     * Return if the given bean name matches the mapped name.
     * <p>The default implementation checks for "xxx*", "*xxx" and "*xxx*" matches,
     * as well as direct equality. Can be overridden in subclasses.
     *
     * @param beanName   the bean name to check
     * @param mappedName the name in the configured list of names
     * @return if the names match
     * @see org.springframework.util.PatternMatchUtils#simpleMatch(String,String)
     */
    protected boolean isMatch(String beanName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, beanName);
    }

    /**
     * Set the names of the beans that should automatically get wrapped with proxies.
     * A name can specify a prefix to match by ending with "*", e.g. "myBean,tx*"
     * will match the bean named "myBean" and all beans whose name start with "tx".
     * <p><b>NOTE:</b> In case of a FactoryBean, only the objects created by the
     * FactoryBean will get proxied. This default behavior applies as of Spring 2.0.
     * If you intend to proxy a FactoryBean instance itself (a rare use case, but
     * Spring 1.2's default behavior), specify the bean name of the FactoryBean
     * including the factory-bean prefix "&": e.g. "&myFactoryBean".
     *
     * @see org.springframework.beans.factory.FactoryBean
     * @see org.springframework.beans.factory.BeanFactory#FACTORY_BEAN_PREFIX
     */
    public void setBeanNames(String[] beanNames) {
        Assert.notEmpty(beanNames, "'beanNames' must not be empty");
        m_beanNames = new ArrayList(beanNames.length);
        for (int i = 0; i < beanNames.length; i++) {
            m_beanNames.add(StringUtils.trimWhitespace(beanNames[i]));
        }
    }

    /**
     * Set the method names defining methods to match.
     * Matching will be the union of all these; if any match,
     * the pointcut matches.
     */
    public void setMappedNames(String[] mappedNames) {
        Assert.notEmpty(mappedNames, "'mappedNames' must not be empty");
        m_mappedNames = mappedNames;
    }

    /**
     * Set the common interceptors. These must be bean names in the current factory.
     * They can be of any advice or advisor type Spring supports.
     * <p>
     * If this property isn't set, there will be zero interceptors.
     */
    public void setInterceptorNames(String[] interceptorNames) {
        Assert.notEmpty(interceptorNames, "'interceptorNames' must not be empty");
        m_inteceptorNames = new ArrayList(interceptorNames.length);
        for (int i = 0; i < interceptorNames.length; i++) {
            m_inteceptorNames.add(StringUtils.trimWhitespace(interceptorNames[i]));
        }
    }

    /**
     * Set the exclude method names.
     * 
     * @param excludeMethodNames
     */
    public void setExcludeMethodNames(String[] excludeMethodNames) {
        Assert.notEmpty(excludeMethodNames, "'excludeMethodNames' must not be empty");
        m_excludeMethodNames = excludeMethodNames;
    }

    private List m_beanNames;
    private String[] m_mappedNames;
    private List m_inteceptorNames;
    private String[] m_excludeMethodNames;
    private final Logger l = LogUtils.getLogger(getClass());
}
