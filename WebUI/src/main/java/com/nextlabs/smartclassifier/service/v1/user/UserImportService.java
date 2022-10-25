package com.nextlabs.smartclassifier.service.v1.user;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.manager.UserManager;
import com.nextlabs.smartclassifier.dto.v1.UserDTO;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class UserImportService
		extends Service {
	
	public UserImportService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public UserImportService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse importUser(List<UserDTO> userList) {
		ServiceResponse response = new ServiceResponse();
		List<String> registeredUsername = new ArrayList<String>();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			UserManager userManager = new UserManager(getSessionFactory(), session);
			
			for(UserDTO userDTO : userList) {
				if(userManager.getUserByUsername(userDTO.getUsername()) == null) {
					userManager.addUser(userDTO.getEntity());
				} else {
					registeredUsername.add(userDTO.getUsername());
				}
			}
			
			transaction.commit();
			
			if(registeredUsername.size() > 0) {
				response.setStatusCode(MessageUtil.getMessage("invalid.import.user.duplicated.code"));
				response.setMessage(MessageUtil.getMessage("invalid.import.user.duplicated", StringUtils.join(registeredUsername, ',')));
			} else {
				response.setStatusCode(MessageUtil.getMessage("success.data.saved.code"));
				response.setMessage(MessageUtil.getMessage("success.data.saved"));
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
