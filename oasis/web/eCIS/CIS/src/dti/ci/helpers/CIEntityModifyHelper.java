package dti.ci.helpers;

import dti.ci.helpers.data.*;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.*;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.cs.activityhistorymgr.ActivityHistoryManager;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Helper class for modifying an Entity.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Dec 10, 2003
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ----------------------------------------------------------------
 *         03/31/2005       HXY         Removed singleton implementation.
 *         04/14/2005       HXY         Added connection commit logic.
 *         04/18/2005       HXY         Created one instance DAO.
 *         08/02/2006       ligj        Add Loss DAO.
 *         02/16/2007       kshen       Added DbaHistory DAO.
 *                                      Added method retrieveEntityDbaData. (iss68160)
 *         07/02/2007       FWCH        Added method updateEntityType.
 *         03/31/2008       wer         Always use CIS as the subSystemCode when recording activity history so the link opens the entity in CIS, in case this was called from the Entity Mini Popup within another application.
 *         03/02/2009       Leo         Issue 87902.
 *         10/16/2009       hxk         Added userHasExpertWitnessClass  and setExpWitTabVisibility functions for issue 97591.
 *         08/27/2010       Kenny       Iss#110852.
 *         04/18/2013       bzhu        Issue 139501.
 *         -----------------------------------------------------------------
 */

public class CIEntityModifyHelper extends CIEntityHelper implements Serializable {

    private CIEntityDAO cIEntityDAO = null;

    /**
     * Get an instance of a CIEntityDAO.
     *
     * @return a CIEntityDAO
     * @throws DAOInstantiationException
     */
    protected CIEntityDAO getCIEntityDAO() throws DAOInstantiationException {
        String methodName = "getCIEntityDAO";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName);
        if (cIEntityDAO == null) {
            cIEntityDAO = (CIEntityDAO) DAOFactory.getDAOFactory().getDAO("CIEntityDAO");
        }
        lggr.exiting(this.getClass().getName(), methodName);
        return cIEntityDAO;
    }

    /**
     * Retrieves a HashMap containing data for a particular entity.
     *
     * @param conn Connection object.
     * @param pk   Entity PK.
     * @return Map with the entity data.
     * @throws Exception
     */
    public Map retrieveEntityDataMap(Connection conn, String pk)
            throws Exception {
        String methodName = "retrieveEntityDataMap";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{conn, pk});
        try {
            Map retrievedEntity = getCIEntityDAO().retrieveDataMap(conn, pk);

            String entityType = ((String) retrievedEntity.get(ENTITY_TYPE_ID)).toUpperCase();
            if (!StringUtils.isBlank(entityType) && entityType.charAt(0) != ENTITY_TYPE_PERSON_CHAR && entityType.charAt(0) != ENTITY_TYPE_ORG_CHAR) {
                entityType = "";
            }
            String displayInformation = "";

            String activityDisplayInformation = "";

            if (!StringUtils.isBlank(entityType) && entityType.charAt(0) == ENTITY_TYPE_PERSON_CHAR) {
                if (!StringUtils.isBlank((String) retrievedEntity.get(ENTITY_NAME_COMPUTED_ID))) {
                    activityDisplayInformation = MessageManager.getInstance().formatMessage(
                                                  "cs.cis.activityHistory.displayInformation",
                                                  new String[]{((String) retrievedEntity.get(ENTITY_NAME_COMPUTED_ID))});
                }
            } else if (!StringUtils.isBlank(entityType) && entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
                if (!StringUtils.isBlank((String) retrievedEntity.get(ORG_NAME_ID))) {
                    activityDisplayInformation = MessageManager.getInstance().formatMessage(
                                                  "cs.cis.activityHistory.displayInformation",
                                                  new String[]{((String) retrievedEntity.get(ORG_NAME_ID))});
                }
            }

            int output = ActivityHistoryManager.getInstance().recordActivityHistory(
                                                  "CIS",    // Always use CIS as the subSystemCode so the link opens the entity in CIS, in case this was called from the Entity Mini Popup within another application.
                                                  "ENTITY", (String) retrievedEntity.get(CLIENT_ID_ID), pk,
                                                  "", activityDisplayInformation, "");

            return retrievedEntity;
        } catch (Exception e) {
            try {
                String exceptMsg = "Class " + this.getClass().getName() +
                        "method " + methodName + ":  exception occurred:  " +
                        e.toString();
                lggr.info(exceptMsg);
            } catch (Exception ignore) {
            }
            throw e;
        } finally {
            lggr.exiting(this.getClass().getName(), methodName);
        }
    }
}
