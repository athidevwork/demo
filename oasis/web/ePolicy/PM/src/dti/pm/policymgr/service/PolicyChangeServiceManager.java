package dti.pm.policymgr.service;

import com.delphi_tech.ows.policychangeservice.MedicalMalpracticePolicyChangeRequestType;
import com.delphi_tech.ows.policychangeservice.MedicalMalpracticePolicyChangeResultType;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/24/12
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/20/2014       awu         Renamed savePolicy to changePolicy.
 * ---------------------------------------------------
 */
public interface PolicyChangeServiceManager {
    public MedicalMalpracticePolicyChangeResultType changePolicy(MedicalMalpracticePolicyChangeRequestType policyChangeRequest, MedicalMalpracticePolicyChangeResultType policyChangeResult);

}
