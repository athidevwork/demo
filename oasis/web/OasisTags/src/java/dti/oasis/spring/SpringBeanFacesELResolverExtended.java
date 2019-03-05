package dti.oasis.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.web.jsf.el.SpringBeanFacesELResolver;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Scope;

import javax.el.ELContext;
import javax.el.ELException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   5/29/12
 *
 * @author mgitelman
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

/***
 * http://forum.springsource.org/showthread.php?123557-Spring-and-JSF-view-scope
 */
public class SpringBeanFacesELResolverExtended extends SpringBeanFacesELResolver {
    Map <String, String> scopes = new HashMap<String, String>();

    @Override
    public Object getValue(ELContext elContext, Object base, Object property) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            BeanFactory bf = getBeanFactory(elContext);
            if (bf.containsBean(beanName)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Successfully resolved variable '" + beanName + "' in Spring BeanFactory");
                }
                elContext.setPropertyResolved(true);
                Object bean = bf.getBean(beanName);

                if (bf instanceof ApplicationContext) {
                    ApplicationContext ctx = (ApplicationContext) bf;
                    if (isViewScope(ctx, beanName)) {
                        ctx.getAutowireCapableBeanFactory().autowireBean(bean);
                    }
                }
                return bean;
            }
        }
        return null;
    }

    private boolean isViewScope(ApplicationContext ctx, String beanName) {
        String scopeValue = scopes.get(beanName);

        if (scopeValue == null) {
            Scope scope = ctx.findAnnotationOnBean(beanName, Scope.class);
            if (scope != null) {
                scopeValue = scope.value();
                scopes.put(beanName, scopeValue);
            }
        }

        return "view".equals(scopeValue);
    }
}
