package dti.oasis.messagemgr;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;
import java.util.Enumeration;

/**
 * This class extends ReloadableResourceBundleMessageSource to provide
 * access to message keys.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 21, 2009
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class ReloadableMessageSource extends ReloadableResourceBundleMessageSource {


    /**
     * get alll properties keys
     * @return
     */
    public Enumeration getKeys() {
        PropertiesHolder propertiesHolder = this.getMergedProperties(Locale.getDefault());
        return propertiesHolder.getProperties().keys();
    }

}
