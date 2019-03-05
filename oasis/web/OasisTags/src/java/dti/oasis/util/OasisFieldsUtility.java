package dti.oasis.util;

import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.tags.WebLayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Utility class for dealing with OasisFields object.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 10, 2004
 *
 * @author Gerald C. Carney
 */
/*
 * Revision Date         Revised By  Description
 * ----------------------------------------------------------------------------
 * <p/> 09/10/2004       GCC         Created.
 * <p/> 11/19/2004       GCC         Changed some string concatenation to use
 *                                   StringBuffer.
 * <p/> 02/04/2005       GCC         Overloaded setFieldsToReadOnly to add a
 *                                   boolean to control whether or not to make
 *                                   non-layer fields in the map read-only.
 * ----------------------------------------------------------------------------
*/

public class OasisFieldsUtility {

  /**
   * Sets all fields to read-only.
   * @param fields   OasisFields object.
   */
  public static void setFieldsToReadOnly(OasisFields fields) {
    ArrayList excludedFieldsList = null;
    OasisFieldsUtility.setFieldsToReadOnly(fields, false, excludedFieldsList, true);
  }

  /**
   * Sets all fields (including layers, if specified) to read-only.
   * @param fields   OasisFields object.
   * @param handleLayers   Set fields in layers to read/only, yes or no.
   */
  public static void setFieldsToReadOnly(OasisFields fields, boolean handleLayers) {
    ArrayList excludedFieldsList = null;
    OasisFieldsUtility.setFieldsToReadOnly(fields, handleLayers, excludedFieldsList, true);
  }

  /**
   * Sets all fields (excluding specified fields) to read-only.
   * @param fields   OasisFields object.
   * @param excludedFieldsList  ArrayList of strings with IDs of fields to be excluded.
   */
  public static void setFieldsToReadOnly(OasisFields fields, ArrayList excludedFieldsList) {
    OasisFieldsUtility.setFieldsToReadOnly(fields, false, excludedFieldsList, true);
  }

  /**
   * Sets all fields (including layers, if specified, and excluding specified fields) to read-only.
   * @param fields   OasisFields object.
   * @param handleLayers   Set fields in layers to read/only, yes or no.
   * @param excludedFieldsList  ArrayList of strings with IDs of fields to be excluded.
   */
  public static void setFieldsToReadOnly(OasisFields fields, boolean handleLayers,
    ArrayList excludedFieldsList) {
    OasisFieldsUtility.setFieldsToReadOnly(fields, handleLayers, excludedFieldsList, true);
  }

  /**
   * Sets all fields (including non-layer fields if specified, including layers, if specified) to read-only.
   * @param fields   OasisFields object.
   * @param handleLayers   Set fields in layers to read/only, yes or no.
   * @param handleNonLayerFields   Set fields in map that are not in layers to read/only, yes or no.
   */
  public static void setFieldsToReadOnly(OasisFields fields, boolean handleLayers,
      boolean handleNonLayerFields) {
    ArrayList excludedFieldsList = null;
    OasisFieldsUtility.setFieldsToReadOnly(fields, handleLayers, excludedFieldsList, handleNonLayerFields);
  }

  /**
   * Sets all fields (including non-layer fields, if specified, including layers,
   * if specified, and excluding specified fields) to read-only.
   * @param fields   OasisFields object.
   * @param handleLayers   Set fields in layers to read/only, yes or no.
   * @param excludedFieldsList  ArrayList of strings with IDs of fields to be excluded.
   * @param handleNonLayerFields   Set fields in map that are not in layers to read/only, yes or no.
   */
  public static void setFieldsToReadOnly(OasisFields fields, boolean handleLayers,
    ArrayList excludedFieldsList, boolean handleNonLayerFields) {
    String methodName = "setFieldsToReadOnly";
    Logger lggr = LogUtils.enterLog(OasisFieldsUtility.class, methodName,
      new Object [] { fields, new Boolean(handleLayers),
                      excludedFieldsList, new Boolean(handleNonLayerFields) } );
    boolean excludeFields = false;
    if (excludedFieldsList != null && excludedFieldsList.size() >= 1) {
      lggr.finer("at least one field is to be excluded");
      excludeFields = true;
    }
    if (fields != null) {
      if (handleNonLayerFields) {
        Iterator itr = fields.keySet().iterator();
        while (itr.hasNext()) {
          String mapKey = (String) itr.next();
          if (mapKey != null) {
            if (fields.get(mapKey) instanceof OasisFormField) {
              boolean makeFldReadOnly = true;
              OasisFormField frmFld = (OasisFormField) fields.get(mapKey);
              if (excludeFields) {
                for (int i = 0; i < excludedFieldsList.size(); i++) {
                  Object curExclFldNameObj = excludedFieldsList.get(i);
                  if (curExclFldNameObj instanceof String) {
                    String curExclFldNameStr = (String) curExclFldNameObj;
                    if (!StringUtils.isBlank(curExclFldNameStr) && curExclFldNameStr.equals(mapKey)) {
                      lggr.finer(new StringBuffer().append("field ").append(mapKey).append(" will be excluded").toString());
                      makeFldReadOnly = false;
                    }
                  }
                }
              }
              if (frmFld != null && makeFldReadOnly) {
                frmFld.setIsReadOnly(true);
                lggr.finer(new StringBuffer().append("field ").append(mapKey).append(" is now read only").toString());
              }
            }
          }
        }
      }
      if (handleLayers) {
        lggr.finer("dealing with layers");
        ArrayList layers = fields.getLayers();
        if (layers != null && layers.size() >= 1) {
          for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i) instanceof WebLayer) {
              ArrayList curLayerFlds = fields.getLayerFields(((WebLayer) layers.get(i)).getLayerId());
              if (curLayerFlds != null && curLayerFlds.size() >= 1) {
                for (int j = 0; j < curLayerFlds.size(); j++) {
                  if (curLayerFlds.get(j) instanceof OasisFormField) {
                    boolean makeFldReadOnly = true;
                    OasisFormField curFld = (OasisFormField) curLayerFlds.get(j);
                    String curFldName = curFld.getFieldId();
                    if (excludeFields) {
                      for (int k = 0; k < excludedFieldsList.size(); k++) {
                        Object curExclFldNameObj = excludedFieldsList.get(k);
                        if (curExclFldNameObj instanceof String) {
                          String curExclFldNameStr = (String) curExclFldNameObj;
                          if (!StringUtils.isBlank(curExclFldNameStr) && curExclFldNameStr.equals(curFldName)) {
                            lggr.finer(new StringBuffer().append("field ").append(curFldName).append(" will be excluded").toString());
                            makeFldReadOnly = false;
                          }
                        }
                      }
                    }
                    if (curFld != null && makeFldReadOnly) {
                      curFld.setIsReadOnly(true);
                      lggr.finer(new StringBuffer().append("field ").append(curFld.getFieldId()).append(" is now read-only").toString());
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    else {
      lggr.finer("fields object is null;  nothing to set");
    }
    lggr.exiting(OasisFieldsUtility.class.getName(), methodName);
    return;

  }
}
