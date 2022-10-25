package com.nextlabs.smartclassifier.database.manager;

import com.nextlabs.smartclassifier.database.dao.DocumentRecordDAO;
import com.nextlabs.smartclassifier.database.entity.DocumentRecord;
import com.nextlabs.smartclassifier.exception.ManagerException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentRecordManager
        extends Manager {
	
    private static final String INVALID_FILE_ID = "0-0";
    private DocumentRecordDAO documentRecordDAO;
    
    public DocumentRecordManager(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
        this.documentRecordDAO = new DocumentRecordDAO(sessionFactory, session);
    }
    
    public DocumentRecord getDocumentRecord(String absoluteFilePath)
            throws ManagerException {
        try {
            return documentRecordDAO.get(absoluteFilePath);
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }
    
    public void addDocumentRecord(DocumentRecord documentRecord)
            throws ManagerException {
        try {
            DocumentRecord record = documentRecordDAO.get(documentRecord.getAbsoluteFilePath());

            if (record != null) {
                record.setFileId(documentRecord.getFileId());
                record.setErrorMessage(documentRecord.getErrorMessage());
                record.isExtracted(documentRecord.isExtracted());
                record.setExtractionTime(documentRecord.getExtractionTime());
                record.setFailCount(documentRecord.getFailCount());
                record.setFileType(documentRecord.getFileType());
                record.setFileSize(documentRecord.getFileSize());
                record.isIndexed(documentRecord.isIndexed());
                record.setIndexingTime(documentRecord.getIndexingTime());
                record.setLastUpdated(new Date());
                record.setLastModified(documentRecord.getLastModified());
                
                documentRecordDAO.saveOrUpdate(record);
            } else {
                documentRecordDAO.saveOrUpdate(documentRecord);
            }
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }
    
    public void updateDocumentRecord(DocumentRecord documentRecord)
            throws ManagerException {
        try {
            DocumentRecord record = documentRecordDAO.get(documentRecord.getAbsoluteFilePath());
            
            if (record != null) {
                if (!INVALID_FILE_ID.equals(documentRecord.getFileId())) {
                    record.setFileId(documentRecord.getFileId());
                }
                record.setErrorMessage(documentRecord.getErrorMessage());
                record.isExtracted(documentRecord.isExtracted());
                record.setExtractionTime(documentRecord.getExtractionTime());
                record.setFailCount(documentRecord.getFailCount());
                record.setFileType(documentRecord.getFileType());
                record.setFileSize(documentRecord.getFileSize());
                record.isIndexed(documentRecord.isIndexed());
                record.setIndexingTime(documentRecord.getIndexingTime());
                record.setLastUpdated(new Date());
                record.setLastModified(documentRecord.getLastModified());
                
                documentRecordDAO.saveOrUpdate(record);
            } else {
                documentRecordDAO.saveOrUpdate(documentRecord);
            }
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }
    
    public String deleteDocumentRecord(String absoluteFilePath)
            throws ManagerException {
        String fileId = null;
        
        try {
            if (absoluteFilePath != null && absoluteFilePath.length() > 0) {
                DocumentRecord record = documentRecordDAO.get(absoluteFilePath);
                
                if (record != null) {
                    fileId = record.getFileId();
                    documentRecordDAO.delete(record);
                }
            }
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
        
        return fileId;
    }
    
    public boolean isDocumentProcessed(String absoluteFilePath, long lastModified)
            throws ManagerException {
        try {
            DocumentRecord documentRecord = documentRecordDAO.get(absoluteFilePath);

            return documentRecord != null && (documentRecord.isIndexed() && documentRecord.isExtracted() && documentRecord.getLastModified() == lastModified);

        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }
    
    public int getTotalCount()
            throws ManagerException {
        try {
            return documentRecordDAO.getCount();
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }
    
    public long getSuccessCount()
            throws ManagerException {
        try {
            List<Criterion> criterion = new ArrayList<>();
            criterion.add(Restrictions.eq(DocumentRecord.EXTRACTED, true));
            criterion.add(Restrictions.eq(DocumentRecord.INDEXED, true));
            
            return documentRecordDAO.getCount(criterion);
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    public long getFailCount()
            throws ManagerException {
        try {
            List<Criterion> criterion = new ArrayList<>();
            criterion.add(Restrictions.eqOrIsNull(DocumentRecord.EXTRACTED, false));
            criterion.add(Restrictions.eqOrIsNull(DocumentRecord.INDEXED, false));
            
            return documentRecordDAO.getCount(criterion);
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }
    
    public long getVolumeUsed()
            throws ManagerException {
        try {
            long sum = documentRecordDAO.getSum(DocumentRecord.FILE_SIZE, Restrictions.eq(DocumentRecord.EXTRACTED, true));
            return sum;
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }
}
