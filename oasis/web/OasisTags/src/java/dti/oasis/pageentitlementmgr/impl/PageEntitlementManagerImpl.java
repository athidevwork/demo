package dti.oasis.pageentitlementmgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ApplicationLifecycleListener;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.pageentitlementmgr.PageEntitlement;
import dti.oasis.pageentitlementmgr.PageEntitlementManager;
import dti.oasis.struts.ActionHelper;
import dti.oasis.tags.IXMLGridHeaderLoader;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details for PageEntitlementManager, which is used to enforce page entitlements.
 *
 * <p>This class loads the system configuration file pageEntitlements.xml, in order enforce the page entitlements. Entitlements
 * that needs to be enforced for all pages are defined under tags that has pageURL defined as empty string. Any Page
 * specific entitlements are defined under tags with pageURL attribute's value as matching page url.</p>
 *
 * <p>The indFieldLocation dictates the location of the indFieldName, the indicator field for enforcing the entitlement. If
 * the indFieldLocation attribute's value is Page, the indFieldName must exist as the request attribute. If the
 * indFieldLocation attribute's value is Row, the indFieldName must exist in the xml island in order to enforce the
 * entitlement. The indFieldName binds the YesNoFlag as the enforcement rule. The matching pair is defined as action
 * attribute value.</p>
 *
 * <p>The configuration file has an optional attribute called gridId for each tag definition. By default, the currently
 * selected grid is used for locating indFieldName that determines the enforcement rule for entitlement. The gridId is
 * defined only if the indFieldName needs to be located in another grid for enforcing entitlements for the selected grid.</p>
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 31, 2007
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/31/2008       Joe         Modified method getPageEntitlementGroup(): to get pageEntitlementGroup by pageURI which may starts with "/".
 * ---------------------------------------------------
 */
public class PageEntitlementManagerImpl extends PageEntitlementManager implements ApplicationLifecycleListener{

    /**
     * This method returns a boolean value indicating whether any entitlement exists for the provided page URI.
     *
     * @param pageURI, the uri of the current request
     * @return boolean true, if entitlement exists for the page uri; otherwise false.
     */
    public boolean hasPageEntitlementGroup(String pageURI) {
        return hasPageEntitlementGroup(pageURI, false);
    }

    /**
     * This method returns a boolean value indicating whether any entitlement exists for the provided page URI.
     *
     * @param pageURI, the uri of the current request
     * @param isPerformExactMatch, boolean that indicates whether the pageURI match should be an exact or pattern match
     * @return boolean true, if entitlement exists for the page uri; otherwise false.
     */
    public boolean hasPageEntitlementGroup(String pageURI, boolean isPerformExactMatch) {
        boolean exists=false;
        if(StringUtils.isBlank(pageURI)) {
            exists = m_pageEntitlementGroup.containsKey(COMMON_ENTITLEMENTS_FOR_PAGES);
        } else {
            exists = m_pageEntitlementGroup.containsKey(pageURI);
            if (!exists && !isPerformExactMatch) {
                while (pageURI.indexOf("/") != -1 && !exists) {
                    pageURI = pageURI.substring(pageURI.indexOf("/")+1);
                    exists = m_pageEntitlementGroup.containsKey("/" + pageURI);
                }
            }
        }
        return exists;
    }

    /**
     * This method returns the PageEntitlementGroup for the provided page URI.
     *
     * @param pageURI, the uri of the current request
     * @return PageEntitlementGroup for the provided page URI
     */
    public PageEntitlementGroup getPageEntitlementGroup(String pageURI) {
        PageEntitlementGroup pageEntitlementGroup = null;
        if(StringUtils.isBlank(pageURI) || hasPageEntitlementGroup(pageURI) == false) {
            pageEntitlementGroup = (PageEntitlementGroup) m_pageEntitlementGroup.get(COMMON_ENTITLEMENTS_FOR_PAGES);
        } else {
            boolean exists = m_pageEntitlementGroup.containsKey(pageURI);
            if (!exists) {
                while (pageURI.indexOf("/") != -1 && !exists) {
                    pageURI = pageURI.substring(pageURI.indexOf("/")+1);
                    exists = m_pageEntitlementGroup.containsKey("/" + pageURI);
                }
            }
            if (exists) {
                if (pageURI.startsWith("/")) {
                    pageEntitlementGroup = (PageEntitlementGroup) m_pageEntitlementGroup.get(pageURI);
                } else {
                    pageEntitlementGroup = (PageEntitlementGroup) m_pageEntitlementGroup.get("/" + pageURI);
                }
            }
        }
        return pageEntitlementGroup; 
    }

