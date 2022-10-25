package com.nextlabs.smartclassifier.database.manager;

import com.nextlabs.smartclassifier.constant.ComponentType;
import com.nextlabs.smartclassifier.database.dao.*;
import com.nextlabs.smartclassifier.database.entity.*;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ExtractorManager
        extends Manager {

    private ExtractorDAO extractorDAO;

    public ExtractorManager(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
        this.extractorDAO = new ExtractorDAO(sessionFactory, session);
    }

    /**
     * Initialize newly installed Extractor with default max file size = 10 MB.
     *
     * @param hostname Hostname where the extractor installed on.
     * @throws ManagerException Database and not handled exceptions.
     */
    public void initDocSizeLimits(String hostname)
            throws ManagerException {
        try {
            Extractor extractor = getExtractorByHostname(hostname);

            if (extractor != null
                    && (extractor.getDocumentSizeLimits() == null
                    || extractor.getDocumentSizeLimits().size() == 0)) {
                logger.info("Initialize document extractor with default file size limit.");

                DocumentExtractorDAO documentExtractorDAO = new DocumentExtractorDAO(sessionFactory, session);
                List<DocumentExtractor> documentExtractors = documentExtractorDAO.getAll();

                if (documentExtractors != null && documentExtractors.size() > 0) {
                    for (DocumentExtractor documentExtractor : documentExtractors) {
                        DocumentSizeLimit documentSizeLimit = new DocumentSizeLimit();

                        documentSizeLimit.setDocumentExtractor(documentExtractor);
                        documentSizeLimit.setExtractor(extractor);
                        documentSizeLimit.setMaxFileSize(documentExtractor.getDefaultSizeLimit());

                        extractor.getDocumentSizeLimits().add(documentSizeLimit);
                    }

                    extractor.setModifiedOn(new Date());

                    extractorDAO.saveOrUpdate(extractor);
                }
            }
        } catch (Exception err) {
            throw new ManagerException(err.getMessage(), err);
        }
    }

    public List<Extractor> getExtractors()
            throws ManagerException {
        try {
            return extractorDAO.getAll();
        } catch (Exception err) {
            throw new ManagerException(err.getMessage(), err);
        }
    }

    public List<Extractor> getExtractorsWithLastHeartbeat()
            throws ManagerException {
        try {
            return getLastHeartbeat(new HeartbeatDAO(sessionFactory, session), extractorDAO.getAll());
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    public List<Extractor> getExtractors(List<Criterion> criterion, List<Order> order, PageInfo pageInfo)
            throws ManagerException {
        try {
            List<Extractor> extractors = extractorDAO.findByCriteria(criterion, order, pageInfo);
            ;

            if (extractors != null) {
                HeartbeatDAO heartbeatDAO = new HeartbeatDAO(sessionFactory, session);
                ExecutionWindowAssociationDAO executionWindowAssociationDAO = new ExecutionWindowAssociationDAO(sessionFactory, session);

                for (Extractor extractor : extractors) {
                	List<Criterion> heartbeatCriteria = new ArrayList<Criterion>();
                	heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_ID, extractor.getId()));
                	heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_TYPE, ComponentType.EXTRACTOR.getCode()));
                	
                    List<Heartbeat> heartbeats = heartbeatDAO.findByCriteria(heartbeatCriteria);

                    if (heartbeats != null
                    		&& heartbeats.size() > 0) {
                        extractor.setLastHeartbeat(heartbeats.get(0).getLastHeartbeat());
                    }

                    List<Criterion> executionWindowCriteria = new ArrayList<Criterion>();
                    executionWindowCriteria.add(Restrictions.eq(ExecutionWindowAssociation.COMPONENT_ID, extractor.getId()));
                    executionWindowCriteria.add(Restrictions.eq(ExecutionWindowAssociation.COMPONENT_TYPE, ComponentType.EXTRACTOR.getCode()));

                    List<ExecutionWindowAssociation> executionWindowAssociations = executionWindowAssociationDAO.findByCriteria(executionWindowCriteria);

                    if (executionWindowAssociations != null && executionWindowAssociations.size() > 0) {
                        for (ExecutionWindowAssociation executionWindowAssociation : executionWindowAssociations) {
                            extractor.getExecutionWindowSets().add(executionWindowAssociation.getExecutionWindowSet());
                        }
                    }
                }
            }

            return extractors;
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    public Extractor getExtractorById(Long id)
            throws ManagerException {
        try {
            logger.debug("Get extractor with id " + id);
            Extractor extractor = extractorDAO.get(id);

            if (extractor != null) {
                logger.debug("Extractor found with id " + id);
                
                List<Criterion> heartbeatCriteria = new ArrayList<Criterion>();
                heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_ID, id));
                heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_TYPE, ComponentType.EXTRACTOR.getCode()));
                
                List<Heartbeat> heartbeats = new HeartbeatDAO(sessionFactory, session).findByCriteria(heartbeatCriteria);

                if (heartbeats != null
                		&& heartbeats.size() > 0) {
                    extractor.setLastHeartbeat(heartbeats.get(0).getLastHeartbeat());
                }

                ExecutionWindowAssociationDAO executionWindowAssociationDAO = new ExecutionWindowAssociationDAO(sessionFactory, session);

                List<Criterion> executionWindowCriteria = new ArrayList<Criterion>();
                executionWindowCriteria.add(Restrictions.eq(ExecutionWindowAssociation.COMPONENT_ID, extractor.getId()));
                executionWindowCriteria.add(Restrictions.eq(ExecutionWindowAssociation.COMPONENT_TYPE, ComponentType.EXTRACTOR.getCode()));

                List<Order> orderList = new ArrayList<Order>();
                orderList.add(Order.asc(ExecutionWindowAssociation.DISPLAY_ORDER));

                List<ExecutionWindowAssociation> executionWindowAssociations = executionWindowAssociationDAO.findByCriteria(executionWindowCriteria, orderList);

                if (executionWindowAssociations != null && executionWindowAssociations.size() > 0) {
                    for (ExecutionWindowAssociation executionWindowAssociation : executionWindowAssociations) {
                        extractor.getExecutionWindowSets().add(executionWindowAssociation.getExecutionWindowSet());
                    }
                }

                return extractor;
            }
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }

        return null;
    }

    public Extractor getExtractorByHostname(String hostname)
            throws ManagerException {
        try {
            logger.debug("Get extractor for hostname " + hostname);
            List<Extractor> extractors = extractorDAO.findByCriteria(Restrictions.eq(Extractor.HOSTNAME, hostname));

            if (extractors != null 
            		&& extractors.size() > 0) {
                logger.debug("Extractor found for hostname " + hostname);
                return extractors.get(0);
            }
        } catch (Exception err) {
            throw new ManagerException(err.getMessage(), err);
        }

        return null;
    }

    public void updateConfigLoadedDate(String hostname, Date configLoadedOn)
            throws ManagerException {
        try {
            logger.debug("Update extractor configuration loaded for hostname " + hostname + " time to " + configLoadedOn);
            List<Extractor> extractors = extractorDAO.findByCriteria(Restrictions.eq(Extractor.HOSTNAME, hostname));

            if (extractors != null && extractors.size() > 0) {
                Extractor extractor = extractors.get(0);
                extractor.setConfigLoadedOn(configLoadedOn);

                extractorDAO.saveOrUpdate(extractor);
            }
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    public void updateExtractor(Extractor extractor)
            throws ManagerException, RecordNotFoundException, IllegalArgumentException {
        try {
            logger.debug("Update extractor configuration for extractor id " + extractor.getId());
            Extractor entity = extractorDAO.get(extractor.getId());

            if (entity != null) {
                JMSProfileDAO jmsProfileDAO = new JMSProfileDAO(sessionFactory, session);
                DocumentSizeLimitDAO documentSizeLimitDAO = new DocumentSizeLimitDAO(sessionFactory, session);
                DocumentExtractorDAO documentExtractorDAO = new DocumentExtractorDAO(sessionFactory, session);

                JMSProfile jmsProfile = jmsProfileDAO.get(extractor.getJMSProfile().getId());
                if (jmsProfile == null) {
                    throw new IllegalArgumentException("Invalid JMS profile. Unable to retrieve JMS profile for given value.");
                }

                Date now = new Date();
                entity.setName(extractor.getName());
                entity.setJMSProfile(jmsProfile);
                entity.setDocumentExtractorCount(extractor.getDocumentExtractorCount());
                entity.setMinHeapMemory(extractor.getMinHeapMemory());
                entity.setConfigReloadInterval(extractor.getConfigReloadInterval());

                // Remove original records for document size limit
                if (entity.getDocumentSizeLimits() != null) {
                    Iterator<DocumentSizeLimit> iterator = entity.getDocumentSizeLimits().iterator();

                    while (iterator.hasNext()) {
                        DocumentSizeLimit documentSizeLimit = iterator.next();
                        iterator.remove();
                        documentSizeLimitDAO.delete(documentSizeLimit);
                    }

                    documentSizeLimitDAO.flush();
                }

                if (extractor.getDocumentSizeLimits() != null) {
                    for (DocumentSizeLimit docSizeLimit : extractor.getDocumentSizeLimits()) {
                        DocumentSizeLimit documentSizeLimit = new DocumentSizeLimit();

                        documentSizeLimit.setExtractor(entity);
                        documentSizeLimit.setDocumentExtractor(documentExtractorDAO.get(docSizeLimit.getDocumentExtractor().getId()));
                        documentSizeLimit.setMaxFileSize(docSizeLimit.getMaxFileSize());

                        entity.getDocumentSizeLimits().add(documentSizeLimit);
                    }
                }

                entity.setModifiedOn(now);

                // Remove original record for execution window
                ExecutionWindowAssociationDAO executionWindowAssociationDAO = new ExecutionWindowAssociationDAO(sessionFactory, session);
                
                List<Criterion> executionWindowCriteria = new ArrayList<Criterion>();
                executionWindowCriteria.add(Restrictions.eq(ExecutionWindowAssociation.COMPONENT_ID, entity.getId()));
                executionWindowCriteria.add(Restrictions.eq(ExecutionWindowAssociation.COMPONENT_TYPE, ComponentType.EXTRACTOR.getCode()));
                
                List<ExecutionWindowAssociation> executionWindowAssociations = executionWindowAssociationDAO.findByCriteria(executionWindowCriteria);

                if (executionWindowAssociations != null && executionWindowAssociations.size() > 0) {
                    for (ExecutionWindowAssociation executionWindowAssociation : executionWindowAssociations) {
                        executionWindowAssociationDAO.delete(executionWindowAssociation);
                    }

                    executionWindowAssociationDAO.flush();
                }

                // Set new execution window
                if (extractor.getExecutionWindowSets() != null) {
                    ExecutionWindowSetDAO executionWindowSetDAO = new ExecutionWindowSetDAO(sessionFactory, session);

                    int displayOrder = 1;
                    for (ExecutionWindowSet executionWindowSet : extractor.getExecutionWindowSets()) {
                        ExecutionWindowAssociation executionWindowAssociation = new ExecutionWindowAssociation();
                        executionWindowAssociation.setComponentId(entity.getId());
                        executionWindowAssociation.setComponentType(ComponentType.EXTRACTOR.getCode());
                        executionWindowAssociation.setDisplayOrder(displayOrder);
                        executionWindowAssociation.setExecutionWindowSet(executionWindowSetDAO.get(executionWindowSet.getId()));
                        executionWindowAssociation.setCreatedOn(now);
                        executionWindowAssociation.setModifiedOn(now);

                        executionWindowAssociationDAO.saveOrUpdate(executionWindowAssociation);

                        displayOrder++;
                    }
                }

                extractorDAO.saveOrUpdate(entity);
            } else {
                throw new RecordNotFoundException("Extractor record not found for the given ExtractorID.");
            }
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    public long getRecordCount(List<Criterion> criterion)
            throws ManagerException {
        try {
            return extractorDAO.getCount(criterion);
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    private List<Extractor> getLastHeartbeat(HeartbeatDAO heartbeatDAO, List<Extractor> extractors)
            throws Exception {
        if (heartbeatDAO != null
                && extractors != null) {
            for (Extractor extractor : extractors) {
				List<Criterion> heartbeatCriteria = new ArrayList<Criterion>();
				heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_ID, extractor.getId()));
				heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_TYPE, ComponentType.EXTRACTOR.getCode()));
				
				List<Heartbeat> heartbeats = heartbeatDAO.findByCriteria(heartbeatCriteria);

                if (heartbeats != null
                		&& heartbeats.size() > 0) {
                    extractor.setLastHeartbeat(heartbeats.get(0).getLastHeartbeat());
                }
            }
        }

        return extractors;
    }

    /**
     * Gets the extractor count
     *
     * @return
     */
    public long getExtractorCount() {
        return extractorDAO.getCount();
    }
}
