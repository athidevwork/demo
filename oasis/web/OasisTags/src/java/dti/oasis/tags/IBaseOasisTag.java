package dti.oasis.tags;

/**
 * Base Interface for OASISFormField related JSP Custom tags
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * @author jbe
 * Date:   Jun 20, 2003
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 *
 * ---------------------------------------------------
 */
public interface IBaseOasisTag {
    public Object getOasisFormField();

    public void setOasisFormField(Object obj);

    public String getMapName();

    public String getFieldName();

}
