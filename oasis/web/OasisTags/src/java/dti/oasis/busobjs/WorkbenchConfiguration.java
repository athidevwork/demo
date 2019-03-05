package dti.oasis.busobjs;

import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.tags.WebLayer;
import dti.oasis.session.UserSessionManager;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.struts.IOasisAction;
import dti.oasis.struts.ActionHelper;

import javax.sql.DataSource;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.struts.action.ActionForm;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 17, 2007
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/18/2007       sxm         Modified getFieldLabel to get layer label if it exits.
 * 09/19/2009       fcb         98370: loadListOfValues and getListOfValuesForField added. 
 * ---------------------------------------------------
 */
public class WorkbenchConfiguration {

    /**
     * Get all default values for an action class into a record
     *
     * @param actionClassName name of the action class to get the oasis fields and default values
     * @return Record record containing all defined default values
     */
    public Record getDefaultValues(String actionClassName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultValues", new Object[]{actionClassName});
        }

        Record defaultValuesRec = new Record();

        OasisFields fields = getOasisFields(actionClassName);

        // Loop through each page field getting default values if specified
        Iterator itr = fields.keySet().iterator();
        while (itr.hasNext()) {
            String mapKey = (String) itr.next();
            if (mapKey != null) {
                if (fields.get(mapKey) instanceof OasisFormField) {
                    OasisFormField frmFld = (OasisFormField) fields.get(mapKey);
                    if (!StringUtils.isBlank(frmFld.getDefaultValue())) {
                        defaultValuesRec.setFieldValue(frmFld.getFieldId(), frmFld.getDefaultValue());
                    }
                }
            }
        }

        // Loop through all layers getting default values for layer fields if specified
        ArrayList layers = fields.getLayers();
        if (layers != null && layers.size() >= 1) {
            for (int i = 0; i < layers.size(); i++) {
                if (layers.get(i) instanceof WebLayer) {
                    ArrayList currentLayerFields = fields.getLayerFields(((WebLayer) layers.get(i)).getLayerId());
                    if (currentLayerFields != null && currentLayerFields.size() >= 1) {
                        for (int j = 0; j < currentLayerFields.size(); j++) {
                            if (currentLayerFields.get(j) instanceof OasisFormField) {
                                OasisFormField layerField = (OasisFormField) currentLayerFields.get(j);
                                if (!StringUtils.isBlank(layerField.getDefaultValue())) {
                                    defaultValuesRec.setFieldValue(layerField.getFieldId(), layerField.getDefaultValue());
                                }
                            }
                        }
                    }
                }
            }
        }