    /**
     * This method returns a collection of page entitlements configured for the provided pageURI. The lookup is
     * performed against empty pageURI tags (which means, default for all pages) and pageURI that matches the provided
     * pageURI parameter as well.
     *
     * @param pageURI, the uri of the current request
     * @return a collection of PageEntitlement bean for the provided url
     */
    public Iterator iterator(String pageURI) {
        Logger l = LogUtils.enterLog(getClass(), "getPageEntitlements", new Object[]{pageURI});

        return getPageEntitlementGroup(pageURI).iterator() ;
    }

    /** Method that loads the system page entitlement configuration file. */
    private boolean loadConfig(InputStream pageEntitlementXML, InputStream pageEntitlementXSD) throws Exception{
        boolean isLoaded = false;
        setPageEntitlmentConfigDoc(null);
        try {
            DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
            // set to validate against schema
            docBuilderFac.setNamespaceAware(true);
            docBuilderFac.setValidating(true);
            docBuilderFac.setAttribute(IXMLGridHeaderLoader.JAXP_SCHEMA_LANGUAGE, IXMLGridHeaderLoader.W3C_XML_SCHEMA);
            docBuilderFac.setAttribute(IXMLGridHeaderLoader.JAXP_SCHEMA_SOURCE, pageEntitlementXSD);

            DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();
            docBuilder.setErrorHandler(this);
            setPageEntitlmentConfigDoc(docBuilder.parse(pageEntitlementXML));

            getPageEntitlmentConfigDoc().getDocumentElement().normalize();
            docBuilder = null;
            docBuilderFac = null;
            isLoaded = true;

        } catch (SAXParseException e) {
            AppException ae = new AppException(AppException.UNEXPECTED_ERROR, "SAX Parsing Exception", e);
            throw ae;
        } catch (SAXException e) {
            AppException ae = new AppException(AppException.UNEXPECTED_ERROR, "SAX Exception", e);
            throw ae;
        } catch (Exception e) {
            AppException ae = new AppException(AppException.UNEXPECTED_ERROR, "Unexpected Exception", e);
            throw ae;
        }
        // close file
        pageEntitlementXML.close();
        return isLoaded;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    /**
     * This method is triggered either by Spring or factory class whenever a new instance of PageEntitlment is requested.
     * It loads all page entitlements from the configuration file into PageEntitlementGroup collection.
     * This method is automatically triggered by either by ApplicationLifecycleAdvisor (if it is Spring configured) or
     * by getInstance() of PageEntitlementManager.
     */
    public void initialize() {
        Logger l = LogUtils.enterLog(getClass(), "initialize");
        if(!hasPageEntitlementFileName()) {
            getPageEntitlementFileName();
        }

        if(!hasPageEntitlementSchemaFileName()) {
            getPageEntitlementSchemaFileName();
        }

        //create the default page entitlement group
        PageEntitlementGroup defaultEntitlementGroup = new PageEntitlementGroup();
        m_pageEntitlementGroup.put(COMMON_ENTITLEMENTS_FOR_PAGES, defaultEntitlementGroup);

        if(hasPageEntitlementFileName()) {
            l.logp(Level.FINE, getClass().getName(), "initialize", "PageEntitlement FileName:"+getPageEntitlementFileName());
            l.logp(Level.FINE, getClass().getName(), "initialize", "PageEntitlement FileName XSD:"+getPageEntitlementSchemaFileName());
            try {
                if(loadConfig(ActionHelper.getResourceAsInputStream(getPageEntitlementFileName()), ActionHelper.getResourceAsInputStream(getPageEntitlementSchemaFileName()))) {
                    PageEntitlement pageEntitlement;

                    //Initialize the default page entitlement group with configured tags.
                    String XPathQry = "//pageEntitlement/page[@pageURI=\"\"]/tags/tag";
                    String pageURI = "";
                    org.apache.xpath.CachedXPathAPI xpathapi = new org.apache.xpath.CachedXPathAPI();
                    NodeList configuredDefaultEntitlements = xpathapi.selectNodeList(getPageEntitlmentConfigDoc(), XPathQry);
                    l.logp(Level.FINE, getClass().getName(), "initialize", "XPathQry : " + XPathQry + " Total default entitlements found :" + configuredDefaultEntitlements.getLength());
                    if (configuredDefaultEntitlements.getLength() > 0) {
                        defaultEntitlementGroup = (PageEntitlementGroup) m_pageEntitlementGroup.get(COMMON_ENTITLEMENTS_FOR_PAGES);

                        loadEntitlement(pageURI, configuredDefaultEntitlements, defaultEntitlementGroup);
                        m_pageEntitlementGroup.put(COMMON_ENTITLEMENTS_FOR_PAGES, defaultEntitlementGroup);
                    }

                    // Load the remaining configured page level entitlement groups.
                    // As part of this loading, load all default page entitlements, if the entitlement is not overridden
                    // at the page level.

                    XPathQry = "//pageEntitlement/page[@pageURI!=\"\"]";
                    NodeList configuredPageList = xpathapi.selectNodeList(getPageEntitlmentConfigDoc(), XPathQry);
                    l.logp(Level.FINE, getClass().getName(), "initialize", "XPathQry : " + XPathQry + " Total Pages:" + configuredPageList.getLength());
                    if(configuredPageList.getLength()>0) {
                        PageEntitlementGroup pageLevelEntitlementGroup = new PageEntitlementGroup();
                        for(int i=0; i<configuredPageList.getLength(); i++) {
                            pageURI = configuredPageList.item(i).getAttributes().getNamedItem("pageURI").getNodeValue();
                            l.logp(Level.FINE, getClass().getName(), "initialize", "pageURI:"+pageURI);
                            if(hasPageEntitlementGroup(pageURI, true)) {
                                pageLevelEntitlementGroup = (PageEntitlementGroup) m_pageEntitlementGroup.get(pageURI);
                            } else {
                                pageLevelEntitlementGroup = new PageEntitlementGroup();
                            }

                            XPathQry = "./tags/tag";
                            NodeList configuredEntitlementList = xpathapi.selectNodeList(configuredPageList.item(i), XPathQry);
                            l.logp(Level.FINE, getClass().getName(), "initialize", "XPathQry : " + XPathQry + " Total entitlementList(tags):" + configuredEntitlementList.getLength());

                            loadEntitlement(pageURI, configuredEntitlementList, pageLevelEntitlementGroup);

                            //At this point, we have loaded all entitlements at the page level.

                            //Now, load all default entitlements, if they are not overridden at the page level.
                            Iterator defaultPageEntitlements = ((PageEntitlementGroup) m_pageEntitlementGroup.get(COMMON_ENTITLEMENTS_FOR_PAGES)).iterator();
                            while(defaultPageEntitlements.hasNext()) {
                                pageEntitlement = (PageEntitlement) defaultPageEntitlements.next();
                                if (pageLevelEntitlementGroup.isIdConfigured(pageEntitlement.getId()) == false) {
                                    pageLevelEntitlementGroup.addPageEntitlement(pageEntitlement);
                                    l.logp(Level.FINE, getClass().getName(), "initialize", "**Added from DefaultEntitlement:"+pageEntitlement.toString());
                                }
                            }

                            m_pageEntitlementGroup.put(pageURI, pageLevelEntitlementGroup);
                        }
                    }
                }
            }  catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, e.getMessage(), e);
                l.logp(Level.SEVERE, getClass().getName(), "initialize", "'ae'" + ae);
                throw ae;
            }
        }

