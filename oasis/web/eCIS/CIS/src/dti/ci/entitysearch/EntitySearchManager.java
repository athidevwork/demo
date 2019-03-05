package dti.ci.entitysearch;

import dti.ci.helpers.ICIConstants;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/26/2018
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface EntitySearchManager extends ICIConstants {

    /**
     * Gets the maximum number of rows allowed for entity search record set.
     *
     * @return int - The maximum number of rows allowed for an entity search.
     * @throws Exception
     */
    int getEntitySearchMaxNum();

    /**
     * Method to determine if a valid policyNo included within the search criteria
     *
     * @param inputRecord
     * @return
     */
    boolean isPolicyNoIncludedWithinSearch(Record inputRecord);

    /**
     * Get entity recordSet with search criteria. Check policy no and claim security. Filter result if necessary
     *
     * @param inputRecord
     * @return
     */
    RecordSet searchEntities(Record inputRecord);


    /**
     * Select entity on Entity Select Search popup page
     *
     * @param inputRecord
     * @return
     */
    RecordSet searchEntitiesForPopup(Record inputRecord);

    /**
     * Retrieve claims the entity participates
     *
     * @param inputRecord
     * @return
     */
    RecordSet getEntityClaims(Record inputRecord);

}
