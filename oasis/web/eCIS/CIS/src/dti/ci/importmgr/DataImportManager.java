package dti.ci.importmgr;

import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/23/14
 *
 * @author ldong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/26/2014       Elvin       Issue 157520: add isValidStateAndCounty
 * ---------------------------------------------------
 */
public interface DataImportManager {

    /**
     * saveDataImport
     * @param entityRs
     * @param addressRs
     * @param phoneRs
     */
    public boolean saveDataImport(RecordSet entityRs, RecordSet addressRs, RecordSet phoneRs, RecordSet licenseRs);

    /**
     * isValidStateAndCounty
     * @param stateCode
     * @param countyCode
     */
    public boolean isValidStateAndCounty(String stateCode, String countyCode);
}
