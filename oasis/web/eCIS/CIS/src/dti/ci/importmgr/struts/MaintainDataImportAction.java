package dti.ci.importmgr.struts;

import dti.ci.importmgr.DataImportManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.data.ColumnNameToJavaFieldNameFormatter;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/23/14
 *
 * @author ldong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/26/2014       Elvin       Issue 157520: add some validations
 * 12/29/2014       Elvin       Issue 157520: remove zip code validation, zip code is not always numeric (q1q 1q1)
 * 02/07/2017       jld         Issue 181813. Corrections and additions for missing fields.
 * 07/05/2017       dpang       Issue 184234. Refactor getColumnInfo to improve performance.
 * ---------------------------------------------------
 */
public class MaintainDataImportAction extends CIBaseAction {
    /**
     * Unspecified
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return initPage(mapping, form, request, response);
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward initPage(ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        String methodName = "initPage";
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }
        Record inputRecord = getInputRecord(request);
        inputRecord.clear();
        String forwardString = "initPage";
        try {
            // Secure the page and get the fields.
            securePage(request, form);
            loadJSMessage();
            saveToken(request);

        } catch (Exception e) {
            forwardString = handleError(
                    AppException.UNEXPECTED_ERROR, "Failed to load Data Import page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (logger.isLoggable(Level.FINER)) {
            logger.exiting(getClass().getName(), "initPage", af);
        }
        return af;
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward processData(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        String methodName = "processData";
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }
        String forwardString = "processData";
        ColumnNameToJavaFieldNameFormatter colFormat = new ColumnNameToJavaFieldNameFormatter();
        try {

            if (hasValidSaveToken(request)) {
                // Secure the page and get the fields.
                securePage(request, form);

                RecordSet entityRs = new RecordSet();   // The RecordSet for Entity table
                RecordSet addressRs = new RecordSet();  // The RecordSet for Address table
                RecordSet phoneRs = new RecordSet();    // The RecordSet for Phone table
                RecordSet licenseRs = new RecordSet();  // The RecordSet for License table
                boolean processError = false;

                FormFile ff = (FormFile) ((DynaActionForm) form).get("dataFile");
                String fileName = ff.getFileName();
                String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
                if (!"xlsx".equalsIgnoreCase(extension)) {
                    MessageManager.getInstance().addErrorMessage("ci.import.process.invalid.fileType");
                    throw new ValidationException();
                }

                InputStream inp = ff.getInputStream();
                Workbook wb = WorkbookFactory.create(inp);

                //Process column definition sheet
                Sheet defineSheet = wb.getSheetAt(0);
                RecordSet defineRs = new RecordSet();

                for (Row row : defineSheet) {
                    if (row.getRowNum() > 0) {
                        Record rd = new Record();
                        rd.setFieldValue("COLUMN", row.getCell(0).getStringCellValue());
                        rd.setFieldValue("TABLE", row.getCell(1).getStringCellValue());
                        rd.setFieldValue("DBCOLUMN", row.getCell(2).getStringCellValue());
                        defineRs.addRecord(rd);
                    }
                }

                //Process data sheet
                Sheet dataSheet = wb.getSheetAt(1);
                int rowNum = dataSheet.getLastRowNum();
                int entityCount = 1;
                int addressCount = 1;
                int phoneCount = 1;
                int licenseCount = 1;
                boolean validationFail = false;
                StringBuffer errorDateFields = new StringBuffer("");
                StringBuffer errorNumberFields = new StringBuffer("");
                StringBuffer requiredFields = new StringBuffer("");
                Map<String, Map> columnInfoMap = new HashMap<>();
                Set<String> tableNameSet = new HashSet<>();

                for (int i = 0; i <= rowNum; i++) {
                    Record entityRd = new Record();
                    Record addressRd = new Record();
                    Record phoneRd = new Record();
                    Record licenseRd = new Record();
                    entityRd.setFieldValue("entitySelect", "0");
                    entityRd.setFieldValue("entityStatus", "");
                    addressRd.setFieldValue("addressSelect", "0");
                    addressRd.setFieldValue("addressStatus", "");
                    phoneRd.setFieldValue("phoneSelect", "0");
                    phoneRd.setFieldValue("phoneStatus", "");
                    licenseRd.setFieldValue("licenseSelect", "0");
                    licenseRd.setFieldValue("licenseStatus", "");
                    boolean entityHasRd = false;
                    boolean addressHasRd = false;
                    boolean phoneHasRd = false;
                    boolean licenseHasRd = false;
                    boolean entityMissingReq = false;
                    boolean addressMissingReq = false;
                    boolean phoneMissingReq = false;
                    boolean licenseMissingReq = false;
                    boolean entityLengthViolate = false;
                    boolean addressLengthViolate = false;
                    boolean phoneLengthViolate = false;
                    boolean licenseLengthViolate = false;

                    if (i > 0) {
                        Row row = dataSheet.getRow(i);
                        if (row == null) {
                            //Skip rows without value
                            continue;
                        }

                        for (int j = 0; j < defineRs.getSize(); j++) {
                            Record record = defineRs.getRecord(j);
                            String pos = record.getStringValue("COLUMN") + (i + 1);
                            CellReference cr = new CellReference(pos);
                            Cell cell = row.getCell(cr.getCol());
                            String tempValue = "";
                            if (cell != null) {
                                if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
                                    tempValue = String.valueOf((int) cell.getNumericCellValue());
                                } else if (cell.getCellType() == cell.CELL_TYPE_STRING) {
                                    tempValue = cell.getStringCellValue();
                                }
                            }
                            Map columnInfo = getColumnInfo(record.getStringValue("TABLE"), record.getStringValue("DBCOLUMN"), columnInfoMap, tableNameSet);


                            if ("ENTITY".equalsIgnoreCase(record.getStringValue("TABLE"))) {
                                if (record.getStringValue("DBCOLUMN").equalsIgnoreCase("LEGACY_DATA_ID")) {
                                    entityRd.setFieldValue(colFormat.format("ENTITY_" + record.getStringValue("DBCOLUMN")), tempValue);
                                } else {
                                    entityRd.setFieldValue(colFormat.format(record.getStringValue("DBCOLUMN")), tempValue);
                                }
                                if (!StringUtils.isBlank(tempValue)) {
                                    if ("VARCHAR2".equalsIgnoreCase((String) columnInfo.get("TYPE_NAME"))) {
                                        if (tempValue.length() > Integer.parseInt(String.valueOf(columnInfo.get("COLUMN_SIZE"))))
                                            entityLengthViolate = true;
                                    } else if ("DATE".equalsIgnoreCase((String) columnInfo.get("TYPE_NAME"))) {
                                        if (!FormatUtils.isDate(tempValue)) {
                                            errorDateFields.append(" Row " + i + ": " + record.getStringValue("DBCOLUMN") + ";");
                                            validationFail = true;
                                        }
                                    }
                                    entityHasRd = true;
                                } else {
                                    if ("0".equalsIgnoreCase((String) columnInfo.get("NULLABLE")))
                                        entityMissingReq = true;
                                }
                            } else if ("ADDRESS_BASE".equalsIgnoreCase(record.getStringValue("TABLE"))) {
                                //Keep the address prefix for legacy_data_id, effective_from_date and effective_to_date to
                                //distinguish columns of the same names on other tables
                                if (record.getStringValue("DBCOLUMN").equalsIgnoreCase("LEGACY_DATA_ID") ||
                                        record.getStringValue("DBCOLUMN").equalsIgnoreCase("EFFECTIVE_FROM_DATE") ||
                                        record.getStringValue("DBCOLUMN").equalsIgnoreCase("EFFECTIVE_TO_DATE")) {
                                    addressRd.setFieldValue(colFormat.format("ADDRESS_" + record.getStringValue("DBCOLUMN")), tempValue);
                                } else {
                                    addressRd.setFieldValue(colFormat.format(record.getStringValue("DBCOLUMN")), tempValue);
                                }
                                if (!StringUtils.isBlank(tempValue)) {
                                    if ("VARCHAR2".equalsIgnoreCase((String) columnInfo.get("TYPE_NAME"))) {
                                        if (tempValue.length() > Integer.parseInt(String.valueOf(columnInfo.get("COLUMN_SIZE"))))
                                            addressLengthViolate = true;
                                    } else if ("DATE".equalsIgnoreCase((String) columnInfo.get("TYPE_NAME"))) {
                                        if (!FormatUtils.isDate(tempValue)) {
                                            errorDateFields.append(" Row " + i + ": " + record.getStringValue("DBCOLUMN") + ";");
                                            validationFail = true;
                                        }
                                    }
                                    addressHasRd = true;
                                } else {
                                    if ("0".equalsIgnoreCase((String) columnInfo.get("NULLABLE")))
                                        addressMissingReq = true;
                                }
                            } else if ("PHONE_NUMBER".equalsIgnoreCase(record.getStringValue("TABLE"))) {
                                phoneRd.setFieldValue(colFormat.format(record.getStringValue("DBCOLUMN")), tempValue);
                                if (!StringUtils.isBlank(tempValue)) {
                                    if ("VARCHAR2".equalsIgnoreCase((String) columnInfo.get("TYPE_NAME"))) {
                                        if (tempValue.length() > Integer.parseInt(String.valueOf(columnInfo.get("COLUMN_SIZE"))))
                                            phoneLengthViolate = true;
                                    } else if ("DATE".equalsIgnoreCase((String) columnInfo.get("TYPE_NAME"))) {
                                        if (!FormatUtils.isDate(tempValue)) {
                                            errorDateFields.append(" Row " + i + ": " + record.getStringValue("DBCOLUMN") + ";");
                                            validationFail = true;
                                        }
                                    }
                                    phoneHasRd = true;
                                } else {
                                    if ("0".equalsIgnoreCase((String) columnInfo.get("NULLABLE")))
                                        phoneMissingReq = true;
                                }
                            } else if ("LICENSE_PROFILE".equalsIgnoreCase(record.getStringValue("TABLE"))) {
                                licenseRd.setFieldValue(colFormat.format(record.getStringValue("DBCOLUMN")), tempValue);
                                if (!StringUtils.isBlank(tempValue)) {
                                    if ("VARCHAR2".equalsIgnoreCase((String) columnInfo.get("TYPE_NAME"))) {
                                        if (tempValue.length() > Integer.parseInt(String.valueOf(columnInfo.get("COLUMN_SIZE"))))
                                            licenseLengthViolate = true;
                                    } else if ("DATE".equalsIgnoreCase((String) columnInfo.get("TYPE_NAME"))) {
                                        if (!FormatUtils.isDate(tempValue)) {
                                            errorDateFields.append(" Row " + i + ": " + record.getStringValue("DBCOLUMN") + ";");
                                            validationFail = true;
                                        }
                                    }
                                    licenseHasRd = true;
                                } else {
                                    if ("0".equalsIgnoreCase((String) columnInfo.get("NULLABLE")))
                                        licenseMissingReq = true;
                                }
                            }
                        }

                        if (entityHasRd) {
                            entityRd.setFieldValue("entityId", String.valueOf(entityCount)); // Entity Pk
                            if (!entityRd.hasField("firstName") || !entityRd.hasField("lastName") || !entityRd.hasField("organizationName")
                                    || (StringUtils.isBlank(entityRd.getStringValue("firstName"))
                                    && StringUtils.isBlank(entityRd.getStringValue("lastName"))
                                    && StringUtils.isBlank(entityRd.getStringValue("organizationName")))) {
                                entityRd.setFieldValue("entityStatus", MessageManager.getInstance().formatMessage("ci.import.process.required.field"));
                                processError = true;
                            }
                            if (entityLengthViolate) {
                                entityRd.setFieldValue("entityStatus", MessageManager.getInstance().formatMessage("ci.import.process.length.violate"));
                                processError = true;
                            }
                            if (entityMissingReq) {
                                entityRd.setFieldValue("entityStatus", MessageManager.getInstance().formatMessage("ci.import.process.required.field"));
                                processError = true;
                            }
                            if (!StringUtils.isBlank(entityRd.getStringValue("organizationName"))) {
                                entityRd.setFieldValue("entityType", "O");
                            } else {
                                entityRd.setFieldValue("entityType", "P");
                            }
                            entityRs.addRecord(entityRd);
                            entityCount++;
                        }
                        if (addressHasRd) {
                            addressRd.setFieldValue("sourceRecordId", String.valueOf(entityCount - 1)); // Entity Fk
                            addressRd.setFieldValue("addressId", String.valueOf(addressCount)); // Address Pk
                            if (addressLengthViolate) {
                                addressRd.setFieldValue("addressStatus", MessageManager.getInstance().formatMessage("ci.import.process.length.violate"));
                                processError = true;
                            }
                            if (addressMissingReq) {
                                addressRd.setFieldValue("addressStatus", MessageManager.getInstance().formatMessage("ci.import.process.required.field"));
                                processError = true;
                            }
                            if (StringUtils.isBlank(addressRd.getStringValue("ADDRESSTYPECODE"))) {
                                requiredFields.append(" Row " + i + ": Address Type;");
                                validationFail = true;
                            }
                            if (StringUtils.isBlank(addressRd.getStringValue("PRIMARYADDRESSB"))) {
                                requiredFields.append(" Row " + i + ": Primary Address?;");
                                validationFail = true;
                            }
                            if (StringUtils.isBlank(addressRd.getStringValue("ADDRESSLINE1"))) {
                                requiredFields.append(" Row " + i + ": Address Line 1;");
                                validationFail = true;
                            }
                            if (StringUtils.isBlank(addressRd.getStringValue("CITY"))) {
                                requiredFields.append(" Row " + i + ": City;");
                                validationFail = true;
                            }
                            if (StringUtils.isBlank(addressRd.getStringValue("STATECODE"))) {
                                requiredFields.append(" Row " + i + ": State;");
                                validationFail = true;
                            } else {
                                // check whether state and county (if has) are valid, if not, process error
                                if (!getDataImportManager().isValidStateAndCounty(addressRd.getStringValue("STATECODE"), addressRd.getStringValue("COUNTYCODE"))) {
                                    addressRd.setFieldValue("addressStatus", MessageManager.getInstance().formatMessage("ci.import.process.invalid.stateAndCounty"));
                                    processError = true;
                                }
                            }
                            addressRs.addRecord(addressRd);
                            addressCount++;
                        }
                        if (phoneHasRd) {
                            phoneRd.setFieldValue("sourceRecordId", String.valueOf(addressCount - 1)); // Address Fk
                            phoneRd.setFieldValue("phoneNumberId", String.valueOf(phoneCount)); // Phone Pk
                            if (phoneLengthViolate) {
                                phoneRd.setFieldValue("phoneStatus", MessageManager.getInstance().formatMessage("ci.import.process.length.violate"));
                                processError = true;
                            }
                            if (phoneMissingReq) {
                                phoneRd.setFieldValue("phoneStatus", MessageManager.getInstance().formatMessage("ci.import.process.required.field"));
                                processError = true;
                            }
                            if (!StringUtils.isNumeric(phoneRd.getStringValue("AREACODE"), true)) {
                                errorNumberFields.append(" Row " + i + ": Area Code;");
                                validationFail = true;
                            }
                            if (!StringUtils.isNumeric(phoneRd.getStringValue("PHONENUMBER"), true)) {
                                errorNumberFields.append(" Row " + i + ": Phone Number;");
                                validationFail = true;
                            }
                            if (!StringUtils.isNumeric(phoneRd.getStringValue("PHONEEXTENSION"), true)) {
                                errorNumberFields.append(" Row " + i + ": Phone Extension;");
                                validationFail = true;
                            }
                            phoneRs.addRecord(phoneRd);
                            phoneCount++;
                        }
                        if (licenseHasRd) {
                            licenseRd.setFieldValue("entityId", String.valueOf(entityCount - 1)); // Entity Fk
                            licenseRd.setFieldValue("licenseProfileId", String.valueOf(licenseCount)); // Address Pk
                            if (licenseLengthViolate) {
                                licenseRd.setFieldValue("licenseStatus", MessageManager.getInstance().formatMessage("ci.import.process.length.violate"));
                                processError = true;
                            }
                            if (licenseMissingReq) {
                                licenseRd.setFieldValue("licenseStatus", MessageManager.getInstance().formatMessage("ci.import.process.required.field"));
                                processError = true;
                            }
                            licenseRs.addRecord(licenseRd);
                            licenseCount++;
                        }
                    }
                }

                if (validationFail) {
                    if (!StringUtils.isBlank(requiredFields.toString())) {
                        MessageManager.getInstance().addErrorMessage("ci.import.process.field.required", new String[]{requiredFields.toString()});
                    }
                    if (!StringUtils.isBlank(errorDateFields.toString())) {
                        MessageManager.getInstance().addErrorMessage("ci.import.process.date.invalid", new String[]{errorDateFields.toString()});
                    }
                    if (!StringUtils.isBlank(errorNumberFields.toString())) {
                        MessageManager.getInstance().addErrorMessage("ci.import.process.number.invalid", new String[]{errorNumberFields.toString()});
                    }
                    throw new ValidationException();
                }

                if (processError) {
                    MessageManager.getInstance().addErrorMessage("ci.import.process.result.error");
                } else {
                    boolean successFlag = getDataImportManager().saveDataImport(entityRs, addressRs, phoneRs, licenseRs);
                    if (successFlag) {
                        MessageManager.getInstance().addInfoMessage("ci.import.process.result.success");
                    } else {
                        MessageManager.getInstance().addErrorMessage("ci.import.process.result.error");
                    }
                }

                // Set loaded data into request
                setDataBean(request, entityRs, ENTITY_GRID_ID);
                setDataBean(request, addressRs, ADDRESS_GRID_ID);
                setDataBean(request, phoneRs, PHONE_GRID_ID);
                setDataBean(request, licenseRs, LICENSE_GRID_ID);

                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, ENTITY_GRID_ID);
                loadGridHeader(request, null, ENTITY_GRID_ID, ENTITY_GRID_LAYER_ID);
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, ADDRESS_GRID_ID);
                loadGridHeader(request, null, ADDRESS_GRID_ID, ADDRESS_GRID_LAYER_ID);
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PHONE_GRID_ID);
                loadGridHeader(request, null, PHONE_GRID_ID, PHONE_GRID_LAYER_ID);
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, LICENSE_GRID_ID);
                loadGridHeader(request, null, LICENSE_GRID_ID, LICENSE_GRID_LAYER_ID);

                /* Load LOV */
                loadListOfValues(request, form);

