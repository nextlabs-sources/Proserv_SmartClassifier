package com.nextlabs.smartclassifier.service.v1.user;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;

import com.nextlabs.smartclassifier.database.entity.User;
import com.nextlabs.smartclassifier.database.manager.UserManager;
import com.nextlabs.smartclassifier.dto.v1.UserDTO;
import com.nextlabs.smartclassifier.dto.v1.request.RetrievalRequest;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class UserRetrievalService 
		extends Service {
	
	public UserRetrievalService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public UserRetrievalService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse getUsers(RetrievalRequest retrievalRequest) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			UserManager userManager = new UserManager(getSessionFactory(), session);
			List<Criterion> criterionList = getCriterion(retrievalRequest);
			List<User> users = userManager.getUsers(criterionList, getOrder(retrievalRequest), getPageInfo(retrievalRequest));
			
			response.setTotalNoOfRecords(userManager.getRecordCount(criterionList));
			
			transaction.commit();
			
			if(users != null && users.size() > 0) {
				response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
				response.setMessage(MessageUtil.getMessage("success.data.loaded"));
				
				List<UserDTO> userDTOs = new ArrayList<UserDTO>();
				for(User user : users) {
					userDTOs.add(new UserDTO(user));
				}
				
				response.setData(userDTOs);
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
	
	public ServiceResponse getUser(Long userId) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			User user = (new UserManager(getSessionFactory(), session)
					.getUserById(userId));
			
			transaction.commit();
			
			if(user != null) {
				response.setStatusCode(MessageUtil.getMessage("success.data.found.code"));
				response.setMessage(MessageUtil.getMessage("success.data.found"));
				
				UserDTO userDTO = new UserDTO(user);
				response.setData(userDTO);
				response.setTotalNoOfRecords(1);
			} else {
				response.setStatusCode(MessageUtil.getMessage("no.data.found.for.criteria.code"));
				response.setMessage(MessageUtil.getMessage("no.data.found.for.criteria"));
			}
			
			response.setPageInfo(getPageInfo(null));
		} catch(ManagerException err) {
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
		} catch(Exception err) {
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
