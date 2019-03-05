package dti.oasis.codelookupmgr.impl;

/**
 * This class holds the Code Lookup properties for a Code Lookup Field.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 28, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/14/2014       fcb         Added ALL select option.
 * ---------------------------------------------------
 */
public class CodeLookupProperties {

    /**
     * Initialize the class with default falues for using Ajax to refresh field-dependant lookups
     * and adding a select option.
     *
     * @param defaultAjaxReload
     * @param defaultAddSelectOption
     * @param displaySelectedOptionsFirst: resort the options by putting the selected optiosn first
     */
    public CodeLookupProperties(boolean defaultAjaxReload, boolean defaultAddSelectOption,boolean displaySelectedOptionsFirst) {
        m_ajaxReload = defaultAjaxReload;
        m_addSelectOption = defaultAddSelectOption;
        m_displaySelectedOptionsFirst = displaySelectedOptionsFirst;
    }

    public boolean hasDelimString() {
        return m_delimString != null;
    }

    public boolean wasDelimStringSpecified() {
        return m_specifiedDelimString;
    }

    public String getDelimString() {
        return m_delimString;
    }

    public void setDelimString(String delimString) {
        m_delimString = delimString;
        m_specifiedDelimString = true;
    }

    public boolean ajaxReload() {
        return m_ajaxReload;
    }

    public boolean wasAjaxReloadSpecified() {
        return m_specifiedAjaxReload;
    }

    public void setAjaxReload(boolean ajaxReload) {
        this.m_ajaxReload = ajaxReload;
        m_specifiedAjaxReload = true;
    }

    public boolean cacheLOV() {
        return m_cacheLOV;
    }

    public boolean wasCacheLOVSpecified() {
        return m_specifiedCacheLOV;
    }

    public void setCacheLOV(boolean cacheLOV) {
        this.m_cacheLOV = cacheLOV;
        m_specifiedCacheLOV = true;
    }

    public boolean addSelectOption() {
        return m_addSelectOption;
    }

    public boolean wasAddSelectOptionSpecified() {
        return m_specifiedAddSelectOption;
    }

    public void setAddSelectOption(boolean addSelectOption) {
        this.m_addSelectOption = addSelectOption;
        m_specifiedAddSelectOption = true;
    }

    public boolean addAllOption() {
        return m_addAllOption;
    }

    public void setUseLabelForEmptyOption(boolean useLabelForEmptyOption) {
        this.m_useLabelForEmptyOption = useLabelForEmptyOption;
    }

    public boolean isUseLabelForEmptyOption() {
        return this.m_useLabelForEmptyOption;
    }

    public boolean wasAddAllOptionSpecified() {
        return m_specifiedAddAllOption;
    }

    public void setAddAllOption(boolean allSelectOption) {
        this.m_addAllOption = allSelectOption;
        m_specifiedAddAllOption = true;
    }

   public void setDisplaySelectedOptionsFirst(boolean displaySelectedOptionsFirst) {
       this.m_displaySelectedOptionsFirst =  displaySelectedOptionsFirst;
    }

   public boolean displaySelectedOptionsFirst() {
      return this.m_displaySelectedOptionsFirst;
   }

    public void setProcessExpiredOptions(boolean m_processExpiredOptions) {
        this.m_processExpiredOptions = m_processExpiredOptions;
    }

    public boolean processExpiredOptions() {
        return m_processExpiredOptions;
    }

    public String toString() {
        return "CodeLookupProperties{" +
            "m_delimString='" + m_delimString + '\'' +
            ", m_ajaxReload=" + m_ajaxReload +
            ", m_cacheLOV=" + m_cacheLOV +
            ", m_addSelectOption=" + m_addSelectOption +
            ", m_displaySelectedOptionsFirst=" + m_displaySelectedOptionsFirst +
            ", m_processExpiredOptions=" + m_processExpiredOptions +
            ", m_useLabelForEmptyOption=" + m_useLabelForEmptyOption +
            '}';
    }

    private String m_delimString;
    private boolean m_specifiedDelimString = false;
    private boolean m_ajaxReload = false;
    private boolean m_specifiedAjaxReload = false;
    private boolean m_cacheLOV = false;
    private boolean m_specifiedCacheLOV = false;
    private boolean m_addSelectOption = false;
    private boolean m_addAllOption = false;
    private boolean m_specifiedAddSelectOption = false;
    private boolean m_specifiedAddAllOption = false;
    private boolean m_displaySelectedOptionsFirst = false;
    private boolean m_processExpiredOptions = false;
    private boolean m_useLabelForEmptyOption = false;
}
