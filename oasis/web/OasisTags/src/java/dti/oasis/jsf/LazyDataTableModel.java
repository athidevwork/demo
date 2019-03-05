package dti.oasis.jsf;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.jpa.Filter;
import dti.oasis.jpa.LoadByFilterService;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   5/28/13
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/19/2013       Parker      use an inner class to replace the implementation of RowSelectionListener interface
 *
 * ---------------------------------------------------
 */
public class LazyDataTableModel<T> extends LazyDataModel<T> {
    public LazyDataTableModel() {
        super();
    }

    public LazyDataTableModel(LoadByFilterService loadByFilterService, DefaultLazyLoadByFilterProcessor loadCountAllByFilterProcessor, DefaultLazyLoadByFilterProcessor loadLoadAllByFilterProcessor) {
        m_loadByFilterService = loadByFilterService;
        m_loadCountAllByFilterProcessor = loadCountAllByFilterProcessor;
        m_loadLoadAllByFilterProcessor = loadLoadAllByFilterProcessor;
    }

    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "load", new Object[]{first, pageSize, sortField, sortOrder, filters});
        }

        List<T> results = m_results;

        try {
            long count = getLoadByFilterService().countAllByFilter(getFilter(), getLoadCountAllByFilterProcessor());
            if (l.isLoggable(Level.FINER)) {
                l.logp(Level.FINER, getClass().getName(), "load","count: "+count);
            }
            int maxRows = first + pageSize;
            if (l.isLoggable(Level.FINER)) {
                l.logp(Level.FINER, getClass().getName(), "load","maxRows: "+maxRows);
            }
            if (getMaxRows() != null ) {
                if (l.isLoggable(Level.FINER)) {
                    l.logp(Level.FINER, getClass().getName(), "load","getMaxRows(): "+getMaxRows());
                }
                maxRows = getMaxRows().intValue();
                count = Math.min(maxRows, count);
            }

            int totalRowCount = (int) count;
            if (l.isLoggable(Level.FINER)) {
                l.logp(Level.FINER, getClass().getName(), "load","totalRowCount: "+totalRowCount);
            }

            setRowCount(totalRowCount);

            if (first < maxRows) {
                if (maxRows < pageSize) {
                    pageSize = maxRows;
                    l.fine("maxRows < pageSize; returning partial list");
                }
                getLoadLoadAllByFilterProcessor().setFirstRow(first);
                getLoadLoadAllByFilterProcessor().setMaxResults(pageSize);
                getLoadLoadAllByFilterProcessor().setSortField(sortField);
                getLoadLoadAllByFilterProcessor().setSortOrder(sortOrder.name());
                results = getLoadByFilterService().loadAllByFilter(getFilter(), getLoadLoadAllByFilterProcessor());
            }
            else {
                l.fine("first is > maxRows; returning empty list");
            }

            // select row
            if (results.size() > 0) {
                // try to find current selected row in result
                T targetRow = null;
                if (getSelectedRow() != null) {
                    for (T record : results) {
                        if (record.equals(getSelectedRow())) {
                            targetRow = record;
                            break;
                        }
                    }
                }
                if (targetRow == null) {
                    // select first row
                    setSelectedRow(results.get(0));
                } else {
                    setSelectedRow(targetRow);
                }
            } else {
                setSelectedRow(null);
            }

            if (getRowSelectionListener() != null) {
                getRowSelectionListener().processRowSelect(getSelectedRow());
            }
        } catch (Exception e){
            //Used for displayErrorMessageDialog()
            if(RequestContext.getCurrentInstance()!=null){
                RequestContext.getCurrentInstance().addCallbackParam("lazyLoadException", "lazyLoadExceptionCaught");
                l.fine("RequestContext.getCurrentInstance():: lazyLoadExceptionCaught");
            } else
                l.fine("RequestContext.getCurrentInstance() is NULL");

            throw new RuntimeException("Caught Lazy Loading Exception in the LazyDataTableModel load() method",e);
        }

        l.fine("results.size() = " + results.size());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "load", results);
        }
        return results;
    }

    /**
     * add filter type mapping
     */
    public void addFilterTypeMapping(String fieldName, Filter.Type type) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addFilterTypeMapping", new Object[]{fieldName,type});
        }
        if (m_loadCountAllByFilterProcessor != null) {
            m_loadCountAllByFilterProcessor.getFieldFilterTypeMap().put(fieldName, type);
        }
        if (m_loadLoadAllByFilterProcessor != null) {
            m_loadLoadAllByFilterProcessor.getFieldFilterTypeMap().put(fieldName, type);
        }
        l.exiting(getClass().getName(), "addFilterTypeMapping");
    }

    public T getFilter() {
        return m_filter;
    }

    public void setFilter(T filter) {
        m_filter = filter;
    }

    public T getSelectedRow() {
        Logger l = LogUtils.enterLog(getClass(), "getSelectedRow");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getSelectedRow", m_selectedRow);
        }
        return m_selectedRow;
    }

    public void setSelectedRow(T selectedRow) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setSelectedRow", new Object[]{selectedRow});
        }

        this.m_selectedRow = selectedRow;

        l.exiting(getClass().getName(), "setSelectedRow");
    }

    public T getCurrentEditRecord() {
        return m_currentEditRecord;
    }

    public void setCurrentEditRecord(T currentEditRecord) {
        this.m_currentEditRecord = currentEditRecord;
    }

    public Integer getMaxRows() {
        return m_maxRows;
    }

    public void setMaxRows(Integer maxRows) {
        m_maxRows = maxRows;
    }

    public void clearMaxRows() {
        m_maxRows = null;
    }

    public DefaultLazyLoadByFilterProcessor getLoadCountAllByFilterProcessor() {
        return m_loadCountAllByFilterProcessor;
    }

    public void setLoadCountAllByFilterProcessor(DefaultLazyLoadByFilterProcessor loadCountAllByFilterProcessor) {
        m_loadCountAllByFilterProcessor = loadCountAllByFilterProcessor;
    }

    public DefaultLazyLoadByFilterProcessor getLoadLoadAllByFilterProcessor() {
        return m_loadLoadAllByFilterProcessor;
    }

    public void setLoadLoadAllByFilterProcessor(DefaultLazyLoadByFilterProcessor loadLoadAllByFilterProcessor) {
        m_loadLoadAllByFilterProcessor = loadLoadAllByFilterProcessor;
    }

    public RowSelectionListener<T> getRowSelectionListener() {
        return m_rowSelectionListener;
    }

    public void setRowSelectionListener(RowSelectionListener<T> rowSelectionListener) {
        this.m_rowSelectionListener = rowSelectionListener;
    }

    public LoadByFilterService getLoadByFilterService() {
        return m_loadByFilterService;
    }

    public void setLoadByFilterService(LoadByFilterService loadByFilterService) {
        m_loadByFilterService = loadByFilterService;
    }


    public List<T> getResults() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.logp(Level.FINER, getClass().getName(), "getResults", "Results Size: "+m_results.size());
        }
        return m_results;
    }

    public void setResults(List<T> results) {
        this.m_results = results;
    }

    private T m_selectedRow;
    private T m_filter;
    private T m_currentEditRecord;
    private Integer m_maxRows;
    private DefaultLazyLoadByFilterProcessor m_loadCountAllByFilterProcessor;
    private DefaultLazyLoadByFilterProcessor m_loadLoadAllByFilterProcessor;
    private RowSelectionListener<T> m_rowSelectionListener;
    private LoadByFilterService m_loadByFilterService;

    private List<T> m_results = new ArrayList<T>();
}

