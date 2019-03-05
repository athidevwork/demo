package dti.oasis.app.impl;

import dti.oasis.util.LogUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Standalone XML application context, taking the context definition files
 * from a URL, file system or classpath.
 * This class should only be used by the SpringApplicationContext class to load Spring configuration for the
 * ApplicationContext class.
 * <p/>
 * If the location is a file system resource,
 * it can be fully qualified, or relative to the VM working directlry.
 * <p/>
 * The config location defaults can be overridden via <code>setConfigLocations</code>,
 * Config locations can either denote concrete files like "/myfiles/context.xml"
 * or Ant-style patterns like "/myfiles/*-context.xml" (see PathMatcher javadoc for
 * pattern details).
 * <p/>
 * Note: In case of multiple config locations, later bean definitions will
 * override ones defined in earlier loaded files. This can be leveraged to
 * deliberately override certain bean definitions via an extra XML file.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
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
public class XmlApplicationContext extends AbstractXmlApplicationContext {

    /**
     * Create a new XmlApplicationContext with no config locations and no parent
     * This class should only be used by the SpringApplicationContext class to load Spring configuration for the
     * ApplicationContext class.
     */
    public XmlApplicationContext() {
        Logger l = LogUtils.enterLog(getClass(), "XmlApplicationContext");
        l.exiting(getClass().getName(), "XmlApplicationContext");
    }

    /**
     * Create a new XmlApplicationContext, loading the definitions
     * from the given XML file and automatically refreshing the context.
     *
     * @param configLocation file path
     */
    public XmlApplicationContext(String configLocation) throws BeansException {
        this(new String[]{configLocation});

        Logger l = LogUtils.enterLog(getClass(), "XmlApplicationContext", new Object[]{configLocation});
        l.exiting(getClass().getName(), "XmlApplicationContext");
    }

    /**
     * Create a new XmlApplicationContext, loading the definitions
     * from the given XML files and automatically refreshing the context.
     *
     * @param configLocations array of file paths
     */
    public XmlApplicationContext(String[] configLocations) throws BeansException {
        this(configLocations, true);

        Logger l = LogUtils.enterLog(getClass(), "XmlApplicationContext", new Object[]{configLocations});
        l.exiting(getClass().getName(), "XmlApplicationContext");
    }

    /**
     * Create a new XmlApplicationContext, loading the definitions
     * from the given XML files.
     *
     * @param configLocations array of file paths
     * @param refresh         whether to automatically refresh the context,
     *                        loading all bean definitions and creating all singletons.
     *                        Alternatively, call refresh manually after further configuring the context.
     * @see #refresh
     */
    public XmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
        Logger l = LogUtils.enterLog(getClass(), "XmlApplicationContext", new Object[]{configLocations, String.valueOf(refresh)});

        this.m_configLocations = configLocations;
        if (refresh) {
            refresh();
        }

        l.exiting(getClass().getName(), "XmlApplicationContext");
    }

    /**
     * Create a new XmlApplicationContext with the given parent,
     * loading the definitions from the given XML files and automatically
     * refreshing the context.
     *
     * @param configLocations array of file paths
     * @param parent          the parent context
     */
    public XmlApplicationContext(String[] configLocations, ApplicationContext parent) throws BeansException {
        this(configLocations, true, parent);

        Logger l = LogUtils.enterLog(getClass(), "XmlApplicationContext", new Object[]{configLocations, parent});
        l.exiting(getClass().getName(), "XmlApplicationContext");
    }

    /**
     * Create a new XmlApplicationContext with the given parent,
     * loading the definitions from the given XML files.
     *
     * @param configLocations array of file paths
     * @param refresh         whether to automatically refresh the context,
     *                        loading all bean definitions and creating all singletons.
     *                        Alternatively, call refresh manually after further configuring the context.
     * @param parent          the parent context
     * @see #refresh
     */
    public XmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent) throws BeansException {
        super(parent);
        Logger l = LogUtils.enterLog(getClass(), "XmlApplicationContext", new Object[]{configLocations, String.valueOf(refresh), parent});

        this.m_configLocations = configLocations;
        if (refresh) {
            refresh();
        }

        l.exiting(getClass().getName(), "XmlApplicationContext");
    }

    /**
     * Load this XmlApplicationContext from the given XML file and automatically refreshing the context.
     *
     * @param configLocation file path
     */
    public void load(String configLocation) {
        Logger l = LogUtils.enterLog(getClass(), "load", new Object[]{configLocation});

        this.load(new String[]{configLocation});

        l.exiting(getClass().getName(), "load");
    }

    /**
     * Load this XmlApplicationContext from the given XML files and automatically refreshing the context.
     *
     * @param configLocations array of file paths
     */
    public void load(String[] configLocations) {
        Logger l = LogUtils.enterLog(getClass(), "load", new Object[]{configLocations});

        this.m_configLocations = configLocations;
        refresh();

        l.exiting(getClass().getName(), "load");
    }

    protected String[] getConfigLocations() {
        Logger l = LogUtils.enterLog(getClass(), "getConfigLocations");
        l.exiting(getClass().getName(), "getConfigLocations", m_configLocations);

        return m_configLocations;
    }

    /**
     * Resolve resource paths as file system paths.
     * <p>Note: This differs from the FileSystemXmlApplicationContext in that fully qualified
     * path names are treated as such, not relative to the VM working directory.
     *
     * @param path path to the resource
     * @return Resource handle
     * @see org.springframework.context.support.FileSystemXmlApplicationContext#getResourceByPath
     */
    protected Resource getResourceByPath(String path) {
        Logger l = LogUtils.enterLog(getClass(), "getResourceByPath", new Object[]{path});

        Resource resource = null;
        try {
            l.logp(Level.FINE, getClass().getName(), "getResourceByPath", "Trying to load " + path + " as a file...");
            resource = new FileSystemResource(path);
            resource.getInputStream();
        } catch (IOException e) {
            l.logp(Level.FINE, getClass().getName(), "getResourceByPath", "Failed to load " + path + " as a file. Trying to load it from the ClassPath...");
            resource = new ClassPathResource(path, getClassLoader());
            l.logp(Level.FINE, getClass().getName(), "getResourceByPath", "Successfully loaded "+ path + " from the Classpath.");
        }

        l.exiting(getClass().getName(), "getResourceByPath", resource);

        return resource;
    }

    private String[] m_configLocations;
}
