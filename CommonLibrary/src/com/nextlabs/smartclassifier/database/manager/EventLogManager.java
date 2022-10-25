package com.nextlabs.smartclassifier.database.manager;

import java.sql.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;

import com.nextlabs.smartclassifier.database.dao.EventLogDAO;
import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.entity.EventLog;
import com.nextlabs.smartclassifier.dto.Event;
import com.nextlabs.smartclassifier.dto.KeyValuePair;
import com.nextlabs.smartclassifier.exception.ManagerException;

public class EventLogManager 
		extends Manager {
	
	private static final String INSERT = "INSERT INTO EVENT_LOGS (COMPONENT_NAME, STAGE, FILE_ID, REPOSITORY_TYPE, FILE_PATH, FILE_NAME, RULE_ID, RULE_NAME, "
			+ "RULE_EXECUTION_ID, RULE_EXECUTION_TYPE, ACTION_PLUGIN_ID, ACTION_NAME, ACTION_ID, CATEGORY, STATUS, MESSAGE_CODE, MESSAGE_PARAM, TIMESTAMP) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String PURGE = "DELETE FROM EVENT_LOGS WHERE TIMESTAMP < ?";
	
	private EventLogDAO eventLogDAO;
	
	public EventLogManager(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
		this.eventLogDAO = new EventLogDAO(sessionFactory, session);
	}
	
	public void logEvents(List<Event> events)
			throws ManagerException {
		try {
			if(events != null) {
				for(Event event : events) {
					SQLQuery sql = session.createSQLQuery(INSERT);
					sql.setString(0, event.getComponentName());
					sql.setString(1, event.getStage().getCode());
					sql.setString(2, event.getFileId());
					sql.setString(3, event.getRepositoryType());
					sql.setString(4, event.getFilePath());
					sql.setString(5, event.getFileName());
					if(event.getRuleId() == null || event.getRuleId() == 0L) {
						sql.setParameter(6, null);
					} else {
						sql.setLong(6, event.getRuleId());
					}
					sql.setString(7, event.getRuleName());
					if(event.getRuleExecutionId() == null || event.getRuleExecutionId() == 0L) {
						sql.setParameter(8, null);
					} else {
						sql.setLong(8, event.getRuleExecutionId());
					}
					sql.setString(9, event.getRuleExecutionType());
					if(event.getActionPluginId() == null || event.getActionPluginId() == 0L) {
						sql.setParameter(10, null);
					} else {
						sql.setLong(10, event.getActionPluginId());
					}
					sql.setString(11, event.getActionName());
					if(event.getActionId() == null || event.getActionId() == 0L) {
						sql.setParameter(12, null);
					} else {
						sql.setLong(12, event.getActionId());
					}
					sql.setString(13, event.getCategory().getCode());
					sql.setString(14, event.getStatus().getCode());
					sql.setString(15, event.getMessageCode());
					sql.setString(16, event.getMessageParam());
					sql.setLong(17, event.getTimestamp());
					
					sql.executeUpdate();
				}
			}
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List getFacet(ProjectionList projectionList, List<Criterion> criterionList, List<Order> orderList, Class transformClass)
			throws ManagerException {
		try {
			return eventLogDAO.findByCriteria(projectionList, criterionList, orderList, transformClass);
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List getEventLogs(ProjectionList projectionList, List<Criterion> criterionList, List<Order> orderList, PageInfo pageInfo, Class transformClass)
			throws ManagerException {
		try {
			return eventLogDAO.findByCriteria(projectionList, criterionList, orderList, pageInfo, transformClass);
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public List<EventLog> getEventLogs(List<Criterion> criterionList, List<Order> orderList, PageInfo pageInfo)
			throws ManagerException {
		try {
			return eventLogDAO.findByCriteria(criterionList, orderList, pageInfo);
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public long getRecordCount(String sql, List<KeyValuePair<String, ? extends Object>> parameters)
			throws ManagerException {
		try {
			SQLQuery recordCount = session.createSQLQuery(sql);
			
			if(parameters != null) {
				int index = 0;
				for(KeyValuePair<String, ? extends Object> parameter : parameters) {
					if("String".equals(parameter.getKey())) {
						recordCount.setString(index, parameter.getValue().toString());
					} else if("Long".equals(parameter.getKey())) {
						recordCount.setLong(index, (Long)parameter.getValue());
					} else if("Boolean".equals(parameter.getKey())) {
						recordCount.setBoolean(index, Boolean.valueOf(parameter.getValue().toString()));
					}
					index++;
				}
			}
			
			return (Integer) recordCount.uniqueResult();
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public long getRecordCount(List<Criterion> criterionList) 
			throws ManagerException {
		try {
			return eventLogDAO.getCount(criterionList);
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public void purgeEventLogs(Date beforeDate)
			throws ManagerException {
		if(beforeDate != null) {
			purgeEventLogs(beforeDate.getTime());
		}
	}
	
	public void purgeEventLogs(Long beforeDate)
			throws ManagerException {
		if(beforeDate != null && beforeDate > 0L) {
			try {
				SQLQuery sql = session.createSQLQuery(PURGE);
				sql.setLong(0, beforeDate);
				
				sql.executeUpdate();
			} catch(HibernateException err) {
				throw new ManagerException(err.getMessage(), err);
			} catch(Exception err) {
				throw new ManagerException(err);
			}
		}
	}
}