        l.exiting(getClass().getName(), "getDefaultValues", defaultValuesRec);
        return defaultValuesRec;
    }

    public String getFieldLabel(String actionClassName, String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDisplayableFieldName", new Object[]{fieldId,});
        }

        String fieldLabel = null;

        // Get fields
        OasisFields fields = getOasisFields(actionClassName);

        // Loop through all layers getting label for layer field if specified
        ArrayList layerIds = fields.getLayerIds();
        if (layerIds != null && layerIds.size() >= 1) {
            for (int i = 0; i < layerIds.size(); i++) {
                LinkedHashMap currentLayerFields = fields.getLayerFieldsMap((String) layerIds.get(i));
                if (currentLayerFields != null && currentLayerFields.containsKey(fieldId)) {
                    fieldLabel = ((OasisFormField) currentLayerFields.get(fieldId)).getLabel();
                }
            }
        }

        // Get the label from page if we did not find it in layere
        if (StringUtils.isBlank(fieldLabel)) {
            OasisFormField field = fields.getField(fieldId);
            fieldLabel = field.getLabel();
        }

        // Remove ":" and spece at the end
        if (!StringUtils.isBlank(fieldLabel)) {
            if(fieldLabel.endsWith(" "))
                fieldLabel = fieldLabel.substring(0, fieldLabel.length()-2);
            if(fieldLabel.endsWith("&nbsp;"))
                fieldLabel = fieldLabel.substring(0, fieldLabel.length()-7);
            if(fieldLabel.endsWith(":"))
                fieldLabel = fieldLabel.substring(0, fieldLabel.length()-2);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFieldLabel", fieldLabel);
        }
        return fieldLabel;
    }

    private OasisFields getOasisFields(String actionClassName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOasisFields", new Object[]{actionClassName,});
        }

        OasisFields fields = null;
        try {
            fields = null;
            String classKeyFields = actionClassName + IOasisAction.KEY_FIELDS;

            // Look for the Fields in the Request Storage in case they have already been loaded.
            if (RequestStorageManager.getInstance().isSetupForRequest() ) {
                if (RequestStorageManager.getInstance().has(IOasisAction.KEY_FIELDS)) {
                    OasisFields cachedFields = (OasisFields) RequestStorageManager.getInstance().get(IOasisAction.KEY_FIELDS);
                    if (cachedFields.getActionClassName().equals(actionClassName)) {
                        fields = cachedFields;
                        l.logp(Level.FINE, getClass().getName(), "getDefaultValues", "WorkbenchConfiguration.getDefaultValues: loaded the oasis fields from the RSM for " +
                            IOasisAction.KEY_FIELDS);
                    }
                }

                if (fields == null && RequestStorageManager.getInstance().has(classKeyFields) ) {
                    fields = (OasisFields) RequestStorageManager.getInstance().get(classKeyFields);
                    l.logp(Level.FINE, getClass().getName(), "getDefaultValues", "WorkbenchConfiguration.getDefaultValues: loaded the oasis fields from the RSM for " +
                            classKeyFields);
                }
            }

            if (fields == null) {
                // Get the oasis fields
                Connection conn = null;
                try {
                    conn = getReadOnlyConnection();
                    fields = OasisFields.createInstance(actionClassName, UserSessionManager.getInstance().getUserSession().getUserId(), conn);
                    RequestStorageManager.getInstance().set(classKeyFields, fields);
                    l.logp(Level.FINE, getClass().getName(), "getDefaultValues", "WorkbenchConfiguration.getDefaultValues: loaded the oasis fields directly.");
                }
                finally {
                    DatabaseUtils.close(conn);
                }
            }
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get the OasisFields.", e);
            l.throwing(getClass().getName(), "getOasisFields", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOasisFields", fields);
        }
        return fields;
    }

    /**
     * Get the connection from the ReadOnlyDataSource.
     */
    protected Connection getReadOnlyConnection() {
        l.entering(getClass().getName(), "getReadOnlyConnection");

        Connection conn = null;
        try {
            conn = getReadOnlyDataSource().getConnection();
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get a connection from the ReadOnlyDataSource", e);
            l.throwing(getClass().getName(), "getReadOnlyConnection", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getReadOnlyConnection", conn);
        }
        return conn;
    }

    /**
     * Load list of Values
     */
    public void loadListOfValues(String actionClassName, Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadListOfValues");
        }

        RequestStorageManager rsm = RequestStorageManager.getInstance();
        HttpServletRequest request = (HttpServletRequest)rsm.get(RequestStorageIds.HTTP_SEVLET_REQUEST);       
        OasisFields fields = getOasisFields(actionClassName);

        Connection conn = getReadOnlyConnection();
        try {
            ActionHelper.recordToBeans(request, record, fields);
            fields.getListOfValues(conn, null, request, true);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load the list of Values.", e);
            l.throwing(getClass().getName(), "loadListOfValues", ae);
            throw ae;
        }
        finally {
            DatabaseUtils.close(conn);
        }

        l.exiting(getClass().getName(), "loadListOfValues");
    }

    /**
     * Get list of values for a fieldId
     *
     * @param fieldId name of field for which we get the values.
     * @return ArrayList Array list of values for the input field.
     */
    public ArrayList getListOfValuesForField(String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getListOfValuesForField", new Object[]{fieldId});
        }

        RequestStorageManager rsm = RequestStorageManager.getInstance();
        HttpServletRequest request = (HttpServletRequest)rsm.get(RequestStorageIds.HTTP_SEVLET_REQUEST);

        ArrayList lov = (ArrayList) request.getAttribute(fieldId + "LOV");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getListOfValuesForField", lov);
        }

        return lov;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getReadOnlyDataSource() == null)
            throw new ConfigurationException("The required property 'readOnlyDataSource' is missing.");
    }

    public WorkbenchConfiguration() {
    }

    public DataSource getReadOnlyDataSource() {
        return m_readOnlyDataSource;
    }

    public void setReadOnlyDataSource(DataSource readOnlyDataSource) {
        m_readOnlyDataSource = readOnlyDataSource;
    }

    private DataSource m_readOnlyDataSource;

    private final Logger l = LogUtils.getLogger(getClass());
}
