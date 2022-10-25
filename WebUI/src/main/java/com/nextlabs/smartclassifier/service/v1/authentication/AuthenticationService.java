package com.nextlabs.smartclassifier.service.v1.authentication;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.database.manager.UserManager;
import com.nextlabs.smartclassifier.dto.v1.UserDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.dto.v1.response.UpdateResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class AuthenticationService 
		extends Service {
	
	public AuthenticationService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public AuthenticationService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse details() {
		RetrievalResponse response = new RetrievalResponse(); 
		
		try {
			HttpServletRequest httpServletRequest = ((HttpServletRequest)servletRequest);
			
			// User is not logged in
			if(httpServletRequest.getSession().getAttribute("UserInfo") == null) {
				logger.debug("User is not logged in, redirect user to logout page.");
				
				return logout();
			} else {
				@SuppressWarnings("unchecked")
				Map<String, String> userInfo = (Map<String, String>)httpServletRequest.getSession().getAttribute("UserInfo");
				UserDTO userDTO = new UserDTO();
				
				if(userInfo != null) {
					userDTO.setId(userInfo.get("Id") == null ? 0 : Long.parseLong(userInfo.get("Id")));
					userDTO.setType(userInfo.get("UserType"));
					userDTO.setUsername(userInfo.get("Username"));
					userDTO.setFirstName(userInfo.get("FirstName"));
					userDTO.setLastName(userInfo.get("LastName"));
					userDTO.isAdmin(Boolean.valueOf(userInfo.get("IsAdmin")));
				}
				
				response.setData(userDTO);
				response.setStatusCode(MessageUtil.getMessage("success.data.found.code"));
				response.setMessage(MessageUtil.getMessage("success.data.found"));
			}
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("server.error.code"));
			response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
		}
		
		return response;
	}
	
	public ServiceResponse updatePassword(UpdateRequest updateRequest) {
		UpdateResponse response = new UpdateResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			new UserManager(getSessionFactory(), session).updatePassword(((UserDTO)updateRequest.getData()).getEntity());
			
			transaction.commit();
			
			response.setStatusCode(MessageUtil.getMessage("success.data.modified.code"));
			response.setMessage(MessageUtil.getMessage("success.data.modified"));
		} catch(RecordNotFoundException err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("no.entity.found.modify.code"));
			response.setMessage(MessageUtil.getMessage("no.entity.found.modify", "Authentication handler"));
		} catch(ManagerException err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			
			if(err.getCause().getMessage().equalsIgnoreCase("Old password doesn't match.")) {
				logger.error(err.getMessage());
				response.setStatusCode(MessageUtil.getMessage("invalid.input.password.mismatch.code"));
				response.setMessage(MessageUtil.getMessage("invalid.input.password.mismatch", err.getMessage()));
			} else {
				logger.error(err.getMessage(), err);
				response.setStatusCode(MessageUtil.getMessage("server.error.code"));
				response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
			}
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
	
	public ServiceResponse logout() {
		ServiceResponse response = new ServiceResponse();
		
		try {
			response.setStatusCode(MessageUtil.getMessage("success.logout.code"));
			response.setMessage(MessageUtil.getMessage("success.logout"));
			
			String logoutURL = getSystemConfig(SystemConfigKey.LOGOUT_URL) + getSystemConfig(SystemConfigKey.SMART_CLASSIFIER_URL);
			((HttpServletRequest)servletRequest).getSession().invalidate();
			((HttpServletResponse)servletResponse).sendRedirect(logoutURL);
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("server.error.code"));
			response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
		}
		
		return response;
	}
}
