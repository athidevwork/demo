package dti.oasis.util;

import dti.oasis.app.AppException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 12, 2014
 *
 * @author Parker
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 *
 * ---------------------------------------------------
 */
public class ZipUtils {

    /**
     * compress a String to Zip Stream. then return a ByteArrayInputStream.
     *
     * @param inputString
     * @return ByteArrayInputStream
     */
    public static final ByteArrayInputStream compress(String inputString) {
        Logger l = LogUtils.getLogger(ZipUtils.class);
        if (l.isLoggable(Level.FINER)) {
            l.entering(ZipUtils.class.getName(), "compress", new Object[]{inputString});
        }
        ByteArrayInputStream byteArrayInputStream = null;
        if (inputString != null) {
            byte[] compressedBytes;
            ByteArrayOutputStream out = null;
            ZipOutputStream zipOut = null;
            try {
                out = new ByteArrayOutputStream();
                zipOut = new ZipOutputStream(out);
                zipOut.putNextEntry(new ZipEntry("0"));
                zipOut.write(inputString.getBytes());
                zipOut.closeEntry();
                compressedBytes = out.toByteArray();
            } catch (IOException e) {
                compressedBytes = null;
                l.throwing(ZipUtils.class.getName(), "Failed to compress the XML", e);
                throw new AppException("Failed to compress the XML");
            } finally {
                if (zipOut != null) {
                    try {
                        zipOut.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
            byteArrayInputStream = new ByteArrayInputStream(compressedBytes);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(ZipUtils.class.getName(), "compress", byteArrayInputStream);
        }
        return byteArrayInputStream;
    }

    /**
     * decompressAndFormat a Stream. then return a ZipInputStream.
     *
     * @param compressedBytes
     * @return ZipInputStream
     */
    public static final ZipInputStream decompressToSteam(byte[] compressedBytes) {
        Logger l = LogUtils.getLogger(ZipUtils.class);
        if (l.isLoggable(Level.FINER)) {
            l.entering(ZipUtils.class.getName(), "decompressToSteam", new Object[]{compressedBytes});
        }
        ZipInputStream zipIn = null;
        if (compressedBytes != null && compressedBytes.length > 0) {
            ByteArrayInputStream in = null;
            try {
                in = new ByteArrayInputStream(compressedBytes);
                zipIn = new ZipInputStream(in);
            } catch (Exception e) {
                l.throwing(ZipUtils.class.getName(), "Failed to return a ZipInputStream", e);
                throw new AppException("Failed to return a ZipInputStream");
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(ZipUtils.class.getName(), "decompressToSteam", zipIn);
        }
        return zipIn;
    }


    /**
     * Another way to decompressAndFormat a Stream. then return a String.
     *
     * @param compressedBytes
     * @return String
     */
    public static final String decompress(byte[] compressedBytes) {
        Logger l = LogUtils.getLogger(ZipUtils.class);
        if (l.isLoggable(Level.FINER)) {
            l.entering(ZipUtils.class.getName(), "decompress", new Object[]{compressedBytes});
        }
        String xmlResult = "";
        if (compressedBytes != null && compressedBytes.length > 0) {
            ByteArrayInputStream in = null;
            ZipInputStream zipIn = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                in = new ByteArrayInputStream(compressedBytes);
                zipIn = new ZipInputStream(in);
                byte[] buf = new byte[1024];
                int num = -1;
                while ((num = zipIn.read(buf, 0, buf.length)) != -1) {
                    baos.write(buf, 0, num);
                }
                xmlResult = baos.toString();
            } catch (IOException e) {
                l.throwing(ZipUtils.class.getName(), "Failed to decompress the XML", e);
                throw new AppException("Failed to decompress the XML");
            } finally {
                if (zipIn != null) {
                    try {
                        zipIn.close();
                    } catch (IOException e) {
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(ZipUtils.class.getName(), "decompress", xmlResult);
        }
        return xmlResult;
    }
}