        if (hasConfigurationErrors()) {
            throw new ConfigurationException(getConfigurationErrors());
        }
        
        l.logp(Level.FINE, getClass().getName(), "initialize", "Page Entitlement Objects Successfully Initialized...");
        l.exiting(getClass().getName(), "initialize");
    }

    private void loadEntitlement(String pageURI, NodeList configuredDefaultEntitlements, PageEntitlementGroup defaultEntitlementGroup) {
        Logger l = LogUtils.enterLog(getClass(), "loadEntitlement");

        PageEntitlement pageEntitlement;
        for(int i=0; i<configuredDefaultEntitlements.getLength(); i++) {
            pageEntitlement = new PageEntitlement();
            pageEntitlement.setId(configuredDefaultEntitlements.item(i).getAttributes().getNamedItem("id").getNodeValue());
            pageEntitlement.setIndFieldName(configuredDefaultEntitlements.item(i).getAttributes().getNamedItem("indFieldName").getNodeValue());
            pageEntitlement.setIndFieldLocation(configuredDefaultEntitlements.item(i).getAttributes().getNamedItem("indFieldLocation").getNodeValue());
            if(configuredDefaultEntitlements.item(i).getAttributes().getNamedItem("gridId")!=null) {
                pageEntitlement.setGridId(configuredDefaultEntitlements.item(i).getAttributes().getNamedItem("gridId").getNodeValue());
            }
            pageEntitlement.setAction(configuredDefaultEntitlements.item(i).getAttributes().getNamedItem("action").getNodeValue());

            // The defaultActionForNoRows is only valid for Row type of page entitlements.
            Node defaultActionForNoRowsNode = configuredDefaultEntitlements.item(i).getAttributes().getNamedItem("defaultActionForNoRows");
            if (pageEntitlement.getIndFieldLocation().isRow()) {
                if (defaultActionForNoRowsNode == null) {
                    l.logp(Level.SEVERE, getClass().getName(), "loadEntitlement", "The defaultActionForNoRows attribute is required for all page entitlements with indFieldLocation=Row. You must add this attribute for the tag with id=\"" + pageEntitlement.getId() + "\" within the page with pageURI=\"" + pageURI + "\".");
                } else {
                    String defaultActionForNoRows = defaultActionForNoRowsNode.getNodeValue();
                    pageEntitlement.setDefaultActionForNoRows(defaultActionForNoRows);
                }
            } else {
                if (defaultActionForNoRowsNode != null ) {
                    addConfigurationError("The defaultActionForNoRows attribute is invalid for all page entitlements with indFieldLocation=Page. You must add this attribute for the tag with id=\"" + pageEntitlement.getId() + "\" within the page with pageURI=\"" + pageURI + "\".");
                }
            }

            l.logp(Level.FINE, getClass().getName(), "initialize", "pageEntitlement:"+pageEntitlement.toString());
            defaultEntitlementGroup.addPageEntitlement(pageEntitlement);
        }
        l.exiting(getClass().getName(), "loadEntitlement");
    }

    /**
     * Cleans up the instance of PageEntitlementManager.
     */
    public void terminate() {

    }

    public void verifyConfig() {
        // Leave this even if this is empty, so that the page entitlement manager can work with
        // PageEntitlementManager.getInstance()
    }

    public PageEntitlementManagerImpl() {
    }

    public Document getPageEntitlmentConfigDoc() {
        return m_pageEntitlmentConfigDoc;
    }

    public void setPageEntitlmentConfigDoc(Document pageEntitlmentConfigDoc) {
        m_pageEntitlmentConfigDoc = pageEntitlmentConfigDoc;
    }

    public boolean hasPageEntitlementFileName() {
        return (m_pageEntitlementFileName != null) ;
    }

    public boolean hasPageEntitlementSchemaFileName() {
        return (m_pageEntitlementSchemaFileName != null) ;
    }

    protected String getPageEntitlementFileName() {
        if (m_pageEntitlementFileName == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_PAGE_ENTITLEMENT_FILENAME)) {
                m_pageEntitlementFileName  = ApplicationContext.getInstance().getProperty(PROPERTY_PAGE_ENTITLEMENT_FILENAME);
            }
        }
        return m_pageEntitlementFileName;
    }

    public void setPageEntitlementFileName(String pageEntitlementFileName) {
        m_pageEntitlementFileName = pageEntitlementFileName;
    }

    public String getPageEntitlementSchemaFileName() {
        if (m_pageEntitlementSchemaFileName == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_PAGE_ENTITLEMENT_SCHEMA_FILENAME)) {
                m_pageEntitlementSchemaFileName  = ApplicationContext.getInstance().getProperty(PROPERTY_PAGE_ENTITLEMENT_SCHEMA_FILENAME);
            }
        }
        return m_pageEntitlementSchemaFileName;
    }

    public void setPageEntitlementSchemaFileName(String pageEntitlementSchemaFileName) {
        m_pageEntitlementSchemaFileName = pageEntitlementSchemaFileName;
    }

    private boolean hasConfigurationErrors() {
        return m_configurationErrors.length() > 0;
    }
    private void addConfigurationError(String errorMsg) {
        m_configurationErrors.append("\n").append(errorMsg);
    }

    private String getConfigurationErrors() {
        return m_configurationErrors.toString();
    }

    private String m_pageEntitlementFileName;
    private String m_pageEntitlementSchemaFileName;
    private Document m_pageEntitlmentConfigDoc = null;
    private Map m_pageEntitlementGroup= new HashMap();
    private StringBuffer m_configurationErrors = new StringBuffer();
    private static String COMMON_ENTITLEMENTS_FOR_PAGES = "commonPageEntitlements";
}
