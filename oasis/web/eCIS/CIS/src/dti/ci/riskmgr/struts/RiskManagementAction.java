package dti.ci.riskmgr.struts;

import dti.ci.struts.action.CIBaseAction;
import dti.ci.helpers.ICIConstants;
import dti.ci.helpers.CILinkGenerator;
import dti.ci.riskmgr.RiskManager;
import dti.ci.riskmgr.RiskFields;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Action class for Risk Management Tab.
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 19, 2008
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/06/2009       kshen       Added codes to support system parameter "PM_RM_DISC_RETRIEVE".
 * 11/30/2011       parker      for issue 127702.missing anchor column name.
 * 03/06/2012       Parker      130270. set CIS notes visible for this business.
 * 01/27/2014       Elvin       Issue 150732: remove sysParams which control layer visible or not,
 *                                      use layer Hidden property directly
 * 05/22/2015       bzhu        Issue 156487 - retrieve accumulated discount point.
 * 06/29/2018       ylu         Issue 194117: update for CSRF security.
 * ---------------------------------------------------
 */

public class RiskManagementAction extends CIBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter
     * sent in along the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return load(mapping, form, request, response);
    }

    /**
     * Method to load rm risk managment page.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "load", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadRmResult";

        try {
            securePage(request, form);

            String entityPk = getInputRecord(request).getStringValue(ICIConstants.PK_PROPERTY, "");

            /* validate */
            if (!FormatUtils.isLong(entityPk)) {
                throw new AppException("ci.cicore.invalidError.EntityfkNotExists",
                    new StringBuffer().append(
                        "entity FK [").append(entityPk)
                        .append("] should be a number.")
                        .toString(),
                    new Object[]{entityPk});
            }

            String entityName = getInputRecord(request).getStringValue(ENTITY_NAME_PROPERTY);
            String entityType = getInputRecord(request).getStringValue(ENTITY_TYPE_PROPERTY);
            request.setAttribute(ENTITY_NAME_PROPERTY, entityName);
            request.setAttribute(ENTITY_TYPE_PROPERTY, entityType);

            /* set menu beans Search & Select an entity type of  'organization'.*/
            if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
                checkCisFolderMenu(request);
            }

            request.setAttribute(ICIConstants.PK_PROPERTY, entityPk);
            Record inputRecord = new Record();
            RiskFields.setEntityId(inputRecord, new Long(entityPk));

            Record output = new Record();

            // Get the current risk management discount.
            Record currentRmDiscountRecord = getRiskManager().getCurrentRiskManagementDiscount(inputRecord);
            if (currentRmDiscountRecord!=null) {
                RiskFields.setCurrentRmDiscountDescr(output,
                    RiskFields.getRmDiscountDescr(currentRmDiscountRecord));
            } else {
                RiskFields.setCurrentRmDiscountDescr(output, NO_RM_DISCOUNT);
            }

            // Get the current manadate window period.
            Record currentMandateWindowPeriodRecord = getRiskManager().getCurrentMandateWindowPeriod(inputRecord);
            if (currentMandateWindowPeriodRecord!=null) {
                RiskFields.setCurrentMandateWindowPeriodDescr(output,
                    RiskFields.getWindowPeriodDescr(currentMandateWindowPeriodRecord));
//                request.setAttribute(RiskFields.MANDATE_WINDOW_PERIOD_FULFILLED,
//                    RiskFields.getWindowPeriodStatus(currentMandateWindowPeriodRecord));
                request.setAttribute(RiskFields.SATISFIED_B, RiskFields.getSatisfiedB(currentMandateWindowPeriodRecord));
            } else {
                RiskFields.setCurrentMandateWindowPeriodDescr(output, NOT_SUBJECT_TO_MANDATE);
//                request.setAttribute(RiskFields.MANDATE_WINDOW_PERIOD_FULFILLED, "Y");
                request.setAttribute(RiskFields.SATISFIED_B, "Y");
            }

            Record accumulatedDiscountPointRecord = getRiskManager().getAccumulatedDiscountPoint(inputRecord);
            if (accumulatedDiscountPointRecord != null) {
                RiskFields.setAccDistPoint(output, RiskFields.getAccDistPoint(accumulatedDiscountPointRecord));
            }

            // If the value of "PM_RM_DISC_RETRIEVE" is not "Y", we do not retrieve and display additional rm discount. 
            String retrieveAdditionalRmDiscountStr = SysParmProvider.getInstance().getSysParm("PM_RM_DISC_RETRIEVE", "N");
            boolean retrieveAdditionalRmDiscountB = retrieveAdditionalRmDiscountStr.equals("Y");

            RecordSet programHistoryRecordSet = getRiskManager().getProgramHistory(inputRecord);
            RecordSet windowPeriodHistoryRecordSet = getRiskManager().getWindowPeriodHistory(inputRecord);
            RecordSet additionalRmDiscountRecordSet = null;
            
            if (retrieveAdditionalRmDiscountB) {
                additionalRmDiscountRecordSet = getRiskManager().getAdditionalRiskManagementDiscount(inputRecord);
            }
            RecordSet ersPointHistoryRecordSet = getRiskManager().getErsPointHistory(inputRecord);

            int negativePointsCount = 0;
            if (ersPointHistoryRecordSet != null) {
                for (int i=0; i<ersPointHistoryRecordSet.getSize();i++) {
                    Integer negativePoints = RiskFields.getNegativePoints(ersPointHistoryRecordSet.getRecord(i));
                    if (negativePoints!=null) {
                        negativePointsCount += negativePoints.intValue();
                    }
                }
            }
            RiskFields.setNegativePointsCount(output, new Integer(negativePointsCount));

            setDataBean(request, programHistoryRecordSet, PROGRAM_HISTORY_GRID);
            setDataBean(request, windowPeriodHistoryRecordSet, WINDOW_PERIOD_HISTORY_GRID);
            if (retrieveAdditionalRmDiscountB) {
                setDataBean(request, additionalRmDiscountRecordSet, ADDITIONAL_RM_DISCOUNT_GRID);
            }
            setDataBean(request, ersPointHistoryRecordSet, ERS_POINT_HISTORY_GRID);

            output.setFields(programHistoryRecordSet.getSummaryRecord());
            output.setFields(windowPeriodHistoryRecordSet.getSummaryRecord(), false);
            if (retrieveAdditionalRmDiscountB) {
                output.setFields(additionalRmDiscountRecordSet.getSummaryRecord(), false);
            }
            output.setFields(ersPointHistoryRecordSet.getSummaryRecord(), false);
            publishOutputRecord(request, output);

            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PROGRAM_HISTORY_GRID);
            loadGridHeader(request, null, PROGRAM_HISTORY_GRID, PROGRAM_HISTORY_GRID_GH);

            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, WINDOW_PERIOD_HISTORY_GRID);
            loadGridHeader(request, null, WINDOW_PERIOD_HISTORY_GRID, WINDOW_PERIOD_HISTORY_GRID_GH);

            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, ADDITIONAL_RM_DISCOUNT_GRID);
            loadGridHeader(request, null, ADDITIONAL_RM_DISCOUNT_GRID, ADDITIONAL_RM_DISCOUNT_GRID_GH);

            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, ERS_POINT_HISTORY_GRID);
            loadGridHeader(request, null, ERS_POINT_HISTORY_GRID, ERS_POINT_HISTORY_GRID_GH);

            loadListOfValues(request, form);

            // set js messages
            addJsMessages();
            setCisHeaderFields(request);  
            saveToken(request);

            new CILinkGenerator().generateLink(request, entityPk, this.getClass().getName());

        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load RM page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "load", af);
        return af;
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.entity.message.imageRight.determine");
    }

    public void verifyConfig() {
        if (getRiskManager() == null) {
            throw new ConfigurationException("The required property 'riskManager' is missing.");
        }
        if (getProgramHistoryGridAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'programHistoryGridAnchorColumnName' is missing.");
        }
        if (getWindowPeriodHistoryGridAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'windowPeriodHistoryGridAnchorColumnName' is missing.");
        }
        if (getErsPointHistoryGridAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'ersPointHistoryGridAnchorColumnName' is missing.");
        }
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        Logger l = LogUtils.enterLog(getClass(), "getAnchorColumnName");
        String anchorName;
        if (RequestStorageManager.getInstance().has(RiskManagementAction.CURRENT_GRID_ID)) {
            String currentGridId = (String) RequestStorageManager.getInstance().get(RiskManagementAction.CURRENT_GRID_ID);
            if (currentGridId.equals(RiskManagementAction.PROGRAM_HISTORY_GRID)) {
                anchorName = getProgramHistoryGridAnchorColumnName();
            } else if (currentGridId.equals(RiskManagementAction.WINDOW_PERIOD_HISTORY_GRID)) {
                anchorName = getWindowPeriodHistoryGridAnchorColumnName();
            } else if (currentGridId.equals(RiskManagementAction.ADDITIONAL_RM_DISCOUNT_GRID)) {
                anchorName = getAdditionalRMDiscountGridAnchorColumnName();
            } else if (currentGridId.equals(RiskManagementAction.ERS_POINT_HISTORY_GRID)) {
                anchorName = getErsPointHistoryGridAnchorColumnName();
            } else {
                anchorName = super.getAnchorColumnName();
            }
        } else {
            anchorName = super.getAnchorColumnName();
        }
        l.exiting(getClass().getName(), "getAnchorColumnName", anchorName);
        return anchorName;
    }
    public RiskManagementAction() {
    }

    public RiskManager getRiskManager() {
        return riskManager;
    }

    public void setRiskManager(RiskManager riskManager) {
        this.riskManager = riskManager;
    }

    public String getProgramHistoryGridAnchorColumnName() {
        return programHistoryGridAnchorColumnName;
    }

    public void setProgramHistoryGridAnchorColumnName(String programHistoryGridAnchorColumnName) {
        this.programHistoryGridAnchorColumnName = programHistoryGridAnchorColumnName;
    }

    public String getWindowPeriodHistoryGridAnchorColumnName() {
        return windowPeriodHistoryGridAnchorColumnName;
    }

    public void setWindowPeriodHistoryGridAnchorColumnName(String windowPeriodHistoryGridAnchorColumnName) {
        this.windowPeriodHistoryGridAnchorColumnName = windowPeriodHistoryGridAnchorColumnName;
    }

    public String getErsPointHistoryGridAnchorColumnName() {
        return ersPointHistoryGridAnchorColumnName;
    }

    public void setErsPointHistoryGridAnchorColumnName(String ersPointHistoryGridAnchorColumnName) {
        this.ersPointHistoryGridAnchorColumnName = ersPointHistoryGridAnchorColumnName;
    }

    public String getAdditionalRMDiscountGridAnchorColumnName() {
        return additionalRMDiscountGridAnchorColumnName;
    }

    public void setAdditionalRMDiscountGridAnchorColumnName(String additionalRMDiscountGridAnchorColumnName) {
        this.additionalRMDiscountGridAnchorColumnName = additionalRMDiscountGridAnchorColumnName;
    }

    private RiskManager riskManager;
    private String programHistoryGridAnchorColumnName;
    private String windowPeriodHistoryGridAnchorColumnName;
    private String ersPointHistoryGridAnchorColumnName;
    private String additionalRMDiscountGridAnchorColumnName;

    private static final String CURRENT_GRID_ID = "currentGridId";

    private static final String NO_RM_DISCOUNT = "No Risk Management Discount";
    private static final String NOT_SUBJECT_TO_MANDATE = "Not subject to Mandate";

    private static final String PROGRAM_HISTORY_GRID = "programHistoryGrid";
    private static final String WINDOW_PERIOD_HISTORY_GRID = "windowPeriodHistoryGrid";
    private static final String ADDITIONAL_RM_DISCOUNT_GRID = "additionalRmDiscountGrid";
    private static final String ERS_POINT_HISTORY_GRID = "ersPointHistoryGrid";

    private static final String PROGRAM_HISTORY_GRID_GH = "programHistoryGrid_GH";
    private static final String WINDOW_PERIOD_HISTORY_GRID_GH = "windowPeriodHistoryGrid_GH";
    private static final String ADDITIONAL_RM_DISCOUNT_GRID_GH = "additionalRmDiscountGrid_GH";
    private static final String ERS_POINT_HISTORY_GRID_GH = "ersPointHistoryGrid_GH";
}