                loadJSMessage();
            }
            saveToken(request);

        } catch (ValidationException ve) {
            return mapping.findForward("initPage");
        } catch (Exception e) {
            forwardString = handleError(
                    AppException.UNEXPECTED_ERROR, "Failed to Process Data Import page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (logger.isLoggable(Level.FINER)) {
            logger.exiting(getClass().getName(), "processData", af);
        }

        return af;
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward saveData(ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        String methodName = "saveData";
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveData";

        try {

            if (hasValidSaveToken(request)) {
                // Secure the page and get the fields.
                securePage(request, form);

                RecordSet entityRs = new RecordSet();   // The RecordSet for Entity table
                RecordSet addressRs = new RecordSet();  // The RecordSet for Address table
                RecordSet phoneRs = new RecordSet();    // The RecordSet for Phone table
                RecordSet licenseRs = new RecordSet();  // The RecordSet for License table

                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, ENTITY_GRID_ID);
                entityRs = getInputRecordSet(request, ENTITY_GRID_ID);
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, ADDRESS_GRID_ID);
                addressRs = getInputRecordSet(request, ADDRESS_GRID_ID);

                if (request.getParameter(PHONE_GRID_ID + TXT_XML_SUFFIX) != null) {
                    RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PHONE_GRID_ID);
                    phoneRs = getInputRecordSet(request, PHONE_GRID_ID);
                }
                //The excel used to import entity may not contain license data.
                if (request.getParameter(LICENSE_GRID_ID + TXT_XML_SUFFIX) != null) {
                    RequestStorageManager.getInstance().set(CURRENT_GRID_ID, LICENSE_GRID_ID);
                    licenseRs = getInputRecordSet(request, LICENSE_GRID_ID);
                }

                getDataImportManager().saveDataImport(entityRs, addressRs, phoneRs, licenseRs);
                MessageManager.getInstance().addInfoMessage("ci.import.process.result.success");

                // Set loaded data into request
                setDataBean(request, entityRs, ENTITY_GRID_ID);
                setDataBean(request, addressRs, ADDRESS_GRID_ID);
                setDataBean(request, phoneRs, PHONE_GRID_ID);
                setDataBean(request, licenseRs, LICENSE_GRID_ID);

                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, ENTITY_GRID_ID);
                loadGridHeader(request, null, ENTITY_GRID_ID, ENTITY_GRID_LAYER_ID);
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, ADDRESS_GRID_ID);
                loadGridHeader(request, null, ADDRESS_GRID_ID, ADDRESS_GRID_LAYER_ID);
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PHONE_GRID_ID);
                loadGridHeader(request, null, PHONE_GRID_ID, PHONE_GRID_LAYER_ID);
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, LICENSE_GRID_ID);
                loadGridHeader(request, null, LICENSE_GRID_ID, LICENSE_GRID_LAYER_ID);

                /* Load LOV */
                loadListOfValues(request, form);

                loadJSMessage();
            }
            saveToken(request);

        } catch (Exception e) {
            forwardString = handleError(
                    AppException.UNEXPECTED_ERROR, "Failed to Save Data Import page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (logger.isLoggable(Level.FINER)) {
            logger.exiting(getClass().getName(), "saveData", af);
        }
        return af;
    }

    /**
     * getColumnInfo
     *
     * @param tableName
     * @param columnName
     * @param columnInfoMap
     * @param tableNameSet
     *
     * @return
     */
    private Map getColumnInfo(String tableName, String columnName, Map<String, Map> columnInfoMap, Set<String> tableNameSet)
            throws Exception {
        String methodName = "getColumnInfo";
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(getClass().getName(), methodName, new Object[]{tableName, columnName, columnInfoMap, tableNameSet});
        }

        String fullColumnName = tableName + TABLE_COLUMN_SEPARATER + columnName;
        if (columnInfoMap.containsKey(fullColumnName)) {
            return columnInfoMap.get(fullColumnName);

            //If the table doesn't have this column, as we have fetched all the column info of this table, return an empty map.
        } else if (tableNameSet.contains(tableName)) {
            return Collections.EMPTY_MAP;
        }

        tableNameSet.add(tableName);
        // Get table column definition for validation
        Connection conn = this.getReadOnlyConnection();
        DatabaseMetaData meta = null;
        ResultSet tableMeta = null;
        try {
            meta = conn.getMetaData();
            tableMeta = meta.getColumns(null, null, tableName, null);

            while (tableMeta.next()) {
                Map columnInfo = new HashMap();
                columnInfo.put("NULLABLE", String.valueOf(tableMeta.getInt("NULLABLE")));
                columnInfo.put("DATA_TYPE", tableMeta.getInt("DATA_TYPE"));
                columnInfo.put("TYPE_NAME", tableMeta.getString("TYPE_NAME"));
                columnInfo.put("COLUMN_SIZE", tableMeta.getInt("COLUMN_SIZE"));

                columnInfoMap.put(tableName + TABLE_COLUMN_SEPARATER + tableMeta.getString("COLUMN_NAME"), columnInfo);
            }
        } catch (Exception ex) {
            logger.throwing(this.getClass().getName(), methodName, ex);
            throw ex;
        } finally {
            if (tableMeta != null)
                tableMeta.close();
        }

        Map columnInfo = columnInfoMap.get(fullColumnName);
        return columnInfo == null ? Collections.EMPTY_MAP : columnInfo;
    }

    protected void loadJSMessage() {
        MessageManager.getInstance().addJsMessage("ci.import.error.file.isRequired");
        MessageManager.getInstance().addJsMessage("cs.changes.lost.confirm");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("ci.import.process.duplicate.select.error");
    }

    public String getAnchorColumnName() {
        return getAnchorColumnName((String) RequestStorageManager.getInstance().get(CURRENT_GRID_ID));
    }

    public String getAnchorColumnName(String gridId) {
        if (ENTITY_GRID_ID.equals(gridId)) {
            return getEntityAnchorColumnName();
        } else if (ADDRESS_GRID_ID.equals(gridId)) {
            return getAddressAnchorColumnName();
        } else if (PHONE_GRID_ID.equals(gridId)) {
            return getPhoneAnchorColumnName();
        } else if (LICENSE_GRID_ID.equals(gridId)) {
            return getLicenseAnchorColumnName();
        } else {
            return super.getAnchorColumnName();
        }

    }

    public String getEntityAnchorColumnName() {
        return entityAnchorColumnName;
    }

    public void setEntityAnchorColumnName(String entityAnchorColumnName) {
        this.entityAnchorColumnName = entityAnchorColumnName;
    }

    public String getAddressAnchorColumnName() {
        return addressAnchorColumnName;
    }

    public void setAddressAnchorColumnName(String addressAnchorColumnName) {
        this.addressAnchorColumnName = addressAnchorColumnName;
    }

    public String getPhoneAnchorColumnName() {
        return phoneAnchorColumnName;
    }

    public void setPhoneAnchorColumnName(String phoneAnchorColumnName) {
        this.phoneAnchorColumnName = phoneAnchorColumnName;
    }

    public String getLicenseAnchorColumnName() {
        return licenseAnchorColumnName;
    }

    public void setLicenseAnchorColumnName(String licenseAnchorColumnName) {
        this.licenseAnchorColumnName = licenseAnchorColumnName;
    }

    private String entityAnchorColumnName;
    private String addressAnchorColumnName;
    private String phoneAnchorColumnName;
    private String licenseAnchorColumnName;

    private static final String ENTITY_GRID_ID = "entityGrid";
    private static final String ENTITY_GRID_LAYER_ID = "CI_IMPORT_ENTITY";

    private static final String ADDRESS_GRID_ID = "addressGrid";
    private static final String ADDRESS_GRID_LAYER_ID = "CI_IMPORT_ADDRESS";

    private static final String PHONE_GRID_ID = "phoneGrid";
    private static final String PHONE_GRID_LAYER_ID = "CI_IMPORT_PHONE";

    private static final String LICENSE_GRID_ID = "licenseGrid";
    private static final String LICENSE_GRID_LAYER_ID = "CI_IMPORT_LICENSE";

    private static final String CURRENT_GRID_ID = "currentGridId";
    private static final String TABLE_COLUMN_SEPARATER = ".";
    private static final String TXT_XML_SUFFIX = "txtXML";

    private final Logger logger = LogUtils.getLogger(getClass());

    public DataImportManager getDataImportManager() {
        return dataImportManager;
    }

    public void setDataImportManager(DataImportManager dataImportManager) {
        this.dataImportManager = dataImportManager;
    }

    private DataImportManager dataImportManager;

}
