package com.nextlabs.smartclassifier.service.v1.report;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.nextlabs.smartclassifier.constant.ExecutionType;
import com.nextlabs.smartclassifier.database.entity.Report;
import com.nextlabs.smartclassifier.database.entity.ReportExecution;
import com.nextlabs.smartclassifier.database.manager.ReportExecutionManager;
import com.nextlabs.smartclassifier.database.manager.ReportManager;
import com.nextlabs.smartclassifier.dto.v1.ReportDTO;
import com.nextlabs.smartclassifier.dto.v1.request.RetrievalRequest;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class ReportRetrievalService 
		extends Service {
	
	public ReportRetrievalService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public ReportRetrievalService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse getAdHocReports(RetrievalRequest retrievalRequest) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			ReportManager reportManager = new ReportManager(getSessionFactory(), session);
			List<Criterion> criterionList = getCriterion(retrievalRequest);
			criterionList.add(Restrictions.eq(Report.TYPE, ExecutionType.ON_DEMAND.getCode()));
			criterionList.add(Restrictions.eq(Report.DELETED, Boolean.FALSE.booleanValue()));
			List<Order> orderList = getOrder(retrievalRequest);
			if(orderList.size() == 0)
				orderList.add(Order.desc(Report.LAST_ACCESSED_ON));
			List<Report> reports = reportManager.getReports(criterionList, orderList, getPageInfo(retrievalRequest));
			response.setTotalNoOfRecords(reportManager.getRecordCount(criterionList));
			
			transaction.commit();
			
			if(reports != null && reports.size() > 0) {
				response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
				response.setMessage(MessageUtil.getMessage("success.data.loaded"));
				
				List<ReportDTO> reportDTOs = new ArrayList<ReportDTO>();
				for(Report report : reports) {
					reportDTOs.add(new ReportDTO(report));
				}
				
				response.setData(reportDTOs);
			} else {
				response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
				response.setMessage(MessageUtil.getMessage("no.data.found"));
			}
			
			response.setPageInfo(getPageInfo(retrievalRequest));
		} catch(ManagerException | Exception err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("server.error.code"));
			response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
		} finally {
			if(session != null) {
				try {
					session.close();
				} catch(HibernateException err) {
					// Ignore
				}
			}
		}
		
		return response;
	}
	
	public ServiceResponse getScheduledReports(RetrievalRequest retrievalRequest) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			ReportManager reportManager = new ReportManager(getSessionFactory(), session);
			ReportExecutionManager reportExecutionManager = new ReportExecutionManager(getSessionFactory(), session);
			List<Criterion> criterionList = getCriterion(retrievalRequest);
			criterionList.add(Restrictions.eq(Report.TYPE, ExecutionType.SCHEDULED.getCode()));
			criterionList.add(Restrictions.eq(Report.DELETED, Boolean.FALSE.booleanValue()));
			List<Order> orderList = getOrder(retrievalRequest);
			if(orderList.size() == 0)
				orderList.add(Order.desc(Report.LAST_ACCESSED_ON));
			List<Report> reports = reportManager.getReports(criterionList, orderList, getPageInfo(retrievalRequest));
			
			if(reports != null) {
				for(Report report : reports) {
					ReportExecution lastExecution = reportExecutionManager.getLastExecution(report.getId());
					
					if(lastExecution != null) {
						report.setLastExecutionDate(lastExecution.getStartTime());
					}
				}
			}
			
			response.setTotalNoOfRecords(reportManager.getRecordCount(criterionList));
			
			transaction.commit();
			
			if(reports != null && reports.size() > 0) {
				response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
				response.setMessage(MessageUtil.getMessage("success.data.loaded"));
				
				List<ReportDTO> reportDTOs = new ArrayList<ReportDTO>();
				for(Report report : reports) {
					reportDTOs.add(new ReportDTO(report));
				}
				
				response.setData(reportDTOs);
			} else {
				response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
				response.setMessage(MessageUtil.getMessage("no.data.found"));
			}
			
			response.setPageInfo(getPageInfo(retrievalRequest));
		} catch(ManagerException | Exception err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("server.error.code"));
			response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
		} finally {
			if(session != null) {
				try {
					session.close();
				} catch(HibernateException err) {
					// Ignore
				}
			}
		}
		
		return response;
	}
	
	public ServiceResponse getReport(Long reportId) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			Report report = (new ReportManager(getSessionFactory(), session).getReportById(reportId));
			
			transaction.commit();
			
			if(report != null) {
				response.setStatusCode(MessageUtil.getMessage("success.data.found.code"));
				response.setMessage(MessageUtil.getMessage("success.data.found"));
				
				ReportDTO reportDTO = new ReportDTO(report);
				response.setData(reportDTO);
				response.setTotalNoOfRecords(1);
			} else {
				response.setStatusCode(MessageUtil.getMessage("no.data.found.for.criteria.code"));
				response.setMessage(MessageUtil.getMessage("no.data.found.for.criteria"));
			}
			
			response.setPageInfo(getPageInfo(null));
		} catch(ManagerException | Exception err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("server.error.code"));
			response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
		} finally {
			if(session != null) {
				try {
					session.close();
				} catch(HibernateException err) {
					// Ignore
				}
			}
		}
		
		return response;
	}
}
