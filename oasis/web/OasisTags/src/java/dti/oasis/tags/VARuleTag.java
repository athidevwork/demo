package dti.oasis.tags;

import dti.oasis.util.LogUtils;
import dti.oasis.var.VarUtil;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: sjzhu
 * Date: Oct 14, 2006
 * Time: 11:03:24 PM
 */
public class VARuleTag extends TagSupport {
    private String formID;

    public void setFormIDonPage(String formIDonPage) {
        this.formIDonPage = formIDonPage;
    }

    private String formIDonPage="";
    private String urlVAR="var.jsp";
    private boolean isActive=true;
    private boolean isGrid=false;
    private String gridXmlID="";
    private String processFieldName="";
    private String processFieldValue="";
    private boolean needOriginal=false;
    private ArrayList original=new ArrayList();

    public int doStartTag() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "doStartTag", this);
        // Get the javascript from the map
        if(formIDonPage.trim().length()<1){
           formIDonPage=formID;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream stringStream = new PrintStream(out);
        stringStream.println("<script language=\"JavaScript\" type=\"text/javascript\" src=\"js/var.js\"></script>");
        stringStream.println("<script type=\"text/javascript\">");
        stringStream.println("var varRule = new VARuleObj();");
        stringStream.println(" varRule.formID='" + formID + "';");
        stringStream.println(" varRule.formIDonPage='" + formIDonPage + "';");
        stringStream.println(" varRule.isActive=" + isActive + ";");
        stringStream.println(" varRule.urlVAR='" + urlVAR + "';");
        stringStream.println(" varRule.isGrid=" + isGrid + ";");
        if(isGrid) {
            stringStream.println(" varRule.gridXmlID=" + gridXmlID + ";");
        }
        stringStream.println(" varRule.needOriginal=" + needOriginal + ";");
        stringStream.println(" varRule.processFieldName='" + processFieldName + "';");
        stringStream.println(" varRule.processFieldValue='" + processFieldValue + "';");
        stringStream.println(" varRule.returnValue=false;");

        stringStream.println("varRule.values={\"data\":[{}]};");

        String jsonOriginal = VarUtil.formatMapsToJSON(original);

        stringStream.println("varRule.original=" + jsonOriginal + ";");

        stringStream.println("</script>");
        stringStream.flush();
        String js=out.toString();
        // write the javascript out
        TagUtils.getInstance().write(pageContext, js);
        l.exiting(getClass().getName(), "doStartTag", String.valueOf(SKIP_BODY));
        return SKIP_BODY;
    }

    public void setOriginal(ArrayList original) {
        this.original = original;
    }

    public void setUrlVAR(String urlVAR) {
        this.urlVAR = urlVAR;
    }


    public void setIsActive(boolean activ) {
        isActive = activ;
    }


    public void setIsGrid(boolean grid) {
        isGrid = grid;
    }

    public void setGridXmlID(String gridXmlID) {
        this.gridXmlID = gridXmlID;
    }


    public void setProcessFieldName(String processFieldName) {
        this.processFieldName = processFieldName;
    }


    public void setProcessFieldValue(String processFieldValue) {
        this.processFieldValue = processFieldValue;
    }


    public void setNeedOriginal(boolean needOrigina) {
        this.needOriginal = needOrigina;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }
}
