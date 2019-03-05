package dti.oasis.util;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.ObjectInputStream;
import java.io.FileInputStream;

import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 21, 2009
 *
 * @author qlxie
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CryptoToolkit {

    private static CryptoToolkit instance = null;
    private static Key key = null;

    /**
     * @param inKeyPath
     * @throws Exception
     */
    private CryptoToolkit(String inKeyPath) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "CryptoToolkit", new Object[]{inKeyPath});
        if (key == null) {
            if (null != inKeyPath) {
                ObjectInputStream in = null;
                try {
                    in = new ObjectInputStream(new FileInputStream(inKeyPath));
                    key = (Key) in.readObject();
                } catch (Exception e) {
                    throw e;
                } finally {
                    if (null != in) {
                        try {
                            in.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                l.logp(Level.FINE, getClass().getName(), "CryptoToolkit", "Key path does not exist!");
            }
        }
    }

    /**
     * @param inKeyPath
     * @return CryptoToolkit
     * @throws Exception
     */
    public static CryptoToolkit getInstance(String inKeyPath) throws Exception {
        if (instance == null) {
            instance = new CryptoToolkit(inKeyPath);
        }

        return instance;
    }

    /**
     * @param inStr
     * @return String
     * @throws Exception
     */
    public String encrypt(String inStr) throws Exception {
        String rtn = "";
        try {
            Cipher cipher = Cipher.getInstance("DES");
            byte[] data = inStr.getBytes();
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(data);

            rtn = new BASE64Encoder().encode(result);

        } catch (NoSuchAlgorithmException e) {
            throw e;
        } catch (NoSuchPaddingException e) {
            throw e;
        } catch (InvalidKeyException e) {
            throw e;
        } catch (IllegalStateException e) {
            throw e;
        } catch (IllegalBlockSizeException e) {
            throw e;
        } catch (BadPaddingException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return rtn;
    }

    /**
     * @param inStr
     * @return String
     * @throws Exception
     */
    public String decrypt(String inStr) throws Exception {
        String rtn = "";

        try {
            Cipher cipher = Cipher.getInstance("DES");
            byte[] data = new BASE64Decoder().decodeBuffer(inStr);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] result = cipher.doFinal(data);
            rtn = new String(result);
        } catch (NoSuchAlgorithmException e) {
            throw e;
        } catch (NoSuchPaddingException e) {
            throw e;
        } catch (InvalidKeyException e) {
            throw e;
        } catch (IllegalStateException e) {
            throw e;
        } catch (IllegalBlockSizeException e) {
            throw e;
        } catch (BadPaddingException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return rtn;
    }


    /**
     * convert string to ascii code (hex).
     *
     * @param data
     * @return
     */
    public static String toHexString(String data) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < data.length(); i++) {
            char character = data.charAt(i);
            if (character < 16) {
                stringBuffer.append("0");
            }
            stringBuffer.append(Integer.toHexString(character));
        }
        return stringBuffer.toString().toUpperCase();
    }

    /**
     * convert hex ascii code to string
     *
     * @param hexData
     * @return
     */
    public static String hexToString(String hexData) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < hexData.length() / 2; i++) {
            String hexChar = hexData.substring(i * 2, i * 2 + 2);
            int ascii = Integer.parseInt(hexChar, 16);
            stringBuffer.append((char) ascii);
        }
        return stringBuffer.toString();
    }
}
