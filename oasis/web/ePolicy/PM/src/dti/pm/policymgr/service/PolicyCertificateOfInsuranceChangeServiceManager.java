package dti.pm.policymgr.service;

import com.delphi_tech.ows.policycertificateofinsurancechangeservice.MedicalMalpracticeCertificateOfLiabilityChangeRequestType;
import com.delphi_tech.ows.policycertificateofinsurancechangeservice.MedicalMalpracticeCertificateOfLiabilityChangeResultType;


/**
 * <p>(C) 2017 Delphi Technology, inc. (dti)</p>
 * User: wrong
 * Date: 09/08/2017
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/08/2017       wrong       187839 - Create for COI change service.
 * ---------------------------------------------------
 */
public interface PolicyCertificateOfInsuranceChangeServiceManager {
    public MedicalMalpracticeCertificateOfLiabilityChangeResultType generateCoi(MedicalMalpracticeCertificateOfLiabilityChangeRequestType medicalMalpracticeCertificateOfLiabilityChangeRequest);
}
