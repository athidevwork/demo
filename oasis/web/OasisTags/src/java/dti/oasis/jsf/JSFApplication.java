package dti.oasis.jsf;

import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.http.Module;
import dti.oasis.security.J2EESecuritySelector;
import dti.oasis.util.*;
import org.springframework.context.annotation.Scope;

import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/28/12
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
@Named("global")
@Scope("singleton")
public class JSFApplication implements Serializable {

    public String getDateFormat() {
        if (dateFormat == null) {
            dateFormat = FormatUtils.getDateFormatForDisplayString();
        }
        if("DD/MON/YYYY".equals(dateFormat.toUpperCase())){
            dateFormat = "dd/MMM/yyyy";
        }
        return dateFormat;
    }

    public String getDateTimeFormat() {
        if (dateTimeFormat == null) {
            dateTimeFormat = FormatUtils.getDateTimeFormatForDisplayString();
        }
        return dateTimeFormat;
    }

    public String getDateTimeNoSecFormat() {
        if (dateTimeNoSecFormat == null) {
            dateTimeNoSecFormat = FormatUtils.getDateTimeNoSecFormatForDisplayString();
        }
        return dateTimeNoSecFormat;
    }

    public ApplicationContext getDtiApplicationContext(){
        if (dtiApplicationContext == null) {
            dtiApplicationContext = ApplicationContext.getInstance();
        }
        return dtiApplicationContext;
    }

    public String getEnvironmentName(){
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        environmentName = SysParmProvider.getInstance().getSysParm(request, "ENVIRONMENTNAME");
        if(StringUtils.isBlank(environmentName))
            environmentName = getApplicationProperty("environmentName");
        return environmentName;
    }

    public String getApplicationProperty(String key){
        return getApplicationProperty(key, null);
    }
    public String getApplicationProperty(String key, String defaultValue){
        return getDtiApplicationContext().getProperty(key, defaultValue);
    }
    
    public boolean getDisallowPasswordChange() {
        return YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("disallowPasswordChange")).booleanValue();

    }
    
    public String getPasswordChangeUrl() {
        String externalPasswordChangeUrl = ApplicationContext.getInstance().getProperty("external.password.change.URL");
        if (StringUtils.isBlank(externalPasswordChangeUrl)) {
            return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/core/changepassword.jsp";
        }
        else {
            return externalPasswordChangeUrl;
        }
    }

    public String getEnvPath(){
        return Module.getEnvPath((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest());
    }

    /**
     * renew session
     * @param event
     */
    public void renewSession(ActionEvent event) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "renewSession", new Object[]{event});
        }
        // do nothing
        l.exiting(getClass().getName(), "renewSession");
    }

    private String dateFormat;
    private String dateTimeFormat;
    private String dateTimeNoSecFormat;
    private ApplicationContext dtiApplicationContext;
    private String envPath;
    private String applicationTitle;
    private String environmentName;

    private static final long serialVersionUID = 1L;

    // Error Handling: Simulate
    public String actionThrowNullPointer() {
        throw new NullPointerException("This is my null pointer exception!");
    }

    public String actionWrappedException() {
        Throwable t = new IllegalStateException("This is wrapped illegal state exception!");
        throw new FacesException(t);
    }

    public String actionSimulateViewExpiredException() {
        throw new ViewExpiredException("This is simulated ViewExpiredException",
                FacesContext.getCurrentInstance().getViewRoot().getViewId());
    }

    public String actionSimulate500Error()throws Exception{
        URL url = new URL ("http://someurl.doesnotexist.com");
        URLConnection connection = url.openConnection();
        connection.connect();
        HttpURLConnection httpConnection = (HttpURLConnection) connection;
        int code = httpConnection.getResponseCode();
        //do something with the code
        throw new FacesException("actionSimulate500Error");
    }

    public void throwRuntimeException() {
        try{
            throw new RuntimeException("peek-a-boo");
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Caught peek-a-boo",e);
        }
    }

    public void throwSQLException() throws SQLException {
        throw new SQLException("DB fail");
    }

    /**
     * BEGIN: Methods to Test Error Handling
     */

    public void removeJSessionId(){
        System.out.println("   ***** LOOKING UP SESSION ID   *****   ");
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String id = null;
        javax.servlet.http.Cookie [] cookies = request.getCookies();
        if(cookies != null){
            for(int x =0; x<cookies.length;x++){
                javax.servlet.http.Cookie cookie = cookies[x];
                String name = cookie.getName();
                if(name.equals("JSESSIONID")){
                    id = cookie.getValue();
                    System.out.println("   ***** FOUND SESSION ID: "+id+"   *****   ");
                }
                cookies[x] = null;
                System.out.println("   ***** SET SESSION ID TO NULL   *****   ");
            }
        }

    }

    public void invalidateSession(){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

        if (session != null) {
            System.out.println("   ***** FOUND SESSION   *****   ");
            session.invalidate();
            System.out.println("   ***** SESSION INVALIDATED   *****   ");
        } else {
            System.out.println("   ***** SESSION NOT FOUND   *****   ");
        }
    }

    public void setSessionTimeout(int sessionTimeout){
        System.out.println("   ***** SETTING SESSION INTERVAL   *****   ");
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.setMaxInactiveInterval(sessionTimeout);
        System.out.println("   ***** SET SESSION INTERVAL TO: "+sessionTimeout+"   *****   ");
    }

    public void logout(){
        System.out.println("   ***** LOGGING OUT   *****   ");
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        J2EESecuritySelector.getJ2EESecurityFactory().getInstance().logout(request);
        System.out.println("   ***** LOGGED OUT   *****   ");
    }

    public boolean isBrowserIE(){
        boolean result = false;
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        result = BrowserUtils.isIE(request.getHeader("user-agent"));
        return result;
    }

    public String getIEVersion(){
        String result = "";
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        result = BrowserUtils.getIEVersion(request.getHeader("user-agent"));

        return result;
    }

    /**
     * END: Methods to Test Error Handling
     */
}
