package dti.oasis.util;

import org.apache.struts.util.RequestUtils;

import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.URL;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.AppException;

/**
 * Created by IntelliJ IDEA.
 * User: gjlong
 * Date: Apr 27, 2009
 * Time: 10:15:05 AM
 * To change this template use File | Settings | File Templates.
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/01/2009       kshen       Added method getTemplateFileUrl.
 * ---------------------------------------------------
*/
public class WebReportTemplate {

    /** method to return the content of template file and return as string,
     *
     * @param templateFileName: name of the template file
     * @return String represented as the template content
     */
   public static String getTemplateAsString(String templateFileName) {
        Logger lggr = LogUtils.enterLog(Class.class, "getTemplateAsString", new Object[]{templateFileName});
        StringBuffer templateContent = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getTemplateAsInputStream(templateFileName)));
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                templateContent.append(line).append("\n");
            }
            bufferedReader.close();
        }
        catch (IOException io) {
            lggr.throwing(Class.class.getName(), "getTemplateAsString",io);
            throw new AppException("Unable to get the content for the template file: " + templateFileName, io);
        }
        finally {
            bufferedReader = null;
        }
        lggr.exiting(Class.class.getName() + ".getTemplateAsString", templateContent.toString());
        return templateContent.toString();

    }

    /**  method to return the content of template file and return as inputstream,
     *
     * @param templateFileName  name of the template file
     * @return
     */
   public static InputStream getTemplateAsInputStream(String templateFileName){
       Logger lggr = LogUtils.enterLog(Class.class,"getTemplateAsInputStream", new Object[]{templateFileName});

       InputStream resource = null;
       String templateFullPath="";
       // Check in the Server's home directory based on template.root.custom.directory property configured
       try {

           String customTemplateDirectory = ApplicationContext.getInstance().getProperty("template.root.custom.directory","dti/templates/");
           templateFullPath = (customTemplateDirectory.endsWith("/")? customTemplateDirectory+templateFileName:customTemplateDirectory+"/"+templateFileName);
           resource = new FileInputStream(templateFullPath);
       } catch (FileNotFoundException e) {
             lggr.fine("template file '" + templateFileName + "' not found from server's home directory, searching trhu classpath now..");
       }

        if (resource == null) {
            // Check in the Web Application's Classpath based on property template.root.classpath.directory
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = RequestUtils.class.getClassLoader();
            }
            String baseTemplateDirectory = ApplicationContext.getInstance().getProperty("template.root.classpath.directory","dti/templates/");
            templateFullPath = (baseTemplateDirectory.endsWith("/")? baseTemplateDirectory+templateFileName:baseTemplateDirectory+"/"+templateFileName);
            resource = classLoader.getResourceAsStream(templateFullPath);
        }

        if (resource == null) {
           lggr.severe("template file '"+templateFileName + "' not found from classpath.");
           throw new AppException("Template file can not be identified:"+templateFileName);
        }

        return resource;
    }

    /**
     * Get the template file url path.
     * @param templateFileName
     * @return
     */
    public static String getTemplateFileUrl(String templateFileName) {
        Logger l = LogUtils.getLogger(WebReportTemplate.class);

        if (l.isLoggable(Level.FINER)) {
            l.entering(WebReportTemplate.class.getName(), "getTemplateFileUrl", new Object[]{templateFileName,});
        }

        String customTemplateDirectory = ApplicationContext.getInstance().getProperty("template.root.custom.directory", "dti/templates/");
        String templateFullPath = (customTemplateDirectory.endsWith("/") ? customTemplateDirectory + templateFileName : customTemplateDirectory + "/" + templateFileName);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(templateFullPath);
        String filename = url.getFile();
        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(WebReportTemplate.class.getName(), "getTemplateFileUrl",filename);
        }
        return  filename;
    }
}
