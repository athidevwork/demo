package dti.oasis.jsf;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.Message;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base View
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/27/12
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/27/2016       Parker      Issue#177786 Remove final String in local method
 *
 * ---------------------------------------------------
 */
public abstract class JSFBaseView implements JSFApplicationIds {

    /**
     * override this method to add common logic for page load
     */
    public abstract void commonOnPageLoad();

    /**
     * override this method to add page logic for page load
     */
    public abstract void handleOnPageLoad();

    /**
     * override this method to add common logic for action
     *
     * @param event
     */
    public abstract void commonOnAction(ActionEvent event);

    /**
     * override this method to add page logic for action
     *
     * @param event
     */
    public abstract void handleOnAction(ActionEvent event);

    /**
     * override this method to add common logic for data change
     *
     * @param event
     */
    public abstract void commonOnChange(AjaxBehaviorEvent event);

    /**
     * override this method to add page logic for data change
     *
     * @param event
     */
    public abstract void handleOnChange(AjaxBehaviorEvent event);

    /**
     * base logic for data change
     *
     * @param event
     */
    public void baseOnChange(AjaxBehaviorEvent event) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "baseOnChange", new Object[]{event});
        }
        try {
            commonOnChange(event);
            handleOnChange(event);
            dataChanged = true;
        } catch (Exception e) {
//            l.throwing(getClass().getName(), "baseOnChange", e);
            handleException("baseOnChange", e);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "baseOnChange");
        }
    }

    @PostConstruct
    public void baseOnPageLoad() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "baseOnPageLoad", "@@@@@@@@@@@@PostConstruct");
        }
        try {
            commonOnPageLoad();
            setPageHelpURL();
            setTopNavMenu();
            setApplicationId();
            setApplicationTitle();
            setApplicationName();
            setProductLogo();
            setLogoTipInfo();
            setModuleId();
            handleOnPageLoad();
        } catch (Exception e) {
//            l.throwing(getClass().getName(), "baseOnPageLoad", e);
            handleException("baseOnPageLoad", e);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "baseOnPageLoad");
        }
    }

    @PreDestroy
    public void baseOnPageUnLoad() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.logp(Level.FINER, getClass().getName(), "baseOnPageUnLoad", "@@@@@@@@@@@@PreDestroy");
        }
        try {
            // for future use
        } catch (Exception e) {
//            l.throwing(getClass().getName(), "baseOnPageUnLoad", e);
            handleException("baseOnPageUnLoad", e);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "baseOnPageUnLoad");
        }
    }

    /**
     * base logic for actions
     *
     * @param event
     */
    public void baseOnAction(ActionEvent event) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "baseOnAction", new Object[]{event});
        }
        try {
            lastActionSuccess = false;
            commonOnAction(event);
            handleOnAction(event);
            lastActionSuccess = true;
        } catch (Exception e) {
            l.throwing(getClass().getName(), "baseOnAction", e);
            handleException("baseOnAction", e);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "baseOnAction");
        }
    }


    /**
     * base logic for row selection
     *
     * @param event
     */
    public void baseOnRowSelect(SelectEvent event) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "baseOnRowSelect", new Object[]{event});
        }
        handleOnRowSelect(event);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "baseOnRowSelect");
        }
    }

    /**
     * override this method to add logic for each page
     *
     * @param event
     */
    public void handleOnRowSelect(SelectEvent event) {
        // do nothing
    }

    /**
     * base logic for page change
     *
     * @param event
     */
    public void baseOnPageChange(PageEvent event) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "baseOnPageChange", new Object[]{event});
        }
        try {
            DataTable dataTable = (DataTable) event.getSource();
            handleOnPageChange(event, dataTable);
        } catch (Exception e) {
            l.throwing(getClass().getName(), "baseOnPageChange", e);
            handleException("baseOnPageChange", e);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "baseOnPageChange");
        }
    }

    /**
     * override this method to add logic for page change
     *
     * @param event
     * @param dataTable
     */
    public void handleOnPageChange(PageEvent event, DataTable dataTable) {
        // do nothing
    }

    /**
     * base logic for sort
     *
     * @param event
     */
    public void baseOnSort(SortEvent event) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "baseOnSort", new Object[]{event});
        }
        handleOnSort(event);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "baseOnSort");
        }
    }

    /**
     * override this method to add logic for sorting
     *
     * @param event
     */
    public void handleOnSort(SortEvent event) {
        // do nothing
    }

    /**
     * Base Excel Post-Processor
     * Workaround: Currently Primefaces Don't Support Column Headers and Process Visible Components within Columns
     *
     * @param document
     * @param columnNames
     */
    protected void basePostProcessXLS(Object document, String[] columnNames) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "basePostProcessXLS", new Object[]{document, columnNames});
        }

        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);

        //Create Styles
        CellStyle cs;
        CellStyle csBold;

        //Bold Fond
        Font bold = wb.createFont();
        bold.setBoldweight(Font.BOLDWEIGHT_BOLD);

        //Bold style
        csBold = wb.createCellStyle();
