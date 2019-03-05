package dti.oasis.http;

import dti.oasis.util.LogUtils;
import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

/**
 * This class provides convenient access to the ContextPath of the current or desired Module.
 * As defined in the HttpServletRequest, the ContextPath is the portion of the request URI
 * that indicates the context of the request. The context path always comes first in a request URI.
 * The path starts with a "/" character but does not end with a "/" character.
 * Use this class to get the ContextPath of any OASIS Module.
 * <p/>
 * If the application was deployed as an EAR with modules as contained Web Applications (WARs),
 * the resulting ContextPath includes the application name before the module name,
 * For example, if the PM module were deployed as a part of the EAR application named "M1",
 * calling getPMPath() will return "/M1/PM".
 * <p/>
 * Alternatively, if the application is deployed with all modules copied into it's Web Application,
 * the resulting ContextPath is the current ContextPath.
 * For example, if CM were deployed with CIS copied into it's Web Application, calling getCISPath() will return "/CM".
 * <p/>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 13, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/21/2006       sxm         Quick & Dirty fix of getModulePath() to handle eComp/WCPHS as well.
 * 06/12/2008       mlm         Added logic to get FM Module paths.
 * 08/13/2008       joe         Fixed bug of getting FM module path.
 * ---------------------------------------------------
 */
public class Module {

    /**
     * The the context relative path for the given URI.
     * If the URI begins with a '~', the ContextPath (ie. the path to the current Web Application)
     * is added as a prefix, the '~' is removed, and the result is returned.
     * Otherwise, the URI is returned as is.
     *
     * @param request the HttpServletRequest
     * @return the context relative path for the given URI.
     */
    public static String getRelativePath(HttpServletRequest request, String uri) {
        Logger l = LogUtils.enterLog(Module.class, "getRelativePath", request);

        if (uri.startsWith("~/core")) {
            uri = getCorePath(request) + uri.substring("~/core".length());
        } else if (uri.startsWith("~")) {
            // Assume that if url starts with '~', it is relative to the Context Path
            uri = request.getContextPath() + uri.substring(1);
        }

        l.exiting(Module.class.getName(), "getRelativePath", uri);
        return uri;
    }

    /**
     * Return the context relative path of the Oasis core module
     */
    public static String getCorePath(HttpServletRequest request) {
        return getModulePath(request, "core");
    }

    /**
     * Return the context relative path of the Common Services module
     */
    public static String getCSPath(HttpServletRequest request) {
        return getModulePath(request, "CS");
    }

    /**
     * Return the context relative path of the CIS module
     */
    public static String getCISPath(HttpServletRequest request) {
        return getModulePath(request, "CIS");
    }

    /**
     * Return the context relative path of the Policy Management (PM) module
     */
    public static String getPMPath(HttpServletRequest request) {
        return getModulePath(request, "PM");
    }

    /**
     * Return the context relative path of the Financial Management (FM) module
     */
    public static String getFMPath(HttpServletRequest request) {
        return getModulePath(request, "FM");
    }

    /**
     * Return the context relative path of the given environment.
     * This is the parent of all applications running in this environment,
     * assuming all applications for an environment are running from the same folder.
     *
     * @param request the HttpServletRequest
     * @return the context relative path of the given module name.
     */
    public static String getEnvPath(HttpServletRequest request) {
        Logger l = LogUtils.enterLog(Module.class, "getModulePath", new Object[]{request});

        String envPath = request.getContextPath();

        if (isDeployedAsEar()) {
            envPath = envPath.substring(0, envPath.lastIndexOf("/"));
        }

        envPath += "/..";

        l.exiting(Module.class.getName(), "getModulePath", envPath);
        return envPath;
  }
    /**
     * Return the context relative path of the given module name.
     *
     * @param request the HttpServletRequest
     * @param moduleName the name of the desired module.
     * @return the context relative path of the given module name.
     */
    private static String getModulePath(HttpServletRequest request, String moduleName) {
        Logger l = LogUtils.enterLog(Module.class, "getModulePath", new Object[]{request, moduleName});

        String modulePath = request.getContextPath();

        if ("core".equals(moduleName)) {
            if (modulePath.indexOf("/FM") >= 0 || modulePath.indexOf("/PM") >= 0 || modulePath.indexOf("/WCPS") >= 0 ||
                modulePath.endsWith("/CM") || modulePath.endsWith("/RM") || modulePath.endsWith("/CIS") ||
                modulePath.endsWith("/CS") ||
                modulePath.endsWith("/wsCS") || modulePath.endsWith("/wsCIS") || modulePath.endsWith("/wsPolicy") || modulePath.endsWith("/wsFM") || modulePath.endsWith("/wsCM") ||
                modulePath.endsWith("/CustWebWB") || modulePath.endsWith("/ConfigProp") || modulePath.endsWith("/ProdDefWB") || modulePath.endsWith("/eAdmin/Portal") || modulePath.endsWith("/eAdmin/SysInfo") ||
                modulePath.endsWith("/eAdmin/Security") ||
                modulePath.endsWith("/CMA")) {
                // PM/WCPHS has OasisTags core in a 'common' subdirectory, but if CS or CIS are bundled as a war, they do not.
                modulePath += "/core";
            }
        }
        else if (isDeployedAsEar()) {
            modulePath = modulePath.substring(0, modulePath.lastIndexOf("/")) + "/" + moduleName;
        }

        l.exiting(Module.class.getName(), "getModulePath", modulePath);
        return modulePath;
  }

    private static boolean isDeployedAsEar() {
        if (c_deployedAsEar == null) {
            c_deployedAsEar = new Boolean(YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("deployed.as.ear", "false")).booleanValue());
        }
        return c_deployedAsEar.booleanValue();
    }

    private static Boolean c_deployedAsEar;
}
