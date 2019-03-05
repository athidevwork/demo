package dti.ci.entityhistoricaldatamgr;

import dti.ci.helpers.ICIConstants;
import dti.oasis.recordset.Record;


/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * User: Michael
 * Date: August 08, 2010
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class EntityHistoricalDataFields implements ICIConstants {
     public static final String ENT_CLS_FLD_ID_PREFIX          = "entityClass_";
  public static final String ENT_CLS_PK_ID                  = ENT_CLS_FLD_ID_PREFIX + "entityClassPK";
  public static final String ENT_CLS_ENT_FK_ID              = ENT_CLS_FLD_ID_PREFIX + "entityFK";


  public static final String STRUTS_TOKEN_CLASS_LIST = "dti.ci.struts.action.CIEntityClassList";
  public static final String STRUTS_TOKEN_CLASS_ADD = "dti.ci.struts.action.CIEntityClassAdd";
  public static final String STRUTS_TOKEN_CLASS_EDIT = "dti.ci.struts.action.CIEntityClassModify";
  public static final String COMMA = ",";
  public static final String NEW_LINE = "<br>";
}
