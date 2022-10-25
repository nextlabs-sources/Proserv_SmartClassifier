package com.nextlabs.smartclassifier.database.manager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.LockAcquisitionException;

import com.nextlabs.smartclassifier.constant.ReportExecutionStatus;
import com.nextlabs.smartclassifier.database.dao.ReportDAO;
import com.nextlabs.smartclassifier.database.dao.ReportExecutionDAO;
import com.nextlabs.smartclassifier.database.entity.Report;
import com.nextlabs.smartclassifier.database.entity.ReportExecution;
import com.nextlabs.smartclassifier.database.entity.ReportSchedule;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;

public class ReportExecutionManager 
		extends Manager {
	
	private ReportExecutionDAO reportExecutionDAO;
	
	public ReportExecutionManager(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
		this.reportExecutionDAO = new ReportExecutionDAO(sessionFactory, session);
	}
	
	public ReportExecution getLastExecution(Long reportId) 
			throws ManagerException {
		if(reportId != null && reportId > 0) {
			try {
				List<Criterion> criterion = new ArrayList<Criterion>();
				criterion.add(Restrictions.eq(ReportExecution.REPORT_ID, reportId));
				criterion.add(Restrictions.disjunction()
										 .add(Restrictions.eq(ReportExecution.STATUS, ReportExecutionStatus.COMPLETED.getCode()))
										 .add(Restrictions.eq(ReportExecution.STATUS, ReportExecutionStatus.EXECUTING.getCode()))
										 .add(Restrictions.eq(ReportExecution.STATUS, ReportExecutionStatus.INTERRUPTED.getCode())));
				
				List<Order> orders = new ArrayList<Order>();
				orders.add(Order.desc(ReportExecution.START_TIME));
				
				List<ReportExecution> lastExecutions = reportExecutionDAO.findByCriteria(criterion, orders, 1);
				
				if(lastExecutions != null && lastExecutions.size() > 0) {
					return lastExecutions.get(0);
				}
			} catch(HibernateException err) {
				throw new ManagerException(err);
			} catch(Exception err) {
				throw new ManagerException(err);
			}
		}
		
		return null;
	}
	
	public void scheduleExecution(Long reportId)
			throws ManagerException, RecordNotFoundException {
		try {
			Report report = new ReportDAO(sessionFactory, session).get(reportId);
			
			if(report == null) {
				throw new RecordNotFoundException("Report record not found for the given ReportID.");
			}
			
			scheduleExecution(report);
		} catch(HibernateException err) {
			throw new ManagerException(err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public synchronized void scheduleExecution(Report report) 
			throws ManagerException, LockAcquisitionException {
		if(report != null && report.getReportSchedule() != null) {
			try {
				List<Criterion> criterionList = new ArrayList<Criterion>();
				criterionList.add(Restrictions.eq(ReportSchedule.ID, report.getReportSchedule().getId()));
				criterionList.add(Restrictions.disjunction()
												.add(Restrictions.eq(ReportExecution.STATUS, ReportExecutionStatus.QUEUE.getCode()))
												.add(Restrictions.eq(ReportExecution.STATUS, ReportExecutionStatus.EXECUTING.getCode())));
				
				if(reportExecutionDAO.getCount(criterionList) > 0) {
					logger.info("Report " + report.getName() + " already scheduled for execution/executing.");
				} else {
					Date nextScheduleTime = report.getReportSchedule().getNextScheduleTime();
					
					if(nextScheduleTime != null) {
						Date now = new Date();
						ReportExecution reportExecution = new ReportExecution();
						
						reportExecution.setReportSchedule(report.getReportSchedule());
						reportExecution.setStatus(ReportExecutionStatus.QUEUE.getCode());
						reportExecution.setPlanTime(nextScheduleTime);
						reportExecution.setCreatedOn(now);
						reportExecution.setModifiedOn(now);
						
						reportExecutionDAO.saveOrUpdate(reportExecution);
					} else {
						logger.info("Report is not schedule to be execute.");
					}
				}
			} catch(ParseException err) {
				throw new ManagerException(err);
			} catch(LockAcquisitionException err) {
				throw err;
			} catch(HibernateException err) {
				throw new ManagerException(err);
			} catch(Exception err) {
				throw new ManagerException(err);
			}
		} else {
			logger.info("Report does not contains execution schedule.");
		}
	}
	
	public synchronized void updateExecution(Report report)
			throws ManagerException {
		try {
			if(report != null && report.getReportSchedule() != null) {
				Date nextScheduleTime = report.getReportSchedule().getNextScheduleTime();
				// Check for uniqueness
				List<Criterion> criterionList = new ArrayList<Criterion>();
				criterionList.add(Restrictions.eq(ReportExecution.REPORT_ID, report.getId()));
				criterionList.add(Restrictions.eq(ReportExecution.STATUS, ReportExecutionStatus.QUEUE.getCode()));
				
				List<ReportExecution> reportExecutions = reportExecutionDAO.findByCriteria(criterionList);
				
				// No longer need for execution, update scheduled record
				if(nextScheduleTime == null) {
					logger.info("Report is not schedule to be execute. Cancelling schedule if exist.");
					
					if(reportExecutions != null	&& reportExecutions.size() > 0) {
						for(ReportExecution reportExecution : reportExecutions) {
							reportExecution.setStatus(report.isDeleted() ? ReportExecutionStatus.DELETED.getCode() : ReportExecutionStatus.EXPIRED.getCode());
							reportExecution.setModifiedOn(new Date());
						}
						
						reportExecutionDAO.saveOrUpdateAll(reportExecutions);
					}
				} else {
					Date now = new Date();
					// Update or create execution
					if(reportExecutions != null && reportExecutions.size() > 0) {
						for(ReportExecution reportExecution : reportExecutions) {
							if(reportExecution.getPlanTime().getTime() != nextScheduleTime.getTime()) {
								reportExecution.setPlanTime(nextScheduleTime);
								reportExecution.setModifiedOn(now);
							}
						}
						
						reportExecutionDAO.saveOrUpdateAll(reportExecutions);
					} else {
						ReportExecution reportExecution = new ReportExecution();
						
						reportExecution.setReportSchedule(report.getReportSchedule());
						reportExecution.setStatus(ReportExecutionStatus.QUEUE.getCode());
						reportExecution.setPlanTime(nextScheduleTime);
						reportExecution.setCreatedOn(now);
						reportExecution.setModifiedOn(now);
						
						reportExecutionDAO.saveOrUpdate(reportExecution);
					}
				}
			}
		} catch(ParseException err) {
			throw new ManagerException(err);
		} catch(HibernateException err) {
			throw new ManagerException(err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
}
