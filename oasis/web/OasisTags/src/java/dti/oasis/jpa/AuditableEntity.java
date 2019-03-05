package dti.oasis.jpa;

import javax.persistence.Entity;
import java.util.Date;

/**
 * <p/>
 * <p/>
 * <p>(C) 2014 Delphi Technology, inc. (dti)</p>
 * Date: 10/24/2014
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public interface AuditableEntity {

    public Date getSysCreateTime();

    public void setSysCreateTime(Date sysCreateTime);

    public String getSysCreatedBy();

    public void setSysCreatedBy(String sysCreatedBy);

    public Date getSysUpdateTime();

    public void setSysUpdateTime(Date sysUpdateTime);

    public String getSysUpdatedBy();

    public void setSysUpdatedBy(String sysUpdatedBy);

}
