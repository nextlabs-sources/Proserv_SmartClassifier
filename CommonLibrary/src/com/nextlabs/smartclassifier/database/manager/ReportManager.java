package com.nextlabs.smartclassifier.database.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.nextlabs.smartclassifier.constant.ExecutionType;
import com.nextlabs.smartclassifier.constant.ReportExecutionStatus;
import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.dao.ReportDAO;
import com.nextlabs.smartclassifier.database.dao.ReportExecutionDAO;
import com.nextlabs.smartclassifier.database.dao.ReportFilterDAO;
import com.nextlabs.smartclassifier.database.dao.ReportFilterGroupDAO;
import com.nextlabs.smartclassifier.database.dao.ReportRecipientDAO;
import com.nextlabs.smartclassifier.database.entity.Report;
import com.nextlabs.smartclassifier.database.entity.ReportExecution;
import com.nextlabs.smartclassifier.database.entity.ReportFilter;
import com.nextlabs.smartclassifier.database.entity.ReportFilterGroup;
import com.nextlabs.smartclassifier.database.entity.ReportRecipient;
import com.nextlabs.smartclassifier.database.entity.ReportSchedule;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordInUseException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.exception.RecordUnmatchedException;

public class ReportManager
		extends Manager {
	
	private ReportDAO reportDAO;
	
	public ReportManager(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
		this.reportDAO = new ReportDAO(sessionFactory, session);
	}
	
	public List<Report> getReports()
			throws ManagerException {
		try {
			return reportDAO.getAll();
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public List<Report> getReports(List<Criterion> criterion, List<Order> order, PageInfo pageInfo)
			throws ManagerException {
		try {
			List<Report> reports = reportDAO.findByCriteria(criterion, order, pageInfo);
			
			if(reports != null) {
				for(Report report : reports) {
					if(report.getReportFilterGroups() != null) {
						for(ReportFilterGroup reportFilterGroup : report.getReportFilterGroups()) {
							reportFilterGroup.getReportFilters();
						}
					}
					report.getReportSchedule();
				}
			}
			
			return reports;
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public Report getReportById(Long id) 
			throws ManagerException {
		try {
			logger.debug("Get report with id " + id);
			Report report = reportDAO.get(id);
			
			if(report != null) {
				logger.debug("Report found with id " + id);
				
				if(report.getReportFilterGroups() != null) {
					for(ReportFilterGroup reportFilterGroup : report.getReportFilterGroups()) {
						reportFilterGroup.getReportFilters();
					}
				}
				report.getReportSchedule();
				report.setLastAccessedOn(new Date());
				reportDAO.saveOrUpdate(report);
				
				return report;
			}
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
		
		return null;
	}
	
	public Long addReport(Report report)
			throws ManagerException {
		try {
			logger.debug("Create report.");
			
			Report entity = new Report();
			Date now = new Date();
			
			entity.setName(report.getName());
			entity.setDescription(report.getDescription());
			entity.setType(report.getType());
			entity.setRange(report.getRange());
			entity.setEventStatus(report.getEventStatus());
			entity.isDeleted(report.isDeleted());
			entity.setCreatedBy(report.getCreatedBy());
			entity.setCreatedOn(now);
			entity.setModifiedOn(now);
			
			if(report.getReportFilterGroups() != null && report.getReportFilterGroups().size() > 0) {
				int filterGroupOrder = 1;
				for(ReportFilterGroup filterGroup : report.getReportFilterGroups()) {
					ReportFilterGroup filterGroupEntity = new ReportFilterGroup();
					filterGroupEntity.setDisplayOrder(filterGroupOrder);
					filterGroupEntity.setReport(entity);
					filterGroupEntity.setOperator(filterGroup.getOperator());
					
					if(filterGroup.getReportFilters() != null && filterGroup.getReportFilters().size() > 0) {
						int filterOrder = 1;
						for(ReportFilter filter : filterGroup.getReportFilters()) {
							ReportFilter filterEntity = new ReportFilter();
							
							filterEntity.setReportFilterGroup(filterGroupEntity);
							filterEntity.setDisplayOrder(filterOrder);
							filterEntity.setFieldName(filter.getFieldName());
							filterEntity.setOperator(filter.getOperator());
							filterEntity.setValue(filter.getValue());
							
							filterGroupEntity.getReportFilters().add(filterEntity);
							filterOrder++;
						}
					}
					
					entity.getReportFilterGroups().add(filterGroupEntity);
					filterGroupOrder++;
				}
			}
			
			if(ExecutionType.SCHEDULED.getCode().equalsIgnoreCase(report.getType())
					&& report.getReportSchedule() != null) {
				ReportSchedule scheduleEntity = new ReportSchedule();
				
				scheduleEntity.setReport(entity);
				scheduleEntity.setExecutionFrequency(entity.getReportSchedule().getExecutionFrequency());
				scheduleEntity.isEnabled(entity.getReportSchedule().isEnabled());
				scheduleEntity.setEffectiveFrom(entity.getReportSchedule().getEffectiveFrom());
				scheduleEntity.setEffectiveUntil(entity.getReportSchedule().getEffectiveUntil());
				scheduleEntity.setEmailSubject(entity.getReportSchedule().getEmailSubject());
				scheduleEntity.setEmailContent(entity.getReportSchedule().getEmailContent());
				
				if(report.getReportSchedule().getReportRecipients() != null
						&& report.getReportSchedule().getReportRecipients().size() > 0) {
					int displayOrder = 1;
					for(ReportRecipient reportRecipient : report.getReportSchedule().getReportRecipients()) {
						ReportRecipient reportRecipientEntity = new ReportRecipient();
						
						reportRecipientEntity.setReportSchedule(scheduleEntity);
						reportRecipientEntity.setDisplayOrder(displayOrder);
						reportRecipientEntity.setField(reportRecipient.getField());
						reportRecipientEntity.setEmail(reportRecipient.getEmail());
						
						scheduleEntity.getReportRecipients().add(reportRecipientEntity);
						displayOrder++;
					}
				}
				
				entity.setReportSchedule(scheduleEntity);
			}
			
			reportDAO.saveOrUpdate(entity);
			
			if(ExecutionType.SCHEDULED.getCode().equalsIgnoreCase(report.getType())
					&& report.getReportSchedule() != null
					&& report.getReportSchedule().isEnabled()) {
				ReportExecutionManager reportExecutionManager = new ReportExecutionManager(sessionFactory, session);
				reportExecutionManager.scheduleExecution(entity);
			}
			
			return entity.getId();
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public void updateReport(Report report)
			throws ManagerException, RecordNotFoundException, IllegalArgumentException {
		try {
			logger.debug("Update report for report id " + report.getId());
			
			Report entity = reportDAO.get(report.getId());
			if(entity == null) {
				throw new RecordNotFoundException("Report record not found for the given ReportID.");
			}
			
			if(entity.isDeleted()) {
				throw new RecordNotFoundException("Report record not found for the given ReportID.");
			}
			
			entity.setName(report.getName());
			entity.setDescription(report.getDescription());
			entity.setType(report.getType());
			entity.setRange(report.getRange());
			entity.setEventStatus(report.getEventStatus());
			entity.setModifiedBy(report.getModifiedBy());
			entity.setModifiedOn(new Date());
			
			// Remove original filter
			if(entity.getReportFilterGroups() != null && entity.getReportFilterGroups().size() > 0) {
				ReportFilterGroupDAO filterGroupDAO = new ReportFilterGroupDAO(sessionFactory, session);
				Iterator<ReportFilterGroup> filterGroupIterator = entity.getReportFilterGroups().iterator();
				
				while(filterGroupIterator.hasNext()) {
					ReportFilterGroup filterGroup = filterGroupIterator.next();
					if(filterGroup.getReportFilters() != null && filterGroup.getReportFilters().size() > 0) {
						ReportFilterDAO filterDAO = new ReportFilterDAO(sessionFactory, session);
						Iterator<ReportFilter> filterIterator = filterGroup.getReportFilters().iterator();
						
						while(filterIterator.hasNext()) {
							ReportFilter reportFilter = filterIterator.next();
							filterIterator.remove();
							
							filterDAO.delete(reportFilter);
						}
						
						filterDAO.flush();
					}
					
					filterGroupIterator.remove();
					filterGroupDAO.delete(filterGroup);
				}
				
				filterGroupDAO.flush();
			}
			
			// New filter group
			if(report.getReportFilterGroups() != null && report.getReportFilterGroups().size() > 0) {
				int filterGroupOrder = 1;
				for(ReportFilterGroup filterGroup : report.getReportFilterGroups()) {
					ReportFilterGroup filterGroupEntity = new ReportFilterGroup();
					filterGroupEntity.setDisplayOrder(filterGroupOrder);
					filterGroupEntity.setReport(entity);
					filterGroupEntity.setOperator(filterGroup.getOperator());
					
					if(filterGroup.getReportFilters() != null && filterGroup.getReportFilters().size() > 0) {
						int filterOrder = 1;
						for(ReportFilter filter : filterGroup.getReportFilters()) {
							ReportFilter filterEntity = new ReportFilter();
							
							filterEntity.setReportFilterGroup(filterGroupEntity);
							filterEntity.setDisplayOrder(filterOrder);
							filterEntity.setFieldName(filter.getFieldName());
							filterEntity.setOperator(filter.getOperator());
							filterEntity.setValue(filter.getValue());
							
							filterGroupEntity.getReportFilters().add(filterEntity);
							filterOrder++;
						}
					}
					
					entity.getReportFilterGroups().add(filterGroupEntity);
					filterGroupOrder++;
				}
			}
			
			if(report.getReportSchedule() != null) {
				if(entity.getReportSchedule() == null) {
					ReportSchedule reportScheduleEntity = new ReportSchedule();
					
					reportScheduleEntity.setReport(entity);
					reportScheduleEntity.setExecutionFrequency(report.getReportSchedule().getExecutionFrequency());
					reportScheduleEntity.isEnabled(report.getReportSchedule().isEnabled());
					reportScheduleEntity.setEffectiveFrom(report.getReportSchedule().getEffectiveFrom());
					reportScheduleEntity.setEffectiveUntil(report.getReportSchedule().getEffectiveUntil());
					reportScheduleEntity.setEmailSubject(report.getReportSchedule().getEmailSubject());
					reportScheduleEntity.setEmailContent(report.getReportSchedule().getEmailContent());
					
					if(report.getReportSchedule().getReportRecipients() != null 
							&& report.getReportSchedule().getReportRecipients().size() > 0) {
						int displayOrder = 1;
						for(ReportRecipient reportRecipient : report.getReportSchedule().getReportRecipients()) {
							ReportRecipient reportRecipientEntity = new ReportRecipient();
							
							reportRecipientEntity.setReportSchedule(reportScheduleEntity);
							reportRecipientEntity.setDisplayOrder(displayOrder);
							reportRecipientEntity.setField(reportRecipient.getField());
							reportRecipientEntity.setEmail(reportRecipient.getEmail());
							
							reportScheduleEntity.getReportRecipients().add(reportRecipientEntity);
							displayOrder++;
						}
					}
					
					entity.setReportSchedule(reportScheduleEntity);
				} else {
					entity.getReportSchedule().setExecutionFrequency(report.getReportSchedule().getExecutionFrequency());
					entity.getReportSchedule().isEnabled(report.getReportSchedule().isEnabled());
					entity.getReportSchedule().setEffectiveFrom(report.getReportSchedule().getEffectiveFrom());
					entity.getReportSchedule().setEffectiveUntil(report.getReportSchedule().getEffectiveUntil());
					entity.getReportSchedule().setEmailSubject(report.getReportSchedule().getEmailSubject());
					entity.getReportSchedule().setEmailContent(report.getReportSchedule().getEmailContent());
					
					// Remove old recipient list
					if(entity.getReportSchedule().getReportRecipients() != null
							&& entity.getReportSchedule().getReportRecipients().size() > 0) {
						ReportRecipientDAO reportRecipientDAO = new ReportRecipientDAO(sessionFactory, session);
						Iterator<ReportRecipient> reportRecipientIterator = entity.getReportSchedule().getReportRecipients().iterator();
						
						while(reportRecipientIterator.hasNext()) {
							ReportRecipient reportRecipient = reportRecipientIterator.next();
							reportRecipientIterator.remove();
							
							reportRecipientDAO.delete(reportRecipient);
						}
						
						reportRecipientDAO.flush();
					}
					
					// Set new recipient list
					if(report.getReportSchedule().getReportRecipients() != null
							&& report.getReportSchedule().getReportRecipients().size() > 0) {
						int displayOrder = 1;
						for(ReportRecipient reportRecipient : report.getReportSchedule().getReportRecipients()) {
							ReportRecipient reportRecipientEntity = new ReportRecipient();
							
							reportRecipientEntity.setReportSchedule(entity.getReportSchedule());
							reportRecipientEntity.setDisplayOrder(displayOrder);
							reportRecipientEntity.setField(reportRecipient.getField());
							reportRecipientEntity.setEmail(reportRecipient.getEmail());
							
							entity.getReportSchedule().getReportRecipients().add(reportRecipientEntity);
							displayOrder++;
						}
					}
				}
			} else {
				if(entity.getReportSchedule() != null
						&& entity.getReportSchedule().getReportRecipients().size() > 0) {
					ReportRecipientDAO reportRecipientDAO = new ReportRecipientDAO(sessionFactory, session);
					Iterator<ReportRecipient> reportRecipientIterator = entity.getReportSchedule().getReportRecipients().iterator();
					
					while(reportRecipientIterator.hasNext()) {
						ReportRecipient reportRecipient = reportRecipientIterator.next();
						reportRecipientIterator.remove();
						
						reportRecipientDAO.delete(reportRecipient);
					}
					
					reportRecipientDAO.flush();
				}
			}
			
			reportDAO.saveOrUpdate(entity);
			
			// Update report execution
			ReportExecutionManager reportExecutionManager = new ReportExecutionManager(sessionFactory, session);
			reportExecutionManager.updateExecution(entity);
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public void deleteReport(Report report) 
			throws ManagerException, RecordNotFoundException, RecordInUseException, RecordUnmatchedException {
		try {
			logger.debug("Delete report for report id " + report.getId());
			
			Report entity = reportDAO.get(report.getId());
			
			if(entity != null) {
				Date now = new Date();
				
				if(entity.getModifiedOn().getTime() == report.getModifiedOn().getTime()) {
					entity.isDeleted(true);
					entity.setModifiedBy(report.getModifiedBy());
					entity.setModifiedOn(now);
					
					reportDAO.saveOrUpdate(entity);
				} else {
					throw new RecordUnmatchedException("Row was updated or deleted by another transaction.");
				}
				
				if(entity.getReportSchedule() != null) {
					ReportExecutionDAO reportExecutionDAO = new ReportExecutionDAO(sessionFactory, session);
					
					List<Criterion> criterion = new ArrayList<Criterion>();
					criterion.add(Restrictions.eq(ReportExecution.REPORT_ID, entity.getId()));
					criterion.add(Restrictions.eq(ReportExecution.STATUS, ReportExecutionStatus.QUEUE.getCode()));
					
					List<ReportExecution> reportExecutions = reportExecutionDAO.findByCriteria(criterion);
					
					if(reportExecutions != null) {
						for(ReportExecution reportExecution : reportExecutions) {
							reportExecution.setStatus(ReportExecutionStatus.DELETED.getCode());
							reportExecution.setModifiedOn(now);
						}
						
						reportExecutionDAO.saveOrUpdateAll(reportExecutions);
					}
				}
			} else {
				throw new RecordNotFoundException("Report record not found for the given ReportID.");
			}
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public long getRecordCount(List<Criterion> criterion) 
			throws ManagerException {
		try {
			if(criterion == null) 
				logger.info("Record count criterion is null");
			
			if(reportDAO == null)
				logger.info("ReportDAO is null");
			
			return reportDAO.getCount(criterion);
		} catch(HibernateException err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err);
		}
	}
}
