package dti.oasis.dwr;

import dti.oasis.request.RequestStorageManager;
import dti.oasis.error.ValidationException;
import dti.oasis.util.LogUtils;
import dti.oasis.session.UserSessionManager;
import dti.oasis.app.AppException;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Apr 5, 2009
 * Time: 2:08:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class DwrProcessor {

    private static DwrProcessor c_instance = null;
    private Connection conn = null;


    public static synchronized DwrProcessor getInstance(){
          if(c_instance == null ){
              c_instance = new DwrProcessor();
          }
       return c_instance;
    }

    public void setConnection(Connection con){
        conn = con;
    }
    public Connection getConnection(){
        return conn;
    }
    /**
     * Called from ActionHelper.securePage
     * collects and stores request attributes/parameters into RequestStorageManager for
     * further processing during transaction interception.
     *
     * @param request
     * @param conn
     */
    public void setupProcess(HttpServletRequest request){
        Logger l = LogUtils.enterLog(getClass(),"setupProcess",request);

        String temp = "";
        RequestStorageManager rsm = RequestStorageManager.getInstance();
        String uri=request.getRequestURI();
        l.fine("uri="+uri+" bus)view="+uri.substring(uri.lastIndexOf("/")+1));
        rsm.set(DwrConstants.BUS_VIEW, uri.substring(uri.lastIndexOf("/")+1));
        Map mp = request.getParameterMap();

        rsm.set(DwrConstants.FORM_FIELD_CURR, convertParmMapToXml(mp));

        if (mp.containsKey(DwrConstants.FORM_FIELD_ORIG)) {
            temp = Arrays.toString((String[]) mp.get(DwrConstants.FORM_FIELD_ORIG));
            rsm.set(DwrConstants.FORM_FIELD_ORIG, temp.substring(1, temp.length() - 1));
        }

        //find Event (process )It should be either attribute or form parameter
        // if neither than set it to null

        String proc = "";
        if (mp.containsKey(DwrConstants.BUS_EVENT)) {
            proc = Arrays.toString((String[]) mp.get(DwrConstants.BUS_EVENT));
            proc = proc.substring(1, proc.length() - 1);
        } else {
            proc = (String) request.getAttribute(DwrConstants.BUS_EVENT);
        }
        l.logp(Level.FINE, getClass().getName(), "setupProcess", "Seeting up event to:"+proc);
        rsm.set(DwrConstants.BUS_EVENT_NAME, proc);
       l.exiting(getClass().getName(),"setupProcess");
    }
    public boolean isMethodMatches(String method){
       Logger l = LogUtils.enterLog(getClass(),"isMethodMatches"); 
       RequestStorageManager store = RequestStorageManager.getInstance();
        boolean result = false;
        try {
                   Map copy = store.getCopy();
                  String event = (String) copy.get(DwrConstants.BUS_EVENT_NAME);
                   if (!copy.containsKey(DwrConstants.BUS_EVENT_NAME) || event==null) {
                       System.out.println("isMethodMatches: NO business event setup");
                       result=false;
                   }else{
                       if(method.toUpperCase().substring(0,event.length()).equalsIgnoreCase(event.toUpperCase())){
                               result = true;
                       }
                   }
        }catch (ValidationException e) {
            e.printStackTrace();
            l.severe(getClass().getName()+":"+e.getMessage());
            throw new ValidationException(e.getMessage());
        }
       System.out.println("is MEthod Matches returns:"+result); 

       return result;
    }
    /**
     *
     *
     * Collect process parameters from RequestStoragteManager and execute process.
     * called from TransactionLifecycleListener
     */
    public void process() throws ValidationException, SQLException{

        Logger l = LogUtils.enterLog(getClass(),"process");
        RequestStorageManager store = RequestStorageManager.getInstance();
        Connection con = null;
        String emptyXml = "<?xml version=\"1.0\"?><form></form>";
        String temp;
        String origXml = emptyXml;
        String currXml = emptyXml;
        String activation = "";
        String event = "NOTHING";

        try {

            Map copy = store.getCopy();
            if (!copy.containsKey(DwrConstants.BUS_VIEW)) {
                return;
            } else {
                activation = (String) copy.get(DwrConstants.BUS_VIEW);
                store.remove(DwrConstants.BUS_VIEW);
            }

            if (copy.containsKey(DwrConstants.BUS_EVENT_NAME)) {
                event = (String) copy.get(DwrConstants.BUS_EVENT_NAME);
                try{
                store.remove(DwrConstants.BUS_EVENT_NAME);
                }catch(Exception e){
                 //do nothing. Key was not there.
                }
            }

            if (copy.containsKey(DwrConstants.FORM_FIELD_CURR)) {
                currXml = (String) copy.get(DwrConstants.FORM_FIELD_CURR);
                try{
                store.remove(DwrConstants.FORM_FIELD_CURR);
                }catch(Exception e){
                    //do nothing. Key was not there.
                }
            }
            if (copy.containsKey(DwrConstants.FORM_FIELD_ORIG)) {
                origXml = (String) copy.get(DwrConstants.FORM_FIELD_ORIG);
                try{
                store.remove(DwrConstants.FORM_FIELD_ORIG);
                }catch(Exception e){
                    //do nothing. Key was not there.
                }
            }

            l.fine("Output from DwrProcessor*******PROCESS***************");

            l.fine("Event-process:" + event);
            l.fine("View-action:" + activation);
            l.fine("ORIG:" + origXml);
            l.fine("Output from DwrProcessor********END**************");

            processDWR(activation, event, currXml, origXml);

        } catch (ValidationException e) {
            e.printStackTrace();
            l.severe(getClass().getName()+":"+e.getMessage());
            throw new ValidationException(e.getMessage());
        }

    }
    /**
     * Accepts parameters and processes all rules specified in rule meta-data
     * @param activation - struts action
     * @param event- process var in action
     * @param conn  - SQlConnection
     * @param curXml - XML containing form values as submitted
     * @param origXml - XML of original form values as rendered
     */
    public void processDWR(String activation, String event, String curXml, String origXml) throws SQLException, ValidationException{
        //get collection of Rule PK
        Logger l = LogUtils.enterLog(this.getClass(), "processDWR");

        DwRuleData dao = new DwRuleData();

        ArrayList rules = dao.createRulesCollection(conn, activation, event, curXml, origXml);

        l.fine("rules:" + rules.size());
        Iterator it = rules.iterator();
        while (it.hasNext()) {
            Rule rule = (Rule) it.next();
            rule.setConnection(conn);
            l.fine("Executing Rule with conditions:" + rule.getConditions().toString());
            rule.execRule();
        }

        l.exiting(this.getClass().getName(), "processDWR", activation);
    }

    /**
     * Same as above only accepting parameters Map from request rahter than XML
     * @param activation
     * @param event
     * @param conn
     * @param requestParamMap
     * @param origXML
     */
    public void processDWR(String activation, String event, Map requestParamMap, String origXML){
        //TBD
    }

    private String convertParmMapToXml(Map parms){
        Logger l = LogUtils.enterLog(getClass(), "convertParmMapToXml");
        l.fine("entering " + getClass().getName() + ".convertParmMapToXml");
        String xmlMap = "<?xml version=\"1.0\"?><form><fields>";
        int gridInd = 0;

        Iterator it = parms.keySet().iterator();
        while (it.hasNext()) {
            String fieldName = (String) it.next();
            String val = Arrays.toString((String[]) parms.get(fieldName));
            if (val.indexOf("<") < 0) {

                if (fieldName == "CROWID") {
                    if (gridInd == 0) {
                        xmlMap += "</fields><ROWS><ROW>";

                    } else {
                        xmlMap += "</ROW><ROW>";
                    }
                    gridInd++;
                }
                //check if field is in array
                xmlMap += "<field name=" + "'" + fieldName + "'" + " value='" + val.replaceAll("'","\"").substring(1, val.length() - 1) + "'/>\n";
            }
        }
        if (gridInd > 0) {
            xmlMap += "</ROW></ROWS>";
        } else {
            xmlMap += "</fields>";
        }
        xmlMap += "</form>";
        l.fine("form to XML " + xmlMap);
        return xmlMap;
    }

}
