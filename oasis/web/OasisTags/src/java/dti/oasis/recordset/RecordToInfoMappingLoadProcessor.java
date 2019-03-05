package dti.oasis.recordset;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.Info;
import dti.oasis.busobjs.InfoGroup;

/**
 * Add the processed Records to the contained DefaultInfoGroup.
 * By default, false is returned from postProcessRecord(),
 * so that the Records are not added to the resulting RecordSet.
 * Specify true for acceptRecords if the
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 18, 2006
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
public class RecordToInfoMappingLoadProcessor extends DefaultRecordLoadProcessor {


    public RecordToInfoMappingLoadProcessor(InfoGroup infoGroup, Class infoClass) {
        this(infoGroup, infoClass, false);
    }

    public RecordToInfoMappingLoadProcessor(InfoGroup infoGroup, Class infoClass, boolean acceptRecords) {
        m_infoGroup = infoGroup;
        m_infoClass = infoClass;
        m_acceptRecords = acceptRecords;

        if (m_infoGroup == null) {
            throw new IllegalArgumentException("RecordToInfoMappingLoadProcessor requires a non-null infoGroup parameter.");
        }
        if (m_infoClass == null) {
            throw new IllegalArgumentException("RecordToInfoMappingLoadProcessor requires a non-null infoClass parameter.");
        }
        if (!Info.class.isAssignableFrom(infoClass)) {
            throw new IllegalArgumentException("RecordToInfoMappingLoadProcessor requires that the infoClass parameter<" + m_infoClass + "> implement the dti.oasis.busobjs.Info interface.");
        }
    }

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {

        // Create a new instance of the Info class
        Info info = null;
        try {
            info = (Info) m_infoClass.newInstance();
        } catch (ClassCastException e) {
            throw new AppException("Failed to create an instance of the class<" + m_infoClass.getName() + ">", e);
        } catch (Exception e) {
            throw new AppException("Failed to create an instance of the class<" + m_infoClass.getName() + ">", e);
        }

        // Map the Record to Info object
        m_recordBeanMapper.map(record, info);

        // Add the new Info object to the DefaultInfoGroup
        m_infoGroup.add(info);
        return m_acceptRecords;
    }

    private InfoGroup m_infoGroup;
    private Class m_infoClass;
    private boolean m_acceptRecords = false;
    private RecordBeanMapper m_recordBeanMapper = new RecordBeanMapper();
}
