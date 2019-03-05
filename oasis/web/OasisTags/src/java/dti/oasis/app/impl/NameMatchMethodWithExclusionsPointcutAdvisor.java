package dti.oasis.app.impl;

import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractGenericPointcutAdvisor;

/**
 * Convenient class for name-match method pointcuts that hold an Advice,
 * making them an Advisor.
 * </p>
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 30, 2008
 *
 * @author jshen
 * @see NameMatchMethodWithExclusionsPointcut
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class NameMatchMethodWithExclusionsPointcutAdvisor extends AbstractGenericPointcutAdvisor {

    public NameMatchMethodWithExclusionsPointcutAdvisor() {
    }

    public NameMatchMethodWithExclusionsPointcutAdvisor(Advice advice) {
        setAdvice(advice);
    }


    /**
     * Set the {@link ClassFilter} to use for this pointcut.
     * Default is {@link ClassFilter#TRUE}.
     *
     * @see NameMatchMethodWithExclusionsPointcut#setClassFilter
     */
    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    /**
     * Convenience method when we have only a single method name to match.
     * Use either this method or <code>setMappedNames</code>, not both.
     *
     * @see #setMappedNames
     * @see NameMatchMethodWithExclusionsPointcut#setMappedName
     */
    public void setMappedName(String mappedName) {
        this.pointcut.setMappedName(mappedName);
    }

    /**
     * Set the method names defining methods to match.
     * Matching will be the union of all these; if any match,
     * the pointcut matches.
     *
     * @see NameMatchMethodWithExclusionsPointcut#setMappedNames
     */
    public void setMappedNames(String[] mappedNames) {
        this.pointcut.setMappedNames(mappedNames);
    }

    /**
     * Add another eligible method name, in addition to those already named.
     * Like the set methods, this method is for use when configuring proxies,
     * before a proxy is used.
     *
     * @param name name of the additional method that will match
     * @return this pointcut to allow for multiple additions in one line
     * @see NameMatchMethodWithExclusionsPointcut#addMethodName
     */
    public NameMatchMethodWithExclusionsPointcut addMethodName(String name) {
        return (NameMatchMethodWithExclusionsPointcut) this.pointcut.addMethodName(name);
    }

    /**
     * Set the exclude method names defining methods to be excluded.
     * If any match, the pointcut will exclude these method names.
     *
     * @see NameMatchMethodWithExclusionsPointcut#setExcludeMethodNames
     */
    public void setExcludeMethodNames(String[] excludeMethodNames) {
        this.pointcut.setExcludeMethodNames(excludeMethodNames);
    }


    public Pointcut getPointcut() {
		return this.pointcut;
	}

    private final NameMatchMethodWithExclusionsPointcut pointcut = new NameMatchMethodWithExclusionsPointcut();
}
