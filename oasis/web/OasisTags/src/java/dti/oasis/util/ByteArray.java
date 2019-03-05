package dti.oasis.util;

import java.util.Arrays;

/**
 * A Wrapper class for the byte[].
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ByteArray {

  public ByteArray(byte[] bytes) {
    this.m_bytes = bytes;
  }

  public byte[] getBytes() {
    return m_bytes;
  }

  public int length() {
    return m_bytes.length;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ByteArray)) return false;

    final ByteArray byteArray = (ByteArray) o;

    if (!Arrays.equals(m_bytes, byteArray.m_bytes)) return false;

    return true;
  }

  public int hashCode() {
    int h = m_hash;
    if (h == 0) {
      byte val[] = m_bytes;
      int len = m_bytes.length;

      for (int i = 0; i < len; i++)
        h = 31*h + val[i];

      m_hash = h;
    }
    return h;
  }


  /** Cache the m_hash code for the byte[] */
  private int m_hash = 0;
  private byte[] m_bytes;
}
