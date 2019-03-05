package dti.oasis.util;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 24, 2012
 *
 * @author ldong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class XmlPhoneNumber {

    private String m_countryCode;
    private String m_areaCode;
    private String m_phoneNumber;
    private String m_extension;

    public static final String c_pattern = "^\\+\\d{1,3}-\\d{3}-\\d{7}(\\+\\d{0,5}){0,1}";

    public XmlPhoneNumber() {
    }

    /**
     * Constructor
     * To convert a string to a Phone Number
     * @param phoneNumber
     */
    public XmlPhoneNumber(String phoneNumber) {
        if (isValidPhoneNumber(phoneNumber)){
            int position = phoneNumber.indexOf("-");
            String countryCode = phoneNumber.substring(1, position);
            String fullPhone = phoneNumber.substring(position).replaceAll("[^0-9]", "");
            String areaCode = fullPhone.substring(0, 3);
            String phone = fullPhone.substring(3, 10);
            String extension = fullPhone.substring(10, fullPhone.length());

            this.setCountryCode(countryCode);
            this.setAreaCode(areaCode);
            this.setPhoneNumber(phone);
            this.setExtension(extension);
        }
    }

    /**
     * To verify if a string is a valid Phone Number for XML
     *
     * @param phone
     * @return
     */
    public static boolean isValidPhoneNumber(String phone) {
        boolean valid = false;
        if (phone != null && phone.length() > 0) {
            valid = phone.matches(c_pattern);
        }
        return valid;
    }

    /**
     * To verify if a string is a valid Phone Number for XML
     *
     * @return
     */
    public String getXmlPhoneNumber() {
        String fullPhoneNo = "";

        if (this.getCountryCode() != null && this.getCountryCode().length() > 0){
            fullPhoneNo += "+" + this.getCountryCode();
        } else {
            fullPhoneNo += "+1";
        }
        if (this.getAreaCode() != null && this.getAreaCode().length() > 0){
            fullPhoneNo += "-" + this.getAreaCode();
        }
        if (this.getPhoneNumber() != null && this.getPhoneNumber().length() > 0){
            fullPhoneNo += "-" + this.getPhoneNumber();
        }
        if (this.getExtension() != null && this.getExtension().length() > 0){
            fullPhoneNo += "+" + this.getExtension();
        }

        return fullPhoneNo;
    }

    public String getCountryCode() {
        return m_countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.m_countryCode = countryCode;
    }

    public String getAreaCode() {
        return m_areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.m_areaCode = areaCode;
    }

    public String getPhoneNumber() {
        return m_phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.m_phoneNumber = phoneNumber;
    }

    public String getExtension() {
        return m_extension;
    }

    public void setExtension(String extension) {
        this.m_extension = extension;
    }


}
