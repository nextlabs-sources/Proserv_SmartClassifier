package com.nextlabs.smartclassifier.service.v1.user;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.manager.UserManager;
import com.nextlabs.smartclassifier.dto.v1.UserDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.dto.v1.response.CreationResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class UserCreationService 
		extends Service {
	
	public UserCreationService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public UserCreationService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse addUser(CreationRequest creationRequest) {
		CreationResponse response = new CreationResponse();
		Session session = null;
		Transaction transaction = null;

		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			UserManager userManager = new UserManager(getSessionFactory(), session);
			UserDTO userDTO = (UserDTO)creationRequest.getData();
			
			if(userManager.getUserByUsername(userDTO.getUsername()) == null) {
				userManager.addUser(userDTO.getEntity());
				
				response.setStatusCode(MessageUtil.getMessage("success.data.saved.code"));
				response.setMessage(MessageUtil.getMessage("success.data.saved"));
			} else {
				response.setStatusCode(MessageUtil.getMessage("invalid.input.data.duplicated.code"));
				response.setMessage(MessageUtil.getMessage("invalid.input.data.duplicated", userDTO.getUsername()));
			}
			
			transaction.commit();
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
