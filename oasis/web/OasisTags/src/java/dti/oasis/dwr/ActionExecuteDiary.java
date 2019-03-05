package dti.oasis.dwr;

import dti.oasis.session.UserSessionManager;
import dti.oasis.session.UserSession;
import dti.oasis.session.impl.UserSessionManagerImpl;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.OasisUser;
import dti.oasis.util.LogUtils;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.ApplicationContext;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import java.sql.Date;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Mar 31, 2009
 * Time: 9:17:10 PM
 * To change this template use File | Settings | File Templates.
 * <p/>
 * List of expected action arguments supplied in meta-data
 * clm_pk              IN      claim.claim_pk%TYPE,
 * ent_pk              IN      entity.entity_pk%TYPE,
 * role_code           IN      entity_role.role_type_code%TYPE,
 * diary_event         IN      event_code.event_code%TYPE,
 * chk_4_prior         IN      varchar2,
 * sched_date          IN      date,
 * comp_date           IN      date,
 * diary_note          IN      oasis_event_history.notes%TYPE,
 * src_tbl_name        IN      oasis_event_history.source_table_name%TYPE
 */
public class ActionExecuteDiary implements IExecuteAction {

    public void exec(RuleActionArgs args, Connection con) {
        Logger l = LogUtils.enterLog(getClass(), "exec");
        LinkedHashMap parms = args.getActionArgs();
        try {
            parms.put(DwrConstants.DIARY_ENTITY_PK, new Long(getUserEntityPK()));
            parms.put(DwrConstants.DIARY_SCHD_DATE, "sysdate");

            parms.putAll(args.getMdConst());

            Record inputRecord = new Record();
            LinkedHashMap fields = args.getActionArgs();

            Iterator it = fields.keySet().iterator();
            //expect only one field for claim ID

            if (it.hasNext()) {
                String key = (String) it.next();
                inputRecord.setFieldValue("clmId", (String) fields.get(key));
            } else {
                inputRecord.setFieldValue("clmId", null);
            }
            long entId = getUserEntityPK();
            inputRecord.setFieldValue("entId",new Long(entId));
            inputRecord.setFieldValue("roleCode", parms.get(DwrConstants.DIARY_ROLE_CODE));
            inputRecord.setFieldValue("diaryEvent", parms.get(DwrConstants.DIARY_EVENT));
            inputRecord.setFieldValue("chk4Prior", "Y");
           // inputRecord.setFieldValue("schedDate", new java.sql.Timestamp(System.currentTimeMillis()));
             inputRecord.setFieldValue("schedDate",null);
            inputRecord.setFieldValue("compDate", null);
            inputRecord.setFieldValue("diaryNote", parms.get(DwrConstants.DIARY_NOTE));
            inputRecord.setFieldValue("srcTblName", parms.get(DwrConstants.DIARY_SRC_TBL_NAME));
            RuleManager rm = (RuleManager) ApplicationContext.getInstance().getBean("ruleManager");
            l.fine("Calling DIARY SP params:" + inputRecord.toString());
            rm.insertDiaryItem(inputRecord);


        } catch (Exception e) {
             e.printStackTrace();
            l.fine("Exception message:" + e.getMessage());
            throw new ValidationException(e.getMessage());
        }
        l.exiting(getClass().getName(), "exec", parms);
    }

    private long getUserEntityPK() {
        UserSessionManager usm;
        UserSession userSession = (UserSession) UserSessionManager.getInstance().getUserSession();
        OasisUser ouser = userSession.getOasisUser();

        return ouser.getEntityPk();
    }
}
