package dti.oasis.tags;

import dti.oasis.util.XMLUtils;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.BaseResultSet;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import java.util.logging.Logger;

/**
 * Generates XML Data Islands
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 9, 2004 
 * @author jbe
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 *
 * ---------------------------------------------------
*/

public class XMLIslandTag extends TagSupport {
    private BaseResultSet data;
    private XMLGridHeader header;
    private String xmlId;

    public BaseResultSet getData() {
        return data;
    }

    public void setData(BaseResultSet data) {
        this.data = data;
    }

    public XMLGridHeader getHeader() {
        return header;
    }

    public void setHeader(XMLGridHeader header) {
        this.header = header;
    }

    public String getXmlId() {
        return xmlId;
    }

    public void setXmlId(String xmlId) {
        this.xmlId = xmlId;
    }

    /**
     * Start tag for simple JSP Custom Tag
     * @return SKIP_BODY
     * @throws JspException
     */
    public int doStartTag() throws JspException {
        Logger l = LogUtils.enterLog(getClass(),"doStartTag");
        l.fine(toString());
        try {
            XMLUtils.resultSetToXml(pageContext.getOut(),xmlId,data,header);
        }
        catch (Throwable t) {
            l.throwing(getClass().getName(),"doStartTag",t);
            throw new JspException(t);
        }
        return SKIP_BODY;
    }

    /**
     * Release JSP Tag resources
     */
    public void release() {
        Logger l = LogUtils.enterLog(getClass(),"release");
        super.release();
        data = null;
        header = null;
        xmlId = null;
        l.exiting(getClass().getName(),"release");
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(super.toString()).append(" ::: ");
        buf.append("XMLIslandTag");
        buf.append("{data=").append(data);
        buf.append(",header=").append(header);
        buf.append(",xmlId=").append(xmlId);
        buf.append('}');
        return buf.toString();
    }
}
