package dti.oasis.util;

import dti.oasis.tags.OasisElements;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.tags.OasisWebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains an empty implementation of PageDefLoadProcessor. This is consumed by methods/functions
 * that does not enforce any post process for securing field, web element, menu item and action item.
 * <p/>
 * This class is a candidate for promoting to the Oasis Core library.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 21, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/18/2008       wer         Set the order of action items based on the sequence
 * ---------------------------------------------------
 */
public class DefaultPageDefLoadProcessor implements PageDefLoadProcessor {

    /**
     * Return a default instance of the DefaultPageDefLoadProcessor.
     */
    public static DefaultPageDefLoadProcessor getInstance() {
        return c_defaultPageDefLoadProcessor;
    }

    /**
     *
     * @param fld OasisFormField, for which postProcess checks for security
     */
    public void postProcessField(OasisFormField fld)
    {
        return ;
    }


    /**
     *
     * @param flds OasisFields collection, for which postProcess checks for security
     */
    public void postProcessFields(OasisFields flds)
    {
        return ;
    }

    /**
     *
     * @param element OasisWebElement, for which postProcess checks for security
     */
    public void postProcessWebElement(OasisWebElement element)
    {
        return ;
    }

    /**
     *
     * @param elements OasisElements collection, for which postProcess checks for security
     */
    public void postProcessWebElements(OasisElements elements)
    {
        return;
    }

    /**
     *
     * @param menuitem
     * @return true, if the menu item is secured; otherwise, false
     */
    public boolean postProcessMenuItem(MenuBean menuitem)
    {
        return true;
    }

    /**
     *
     * @param menuitems
     */
    public void postProcessMenuItems(List menuitems)
    {
        return;
    }

    /**
     *
     * @param actionitem
     * @return true, if the action item is secured; otherwise, false
     */
    public boolean postProcessActionItem(MenuBean actionitem)
    {
        return true;
    }

    /**
     *
     * @param actionitems
     */
    public void postProcessActionItems(List actionitems)
    {
        return;
    }

    private static DefaultPageDefLoadProcessor c_defaultPageDefLoadProcessor = new DefaultPageDefLoadProcessor();
}
