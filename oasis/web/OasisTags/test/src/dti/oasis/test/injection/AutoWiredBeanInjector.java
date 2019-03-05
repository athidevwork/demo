package dti.oasis.test.injection;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.test.annotations.OasisAutoWired;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/27/2018
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
public class AutoWiredBeanInjector extends BeanInjector {
    @Override
    public void injectDependency(Object testInstance) {
        injectDependency(testInstance, testInstance.getClass());
    }

    private void injectDependency(Object testInstance, Class<?> clazz) {
        if (!clazz.equals(Object.class)) {
            // Inject beans of the current class.
            injectAutoWiredBeans(testInstance, clazz);

            // Inject beans for the super class.
            injectDependency(testInstance, clazz.getSuperclass());
        }
    }

    private void injectAutoWiredBeans(Object testInstance, Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            // Only support non-static field.
            if (!Modifier.isStatic(field.getModifiers()) &&
                    field.isAnnotationPresent(OasisAutoWired.class)) {
                OasisAutoWired annotation = field.getAnnotation(OasisAutoWired.class);
                // Inject beans by annotation.
                injectAutoWiredBean(testInstance, field, annotation);
            }
        }
    }

    private void injectAutoWiredBean(Object testInstance, Field field, OasisAutoWired annotation) {
        Object bean;

        if (annotation.value() .equals("")) {
            // If the value property of the annotation is empty, get bean by field type.
            bean = ApplicationContext.getInstance().getBean(field.getType());
        } else {
            // If the value property of the annotation is not empty, get bean by bean name.
            bean = ApplicationContext.getInstance().getBean(annotation.value());
        }

        if (bean == null) {
            throw new ConfigurationException("Could not find bean for the field: " + field.getName());
        }

        try {
            field.setAccessible(true);
            field.set(testInstance, bean);
        } catch (IllegalAccessException e) {
            throw new AppException("Could not inject bean to field: " + field.getName());
        }
    }
}
