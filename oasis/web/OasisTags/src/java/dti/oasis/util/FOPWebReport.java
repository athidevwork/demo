package dti.oasis.util;

import dti.oasis.app.AppException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FOP Implementation of PDF Generation
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 10, 2005
 *
 * @author jbe
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/23/2009       Leo         Issue 95048, the parameter "xslFileName" passed
 *                              to these functions will be changed from "filePath/filename" to "filename".
 * 07/01/2009       kshen       Changed method FOPFile to support using dtd in xsl file.
 * 05/03/2011       kshen       Changed method FOPFile to using WebReportTemplate.getTemplateAsInputStream
 *                              to load xsl file.
 * ---------------------------------------------------
*/

public class FOPWebReport {
    /**
     * Uses FOP to generate PDF file from XML and XSL
     *
     * @param xml         report data in XML
     * @param xslFileName filename of XSL that contains report format
     * @param pdfFileName filename of PDF report
     * @throws Exception
     */
    public void FOPFile(String xml, String xslFileName, String pdfFileName)
        throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "FOPFile",
            new Object[]{xml, xslFileName, pdfFileName});
        FileOutputStream fos = null;

        // configure foUserAgent as desired
        FOUserAgent foUserAgent = getFopFactory().newFOUserAgent();
        foUserAgent.setBaseURL(getFopFactory().getBaseURL());

        try {
            File pdfFile = new File(pdfFileName);

            //Setup the OutputStream for FOP
            fos = new FileOutputStream(pdfFile);

            // Construct fop with desired output format
            Fop fop = getFopFactory().newFop(MimeConstants.MIME_PDF, foUserAgent, fos);

            //Make sure the XSL transformation's result is piped through to FOP
            javax.xml.transform.Result res = new SAXResult(fop.getDefaultHandler());

            //Setup XML input
            Source src = new StreamSource(new StringReader(xml));

            //Setup Transformer
            Source xsltSrc = new StreamSource(WebReportTemplate.getTemplateAsInputStream(xslFileName));
            String systemId = Thread.currentThread().getContextClassLoader().getResource("/dti/templates/" +xslFileName).toString();//fUrl.toExternalForm();
            xsltSrc.setSystemId(systemId);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(xsltSrc);

            //Start the transformation and rendering process
            transformer.transform(src, res);
            l.exiting(getClass().getName(), "FOPFile");
        }
        finally {
            if (fos != null) fos.close();
        }
    }

    /**
     * Uses FOP to generate PDF stream from XML and XSL
     *
     * @param xml         report data in XML
     * @param xslFileName filename of XSL that contains report format
     * @return PDF stream
     * @throws Exception
     */
    public ByteArrayOutputStream FOPStream(String xml, String xslFileName)
        throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "FOPStream",
            new Object[]{xml, xslFileName});

        // configure foUserAgent as desired
        FOUserAgent foUserAgent = getFopFactory().newFOUserAgent();
        foUserAgent.setBaseURL(getFopFactory().getBaseURL());

        //Setup the OutputStream for FOP
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // Construct fop with desired output format
        Fop fop = getFopFactory().newFop(MimeConstants.MIME_PDF, foUserAgent, bos);

        //Make sure the XSL transformation's result is piped through to FOP
        javax.xml.transform.Result res = new SAXResult(fop.getDefaultHandler());

        //Setup XML input
        Source src = new StreamSource(new StringReader(xml));

        //Setup Transformer
        Source xsltSrc = new StreamSource(WebReportTemplate.getTemplateAsInputStream(xslFileName));
        String systemId = Thread.currentThread().getContextClassLoader().getResource("/dti/templates/" +xslFileName).toString();//fUrl.toExternalForm();
        xsltSrc.setSystemId(systemId);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(xsltSrc);

        //Start the transformation and rendering process
        transformer.transform(src, res);
        l.exiting(getClass().getName(), "FOPStream");
        return bos;
    }

    /**
     * Return the single instance of FopFactory
     *
     * @return instance of FopFactory
     */
    private FopFactory getFopFactory() {
        return c_fopFactory;
    }

    /**
     * Set base URL for FopFactory which is needed for FOP get current relative path for images.
     *
     * @param baseURL Base dir
     */
    public static synchronized void setBaseURL(String baseURL) {
        Logger l = LogUtils.getLogger(FOPWebReport.class);
        if (l.isLoggable(Level.FINER)) {
            l.entering(FOPWebReport.class.getName(), "setBaseURL", new Object[]{baseURL,});
        }

        try {
            c_fopFactory.setBaseURL(baseURL);
        }
        catch (MalformedURLException e) {
            AppException ae = new AppException("Invalid Base URL.", e);
            l.throwing(FOPWebReport.class.getName(), "setBaseURL", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(FOPWebReport.class.getName(), "setBaseURL");
        }
    }

    private static FopFactory c_fopFactory = FopFactory.newInstance();
}
