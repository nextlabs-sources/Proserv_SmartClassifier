package com.nextlabs.smartclassifier.database.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.nextlabs.smartclassifier.constant.UserStatus;
import com.nextlabs.smartclassifier.constant.UserType;
import com.nextlabs.smartclassifier.database.dao.AuthenticationHandlerDAO;
import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.dao.UserDAO;
import com.nextlabs.smartclassifier.database.entity.AuthenticationHandler;
import com.nextlabs.smartclassifier.database.entity.User;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordInUseException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.exception.RecordUnmatchedException;

public class UserManager 
		extends Manager {
	
	private UserDAO userDAO;
	
	public UserManager(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
		this.userDAO = new UserDAO(sessionFactory, session);
	}
	
	public Long addUser(User user)
			throws ManagerException {
		try {
			logger.debug("Create user.");
			
			Date now = new Date();
			User entity = new User();
			
			entity.setUserType(user.getUserType());
			entity.setUsername(user.getUsername());
			entity.setDisplayName(user.getDisplayName());
			entity.setFirstName(user.getFirstName());
			entity.setLastName(user.getLastName());
			entity.setEmail(user.getEmail());
			entity.setPassword(user.getPassword());
			entity.isAdmin(false);
			entity.isVisible(true);
			entity.isEnabled(true);
			entity.setStatus(UserStatus.ACTIVE.getCode());
			if(user.getAuthenticationHandler() != null
					&& user.getAuthenticationHandler().getId() > 0) {
				entity.setAuthenticationHandler(getAuthenticationHandler(user.getAuthenticationHandler().getId()));
			}
			entity.setFailedAttempt(0);
			entity.setLoginOn(0L);
			entity.setLastLoginOn(0L);
			entity.setCreatedOn(now);
			entity.setModifiedOn(now);
			
			userDAO.saveOrUpdate(entity);
			
			return entity.getId();
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public List<User> getUsers() 
			throws ManagerException {
		try {
			return userDAO.getAll();
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public List<User> getUsers(List<Criterion> criterion, List<Order> order, PageInfo pageInfo)
			throws ManagerException {
		try {
			if(order == null) {
				order = new ArrayList<Order>();
			}
			
			if(order.size() == 0) {
				order.add(Order.asc(User.DISPLAY_NAME));
			}
			
			return userDAO.findByCriteria(criterion, order, pageInfo);
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public User getUserById(Long id)
			throws ManagerException {
		try {
			logger.debug("Get user with id " + id);
			User user = userDAO.get(id);
			
			if(user != null) {
				logger.debug("User found with id " + id);
				return user;
			}
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
		
		return null;
	}
	
	public User getUserByUsername(String username)
			throws ManagerException {
		try {
			logger.debug("Get user with username " + username);
			List<User> users = userDAO.findByCriteria(Restrictions.eq(User.USERNAME, username));
			
			if(users != null && users.size() > 0) {
				logger.debug("User found for username " + username);
				return users.get(0);
			}
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
		
		return null;
	}
	
	public void updateUser(User user)
			throws ManagerException, RecordNotFoundException, IllegalArgumentException {
		try {
			logger.debug("Update user configuration for user id " + user.getId());
			User entity = userDAO.get(user.getId());
			
			if(entity != null) {
				entity.setDisplayName(user.getDisplayName());
				if(entity.getUserType().equals(UserType.INTERNAL.getCode())) {
					entity.setFirstName(user.getFirstName());
					entity.setLastName(user.getLastName());
					entity.setEmail(user.getEmail());
				}
				
				entity.setModifiedOn(new Date());
				
				userDAO.saveOrUpdate(entity);
			} else {
				throw new RecordNotFoundException("User record not found for the given UserID.");
			}
		} catch(HibernateException err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err);
		}
	}
	
	public void deleteUser(User user)
			throws ManagerException, RecordNotFoundException, RecordInUseException, RecordUnmatchedException {
		try {
			logger.debug("Delete user for user id " + user.getId());
			User entity = userDAO.get(user.getId());
			
			if(entity != null) {
				if(entity.getModifiedOn().getTime() == user.getModifiedOn().getTime()) {
					userDAO.delete(entity);
				} else {
					throw new RecordUnmatchedException("Row was updated or deleted by another transaction.");
				}
			} else {
				throw new RecordNotFoundException("User record not found for the given UserID.");
			}
		} catch(HibernateException err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err);
		}
	}
	
	public void updatePassword(User user)
			throws ManagerException, RecordNotFoundException, IllegalArgumentException {
		try {
			logger.debug("Update user password for user id " + user.getId());
			User entity = userDAO.get(user.getId());
			
			if(entity != null
					&& entity.getUsername().equals(user.getUsername())) {
				if(entity.getPassword().equalsIgnoreCase(user.getPassword())) {
					Date now = new Date();
					entity.setPassword(user.getNewPassword());
					entity.setPasswordChangedOn(now.getTime());
					entity.setModifiedOn(now);
					
					userDAO.saveOrUpdate(entity);
				} else {
					throw new IllegalArgumentException("Old password doesn't match.");
				}
			} else {
				throw new RecordNotFoundException("User record not found for the given UserID.");
			}
		} catch(HibernateException err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err);
		}
	}
	
	public long getRecordCount(List<Criterion> criterion) 
			throws ManagerException {
		try {
			return userDAO.getCount(criterion);
		} catch(HibernateException err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err);
		}
	}
	
	private AuthenticationHandler getAuthenticationHandler(Long authenticationHandlerId) {
		try {
			return (new AuthenticationHandlerDAO(sessionFactory, session)).get(authenticationHandlerId);
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
		}
		
		return null;
	}
}
