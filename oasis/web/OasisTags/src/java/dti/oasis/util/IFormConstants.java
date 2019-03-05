package dti.oasis.util;
/**
 * Web Form Constants
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 1, 2004 
 * @author jbe
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/14/2016       mlm         170307 - Integration of Ghostdraft.
 * 07/27/2016       mlm         178416 - Enhanced to support os_form.template_id.
 * 03/30/2016       mlm         184455 - Refactored to handle data hold form for FM.
 * 06/13/2017       mlm         183484 - Enhanced to support for GhostDraft based Form Letter in UFE.
 * 08/24/2017       mlm         187804 - Refactored to get data entry design name from Ghost Draft package for interactive forms.
 * ---------------------------------------------------
*/

public interface IFormConstants {
    public String KEY_SOURCE_TABLE_NAME = "sourceTableName";
    public String KEY_SOURCE_RECORD_FK = "sourceRecordFk";
    public String KEY_FORM_CODE = "formCode";
    public String KEY_FORM_ID = "formId";
    public String KEY_FORM_DESC = "formDesc";
    public String KEY_FORM_TYPE = "formType";
    public String KEY_DATA_XML = "dataXML";
    public String KEY_VARIABLE_XML = "variablesXML";
    public String KEY_REQUEST_ID = "requestId";
    public String KEY_REQUEST_QUEUE_ID = "osRequestQueueId";
    public String KEY_MANUSCRIPT_ENDORSEMENT_ID = "manuscriptEndorsementId";
    public String KEY_EXTERNAL_ID = "externalId";
    public String ELOQUENCE_PRODUCT_ID = "ELOQUENCE";
    public String GHOSTDRAFT_PRODUCT_ID = "GHOSTDRAFT";
    public String KEY_TEMPLATE_ID = "templateId";
    public String DOC_GEN_PRODUCT_NAME = "docGenPrdName";
    public String KEY_SUBSYSTEM_CODE = "subSystemCode";
    public String KEY_IS_FORMLETTER = "isFormLetter";
    public String KEY_ALL_FIELDS = "allFields";
    public String KEY_UFE_FORM_REQUEST_ID = "ufeFormRequestId";
    public String KEY_UFE_FORM_INSTANCE_ID = "ufeFormInstanceId";
    public String KEY_DATA_ENTRY_DESIGN_NAME = "dataEntryDesignName";
}
