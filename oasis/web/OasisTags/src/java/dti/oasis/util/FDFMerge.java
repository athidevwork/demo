package dti.oasis.util;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.FdfReader;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.xml.xmp.XmpArray;
import com.lowagie.text.xml.xmp.XmpSchema;
import com.lowagie.text.xml.xmp.XmpWriter;
import dti.oasis.request.RequestStorageManager;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This object merges FDF files with PDF files to
 * create new PDF Files with data bound to its fields.
 * Singleton!
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * @author jbe
 * Date:   Jan 16, 2004
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/23/2009       qlxie       Modified merge() to close the output after use.
 * 02/26/2014       mlm         151310 - Refactor to support pdf template without session objects.
 * 01/22/2016       dpang       157243 - Enhance UFE to upload templates and manage versions of templates uploaded.
 * ---------------------------------------------------
 */

public class FDFMerge {

    public class UFECustomXmpSchema extends XmpSchema {
//        private static final long serialVersionUID = -4551741356974797330L;

        public static final String FORM_REQUEST_ID = "form_request_id";
        public static final String REQUEST_VERSION = "form_request_version";
        public static final String USER_ID = "userid";
        public static final String IS_FORM_LETTER = "isFormLetter";

        public UFECustomXmpSchema() {
            super("UFERequestInfo");
        }

        public void addFormRequestId(String ufeFormRequestId) {
            XmpArray array = new XmpArray(XmpArray.UNORDERED);
            array.add(ufeFormRequestId);
            setProperty(FORM_REQUEST_ID, array);
        }

        public void addRequestVersion(String requestVersion) {
            XmpArray array = new XmpArray(XmpArray.UNORDERED);
            array.add(requestVersion);
            setProperty(REQUEST_VERSION, array);
        }

        public void addUserId(String userId) {
            XmpArray array = new XmpArray(XmpArray.UNORDERED);
            array.add(userId);
            setProperty(USER_ID, array);
        }

        public void addIsFormLetter(String isFormLetter) {
            XmpArray array = new XmpArray(XmpArray.UNORDERED);
            array.add(isFormLetter);
            setProperty(IS_FORM_LETTER, array);
        }
    }

    private static final FDFMerge INSTANCE = new FDFMerge();

    /**
     * Call this method to get the single instance of this class
     * @return
     */
    public static FDFMerge getInstance() {
        return INSTANCE;
    }

    private FDFMerge() {
    }

    private Object readResolve() {
        return INSTANCE;
    }

    /**
     * Merge a pdf file containing fields, with an FDF File to create a new PDF File.
     * The resulting PDF File does not have its fields flattened.
     * @param templateBytes Byte array of the template
     * @param pdfFileIn Name of PDF File that contains fields
     * @param fdfFileIn Name of FDF File
     * @param pdfFileOut Name of PDF File to create upon merge
     * @throws Exception
     */
    public void merge(byte[] templateBytes, String pdfFileIn, String fdfFileIn, String pdfFileOut) throws Exception {
        merge(templateBytes, pdfFileIn, fdfFileIn, pdfFileOut, false);
    }

    /**
     * Merge a pdf file containing fields, with an FDF File to create a new PDF File
     *
     * @param templateBytes Byte array of the template
     * @param pdfFileIn Name of PDF File that contains fields
     * @param fdfFileIn Name of FDF File
     * @param pdfFileOut Name of PDF File to create upon merge
     * @param flatten true to flatten fields in resulting PDF File.
     * @throws Exception
     */
    public void merge(byte[] templateBytes, String pdfFileIn, String fdfFileIn, String pdfFileOut, boolean flatten) throws Exception {
        ByteArrayOutputStream baos = null;
        ByteArrayOutputStream baosForXmp = null;
        FileOutputStream output = null;
        Logger l = LogUtils.enterLog(getClass(),"merge",
                new Object[] {pdfFileIn,fdfFileIn,pdfFileOut, String.valueOf(flatten)});
        try {
            // Load pdf file
            PdfReader reader;
            if (templateBytes != null) {
                reader = new PdfReader(templateBytes);
            } else {
                reader = new PdfReader(pdfFileIn);
            }

            // create outputstream
            baos = new ByteArrayOutputStream();
            // create stamper given pdf file and output stream
            PdfStamper stamper = new PdfStamper(reader, baos);
            // load fdf file
            FdfReader fReader = new FdfReader(fdfFileIn);
            // get fields from pdf file
            AcroFields form = stamper.getAcroFields();

            if (l.isLoggable(Level.FINEST)) {
                Iterator it = stamper.getAcroFields().getFields().keySet().iterator();
                while(it.hasNext()) {
                    String key = (String) it.next();
                    l.finest("Field:" + key + ", Field Value:" +  fReader.getFieldValue(key));
                }
            }

            // merge
            form.setFields(fReader);

            HashMap<String, String> info = reader.getInfo();
            info.put("form_request_id", (String) RequestStorageManager.getInstance().get("form_request_id"));
            info.put("form_request_version", (String) RequestStorageManager.getInstance().get("form_request_version"));
            info.put("userid", (String) RequestStorageManager.getInstance().get("userid"));
            info.put("isFormLetter", (RequestStorageManager.getInstance().has("isFormLetter") ?  (String) RequestStorageManager.getInstance().get("isFormLetter") : "N"));
            stamper.setMoreInfo(info);

            baosForXmp = new ByteArrayOutputStream();
            XmpWriter xmp = new XmpWriter(baosForXmp);
            UFECustomXmpSchema UFEXmpSchema=new UFECustomXmpSchema();
            UFEXmpSchema.addFormRequestId((String) RequestStorageManager.getInstance().get("form_request_id"));
            UFEXmpSchema.addRequestVersion((String) RequestStorageManager.getInstance().get("form_request_version"));
            UFEXmpSchema.addUserId((String) RequestStorageManager.getInstance().get("userid"));
            UFEXmpSchema.addIsFormLetter( (RequestStorageManager.getInstance().has("isFormLetter") ?  (String) RequestStorageManager.getInstance().get("isFormLetter") : "N") );
            xmp.addRdfDescription(UFEXmpSchema);
            xmp.close();
            stamper.setXmpMetadata(baosForXmp.toByteArray());

            // set the flatten property
            stamper.setFormFlattening(flatten);
            // close the stamper
            stamper.close();
            // write out the new pdf file
            output = new FileOutputStream(pdfFileOut);
            baos.writeTo(output);
            l.exiting(getClass().getName(),"merge");
        }
        finally {
            // close the output stream
            if (baos != null) baos.close();
            if (baosForXmp != null) baosForXmp.close();
            if (output != null) output.close();
        }

    }
}

