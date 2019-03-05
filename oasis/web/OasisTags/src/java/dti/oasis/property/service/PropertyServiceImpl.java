package dti.oasis.property.service;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.impl.SpringApplicationContext;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.messagemgr.MessageSourceResourceBundle;
import dti.oasis.messagemgr.ReloadableMessageSource;
import dti.oasis.util.LogUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;


import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * <p/>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date: 11/10/2015
 *
 * @author tmarius
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
@Named
public class PropertyServiceImpl implements PropertyService {

    @Override
    public Map<String, String> getProperties(){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProperties");
        }

        HashMap<String, String> data = new HashMap<String, String>();

        ApplicationContext appContext = ApplicationContext.getInstance();
        Enumeration props = appContext.getProperties().propertyNames();
        while (props.hasMoreElements()) {
            String key = (String) props.nextElement();
            String value = appContext.getProperty(key);
            data.put(key, value);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getProperties");
        }
        return data;
    }


    @Override
    public Map<String, String> getMessageResourceProperties(){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllResourceProperties");
        }

//        MessageSource messageSource =
//                (MessageSource) ApplicationContext.getInstance().getBean(SpringApplicationContext.MESSAGE_SOURCE_BEAN_NAME);

//        ReloadableResourceBundleMessageSource bundleMessageSource = (ReloadableResourceBundleMessageSource) ApplicationContext.getInstance().getBean(SpringApplicationContext.MESSAGE_SOURCE_BEAN_NAME);
        ReloadableMessageSource bundleMessageSource = (ReloadableMessageSource) ApplicationContext.getInstance().getBean(SpringApplicationContext.MESSAGE_SOURCE_BEAN_NAME);

        MessageSourceResourceBundle messageSourceResourceBundle = new MessageSourceResourceBundle(bundleMessageSource, Locale.getDefault());
        HashMap<String, String> data = new HashMap<String, String>();

//        ApplicationContext appContext = ApplicationContext.getInstance();
//        String propertyFileName = appContext.getProperty("message.property.file","dti/applicationResources");

//        MessageManager messageManager = MessageManager.getInstance();
//        Enumeration<String> keys = messageManager.getResourceBundle().getBundle("dti/applicationResources-eAdmin", Locale.getDefault()).getKeys();
        Enumeration<String> keys = bundleMessageSource.getKeys();


        for (Enumeration<String> iter = keys; iter.hasMoreElements();) {
            String key = iter.nextElement();
            String value = messageSourceResourceBundle.getString(key);
            System.out.println("Key: "+key+" Value: "+value);
            data.put(key, value);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllResourceProperties");
        }
        return data;
    }

    @Override
    public Map<String, String> getPropertiesFor(List<String> keys){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPropertiesFor");
        }

        MessageSource messageSource =
                (MessageSource) ApplicationContext.getInstance().getBean(SpringApplicationContext.MESSAGE_SOURCE_BEAN_NAME);

        HashMap<String, String> data = new HashMap<String, String>();

        ApplicationContext appContext = ApplicationContext.getInstance();
        for (String key : keys) {
            String value = appContext.getProperty(key);
            if (value == null || (value != null && value.isEmpty())) {
                if (key.equalsIgnoreCase("dti.technicalSupport.emailAddress")) {    // map to OASIS name
                    String messageKey = "label.login.support.mailto";
                    value = messageSource.getMessage(messageKey, null, Locale.getDefault());
                }
            }
            data.put(key, value);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPropertiesFor");
        }
        return data;
    }


    @Override
     public String getProperty(String name) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProperty", new Object[]{name});
        }

        ApplicationContext appContext = ApplicationContext.getInstance();
        String value = appContext.getProperty(name);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getProperty", name);
        }
        return value;
    }

}
