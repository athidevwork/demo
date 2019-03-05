package dti.oasis.obr.impl;

import dti.oasis.obr.PackageMetaData;
import org.drools.KnowledgeBase;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 1, 2011
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
public class KnowledgeBaseInfo {
    public KnowledgeBaseInfo() {
    }

    public KnowledgeBaseInfo(long lastModifiedTime, KnowledgeBase knowledgeBase, PackageMetaData packageMetaData) {
        m_lastModifiedTime = lastModifiedTime;
        m_knowledgeBase = knowledgeBase;
        m_packageMetaData = packageMetaData;
    }

    public long getLastModifiedTime() {
        return m_lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        m_lastModifiedTime = lastModifiedTime;
    }

    public KnowledgeBase getKnowledgeBase() {
        return m_knowledgeBase;
    }

    public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
        m_knowledgeBase = knowledgeBase;
    }

    public PackageMetaData getPackageMetaData() {
        return m_packageMetaData;
    }

    public void setPackageMetaData(PackageMetaData packageMetaData) {
        m_packageMetaData = packageMetaData;
    }

    public KnowledgeBase getRemoveRecordKnowledgeBase() {
        return m_removeRecordKnowledgeBase;
    }

    public void setRemoveRecordKnowledgeBase(KnowledgeBase removeRecordKnowledgeBase) {
        m_removeRecordKnowledgeBase = removeRecordKnowledgeBase;
    }

    @Override
    public String toString() {
        return "KnowledgeBaseInfo{" +
            "m_lastModifiedTime=" + m_lastModifiedTime +
            ", m_knowledgeBase=" + m_knowledgeBase +
            ", m_removeRecordKnowledgeBase=" + m_removeRecordKnowledgeBase +
            ", m_packageMetaData=" + m_packageMetaData +
            '}';
    }

    private long m_lastModifiedTime;
    private KnowledgeBase m_knowledgeBase;
    private KnowledgeBase m_removeRecordKnowledgeBase;
    private PackageMetaData m_packageMetaData;
}
