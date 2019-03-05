package dti.oasis.pageentitlementmgr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.pageentitlementmgr.impl.PageEntitlementGroup;
import dti.oasis.pageentitlementmgr.impl.PageEntitlementManagerImpl;
import dti.oasis.util.LogUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * This PageEntitlementManager class provides abstract methods to access the page entitlements configuration information.
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
 *
 * ---------------------------------------------------
 */
public abstract class PageEntitlementManager implements ErrorHandler {

    /**
     * The bean name of a PageEntitlementManager extension if it is configured in the ApplicationContext.
     */
    public static final String BEAN_NAME = "PageEntitlementManager";

    /**
     * The name of the property that points to the page entitlement configuration file.
     */
    public static final String PROPERTY_PAGE_ENTITLEMENT_FILENAME = "pageentitlementmgr.page.entitlement.filename";

    /**
     * The name of the property that points to the page entitlement schema file.
     */
    public static final String PROPERTY_PAGE_ENTITLEMENT_SCHEMA_FILENAME = "pageentitlementmgr.page.entitlement.schema.filename";

    /**
     * Returns a synchronized static instance of Page Entitlement Manager that has the implementation information.
     */
    public synchronized static PageEntitlementManager getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(BEAN_NAME)) {
                c_instance = (PageEntitlementManager) ApplicationContext.getInstance().getBean(BEAN_NAME);
            }
            else{
                c_instance = new PageEntitlementManagerImpl();
                ((PageEntitlementManagerImpl) c_instance).verifyConfig();
                ((PageEntitlementManagerImpl) c_instance).initialize();
            }
        }
        return c_instance;
    }

    /**
     * This method returns a boolean value indicating whether any entitlement exists for the provided page URI.
     *
     * @param pageURI, the uri of the current request
     * @return boolean true, if entitlement exists for the page uri; otherwise false.
     */
    public abstract boolean hasPageEntitlementGroup(String pageURI);

    /**
     * This method returns the PageEntitlementGroup for the provided page URI.
     *
     * @param pageURI, the uri of the current request
     * @return PageEntitlementGroup for the provided page URI
     */
    public abstract PageEntitlementGroup getPageEntitlementGroup(String pageURI);

    /**
     * This method returns a collection of page entitlements configured for the provided pageURI. The lookup is
     * performed against empty pageURI tags (which means, default for all pages) and pageURI that matches the provided
     * pageURI parameter as well.
     *
     * @param pageURI, the uri of the current request
     * @return a collection of PageEntitlement bean for the provided uri
     */
    public abstract Iterator iterator(String pageURI);

    /**
   * Build useful error message
   * @param exception Some kind of SAX Parsing error/warning
   * @param level e.g. "Error", "Fatal Error", "Warning"
   * @return Error message.
   */
  protected String getSAXErrorMessage(SAXParseException exception, String level) {
      return "**Parsing "+level + "**"+
          "\nLine:\t" + exception.getLineNumber() +
          "\nURI:\t" + exception.getSystemId() +
          "\nMessage:\t"+exception.getMessage();
  }

    /**
     * Receive notification of a recoverable error.
     * @param exception Some kind of SAX parsing error.
     * @throws SAXException
     */
    public void error(SAXParseException exception) throws SAXException {
        String msg = getSAXErrorMessage(exception, "Error");
        l.warning(msg);
    }

    /**
     * Receive notification of a non-recoverable error.
     * @param exception  Some kind of fatal SAX Parsing error
     * @throws SAXException
     */
    public void fatalError(SAXParseException exception) throws SAXException {
        String msg = getSAXErrorMessage(exception, "Fatal Error");
        l.severe(getSAXErrorMessage(exception, " Fatal Error"));
        throw new SAXException(msg);
    }

    /**
     * Receive notification of a warning.
     * @param exception Some kind of recoverable SAX Parsing warning
     * @throws SAXException
     */
    public void warning(SAXParseException exception) throws SAXException {
        String msg = getSAXErrorMessage(exception, "Error");
        l.warning(msg);
    }

    private final Logger l = LogUtils.getLogger(getClass());

    private static PageEntitlementManager c_instance;
}
