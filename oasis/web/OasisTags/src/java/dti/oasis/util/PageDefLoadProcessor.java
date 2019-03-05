package dti.oasis.util;

import dti.oasis.tags.OasisElements;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.tags.OasisWebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * An interface to implement the Page load processor to inforce data security.
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
public interface PageDefLoadProcessor extends LoadProcessor{
    public void postProcessField(OasisFormField fld)  ;
    public void postProcessFields(OasisFields flds)  ;
    public void postProcessWebElement(OasisWebElement element)  ;
    public void postProcessWebElements(OasisElements elements)  ;
    public boolean postProcessMenuItem(MenuBean menuitem)  ;
    public void postProcessMenuItems(List menuitems)  ;
    public boolean postProcessActionItem(MenuBean actionitem)  ;
    public void postProcessActionItems(List actionitems)  ;
}
