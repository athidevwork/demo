package dti.oasis.tags;

import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Generates a Constants Javascript object given
 * a fully qualified classname.  It uses reflection and
 * caches the Javascript by class.  It will use every
 * field in the class except for those marked as
 * String[].  You should generally use this on
 * interfaces otherwise you will get unpredictable results.
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 28, 2004
 *
 * @author jbe
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 3/28/2005        jbe         Get ancestor constants as well, but pull
 *                              the value from current class.
 * 9/2/2005         jbe         Support Struts 1.2 - replace ResponseUtils with TagUtils.
 * 9/13/2005        jbe         Replace language='javascript' with type='text/javascript' 
 * ---------------------------------------------------
*/

public class ConstantsTag extends TagSupport {

    /**
     * Internal HashMap of JavaScript which generates a JS Object containing
     * constants.  The key is the name of the class/interface.
     */
    private static HashMap constants = new HashMap();

    /**
     * Fully qualified Class/Interface whose fields will be reflected upon.
     */
    private String constantClass;

    /**
     * Handle tag processing
     *
     * @return SKIP_BODY
     * @throws JspException
     */
    public int doStartTag() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "doStartTag", this);
        // Get the javascript from the map
        String js = (String) constants.get(constantClass);
        // if it is empty or null, create it
        if (StringUtils.isBlank(js)) {
            try {
                js = generateJSObject();
            }
            catch (Throwable e) { // could be caused by any number of problems
                l.throwing(getClass().getName(), "doStartTag", e);
                throw new JspException(e);
            }
            // store the javascript in the map
            constants.put(constantClass, js);
        }
        // write the javascript out
        TagUtils.getInstance().write(pageContext, js);
        l.exiting(getClass().getName(), "doStartTag", String.valueOf(SKIP_BODY));
        return SKIP_BODY;
    }

    /**
     * Generate javascript to construct a javascript object w/ constants
     *
     * @return javascript (String)
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     */
    private String generateJSObject() throws ClassNotFoundException, IllegalAccessException {
        Logger l = LogUtils.enterLog(getClass(), "generateJSObject");
        // Load the class
        Class cls = Class.forName(constantClass);
        // We want just the classname for the javascript object
        String nm = constantClass.substring(constantClass.lastIndexOf('.') + 1).toUpperCase();
        // Start generating javascript
        StringBuffer buff = new StringBuffer("<script type='text/javascript'>\n");
        buff.append("function ").append(nm).append("() {\n");
        Field[] fields = cls.getFields();
        int sz = fields.length;
        // loop through the fields, ignoring any String Arrays
        for (int i = 0; i < sz; i++) {
            if (fields[i].getType() != String[].class) {
                // get field name
                String fldNm = fields[i].getName();
                // get value
                Object oVal = fields[i].get(null);
                // Make sure we use this class/interface's value for this field
                // if it was overridden.  We only want the value from an
                // ancestor class if it was NOT overridden here.
                try {
                    // field exists at this level?
                    Field dFld = cls.getDeclaredField(fldNm);
                    // It must, otherwise we'dve blown up already
                    // get this field's value
                    oVal = dFld.get(null);
                }
                catch (NoSuchFieldException e) {
                    // field in some ancestor, ignore the exception.
                }
                buff.append("this.").append(fldNm).append(" = \"").
                        append(String.valueOf(oVal).replace('\n', ' ').replace('\r', ' ')).
                        append("\";\n");
            }
        }
        buff.append("}\n");
        buff.append("var ").append(nm.toLowerCase()).append(" = new ").append(nm).append("();\n");
        buff.append("</script>");
        String s = buff.toString();
        l.exiting(getClass().getName(), "generateJSObject", s);
        return s;
    }

    public String getConstantClass() {
        return constantClass;
    }

    public void setConstantClass(String constantClass) {
        this.constantClass = constantClass;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.ConstantsTag");
        buf.append("{constantClass=").append(constantClass);
        buf.append('}');
        buf.append("{constants=").append(constants);
        buf.append('}');
        return buf.toString();
    }
}
