package dti.oasis.adapter;

import dti.oasis.recordset.Record;
import dti.oasis.struts.BaseAction;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.XMLGridHeader;

import dti.oasis.util.PageBean;

import javax.servlet.http.HttpServletRequest;


/**
 * We need this class since we can't instantiate
 * abstruct BaseAction
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   May 16, 2011
 *
 * @author mgitelman
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class BaseActionAdapter extends BaseAction {

    public void init(HttpServletRequest request)  throws Exception {
            securePage(request);
    }

    /**
     * After BaseAction.securePage(HttpServletRequest request, ActionForm form)
     * @param request
     * @throws Exception
     */
    protected void securePage(HttpServletRequest request) throws Exception {
        System.out.println(new java.sql.Timestamp(System.currentTimeMillis()) + " -- "
                + this.getClass().getName() + " -- " + "BaseActionAdapter.securePage(HttpServletRequest request)");
        //ActionForm is never used in securePage
        super.securePage(request, null);
    }

    /**
     * After BaseAction.getInputRecord(HttpServletRequest request)
     * @param request
     * @return
     */
    public Record getInputRecord(HttpServletRequest request) {
        System.out.println(new java.sql.Timestamp(System.currentTimeMillis()) + " -- "
                + this.getClass().getName() + " -- " + "BaseActionAdapter.getInputRecord(HttpServletRequest request)");
        return super.getInputRecord(request);
    }

    /* TODO: Handle Page with Multiple Grids
        loadGridHeader(HttpServletRequest request, String gridHeaderFieldnameSuffix,
                                  String gridId, String layerId)
    */
    /**
     * After BaseAction.loadGridHeader(HttpServletRequest request)
     * @param request
     * @return
     */
    public XMLGridHeader getGridHeader(HttpServletRequest request) {
        System.out.println(new java.sql.Timestamp(System.currentTimeMillis()) + " -- "
                + this.getClass().getName() + " -- " + "BaseActionAdapter.getGridHeader(HttpServletRequest request)");
        return getGridHeader(request, null);
    }

    /**
     * After BaseAction.loadGridHeader(HttpServletRequest request, String gridHeaderFieldnameSuffix)
     * @param request
     * @param gridHeaderFieldnameSuffix
     * @return
     */
    public XMLGridHeader getGridHeader(HttpServletRequest request, String gridHeaderFieldnameSuffix) {
        if(request.getAttribute("gridHeaderBean")==null)
            this.loadGridHeader(request, gridHeaderFieldnameSuffix);
        XMLGridHeader xmlHeader = (XMLGridHeader)request.getAttribute("gridHeaderBean");
        System.out.println(new java.sql.Timestamp(System.currentTimeMillis()) + " -- "
                + this.getClass().getName() + " -- " + "XMLGridHeader xmlHeader: "+xmlHeader.toString());
        return xmlHeader;
    }

    public PageBean getPageBean(HttpServletRequest request){
        PageBean pageBean = (PageBean)request.getAttribute(IOasisAction.KEY_PAGEBEAN);
        if(pageBean==null)
            System.out.println("PAGE BEAN IS NULL");
        else
            System.out.println("PAGE BEAN IS NOT NULL "+pageBean.getTitle());        
        return pageBean;
    }
}
