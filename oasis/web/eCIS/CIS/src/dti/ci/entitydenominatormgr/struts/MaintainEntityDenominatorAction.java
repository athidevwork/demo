package dti.ci.entitydenominatormgr.struts;

import dti.ci.core.CIFields;
import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.entitydenominatormgr.EntityDenominatorManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static dti.ci.entitydenominatormgr.EntityDenominatorFields.*;

/**
 * Action Class for Denominator
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   May 30, 2006
 *
 * @author bhong
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 01/20/2011       Michael Li  Issue:116335
 * 07/01/2013       hxk         Issue 141840
 *                              Add pk to request for CIS security.
 * 01/27/2015       bzhu        Issue 159739. Get ciDenominatorListGrid.xml from applicationConfig-cis.properties
 *                              so that it can be overwriten.
 * 02/02/2015       Elvin       Issue 159162: add JS message for start/end date validation
 * 08/21/2015       ylu         Issue 164732: column lov dependence
 * 08/24/2015       kyle        Issue 164732: update for grid's filter and sort
 * 12/08/2017       ylu         Issue 190017
 * 06/28/2018       ylu         Issue 194117: update for CSRF security.
 * ---------------------------------------------------
 */
public class MaintainEntityDenominatorAction extends MaintainEntityFolderBaseAction {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllEntityDenominator(mapping, form, request, response);
    }

    public ActionForward loadAllEntityDenominator(ActionMapping mapping, ActionForm form,
                                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllEntityDenominator", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAllEntityDenominatorResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.DATA_BEAN);
            if (rs == null) {
                Record searchCriteria = getSearchCriteria(inputRecord);

                rs = getEntityDenominatorManager().loadAllEntityDenominator(searchCriteria);
            }

            setDataBean(request, rs);

            publishOutputRecord(request, inputRecord);

            loadGridHeader(request);

            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);

        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                    "Failed to load the Entity Denominator page.",
                    e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllEntityDenominator", af);
        }
        return af;
    }

    public ActionForward refresh(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllEntityDenominator(mapping, form, request, response);
    }

    public ActionForward saveAllEntityDenominator(ActionMapping mapping, ActionForm form,
                                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllEntityDenominator", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveAllEntityDenominatorResult";

        RecordSet rs = null;

        try {
            if (isTokenValid(request, true)) {
                securePage(request, form, false);

                rs = getInputRecordSet(request);

                getEntityDenominatorManager().saveAllEntityDenominator(rs);
            }
        } catch (ValidationException ve) {
            request.setAttribute(RequestIds.DATA_BEAN, rs);
            handleValidationException(ve, request);

        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Unable to save entity Denominator.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllEntityDenominator", af);
        }
        return af;
    }

    protected Record getSearchCriteria(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSearchCriteria", new Object[]{inputRecord});
        }

        Record record = new Record();

        record.setFieldValue(CIFields.ENTITY_ID, inputRecord.getStringValue(CIFields.PK));

        List<String> fieldNames = inputRecord.getFieldNameList();
        for (String fieldName : fieldNames) {
            if (fieldName.startsWith(FILTER_CRITERIA_PREFIX)) {
                record.setFieldValue(StringUtils.strRight(fieldName, FILTER_CRITERIA_PREFIX), inputRecord.getStringValueDefaultEmpty(fieldName));
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getSearchCriteria", record);
        }
        return record;
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.newRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.existRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.value.number");
        MessageManager.getInstance().addJsMessage("ci.common.error.element.required");
        MessageManager.getInstance().addJsMessage("ci.entity.message.date.enter");
        MessageManager.getInstance().addJsMessage("ci.common.error.element.before");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("ci.detail.denominator.date.after");
        MessageManager.getInstance().addJsMessage("cs.records.delete.confirm");
    }

    public void verifyConfig() {
        if (getEntityDenominatorManager() == null)
            throw new ConfigurationException("The required property 'entityDenominatorManager' is missing.");
    }

    public EntityDenominatorManager getEntityDenominatorManager() {
        return m_entityDenominatorManager;
    }

    public void setEntityDenominatorManager(EntityDenominatorManager entityDenominatorManager) {
        m_entityDenominatorManager = entityDenominatorManager;
    }

    private EntityDenominatorManager m_entityDenominatorManager;
}
