package com.nextlabs.smartclassifier.service.v1.report;

import com.nextlabs.smartclassifier.constant.EventStage;
import com.nextlabs.smartclassifier.constant.EventStatus;
import com.nextlabs.smartclassifier.constant.ExecutionType;
import com.nextlabs.smartclassifier.constant.Punctuation;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.entity.EventLog;
import com.nextlabs.smartclassifier.database.manager.EventLogManager;
import com.nextlabs.smartclassifier.dto.KeyValuePair;
import com.nextlabs.smartclassifier.dto.v1.EventLogDTO;
import com.nextlabs.smartclassifier.dto.v1.FacetDTO;
import com.nextlabs.smartclassifier.dto.v1.ReportFilterDTO;
import com.nextlabs.smartclassifier.dto.v1.ReportFilterGroupDTO;
import com.nextlabs.smartclassifier.dto.v1.request.ReportQueryRequest;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.DateUtil;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.opencsv.CSVWriter;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportQueryService 
		extends Service {
	
	private static final String HEADER_TIMESTAMP = "Timestamp";
	private static final String HEADER_REPOSITORY_TYPE = "Repository Type";
	private static final String HEADER_FILE_PATH = "Directory";
	private static final String HEADER_FILE_NAME = "File Name";
	private static final String HEADER_EVENT_STAGE = "Event Stage";
	private static final String HEADER_RULE_NAME = "Rule Name";
	private static final String HEADER_EXECUTION_TYPE = "Execution Type";
	private static final String HEADER_ACTION_NAME = "Action";
	private static final String HEADER_EVENT_STATUS = "Event Status";
	private static final String HEADER_MESSAGE = "Message";
	
	private StringBuilder recordCountSql = new StringBuilder();
	private List<KeyValuePair<String, ? extends Object>> sqlParameters = new ArrayList<KeyValuePair<String, ? extends Object>>();
	
	public ReportQueryService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public ReportQueryService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	@SuppressWarnings("unchecked")
	public ServiceResponse search(ReportQueryRequest queryRequest) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			ProjectionList documentProjectionList = Projections.projectionList();
			documentProjectionList.add(Projections.groupProperty(EventLog.REPOSITORY_TYPE), EventLog.REPOSITORY_TYPE);
			documentProjectionList.add(Projections.groupProperty(EventLog.FILE_PATH), EventLog.FILE_PATH);
			documentProjectionList.add(Projections.groupProperty(EventLog.FILE_NAME), EventLog.FILE_NAME);
			
			recordCountSql.append("SELECT COUNT(*) FROM (SELECT REPOSITORY_TYPE, FILE_PATH, FILE_NAME FROM EVENT_LOGS WHERE");
			List<Criterion> criterionList = getCriterionList(queryRequest);
			recordCountSql.append(" GROUP BY REPOSITORY_TYPE, FILE_PATH, FILE_NAME) T");
			List<Order> orderList = getOrder(queryRequest);
			Map<String, Object> data = new LinkedHashMap<String, Object>();
			
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			EventLogManager eventLogManager = new EventLogManager(getSessionFactory(), session);
			
			// Get facet information
			Map<String, List<FacetDTO>> facets = getFacet(eventLogManager, criterionList);
			
			// Get record information
			@SuppressWarnings("rawtypes")
			List eventLogs = eventLogManager.getEventLogs(documentProjectionList, criterionList, orderList, getQueryPageInfo(queryRequest), EventLog.class);
			response.setTotalNoOfRecords(eventLogManager.getRecordCount(criterionList));
			response.setTotalDocumentRecords(eventLogManager.getRecordCount(recordCountSql.toString(), sqlParameters));
			
			transaction.commit();
			
			data.put("facets", facets);
			
			if(eventLogs != null && eventLogs.size() > 0) {
				List<EventLogDTO> eventLogDTOs = new ArrayList<EventLogDTO>();
				
				for(EventLog eventLog : (List<EventLog>)eventLogs) {
					EventLogDTO eventLogDTO = new EventLogDTO(eventLog);
					RepositoryType repositoryType = RepositoryType.getRepositoryType(eventLog.getRepositoryType());
					if(repositoryType != null) {
						eventLogDTO.setRepositoryType(repositoryType.getDisplayValue());
					}
					
					eventLogDTOs.add(eventLogDTO);
				}
				
				data.put("documents", eventLogDTOs);
				
				response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
				response.setMessage(MessageUtil.getMessage("success.data.loaded"));
			} else {
				response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
				response.setMessage(MessageUtil.getMessage("no.data.found"));
			}
			
			response.setData(data);
			response.setPageInfo(getQueryPageInfo(queryRequest));
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
	
	@SuppressWarnings("unchecked")
	public ServiceResponse loadDocument(ReportQueryRequest queryRequest) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			ProjectionList documentProjectionList = Projections.projectionList();
			documentProjectionList.add(Projections.groupProperty(EventLog.REPOSITORY_TYPE), EventLog.REPOSITORY_TYPE);
			documentProjectionList.add(Projections.groupProperty(EventLog.FILE_PATH), EventLog.FILE_PATH);
			documentProjectionList.add(Projections.groupProperty(EventLog.FILE_NAME), EventLog.FILE_NAME);
			
			recordCountSql.append("SELECT COUNT(*) FROM (SELECT REPOSITORY_TYPE, FILE_PATH, FILE_NAME FROM EVENT_LOGS WHERE");
			List<Criterion> criterionList = getCriterionList(queryRequest);
			recordCountSql.append(" GROUP BY REPOSITORY_TYPE, FILE_PATH, FILE_NAME) T");
			List<Order> orderList = getOrder(queryRequest);
			
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			EventLogManager eventLogManager = new EventLogManager(getSessionFactory(), session);
			
			// Get record information
			@SuppressWarnings("rawtypes")
			List eventLogs = eventLogManager.getEventLogs(documentProjectionList, criterionList, orderList, getQueryPageInfo(queryRequest), EventLog.class);
			
			transaction.commit();
			
			if(eventLogs != null && eventLogs.size() > 0) {
				List<EventLogDTO> eventLogDTOs = new ArrayList<EventLogDTO>();
				
				for(EventLog eventLog : (List<EventLog>)eventLogs) {
					EventLogDTO eventLogDTO = new EventLogDTO(eventLog);
					RepositoryType repositoryType = RepositoryType.getRepositoryType(eventLog.getRepositoryType());
					if(repositoryType != null) {
						eventLogDTO.setRepositoryType(repositoryType.getDisplayValue());
					}
					
					eventLogDTOs.add(eventLogDTO);
				}
				
				response.setData(eventLogDTOs);
				
				response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
				response.setMessage(MessageUtil.getMessage("success.data.loaded"));
			} else {
				response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
				response.setMessage(MessageUtil.getMessage("no.data.found"));
			}
			
			response.setPageInfo(getQueryPageInfo(queryRequest));
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
	
	@SuppressWarnings("unchecked")
	public ServiceResponse loadDocumentEvent(ReportQueryRequest queryRequest) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			ProjectionList eventProjectionList = Projections.projectionList();
			eventProjectionList.add(Projections.property(EventLog.ID), EventLog.ID);
			eventProjectionList.add(Projections.property(EventLog.STAGE), EventLog.STAGE);
			eventProjectionList.add(Projections.property(EventLog.RULE_NAME), EventLog.RULE_NAME);
			eventProjectionList.add(Projections.property(EventLog.RULE_EXECUTION_TYPE), EventLog.RULE_EXECUTION_TYPE);
			eventProjectionList.add(Projections.property(EventLog.ACTION_NAME), EventLog.ACTION_NAME);
			eventProjectionList.add(Projections.property(EventLog.STATUS), EventLog.STATUS);
			eventProjectionList.add(Projections.property(EventLog.MESSAGE_CODE), EventLog.MESSAGE_CODE);
			eventProjectionList.add(Projections.property(EventLog.MESSAGE_PARAM), EventLog.MESSAGE_PARAM);
			eventProjectionList.add(Projections.property(EventLog.TIMESTAMP), EventLog.TIMESTAMP);
			
			recordCountSql.append("SELECT COUNT(*) FROM (SELECT STAGE, RULE_NAME, RULE_EXECUTION_TYPE, ACTION_NAME, STATUS, MESSAGE_CODE, MESSAGE_PARAM, TIMESTAMP FROM EVENT_LOGS WHERE");
			List<Criterion> criterionList = getCriterionList(queryRequest);
			recordCountSql.append(") T");
			List<Order> orderList = getOrder(queryRequest);
			
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			EventLogManager eventLogManager = new EventLogManager(getSessionFactory(), session);
			
			// Get record information
			@SuppressWarnings("rawtypes")
			List eventLogs = eventLogManager.getEventLogs(eventProjectionList, criterionList, orderList, getQueryPageInfo(queryRequest), EventLog.class);
			response.setTotalNoOfRecords(eventLogManager.getRecordCount(criterionList));
			
			transaction.commit();
			
			if(eventLogs != null && eventLogs.size() > 0) {
				List<EventLogDTO> eventLogDTOs = new ArrayList<EventLogDTO>();
				
				for(EventLog eventLog : (List<EventLog>)eventLogs) {
					EventLogDTO eventLogDTO = new EventLogDTO(eventLog);
					
					EventStage eventStage = EventStage.getStage(eventLogDTO.getStage());
					if(eventStage != null) {
						eventLogDTO.setStage(eventStage.getName());
					}
					
					ExecutionType executionType = ExecutionType.getType(eventLogDTO.getRuleExecutionType());
					if(executionType != null) {
						eventLogDTO.setRuleExecutionType(executionType.getName());
					}
					
					EventStatus eventStatus = EventStatus.getStatus(eventLogDTO.getStatus());
					if(eventStatus != null) {
						eventLogDTO.setStatus(eventStatus.getName());
					}
					
					if(StringUtils.isNotBlank(eventLog.getMessageCode())) {
						eventLogDTO.setMessage(parseMessage(eventLog.getMessageCode(), eventLog.getMessageParam()));
					}
					eventLogDTOs.add(eventLogDTO);
				}
				
				response.setData(eventLogDTOs);
				
				response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
				response.setMessage(MessageUtil.getMessage("success.data.loaded"));
			} else {
				response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
				response.setMessage(MessageUtil.getMessage("no.data.found"));
			}
			
			response.setPageInfo(getQueryPageInfo(queryRequest));
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
	
	@SuppressWarnings("unchecked")
	public void export(ReportQueryRequest queryRequest) {
		Session session = null;
		Transaction transaction = null;
		
		try {
			ProjectionList eventProjectionList = Projections.projectionList();
			eventProjectionList.add(Projections.property(EventLog.ID), EventLog.ID);
			eventProjectionList.add(Projections.property(EventLog.REPOSITORY_TYPE), EventLog.REPOSITORY_TYPE);
			eventProjectionList.add(Projections.property(EventLog.FILE_PATH), EventLog.FILE_PATH);
			eventProjectionList.add(Projections.property(EventLog.FILE_NAME), EventLog.FILE_NAME);
			eventProjectionList.add(Projections.property(EventLog.STAGE), EventLog.STAGE);
			eventProjectionList.add(Projections.property(EventLog.RULE_NAME), EventLog.RULE_NAME);
			eventProjectionList.add(Projections.property(EventLog.RULE_EXECUTION_TYPE), EventLog.RULE_EXECUTION_TYPE);
			eventProjectionList.add(Projections.property(EventLog.ACTION_NAME), EventLog.ACTION_NAME);
			eventProjectionList.add(Projections.property(EventLog.STATUS), EventLog.STATUS);
			eventProjectionList.add(Projections.property(EventLog.MESSAGE_CODE), EventLog.MESSAGE_CODE);
			eventProjectionList.add(Projections.property(EventLog.MESSAGE_PARAM), EventLog.MESSAGE_PARAM);
			eventProjectionList.add(Projections.property(EventLog.TIMESTAMP), EventLog.TIMESTAMP);
			
			List<Criterion> criterionList = getCriterionList(queryRequest);
			List<Order> orderList = getOrder(queryRequest);
			
			if("csv".equals(queryRequest.getFormat().toLowerCase())) {
				CSVWriter csvWriter = null;
				
				try {
					csvWriter = getCSVWriter();
					if(csvWriter != null) {
						session = getSessionFactory().openSession();
						transaction = session.beginTransaction();
						
						EventLogManager eventLogManager = new EventLogManager(getSessionFactory(), session);
						long recordCount = eventLogManager.getRecordCount(criterionList);
						
						if(recordCount > 0) {
							long exportedCount = 0L;
							PageInfo pageInfo = new PageInfo();
							pageInfo.setSize(250);
							
							for(int page = 0; exportedCount < recordCount; page++) {
								pageInfo.setSkip(page * 250);
								@SuppressWarnings("rawtypes")
								List eventLogs = eventLogManager.getEventLogs(eventProjectionList, criterionList, orderList, pageInfo, EventLog.class);
								writeReport(csvWriter, eventLogs);
								exportedCount += eventLogs.size();
							}
						}
						
						transaction.commit();
						csvWriter.flush();
					}
				} catch(Exception err) {
					logger.error(err.getMessage(), err);
				} finally {
					if(csvWriter != null) {
						try {
							csvWriter.close();
						} catch(Exception err) {
							// Ignore
						}
					}
				}
			} else if("xlsx".equals(queryRequest.getFormat().toLowerCase())) {
				XSSFWorkbook workbook = null;
				OutputStream outputStream = null;
				
				try {
					workbook = getXSSFWorkbook();
					
					if(workbook != null) {
						session = getSessionFactory().openSession();
						transaction = session.beginTransaction();
							
						EventLogManager eventLogManager = new EventLogManager(getSessionFactory(), session);
						long recordCount = eventLogManager.getRecordCount(criterionList);

						if(recordCount > 0) {
							long exportedCount = 0L;
							PageInfo pageInfo = new PageInfo();
							pageInfo.setSize(250);
							
							for(int page = 0; exportedCount < recordCount; page++) {
								pageInfo.setSkip(page * 250);
								@SuppressWarnings("rawtypes")
								List eventLogs = eventLogManager.getEventLogs(eventProjectionList, criterionList, orderList, pageInfo, EventLog.class);
								writeReport(workbook, eventLogs);
								exportedCount += eventLogs.size();
							}
						}
						
						transaction.commit();
						
						HttpServletResponse httpServletResponse = (HttpServletResponse)servletResponse;
						httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
						httpServletResponse.addHeader("Content-Disposition", "attachment;filename=Report.xlsx");
						
						outputStream = httpServletResponse.getOutputStream();
						workbook.write(outputStream);
						outputStream.flush();
					}
				} catch(Exception err) {
					logger.error(err.getMessage(), err);
				} finally {
					if(outputStream != null) {
						try {
							outputStream.close();
						} catch(Exception err) {
							// Ignore
						}
					}
					
					if(workbook != null) {
						try {
							workbook.close();
						} catch(Exception err) {
							// Ignore
						}
					}
				}
			}
		} catch(ManagerException | Exception err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			logger.error(err.getMessage(), err);
		} finally {
			if(session != null) {
				try {
					session.close();
				} catch(HibernateException err) {
					// Ignore
				}
			}
		}
	}
	
	private List<Criterion> getCriterionList(ReportQueryRequest request) {
		List<Criterion> criterionList = new ArrayList<Criterion>();
		
		// Limit to certain document path or name
		if(StringUtils.isNotBlank(request.getFilePath()) || StringUtils.isNotBlank(request.getFileName())) {
			if(StringUtils.isNotBlank(request.getFilePath())) {
				criterionList.add(Restrictions.eq(EventLog.FILE_PATH, request.getFilePath().replace(Punctuation.BACK_SLASH, Punctuation.FORWARD_SLASH)));
				recordCountSql.append(" FILE_PATH = ?");
				sqlParameters.add(new KeyValuePair<String, String>("String", request.getFilePath().replace(Punctuation.BACK_SLASH, Punctuation.FORWARD_SLASH)));
			}
			
			if(StringUtils.isNotBlank(request.getFileName())) {
				criterionList.add(Restrictions.eq(EventLog.FILE_NAME, request.getFileName()));
				recordCountSql.append(" AND FILE_NAME = ?");
				sqlParameters.add(new KeyValuePair<String, String>("String", request.getFileName()));
			}
		} else {
			// Limit to document related events only
			criterionList.add(Restrictions.neOrIsNotNull(EventLog.FILE_NAME, StringUtils.EMPTY));
			recordCountSql.append(" FILE_NAME <> ?");
			sqlParameters.add(new KeyValuePair<String, String>("String", StringUtils.EMPTY));
		}
		
		// Time range
		if(request.getRange() != null
				&& !"Any".equalsIgnoreCase(request.getRange().getOption())) {
			if("Predefined".equalsIgnoreCase(request.getRange().getOption())) {
				long startDate;
				if("DAY".equalsIgnoreCase(request.getRange().getUnit())) {
					startDate = DateUtil.toStartOfTheDay(DateUtil.subtractDays(new Date(), request.getRange().getValue()));
					criterionList.add(Restrictions.ge(EventLog.TIMESTAMP, startDate));
				} else if("MONTH".equalsIgnoreCase(request.getRange().getUnit())) { 
					startDate = DateUtil.toStartOfTheDay(DateUtil.subtractMonths(new Date(), request.getRange().getValue()));
					criterionList.add(Restrictions.ge(EventLog.TIMESTAMP, startDate));
				} else {
					startDate = DateUtil.toStartOfTheDay(DateUtil.subtractYears(new Date(), request.getRange().getValue()));
					criterionList.add(Restrictions.ge(EventLog.TIMESTAMP, startDate));
				}
				recordCountSql.append(" AND TIMESTAMP >= ?");
				sqlParameters.add(new KeyValuePair<String, Long>("Long", startDate));
			} else {	// Option is custom
				criterionList.add(Restrictions.ge(EventLog.TIMESTAMP, request.getRange().getFrom()));
				criterionList.add(Restrictions.le(EventLog.TIMESTAMP, request.getRange().getTo()));
				recordCountSql.append(" AND TIMESTAMP >= ? AND TIMESTAMP <= ?");
				sqlParameters.add(new KeyValuePair<String, Long>("Long", request.getRange().getFrom()));
				sqlParameters.add(new KeyValuePair<String, Long>("Long", request.getRange().getTo()));
			}
		}
		
		// Event status
		if(!EventStatus.ALL.getCode().equalsIgnoreCase(request.getEventStatus())) {
			criterionList.add(Restrictions.eq(EventLog.STATUS, request.getEventStatus()));
			recordCountSql.append(" AND STATUS = ?");
			sqlParameters.add(new KeyValuePair<String, String>("String", request.getEventStatus()));
		}
		
		// Grouped criteria
		Criterion reportFilter = getFilterCriterion(request);
		if(reportFilter != null) {
			criterionList.add(reportFilter);
		}
		
		return criterionList;
	}
	
	private Criterion getFilterCriterion(ReportQueryRequest request) {
		LogicalExpression andExpression = null;
		LogicalExpression firstGroupedExpression = null;
		Criterion firstGroupedCriterion = null;
		
		if(request != null) {
			if(request.getFilterGroups() != null) {
				for(ReportFilterGroupDTO reportFilterGroupDTO : request.getFilterGroups()) {
					recordCountSql.append(" AND (");
					LogicalExpression orExpression = null;
					Criterion firstCriterion = null;
					
					if(reportFilterGroupDTO.getFilters() != null && reportFilterGroupDTO.getFilters().size() > 0) {
						boolean firstItem = true;
						for(ReportFilterDTO reportFilterDTO : reportFilterGroupDTO.getFilters()) {
							if(!firstItem) {
								recordCountSql.append(" OR ");
							}
							
							String entityColumnName = getEntityColumnName(reportFilterDTO.getFieldName());
							if(EventLog.FILE_PATH.equals(entityColumnName)) {
								reportFilterDTO.setValue(reportFilterDTO.getValue().replace(Punctuation.BACK_SLASH, Punctuation.FORWARD_SLASH));
							}
							
							if("=".equalsIgnoreCase(reportFilterDTO.getOperator())) {
								if(firstCriterion == null) {
									firstCriterion = Restrictions.eq(entityColumnName, reportFilterDTO.getValue());
								} else if(orExpression == null) {
									orExpression = Restrictions.or(firstCriterion, 
											Restrictions.eq(entityColumnName, reportFilterDTO.getValue()));
								} else {
									orExpression = Restrictions.or(orExpression, 
											Restrictions.eq(entityColumnName, reportFilterDTO.getValue()));
								}
								recordCountSql.append(getActualColumnName(reportFilterDTO.getFieldName())).append(" = ?");
								sqlParameters.add(new KeyValuePair<String, String>("String", reportFilterDTO.getValue()));
							} else if("!=".equalsIgnoreCase(reportFilterDTO.getOperator())) {
								if(firstCriterion == null) {
									firstCriterion = Restrictions.ne(entityColumnName, reportFilterDTO.getValue());
								} else if(orExpression == null) {
									orExpression = Restrictions.or(firstCriterion, 
											Restrictions.ne(entityColumnName, reportFilterDTO.getValue()));
								} else {
									orExpression = Restrictions.or(orExpression, 
											Restrictions.ne(entityColumnName, reportFilterDTO.getValue()));
								}
								recordCountSql.append(getActualColumnName(reportFilterDTO.getFieldName())).append(" != ?");
								sqlParameters.add(new KeyValuePair<String, String>("String", reportFilterDTO.getValue()));
							} else if("like".equalsIgnoreCase(reportFilterDTO.getOperator())) {
								String wildcardValue = replaceWildcard(reportFilterDTO.getValue());
								if(firstCriterion == null) {
									firstCriterion = Restrictions.like(entityColumnName, wildcardValue);
								} else if(orExpression == null) {
									orExpression = Restrictions.or(firstCriterion, 
											Restrictions.like(entityColumnName, wildcardValue));
								} else {
									orExpression = Restrictions.or(orExpression, 
											Restrictions.like(entityColumnName, wildcardValue));
								}
								recordCountSql.append(getActualColumnName(reportFilterDTO.getFieldName())).append(" LIKE ?");
								sqlParameters.add(new KeyValuePair<String, String>("String", wildcardValue));
							} else if("not like".equalsIgnoreCase(reportFilterDTO.getOperator())) {
								String wildcardValue = replaceWildcard(reportFilterDTO.getValue());
								if(firstCriterion == null) {
									firstCriterion = Restrictions.not(Restrictions.like(entityColumnName, wildcardValue));
								} else if(orExpression == null) {
									orExpression = Restrictions.or(firstCriterion, 
											Restrictions.not(Restrictions.like(entityColumnName, wildcardValue)));
								} else {
									orExpression = Restrictions.or(orExpression, 
											Restrictions.not(Restrictions.like(entityColumnName, wildcardValue)));
								}
								recordCountSql.append(getActualColumnName(reportFilterDTO.getFieldName())).append(" NOT LIKE ?");
								sqlParameters.add(new KeyValuePair<String, String>("String", wildcardValue));
							}
							
							firstItem = false;
						}
					}
					
					if(firstGroupedExpression == null && firstGroupedCriterion == null) {
						if(orExpression != null) {
							firstGroupedExpression = orExpression;
						} else if(firstCriterion != null) {
							firstGroupedCriterion = firstCriterion;
						}
					} else {
						if (andExpression == null) {
							if(orExpression != null) {
								if(firstGroupedExpression != null) {
									andExpression = Restrictions.and(firstGroupedExpression, orExpression);
								} else if(firstGroupedCriterion != null) {
									andExpression = Restrictions.and(firstGroupedCriterion, orExpression);
								}
							} else if(firstCriterion != null) {
								if(firstGroupedExpression != null) {
									andExpression = Restrictions.and(firstGroupedExpression, firstCriterion);
								} else if(firstGroupedCriterion != null) {
									andExpression = Restrictions.and(firstGroupedCriterion, firstCriterion);
								}
							}
						} else {
							if(orExpression != null) {
								andExpression = Restrictions.and(andExpression, orExpression);
							} else if(firstCriterion != null) {
								andExpression = Restrictions.and(andExpression, firstCriterion);
							}
						}
					}
					
					recordCountSql.append(")");
				}
			}
			
			if(request.getRefineGroups() != null) {
				for(ReportFilterGroupDTO reportFilterGroupDTO : request.getRefineGroups()) {
					recordCountSql.append(" AND (");
					LogicalExpression orExpression = null;
					Criterion firstCriterion = null;
					
					if(reportFilterGroupDTO.getFilters() != null && reportFilterGroupDTO.getFilters().size() > 0) {
						boolean firstItem = true;
						for(ReportFilterDTO reportFilterDTO : reportFilterGroupDTO.getFilters()) {
							if(!firstItem) {
								recordCountSql.append(" OR ");
							}
							
							String entityColumnName = getEntityColumnName(reportFilterDTO.getFieldName());
							if(EventLog.FILE_PATH.equals(entityColumnName)) {
								reportFilterDTO.setValue(reportFilterDTO.getValue().replace(Punctuation.BACK_SLASH, Punctuation.FORWARD_SLASH));
							}
							
							if(firstCriterion == null) {
								firstCriterion = Restrictions.eq(entityColumnName, reportFilterDTO.getValue());
							} else if(orExpression == null) {
								orExpression = Restrictions.or(firstCriterion, 
										Restrictions.eq(entityColumnName, reportFilterDTO.getValue()));
							} else {
								orExpression = Restrictions.or(orExpression, 
										Restrictions.eq(entityColumnName, reportFilterDTO.getValue()));
							}
							
							recordCountSql.append(getActualColumnName(reportFilterDTO.getFieldName())).append(" = ?");
							sqlParameters.add(new KeyValuePair<String, String>("String", reportFilterDTO.getValue()));
							firstItem = false;
						}
					}
					
					if(firstGroupedExpression == null && firstGroupedCriterion == null) {
						if(orExpression != null) {
							firstGroupedExpression = orExpression;
						} else if(firstCriterion != null) {
							firstGroupedCriterion = firstCriterion;
						}
					} else {
						if (andExpression == null) {
							if(orExpression != null) {
								if(firstGroupedExpression != null) {
									andExpression = Restrictions.and(firstGroupedExpression, orExpression);
								} else if(firstGroupedCriterion != null) {
									andExpression = Restrictions.and(firstGroupedCriterion, orExpression);
								}
							} else if(firstCriterion != null) {
								if(firstGroupedExpression != null) {
									andExpression = Restrictions.and(firstGroupedExpression, firstCriterion);
								} else if(firstGroupedCriterion != null) {
									andExpression = Restrictions.and(firstGroupedCriterion, firstCriterion);
								}
							}
						} else {
							if(orExpression != null) {
								andExpression = Restrictions.and(andExpression, orExpression);
							} else if(firstCriterion != null) {
								andExpression = Restrictions.and(andExpression, firstCriterion);
							}
						}
					}
					
					recordCountSql.append(")");
				}
			}
		}
		
		if(andExpression != null) {
			return andExpression;
		}
		
		if(firstGroupedExpression != null) {
			return firstGroupedExpression;
		}
		
		if(firstGroupedCriterion != null) {
			return firstGroupedCriterion;
		}
		
		return null;
	}
	
	private String getActualColumnName(String fieldDisplayName) {
		if("Directory".equalsIgnoreCase(fieldDisplayName)) {
			return "FILE_PATH";
		} else if("File Name".equalsIgnoreCase(fieldDisplayName)) {
			return "FILE_NAME";
		} else if("Rule Name".equalsIgnoreCase(fieldDisplayName)) {
			return "RULE_NAME";
		} else if("Action".equalsIgnoreCase(fieldDisplayName)) {
			return "ACTION_NAME";
		} else if("Repository Type".equalsIgnoreCase(fieldDisplayName)) {
			return "REPOSITORY_TYPE";
		} else if("Event Stage".equalsIgnoreCase(fieldDisplayName)) {
			return "STAGE";
		} else if("Event Status".equalsIgnoreCase(fieldDisplayName)) {
			return "STATUS";
		}
		
		return null; 
	}
	
	private String getEntityColumnName(String fieldDisplayName) {
		if("Directory".equalsIgnoreCase(fieldDisplayName)) {
			return EventLog.FILE_PATH;
		} else if("File Name".equalsIgnoreCase(fieldDisplayName)) {
			return EventLog.FILE_NAME;
		} else if("Rule Name".equalsIgnoreCase(fieldDisplayName)) {
			return EventLog.RULE_NAME;
		} else if("Action".equalsIgnoreCase(fieldDisplayName)) {
			return EventLog.ACTION_NAME;
		} else if("Repository Type".equalsIgnoreCase(fieldDisplayName)) {
			return EventLog.REPOSITORY_TYPE;
		} else if("Event Stage".equalsIgnoreCase(fieldDisplayName)) {
			return EventLog.STAGE;
		} else if("Event Status".equalsIgnoreCase(fieldDisplayName)) {
			return EventLog.STATUS;
		}
		
		return null; 
	}
	
	private String replaceWildcard(String fieldValue) {
		if(fieldValue.indexOf(Punctuation.ASTERISK) == -1) {
			return Punctuation.PERCENTAGE + fieldValue + Punctuation.PERCENTAGE;
		}
		
		return fieldValue.replace(Punctuation.ASTERISK, Punctuation.PERCENTAGE);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, List<FacetDTO>> getFacet(EventLogManager eventLogManager, List<Criterion> criterionList) 
			throws ManagerException, Exception {
		Map<String, List<FacetDTO>> facets = new LinkedHashMap<String, List<FacetDTO>>();
		List<Order> orderList = new ArrayList<Order>();
		ProjectionList projectionList;
		
		// Get Repository Type facet
		projectionList = Projections.projectionList();
		projectionList.add(Projections.groupProperty(EventLog.REPOSITORY_TYPE), FacetDTO.VALUE);
		projectionList.add(Projections.rowCount(), FacetDTO.COUNT);
		orderList.add(Order.asc(EventLog.REPOSITORY_TYPE));
		
		@SuppressWarnings("rawtypes")
		List repositoryTypeFacetDTOs = eventLogManager.getFacet(projectionList, criterionList, orderList, FacetDTO.class);
		
		if(repositoryTypeFacetDTOs != null) {
			List<FacetDTO> filteredDTOs = (List<FacetDTO>)repositoryTypeFacetDTOs;
			Iterator<FacetDTO> filterIterator = filteredDTOs.iterator();
			
			while(filterIterator.hasNext()) {
				FacetDTO facetDTO = filterIterator.next();
				if(StringUtils.isBlank(facetDTO.getValue())) {
					filterIterator.remove();
				} else {
					RepositoryType repositoryType = RepositoryType.getRepositoryType(facetDTO.getValue());
					
					if(repositoryType != null) {
						facetDTO.setDisplayName(repositoryType.getDisplayValue());
					}
				}
			}
			
			facets.put("repositoryType", filteredDTOs);
		}
		
		// Event Stage
		projectionList = Projections.projectionList();
		projectionList.add(Projections.groupProperty(EventLog.STAGE), FacetDTO.VALUE);
		projectionList.add(Projections.rowCount(), FacetDTO.COUNT);
		orderList.clear();
		orderList.add(Order.asc(EventLog.STAGE));
		
		@SuppressWarnings("rawtypes")
		List eventStageFacetDTOs = eventLogManager.getFacet(projectionList, criterionList, orderList, FacetDTO.class);
		
		if(eventStageFacetDTOs != null) {
			List<FacetDTO> filteredDTOs = (List<FacetDTO>)eventStageFacetDTOs;
			Iterator<FacetDTO> filterIterator = filteredDTOs.iterator();
			
			while(filterIterator.hasNext()) {
				FacetDTO facetDTO = filterIterator.next();
				if(StringUtils.isBlank(facetDTO.getValue())) {
					filterIterator.remove();
				} else {
					EventStage eventStage = EventStage.getStage(facetDTO.getValue());
					
					if(eventStage != null) {
						facetDTO.setDisplayName(eventStage.getName());
					}
				}
			}
			
			facets.put("eventStage", filteredDTOs);
		}
		
		// Get Rule facet
		projectionList = Projections.projectionList();
		projectionList.add(Projections.groupProperty(EventLog.RULE_NAME), FacetDTO.VALUE);
		projectionList.add(Projections.rowCount(), FacetDTO.COUNT);
		orderList.clear();
		orderList.add(Order.asc(EventLog.RULE_NAME));
		
		@SuppressWarnings("rawtypes")
		List ruleFacetDTOs = eventLogManager.getFacet(projectionList, criterionList, orderList, FacetDTO.class);
		
		if(ruleFacetDTOs != null) {
			List<FacetDTO> filteredDTOs = (List<FacetDTO>)ruleFacetDTOs;
			Iterator<FacetDTO> filterIterator = filteredDTOs.iterator();
			
			while(filterIterator.hasNext()) {
				FacetDTO facetDTO = filterIterator.next();
				if(StringUtils.isBlank(facetDTO.getValue())) {
					filterIterator.remove();
				} else {
					facetDTO.setDisplayName(facetDTO.getValue());
				}
			}
			
			facets.put("ruleName", filteredDTOs);
		}
		
		// Get Action facet
		projectionList = Projections.projectionList();
		projectionList.add(Projections.groupProperty(EventLog.ACTION_NAME), FacetDTO.VALUE);
		projectionList.add(Projections.rowCount(), FacetDTO.COUNT);
		orderList.clear();
		orderList.add(Order.asc(EventLog.ACTION_NAME));
		
		@SuppressWarnings("rawtypes")
		List actionFacetDTOs = eventLogManager.getFacet(projectionList, criterionList, orderList, FacetDTO.class);
		
		if(actionFacetDTOs != null) {
			List<FacetDTO> filteredDTOs = (List<FacetDTO>)actionFacetDTOs;
			Iterator<FacetDTO> filterIterator = filteredDTOs.iterator();
			
			while(filterIterator.hasNext()) {
				FacetDTO facetDTO = filterIterator.next();
				if(StringUtils.isBlank(facetDTO.getValue())) {
					filterIterator.remove();
				} else {
					facetDTO.setDisplayName(facetDTO.getValue());
				}
			}
			
			facets.put("actionName", filteredDTOs);
		}
		
		// Get Status facet
		projectionList = Projections.projectionList();
		projectionList.add(Projections.groupProperty(EventLog.STATUS), FacetDTO.VALUE);
		projectionList.add(Projections.rowCount(), FacetDTO.COUNT);
		orderList.clear();
		orderList.add(Order.asc(EventLog.STATUS));
		
		@SuppressWarnings("rawtypes")
		List statusFacetDTOs = eventLogManager.getFacet(projectionList, criterionList, orderList, FacetDTO.class);
		
		if(statusFacetDTOs != null) {
			List<FacetDTO> filteredDTOs = (List<FacetDTO>)statusFacetDTOs;
			Iterator<FacetDTO> filterIterator = filteredDTOs.iterator();
			
			while(filterIterator.hasNext()) {
				FacetDTO facetDTO = filterIterator.next();
				if(StringUtils.isBlank(facetDTO.getValue())) {
					filterIterator.remove();
				} else {
					EventStatus eventStatus = EventStatus.getStatus(facetDTO.getValue());
					
					if(eventStatus != null) {
						facetDTO.setDisplayName(eventStatus.getName());
					}
				}
			}
			
			facets.put("eventStatus", filteredDTOs);
		}
		
		return facets;
	}
	
	private String[] jsonToStringArray(String jsonString) {
		if(StringUtils.isNotBlank(jsonString)) {
			return gson.fromJson(jsonString, String[].class); 
		}
		
		return null;
	}
	
	private String parseMessage(String messageCode, String messageParam) {
		if(messageParam == null)
			return MessageUtil.getMessage(messageCode);
		
		return MessageUtil.getMessage(messageCode, (Object[])jsonToStringArray(messageParam));
	}
	
	private XSSFWorkbook getXSSFWorkbook()
			throws Exception {
		XSSFWorkbook reportWorkbook = new XSSFWorkbook();
		Sheet sheet = reportWorkbook.createSheet("Results");
		
		// Create header
		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue(HEADER_TIMESTAMP);
		headerRow.createCell(1).setCellValue(HEADER_REPOSITORY_TYPE);
		headerRow.createCell(2).setCellValue(HEADER_FILE_PATH);
		headerRow.createCell(3).setCellValue(HEADER_FILE_NAME);
		headerRow.createCell(4).setCellValue(HEADER_EVENT_STAGE);
		headerRow.createCell(5).setCellValue(HEADER_RULE_NAME);
		headerRow.createCell(6).setCellValue(HEADER_EXECUTION_TYPE);
		headerRow.createCell(7).setCellValue(HEADER_ACTION_NAME);
		headerRow.createCell(8).setCellValue(HEADER_EVENT_STATUS);
		headerRow.createCell(9).setCellValue(HEADER_MESSAGE);
		
		// Make header bold
		CellStyle headerCellStyle = reportWorkbook.createCellStyle();
		Font boldFont = reportWorkbook.createFont();
		boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerCellStyle.setFont(boldFont);
		
		for(int i = 0; i < headerRow.getLastCellNum(); i++) {
			headerRow.getCell(i).setCellStyle(headerCellStyle);
		}
		
		return reportWorkbook;
	}
	
	private void writeReport(XSSFWorkbook reportWorkbook, List<EventLog> eventLogs) {
		try {
			if(eventLogs != null) {
				SimpleDateFormat dateFormatter = new SimpleDateFormat(DateUtil.DF_DDMMMYYHHMMSS);
				Sheet sheet = reportWorkbook.getSheet("Results");
				int rowCounter = sheet.getLastRowNum() + 1;
				
				for(EventLog eventLog : eventLogs) {
					Row record = sheet.createRow(rowCounter);
					
					record.createCell(0).setCellValue(dateFormatter.format(new Date(eventLog.getTimestamp())));
					RepositoryType repositoryType = RepositoryType.getRepositoryType(eventLog.getRepositoryType());
					record.createCell(1).setCellValue(repositoryType == null ? "" : repositoryType.getDisplayValue());
					record.createCell(2).setCellValue(eventLog.getFilePath());
					record.createCell(3).setCellValue(eventLog.getFileName());
					EventStage eventStage = EventStage.getStage(eventLog.getStage());
					record.createCell(4).setCellValue(eventStage == null ? "" : eventStage.getName());
					record.createCell(5).setCellValue(maskNull(eventLog.getRuleName()));
					ExecutionType executionType = ExecutionType.getType(eventLog.getRuleExecutionType());
					record.createCell(6).setCellValue(executionType == null ? "" : executionType.getName());
					record.createCell(7).setCellValue(maskNull(eventLog.getActionName()));
					EventStatus eventStatus = EventStatus.getStatus(eventLog.getStatus());
					record.createCell(8).setCellValue(eventStatus == null ? "" : eventStatus.getName());
					if(StringUtils.isNotBlank(eventLog.getMessageCode())) {
						record.createCell(9).setCellValue(parseMessage(eventLog.getMessageCode(), eventLog.getMessageParam()));
					}
					
					rowCounter++;
				}
			}
		} catch(Exception err) {
			logger.error(err);
		}
	}
	
	private CSVWriter getCSVWriter() 
			throws Exception {
		HttpServletResponse httpServletResponse = (HttpServletResponse)servletResponse;
		httpServletResponse.setContentType("text/csv");
		httpServletResponse.addHeader("Content-Disposition", "attachment;filename=Report.csv");
		
		CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new OutputStreamWriter(httpServletResponse.getOutputStream())), 
								  Punctuation.COMMA.charAt(0), 
								  Punctuation.DOUBLE_QUOTE.charAt(0));
		String[] rowArray = new String[10];
		// Create header
		rowArray[0] = HEADER_TIMESTAMP;
		rowArray[1] = HEADER_REPOSITORY_TYPE;
		rowArray[2] = HEADER_FILE_PATH;
		rowArray[3] = HEADER_FILE_NAME;
		rowArray[4] = HEADER_EVENT_STAGE;
		rowArray[5] = HEADER_RULE_NAME;
		rowArray[6] = HEADER_EXECUTION_TYPE;
		rowArray[7] = HEADER_ACTION_NAME;
		rowArray[8] = HEADER_EVENT_STATUS;
		rowArray[9] = HEADER_MESSAGE;
		
		csvWriter.writeNext(rowArray, true);
		
		return csvWriter;
	}
	
	private void writeReport(CSVWriter csvWriter, List<EventLog> eventLogs) {
		try {
			String[] rowArray = new String[10];
			
			if(eventLogs != null) {
				SimpleDateFormat dateFormatter = new SimpleDateFormat(DateUtil.DF_DDMMMYYHHMMSS);
				
				for(EventLog eventLog : eventLogs) {
					rowArray[0] = dateFormatter.format(new Date(eventLog.getTimestamp()));
					RepositoryType repositoryType = RepositoryType.getRepositoryType(eventLog.getRepositoryType());
					rowArray[1] = repositoryType == null ? "" : repositoryType.getDisplayValue();
					rowArray[2] = eventLog.getFilePath();
					rowArray[3] = eventLog.getFileName();
					EventStage eventStage = EventStage.getStage(eventLog.getStage());
					rowArray[4] = eventStage == null ? "" : eventStage.getName();
					rowArray[5] = maskNull(eventLog.getRuleName());
					ExecutionType executionType = ExecutionType.getType(eventLog.getRuleExecutionType());
					rowArray[6] = executionType == null ? "" : executionType.getName();
					rowArray[7] = maskNull(eventLog.getActionName());
					EventStatus eventStatus = EventStatus.getStatus(eventLog.getStatus());
					rowArray[8] = eventStatus == null ? "" : eventStatus.getName();
					if(StringUtils.isNotBlank(eventLog.getMessageCode())) {
						rowArray[9] = parseMessage(eventLog.getMessageCode(), eventLog.getMessageParam());
					} else {
						rowArray[9] = StringUtils.EMPTY;
					}
					
					csvWriter.writeNext(rowArray, true);
				}
			}
		} catch(Exception err) {
			logger.error(err);
		}
	}
	
	private String maskNull(String value) {
		if(value == null)
			return StringUtils.EMPTY;
		
		return value;
	}
}
