package dti.oasis.jpa;

import dti.oasis.util.DateUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/2/2016
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class DateFormatterAdapter extends XmlAdapter<String, Date> {
    /**
     * Convert a value type to a bound type.
     *
     * @param dateString The value to be converted. Can be null.
     * @throws Exception if there's an error during the conversion. The caller is responsible for
     *                   reporting the error to the user through {@link javax.xml.bind.ValidationEventHandler}.
     */
    @Override
    public Date unmarshal(String dateString) throws Exception {
        return DateUtils.convertDateStringToDate(dateString);
    }

    /**
     * Convert a bound type to a value type.
     *
     * @param date The value to be convereted. Can be null.
     * @throws Exception if there's an error during the conversion. The caller is responsible for
     *                   reporting the error to the user through {@link javax.xml.bind.ValidationEventHandler}.
     */
    @Override
    public String marshal(Date date) throws Exception {
        return DateUtils.convertDateToDateOnlyString(date);
    }
}
