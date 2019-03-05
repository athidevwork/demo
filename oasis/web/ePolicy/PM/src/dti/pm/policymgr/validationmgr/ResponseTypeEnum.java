package dti.pm.policymgr.validationmgr;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/16/2016
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/16/2016       tzeng       166929 - Initial version.
 * ---------------------------------------------------
 */
public enum ResponseTypeEnum {
    USER("User"),
    SYSYTEM_DEFAULT("System Default");

    private String value;

    ResponseTypeEnum (String value) {
        this.value = value;
    }

    public String getResponseTypeValue() {
        return value;
    }
}
