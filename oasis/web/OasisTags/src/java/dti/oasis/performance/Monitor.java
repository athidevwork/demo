package dti.oasis.performance;

/**
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
public class Monitor {

  public Monitor(String name) {
    _name = name;
  }

  public Monitor start() {
    _startTime = System.currentTimeMillis();
    return this;
  }

  public Monitor stop() {
    _elapsedTime += (System.currentTimeMillis() - _startTime);
    return this;
  }

  public long getElapsedTime() {
    return _elapsedTime;
  }

  public String getStatus() {
    StringBuffer msg = new StringBuffer(_name).append(" executed in ").append(_elapsedTime).append(" milliseconds.");
    return msg.toString();
  }

  public String toString() {
    return new StringBuffer(this.getClass().getName())
        .append("_name='").append(_name).append("'")
        .append("; _elapsedTime=").append(_elapsedTime)
        .append("}").append(super.toString()).toString();
  }


  private String _name;
  private long _startTime;
  private long _elapsedTime;}