//        csBold.setBorderBottom(CellStyle.BORDER_THIN);
        csBold.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        csBold.setFont(bold);

        int rowIdx = 0;

        //Get Mode
        String displayMode = ApplicationContext.getInstance().getProperty("gridExportExcel.infoHeaderMode", "HEADER");

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "basePostProcessXLS", "displayMode: " + displayMode);
        }

        if (!displayMode.equalsIgnoreCase("NONE")) {
            //Get current Date and Time
            java.util.Date date = new java.util.Date(System.currentTimeMillis());
            String dateString = dti.oasis.util.FormatUtils.formatDateTimeForDisplay(date);

            //Get User
            String exportUser = "";
            OasisUser user = (dti.oasis.util.OasisUser) getRequest().getSession().getAttribute(dti.oasis.struts.IOasisAction.KEY_OASISUSER);

            if (user != null) {
                exportUser = user.getUserName() + "(" + user.getUserId() + ")";
            } else {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "basePostProcessXLS", "User IS NULL");
                }
            }

            if (displayMode.equalsIgnoreCase("HEADER")) {
                Header header = sheet.getHeader();
                header.setLeft(HSSFHeader.fontSize((short) 9) + "Exported By: " + exportUser + "\n" + "Exported From: " + getPageTitle());
                header.setRight(HSSFHeader.fontSize((short) 9) + "Exported On: " + dateString);
            } else if (displayMode.equalsIgnoreCase("FOOTER")) {
                Footer footer = sheet.getFooter();
                footer.setLeft(HSSFFooter.fontSize((short) 9) + "Exported By: " + exportUser + "\n" + "Exported From: " + getPageTitle());
                footer.setRight(HSSFFooter.fontSize((short) 9) + "Exported On: " + dateString);
            } else if (displayMode.equalsIgnoreCase("BODY")) {
                //Shift down four rows to provide empty space
                sheet.shiftRows(0, sheet.getLastRowNum(), 4);

                Row infoRow;
                Cell infoCell;

                //First header row
                infoRow = sheet.createRow(rowIdx++);
                infoCell = infoRow.createCell(0);
                infoCell.setCellValue("Exported By:");
                infoCell = infoRow.createCell(1);
                infoCell = infoRow.createCell(2);
                infoCell.setCellValue(exportUser);

                //Second header row
                infoRow = sheet.createRow(rowIdx++);
                infoCell = infoRow.createCell(0);
                infoCell.setCellValue("Exported On:");
                infoCell = infoRow.createCell(1);
                infoCell = infoRow.createCell(2);
                infoCell.setCellValue(dateString);

                //Third header row
                infoRow = sheet.createRow(rowIdx++);
                infoCell = infoRow.createCell(0);
                infoCell.setCellValue("Exported From:");
                infoCell = infoRow.createCell(2);
                infoCell.setCellValue(getPageTitle());

                //Empty row
                rowIdx++;
            }
        }

        HSSFRow header = sheet.getRow(rowIdx);
        if (columnNames.length >= header.getPhysicalNumberOfCells()) {
            for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
                Cell cell = header.getCell(i);
                cell.setCellStyle(csBold);
                cell.setCellValue(columnNames[i]);
            }
        } else {
            l.logp(Level.INFO, getClass().getName(), "basePostProcessXLS", "Number of Header is less than number of cells.");
        }

        commonPostProcessXLS(document, columnNames);
        handlePostProcessXLS(document, columnNames);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "basePostProcessXLS");
        }
    }

    /**
     * override this method to add logic for Excel Post-Processing
     *
     * @param document
     * @param columnNames
     */
    public void handlePostProcessXLS(Object document, String[] columnNames) {
        // do nothing
    }

    /**
     * override this method to add common logic for Excel Post-Processing
     */
    public abstract void commonPostProcessXLS(Object document, String[] columnNames);

    /**
     * filter result by date
     */
    public void baseOnDateSelect(SelectEvent event) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "baseOnDateSelect", new Object[]{event});
        }
        String id = ((Calendar) event.getSource()).getId();
        handleOnDateSelect(id, (Date) event.getObject(), event);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "baseOnDateSelect");
        }
    }

    /**
     * override this method to add logic for date selection
     *
     * @param id
     * @param date
     * @param event
     */
    public void handleOnDateSelect(String id, Date date, SelectEvent event) {
        // do nothing
    }

    /**
     * set data table to display first page
     *
     * @param dataTableId
     */
    protected void gotoFirstPage(String dataTableId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "gotoFirstPage", new Object[]{dataTableId});
        }
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(dataTableId);
        dataTable.setFirst(0);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "gotoFirstPage");
        }
    }

    /**
     * set data table to display last page
     *
     * @param dataTableId
     */
    protected void gotoLastPage(String dataTableId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "gotoLastPage", new Object[]{dataTableId});
        }
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(dataTableId);
        int first = 1;
        if (dataTable.getRowCount() % dataTable.getRows() == 0) {
            first = (dataTable.getRowCount() - dataTable.getRows());
        } else {
            first = (dataTable.getRowCount() / dataTable.getRows()) * dataTable.getRows();
        }
        dataTable.setFirst(first);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "gotoLastPage");
        }
    }


    /**
     * select first record on page N
     *
     * @param dataTableModel
     * @param page           start from 0 (first page)
     * @param <T>
     * @return
     */
    protected <T> T selectFirstRowOnPage(DataTableModel<T> dataTableModel, int page, int rowsOnEachPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "selectFirstRowOnPage", new Object[]{dataTableModel, page, rowsOnEachPage});
        }
        T record = null;
        List<T> list = dataTableModel.getDisplayList();
        int position = page * rowsOnEachPage;
        if (list.size() >= (position + 1)) {
            record = list.get(position);
            dataTableModel.setSelectedRow(record);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "selectFirstRowOnPage", record);
        }
        return record;
    }

    public String displayFirstCharacters(String inputString, int maxLength) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "displayFirstCharacters", new Object[]{inputString, maxLength});
        }
        if (inputString.length() >= maxLength)
            inputString = inputString.substring(0, maxLength - 3) + "...";
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "displayFirstCharacters", inputString);
        }
        return inputString;
    }

    /**
     * get component
     *
     * @param id
     * @return
     */
    protected UIComponent getPageComponent(String id) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPageComponent", new Object[]{id});
        }
        UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent(id);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPageComponent", component);
        }
        return component;
    }

    /**
     * set ajax return parameters
     *
     * @param key
     * @param object
     */
    protected void setCallBackParam(String key, Object object) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setCallBackParam", new Object[]{key, object});
        }
        RequestContext.getCurrentInstance().addCallbackParam(key, object);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setCallBackParam");
        }
    }

    /**
     * set warning message in dialog. It will be displayed in dialog on client side
     *
     */
    protected void setWarningMessageInDialog() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setWarningMessageInDialog");
        }
        StringBuffer message = new StringBuffer();
        Iterator it = MessageManager.getInstance().getWarningMessages();
        while (it.hasNext()) {
            Message me = (Message) it.next();
            message.append(me.getMessage()).append("xyzXYZ");
        }
        setWarningMessageInDialog(message.toString());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setWarningMessageInDialog");
        }
    }

    /**
     * set warning message in dialog. It will be displayed in dialog on client side
     *
     * @param message
     */
    protected void setWarningMessageInDialog(String message) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setWarningMessageInDialog", new Object[]{message});
        }
        String dialogTitle = MessageManager.getInstance().formatMessage("eAdmin.common.default.warning.message.title");
        setWarningMessageInDialog(dialogTitle, message);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setWarningMessageInDialog");
        }
    }

    /**
     * set warning message in dialog. It will be displayed in dialog on client side
     *
     * @param dialogTitle
     * @param message
     */
    protected void setWarningMessageInDialog(String dialogTitle, String message) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setWarningMessageInDialog", new Object[]{dialogTitle, message});
        }
        setCallBackParam("DISPLAY_WARNING_DIALOG", "true");
        setCallBackParam("WARNING_TITLE", dialogTitle.replaceAll("&", "%26"));
        setCallBackParam("WARNING_MESSAGE", message.replaceAll("&", "%26"));
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setWarningMessageInDialog");
        }
    }

    protected void setConfirmMessageInDialog(String dialogTitle, String message) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setConfirmMessageInDialog", new Object[]{dialogTitle, message});
        }
        setCallBackParam("DISPLAY_CONFIRM_DIALOG", "true");
        setCallBackParam("CONFIRM_TITLE", dialogTitle.replaceAll("&", "%26"));
        setCallBackParam("CONFIRM_MESSAGE", message.replaceAll("&", "%26"));
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setConfirmMessageInDialog");
        }
    }

    /**
     * handle Exceptions
     *
     * @param methodName
     * @param e
     */
    protected void handleException(String methodName, Exception e) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleException", new Object[]{methodName, e});
        }
        if (e instanceof ValidationException) {
            handleValidationException((ValidationException) e);
        } else if (e instanceof ConfigurationException) {
            handleConfigurationException((ConfigurationException) e);
            throw (ConfigurationException) e;
        } else if (e instanceof AppException) {
            handleAppException((AppException) e);
            throw (AppException) e;
        } else {
            throw ExceptionHelper.getInstance().handleException("Unexpected exception.", e);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "handleException");
        }
    }


    /**
     * Handle validation exception
     *
     * @param e
     */
    protected void handleValidationException(ValidationException e) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleValidationException", new Object[]{e});
        }
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "handleError", e.getMessage() == null ? e.getClass().getName() : e.getMessage());
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "handleValidationException");
        }
    }

    /**
     * handle App Exception
     *
     * @param e
     */
    protected void handleConfigurationException(AppException e) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleConfigurationException", new Object[]{e});
        }

        String messageTitle = MessageManager.getInstance().formatMessage("appException.configuration.error.title");
        String messageDetail = MessageManager.getInstance().formatMessage("appException.configuration.error");
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageTitle, messageDetail);
        FacesContext.getCurrentInstance().addMessage(null, message);

        l.logp(Level.WARNING, getClass().getName(), "handleConfigurationException", e.getMessage() == null ? e.getClass().getName() : e.getMessage());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "handleConfigurationException");
        }
    }

    /**
     * handle App Exception
     *
     * @param e
     */
    protected void handleAppException(AppException e) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleAppException", new Object[]{e});
        }
        handleError(e);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "handleAppException");
        }
    }

    /**
     * handle error
     *
     * @param e
     * @return
     */
    protected void handleError(Exception e) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleError", new Object[]{e});
        }
        String messageTitle = MessageManager.getInstance().formatMessage("appException.unexpected.error.title");
        String messageDetail = MessageManager.getInstance().formatMessage("appException.unexpected.error");
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageTitle, messageDetail);
        FacesContext.getCurrentInstance().addMessage(null, message);

        l.logp(Level.SEVERE, getClass().getName(), "handleError", e.getMessage() == null ? e.getClass().getName() : e.getMessage());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "handleError");
        }
    }

    /**
     * get stack trace
     *
     * @param e
     * @return
     */
    private String getStackTrace(Throwable e) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleError", new Object[]{e});
        }
        StringBuffer traceBuffer = new StringBuffer();
        StackTraceElement[] trace = e.getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            traceBuffer.append("\t\nat " + trace[i].toString());
        }
        Throwable ourCause = e.getCause();
        if (ourCause != null) {
            traceBuffer.append(getStackTrace(ourCause));
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "handleError", traceBuffer);
        }
        return traceBuffer.toString();
    }

    public int compareByUpperCase(Object arg0, Object arg1) {
        return ((String) arg0).toUpperCase().compareTo(((String) arg1).toUpperCase());
    }

    /**
     * get http request
     *
     * @return
     */
    protected HttpServletRequest getRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }


    public boolean isDataChanged() {
        return dataChanged;
    }

    public void setDataChanged(boolean dataChanged) {
        this.dataChanged = dataChanged;
    }

    public boolean isLastActionSuccess() {
        return lastActionSuccess;
    }

    public void setLastActionSuccess(boolean lastActionSuccess) {
        this.lastActionSuccess = lastActionSuccess;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public abstract String getPageHelpId();

    public String getPageHelpURL() {
        return getBaseHelpUri() + getModuleHelpId() + getPageHelpId();
    }

    public void setPageHelpURL() {
        String pageHelpURL = getPageHelpURL();
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put(JSFApplicationIds.PAGE_HELP_URL, pageHelpURL);
    }

    public abstract ArrayList<MenuBean> getTopNavMenu();

    public void setTopNavMenu() {
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put(JSFApplicationIds.TOP_NAV_MENU, getTopNavMenu());
    }

    public abstract String getModuleId();

    public void setModuleId() {
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put(JSFApplicationIds.MODULE_ID, getModuleId());
    }

    public String getApplicationId() {
        return application.getApplicationProperty(getFormattedModuleId() + JSFApplicationIds.APPLICATION_ID);
    }

    public void setApplicationId() {
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put(JSFApplicationIds.APPLICATION_ID, getApplicationId());
    }

    public String getApplicationName() {
        return application.getApplicationProperty(getFormattedModuleId() + JSFApplicationIds.APPLICATION_NAME);
    }

    public void setApplicationName() {
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put(JSFApplicationIds.APPLICATION_NAME, getApplicationName());
    }

    public String getApplicationTitle() {
        String appTitle = application.getApplicationProperty(getFormattedModuleId() + JSFApplicationIds.APPLICATION_TITLE);
        String environmentNameBase = ApplicationContext.getInstance().getProperty("environmentName", "");
        String environmentNameOverride = SysParmProvider.getInstance().getSysParm(getRequest(), "ENVIRONMENTNAME");
        if(!StringUtils.isBlank(environmentNameOverride))
            appTitle = appTitle.replace(environmentNameBase, environmentNameOverride);
        return appTitle;
    }

    public void setApplicationTitle() {
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put(JSFApplicationIds.APPLICATION_TITLE, getApplicationTitle());
    }

    public String getBaseHelpUri() {
        return application.getApplicationProperty(getFormattedModuleId() + JSFApplicationIds.BASE_HELP_URI);
    }

    public void setBaseHelpUri() {
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put(JSFApplicationIds.BASE_HELP_URI, getBaseHelpUri());
    }

    public String getProductLogo() {
        return application.getApplicationProperty(getFormattedModuleId() + JSFApplicationIds.PRODUCT_LOGO);
    }

    public void setProductLogo() {
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put(JSFApplicationIds.PRODUCT_LOGO, getProductLogo());
    }

    public String getLogoTipInfo() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLogoTipInfo");
        }
        String applicationTitle = getApplicationTitle();
        String strLogoTipInfo = MessageManager.getInstance().formatMessage("label.header.page.version", new String[]{applicationTitle});
        if (getRequest().getUserPrincipal() != null) {
            // User is logged in, meaning this is not being included from within the login.jsp page
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            OasisUser userBean = (OasisUser) session.getAttribute(IOasisAction.KEY_OASISUSER);
            String userName = userBean.getUserName();

            String dbid = ActionHelper.getDbPoolId(getRequest());
            if (dbid.startsWith("jdbc/"))
                dbid = dbid.substring(5);

            // Update the Logo Tip Info with the User and DB info
            strLogoTipInfo = MessageManager.getInstance().formatMessage("label.header.page.logoTipInfo",
                    new String[]{applicationTitle, userBean.getUserId(), (String) session.getAttribute(IOasisAction.KEY_PRIOR_LOGIN_TS), dbid});
        }
        strLogoTipInfo = strLogoTipInfo.replace("&#13;", "\n");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLogoTipInfo", strLogoTipInfo);
        }
        return strLogoTipInfo;
    }

    public void setLogoTipInfo() {
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().put(JSFApplicationIds.LOGO_TIP_INFO, getLogoTipInfo());
    }

    private String getFormattedModuleId() {
        String result = "";
        if (!StringUtils.isBlank(getModuleId()))
            result = getModuleId() + ".";
        return result;
    }

    public abstract String getModuleHelpId();

    private String pageTitle;

    private boolean dataChanged = false;

    private boolean lastActionSuccess = false;

    @Inject
    @Named("global")
    private JSFApplication application;

