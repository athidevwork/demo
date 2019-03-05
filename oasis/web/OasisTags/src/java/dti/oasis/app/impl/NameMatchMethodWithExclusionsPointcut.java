package dti.oasis.app.impl;

import org.springframework.aop.support.NameMatchMethodPointcut;

import java.util.List;
import java.util.LinkedList;
import java.lang.reflect.Method;

/**
 * Pointcut bean extended from NameMatchMethodPointcut - add property excludeMethodNames
 * to support excluding methods from being advised.
 * </p>
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 30, 2008
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class NameMatchMethodWithExclusionsPointcut extends NameMatchMethodPointcut {
    /**
	 * Set the exclude method names defining methods to be excluded.
	 * If any match, the pointcut will exclude these methods names.
	 */
	public void setExcludeMethodNames(String[] excludeMethodNames) {
		if (excludeMethodNames != null) {
			for (int i = 0; i < excludeMethodNames.length; i++) {
				this.excludeMethodNames.add(excludeMethodNames[i]);
			}
		}
	}

    public boolean matches(Method method, Class targetClass) {
		for (int i = 0; i < this.excludeMethodNames.size(); i++) {
			String excludMethodName = (String) this.excludeMethodNames.get(i);
			if (excludMethodName.equals(method.getName()) || isMatch(method.getName(), excludMethodName)) {
				return false;
			}
		}
		return super.matches(method, targetClass);
	}

    private List excludeMethodNames = new LinkedList();
}
