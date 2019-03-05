package dti.oasis.dwr;

import dti.oasis.util.LogUtils;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.ApplicationContext;
import dti.oasis.dwr.DwrConstants;
import dti.oasis.dwr.RuleManager;
import dti.oasis.session.UserSessionManager;

import javax.servlet.jsp.JspException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Iterator;

import org.apache.struts.taglib.TagUtils;

/**
 * Created by IntelliJ IDEA.
 * User: gjlong         
 * Date: Apr 6, 2009
 * Time: 5:49:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class OasisWebRuleTag extends javax.servlet.jsp.tagext.BodyTagSupport {
   /* (non-Javadoc)
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException {
       Logger l = LogUtils.enterLog(getClass(), "doStartTag");
       int rc = EVAL_BODY_BUFFERED;


       // If the user has not logged in yet or is not setup in this environment, skip this logic.
       if (UserSessionManager.isConfigured()) {
           TagUtils util = TagUtils.getInstance();

           util.write(pageContext, "\n<script language='javascript'> <!-- web rules...-->" + "\n\n");

           // get the original rule arguments,and populate originalArgumentArray
           Iterator original = getRuleArguments(true).getRecords();

           util.write(pageContext, "\tvar originalArgumentArray = new Array();\n");
           util.write(pageContext, "\n");
           int i = 0;
           while (original.hasNext()) {
               Record record = (Record) original.next();
               util.write(pageContext, "\toriginalArgumentArray[" + i + "] = \"" + record.getStringValue("argumentName") + "\";\n");
               i += 1;
           }
           l.logp(Level.FINE, getClass().getName(), "doStartTag", "originalArgment size:" + i / 2);

           util.write(pageContext, "\n");


           util.write(pageContext, "\t// declare  javascript function\n\n");
           util.write(pageContext, "\tfunction collectOriginalValues(){ \n");
           // create a hidden form field to store the value.
           util.write(pageContext, "\t  var originalFormValues= formValueToXMLString(document.forms[0],originalArgumentArray);\n");
           util.write(pageContext, "\t  setInputFormField(\"" + DwrConstants.FORM_FIELD_ORIG + "\",originalFormValues );\n");
           util.write(pageContext, "\t } \n\n");

           // attach events to window
           util.write(pageContext, "\n\n\t// attach events to window \n");
           util.write(pageContext, "\tvar o = new Object();\n");
           util.write(pageContext, "\to.ev=function (){collectOriginalValues(originalArgumentArray)};\n");
           util.write(pageContext, "\twindow.attachEvent?window.attachEvent(\"onload\",o.ev):void(0);\n\n");

           util.write(pageContext, "\n\tfunction formValueToXMLString(form, arr){\n");
           util.write(pageContext, "\nfixArray();");

           util.write(pageContext, "\nvar formFieldString='<?xml version=\"1.0\"?><form>';");
           util.write(pageContext, "\nvar gridInd = 0;");
           util.write(pageContext, "\nvar fieldCount=form.elements.length;");
           util.write(pageContext, "\nvar first=true;");
           util.write(pageContext, "\nfor(var i=0; i<fieldCount; i++){");
           util.write(pageContext, "\nif(form.elements[i].id || form.elements[i].name){");
           util.write(pageContext, "\nif(fieldName !=\"varTest\"){");
           util.write(pageContext, "\nvar fieldName = form.elements[i].id;");
           util.write(pageContext, "\nif(fieldName==\"\"){");
           util.write(pageContext, "\nfieldName = form.elements[i].name;");
           util.write(pageContext, "\n       }");
           util.write(pageContext, "\n       if(fieldName == \"CROWID\" ){");
           util.write(pageContext, "\n           if( gridInd==0){");
           util.write(pageContext, "\n              formFieldString +=\"<ROWS><ROW>\";");
           util.write(pageContext, "\n           }else{");
           util.write(pageContext, "\n               formFieldString +=\"</ROW><ROW>\";");
           util.write(pageContext, "\n           }");
           util.write(pageContext, "\n        gridInd++;");
           util.write(pageContext, "\n       }");
           util.write(pageContext, "\n          //check if field is in array");
           util.write(pageContext, "\n          if(arr.indexOf(fieldName)>=0){");
           util.write(pageContext, "\n              formFieldString+='<field name=\"'+fieldName+'\" value=\"'+ form.elements[i].value+'\"/>';");
           util.write(pageContext, "\n          }");
           util.write(pageContext, "\n       }");
           util.write(pageContext, "\n    }");
           util.write(pageContext, "\n  }");
           util.write(pageContext, "\n  if(gridInd>0){ ");
           util.write(pageContext, "\n         formFieldString+=\"</ROW></ROWS>\" ");
           util.write(pageContext, "\n  }");
           util.write(pageContext, "\nformFieldString+=\"</form>\";");
           util.write(pageContext, "\n //alert(\"formValueToXMLString\"+formFieldString);");
           util.write(pageContext, "\n  return formFieldString;");
           util.write(pageContext, "\n}");
           util.write(pageContext, "\n//make sure Array supports IndexOf");
           util.write(pageContext, "\n\nfunction fixArray(){");
           util.write(pageContext, "\n  if(!Array.indexOf){");
           util.write(pageContext, "\n          Array.prototype.indexOf = function(obj){");
           util.write(pageContext, "\n              for(var i=0; i<this.length; i++){");
           util.write(pageContext, "\n                  if(this[i]== obj){");
           util.write(pageContext, "\n                      return i;");
           util.write(pageContext, "\n                  }");
           util.write(pageContext, "\n              }");
           util.write(pageContext, "\n              return -1;");
           util.write(pageContext, "\n          }");
           util.write(pageContext, "\n      }");
           util.write(pageContext, "\n}");

           util.write(pageContext, "</script>  <!-- end of web rules...-->\n\n");
       }

       rc = super.doStartTag();
       l.exiting(getClass().getName(), "doStartTag");
       return rc;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public OasisWebRuleTag() {
    }

    /**
     *
     * @param originalArguments: true/false to indicate to get original arguments or current arguments
     * @return recordset containing argument names and argument values
     */
    private RecordSet getRuleArguments(boolean originalArguments){
       Logger l = LogUtils.enterLog(getClass(), "getConfiguredRuleParameters");

       String pageURI = "";
       if (pageContext.getRequest().getAttribute(RequestIds.REQUEST_URI) != null) {
           pageURI = ((String) pageContext.getRequest().getAttribute(RequestIds.REQUEST_URI)).substring(1);
       }
       l.logp(Level.FINE, getClass().getName(), "getConfiguredRuleArguments", "pageURI:" + pageURI);

       String processParameter = "";
       if (pageContext.getRequest().getParameter(RequestIds.REQUEST_URI) != null) {
           processParameter = (String) pageContext.getRequest().getAttribute(RequestIds.PROCESS);
       }
       l.logp(Level.FINE, getClass().getName(), "getConfiguredRuleArguments", "processParameter:" + processParameter);

       Record inputRecord = new Record();
       inputRecord.setFieldValue("busView", pageURI);
       inputRecord.setFieldValue("busEvent", processParameter);

       //Get original configured rule arguments for the current page URI.
       inputRecord.setFieldValue("origInd", originalArguments?"Y":"N");

       RuleManager rm = (RuleManager) ApplicationContext.getInstance().getBean("ruleManager");
       RecordSet arguments =rm.loadAllArguments(inputRecord);

       l.exiting(getClass().getName(),"getConfiguredRuleArguments",arguments);
       return arguments;
    }
}
