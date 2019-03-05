package dti.oasis.property.service;

import java.util.List;
import java.util.Map;

/**
 * <p/>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date: 11/10/2015
 *
 * @author tmarius
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public interface PropertyService {

    /**
     * get application properties
     * @return
     */
    public Map<String, String> getProperties();

    /**
     * get message properties
     * @return
     */
    public Map<String, String> getMessageResourceProperties();

    /**
     * get properties for
     * @return
     */
    public Map<String, String> getPropertiesFor(List<String> keys);

    /**
     * get property
     * @param name
     * @return
     */
    public String getProperty(String name);
}