//    private static String BASE_HELP_URI = "../CS/help/WebHelp/eProducts";

    public SelectItem[] getSelectOptions(Collection<String> data) {
        SelectItem[] options = new SelectItem[data.size() + 1];
        options[0] = new SelectItem("", "-SELECT-");
        int i = 1;
        for (String str : data) {
            options[i] = new SelectItem(str, str);

            i++;
        }
        return options;
    }

    public SelectItem[] getSelectOptions(Collection<String> data, Boolean isSelectOptionAllowed) {
        SelectItem[] options;
        int i = 0;
        if (isSelectOptionAllowed) {
            options = new SelectItem[data.size() + 1];
            options[i] = new SelectItem("", "-SELECT-");
            i++;
        } else {
            options = new SelectItem[data.size()];
        }
        for (String str : data) {
            options[i] = new SelectItem(str, str);
            i++;
        }
        return options;
    }

    public SelectItem[] getSelectOptions(Map<Long, String> data) {
        SelectItem[] options = new SelectItem[data.size() + 1];
        options[0] = new SelectItem("", "-SELECT-");
        int i = 1;
        Iterator it = data.keySet().iterator();
        while (it.hasNext()) {
            Long Pk = (Long) it.next();
            options[i] = new SelectItem(Pk, data.get(Pk));
            i++;
        }
        return options;
    }

    public SelectItem[] getSelectOptionsWithStringKeys(Map<String, String> data, Boolean isSelectOptionAllowed) {
        SelectItem[] options;
        int i = 0;
        if (isSelectOptionAllowed) {
            options = new SelectItem[data.size() + 1];
            options[i] = new SelectItem("", "-SELECT-");
            i++;
        } else {
            options = new SelectItem[data.size()];
        }
        for (String key : data.keySet()) {
            options[i] = new SelectItem(key, data.get(key));
            i++;
        }
        return options;
    }

    public SelectItem[] getSelectOptionsWithStringKeys(Map<String, String> data) {
        SelectItem[] options = new SelectItem[data.size() + 1];
        options[0] = new SelectItem("", "-SELECT-");
        int i = 1;
        Iterator it = data.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            options[i] = new SelectItem(key, data.get(key));
            i++;
        }
        return options;
    }

    public SelectItem[] getYesNoOptions() {
        List values = new ArrayList();
        values.add("YES");
        values.add("NO");
        return getSelectOptions(values);
    }

    public void addMessage(String clientId, javax.faces.application.FacesMessage.Severity severity, String titleKey, String detailKey, String[] parameters) {
        String messageTitle = "";
        String messageDetail = "";
        if (!StringUtils.isBlank(titleKey))
            messageTitle = MessageManager.getInstance().formatMessage(titleKey);
        if (!StringUtils.isBlank(detailKey)) {
            if (parameters != null && parameters.length > 0)
                messageDetail = MessageManager.getInstance().formatMessage(detailKey,
                        parameters);
            else
                messageDetail = MessageManager.getInstance().formatMessage(detailKey);
        }
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(severity, messageTitle, messageDetail));

    }

    public void addMessage(javax.faces.application.FacesMessage.Severity severity, String titleKey, String detailKey, String[] parameters) {
        addMessage(null, severity, titleKey, detailKey, parameters);
    }

    public void addMessage(javax.faces.application.FacesMessage.Severity severity, String titleKey, String detailKey) {
        addMessage(null, severity, titleKey, detailKey, null);
    }
}
