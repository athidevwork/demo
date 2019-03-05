package dti.oasis.messagemgr;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.Assert;

import java.util.ResourceBundle;
import java.util.Locale;
import java.util.Enumeration;

/**
 * This class is from org.springframework.context.support.MessageSourceResourceBundle
 * We change method getKeys to get message keys
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
public class MessageSourceResourceBundle extends ResourceBundle {

	private final MessageSource messageSource;

	private final Locale locale;


	/**
	 * Create a new MessageSourceResourceBundle for the given MessageSource and Locale.
	 * @param source the MessageSource to retrieve messages from
	 * @param locale the Locale to retrieve messages for
	 */
	public MessageSourceResourceBundle(MessageSource source, Locale locale) {
		Assert.notNull(source, "MessageSource is required");
		this.messageSource = source;
		this.locale = locale;
	}


	/**
	 * This implementation resolves the code in the MessageSource.
	 * Returns null if the message could not be resolved.
	 */
	protected Object handleGetObject(String code) {
		try {
			return this.messageSource.getMessage(code, null, this.locale);
		}
		catch (NoSuchMessageException ex) {
			return null;
		}
	}

    /**
     * return message keys if messageSource is an instance of ReloadableMessageSource
     * @return
     */
    public Enumeration getKeys() {
        if (messageSource instanceof ReloadableMessageSource) {
            ReloadableMessageSource reloadableMessageSource = (ReloadableMessageSource) messageSource;
            return reloadableMessageSource.getKeys();
        } else {
            return null;
        }
    }

}
